package jsonresourceanalyzer;

public enum ErrorCode {
    INVALID_ARGUMENTS,
    INVALID_FILE,
    INVALID_INPUT_STREAM,
    INVALID_JSON,
    INVALID_JSON_FORMAT_NOT_ARRAY,
    INVALID_OUTPUT_FILE,
    INVALID_URL,
    UNKNOWN_ERROR_WHILE_PARSING,
    UNKNOWN_ERROR_WHILE_CLOSING_STREAM,
    UNKNOWN_PROPERTY,
    WORK_DISPATCHER_THREAD_INTERRUPTED;

    public int getValue() {
        return ordinal() + 1;
    }
}