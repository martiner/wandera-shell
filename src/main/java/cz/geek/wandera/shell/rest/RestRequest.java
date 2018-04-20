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

	public RestRequest(MediaType contentType, Object body) {
		this.contentType = requireNonNull(contentType, "contentType");
		this.body = body;
	}

	public RestRequest(String data) {
		this(MediaType.APPLICATION_JSON, data);
	}

	public Object getBody() {
		return body;
	}

	public MediaType getContentType() {
		return contentType;
	}

	public static RestRequest create(String data, File source) {
		if (data != null) {
			return new RestRequest(data);
		}
		if (source != null) {
			try {
				String content = FileUtils.readFileToString(source, StandardCharsets.UTF_8);
				return new RestRequest(content);
			} catch (IOException e) {
				throw new IllegalArgumentException("Unable to read file: " + source.getAbsolutePath(), e);
			}
		}
		return new RestRequest(null);
	}
}
