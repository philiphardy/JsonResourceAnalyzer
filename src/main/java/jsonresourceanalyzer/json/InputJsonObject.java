package jsonresourceanalyzer.json;

import jsonresourceanalyzer.enums.ErrorCode;
import jsonresourceanalyzer.constants.ErrorMessages;
import java.util.HashMap;

public class InputJsonObject {

  protected static final String PATH = "path";
  protected static final String SIZE = "size";
  protected static final String URL = "url";

  protected static HashMap<String, Class<?>> validPropertyNames;

  static {
    validPropertyNames = new HashMap<>();
    validPropertyNames.put(PATH, String.class);
    validPropertyNames.put(SIZE, Integer.class);
    validPropertyNames.put(URL, String.class);
  }


  public static Class<?> getPropertyType(String propertyName) {
    return validPropertyNames.get(propertyName);
  }

  public static boolean isValidProperty(String propertyName) {
    return validPropertyNames.containsKey(propertyName);
  }

  private String path;
  private Integer size;
  private String url;

  public String getPath() {
    return path;
  }

  public Integer getSize() {
    return size;
  }

  public String getUrl() {
    return url;
  }

  public void setProperty(String name, Object value) {
    switch (name) {
      case PATH:
        path = (String) value;
        break;
      case SIZE:
        size = (Integer) value;
        break;
      case URL:
        url = (String) value;
        break;
      default:
        System.err.println(String.format(ErrorMessages.UNKNOWN_PROPERTY, name));
        System.exit(ErrorCode.UNKNOWN_PROPERTY.getValue());
    }
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  /**
   * Validates that this has no null fields.
   */
  public void validate() {
    if (path == null || size == null || url == null) {
      System.err.println(ErrorMessages.INVALID_JSON_INPUT_OBJECT);
      System.exit(ErrorCode.INVALID_JSON_INPUT_OBJECT.getValue());
    }
  }
}
