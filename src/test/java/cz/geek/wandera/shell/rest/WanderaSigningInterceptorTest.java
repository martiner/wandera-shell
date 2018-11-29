package cz.geek.wandera.shell.rest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
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
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import cz.geek.wandera.shell.keys.KeysHolder;
import cz.geek.wandera.shell.keys.WanderaKeys;

@RunWith(SpringRunner.class)
@RestClientTest(RestService.class)
public class WanderaSigningInterceptorTest {

	@Autowired
	private MockRestServiceServer server;

	@Autowired
	private RestService service;

	@Test
	public void shouldAddHeaders() throws Exception {
		server.expect(requestTo("http://localhost/test"))
				.andExpect(header("X-Sig", is(notNullValue())))
				.andExpect(header("X-Ts", is("3600000")))
				.andExpect(header("X-Key", is("api")))
				.andRespond(withSuccess());

		service.exchange("http://localhost/test", HttpMethod.GET);
	}

	static class Config {

		@Bean WanderaSigningInterceptor interceptor() {
			WanderaKeys keys = new WanderaKeys("api", "secret");
			Clock clock = Clock.fixed(Instant.ofEpochSecond(3600), ZoneId.systemDefault());
			return new WanderaSigningInterceptor(new KeysHolder(keys), clock);
		}

	}
}