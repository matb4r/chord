package staticgroups;

import peersim.core.Control;

public class StaticGroupsMaintainer implements Control {

    public StaticGroupsMaintainer(String prefix) {
    }

    @Override
    public boolean execute() {
        System.out.println("executing maintaining");
        fixNodes(false);
        return false;
    }

    public static void fixNodes(boolean fixAllFingers) {
        for (StaticGroupsProtocol node : Utils.NODES) {
            node.checkSuccessor();
            node.checkPredecessor();
            try {
                node.stabilize();
            } catch (Exception ex) {
                StaticGroupsMetrics.exceptionsCounter++;
            }
            if (fixAllFingers) {
                for (int i = 0; i < node.M; i++)
                    node.fixFingers();
            } else {
                node.fixFingers();
            }
        }
    }
}
