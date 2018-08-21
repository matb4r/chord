package example.chordgroups;

import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.DynamicNetwork;

public class ChordDynamicNetwork extends DynamicNetwork {

    public ChordDynamicNetwork(String prefix) {
        super(prefix);
    }

    public final boolean execute()
    {
        if (add == 0)
            return false;
        if (!substitute) {
            if ((maxsize <= Network.size() && add > 0)
                    || (minsize >= Network.size() && add < 0))
                return false;
        }
        int toadd = 0;
        int toremove = 0;
        if (add > 0) {
            toadd = (int) Math.round(add < 1 ? add * Network.size() : add);
            if (!substitute && toadd > maxsize - Network.size())
                toadd = maxsize - Network.size();
            if (substitute)
                toremove = toadd;
        } else if (add < 0) {
            toremove = (int) Math.round(add > -1 ? -add * Network.size() : -add);
            if (!substitute && toremove > Network.size() - minsize)
                toremove = Network.size() - minsize;
            if (substitute)
                toadd = toremove;
        }

        // info mb: dodane zeby najpierw add jesli wieksze od 0
        if (add > 0) {
            add(toadd);
            remove(toremove);
        } else {
            remove(toremove);
            add(toadd);
        }
        return false;
    }

    protected void remove(int n)
    {
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
