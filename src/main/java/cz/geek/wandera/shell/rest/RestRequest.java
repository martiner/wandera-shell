package cz.geek.wandera.shell.rest;

import org.springframework.http.MediaType;

public class RestRequest {

	private final Object body;
	private final MediaType contentType;

	public RestRequest(Object body, MediaType contentType) {
		this.body = body;
		this.contentType = contentType;
	}

	public RestRequest(String data) {
		this(data, MediaType.APPLICATION_JSON);
	}

	public Object getBody() {
		return body;
	}

	public MediaType getContentType() {
		return contentType;
	}
}
