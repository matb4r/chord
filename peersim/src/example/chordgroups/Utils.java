package example.chordgroups;

import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;

import java.math.BigInteger;
import java.util.ArrayList;

public class Utils {

    public static String generateIp(BigInteger groupNo, int m) {
        return "0.0." + groupNo + "." + new BigInteger(m, CommonState.r);
    }

    public static ArrayList<ChordProtocol> getAllNodes(int pid) {
        ArrayList<ChordProtocol> nodes = new ArrayList<>();
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            if (node != null && node.isUp()) {
                ChordProtocol cp = (ChordProtocol) node.getProtocol(pid);
                nodes.add(cp);
            }
        }
        return nodes;
    }

    public static ChordProtocol getRandomCP(int pid) {
        Node n;
        do {
            n = Network.get(CommonState.r.nextInt(Network.size()));
        } while (n == null || n.isUp() == false);
        return ((ChordProtocol) n.getProtocol(pid));
    }

    public static ArrayList<ChordProtocol> getCPsByNo(BigInteger no, int pid) {
        ArrayList<ChordProtocol> nodes = new ArrayList<>();
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            if (node != null && node.isUp()) { // todo mb look here
                ChordProtocol cp = (ChordProtocol) node.getProtocol(pid);
                if (cp.group.no.equals(no)) {
                    nodes.add(cp);
                }
            }
        }
        return nodes;
    }

    public static ChordProtocol getFirstCPByNo(BigInteger no, int pid) {
        ArrayList<ChordProtocol> nodes = getCPsByNo(no, pid);
        if (nodes.size() == 0) {
            return null;
        } else {
            return nodes.get(0);
        }
    }

    public static void updateIps(BigInteger n, ArrayList<String> ips, int pid) {
        ArrayList<ChordProtocol> allNodes = getAllNodes(pid);
        for (ChordProtocol cp : allNodes) {
            if (cp.group.no.equals(n)) {
                cp.group.ips = ips;
            }
        }
    }

    public static void updateSuccessor(BigInteger n, Group successor, int pid) {
        ArrayList<ChordProtocol> allNodes = getAllNodes(pid);
        for (ChordProtocol cp : allNodes) {
            if (cp.group.no.equals(n)) {
                cp.successor = successor;
            }
        }
    }

    public static void updatePredecessor(BigInteger n, Group predecessor, int pid) {
        ArrayList<ChordProtocol> allNodes = getAllNodes(pid);
        for (ChordProtocol cp : allNodes) {
            if (cp.group.no.equals(n)) {
                cp.predecessor = predecessor;
            }
        }
    }

    public static void updateFingerTable(BigInteger n, Finger[] fingerTable, int pid) {
        ArrayList<ChordProtocol> allNodes = getAllNodes(pid);
        for (ChordProtocol cp : allNodes) {
            if (cp.group.no.equals(n)) {
                cp.fingerTable = fingerTable;
            }
        }
    }

    public static boolean inAB(BigInteger bid, BigInteger ba, BigInteger bb) {
        long id = bid.longValue();
        long a = ba.longValue();
        long b = bb.longValue();

        if (id == a || id == b)
            return true;
        if (id > a && id < b)
            return true;
        if (id < a && a > b && id < b)
            return true;
        if (id > b && a > b && id > a)
            return true;

        return false;
    }

    public static boolean betweenAB(BigInteger bid, BigInteger ba, BigInteger bb) {
        long id = bid.longValue();
        long a = ba.longValue();
        long b = bb.longValue();

        if (id == a || id == b)
            return false;
        if (id > a && id < b)
            return true;
        if (id < a && a > b && id < b)
            return true;
        if (id > b && a > b && id > a)
            return true;

        return false;
    }

    public static BigInteger generateUniqueId(int idLength, int pid) {
        ArrayList<ChordProtocol> allNodes = getAllNodes(pid);
        BigInteger no;
        do {
            no = new BigInteger(idLength, CommonState.r);
        } while (isNoInList(no, allNodes));
        return no;
    }

    private static boolean isNoInList(BigInteger no, ArrayList<ChordProtocol> list) {
        for(ChordProtocol cp : list) {
            if (cp.group != null && cp.group.no != null && cp.group.no.equals(no)) {
                return true;
            }
        }
        return false;
    }
}
