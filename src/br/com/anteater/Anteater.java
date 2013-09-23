package br.com.anteater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import br.com.anteater.builder.AntBuilder;
import br.com.anteater.builder.MissingMethodException;
import br.com.anteater.builder.nested.NestedContainer;
import br.com.anteater.builder.nested.NestedLambda;
import br.com.anteater.builder.nested.NestedParams;

public class Anteater {

	AntBuilder ant = new AntBuilder();

	public Object getProp(NativeArray args) throws InvalidArguments {
		if (args.size() != 1) {
			throw new InvalidArguments("Invalid number of parameters in call of the prop function.");
		}

		Object object = args.get(0);
		if (!(object instanceof String)) {
			throw new InvalidArguments("Invalid arguments in call of prop function.");
		}
		return ant.getProject().getProperties().get(object);
	}

	public Object exec(Context cx, Scriptable thisObj, Object[] functionParams, Function funObj) throws MissingMethodException, InvalidArguments {
		String methodName = (String) functionParams[0];
		NativeArray args = (NativeArray) functionParams[1];
		if ("prop".equals(methodName)) {
			return getProp(args);
		} else if ("executeTarget".equals(methodName)) {
			return executeTarget(args);
		}

		NestedContainer container = new NestedContainer();
		ArrayList<Object> arguments = new ArrayList<Object>();
		HashMap<String, Object> params = new HashMap<String, Object>();
		arguments.add(params);

		String paramText = null;
		NestedLambda method = null;

		if (args.size() > 4) {
			throw new InvalidArguments("Number of params don\'t supported.");
		}

		for (Object arg : args) {
			if (arg instanceof NativeObject) {
				getParams(thisObj, (Scriptable) arg, params, container);
			} else if (arg instanceof String) {
				if (paramText != null) {
					throw new InvalidArguments("Two params text don\'t supported.");
				}

				paramText = (String) arg;
				arguments.add(paramText);
			} else if (arg instanceof Function) {

				if (method != null) {
					throw new InvalidArguments("Two lambda functions don\'t supported.");
				}
				method = new NestedLambda(cx, thisObj, (Function) arg);
				container.addObject(method);// Adicionado no container, pois
											// terá mais NestedObject
			} else {
				throw new InvalidArguments("Type of param don't supported.");
			}
		}
		arguments.add(container);
		ant.invokeMethod(methodName, arguments.toArray());
		return false;
	}

	private Object executeTarget(NativeArray args) throws InvalidArguments {
		if (args.size() != 1) {
			throw new InvalidArguments("Invalid number of parameters in call of the executeTarget function.");
		}

		Object object = args.get(0);
		if (!(object instanceof String)) {
			throw new InvalidArguments("Invalid arguments in call of executeTarget function.");
		}
		ant.getProject().executeTarget((String) object);
		return null;
	}

	private void getScriptableParams(Scriptable thisObj, String myName, Scriptable object, NestedContainer parent) {
		Map<String, Object> params = new HashMap<String, Object>();
		NestedParams thisNested = new NestedParams(myName, params, ant);
		parent.addObject(thisNested);

		getParams(thisObj, object, params, thisNested);
	}

	private void getParams(Scriptable thisObj, Scriptable object, Map<String, Object> params, NestedContainer thisNested) {
		for (Object objName : object.getIds()) {
			String name = (String) objName;
			Object child = object.get(name, thisObj);
			if (child instanceof NativeObject) {
				getScriptableParams(thisObj, name, (Scriptable) child, thisNested);
				/*
				 * This permit the call of several times of same method
				 * (include,include ...)
				 */
			} else if (child instanceof NativeArray) {
				NativeArray array = (NativeArray) child;
				for (Object obj : array) {
					getScriptableParams(thisObj, name, (Scriptable) obj, thisNested);
				}
			} else {
				params.put(name, child);
			}
		}
	}

	public void endExecution() {

	}
}
