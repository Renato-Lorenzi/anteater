// Only string name to define target
ant.target("test-file1", function() {
	ant.copy({
		file : "test-resources/in/file1.txt",
		tofile : "test-resources/out/file1.txt"
	});
});

// Object with String name and depends
ant.target({
	name : "test-file",
	depends : "test-file1"
}, function() {
	ant.copy({
		file : "test-resources/in/file.txt",
		tofile : "test-resources/out/file.txt"
	});
});

// Object with String name and depends
ant.target({
	name : "main",
	depends : "test-file,test-file1"
}, function() {
	ant.copy({
		file : "test-resources/in/file.txt",
		tofile : "test-resources/out/file.txt"
	});
});

ant.executeTarget("main");