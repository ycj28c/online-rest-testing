package handler;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

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
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ClearValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;

import api.ApiRunner;
import config.CommonConfig;
import config.SpreadSheetSingleConfig;
import config.TestConfig;
import google.SpreadSheetOperater;
import google.SpreadSheetSingleProperties;
import util.DataDriverModel;
import util.RequestMethodUtil;

@ContextConfiguration(classes = { TestConfig.class, CommonConfig.class, SpreadSheetSingleConfig.class  })
@TestPropertySource(locations = { "classpath:spreadsheet-${spreadsheet:default}.properties" })
public class APIRequestHandler extends AbstractTestNGSpringContextTests{
	
	@Autowired
	protected Environment env;
	
	@Autowired
	protected SpreadSheetSingleProperties spreadSheetConn;
	
	@BeforeClass
	public void initConnection(){
		try{
			/* clear the table */
			if(spreadSheetConn.isCleanContent()){
				ClearValuesRequest requestBody = new ClearValuesRequest();
				Sheets.Spreadsheets.Values.Clear request = spreadSheetConn.getService().spreadsheets().values()
						.clear(spreadSheetConn.getSpreadsheetId(), spreadSheetConn.getCleanRange(), requestBody);
			    ClearValuesResponse response = request.execute();
			    System.out.println(response);
			}
		} catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@DataProvider
	private Object[][] spreadSheetProvider() throws Exception{
        ValueRange response = spreadSheetConn.getService().spreadsheets().values()
            .get(spreadSheetConn.getSpreadsheetId(), spreadSheetConn.getTest_result_range())
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
        		
        		String requestMethodStr = (String)values.get(i).get(4);
        		ddm.setRequestMethod(RequestMethodUtil.convertRequetMethod(requestMethodStr));
        		ddm.setPayload((String)values.get(i).get(5));
        		ddm.setAction((String)values.get(i).get(6));
        		ddm.setValidation(values.get(i).get(7));
        		
        		System.out.println("ddm data:" + ddm.toString());
        		obj[i][0] = ddm;
        	}
        }
        return obj;
	}

	@Test(dataProvider = "spreadSheetProvider")
	public void testAPICalls(DataDriverModel ddm) {
		System.out.println(ddm.getId() + "." + ddm.getName() + ":" + ddm.getDescription());
		ValidatableResponse rs = null;
		Response source = null;
		
		switch(ddm.getRequestMethod()){
			case POST:
				/* post only support the json so far */
				RequestSpecification rsf = given().body(ddm.getPayload()).contentType("application/json");
				source = rsf.when().post(ddm.getRequestUrl());
				break;
			case GET:
				source = when().get(ddm.getRequestUrl());
				break;
			default:
				Assert.fail("Error, the RequestMethod '"+ddm.getRequestMethod()+"' is not supported");
		}

		rs = source.then();
		ApiRunner.exectuteRequestMethod(ddm, source, rs);
	}
	
	@AfterMethod
	public void teardown(ITestResult result) throws Exception {
		SpreadSheetOperater.OutputSingleTestResult(result, spreadSheetConn);
	}

}
