package cz.geek.wandera.shell.commands;

import static org.springframework.shell.standard.ShellOption.NULL;

import javax.annotation.PostConstruct;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
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

	@PostConstruct
	public void loadDefault() {
		WanderaKeys keys = repository.loadDefault();
		if (keys != null) {
			holder.hold(keys);
		}
	}

	@ShellMethod(key = "key set", value = "Set keys")
	public void key(String api, String secret,
			@ShellOption(defaultValue = NULL) String service, @ShellOption(defaultValue = NULL) String connectorNode) {
		holder.hold(new WanderaKeys(api, secret, service, connectorNode));
	}

	@ShellMethod(key = "key save", value = "Set and save keys")
	public void keySave(String name, String api, String secret,
			@ShellOption(defaultValue = NULL) String service, @ShellOption(defaultValue = NULL) String connectorNode) {
		WanderaKeys keys = new WanderaKeys(name, api, secret, service, connectorNode);
		holder.hold(keys);
		repository.store(keys);
	}

	@ShellMethod(key = "key use", value = "Use saved keys")
	public void keyUse(String name) {
		WanderaKeys keys = repository.loadAndSaveDefault(name);
		holder.hold(keys);
	}

	@ShellMethod(key = "key list", value = "List all keys")
	public String keyList() {
		TableModelBuilder<String> model = new TableModelBuilder<String>()
				.addRow()
				.addValue("Name")
				.addValue("API key")
				.addValue("Secret key")
				.addValue("Service")
				.addValue("Connector Node");

		repository.list().forEach((name, value) ->
				model.addRow()
						.addValue(name)
						.addValue(value.getApiKey())
						.addValue(value.getSecretKey())
						.addValue(value.getService())
						.addValue(value.getConnectorNode()));

		return new TableBuilder(model.build())
				.addHeaderAndVerticalsBorders(BorderStyle.oldschool)
				.build()
				.render(80);
	}
}
