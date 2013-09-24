package br.com.anteater.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;

import br.com.anteater.Anteater;
import br.com.anteater.InvalidArguments;
import br.com.anteater.builder.MissingMethodException;

public class AnteaterScript extends ScriptableObject {
	private static final long serialVersionUID = -5638074146250193112L;

	private static Anteater anteater = null;

	@Override
	public String getClassName() {
		return "global";
	}

	/**
	 * Process arguments as would a normal Java program. Also create a new
	 * Context and associate it with the current thread. Then set up the
	 * execution environment and begin to execute scripts.
	 */
	public void execute(String args[]) {
		anteater = new Anteater();
		try {
			// Associate a new Context with this thread
			Context cx = Context.enter();
			try {
				// Initialize the standard objects (Object, Function, etc.)
				// This must be done before scripts can be executed.
				AnteaterScript shell = new AnteaterScript();
				cx.initStandardObjects(shell);

				// Define some global functions particular to the shell. Note
				// that these functions are not part of ECMA.
				String[] names = { "print", "quit", "version", "load", "help", "executeAnt", "shellExec" };
				shell.defineFunctionProperties(names, AnteaterScript.class, ScriptableObject.DONTENUM);

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
				Scriptable argsObj = cx.newArray(shell, array);
				shell.defineProperty("arguments", argsObj, ScriptableObject.DONTENUM);

				shell.processSource(cx, "script/ant.js");
				shell.processSource(cx, args.length == 0 ? null : args[0]);
			} finally {
				Context.exit();
			}
		} finally {
			anteater.endExecution();
			anteater = null;
		}
	}

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
			}
			if (arg.equals("-version")) {
				if (++i == args.length)
					usage(arg);
				double d = Context.toNumber(args[i]);
				if (d != d)
					usage(arg);
				cx.setLanguageVersion((int) d);
				continue;
			}
			usage(arg);
		}
		return new String[0];
	}

	/**
	 * Print a usage message.
	 */
	private static void usage(String s) {
		p("Didn't understand \"" + s + "\".");
		p("Valid arguments are:");
		p("-version 100|110|120|130|140|150|160|170");
		System.exit(1);
	}

	/**
	 * Print a help message.
	 * 
	 * This method is defined as a JavaScript function.
	 */
	public void help() {
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
		p("print([expr ...])      Evaluate and print expressions. ");
		p("quit()                 Quit the shell. ");
		p("version([number])      Get or set the JavaScript version number.");
		p("");
	}

	/**
	 * Print the string values of its arguments.
	 * 
	 * This method is defined as a JavaScript function. Note that its arguments
	 * are of the "varargs" form, which allows it to handle an arbitrary number
	 * of arguments supplied to the JavaScript function.
	 * 
	 */
	public static void print(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		for (int i = 0; i < args.length; i++) {
			if (i > 0)
				System.out.print(" ");

			// Convert the arbitrary JavaScript value into a string form.
			// String s = Context.toString(args[i]);

			System.out.println(Context.toObject(args[i], thisObj).getClassName());

		}
		System.out.println();
	}

	/**
	 * Quit the shell.
	 * 
	 * This only affects the interactive mode.
	 * 
	 * This method is defined as a JavaScript function.
	 */
	public void quit() {
		quitting = true;
	}

	/**
	 * Get and set the language version.
	 * 
	 * This method is defined as a JavaScript function.
	 */
	public static double version(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		double result = cx.getLanguageVersion();
		if (args.length > 0) {
			double d = Context.toNumber(args[0]);
			cx.setLanguageVersion((int) d);
		}
		return result;
	}

	/**
	 * Load and execute a set of JavaScript source files.
	 * 
	 * This method is defined as a JavaScript function.
	 * 
	 */
	public static void load(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		AnteaterScript shell = (AnteaterScript) getTopLevelScope(thisObj);
		for (int i = 0; i < args.length; i++) {
			shell.processSource(cx, Context.toString(args[i]));
		}
	}

	/**
	 * Evaluate JavaScript source.
	 * 
	 * @param cx
	 *            the current context
	 * @param filename
	 *            the name of the file to compile, or null for interactive mode.
	 */
	private void processSource(Context cx, String filename) {
		if (filename == null) {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String sourceName = "<stdin>";
			int lineno = 1;
			boolean hitEOF = false;
			do {
				int startline = lineno;
				System.err.print("js> ");
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
						System.err.println(Context.toString(result));
					}
				} catch (WrappedException we) {
					// Some form of exception was caught by JavaScript and
					// propagated up.
					System.err.println(we.getWrappedException().toString());
					we.printStackTrace();
				} catch (EvaluatorException ee) {
					// Some form of JavaScript error.
					System.err.println("js: " + ee.getMessage());
				} catch (JavaScriptException jse) {
					// Some form of JavaScript error.
					System.err.println("js: " + jse.getMessage());
				} catch (IOException ioe) {
					System.err.println(ioe.toString());
				}
				if (quitting) {
					// The user executed the quit() function.
					break;
				}
			} while (!hitEOF);
			System.err.println();
		} else {
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
			} catch (WrappedException we) {
				System.err.println(we.getWrappedException().toString());
				we.printStackTrace();
			} catch (EvaluatorException ee) {
				System.err.println("js: " + ee.getMessage());
			} catch (JavaScriptException jse) {
				System.err.println("js: " + jse.getMessage());
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
	}

	private static void p(String s) {
		System.out.println(s);
	}

	private boolean quitting;

	/**
	 * @throws InvalidArguments
	 * @throws MissingMethodException
	 * 
	 * 
	 */

	public static Object executeAnt(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws MissingMethodException, InvalidArguments {
		return anteater.exec(cx, thisObj, args, funObj);
	}

	/**
	 * @throws InvalidArguments
	 * @throws MissingMethodException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 * 
	 */

	public static Object shellExec(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws MissingMethodException, InvalidArguments, IOException, InterruptedException {
		String exeName = (String) args[0];
		String cmdLine = exeName;
		NativeArray array = (NativeArray) args[1];
		for (Object param : array) {
			cmdLine += " " + (String) param;
		}
		return execCMD("[shell " + exeName + "] ", cmdLine);
	}

	public static int execCMD(final String prefix, String cmdLine) throws IOException, InterruptedException {
		Process process = Runtime.getRuntime().exec(cmdLine);
		InputStream stdout = process.getInputStream();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
		final BufferedReader readerError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		new Thread() {

			@Override
			public void run() {
				String line;
				try {
					while ((line = reader.readLine()) != null) {
						System.out.println(prefix + line);
					}

					while ((line = readerError.readLine()) != null) {
						System.err.println(prefix + "ERROR: " + line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}.start();
		return process.waitFor();
	}

}
