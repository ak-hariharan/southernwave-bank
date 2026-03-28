package com.southernwavebank.notification_service.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.southernwavebank.notification_service.circuitbreaker.NotificationCircuitBreaker;
import com.southernwavebank.notification_service.exception.NotificationFailedException;
import com.southernwavebank.notification_service.external.ExternalUserServiceClient;
import com.southernwavebank.notification_service.model.AccountStatus;
import com.southernwavebank.notification_service.model.NotificationStatus;
import com.southernwavebank.notification_service.model.NotificationType;
import com.southernwavebank.notification_service.model.dto.NotificationDto;
import com.southernwavebank.notification_service.model.entity.Notification;
import com.southernwavebank.notification_service.model.externaldto.TransactionDto;
import com.southernwavebank.notification_service.repository.NotificationRepository;
import com.southernwavebank.notification_service.service.NotificationService;
import com.southernwavebank.notification_service.utils.FetchTimeAndDate;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

	private JavaMailSender mailSender;
	private NotificationRepository notificationRepository;
	private ExternalUserServiceClient externalUserServiceClient;
	private NotificationCircuitBreaker notificationCircuitBreaker;

	@Autowired
	public NotificationServiceImpl(JavaMailSender mailSender, NotificationRepository notificationRepository,
			ExternalUserServiceClient externalUserServiceClient,
			NotificationCircuitBreaker notificationCircuitBreaker) {
		this.mailSender = mailSender;
		this.notificationRepository = notificationRepository;
		this.externalUserServiceClient = externalUserServiceClient;
		this.notificationCircuitBreaker = notificationCircuitBreaker;
	}

	@Override
	public void sendEmail(NotificationDto notificationDto, String eventType) {
		log.info("Processing email notification for eventType {}", eventType);

		String briefOfEmail = switch (eventType) {
		case "USER_CREATED" -> "New user registered";
		case "OFFICER_CREATED" -> "New officer registered";
		case "ACCOUNT_CREATED" -> "Account created for new user";
		case "ACCOUNT_STATUS" -> "Account status update";
		case "TRANSACTION" -> "Transaction alert";
		case "BALANCE" -> "Balance enquiry";
		case "NO_TRANSACTION" -> "No transaction alert";
		case "OTP" -> "OTP notification";
		case "PASSWORD" -> "Password updated";
		default -> "Notification";
		};

		// Use email + briefOfEmail as unique key for idempotency
		Notification notification = notificationRepository
			    .findByEmailAndMessageAndTime(notificationDto.getEmailId(), briefOfEmail, notificationDto.getTime())
			    .orElseGet(() -> {
			        String email = notificationDto.getEmailId();
			        String contactNumber = notificationDto.getContactNumber();

			        if (email == null || contactNumber == null) {
			            ResponseEntity<?> response = externalUserServiceClient.getUserData(notificationDto.getUserId());
			            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
			                ObjectMapper mapper = new ObjectMapper();
			                Map<String, Object> userData = mapper.convertValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
			                if (email == null) {
			                    email = (String) userData.get("emailId");
			                }
			                if (contactNumber == null) {
			                    contactNumber = (String) userData.get("contactNumber");
			                }
			            }
			        }

			        Notification newNotification = new Notification();
			        newNotification.setMessage(briefOfEmail);
			        newNotification.setEmail(email);
			        newNotification.setPhoneNumber(contactNumber);
			        newNotification.setNotificationType(NotificationType.EMAIL);
			        newNotification.setTime(Optional.ofNullable(notificationDto.getTransactionTime()).orElse(notificationDto.getTime()));
			        newNotification.setNotificationStatus(NotificationStatus.PENDING);

			        return notificationRepository.save(newNotification);
			    });

		if (notification.getNotificationStatus() == NotificationStatus.SENT) {
			log.info("Email already sent to {} for event {}, skipping...", notification.getEmail(), eventType);
			return;
		}

		try {
			MimeMessage message = prepareMessage(notificationDto, eventType, notification, briefOfEmail);

			mailSender.send(message);
			notification.setNotificationStatus(NotificationStatus.SENT);
			notificationRepository.save(notification);

			log.info("Email sent successfully for event {}", eventType);

		} catch (Exception e) {
			notification.setNotificationStatus(NotificationStatus.FAILED);
			notificationRepository.save(notification);

			log.error("Failed to send email for event {}: {}", eventType, e.getMessage(), e);
			throw new NotificationFailedException("Failed to send email notification");
		}
	}


	private MimeMessage prepareMessage(NotificationDto dto, String eventType, Notification notification,
			String briefOfEmail) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		String email = notification.getEmail();
		String subject = "";
		String mainMessage = "";
		String maskedAcc = "";

		String logoUrl = "https://drive.google.com/uc?export=view&id=1JE5A8Orkt54LB4eYZB6E1b3dQLM7Jkgo";

		switch (eventType) {
		case "USER_CREATED":
		    subject = "Welcome to Southern Wave Bank!";
		    mainMessage = String.format(
		        "Dear %s,<br><br>"
		        + "Welcome to Southern Wave Bank! Your account with us allows you to access secure banking, manage your finances, and enjoy our premium services.<br><br>"
		        + "Your account has been successfully registered. Below are the details to help you log in securely:<br>"
		        + "<ul>"
		        + "<li>Your temporary password is generated as a combination of:</li>"
		        + "<ul>"
		        + "<li>First 4 letters of your username</li>"
		        + "<li>Date of registration (yyyyMMdd)</li>"
		        + "<li>'@' symbol</li>"
		        + "<li>Last 2 digits of your contact number</li>"
		        + "</ul>"
		        + "</ul>"
		        + "Please log in using this password and change it immediately for security purposes.<br><br>"
		        + "We are delighted to have you on board. Thank you for choosing Southern Wave Bank!", 
		        dto.getUsername()
		    );
		    break;

		case "OFFICER_CREATED":
		    subject = "Welcome to the Southern Wave Bank Team!";
		    mainMessage = String.format(
		        "Dear <b>%s</b>,<br><br>"
		        + "Welcome to Southern Wave Bank! Your administrative Officer account has been successfully registered by a Super Officer.<br><br>"
		        + "To ensure the highest level of security, we invite you to securely configure your own permanent password for this account before logging in.<br><br>"
		        + "Please click the secure link below to verify your identity and set your password:<br><br>"
		        + "👉 <b><a href=\"http://localhost:4200/landing?reset=true\">Click here to Set Your Password</a></b><br><br>"
		        + "<i>(Note: For security reasons, you will be prompted to verify your email address to receive a secure OTP before choosing your new password).</i><br><br>"
		        + "We are thrilled to have you on board!<br><br>"
		        + "Thank you,<br>"
		        + "Southern Wave Bank Administration", 
		        dto.getUsername()
		    );
		    break;

		case "ACCOUNT_CREATED":
			subject = "Account Successfully Created";
			mainMessage = String.format("Dear Customer,<br><br>"
					+ "We are pleased to inform you that your new account <b>%s</b> has been successfully created.<br>"
					+ "You can now perform transactions, check balances, and use all Southern Wave Bank services securely.<br><br>"
					+ "Thank you for banking with us.", dto.getAccountNumber());
			break;

		case "ACCOUNT_STATUS":
		    subject = "Account Status Update";
		    AccountStatus accountStatus = dto.getAccountStatus();
		    String additionalMessage;

		    if (accountStatus.equals(AccountStatus.ACTIVE)) {
		        additionalMessage = "You can continue to use your account for all banking transactions.";
		    } else if (accountStatus.equals(AccountStatus.CLOSED)) {
		        additionalMessage = "Your account is now closed. If this was unexpected, please contact our support team.";
		    } else {
		        additionalMessage = "For more details about this status, please reach out to our customer support.";
		    }

		    mainMessage = String.format(
		        "Dear Customer,<br><br>"
		        + "The status of your account <b>%s</b> has changed to <b>%s</b>.<br>"
		        + "%s<br><br>"
		        + "Thank you for banking with us.",
		        dto.getAccountNumber(), accountStatus, additionalMessage
		    );
		    break;


		case "TRANSACTION":
			maskedAcc = "XXXX" + dto.getAccountNumber().substring(dto.getAccountNumber().length() - 4);
			subject = "Transaction Alert";
			mainMessage = String.format("Dear Customer,<br><br>"
					+ "A <b>%s</b> of <b>₹%s</b> has been made from your account <b>%s</b> on <b>%s</b>.<br>"
					+ "If you did not authorize this transaction, please contact our customer support immediately.<br><br>"
					+ "Thank you for banking with us.", 
					dto.getTransactionType(), dto.getAmount(), maskedAcc,
					FetchTimeAndDate.getFormattedTime(dto.getTransactionTime()));
			break;

		case "BALANCE":
			maskedAcc = "XXXX" + dto.getAccountNumber().substring(dto.getAccountNumber().length() - 4);
			subject = "Balance Enquiry";
			mainMessage = String.format(
					"Dear Customer,<br><br>" + "Your current account balance for account <b>%s</b> is <b>₹%s</b>.<br>"
							+ "Please review your transactions and contact us if you notice any discrepancies.<br><br>"
							+ "Thank you for banking with us.",
					 maskedAcc, dto.getAvailableBalance());
			break;

		case "NO_TRANSACTION":
			maskedAcc = "XXXX" + dto.getAccountNumber().substring(dto.getAccountNumber().length() - 4);
			subject = "No Transaction Alert";
			mainMessage = String.format(
				    "Dear Customer,<br><br>"
				    + "There are no transactions on your account <b>%s</b> at the moment.<br>"
				    + "Enjoy the convenience of our digital banking services whenever you need them.<br><br>"
				    + "Thank you for choosing our bank.",
				    maskedAcc
				);

			break;

		case "OTP":
			subject = "OTP Notification";
			mainMessage = String
					.format("Dear Customer,<br><br>" + "Your One-Time Password (OTP) for authentication is <b>%s</b>.<br>"
							+ "This OTP is valid for the next 5 minutes. Do not share this OTP with anyone.<br><br>"
							+ "Thank you for banking with us.", dto.getOtp());
			break;

		case "PASSWORD":
			subject = "Password Updated";
			mainMessage = String.format("Dear Customer,<br><br>" + "Your password has been successfully updated.<br>"
					+ "If you did not perform this change, please contact Southern Wave Bank immediately.<br><br>"
					+ "Thank you for banking with us.");
			break;

		default:
			subject = "Notification from Southern Wave Bank";
			mainMessage = String.format("Dear Customer,<br><br>%s<br><br>Thank you for banking with us.", 
					briefOfEmail);
			break;
		}

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
				                  a.button { color: #0066cc; text-decoration: underline; font-weight: normal; font-size: 14px; margin-top: 15px; display:inline-block; }
				                </style>
				              </head>
				              <body>
				                <div class="email-container">
				                  <div class="header">
				                    <img src="%s" alt="Bank Logo" />
				                    <h2>Southern Wave Bank</h2>
				                  </div>
				                  <div class="content">
				                    %s
				                    <br/>
				                    <a href="https://yourbankwebsite.com" class="button">Visit Our Website</a>
				                  </div>
				                  <div class="footer">
				                    <p>© 2025 Southern Wave Bank. All rights reserved.</p>
				                    <p>This is an automated email. Please do not reply.</p>
				                  </div>
				                </div>
				              </body>
				            </html>
				            """
				.formatted(logoUrl, mainMessage);

		helper.setTo(email);
		helper.setSubject(subject);
		helper.setText(htmlContent, true);

		return message;
	}
	

	@Override
	public void sendEmail(List<TransactionDto> transactions, LocalDateTime time) {

	    log.info("Sending email with transaction history link");

	    if (transactions == null || transactions.isEmpty())
	        return;

	    Notification notification = new Notification();
	    notification.setNotificationStatus(NotificationStatus.PENDING);
	    notification.setNotificationType(NotificationType.EMAIL);
	    notification.setTime(time);

	    String email = "";
	    String contactNumber = "";

	    try {
	        Long userId = transactions.get(0).getUserId();
	        ResponseEntity<?> response = externalUserServiceClient.getUserData(userId);

	        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
	            ObjectMapper mapper = new ObjectMapper();
	            Map<String, Object> userData = mapper.convertValue(
	                response.getBody(), new TypeReference<Map<String, Object>>() {}
	            );
	            email = (String) userData.get("emailId");
	            contactNumber = (String) userData.get("contactNumber");
	        }

	        notification.setEmail(email);
	        notification.setPhoneNumber(contactNumber);

	        String generatedLink = notificationCircuitBreaker.genearteFromUserService(transactions, email);
	        if (generatedLink == null || !generatedLink.startsWith("http")) {
	            throw new NotificationFailedException("Invalid report link returned");
	        }

	        String directDownloadLink = generatedLink;  // default fallback
	        Pattern pattern = Pattern.compile("/d/([a-zA-Z0-9_-]+)");
	        Matcher matcher = pattern.matcher(generatedLink);

	        if (matcher.find()) {
	            String fileId = matcher.group(1);
	            directDownloadLink = "https://drive.google.com/file/d/" + fileId + "/view?usp=sharing";
	        } else {
	            log.warn("File ID extraction failed, using original link");
	        }

	        sendEmailWithDriveLink(email, directDownloadLink);

	        notification.setNotificationStatus(NotificationStatus.SENT);
	        notification.setMessage("Transaction history report sent successfully");

	    } catch (Exception e) {
	        log.error("Failed to send transaction history email: {}", e.getMessage(), e);
	        notification.setNotificationStatus(NotificationStatus.FAILED);
	        notification.setMessage("Failed to send transaction history email");
	        throw new NotificationFailedException("Email sending failed");
	    } finally {
	        notificationRepository.save(notification);
	        log.debug("Notification saved: {}", notification);
	    }
	}


	protected void sendEmailWithDriveLink(String to, String driveFileUrl) throws MessagingException {
	    log.info("Preparing email with Google Drive link for {}", to);
	    
	    MimeMessage message = mailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // enable HTML

	    helper.setTo(to);
	    helper.setSubject("Transaction History Report");

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
	                <p>Your transaction history report is ready.</p>
	                <p>
	                  <a href="%s" class="button">View Your Report</a>
	                </p>
	                <p>Thank you for banking with us.</p>
	              </div>
	              <div class="footer">
	                <p>© 2025 Southern Wave Bank. All rights reserved.</p>
	                <p>This is an automated email. Please do not reply.</p>
	              </div>
	            </div>
	          </body>
	        </html>
	        """.formatted(logoUrl, driveFileUrl);

	    helper.setText(htmlContent, true); 
	    mailSender.send(message);

	    log.info("HTML email with Drive link sent successfully to {}", to);
	}

}
