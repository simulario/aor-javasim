package aors.module.visopengl3d.lang;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;

import aors.util.jar.JarUtil;

/**
 * The language loader and message manager class
 * 
 * @author Mircea Diaconescu
 * @since 17 July 2010
 * @version 1.0
 */
public class LanguageManager {
  // default used language
  private static String langDefault = "en";

  // default country for the chosen language
  private static String countryDefault = "US";

  // path to language files
  private static final String languageFilesLocation = JarUtil.TMP_DIR
      + File.separator + "visOpenGL";

  // the base name of the files that contains translated messages
  private static String messageFileName = "translation";

  // the Locale object
  private static Locale currentLocale = new Locale(langDefault, countryDefault);

  // the message bundler (that help extracting translated messages)
  private static ResourceBundle messageBundler = null;

  /**
   * Get the message for the current language
   * 
   * @param messageID
   *          the ID of the message to load
   * @return the message translated in the current language
   */
  public static String getMessage(String messageID) {
    // create first time use language - if not set before, this is a backup
    if (messageBundler == null) {
      changeLanguage(langDefault, countryDefault);
    }

    return messageBundler.getString(messageID);
  }

  /**
   * Update the current used locales. This will change the language for
   * extracted messages.
   * 
   * @param langCode
   *          the language code (e.g. en, de,...)
   * @param country
   *          the country of the language (e.g. US, DE, ...)
   */
  public static void changeLanguage(String langCode, String country) {
    // null language, then use default
    if (langCode == null) {
      langCode = langDefault;
    }

    // update locale object
    if (country == null || country.trim().length() < 1) {
      currentLocale = new Locale(langCode);
    } else {
      currentLocale = new Locale(langCode, country);
    }

    File file = new File(languageFilesLocation);
    try {
      URL[] urls = new URL[1];
      urls[0] = file.toURI().toURL();
      ClassLoader loader = new URLClassLoader(urls);

      // update message bundler based on new language
      messageBundler = ResourceBundle.getBundle(messageFileName, currentLocale,
          loader);
    } catch (MalformedURLException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Get the current used language code
   * 
   * @return ISO 639 code for language
   */
  public static String getCurrentLanguageCode() {
    return currentLocale.getLanguage();
  }
}
