package com.mini.bank.account.controller;

import com.mini.bank.account.dto.*;
import com.mini.bank.account.service.AccountService;
import com.mini.bank.transfer.service.TransferService;
import com.mini.bank.common.util.IpUtil;
import com.mini.bank.corebanking.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(
            summary = "Create Bank Account",
            description = "Creates a new customer bank account"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Account created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            HttpServletRequest http) {

        String ip = IpUtil.getClientIp(http);
        AccountResponse response = accountService.createAccount(request, ip);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get My Accounts",
            description = "Returns all accounts of current customer"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Accounts fetched"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/my-accounts")
    public ResponseEntity<List<AccountResponse>> getMyAccounts(HttpServletRequest http) {
        String ip = IpUtil.getClientIp(http);
        List<AccountResponse> responses = accountService.getAllAccounts(ip);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable UUID id, HttpServletRequest http) {
        String ip = IpUtil.getClientIp(http);
        AccountResponse response = accountService.getAccountByIdOrNumber(id, null, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByNumber(
            @PathVariable String accountNumber,
            HttpServletRequest http) {

        String ip = IpUtil.getClientIp(http);
        AccountResponse response = accountService.getAccountByIdOrNumber(null, accountNumber, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Deposit Money",
            description = "Deposits amount into account"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Deposit successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid amount"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse> deposit(
            @Valid @RequestBody DepositRequest request,
            HttpServletRequest http
    ) {
        String ip = IpUtil.getClientIp(http);
        ApiResponse response = accountService.deposit(request, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Withdraw Money",
            description = "Withdraws amount from account"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Withdraw successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Insufficient balance"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse> withdraw(
            @Valid @RequestBody WithdrawRequest request,
            HttpServletRequest http
    ) {
        String ip = IpUtil.getClientIp(http);
        ApiResponse response = accountService.withdraw(request, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Get Account Balance",
            description = "Returns current account balance"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Balance fetched"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/balance/{id}")
    public ResponseEntity<BalanceResponse> getBalance(
            @PathVariable UUID id,
            HttpServletRequest http
    ) {
        String ip = IpUtil.getClientIp(http);
        BalanceResponse response = accountService.getBalance(id, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Update Account Status",
            description = "Admin can activate, freeze or close account"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "No changes detected"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/status/{accountId}")
    public ResponseEntity<ApiResponse> updateAccountStatus(
            @PathVariable UUID accountId,
            @Valid
            @RequestBody
            UpdateAccountStatusRequest request,
            HttpServletRequest http
    ) {
        String ip = IpUtil.getClientIp(http);
        ApiResponse response = accountService.updateStatus(accountId, request, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
