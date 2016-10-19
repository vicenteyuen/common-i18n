/**
 * 
 */
package org.vsg.common.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author vison ruan
 *
 */
public abstract class ReLoaderI18nMessageSource extends AbstractMessageSource implements I18nMesssageSupport {
	
	static final String PROPERTIES_SUFFIX = ".properties";

	private static Logger logger = LoggerFactory.getLogger( ReLoaderI18nMessageSource.class );
	
	protected long cacheMillis = -1;		
	
	private String fileEncodings;
	
	private String defaultEncoding = "UTF-8";
	
	private final Map<String, PropertiesHolder> cachedProperties = new HashMap<String, PropertiesHolder>();
	
	private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();	
	
	private String[] basenames;
	
	private Locale locale;	

	public String[] getBasenames() {
		return basenames;
	}

	public void setBasenames(String[] basenames) {
		this.basenames = basenames;
	}
	
	
	protected Map<String, PropertiesHolder> getCachedProperties() {
		return cachedProperties;
	}
	
	protected Locale getLocale() {
		return locale;
	}

	protected void setLocale(Locale locale) {
		this.locale = locale;
	}

	/* (non-Javadoc)
	 * @see org.vsg.common.i18n.I18nMesssageSupport#getMessage(java.lang.String, java.lang.Object[], java.lang.String)
	 */
	@Override
	public String getMessage(String code, Object[] args, String def) {
		// TODO Auto-generated method stub
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.vsg.common.i18n.I18nMesssageSupport#getMessage(java.lang.String, java.lang.Object[])
	 */
	@Override
	public String getMessage(String code, Object[] args) {
		String msg = getMessageInternal(code, args, locale);
		if (msg != null) {
			return msg;
		}
		String fallback = getDefaultMessage(code);
		if (fallback != null) {
			return fallback;
		}
		throw new NoSuchMessageException(code, locale);		

	}
	
	/**
	 * Return a fallback default message for the given code, if any.
	 * <p>Default is to return the code itself if "useCodeAsDefaultMessage" is activated,
	 * or return no fallback else. In case of no fallback, the caller will usually
	 * receive a NoSuchMessageException from {@code getMessage}.
	 * @param code the message code that we couldn't resolve
	 * and that we didn't receive an explicit default message for
	 * @return the default message to use, or {@code null} if none
	 * @see #setUseCodeAsDefaultMessage
	 */
	protected String getDefaultMessage(String code) {
		if (isUseCodeAsDefaultMessage()) {
			return code;
		}
		return null;
	}	

	protected boolean isUseCodeAsDefaultMessage() {
		return this.useCodeAsDefaultMessage;
	}

	private boolean useCodeAsDefaultMessage = false;	
	
	
	/**
	 * Resolve the given code and arguments as message in the given Locale,
	 * returning {@code null} if not found. Does <i>not</i> fall back to
	 * the code as default message. Invoked by {@code getMessage} methods.
	 * @param code the code to lookup up, such as 'calculator.noRateSet'
	 * @param args array of arguments that will be filled in for params
	 * within the message
	 * @param locale the Locale in which to do the lookup
	 * @return the resolved message, or {@code null} if not found
	 * @see #getMessage(String, Object[], String, Locale)
	 * @see #getMessage(String, Object[], Locale)
	 * @see #getMessage(MessageSourceResolvable, Locale)
	 * @see #setUseCodeAsDefaultMessage
	 */
	protected String getMessageInternal(String code, Object[] args, Locale locale) {
		if (code == null) {
			return null;
		}
		if (locale == null) {
			locale = Locale.getDefault();
		}
		Object[] argsToUse = args;

		if (!isAlwaysUseMessageFormat() && (args == null || args.length == 0)) {
			// Optimized resolution: no arguments to apply,
			// therefore no MessageFormat needs to be involved.
			// Note that the default implementation still uses MessageFormat;
			// this can be overridden in specific subclasses.
			String message = resolveCodeWithoutArguments(code, locale);
			if (message != null) {
				return message;
			}
		}

		else {
			// Resolve arguments eagerly, for the case where the message
			// is defined in a parent MessageSource but resolvable arguments
			// are defined in the child MessageSource.
			argsToUse = resolveArguments(args, locale);

			MessageFormat messageFormat = resolveCode(code, locale);
			if (messageFormat != null) {
				synchronized (messageFormat) {
					return messageFormat.format(argsToUse);
				}
			}
		}

		// Check locale-independent common messages for the given message code.
		Properties commonMessages = getCommonMessages();
		if (commonMessages != null) {
			String commonMessage = commonMessages.getProperty(code);
			if (commonMessage != null) {
				return formatMessage(commonMessage, args, locale);
			}
		}

		// Not found -> check parent, if any.
		return getMessageFromParent(code, argsToUse, locale);
	}


	
	/* (non-Javadoc)
	 * @see org.vsg.common.i18n.I18nMesssageSupport#getAllByLocale(java.util.Locale)
	 */
	@Override
	public Properties getAllByLocale(Locale currentLocal) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Get a PropertiesHolder that contains the actually visible properties
	 * for a Locale, after merging all specified resource bundles.
	 * Either fetches the holder from the cache or freshly loads it.
	 * <p>Only used when caching resource bundle contents forever, i.e.
	 * with cacheSeconds < 0. Therefore, merged properties are always
	 * cached forever.
	 */
	protected PropertiesHolder getMergedProperties(Locale locale) {
		synchronized (this.cachedMergedProperties) {
			PropertiesHolder mergedHolder = this.cachedMergedProperties.get(locale);
			if (mergedHolder != null) {
				return mergedHolder;
			}
			Properties mergedProps = new Properties();
			mergedHolder = new PropertiesHolder(mergedProps, -1);
			for (int i = this.basenames.length - 1; i >= 0; i--) {
				List<String> filenames = calculateAllFilenames(this.basenames[i], locale);
				for (int j = filenames.size() - 1; j >= 0; j--) {
					String filename = filenames.get(j);
					
					
					PropertiesHolder propHolder = getProperties(filename);
					
					System.out.println(propHolder + " , " + filename);
					
					if (propHolder.getProperties() != null) {
						mergedProps.putAll(propHolder.getProperties());
					}
				}
			}
			this.cachedMergedProperties.put(locale, mergedHolder);
			return mergedHolder;
		}
	}

	
	/**
	 * Resolves the given message code as key in the retrieved bundle files,
	 * using a cached MessageFormat instance per message code.
	 */
	protected MessageFormat resolveCode(String code, Locale locale) {
		if (this.cacheMillis < 0) {
			PropertiesHolder propHolder = getMergedProperties(locale);
			MessageFormat result = propHolder.getMessageFormat(code, locale);
			if (result != null) {
				return result;
			}
		}
		else {
			for (String basename : this.basenames) {
				List<String> filenames = calculateAllFilenames(basename, locale);
				for (String filename : filenames) {
					PropertiesHolder propHolder = getProperties(filename);
					MessageFormat result = propHolder.getMessageFormat(code, locale);
					if (result != null) {
						return result;
					} else {
						propHolder = getResourceProperties(filename);
						System.out.println("out holder : " + propHolder);
					}
				}
			}
		}
		return null;
	}	
	
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		// --- read message first ---

		if (this.cacheMillis < 0) {
			PropertiesHolder propHolder = getMergedResourceProperties(locale);
			String result = propHolder.getProperty(code);
			if (result != null) {
				return result;
			}
		}
		else {

			for (String basename : this.basenames) {
				List<String> filenames = calculateAllFilenames(basename, locale);
				
				
				for (String filename : filenames) {
					
					PropertiesHolder propHolder = getProperties(filename);
					String result = propHolder.getProperty(code);
					System.out.println(filename);
					if (result != null) {
						return result;
					} else {
						// --- read another properties holder --- 
						propHolder = getResourceProperties(filename);
						System.out.println(propHolder);
						result = propHolder.getProperty(code);
						
						if ( result != null) {
							return result;
						}
						
					}
				}
			}
		
		
			
		}
		return null;
	}	
	
