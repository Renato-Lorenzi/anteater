var pathId = "class-path-id";
ant.path({
	id : pathId,
	fileset : [ {
		dir : "lib",
		includes : "**/*.jar",
		excludes : "**/ant1.9.2.jar"
	}, {
		dir : "lib-test",
		includes : "**/*.jar"
	} ],
	dirset : {
		dir : "bin",
		includes : "**"
	}
});

/**
 * Compile all src and tests resources
 */
ant.javac({
	srcdir : "src",
	destdir : "bin",
	classpath : {
		refid : pathId
	}
});

ant.javac({
	srcdir : "tests",
	destdir : "bin",
	classpath : {
		refid : pathId
	}
});

ant.taskdef({
	name : "junit",
	classname : "org.apache.tools.ant.taskdefs.optional.junit.JUnitTask",
	classpath : {
		refid : pathId
	}
});

ant.junit({
	printsummary : "yes",
	fork : "yes",
	showoutput : "true",
	classpath : {
		refid : pathId
	},
	formatter : {
		type : "xml"
	},
	test : {
		name : "br.com.anteater.script.AnteaterScriptTest",
		outfile : "temp/test.xml"
	}
});

/**
 * Echo path
 * 
 * @param classpathId
 */
function echoPath(classpathId) {
	ant.echo("This path: ");
	ant.echo(pathToStr(classpathId));
}

function pathToStr(classpathId) {
	var outprop = "echo-path";
	ant.pathconvert({
		property : outprop,
		refid : classpathId
	});
	return ant.prop(outprop);
}