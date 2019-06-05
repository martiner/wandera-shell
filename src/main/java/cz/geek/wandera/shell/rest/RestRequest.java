package cz.geek.wandera.shell.rest;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public class RestRequest {

	private final HttpMethod method;
	private final String uri;
	private final MediaType contentType;
	private final Object body;

	private RestRequest(HttpMethod method, String uri, MediaType contentType, Object body) {
		this.method = requireNonNull(method, "method");
		this.uri = requireNonNull(uri, "uri");
		this.contentType = requireNonNull(contentType, "contentType");
		this.body = body;
	}

	public Object getBody() {
		return body;
	}

	public MediaType getContentType() {
		return contentType;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getUri() {
		return uri;
	}

	public static Builder create(HttpMethod method, String uri) {
		return new Builder(method, uri);
	}

	public static class Builder {

		private final HttpMethod method;
		private final String uri;
		private String data;

		public Builder(HttpMethod method, String uri) {
			this.method = requireNonNull(method, "method");
			this.uri = requireNonNull(uri, "uri");
		}

		public Builder data(String data) {
			this.data = data;
			return this;
		}

		public Builder data(File source) {
			if (source != null) {
				try {
					data = FileUtils.readFileToString(source, StandardCharsets.UTF_8);
				} catch (IOException e) {
					throw new IllegalArgumentException("Unable to read file: " + source.getAbsolutePath(), e);
				}
			}
			return this;
		}

		public RestRequest build() {
			return new RestRequest(method, uri, MediaType.APPLICATION_JSON, data);
		}

	}

}
