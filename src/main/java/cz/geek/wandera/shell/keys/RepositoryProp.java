package cz.geek.wandera.shell.keys;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

enum RepositoryProp {

	API("api", WanderaKeys::getApiKey, WanderaKeys::setApiKey),
	SECRET("secret", WanderaKeys::getSecretKey, WanderaKeys::setSecretKey),
	SERVICE("service", WanderaKeys::getService, WanderaKeys::setService),
	CONNECTOR_NODE("connectorNode", WanderaKeys::getConnectorNode, WanderaKeys::setConnectorNode);

	private final String propertyName;
	private final Function<WanderaKeys, String> getter;
	private final BiConsumer<WanderaKeys, String> setter;

	RepositoryProp(String propertyName, Function<WanderaKeys, String> getter, BiConsumer<WanderaKeys, String> setter) {
		this.propertyName = propertyName;
		this.getter = getter;
		this.setter = setter;
	}

	String getPropertyName() {
		return propertyName;
	}

	boolean hasPropertyName(String propertyName) {
		return this.propertyName.equals(propertyName);
	}

	String getValue(WanderaKeys keys) {
		return getter.apply(keys);
	}

	boolean hasValue(WanderaKeys keys) {
		return getValue(keys) != null;
	}

	void setValue(WanderaKeys wk, String value) {
		setter.accept(wk, value);
	}

	static Stream<RepositoryProp> stream() {
		return Arrays.stream(RepositoryProp.values());
	}
}
