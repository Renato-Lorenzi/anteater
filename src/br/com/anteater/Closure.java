package br.com.anteater;

public interface Closure {

	void call();

	/**
	 * Ver o se � necess�rio
	 * 
	 * @param methodName
	 */
	@Deprecated
	Object call(String methodName);

	/**
	 * Ver o se � necess�rio
	 * 
	 * @param methodName
	 */
	@Deprecated
	void setDelegate(BuilderSupport builderSupport);

}
