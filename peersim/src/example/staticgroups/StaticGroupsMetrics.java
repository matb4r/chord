package example.staticgroups;

import peersim.core.Control;
import peersim.core.Network;

import java.time.LocalTime;

public class StaticGroupsMetrics implements Control {

    LocalTime started;
    LocalTime stopped;
    int maxNetSize = Integer.MIN_VALUE;
    int minNetSize = Integer.MAX_VALUE;
    static int exceptionsCounter = 0;

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

    public void executeOnStart() {
        started = LocalTime.now();
    }

    public void executeOnEnd() {
        stopped = LocalTime.now();
        System.out.println("started " + started);
        System.out.println("stopped " + stopped);
        System.out.println("time: " + stopped.minusNanos(started.toNanoOfDay()));
        System.out.println("Min net size: " + minNetSize);
        System.out.println("Max net size: " + maxNetSize);
        System.out.println("Exceptions count: " + exceptionsCounter);
    }

}
