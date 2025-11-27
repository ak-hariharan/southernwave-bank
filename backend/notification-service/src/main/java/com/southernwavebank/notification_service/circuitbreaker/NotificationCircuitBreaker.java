package com.southernwavebank.notification_service.circuitbreaker;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.southernwavebank.notification_service.external.ExternalReportServiceClient;
import com.southernwavebank.notification_service.model.externaldto.TransactionDto;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationCircuitBreaker {
	
	private JavaMailSender mailSender;
	private  ExternalReportServiceClient externalReportServiceClient;
	
	@Autowired
	public NotificationCircuitBreaker(JavaMailSender mailSender,
			ExternalReportServiceClient externalReportServiceClient) {
		this.mailSender = mailSender;
		this.externalReportServiceClient = externalReportServiceClient;
	}
    
    @CircuitBreaker(name = "reportServiceCircuitBreaker", fallbackMethod = "fallbackGenerateReportFromReportService")
    public String genearteFromUserService(List<TransactionDto> transactions, String email) {
         return externalReportServiceClient.generateReport(transactions);
    }

    // Fallback method
    public String fallbackGenerateReportFromReportService(List<TransactionDto> transactions, String email, Throwable t) {
        log.error("Fallback triggered for generateReportFromTransactionService: {}", t.getMessage());

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // enable HTML
            helper.setTo(email);
            helper.setSubject("Transaction Report Unavailable");

            String logoUrl = "https://drive.google.com/uc?export=view&id=1JE5A8Orkt54LB4eYZB6E1b3dQLM7Jkgo";

            String htmlContent = """
                <html>
                  <head>
                    <style>
                      body { font-family: Arial, sans-serif; background-color: #f5f7fa; margin:0; padding:0; }
                      .email-container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); overflow: hidden; }
                      .header { background-color: #003366; color: #ffffff; padding: 10px 15px; display: flex; align-items: center; }
                      .header img { height: 50px; margin-right: 15px; vertical-align: middle; }
                      .header h2 { flex: 1; margin: 0; font-size: 20px; line-height: 50px;text-align: center; }
                      .content { padding: 25px; font-size: 15px; color: #333; line-height: 1.6; }
                      .footer { background-color: #f0f0f0; text-align: center; padding: 15px; font-size: 13px; color: #666666; }
                      a.button { background-color: #0066cc; color: #ffffff; text-decoration: none; padding: 10px 15px; border-radius: 4px; display: inline-block; margin-top: 15px; }
                    </style>
                  </head>
                  <body>
                    <div class="email-container">
                      <div class="header">
                        <img src="%s" alt="Bank Logo" />
                        <h2>Southern Wave Bank</h2>
                      </div>
                      <div class="content">
                        <p>Dear Customer,</p>
                        <p>Due to an unexpected system issue, we are currently unable to generate your transaction report.</p>
                        <p>Please try again later. We apologize for the inconvenience.</p>
                        <p>Thank you for banking with us.</p>
                      </div>
                      <div class="footer">
                        <p>© 2025 Southern Wave Bank. All rights reserved.</p>
                        <p>This is an automated email. Please do not reply.</p>
                      </div>
                    </div>
                  </body>
                </html>
                """.formatted(logoUrl);

            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("Fallback HTML email sent successfully to {}", email);

        } catch (MessagingException e) {
            log.error("Failed to send fallback email to {}: {}", email, e.getMessage(), e);
        }

        return null;
    }

    
}
        