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
                    if (!StaticGroupsMetrics.badNodes.contains(Utils.NODES.get(i)))
                        StaticGroupsMetrics.badNodes.add(Utils.NODES.get(i));
                }
                if (successorTest(Utils.NODES.get(i)) == false) {
                    StaticGroupsMetrics.badSuccessorsCounter++;
                    if (!StaticGroupsMetrics.badNodes.contains(Utils.NODES.get(i)))
                        StaticGroupsMetrics.badNodes.add(Utils.NODES.get(i));
                }
                if (fingersTest(Utils.NODES.get(i)) == false) {
                    StaticGroupsMetrics.badFingerTableCounter++;
                    if (!StaticGroupsMetrics.badNodes.contains(Utils.NODES.get(i)))
                        StaticGroupsMetrics.badNodes.add(Utils.NODES.get(i));
                }
            }
            System.out.println("done!");
        }
    }

    private static boolean predecessorTest(StaticGroupsProtocol n) {
        if (n.predecessor == null) {
            return true;
        }
        if (Utils.isOnlyOneGroupInNetwork()) {
            return n.predecessor.no.equals(n.group.no);
        }
        if (n.group.no.equals(Utils.getLowestGroupNo())) {
            return n.predecessor.no.equals(Utils.getHighestGroupNo());
        }

        BigInteger highestLowerThan = Utils.getLowestGroupNo();

        for (BigInteger no : Utils.GROUPS.keySet()) {
            if (no.compareTo(highestLowerThan) == 1
                    && no.compareTo(n.group.no) == -1) {
                highestLowerThan = no;
            }
        }

        if (highestLowerThan != n.predecessor.no)
            return false;
        return true;
    }

    private static boolean successorTest(StaticGroupsProtocol n) {
        if (Utils.isOnlyOneGroupInNetwork()) {
            return n.successor.no.equals(n.group.no);
        }
        if (n.group.no.equals(Utils.getHighestGroupNo())) {
            return n.successor.no.equals(Utils.getLowestGroupNo());
        }

        BigInteger lowestBiggerThanP = Utils.getHighestGroupNo();

        for (BigInteger no : Utils.GROUPS.keySet()) {
            if (no.compareTo(lowestBiggerThanP) == -1
                    && no.compareTo(n.group.no) == 1) {
                lowestBiggerThanP = no;
            }
        }

        if (lowestBiggerThanP != n.successor.no)
            return false;
        return true;
    }

    private static boolean fingersTest(StaticGroupsProtocol n) {
        if (Utils.isOnlyOneGroupInNetwork()) {
            for (int i = 0; i < n.M; i++) {
                if (!n.fingerTable[i].group.no.equals(n.group.no))
                    return false;
            }
            return true;
        }
        for (int i = 0; i < n.M; i++) {
            if (n.fingerTable[i].start.compareTo(Utils.getHighestGroupNo()) == 1) {
                if (!n.fingerTable[i].group.no.equals(Utils.getLowestGroupNo())) {
                    return false;
                }
            } else {
                BigInteger lowestGEThanStart = Utils.getHighestGroupNo();
                for (BigInteger no : Utils.GROUPS.keySet()) {
                    if (no.compareTo(lowestGEThanStart) == -1
                            && no.compareTo(n.fingerTable[i].start) >= 0) {
                        lowestGEThanStart = no;
                    }
                }
                if (!n.fingerTable[i].group.no.equals(lowestGEThanStart)) {
                    return false;
                }
            }
        }
        return true;
    }

}
