package signal.exception;

/**
 * <p>This Exception is thrown when the method is either already connected or not connected.</p>
 * 
 * @author Nacharon
 * @version 1.0
 * @since 1.0
 */
public class InvalidConnectionException extends Exception
{
    /**
     * Create the exception.
     * 
     * @param error_message The error message that will be displayed.
     * @param error The exception that caused this exception.
     * 
     * @since 1.0
     */
    public InvalidConnectionException(String error_message, Throwable error)
    {
        super(error_message, error);
    }

    /**
     * Create the exception.
     * 
     * @param error_message The error message that will be displayed.
     * 
     * @since 1.0
     */
    public InvalidConnectionException(String error_message)
    {
        super(error_message);
    }
}