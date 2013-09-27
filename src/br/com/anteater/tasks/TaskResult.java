package br.com.anteater.tasks;

public class TaskResult {

	private boolean isExecuted = false;
	private Object result = null;

	public boolean isExecuted() {
		return isExecuted;
	}

	void setExecuted(boolean isExecuted) {
		this.isExecuted = isExecuted;
	}

	public Object getResult() {
		return result;
	}

	void setResult(Object result) {
		this.result = result;
	}

}
