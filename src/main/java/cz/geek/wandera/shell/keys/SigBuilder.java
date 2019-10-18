package cz.geek.wandera.shell.keys;

import java.net.URI;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.HmacUtils;

public class SigBuilder {

	private final String timestamp;

	public SigBuilder(Instant timestamp) {
		this.timestamp = Long.toString(timestamp.toEpochMilli());
	}

	private static String createPath(URI uri) {
		StringBuilder path = new StringBuilder(uri.getPath());
		String query = uri.getQuery();
		if (query != null) {
			path.append("?").append(query);
		}
		return path.toString();
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String createSig(String secretKey, URI uri) {
		String value = createValue(createPath(uri));
		byte[] hmacSha1 = HmacUtils.hmacSha1(secretKey, value);
		return Base64.getEncoder().encodeToString(hmacSha1);
	}

	private String createValue(String path) {
		return createValues(path).entrySet().stream()
				.map(entry -> entry.getKey() + "=" + entry.getValue() + ";")
				.collect(Collectors.joining());
	}

	protected Map<String, String> createValues(String path) {
		Map<String, String> values = new LinkedHashMap<>();
		values.put("ts", timestamp);
		values.put("path", path);
		return values;
	}

}
