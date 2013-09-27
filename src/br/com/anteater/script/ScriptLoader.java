package br.com.anteater.script;

import org.mozilla.javascript.Context;

/**
 * Interface allowing to be loaded script of generic form
 * 
 * @author Renato.Lorenzi
 * 
 */
public interface ScriptLoader {

	void load(Context cx, String fileName);

}
