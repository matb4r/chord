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

    public void executeOnEnd() {
        if (test) {
            for (StaticGroupsProtocol p : Utils.NODES) {
                if (predecessorTest(p) == false) {
                    StaticGroupsMetrics.badPredecessorsCounter++;
                    if (!StaticGroupsMetrics.badNodes.contains(p))
                        StaticGroupsMetrics.badNodes.add(p);
                }
                if (successorTest(p) == false) {
                    StaticGroupsMetrics.badSuccessorsCounter++;
                    if (!StaticGroupsMetrics.badNodes.contains(p))
                        StaticGroupsMetrics.badNodes.add(p);
                }
                if(fingersTest(p) == false) {
                    StaticGroupsMetrics.badFingerTableCounter++;
                    if (!StaticGroupsMetrics.badNodes.contains(p))
                        StaticGroupsMetrics.badNodes.add(p);
                }
            }
        }
    }

    private boolean predecessorTest(StaticGroupsProtocol p) {
        if (p.predecessor == null) {
            return true;
        }
        if (Utils.isOnlyOneGroupInNetwork()) {
            return p.predecessor.no.equals(p.group.no);
        }
        if (p.group.no.equals(getLowestGroupNo())) {
            return p.predecessor.no.equals(getHighestGroupNo());
        }

        BigInteger highestLowerThan = getLowestGroupNo();

        for (StaticGroupsProtocol node : Utils.NODES) {
            if (node.group.no.compareTo(highestLowerThan) == 1
                    && node.group.no.compareTo(p.group.no) == -1) {
                highestLowerThan = node.group.no;
            }
        }

        if (highestLowerThan != p.predecessor.no)
            return false;
        return true;
    }

    private boolean successorTest(StaticGroupsProtocol p) {
        if (Utils.isOnlyOneGroupInNetwork()) {
            return p.successor.no.equals(p.group.no);
        }
        if (p.group.no.equals(getHighestGroupNo())) {
            return p.successor.no.equals(getLowestGroupNo());
        }

        BigInteger lowestBiggerThanP = getHighestGroupNo();

        for (StaticGroupsProtocol node : Utils.NODES) {
            if (node.group.no.compareTo(lowestBiggerThanP) == -1
                    && node.group.no.compareTo(p.group.no) == 1) {
                lowestBiggerThanP = node.group.no;
            }
        }

        if (lowestBiggerThanP != p.successor.no)
            return false;
        return true;
    }

    private boolean fingersTest(StaticGroupsProtocol p) {
        if (Utils.isOnlyOneGroupInNetwork()) {
            for (int i = 0; i < p.m; i++) {
                if (!p.fingerTable[i].group.no.equals(p.group.no))
                    return false;
            }
            return true;
        }
        for (int i = 0; i < p.m; i++) {
            if (p.fingerTable[i].start.compareTo(getHighestGroupNo())==1) {
                if (!p.fingerTable[i].group.no.equals(getLowestGroupNo())) {
                    return false;
                }
            } else {
                BigInteger lowestGEThanStart = getHighestGroupNo();
                for (StaticGroupsProtocol node : Utils.NODES) {
                    if (node.group.no.compareTo(lowestGEThanStart) == -1
                            && node.group.no.compareTo(p.fingerTable[i].start) >= 0) {
                        lowestGEThanStart = node.group.no;
                    }
                }
                if (!p.fingerTable[i].group.no.equals(lowestGEThanStart)) {
                    return false;
                }
            }
        }
        return true;
    }

    private BigInteger getLowestGroupNo() {
        BigInteger lowest = Utils.NODES.get(0).group.no;
        for (StaticGroupsProtocol p : Utils.NODES) {
            if (p.group.no.compareTo(lowest) == -1) {
                lowest = p.group.no;
            }
        }
        return lowest;
    }

    private BigInteger getHighestGroupNo() {
        BigInteger highest = Utils.NODES.get(0).group.no;
        for (StaticGroupsProtocol p : Utils.NODES) {
            if (p.group.no.compareTo(highest) == 1) {
                highest = p.group.no;
            }
        }
        return highest;
    }

}
