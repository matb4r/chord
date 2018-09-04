package example.staticgroups;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.DynamicNetwork;

public class RandomDynamicNetwork extends DynamicNetwork {

    private static final String PAR_RANDOM = "random";

    private boolean random = false;

    int addPositive = (int) add >= 0 ? (int) add : (int) -add;
    boolean isAddNow = (int) add >= 0;

    int addCounter = (int) addPositive;
    int removeCounter = (int) addPositive;

    public RandomDynamicNetwork(String prefix) {
        super(prefix);
        random = Configuration.contains(prefix + "." + PAR_RANDOM);
    }

    public final boolean execute() {
        if (random) {
            if (CommonState.r.nextBoolean()) {
                try {
                    add(1);
                } catch (Exception ex) {
                }
            } else {
                if (Network.size() > 1) {
                    try {
                        remove();
                    } catch (Exception ex) {
                    }
                }
            }
        } else {
            if (isAddNow) {
                try {
                    if (Network.size() < maxsize)
                        add(1);
                    addCounter--;
                    if (addCounter == 0) {
                        addCounter = (int) addPositive;
                        isAddNow = false;
                    }
                } catch (Exception ex) {
                }
            } else {
                try {
                    if (Network.size() > 0 && Network.size() > minsize)
                        remove();
                    removeCounter--;
                    if (removeCounter == 0) {
                        removeCounter = (int) addPositive;
                        isAddNow = true;
                    }
                } catch (Exception ex) {
                }
            }
        }

        return false;
    }

    protected void remove() {
        int index = CommonState.r.nextInt(Network.size());
        Node node = Network.get(index);
        StaticGroupsProtocol cp = (StaticGroupsProtocol) node.getProtocol(0);
        Network.remove(index);
        System.out.println("Node " + cp.ip + " died");
    }

}
