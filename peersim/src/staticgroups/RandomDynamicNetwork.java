package staticgroups;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.DynamicNetwork;

public class RandomDynamicNetwork extends DynamicNetwork {

    private static final String PAR_RANDOM = "RANDOM";
    private static final String PAR_RANDOM_ADD_PROBABILITY = "RANDOM_ADD_PROBABILITY";

    public static boolean RANDOM = false;
    public static double RANDOM_ADD_PROBABILITY;

    int addPositive = (int) add >= 0 ? (int) add : (int) -add;
    boolean isAddNow = (int) add >= 0;

    int addCounter = (int) addPositive;
    int removeCounter = (int) addPositive;

    public RandomDynamicNetwork(String prefix) {
        super(prefix);
        RANDOM = Configuration.contains(prefix + "." + PAR_RANDOM);
        RANDOM_ADD_PROBABILITY = Configuration.getDouble(prefix + "." + PAR_RANDOM_ADD_PROBABILITY, 0.5);
    }

    public final boolean execute() {
        try {
            if (RANDOM) {
                if (CommonState.r.nextDouble() <= RANDOM_ADD_PROBABILITY) {
                    add(1);
                } else {
                    if (Network.size() > 1) {
                        remove();
                    } else {
                        add(1);
                    }
                }
            } else {
                if (isAddNow) {
                    if (Network.size() < maxsize)
                        add(1);
                    addCounter--;
                    if (addCounter == 0) {
                        addCounter = (int) addPositive;
                        isAddNow = false;
                    }
                } else {
                    if (Network.size() > 0 && Network.size() > minsize)
                        remove();
                    removeCounter--;
                    if (removeCounter == 0) {
                        removeCounter = (int) addPositive;
                        isAddNow = true;
                    }
                }
            }
        } catch (Exception ex) {
            StaticGroupsMetrics.exceptionsCounter++;
        }

        return false;
    }

    protected void remove() {
        // remove node with lowest stability from 4 randoms
        int index = CommonState.r.nextInt(Network.size());
        Node node = Network.get(index);
        StaticGroupsProtocol n = (StaticGroupsProtocol) node.getProtocol(0);

        int index2 = CommonState.r.nextInt(Network.size());
        Node node2 = Network.get(index2);
        StaticGroupsProtocol n2 = (StaticGroupsProtocol) node2.getProtocol(0);

        int index3 = CommonState.r.nextInt(Network.size());
        Node node3 = Network.get(index3);
        StaticGroupsProtocol n3 = (StaticGroupsProtocol) node3.getProtocol(0);

        int index4 = CommonState.r.nextInt(Network.size());
        Node node4 = Network.get(index4);
        StaticGroupsProtocol n4 = (StaticGroupsProtocol) node4.getProtocol(0);

        if (n.stability <= n2.stability &&
                n.stability <= n3.stability &&
                n.stability <= n4.stability) {
            Utils.removeNode(n);
            Network.remove(index);
            if (StaticGroupsProtocol.DEBUG) System.out.println("Node " + n.address + " died");
        } else if (n2.stability <= n.stability &&
                n2.stability <= n3.stability &&
                n2.stability <= n4.stability) {
            Utils.removeNode(n2);
            Network.remove(index2);
            if (StaticGroupsProtocol.DEBUG) System.out.println("Node " + n2.address + " died");
        } else if (n3.stability <= n.stability &&
                n3.stability <= n2.stability &&
                n3.stability <= n4.stability) {
            Utils.removeNode(n3);
            Network.remove(index3);
            if (StaticGroupsProtocol.DEBUG) System.out.println("Node " + n3.address + " died");
        } else if (n4.stability <= n.stability &&
                n4.stability <= n2.stability &&
                n4.stability <= n3.stability) {
            Utils.removeNode(n4);
            Network.remove(index4);
            if (StaticGroupsProtocol.DEBUG) System.out.println("Node " + n4.address + " died");
        }
    }

}
