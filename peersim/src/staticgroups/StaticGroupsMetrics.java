package staticgroups;

import peersim.cdsim.CDSimulator;
import peersim.core.Control;
import peersim.core.Network;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;

import static java.time.temporal.ChronoUnit.MILLIS;

public class StaticGroupsMetrics implements Control {

    public static int exceptionsCounter = 0;
    public static int badPredecessorsCounter = 0;
    public static int badSuccessorsCounter = 0;
    public static int badFingerTableCounter = 0;
    public static int actualCycle = 0;
    public static int actualExperiment = 0;
    public static int sumOfGroupsLiveTime = 0;
    public static int numberOfDiedGroups = 0;

    public static LocalTime started;
    public static LocalTime stopped;
    public static int maxNetSize = Integer.MIN_VALUE;
    public static int minNetSize = Integer.MAX_VALUE;


    public static ArrayList<StaticGroupsProtocol> badNodes = new ArrayList<>();
    public static ArrayList<Group> badGroupes = new ArrayList<>();

    public StaticGroupsMetrics(String prefix) {
    }

    @Override
    public boolean execute() {
        if (Network.size() > maxNetSize) {
            maxNetSize = Network.size();
        }
        if (Network.size() < minNetSize) {
            minNetSize = Network.size();
        }
        return false;
    }

    public static void executeOnStart() {
        started = LocalTime.now();
    }

    public static void executeOnEnd() {
        stopped = LocalTime.now();
        for (BigInteger id : Utils.GROUPS.keySet()) {
            sumOfGroupsLiveTime += Utils.GROUPS.get(id).get(0).group.liveTime;
            numberOfDiedGroups++;
        }
        if (StaticGroupsProtocol.DEBUG) {
            System.out.println("cycles: " + CDSimulator.cycles);
            System.out.println("network init size: " + Network.initialSize);
            System.out.println("random: " + RandomDynamicNetwork.RANDOM);
            System.out.println("random add probability: " + RandomDynamicNetwork.RANDOM_ADD_PROBABILITY);
            System.out.println("M: " + StaticGroupsProtocol.M);
            System.out.println("max group size: " + StaticGroupsProtocol.MAX_GROUP_SIZE);
            System.out.println("average group size: " + calculateAvgGroupSize());
            System.out.println("number of groups: " + Utils.GROUPS.size());
            System.out.println("stability requirement: " + StaticGroupsProtocol.STABILITY_REQUIREMENT);
            System.out.println("started " + started);
            System.out.println("stopped " + stopped);
            System.out.println("time: " + stopped.minusNanos(started.toNanoOfDay()));
            System.out.println("min net size: " + minNetSize);
            System.out.println("max net size: " + maxNetSize);
            System.out.println("died groups: " + numberOfDiedGroups);
            System.out.println("avg live of groups: " + sumOfGroupsLiveTime / numberOfDiedGroups);
            System.out.println("Exceptions count: " + exceptionsCounter);
            if (StaticGroupsTests.test) {
                System.out.println("bad groupes count: " + badGroupes.size());
                System.out.println("bad nodes count: " + badNodes.size());
                System.out.println("bad predecessor count: " + badPredecessorsCounter);
                System.out.println("bad successors count: " + badSuccessorsCounter);
                System.out.println("bad finger table count: " + badFingerTableCounter);
            }
        }
//        System.out.println(avgGroupSizeToStabilityRequirement());
//        System.out.println(avgGroupSizeToTime());
//        System.out.println(avgGroupSizeToMaxGroupSize());
//        System.out.println(avgStabilityToStabilityRequirement());
//        System.out.println(avgStabilityToGroupSize());
//        System.out.println(avgGroupSizeToRandomAddProb());
//        System.out.println(timeToNetSize());
//        System.out.println(avgGroupLiveTimeToMaxGroupSize());
//        System.out.println(avgGroupLiveTimeToStabilityRequirement());
    }

    public static String avgGroupLiveTimeToMaxGroupSize() {
        return "(" + StaticGroupsProtocol.MAX_GROUP_SIZE + "," + sumOfGroupsLiveTime / numberOfDiedGroups + ")";
    }

    public static String avgGroupLiveTimeToStabilityRequirement() {
        return "(" + StaticGroupsProtocol.STABILITY_REQUIREMENT + "," + sumOfGroupsLiveTime / numberOfDiedGroups + ")";
    }

    public static String timeToNetSize() {
        return "(" + Network.initialSize + "," + MILLIS.between(started, stopped) + ")";
    }

    public static String avgGroupSizeToRandomAddProb() {
        return "(" + RandomDynamicNetwork.RANDOM_ADD_PROBABILITY + "," + calculateAvgGroupSize() + ")";
    }

    public static String avgGroupSizeToNetSize() {
        return "(" + Utils.NODES.size() + "," + calculateAvgGroupSize() + ")";
    }

    private static String avgStabilityToGroupSize() {
        StringBuilder sb = new StringBuilder();
        for (BigInteger id : Utils.GROUPS.keySet()) {
            ArrayList<StaticGroupsProtocol> nodes = Utils.GROUPS.get(id);
            sb.append("(" + nodes.size() + "," + calculateAvgStability(nodes) + ")\n");
        }
        return sb.toString();
    }

    private static String avgStabilityToStabilityRequirement() {
        return "(" + StaticGroupsProtocol.STABILITY_REQUIREMENT + "," + calculateAvgStability() + ")";
    }

    private static String avgGroupSizeToMaxGroupSize() {
        return "(" + StaticGroupsProtocol.MAX_GROUP_SIZE + "," + calculateAvgGroupSize() + ")";
    }

    private static String avgGroupSizeToTime() {
        return "(" + calculateAvgGroupSize() + "," + StaticGroupsMetrics.actualExperiment + ")";
    }

    private static String avgGroupSizeToStabilityRequirement() {
        return "(" + StaticGroupsProtocol.STABILITY_REQUIREMENT + "," + calculateAvgGroupSize() + ")";
    }

    private static double calculateAvgStability() {
        double sum = 0;
        int i = 0;
        for (StaticGroupsProtocol n : Utils.NODES) {
            sum += n.stability;
            i++;
        }
        return sum / i;
    }

    private static double calculateAvgStability(ArrayList<StaticGroupsProtocol> nodes) {
        double sum = 0;
        int i = 0;
        for (StaticGroupsProtocol n : nodes) {
            sum += n.stability;
            i++;
        }
        return sum / i;
    }

    private static double calculateAvgGroupSize() {
        double sum = 0;
        int i = 0;
        for (BigInteger id : Utils.GROUPS.keySet()) {
            sum += Utils.GROUPS.get(id).size();
            i++;
        }
        return sum / i;
    }

}
