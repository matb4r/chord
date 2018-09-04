package example.staticgroups;

import java.math.BigInteger;
import java.util.Comparator;

public class StaticGroupNodeComparator implements Comparator {

    @Override
    public int compare(Object arg0, Object arg1) {
        String a = ((StaticGroupsProtocol) arg0).ip;
        String b = ((StaticGroupsProtocol) arg1).ip;

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
