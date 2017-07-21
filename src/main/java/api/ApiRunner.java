package api;

import org.testng.Assert;
import static org.hamcrest.Matchers.equalTo;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;

import util.DataDriverModel;

public class ApiRunner {
	
	/**
	 * run the API base on the url/requestmethod/payload/action/validation, return the API test result.
	 * use restAssured library, for detail grammer, please check http://rest-assured.io/
	 * @param ddm
	 * @param source
	 * @param rs
	 * @return
	 */
	public static ValidatableResponse exectuteRequestMethod(DataDriverModel ddm, Response source, ValidatableResponse rs) {
		try {
			source.path("."); //check if the response is json format
			return exectuteRequestMethodJson(ddm, source, rs);
		} catch(com.jayway.restassured.path.json.exception.JsonPathException e){
			System.out.println("** the response is not json format");
			return exectuteRequestMethodText(ddm, source, rs);
		} catch(IllegalStateException ex){
			System.out.println("** the server response :"+ ex);
			return exectuteRequestMethodText(ddm, source, rs);
		}
		/* TODO: xml */
	}
	
	/**
	 * run the API call when the response is JSON format
	 * @param ddm
	 * @param source
	 * @param rs
	 * @return
	 */
	private static ValidatableResponse exectuteRequestMethodJson(DataDriverModel ddm, Response source, ValidatableResponse rs) {
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
	
	/**
	 * run the API call when the response is text format
	 * @param ddm
	 * @param source
	 * @param rs
	 * @return
	 */
	private static ValidatableResponse exectuteRequestMethodText(DataDriverModel ddm, Response source, ValidatableResponse rs) {
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
