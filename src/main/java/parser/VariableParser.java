package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.api.services.sheets.v4.model.ValueRange;

import google.SpreadSheetProperties;

public class VariableParser {

	/**
	 * parse string variable such as A,B,C
	 * @param string variable
	 * @return list format of string variable
	 */
	private static List<String> parseStringVariable(String varilable){
		if(varilable == null || varilable.isEmpty()){
			return null;
		}
		List<String> list = new ArrayList<String>();
		String[] array = varilable.split(",");
		for(int i=0;i<array.length;i++){
			list.add(array[i].trim()); // URL parameter should not contains blanket
		}
		return list;
	}
	
	/**
	 * parse JSON variable such as [{"a": 111111, "b": 111111},{"a": 222222, "b": 222222},{"a": 333333, "b": 333333}]
	 * @param JSON variable
	 * @return list format of JSON variable
	 */
	private static List<String> parseJsonVariable(String varilable) throws JsonParseException, JsonMappingException, IOException{
		if(varilable == null || varilable.isEmpty()){
			return null;
		}
		varilable = varilable.replaceAll(" ", "");
		varilable = varilable.substring(varilable.indexOf("[{")+2, varilable.lastIndexOf("}]"));
		List<String> list = new ArrayList<String>();
//		/* only accept one layer json */
		String[] array = varilable.split("\\}\\,\\{");
		for(int i=0;i<array.length;i++){
			list.add("{" + array[i].trim() + "}"); // URL parameter should not contains blanket
		}
		return list;
	}
	
	/**
	 * convert the JDBC query result to JSON string format
	 * @param list
	 * @return
	 */
	private static String listmap_to_json_string(List<Map<String, Object>> list){       
	    JSONArray json_arr=new JSONArray();
	    for (Map<String, Object> map : list) {
	        JSONObject json_obj=new JSONObject();
	        for (Map.Entry<String, Object> entry : map.entrySet()) {
	            String key = entry.getKey();
	            Object value = entry.getValue();
	            try {
	                json_obj.put(key,value);
	            } catch (JSONException e) {
	                e.printStackTrace();
	            }                           
	        }
	        json_arr.put(json_obj);
	    }
	    return json_arr.toString();
	}
	
	/**
	 * SQL Injection protection
	 * @param s
	 * @return
	 */
	private static boolean passSqlInjectionCheck(String s){
		String injectionKeywords = "insert|delete|update|drop|execute|create|replace|truncate|alter";
		String[] injectionKeywordsArray = injectionKeywords.split("\\|");
		String formatS = s.trim().toLowerCase();
//		System.out.println("formatS:  "+formatS);
		try {
			for(int i=0;i<injectionKeywordsArray.length;i++){
//				System.out.println("injectionKeywordsArray[i]: "+injectionKeywordsArray[i]);
				if (formatS.contains(injectionKeywordsArray[i])) {
					throw new java.sql.SQLDataException("Error, the sql contain the injection "+injectionKeywordsArray[i]+", stop running");
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * parse query variable such as select a,b from abc
	 * @param query variable
	 * @return list format of query variable
	 */
	private static List<String> parseQueryVariable(String varilable, JdbcTemplate jdbcTemplate){
		if(varilable == null || varilable.isEmpty()){
			return null;
		}
		List<String> list = new ArrayList<String>();
		
		if(passSqlInjectionCheck(varilable)){
			List<Map<String, Object>> results = jdbcTemplate.queryForList(
					varilable,
	    			new Object[] {}
	    	);
			
			String jsonStr = listmap_to_json_string(results);
			
			jsonStr = jsonStr.replaceAll(" ", "");
			jsonStr = jsonStr.substring(jsonStr.indexOf("[{")+2, jsonStr.lastIndexOf("}]"));
			String[] array = jsonStr.split("\\}\\,\\{");
			for(int i=0;i<array.length;i++){
				list.add("{" + array[i].trim() + "}"); // URL parameter should not contains blanket
			}
		}
		return list;
	}
	
	/**
	 * generate the variable map, accept the string/JSON/query format
	 * @param spreadSheetConn
	 * @param variableMap
	 * @param jdbcTemplate
	 */
	public static void generateVaribleMap(SpreadSheetProperties spreadSheetConn, HashMap<String, List<?>> variableMap, JdbcTemplate jdbcTemplate){
		try{
			ValueRange response = spreadSheetConn.getService().spreadsheets().values()
					.get(spreadSheetConn.getSpreadsheetId(), spreadSheetConn.getVarible_range()).execute();
			List<List<Object>> values = response.getValues();
			for(int i=0;i<values.size();i++){
				String variableName = (String)values.get(i).get(1);
				String varibleType = ((String)values.get(i).get(3)).toLowerCase();
				String variableValue = (String)values.get(i).get(4);
						
				List<String> parameters = null;
				switch(varibleType){
					case "string":
						parameters = parseStringVariable(variableValue);
						variableMap.put(variableName, parameters);
						break;
					case "query":
						/* the query will convert to json in db side? such as select to_json(pc) from proxy_company pc */
						parameters = parseQueryVariable(variableValue, jdbcTemplate);
						variableMap.put(variableName, parameters);
//						System.out.println("variableMap.size():"+variableMap.size());
						//TODO add query result limit
						break;
					case "json":
						/* it is like [{a: 1, b :2, c: 3}, {a: 4, b :5, c: 6}] */
						parameters = parseJsonVariable(variableValue);
						/* for json, only keep the root as variable name */
						variableMap.put(variableName, parameters);
						break;
					default:
						throw new java.lang.IllegalArgumentException("Error, Varible type '"+varibleType+"' is not acceptable");
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
}
