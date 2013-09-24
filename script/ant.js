function MissingMethodClass() {}

function antNoMethod(name, args) {
	return executeAnt(name, args);
}

function shellNoMethod(name, args) {
	return shellExec(name, args);
}

ant = new MissingMethodClass();
ant.__noSuchMethod__ = antNoMethod;

shell = new MissingMethodClass();
shell.__noSuchMethod__ = shellNoMethod;