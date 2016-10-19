package org.vsg.common.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

public class DefaultPropertiesPersister implements PropertiesPersister {

	@Override
	public void load(Properties props, InputStream is) throws IOException {
		props.load(is);
	}

	@Override
	public void load(Properties props, Reader reader) throws IOException {
		props.load(reader);
	}

	@Override
	public void store(Properties props, OutputStream os, String header) throws IOException {
		props.store(os, header);
	}

	@Override
	public void store(Properties props, Writer writer, String header) throws IOException {
		props.store(writer, header);
	}

	
}
