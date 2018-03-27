package cz.geek.wandera.shell.commands;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.springframework.shell.standard.ShellOption.NULL;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import cz.geek.wandera.shell.rest.RestService;
import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.exception.JsonQueryException;

@ShellComponent
public class RestCommand {

	private static final List<MediaType> JSON_TYPE = asList(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));

	private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	private final RestService service;

	public RestCommand(RestService service) {
		this.service = requireNonNull(service);
	}

	@ShellMethod("Issue GET request")
	public String get(String uri, @ShellOption(defaultValue = NULL) String jq,
			@ShellOption(defaultValue = NULL) File target,
			@ShellOption(defaultValue = "false") boolean raw,
			@ShellOption(defaultValue = "false") boolean headers
			) throws IOException {
		ResponseEntity<byte[]> response = service.get(uri, byte[].class);
		return processResponse(response, jq, target, raw, headers).toString();
	}

	private RestResponse processResponse(ResponseEntity<byte[]> response, String jq, File target, boolean raw,
			boolean headers) throws IOException {
		RestResponse restResponse = new RestResponse(response.getStatusCodeValue());

		if (headers) {
			restResponse
					.showStatus()
					.headers(response.getHeaders());
		}

		if (response.getBody() == null) {
			return restResponse.showStatus();
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

		if (target != null) {
			try (OutputStream output = Files.newOutputStream(target.toPath())) {
				final int length = IOUtils.copy(new ByteArrayInputStream(response.getBody()), output);
				return restResponse
						.showStatus()
						.body(target.getAbsolutePath() + ": " + length + " bytes");
			}
		}

		return restResponse.body(new String(response.getBody()));
	}

	private static boolean isCompatible(final HttpHeaders headers) {
		MediaType contentType = headers.getContentType();
		return JSON_TYPE.stream()
				.anyMatch(mediaType -> mediaType.isCompatibleWith(contentType));
	}

}
