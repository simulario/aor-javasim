package aors.module.statistics.gui;

/**
 * KeyValuePair
 * 
 * special Item-Class usable in JComboBox, JList etc. necessary for
 * internationalization
 * 
 * @author Daniel Draeger
 * @since 01.12.2009
 */
public class KeyValuePair {

  private static final long serialVersionUID = 3834784689680730612L;
  private final Object key;
  private String value;

  /**
   * Create a new {@code KeyValuePair}.
   * 
   * @param key
   */
  public KeyValuePair(Object key) {
    this.key = key;
    setValue();
  }

  /**
   * Usage: set the value to the language specific key value
   * 
   */
  public void setValue() {
    this.value = ComponentTranslator.getResourceBundle().getString(
        key.toString());
  }

  /**
   * Usage: return the key
   * 
   * @return Object
   */
  public Object getKey() {
    return key;
  }

  @Override
  public String toString() {
    setValue();
    return value;
  }
}
