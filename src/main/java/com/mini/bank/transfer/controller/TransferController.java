package com.mini.bank.transfer.controller;

import com.mini.bank.transfer.service.TransferService;
import com.mini.bank.transfer.dto.TransferRequest;
import com.mini.bank.common.util.IpUtil;
import com.mini.bank.corebanking.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<ApiResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            HttpServletRequest http
    ) {
        String ip = IpUtil.getClientIp(http);
        ApiResponse response = transferService.transfer(request, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
