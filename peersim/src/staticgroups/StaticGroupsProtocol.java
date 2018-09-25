package staticgroups;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;

import static staticgroups.Utils.*;

public class StaticGroupsProtocol implements CDProtocol {

    private static final String PAR_DEBUG = "DEBUG";
    private static final String PAR_PROT = "protocol";
    private static final String PAR_M = "M";
    private static final String PAR_MAX_GROUP_SIZE = "MAX_GROUP_SIZE";
    private static final String PAR_STABILITY_REQUIREMENT = "STABILITY_REQUIREMENT";
    private static final String PAR_UPDATE_WHOLE_GROUP = "UPDATE_WHOLE_GROUP";

    public static boolean DEBUG = false;
    public static int pid;
    public static int M = 0;
    public static int MAX_GROUP_SIZE = 0;
    public static double STABILITY_REQUIREMENT = 0;
    public static boolean UPDATE_WHOLE_GROUP = false;

    public double stability;

    public String address;
    public Group group;

    public Finger[] fingerTable;
    public Group successor;
    public Group predecessor;

    public int next;

    public StaticGroupsProtocol(String prefix) {
        DEBUG = Configuration.contains(prefix + "." + PAR_DEBUG);
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        M = Configuration.getInt(prefix + "." + PAR_M);
        MAX_GROUP_SIZE = Configuration.getInt(prefix + "." + PAR_MAX_GROUP_SIZE);
        STABILITY_REQUIREMENT = Configuration.getDouble(prefix + "." + PAR_STABILITY_REQUIREMENT);
        UPDATE_WHOLE_GROUP = Configuration.contains(prefix + "." + PAR_UPDATE_WHOLE_GROUP);
    }

    public void start(StaticGroupsProtocol nodeInRing) {
        if (GROUPS.keySet().size() >= Math.pow(2, M)) {
            throw new RuntimeException("Too many nodes: M exceeded!");
        }

        next = 0;
        fingerTable = new Finger[M];
        group = new Group();
        stability = calculateStability();

        if (nodeInRing == null) {
            create();
        } else {
            Group g = nodeInRing.findGroupToJoin();
            if (g == null || (stability >= STABILITY_REQUIREMENT && g.addresses.size() >= (MAX_GROUP_SIZE / 2))) {
                join();
            } else {
                joinToGroup(g);
            }

        }
        addNode(this);
        if (StaticGroupsProtocol.DEBUG) System.out.println("Node " + address + " added");
    }

    public void create() {
        group.id = generateUniqueId(M);
        address = generateUniqueAddress(group.id, M);
        group.addresses.add(address);
        successor = group;
        predecessor = group;
        for (int j = 1; j <= M; j++) {
            fingerTable[j - 1] = new Finger();
            fingerTable[j - 1].i = j;
            fingerTable[j - 1].start = (group.id.add(BigDecimal.valueOf(Math.pow(2, j - 1)).toBigInteger()).mod(BigDecimal.valueOf(Math.pow(2, M)).toBigInteger()));
            fingerTable[j - 1].end = (group.id.add(BigDecimal.valueOf(Math.pow(2, j)).toBigInteger().subtract(BigInteger.ONE)).mod(BigDecimal.valueOf(Math.pow(2, M)).toBigInteger()));
            fingerTable[j - 1].group = group;
        }
    }

    public float calculateStability() {
        return CommonState.r.nextFloat();
    }

    public Group findGroupToJoin() {
        StaticGroupsProtocol firstNodeById = getFirstNodeById(fingerTable[0].group.id);
        if (firstNodeById == null) {
            return null;
        }
        Group smallestGroup = firstNodeById.smallestGroupFromFingerTable();
        for (int i = 1; i < M; i++) {
            Group g = getFirstNodeById(fingerTable[i].group.id).smallestGroupFromFingerTable();
            if (g.addresses.size() < smallestGroup.addresses.size()) {
                smallestGroup = g;
            }
        }
        if (smallestGroup.addresses.size() == MAX_GROUP_SIZE) {
            return null;
        } else {
            return smallestGroup;
        }
    }

    public Group smallestGroupFromFingerTable() {
        Group smallestGroup = fingerTable[0].group;
        for (int i = 1; i < M; i++) {
            if (fingerTable[i].group.addresses.size() < smallestGroup.addresses.size()) {
                smallestGroup = fingerTable[i].group;
            }
        }
        return smallestGroup;
    }

    public void join() {
        group.id = generateUniqueId(M);
        address = generateUniqueAddress(group.id, M);
        group.addresses.add(address);
        initFingerTable();
    }

    public void joinToGroup(Group g) {
        group = g;
        address = generateUniqueAddress(g.id, M);
        g.addresses.add(address);
        updateAddresses(g.id, g.addresses);
        StaticGroupsProtocol firstNodeById = getFirstNodeById(g.id);
        fingerTable = firstNodeById.fingerTable;
        predecessor = firstNodeById.predecessor;
        successor = firstNodeById.successor;
    }

