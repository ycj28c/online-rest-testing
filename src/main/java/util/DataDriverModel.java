package util;

public class DataDriverModel {

	@Override
	public String toString() {
		return "DataDriverModel [id=" + id + ", name=" + name + ", description=" + description + ", requestUrl=" + requestUrl
				+ ", requestMethod=" + requestMethod + ", payload=" + payload + ", action=" + action + ", validation="
				+ validation + "]";
	}
	
	public DataDriverModel(){}

	public DataDriverModel(Integer id, String name, String description, String requestUrl, String requestMethod,
			String payload, String action, Object validation) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.requestUrl = requestUrl;
		this.requestMethod = requestMethod;
		this.payload = payload;
		this.action = action;
		this.validation = validation;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Object getValidation() {
		return validation;
	}

	public void setValidation(Object validation) {
		this.validation = validation;
	}

	Integer id;
	String name;
	String description;
	String requestUrl;
	String requestMethod;
	String payload;
	String action;
	Object validation;
}
