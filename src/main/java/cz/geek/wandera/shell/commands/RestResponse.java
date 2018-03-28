package cz.geek.wandera.shell.commands;

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

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("");
		if (showStatus) {
			sb.append("Status: ").append(status).append("\n");
		}
		if (headers != null) {
			headers.forEach((headerName, headerValues) ->
					headerValues.forEach(
							headerValue -> sb.append(headerName).append(": ").append(headerValue).append("\n"))
			);
		}
		sb.append(body);
		return sb.toString();
	}
}
