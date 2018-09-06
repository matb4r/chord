package example.staticgroups;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 * info mb
 * co sie dzieje na samiuskim poczatku
 * network.size definiuje ile na poczatku wezlowe tworzonych przez CreateInitialNodes
 */
public class CreateInitialNodes implements Control {

    private static final String PAR_PROT = "protocol";
    private static final String PAR_IDLENGTH = "idLength";
    private static final String PAR_MAX_GROUP_SIZE = "maxGroupSize";
    private static final String PAR_STABILITY_RESTRICTION = "stabilityRestriction";

    private int pid = 0;
    private int idLength = 0;
    private int maxGroupSize = 0;
    private double stabilityRestriction = 0;

    public CreateInitialNodes(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        idLength = Configuration.getInt(prefix + "." + PAR_IDLENGTH);
        maxGroupSize = Configuration.getInt(prefix + "." + PAR_MAX_GROUP_SIZE);
        stabilityRestriction = Configuration.getDouble(prefix + "." + PAR_STABILITY_RESTRICTION);
    }

    @Override
    public boolean execute() {

        // info mb: tworzenie nowych wezlow na samym poczatku: kazdy wezel tworzy nowa grupe
        System.out.println("executing CreateInitialNodes");
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            StaticGroupsProtocol cp = (StaticGroupsProtocol) node.getProtocol(pid);
            cp.next = 0;
            cp.pid = pid;
            cp.m = idLength;
            cp.MAX_GROUP_SIZE = maxGroupSize;
            cp.fingerTable = new Finger[cp.m];
            cp.group = new Group();

            if (i == 0) {
                cp.group.no = Utils.generateUniqueNo(idLength);
                cp.ip = Utils.generateIp(cp.group.no, cp.m);
                cp.group.ips.add(cp.ip);
                cp.successor = cp.group;
                cp.predecessor = cp.group;
                for (int j = 1; j <= cp.m; j++) {
                    cp.fingerTable[j - 1] = new Finger();
                    cp.fingerTable[j - 1].i = j;
                    cp.fingerTable[j - 1].start = (cp.group.no.add(BigDecimal.valueOf(Math.pow(2, j - 1)).toBigInteger()).mod(BigDecimal.valueOf(Math.pow(2, cp.m)).toBigInteger()));
                    cp.fingerTable[j - 1].end = (cp.group.no.add(BigDecimal.valueOf(Math.pow(2, j)).toBigInteger().subtract(BigInteger.ONE)).mod(BigDecimal.valueOf(Math.pow(2, cp.m)).toBigInteger()));
                    cp.fingerTable[j - 1].group = cp.group;
                }

            } else {

                float stability = cp.calculateStability();
                if (stability >= stabilityRestriction) {
                    cp.join(idLength);
                } else {
                    Group g = Utils.getRandomCP(cp, pid).findGroupToJoin(stability);
                    if (g == null) {
                        cp.join(idLength);
                    } else {
                        cp.joinToGroup(g);
                    }
                }
            }
//            cp.group.no = Utils.generateUniqueNo(idLength);
//            cp.ip = Utils.generateIp(cp.group.no, cp.m);
//            cp.group.ips.add(cp.ip);
            Utils.NODES.add(cp);
            ArrayList<StaticGroupsProtocol> group = Utils.GROUPS.get(cp.group.no);
            if (group == null) {
                group = new ArrayList<>();
            }
            group.add(cp);
            Utils.GROUPS.put(cp.group.no, group);


            for (StaticGroupsProtocol p : Utils.NODES) {
                p.checkSuccessor();
                p.checkPredecessor();
                try {
                    p.stabilize();
                } catch (Exception ex) {
                    StaticGroupsMetrics.exceptionsCounter++;
                }
                for (int j = 0; j < p.m; j++)
                    p.fixFingers();
            }
        }


//        Network.sort(new NodeComparator(pid));
//        Utils.NODES.sort(new StaticGroupNodeComparator());

//        setPredecessors();
//        setSuccessors();
//        createFingerTable();
//        addToGroupHash();
        return false;
    }

    public void setPredecessors() {
        for (int i = 0; i < Utils.NODES.size(); i++) {
            StaticGroupsProtocol cp = Utils.NODES.get(i);

            if (i == 0) {
                // info mb: dla pierwszego wezla, poprzednikiem musi byc wezel ostatni
                StaticGroupsProtocol pred = Utils.NODES.get(Utils.NODES.size() - 1);
                cp.predecessor = pred.group;
            } else {
                StaticGroupsProtocol pred = Utils.NODES.get(i - 1);
                cp.predecessor = pred.group;
            }
        }
    }

    public void setSuccessors() {
        for (int i = 0; i < Utils.NODES.size(); i++) {
            StaticGroupsProtocol cp = Utils.NODES.get(i);

            if (i == Utils.NODES.size() - 1) {
                // info mb: dla ostatniego wezla, nastepnikiem musi byc pierwszy wezel
                StaticGroupsProtocol succ = Utils.NODES.get(0);
                cp.successor = succ.group;
            } else {
                StaticGroupsProtocol succ = Utils.NODES.get(i + 1);
                cp.successor = succ.group;
            }
        }
    }

    public void createFingerTable() {
        for (StaticGroupsProtocol cp : Utils.NODES) {

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
                cp.fingerTable[i - 1].group = findSmallestGroupGE(cp.fingerTable[i - 1].start);
            }
        }
    }

    public void addToGroupHash() {
        for (StaticGroupsProtocol cp : Utils.NODES) {
            ArrayList<StaticGroupsProtocol> group = Utils.GROUPS.get(cp.group.no);
            if (group == null) {
                group = new ArrayList<>();
            }
            group.add(cp);
            Utils.GROUPS.put(cp.group.no, group);
        }
    }

    public Group findSmallestGroupGE(BigInteger id) {
        // dziala tylko dlatego, ze wezly sa posortowane po group.no
        for (StaticGroupsProtocol cp : Utils.NODES) {
            if (cp.group.no.compareTo(id) >= 0) {
                return cp.group;
            }
        }
        return Utils.NODES.get(0).group;
    }
}
