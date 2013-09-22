/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;

import com.jant.ant.AntExecute;
import com.jant.ant.AntWrapperExecute;
import com.jant.ant.EspecialFunctions;
import com.sun.xml.internal.bind.marshaller.XMLWriter;

/**
 * The shell program.
 * 
 * Can execute scripts interactively or in batch mode at the command line. An
 * example of controlling the JavaScript engine.
 * 
 * @author Norris Boyd
 */
public class Shell extends ScriptableObject {
	private static final long serialVersionUID = -5638074146250193112L;

	@Override
	public String getClassName() {
		return "global";
	}

	/**
	 * Main entry point.
	 * 
	 * Process arguments as would a normal Java program. Also create a new
	 * Context and associate it with the current thread. Then set up the
	 * execution environment and begin to execute scripts.
	 */
	public static void main(String args[]) {
		// Associate a new Context with this thread
		Context cx = Context.enter();
		try {
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed.
			Shell shell = new Shell();
			cx.initStandardObjects(shell);

			// Define some global functions particular to the shell. Note
			// that these functions are not part of ECMA.
			String[] names = { "print", "quit", "version", "load", "help", "executeAnt" };
			shell.defineFunctionProperties(names, Shell.class, ScriptableObject.DONTENUM);

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

			shell.processSource(cx, "src/javascript/ant.js");
			shell.processSource(cx, args.length == 0 ? null : args[0]);
		} finally {
			Context.exit();
		}
	}

	/**
	 * Parse arguments.
	 */
	public static String[] processOptions(Context cx, String args[]) {
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
		Shell shell = (Shell) getTopLevelScope(thisObj);
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
			FileReader in = null;
			try {
				in = new FileReader(filename);
			} catch (FileNotFoundException ex) {
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

	static AntWrapperExecute exec = new AntWrapperExecute();

	public static boolean e(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		Map<String, Object> map = new HashMap<String, Object>();
		NativeArray nativeArray = (NativeArray) args[1];
		if (nativeArray.size() > 0) {
			Object natObj = nativeArray.get(0);
			if (natObj instanceof Scriptable) {
				// toXML(thisObj, map, natObj);
			} else {
				map.put(EspecialFunctions.ADD_TEXT, Context.toString(natObj));
			}
		}
		return exec.execute(Context.toString(args[0]), map);
	}

	private static void p(String s) {
		System.out.println(s);
	}

	private boolean quitting;

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	public static boolean executeAnt(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		Map<String, Object> map = new HashMap<String, Object>();
		NativeArray nativeArray = (NativeArray) args[1];
		if (nativeArray.size() > 0) {
			Object natObj = nativeArray.get(0);
			if (natObj instanceof Scriptable) {
				StringBuilder sb = new StringBuilder();
				go((Scriptable) natObj, Context.toString(args[0]), sb);
				System.out.println(sb);
			} else {
				map.put(EspecialFunctions.ADD_TEXT, Context.toString(natObj));
			}
		}
		return false;// exec.execute(Context.toString(args[0]), map);
	}

	static void go(Scriptable natObj, String name, StringBuilder ret) {
		Project prj = new Project();
		File buildFile = new File("buildA.xml");
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(buildFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		prj.addBuildListener(consoleLogger);
		try {
			ret.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			ret.append("	<project default=\"main\" name=\"seniortools\">	\n");
			ret.append("<target name=\"main\">\n");
			toXML(natObj, name, ret);
			ret.append("	</target>");
			ret.append("	</project>");
			out.write(ret.toString().getBytes());
			prj.fireBuildStarted();
			prj.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			prj.addReference("ant.projectHelper", helper);
			helper.parse(prj, buildFile);
			prj.getTargets().get(name).getTasks()[0].perform();
			out.close();
			prj.fireBuildFinished(null);
		} catch (BuildException e) {
			prj.fireBuildFinished(e);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void toXML(Scriptable natObj, String name, StringBuilder ret) {
		ret.append("\n<" + name);
		boolean withScript = false;
		for (Object id : natObj.getIds()) {
			String idTxt = Context.toString(id);
			Object obj = natObj.get(idTxt, natObj);
			if (obj instanceof Scriptable) {
				ret.append(">");
				withScript = true;
				toXML((Scriptable) obj, idTxt, ret);
			} else {
				ret.append(" " + idTxt + "= \"" + Context.toString(obj) + "\" ");
			}
		}
		ret.append(withScript ? "<" + name + "/>\n" : "/>\n");
	}
}