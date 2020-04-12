package jsonresourceanalyzer.constants;

public class ErrorMessages {

  private ErrorMessages() throws InstantiationException {
    throw new InstantiationException("This class should not be instantiated.");
  }

  public static final String ACTUAL_RESOURCE_SIZE_AND_REPORTED_SIZE_DIFFER_WARNING = "WARNING: The actual URL=(%s) resource size=(%d) and claimed resource size=(%d) differ";
  public static final String INVALID_FILE = "The given file path either does not exist, is not a file, or cannot be read.";
  public static final String INVALID_INPUT_STREAM = "An error occurred when opening the input stream. Caused by: \n%s";
  public static final String INVALID_JSON = "Invalid JSON was encountered while parsing the input stream.";
  public static final String INVALID_JSON_FORMAT_NOT_ARRAY = "The JSON provided must be an array of JSON objects.";
  public static final String INVALID_JSON_INPUT_OBJECT = "Invalid JSON input object encountered. Fields `path`, `size`, and `url` must all be non-null.";
  public static final String INVALID_JSON_PATH_VALUE_DUPLICATES = "Invalid JSON was encountered while parsing the input stream. Duplicate path values exist for path=(%s).";
  public static final String INVALID_OUTPUT_FILE = "The given output file path cannot be written to.";
  public static final String INVALID_URL = "The URL=(%s) is invalid.";
  public static final String UNKNOWN_ERROR_WHILE_CLOSING_INPUT_STREAM = "An unknown error occurred while closing the input JSON stream. Caused by:\n%s";
  public static final String UNKNOWN_ERROR_WHILE_CLOSING_OUTPUT_STREAM = "An unknown error occurred while closing the output JSON stream. Caused by:\n%s";
  public static final String UNKNOWN_ERROR_WHILE_OPENING_OUTPUT_FILE = "An unknown error occurred while opening the output file for writing. Caused by:\n%s";
  public static final String UNKNOWN_ERROR_WHILE_OPENING_URL_STREAM = "An unknown error occurred while opening the stream for URL=(%s). Caused by:\n%s";
  public static final String UNKNOWN_ERROR_WHILE_PARSING = "An unknown error occurred while parsing the JSON. Caused by:\n%s";
  public static final String UNKNOWN_ERROR_WHILE_READING_URL_STREAM = "An unknown error occurred while reading the URL=(%s) stream. Caused by:\n%s";
  public static final String UNKNOWN_ERROR_WHILE_WRITING = "An unknown error occurred while writing the JSON to the output file. Caused by:\n%s";
  public static final String UNKNOWN_PROPERTY = "Unknown property encountered: %s";
  public static final String WORK_DISPATCHER_THREAD_INTERRUPTED = "Fatal error: Worker dispatcher thread was interrupted.";
}
