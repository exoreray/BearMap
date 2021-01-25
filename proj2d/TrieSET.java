
package proj2d;

//@source TriSET https://algs4.cs.princeton.edu/52trie/shellsST.txt

import java.util.HashSet;
import java.util.Set;

public class TrieSET {
    private static final int R = 27;        // extended ASCII

    private Node root;      // root of trie

    // R-way trie node
    private static class Node {
        private Node[] next = new Node[R];
        private boolean isString;
    }

    public TrieSET() {
    }

    public boolean contains(String key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to contains() is null");
        }
        Node x = get(root, key, 0);
        if (x == null) {
            return false;
        }
        return x.isString;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) {
            return null;
        }
        if (d == key.length()) {
            return x;
        }
        char c = key.charAt(d);
        if (c == (char) 32) {
            c = (char) 123;
        }
        return get(x.next[c - 97], key, d + 1);
    }

    public void add(String key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to add() is null");
        }
        root = add(root, key, 0);
    }

    private Node add(Node x, String key, int d) {
        if (x == null) {
            x = new Node();
        }
        if (d == key.length()) {
            x.isString = true;
        } else {
            char c = key.charAt(d);
            if (c == (char) 32) {
                c = (char) 123;
            }
            x.next[c - 97] = add(x.next[c - 97], key, d + 1);
        }
        return x;
    }

    public Iterable<String> keysWithPrefix(String prefix) {
        Set<String> results = new HashSet<>();
        Node x = get(root, prefix, 0);
        collect(x, new StringBuilder(prefix), results);
        return results;
    }

    private void collect(Node x, StringBuilder prefix, Set<String> results) {
        if (x == null) {
            return;
        }
        if (x.isString) {
            results.add(prefix.toString());
        }
        for (char c = 97; c < R + 97; c++) {
            if (c == (char) 123) {
                c = (char) 32;
            }
            prefix.append(c);
            if (c == (char) 32) {
                c = (char) 123;
            }
            collect(x.next[c - 97], prefix, results);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }
}
