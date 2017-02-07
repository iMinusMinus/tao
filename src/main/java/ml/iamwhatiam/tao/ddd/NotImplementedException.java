package ml.iamwhatiam.tao.ddd;

public class NotImplementedException extends RuntimeException {

	private static final long serialVersionUID = -2976420399084476407L;
	
	/**
     * Constructs a new exception with <code>null</code> as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public NotImplementedException() {
	super();
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for 
     *          later retrieval by the {@link #getMessage()} method.
     */
    public NotImplementedException(String message) {
	super(message);
    }

}
