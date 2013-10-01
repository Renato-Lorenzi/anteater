ant.target("test-file1", function() {
	ant.copy({
		file : "src/test/resources/in/file1.txt",
		tofile : "src/test/resources/out/file1.txt"
	});
});

ant.target("test-file", function() {
	ant.copy({
		file : "src/test/resources/in/file.txt",
		tofile : "src/test/resources/out/file.txt"
	});
});

// Set this target, to test the no call of default target in call parameterized
// target
ant.defaultTarget("test-file1");