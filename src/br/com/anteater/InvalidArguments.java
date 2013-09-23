package br.com.anteater;

public class InvalidArguments extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidArguments(String msg) {
		super("Invalid arguments. " + msg);
	}

}
