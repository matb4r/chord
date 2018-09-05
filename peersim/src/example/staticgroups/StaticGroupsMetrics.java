package example.staticgroups;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

import java.time.LocalTime;
import java.util.ArrayList;

public class StaticGroupsMetrics implements Control {

    private static final String PAR_IDLENGTH = "idLength";
    private static final String PAR_MAX_GROUP_SIZE = "maxGroupSize";
    private static final String PAR_CYCLES = "cycles";
    private static final String PAR_INIT_NETWORK_SIZE = "networkInitSize";
    private static final String PAR_STABILITY_RESTRICTION = "stabilityRestriction";

    private int idLength = 0;
    private int maxGroupSize = 0;
    private int cycles = 0;
    private int networkInitSize = 0;
    private double stabilityRestriction = 0;

    LocalTime started;
    LocalTime stopped;
    int maxNetSize = Integer.MIN_VALUE;
    int minNetSize = Integer.MAX_VALUE;
    static int exceptionsCounter = 0;
    static int badPredecessorsCounter = 0;
    static int badSuccessorsCounter = 0;

    static ArrayList<StaticGroupsProtocol> badNodes = new ArrayList<>();

    public StaticGroupsMetrics(String prefix) {
        idLength = Configuration.getInt(prefix + "." + PAR_IDLENGTH);
        maxGroupSize = Configuration.getInt(prefix + "." + PAR_MAX_GROUP_SIZE);
        cycles = Configuration.getInt(prefix + "." + PAR_CYCLES);
        networkInitSize = Configuration.getInt(prefix + "." + PAR_INIT_NETWORK_SIZE);
        stabilityRestriction = Configuration.getDouble(prefix + "." + PAR_STABILITY_RESTRICTION);
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

    public void executeOnStart() {
        started = LocalTime.now();
    }

    public void executeOnEnd() {
        stopped = LocalTime.now();
        System.out.println("cycles: " + cycles);
        System.out.println("network init size: " + networkInitSize);
        System.out.println("m: " + idLength);
        System.out.println("max group size: " + maxGroupSize);
        System.out.println("stability restriction: " + stabilityRestriction);
        System.out.println("started " + started);
        System.out.println("stopped " + stopped);
        System.out.println("time: " + stopped.minusNanos(started.toNanoOfDay()));
        System.out.println("Min net size: " + minNetSize);
        System.out.println("Max net size: " + maxNetSize);
        System.out.println("Exceptions count: " + exceptionsCounter);
        if (StaticGroupsTests.test) {
            System.out.println("bad nodes count: " + badNodes.size());
            System.out.println("bad predecessor count: " + badPredecessorsCounter);
            System.out.println("bad successors count: " + badSuccessorsCounter);
        }
    }

}
