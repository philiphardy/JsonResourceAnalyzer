package jsonresourceanalyzer.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import jsonresourceanalyzer.ArgParser;
import jsonresourceanalyzer.ErrorCode;
import jsonresourceanalyzer.constants.ErrorMessages;

/**
 * This class is responsible for writing the JSON to the output file.
 */
public class JsonWriter {

  private interface WriterWrapper {

    void write() throws IOException;
  }

  private JsonGenerator jsonGenerator;
  private final SortedSet<String> pathSet;

  public JsonWriter(ArgParser argParser) {
    // since there is potential to have a large set, use a BST instead of a HashMap to optimize memory usage
    pathSet = Collections.synchronizedSortedSet(new TreeSet<>());

    JsonFactory jsonFactory = new JsonFactory();

    try {
      jsonGenerator = jsonFactory.createGenerator(argParser.getOutputFile(), JsonEncoding.UTF8);
    } catch (IOException ex) {
      System.err.println(
          String.format(ErrorMessages.UNKNOWN_ERROR_WHILE_OPENING_OUTPUT_FILE, ex.getMessage())
      );
      System.exit(ErrorCode.UNKNOWN_ERROR_WHILE_OPENING_OUTPUT_FILE.getValue());
    }
  }

  /**
   * Closes the output stream.
   */
  private void close() {
    try {
      jsonGenerator.close();
    } catch (IOException ex) {
      System.err.println(
          String.format(ErrorMessages.UNKNOWN_ERROR_WHILE_CLOSING_OUTPUT_STREAM, ex.getMessage())
      );
      System.exit(ErrorCode.UNKNOWN_ERROR_WHILE_CLOSING_OUTPUT_STREAM.getValue());
    }
  }

  /**
   * Ends the output file by writing "}" and closing the stream.
   */
  public void endFile() {
    wrapWrite(() -> jsonGenerator.writeEndObject());
    close();
  }

  /**
   * Begins the output file by writing "{" to the stream.
   */
  public void startFile() {
    wrapWrite(() -> jsonGenerator.writeStartObject());
  }

  /**
   * Wraps the write operation in a try/catch to handle the IOException and exit with appropriate
   * code.
   *
   * @param writerWrapper WriterWrapper with write operation embedded.
   */
  private void wrapWrite(WriterWrapper writerWrapper) {
    try {
      writerWrapper.write();
    } catch (IOException ex) {
      System.err.println(
          String.format(ErrorMessages.UNKNOWN_ERROR_WHILE_WRITING, ex.getMessage())
      );
      System.exit(ErrorCode.UNKNOWN_ERROR_WHILE_WRITING.getValue());
    }
  }

  /**
   * Writes the InputJsonObject to the output stream.
   *
   * @param inputJsonObject The InputJsonObject to write to output
   */
  public synchronized void writeObject(InputJsonObject inputJsonObject) {
    // check to see if the InputJsonObject path value has already been written to the output file
    // if so, report invalid JSON error and exit with appropriate code
    if (!pathSet.add(inputJsonObject.getPath())) {
      System.err.println(
          String.format(ErrorMessages.INVALID_JSON_PATH_VALUE_DUPLICATES, inputJsonObject.getPath())
      );
      System.exit(ErrorCode.INVALID_JSON_PATH_VALUE_DUPLICATES.getValue());
    }

    wrapWrite(() -> {
      jsonGenerator.writeFieldName(inputJsonObject.getPath());
      jsonGenerator.writeStartObject();

      // write size property
      jsonGenerator.writeFieldName(InputJsonObject.SIZE);
      jsonGenerator.writeNumber(inputJsonObject.getSize());

      // write url property
      jsonGenerator.writeFieldName(InputJsonObject.URL);
      jsonGenerator.writeString(inputJsonObject.getUrl());

      jsonGenerator.writeEndObject();
    });
  }
}
