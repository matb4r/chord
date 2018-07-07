package com.mb;

import java.util.ArrayList;

public class Main {

    static final int[] ids = {1, 3, 8, 11, 15, 22, 35};
    static final int m = 6;
    static ArrayList<Node> nodes = initNodes();

    private static ArrayList<Node> initNodes() {
        ArrayList<Node> nodes = new ArrayList<>();
        for (int id : ids) {
            nodes.add(new Node(id));
        }
        return nodes;
    }

    static Node idToNode(int id) {
        for (Node n : nodes) {
            if (n.n == id) {
                return n;
            }
        }
        throw new RuntimeException("There is no Node with n=" + id);
    }

    public static void main(String[] args) {
        testFindSuccessor(false);
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
