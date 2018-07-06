package com.mb;

import java.util.ArrayList;

import static com.mb.Main.idToNode;
import static com.mb.Main.m;

class Node {

    public int n; // node/group id
    String ip; // node ip
    private ArrayList<String> ips; // all ips in group

    private Finger[] fingerTable;

    private int successor;
    private ArrayList<String> successorIps;
    private int predecessor;
    private ArrayList<String> predecessorIps;

    Node(int n) {
        this(n, null);
    }

    Node(int n, String ip) {
        this.n = n;
        this.ip = ip;
        this.ips = new ArrayList<>();
        this.fingerTable = new Finger[m];
        this.successorIps = new ArrayList<>();
        this.predecessorIps = new ArrayList<>();
    }

//    Node(int n) {
//        for (int i = 1; i <= m; i++) {
//            fingerTable[i - 1] = new Finger();
//            fingerTable[i - 1].i = i;
//            fingerTable[i - 1].start = (n + (int) Math.pow(2, i - 1)) % (int) Math.pow(2, m);
//            fingerTable[i - 1].end = (n + (int) Math.pow(2, i) - 1) % (int) Math.pow(2, m);
//            fingerTable[i - 1].node = successor(fingerTable[i - 1].start);
//        }
//        this.successor = fingerTable[0].node;
//        this.predecessor = predecessor(n);
//    }

//    private static int successor(int id) {
//        for (int i = 0; i < ids.length - 1; i++) {
//            if (ids[i + 1] >= id && ids[i] < id) {
//                return ids[i + 1];
//            }
//        }
//        return ids[0];
//    }
//
//    private int predecessor(int id) {
//        for (int i = 1; i < ids.length; i++) {
//            if (this.n == ids[i]) {
//                return ids[i - 1];
//            }
//        }
//        return ids[ids.length - 1];
//    }

    // lookups

    public int find_successor(int id) {
        if (id >= Math.pow(2, m)) {
            throw new RuntimeException("Id [" + id + "] too big for the identifier space");
        }
        if (id > predecessor && id <= n) {
            return n;
        }
        int np = find_predecessor(id);
        return idToNode(np).successor;
    }

    private int find_predecessor(int id) {
        int np = this.n;
        while (!isIdBetweenNAndNSucc(id, np, idToNode(np).successor)) {
            np = idToNode(np).closest_preceding_finger(id);
        }
        return np;
    }

    private boolean isIdBetweenNAndNSucc(int id, int np, int np_succ) {
        if (np < np_succ) {
            return id > np && id <= np_succ;
        } else {
            return id > np || id <= np_succ;
        }
    }

    private int closest_preceding_finger(int id) {
        for (int i = m - 1; i >= 0; i--) {
            if (n == id) {
                return this.predecessor;
            } else if (n > id) {
                if (fingerTable[i].node > this.n || fingerTable[i].node < id) {
                    return fingerTable[i].node;
                }
            } else {
                if (fingerTable[i].node > this.n && fingerTable[i].node < id) {
                    return fingerTable[i].node;
                }
            }
        }
        return this.predecessor;
    }

    // joins

    public void join(Node node) {
        if (node != null) {
            init_finger_table(node);
            update_others();
        } else {
            for (int i = 1; i <= m; i++) {
                fingerTable[i - 1] = new Finger();
                fingerTable[i - 1].i = i;
                fingerTable[i - 1].node = n;
                fingerTable[i - 1].start = (n + (int) Math.pow(2, i - 1)) % (int) Math.pow(2, m);
                fingerTable[i - 1].end = (n + (int) Math.pow(2, i) - 1) % (int) Math.pow(2, m);
            }
            predecessor = n;
            successor = fingerTable[0].node;
            // todo what about that?
//            predecessorIps = ips;
//            successor = n;
//            successorIps = ips;
        }
    }

    public void init_finger_table(Node node) {
        fingerTable[0] = new Finger();
        fingerTable[0].i = 0;
        fingerTable[0].start = (n + (int) Math.pow(2, 0)) % (int) Math.pow(2, m);
        fingerTable[0].end = (n + (int) Math.pow(2, 1) - 1) % (int) Math.pow(2, m);
        fingerTable[0].node = node.find_successor(fingerTable[0].start);
        successor = fingerTable[0].node;
        predecessor = idToNode(successor).predecessor;
        idToNode(successor).predecessor = n;
        idToNode(successor).predecessorIps.add(ip);
        idToNode(predecessor).successor = n;
        idToNode(predecessor).successorIps.add(ip);
        for (int i = 2; i <= m; i++) {
            fingerTable[i - 1] = new Finger();
            fingerTable[i - 1].i = i;
            fingerTable[i - 1].start = (n + (int) Math.pow(2, i - 1)) % (int) Math.pow(2, m);
            fingerTable[i - 1].end = (n + (int) Math.pow(2, i) - 1) % (int) Math.pow(2, m);
            if (fingerTable[i - 1].start >= n && fingerTable[i - 1].start < fingerTable[i - 2].node) {
                fingerTable[i - 1].node = fingerTable[i - 2].node;
            } else {
                fingerTable[i - 1].node = node.find_successor(fingerTable[i - 1].start);
            }
        }
    }

    public void update_others() {
        for (int i = 1; i <= m; i++) {
            int p = find_predecessor(n - (int) Math.pow(2, i - 1));
            idToNode(p).update_finger_table(n, i - 1);
        }
    }

    public void update_finger_table(int s, int i) {
        if (s >= n && s < fingerTable[i].node) {
            fingerTable[i].node = s;
            int p = predecessor;
            idToNode(p).update_finger_table(s, i);
        }
    }

    public int getSuccessor() {
        return fingerTable[0].node;
    }

    class Finger {
        int i;
        int start;
        int end;
        int node;
        ArrayList<String> ips;
    }
}
