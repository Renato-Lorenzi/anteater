ant.defaultTarget("generate-anteater");
ant.property({file: "generate.properties"});


/**
 * Command used to generate anteater.jar to release
 */
ant.target("generate-anteater", function() {	
	var version = ant.prop("anteater.version");
	var generatedFileName = "anteater_" + version;
	var outputdir = ant.prop("java.io.tmpdir") + generatedFileName;

	ant.delete({
		dir : outputdir
	});
	
	ant.copy({
		file: "${anteater.rhino.jar}",
		tofile : outputdir + "/lib/js-14.jar",		
	});
	
	
	ant.echo("******* generating anteater.jar *******");
	ant.jar({
		destfile : outputdir + "/lib/anteater.jar",
		basedir : "target/classes",
		manifest : {
			attribute : {
				name : "Anteater-Version",
				value : version
			}
		}
	});
	
	ant.echo("******* Copying files from ant instalation *******");
	ant.copy({
		todir : outputdir + "/lib",		
		fileset : {
			dir : "${anteater.ant.home}/lib",
			includes : "**/*.jar"
		}
	});

	ant.copy({
		todir : outputdir + "/docs",		
		fileset : {
			dir : "${anteater.ant.home}/docs"
		}
	});

	ant.echo("******* Copying bin files from anteater *******");	
	ant.copy({
		todir : outputdir + "/bin",		
		fileset : {
			dir : "bin"
		}
	});

	
	ant.echo("******* Generating zip file *******");	
	ant.zip({
		basedir : outputdir,
		destfile : "${anteater.output}/" + generatedFileName + ".zip"
	});

	ant.echo("******* Generating tar file *******");
	ant.tar({
		basedir : outputdir,
		destfile : "${anteater.output}/" + generatedFileName + ".tar",
		compression: "gzip"
	});

});