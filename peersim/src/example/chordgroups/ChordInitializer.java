package example.chordgroups;

import peersim.config.Configuration;
import peersim.core.Node;
import peersim.dynamics.NodeInitializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

public class ChordInitializer implements NodeInitializer {

    private static final String PAR_PROT = "protocol";
    private static final String PAR_IDLENGTH = "idLength";
    private static final String PAR_MAX_GROUP_SIZE = "maxGroupSize";

    private int pid = 0;
    private int idLength = 0;
    private int maxGroupSize = 0;

    private ChordProtocol cp;

    public ChordInitializer(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        idLength = Configuration.getInt(prefix + "." + PAR_IDLENGTH);
        maxGroupSize = Configuration.getInt(prefix + "." + PAR_MAX_GROUP_SIZE);
    }

    @Override
    public void initialize(Node n) {
        System.out.println("executing ChordInitializer");
        cp = (ChordProtocol) n.getProtocol(pid);
        cp.next = 0;
        cp.pid = pid;
        cp.m = idLength;
        cp.MAX_GROUP_SIZE = maxGroupSize;
        cp.fingerTable = new Finger[cp.m];
        cp.group = new Group();


        float stability = cp.calculateStability();
        if (stability >= 0.5) {
            join(cp);
        } else {
            Group g = Utils.getAnyCP(pid).findGroupToJoin(stability);
            if (g == null) {
                join(cp);
            } else {
                joinToGroup(cp, g);
            }
        }
        ArrayList<ChordProtocol> allNodes = Utils.getAllNodes(pid);
        System.out.println("Node " + cp.ip + " added");
    }

    public void join(ChordProtocol cp) {
        cp.group.no = Utils.generateUniqueId(idLength, pid);
        cp.ip = Utils.generateIp(cp.group.no, cp.m);
        cp.group.ips.add(cp.ip);
        initFingerTable(cp);
    }

    public void joinToGroup(ChordProtocol cp, Group g) {
        cp.group = g;
        cp.ip = Utils.generateIp(g.no, cp.m);
        g.ips.add(cp.ip);
        Utils.updateIps(g.no, g.ips, pid);
        ChordProtocol firstCPByNo = Utils.getFirstCPByNo(g.no, pid);
        cp.fingerTable = firstCPByNo.fingerTable;
        cp.predecessor = firstCPByNo.predecessor;
        cp.successor = firstCPByNo.successor;
    }

    public void initFingerTable(ChordProtocol cp) {
        ChordProtocol randomCP = Utils.getAnyCP(pid);
        // update newNode.succ
        cp.successor = randomCP.findSuccessor(cp.group.no);
        Utils.updateSuccessor(cp.group.no, cp.successor, pid);

        // update newNode.pred
        ChordProtocol succCP = Utils.getFirstCPByNo(cp.successor.no, pid);
        cp.predecessor = succCP.predecessor;
        Utils.updatePredecessor(cp.group.no, cp.predecessor, pid);

        // update newNode.pred.succ
        if (cp.predecessor != null) {
            ChordProtocol predCP = Utils.getFirstCPByNo(cp.predecessor.no, pid);
            Utils.updateSuccessor(predCP.group.no, cp.group, pid);
        }

        // update newNode.succ.pred
        Utils.updatePredecessor(succCP.group.no, cp.group, pid);

        cp.fingerTable[0] = new Finger();
        cp.fingerTable[0].i = 1;
        cp.fingerTable[0].start = (cp.group.no.add(BigDecimal.valueOf(Math.pow(2, 0)).toBigInteger()).mod(BigDecimal.valueOf(Math.pow(2, cp.m)).toBigInteger()));
        cp.fingerTable[0].end = (cp.group.no.add(BigDecimal.valueOf(Math.pow(2, 1)).toBigInteger().subtract(BigInteger.ONE)).mod(BigDecimal.valueOf(Math.pow(2, cp.m)).toBigInteger()));
        cp.fingerTable[0].group = cp.successor;

        for (int i = 2; i <= cp.m; i++) {
            cp.fingerTable[i - 1] = new Finger();
            cp.fingerTable[i - 1].i = i;
            cp.fingerTable[i - 1].start = (cp.group.no.add(BigDecimal.valueOf(Math.pow(2, i - 1)).toBigInteger()).mod(BigDecimal.valueOf(Math.pow(2, cp.m)).toBigInteger()));
            cp.fingerTable[i - 1].end = (cp.group.no.add(BigDecimal.valueOf(Math.pow(2, i)).toBigInteger().subtract(BigInteger.ONE)).mod(BigDecimal.valueOf(Math.pow(2, cp.m)).toBigInteger()));
            if (Utils.inAB(cp.fingerTable[i - 1].start, cp.group.no, cp.fingerTable[i - 2].group.no)) {
                cp.fingerTable[i - 1].group = cp.fingerTable[i - 2].group;
            } else {
                cp.fingerTable[i - 1].group = randomCP.findSuccessor(cp.fingerTable[i - 1].start);
            }
        }
    }
}
