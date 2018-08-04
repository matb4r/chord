package com.mb;

import java.util.ArrayList;
import java.util.Collections;

public class Main {

    static final int m = 6;
    static ArrayList<Node> nodes = new ArrayList<>();

    static Node idToNode(int id) {
        for (Node n : nodes) {
            if (n.n == id) {
                return n;
            }
        }
        throw new RuntimeException("There is no Node with n=" + id);
    }

    public static void main(String[] args) {
        testJoin(true);
    }

    static void testJoin(boolean debug) {
        for (int i = 0; i < 1000; i++) {
            nodes.clear();
            Node n1 = new Node(1, "192.168.1.1");
            Node n3 = new Node(3, "192.168.1.3");
            Node n8 = new Node(8, "192.168.1.8");
            Node n11 = new Node(11, "192.168.1.11");
            Node n15 = new Node(15, "192.168.1.15");
            Node n22 = new Node(22, "192.168.1.22");
            Node n35 = new Node(35, "192.168.1.35");
            nodes.add(n1);
            nodes.add(n3);
            nodes.add(n8);
            nodes.add(n11);
            nodes.add(n15);
            nodes.add(n22);
            nodes.add(n35);
            Collections.shuffle(nodes);

            if (debug) {
                for (Node n : nodes) {
                    System.out.print(n.n + " ");
                }
                System.out.print("\n");
            }

            nodes.get(0).join(null);
            for (int j = 1; j < nodes.size(); j++) {
                nodes.get(j).join(nodes.get(j - 1));
            }
            Node.Finger[] fingers = n3.getFingerTable();
            testJoin(fingers[0].start, 4, debug);
            testJoin(fingers[1].start, 5, debug);
            testJoin(fingers[2].start, 7, debug);
            testJoin(fingers[3].start, 11, debug);
            testJoin(fingers[4].start, 19, debug);
            testJoin(fingers[5].start, 35, debug);
            testJoin(fingers[0].end, 4, debug);
            testJoin(fingers[1].end, 6, debug);
            testJoin(fingers[2].end, 10, debug);
            testJoin(fingers[3].end, 18, debug);
            testJoin(fingers[4].end, 34, debug);
            testJoin(fingers[5].end, 2, debug);
            testJoin(fingers[0].node, 8, debug);
            testJoin(fingers[1].node, 8, debug);
            testJoin(fingers[2].node, 8, debug);
            testJoin(fingers[3].node, 11, debug);
            testJoin(fingers[4].node, 22, debug);
            testJoin(fingers[5].node, 35, debug);
            System.out.println("  iteration " + i + " ok!");
        }
        System.out.println("Done! All ok!");
    }

    static void testJoin(int result, int expected, boolean debug) {
        if (result != expected) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Node n : nodes) {
                System.out.print(n.n + " ");
            }
            throw new RuntimeException("join: should get " + expected + " but got " + result);
        }
    }

    static void testFindSuccessor(boolean debug) {
        for (Node testNode : nodes) {
            System.out.println("Node " + testNode.n);
            testFindSuccessor(testNode, 0, 1, debug);
            testFindSuccessor(testNode, 1, 1, debug);
            testFindSuccessor(testNode, 2, 3, debug);
            testFindSuccessor(testNode, 3, 3, debug);
            testFindSuccessor(testNode, 4, 8, debug);
            testFindSuccessor(testNode, 5, 8, debug);
            testFindSuccessor(testNode, 6, 8, debug);
            testFindSuccessor(testNode, 7, 8, debug);
            testFindSuccessor(testNode, 8, 8, debug);
            testFindSuccessor(testNode, 9, 11, debug);
            testFindSuccessor(testNode, 10, 11, debug);
            testFindSuccessor(testNode, 11, 11, debug);
            testFindSuccessor(testNode, 12, 15, debug);
            testFindSuccessor(testNode, 13, 15, debug);
            testFindSuccessor(testNode, 14, 15, debug);
            testFindSuccessor(testNode, 15, 15, debug);
            testFindSuccessor(testNode, 16, 22, debug);
            testFindSuccessor(testNode, 17, 22, debug);
            testFindSuccessor(testNode, 18, 22, debug);
            testFindSuccessor(testNode, 19, 22, debug);
            testFindSuccessor(testNode, 20, 22, debug);
            testFindSuccessor(testNode, 21, 22, debug);
            testFindSuccessor(testNode, 22, 22, debug);
            testFindSuccessor(testNode, 23, 35, debug);
            testFindSuccessor(testNode, 24, 35, debug);
            testFindSuccessor(testNode, 25, 35, debug);
            testFindSuccessor(testNode, 26, 35, debug);
            testFindSuccessor(testNode, 27, 35, debug);
            testFindSuccessor(testNode, 28, 35, debug);
            testFindSuccessor(testNode, 29, 35, debug);
            testFindSuccessor(testNode, 30, 35, debug);
            testFindSuccessor(testNode, 31, 35, debug);
            testFindSuccessor(testNode, 32, 35, debug);
            testFindSuccessor(testNode, 33, 35, debug);
            testFindSuccessor(testNode, 34, 35, debug);
            testFindSuccessor(testNode, 35, 35, debug);
            testFindSuccessor(testNode, 36, 1, debug);
            testFindSuccessor(testNode, 37, 1, debug);
            testFindSuccessor(testNode, 38, 1, debug);
            testFindSuccessor(testNode, 39, 1, debug);
            testFindSuccessor(testNode, 40, 1, debug);
            testFindSuccessor(testNode, 41, 1, debug);
            testFindSuccessor(testNode, 42, 1, debug);
            testFindSuccessor(testNode, 43, 1, debug);
            testFindSuccessor(testNode, 44, 1, debug);
            testFindSuccessor(testNode, 45, 1, debug);
            testFindSuccessor(testNode, 46, 1, debug);
            testFindSuccessor(testNode, 47, 1, debug);
            testFindSuccessor(testNode, 48, 1, debug);
            testFindSuccessor(testNode, 49, 1, debug);
            testFindSuccessor(testNode, 50, 1, debug);
            testFindSuccessor(testNode, 51, 1, debug);
            testFindSuccessor(testNode, 52, 1, debug);
            testFindSuccessor(testNode, 53, 1, debug);
            testFindSuccessor(testNode, 54, 1, debug);
            testFindSuccessor(testNode, 55, 1, debug);
            testFindSuccessor(testNode, 56, 1, debug);
            testFindSuccessor(testNode, 57, 1, debug);
            testFindSuccessor(testNode, 58, 1, debug);
            testFindSuccessor(testNode, 59, 1, debug);
            testFindSuccessor(testNode, 60, 1, debug);
            testFindSuccessor(testNode, 61, 1, debug);
            testFindSuccessor(testNode, 62, 1, debug);
            testFindSuccessor(testNode, 63, 1, debug);
            System.out.println("  ok");
        }
        System.out.println("Done! All ok!");
    }

    static void testFindSuccessor(Node node, int find, int expected, boolean debug) {
        int x = node.find_successor(find);
        if (x != expected) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            throw new RuntimeException("find_successor(" + node.n + ", " + find + ", " + expected + "): should get " + expected + " but got " + x);
        } else {
            if (debug) {
                System.out.println("\t" + find + " -> " + expected);
            }
        }
    }

}
