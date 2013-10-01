ant.import({
	file : "src/test/resources/import.js",
	as : "importjs"
});

ant.import({
	file : "anteater/src/test/resources/import.xml",
	as : "importxml"
});

ant.executeTarget("importxml.copy-file1");
ant.executeTarget("importjs.copy-file");