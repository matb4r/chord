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
        for (StaticGroupsProtocol cp : Utils.NODES) {
            cp.checkSuccessor();
            cp.checkPredecessor();
            try {
                cp.stabilize();
            } catch (Exception ex) {
                StaticGroupsMetrics.exceptionsCounter++;
            }
//            for (int i = 0; i < cp.m; i++)
            cp.fixFingers();
        }
    }
}
