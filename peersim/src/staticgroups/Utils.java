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
        ArrayList<StaticGroupsProtocol> g = Utils.GROUPS.get(n.group.id);
        if (g == null) {
            g = new ArrayList<>();
        }
        g.add(n);
        Utils.GROUPS.put(n.group.id, g);
    }

    public static void removeNode(StaticGroupsProtocol n) {
        ArrayList<StaticGroupsProtocol> g = Utils.GROUPS.get(n.group.id);
        if (g.size() < 2) {
            Utils.GROUPS.remove(n.group.id);
        } else {
            g.remove(n);
            Utils.GROUPS.put(n.group.id, g);
        }
        Utils.NODES.remove(n);
    }

    public static BigInteger generateUniqueId(int idLength) {
        BigInteger id;
        do {
            id = new BigInteger(idLength, CommonState.r);
        } while (isIdInList(id));
        return id;
    }

    public static String generateUniqueAddress(BigInteger groupId, int m) {
        String addr;
        do {
            addr = "0.0." + groupId + "." + new BigInteger(m, CommonState.r);
        } while (isAddressInGroup(groupId, addr));
        return addr;
    }

    public static boolean isIdInList(BigInteger id) {
        return GROUPS.get(id) != null;
    }

    public static boolean isAddressInGroup(BigInteger groupId, String addr) {
        ArrayList<StaticGroupsProtocol> nodes = GROUPS.get(groupId);
        return nodes != null && nodes.contains(addr);
    }

    public static BigInteger getLowestGroupId() {
        return Collections.min(Utils.GROUPS.keySet());
    }

    public static BigInteger getHighestGroupId() {
        return Collections.max(Utils.GROUPS.keySet());
    }

    public static StaticGroupsProtocol getRandomNode(StaticGroupsProtocol n) {
        if (n == null || n.group == null || n.group.id == null) {
            return NODES.get(CommonState.r.nextInt(NODES.size()));
        }
        StaticGroupsProtocol randomNode;
        do {
            randomNode = NODES.get(CommonState.r.nextInt(NODES.size()));
        } while (randomNode.group.id.equals(n.group.id));
        return randomNode;
    }

    public static StaticGroupsProtocol getNodeByGroupIdAndIp(BigInteger id, String ip) {
        ArrayList<StaticGroupsProtocol> nodes = GROUPS.get(id);
        for (StaticGroupsProtocol n : nodes) {
            if (n.address.equals(ip)) {
                return n;
            }
        }
        return null;
    }

    public static boolean isOnlyOneGroupInNetwork() {
        return Utils.GROUPS.size() < 2;
    }

    public static StaticGroupsProtocol getFirstNodeById(BigInteger id) {
        ArrayList<StaticGroupsProtocol> nodes = GROUPS.get(id);
        if (nodes == null) {
            return null;
        } else {
            return nodes.get(0);
        }
    }

    public static void updateIps(BigInteger id, ArrayList<String> ips) {
        ArrayList<StaticGroupsProtocol> nodes = GROUPS.get(id);
        if (nodes != null)
            for (StaticGroupsProtocol n : nodes) {
                n.group.addresses = ips;
            }
    }

    public static void updateSuccessor(BigInteger id, Group successor) {
        ArrayList<StaticGroupsProtocol> nodes = GROUPS.get(id);
        if (nodes != null) {
            for (StaticGroupsProtocol n : nodes) {
                n.successor = successor;
            }
        }
    }

    public static void updatePredecessor(BigInteger id, Group predecessor) {
        ArrayList<StaticGroupsProtocol> nodes = GROUPS.get(id);
        if (nodes != null) {
            for (StaticGroupsProtocol n : nodes) {
                n.predecessor = predecessor;
            }
        }
    }

    public static void updateFingerTable(BigInteger id, Finger[] fingerTable) {
        ArrayList<StaticGroupsProtocol> nodes = GROUPS.get(id);
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
