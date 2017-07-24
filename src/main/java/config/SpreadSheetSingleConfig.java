package config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import com.google.api.services.sheets.v4.Sheets;

import google.GoogleAPIService;
import google.SpreadSheetSingleProperties;

@Configuration
public class SpreadSheetSingleConfig {

	@Autowired
	ConfigurableEnvironment env;

	@Bean
	SpreadSheetSingleProperties getSpreadSheetProperties() {
		SpreadSheetSingleProperties singleProperties = new SpreadSheetSingleProperties();

		try {
			GoogleAPIService gas = new GoogleAPIService();

			Sheets service = gas.getSheetsService(env.getProperty("CLIENT_SECRET_PATH"));
			singleProperties.setService(service);

			String spreadsheetId = env.getProperty("SPREADSHEET_ID");
			singleProperties.setSpreadsheetId(spreadsheetId);

			// test result spreadsheet configuration
			int test_result_spreadsheetGid = env.getRequiredProperty("SPREADSHEET_GID", Integer.class);
			singleProperties.setTest_result_spreadsheetGid(test_result_spreadsheetGid);

			String test_result_sheetname = env.getProperty("SHEET_NAME");
			singleProperties.setTest_result_sheetname(test_result_sheetname);

			String test_result_range = env.getProperty("VALUE_RANGE");
			String test_result_range_str = test_result_sheetname + "!" + test_result_range;
			singleProperties.setTest_result_range(test_result_range_str);

			int test_result_range_index = 1;
			Pattern pattern = Pattern.compile("([0-9]+):");
			Matcher matcher = pattern.matcher(test_result_range);
			if (matcher.find()) {
				test_result_range_index = Integer.parseInt(matcher.group(1)) - 1;
			}
			singleProperties.setTest_result_range_index(test_result_range_index);

			// test clean spreadsheet configuration
			boolean cleanContent = env.getProperty("CLEAN_CONTENT", Boolean.class);
			singleProperties.setCleanContent(cleanContent);

			String cleanRange = env.getProperty("SHEET_NAME") + "!" + env.getProperty("CLEAN_RANGE");
			singleProperties.setCleanRange(cleanRange);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return singleProperties;
	}
}
