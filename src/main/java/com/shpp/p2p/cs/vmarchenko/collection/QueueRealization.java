package com.shpp.p2p.cs.vmarchenko.collection;

import java.util.NoSuchElementException;

public class QueueRealization<E> {
    private final LinkedListRealization<E> list = new LinkedListRealization<>();
    private int size = 0;

    public void enqueue(E element) {
        list.add(element);
        size++;
    }

    public E dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        E element = list.get(0);
        list.remove(element);
        size--;
        return element;
    }

    public E peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return list.get(0);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }
}