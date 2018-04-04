package cz.geek.wandera.shell.rest;

import static java.util.Arrays.asList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.exception.JsonQueryException;

@Service
public class RestProcessor {

	private static final List<MediaType> JSON_TYPE = asList(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));

	private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	public RestResponse processResponse(ResponseEntity<byte[]> response, String jq, File target, boolean raw,
			boolean headers) throws IOException {
		RestResponse restResponse = new RestResponse(response.getStatusCodeValue());
		URI location = response.getHeaders().getLocation();

		if (headers) {
			restResponse
					.showStatus()
					.headers(response.getHeaders());
		} else if (location != null) {
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setLocation(location);
			restResponse.headers(httpHeaders);
		}

		if (response.getBody() == null) {
			return restResponse.showStatus();
		}
		if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
				|| response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
			restResponse.showStatus();
		}

		if (target != null) {
			try (OutputStream output = Files.newOutputStream(target.toPath())) {
				final int length = IOUtils.copy(new ByteArrayInputStream(response.getBody()), output);
				return restResponse
						.showStatus()
						.body(target.getAbsolutePath() + ": " + length + " bytes");
			}
		}

		boolean jsonCompatible = isCompatible(response.getHeaders());
		if ((jq != null || !raw) && jsonCompatible) {
			final JsonNode tree = mapper.readTree(response.getBody());
			if (jq != null) {
				try {
					JsonQuery q = JsonQuery.compile(jq);
					List<JsonNode> result = q.apply(tree);
					return restResponse.body((result.size() == 1 ? result.get(0) : result).toString());
				} catch (JsonQueryException e) {
					throw new IllegalArgumentException(e.getMessage(), e);
				}
			}
			return restResponse.body(mapper.writeValueAsString(tree));
		}

		return restResponse.body(new String(response.getBody()));
	}

	private static boolean isCompatible(final HttpHeaders headers) {
		MediaType contentType = headers.getContentType();
		return JSON_TYPE.stream()
				.anyMatch(mediaType -> mediaType.isCompatibleWith(contentType));
	}

}
