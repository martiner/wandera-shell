package cz.geek.wandera.shell.commands;

import java.util.Map;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import cz.geek.wandera.shell.keys.KeysHolder;
import cz.geek.wandera.shell.keys.KeysRepository;
import cz.geek.wandera.shell.keys.WanderaKeys;

@ShellComponent
public class KeyCommand {

	private final KeysHolder holder;
	private final KeysRepository repository;

	public KeyCommand(KeysHolder holder, KeysRepository repository) {
		this.holder = holder;
		this.repository = repository;
	}

	@ShellMethod(key = "key", value = "Configure keys")
	public void key(String api, String secret) {
		holder.hold(new WanderaKeys(api, secret));
	}

	@ShellMethod(key = "key save", value = "Configure and save keys")
	public void keySave(String name, String api, String secret) {
		WanderaKeys keys = new WanderaKeys(api, secret);
		holder.hold(keys);
		repository.store(name, keys);
	}

	@ShellMethod(key = "key load", value = "Load saved keys")
	public void keyLoad(String name) {
		WanderaKeys keys = repository.load(name);
		holder.hold(keys);
	}

	@ShellMethod(key = "key list", value = "List all keys")
	public Map<String, WanderaKeys> keyList(String name) {
		return repository.list();
	}
}
