package example.chordgroups;

import peersim.cdsim.CDProtocol;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;

import java.math.BigInteger;

public class ChordProtocol implements CDProtocol {

    public String ip;
    public Group group;

    public Finger[] fingerTable;
    public Group successor;
    public Group predecessor;

    public int next;
    public int m;
    public int MAX_GROUP_SIZE;

    public int pid;

    public ChordProtocol(String prefix) {
    }

    public void join(Node n) {
        System.out.println("cp join");
    }

    public Group findSuccessor(BigInteger id) {
        if (Utils.inAB(id, group.no, successor.no)) {
            return successor;
        } else {
            Group g = closestPrecedingNode(id);

            // because of recursive calls and stackoverflow...
            if (g.no.equals(group.no)) {
                return group;
            }

            return Utils.getFirstCPByNo(g.no, pid).findSuccessor(id);
        }
    }

    public Group closestPrecedingNode(BigInteger id) {
        for (int i = m - 1; i >= 0; i--) {
//            if (group.no == id) {
//                return this.predecessor;
//            }
//            else
            if (Utils.betweenAB(fingerTable[i].group.no, group.no, id)) {
                return fingerTable[i].group;
            }
        }
        return group;
//        return this.predecessor;
    }

    public Group findGroupToJoin(float stability) {
        Group smallestGroup = Utils.getFirstCPByNo(fingerTable[0].group.no, pid).smallestGroupFromFingerTable();
        for (int i = 1; i < m; i++) {
            Group g = smallestGroupFromFingerTable();
            if (g.ips.size() < smallestGroup.ips.size()) {
                smallestGroup = g;
            }
        }
        if (smallestGroup.ips.size() == MAX_GROUP_SIZE) {
            return null;
        } else {
            return smallestGroup;
        }
    }

    public Group smallestGroupFromFingerTable() {
        Group smallestGroup = fingerTable[0].group;
        for (int i = 1; i < m; i++) {
            if (fingerTable[i].group.ips.size() < smallestGroup.ips.size()) {
                smallestGroup = fingerTable[i].group;
            }
        }
        return smallestGroup;
    }

    public float calculateStability() {
        return CommonState.r.nextFloat();
    }

    public void stabilize() {
        Group p = Utils.getFirstCPByNo(successor.no, pid).predecessor;
        if (Utils.betweenAB(p.no, group.no, successor.no)) {
            Utils.updateSuccessor(group.no, p, pid);
        }
        Utils.getFirstCPByNo(successor.no, pid).notify(group);
    }

    public void notify(Group g) {
        if (predecessor == null || Utils.betweenAB(g.no, predecessor.no, group.no)) {
            Utils.updatePredecessor(group.no, g, pid);
        }
    }

    public void fixFingers() {
        if (next >= m) {
            next = 0;
        }
        fingerTable[next].group = findSuccessor(fingerTable[next].start);
        Utils.updateFingerTable(group.no, fingerTable, pid);
        next++;
    }

//    public void checkSuccessor() {
//        if (successor == null || Utils.getFirstCPByNo(successor.no, pid) == null) {
////        ChordProtocol succCP = Utils.getFirstCPByNo(successor.no, pid);
////        if (succCP == null) {
//            successor = null;
//            Utils.updateSuccessor(group.no, null, pid);
//            successor = Utils.getRandomCP(pid).findSuccessor(group.no);
//            ChordProtocol p = Utils.getFirstCPByNo(successor.no, pid);
//            p.predecessor = group;
//            Utils.updatePredecessor(p.group.no, p.predecessor, pid);
//        }
//    }

    @Override
    public void nextCycle(Node node, int protocolID) {

    }

    @Override
    public Object clone() {
        ChordProtocol cp = null;
        try {
            cp = (ChordProtocol) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return cp;
    }

}
