package practica1.LinkedQ;

public class TestLQ {

  public static void main(String[] args) {
    LinkedQueue<Integer> q = new LinkedQueue<>();
    System.out.println("Queue: " + q);
    System.out.println("Size: " + q.size());
    q.put(1);
    q.put(2);
    q.put(3);
    System.out.println("Queue: " + q);
    System.out.println("Size: " + q.size());
    q.put(4);
    q.put(5);
    System.out.println("Queue: " + q);
    System.out.println("Size: " + q.size());
    q.get();
    q.get();
    System.out.println("Queue: " + q);
    System.out.println("Size: " + q.size());
    q.get();
    q.get();
    q.get();
    System.out.println("Queue: " + q);
    System.out.println("Size: " + q.size());
  }
}
