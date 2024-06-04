package co.uk.zing.ledger.exception;

public class MissingAccountNameException extends RuntimeException {

    public MissingAccountNameException() {
        super();
    }

    public MissingAccountNameException(String message) {
        super(message);
    }

    public MissingAccountNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingAccountNameException(Throwable cause) {
        super(cause);
    }
}
