var isExecuted = false;

ant.target({
	name : "test-file1",
	depends : "main"
}, function() {
	if (isExecuted) {
		ant.copy({
			file : "test-resources/in/file1.txt",
			tofile : "test-resources/out/file1.txt"
		});
	}
});

ant.target({
	name : "test-file",
	depends : "test-file1"
}, function() {
	if (isExecuted) {
		ant.copy({
			file : "test-resources/in/file.txt",
			tofile : "test-resources/out/file.txt"
		});
	}
});

ant.target({
	name : "main",
	depends : "test-file,test-file1"
}, function() {
	if (isExecuted) {
		ant.copy({
			file : "test-resources/in/file.txt",
			tofile : "test-resources/out/file.txt"
		});
	}
});

// Setting this flag should be copied
isExecuted = true;
ant.executeTarget("main");