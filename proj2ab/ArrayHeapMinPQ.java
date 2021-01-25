package proj2ab;

import java.util.HashMap;
import java.util.NoSuchElementException;

public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    private T[] pq;
    private int size;
    private HashMap<T, Double[]> maps;

    public ArrayHeapMinPQ() {
        this(1);
    }

    private ArrayHeapMinPQ(int initCapacity) {
        pq = (T[]) new Object[initCapacity + 1];
        maps = new HashMap<>();
        size = 0;
    }

    @Override
    public void add(T item, double priority) {
        Double[] temp = new Double[2];
        temp[0] = priority;
        temp[1] = (double) size + 1;
        maps.put(item, temp);

        if (size == pq.length - 1) {
            resize(2 * pq.length);
        }
        pq[++size] = item;
        swim(size);
    }

    private void resize(int capacity) {
        assert capacity > size;
        T[] temp = (T[]) new Object[capacity];
        if (size >= 0) {
            System.arraycopy(pq, 1, temp, 1, size);
        }
        pq = temp;
    }

    private void swim(int k) {
        while (k > 1 && greater(k / 2, k)) {
            exchange(k, k / 2);
            k = k / 2;
        }
    }

    private boolean greater(int i, int k) {
        return maps.get(pq[i])[0] > maps.get(pq[k])[0];
    }

    private void exchange(int i, int k) {
        double temp = maps.get(pq[i])[1];
        maps.get(pq[i])[1] = maps.get(pq[k])[1];
        maps.get(pq[k])[1] = temp;

        T swap = pq[i];
        pq[i] = pq[k];
        pq[k] = swap;
    }

    @Override
    public boolean contains(T item) {
        return maps.containsKey(item);
    }

    @Override
    public T getSmallest() {
        if (size == 0) {
            throw new NoSuchElementException("Priority queue underflow");
        }
        return pq[1];
    }

    @Override
    public T removeSmallest() {
        if (size == 0) {
            throw new NoSuchElementException("Priority queue underflow");
        }
        T min = pq[1];
        exchange(1, size--);
        sink(1);
        pq[size + 1] = null;     // to avoid loiterig and help with garbage collection
        if ((size > 0) && (size == (pq.length - 1) / 4)) {
            resize(pq.length / 2);
        }
        //assert isMinHeap();
        return min;
    }

    private void sink(int k) {
        while (2 * k <= size) {
            int j = 2 * k;
            if (j < size && greater(j, j + 1)) {
                j++;
            }
            if (!greater(k, j)) {
                break;
            }
            exchange(k, j);
            k = j;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException();
        }
        double prevPriority = maps.get(item)[0];
        Double[] temp = new Double[2];
        temp[0] = priority;
        temp[1] = maps.get(item)[1];
        maps.replace(item, temp);
        int itemPos = (int) Math.round(maps.get(item)[1]);
        if (prevPriority < priority) {
            sink(itemPos);
        } else {
            swim(itemPos);
        }
    }
}
