package util;

import api.RequestMethod;

public class RequestMethodUtil {
	
	public static RequestMethod convertRequetMethod(String requestMethodStr){
		if(requestMethodStr.trim().equalsIgnoreCase("get")){
			return RequestMethod.GET;
		} else if(requestMethodStr.trim().equalsIgnoreCase("post")){
			return RequestMethod.POST;
		} else if(requestMethodStr.trim().equalsIgnoreCase("delete")){
			return RequestMethod.DELETE;
		} else if(requestMethodStr.trim().equalsIgnoreCase("copy")){
			return RequestMethod.COPY;
		} else if(requestMethodStr.trim().equalsIgnoreCase("head")){
			return RequestMethod.HEAD;
		} else if(requestMethodStr.trim().equalsIgnoreCase("link")){
			return RequestMethod.LINK;
		} else if(requestMethodStr.trim().equalsIgnoreCase("lock")){
			return RequestMethod.LOCK;
		} else if(requestMethodStr.trim().equalsIgnoreCase("options")){
			return RequestMethod.OPTIONS;
		} else if(requestMethodStr.trim().equalsIgnoreCase("patch")){
			return RequestMethod.PATCH;
		} else if(requestMethodStr.trim().equalsIgnoreCase("propfind")){
			return RequestMethod.PROPFIND;
		} else if(requestMethodStr.trim().equalsIgnoreCase("purge")){
			return RequestMethod.PURGE;
		} else if(requestMethodStr.trim().equalsIgnoreCase("unlink")){
			return RequestMethod.UNLINK;
		} else if(requestMethodStr.trim().equalsIgnoreCase("unlock")){
			return RequestMethod.UNLOCK;
		} else if(requestMethodStr.trim().equalsIgnoreCase("view")){
			return RequestMethod.VIEW;
		} else {
			return null;
		}
	}
}
