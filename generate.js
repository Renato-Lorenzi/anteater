/**
 * Command used to generate anteater.jar
 */
ant.jar({
	destfile : "jar/last-version/anteater.jar",
	basedir : "bin",
	excludes : "**/*Test.class"
});
