package jsonresourceanalyzer.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import jsonresourceanalyzer.ArgParser;
import jsonresourceanalyzer.ErrorCode;
import jsonresourceanalyzer.concurrency.WorkDispatcher;
import jsonresourceanalyzer.constants.ErrorMessages;

/**
 * This class is responsible for reading the JSON from either the file or url provided.
 */
public class JsonReader {

  private interface ReadWrapper {

    Object read() throws IOException;
  }

  private JsonParser jsonParser;
  private WorkDispatcher workDispatcher;

  public JsonReader(ArgParser argParser) {
    JsonFactory jsonFactory = new JsonFactory();
    try {
      if (argParser.getFile() != null) {
        jsonParser = jsonFactory.createParser(argParser.getFile());
      } else {
        jsonParser = jsonFactory.createParser(argParser.getUrl());
      }

    } catch (JsonParseException parseException) {
      close();
      System.err.println(ErrorMessages.INVALID_JSON);
      System.exit(ErrorCode.INVALID_JSON.getValue());

    } catch (IOException ioException) {
      System.err.println(
          String.format(ErrorMessages.INVALID_INPUT_STREAM, ioException.getMessage())
      );
      System.exit(ErrorCode.INVALID_INPUT_STREAM.getValue());
    }

    workDispatcher = new WorkDispatcher();
  }

  /**
   * Closes the input stream.
   */
  private void close() {
    try {
      jsonParser.close();
    } catch (IOException ex) {
      System.err.println(
          String.format(ErrorMessages.UNKNOWN_ERROR_WHILE_CLOSING_INPUT_STREAM, ex.getMessage())
      );
      System.exit(ErrorCode.UNKNOWN_ERROR_WHILE_CLOSING_INPUT_STREAM.getValue());
    }
  }

  /**
   * Reads the next token from the input stream.
   * @return The next token.
   */
  private JsonToken nextToken() {
    return wrapRead(() -> jsonParser.nextToken(), JsonToken.class);
  }

  /**
   * Reads the next field name from the input stream.
   * @return The next field name.
   */
  private String nextFieldName() {
    return wrapRead(() -> jsonParser.nextFieldName(), String.class);
  }

  /**
   * Reads the next value in the input stream as the type given.
   *
   * @param type String.class or Integer.class
   * @return The next value
   */
  private Object nextValueByType(Class<?> type) {
    return wrapRead(() -> {
      if (String.class.equals(type)) {
        return jsonParser.nextTextValue();
      }

      int integer = jsonParser.nextIntValue(-1);
      if (integer == -1) {
        close();
        System.err.println(ErrorMessages.INVALID_JSON);
        System.exit(ErrorCode.INVALID_JSON.getValue());
      }
      return integer;
    });
  }

  /**
   * Reads the entire input stream.
   */
  public void readFile() {
    if (nextToken() != JsonToken.START_ARRAY) {
      close();
      System.err.println(ErrorMessages.INVALID_JSON_FORMAT_NOT_ARRAY);
      System.exit(ErrorCode.INVALID_JSON_FORMAT_NOT_ARRAY.getValue());
    }

    // advance the stream to the first json object in the array, or the end of the array (if it was an empty array)
    if (nextToken() == JsonToken.END_ARRAY) {
      close();
      return;
    }

    // while the end of the stream hasn't been reached
    while (jsonParser.currentToken() != null) {
      InputJsonObject jsonObject = readObject();

      // check if the end of the array has been reached
      // if it was ensure that the next token is the end of the file
      if (nextToken() == JsonToken.END_ARRAY && nextToken() != null) {
        close();
        System.err.println(ErrorMessages.INVALID_JSON);
        System.exit(ErrorCode.INVALID_JSON.getValue());
      }
    }

    close();
  }

  /**
   * Reads the next json object from the input stream as a InputJsonObject.
   *
   * @return The InputJsonObject
   */
  private InputJsonObject readObject() {
    // make sure that there is nothing unexpected before attempting to read the next json object
    if (jsonParser.currentToken() != JsonToken.START_OBJECT) {
      close();
      System.err.println(ErrorMessages.INVALID_JSON);
      System.exit(ErrorCode.INVALID_JSON.getValue());
    }

    // grab the next 3 fields and add them to the InputJsonObject
    InputJsonObject inputJsonObject = new InputJsonObject();
    for (int i = 0; i < InputJsonObject.validPropertyNames.size(); i++) {

      String fieldName = nextFieldName();
      validateProperty(fieldName);

      Class<?> propertyType = InputJsonObject.getPropertyType(fieldName);
      Object fieldValue = nextValueByType(propertyType);

      inputJsonObject.setProperty(fieldName, fieldValue);
    }

    if (nextToken() != JsonToken.END_OBJECT) {
      close();
      System.err.println(ErrorMessages.INVALID_JSON);
      System.exit(ErrorCode.INVALID_JSON.getValue());
    }

    return inputJsonObject;
  }

  /**
   * Attempts the wrapped read operation on the JSON input stream. If an IOException is thrown, it
   * is logged and the system exits with the appropriate code.
   *
   * @param readWrapper Wrapper read operation
   * @return The value returned by the readWrapper
   */
  private Object wrapRead(ReadWrapper readWrapper) {
    return wrapRead(readWrapper, Object.class);
  }

  /**
   * Attempts the wrapped read operation on the JSON input stream. If an IOException is thrown, it
   * is logged and the system exits with the appropriate code.
   *
   * @param readWrapper Wrapper read operation
   * @param returnType  Return type of the readWrapper
   * @return The value returned by the readWrapper
   */
  private <T> T wrapRead(ReadWrapper readWrapper, Class<T> returnType) {
    try {
      return (T) readWrapper.read();
    } catch (IOException ex) {
      close();
      System.err.println(String.format(ErrorMessages.UNKNOWN_ERROR_WHILE_PARSING, ex.getMessage()));
      System.exit(ErrorCode.UNKNOWN_ERROR_WHILE_PARSING.getValue());
      return null;
    }
  }

  /**
   * Validates the property name is a valid InputJsonObject property.
   *
   * @param propertyName Name of property from input stream
   */
  private void validateProperty(String propertyName) {
    if (!InputJsonObject.isValidProperty(propertyName)) {
      close();
      System.err.println(ErrorMessages.INVALID_JSON);
      System.exit(ErrorCode.INVALID_JSON.getValue());
    }
  }
}
