package enums;

public enum ErrorCode {
    INVALID_URL;

    public int getValue() {
        return ordinal() + 1;
    }
}
