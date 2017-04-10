package example.restassured;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class MyZooTestDemo {

	/*
	 * some examples for reference
	 * http://www.programcreek.com/java-api-examples/index.php?api=com.jayway.
	 * restassured.RestAssured
	 */

	@BeforeClass
	public void setupClass() {
		RestAssured.baseURI = "http://54.219.154.2";
		RestAssured.port = 8080;
		// String sessionId =
		// given().parameters("j_username", "kyle", "j_password", "novirus")
		// .when().post("/keymgmt/j_spring_security_check").sessionId();
		// System.out.println(sessionId);
	}

	@Test
	public void testZoos() {
		when().get("/zoos").then().statusCode(200).and().body("1.name", equalTo("Atascadero Charles Paddock Zoo"));
	}

	@Test
	public void testZooById() {
		when().get("/zoos/1").then().statusCode(200).and().body("address", org.hamcrest.Matchers.containsString("Pismo Avenue"));
	}

	@Test
	public void testZooNotNull() {
		Response response = RestAssured.get("/zoos/2");
		System.out.println("Extracting response message ... ");
		String responseBody = response.body().asString();
		System.out.println("responseBody: " + responseBody);
		Assert.assertNotNull(responseBody);
	}

	@Test
	public void testGet() {
		expect().statusCode(HttpStatus.SC_OK).when().get("/zoos");
	}
	
	@Test
	public void testPost() throws Exception {
	    Response response =
	            given()
		            .header("Accept","*/*")
		            .header("Content-Type", "application/json")
		            .body("{\"address\":\"restassuredtesting\",\"animalsList\":[{\"id\":0,\"name\":\"test1\"}],\"name\":\"restassured\",\"website\":\"ycj28c.io\"}")
		            .post("/zoos");
	    System.out.println(response.asString());
	    Assert.assertEquals(200, response.statusCode());
	    System.out.println(response.headers());
	    Assert.assertTrue(response.getHeader("Transfer-Encoding").startsWith("chunk"));
		/* xml example */
//	    XmlPath receipt = new XmlPath(response.prettyPrint()).setRoot("entry");
	}
	
	@Test
	public void testDelete() throws Exception {
		Response response = delete("zoos/24");
		expect().statusCode(HttpStatus.SC_OK);
		System.out.println(response.asString());
	}
	 
	@Test(enabled=false)
	/* don't have authentication for this app*/
	public void testAuthentication() throws Exception {
		RestAssured.authentication = form("John", "Doe");

	    try {
	    expect().
	            statusCode(200).
	            body(equalTo("OK")).
	    when().
	            get("/zoos");
	    } finally {
//	        RestAssured.reset();
	    }
	}

}
