package practica1.LinkedQ;

import java.util.Iterator;
import util.Queue;

public class LinkedQueue<E> implements Queue<E> {

    private int size;
    private Node<E> first, last;

    public LinkedQueue() {
        this.size = 0;
        this.first = null;
        this.last = null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int free() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean empty() {
        return size == 0;
    }

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public E peekFirst() {
        return first.getValue();
    }

    @Override
    public E get() {
        E res = peekFirst();
        first = first.getNext();
        size--;
        return res;
    }

    @Override
    public void put(E e) {
        Node<E> new_node = new Node<>();
        new_node.setValue(e);
        new_node.setNext(null);

        if (this.empty()) {
            first = new_node;
        } else {
            last.setNext(new_node);
        }

        last = new_node;
        size++;
    }

    @Override
    public String toString() {
        String res = "[";

        for (Node<E> node = first; node != null; node = node.getNext()) {
            res += node.getValue();
            if (node.getNext() != null)
                res += ", ";
        }

        return res + "]";
    }

    @Override
    public Iterator<E> iterator() {
        return new MyIterator();
    }

    class MyIterator implements Iterator {

        Node<E> ppnode, pnode, node;
        
        public MyIterator() {
            ppnode = pnode = null;
            node = first;
        }

        @Override
        public boolean hasNext() {
            return node != null;
        }

        @Override
        public E next() {
            E res = node.getValue();
            ppnode = pnode;
            pnode = node;
            node = node.getNext();
            return res;
        }

        @Override
        public void remove() {
            if (node == null)
                last = ppnode;
            
            if (ppnode == null)
                first = node;
            else
                ppnode.setNext(node);
            
            pnode = ppnode;
        }
    }
}
