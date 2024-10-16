package by.polikarpov.exception;

/**
 * Custom exception class for Data Access Objects (DAO) operations.
 * This exception is a runtime exception, which means it does not need to be declared in a method's or constructor's
 * {@code throws} clause.
 */
public class DaoException extends RuntimeException {

    /**
     * Constructs a new DaoException with the specified cause.
     *
     * @param cause the cause of the exception (which is saved for later retrieval by the {@link Throwable#getCause()} method)
     */
    public DaoException(Throwable cause) {
        super(cause);
    }
}
