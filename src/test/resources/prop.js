ant.target({
	name : "test-target"
}, function() {
	ant.copy({
		file : "src/test/resources/in/file.txt",
		tofile : "src/test/resources/out/file.txt"
	});
});

ant.project.executeTarget("test-target");