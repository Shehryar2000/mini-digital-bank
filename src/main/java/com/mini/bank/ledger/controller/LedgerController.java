package com.mini.bank.ledger.controller;

import com.mini.bank.common.util.IpUtil;
import com.mini.bank.ledger.dto.LedgerResponse;
import com.mini.bank.ledger.service.LedgerEntryService;
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

//    @GetMapping("/history/{accountId}")
//    public ResponseEntity<List<LedgerResponse>> getHistory(@PathVariable UUID accountId, HttpServletRequest http){
//
//        String ip = IpUtil.getClientIp(http);
//        List<LedgerResponse> responses = ledgerEntryService.getHistory(accountId, ip);
//        return ResponseEntity.status(HttpStatus.OK).body(responses);
//    }

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
