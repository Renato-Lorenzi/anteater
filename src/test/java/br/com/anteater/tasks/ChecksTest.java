package br.com.anteater.tasks;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import br.com.anteater.InvalidArgumentsException;

public class ChecksTest {

	@Test
	public void testArgs() throws InvalidArgumentsException {
		NativeArray args = new NativeArray(new Object[] { "test", 1 });
		Checks.checkArgs(args, String.class, Integer.class);

		try {
			Checks.checkArgs(args, Integer.class, String.class);
			fail("InvalidArguments should have occurred.");
		} catch (InvalidArgumentsException e) {
		}
	}

	@Test
	public void testArgsWithCount() throws InvalidArgumentsException {
		NativeArray args = new NativeArray(new Object[] { "test", 1 });
		Checks.checkArgs(args, 2, String.class, Integer.class);

		try {
			Checks.checkArgs(args, 1, String.class, Integer.class);
			fail("InvalidArguments should have occurred.");
		} catch (InvalidArgumentsException e) {
		}
	}

	@Test
	public void testNamedArgs() throws InvalidArgumentsException {
		NativeObject obj = new NativeObject();
		obj.defineProperty("property", "value", 0);
		obj.defineProperty("intProp", 1, 1);

		String[] props = new String[] { "property", "intProp" };

		Checks.checkArgs(obj, props, String.class, Integer.class);

		try {
			Checks.checkArgs(obj, props, Integer.class, String.class);
			fail("InvalidArguments should have occurred.");
		} catch (InvalidArgumentsException e) {
		}

	}

}
