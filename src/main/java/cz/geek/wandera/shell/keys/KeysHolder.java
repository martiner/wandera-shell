package cz.geek.wandera.shell.keys;

import static java.util.Objects.requireNonNull;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Component;

@Component
public class KeysHolder {

	private WanderaKeys keys;

	public KeysHolder() {
	}

	public KeysHolder(WanderaKeys keys) {
		this.keys = keys;
	}

	public void hold(WanderaKeys keys) {
		this.keys = requireNonNull(keys);
	}

	public WanderaKeys getKeys() {
		if (keys == null) {
			throw new NoSuchElementException();
		}
		return keys;
	}

	public boolean hasKeys() {
		return keys != null;
	}
}
