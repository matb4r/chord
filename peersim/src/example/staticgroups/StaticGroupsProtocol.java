package example.staticgroups;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;

import java.math.BigDecimal;
import java.math.BigInteger;

public class StaticGroupsProtocol implements CDProtocol {

    public String ip;
    public Group group;

    public Finger[] fingerTable;
    public Group successor;
    public Group predecessor;

    public int next;
    public int m;
    public int MAX_GROUP_SIZE;

    public static int pid;

    private static final String PAR_PROT = "protocol";
    private static final String PAR_IDLENGTH = "idLength";
    private static final String PAR_MAX_GROUP_SIZE = "maxGroupSize";
    private static final String PAR_STABILITY_RESTRICTION = "stabilityRestriction";

    private int idLength = 0;
    private int maxGroupSize = 0;
    private double stabilityRestriction = 0;

    public StaticGroupsProtocol(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        idLength = Configuration.getInt(prefix + "." + PAR_IDLENGTH);
        maxGroupSize = Configuration.getInt(prefix + "." + PAR_MAX_GROUP_SIZE);
        stabilityRestriction = Configuration.getDouble(prefix + "." + PAR_STABILITY_RESTRICTION);
    }

    public void start(StaticGroupsProtocol nodeInRing) {
        next = 0;
        m = idLength;
        MAX_GROUP_SIZE = maxGroupSize;
        fingerTable = new Finger[m];
        group = new Group();

        if (nodeInRing == null) {
            create();
        } else {
            float stability = calculateStability();
            if (stability >= stabilityRestriction) {
                join();
            } else {
                Group g = nodeInRing.findGroupToJoin(stability);
                if (g == null) {
                    join();
                } else {
                    joinToGroup(g);
                }
            }
        }
        System.out.println("Node " + ip + " added");
        Utils.addNode(this);
    }

    public void create() {
        group.no = Utils.generateUniqueNo(idLength);
        ip = Utils.generateIp(group.no, m);
        group.ips.add(ip);
        successor = group;
        predecessor = group;
        for (int j = 1; j <= m; j++) {
            fingerTable[j - 1] = new Finger();
            fingerTable[j - 1].i = j;
            fingerTable[j - 1].start = (group.no.add(BigDecimal.valueOf(Math.pow(2, j - 1)).toBigInteger()).mod(BigDecimal.valueOf(Math.pow(2, m)).toBigInteger()));
            fingerTable[j - 1].end = (group.no.add(BigDecimal.valueOf(Math.pow(2, j)).toBigInteger().subtract(BigInteger.ONE)).mod(BigDecimal.valueOf(Math.pow(2, m)).toBigInteger()));
            fingerTable[j - 1].group = group;
        }
    }

    public void join(Node n) {
        System.out.println("cp join");
    }

    public void join() {
        group.no = Utils.generateUniqueNo(idLength);
        ip = Utils.generateIp(group.no, m);
        group.ips.add(ip);
        initFingerTable();
    }

    public void joinToGroup(Group g) {
        group = g;
        ip = Utils.generateIp(g.no, m);
        g.ips.add(ip);
        Utils.updateIps(g.no, g.ips);
        StaticGroupsProtocol firstCPByNo = Utils.getFirstNodeByNo(g.no);
        fingerTable = firstCPByNo.fingerTable;
        predecessor = firstCPByNo.predecessor;
        successor = firstCPByNo.successor;
    }

