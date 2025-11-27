package com.southernwavebank.report_service.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleDriveService {

    private static final String APPLICATION_NAME = "Spring Boot PDF Uploader";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance(); 
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static Drive driveService;

    public static Drive getDriveService() throws IOException, GeneralSecurityException {
        if (driveService != null) return driveService;

        InputStream in = GoogleDriveService.class.getResourceAsStream("/credentials.json");
        if (in == null) {
            throw new FileNotFoundException("Resource not found: credentials.json");
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                clientSecrets,
                SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        Credential credential = new AuthorizationCodeInstalledApp(
                flow,
                new LocalServerReceiver.Builder().setPort(8888).build()
        ).authorize("user");


        driveService = new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        return driveService;
    }

    
	public static String uploadFile(java.io.File file, String mimeType, String folderId)
			throws IOException, GeneralSecurityException {
		File fileMetadata = new File();
		fileMetadata.setName(file.getName());
		if (folderId != null && !folderId.isEmpty()) {
			fileMetadata.setParents(Collections.singletonList(folderId));
		}
		FileContent mediaContent = new FileContent(mimeType, file);
		File uploadedFile = getDriveService().files().create(fileMetadata, mediaContent).setFields("id, name")
				.execute();

		return uploadedFile.getId();
	}

}
