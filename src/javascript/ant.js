function Ant() {

}

function noMethod(name, args) {
	executeAnt(name, args);
}

ant = new Ant();
ant.__noSuchMethod__ = noMethod;