	private final Map<Locale, PropertiesHolder> cachedMergedProperties = new HashMap<Locale, PropertiesHolder>();	
	
	private final Map<String, Map<Locale, List<String>>> cachedFilenames =
			new HashMap<String, Map<Locale, List<String>>>();	

	/**
	 * Get a PropertiesHolder for the given filename, either from the
	 * cache or freshly loaded.
	 * @param filename the bundle filename (basename + Locale)
	 * @return the current PropertiesHolder for the bundle
	 */
	protected PropertiesHolder getProperties(String filename) {
		synchronized (this.cachedProperties) {
			PropertiesHolder propHolder = this.cachedProperties.get(filename);
			if (propHolder != null &&
					(propHolder.getRefreshTimestamp() < 0 ||
					 propHolder.getRefreshTimestamp() > System.currentTimeMillis() - this.cacheMillis)) {
				// up to date
				return propHolder;
			}
			return refreshProperties(filename, propHolder);
		}
	}	
	

	/**
	 * Refresh the PropertiesHolder for the given bundle filename.
	 * The holder can be {@code null} if not cached before, or a timed-out cache entry
	 * (potentially getting re-validated against the current last-modified timestamp).
	 * @param filename the bundle filename (basename + Locale)
	 * @param propHolder the current PropertiesHolder for the bundle
	 */
	protected PropertiesHolder refreshProperties(String filename, PropertiesHolder propHolder) {
		long refreshTimestamp = (this.cacheMillis < 0 ? -1 : System.currentTimeMillis());

		this.cachedProperties.put(filename, propHolder);
		return propHolder;
	}	
	
