package com.mini.bank.account.controller;

import com.mini.bank.account.dto.*;
import com.mini.bank.account.service.AccountService;
import com.mini.bank.transfer.service.TransferService;
import com.mini.bank.common.util.IpUtil;
import com.mini.bank.corebanking.dto.ApiResponse;
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
    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            HttpServletRequest http) {

        String ip = IpUtil.getClientIp(http);
        AccountResponse response = accountService.createAccount(request, ip);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

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

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse> deposit(
            @Valid @RequestBody DepositRequest request,
            HttpServletRequest http
    ) {
        String ip = IpUtil.getClientIp(http);
        ApiResponse response = accountService.deposit(request, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse> withdraw(
            @Valid @RequestBody WithdrawRequest request,
            HttpServletRequest http
    ) {
        String ip = IpUtil.getClientIp(http);
        ApiResponse response = accountService.withdraw(request, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/balance/{id}")
    public ResponseEntity<BalanceResponse> getBalance(
            @PathVariable UUID id,
            HttpServletRequest http
    ) {
        String ip = IpUtil.getClientIp(http);
        BalanceResponse response = accountService.getBalance(id, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

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
