package cz.geek.wandera.shell;

import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;
import static java.nio.file.attribute.PosixFilePermissions.asFileAttribute;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

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

	private static final FileAttribute<Set<PosixFilePermission>> ATTRS = asFileAttribute(new HashSet<>(asList(
			OWNER_READ, OWNER_WRITE, OWNER_EXECUTE)));

	@Autowired @Lazy
	private History history;

	@Bean
	public History history(LineReader lineReader) {

		final Path dir = Paths.get(System.getProperty("user.home"), ".wanderashell");
		try {
			Files.createDirectories(dir, ATTRS);
		} catch (IOException ignored) {
		}

		lineReader.setVariable(LineReader.HISTORY_FILE, dir.resolve("history.log"));
		return new DefaultHistory(lineReader);
	}

	@EventListener
	public void onContextClosedEvent(ContextClosedEvent event) throws IOException {
		history.save();
	}

}
