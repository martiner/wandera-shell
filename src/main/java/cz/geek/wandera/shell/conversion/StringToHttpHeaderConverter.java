package cz.geek.wandera.shell.conversion;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import cz.geek.wandera.shell.rest.HttpHeader;

@Component
public class StringToHttpHeaderConverter implements Converter<String, HttpHeader> {

	@Override
	public HttpHeader convert(String source) {
		String[] result = source.split("=", 2);
		if (result.length == 0 || result.length > 2) {
			throw new IllegalArgumentException("Expected a single delimiter");
		}
		return new HttpHeader(result[0], result.length == 2 ? result[1] : null);
	}
}