    public void initFingerTable() {
        StaticGroupsProtocol randomCP = Utils.getRandomCP(this);

        // update newNode.succ
        successor = randomCP.findSuccessor(group.no);
        Utils.updateSuccessor(group.no, successor);

        // update newNode.pred
        StaticGroupsProtocol succCP = Utils.getFirstNodeByNo(successor.no);
        predecessor = succCP.predecessor;
        Utils.updatePredecessor(group.no, predecessor);

        // update newNode.pred.succ
        if (predecessor != null) {
            StaticGroupsProtocol predCP = Utils.getFirstNodeByNo(predecessor.no);
            Utils.updateSuccessor(predCP.group.no, group);
        }

        // update newNode.succ.pred
        Utils.updatePredecessor(succCP.group.no, group);

        fingerTable[0] = new Finger();
        fingerTable[0].i = 1;
        fingerTable[0].start = (group.no.add(BigDecimal.valueOf(Math.pow(2, 0)).toBigInteger()).mod(BigDecimal.valueOf(Math.pow(2, m)).toBigInteger()));
        fingerTable[0].end = (group.no.add(BigDecimal.valueOf(Math.pow(2, 1)).toBigInteger().subtract(BigInteger.ONE)).mod(BigDecimal.valueOf(Math.pow(2, m)).toBigInteger()));
        fingerTable[0].group = successor;

        for (int i = 2; i <= m; i++) {
            fingerTable[i - 1] = new Finger();
            fingerTable[i - 1].i = i;
            fingerTable[i - 1].start = (group.no.add(BigDecimal.valueOf(Math.pow(2, i - 1)).toBigInteger()).mod(BigDecimal.valueOf(Math.pow(2, m)).toBigInteger()));
            fingerTable[i - 1].end = (group.no.add(BigDecimal.valueOf(Math.pow(2, i)).toBigInteger().subtract(BigInteger.ONE)).mod(BigDecimal.valueOf(Math.pow(2, m)).toBigInteger()));
            if (Utils.inAB(fingerTable[i - 1].start, group.no, fingerTable[i - 2].group.no)) {
                fingerTable[i - 1].group = fingerTable[i - 2].group;
            } else {
                fingerTable[i - 1].group = randomCP.findSuccessor(fingerTable[i - 1].start);
            }
        }
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
            StaticGroupsProtocol firstCPByNo = Utils.getFirstNodeByNo(g.no);
            if (firstCPByNo == null) {
                return group;
            } else {
                return firstCPByNo.findSuccessor(id);
            }
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
        Group smallestGroup = Utils.getFirstNodeByNo(fingerTable[0].group.no).smallestGroupFromFingerTable();
        for (int i = 1; i < m; i++) {
            Group g = Utils.getFirstNodeByNo(fingerTable[i].group.no).smallestGroupFromFingerTable();
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
        StaticGroupsProtocol firstCPByNo = Utils.getFirstNodeByNo(successor.no);
        Group p = firstCPByNo.predecessor;
        if (Utils.betweenAB(p.no, group.no, successor.no)) {
            Utils.updateSuccessor(group.no, p);
        }
        Utils.getFirstNodeByNo(successor.no).notify(group);
    }

    public void notify(Group g) {
        if (predecessor == null || Utils.betweenAB(g.no, predecessor.no, group.no)) {
            Utils.updatePredecessor(group.no, g);
        }
    }

    public void fixFingers() {
        if (next >= m) {
            next = 0;
        }
        fingerTable[next].group = findSuccessor(fingerTable[next].start);
        Utils.updateFingerTable(group.no, fingerTable);
        next++;
    }

    public void checkSuccessor() {
        if (Utils.getFirstNodeByNo(successor.no) == null) {
            StaticGroupsProtocol randomCP = Utils.getRandomCP(this);
            if (randomCP == null) {
                // jesli ta grupa jest jedyna w sieci
                successor = group;
            } else {
                successor = randomCP.findSuccessor(group.no);
            }
            Utils.updateSuccessor(group.no, successor);
            Utils.updatePredecessor(successor.no, group);
        }
    }

    public void checkPredecessor() {
        if (predecessor == null || Utils.getFirstNodeByNo(predecessor.no) == null) {
            Utils.updatePredecessor(group.no, null);
        }
    }

    @Override
    public void nextCycle(Node node, int protocolID) {

    }

    @Override
    public Object clone() {
        StaticGroupsProtocol cp = null;
        try {
            cp = (StaticGroupsProtocol) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return cp;
    }

}
