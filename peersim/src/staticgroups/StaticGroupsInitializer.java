package staticgroups;

import peersim.config.Configuration;
import peersim.core.Node;
import peersim.dynamics.NodeInitializer;

public class StaticGroupsInitializer implements NodeInitializer {

    private static final String PAR_PROT = "protocol";
    private static final String PAR_IDLENGTH = "idLength";
    private static final String PAR_MAX_GROUP_SIZE = "maxGroupSize";
    private static final String PAR_STABILITY_RESTRICTION = "stabilityRestriction";

    private int pid = 0;
    private int idLength = 0;
    private int maxGroupSize = 0;
    private double stabilityRestriction = 0;

    private StaticGroupsProtocol n;

    public StaticGroupsInitializer(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        idLength = Configuration.getInt(prefix + "." + PAR_IDLENGTH);
        maxGroupSize = Configuration.getInt(prefix + "." + PAR_MAX_GROUP_SIZE);
        stabilityRestriction = Configuration.getDouble(prefix + "." + PAR_STABILITY_RESTRICTION);
    }

    @Override
    public void initialize(Node node) {
        System.out.println("executing StaticGroupsInitializer");
        n = (StaticGroupsProtocol) node.getProtocol(pid);
        n.start(Utils.getRandomCP(n));
    }


}
