package com.ghostofpq.kulkan.commons;

import java.util.*;

/**
 * Element of @link{com.ghostofpq.kulkan.commons.Tree}.<br/>
 * Implement @link{Comparable} on the distance to the root Node.
 *
 * @param <T> Type of the content.
 */
public class Node<T> implements Comparable<Node<T>> {
    /**
     * @link{com.ghostofpq.kulkan.commons.Tree} in which the Node is contained.
     */
    private Tree<T> tree;
    /**
     * Content.
     */
    private T data;
    /**
     * Parent.
     */
    private Node<T> parent;
    /**
     * Children.
     */
    private List<Node<T>> children;
    /**
     * Distance between this Node and the root Node.<br/>
     * (Not necessarily the number of element between this Node and the root Node)
     */
    private int distanceFromTop;

    /**
     * Constructor
     *
     * @param data            Node content
     * @param tree            Tree containing this Node
     * @param parent          Parent Node
     * @param distanceFromTop Distance from root Node
     */
    public Node(T data, Tree<T> tree, Node<T> parent, int distanceFromTop) {
        this.setTree(tree);
        this.setData(data);
        this.setParent(parent);
        this.setDistanceFromTop(distanceFromTop);
        this.setChildren(new ArrayList<Node<T>>());
    }

    /**
     * Create a child Node to this Node.
     *
     * @param childData Node content
     * @param distance  Distance to this Node
     * @return created Node
     */
    public Node<T> addChild(T childData, int distance) {
        int childDistanceFromTop = (getDistanceFromTop() + distance);
        Node<T> child = new Node<T>(childData, getTree(), this, childDistanceFromTop);
        getChildren().add(child);
        return child;
    }

    /**
     * Checks recursively if the element is in a subtree with this Node as the root Node.
     *
     * @param element element to search.
     * @return true if the element is contained.
     */
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

    /**
     * Searches recursively the Nodes where the data field is equal to the element.
     *
     * @param element element to search
     * @return the List of Node where the the data field is equal to the element.
     */
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

    /**
     * Assemble recursively the path to the root Node.
     *
     * @return the path to the top as a List of Nodes
     */
    public List<T> getPathToTop() {
        List<T> result = new ArrayList<T>();
        Node<T> node = this;
        while (null != node.getParent()) {
            result.add(node.getData());
            node = node.getParent();
        }
        return result;
    }

    /**
     * Get the path from the root Node to this Node.
     *
     * @return the path from the top as a List of Nodes
     */
    public List<T> getPathFromTop() {
        List<T> result = getPathToTop();
        Collections.reverse(result);
        return result;
    }

    /**
     * Get all the element contained in the subtree with this Node as the root Node.
     *
     * @return all the element contained as a List of element.
     */
    public Set<T> getAllElements() {
        Set<T> result = new LinkedHashSet<T>();
        result.add(getData());
        for (Node<T> child : getChildren()) {
            result.addAll(child.getAllElements());
        }
        return result;
    }

    /**
     * Remove recursively all the Node where the data field is equal to the element.
     *
     * @param element element to search
     */
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
    /**
     * Compare two Nodes on the distance to the top. <br/>
     * If the distance is superior, then the Node is superior.
     */
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



