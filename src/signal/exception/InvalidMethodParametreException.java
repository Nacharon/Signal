package signal.exception;

/**
 * <p>This exception is thrown when a method parameter is incorrect.</p>
 * 
 * @author Nacharon
 * @version 1.1
 * @since 1.0
 */
public class InvalidMethodParametreException extends Exception
{
    /**
     * Constructs the exception.
     * 
     * @param error_message the error message that will be displayed
     * @param error the exception that caused this exception
     * 
     * @since 1.0
     */
    public InvalidMethodParametreException(String error_message, Throwable error)
    {
        super(error_message, error);
    }
    
    /**
     * Constructs the exception.
     * 
     * @param error_message the error message that will be displayed
     * 
     * @since 1.0
     */
    public InvalidMethodParametreException(String error_message)
    {
        super(error_message);
    }
}