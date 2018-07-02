package com.mb;

import java.util.ArrayList;

import static com.mb.Main.*;

class Node {

    String ip; // node ip

    public int n; // node/group id
    ArrayList<String> ips; // all ips in group

    private Finger[] fingerTable;

    private int successor;
    private ArrayList<String> successorIps;
    private int predecessor;
    private ArrayList<String> predecessorIps;

    Node(int n) {
        this.n = n;
        this.fingerTable = new Finger[m];
        for (int i = 1; i <= m; i++) {
            fingerTable[i - 1] = new Finger();
            fingerTable[i - 1].i = i;
            fingerTable[i - 1].start = (n + (int) Math.pow(2, i - 1)) % (int) Math.pow(2, m);
            fingerTable[i - 1].end = (n + (int) Math.pow(2, i) - 1) % (int) Math.pow(2, m);
            fingerTable[i - 1].node = successor(fingerTable[i - 1].start);
        }
        this.successor = fingerTable[0].node;
        this.predecessor = predecessor(n);
    }

    private static int successor(int id) {
        for (int i = 0; i < ids.length - 1; i++) {
            if (ids[i + 1] >= id && ids[i] < id) {
                return ids[i + 1];
            }
        }
        return ids[0];
    }

    private int predecessor(int id) {
        for (int i = 1; i < ids.length; i++) {
            if (this.n == ids[i]) {
                return ids[i - 1];
            }
        }
        return ids[ids.length - 1];
    }

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

    class Finger {
        int i;
        int start;
        int end;
        int node;
        ArrayList<String> ips;
    }
}
