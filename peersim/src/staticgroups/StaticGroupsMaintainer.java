package staticgroups;

import peersim.core.Control;

public class StaticGroupsMaintainer implements Control {

    public StaticGroupsMaintainer(String prefix) {
    }

    @Override
    public boolean execute() {
        System.out.println("executing maintaining");
        fixNodes();
        return false;
    }

    public static void fixNodes() {
        for (StaticGroupsProtocol node : Utils.NODES) {
            node.checkSuccessor();
            node.checkPredecessor();
            try {
                node.stabilize();
            } catch (Exception ex) {
                StaticGroupsMetrics.exceptionsCounter++;
            }
//            for (int i = 0; i < cp.M; i++)
            node.fixFingers();
        }
    }
}
