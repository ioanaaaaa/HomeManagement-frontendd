package com.fmi.relovut.controllers;


import com.fmi.relovut.dto.ReportDto;
import com.fmi.relovut.dto.transactions.TransactionDto;
import com.fmi.relovut.services.EmailService;
import com.fmi.relovut.services.TransactionService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/report")
    public void sendEmailWithAttachment(@RequestBody ReportDto reportDto, Principal principal) throws URISyntaxException, ParseException, MessagingException, DocumentException, IOException {
        List<TransactionDto> transactionDtoList = transactionService.getTransactionsInInterval(
                Timestamp.valueOf(reportDto.getFromDate() + " 00:00:00.00"),
                Timestamp.valueOf(reportDto.getToDate() + " 00:00:00.00"),
                principal.getName());
        emailService.sendReportEmail(principal.getName(), transactionDtoList);
    }
}
