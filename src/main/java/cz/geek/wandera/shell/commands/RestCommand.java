package cz.geek.wandera.shell.commands;

import static java.util.Objects.requireNonNull;
import static org.springframework.shell.standard.ShellOption.NULL;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import cz.geek.wandera.shell.rest.RestProcessor;
import cz.geek.wandera.shell.rest.RestRequest;
import cz.geek.wandera.shell.rest.RestService;

@ShellComponent
public class RestCommand {

	private final RestService service;
	private final RestProcessor processor;

	public RestCommand(RestService service, RestProcessor processor) {
		this.service = requireNonNull(service);
		this.processor = requireNonNull(processor);
	}

	@ShellMethod("Issue GET request")
	public String get(String uri, @ShellOption(defaultValue = NULL) String jq,
			@ShellOption(defaultValue = NULL) File target,
			@ShellOption(defaultValue = "false") boolean raw,
			@ShellOption(defaultValue = "false") boolean headers
			) throws IOException {
		ResponseEntity<byte[]> response = service.get(uri, byte[].class);
		return processor.processResponse(response, jq, target, raw, headers).toString();
	}

	@ShellMethod("Issue POST request")
	public String post(String uri, @ShellOption(defaultValue = NULL) String jq,
			@ShellOption(defaultValue = NULL) String data,
			@ShellOption(defaultValue = NULL) File source,
			@ShellOption(defaultValue = NULL) File target,
			@ShellOption(defaultValue = "false") boolean raw,
			@ShellOption(defaultValue = "false") boolean headers
			) throws IOException {
		RestRequest request = createRestRequest(data, source);
		ResponseEntity<byte[]> response = service.post(uri, request, byte[].class);
		return processor.processResponse(response, jq, target, raw, headers).toString();
	}

	private static RestRequest createRestRequest(String data, File source) {
		if (data != null) {
			return new RestRequest(data);
		} else if (source != null) {
			try {
				String content = FileUtils.readFileToString(source, StandardCharsets.UTF_8);
				return new RestRequest(content);
			} catch (IOException e) {
				throw new IllegalArgumentException("Unable to read file: " + source.getAbsolutePath(), e);
			}
		} else {
			throw new IllegalArgumentException("One of data or source has to be specified");
		}
	}

}
