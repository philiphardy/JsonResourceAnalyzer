package jsonresourceanalyzer.json;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import jsonresourceanalyzer.ErrorCode;
import jsonresourceanalyzer.constants.ErrorMessages;

/**
 * This class is responsible for validating a InputJsonObject.
 */
public class InputJsonObjectValidator {
  public static void validateJsonObject(InputJsonObject inputJsonObject) {
    new InputJsonObjectValidator(inputJsonObject).validate();
  }

  private static final int MAX_ATTEMPTS = 5;
  private static final int EOF = -1;
  private static final int READ_MAX_BYTES = 10000;

  private final byte[] buffer;
  private final InputJsonObject inputJsonObject;

  private InputJsonObjectValidator(InputJsonObject inputJsonObject) {
    this.buffer = new byte[READ_MAX_BYTES];
    this.inputJsonObject = inputJsonObject;
  }

  /**
   * Counts the size of the resource pointed to by the URL in bytes.
   * @return The integer size in bytes
   */
  private int countUrlResourceSizeInBytes() {
    InputStream urlInputStream = openUrlInputStream();
    int urlResourceSize = 0;

    // read bytes from the url stream until the end of the file is reached
    int bytesRead = 0;
    while (bytesRead != EOF) {
      try {
        bytesRead = urlInputStream.read(buffer);
        if (bytesRead != EOF) {
          urlResourceSize += bytesRead;
        }
      } catch (IOException ex) {
        System.err.println(
            String.format(
                ErrorMessages.UNKNOWN_ERROR_WHILE_READING_URL_STREAM,
                inputJsonObject.getUrl(),
                ex.getMessage()
            )
        );
        System.exit(ErrorCode.UNKNOWN_ERROR_WHILE_READING_URL_STREAM.getValue());
      }
    }

    return urlResourceSize;
  }

  /**
   * Opens an input stream from the URL.
   * @return The URL's input stream
   */
  private InputStream openUrlInputStream() {
    URL url;
    try {
      url = new URL(inputJsonObject.getUrl());
    } catch (MalformedURLException ex) {
      System.err.println(String.format(ErrorMessages.INVALID_URL, inputJsonObject.getUrl()));
      System.exit(ErrorCode.INVALID_URL.getValue());
      return null;
    }

    // attempt to open the stream 5 times
    IOException ioException = null;
    for (int i = 0; i < MAX_ATTEMPTS; i++) {
      try {
        return url.openStream();
      } catch (IOException ex) {
        ioException = ex;
      }
    }

    // a connection to the URL could not be established. report error and exit
    System.err.println(
        String.format(
            ErrorMessages.UNKNOWN_ERROR_WHILE_OPENING_URL_STREAM,
            inputJsonObject.getUrl(),
            ioException.getMessage()
        )
    );
    System.exit(ErrorCode.UNKNOWN_ERROR_WHILE_OPENING_URL_STREAM.getValue());
    return null;
  }

  /**
   * Validates that the InputJsonObject has a valid URL and corrects the size if it differs.
   */
  public void validate() {
    inputJsonObject.validate();

    // report warning and correct size if url resource size differs from reported size in the InputJsonObject
    int urlResourceSize = countUrlResourceSizeInBytes();
    if (urlResourceSize != inputJsonObject.getSize()) {
      System.err.println(
          String.format(
              ErrorMessages.ACTUAL_RESOURCE_SIZE_AND_REPORTED_SIZE_DIFFER_WARNING,
              inputJsonObject.getUrl(),
              urlResourceSize,
              inputJsonObject.getSize()
          )
      );
      inputJsonObject.setSize(urlResourceSize);
    }
  }
}
