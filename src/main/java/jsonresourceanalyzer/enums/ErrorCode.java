package jsonresourceanalyzer.enums;

public enum ErrorCode {
    INVALID_ARGUMENTS,
    INVALID_FILE,
    INVALID_INPUT_STREAM,
    INVALID_JSON,
    INVALID_JSON_FORMAT_NOT_ARRAY,
    INVALID_JSON_INPUT_OBJECT,
    INVALID_JSON_PATH_VALUE_DUPLICATES,
    INVALID_OUTPUT_FILE,
    INVALID_URL,
    UNKNOWN_ERROR_WHILE_CLOSING_INPUT_STREAM,
    UNKNOWN_ERROR_WHILE_CLOSING_OUTPUT_STREAM,
    UNKNOWN_ERROR_WHILE_CLOSING_URL_STREAM,
    UNKNOWN_ERROR_WHILE_OPENING_OUTPUT_FILE,
    UNKNOWN_ERROR_WHILE_OPENING_URL_STREAM,
    UNKNOWN_ERROR_WHILE_PARSING,
    UNKNOWN_ERROR_WHILE_READING_URL_STREAM,
    UNKNOWN_ERROR_WHILE_WRITING,
    UNKNOWN_PROPERTY,
    WORK_DISPATCHER_THREAD_INTERRUPTED;

    public int getValue() {
        return ordinal() + 1;
    }
}
