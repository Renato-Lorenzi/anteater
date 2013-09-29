package br.com.anteater;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.com.anteater.script.AnteaterScriptTest;
import br.com.anteater.tasks.ChecksTest;

@RunWith(Suite.class)
@SuiteClasses({ ChecksTest.class,//
		AnteaterScriptTest.class })
public class AllTests {

}
