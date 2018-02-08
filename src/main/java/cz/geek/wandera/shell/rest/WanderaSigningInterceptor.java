package cz.geek.wandera.shell.rest;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class WanderaSigningInterceptor implements ClientHttpRequestInterceptor {

	private final WanderaKeys wanderaKeys;
	private final Clock clock;

	WanderaSigningInterceptor(WanderaKeys wanderaKeys, Clock clock) {
		this.clock = requireNonNull(clock, "clock");
		this.wanderaKeys = requireNonNull(wanderaKeys, "keys");
	}

	@Autowired
	public WanderaSigningInterceptor(WanderaKeys wanderaKeys) {
		this(wanderaKeys, Clock.systemUTC());
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		String path = request.getURI().getPath();
		long timestamp = Instant.now(clock).toEpochMilli();

		String value = "ts=" + timestamp + ";path=" + path + ";";
		byte[] hmacSha1 = HmacUtils.hmacSha1(wanderaKeys.getSecretKey(), value);
		String sig = Base64.getEncoder().encodeToString(hmacSha1);

		request.getHeaders().set("X-Key", wanderaKeys.getApiKey());
		request.getHeaders().set("X-Sig", sig);
		request.getHeaders().set("X-TS", Long.toString(timestamp));

		return execution.execute(request, body);
	}
}
