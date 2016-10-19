package org.vsg.common.i18n;

import java.util.Locale;

import org.junit.Test;

public class ClasspathI18nMessageProviderTest {
	
	@Test
	public void testI18nMessageProvider() throws Exception {
		ClasspathI18nMessageProvider provider = new ClasspathI18nMessageProvider();
		provider.setBasenames(new String[]
			{"i18n/message","i18n/error"}
		);
		
		
		String firstName = provider.getMessage("label.firstname", null);
		System.out.println(firstName);
	}
	
	@Test
	public void testI18nMessageProvider_Chinese() throws Exception {
		ClasspathI18nMessageProvider provider = new ClasspathI18nMessageProvider();
		provider.setBasenames(new String[]
			{"i18n/message","i18n/error"}
		);
		
		
		String firstName = provider.getMessage("label.firstname", null, Locale.CHINA);
		System.out.println(firstName);
	}	

}
