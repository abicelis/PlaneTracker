package ve.com.abicelis.planetracker.data.model.exception;

/**
 * Created by abicelis on 1/9/2017.
 */

public class ErrorParsingDataException extends Exception {

    private static final String DEFAULT_MESSAGE = "Data could not be parsed";

    public ErrorParsingDataException() {
        super(DEFAULT_MESSAGE);
    }
    public ErrorParsingDataException(String message) {
        super(message);
    }
    public ErrorParsingDataException(String message, Throwable cause) {
        super(message, cause);
    }

}
