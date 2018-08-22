package example.chordgroups;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.DynamicNetwork;

public class ChordDynamicNetwork extends DynamicNetwork {

    private static final String PAR_RANDOM = "random";

    private boolean random = false;

    int addPositive = (int) add >= 0 ? (int) add : (int) -add;
    boolean isAddNow = (int) add >= 0;

    int addCounter = (int) addPositive;
    int removeCounter = (int) addPositive;

    public ChordDynamicNetwork(String prefix) {
        super(prefix);
        random = Configuration.contains(prefix + "." + PAR_RANDOM);
    }

    public final boolean execute() {
        if (random) {
            if(CommonState.r.nextBoolean()) {
                add(1);
            } else {
                remove(1);
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
                    if (Network.size() > minsize)
                        remove(1);
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

    protected void remove(int n) {
        //info mb: dodane dla sysout
        for (int i = 0; i < n; ++i) {
            int index = CommonState.r.nextInt(Network.size());
            Node node = Network.get(index);
            ChordProtocol cp = (ChordProtocol) node.getProtocol(0);
            Network.remove(index);
            System.out.println("Node " + cp.ip + " died");
        }
    }

}
