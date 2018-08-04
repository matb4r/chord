package example.chord1;

import peersim.core.Node;

import java.math.BigInteger;
import java.util.Comparator;

public class NodeComparator implements Comparator {

	public int pid = 0;

	public NodeComparator(int pid) {
		this.pid = pid;
	}

	public int compare(Object arg0, Object arg1) {
		BigInteger one = ((ChordProtocol) ((Node) arg0).getProtocol(pid)).chordId;
		BigInteger two = ((ChordProtocol) ((Node) arg1).getProtocol(pid)).chordId;
		return one.compareTo(two);
	}

}
