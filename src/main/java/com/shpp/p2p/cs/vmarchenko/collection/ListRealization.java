package com.shpp.p2p.cs.vmarchenko.collection;

public interface ListRealization<E> {
    void add(E element);

    void add(int index, E element);

    boolean remove(E element);

    E get(int index);
}
