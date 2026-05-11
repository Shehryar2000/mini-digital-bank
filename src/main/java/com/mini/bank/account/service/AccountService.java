package com.mini.bank.account.service;

import com.mini.bank.account.dto.*;
import com.mini.bank.account.entity.Account;
import com.mini.bank.account.enums.AccountStatus;
import com.mini.bank.ledger.enums.LedgerType;
import com.mini.bank.account.repository.AccountRepository;
import com.mini.bank.account.validator.AccountValidator;
import com.mini.bank.audit.enums.AuditAction;
import com.mini.bank.audit.enums.AuditEntityType;
import com.mini.bank.audit.service.AuditService;
import com.mini.bank.common.exception.*;
import com.mini.bank.common.security.AuthContext;
import com.mini.bank.common.util.ReferenceIdGenerator;
import com.mini.bank.corebanking.branch.entity.Branch;
import com.mini.bank.corebanking.branch.repository.BranchRepository;
import com.mini.bank.corebanking.dto.ApiResponse;
import com.mini.bank.customer.entity.Customer;
import com.mini.bank.customer.repository.CustomerRepository;
import com.mini.bank.ledger.service.LedgerEntryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final AuditService auditService;
    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;
    private final AuthContext authContext;
    private final LedgerEntryService ledgerEntryService;
    private final AccountValidator accountValidator;
    private final ReferenceIdGenerator refIdGenerator;

    // Create Account
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request, String ip) {

        String refId = refIdGenerator.generate("DEP");

        try {

            Customer customer = getCustomerById(ip, AuditAction.ACCOUNT_CREATED);

            log.info("Creating account | customer={} | branch={} | type={}",
                    customer.getCustomerNumber(),
                    request.getBranchId(),
                    request.getAccountType()
            );

            Branch branch = getBranchById(request, ip, AuditAction.ACCOUNT_CREATED);

            Long accNum = accountRepository.getNextAccountNumber();
            String accountNumber = String.format("%s-%s", branch.getCode(), accNum);
            log.debug("Generated account number={}", accountNumber);

            Account account = new Account();
            account.setName(request.getName());
            account.setAccountNumber(accNum);
            account.setAccountType(request.getAccountType());
            account.setCustomer(customer);
            account.setBalance(BigDecimal.ZERO);
            account.setStatus(AccountStatus.ACTIVE);
            account.setBranch(branch);

            Account savedAccount = accountRepository.save(account);

            log.info("Account created | id={} | number={} | customer={} | branch={}, referenceId={}",
                    savedAccount.getId(),
                    savedAccount.getAccountNumber(),
                    customer.getCustomerNumber(),
                    branch.getCode(),
                    refId
            );

            String branchCode = String.format("%s-%s",
                    savedAccount.getBranch().getCity().getPrefix(),
                    savedAccount.getBranch().getCode());

            // Ledger Entry
            ledgerEntryService.createLedgerEntry(savedAccount, BigDecimal.ZERO, LedgerType.CREDIT, refId, ip);

            // Audit Maintaining - Account Created Success
            auditService.success(
                    authContext.getUserId(),
                    AuditAction.ACCOUNT_CREATED,
                    ip,
                    AuditEntityType.ACCOUNT,
                    savedAccount.getId(),
                    Map.of(
                            "customerId", customer.getId(),
                            "customerNumber", customer.getCustomerNumber(),
                            "accountId", savedAccount.getId(),
                            "accountNumberRaw", accNum,
                            "accountNumberDisplay", accountNumber,
                            "branch", branchCode,
                            "referenceId", refId
                    )
            );

            return AccountResponse.builder()
                    .accountNumber(accountNumber)
                    .name(savedAccount.getName())
                    .type(savedAccount.getAccountType().name())
                    .status(savedAccount.getStatus().name())
                    .branch(branchCode)
                    .build();

        } catch (CustomerNotFoundException | BranchNotFoundException e) {
            throw e;
        } catch (Exception e) {

            log.error(
                    "Account creation failed | user={} | error={}",
                    authContext.getUserId(),
                    e.getMessage(),
                    e
            );

            auditService.failure(
                    authContext.getUserId(),
                    AuditAction.ACCOUNT_CREATED,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    errorMeta(e)
            );

            throw e;
        }

    }

    // Amount Deposit
    @Transactional
    public ApiResponse deposit(DepositRequest request, String ip) {

        String refId = refIdGenerator.generate("DEP");

        try {
            accountValidator.validateAmount(request.getAccountId(), request.getAmount(), ip, AuditAction.ACCOUNT_DEPOSIT);
            Account account = accountValidator.getAccountById(request.getAccountId(), ip, AuditAction.ACCOUNT_DEPOSIT);
//            accountValidator.validateAccountActive(account, ip, AuditAction.ACCOUNT_DEPOSIT);
            accountValidator.validateOwnership(account, ip, AuditAction.ACCOUNT_DEPOSIT);
            accountValidator.validateCreditAllowed(account, ip, AuditAction.ACCOUNT_DEPOSIT);
            BigDecimal newBalance = account.getBalance().add(request.getAmount());

            account.setBalance(newBalance);
            accountRepository.save(account);

            // Ledger Entry
            ledgerEntryService.createLedgerEntry(account, request.getAmount(), LedgerType.CREDIT, refId, ip);

            log.info("Amount deposited successfully  | account={} | amount={} | newBalance={}, referenceId={}",
                    account.getAccountNumber(),
                    request.getAmount(),
                    newBalance,
                    refId);

            auditService.success(
                    authContext.getUserId(),
                    AuditAction.ACCOUNT_DEPOSIT,
                    ip,
                    AuditEntityType.ACCOUNT,
                    account.getId(),
                    Map.of(
                            "accountNumber", account.getAccountNumber(),
                            "amount", request.getAmount(),
                            "newBalance", newBalance
                    )
            );

            return ApiResponse.builder()
                    .message(request.getAmount() + " PKR has been successfully deposited")
                    .build();

        } catch (UnauthorizedException | IllegalArgumentException | AccountNotFoundException |
                 AccountNotActiveException | AccountOperationRestrictedException e) {
            throw e;
        } catch (Exception e) {

            log.error("Deposit failed | accountId={} | user={} | error={}",
                    request.getAccountId(),
                    authContext.getUserId(),
                    e.getMessage(),
                    e);

            auditService.failure(
                    authContext.getUserId(),
                    AuditAction.ACCOUNT_DEPOSIT,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    errorMeta(e)
            );

            throw e;
        }

    }

    // Amount Withdraw
    @Transactional
    public ApiResponse withdraw(WithdrawRequest request, String ip) {

        String refId = refIdGenerator.generate("WDR");

        try {

            accountValidator.validateAmount(request.getAccountId(), request.getAmount(), ip, AuditAction.ACCOUNT_WITHDRAW);
            Account account = accountValidator.getAccountById(request.getAccountId(), ip, AuditAction.ACCOUNT_WITHDRAW);
//            accountValidator.validateAccountActive(account, ip, AuditAction.ACCOUNT_WITHDRAW);
            accountValidator.validateOwnership(account, ip, AuditAction.ACCOUNT_WITHDRAW);
            accountValidator.validateBalance(account, request.getAmount(), ip, AuditAction.ACCOUNT_WITHDRAW);
            accountValidator.validateDebitAllowed(account, ip, AuditAction.ACCOUNT_WITHDRAW);

            BigDecimal newBalance = account.getBalance().subtract(request.getAmount());

            account.setBalance(newBalance);
            accountRepository.save(account);

            // Ledger Entry
            ledgerEntryService.createLedgerEntry(account, request.getAmount(), LedgerType.DEBIT, refId, ip);

            log.info("Withdraw | account={} | amount={} | newBalance={}",
                    account.getAccountNumber(),
                    request.getAmount(),
                    newBalance);

            auditService.success(
                    authContext.getUserId(),
                    AuditAction.ACCOUNT_WITHDRAW,
                    ip,
                    AuditEntityType.ACCOUNT,
                    account.getId(),
                    Map.of(
                            "accountNumber", account.getAccountNumber(),
                            "amount", request.getAmount(),
                            "newBalance", newBalance
                    )
            );

            return ApiResponse.builder()
                    .message(request.getAmount() + " PKR has been successfully withdrawn")
                    .build();

        } catch (UnauthorizedException | IllegalArgumentException | AccountNotFoundException |
                 AccountNotActiveException | InsufficientBalanceException | AccountOperationRestrictedException e) {
            throw e;
        } catch (Exception e) {

            log.error("Withdraw failed | accountId={} | user={} | error={}",
                    request.getAccountId(),
                    authContext.getUserId(),
                    e.getMessage(),
                    e);

            auditService.failure(
                    authContext.getUserId(),
                    AuditAction.ACCOUNT_WITHDRAW,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    errorMeta(e)
            );

            throw e;
        }

    }

    // Get All Accounts of User
    public List<AccountResponse> getAllAccounts(String ip) {

        try {

            Customer customer = getCustomerById(ip, AuditAction.ACCOUNT_FETCH);
            UUID customerId = customer.getId();

            List<Account> accounts = accountRepository.findByCustomerId(customerId);

            if (accounts.isEmpty()) {

                auditService.failure(
                        authContext.getUserId(),
                        AuditAction.ACCOUNT_FETCH,
                        ip,
                        AuditEntityType.ACCOUNT,
                        null,
                        Map.of(
                                "userId", authContext.getUserId(),
                                "customerId", customerId,
                                "reason", "no records found"
                        )
                );
                throw new AccountNotFoundException("no records found");

            }

            List<AccountResponse> responses = new ArrayList<>();
            String accountNumber;
            String branchCode;

            for (Account account : accounts) {

                accountValidator.validateOwnership(account, ip, AuditAction.ACCOUNT_FETCH);

                accountNumber = String.format("%s-%s", account.getBranch().getCode(), account.getAccountNumber());
                branchCode = String.format("%s-%s",
                        account.getBranch().getCity().getPrefix(),
                        account.getBranch().getCode());

                responses.add(
                        AccountResponse.builder()
                                .accountNumber(accountNumber)
                                .name(account.getName())
                                .type(account.getAccountType().name())
                                .status(account.getStatus().name())
                                .branch(branchCode)
                                .build()
                );

            }

            auditService.success(
                    authContext.getUserId(),
                    AuditAction.ACCOUNT_FETCH,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    Map.of("count", accounts.size())
            );

            return responses;

        } catch (CustomerNotFoundException | AccountNotFoundException | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {

            auditService.failure(
                    authContext.getUserId(),
                    AuditAction.ACCOUNT_FETCH,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    errorMeta(e)
            );

            throw e;
        }
    }

    // Get Account By ID
    public AccountResponse getAccountByIdOrNumber(UUID accountId, String acctNo, String ip) {

        try {

            Account account = null;

            if (accountId != null) {
                account = accountValidator.getAccountById(accountId, ip, AuditAction.ACCOUNT_FETCH);
                accountValidator.validateOwnership(account, ip, AuditAction.ACCOUNT_FETCH);
            } else if (acctNo != null) {
                account = accountValidator.getAccountByNumber(acctNo, ip, AuditAction.ACCOUNT_FETCH);
                accountValidator.validateOwnership(account, ip, AuditAction.ACCOUNT_FETCH);
            } else {

                log.warn("accountId or accountNumber required");
                throw new IllegalArgumentException("Either accountId or accountNumber required");
            }

//            accountValidator.validateAccountActive(account, ip, AuditAction.ACCOUNT_FETCH);
            accountValidator.validateAccountAccessible(account, ip, AuditAction.ACCOUNT_FETCH);

            String accountNumber = String.format("%s-%s", account.getBranch().getCode(), account.getAccountNumber());
            String branchCode = String.format("%s-%s",
                    account.getBranch().getCity().getPrefix(),
                    account.getBranch().getCode());

            log.info("Account fetched | accountId={}, accountNumber={}, branchCode={}",
                    account.getId(),
                    accountNumber,
                    branchCode
            );

            // Audit Maintaining - Account Fetch Success
            auditService.success(
                    authContext.getUserId(),
                    AuditAction.ACCOUNT_FETCH,
                    ip,
                    AuditEntityType.ACCOUNT,
                    account.getId(),
                    Map.of(
                            "accountId", account.getId(),
                            "accountNumberRaw", account.getAccountNumber(),
                            "accountNumberDisplay", accountNumber,
                            "branch", branchCode
                    )
            );

            return AccountResponse.builder()
                    .accountNumber(accountNumber)
                    .name(account.getName())
                    .type(account.getAccountType().name())
                    .status(account.getStatus().name())
                    .branch(branchCode)
                    .build();

        } catch (IllegalArgumentException | AccountNotFoundException | AccountNotActiveException |
                 UnauthorizedException e) {
            throw e;
        } catch (Exception e) {

            log.warn("Account fetching failed");

            auditService.failure(
                    authContext.getUserId(),
                    AuditAction.ACCOUNT_FETCH,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    errorMeta(e)
            );

            throw e;
        }
    }

    //Get Account Balance
    public BalanceResponse getBalance(UUID accountId, String ip) {

        try {
            Account account = accountValidator.getAccountById(accountId, ip, AuditAction.ACCOUNT_BALANCE);
//            accountValidator.validateAccountActive(account, ip, AuditAction.ACCOUNT_BALANCE);
            accountValidator.validateAccountAccessible(account, ip, AuditAction.ACCOUNT_BALANCE);
            accountValidator.validateOwnership(account, ip, AuditAction.ACCOUNT_BALANCE);

            log.info("Account balance fetched | accountId={}, accountNumber={}",
                    account.getId(),
                    account.getAccountNumber()
            );

            // Audit Maintaining - Account Balance Success
            auditService.success(
                    authContext.getUserId(),
                    AuditAction.ACCOUNT_BALANCE,
                    ip,
                    AuditEntityType.ACCOUNT,
                    account.getId(),
                    Map.of(
                            "accountId", account.getId(),
                            "accountNumberRaw", account.getAccountNumber()
                    )
            );

            return BalanceResponse.builder()
                    .balance(account.getBalance())
                    .build();

        } catch (AccountNotFoundException | AccountNotActiveException | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {

            log.info("Failed to fetch account balance | accountId={}",
                    accountId
            );

            auditService.failure(
                    authContext.getUserId(),
                    AuditAction.ACCOUNT_BALANCE,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    errorMeta(e)
            );
            throw e;
        }
    }

    // Update Account Status
    @Transactional
    public ApiResponse updateStatus(UUID accountId, UpdateAccountStatusRequest request, String ip) {

        try {

            log.info(
                    "Updating Account status | accountId={} |  newStatus={}",
                    accountId,
                    request.getStatus()
            );

            Account account = accountValidator.getAccountById(accountId, ip, AuditAction.ACCOUNT_STATUS_UPDATE);
            AccountStatus oldStatus = account.getStatus();

            accountValidator.validateStatusTransition(account, oldStatus, request.getStatus(), ip, AuditAction.ACCOUNT_STATUS_UPDATE);

            if (oldStatus == request.getStatus()) {

                log.warn(
                        "Account status unchanged | accountId={} |  oldStatus={} | newStatus={}",
                        accountId,
                        oldStatus,
                        request.getStatus()
                );

                auditService.failure(
                        authContext.getUserId(),
                        AuditAction.ACCOUNT_STATUS_UPDATE,
                        ip,
                        AuditEntityType.ACCOUNT,
                        accountId,
                        Map.of(
                                "accountId", accountId.toString(),
                                "oldStatus", oldStatus.name(),
                                "newStatus", request.getStatus().name()
                        )
                );

                throw new NoChangesDetectedException("No changes detected");

            }

            account.setStatus(request.getStatus());
            accountRepository.save(account);

            log.info(
                    "Account status updated | accountId={} | oldStatus={} | newStatus={}",
                    accountId,
                    oldStatus,
                    request.getStatus()
            );

            auditService.success(
                    authContext.getUserId(),
                    AuditAction.ACCOUNT_STATUS_UPDATE,
                    ip,
                    AuditEntityType.ACCOUNT,
                    accountId,
                    Map.of(
                            "oldStatus", oldStatus,
                            "newStatus", request.getStatus()
                    )
            );


            return ApiResponse.builder()
                    .message("Account status updated successfully")
                    .build();

        } catch (AccountNotFoundException | NoChangesDetectedException e) {
            throw e;
        } catch (Exception e) {

            log.error("Failed to change account status | accountId={} | newStatus={}",
                    accountId,
                    request.getStatus()
            );

            auditService.failure(
                    authContext.getUserId(),
                    AuditAction.ACCOUNT_STATUS_UPDATE,
                    ip,
                    AuditEntityType.ACCOUNT,
                    accountId,
                    errorMeta(e)
            );

            throw e;
        }

    }

    // --------------- Helper Methods --------------------

    private Customer getCustomerById(String ip, AuditAction action) {

        Customer customer = customerRepository.findById(authContext.getCustomerId()).orElse(null);

        if (customer == null) {

            log.warn("Customer not found | customerId={}", authContext.getCustomerId());

            auditService.failure(
                    authContext.getUserId(),
                    action,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    Map.of(
                            "userId", authContext.getUserId(),
                            "reason", "customer not found"
                    )
            );
            throw new CustomerNotFoundException("Customer not found");
        }

        return customer;
    }

    private Branch getBranchById(CreateAccountRequest request, String ip, AuditAction action) {

        Branch branch = branchRepository.findById(request.getBranchId()).orElse(null);
        if (branch == null) {

            log.warn("Branch not found | branchId={}", request.getBranchId());

            auditService.failure(
                    authContext.getUserId(),
                    action,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    Map.of(
                            "customerId", authContext.getCustomerId(),
                            "branchId", request.getBranchId(),
                            "reason", "Invalid branch Id given"
                    )
            );
            throw new BranchNotFoundException("Branch not found");
        }

        return branch;
    }
/*
    private Account getAccountById(UUID accountId, String ip, AuditAction action) {

        Account account = accountRepository.findById(accountId).orElse(null);

        if (account == null) {

            log.warn("Account not found | accountId={}", accountId);

            auditService.failure(
                    authContext.getUserId(),
                    action,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    Map.of(
                            "accountId", accountId.toString(),
                            "reason", "account not found"
                    )
            );

            throw new AccountNotFoundException("account not found");

        }

        return account;
    }

    private Account getAccountByNumber(String accountNumber, String ip, AuditAction action) {

        accountNumber = accountNumber.trim();

        if (accountNumber.length() != 14) {

            log.warn("Incomplete account number | accountNumber={}", accountNumber);

            auditService.failure(
                    authContext.getUserId(),
                    action,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    Map.of(
                            "accountNumber", accountNumber,
                            "reason", "incomplete account number"
                    )
            );

            throw new IncompleteAccountNumberException("Account number should be of 14 digits long");

        }

        Long acctNum = Long.valueOf(accountNumber.substring(4));

        Account account = accountRepository.findByAccountNumber(acctNum).orElse(null);

        if (account == null) {

            log.warn("Account not found | accountNumber={}", accountNumber);

            auditService.failure(
                    authContext.getUserId(),
                    action,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    Map.of(
                            "accountNumber", accountNumber,
                            "reason", "account not found"
                    )
            );

            throw new AccountNotFoundException("account not found");

        }

        return account;
    }

    private void validateAccountActive(Account account, String ip, AuditAction action) {

        if (!account.getStatus().equals(AccountStatus.ACTIVE)) {

            log.warn("Account inactive | accountId={}", account.getId());

            auditService.failure(
                    authContext.getUserId(),
                    action,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    Map.of(
                            "accountId", account.getId().toString(),
                            "reason", "account not active"
                    )
            );
            throw new AccountNotActiveException("Account not active");
        }
    }

    private void validateOwnership(Account account, String ip, AuditAction action) {
        if (!account.getCustomer().getId().equals(authContext.getCustomerId())) {

            log.warn("Invalid ownership of account | accountId={} | customerId={}", account.getId(), authContext.getCustomerId());

            auditService.failure(
                    authContext.getUserId(),
                    action,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    Map.of(
                            "customerId", authContext.getCustomerId(),
                            "accountId", account.getId().toString(),
                            "reason", "account not belongs to the customer"
                    )
            );

            throw new UnauthorizedException("Access denied");
        }
    }

    private void validateAmount(UUID accountId, BigDecimal amount, String ip, AuditAction action) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {

            log.warn("Invalid amount | accountId={} | amount={}", accountId, amount);

            auditService.failure(
                    authContext.getUserId(),
                    action,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    Map.of(
                            "accountId", accountId.toString(),
                            "amount", amount,
                            "reason", "amount must be greater than zero"
                    )
            );

            throw new IllegalArgumentException("amount must be greater than zero");
        }
    }
*/


    private Map<String, Object> errorMeta(Exception e) {
        return Map.of(
                "error", e.getClass().getSimpleName(),
                "message", e.getMessage()
        );
    }

}
