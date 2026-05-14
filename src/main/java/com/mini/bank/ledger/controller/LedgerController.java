package com.mini.bank.ledger.controller;

import com.mini.bank.common.util.IpUtil;
import com.mini.bank.ledger.dto.LedgerResponse;
import com.mini.bank.ledger.service.LedgerEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerEntryService ledgerEntryService;

    @Operation(
            summary = "Get Ledger History",
            description = "Returns transaction history of account"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ledger fetched"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/history/{accountId}")
    public ResponseEntity<Page<LedgerResponse>> getHistory(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest http) {

        String ip = IpUtil.getClientIp(http);
        Page<LedgerResponse> responses = ledgerEntryService.getHistory(accountId, page, size, ip);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/mini-statement/{accountId}")
    public ResponseEntity<List<LedgerResponse>>
    getMiniStatement(
            @PathVariable UUID accountId,
            HttpServletRequest http
    ) {

        String ip = IpUtil.getClientIp(http);

        List<LedgerResponse> response =
                ledgerEntryService.getMiniStatement(
                        accountId,
                        ip
                );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
