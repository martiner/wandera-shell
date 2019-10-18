package cz.geek.wandera.shell;

import java.util.Optional;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

import cz.geek.wandera.shell.keys.KeysHolder;

@Component
public class WanderaShellPromptProvider implements PromptProvider {

	private static final AttributedStyle STYLE = AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW);

	private final KeysHolder holder;

	public WanderaShellPromptProvider(KeysHolder holder) {
		this.holder = holder;
	}

	@Override
	public AttributedString getPrompt() {
		StringBuilder builder = new StringBuilder();
		builder.append("wnd:");
		if (holder.hasKeys()) {
			String keyName = Optional.ofNullable(holder.getKeys().getName()).orElse("(unnamed)");
			builder.append(keyName).append(":");
		}

		builder.append("> ");
		return new AttributedString(builder, STYLE);
	}
}
