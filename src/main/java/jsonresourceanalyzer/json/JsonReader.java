package jsonresourceanalyzer.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import jsonresourceanalyzer.concurrency.WorkDispatcher;
import jsonresourceanalyzer.ArgParser;
import jsonresourceanalyzer.ErrorCode;
import jsonresourceanalyzer.constants.ErrorMessages;
import java.io.IOException;

public class JsonReader {

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

  private void close() {
    try {
      jsonParser.close();
    } catch (IOException ex) {
      close();
      System.err.println(
          String.format(ErrorMessages.UNKNOWN_ERROR_WHILE_CLOSING_STREAM, ex.getMessage()));
      System.exit(ErrorCode.UNKNOWN_ERROR_WHILE_CLOSING_STREAM.getValue());
    }
  }

  private JsonToken nextToken() {
    try {
      return jsonParser.nextToken();
    } catch (IOException ex) {
      close();
      System.err.println(String.format(ErrorMessages.UNKNOWN_ERROR_WHILE_PARSING, ex.getMessage()));
      System.exit(ErrorCode.UNKNOWN_ERROR_WHILE_PARSING.getValue());
      return null;
    }
  }

  private String nextFieldName() {
    try {
      return jsonParser.nextFieldName();
    } catch (IOException ex) {
      close();
      System.err.println(String.format(ErrorMessages.UNKNOWN_ERROR_WHILE_PARSING, ex.getMessage()));
      System.exit(ErrorCode.UNKNOWN_ERROR_WHILE_PARSING.getValue());
      return null;
    }
  }

  private <T> T nextValueByType(Class<T> type) {
    try {
      if (String.class.equals(type)) {
        return (T) jsonParser.nextTextValue();
      }

      Integer integer = jsonParser.nextIntValue(-1);
      if (integer == -1) {
        close();
        System.err.println(ErrorMessages.INVALID_JSON);
        System.exit(ErrorCode.INVALID_JSON.getValue());
      }
      return (T) integer;
    } catch (IOException ex) {
      close();
      System.err.println(String.format(ErrorMessages.UNKNOWN_ERROR_WHILE_PARSING, ex.getMessage()));
      System.exit(ErrorCode.UNKNOWN_ERROR_WHILE_PARSING.getValue());
      return null;
    }
  }

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

  private InputJsonObject readObject() {
    // make sure that there is nothing unexpected before attempting to read the next json object
    if (jsonParser.currentToken() != JsonToken.START_OBJECT) {
      close();
      System.err.println(ErrorMessages.INVALID_JSON);
      System.exit(ErrorCode.INVALID_JSON.getValue());
    }

    // grab the next 3 fields and add them to the InputJsonObject
    InputJsonObject inputJsonObject = new InputJsonObject();
    for (int i = 0; i < 3; i++) {

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

  private void validateProperty(String propertyName) {
    if (!InputJsonObject.isValidProperty(propertyName)) {
      close();
      System.err.println(ErrorMessages.INVALID_JSON);
      System.exit(ErrorCode.INVALID_JSON.getValue());
    }
  }
}
