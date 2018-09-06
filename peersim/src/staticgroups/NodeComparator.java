package staticgroups;

import peersim.core.Node;

import java.math.BigInteger;
import java.util.Comparator;

public class NodeComparator implements Comparator {

    public int pid = 0;

    public NodeComparator(int pid) {
        this.pid = pid;
    }

    @Override
    public int compare(Object arg0, Object arg1) {
        String a = ((StaticGroupsProtocol) ((Node) arg0).getProtocol(pid)).ip;
        String b = ((StaticGroupsProtocol) ((Node) arg1).getProtocol(pid)).ip;

        BigInteger aGroupNo = new BigInteger(a.split("\\.")[2]);
        BigInteger bGroupNo = new BigInteger(b.split("\\.")[2]);
        if (aGroupNo.equals(bGroupNo)) {
            BigInteger aIpId = new BigInteger(a.split("\\.")[3]);
            BigInteger bIpId = new BigInteger(b.split("\\.")[3]);
            return aIpId.compareTo(bIpId);
        } else {
            return aGroupNo.compareTo(bGroupNo);
        }
    }
}
