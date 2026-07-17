package com.shpp.p2p.cs.vmarchenko.collection;

import java.util.EmptyStackException;

public class StackRealization<E> {
    private final LinkedListRealization<E> list = new LinkedListRealization<>();
    private int size = 0;

    public void push(E element) {
        list.add(element);
        size++;
    }

    public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        E element = list.get(size - 1);
        list.remove(element);
        size--;
        return element;
    }

    public E peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return list.get(size() - 1);
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size;
    }
}