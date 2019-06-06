package cz.geek.wandera.shell.rest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class RestRequestTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void shouldCreateRestRequestWithJsonBody() throws Exception {
		RestRequest request = RestRequest.create(GET, "uri").data("json").build();
		assertThat(request.getBody(), is("json"));
		assertThat(request.getContentType(), is(APPLICATION_JSON));
	}

	@Test
	public void shouldCreateRestRequestWithNullJsonBody() throws Exception {
		RestRequest request = RestRequest.create(GET, "uri").build();
		assertThat(request.getBody(), is(nullValue()));
		assertThat(request.getContentType(), is(APPLICATION_JSON));
	}

	@Test
	public void shouldCreateRestRequestWithFileJsonBody() throws Exception {
		File file = folder.newFile();
		FileUtils.write(file, "foo", StandardCharsets.UTF_8);

		RestRequest request = RestRequest.create(GET, "uri").data(file).build();
		assertThat(request.getBody(), is("foo"));
		assertThat(request.getContentType(), is(APPLICATION_JSON));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenFileDoesNotExist() throws Exception {
		File file = folder.newFile();
		if (!file.delete()) {
			fail("Unable to create fixture: file " + file + " can't be deleted");
		}

		RestRequest.create(GET, "uri").data(file).build();
	}

	@Test
	@Parameters
	public void shouldReturnNonNullHeaders(RestRequest request) throws Exception {
		assertThat(request.getHeaders(), is(notNullValue()));
		// todo assertThat(request.getHeaders(), colleitearra(notNullValue()));
	}

	public Object[][] parametersForShouldReturnNonNullHeaders() {
		return new Object[][] {
				new Object[] { RestRequest.create(GET, "uri").build() },
				new Object[] { RestRequest.create(GET, "uri").headers((HttpHeader[]) null).build() },
				new Object[] { RestRequest.create(GET, "uri").headers((HttpHeader) null).build() },
				new Object[] { RestRequest.create(GET, "uri").headers(new HttpHeader[]{null}).build() },
		};
	}
}