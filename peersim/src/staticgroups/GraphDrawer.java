package staticgroups;

import peersim.config.Configuration;
import peersim.core.Control;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

public class GraphDrawer implements Control {

    private static final String PAR_DRAW = "draw";

    private static final String PATH = "../master-thesis/graph.tex";
    private static final double R = 4;
    private static final double NODE_SIZE = 0.1;
    private static final String NODE_COLOR = "teal";
    private static final String NODE_BORDER_COLOR = "teal";
    private static final String CONNECTIONS_COLOR = "gray";
    private static final String BAD_CONNECTIONS_COLOR = "red";
    private static final String BAD_PREDECESSORS_COLOR = "magenta";
    private static final String BAD_SUCCESSORS_COLOR = "orange";
    private static final String NEIGH_COLOR = "gray";

    private static boolean draw = false;

    public GraphDrawer(String prefix) {
        draw = Configuration.contains(prefix + "." + PAR_DRAW);
    }

    @Override
    public boolean execute() {
        return false;
    }

    public static void drawGraph() {
        if (draw) {
            StringBuilder sb = new StringBuilder();
            sb.append("\\begin{tikzpicture}\n");
            sb.append(ringStr());
            sb.append(connectionsStr());
            sb.append(neighStr());
            sb.append(wrongConnectionsStr());
            sb.append(wrongPredecessorsStr());
            sb.append(wrongSuccessorsStr());
            sb.append("\\end{tikzpicture}\n");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATH))) {
                writer.write(sb.toString());
            } catch (Exception ex) {
            }
        }
    }

    private static String connectionsStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("\\draw [" + CONNECTIONS_COLOR + "]\n");
        for (ArrayList<StaticGroupsProtocol> l : Utils.GROUPS.values()) {
            try {
                StaticGroupsProtocol n = l.get(0);
                Coord c = nodeToCoord(n);
                for (int i = 0; i < n.M; i++) {
                    Coord f = nodeToCoord(Utils.getFirstNodeById(n.fingerTable[i].group.id));
                    sb.append("(" + c.x + "," + c.y + ") -- " + "(" + f.x + "," + f.y + ")\n");
                }
            } catch (Exception ex) {
            }
        }
        sb.append(";\n");
        return sb.toString();
    }

    private static String ringStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("\\filldraw[fill=" + NODE_COLOR + ", draw=" + NODE_BORDER_COLOR + "]\n");
        for (ArrayList<StaticGroupsProtocol> l : Utils.GROUPS.values()) {
            try {
                StaticGroupsProtocol n = l.get(0);
                Coord c = nodeToCoord(n);
                sb.append("(" + c.x + "," + c.y + ") circle (" + c.size + ")");
            } catch (Exception ex) {
            }
        }
        sb.append(";\n");
        return sb.toString();
    }

    private static String neighStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("\\draw [" + NEIGH_COLOR + "]\n");
        for (ArrayList<StaticGroupsProtocol> l : Utils.GROUPS.values()) {
            try {
                StaticGroupsProtocol n = l.get(0);
                Coord c = nodeToCoord(n);
                Coord pred = nodeToCoord(Utils.getFirstNodeById(n.predecessor.id));
                Coord succ = nodeToCoord(Utils.getFirstNodeById(n.successor.id));
                sb.append("(" + c.x + "," + c.y + ") -- " + "(" + pred.x + "," + pred.y + ")\n");
                sb.append("(" + c.x + "," + c.y + ") -- " + "(" + succ.x + "," + succ.y + ")\n");
            } catch (Exception ex) {
            }
        }
        sb.append(";\n");
        return sb.toString();
    }

    private static String wrongConnectionsStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("\\draw [" + BAD_CONNECTIONS_COLOR + "]\n");
        for (ArrayList<StaticGroupsProtocol> l : Utils.GROUPS.values()) {
            try {
                StaticGroupsProtocol n = l.get(0);
                Coord c = nodeToCoord(n);
                for (int i = 0; i < n.M; i++) {
                    if (StaticGroupsTests.fingersTest(n, i) == false) {
                        Coord bad = nodeToCoord(Utils.getFirstNodeById(n.fingerTable[i].group.id));
                        sb.append("(" + c.x + "," + c.y + ") -- " + "(" + bad.x + "," + bad.y + ")\n");
                    }
                }
            } catch (Exception ex) {
            }
        }
        sb.append(";\n");
        return sb.toString();
    }

    private static String wrongPredecessorsStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("\\draw [" + BAD_PREDECESSORS_COLOR + "]\n");
        for (ArrayList<StaticGroupsProtocol> l : Utils.GROUPS.values()) {
            try {
                StaticGroupsProtocol n = l.get(0);
                Coord c = nodeToCoord(n);
                if (StaticGroupsTests.predecessorTest(n) == false) {
                    Coord bad = nodeToCoord(Utils.getFirstNodeById(n.predecessor.id));
                    sb.append("(" + c.x + "," + c.y + ") -- " + "(" + bad.x + "," + bad.y + ")\n");
                }
            } catch (Exception ex) {
            }
        }
        sb.append(";\n");
        return sb.toString();
    }

    private static String wrongSuccessorsStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("\\draw [" + BAD_SUCCESSORS_COLOR + "]\n");
        for (ArrayList<StaticGroupsProtocol> l : Utils.GROUPS.values()) {
            try {
                StaticGroupsProtocol n = l.get(0);
                Coord c = nodeToCoord(n);
                if (StaticGroupsTests.successorTest(n) == false) {
                    Coord bad = nodeToCoord(Utils.getFirstNodeById(n.successor.id));
                    sb.append("(" + c.x + "," + c.y + ") -- " + "(" + bad.x + "," + bad.y + ")\n");
                }
            } catch (Exception ex) {
            }
        }
        sb.append(";\n");
        return sb.toString();
    }

    private static Coord nodeToCoord(StaticGroupsProtocol n) throws Exception {
        double maxId = Math.pow(2, n.M);
        Coord coord = new Coord();
        coord.id = n.group.id.intValue();
        coord.x = R * Math.sin(Math.toRadians((360 / maxId) * coord.id));
        coord.y = R * Math.cos(Math.toRadians((360 / maxId) * coord.id));
        coord.size = NODE_SIZE * n.group.addresses.size();
        return coord;
    }

    static class Coord {
        int id;
        double x;
        double y;
        double size;
    }

}
