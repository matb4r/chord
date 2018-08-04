package example.chord1;

import peersim.core.Node;

import java.math.BigInteger;

public class LookUpMessage implements ChordMessage {

	private Node sender;

	private BigInteger targetId;

	private int hopCounter = -1;

	public LookUpMessage(Node sender, BigInteger targetId) {
		this.sender = sender;
		this.targetId = targetId;
	}

	public void increaseHopCounter() {
		hopCounter++;
	}

	/**
	 * @return the senderId
	 */
	public Node getSender() {
		return sender;
	}

	/**
	 * @return the target
	 */
	public BigInteger getTarget() {
		return targetId;
	}

	/**
	 * @return the hopCounter
	 */
	public int getHopCounter() {
		return hopCounter;
	}

}
