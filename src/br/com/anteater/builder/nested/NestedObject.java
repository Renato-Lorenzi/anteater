package br.com.anteater.builder.nested;

import br.com.anteater.builder.BuilderSupport;
import br.com.anteater.builder.MissingMethodException;

/**
 * 
 * Represent an abstract object that can be nested.
 * 
 * @author renatol
 * 
 */
public interface NestedObject {

	void call() throws MissingMethodException;

	/**
	 * needless
	 * 
	 * @param methodName
	 */
	@Deprecated
	Object call(String methodName);

	/**
	 * needless
	 * 
	 * @param methodName
	 */
	@Deprecated
	void setDelegate(BuilderSupport builderSupport);

}
