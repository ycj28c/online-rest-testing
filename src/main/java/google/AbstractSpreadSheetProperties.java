package google;

public class AbstractSpreadSheetProperties {
	
	private String client_secret_path;
	private String spreadsheetId;
	private int test_result_spreadsheetGid;
	private String test_result_range;
	private String test_result_sheetname;
	private boolean cleanContent;
	private String cleanRange;

	public String getClient_secret_path() {
		return client_secret_path;
	}

	public void setClient_secret_path(String client_secret_path) {
		this.client_secret_path = client_secret_path;
	}
	
	public String getSpreadsheetId() {
		return spreadsheetId;
	}

	public void setSpreadsheetId(String spreadsheetId) {
		this.spreadsheetId = spreadsheetId;
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
