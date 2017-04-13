package handler;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridCoordinate;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;

import config.CommonConfig;
import config.TestConfig;
import google.GoogleAPIService;
import util.DataDriverModel;

@ContextConfiguration(classes = { TestConfig.class, CommonConfig.class })
@TestPropertySource(locations = { "classpath:spreadsheet-${spreadsheet:default}.properties" })
public class APIRequestHandler extends AbstractTestNGSpringContextTests{
	
	private Sheets service = null;
	private String spreadsheetId = null;
	private int spreadsheetGid = 0;
	private String range = null;
	private int index = 1;
	
	@Autowired
	protected Environment env;
	
	@BeforeClass
	public void initConnection() throws Exception{
		GoogleAPIService gas = new GoogleAPIService();
		service = gas.getSheetsService(env.getProperty("CLIENT_SECRET_PATH"));
		spreadsheetId = env.getProperty("SPREADSHEET_ID");
		spreadsheetGid = env.getRequiredProperty("SPREADSHEET_GID", Integer.class);
		range = env.getProperty("SHEET_NAME")+"!"+env.getProperty("VALUE_RANGE");
	}
	
	@DataProvider
	public Object[][] dp() throws Exception{
        ValueRange response = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute();
        List<List<Object>> values = response.getValues();
		
		Object obj[][] = new Object[values.size()][1];
        if (values == null || values.size() == 0) {
            System.out.println("No data found.");
        } else {
        	for(int i=0;i<values.size();i++){
        		DataDriverModel ddm = new DataDriverModel();
        		ddm.setId((String)values.get(i).get(0));
        		ddm.setName((String)values.get(i).get(1));
        		ddm.setDescription((String)values.get(i).get(2));
        		ddm.setRequestUrl((String)values.get(i).get(3));
        		ddm.setRequestMethod((String)values.get(i).get(4));
        		ddm.setPayload((String)values.get(i).get(5));
        		ddm.setAction((String)values.get(i).get(6));
        		ddm.setValidation(values.get(i).get(7));
        		
        		System.out.println("ddm data:" + ddm.toString());
        		obj[i][0] = ddm;
        	}
        }
        return obj;
	}

	@Test(dataProvider = "dp")
	public void testAPICalls(DataDriverModel ddm) {
		System.out.println(ddm.getId() + "." + ddm.getName() + ":" + ddm.getDescription());
		ValidatableResponse rs = null;
		Response source = when().get(ddm.getRequestUrl());
		rs = source.then();
		exectuteRequestMethod(ddm, source, rs);
	}
	
