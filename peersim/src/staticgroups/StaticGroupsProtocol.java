package staticgroups;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;

import java.math.BigDecimal;
import java.math.BigInteger;

public class StaticGroupsProtocol implements CDProtocol {

    private static final String PAR_PROT = "protocol";
    private static final String PAR_M = "M";
    private static final String PAR_MAX_GROUP_SIZE = "MAX_GROUP_SIZE";
    private static final String PAR_STABILITY_RESTRICTION = "STABILITY_RESTRICTION";

    public static int pid;
    public static int M = 0;
    public static int MAX_GROUP_SIZE = 0;
    public static double stabilityRestriction = 0;

    public String ip;
    public Group group;

    public Finger[] fingerTable;
    public Group successor;
    public Group predecessor;

    public int next;

    public StaticGroupsProtocol(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        M = Configuration.getInt(prefix + "." + PAR_M);
        MAX_GROUP_SIZE = Configuration.getInt(prefix + "." + PAR_MAX_GROUP_SIZE);
        stabilityRestriction = Configuration.getDouble(prefix + "." + PAR_STABILITY_RESTRICTION);
    }

    public void start(StaticGroupsProtocol nodeInRing) {
        if (Utils.GROUPS.keySet().size() >= Math.pow(2, M)) {
            throw new RuntimeException("Too many nodes: M exceeded!");
        }

        next = 0;
        fingerTable = new Finger[M];
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
        Utils.addNode(this);
        System.out.println("Node " + ip + " added");
    }

    public void create() {
        group.no = Utils.generateUniqueNo(M);
        ip = Utils.generateIp(group.no, M);
        group.ips.add(ip);
        successor = group;
        predecessor = group;
        for (int j = 1; j <= M; j++) {
            fingerTable[j - 1] = new Finger();
            fingerTable[j - 1].i = j;
            fingerTable[j - 1].start = (group.no.add(BigDecimal.valueOf(Math.pow(2, j - 1)).toBigInteger()).mod(BigDecimal.valueOf(Math.pow(2, M)).toBigInteger()));
            fingerTable[j - 1].end = (group.no.add(BigDecimal.valueOf(Math.pow(2, j)).toBigInteger().subtract(BigInteger.ONE)).mod(BigDecimal.valueOf(Math.pow(2, M)).toBigInteger()));
            fingerTable[j - 1].group = group;
        }
    }

    public void join() {
        group.no = Utils.generateUniqueNo(M);
        ip = Utils.generateIp(group.no, M);
        group.ips.add(ip);
        initFingerTable();
    }

    public void joinToGroup(Group g) {
        group = g;
        ip = Utils.generateIp(g.no, M);
        g.ips.add(ip);
        Utils.updateIps(g.no, g.ips);
        StaticGroupsProtocol firstNodeByNo = Utils.getFirstNodeByNo(g.no);
        fingerTable = firstNodeByNo.fingerTable;
        predecessor = firstNodeByNo.predecessor;
        successor = firstNodeByNo.successor;
    }

    public void initFingerTable() {
        StaticGroupsProtocol randomNode = Utils.getRandomNode(this);

        // update newNode.succ
        successor = randomNode.findSuccessor(group.no);
        Utils.updateSuccessor(group.no, successor);

        // update newNode.pred
        StaticGroupsProtocol succNode = Utils.getFirstNodeByNo(successor.no);
        predecessor = succNode.predecessor;
        Utils.updatePredecessor(group.no, predecessor);

        // update newNode.pred.succ
        if (predecessor != null) {
            StaticGroupsProtocol predNode = Utils.getFirstNodeByNo(predecessor.no);
            Utils.updateSuccessor(predNode.group.no, group);
        }

        // update newNode.succ.pred
        Utils.updatePredecessor(succNode.group.no, group);

        fingerTable[0] = new Finger();
        fingerTable[0].i = 1;
        fingerTable[0].start = (group.no.add(BigDecimal.valueOf(Math.pow(2, 0)).toBigInteger()).mod(BigDecimal.valueOf(Math.pow(2, M)).toBigInteger()));
        fingerTable[0].end = (group.no.add(BigDecimal.valueOf(Math.pow(2, 1)).toBigInteger().subtract(BigInteger.ONE)).mod(BigDecimal.valueOf(Math.pow(2, M)).toBigInteger()));
        fingerTable[0].group = successor;

        for (int i = 2; i <= M; i++) {
            fingerTable[i - 1] = new Finger();
            fingerTable[i - 1].i = i;
            fingerTable[i - 1].start = (group.no.add(BigDecimal.valueOf(Math.pow(2, i - 1)).toBigInteger()).mod(BigDecimal.valueOf(Math.pow(2, M)).toBigInteger()));
            fingerTable[i - 1].end = (group.no.add(BigDecimal.valueOf(Math.pow(2, i)).toBigInteger().subtract(BigInteger.ONE)).mod(BigDecimal.valueOf(Math.pow(2, M)).toBigInteger()));
            if (Utils.inAB(fingerTable[i - 1].start, group.no, fingerTable[i - 2].group.no)) {
                fingerTable[i - 1].group = fingerTable[i - 2].group;
            } else {
                fingerTable[i - 1].group = randomNode.findSuccessor(fingerTable[i - 1].start);
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
            StaticGroupsProtocol firstNodeByNo = Utils.getFirstNodeByNo(g.no);
            if (firstNodeByNo == null) {
                return group;
            } else {
                return firstNodeByNo.findSuccessor(id);
            }
        }
    }

    public Group closestPrecedingNode(BigInteger id) {
        for (int i = M - 1; i >= 0; i--) {
            if (Utils.betweenAB(fingerTable[i].group.no, group.no, id)) {
                return fingerTable[i].group;
            }
        }
        return group;
    }

    public Group findGroupToJoin(float stability) {
        Group smallestGroup = Utils.getFirstNodeByNo(fingerTable[0].group.no).smallestGroupFromFingerTable();
        for (int i = 1; i < M; i++) {
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
        for (int i = 1; i < M; i++) {
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
        StaticGroupsProtocol firstNodeByNo = Utils.getFirstNodeByNo(successor.no);
        Group p = firstNodeByNo.predecessor;
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
        if (next >= M) {
            next = 0;
        }
        fingerTable[next].group = findSuccessor(fingerTable[next].start);
        Utils.updateFingerTable(group.no, fingerTable);
        next++;
    }

    public void checkSuccessor() {
        if (Utils.getFirstNodeByNo(successor.no) == null) {
            StaticGroupsProtocol randomNode = Utils.getRandomNode(this);
            if (randomNode == null) {
                // jesli ta grupa jest jedyna w sieci
                successor = group;
            } else {
                successor = randomNode.findSuccessor(group.no);
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
        StaticGroupsProtocol node = null;
        try {
            node = (StaticGroupsProtocol) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return node;
    }

}
