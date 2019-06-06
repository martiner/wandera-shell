package cz.geek.wandera.shell.rest;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RestProcessorTest {

	protected static final String BODY = "hello";
	private static final byte[] BODY_BYTES = BODY.getBytes();

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private RestProcessor processor;
	private String jsonPretty;
	private String jsonRaw;
	private HttpHeaders jsonHeaders;
	private ResponseEntity<byte[]> jsonResponse;
	private ResponseEntity<byte[]> simpleResponse;

	@Before
	public void setUp() throws Exception {
		processor = new RestProcessor();
		jsonPretty = IOUtils.toString(new ClassPathResource("/pretty.json").getInputStream(), StandardCharsets.UTF_8);
		jsonRaw = IOUtils.toString(new ClassPathResource("/test.json").getInputStream(), StandardCharsets.UTF_8);
		byte[] jsonBytes = jsonRaw.getBytes();
		jsonHeaders = new HttpHeaders();
		jsonHeaders.set("Content-type", "application/json");
		jsonResponse = new ResponseEntity<>(jsonBytes, jsonHeaders, HttpStatus.OK);
		simpleResponse = new ResponseEntity<>(BODY_BYTES, HttpStatus.OK);
	}

	@Test
	public void shouldShowStatusForNullBody() throws Exception {
		RestResponse response = processor.processResponse(new ResponseEntity<>(HttpStatus.OK), null, null, false, false);

		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.isShowStatus()).isTrue();
		assertThat(response.getHeaders()).isNull();
		assertThat(response.getBody()).isNull();
	}

	@Test
	public void shouldShowLocationHeader() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("foo"));
		ResponseEntity<byte[]> entity = new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);

		RestResponse response = processor.processResponse(entity, null, null, false, false);

		assertThat(response.getStatus()).isEqualTo(301);
		assertThat(response.isShowStatus()).isTrue();
		assertThat(response.getHeaders()).containsEntry("Location", singletonList("foo"));
		assertThat(response.getBody()).isNull();
	}

	@Test
	public void shouldShowHeaders() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.set("foo", "bar");
		ResponseEntity<byte[]> entity = new ResponseEntity<>(headers, HttpStatus.OK);

		RestResponse response = processor.processResponse(entity, null, null, false, true);

		assertThat(response.isShowStatus()).isTrue();
		assertThat(response.getHeaders()).containsEntry("foo", singletonList("bar"));
		assertThat(response.getBody()).isNull();
	}

	@Test
	public void shouldShowBodyForRegularResponse() throws Exception {
		RestResponse response = processor.processResponse(simpleResponse, null, null, false, false);

		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.isShowStatus()).isFalse();
		assertThat(response.getBody()).isEqualTo(BODY);
		assertThat(response.getHeaders()).isNull();
	}

	@Test
	public void shouldShowStatusForError() throws Exception {
		RestResponse response = processor.processResponse(new ResponseEntity<>(BODY_BYTES, HttpStatus.INTERNAL_SERVER_ERROR), null, null, false, false);

		assertThat(response.getStatus()).isEqualTo(500);
		assertThat(response.isShowStatus()).isTrue();
		assertThat(response.getBody()).isEqualTo(BODY);
		assertThat(response.getHeaders()).isNull();
	}

	@Test
	public void shouldPrettyPrintJson() throws Exception {
		RestResponse response = processor.processResponse(jsonResponse, null, null, false, false);
		assertThat(response.getBody()).isEqualTo(jsonPretty);
	}

	@Test
	public void shouldShowRawJson() throws Exception {
		RestResponse response = processor.processResponse(jsonResponse, null, null, true, false);
		assertThat(response.getBody()).isEqualTo(jsonRaw);
	}

	@Test
	public void shouldShowInvalidJson() throws Exception {
		String invalidJson = "foo";
		ResponseEntity<byte[]> invalid = new ResponseEntity<>(invalidJson.getBytes(), jsonHeaders, HttpStatus.OK);
		RestResponse response = processor.processResponse(invalid, null, null, false, false);
		assertThat(response.getBody()).isEqualTo(invalidJson);
	}

	@Test
	public void shouldShowJQResult() throws Exception {
		RestResponse response = processor.processResponse(jsonResponse, ".foo", null, false, false);
		assertThat(response.getBody()).isEqualTo("\"bar\"");
	}

	@Test
	public void shouldShowJQMultiResult() throws Exception {
		RestResponse response = processor.processResponse(jsonResponse, ".arr", null, false, false);
		assertThat(response.getBody()).isEqualTo("[1,2]");
	}

	@Test
	public void shouldWriteResponseToFile() throws Exception {
		File target = folder.newFile();
		RestResponse response = processor.processResponse(simpleResponse, null, target, false, false);
		assertThat(response.getBody())
				.contains(target.getAbsolutePath())
				.contains("5 bytes")
		;
		assertThat(target).isFile().hasContent(BODY);
	}

	@Test
	public void shouldWriteJsonResponseToFile() throws Exception {
		File target = folder.newFile();
		RestResponse response = processor.processResponse(jsonResponse, null, target, false, false);
		assertThat(response.getBody())
				.contains(target.getAbsolutePath())
				.contains("28 bytes")
		;
		assertThat(target).isFile().hasContent(jsonRaw);
	}

}