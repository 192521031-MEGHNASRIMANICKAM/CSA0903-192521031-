import java.util.*;

public class TriageBench {
    static long sink;
    public static void main(String[] args) {
        System.out.println("PriorityQueue triage: average cost per operation (microseconds), 5 runs, random severities 1-5");
        System.out.printf("%-10s %16s %18s %22s%n", "n", "admit (us/op)", "next() (us/op)", "predicted shape");
        System.out.println("-".repeat(70));
        for (int w = 0; w < 3; w++) run(20000);
        double prevA = 0, prevN = 0;
        for (int n : new int[]{1000, 10000, 100000, 1000000}) {
            double a = 0, nx = 0;
            for (int r = 0; r < 5; r++) { double[] x = run(n); a += x[0] / 5; nx += x[1] / 5; }
            System.out.printf("%-10d %16.3f %18.3f %22s%n", n, a, nx, "add O(log n), poll O(log n)");
        }
        System.out.println();
        int perDay = 5000;
        double[] x = run(perDay);
        System.out.printf("City hospital load (%d patients/day): admit all + serve all = %.2f ms total%n", perDay, (x[0] + x[1]) * perDay / 1000.0);
    }
    static double[] run(int n) {
        Triage er = new Triage();
        Random rnd = new Random(3);
        long t = System.nanoTime();
        for (int i = 0; i < n; i++) er.admit("P" + i, 1 + rnd.nextInt(5));
        double add = (System.nanoTime() - t) / 1e3 / n;
        t = System.nanoTime();
        Patient p; while ((p = er.next()) != null) sink += p.severity;
        double nx = (System.nanoTime() - t) / 1e3 / n;
        return new double[]{add, nx};
    }
}
