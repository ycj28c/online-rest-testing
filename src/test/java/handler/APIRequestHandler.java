package handler;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.api.services.sheets.v4.Sheets;
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
	
	@Autowired
	protected Environment env;
	
	@DataProvider
	public Object[][] dp() throws Exception{
		GoogleAPIService gas = new GoogleAPIService();
		Sheets service = gas.getSheetsService(env.getProperty("CLIENT_SECRET_PATH"));
		String spreadsheetId = env.getProperty("SPREADSHEET_ID");
		String range = env.getProperty("SHEET_NAME")+"!"+env.getProperty("VALUE_RANGE");
		
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
