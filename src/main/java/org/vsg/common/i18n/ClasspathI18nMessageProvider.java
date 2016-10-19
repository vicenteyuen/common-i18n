/**
 * 
 */
package org.vsg.common.i18n;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vison ruan
 *
 */
public class ClasspathI18nMessageProvider extends ReLoaderI18nMessageSource {
	
	
	private static Logger logger = LoggerFactory.getLogger( ClasspathI18nMessageProvider.class );

	
	/**
	 * copy all property handle 
	 * @param desc
	 * @param org
	 */
	private void copyProperties(Properties desc , Properties org) {
		for (Enumeration propertyNames = org.propertyNames(); propertyNames.hasMoreElements(); ) {
			Object key = propertyNames.nextElement();
		    desc.put(key, org.get(key));
		}
	}

	@Override
	PropertiesHolder refreshResourceProperties(String filename,
			PropertiesHolder propHolder) {
		// TODO Auto-generated method stub
		long refreshTimestamp = (this.cacheMillis < 0) ? -1 : System.currentTimeMillis();

		// --- find resource ---
		ClassLoader clsLoader = this.getClass().getClassLoader();
		
		try {
			
			long fileTimestamp = -1;

			Properties integProps =  new Properties();
			
			String file = filename + PROPERTIES_SUFFIX;
			// ---- read the i18n file in jar ---			
			URL url = clsLoader.getResource(file);
			
			// --- check file exist ---
			if (null == url) {
				return propHolder;
			}

			
			Properties props = loadProperties(url, file);

			copyProperties(integProps , props);

			
			// --- end loop ---
			propHolder = new PropertiesHolder(integProps, fileTimestamp);				

			
			// --- put the all properties ---
			this.getCachedProperties().put(file, propHolder);				
			
			
		} catch (Exception ex) {

			if (logger.isWarnEnabled()) {
				logger.warn("Could not parse properties file [" +  "]", ex);
			}
			
		}
			
		return propHolder;
	}
	
	
	
}
