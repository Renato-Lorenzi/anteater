package br.com.anteater.builder.nested;

import java.util.Map;

import br.com.anteater.builder.AntBuilder;
import br.com.anteater.builder.MissingMethodException;

/**
 * Represents nested params.
 * 
 * 
 * Ex.: FileSet, Classpath, and any typedef.
 * 
 * @author renatol
 * 
 */
public class NestedParams extends NestedContainer {

	private String name;
	private Map<String, Object> params;
	private AntBuilder ant;
	private boolean isInvoking = false;

	public NestedParams(String name, Map<String, Object> params, AntBuilder ant) {
		this.name = name;
		this.params = params;
		this.ant = ant;
	}

	@Override
	public void call() throws MissingMethodException {
		// I'm not proud of it
		if (!isInvoking) {
			isInvoking = true;
			try {
				// se passa como nestedObject para fazer a recursividade e criar
				// os filhos
				ant.invokeMethod(name, new Object[] { params, this });
			} finally {
				isInvoking = false;
			}
		} else {
			super.call();
		}
	}
}
