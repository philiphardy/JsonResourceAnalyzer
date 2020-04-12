package jsonresourceanalyzer.json;

import jsonresourceanalyzer.ErrorCode;
import jsonresourceanalyzer.constants.ErrorMessages;
import java.util.HashMap;

public class InputJsonObject {
  private static final String PATH = "path";
  private static final String SIZE = "size";
  private static final String URL = "url";

  private static HashMap<String, Class<?>> validPropertyNames;

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
}
