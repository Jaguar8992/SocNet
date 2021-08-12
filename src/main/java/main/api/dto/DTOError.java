package main.api.dto;

public enum DTOError {

    INVALID_REQUEST("invalid_request"),
    UNAUTHORIZED("unauthorized"),
    BAD_REQUEST("Bad Request");

    private final String errorMessage;

    DTOError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String get() {
        return errorMessage;
    }
}
