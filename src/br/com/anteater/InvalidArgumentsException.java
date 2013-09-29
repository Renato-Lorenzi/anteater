package br.com.anteater;

public class InvalidArgumentsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidArgumentsException(String msg) {
		super(msg);
	}

	public InvalidArgumentsException(String msg, Object... args) {
		super(String.format(msg, args));
	}

}
