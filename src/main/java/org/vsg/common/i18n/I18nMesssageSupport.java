package org.vsg.common.i18n;

import java.util.Locale;
import java.util.Properties;

/**
 * get i18n support
 * @author vison ruan
 *
 */
public interface I18nMesssageSupport {

	String getMessage(String code , Object[] args , String def);
	
	String getMessage(String code , Object[] args );
	
	Properties getAllByLocale(Locale  currentLocal);	
	
}
