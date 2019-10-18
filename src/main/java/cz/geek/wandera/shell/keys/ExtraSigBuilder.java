package cz.geek.wandera.shell.keys;

import java.time.Instant;
import java.util.Map;

import cz.geek.wandera.shell.rest.HttpHeader;

public class ExtraSigBuilder extends SigBuilder {

	private final HttpHeader extraHeader;

	public ExtraSigBuilder(Instant timestamp, HttpHeader extraHeader) {
		super(timestamp);
		this.extraHeader = extraHeader;
	}

	@Override
	protected Map<String, String> createValues(String path) {
		Map<String, String> values = super.createValues(path);
		values.put(extraHeader.getName(), extraHeader.getValue());
		return values;
	}

}
