package cz.geek.wandera.shell.rest;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.util.stream.Stream;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestService {

	static {
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
	}

	private final RestTemplate restTemplate;
	private URI lastUri;

	public RestService(RestTemplateBuilder builder, WanderaSigningInterceptor signingInterceptor) {
		this.restTemplate = builder
				.additionalInterceptors(signingInterceptor)
				.errorHandler(new NoopResponseErrorHandler())
				.build();
	}

	public ResponseEntity<byte[]> exchange(RestRequest request) {
		final HttpEntity<?> entity = createEntity(request);
		final URI requestUri = createUri(request.getUri());
		return restTemplate.exchange(requestUri, request.getMethod(), entity, byte[].class);
	}

	void clearLastUri() {
		lastUri = null;
	}

	private HttpEntity<?> createEntity(RestRequest request) {
		requireNonNull(request, "request");
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(request.getContentType());
		Stream.of(request.getHeaders())
				.forEach(header -> headers.set(header.getName(), header.getValue()));
		return new HttpEntity<>(request.getBody(), headers);
	}

	private URI createUri(String uriString) {
		URI uri = URI.create(uriString);
		if (!uri.isAbsolute()) {
			if (lastUri == null) {
				throw new UnableToResolveUriException("URI is not absolute and no last URI to resolve");
			}
			uri = lastUri.resolve(uri);
		}
		return lastUri = uri;
	}
}
