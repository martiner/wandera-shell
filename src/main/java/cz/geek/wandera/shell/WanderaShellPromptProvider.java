package cz.geek.wandera.shell;

import java.util.Optional;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

import cz.geek.wandera.shell.keys.KeysHolder;
import cz.geek.wandera.shell.rest.RestService;

@Component
public class WanderaShellPromptProvider implements PromptProvider {

	private static final AttributedStyle STYLE = AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW).bold();
	private static final AttributedStyle BLUE = AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE);

	private final KeysHolder holder;
	private final RestService service;

	public WanderaShellPromptProvider(KeysHolder holder, RestService service) {
		this.holder = holder;
		this.service = service;
	}

	@Override
	public AttributedString getPrompt() {
		AttributedStringBuilder builder = new AttributedStringBuilder();
		builder.append("wnd:", STYLE);
		if (holder.hasKeys()) {
			String keyName = Optional.ofNullable(holder.getKeys().getName()).orElse("(unnamed)");
			builder.append(keyName, BLUE)
					.append(":", STYLE);
		}
		if (service.hasLastUri()) {
			builder.append(service.getLastUri().toString());
		}

		builder.append("> ", STYLE);
		return builder.toAttributedString();
	}
}
