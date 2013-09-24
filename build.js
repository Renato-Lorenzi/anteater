var pathId = "class-path-id";
var testClasspath = {
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
};

ant.javac({
	srcdir : "src",
	classpath : testClasspath
});

ant.taskdef({
	name : "junit",
	classname : "org.apache.tools.ant.taskdefs.optional.junit.JUnitTask",
	classpath : testClasspath
});

ant.junit({
	printsummary : "yes",
	fork : "yes",
	classpath : testClasspath,
	formatter : {
		type : "xml"
	},
	test : {
		name : "br.com.anteater.script.AnteaterScriptTest",
		outfile : "temp/test.xml"
	}
});

echoPath(pathId);

/**
 * Command used to generate anteater.jar
 */
function generateAnteater() {
	ant.jar({
		destfile : "jar/test.jar",
		basedir : "bin",
		excludes : "**/Test.class"
	});
}

/**
 * Echo path
 * 
 * @param classpathId
 */
function echoPath(classpathId) {
	var outprop = "echo-path";

	ant.pathconvert({
		property : outprop,
		refid : classpathId
	});
	ant.echo("This path: ");
	ant.echo(ant.prop(outprop));
}