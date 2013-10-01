package br.com.anteater.builder.nested;

import java.util.ArrayList;
import java.util.List;

import br.com.anteater.builder.BuilderSupport;
import br.com.anteater.builder.MissingMethodException;

public class NestedContainer implements NestedObject {

	private List<NestedObject> container = new ArrayList<NestedObject>();

	@Override
	public void call() throws MissingMethodException {
		for (NestedObject obj : container) {
			obj.call();
		}
	}

	@Override
	@Deprecated
	public Object call(String methodName) {
		return null;
	}

	@Override
	@Deprecated
	public void setDelegate(BuilderSupport builderSupport) {

	}

	public void addObject(NestedObject nestedObject) {
		container.add(nestedObject);
	}

	public boolean isEmpty() {
		return container.isEmpty();
	}
}
