package com.southernwavebank.report_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import com.southernwavebank.report_service.model.TransactionStatus;
import com.southernwavebank.report_service.model.TransactionType;
import com.southernwavebank.report_service.model.entity.Report;
import com.southernwavebank.report_service.model.externalDto.TransactionDto;
import com.southernwavebank.report_service.repository.ReportRepository;
import com.southernwavebank.report_service.serviceimpl.ReportServiceImpl;

//@ExtendWith(MockitoExtension.class)
//class ReportServiceImplTest {
//
//    @InjectMocks
//    private ReportServiceImpl reportService;
//
//    @Mock
//    private ReportRepository reportRepository;
//
//    @TempDir
//    Path tempDir; // JUnit creates a temp folder for safe file write testing
//    
//    @Mock
//    private File directory; 
//
//    private List<TransactionDto> transactions;
//
//    @BeforeEach
//    void setUp() {
//        TransactionDto transaction = new TransactionDto();
//        transaction.setAccountNumber("1234567890");
//        transaction.setTransactionType(TransactionType.DEPOSIT);
//        transaction.setAmount(BigDecimal.valueOf(500.00));
//        transaction.setStatus(TransactionStatus.SUCCESS);
//        transaction.setTransactionTime(LocalDateTime.now());
//
//        transactions = List.of(transaction);
//
//        // Inject the temp path dynamically if needed (reflection/mock config if hardcoded)
//    }
//
//    @Test
//    void generateTransactionReport_shouldReturnFilePathAndSaveReport() {
//        // Given
//        LocalDateTime now = LocalDateTime.now();
//
//        // When
//        String filePath = reportService.generateTransactionReport(transactions, now);
//
//        // Then
//        assertNotNull(filePath);
//        assertTrue(filePath.endsWith(".pdf"));
//        assertTrue(Files.exists(Path.of(filePath)));
//
//        // Verify reportRepository interaction
//        verify(reportRepository, times(1)).save(any(Report.class));
//    }
    
//    @Test
//    void generateTransactionReport_directoryCreationFails_shouldThrowException() {
//        // Given
//        LocalDateTime now = LocalDateTime.now();
//
//        // Mock directory creation failure (mkdirs() returns false)
//        when(directory.exists()).thenReturn(false);
//        when(directory.mkdirs()).thenReturn(false);  // Simulate failure in directory creation
//
//        // When & Then
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            reportService.generateTransactionReport(transactions, now);
//        });
//
//        assertEquals("Failed to create report directory.", exception.getMessage());
//    }
//}


//@ExtendWith(MockitoExtension.class)
//class ReportServiceImplTest {
//
//    @InjectMocks
//    private ReportServiceImpl reportService;
//
//    @Mock
//    private ReportRepository reportRepository;
//
//    @TempDir
//    File tempDir;
//    
//    @Mock
//    private Document documentMock;
//    
//    @Mock
//    private PdfWriter pdfWriterMock;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        // Inject temporary directory into reportDirectoryPath
//        Field pathField = ReportServiceImpl.class.getDeclaredField("reportDirectoryPath");
//        pathField.setAccessible(true);
//        pathField.set(reportService, tempDir.getAbsolutePath());
//    }
//
//    @Test
//    void testGenerateTransactionReport_success() {
//        // Arrange
//        TransactionDto txn = new TransactionDto();
//        txn.setAccountNumber("1234567890");
//        txn.setAmount(BigDecimal.valueOf(500.00));
//        txn.setTransactionType(TransactionType.DEPOSIT);
//        txn.setStatus(TransactionStatus.SUCCESS);
//        txn.setTransactionTime(LocalDateTime.now());
//
//        List<TransactionDto> transactions = List.of(txn);
//        LocalDateTime now = LocalDateTime.now();
//
//        // Act
//        String filePath = reportService.generateTransactionReport(transactions, now);
//
//        // Assert
//        assertNotNull(filePath);
//        assertTrue(filePath.endsWith(".pdf"));
//        assertTrue(new File(filePath).exists());
//
//        verify(reportRepository, times(1)).save(any(Report.class));
//    }
//    
//    @Test
//    void testGenerateTransactionReport_directoryCreationFails_throwsException() throws Exception {
//        // Arrange
//        TransactionDto txn = new TransactionDto();
//        txn.setAccountNumber("1234567890");
//        txn.setAmount(BigDecimal.valueOf(500.00));
//        txn.setTransactionType(TransactionType.WITHDRAW);
//        txn.setStatus(TransactionStatus.SUCCESS);
//        txn.setTransactionTime(LocalDateTime.now());
//
//        List<TransactionDto> transactions = List.of(txn);
//        LocalDateTime now = LocalDateTime.now();
//
//        // Spy the service to override internal logic
//        ReportServiceImpl spyService = Mockito.spy(reportService);
//
//        // Use a dummy directory path that we will simulate failure for
//        String failingDirPath = tempDir.getAbsolutePath() + "/fakeDir";
//
//        // Inject the path into the spy
//        Field pathField = ReportServiceImpl.class.getDeclaredField("reportDirectoryPath");
//        pathField.setAccessible(true);
//        pathField.set(spyService, failingDirPath);
//
//        // Use a mocked File to simulate failure
//        File mockFile = Mockito.mock(File.class);
//        Mockito.when(mockFile.exists()).thenReturn(false);
//        Mockito.when(mockFile.mkdirs()).thenReturn(false);
//
//        // Override `new File()` in the spy using doReturn
//        Mockito.doReturn(mockFile).when(spyService).createFileFromPath(anyString());
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class, () ->
//            spyService.generateTransactionReport(transactions, now)
//        );
//
//        assertEquals("Failed to create report directory.", exception.getMessage());
//    }
//    
//
//}
