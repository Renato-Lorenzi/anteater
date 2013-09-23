package br.com.anteater.script;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AnteaterScriptTest {

	public static final String TEST_RES = "test-resources/";
	public static final String IN_DIR = TEST_RES + "in/";
	public static final String OUT_DIR = TEST_RES + "out/";
	File outDirFile = new File(OUT_DIR);
	File tempScript;

	@Before
	public void setup() throws IOException {
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

		writeScript("ant.copy({file : '%s', tofile : '%s'});", inFile.getAbsolutePath(), outFile.getAbsolutePath());

		AnteaterScript script = new AnteaterScript();
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

		AnteaterScript script = new AnteaterScript();
		script.execute(new String[] { tempScript.getAbsolutePath() });

		assertFile(outFile, inFile);
		assertFile(outFile1, inFile1);
	}

	@Test
	public void testNestedObjectWithChild() throws IOException {
		File outFile = new File(OUT_DIR + "nestedWithChild.txt");
		File inFile = new File(IN_DIR + "nestedWithChildRet.txt");

		AnteaterScript script = new AnteaterScript();
		script.execute(new String[] { TEST_RES + "nestedWithChild.js" });

		assertFile(outFile, inFile);
	}

	@Test
	public void testNestedObjectWithArray() throws IOException {
		File outFile = new File(OUT_DIR + "file.txt");
		File outFile1 = new File(OUT_DIR + "file1.txt");
		File inFile = new File(IN_DIR + "file.txt");
		File inFile1 = new File(IN_DIR + "file1.txt");

		AnteaterScript script = new AnteaterScript();
		script.execute(new String[] { TEST_RES + "nestedObjectWithArray.js" });

		assertFile(outFile, inFile);
		assertFile(outFile1, inFile1);
	}

	@Test
	public void testTarget() throws IOException {
		File outFile = new File(OUT_DIR + "file.txt");
		File inFile = new File(IN_DIR + "file.txt");

		AnteaterScript script = new AnteaterScript();
		script.execute(new String[] { TEST_RES + "target.js" });

		assertFile(outFile, inFile);
	}

	@Test
	public void testTaskContainer() throws IOException {
		File outFile = new File(OUT_DIR + "file.txt");
		File inFile = new File(IN_DIR + "file.txt");

		AnteaterScript script = new AnteaterScript();
		script.execute(new String[] { TEST_RES + "taskContainer.js" });

		assertFile(outFile, inFile);
	}

	@Test
	public void testProp() throws IOException {
		File outFile = new File(OUT_DIR + "file.txt");
		File inFile = new File(IN_DIR + "file.txt");

		StringBuilder scrp = new StringBuilder();
		scrp.append(format("ant.property({file: '%s'});\n", TEST_RES + "prop.properties")).//
				append("ant.copy({file : '${in.file}', tofile : ant.prop('out.file')});");
		writeScript(scrp.toString());

		AnteaterScript script = new AnteaterScript();
		script.execute(new String[] { tempScript.getAbsolutePath() });

		assertFile(outFile, inFile);

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
				writer.write(format(line, args) + "\n");
			}

		} finally {
			reader.close();
			writer.close();
		}
	}

	public void writeScript(String script, Object... args) throws IOException {
		FileWriter writer = new FileWriter(tempScript);
		try {
			writer.write(format(script, args));
		} finally {
			writer.close();
		}
	}

	@After
	public void after() {
		tempScript.delete();
		clearDir(outDirFile);
	}

}
