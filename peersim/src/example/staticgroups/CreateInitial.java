package example.staticgroups;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * info mb
 * co sie dzieje na samiuskim poczatku
 * network.size definiuje ile na poczatku wezlowe tworzonych przez CreateInitial
 */
public class CreateInitial implements Control {

    private static final String PAR_PROT = "protocol";
    private static final String PAR_IDLENGTH = "idLength";
    private static final String PAR_MAX_GROUP_SIZE = "maxGroupSize";

    private int pid = 0;
    private int idLength = 0;
    private int maxGroupSize = 0;

    public CreateInitial(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        idLength = Configuration.getInt(prefix + "." + PAR_IDLENGTH);
        maxGroupSize = Configuration.getInt(prefix + "." + PAR_MAX_GROUP_SIZE);
    }

    @Override
    public boolean execute() {

        // info mb: tworzenie nowych wezlow na samym poczatku: kazdy wezel tworzy nowa grupe
        System.out.println("executing CreateInitial");
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            StaticGroupsProtocol cp = (StaticGroupsProtocol) node.getProtocol(pid);
            cp.next = 0;
            cp.pid = pid;
            cp.m = idLength;
            cp.MAX_GROUP_SIZE = maxGroupSize;
            cp.fingerTable = new Finger[cp.m];
            cp.group = new Group();
            cp.group.no = Utils.generateUniqueId(idLength, pid);
            cp.ip = Utils.generateIp(cp.group.no, cp.m);
            cp.group.ips.add(cp.ip);
        }

        Network.sort(new NodeComparator(pid));

        setPredecessors();
        setSuccessors();
        createFingerTable();
        return false;
    }

    public void setPredecessors() {
        for (int i = 0; i < Network.size(); i++) {
            StaticGroupsProtocol cp = (StaticGroupsProtocol) Network.get(i).getProtocol(pid);
            if (i == 0) {
                // info mb: dla pierwszego wezla, poprzednikiem musi byc wezel ostatni
                StaticGroupsProtocol pred = (StaticGroupsProtocol) Network.get(Network.size() - 1).getProtocol(pid);
                cp.predecessor = pred.group;
            } else {
                StaticGroupsProtocol pred = (StaticGroupsProtocol) Network.get(i - 1).getProtocol(pid);
                cp.predecessor = pred.group;
            }
        }
    }

    public void setSuccessors() {
        for (int i = 0; i < Network.size(); i++) {
            StaticGroupsProtocol cp = (StaticGroupsProtocol) Network.get(i).getProtocol(pid);
            if (i == Network.size() - 1) {
                // info mb: dla ostatniego wezla, nastepnikiem musi byc pierwszy wezel
                StaticGroupsProtocol succ = (StaticGroupsProtocol) Network.get(0).getProtocol(pid);
                cp.successor = succ.group;
            } else {
                StaticGroupsProtocol succ = (StaticGroupsProtocol) Network.get(i + 1).getProtocol(pid);
                cp.successor = succ.group;
            }
        }
    }

    public void createFingerTable() {
        for (StaticGroupsProtocol cp : Utils.getAllNodes(pid)) {

            cp.fingerTable[0] = new Finger();
            cp.fingerTable[0].i = 1;
            cp.fingerTable[0].start = (cp.group.no.add(BigDecimal.valueOf(Math.pow(2, 0)).toBigInteger()).mod(BigDecimal.valueOf(Math.pow(2, cp.m)).toBigInteger()));
            cp.fingerTable[0].end = (cp.group.no.add(BigDecimal.valueOf(Math.pow(2, 1)).toBigInteger().subtract(BigInteger.ONE)).mod(BigDecimal.valueOf(Math.pow(2, cp.m)).toBigInteger()));
            cp.fingerTable[0].group = cp.successor;

            for (int i = 2; i <= cp.m; i++) {
                cp.fingerTable[i - 1] = new Finger();
                cp.fingerTable[i - 1].i = i;
                cp.fingerTable[i - 1].start = (cp.group.no.add(BigDecimal.valueOf(Math.pow(2, i - 1)).toBigInteger()).mod(BigDecimal.valueOf(Math.pow(2, cp.m)).toBigInteger()));
                cp.fingerTable[i - 1].end = (cp.group.no.add(BigDecimal.valueOf(Math.pow(2, i)).toBigInteger().subtract(BigInteger.ONE)).mod(BigDecimal.valueOf(Math.pow(2, cp.m)).toBigInteger()));
                cp.fingerTable[i - 1].group = findSmallestGroupGE(cp.fingerTable[i - 1].start);
            }
        }
    }

    public Group findSmallestGroupGE(BigInteger id) {
        // dziala tylko dlatego, ze wezly sa posortowane po group.no
        for (StaticGroupsProtocol cp : Utils.getAllNodes(pid)) {
            if (cp.group.no.compareTo(id) >= 0) {
                return cp.group;
            }
        }
        return Utils.getAllNodes(pid).get(0).group;
    }
}
