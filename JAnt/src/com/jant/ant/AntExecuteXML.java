package com.jant.ant;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class AntExecuteXML {
	Project prj = new Project();

	public AntExecuteXML() {
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
			setParams(task, taskInstance, map);

			if (task.getInterfaces().equals(Condition.class)) {
				return (Boolean) task.getMethod("eval").invoke(taskInstance);
			} else {
				task.getMethod("execute").invoke(taskInstance);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private void setParams(Class<?> task, Task taskInstance, Map<String, Object> map) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (String param : map.keySet()) {
			Object paramObj = map.get(param);
			param = paramToFunction(param);
			Method method = task.getMethod(param, paramObj.getClass());
			// TODO Será?
			paramObj = (paramObj.getClass().equals(String.class)) ? prj.replaceProperties((String) paramObj) : paramObj;
			method.invoke(taskInstance, paramObj);
		}

	}

	private String paramToFunction(String param) {
		param = param.equals(EspecialFunctions.ADD_TEXT) ? param : "set" + param.substring(0, 1).toUpperCase() + param.substring(1, param.length());
		return param;
	}


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
	 * 
	 * 
	 * 
	 * 
	 * TeSTE
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		File buildFile = new File("build.xml");
		Project p = new Project();
		p.setUserProperty("ant.file", buildFile.getAbsolutePath());
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(consoleLogger);

		try {
			p.fireBuildStarted();
			p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			p.addReference("ant.projectHelper", helper);
			helper.parse(p, buildFile);
			p.executeTarget(p.getDefaultTarget());
			p.fireBuildFinished(null);
		} catch (BuildException e) {
			p.fireBuildFinished(e);
		}
	}
}
