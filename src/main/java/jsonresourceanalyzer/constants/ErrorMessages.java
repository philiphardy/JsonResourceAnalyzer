package jsonresourceanalyzer.constants;

public class ErrorMessages {
  private ErrorMessages() throws InstantiationException {
    throw new InstantiationException("This class should not be instantiated.");
  }

  public static final String INVALID_FILE = "The given file path either does not exist, is not a file, or cannot be read.";
  public static final String INVALID_INPUT_STREAM = "An error occurred when opening the input stream. Caused by: \n%s";
  public static final String INVALID_JSON = "Invalid JSON was encountered while parsing the input stream.";
  public static final String INVALID_JSON_FORMAT_NOT_ARRAY = "The JSON provided must be an array of JSON objects.";
  public static final String INVALID_OUTPUT_FILE = "The given output file path cannot be written to.";
  public static final String INVALID_URL = "The given URL was invalid.";
  public static final String UNKNOWN_ERROR_WHILE_CLOSING_STREAM = "An unknown error occurred while closing the JSON stream. Caused by:\n%s";
  public static final String UNKNOWN_ERROR_WHILE_PARSING = "An unknown error occurred while parsing the JSON. Caused by:\n%s";
  public static final String UNKNOWN_PROPERTY = "Unknown property encountered: %s";
  public static final String WORK_DISPATCHER_THREAD_INTERRUPTED = "Fatal error: Worker dispatcher thread was interrupted.";
}
