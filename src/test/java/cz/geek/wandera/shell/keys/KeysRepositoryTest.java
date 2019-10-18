package cz.geek.wandera.shell.keys;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import cz.geek.wandera.shell.WanderaShellDirectory;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
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
	public void shouldReturnNullOnNoDefault() throws Exception {
		WanderaKeys keys = repository.loadDefault();
		assertThat(keys).isNull();
	}

	@Test
	@Parameters(method = "keys")
	public void shouldStoreLoadAndListKeys(WanderaKeys original) throws Exception {
		repository.store(original);

		WanderaKeys keys = repository.loadAndSaveDefault("foo");
		assertKeysEqual(original, keys);

		WanderaKeys defaultKeys = repository.loadDefault();
		assertKeysEqual(original, defaultKeys);

		Map<String, WanderaKeys> keysMap = repository.list();
		assertThat(keysMap).isNotEmpty();
		assertThat(keysMap).containsKeys("foo");

		WanderaKeys fooKey = keysMap.get("foo");
		assertKeysEqual(original, fooKey);
	}

	public Object[][] keys() {
		return new Object[][] {
				new Object[] { new WanderaKeys("foo", "API", "SECRET", null, null) },
				new Object[] { new WanderaKeys("foo", "API", "SECRET", "SERVICE", null) },
				new Object[] { new WanderaKeys("foo", "API", "SECRET", null, "NODE") },
		};
	}
	private void assertKeysEqual(WanderaKeys original, WanderaKeys keys) {
		assertThat(keys).isNotNull();
		assertThat(keys.getApiKey()).isEqualTo(original.getApiKey());
		assertThat(keys.getSecretKey()).isEqualTo(original.getSecretKey());
	}
}