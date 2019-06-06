package cz.geek.wandera.shell.rest;

import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.port;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.MockRestServiceServerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import cz.geek.wandera.shell.keys.KeysHolder;
import cz.geek.wandera.shell.keys.WanderaKeys;
import net.jadler.junit.rule.JadlerRule;
import net.jadler.stubbing.server.jdk.JdkStubHttpServer;

@RunWith(SpringRunner.class)
@RestClientTest(
		components = {RestService.class, WanderaSigningInterceptor.class, KeysHolder.class},
		excludeAutoConfiguration = MockRestServiceServerAutoConfiguration.class
)
public class RestServiceTest {

	@Rule
	public JadlerRule defaultJadler = new JadlerRule(new JdkStubHttpServer())
			.withDefaultResponseContentType("application/json")
			.withDefaultResponseStatus(HttpStatus.NOT_FOUND.value());

	@Autowired
	private KeysHolder holder;

	@Autowired
	private RestService service;

	@Before
	public void setUp() throws Exception {
		holder.hold(new WanderaKeys("key", "secret"));
	}

	@Test
	@DirtiesContext
	public void shouldPerformSecondGetRequestWithRelativeUri() throws Exception {
		onRequest()
				.havingMethodEqualTo("GET")
				.havingPathEqualTo("/1")
			.respond()
				.withStatus(200)
				.withContentType("text/plain")
				.withBody("foo");
		onRequest()
				.havingMethodEqualTo("GET")
				.havingPathEqualTo("/2")
			.respond()
				.withStatus(200)
				.withContentType("text/plain")
				.withBody("bar");

		ResponseEntity<byte[]> response = service.exchange(RestRequest.create(GET, "http://localhost:" + port() + "/1").build());
		assertThat(response.getBody(), is("foo".getBytes()));

		ResponseEntity<byte[]> second = service.exchange(RestRequest.create(GET, "/2").build());
		assertThat(second.getBody(), is("bar".getBytes()));
	}

	@Test(expected = UnableToResolveUriException.class)
	public void shouldFailFirstRequestWithRelativeUri() throws Exception {
		service.clearLastUri();
		service.exchange(RestRequest.create(GET, "/1").build());
	}

	@Test
	public void shouldPostRequest() throws Exception {
		onRequest()
				.havingMethodEqualTo("POST")
				.havingPathEqualTo("/2")
				.havingHeaderEqualTo("Content-type", "application/json")
				.havingBodyEqualTo("foo")
			.respond()
				.withStatus(200)
				.withContentType("text/plain")
				.withBody("bar");

		RestRequest request = RestRequest.create(POST, "http://localhost:" + port() + "/2").data("foo").build();
		ResponseEntity<byte[]> response = service.exchange(request);
		assertThat(response.getBody(), is("bar".getBytes()));
	}

	@Test
	public void shouldPutRequest() throws Exception {
		onRequest()
				.havingMethodEqualTo("PUT")
				.havingPathEqualTo("/2")
				.havingHeaderEqualTo("Content-type", "application/json")
				.havingBodyEqualTo("foo")
			.respond()
				.withStatus(200)
				.withContentType("text/plain")
				.withBody("bar");

		RestRequest request = RestRequest.create(PUT, "http://localhost:" + port() + "/2").data("foo").build();
		ResponseEntity<byte[]> response = service.exchange(request);
		assertThat(response.getBody(), is("bar".getBytes()));
	}

	@Test
	public void shouldDeleteRequest() throws Exception {
		onRequest()
				.havingMethodEqualTo("DELETE")
				.havingPathEqualTo("/2")
			.respond()
				.withStatus(204);

		RestRequest request = RestRequest.create(DELETE, "http://localhost:" + port() + "/2").build();
		ResponseEntity<byte[]> response = service.exchange(request);
		assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
	}

	@Test
	public void shouldGetWithViaHeader() throws Exception {
		onRequest()
				.havingMethodEqualTo("GET")
				.havingPathEqualTo("/2")
				.havingHeaderEqualTo("Via", "foo")
			.respond()
				.withStatus(200);

		RestRequest request = RestRequest.create(GET, "http://localhost:" + port() + "/2")
				.headers(new HttpHeader("Via", "foo"))
				.build();
		ResponseEntity<byte[]> response = service.exchange(request);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}
}