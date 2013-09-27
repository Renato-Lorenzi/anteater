package br.com.anteater.script;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.tools.ant.BuildException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptStackElement;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Define basic script functions and functionality. <br>
 * Here should not contain anteater functions.
 * 
 * @author renatol
 * 
 */
public abstract class ScriptBase extends ScriptableObject implements ScriptLoader {

	private static final long serialVersionUID = -5638074146250193112L;
	protected static ScriptBase self = null;

	@Override
	public String getClassName() {
		return "global";
	}

	/**
	 * Process arguments as would a normal Java program. Also create a new
	 * Context and associate it with the current thread. Then set up the
	 * execution environment and begin to execute scripts.
	 */
	public final int execute(String args[]) {
		int exitCode = -1;
		self = this;
		// Associate a new Context with this thread
		Context cx = Context.enter();
		try {
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed.
			cx.initStandardObjects(this);
			defineFunctions();
			args = processOptions(cx, args);

			// Set up "arguments" in the global scope to contain the command
			// line arguments after the name of the script to execute
			Object[] array;
			if (args.length == 0) {
				array = new Object[0];
			} else {
				int length = args.length - 1;
				array = new Object[length];
				System.arraycopy(args, 1, array, 0, length);
			}
			Scriptable argsObj = cx.newArray(this, array);
			defineProperty("arguments", argsObj, ScriptableObject.DONTENUM);
			processSource(cx, "script/ant.js");
			exitCode = doExecute(cx, args.length == 0 ? null : args[0]);

		} finally {
			self = null;
			Context.exit();
		}
		return exitCode;
	}

	protected abstract void defineFunctions();

	/**
	 * Print stack trace of js
	 * 
	 * @param scriptStack
	 * @param msg
	 */
	protected void printScriptStack(ScriptStackElement[] scriptStack, String msg) {
		// Skip line 0 (ant.js)
		if (scriptStack.length > 1) {
			System.err.println(scriptStack[1] + (msg != null ? " - " + msg : ""));
			for (int i = 2; i < scriptStack.length; i++) {
				System.err.println(scriptStack[i]);
			}
		}
	}

	/**
	 * Childs override to execute
	 * 
	 * @param cx
	 * @param command
	 * @return
	 */
	protected abstract int doExecute(Context cx, String command);

	/**
	 * Parse arguments.
	 */
	private String[] processOptions(Context cx, String args[]) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (!arg.startsWith("-")) {
				String[] result = new String[args.length - i];
				for (int j = i; j < args.length; j++)
					result[j - i] = args[j];
				return result;
			} else if (arg.equals("-version")) {
				// TODO review version
			}
		}
		return new String[0];
	}

	/**
	 * Evaluate JavaScript source.
	 * 
	 * @param cx
	 *            the current context
	 * @param filename
	 *            the name of the file to compile, or null for interactive mode.
	 */
	protected void processSource(Context cx, String filename) {
		Reader in = null;
		try {
			File file = new File(filename);
			in = file.exists() ? new FileReader(file) : new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filename));
		} catch (Exception ex) {
			Context.reportError("Couldn't open file \"" + filename + "\".");
			return;
		}

		try {
			// Here we evalute the entire contents of the file as
			// a script. Text is printed only if the print() function
			// is called.
			cx.evaluateReader(this, in, filename, 1, null);
		} catch (IOException ioe) {
			System.err.println(ioe.toString());
		} finally {
			try {
				in.close();
			} catch (IOException ioe) {
				System.err.println(ioe.toString());
			}
		}
	}

	/**
	 * Load and execute a set of JavaScript source files.
	 * 
	 * This method is defined as a JavaScript function.
	 * 
	 */
	@Override
	public void load(Context cx, String fileName) {
		processSource(cx, fileName);
	}

}
