package application;

/**
 * An exception used by the Environment class.
 * 
 */
public class EnvironmentException extends Exception {

	/**
	 * Accepts a specific message about the problem.
	 * @param message A string error message.
	 */
	public EnvironmentException (String message ) {
		super(message);
	}
	
} // end EnvironmentException class
