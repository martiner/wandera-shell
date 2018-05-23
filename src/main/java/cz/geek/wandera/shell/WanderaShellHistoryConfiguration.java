package cz.geek.wandera.shell;

import java.io.IOException;

import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.impl.history.DefaultHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

@Configuration
public class WanderaShellHistoryConfiguration {

	@Autowired @Lazy
	private History history;

	@Bean
	public History history(LineReader lineReader, WanderaShellDirectory directory) {
		lineReader.setVariable(LineReader.HISTORY_FILE, directory.getHistoryFile());
		return new DefaultHistory(lineReader);
	}

	@EventListener
	public void onContextClosedEvent(ContextClosedEvent event) throws IOException {
		history.save();
	}

}
