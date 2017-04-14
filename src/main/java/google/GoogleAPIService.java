package google;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

public class GoogleAPIService {
	/** Application name. */
	private static String APPLICATION_NAME = null;
	private static String DEFAULT_APPLICATION_NAME = "Google Sheets API";

	/** Directory to store user credentials for this application. */
	private static java.io.File DATA_STORE_DIR = null;

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	private static String CLIENT_SECRET = "/client_secret.json";

	/**
	 * Global instance of the scopes required by this quickstart.
	 *
	 * If modifying these scopes, delete your previously saved credentials at
	 * ~/.credentials/sheets.googleapis.com-java-quickstart
	 */
	private static List<String> SCOPES;

	public GoogleAPIService(String APPLICATION_NAME) {
		GoogleAPIService.APPLICATION_NAME = APPLICATION_NAME;
		GoogleAPIService.SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);
		DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/sheets.googleapis.com");
		setEnvironement();
	}

	public GoogleAPIService() {
		GoogleAPIService.APPLICATION_NAME = DEFAULT_APPLICATION_NAME;
//		GoogleAPIService.SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY);
		GoogleAPIService.SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);
		DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/sheets.googleapis.com");
		setEnvironement();
	}

	public void setEnvironement() {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @return an authorized Credential object.
	 * @throws Exception
	 */
	private static Credential authorize(String secretPath) throws Exception {
		// Load client secrets.
		InputStream in = GoogleAPIService.class.getResourceAsStream(secretPath);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}

	/**
	 * Build and return an authorized Sheets API client service.
	 * 
	 * @return an authorized Sheets API client service
	 * @throws Exception
	 */
	public Sheets getSheetsService(String secretPath) throws Exception {
		Credential credential = authorize(secretPath);
		return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
				.build();
	}

	public Sheets getSheetsService() throws Exception {
		return getSheetsService(CLIENT_SECRET);
	}

}
