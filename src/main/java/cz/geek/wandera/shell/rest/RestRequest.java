package cz.geek.wandera.shell.rest;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;

public class RestRequest {

	private final MediaType contentType;
	private final Object body;

	private RestRequest(MediaType contentType, Object body) {
		this.contentType = requireNonNull(contentType, "contentType");
		this.body = body;
	}

	private RestRequest(String data) {
		this(MediaType.APPLICATION_JSON, data);
	}

	public Object getBody() {
		return body;
	}

	public MediaType getContentType() {
		return contentType;
	}

	public static Builder create() {
		return new Builder();
	}

	public static class Builder {

		private String data;

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
			return new RestRequest(data);
		}

	}

}
