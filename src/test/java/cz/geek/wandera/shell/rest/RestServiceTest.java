package cz.geek.wandera.shell.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

@RunWith(SpringRunner.class)
@RestClientTest(RestService.class)
public class RestServiceTest {

	@MockBean
	private WanderaSigningInterceptor interceptor;

	@Autowired
	private MockRestServiceServer server;

	@Autowired
	private RestService service;

	@Before
	public void setUp() throws Exception {
		when(interceptor.intercept(any(), any(), any())).then(invocation -> {
			ClientHttpRequest request = invocation.getArgumentAt(0, ClientHttpRequest.class);
			byte[] body = invocation.getArgumentAt(1, byte[].class);
			ClientHttpRequestExecution execution = invocation.getArgumentAt(2, ClientHttpRequestExecution.class);
			return execution.execute(request, body);
		});
	}

	@Test
	@DirtiesContext
	public void shouldPerformSecondGetRequestWithRelativeUri() throws Exception {
		server.expect(manyTimes(), requestTo("http://localhost/1"))
				.andExpect(method(GET))
				.andRespond(withSuccess("foo", MediaType.TEXT_PLAIN));

		ResponseEntity<byte[]> response = service.exchange("http://localhost/1", GET);
		assertThat(response.getBody(), is("foo".getBytes()));

		ResponseEntity<byte[]> second = service.exchange("/1", GET);
		assertThat(second.getBody(), is("foo".getBytes()));
	}

	@Test(expected = UnableToResolveUriException.class)
	public void shouldFailFirstRequestWithRelativeUri() throws Exception {
		service.clearLastUri();
		service.exchange("/1", GET);
	}

	@Test
	public void shouldPostRequest() throws Exception {
		server.expect(requestTo("http://localhost/2"))
				.andExpect(method(POST))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string("foo"))
				.andRespond(withSuccess("bar", MediaType.TEXT_PLAIN));

		ResponseEntity<byte[]> response = service
				.exchange("http://localhost/2", POST, new RestRequest("foo"));
		assertThat(response.getBody(), is("bar".getBytes()));
	}

	@Test
	public void shouldPutRequest() throws Exception {
		server.expect(requestTo("http://localhost/2"))
				.andExpect(method(PUT))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string("foo"))
				.andRespond(withSuccess("bar", MediaType.TEXT_PLAIN));

		ResponseEntity<byte[]> response = service.exchange("http://localhost/2", PUT, new RestRequest("foo"));
		assertThat(response.getBody(), is("bar".getBytes()));
	}

	@Test
	public void shouldDeleteRequest() throws Exception {
		server.expect(requestTo("http://localhost/2"))
				.andExpect(method(DELETE))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andRespond(withNoContent());

		ResponseEntity<byte[]> response = service.exchange("http://localhost/2", DELETE);
		assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
	}
}