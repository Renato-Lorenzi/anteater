var isExecuted = false;
ant.target({
	name : "test-target"
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
ant.executeTarget("test-target");