package com.mini.bank.transfer.controller;

import com.mini.bank.transfer.dto.TransferDetailsResponse;
import com.mini.bank.transfer.service.TransferService;
import com.mini.bank.transfer.dto.TransferRequest;
import com.mini.bank.common.util.IpUtil;
import com.mini.bank.corebanking.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @Operation(
            summary = "Transfer Money",
            description = "Transfers money securely between accounts using pessimistic locking"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transfer successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Insufficient balance"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<ApiResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            HttpServletRequest http
    ) {
        String ip = IpUtil.getClientIp(http);
        ApiResponse response = transferService.transfer(request, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Get Transfer Details",
            description = "Returns transfer transaction details by reference ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transfer found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transfer not found")
    })
    @GetMapping("/{referenceId}")
    public ResponseEntity<TransferDetailsResponse> getTransferDetails(
            @PathVariable String referenceId,
            HttpServletRequest http
    ) {
        String ip = IpUtil.getClientIp(http);
        TransferDetailsResponse response = transferService.getTransferDetails(referenceId, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
