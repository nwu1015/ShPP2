package com.shpp.p2p.cs.vmarchenko.collection;

public class ArrayListRealization<E> implements ListRealization<E> {
    public static final int DEFAULT_CAPACITY = 10;
    public static final int QUANTITY_FOR_EXPANSION = 5;

    private E[] list;

    private static int index = 0;

    public ArrayListRealization() {
        list = (E[]) new Object[DEFAULT_CAPACITY];
    }

    @Override
    public void add(E element) {
        if(index == list.length) {
            E[] copyList = list;
            list = (E[]) new Object[list.length + QUANTITY_FOR_EXPANSION];
            System.arraycopy(copyList, 0, list, 0, copyList.length);
        }

        list[index] = element;
        index++;
    }

    @Override
    public void add(int index, E element) {
        if(index == list.length - 1) {
            E[] copyList = list;
            list = (E[]) new Object[list.length + QUANTITY_FOR_EXPANSION];
            System.arraycopy(copyList, 0, list, 0, copyList.length);
        }

        E[] newList = (E[]) new Object[list.length];
        if (index >= 0) System.arraycopy(list, 0, newList, 0, index);
        newList[index] = element;
        System.arraycopy(list, index, newList, index, list.length - index);

        list = newList;
    }

    @Override
    public boolean remove(Object element) {

        int indexToRemove = -1;
        E[] newList = (E[]) new Object[list.length];

        for(int i = 0; i < list.length; i++) {
            if(list[i].equals(element)) {
                indexToRemove = i;
            }
        }
        if(indexToRemove != -1) {
            System.arraycopy(list, 0, newList, 0, indexToRemove);
            if (list.length - (indexToRemove + 1) >= 0)
                System.arraycopy(list, indexToRemove + 1, newList, indexToRemove + 1, list.length - (indexToRemove + 1));
        }

        list = newList;
        return true;
    }

    @Override
    public E get(int index) {
        if(index < 0 || index >= list.length) {
            return null;
        }
        return list[index];
    }
}