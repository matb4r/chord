package example.staticgroups;

import peersim.config.Configuration;
import peersim.core.Node;
import peersim.dynamics.NodeInitializer;

import java.util.ArrayList;

public class StaticGroupsInitializer implements NodeInitializer {

    private static final String PAR_PROT = "protocol";
    private static final String PAR_IDLENGTH = "idLength";
    private static final String PAR_MAX_GROUP_SIZE = "maxGroupSize";
    private static final String PAR_STABILITY_RESTRICTION = "stabilityRestriction";

    private int pid = 0;
    private int idLength = 0;
    private int maxGroupSize = 0;
    private double stabilityRestriction = 0;

    private StaticGroupsProtocol cp;

    public StaticGroupsInitializer(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        idLength = Configuration.getInt(prefix + "." + PAR_IDLENGTH);
        maxGroupSize = Configuration.getInt(prefix + "." + PAR_MAX_GROUP_SIZE);
        stabilityRestriction = Configuration.getDouble(prefix + "." + PAR_STABILITY_RESTRICTION);
    }

    @Override
    public void initialize(Node n) {
        System.out.println("executing StaticGroupsInitializer");
        cp = (StaticGroupsProtocol) n.getProtocol(pid);
        cp.start(Utils.getRandomCP(cp, pid));
    }


}
