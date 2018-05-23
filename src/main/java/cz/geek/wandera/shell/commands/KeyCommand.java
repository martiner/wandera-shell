package cz.geek.wandera.shell.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;

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

	@ShellMethod(key = "key set", value = "Set keys")
	public void key(String api, String secret) {
		holder.hold(new WanderaKeys(api, secret));
	}

	@ShellMethod(key = "key save", value = "Set and save keys")
	public void keySave(String name, String api, String secret) {
		WanderaKeys keys = new WanderaKeys(api, secret);
		holder.hold(keys);
		repository.store(name, keys);
	}

	@ShellMethod(key = "key use", value = "Use saved keys")
	public void keyUse(String name) {
		WanderaKeys keys = repository.load(name);
		holder.hold(keys);
	}

	@ShellMethod(key = "key list", value = "List all keys")
	public String keyList() {
		TableModelBuilder<String> model = new TableModelBuilder<String>()
				.addRow()
				.addValue("Name")
				.addValue("API key")
				.addValue("Secret key");

		repository.list().forEach((name, value) ->
				model.addRow()
						.addValue(name)
						.addValue(value.getApiKey())
						.addValue(value.getSecretKey()));

		return new TableBuilder(model.build())
				.addHeaderAndVerticalsBorders(BorderStyle.oldschool)
				.build()
				.render(80);
	}
}
