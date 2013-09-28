package br.com.anteater.tasks;

import org.apache.tools.ant.Project;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import br.com.anteater.InvalidArguments;
import br.com.anteater.builder.MissingMethodException;
import br.com.anteater.script.ScriptLoader;

public class AnteaterTasks {
	private TargetManager targetManager;
	private Project project;
	private ScriptLoader loader;
	private String projectName = null;

	public AnteaterTasks(Project project, ScriptLoader loader) {
		this.project = project;
		this.loader = loader;
		targetManager = new TargetManager(project);
	}

	public TaskResult execute(Context cx, Scriptable thisObj, String methodName, NativeArray args) throws InvalidArguments, MissingMethodException {
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
		}

		return ret;
	}

	private boolean importSource(Context cx, NativeArray args) throws InvalidArguments {
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
				throw new InvalidArguments("Arg 'as cannot be empty.");
			}
			loader.load(cx, fileName);
		} finally {
			projectName = actualProject;
		}
		return true;
	}

	private Object getProp(NativeArray args) throws InvalidArguments {
		Checks.checkArgs(args, 1, String.class);
		return project.getProperties().get(args.get(0));
	}
}
