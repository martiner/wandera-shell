package cz.geek.wandera.shell.rest;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public class RestRequest {

	private static final HttpHeader[] EMPTY = {};

	private final HttpMethod method;
	private final String uri;
	private final MediaType contentType;
	private Object body;
	private HttpHeader[] headers = EMPTY;

	private RestRequest(HttpMethod method, String uri, MediaType contentType) {
		this.method = requireNonNull(method, "method");
		this.uri = requireNonNull(uri, "uri");
		this.contentType = requireNonNull(contentType, "contentType");
	}

	private void setHeaders(HttpHeader... headers) {
		this.headers = headers;
	}

	private void setBody(Object body) {
		this.body = body;
	}

	public Object getBody() {
		return body;
	}

	public HttpHeader[] getHeaders() {
		return headers;
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
		private HttpHeader[] headers = EMPTY;

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
			RestRequest request = new RestRequest(method, uri, MediaType.APPLICATION_JSON);
			request.setBody(data);
			request.setHeaders(headers);
			return request;
		}

		public Builder headers(HttpHeader... headers) {
			if (headers != null) {
				this.headers = Stream.of(headers).filter(Objects::nonNull).toArray(HttpHeader[]::new);
			}
			return this;
		}

	}

}
