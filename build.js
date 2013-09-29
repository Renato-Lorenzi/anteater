var runTestsID = "run-tests";

ant.defaultTarget(runTestsID);

ant.property({
	file : "build.properties"
});

/**
 * Compile and run all tests
 * 
 */
ant.target(runTestsID, function() {
	var pathId = "class-path-id";

	ant.path({
		id : pathId,
		fileset : [ {
			dir : "lib",
			includes : "**/*.jar"
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
			name : "br.com.anteater.AllTests",
			outfile : "temp/test.xml"
		}
	});

});

/**
 * Command used to generate anteater.jar
 */
ant.target("generate-anteater", function() {
	ant.jar({
		destfile : "jar/anteater.jar",
		basedir : "bin",
		excludes : "**/*Test.class",
		manifest : {
			attribute : {
				name : "Anteater-Version",
				value : "${anteater.version}"
			}
		}
	});

});

// ************ Utils ************

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