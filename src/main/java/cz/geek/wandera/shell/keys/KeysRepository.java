package cz.geek.wandera.shell.keys;

import static cz.geek.wandera.shell.keys.RepositoryProp.API;
import static cz.geek.wandera.shell.keys.RepositoryProp.CONNECTOR_NODE;
import static cz.geek.wandera.shell.keys.RepositoryProp.SECRET;
import static cz.geek.wandera.shell.keys.RepositoryProp.SERVICE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cz.geek.wandera.shell.WanderaShellDirectory;

@Service
public class KeysRepository  {

	private static final String DELIMITER = ".";
	private static final String KEYS_PREFIX = "keys";
	private static final String DEFAULT_KEY_NAME = "key.default";

	private final Path keysFile;

	public KeysRepository(WanderaShellDirectory directory) {
		keysFile = directory.getKeysFile();
	}

	public void store(WanderaKeys keys) {
		Properties properties = loadProperties();

		RepositoryProp.stream()
				.filter(prop -> prop.hasValue(keys))
				.forEach(prop -> properties.put(createPropertyName(keys.getName(), prop), prop.getValue(keys)));

		storeDefault(properties, keys.getName());
		saveProperties(properties);
	}

	private void storeDefault(Properties properties, String name) {
		properties.put(DEFAULT_KEY_NAME, name);
	}

	public WanderaKeys loadAndSaveDefault(String name) {
		Properties properties = loadProperties();
		WanderaKeys keys = getKeys(properties, name);
		storeDefault(properties, name);
		try {
			saveProperties(properties);
		} catch (IllegalStateException ignored) {
		}
		return keys;
	}

	public WanderaKeys loadDefault() {
		Properties properties = loadProperties();
		String name = properties.getProperty(DEFAULT_KEY_NAME);
		if (name == null) {
			return null;
		}
		try {
			return getKeys(properties, name);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private WanderaKeys getKeys(Properties properties, String name) {
		String api = properties.getProperty(createPropertyName(name, API));
		String secret = properties.getProperty(createPropertyName(name, SECRET));
		String service = properties.getProperty(createPropertyName(name, SERVICE));
		String connectorNode = properties.getProperty(createPropertyName(name, CONNECTOR_NODE));

		if (api == null || secret == null) {
			throw new IllegalArgumentException("Keys named " + name + " don't exist");
		}
		return new WanderaKeys(name, api, secret, service, connectorNode);
	}

	public Map<String, WanderaKeys> list() {
		Properties properties = loadProperties();
		Map<String, WanderaKeys> keys = new LinkedHashMap<>();
		properties.forEach((key, value) -> addOrUpdateKey(keys, (String)key, (String)value));
		return keys;
	}

	private void addOrUpdateKey(Map<String, WanderaKeys> keys, String key, String value) {
		String[] items = StringUtils.tokenizeToStringArray(key, DELIMITER);
		if (items.length != 3) {
			return;
		}

		String prefix = items[0];
		String name = items[1];
		String suffix = items[2];

		if (!KEYS_PREFIX.equals(prefix)) {
			return;
		}

		WanderaKeys wk = keys.getOrDefault(name, new WanderaKeys());
		RepositoryProp.stream()
				.filter(prop -> prop.hasPropertyName(suffix))
				.peek(prop -> prop.setValue(wk, value))
				.findFirst()
				.map(prop -> keys.put(name, wk))
		;
	}

	private Properties loadProperties() {
		Properties properties = new Properties();
		if (!Files.isRegularFile(keysFile)) {
			return properties;
		}
		try (InputStream input = Files.newInputStream(keysFile)) {
			properties.load(input);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to load " + keysFile, e);
		}
		return properties;
	}

	private void saveProperties(Properties properties) {
		try (OutputStream output = Files.newOutputStream(keysFile)) {
			properties.store(output, "Wandera Shell Keys");
		} catch (IOException e) {
			throw new IllegalStateException("Unable to save " + keysFile, e);
		}
	}

	private static String createPropertyName(String name, RepositoryProp property) {
		return KEYS_PREFIX + DELIMITER + name + DELIMITER + property.getPropertyName();
	}

}
