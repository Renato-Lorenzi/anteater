package br.com.anteater.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AnteaterScriptTest extends TestCase {

	public static final String TEST_RES = "test-resources/";
	public static final String IN_DIR = TEST_RES + "in/";
	public static final String OUT_DIR = TEST_RES + "out/";
	File outDirFile = new File(OUT_DIR);
	File tempScript;

	@Before
	public void setUp() throws IOException {
		tempScript = File.createTempFile("script-test", ".js");
		if (outDirFile.exists()) {
			clearDir(outDirFile);
		} else {
			outDirFile.mkdir();
		}
	}

	@Test
	public void testBasic() throws IOException {
		File outFile = new File(OUT_DIR + "file.txt");
		File inFile = new File(IN_DIR + "file.txt");

		writeScript("ant.copy({file : '%s', tofile : '%s'});", resolveDir(inFile.getAbsolutePath()), resolveDir(outFile.getAbsolutePath()));

		ScriptBase script = new BuildScript();
		script.execute(new String[] { tempScript.getAbsolutePath() });

		assertFile(outFile, inFile);
	}

	@Test
	public void testNestedObject() throws IOException {
		File outFile = new File(OUT_DIR + "file.txt");
		File outFile1 = new File(OUT_DIR + "file1.txt");
		File inFile = new File(IN_DIR + "file.txt");
		File inFile1 = new File(IN_DIR + "file1.txt");

		writeScript("ant.copy({todir: '%s', fileset: {dir: '%s'}});", OUT_DIR, IN_DIR);

		ScriptBase script = new BuildScript();
		script.execute(new String[] { tempScript.getAbsolutePath() });

		assertFile(outFile, inFile);
		assertFile(outFile1, inFile1);
	}

	@Test
	public void testNestedObjectWithChild() throws IOException {
		File outFile = new File(OUT_DIR + "nestedWithChild.txt");
		File inFile = new File(IN_DIR + "nestedWithChildRet.txt");

		ScriptBase script = new BuildScript();
		script.execute(new String[] { TEST_RES + "nestedWithChild.js" });

		assertFile(outFile, inFile);
	}

	@Test
	public void testNestedObjectWithArray() throws IOException {
		File outFile = new File(OUT_DIR + "file.txt");
		File outFile1 = new File(OUT_DIR + "file1.txt");
		File inFile = new File(IN_DIR + "file.txt");
		File inFile1 = new File(IN_DIR + "file1.txt");

		ScriptBase script = new BuildScript();
		script.execute(new String[] { TEST_RES + "nestedObjectWithArray.js" });

		assertFile(outFile, inFile);
		assertFile(outFile1, inFile1);
	}

	@Test
	public void testTarget() throws IOException {
		File outFile = new File(OUT_DIR + "file.txt");
		File inFile = new File(IN_DIR + "file.txt");

		ScriptBase script = new BuildScript();
		script.execute(new String[] { TEST_RES + "target.js" });

		assertFile(outFile, inFile);
	}

	@Test
	public void testTaskContainer() throws IOException {
		File outFile = new File(OUT_DIR + "file.txt");
		File inFile = new File(IN_DIR + "file.txt");

		ScriptBase script = new BuildScript();
		script.execute(new String[] { TEST_RES + "taskContainer.js" });

		assertFile(outFile, inFile);
	}

	@Test
	public void testProp() throws IOException {
		File outFile = new File(OUT_DIR + "file.txt");
		File inFile = new File(IN_DIR + "file.txt");

		StringBuilder scrp = new StringBuilder();
		scrp.append(String.format("ant.property({file: '%s'});\n", TEST_RES + "prop.properties")).//
				append("ant.copy({file : '${in.file}', tofile : ant.prop('out.file')});");
		writeScript(scrp.toString());

		ScriptBase script = new BuildScript();
		script.execute(new String[] { tempScript.getAbsolutePath() });

		assertFile(outFile, inFile);

	}

	@Test
	public void testImport() {
		File outFile = new File(OUT_DIR + "file.txt");
		File outFile1 = new File(OUT_DIR + "file1.txt");
		File inFile = new File(IN_DIR + "file.txt");
		File inFile1 = new File(IN_DIR + "file1.txt");

		ScriptBase script = new BuildScript();
		script.execute(new String[] { TEST_RES + "importMain.js" });

		assertFile(outFile, inFile);
		assertFile(outFile1, inFile1);

	}

	@Test
	public void testMacrodef() throws IOException {
		File outFile = new File(OUT_DIR + "file.txt");
		File outFile1 = new File(OUT_DIR + "file1.txt");
		File inFile = new File(IN_DIR + "file.txt");
		File inFile1 = new File(IN_DIR + "file1.txt");

		ScriptBase script = new BuildScript();
		script.execute(new String[] { TEST_RES + "macrodef.js" });

		assertFile(outFile, inFile);
		assertFile(outFile1, inFile1);
	}

	private void assertFile(File outFile, File inFile) {
		assertTrue("The file should be found", outFile.exists());
		assertEquals("The file size should be equals", inFile.length(), outFile.length());
	}

	public static void clearDir(File dir) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				clearDir(file);
			}
			assertTrue("Error to delete file/dir " + file.getAbsolutePath(), file.delete());
		}
	}

	public void writeScript(File script, Object... args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(script));
		FileWriter writer = new FileWriter(tempScript);
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				writer.write(String.format(line, args) + "\n");
			}

		} finally {
			reader.close();
			writer.close();
		}
	}

	public void writeScript(String script, Object... args) throws IOException {
		FileWriter writer = new FileWriter(tempScript);
		try {
			writer.write(String.format(script, args));
		} finally {
			writer.close();
		}
	}

	public String resolveDir(String dir) {
		return dir == null ? "" : dir.replace("\\", "/");
	}

	@After
	public void tearDown() {
		tempScript.delete();
		clearDir(outDirFile);
	}

}
