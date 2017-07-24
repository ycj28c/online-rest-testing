package google;

import com.google.api.services.sheets.v4.Sheets;

public class SpreadSheetSingleProperties extends AbstractSpreadSheetProperties{
	
	private Sheets service;
	private int test_result_range_index;
	
	public Sheets getService() {
		return service;
	}

	public void setService(Sheets service) {
		this.service = service;
	}
	
	public int getTest_result_range_index() {
		return test_result_range_index;
	}

	public void setTest_result_range_index(int test_result_range_index) {
		this.test_result_range_index = test_result_range_index;
	}
}
