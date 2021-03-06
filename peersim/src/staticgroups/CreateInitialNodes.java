package staticgroups;

import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class CreateInitialNodes implements Control {

    public CreateInitialNodes(String prefix) {
    }

    @Override
    public boolean execute() {
        if (StaticGroupsProtocol.DEBUG) System.out.println("executing CreateInitialNodes");
        for (int i = 0; i < Network.size(); i++) {
            if (StaticGroupsProtocol.DEBUG) System.out.print(i + " ");
            Node node = Network.get(i);
            StaticGroupsProtocol n = (StaticGroupsProtocol) node.getProtocol(StaticGroupsProtocol.pid);

            if (i == 0) {
                n.start(null);
            } else {
                n.start(Utils.getRandomNode(n));
            }

            StaticGroupsMaintainer.fixNodes(true, true);
        }
        return false;
    }
}
