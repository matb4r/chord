package staticgroups;

import peersim.core.Node;
import peersim.dynamics.NodeInitializer;

public class StaticGroupsInitializer implements NodeInitializer {

    private StaticGroupsProtocol node;

    public StaticGroupsInitializer(String prefix) {
    }

    @Override
    public void initialize(Node n) {
        System.out.println("executing StaticGroupsInitializer");
        this.node = (StaticGroupsProtocol) n.getProtocol(StaticGroupsProtocol.pid);
        this.node.start(Utils.getRandomNode(this.node));
    }


}
