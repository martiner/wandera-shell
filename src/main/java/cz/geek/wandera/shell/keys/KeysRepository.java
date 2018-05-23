package cz.geek.wandera.shell.keys;

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
	private static final String API = "api";
	private static final String SECRET = "secret";
	private final Path keysFile;

	public KeysRepository(WanderaShellDirectory directory) {
		keysFile = directory.getKeysFile();
	}

	public void store(String name, WanderaKeys keys) {
		Properties properties = loadProperties();
		properties.put(apiName(name), keys.getApiKey());
		properties.put(secretName(name), keys.getSecretKey());
		saveProperties(properties);
	}

	public WanderaKeys load(String name) {
		Properties properties = loadProperties();
		String api = properties.getProperty(apiName(name));
		String secret = properties.getProperty(secretName(name));

		if (api == null || secret == null) {
			throw new IllegalArgumentException("Keys named " + name + " don't exist");
		}
		return new WanderaKeys(api, secret);
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
		if (API.equals(suffix)) {
			wk.setApiKey(value);
		} else if (SECRET.equals(suffix)) {
			wk.setSecretKey(value);
		} else {
			return;
		}
		keys.put(name, wk);
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

	private static String secretName(String name) {
		return KEYS_PREFIX + DELIMITER + name + DELIMITER + SECRET;
	}

	private static String apiName(String name) {
		return KEYS_PREFIX + DELIMITER + name + DELIMITER + API;
	}
}
