function Ant() {

}

function noMethod(name, args) {
	return executeAnt(name, args);
}

ant = new Ant();
ant.__noSuchMethod__ = noMethod;