package br.com.anteater;

public interface Closure {

	void call();

	/**
	 * Ver o se é necessário
	 * 
	 * @param methodName
	 */
	@Deprecated
	Object call(String methodName);

	/**
	 * Ver o se é necessário
	 * 
	 * @param methodName
	 */
	@Deprecated
	void setDelegate(BuilderSupport builderSupport);

}
