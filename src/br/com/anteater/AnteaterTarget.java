package br.com.anteater;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

/**
 * Anteater target is only "special" lambda functions
 * 
 * @author Renato.Lorenzi
 * 
 */
public class AnteaterTarget {
	private Scriptable thisObj;
	private String name;
	private Function lambda;
	private String[] depends;

	public AnteaterTarget(String projectName, Scriptable thisObj, NativeArray args) {
		super();
		this.thisObj = thisObj;
		// TODO Check params
		this.lambda = (Function) args.get(1);
		NativeObject params = (NativeObject) args.get(0);
		name = (String) params.get("name");
		String dependsStr = (String) params.get("depends");
		depends = dependsStr != null && !dependsStr.equals("") ? dependsStr.split(",") : new String[] {};

		name = projectName == null ? name : projectName + "." + name;
	}

	public Scriptable getThisObj() {
		return thisObj;
	}

	public void setThisObj(Scriptable thisObj) {
		this.thisObj = thisObj;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Function getLambda() {
		return lambda;
	}

	public void setLambda(Function lambda) {
		this.lambda = lambda;
	}

	public String[] getDepends() {
		return depends;
	}
}
