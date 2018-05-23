package cz.geek.wandera.shell.keys;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import cz.geek.wandera.shell.WanderaShellDirectory;

public class KeysRepositoryTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private KeysRepository repository;

	@Before
	public void setUp() throws Exception {
		WanderaShellDirectory directory = new WanderaShellDirectory(temporaryFolder.newFolder().toString());
		directory.ensureDirExists();
		repository = new KeysRepository(directory);
	}

	@Test
	public void shouldNotFailIfFileDoesNotExist() throws Exception {
		Map<String, WanderaKeys> keysMap = repository.list();
		assertThat(keysMap).isEmpty();
	}

	@Test
	public void shouldStoreLoadAndListKeys() throws Exception {
		repository.store("foo", new WanderaKeys("API", "SECRET"));

		WanderaKeys keys = repository.load("foo");
		assertThat(keys).isNotNull();
		assertThat(keys.getApiKey()).isEqualTo("API");
		assertThat(keys.getSecretKey()).isEqualTo("SECRET");

		Map<String, WanderaKeys> keysMap = repository.list();
		assertThat(keysMap).isNotEmpty();
		assertThat(keysMap).containsKeys("foo");

		WanderaKeys fooKey = keysMap.get("foo");
		assertThat(fooKey.getApiKey()).isEqualTo("API");
		assertThat(fooKey.getSecretKey()).isEqualTo("SECRET");
	}
}