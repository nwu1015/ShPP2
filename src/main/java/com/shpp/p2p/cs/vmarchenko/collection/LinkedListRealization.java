package com.shpp.p2p.cs.vmarchenko.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class LinkedListRealization<E> implements ListRealization<E> {
    Node<E> firstElement;
    Node<E> lastElement;

    private int size = 0;

    private static class Node<E> {
        private E value;
        private Node<E> next;
        private Node<E> previous;

        public Node(Node<E> previous, E value, Node<E> next) {
            this.value = value;
            this.next = next;
            this.previous = previous;
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

    @Override
    public void add(E element) {
        Node<E> l = lastElement;
        Node<E> newNode = new Node<>(l, element, null);
        lastElement = newNode;

        if (l == null) {
            firstElement = newNode;
        } else {
            l.setNext(newNode);
        }
        size++;

    }

    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        if (index == size) {
            add(element);
        } else {
            Node<E> succ = node(index);
            Node<E> pred = succ.getPrevious();
            Node<E> newNode = new Node<>(pred, element, succ);
            succ.setPrevious(newNode);

            if (pred == null) {
                firstElement = newNode;
            } else {
                pred.setNext(newNode);
            }
            size++;
        }
    }

    private Node<E> node(int index) {
        if (index < (size >> 1)) {
            Node<E> x = firstElement;
            for (int i = 0; i < index; i++) {
                x = x.getNext();
            }
            return x;
        } else {
            Node<E> x = lastElement;
            for (int i = size - 1; i > index; i--) {
                x = x.getPrevious();
            }
            return x;
        }
    }

    @Override
    public boolean remove(E element) {
        for (Node<E> x = firstElement; x != null; x = x.getNext()) {
            if (Objects.equals(element, x.getValue())) {
                unlink(x);
                return true;
            }
        }
        return false;
    }

    private void unlink(Node<E> x) {
        Node<E> next = x.getNext();
        Node<E> prev = x.getPrevious();

        if (prev == null) {
            firstElement = next;
        } else {
            prev.setNext(next);
            x.setPrevious(null);
        }

        if (next == null) {
            lastElement = prev;
        } else {
            next.setPrevious(prev);
            x.setNext(null);
        }

        x.setValue(null);
        size--;
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node<E> x = firstElement;
        for (int i = 0; i < index; i++) {
            x = x.getNext();
        }
        return x.getValue();
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private Node<E> nextNode = firstElement;

            @Override
            public boolean hasNext() {
                return nextNode != null;
            }

            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                E value = nextNode.getValue();
                nextNode = nextNode.getNext();
                return value;
            }
        };
    }
}
