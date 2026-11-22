package hu.prog2.fittrack.exception;

/**
 * Custom checked exception thrown when invalid workout or profile data is provided,
 * violating domain rules.
 */
public class InvalidWorkoutDataException extends Exception {
    public InvalidWorkoutDataException(String message) {
        super(message);
    }

    public InvalidWorkoutDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
