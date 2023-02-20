package messages;

import java.util.List;

public class Response {
	private final String apiName;
	private final List<String> responses;

	public Response(String apiName, List<String> responses) {
		this.apiName = apiName;
		this.responses = responses;
	}

	public String getApiName() {
		return apiName;
	}

	public List<String> getResponses() {
		return responses;
	}
}
