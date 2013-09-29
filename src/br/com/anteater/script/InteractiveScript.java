package br.com.anteater.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.tools.ant.BuildException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;

/**
 * Represent interactive script execution
 * 
 * @author renatol
 * 
 */
public class InteractiveScript extends AnteaterScript {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static boolean quitting;

	@Override
	protected int doExecute(Context cx, String[] args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String sourceName = "<stdin>";
		int lineno = 1;
		boolean hitEOF = false;
		do {
			int startline = lineno;
			System.err.flush();
			System.err.print("anteater> ");
			System.err.flush();
			try {
				String source = "";
				// Collect lines of source to compile.
				while (true) {
					String newline;
					newline = in.readLine();
					if (newline == null) {
						hitEOF = true;
						break;
					}
					source = source + newline + "\n";
					lineno++;
					// Continue collecting as long as more lines
					// are needed to complete the current
					// statement. stringIsCompilableUnit is also
					// true if the source statement will result in
					// any error other than one that might be
					// resolved by appending more source.
					if (cx.stringIsCompilableUnit(source))
						break;
				}
				Object result = cx.evaluateString(this, source, sourceName, startline, null);
				if (result != Context.getUndefinedValue()) {
					System.err.println("[return] " + Context.toString(result));
				}
			} catch (WrappedException we) {
				// Some form of exception was caught by JavaScript and
				// propagated up.
				Throwable wrappedException = we.getWrappedException();
				if (wrappedException instanceof BuildException) {
					System.err.println("[buid failure] " + wrappedException.getMessage());
				} else {
					wrappedException.printStackTrace();
				}
			} catch (RhinoException ee) {
				// Some form of JavaScript error.
				System.err.println("js: " + ee.getMessage());
			} catch (IOException ioe) {
				System.err.println(ioe.toString());
			} catch (Exception ex) {
				System.err.println("Unexpected: " + ex.getMessage());
				ex.printStackTrace();
			}
			if (quitting) {
				// The user executed the quit() function.
				break;
			}
		} while (!hitEOF);
		System.err.println();
		return 0;
	}

	@Override
	protected void defineFunctions() {
		defineFunctionProperties(new String[] { "quit", "help", "executeAnt", "shellExec" }, InteractiveScript.class, ScriptableObject.DONTENUM);
	}

	public static Object executeAnt(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		return AnteaterScript.executeAnt(cx, thisObj, args, funObj);
	}

	public static Object shellExec(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		return AnteaterScript.shellExec(cx, thisObj, args, funObj);
	}

	/**
	 * Quit the shell.
	 * 
	 * This only affects the interactive mode.
	 * 
	 * This method is defined as a JavaScript function.
	 */
	public static void quit(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		quitting = true;
	}

	/**
	 * Print a help message.
	 * 
	 * This method is defined as a JavaScript function.
	 */
	public static void help(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		p("");
		p("Command                Description");
		p("=======                ===========");
		p("help()                 Display usage and help messages. ");
		p("defineClass(className) Define an extension using the Java class");
		p("                       named with the string argument. ");
		p("                       Uses ScriptableObject.defineClass(). ");
		p("load(['foo.js', ...])  Load JavaScript source files named by ");
		p("                       string arguments. ");
		p("loadClass(className)   Load a class named by a string argument.");
		p("                       The class must be a script compiled to a");
		p("                       class file. ");
		p("quit()                 Quit the shell. ");
		p("");
	}

	private static void p(String s) {
		System.out.println(s);
	}

}
