online-rest-testing
===================

purpose
-------

It is a light API automation test framework.
It is data driver with keywords action for API testing.
After setting the google authentication API, you're able to easily write test cases in google spreadsheet and run it in command line.

Environment requirement
-----------------------
	Java >1.7
	MAVEN >3.0.3
	Google Account

How to setup google API
-----------------------
Since we are using google spread sheet for Data Drive test, please connect to the google spreadsheet follow by manual:
	
	https://developers.google.com/sheets/api/quickstart/java

* Tips:
When first time running automation test, test program will connect to the google spreadsheet, google will require the spreadsheet owner to authenticate the connection. The problem may happen when you connect by jenkins, since jenkins remote machine will not pop out the browser for you to authenticate, you need to do below trick:

First run below, you got authenticate URL in browser like this:
	
	https://accounts.google.com/o/oauth2/auth?access_type=offline&client_id={your client id, such as xxxxxxxxxxxxxxxxxxxxxxx.apps.googleusercontent.com}&redirect_uri=http://localhost:56942/Callback&response_type=code&scope=https://www.googleapis.com/auth/spreadsheets

Then run in jenkins, find the jenkins console log:
	
	18:13:54.478 [main] INFO org.mortbay.log - Started SocketConnector@localhost:56384

replace the port in URL, and run it in your local, the authenticate can be done:

	https://accounts.google.com/o/oauth2/auth?access_type=offline&client_id={your client id, such as xxxxxxxxxxxxxxxxxxxxxxx.apps.googleusercontent.com}&redirect_uri=http://localhost:56384/Callback&response_type=code&scope=https://www.googleapis.com/auth/spreadsheets

Configuration
-------------
There are two configuration files:

* client_secret.json
This is the google credential file download from google, please follow manullay to setup [Java Quickstart](https://developers.google.com/sheets/api/quickstart/java)

1. Use [this wizard](https://console.developers.google.com/flows/enableapi?apiid=sheets.googleapis.com) to create or select a project in the Google Developers Console and automatically turn on the API. Click Continue, then Go to credentials.
2. On the Add credentials to your project page, click the Cancel button.
3. At the top of the page, select the OAuth consent screen tab. Select an Email address, enter a Product name if not already set, and click the Save button.
4. Select the Credentials tab, click the Create credentials button and select OAuth client ID.
5. Select the application type Other, enter the name "Google Sheets API Quickstart", and click the Create button.
6. Click OK to dismiss the resulting dialog.
7. Click the file_download (Download JSON) button to the right of the client ID.
8. Move this file to your working directory and rename it client_secret.json.

* Single mode: spreadsheet-${name}.properties
Example:
```	
# google oauth2 credential, point relative path to your project root
CLIENT_SECRET_PATH = /client_secret.json
	
# the google spreadsheet id https://docs.google.com/spreadsheets/d/xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
SPREADSHEET_ID = xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	
# the sheet name tag
SHEET_NAME = zoos
	
# the data range of target sheet
VALUE_RANGE = A2:H
```

* Bulk mode: spreadsheet-bulk-${name}.properties 
Example:
```	
# google spreadsheet connection
SPREADSHEET_ID = xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

# test cases
TEST_CASE_SPREADSHEET_GID = 11111111
TEST_CASE_SHEET_NAME = API-TestCases
TEST_CASE_VALUE_RANGE = A2:H

# test results
TEST_RESULT_SPREADSHEET_GID = 22222222
TEST_RESULT_SHEET_NAME = API-Results
TEST_RESULT_VALUE_RANGE = A2:L

# variables
VARIABLE_SPREADSHEET_GID = 33333333
VARIABLE_SHEET_NAME = GlobalVaribles
VARIABLE_VALUE_RANGE = A2:E

# test parameter
CLEAN_CONTENT = true
CLEAN_RANGE = A2:L
```

Write API Test
--------------

| ID | NAME           | DESCRIPTION          | REQUEST_URL                   |REQUEST_METHOD| PAYLOAD | ACTION              | VALIDATION                   |
|----|:--------------:|:--------------------:|:-----------------------------:|:------------:|:-------:|:-------------------:|:----------------------------:|
| 1  |test get        |test zoos api get     |http://54.219.154.2:8080/zoos  |GET           |         | status              |200                           |
| 2	 |test contains	  |test zoos api contains|http://54.219.154.2:8080/zoos/1|GET           |         |("1.name").contains  |Atascadero Charles Paddock Zoo|
| 3	 |test equalTo	  |test zoos api equal	 |http://54.219.154.2:8080/zoos/2|GET           |         |("2.website").equalTo|bigbearzoo.org                |

+ ID: the test id order
+ Name:           the test name
+ Description:    describe your test purpose
+ Request_url:    the request API URL with path
+ Request_method: request type (only get so far)
+ Payload:        the payload send alone with URL request
+ Action:         test check action
+ Validation:     test check expectation

Example Google SpreadSheet Link: [Zoos API Google SpreadSheet Example](https://docs.google.com/spreadsheets/d/1oqaZoH4b1wfLr34iLqeppQVT5R-Qnmh3sdmbswlHRMM/edit?usp=sharing)

Example Google SpreadSheet Link: [More Functional Zoos API SpreadSheet Example](https://docs.google.com/spreadsheets/d/1oqaZoH4b1wfLr34iLqeppQVT5R-Qnmh3sdmbswlHRMM/edit#gid=1759607121)

current support action command(json and text):
+ contains: is the response json node or text contains string
+ equalTo:  is the response json node or text equal to number or string 
+ status:   is HTTP response response status equals to validation
+ isNull:   is response null

Normal Testing Mode
===================

How to run
----------
We can run in by MAVEN command line

	mvn test -Dspreadsheet=zoos
	mvn test -Dspreadsheet=zoos -Dhandler=APIRequestHandler

It will grab the configuration in /test/resources/spreadsheet-${name}.properties (in this case is zoos)

Bulk Testing Mode
=================

How to run
----------
We can run in by MAVEN command line, here we must use the APIRequestBulkHandler to let it run in bulk mode

	mvn test -Dspreadsheet=zoos -Dhandler=APIRequestBulkHandler -Ddb=zoodb

If you need to run same API test for a series of test with different parameter, you can use bulk API testing mode.
Now it accept the string/JSON/query format variable injection to support your bulk test.

In bulk API testing, there will have 3 types of spreadsheets:
1. GlobalVaribles spreadsheet
Here is the place to put your global variables, current support string/JSON/query variables

| ID | VARIABLE_NAME  | VARIABLE_DESCRIPTION | VARIABLE_TYPE |VALUE                        |
|----|:--------------:|:--------------------:|:-------------:|:---------------------------:|
| 1  |zooid           |zoo ids               |string         |1,2,3,4,5,6,7,8              |
| 2	 |animals	      |animal species        |query          |select id, name from animals |
| 3	 |jsontest	      |json example          |json           |[{"a":1,"b":2},{"a":3,"b":4}]|
|... |...	          |...                   |...            |....                         |

* If String, the variable will split by comma, when inject into test cases, the test case will go over each situation of the split value. 
* If JSON, the variable if parser with json format, will inject into test cases by each block, for example: for "[{"a":1,"b":2},{"a":3,"b":4}]" has two test parameter block, {"a":1,"b":2} is first block, "{"a":3,"b":4}" is second block. (currently only support one hierarchy json array).
* If Query, the variable spreadsheet is able to query the database, the usage is same as JSON, for example: "select id, name from animals" mean get id and name value from animals table, then you're able to inejct parameter in test cases spreadsheet, such as {{animals.id}}, {{animals.name}}.
To use the query variable, we need to set up the database connection properties like below(db-***.properties in src/test/resources). 
```
	product=xxx
	xxx.driverClassName=org.postgresql.Driver
	xxx.url=jdbc:postgresql://1.1.1.1:5432/xxx
	xxx.username=xxx
	xxx.password=xxx
	xxx.maxPoolSize = 5
```	
When run the test, need to add the correct db parameters:

	mvn test -Dspreadsheet=zoos -Dhandler=APIRequestBulkHandler -Ddb={dbname}
	
Example Bulk Google SpreadSheet Link: [Zoos API Google SpreadSheet Example GlobalVariable](https://docs.google.com/spreadsheets/d/17QhgC_kszjrniVQEbcRpprElDTjw6-QcyLIRPyLvkbg/edit#gid=530185914)
	
2. TestResult spreadsheet
It is the same as normal testing mode spreadsheet, but this spreadsheet is read-only, according to the test cases and the variable, display the test result into this spreadsheet

Example Bulk Google SpreadSheet Link: [Zoos API Google SpreadSheet Example Test Result](https://docs.google.com/spreadsheets/d/17QhgC_kszjrniVQEbcRpprElDTjw6-QcyLIRPyLvkbg/edit#gid=1118839030)

3. TestCase spreadsheet, look like below

| ID | NAME           | DESCRIPTION          | REQUEST_URL                   |REQUEST_METHOD| PAYLOAD | ACTION              | VALIDATION                   |
|----|:--------------:|:--------------------:|:-----------------------------:|:------------:|:-------:|:-------------------:|:----------------------------:|
| 1  |test get        |test zoos api get     |http://54.219.154.2:8080/zoos  |GET           |         | status              |200                           |
| 2	 |test contains	  |test zoos api contains|http://54.219.154.2:8080/zoos/{zooid}|GET           |         |("1.name").contains  |Atascadero Charles Paddock Zoo|
| 3	 |test equalTo	  |test zoos api equal	 |http://54.219.154.2:8080/zoos/2/{animals.name}|GET           |         |("2.website").equalTo|bigbearzoo.org                |

This part is to add the test cases, similar to the normal testing, the things different is we can injection the variable into the REQUEST_URL/REQUEST_METHOD/PAYLOAD/ACTION/VALIDATION.
* If it is string variable, we can inject like {{string-name}}
* If it is JSON variable, we can inject like {{JSON-name.a}}, {{json.b}}
* If it is query variable, we can inject like {{query-name.col1}}, {{query-name.col2}}
We can inject the parameter with multiple combinations, such as string + string, string + json, json + query, string + json + query. According to the variables you have, the test will automatically run each parameter combination, print the result in TestResult spreadsheet.

Example Bulk Google SpreadSheet Link: [Zoos API Google SpreadSheet Example Test Case](https://docs.google.com/spreadsheets/d/17QhgC_kszjrniVQEbcRpprElDTjw6-QcyLIRPyLvkbg/edit#gid=64827870)