    public void initFingerTable() {
        StaticGroupsProtocol randomNode = getRandomNode(this);

        successor = randomNode.findSuccessor(group.id);

        StaticGroupsProtocol succNode = getFirstNodeById(successor.id);
        predecessor = succNode.predecessor;

        // update newNode.pred.succ
        if (predecessor != null) {
            StaticGroupsProtocol predNode = getFirstNodeById(predecessor.id);
            updateSuccessor(predNode.group.id, group);
        }

        // update newNode.succ.pred
        updatePredecessor(succNode.group.id, group);

        fingerTable[0] = new Finger();
        fingerTable[0].i = 1;
        fingerTable[0].start = (group.id.add(BigDecimal.valueOf(Math.pow(2, 0)).toBigInteger()).mod(BigDecimal.valueOf(Math.pow(2, M)).toBigInteger()));
        fingerTable[0].end = (group.id.add(BigDecimal.valueOf(Math.pow(2, 1)).toBigInteger().subtract(BigInteger.ONE)).mod(BigDecimal.valueOf(Math.pow(2, M)).toBigInteger()));
        fingerTable[0].group = successor;

        for (int i = 2; i <= M; i++) {
            fingerTable[i - 1] = new Finger();
            fingerTable[i - 1].i = i;
            fingerTable[i - 1].start = (group.id.add(BigDecimal.valueOf(Math.pow(2, i - 1)).toBigInteger()).mod(BigDecimal.valueOf(Math.pow(2, M)).toBigInteger()));
            fingerTable[i - 1].end = (group.id.add(BigDecimal.valueOf(Math.pow(2, i)).toBigInteger().subtract(BigInteger.ONE)).mod(BigDecimal.valueOf(Math.pow(2, M)).toBigInteger()));
            if (inAB(fingerTable[i - 1].start, group.id, fingerTable[i - 2].group.id)) {
                fingerTable[i - 1].group = fingerTable[i - 2].group;
            } else {
                fingerTable[i - 1].group = randomNode.findSuccessor(fingerTable[i - 1].start);
            }
        }
    }

    public Group findSuccessor(BigInteger id) {
        if (inAB(id, group.id, successor.id)) {
            return successor;
        } else {
            Group g = closestPrecedingNode(id);

            // because of recursive calls and stackoverflow...
            if (g.id.equals(group.id)) {
                return group;
            }
            StaticGroupsProtocol firstNodeById = getFirstNodeById(g.id);
            if (firstNodeById == null) {
                return group;
            } else {
                return firstNodeById.findSuccessor(id);
            }
        }
    }

    public Group closestPrecedingNode(BigInteger id) {
        for (int i = M - 1; i >= 0; i--) {
            if (betweenAB(fingerTable[i].group.id, group.id, id)) {
                return fingerTable[i].group;
            }
        }
        return group;
    }

    public void stabilize() {
        StaticGroupsProtocol successorNode = getFirstNodeById(successor.id);
        Group p = successorNode.predecessor;
        if (p != null && betweenAB(p.id, group.id, successor.id)) {
            successor = p;
            if (UPDATE_WHOLE_GROUP) updateSuccessor(group.id, p);
        }
        getFirstNodeById(successor.id).notify(group);
    }

    public void notify(Group g) {
        if (predecessor == null || betweenAB(g.id, predecessor.id, group.id)) {
            predecessor = g;
            if (UPDATE_WHOLE_GROUP) updatePredecessor(group.id, g);
        }
    }

    public void fixFingers() {
        if (next >= M) {
            next = 0;
        }
        fingerTable[next].group = findSuccessor(fingerTable[next].start);
        if (UPDATE_WHOLE_GROUP) updateFingerTable(group.id, fingerTable);
        next++;
    }

    public void checkGroup() {
        Iterator<String> it = group.addresses.iterator();
        while (it.hasNext()) {
            if (getNodeByGroupIdAndIp(group.id, it.next()) == null) {
                it.remove();
            }
        }
        if (UPDATE_WHOLE_GROUP) updateAddresses(group.id, group.addresses);
    }

    public void checkSuccessor() {
        if (getFirstNodeById(successor.id) == null) {
            if (Utils.isOnlyOneGroupInNetwork()) {
                successor = group;
            } else {
                StaticGroupsProtocol randomNode = getRandomNode(this);
                successor = randomNode.findSuccessor(group.id);
            }
            if (UPDATE_WHOLE_GROUP) updateSuccessor(group.id, successor);
            updatePredecessor(successor.id, group);
        }
    }

    public void checkPredecessor() {
        if (predecessor == null || getFirstNodeById(predecessor.id) == null) {
            predecessor = null;
            if (UPDATE_WHOLE_GROUP) updatePredecessor(group.id, null);
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
