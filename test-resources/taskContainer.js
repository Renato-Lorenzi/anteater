ant.sequential(function() {
	ant.copy({
		file : "test-resources/in/file.txt",
		tofile : "test-resources/out/file.txt"
	});
});