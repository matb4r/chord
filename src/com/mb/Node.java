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
        this.ips.add(this.ip);
        this.fingerTable = new Finger[m];
        this.successorIps = new ArrayList<>();
        this.predecessorIps = new ArrayList<>();
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
        fingerTable[0].i = 1;
        fingerTable[0].start = (n + (int) Math.pow(2, 0)) % (int) Math.pow(2, m);
        fingerTable[0].end = (n + (int) Math.pow(2, 1) - 1) % (int) Math.pow(2, m);
        fingerTable[0].node = node.find_successor(fingerTable[0].start);
        successor = fingerTable[0].node;
        predecessor = idToNode(successor).predecessor;
        idToNode(successor).predecessor = n;
        idToNode(successor).predecessorIps = ips;
        idToNode(predecessor).successor = n;
        idToNode(predecessor).successorIps = ips;
        for (int i = 2; i <= m; i++) {
            fingerTable[i - 1] = new Finger();
            fingerTable[i - 1].i = i;
            fingerTable[i - 1].start = (n + (int) Math.pow(2, i - 1)) % (int) Math.pow(2, m);
            fingerTable[i - 1].end = (n + (int) Math.pow(2, i) - 1) % (int) Math.pow(2, m);
            if (n > fingerTable[i - 2].node) {
                if (fingerTable[i - 1].start >= n || fingerTable[i - 1].start <= fingerTable[i - 2].node) {
                    fingerTable[i - 1].node = fingerTable[i - 2].node;
                } else {
                    fingerTable[i - 1].node = node.find_successor(fingerTable[i - 1].start);
                }
            } else {
                if (fingerTable[i - 1].start >= n && fingerTable[i - 1].start <= fingerTable[i - 2].node) {
                    fingerTable[i - 1].node = fingerTable[i - 2].node;
                } else {
                    fingerTable[i - 1].node = node.find_successor(fingerTable[i - 1].start);
                }
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
        for (int j = 1; j <= m; j++) {
            fingerTable[j - 1].node = find_successor(fingerTable[j - 1].start);
        }
        if (s >= n && s <= fingerTable[i].node) {
            fingerTable[i].node = s;
            int p = predecessor;
            // condition to prevent stack overflow in 1 -> 35 -> ... combination
            if (p != successor) {
                idToNode(p).update_finger_table(s, i);
            }
        }
    }

    public Finger[] getFingerTable() {
        return fingerTable;
    }

    class Finger {
        int i;
        int start;
        int end;
        int node;
        ArrayList<String> ips;
    }
}
