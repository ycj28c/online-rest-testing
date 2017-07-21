package example.google;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
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
public class ZooGoogleDataProvider extends AbstractTestNGSpringContextTests{
	
	@Autowired
	protected Environment env;
	
	@DataProvider
	public Object[][] dp() throws Exception{
//		return new Object[][] {
//			{new DataDriverModel("1","test zoos","test zoos api","http://54.219.154.2:8080/zoos","GET","","status","200")},
//			{new DataDriverModel("1","test zoos id","test zoos api id","http://54.219.154.2:8080/zoos/1","GET","","status","200")}
//		};
		
		GoogleAPIService gas = new GoogleAPIService();
		Sheets service = gas.getSheetsService(env.getProperty("CLIENT_SECRET_PATH"));
		String spreadsheetId = env.getProperty("SPREADSHEET_ID");
		String range = env.getProperty("SHEET_NAME")+"!"+env.getProperty("VALUE_RANGE");
		
//		Sheets service = Quickstart.getSheetsService();
//      String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
//      String range = "zoos!A2:H";
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
        		ddm.setValidation((String)values.get(i).get(7));
        		
        		System.out.println("ddm data:" + ddm.toString());
        		obj[i][0] = ddm;
//        		StringBuilder sb = new StringBuilder();
//        		for(int j=0;j<values.get(i).size();j++){
//        			sb.append(values.get(i).get(j) + ",");
//        		}
//				System.out.println("Row " + i + ":" + sb.toString());
//        		System.out.println("=========================================");
        	}
        }
        
        return obj;
		
	}

	@Test(dataProvider = "dp")
	public void testZoos(DataDriverModel ddm) {
//		Pharser(ddm.getRequestMethod())
		
		ValidatableResponse rs = null;
		Response source = when().get(ddm.getRequestUrl());
		rs = source.then();
		exectuteRequestMethod(ddm, source, rs);
//		rs = rs.statusCode(Integer.valueOf(ddm.getValidation()));
//		rs = rs.body("1.name", equalTo("Atascadero Charles Paddock Zoo"));
		
//		when()
//			.get(ddm.getRequestUrl())
//		.then()
//			.statusCode(Integer.valueOf(ddm.getValidation()))
//		.and()
//			.body("1.name", equalTo("Atascadero Charles Paddock Zoo"));
	}

	private ValidatableResponse exectuteRequestMethod(DataDriverModel ddm, Response source, ValidatableResponse rs) {
		if(ddm.getAction().contains("status")){
			return rs.statusCode(Integer.parseInt((String)ddm.getValidation()));
		} else if(ddm.getAction().substring(ddm.getAction().length()-".contains".length()).equalsIgnoreCase(".contains")){
			String jsonPath = ddm.getAction().substring(ddm.getAction().indexOf("(\"")+2, ddm.getAction().indexOf("\")"));
			System.out.println("jsonPath: "+jsonPath);
			if(!(source.path(jsonPath) instanceof String)){
				Assert.fail("Error, the 'contains' check only use for String type");
			}
			return rs.body(jsonPath, org.hamcrest.Matchers.containsString((String)ddm.getValidation()));
		} else if(ddm.getAction().substring(ddm.getAction().length()-".equalTo".length()).equalsIgnoreCase(".equalTo")){
			String jsonPath = ddm.getAction().substring(ddm.getAction().indexOf("(\"")+2, ddm.getAction().indexOf("\")"));
			System.out.println("jsonPath: "+jsonPath);
			if((source.path(jsonPath) instanceof String)){
				return rs.body(jsonPath, equalTo((String)ddm.getValidation()));
			} else if((source.path(jsonPath) instanceof Integer)){
				return rs.body(jsonPath, equalTo(Integer.parseInt((String)ddm.getValidation())));
			} else {
				Assert.fail("Error, the 'equalTo' check only String and Integer type");
				return null;
			}
		} else {
			return rs;
		}
	}

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//	}

}
