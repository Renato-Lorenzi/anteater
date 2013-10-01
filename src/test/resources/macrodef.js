ant.macrodef({
	name : "copyFiles",
	element : {
		name : "eventCopy"
	}
}, function() {
	ant.sequential(function() {

		ant.copy({
			file : "src/test/resources/in/file.txt",
			tofile : "src/test/resources/out/file.txt"
		});
		ant.eventCopy();
	});
});

ant.copyFiles({
	eventCopy : {
		copy : {
			file : "src/test/resources/in/file1.txt",
			tofile : "src/test/resources/out/file1.txt"
		}
	}
});
