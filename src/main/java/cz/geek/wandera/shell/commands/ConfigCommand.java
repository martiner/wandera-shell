package cz.geek.wandera.shell.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import cz.geek.wandera.shell.rest.WanderaKeys;

@ShellComponent
public class ConfigCommand {

	private final WanderaKeys keys;

	public ConfigCommand(WanderaKeys keys) {
		this.keys = keys;
	}

	@ShellMethod("Configure keys")
	public void key(String api, String secret) {
		keys.setApiKey(api);
		keys.setSecretKey(secret);
	}
}
