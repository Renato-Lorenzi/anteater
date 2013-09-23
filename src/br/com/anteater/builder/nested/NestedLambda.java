package br.com.anteater.builder.nested;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import br.com.anteater.builder.BuilderSupport;

/**
 * Represents a lambda function that some tasks may have.
 * 
 * Ex.: Sequential, Parallel and target.
 * 
 * @author renatol
 * 
 */
public class NestedLambda implements NestedObject {

	private Context cx;
	private Scriptable parentObj;
	private Function function;

	public NestedLambda(Context cx, Scriptable parentObj, Function function) {
		this.cx = cx;
		this.parentObj = parentObj;
		this.function = function;
	}

	@Override
	public void call() {
		function.call(cx, parentObj, parentObj, new Object[] {});
	}

	/**
	 * needless
	 * 
	 * @param methodName
	 */
	@Deprecated
	@Override
	public Object call(String methodName) {
		return null;
	}

	/**
	 * needless
	 * 
	 * @param methodName
	 */
	@Deprecated
	@Override
	public void setDelegate(BuilderSupport builderSupport) {

	}

}
