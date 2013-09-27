ant.import({
	file : "test-resources/import.js",
	as : "importjs"
});

ant.import({
	file : "anteater/test-resources/import.xml",
	as : "importxml"
});

ant.executeTarget("importxml.copy-file1");
ant.executeTarget("importjs.copy-file");