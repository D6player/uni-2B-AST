package practica1.CircularQ;

public class TestCQ_1 {

    public static void main(String[] args) {
        int dist_1 = 0;
        int dist_2 = 0;

        while (dist_1 < 10 && dist_2 < 10) {
            int corredor = (int) Math.floor(Math.random() * 2 + 1);
            int distancia = (int) Math.floor(Math.random() * 3 + 1);

            if (corredor == 1) {
                dist_1 = (distancia = distancia + dist_1);
            } else {
                dist_2 = (distancia = distancia + dist_2);
            }

            System.out.println("Corredor " + corredor + " ha recorregut " + distancia + "m");
        }

        System.out.println("Cursa acabada");
    }
}
