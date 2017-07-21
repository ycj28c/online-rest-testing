package config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import com.google.api.services.sheets.v4.Sheets;

import google.GoogleAPIService;
import google.SpreadSheetProperties;

@Configuration
public class SpreadSheetConfig {

	@Autowired
	ConfigurableEnvironment env;

	@Bean
	SpreadSheetProperties getSpreadSheetProperties() {
		SpreadSheetProperties bulkProperties = new SpreadSheetProperties();

		try {
			GoogleAPIService gas = new GoogleAPIService();

			Sheets service = gas.getSheetsService(env.getProperty("CLIENT_SECRET_PATH"));
			bulkProperties.setService(service);

			String spreadsheetId = env.getProperty("SPREADSHEET_ID");
			bulkProperties.setSpreadsheetId(spreadsheetId);

			// test cases spreadsheet configuration
			int test_case_spreadsheetGid = env.getRequiredProperty("TEST_CASE_SPREADSHEET_GID", Integer.class);
			bulkProperties.setTest_case_spreadsheetGid(test_case_spreadsheetGid);

			String test_case_sheetname = env.getProperty("TEST_CASE_SHEET_NAME");
			bulkProperties.setTest_case_sheetname(test_case_sheetname);

			String test_case_range = test_case_sheetname + "!" + env.getProperty("TEST_CASE_VALUE_RANGE");
			bulkProperties.setTest_case_range(test_case_range);

			// test result spreadsheet configuration
			int test_result_spreadsheetGid = env.getRequiredProperty("TEST_RESULT_SPREADSHEET_GID", Integer.class);
			bulkProperties.setTest_result_spreadsheetGid(test_result_spreadsheetGid);

			String test_result_sheetname = env.getProperty("TEST_RESULT_SHEET_NAME");
			bulkProperties.setTest_result_sheetname(test_result_sheetname);

			String test_result_range = env.getProperty("TEST_RESULT_VALUE_RANGE");
			String test_result_range_str = test_result_sheetname + "!" + test_result_range;
			bulkProperties.setTest_result_range(test_result_range_str);

			int test_result_range_index = 1;
			Pattern pattern = Pattern.compile("([0-9]+):");
			Matcher matcher = pattern.matcher(test_result_range);
			if (matcher.find()) {
				test_result_range_index = Integer.parseInt(matcher.group(1)) - 1;
			}
			bulkProperties.setTest_result_range_index(test_result_range_index);

			// test variable spreadsheet configuration
			int varible_spreadsheetGid = env.getRequiredProperty("VARIABLE_SPREADSHEET_GID", Integer.class);
			bulkProperties.setVarible_spreadsheetGid(varible_spreadsheetGid);

			String varible_sheetname = env.getProperty("VARIABLE_SHEET_NAME");
			bulkProperties.setVarible_sheetname(varible_sheetname);

			String varible_range = varible_sheetname + "!" + env.getProperty("VARIABLE_VALUE_RANGE");
			bulkProperties.setVarible_range(varible_range);

			boolean cleanContent = env.getProperty("CLEAN_CONTENT", Boolean.class);
			bulkProperties.setCleanContent(cleanContent);

			String cleanRange = env.getProperty("TEST_RESULT_SHEET_NAME") + "!" + env.getProperty("CLEAN_RANGE");
			bulkProperties.setCleanRange(cleanRange);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return bulkProperties;
	}
}
