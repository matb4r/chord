package example.staticgroups;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

public class CreateInitialNodes implements Control {

    private static final String PAR_PROT = "protocol";
    private static final String PAR_IDLENGTH = "idLength";
    private static final String PAR_MAX_GROUP_SIZE = "maxGroupSize";
    private static final String PAR_STABILITY_RESTRICTION = "stabilityRestriction";

    private int pid = 0;
    private int idLength = 0;
    private int maxGroupSize = 0;
    private double stabilityRestriction = 0;

    public CreateInitialNodes(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        idLength = Configuration.getInt(prefix + "." + PAR_IDLENGTH);
        maxGroupSize = Configuration.getInt(prefix + "." + PAR_MAX_GROUP_SIZE);
        stabilityRestriction = Configuration.getDouble(prefix + "." + PAR_STABILITY_RESTRICTION);
    }

    @Override
    public boolean execute() {
        System.out.println("executing CreateInitialNodes");
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            StaticGroupsProtocol n = (StaticGroupsProtocol) node.getProtocol(pid);

            if (i == 0) {
                n.start(null);
            } else {
                n.start(Utils.getRandomCP(n));
            }

            StaticGroupsMaintainer.fixNodes();
        }
        return false;
    }
}
