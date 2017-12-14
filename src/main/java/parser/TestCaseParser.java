package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.DataDriverModel;
import util.JSONUtils;
import util.RequestMethodUtil;

public class TestCaseParser {
	
	/**
	 * translate the spreadsheet row value into data driver model list according to the inject variables
	 * @param spreadsheetRowValues
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	public static List<DataDriverModel> translateDataProviderParameter(List<Object> spreadsheetRowValues, HashMap<String, List<?>> variableMap) {
		List<DataDriverModel> ddmList = new ArrayList<DataDriverModel>();
		//only check the request_url, request_method, payload, action and validation column
	
		String requestUrl = (String)spreadsheetRowValues.get(3);
		String requestMethod = (String)spreadsheetRowValues.get(4);
		String payload = (String)spreadsheetRowValues.get(5);
		String action = (String)spreadsheetRowValues.get(6);
		String validation = (String)spreadsheetRowValues.get(7);
		
		/* 
		 * load all the variable keys, format usage example: string:{yy}, json:{{xx.a}} or {{xx.b}}
		 */
		HashSet<String> variableKeys = new HashSet<String>();
		List<String> requestUrlKeys = findVaribleKeysFromString(requestUrl);
		List<String> requestMethodKeys = findVaribleKeysFromString(requestMethod);
		List<String> payloadKeys = findVaribleKeysFromString(payload);
		List<String> actionKeys = findVaribleKeysFromString(action);
		List<String> validationKeys = findVaribleKeysFromString(validation);
		if(requestUrlKeys!=null) variableKeys.addAll(requestUrlKeys);
		if(requestMethodKeys!=null) variableKeys.addAll(requestMethodKeys);
		if(payloadKeys!=null) variableKeys.addAll(payloadKeys);
		if(actionKeys!=null) variableKeys.addAll(actionKeys);
		if(validationKeys!=null) variableKeys.addAll(validationKeys);
		
		List<String> allUniqueVariableKeys = validVariableKeys(variableMap, requestUrlKeys, requestMethodKeys, payloadKeys, actionKeys, validationKeys);
		/* get the minimum keys size from key hash, generate list of DataDriverModel objects */
		
		// add the original ddm
		DataDriverModel initialDDM = new DataDriverModel();
		initialDDM.setId((String)spreadsheetRowValues.get(0));
		initialDDM.setName((String)spreadsheetRowValues.get(1));
		initialDDM.setDescription((String)spreadsheetRowValues.get(2));
		initialDDM.setRequestUrl(requestUrl);
		initialDDM.setRequestMethod(RequestMethodUtil.convertRequetMethod(requestMethod));
		initialDDM.setPayload(payload);
		initialDDM.setAction(action);
		initialDDM.setValidation(validation);
//		System.out.println("initialDDM data:" + initialDDM.toString());
		ddmList.add(initialDDM);
		
		HashSet<String> allUniqueParentKeys = new HashSet<String>();
		HashMap<String, HashSet<String>> validKeyMapping = new HashMap<String, HashSet<String>>();
		
		/* according to the unique variable, find out the unique parent key */
		for(int i=0;i<allUniqueVariableKeys.size();i++){
			String currentKey = allUniqueVariableKeys.get(i);
			
			/* json format variable only use the root name as key */
			if(currentKey.contains(".")){
				String rootVariableName = currentKey.substring(0, currentKey.indexOf("."));
				if(variableMap.containsKey(rootVariableName)){
					allUniqueParentKeys.add(rootVariableName);
					if(validKeyMapping.containsKey(rootVariableName)){
						validKeyMapping.get(rootVariableName).add(currentKey);
					} else {
						HashSet<String> subkeys = new HashSet<String>();
						subkeys.add(currentKey);
						validKeyMapping.put(rootVariableName, subkeys);
					}
				}
			} else {
				if(variableMap.containsKey(currentKey)){
					allUniqueParentKeys.add(currentKey);
					if(validKeyMapping.containsKey(currentKey)){
						validKeyMapping.get(currentKey).add(currentKey);
					} else {
						HashSet<String> subkeys = new HashSet<String>();
						subkeys.add(currentKey);
						validKeyMapping.put(currentKey, subkeys);
					}
				}
			}
		}
		
