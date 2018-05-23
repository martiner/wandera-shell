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

import cz.geek.wandera.shell.keys.KeysHolder;

@Component
public class WanderaSigningInterceptor implements ClientHttpRequestInterceptor {

	private final KeysHolder holder;
	private final Clock clock;

	WanderaSigningInterceptor(KeysHolder holder, Clock clock) {
		this.clock = requireNonNull(clock, "clock");
		this.holder = requireNonNull(holder, "holder");
	}

	@Autowired
	public WanderaSigningInterceptor(KeysHolder holder) {
		this(holder, Clock.systemUTC());
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		String path = request.getURI().getPath();
		long timestamp = Instant.now(clock).toEpochMilli();

		String value = "ts=" + timestamp + ";path=" + path + ";";
		byte[] hmacSha1 = HmacUtils.hmacSha1(holder.getKeys().getSecretKey(), value);
		String sig = Base64.getEncoder().encodeToString(hmacSha1);

		request.getHeaders().set("X-Key", holder.getKeys().getApiKey());
		request.getHeaders().set("X-Sig", sig);
		request.getHeaders().set("X-TS", Long.toString(timestamp));

		return execution.execute(request, body);
	}
}
