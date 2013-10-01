/*
 * 
 *  
 *  <copy todir="src/test/resources/out/">
 * 		<fileset>
 * 			<include name="**file.txt"/>
 *      	<include name="**file.txt"/>
 * 		</fileset>
 *  </copy>
 * 
 */

ant.copy({
	todir : 'src/test/resources/out/',
	fileset : {
		dir : 'src/test/resources/in/',
		include : [ {
			name : "**/file.txt"
		}, {
			name : "**/file1.txt"
		} ]
	}
});
