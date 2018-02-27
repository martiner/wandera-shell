package cz.geek.wandera.shell.rest;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestService {

	private final RestTemplate restTemplate;

	public RestService(RestTemplateBuilder builder, WanderaSigningInterceptor signingInterceptor) {
		this.restTemplate = builder
				.additionalInterceptors(signingInterceptor)
				.build();
	}

	public <T> ResponseEntity<T> get(String uri, Class<T> cls) {
		return restTemplate.getForEntity(uri, cls);
	}
}
