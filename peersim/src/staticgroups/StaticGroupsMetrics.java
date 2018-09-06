package staticgroups;

import peersim.cdsim.CDSimulator;
import peersim.core.Control;
import peersim.core.Network;

import java.time.LocalTime;
import java.util.ArrayList;

public class StaticGroupsMetrics implements Control {

    public static int exceptionsCounter = 0;
    public static int badPredecessorsCounter = 0;
    public static int badSuccessorsCounter = 0;
    public static int badFingerTableCounter = 0;
    public static int actualCycle = 0;

    public static LocalTime started;
    public static LocalTime stopped;
    public static int maxNetSize = Integer.MIN_VALUE;
    public static int minNetSize = Integer.MAX_VALUE;


    static ArrayList<StaticGroupsProtocol> badNodes = new ArrayList<>();

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
        System.out.println("cycles: " + CDSimulator.cycles);
        System.out.println("network init size: " + Network.initialSize);
        System.out.println("random: " + RandomDynamicNetwork.random);
        System.out.println("random add probability: " + RandomDynamicNetwork.randomAddProbability);
        System.out.println("M: " + StaticGroupsProtocol.M);
        System.out.println("max group size: " + StaticGroupsProtocol.MAX_GROUP_SIZE);
        System.out.println("stability restriction: " + StaticGroupsProtocol.stabilityRestriction);
        System.out.println("started " + started);
        System.out.println("stopped " + stopped);
        System.out.println("time: " + stopped.minusNanos(started.toNanoOfDay()));
        System.out.println("min net size: " + minNetSize);
        System.out.println("max net size: " + maxNetSize);
        System.out.println("Exceptions count: " + exceptionsCounter);
        if (StaticGroupsTests.test) {
            System.out.println("bad nodes count: " + badNodes.size());
            System.out.println("bad predecessor count: " + badPredecessorsCounter);
            System.out.println("bad successors count: " + badSuccessorsCounter);
            System.out.println("bad finger table count: " + badFingerTableCounter);
        }
    }

}
