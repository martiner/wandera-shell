package cz.geek.wandera.shell.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

public class RestResponseTest {

	private RestResponse response;
	private HttpHeaders headers;

	@Before
	public void setUp() throws Exception {
		response = new RestResponse(200);
		headers = new HttpHeaders();
	}

	@Test
	public void shouldReturnEmptyString() throws Exception {
		assertThat(response.toString(), is(""));
	}

	@Test
	public void shouldReturnStatus() throws Exception {
		response.showStatus();
		assertThat(response.toString(), is("Status: 200"));
	}

	@Test
	public void shouldReturnBody() throws Exception {
		response.body("foo");
		assertThat(response.toString(), is("foo"));
	}

	@Test
	public void shouldReturnHeaders() throws Exception {
		headers.set("foo", "1");
		headers.add("foo", "2");
		response.headers(headers);
		assertThat(response.toString(), is("foo: 1\nfoo: 2"));
	}

	@Test
	public void shouldReturnAll() throws Exception {
		headers.set("foo", "1");
		response.showStatus()
				.body("bar")
				.headers(headers);

		String result = response.toString();
		assertThat(result, containsString("Status: 200"));
		assertThat(result, containsString("foo: 1"));
		assertThat(result, containsString("bar"));
	}
}