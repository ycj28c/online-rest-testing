package google;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.ITestResult;

import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridCoordinate;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;

import util.DataDriverModel;
import util.TimeUtil;

public class SpreadSheetOperater {

	/**
	 * insert the data into google spreadsheet cell format
	 * @param celldatalist
	 * @param requests
	 * @param value
	 * @param spreadSheetGid
	 * @param rowIndex
	 * @param colIndex
	 */
	private static void insertCellData(List<CellData> celldatalist, List<Request> requests, String value,
			int spreadSheetGid, int rowIndex, int colIndex) {
		celldatalist.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(value)));
		requests.add(new Request().setUpdateCells(new UpdateCellsRequest()
				.setStart(
						new GridCoordinate().setSheetId(spreadSheetGid).setRowIndex(rowIndex).setColumnIndex(colIndex))
				.setRows(Arrays.asList(new RowData().setValues(celldatalist)))
				.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));
	}
	
	private static String getTestStatus(ITestResult result) {
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
		return testStatus;
	}
	
	public static void OutputSingleTestResult(ITestResult result, SpreadSheetSingleProperties spreadSheetConn) {
		String errorLog = null;
		String start_time = TimeUtil.millisecondsToTime(result.getStartMillis());
		String end_time = TimeUtil.millisecondsToTime(result.getEndMillis());
		try{
			errorLog = result.getThrowable().getMessage();
		}catch(Exception e){}
		
		String testStatus = getTestStatus(result);
	    
		/* http://stackoverflow.com/questions/38486286/write-data-into-google-spreadsheet-w-java 
		 * http://stackoverflow.com/questions/39629260/insert-row-without-overwritting-data
		 * */
		List<Request> requests = new ArrayList<Request>();
		List<CellData> values_Test_Result = new ArrayList<CellData>();
		List<CellData> values_Test_Log = new ArrayList<CellData>();
		List<CellData> start_Time_Col = new ArrayList<CellData>();
		List<CellData> end_Time_Col = new ArrayList<CellData>();
		
		int curIndex = spreadSheetConn.getTest_result_range_index();
		int test_result_spreadsheetGid = spreadSheetConn.getTest_result_spreadsheetGid();
		int colIndex = 8;

		insertCellData(values_Test_Result, requests, testStatus, test_result_spreadsheetGid, curIndex, colIndex++);
		insertCellData(values_Test_Log, requests, errorLog, test_result_spreadsheetGid, curIndex, colIndex++);
		insertCellData(start_Time_Col, requests, start_time, test_result_spreadsheetGid, curIndex, colIndex++);
		insertCellData(end_Time_Col, requests, end_time, test_result_spreadsheetGid, curIndex, colIndex++);

		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
		try {
			spreadSheetConn.getService().spreadsheets()
					.batchUpdate(spreadSheetConn.getSpreadsheetId(), batchUpdateRequest).execute();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		spreadSheetConn.setTest_result_range_index(curIndex+1);
	}

	public static void OutputBulkTestResult(DataDriverModel ddm, ITestResult result,
			SpreadSheetBulkProperties spreadSheetConn) {
		String name_copy = ddm.getName();
		String description_copy = ddm.getDescription();
		String requestUrl_copy = ddm.getRequestUrl();
		String requestMethod_copy = ddm.getRequestMethod();
		String payload_copy = ddm.getPayload();
		String action_copy = ddm.getAction();
		String validation_copy = (String) ddm.getValidation();

		String errorLog = null;
		String start_time = TimeUtil.millisecondsToTime(result.getStartMillis());
		String end_time = TimeUtil.millisecondsToTime(result.getEndMillis());
		try {
			errorLog = result.getThrowable().getMessage();
		} catch (Exception e) {
		}

		String testStatus = getTestStatus(result);

		/*
		 * http://stackoverflow.com/questions/38486286/write-data-into-google-spreadsheet-w-java
		 * http://stackoverflow.com/questions/39629260/insert-row-without-overwritting-data
		 */
		List<Request> requests = new ArrayList<Request>();
		List<CellData> index_cp = new ArrayList<CellData>();
		List<CellData> name_cp = new ArrayList<CellData>();
		List<CellData> description_cp = new ArrayList<CellData>();
		List<CellData> requestUrl_cp = new ArrayList<CellData>();
		List<CellData> requestMethod_cp = new ArrayList<CellData>();
		List<CellData> payload_cp = new ArrayList<CellData>();
		List<CellData> action_cp = new ArrayList<CellData>();
		List<CellData> validation_cp = new ArrayList<CellData>();

		List<CellData> values_Test_Result = new ArrayList<CellData>();
		List<CellData> values_Test_Log = new ArrayList<CellData>();
		List<CellData> start_Time_Col = new ArrayList<CellData>();
		List<CellData> end_Time_Col = new ArrayList<CellData>();

		int curIndex = spreadSheetConn.getTest_result_range_index();
		int test_result_spreadsheetGid = spreadSheetConn.getTest_result_spreadsheetGid();
		int colIndex = 0;
		int cursor = spreadSheetConn.getCursor();

		// insert the cell data
		insertCellData(index_cp, requests, String.valueOf(cursor), test_result_spreadsheetGid, curIndex, colIndex++);
		insertCellData(name_cp, requests, name_copy, test_result_spreadsheetGid, curIndex, colIndex++);
		insertCellData(description_cp, requests, description_copy, test_result_spreadsheetGid, curIndex, colIndex++);
		insertCellData(requestUrl_cp, requests, requestUrl_copy, test_result_spreadsheetGid, curIndex, colIndex++);
		insertCellData(requestMethod_cp, requests, requestMethod_copy, test_result_spreadsheetGid, curIndex, colIndex++);
		insertCellData(payload_cp, requests, payload_copy, test_result_spreadsheetGid, curIndex, colIndex++);
		insertCellData(action_cp, requests, action_copy, test_result_spreadsheetGid, curIndex, colIndex++);
		insertCellData(validation_cp, requests, validation_copy, test_result_spreadsheetGid, curIndex, colIndex++);
		insertCellData(values_Test_Result, requests, testStatus, test_result_spreadsheetGid, curIndex, colIndex++);
		insertCellData(values_Test_Log, requests, errorLog, test_result_spreadsheetGid, curIndex, colIndex++);
		insertCellData(start_Time_Col, requests, start_time, test_result_spreadsheetGid, curIndex, colIndex++);
		insertCellData(end_Time_Col, requests, end_time, test_result_spreadsheetGid, curIndex, colIndex++);

		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
		try {
			spreadSheetConn.getService().spreadsheets()
					.batchUpdate(spreadSheetConn.getSpreadsheetId(), batchUpdateRequest).execute();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		spreadSheetConn.setTest_result_range_index(++curIndex);
		spreadSheetConn.setCursor(++cursor);
	}
	
}
