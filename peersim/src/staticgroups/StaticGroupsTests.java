package staticgroups;

import peersim.config.Configuration;
import peersim.core.Control;

import java.math.BigInteger;

public class StaticGroupsTests implements Control {

    private static final String PAR_TEST = "test";

    public static boolean test = false;

    public StaticGroupsTests(String prefix) {
        test = Configuration.contains(prefix + "." + PAR_TEST);
    }

    @Override
    public boolean execute() {
        return false;
    }

    public static void executeTests() {
        if (test) {
            System.out.print("Tests: ");
            for (int i = 0; i < Utils.NODES.size(); i++) {
                System.out.print(i + ", ");
                if (predecessorTest(Utils.NODES.get(i)) == false) {
                    StaticGroupsMetrics.badPredecessorsCounter++;
                    if (!StaticGroupsMetrics.badGroupes.contains(Utils.NODES.get(i).group))
                        StaticGroupsMetrics.badGroupes.add(Utils.NODES.get(i).group);
                    if (!StaticGroupsMetrics.badNodes.contains(Utils.NODES.get(i)))
                        StaticGroupsMetrics.badNodes.add(Utils.NODES.get(i));
                }
                if (successorTest(Utils.NODES.get(i)) == false) {
                    StaticGroupsMetrics.badSuccessorsCounter++;
                    if (!StaticGroupsMetrics.badGroupes.contains(Utils.NODES.get(i).group))
                        StaticGroupsMetrics.badGroupes.add(Utils.NODES.get(i).group);
                    if (!StaticGroupsMetrics.badNodes.contains(Utils.NODES.get(i)))
                        StaticGroupsMetrics.badNodes.add(Utils.NODES.get(i));
                }
                for (int j = 0; j < StaticGroupsProtocol.M; j++) {
                    if (fingersTest(Utils.NODES.get(i), j) == false) {
                        StaticGroupsMetrics.badFingerTableCounter++;
                        if (!StaticGroupsMetrics.badGroupes.contains(Utils.NODES.get(i).group))
                            StaticGroupsMetrics.badGroupes.add(Utils.NODES.get(i).group);
                        if (!StaticGroupsMetrics.badNodes.contains(Utils.NODES.get(i)))
                            StaticGroupsMetrics.badNodes.add(Utils.NODES.get(i));
                        break;
                    }
                }
            }
            System.out.println("done!");
        }
    }

    public static boolean predecessorTest(StaticGroupsProtocol n) {
        if (n.predecessor == null) {
            return true;
        }
        if (Utils.isOnlyOneGroupInNetwork()) {
            return n.predecessor.id.equals(n.group.id);
        }
        if (n.group.id.equals(Utils.getLowestGroupId())) {
            return n.predecessor.id.equals(Utils.getHighestGroupId());
        }

        BigInteger highestLowerThan = Utils.getLowestGroupId();

        for (BigInteger id : Utils.GROUPS.keySet()) {
            if (id.compareTo(highestLowerThan) == 1
                    && id.compareTo(n.group.id) == -1) {
                highestLowerThan = id;
            }
        }

        return highestLowerThan == n.predecessor.id;
    }

    public static boolean successorTest(StaticGroupsProtocol n) {
        if (Utils.isOnlyOneGroupInNetwork()) {
            return n.successor.id.equals(n.group.id);
        }
        if (n.group.id.equals(Utils.getHighestGroupId())) {
            return n.successor.id.equals(Utils.getLowestGroupId());
        }

        BigInteger lowestBiggerThanP = Utils.getHighestGroupId();

        for (BigInteger id : Utils.GROUPS.keySet()) {
            if (id.compareTo(lowestBiggerThanP) == -1
                    && id.compareTo(n.group.id) == 1) {
                lowestBiggerThanP = id;
            }
        }

        return lowestBiggerThanP == n.successor.id;
    }

    public static boolean fingersTest(StaticGroupsProtocol n, int i) {
        if (Utils.isOnlyOneGroupInNetwork()) {
            return n.fingerTable[i].group.id.equals(n.group.id);
        }
        if (n.fingerTable[i].start.compareTo(Utils.getHighestGroupId()) == 1) {
            if (!n.fingerTable[i].group.id.equals(Utils.getLowestGroupId())) {
                return false;
            }
        } else {
            BigInteger lowestGEThanStart = Utils.getHighestGroupId();
            for (BigInteger id : Utils.GROUPS.keySet()) {
                if (id.compareTo(lowestGEThanStart) == -1
                        && id.compareTo(n.fingerTable[i].start) >= 0) {
                    lowestGEThanStart = id;
                }
            }
            if (!n.fingerTable[i].group.id.equals(lowestGEThanStart)) {
                return false;
            }
        }
        return true;
    }

}
