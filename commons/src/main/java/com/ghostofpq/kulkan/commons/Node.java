package com.ghostofpq.kulkan.commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node<T> implements Comparable<Node<T>> {
    private Tree<T> tree;
    private T data;
    private Node<T> parent;
    private List<Node<T>> children;
    private int distanceFromTop;

    public Node(T data, Tree<T> tree, Node<T> parent, int distanceFromTop) {
        this.setTree(tree);
        this.setData(data);
        this.setParent(parent);
        this.setDistanceFromTop(distanceFromTop);
        this.setChildren(new ArrayList<Node<T>>());
    }

    public Node<T> addChild(T childData, int distance) {
        int childDistanceFromTop = (getDistanceFromTop() + distance);
        Node<T> child = new Node<T>(childData, getTree(), this, childDistanceFromTop);
        getChildren().add(child);
        return child;
    }

    public boolean contains(T element) {
        boolean result = false;

        if (getData().equals(element)) {
            result = true;
        } else {
            if (getChildren().isEmpty()) {
                result = false;
            } else {
                int i = 0;
                while (!result && i < getChildren().size()) {
                    result = getChildren().get(i).contains(element);
                    i++;
                }
            }
        }

        return result;
    }

    public List<Node<T>> find(T element) {
        List<Node<T>> result = new ArrayList<Node<T>>();
        if (getData().equals(element)) {
            result.add(this);
        }
        for (Node<T> child : getChildren()) {
            result.addAll(child.find(element));
        }
        return result;
    }

    public List<T> getPathToTop() {
        List<T> result = new ArrayList<T>();
        Node<T> node = this;
        while (null != node.getParent()) {
            result.add(node.getData());
            node = node.getParent();
        }
        return result;
    }

    public List<T> getPathFromTop() {
        List<T> result = getPathToTop();
        Collections.reverse(result);
        return result;
    }

    public List<T> getAllElements() {
        List<T> result = new ArrayList<T>();
        result.add(getData());
        for (Node<T> child : getChildren()) {
            result.addAll(child.getAllElements());
        }
        return result;
    }

    public void remove(T element) {
        for (int i = 0; i < getChildren().size(); i++) {
            if (getChildren().get(i).getData().equals(element)) {
                getChildren().remove(i);
                i--;
            }
        }
        for (Node<T> child : getChildren()) {
            child.remove(element);
        }
    }

    @Override
    public int compareTo(Node<T> other) {
        int res;
        if (getDistanceFromTop() < other.getDistanceFromTop()) {
            res = -1;
        } else if (getDistanceFromTop() > other.getDistanceFromTop()) {
            res = 1;
        } else {
            res = 0;
        }
        return res;
    }

    /*
     * GETTERS & SETTERS
     */

    public Tree<T> getTree() {
        return tree;
    }

    public void setTree(Tree<T> tree) {
        this.tree = tree;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Node<T> getParent() {
        return parent;
    }

    public void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public List<Node<T>> getChildren() {
        return children;
    }

    public void setChildren(List<Node<T>> children) {
        this.children = children;
    }

    public int getDistanceFromTop() {
        return distanceFromTop;
    }

    public void setDistanceFromTop(int distanceFromTop) {
        this.distanceFromTop = distanceFromTop;
    }
}



