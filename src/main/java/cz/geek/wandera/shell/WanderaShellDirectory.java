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

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WanderaShellDirectory {

	private static final FileAttribute<Set<PosixFilePermission>> ATTRS = asFileAttribute(new HashSet<>(asList(
			OWNER_READ, OWNER_WRITE, OWNER_EXECUTE)));

	private final Path directory;

	@Autowired
	public WanderaShellDirectory() {
		this(System.getProperty("user.home"));
	}

	public WanderaShellDirectory(String rootDirectory) {
		directory = Paths.get(rootDirectory, ".wanderashell");
	}

	@PostConstruct
	public void ensureDirExists() {
		try {
			Files.createDirectories(directory, ATTRS);
		} catch (IOException ignored) {
		}
	}

	public Path getHistoryFile() {
		return directory.resolve("history.log");
	}

	public Path getKeysFile() {
		return directory.resolve("keys.properties");
	}
}
