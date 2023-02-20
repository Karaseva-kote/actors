package messages;

import static configuration.Config.PORT;

public class ApiRequest {
	private final String apiName;
	private final String request;

	public ApiRequest(String apiName, String request) {
		this.apiName = apiName;
		this.request = request;
	}

	public String getApiRequest() {
		return String.format("http://localhost:%d/%s/com/%s", PORT, apiName, request);
	}

	public String getApiName() {
		return apiName;
	}
}
