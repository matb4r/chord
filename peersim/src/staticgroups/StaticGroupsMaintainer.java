package staticgroups;

import peersim.core.Control;

public class StaticGroupsMaintainer implements Control {

    public StaticGroupsMaintainer(String prefix) {
    }

    @Override
    public boolean execute() {
        System.out.println("executing maintaining");
        fixNodes(false, false);
        return false;
    }

    public static void fixNodes(boolean fixAllFingers, boolean checkSuccessor) {
        for (StaticGroupsProtocol node : Utils.NODES) {
            if (checkSuccessor)
                node.checkSuccessor();
            node.checkPredecessor();
            node.checkGroup();
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
