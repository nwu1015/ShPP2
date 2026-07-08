package com.shpp.p2p.cs.vmarchenko.collection;


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class ArrayListRealization<E> implements ListRealization<E> {
    public static final int DEFAULT_CAPACITY = 10;
    public static final int QUANTITY_FOR_EXPANSION = 2;

    private E[] list;

    private int size = 0;

    @SuppressWarnings("unchecked")
    public ArrayListRealization() {
        list = (E[]) new Object[DEFAULT_CAPACITY];
    }

    @Override
    public void add(E element) {
        ensureCapacity();

        list[size++] = element;
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size == list.length) {
            int newCapacity = list.length * QUANTITY_FOR_EXPANSION;
            E[] newList = (E[]) new Object[newCapacity];
            System.arraycopy(list, 0, newList, 0, list.length);
            list = newList;
        }
    }

    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ensureCapacity();
        System.arraycopy(list, index, list, index + 1, size - index);
        list[index] = element;
        size++;
    }

    @Override
    public boolean remove(Object element) {
        for (int i = 0; i < size; i++) {
            if ((element == null && list[i] == null) || (element != null && element.equals(list[i]))) {
                fastRemove(i);
                return true;
            }
        }
        return false;
    }

    private void fastRemove(int index) {
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(list, index + 1, list, index, numMoved);
        }
        list[--size] = null;
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return list[index];
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public E next() {
                if (cursor >= size) {
                    throw new NoSuchElementException();
                }
                return list[cursor++];
            }
        };
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        ListRealization.super.forEach(action);
    }
}