	/**
	 * Calculate all filenames for the given bundle basename and Locale.
	 * Will calculate filenames for the given Locale, the system Locale
	 * (if applicable), and the default file.
	 * @param basename the basename of the bundle
	 * @param locale the locale
	 * @return the List of filenames to check
	 * @see #setFallbackToSystemLocale
	 * @see #calculateFilenamesForLocale
	 */
	protected List<String> calculateAllFilenames(String basename, Locale locale) {
		synchronized (this.cachedFilenames) {
			Map<Locale, List<String>> localeMap = this.cachedFilenames.get(basename);
			if (localeMap != null) {
				List<String> filenames = localeMap.get(locale);
				if (filenames != null) {
					return filenames;
				}
			}
			List<String> filenames = new ArrayList<String>(7);
			filenames.addAll(calculateFilenamesForLocale(basename, locale));
			if (this.fallbackToSystemLocale && !locale.equals(Locale.getDefault())) {
				List<String> fallbackFilenames = calculateFilenamesForLocale(basename, Locale.getDefault());
				for (String fallbackFilename : fallbackFilenames) {
					if (!filenames.contains(fallbackFilename)) {
						// Entry for fallback locale that isn't already in filenames list.
						filenames.add(fallbackFilename);
					}
				}
			}
			filenames.add(basename);
			if (localeMap != null) {
				localeMap.put(locale, filenames);
			}
			else {
				localeMap = new HashMap<Locale, List<String>>();
				localeMap.put(locale, filenames);
				this.cachedFilenames.put(basename, localeMap);
			}
			return filenames;
		}
	}	
	
	private  PropertiesHolder getResourceProperties(String filename) {

		synchronized (this.cachedProperties) {
			PropertiesHolder propHolder = this.cachedProperties.get(filename);
			if (propHolder != null &&
					(propHolder.getRefreshTimestamp() < 0 ||
					 propHolder.getRefreshTimestamp() > System.currentTimeMillis() - this.cacheMillis)) {
				// up to date
				return propHolder;
			}
			
			PropertiesHolder holder = refreshResourceProperties(filename, propHolder);
			
			return holder;
		}
	}
	
	/**
	 * Refresh the PropertiesHolder for the given bundle filename.
	 * The holder can be <code>null</code> if not cached before, or a timed-out cache entry
	 * (potentially getting re-validated against the current last-modified timestamp).
	 * @param filename the bundle filename (basename + Locale)
	 * @param propHolder the current PropertiesHolder for the bundle
	 */
	abstract PropertiesHolder refreshResourceProperties(String filename, PropertiesHolder propHolder);

	
	
