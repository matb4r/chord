package example.chordgroups;

import peersim.config.Configuration;
import peersim.core.Control;

import java.util.ArrayList;

public class ChordMaintainer implements Control {

    private static final String PAR_PROT = "protocol";

    private int pid = 0;

    public ChordMaintainer(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
    }

    @Override
    public boolean execute() {
        System.out.println("executing maintaining");
        ArrayList<ChordProtocol> allNodes = Utils.getAllNodes(pid);
        for (ChordProtocol cp : allNodes) {
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
