package com.jant.ant;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class AntWrapperExecute {
	Project prj = new Project();

	public AntWrapperExecute() {
		prj.init();
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		prj.addReference("ant.projectHelper", ProjectHelper.getProjectHelper());
		prj.addBuildListener(consoleLogger);
	}

	public boolean execute(String name, Map<String, Object> map) {
		Class<?> task = prj.getTaskDefinitions().get(name);
		try {
			Task taskInstance = (Task) task.newInstance();
			taskInstance.setProject(prj);
			setParams(taskInstance, map);
			taskInstance.perform();
			// TODO Check how the condition will be
			// if (task.getInterfaces().equals(Condition.class)) {
			// return (Boolean) task.getMethod("eval").invoke(taskInstance);
			// } else {
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private void setParams(Task taskInstance, Map<String, Object> map) {
		RuntimeConfigurable wrapper = new RuntimeConfigurable(taskInstance, taskInstance.getTaskName());
		for (String param : map.keySet()) {
			Object paramObj = map.get(param);
			if (param.equals(EspecialFunctions.ADD_TEXT)) {
				wrapper.addText((String) paramObj);
			} else {
				wrapper.setAttribute(param, paramObj);
			}
		}
	}

	private String toTask() {
		return "";
	}

}
