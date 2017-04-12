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
	
	https://accounts.google.com/o/oauth2/auth?access_type=offline&client_id=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.apps.googleuserconte	nt.com&redirect_uri=http://localhost:56942/Callback&response_type=code&scope=https://www.googleapis.com/auth/spreadsheets.readonly

Then run in jenkins, find the jenkins console log:
	
	18:13:54.478 [main] INFO org.mortbay.log - Started SocketConnector@localhost:56384

replace the port in URL, and run it in your local, the authenticate can be done:

	https://accounts.google.com/o/oauth2/auth?access_type=offline&client_id=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.apps.googleuserconte	nt.com&redirect_uri=http://localhost:56384/Callback&response_type=code&scope=https://www.googleapis.com/auth/spreadsheets.readonly

How to run
----------
We can run in by MAVEN command line

	mvn test -Dspreadsheet=zoos

It will grab the configuration in /test/resources/spreadsheet-${name}.properties (in this case is zoos)

Configuration
-------------
There are two configuration files:

* client_secret.json
This is the google credential file download from google, please follow manullay to setup [Java Quickstart](https://developers.google.com/sheets/api/quickstart/java)
1.Use [this wizard](https://console.developers.google.com/flows/enableapi?apiid=sheets.googleapis.com) to create or select a project in the Google Developers Console and automatically turn on the API. Click Continue, then Go to credentials.
2.On the Add credentials to your project page, click the Cancel button.
3.At the top of the page, select the OAuth consent screen tab. Select an Email address, enter a Product name if not already set, and click the Save button.
4.Select the Credentials tab, click the Create credentials button and select OAuth client ID.
5.Select the application type Other, enter the name "Google Sheets API Quickstart", and click the Create button.
6.Click OK to dismiss the resulting dialog.
7.Click the file_download (Download JSON) button to the right of the client ID.
8.Move this file to your working directory and rename it client_secret.json.

* spreadsheet-${name}.properties
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

	

