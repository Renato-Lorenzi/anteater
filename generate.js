/**
 * Command used to generate anteater.jar
 */
ant.jar({
	destfile : "jar/anteater.jar",
	basedir : "bin",
	excludes : "**/*Test.class"
});
