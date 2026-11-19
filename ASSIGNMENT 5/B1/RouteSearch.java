
import java.io.*;

public class RouteSearch {

    public static void main(String[] args) throws IOException {

        String filename = "passes.csv";
        int route = 12;

        try (BufferedReader br = new BufferedReader(
                new FileReader(filename))) {

            String line;

            System.out.println("Bus Passes for Route " + route);

            while ((line = br.readLine()) != null) {

                String[] p = line.split(",", -1);

                if (p.length != 5) continue;

                try {
                    if (Integer.parseInt(p[3]) == route) {
                        System.out.println(line);
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }
    }
}
