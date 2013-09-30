ant.copy({
	todir : 'src/test/resources/out/',
	fileset : {
		dir : 'src/test/resources/in/',
		include : {
			name : "**/nestedWithChild.txt"
		}
	},
	filterset : {
		filter : {
			token : 'TITLE',
			value : 'foo bar'
		}
	}
});
