package br.com.anteater.tasks;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import br.com.anteater.InvalidArguments;

public class Checks {

	/**
	 * Check args type
	 * 
	 * @param args
	 * @param argsCount
	 * @param classes
	 * @throws InvalidArguments
	 */
	public static void checkArgs(NativeArray args, Class<?>... classes) throws InvalidArguments {
		for (int i = 0; i < classes.length; i++) {
			if (!(args.get(i).getClass().equals(classes[i]))) {
				throw new InvalidArguments("Invalid arguments in call of task.");
			}
		}
	}

	/**
	 * Check args count and args type
	 * 
	 * @param args
	 * @param argsCount
	 * @param classes
	 * @throws InvalidArguments
	 */
	public static void checkArgs(NativeArray args, int argsCount, Class<?>... classes) throws InvalidArguments {
		if (args.size() != argsCount) {
			throw new InvalidArguments("Invalid number of parameters in call of the task.");
		}
	}

	/**
	 * 
	 * @param args
	 * @param names
	 * @param classes
	 * @throws InvalidArguments
	 */
	public static void checkArgs(NativeObject args, String[] names, Class<?>... classes) throws InvalidArguments {
		for (int i = 0; i < names.length; i++) {
			Object object = args.get(names[i]);
			if (object == null) {
				throw new InvalidArguments("Required arg " + names[i] + " was not informed.");
			}
			if (!(object.getClass().equals(classes[i]))) {
				throw new InvalidArguments("Invalid arguments in call of task.");
			}
		}
	}

}
