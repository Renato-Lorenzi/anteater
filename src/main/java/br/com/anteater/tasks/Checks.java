package br.com.anteater.tasks;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import br.com.anteater.InvalidArgumentsException;

public class Checks {

	/**
	 * Check args type
	 * 
	 * @param args
	 * @param argsCount
	 * @param classes
	 * @throws InvalidArgumentsException
	 */
	public static void checkArgs(NativeArray args, Class<?>... classes) throws InvalidArgumentsException {
		for (int i = 0; i < classes.length; i++) {
			Class<? extends Object> clazz = args.get(i).getClass();
			if (!(classes[i].isAssignableFrom(clazz))) {
				throw new InvalidArgumentsException("Invalid argument type in parameter %d. Type: expected: %s, found: %s", i, classes[i].getName(), clazz.getName());
			}
		}
	}

	/**
	 * Check args count and args type
	 * 
	 * @param args
	 * @param argsCount
	 * @param classes
	 * @throws InvalidArgumentsException
	 */
	public static void checkArgs(NativeArray args, int argsCount, Class<?>... classes) throws InvalidArgumentsException {
		if (args.size() != argsCount) {
			throw new InvalidArgumentsException("Invalid number of parameters in call of the task.");
		}
		checkArgs(args, classes);
	}

	/**
	 * 
	 * @param args
	 * @param names
	 * @param classes
	 * @throws InvalidArgumentsException
	 */
	public static void checkArgs(NativeObject args, String[] names, Class<?>... classes) throws InvalidArgumentsException {
		for (int i = 0; i < names.length; i++) {
			Object object = args.get(names[i]);
			if (object == null) {
				throw new InvalidArgumentsException("Required arg " + names[i] + " was not informed.");
			}
			Class<? extends Object> objClass = object.getClass();
			if (!(classes[i].isAssignableFrom(objClass))) {
				throw new InvalidArgumentsException("Invalid argument type in parameter %s. Type: expected: %s, found: %s", names[i], classes[i].getName(), objClass.getName());
			}
		}
	}
}
