package cz.geek.wandera.shell.commands;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.shell.standard.ShellOption.NULL;

import java.io.File;
import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import cz.geek.wandera.shell.keys.KeysHolder;
import cz.geek.wandera.shell.rest.HttpHeader;
import cz.geek.wandera.shell.rest.RestProcessor;
import cz.geek.wandera.shell.rest.RestRequest;
import cz.geek.wandera.shell.rest.RestService;

@ShellComponent
public class RestCommand {

	private static final String URI = "Absolute URI of request or relative URI resolved according to previous request";
	private static final String HEADER = "request header in form 'name=value'";
	private static final String JQ = "jq expression to be applied JSON response";
	private static final String TARGET = "save response to file";
	private static final String RAW = "show raw response (don't format JSON)";
	private static final String HEADERS = "show response headers";
	private static final String DATA = "data to be send with request";
	private static final String SOURCE = "load data to be send with request from file";

	private final RestService service;
	private final RestProcessor processor;
	private final KeysHolder keysHolder;

	public RestCommand(RestService service, RestProcessor processor, KeysHolder keysHolder) {
		this.service = requireNonNull(service, "service");
		this.processor = requireNonNull(processor, "processor");
		this.keysHolder = requireNonNull(keysHolder, "keysHolder");
	}

	@ShellMethodAvailability
	public Availability availability() {
		return keysHolder.hasKeys()
				? Availability.available()
				: Availability.unavailable("you have to use 'key set' or 'key save' command first");
	}

	@ShellMethod("Issue GET request")
	public String get(
			@ShellOption(help = URI) String uri,
			@ShellOption(defaultValue = NULL, help = JQ) String jq,
			@ShellOption(defaultValue = NULL, help = HEADER) HttpHeader header,
			@ShellOption(defaultValue = NULL, help = TARGET) File target,
			@ShellOption(defaultValue = "false", help = RAW) boolean raw,
			@ShellOption(defaultValue = "false", help = HEADERS) boolean headers
			) throws IOException {
		RestRequest request = RestRequest.create(GET, uri).headers(header).build();
		ResponseEntity<byte[]> response = service.exchange(request);
		return processor.processResponse(response, jq, target, raw, headers).toString();
	}

	@ShellMethod("Issue POST request")
	public String post(
			@ShellOption(help = URI) String uri,
			@ShellOption(defaultValue = NULL, help = JQ) String jq,
			@ShellOption(defaultValue = NULL, help = HEADER) HttpHeader header,
			@ShellOption(defaultValue = NULL, help = DATA) String data,
			@ShellOption(defaultValue = NULL, help = SOURCE) File source,
			@ShellOption(defaultValue = NULL, help = TARGET) File target,
			@ShellOption(defaultValue = "false", help = RAW) boolean raw,
			@ShellOption(defaultValue = "false", help = HEADERS) boolean headers
			) throws IOException {
		RestRequest request = RestRequest.create(POST, uri).headers(header).data(data).data(source).build();
		ResponseEntity<byte[]> response = service.exchange(request);
		return processor.processResponse(response, jq, target, raw, headers).toString();
	}

	@ShellMethod("Issue PUT request")
	public String put(
			@ShellOption(help = URI) String uri,
			@ShellOption(defaultValue = NULL, help = JQ) String jq,
			@ShellOption(defaultValue = NULL, help = HEADER) HttpHeader header,
			@ShellOption(defaultValue = NULL, help = DATA) String data,
			@ShellOption(defaultValue = NULL, help = SOURCE) File source,
			@ShellOption(defaultValue = NULL, help = TARGET) File target,
			@ShellOption(defaultValue = "false", help = RAW) boolean raw,
			@ShellOption(defaultValue = "false", help = HEADERS) boolean headers
			) throws IOException {
		RestRequest request = RestRequest.create(PUT, uri).headers(header).data(data).data(source).build();
		ResponseEntity<byte[]> response = service.exchange(request);
		return processor.processResponse(response, jq, target, raw, headers).toString();
	}

	@ShellMethod("Issue DELETE request")
	public String delete(
			@ShellOption(help = URI) String uri,
			@ShellOption(defaultValue = NULL, help = HEADER) HttpHeader header,
			@ShellOption(defaultValue = "false", help = RAW) boolean raw,
			@ShellOption(defaultValue = "false", help = HEADERS) boolean headers
			) throws IOException {
		RestRequest request = RestRequest.create(DELETE, uri).headers(header).build();
		ResponseEntity<byte[]> response = service.exchange(request);
		return processor.processResponse(response, null, null, raw, headers).toString();
	}

}
