package cz.geek.wandera.shell.rest;

import static cz.geek.wandera.shell.keys.WanderaKeys.KEY_HEADER;
import static cz.geek.wandera.shell.keys.WanderaKeys.SIG_HEADER;
import static cz.geek.wandera.shell.keys.WanderaKeys.TIMESTAMP_HEADER;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import cz.geek.wandera.shell.keys.KeysHolder;
import cz.geek.wandera.shell.keys.SigBuilder;
import cz.geek.wandera.shell.keys.WanderaKeys;

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

		WanderaKeys keys = holder.getKeys();
		Instant timestamp = Instant.now(clock);

		SigBuilder builder = new SigBuilder(timestamp);
		String sig = builder.createSig(keys.getSecretKey(), request.getURI());

		HttpHeaders headers = request.getHeaders();
		headers.set(KEY_HEADER, keys.getApiKey());
		headers.set(SIG_HEADER, sig);
		headers.set(TIMESTAMP_HEADER, builder.getTimestamp());

		return execution.execute(request, body);
	}

}