	@AfterMethod
	public void teardown(ITestResult result) throws Exception {
//		DataDriverModel ddm = (DataDriverModel) result.getParameters()[0];
		String errorLog = null;
		try{
			errorLog = result.getThrowable().getMessage();
//			System.out.println("eventName: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+ ddm.getId()+", "+result.getStatus()+","+result.isSuccess()+","+result.getThrowable());
		}catch(Exception e){}
		
		String testStatus = null;
		switch (result.getStatus()) {
			case (ITestResult.FAILURE):
				testStatus = "Failure";
				break;
			case (ITestResult.SKIP):
				testStatus = "Skip";
				break;
			case (ITestResult.STARTED):
				testStatus = "Started";
				break;
			case (ITestResult.SUCCESS):
				testStatus = "Success";
				break;
			case (ITestResult.SUCCESS_PERCENTAGE_FAILURE):
				testStatus = "Success_Percentage_Failure";
				break;
			default:
				testStatus = "Null";
				break;
		}
		
//		//
//		List<Request> requests = new ArrayList<Request>();
//		// Change the spreadsheet's title.
//		requests.add(new Request()
//		        .setUpdateSpreadsheetProperties(new UpdateSpreadsheetPropertiesRequest()
//		                .setProperties(new SpreadsheetProperties()
//		                        .setTitle("Template"))
//		                .setFields("A2")));
//		// Find and replace text.
//		requests.add(new Request()
//		        .setFindReplace(new FindReplaceRequest()
//		                .setFind("12")
//		                .setReplacement("22")
//		                .setAllSheets(false)));
//		// Add additional requests (operations) ...
//
//		BatchUpdateSpreadsheetRequest body =
//		        new BatchUpdateSpreadsheetRequest().setRequests(requests);
//		BatchUpdateSpreadsheetResponse response =
//		        service.spreadsheets().batchUpdate(spreadsheetId, body).execute();
//		FindReplaceResponse findReplaceResponse = response.getReplies().get(1).getFindReplace();
//		System.out.printf("%d replacements made.", findReplaceResponse.getOccurrencesChanged());
	    
		/* http://stackoverflow.com/questions/38486286/write-data-into-google-spreadsheet-w-java 
		 * http://stackoverflow.com/questions/39629260/insert-row-without-overwritting-data
		 * */
		List<Request> requests = new ArrayList<Request>();
		List<CellData> values_Test_Result = new ArrayList<CellData>();
		List<CellData> values_Test_Log = new ArrayList<CellData>();

		int curIndex = index;
		values_Test_Result.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(testStatus)));
		requests.add(new Request().setUpdateCells(
				new UpdateCellsRequest().setStart(new GridCoordinate()
						.setSheetId(spreadsheetGid) //#gid=111111111111
						.setRowIndex(curIndex)
						.setColumnIndex(8))
						.setRows(Arrays.asList(new RowData().setValues(values_Test_Result)))
						.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));
		
		values_Test_Log.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(errorLog)));
		requests.add(new Request().setUpdateCells(
				new UpdateCellsRequest().setStart(new GridCoordinate()
						.setSheetId(spreadsheetGid) //#gid=1111111111111
						.setRowIndex(curIndex)
						.setColumnIndex(9))
						.setRows(Arrays.asList(new RowData().setValues(values_Test_Log)))
						.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));

		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
		service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
		
		index++;
	}
	
	private ValidatableResponse exectuteRequestMethod(DataDriverModel ddm, Response source, ValidatableResponse rs) {
		try {
			source.path("."); //check if the response is json format
			return exectuteRequestMethodJson(ddm, source, rs);
		} catch(com.jayway.restassured.path.json.exception.JsonPathException e){
			System.out.println("** the response is not json format");
			return exectuteRequestMethodText(ddm, source, rs);
		}
		/* TODO: xml */
	}
	
	private ValidatableResponse exectuteRequestMethodJson(DataDriverModel ddm, Response source, ValidatableResponse rs) {
		if(ddm.getAction().contains("status")){
			return rs.statusCode(Integer.parseInt((String)ddm.getValidation()));
		} else if (ddm.getAction().length() >= ".contains".length() && ddm.getAction()
				.substring(ddm.getAction().length() - ".contains".length()).equalsIgnoreCase(".contains")) {
			String jsonPath = ddm.getAction().substring(ddm.getAction().indexOf("(\"")+2, ddm.getAction().indexOf("\")"));
			System.out.println("jsonPath: "+jsonPath);
			if(!(source.path(jsonPath) instanceof String)){
				Assert.fail("Error, the 'contains' check only use for String type");
			}
			return rs.body(jsonPath, org.hamcrest.Matchers.containsString((String)ddm.getValidation()));
		} else if (ddm.getAction().length() >= ".equalTo".length() && ddm.getAction()
				.substring(ddm.getAction().length() - ".equalTo".length()).equalsIgnoreCase(".equalTo")) {
			String jsonPath = ddm.getAction().substring(ddm.getAction().indexOf("(\"")+2, ddm.getAction().indexOf("\")"));
			System.out.println("jsonPath: "+jsonPath);
			if(source.path(jsonPath) instanceof String){
				return rs.body(jsonPath, equalTo((String)ddm.getValidation()));
			} else if(source.path(jsonPath) instanceof Integer){
				return rs.body(jsonPath, equalTo(Integer.parseInt((String)ddm.getValidation())));
			} else if(source.path(jsonPath) instanceof Float){
				return rs.body(jsonPath, equalTo(Float.parseFloat((String)ddm.getValidation())));
			} else if(source.path(jsonPath) instanceof Boolean){
				return rs.body(jsonPath, equalTo(Boolean.parseBoolean((String)ddm.getValidation())));
			} else {
				Assert.fail("Error, the 'equalTo' check only String, Integer or Float type");
				return null;
			}
		} else if (ddm.getAction().length() >= ".isNull".length() && ddm.getAction()
				.substring(ddm.getAction().length() - ".isNull".length()).equalsIgnoreCase(".isNull")) {
			String jsonPath = ddm.getAction().substring(ddm.getAction().indexOf("(\"")+2, ddm.getAction().indexOf("\")"));
			System.out.println("jsonPath: "+jsonPath);
			if(Boolean.parseBoolean((String)ddm.getValidation())){
				Assert.assertNull(source.path(jsonPath));
			} else if(((String)ddm.getValidation()).equalsIgnoreCase("false")){
				Assert.assertNotNull(source.path(jsonPath));
			} else {
				Assert.fail("Error, the 'VALIDATION' should be either false or true where check 'isNull'");
			}
			return null;
		} else {
			return rs;
		}
	}
	
	private ValidatableResponse exectuteRequestMethodText(DataDriverModel ddm, Response source, ValidatableResponse rs) {
		if(ddm.getAction().contains("status")){
			return rs.statusCode(Integer.parseInt((String)ddm.getValidation()));
		} else if (ddm.getAction().length() >= "contains".length() && ddm.getAction()
				.substring(ddm.getAction().length() - "contains".length()).equalsIgnoreCase("contains")) {
			String responseString = source.asString();
			Assert.assertTrue(responseString.indexOf((String)ddm.getValidation())!=-1);
			return rs;
		} else if (ddm.getAction().length() >= "equalTo".length() && ddm.getAction()
				.substring(ddm.getAction().length() - "equalTo".length()).equalsIgnoreCase("equalTo")) {
			String responseString = source.asString();
			Assert.assertTrue(responseString.equalsIgnoreCase((String)ddm.getValidation()));
			return rs;
		} else if (ddm.getAction().length() >= "isNull".length()
				&& ddm.getAction().substring(ddm.getAction().length() - "isNull".length()).equalsIgnoreCase("isNull")) {
			String responseString = source.asString();
			Assert.assertTrue(!responseString.trim().isEmpty());
			return rs;
		} else {
			return rs;
		}
	}

}