		/* loop for each parent key, each variable in parent key */
		for(Iterator<String> iter = allUniqueParentKeys.iterator();iter.hasNext();){
			String parentKey = (String) iter.next();
			List<?> childKeys = variableMap.get(parentKey);
			
			HashSet<DataDriverModel> ddmTemp = new HashSet<DataDriverModel>();
			for(int i =0;i<childKeys.size();i++){
				for(int k =0;k<ddmList.size();k++){
					DataDriverModel cloner = (DataDriverModel)ddmList.get(k).clone();
					for (String validKey: validKeyMapping.get(parentKey)) {
						if(validKey.contains(".")){ //if is json or query variable, mapping looks like {a:1111,b:2222}
//							System.out.println("validKey:"+validKey+", (String)childKeys.get(i):"+(String)childKeys.get(i)+", childKeys:");
							System.out.println("cloner.getRequestUrl():"+ cloner.getRequestUrl());
							//request URL
							//TODO currently force string
							cloner.setRequestUrl(addressJSONVarible(cloner.getRequestUrl(), validKey, (String)childKeys.get(i))); 
				    		//request method
							cloner.setRequestMethod(RequestMethodUtil.convertRequetMethod(addressJSONVarible(
									cloner.getRequestMethod().getRequestMethod(), validKey, (String)childKeys.get(i))));
				    		//payLoad
							cloner.setPayload(addressJSONVarible(cloner.getPayload(), validKey, (String)childKeys.get(i)));
				    		//action
							cloner.setAction(addressJSONVarible(cloner.getAction(), validKey, (String)childKeys.get(i)));
				    		//validation
							cloner.setValidation(addressJSONVarible((String)cloner.getValidation(), validKey, (String)childKeys.get(i)));
						} else {
//							System.out.println("111validKey:"+validKey+", (String)childKeys.get(i):"+(String)childKeys.get(i));
							System.out.println("cloner.getRequestUrl():"+ cloner.getRequestUrl());
							//request URL
							cloner.setRequestUrl(addressVarible(cloner.getRequestUrl(), validKey, (String)childKeys.get(i)));
				    		//request method
							cloner.setRequestMethod(RequestMethodUtil.convertRequetMethod(addressVarible(
									cloner.getRequestMethod().getRequestMethod(), validKey, (String)childKeys.get(i))));
				    		//payLoad
							cloner.setPayload(addressVarible(cloner.getPayload(), validKey, (String)childKeys.get(i)));
				    		//action
							cloner.setAction(addressVarible(cloner.getAction(), validKey, (String)childKeys.get(i)));
				    		//validation
							cloner.setValidation(addressVarible((String)cloner.getValidation(), validKey, (String)childKeys.get(i)));
						}
						ddmTemp.add(cloner);
					}
				}
			}
			/* remove the duplicate test */
			ddmList.clear();
			ddmList.addAll(ddmTemp);
		}
		
		// Iteration ddm for all variables 
		//TODO limit maximum loop
//		for(int i=0;i<allUniqueVariableKeys.size();i++){
//			String currentKey = allUniqueVariableKeys.get(i);
//			
//			/* json format variable only use the root name as key */
//			List<String> parameters = null;
//			if(currentKey.contains(".")){
//				String rootVariableName = currentKey.substring(0, currentKey.indexOf("."));
//				parameters = (List<String>)variableMap.get(rootVariableName);
//			} else {
//				parameters = (List<String>)variableMap.get(currentKey);
//			}
//			
//			List<DataDriverModel> ddmTemp = new ArrayList<DataDriverModel>();
//			
//			for(int k=0;k<ddmList.size();k++){
//				for(int j=0;j<parameters.size();j++){
//					if(currentKey.contains(".")){ //if is json or query variable
//						System.out.println("currentKey:"+currentKey+", parameters.get(j):"+parameters.get(j));
//						DataDriverModel cloner = (DataDriverModel)ddmList.get(k).clone();
//						System.out.println("cloner.getRequestUrl():"+ cloner.getRequestUrl());
//						//request URL
//						cloner.setRequestUrl(addressJSONVarible(cloner.getRequestUrl(), currentKey, parameters.get(j)));
//			    		//request method
//						cloner.setRequestMethod(RequestMethodUtil.convertRequetMethod(addressJSONVarible(
//								cloner.getRequestMethod().getRequestMethod(), currentKey, parameters.get(j))));
//			    		//payLoad
//						cloner.setPayload(addressJSONVarible(cloner.getPayload(), currentKey, parameters.get(j)));
//			    		//action
//						cloner.setAction(addressJSONVarible(cloner.getAction(), currentKey, parameters.get(j)));
//			    		//validation
//			    		//TODO currently force string
//						cloner.setValidation(addressJSONVarible((String)cloner.getValidation(), currentKey, parameters.get(j)));
//						ddmTemp.add(cloner);
//					} else {
////						System.out.println("allUniqueVariableKeys.size(): "+allUniqueVariableKeys.size()+", parameters.size():"+ parameters.size() +", ddmTemp.size():"+ddmTemp.size());
//						DataDriverModel cloner = (DataDriverModel)ddmList.get(k).clone();
//						System.out.println("cloner.getRequestUrl():"+ cloner.getRequestUrl());
//						//request URL
//						cloner.setRequestUrl(addressVarible(cloner.getRequestUrl(), currentKey, parameters.get(j)));
//			    		//request method
//						cloner.setRequestMethod(RequestMethodUtil.convertRequetMethod(addressVarible(
//								cloner.getRequestMethod().getRequestMethod(), currentKey, parameters.get(j))));
//			    		//payLoad
//						cloner.setPayload(addressVarible(cloner.getPayload(), currentKey, parameters.get(j)));
//			    		//action
//						cloner.setAction(addressVarible(cloner.getAction(), currentKey, parameters.get(j)));
//			    		//validation
//			    		//TODO currently force string
//						cloner.setValidation(addressVarible((String)cloner.getValidation(), currentKey, parameters.get(j)));
//						ddmTemp.add(cloner);
//					}
//				}
//			}
//			ddmList = ddmTemp;
//		}
		
