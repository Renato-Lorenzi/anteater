package br.com.anteater.main;

import java.io.File;

import br.com.anteater.InvalidArgumentsException;
import br.com.anteater.script.BuildScript;
import br.com.anteater.script.InteractiveScript;
import br.com.anteater.script.ScriptBase;

public class Main {

	/**
	 * @param args
	 * @throws InvalidArgumentsException
	 */
	public static void main(String[] args) throws InvalidArgumentsException {
		boolean buildFileExists = new File("build.js").exists();
		ScriptBase script = (args.length == 1 && args[0].equals("-c")) || args.length == 0 && !buildFileExists ? new InteractiveScript() : new BuildScript();

		int exitCode = script.execute(args);
		if (exitCode != 0) {
			System.exit(exitCode);
		}
	}
}
