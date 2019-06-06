package cz.geek.wandera.shell.conversion;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import cz.geek.wandera.shell.rest.HttpHeader;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class StringToHttpHeaderConverterTest {

	private StringToHttpHeaderConverter converter = new StringToHttpHeaderConverter();

	@Test
	@Parameters
	public void shouldConvert(String source, HttpHeader header) throws Exception {
		assertThat(converter.convert(source))
				.hasFieldOrPropertyWithValue("name", header.getName())
				.hasFieldOrPropertyWithValue("value", header.getValue());
	}

	public Object[][] parametersForShouldConvert() {
		return new Object[][] {
				new Object[] { "foo", new HttpHeader("foo", null)},
				new Object[] { "foo=bar", new HttpHeader("foo", "bar")},
				new Object[] { "foo=bar=baz", new HttpHeader("foo", "bar=baz")},
		};
	}

}