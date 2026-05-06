package com.mini.bank.common.util;

import com.mini.bank.transfer.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class ReferenceIdGenerator {

    private final TransferRepository transferRepository;

    public String generate(String prefix) {
        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        Long seq = transferRepository.getNextRefNumber();

        return prefix + "-" + date + "-" + seq;
    }

}
