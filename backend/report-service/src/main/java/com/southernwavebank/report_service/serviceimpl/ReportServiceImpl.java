package com.southernwavebank.report_service.serviceimpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.southernwavebank.report_service.model.entity.Report;
import com.southernwavebank.report_service.model.externalDto.TransactionDto;
import com.southernwavebank.report_service.repository.ReportRepository;
import com.southernwavebank.report_service.service.ReportService;
import com.southernwavebank.report_service.util.GoogleDriveService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Value("${google.drive.upload.folder-id}")
    private String folderId;

    @Override
    public String generateTransactionReport(List<TransactionDto> transactions, LocalDateTime time)
            throws GeneralSecurityException {

        if (transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException("Transaction list is empty");
        }

        String accountNumber = transactions.get(0).getAccountNumber();
        String maskedAccount = String.format("XXXX%s",
                accountNumber.substring(accountNumber.length() - 4));

        String timestamp = time.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "Transaction_History_" + maskedAccount + "_" + timestamp + ".pdf";

        log.info("Starting transaction report generation: {}", fileName);

        File pdfFile = new File(fileName);

        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {

            Document document = new Document();
            PdfWriter.getInstance(document, fos);

            document.open();

            // Title
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);

            Paragraph title = new Paragraph("SouthernWave Bank - Transaction History", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            float[] columnWidths = { 3f, 3f, 2f, 2f, 3f };
            table.setWidths(columnWidths);

            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);

            Stream.of("Account Number", "Transaction Type", "Amount", "Status", "Transaction Time")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell(new Phrase(columnTitle, headerFont));
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(header);
                    });

            Font bodyFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            transactions.stream()
                    .flatMap(transaction -> Stream.of(
                            new PdfPCell(new Phrase(transaction.getAccountNumber(), bodyFont)),
                            new PdfPCell(new Phrase(transaction.getTransactionType().toString(), bodyFont)),
                            new PdfPCell(new Phrase(transaction.getAmount().toString(), bodyFont)),
                            new PdfPCell(new Phrase(transaction.getStatus().toString(), bodyFont)),
                            new PdfPCell(new Phrase(transaction.getTransactionTime().format(formatter), bodyFont))))
                    .forEach(table::addCell);

            document.add(table);
            document.close();

            // Upload to Google Drive
            String driveFileId = GoogleDriveService.uploadFile(pdfFile, "application/pdf", folderId);
            String driveFileUrl = "https://drive.google.com/file/d/" + driveFileId + "/view";

            // Save metadata to DB
            Report report = new Report();
            report.setAccountNumber(accountNumber);
            report.setReportName(fileName);
            report.setReportUrl(driveFileUrl);
            report.setGeneratedAt(time);

            reportRepository.save(report);

            log.info("Report uploaded to Google Drive successfully");

            return driveFileUrl;

        } catch (DocumentException | IOException e) {
            log.error("Error generating or uploading PDF", e);
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
