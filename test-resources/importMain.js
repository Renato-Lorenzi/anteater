load("test-resources/import.js");

ant.import({
	file : "anteater/test-resources/import.xml",
	as : "importxml"
});

ant.executeTarget("importxml.copy-file1");
ant.executeTarget("copy-file");