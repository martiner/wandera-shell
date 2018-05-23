package cz.geek.wandera.shell.keys;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class KeysRepository  {

	private final Map<String, WanderaKeys> keys = new LinkedHashMap<>();

	public void store(String name, WanderaKeys keys) {
		this.keys.put(name, keys);
	}

	public WanderaKeys load(String name) {
		WanderaKeys keys = this.keys.get(name);
		if (keys == null) {
			throw new IllegalArgumentException("Invalid name " + this.keys.keySet());
		}
		return keys;
	}

	public Map<String, WanderaKeys> list() {
		return keys;
	}
}
