package cz.geek.wandera.shell;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
public class WanderaShell {

	public static void main(String[] args) {
		SpringApplication.run(WanderaShell.class, args);
	}

	@Bean
	public PromptProvider promptProvider() {
		return () -> new AttributedString("wnd:> ", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
	}
}
