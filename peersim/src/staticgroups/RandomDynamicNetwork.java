package staticgroups;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.DynamicNetwork;

public class RandomDynamicNetwork extends DynamicNetwork {

    private static final String PAR_RANDOM = "random";
    private static final String PAR_RANDOM_ADD_PROBABILITY = "randomAddProbability";

    public static boolean random = false;
    public static double randomAddProbability;

    int addPositive = (int) add >= 0 ? (int) add : (int) -add;
    boolean isAddNow = (int) add >= 0;

    int addCounter = (int) addPositive;
    int removeCounter = (int) addPositive;

    public RandomDynamicNetwork(String prefix) {
        super(prefix);
        random = Configuration.contains(prefix + "." + PAR_RANDOM);
        randomAddProbability = Configuration.getDouble(prefix + "." + PAR_RANDOM_ADD_PROBABILITY, 0.5);
    }

    public final boolean execute() {
        try {
            if (random) {
                if (CommonState.r.nextDouble() <= randomAddProbability) {
                    add(1);
                } else {
                    if (Network.size() > 1) {
                        remove();
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
        int index = CommonState.r.nextInt(Network.size());
        Node node = Network.get(index);
        StaticGroupsProtocol n = (StaticGroupsProtocol) node.getProtocol(0);
        Utils.removeNode(n);
        Network.remove(index);
        System.out.println("Node " + n.address + " died");
    }

}
