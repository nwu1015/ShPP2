package com.shpp.p2p.cs.vmarchenko.collection;

public class LinkedListRealization<E> implements ListRealization<E> {

    Node<E> firstElement;
    Node<E> lastElement;

    Node<E> currentElement;
    Node<E> prevElement;

    @Override
    public void add(E element) {
        prevElement = currentElement;

        Node<E> newNode = new Node<>(element);

        currentElement = newNode;

        prevElement.setNext(newNode);
        newNode.setPrevious(prevElement);
        lastElement = newNode;
    }

    @Override
    public void add(int index, E element) {

    }

    @Override
    public boolean remove(E element) {
        return false;
    }

    @Override
    public E get(int index) {
        return null;
    }
}

class Node<E> {
    E value;

    Node<E> next;
    Node<E> previous;

    public Node(E value) {
        this.value = value;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public Node<E> getNext() {
        return next;
    }

    public void setNext(Node<E> next) {
        this.next = next;
    }

    public Node<E> getPrevious() {
        return previous;
    }

    public void setPrevious(Node<E> previous) {
        this.previous = previous;
    }
}
