package practica1.CircularQ;

public class TestCQ {

  public static void main(String[] args) {
    CircularQueue<Integer> q = new CircularQueue<>(5);
    System.out.println("Empty queue: " + q);
    System.out.println("Size: " + q.size());
    q.put(1);
    q.put(2);
    q.put(3);
    System.out.println("Half-full queue: " + q);
    System.out.println("Size: " + q.size());
    q.put(4);
    q.put(5);
    System.out.println("Full queue: " + q);
    System.out.println("Size: " + q.size());
    q.get();
    q.get();
    System.out.println("Half-full queue: " + q);
    System.out.println("Size: " + q.size());
    q.put(6);
    q.put(7);
    System.out.println("Full queue: " + q);
    System.out.println("Size: " + q.size());
    q.get();
    q.get();
    q.get();
    q.get();
    q.get();
    System.out.println("Empty queue: " + q);
    System.out.println("Size: " + q.size());
  }
}
