ant.macrodef({
	name : "copyFiles",
	element : {
		name : "eventCopy"
	}
}, function() {
	ant.sequential(function() {
		ant.copy({
			file : "test-resources/in/file.txt",
			tofile : "test-resources/out/file.txt"
		});
		ant.eventCopy();
	});
});

ant.copyFiles(function() {
	ant.eventCopy(function() {
		ant.copy({
			file : "test-resources/in/file1.txt",
			tofile : "test-resources/out/file1.txt"
		});
	});
});