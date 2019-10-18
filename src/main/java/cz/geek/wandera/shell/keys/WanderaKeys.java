package cz.geek.wandera.shell.keys;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.Optional;

import cz.geek.wandera.shell.rest.HttpHeader;

public class WanderaKeys {

	public static final String KEY_HEADER = "X-Key";
	public static final String SIG_HEADER = "X-Sig";
	public static final String TIMESTAMP_HEADER = "X-TS";
	public static final String SERVICE_HEADER = "X-Service";
	public static final String CONNECTOR_NODE_HEADER = "X-Connector-Node";

	private String apiKey;

	private String secretKey;

	private String service;

	private String connectorNode;

	public WanderaKeys() {
	}

	public WanderaKeys(String apiKey, String secretKey) {
		this.apiKey = apiKey;
		this.secretKey = secretKey;
	}

	public WanderaKeys(String apiKey, String secretKey, String service, String connectorNode) {
		if (service != null && connectorNode != null) {
			throw new IllegalArgumentException("service and connectorNode are mutually exclusive");
		}
		this.apiKey = apiKey;
		this.secretKey = secretKey;
		this.service = service;
		this.connectorNode = connectorNode;
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

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getConnectorNode() {
		return connectorNode;
	}

	public void setConnectorNode(String connectorNode) {
		this.connectorNode = connectorNode;
	}

	public Optional<HttpHeader> getExtraHeader() {
		if (!isEmpty(service)) {
			return Optional.of(new HttpHeader(SERVICE_HEADER, service));
		}
		if (!isEmpty(connectorNode)) {
			return Optional.of(new HttpHeader(CONNECTOR_NODE_HEADER, connectorNode));
		}
		return Optional.empty();
	}
}