package cz.geek.wandera.shell.rest;

import static java.util.Objects.requireNonNull;

import java.net.URI;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestService {

	private final RestTemplate restTemplate;
	private URI lastUri;

	public RestService(RestTemplateBuilder builder, WanderaSigningInterceptor signingInterceptor) {
		this.restTemplate = builder
				.additionalInterceptors(signingInterceptor)
				.errorHandler(new NoopResponseErrorHandler())
				.build();
	}

	public <T> ResponseEntity<T> get(String uri, Class<T> cls) {
		return restTemplate.getForEntity(createUri(uri), cls);
	}

	public <T> ResponseEntity<T> post(String uri, RestRequest request, Class<T> cls) {
		final HttpEntity<?> entity = createEntity(request);
		return restTemplate.exchange(createUri(uri), HttpMethod.POST, entity, cls);
	}

	public <T> ResponseEntity<T> put(String uri, RestRequest request, Class<T> cls) {
		final HttpEntity<?> entity = createEntity(request);
		return restTemplate.exchange(createUri(uri), HttpMethod.PUT, entity, cls);
	}

	private HttpEntity<?> createEntity(RestRequest request) {
		requireNonNull(request, "request");
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(request.getContentType());
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
