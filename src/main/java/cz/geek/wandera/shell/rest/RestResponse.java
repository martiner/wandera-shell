package cz.geek.wandera.shell.rest;

import java.util.StringJoiner;

import org.springframework.http.HttpHeaders;

public class RestResponse {

	private final int status;
	private HttpHeaders headers;
	private String body;
	private boolean showStatus;

	public RestResponse(int statusCodeValue) {
		this.status = statusCodeValue;
	}

	public RestResponse showStatus() {
		this.showStatus = true;
		return this;
	}

	public RestResponse headers(HttpHeaders headers) {
		this.headers = headers;
		return this;
	}

	public RestResponse body(String body) {
		this.body = body;
		return this;
	}

	public int getStatus() {
		return status;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public String getBody() {
		return body;
	}

	public boolean isShowStatus() {
		return showStatus;
	}

	@Override
	public String toString() {
		final StringJoiner result = new StringJoiner("\n");
		if (showStatus) {
			result.add("Status: " + status);
		}
		if (headers != null) {
			headers.forEach((headerName, headerValues) ->
					headerValues.forEach(
							headerValue -> result.add(headerName + ": " + headerValue))
			);
		}
		if (body != null) {
			result.add(body);
		}
		return result.toString();
	}
}
