package br.com.anteater.script;

import org.apache.tools.ant.BuildException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.WrappedException;

public class BuildScript extends AnteaterScript {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected int doExecute(Context cx, String command) {
		int exitCode = -1;
		long startTime = System.currentTimeMillis();
		try {
			System.out.println("Buildfile: " + command);
			processSource(cx, command);
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
}
