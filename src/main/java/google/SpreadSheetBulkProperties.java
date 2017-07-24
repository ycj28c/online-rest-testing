package google;

import com.google.api.services.sheets.v4.Sheets;

public class SpreadSheetBulkProperties extends AbstractSpreadSheetProperties{

	private Sheets service;

	private int test_case_spreadsheetGid;
	private String test_case_range;
	private String test_case_sheetname;

	private int test_result_range_index;

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
