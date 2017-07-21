package google;

import com.google.api.services.sheets.v4.Sheets;

public class SpreadSheetProperties {

	private Sheets service;
	private String spreadsheetId;

	private int test_case_spreadsheetGid;
	private String test_case_range;
	private String test_case_sheetname;

	private int test_result_spreadsheetGid;
	private int test_result_range_index;
	private String test_result_range;
	private String test_result_sheetname;

	private int varible_spreadsheetGid;
	private String varible_range;
	private String varible_sheetname;

	private boolean cleanContent;
	private String cleanRange;

	public Sheets getService() {
		return service;
	}

	public void setService(Sheets service) {
		this.service = service;
	}

	public String getSpreadsheetId() {
		return spreadsheetId;
	}

	public void setSpreadsheetId(String spreadsheetId) {
		this.spreadsheetId = spreadsheetId;
	}

	public int getTest_case_spreadsheetGid() {
		return test_case_spreadsheetGid;
	}

	public void setTest_case_spreadsheetGid(int test_case_spreadsheetGid) {
		this.test_case_spreadsheetGid = test_case_spreadsheetGid;
	}

	public String getTest_case_range() {
		return test_case_range;
	}

	public void setTest_case_range(String test_case_range) {
		this.test_case_range = test_case_range;
	}

	public int getTest_result_range_index() {
		return test_result_range_index;
	}

	public void setTest_result_range_index(int test_result_range_index) {
		this.test_result_range_index = test_result_range_index;
	}

	public String getTest_case_sheetname() {
		return test_case_sheetname;
	}

	public void setTest_case_sheetname(String test_case_sheetname) {
		this.test_case_sheetname = test_case_sheetname;
	}

	public int getTest_result_spreadsheetGid() {
		return test_result_spreadsheetGid;
	}

	public void setTest_result_spreadsheetGid(int test_result_spreadsheetGid) {
		this.test_result_spreadsheetGid = test_result_spreadsheetGid;
	}

	public String getTest_result_range() {
		return test_result_range;
	}

	public void setTest_result_range(String test_result_range) {
		this.test_result_range = test_result_range;
	}

	public String getTest_result_sheetname() {
		return test_result_sheetname;
	}

	public void setTest_result_sheetname(String test_result_sheetname) {
		this.test_result_sheetname = test_result_sheetname;
	}

	public int getVarible_spreadsheetGid() {
		return varible_spreadsheetGid;
	}

	public void setVarible_spreadsheetGid(int varible_spreadsheetGid) {
		this.varible_spreadsheetGid = varible_spreadsheetGid;
	}

	public String getVarible_range() {
		return varible_range;
	}

	public void setVarible_range(String varible_range) {
		this.varible_range = varible_range;
	}

	public String getVarible_sheetname() {
		return varible_sheetname;
	}

	public void setVarible_sheetname(String varible_sheetname) {
		this.varible_sheetname = varible_sheetname;
	}

	public boolean isCleanContent() {
		return cleanContent;
	}

	public void setCleanContent(boolean cleanContent) {
		this.cleanContent = cleanContent;
	}

	public String getCleanRange() {
		return cleanRange;
	}

	public void setCleanRange(String cleanRange) {
		this.cleanRange = cleanRange;
	}
}
