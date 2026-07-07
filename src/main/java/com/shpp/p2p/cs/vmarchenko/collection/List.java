package com.shpp.p2p.cs.vmarchenko.collection;

public interface List<E> {
    boolean add(E element);

    boolean add(int index, E element);

    boolean remove(E element);

    E get(int index);
}
