package handler;

import static com.jayway.restassured.RestAssured.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
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

import api.ApiRunner;
import config.CommonConfig;
import config.DatabaseConfig;
import config.SpreadSheetBulkConfig;
import config.TestConfig;
import google.SpreadSheetBulkProperties;
import google.SpreadSheetOperater;
import parser.TestCaseParser;
import parser.VariableParser;
import util.DataDriverModel;

@ContextConfiguration(classes = { TestConfig.class, CommonConfig.class, DatabaseConfig.class, SpreadSheetBulkConfig.class })
@TestPropertySource(locations = { "classpath:spreadsheet-bulk-${spreadsheet:default}.properties" })
public class APIRequestBulkHandler extends AbstractTestNGSpringContextTests{
	
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	
	@Autowired
	protected Environment env;
	
	@Autowired
	protected SpreadSheetBulkProperties spreadSheetConn;

	private Integer MAXIMUM_DDM_PER_ROW = 50;
	
	private HashMap<String, List<?>> variableMap = new HashMap<String, List<?>>(); //must be string to read? any thought
	
	@BeforeClass
	public void initConnection(){
		try{
			VariableParser.generateVaribleMap(spreadSheetConn, variableMap, jdbcTemplate);
			
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
	
	/**
	 * read data from spreadsheet and provider for the test
	 */
	@DataProvider
	private Object[][] spreadSheetProvider() throws Exception{
//        ValueRange response = service.spreadsheets().values()
//            .get(spreadsheetId, test_case_range)
//            .execute();
		ValueRange response = spreadSheetConn.getService().spreadsheets().values()
				.get(spreadSheetConn.getSpreadsheetId(), spreadSheetConn.getTest_case_range()).execute();
        List<List<Object>> values = response.getValues();
        List<DataDriverModel> ddmList = new ArrayList<DataDriverModel>();
		
        if (values == null || values.size() == 0) {
            System.out.println("No data found.");
        } else {
        	for(int i=0;i<values.size();i++){
				List<DataDriverModel> ddms = TestCaseParser.translateDataProviderParameter(values.get(i), variableMap);
				System.out.println("Total DDM size: " + ddms.size());
				System.out.println("We only allow run maximum " + MAXIMUM_DDM_PER_ROW + " DDM for one row test");
				System.out.println("If more than limit test, will pick random " + MAXIMUM_DDM_PER_ROW + " tests");
				if (ddms.size() > MAXIMUM_DDM_PER_ROW) {
					Collections.shuffle(ddms);
					for (int cnt = 0; cnt < MAXIMUM_DDM_PER_ROW; cnt++) {
						ddmList.add(ddms.get(cnt));
					}
				} else {
					ddmList.addAll(ddms);
				}
        	}
        }
        
        Object obj[][] = new Object[ddmList.size()][1];
        for(int i=0;i<ddmList.size();i++){
        	obj[i][0] = ddmList.get(i);
        }
        return obj;
	}

	@Test(dataProvider = "spreadSheetProvider")
	public void testAPICalls(DataDriverModel ddm) {
		System.out.println(ddm.getId() + "." + ddm.getName() + ":" + ddm.getDescription());
		ValidatableResponse rs = null;
		Response source = when().get(ddm.getRequestUrl());
		rs = source.then();
		ApiRunner.exectuteRequestMethod(ddm, source, rs);
	}
	
	@AfterMethod
	public void teardown(ITestResult result) {
		DataDriverModel ddm = (DataDriverModel) result.getParameters()[0];
		
		SpreadSheetOperater.OutputBulkTestResult(ddm, result, spreadSheetConn);
	}

}
