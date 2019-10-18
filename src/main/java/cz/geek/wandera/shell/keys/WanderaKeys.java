package cz.geek.wandera.shell.keys;

public class WanderaKeys {

	public static final String KEY_HEADER = "X-Key";
	public static final String SIG_HEADER = "X-Sig";
	public static final String TIMESTAMP_HEADER = "X-TS";

	private String apiKey;

	private String secretKey;

	public WanderaKeys() {
	}

	public WanderaKeys(String apiKey, String secretKey) {
		this.apiKey = apiKey;
		this.secretKey = secretKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getApiKey() {
		return apiKey;
	}
}