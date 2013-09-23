ant.copy({
	todir : 'test-resources/out/',
	fileset : {
		dir : 'test-resources/in/',
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
