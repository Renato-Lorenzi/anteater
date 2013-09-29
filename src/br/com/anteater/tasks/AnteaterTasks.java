package br.com.anteater.tasks;

import org.apache.tools.ant.Project;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import br.com.anteater.InvalidArgumentsException;
import br.com.anteater.builder.MissingMethodException;
import br.com.anteater.script.ScriptLoader;

public class AnteaterTasks {
	private TargetManager targetManager;
	private Project project;
	private ScriptLoader loader;
	private String projectName = null;
	private String defaultTarget = null;

	public AnteaterTasks(Project project, ScriptLoader loader) {
		this.project = project;
		this.loader = loader;
		targetManager = new TargetManager(project);
	}

	public TaskResult execute(Context cx, Scriptable thisObj, String methodName, NativeArray args) throws InvalidArgumentsException, MissingMethodException {
		TaskResult ret = new TaskResult();

		if ("prop".equals(methodName)) {
			ret.setExecuted(true);
			ret.setResult(getProp(args));
		} else if ("executeTarget".equals(methodName)) {
			ret.setExecuted(true);
			targetManager.execute(cx, args);
		} else if ("target".equals(methodName)) {
			ret.setExecuted(true);
			targetManager.addTarget(projectName, thisObj, args);
		} else if ("import".equals(methodName)) {
			ret.setExecuted(importSource(cx, args));
		} else if ("defaultTarget".equals(methodName)) {
			setDefaultTarget(args);
			ret.setExecuted(true);
		}

		return ret;
	}

	/**
	 * Set default target variable. Don't validate if exists the target because
	 * the target cannot exists here
	 * 
	 * @param args
	 * @throws InvalidArgumentsException
	 */
	private void setDefaultTarget(NativeArray args) throws InvalidArgumentsException {
		Checks.checkArgs(args, String.class);
		defaultTarget = (String) args.get(0);
	}

	private boolean importSource(Context cx, NativeArray args) throws InvalidArgumentsException {
		Checks.checkArgs(args, 1, NativeObject.class);
		NativeObject obj = (NativeObject) args.get(0);

		Checks.checkArgs(obj, new String[] { "file" }, String.class);
		String fileName = (String) obj.get("file");

		if (!fileName.endsWith(".js")) {
			return false;
		}

		Checks.checkArgs(obj, new String[] { "as" }, String.class);
		String actualProject = projectName;
		projectName = (String) obj.get("as");
		try {
			if (projectName.equals("")) {
				throw new InvalidArgumentsException("Arg 'as cannot be empty.");
			}
			loader.load(cx, fileName);
		} finally {
			projectName = actualProject;
		}
		return true;
	}

	private Object getProp(NativeArray args) throws InvalidArgumentsException {
		Checks.checkArgs(args, 1, String.class);
		return project.getProperties().get(args.get(0));

	}

	public String getDefaultTarget() {
		return defaultTarget;
	}

	public void executeTarget(Context cx, String defaultTarget) {
		targetManager.execute(cx, defaultTarget);
	}
}