		return ddmList;
	}
	
	private static String addressVarible(String s, String keys, String mappingValue){
		if (s == null || s.isEmpty() || keys == null || keys.isEmpty() || mappingValue == null
				|| mappingValue.isEmpty()) {
			return s;
		}
//		System.out.println("s: " + s + ", keys: " + keys + ", mappingValue:" + mappingValue);
		String result = s.replaceAll("\\{\\{"+keys+"\\}\\}", mappingValue);
		return result;
	}
	
	private static String addressJSONVarible(String s, String keys, String mappingValue){
		if (s == null || s.isEmpty() || keys == null || keys.isEmpty() || mappingValue == null
				|| mappingValue.isEmpty()) {
			return s;
		}
//		System.out.println("s: " + s + ", keys: " + keys + ", mappingValue:" + mappingValue);
//		 mappingValue = "{\"pgid\":1,\"userid\":2}";
		Map<String, Object> map = JSONUtils.convertJsonToMap(mappingValue);
		
		/* get the replace key mapping */
		//TODO
//		String sourceKey = keys.replaceAll("\\{\\{|\\}\\}", "");
		String sourceKeyHead = keys.substring(0, keys.lastIndexOf("."));
		String sourcekeyNode = keys.substring(keys.lastIndexOf(".") + 1);
		String sourceKey = sourceKeyHead + "." + sourcekeyNode;
		String mappingKey = (String) map.get(sourcekeyNode);
		
		String result = s.replaceAll("\\{\\{"+sourceKey+"\\}\\}", mappingKey);
//		System.out.println("sourcekeyNode: " + sourcekeyNode + ", mappingKey: " + mappingKey + ", result:" + result);
		return result;
	}
	
	private static List<String> findVaribleKeysFromString(String varible){
		if(varible == null || varible.isEmpty()){
			return null;
		}
		List<String> keys = new ArrayList<String>();
		//keys {{}} format
		//TODO json key {{xx.a}}
//		Pattern pattern = Pattern.compile("\\{\\{[a-zA-Z0-9][a-zA-Z0-9 ]+\\}\\}"); //variable must combine with alpha-beta and number
		Pattern pattern = Pattern.compile("\\{\\{[a-zA-Z0-9 _\\.]+\\}\\}"); //fit for json variable {{xx.xx}}, alpha-beat/number/dot/space/underscore
		Matcher matcher = pattern.matcher(varible);
		while (matcher.find()) {
			String group = matcher.group();
			System.out.println(group);
			group = group.replaceAll("\\{\\{|\\}\\}", "");
			keys.add(group);
		}
		return keys;
	}
	
	/**
	 * remove duplicate keys and the keys not in variable map
	 * @return
	 */
	@SafeVarargs
	private static List<String> validVariableKeys(HashMap<String, List<?>> variableMap, List<String>... rawVariables) {
		System.out.println("validVariableKeys: generate valid variables");
		List<String> result = new ArrayList<String>();
		HashSet<String> hashedRawVariable = new HashSet<String>();
		if (variableMap == null || variableMap.isEmpty() || rawVariables.length<1) {
			return result;
		}
		
		/* remove the duplicate keys */
		for(List<String> rawVariable: rawVariables){
			if (rawVariable == null || rawVariable.isEmpty()) {
				continue;
			}
//			System.out.println("rawVariable: "+rawVariable);
			hashedRawVariable.addAll(rawVariable);
		}
		
		/* the variable map should contains the valid key */
		Iterator<String> rawVariableIterator = hashedRawVariable.iterator();
		while(rawVariableIterator.hasNext()){
			String variableKey = rawVariableIterator.next();
			String fixedVariableKey = variableKey;
//			System.out.println("fixedVariableKey: "+fixedVariableKey);
			//if json, check the root variable name
			if(fixedVariableKey.contains(".")){
				fixedVariableKey = fixedVariableKey.substring(0, fixedVariableKey.indexOf("."));
			}
			if(variableMap.containsKey(fixedVariableKey)){
				result.add(variableKey);
			}
		}
//		System.out.println("result.size(): "+result.size());
		return result;
	}

}
