package staticgroups;

import java.math.BigInteger;
import java.util.ArrayList;

public class Group {

    public Group() {
        addresses = new ArrayList<>();
        liveTime = 0;
    }

    public BigInteger id;
    public ArrayList<String> addresses;
    public int liveTime;

    public static void updateLiveTime() {
        for (BigInteger id : Utils.GROUPS.keySet()) {
            Utils.GROUPS.get(id).get(0).group.liveTime++;
        }
    }

}