	private PropertiesHolder getMergedResourceProperties(Locale locale) {
		synchronized (this.cachedMergedProperties) {
			PropertiesHolder mergedHolder = this.cachedMergedProperties.get(locale);
			if (mergedHolder != null) {
				return mergedHolder;
			}
			Properties mergedProps = new Properties();
			mergedHolder = new PropertiesHolder(mergedProps, -1);
			
			// --- update merge resource ---
			for (int i = this.basenames.length - 1; i >= 0; i--) {
				List<String> filenames = calculateAllFilenames(this.basenames[i], locale);
				for (String filename : filenames) {
					if (!filename.endsWith(locale.toString())) {
						continue;
					}
					
					PropertiesHolder propHolder = getProperties(filename);
					
					if (null == propHolder) {
						propHolder = getResourceProperties(filename);
					}
					if (propHolder != null && propHolder.getProperties() != null) {
						mergedProps.putAll(propHolder.getProperties());
					}					

				}
			}
			this.cachedMergedProperties.put(locale, mergedHolder);
			return mergedHolder;
		}
	}
	
	protected List<String> calculateFilenamesForLocale(String basename, Locale locale) {
		List<String> result = new ArrayList<String>(3);
		String language = locale.getLanguage();
		String country = locale.getCountry();
		String variant = locale.getVariant();
		StringBuilder temp = new StringBuilder(basename);

		temp.append('_');
		if (language.length() > 0) {
			temp.append(language);
			result.add(0, temp.toString());
		}

		temp.append('_');
		if (country.length() > 0) {
			temp.append(country);
			result.add(0, temp.toString());
		}

		if (variant.length() > 0 && (language.length() > 0 || country.length() > 0)) {
			temp.append('_').append(variant);
			result.add(0, temp.toString());
		}

		return result;
	}
	
	private Map<String, Map<Locale, List<String>>> cachedResourceNames = new LinkedHashMap<String, Map<Locale, List<String>>>();;
	
	private boolean fallbackToSystemLocale = true;
	
	
	public List<String> calculateAllFilenamesToResource(String basename, Locale locale) {

		synchronized (this.cachedResourceNames) {
			Map<Locale, List<String>> localeMap = this.cachedResourceNames.get(basename);
			if (localeMap != null) {
				List<String> filenames = localeMap.get(locale);
				if (filenames != null) {
					return filenames;
				}
			}
			List<String> filenames = new ArrayList<String>(7);
			filenames.addAll(calculateFilenamesForLocale(basename, locale));
			if (this.fallbackToSystemLocale && !locale.equals(Locale.getDefault())) {
				List<String> fallbackFilenames = calculateFilenamesForLocale(basename, Locale.getDefault());
				for (String fallbackFilename : fallbackFilenames) {
					if (!filenames.contains(fallbackFilename)) {
						// Entry for fallback locale that isn't already in filenames list.
						filenames.add(fallbackFilename);
					}
				}
			}
			filenames.add(basename);
			if (localeMap != null) {
				localeMap.put(locale, filenames);
			}
			else {
				localeMap = new HashMap<Locale, List<String>>();
				localeMap.put(locale, filenames);
				this.cachedResourceNames.put(basename, localeMap);
			}
			return filenames;
		}
	}		
	
	private String findMatchfilename(String filename) {
		// --- get pattern match ---
		Set<String> resFileKeys = this.cachedProperties.keySet();

		String tmpfilename = "";
		if (filename.startsWith("classpath:")) {
			tmpfilename = filename.substring("classpath:".length());
		}
		
		for (String resfile : resFileKeys) {
			if (tmpfilename.equals(resfile) ) {
				return resfile;
			}
		}
		return null;
		
	}
	
	protected Properties loadProperties(URL resource, String filename) throws IOException {
		Properties props = new Properties();
		InputStream is = resource.openStream();
		String encoding = null;
		if (this.fileEncodings != null) {
			encoding = "UTF-8";
		}
		if (encoding == null) {
			encoding = this.defaultEncoding;
		}
		if (encoding != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Loading properties [" + resource + "] with encoding '" + encoding + "'");
			}
			this.propertiesPersister.load(props, new InputStreamReader(is, encoding));
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("Loading properties [" + resource + "]");
			}
			this.propertiesPersister.load(props, is);
		}
		return props;
		
	}

}
