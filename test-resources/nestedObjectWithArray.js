/*
 * 
 *  
 *  <copy todir="test-resources/out/">
 * 		<fileset>
 * 			<include name="**file.txt"/>
 *      	<include name="**file.txt"/>
 * 		</fileset>
 *  </copy>
 * 
 */

ant.copy({
	todir : 'test-resources/out/',
	fileset : {
		dir : 'test-resources/in/',
		include : [ {
			name : "**/file.txt"
		}, {
			name : "**/file1.txt"
		} ]
	}
});
