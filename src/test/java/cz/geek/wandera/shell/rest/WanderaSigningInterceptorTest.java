package cz.geek.wandera.shell.rest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import cz.geek.wandera.shell.keys.KeysHolder;
import cz.geek.wandera.shell.keys.WanderaKeys;

@RunWith(SpringRunner.class)
@RestClientTest(RestService.class)
public class WanderaSigningInterceptorTest {

	private static final Instant TIMESTAMP = Instant.ofEpochSecond(3600);

	@Autowired
	private MockRestServiceServer server;

	@Autowired
	private RestService service;

	@Autowired
	private KeysHolder holder;

	@Test
	public void shouldAddHeaders() throws Exception {
		WanderaKeys keys = new WanderaKeys("api", "secret");
		holder.hold(keys);

		server.expect(requestTo("http://localhost/test"))
				.andExpect(header("X-Sig", is(notNullValue())))
				.andExpect(header("X-Ts", is(Long.toString(TIMESTAMP.toEpochMilli()))))
				.andExpect(header("X-Key", is(keys.getApiKey())))
				// Spring 5.2 .andExpect(headerDoesNotExist("X-Service"))
				// Spring 5.2 .andExpect(headerDoesNotExist("X-Connector-Node"))
				.andRespond(withSuccess());

		service.exchange(RestRequest.create(GET, "http://localhost/test").build());
	}

	@Test
	public void shouldAddServiceHeaders() throws Exception {
		WanderaKeys keys = new WanderaKeys("api", "secret", "service", null);
		holder.hold(keys);

		server.expect(requestTo("http://localhost/test"))
				.andExpect(header("X-Sig", is(notNullValue())))
				.andExpect(header("X-Ts", is(Long.toString(TIMESTAMP.toEpochMilli()))))
				.andExpect(header("X-Key", is(keys.getApiKey())))
				.andExpect(header("X-Service", is(keys.getService())))
				// Spring 5.2 .andExpect(headerDoesNotExist("X-Connector-Node"))
				.andRespond(withSuccess());

		service.exchange(RestRequest.create(GET, "http://localhost/test").build());
	}

	@Test
	public void shouldAddConnectorHeaders() throws Exception {
		WanderaKeys keys = new WanderaKeys("api", "secret", null, "node");
		holder.hold(keys);

		server.expect(requestTo("http://localhost/test"))
				.andExpect(header("X-Sig", is(notNullValue())))
				.andExpect(header("X-Ts", is(Long.toString(TIMESTAMP.toEpochMilli()))))
				.andExpect(header("X-Key", is(keys.getApiKey())))
				.andExpect(header("X-Connector-Node", is(keys.getConnectorNode())))
				// Spring 5.2 .andExpect(headerDoesNotExist("X-Service"))
				.andRespond(withSuccess());

		service.exchange(RestRequest.create(GET, "http://localhost/test").build());
	}

	static class Config {

		@Bean WanderaSigningInterceptor interceptor(KeysHolder holder) {
			Clock clock = Clock.fixed(TIMESTAMP, ZoneId.systemDefault());
			return new WanderaSigningInterceptor(holder, clock);
		}

		@Bean KeysHolder holder() {
			return new KeysHolder();
		}
	}
}