package br.com.anteater;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;

public class TargetManager {

	private Project project;
	private Map<String, AnteaterTarget> targets = new HashMap<String, AnteaterTarget>();

	public TargetManager(Project project) {
		super();
		this.project = project;
	}

	public void addTarget(Scriptable thisObj, NativeArray args) {
		// TODO Check projectName dependencies
		AnteaterTarget anteaterTarget = new AnteaterTarget(null, thisObj, args);
		String name = anteaterTarget.getName();
		if (targets.containsKey(name)) {
			throw new BuildException("Duplicate target " + name);
		}
		targets.put(name, anteaterTarget);

	}

	public void execute(Context cx, NativeArray args) throws InvalidArguments {
		if (args.size() != 1) {
			throw new InvalidArguments("Invalid number of parameters in call of the executeTarget function.");
		}

		Object object = args.get(0);
		if (!(object instanceof String)) {
			throw new InvalidArguments("Invalid arguments in call of executeTarget function.");
		}

		String targetName = (String) object;
		HashSet<String> executed = new HashSet<String>();
		HashSet<String> myDependents = new HashSet<String>();

		execTarget(cx, targetName, executed, myDependents);
	}

	/**
	 * Execute the target and resolve dependencies
	 * 
	 * @param cx
	 * @param targetName
	 * @param executed
	 * @param myDependents
	 */
	private void execTarget(Context cx, String targetName, HashSet<String> executed, HashSet<String> myDependents) {
		if (!executed.contains(targetName)) {
			AnteaterTarget target = targets.get(targetName);
			if (target != null) {
				if (myDependents.contains(targetName)) {
					throw new BuildException("Target " + targetName + " has circular references.");
				}
				myDependents.add(targetName);

				for (String depend : target.getDepends()) {
					if (depend.equals(targetName)) {
						throw new BuildException("Target can not even depend on it.");
					}
					execTarget(cx, depend, executed, myDependents);
				}
				myDependents.remove(targetName);
				executed.add(targetName);
				System.out.println(targetName + ":");
				target.getLambda().call(cx, target.getThisObj(), target.getThisObj(), new Object[] {});
			} else {
				executed.add(targetName);
				project.executeTarget(targetName);
			}
		}
	}
}
