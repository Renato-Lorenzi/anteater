package br.com.anteater.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.tools.ant.BuildException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;

import br.com.anteater.Anteater;
import br.com.anteater.InvalidArguments;
import br.com.anteater.builder.MissingMethodException;

/**
 * Represent anteater function
 * 
 * @author renatol
 * 
 */
public abstract class AnteaterScript extends ScriptBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static Anteater anteater = null;

	public AnteaterScript() {
		anteater = new Anteater();
	}

	/**
	 * @throws InvalidArguments
	 * @throws MissingMethodException
	 * 
	 * 
	 */
	@Override
	public Object doExecuteAnt(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		Object ret;
		try {
			ret = anteater.exec(cx, thisObj, args, funObj);
		} catch (MissingMethodException e) {
			throw new BuildException(e);
		} catch (InvalidArguments e) {
			throw new BuildException(e);
		}
		return ret == null ? Context.getUndefinedValue() : ret;
	}

	/**
	 * 
	 * 
	 */
	@Override
	public Object doShellExec(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		String exeName = (String) args[0];
		String cmdLine = exeName;
		NativeArray array = (NativeArray) args[1];
		for (Object param : array) {
			cmdLine += " " + (String) param;
		}

		try {
			return execCMD("[shell " + exeName + "] ", cmdLine);
		} catch (Exception e) {
			throw new BuildException("Command " + exeName + " not found");
		}
	}

	private int execCMD(final String prefix, String cmdLine) throws IOException, InterruptedException {
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
