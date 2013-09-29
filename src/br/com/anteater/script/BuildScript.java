package br.com.anteater.script;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;

import br.com.anteater.InvalidArgumentsException;

public class BuildScript extends AnteaterScript {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String buildFile;
	private String[] targets = new String[] {};

	@Override
	protected int doExecute(Context cx, String[] args) throws InvalidArgumentsException {
		processArgs(args);

		int exitCode = -1;
		long startTime = System.currentTimeMillis();
		try {
			System.out.println("Buildfile: " + buildFile);
			processSource(cx, buildFile);
			executeTargets(cx);
			System.out.println("BUILD SUCCESSFUL");
			exitCode = 0;
		} catch (WrappedException we) {
			// Some form of exception was caught by JavaScript and
			// propagated up.
			Throwable wrappedException = we.getWrappedException();
			if (wrappedException instanceof BuildException) {
				System.err.println("BUILD FAILED");
				printScriptStack(we.getScriptStack(), wrappedException.getMessage());
			} else {
				System.err.println("BUILD ERROR");
				printScriptStack(we.getScriptStack(), wrappedException.getMessage());
				wrappedException.printStackTrace();
			}
		} catch (RhinoException rhinoE) {
			// Some form of Rhino error
			System.err.println("js: " + rhinoE.getMessage());
			printScriptStack(rhinoE.getScriptStack(), null);
		} catch (Exception ex) {
			System.err.println("BUILD FAILED");
			System.err.println("Unexpected: " + ex.getMessage());
			ex.printStackTrace();
		}
		System.out.println("Total time: " + (System.currentTimeMillis() - startTime) + " milliseconds");
		return exitCode;
	}

	/**
	 * Execute args targets or default target if args targets dont exists
	 * 
	 * @param cx
	 */
	private void executeTargets(Context cx) {
		if (targets.length > 0) {
			for (String target : targets) {
				anteater.executeTarget(cx, target);
			}
		} else {
			String defaultTarget = anteater.getDefaultTarget();
			if (defaultTarget != null) {
				anteater.executeTarget(cx, defaultTarget);
			}
		}
	}

	private void processArgs(String[] args) throws InvalidArgumentsException {
		// ************************************ if setted file then use
		buildFile = args.length == 0 || args[0].endsWith(".js") ? args[0] : DEFAULT_SCRIPT;

		if (!new File(buildFile).exists()) {
			throw new InvalidArgumentsException("Build file " + buildFile + " not found in current directory.");
		}

		// ALL args because the build file name can be the default
		for (String arg : args) {
			if (arg.startsWith("-t:")) {
				targets = arg.split(":")[1].split(";");
			}
		}
	}

	@Override
	protected void defineFunctions() {
		defineFunctionProperties(new String[] { "executeAnt", "shellExec" }, BuildScript.class, ScriptableObject.DONTENUM);
	}

	public static Object executeAnt(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		return AnteaterScript.executeAnt(cx, thisObj, args, funObj);
	}

	public static Object shellExec(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		return AnteaterScript.shellExec(cx, thisObj, args, funObj);
	}

}
