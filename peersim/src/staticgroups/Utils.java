package staticgroups;

import peersim.core.CommonState;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Utils {

    public static ArrayList<StaticGroupsProtocol> NODES = new ArrayList<>();
    public static HashMap<BigInteger, ArrayList<StaticGroupsProtocol>> GROUPS = new HashMap<>();

    public static void addNode(StaticGroupsProtocol n) {
        Utils.NODES.add(n);
        ArrayList<StaticGroupsProtocol> g = Utils.GROUPS.get(n.group.no);
        if (g == null) {
            g = new ArrayList<>();
        }
        g.add(n);
        Utils.GROUPS.put(n.group.no, g);
    }

    public static void removeNode(StaticGroupsProtocol n) {
        ArrayList<StaticGroupsProtocol> g = Utils.GROUPS.get(n.group.no);
        if (g.size() < 2) {
            Utils.GROUPS.remove(n.group.no);
        } else {
            g.remove(n);
            Utils.GROUPS.put(n.group.no, g);
        }
        Utils.NODES.remove(n);
    }

    public static BigInteger generateUniqueNo(int idLength) {
        BigInteger no;
        do {
            no = new BigInteger(idLength, CommonState.r);
        } while (isNoInList(no));
        return no;
    }

    public static boolean isNoInList(BigInteger no) {
        return GROUPS.get(no) != null;
    }

    public static String generateIp(BigInteger groupNo, int m) {
        return "0.0." + groupNo + "." + new BigInteger(m, CommonState.r);
    }

    public static BigInteger getLowestGroupNo() {
        return Collections.min(Utils.GROUPS.keySet());
    }

    public static BigInteger getHighestGroupNo() {
        return Collections.max(Utils.GROUPS.keySet());
    }

    public static StaticGroupsProtocol getRandomNode(StaticGroupsProtocol n) {
        if (n == null || n.group == null || n.group.no == null) {
            return NODES.get(CommonState.r.nextInt(NODES.size()));
        }
        StaticGroupsProtocol randomNode;
        do {
            randomNode = NODES.get(CommonState.r.nextInt(NODES.size()));
        } while (randomNode.group.no.equals(n.group.no));
        return randomNode;
    }

    public static StaticGroupsProtocol getNodeByNoAndIp(BigInteger no, String ip) {
        ArrayList<StaticGroupsProtocol> nodes = GROUPS.get(no);
        for (StaticGroupsProtocol n : nodes) {
            if (n.ip.equals(ip)) {
                return n;
            }
        }
        return null;
    }

    public static boolean isOnlyOneGroupInNetwork() {
        return Utils.GROUPS.size() < 2;
    }

    public static StaticGroupsProtocol getFirstNodeByNo(BigInteger no) {
        ArrayList<StaticGroupsProtocol> nodes = GROUPS.get(no);
        if (nodes == null) {
            return null;
        } else {
            return nodes.get(0);
        }
    }

    public static void updateIps(BigInteger no, ArrayList<String> ips) {
        ArrayList<StaticGroupsProtocol> nodes = GROUPS.get(no);
        if (nodes != null)
            for (StaticGroupsProtocol n : nodes) {
                n.group.ips = ips;
            }
    }

    public static void updateSuccessor(BigInteger no, Group successor) {
        ArrayList<StaticGroupsProtocol> nodes = GROUPS.get(no);
        if (nodes != null) {
            for (StaticGroupsProtocol n : nodes) {
                n.successor = successor;
            }
        }
    }

    public static void updatePredecessor(BigInteger no, Group predecessor) {
        ArrayList<StaticGroupsProtocol> nodes = GROUPS.get(no);
        if (nodes != null) {
            for (StaticGroupsProtocol n : nodes) {
                n.predecessor = predecessor;
            }
        }
    }

    public static void updateFingerTable(BigInteger no, Finger[] fingerTable) {
        ArrayList<StaticGroupsProtocol> nodes = GROUPS.get(no);
        if (nodes != null) {
            for (StaticGroupsProtocol n : nodes) {
                n.fingerTable = fingerTable;
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

}
