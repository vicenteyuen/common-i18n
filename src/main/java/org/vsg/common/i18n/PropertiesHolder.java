/**
 * 
 */
package org.vsg.common.i18n;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * @author vison ruan
 *
 */
class PropertiesHolder {
	private Properties properties;

	private long fileTimestamp = -1;

	private long refreshTimestamp = -1;

	/** Cache to hold already generated MessageFormats per message code */
	private final Map<String, Map<Locale, MessageFormat>> cachedMessageFormats =
			new HashMap<String, Map<Locale, MessageFormat>>();

	public PropertiesHolder(Properties properties, long fileTimestamp) {
		this.properties = properties;
		this.fileTimestamp = fileTimestamp;
	}

	public PropertiesHolder() {
	}

	public Properties getProperties() {
		return properties;
	}

	public long getFileTimestamp() {
		return fileTimestamp;
	}

	public void setRefreshTimestamp(long refreshTimestamp) {
		this.refreshTimestamp = refreshTimestamp;
	}

	public long getRefreshTimestamp() {
		return refreshTimestamp;
	}

	public String getProperty(String code) {
		if (this.properties == null) {
			return null;
		}
		return this.properties.getProperty(code);
	}

	public MessageFormat getMessageFormat(String code, Locale locale) {
		if (this.properties == null) {
			return null;
		}
		synchronized (this.cachedMessageFormats) {
			Map<Locale, MessageFormat> localeMap = this.cachedMessageFormats.get(code);
			if (localeMap != null) {
				MessageFormat result = localeMap.get(locale);
				if (result != null) {
					return result;
				}
			}
			String msg = this.properties.getProperty(code);
			if (msg != null) {
				if (localeMap == null) {
					localeMap = new HashMap<Locale, MessageFormat>();
					this.cachedMessageFormats.put(code, localeMap);
				}
				MessageFormat result = createMessageFormat(msg, locale);
				localeMap.put(locale, result);
				return result;
			}
			return null;
		}
	}
	
	protected MessageFormat createMessageFormat(String msg, Locale locale) {
		return new MessageFormat((msg != null ? msg : ""), locale);
	}	
}
