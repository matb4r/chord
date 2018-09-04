package example.staticgroups;

import peersim.config.Configuration;
import peersim.core.Control;

import java.util.ArrayList;

public class StaticGroupsMaintainer implements Control {

    private static final String PAR_PROT = "protocol";

    private int pid = 0;

    public StaticGroupsMaintainer(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
    }

    @Override
    public boolean execute() {
        System.out.println("executing maintaining");
        ArrayList<StaticGroupsProtocol> allNodes = Utils.getAllNodes(pid);
        for (StaticGroupsProtocol cp : allNodes) {
            cp.checkSuccessor();
            cp.checkPredecessor();
            try {
                cp.stabilize();
            } catch (Exception ex) {}
            for (int i = 0; i < cp.m; i++) {
                cp.fixFingers();
            }
        }
        return false;
    }
}
