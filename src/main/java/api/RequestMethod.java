package api;

public enum RequestMethod {
	GET("GET"), 
	POST("POST"), 
	PUT("PUT"), 
	PATCH("PATCH"), 
	DELETE("DELETE"), 
	COPY("COPY"), 
	HEAD("HEAD"), 
	OPTIONS("OPTIONS"), 
	LINK("LINK"), 
	UNLINK("UNLINK"), 
	PURGE("PURGE"), 
	LOCK("LOCK"), 
	UNLOCK("UNLOCK"), 
	PROPFIND("PROFIND"), 
	VIEW("VIEW");
	
	private RequestMethod(final String requestMethod) {
		this.requestMethod = requestMethod;
	}
	
    private final String requestMethod;
    
    public String getRequestMethod() {
        return this.requestMethod;
    }
    
}
