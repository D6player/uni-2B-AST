package practica1.CircularQ;

import java.util.Iterator;
import util.Queue;

public class CircularQueue<E> implements Queue<E> {

    private final E[] queue;
    private final int N;
    private int first, last, size;

    public CircularQueue(int N) {
        this.N = N;
        queue = (E[]) (new Object[N]);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public int free() {
        return this.N - this.size;
    }

    @Override
    public boolean empty() {
        return this.size == 0;
    }

    @Override
    public boolean full() {
        return this.size == this.N;
    }

    @Override
    public E peekFirst() {
        assert !empty();
        return this.queue[this.first];
    }

    @Override
    public E get() {
        E res = this.peekFirst();
        this.first++;
        this.first %= this.N;
        this.size--;
        return res;
    }

    @Override
    public void put(E e) {
        assert !full();
        this.queue[this.last] = e;
        this.last++;
        this.size++;
        this.last %= this.N;
    }

    @Override
    public String toString() {
        String res = "[";
        for (int i = 0; i < this.size; i++) {
            res += this.queue[(this.first + i)%this.N];
            if (i != this.size - 1)
                res += ", ";
        }
        
        return res + "]";
    }

    @Override
    public Iterator<E> iterator() {
        return new MyIterator();
    }

    class MyIterator implements Iterator {

        private int i;
        
        public MyIterator() {
            this.i = 0;
        }

        @Override
        public boolean hasNext() {
            return this.i < size;
        }

        @Override
        public E next() {
            return queue[(first + this.i++)%N];
        }

        @Override
        public void remove() {
            size--;
            for (int j = --this.i; j < size; j++)
                queue[(first + j)%N] = queue[(first + j + 1)%N];
            
            last += N - 1;
            last %= N;
        }

    }
}
