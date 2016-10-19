package org.vsg.common.i18n;

public interface MessageSourceResolvable {


	/**
	 * Return the codes to be used to resolve this message, in the order that
	 * they should get tried. The last code will therefore be the default one.
	 * @return a String array of codes which are associated with this message
	 */
	String[] getCodes();

	/**
	 * Return the array of arguments to be used to resolve this message.
	 * @return an array of objects to be used as parameters to replace
	 * placeholders within the message text
	 * @see java.text.MessageFormat
	 */
	Object[] getArguments();

	/**
	 * Return the default message to be used to resolve this message.
	 * @return the default message, or {@code null} if no default
	 */
	String getDefaultMessage();	
}
