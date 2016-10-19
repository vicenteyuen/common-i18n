package org.vsg.common.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

public interface PropertiesPersister {
	/**
	 * Load properties from the given InputStream into the given
	 * Properties object.
	 * @param props the Properties object to load into
	 * @param is the InputStream to load from
	 * @throws IOException in case of I/O errors
	 * @see java.util.Properties#load
	 */
	void load(Properties props, InputStream is) throws IOException;

	/**
	 * Load properties from the given Reader into the given
	 * Properties object.
	 * @param props the Properties object to load into
	 * @param reader the Reader to load from
	 * @throws IOException in case of I/O errors
	 */
	void load(Properties props, Reader reader) throws IOException;


	/**
	 * Write the contents of the given Properties object to the
	 * given OutputStream.
	 * @param props the Properties object to store
	 * @param os the OutputStream to write to
	 * @param header the description of the property list
	 * @throws IOException in case of I/O errors
	 * @see java.util.Properties#store
	 */
	void store(Properties props, OutputStream os, String header) throws IOException;

	/**
	 * Write the contents of the given Properties object to the
	 * given Writer.
	 * @param props the Properties object to store
	 * @param writer the Writer to write to
	 * @param header the description of the property list
	 * @throws IOException in case of I/O errors
	 */
	void store(Properties props, Writer writer, String header) throws IOException;

}
