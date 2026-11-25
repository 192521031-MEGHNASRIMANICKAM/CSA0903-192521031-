import java.util.*;

public class Starvation {
    public static void main(String[] args) {
        int ticks = 60;
        // Plain priority queue (severity, then arrival)
        PriorityQueue<FairTriage.P> plain = new PriorityQueue<>(Comparator.comparingInt((FairTriage.P p) -> p.sev).thenComparingLong(p -> p.arrival));
        plain.add(new FairTriage.P("Asha", 5, 0));
        int plainServed = -1;
        for (int t = 0; t < ticks; t++) {
            plain.add(new FairTriage.P("A" + t, 2, t)); plain.add(new FairTriage.P("B" + t, 2, t));   // 2 severe arrivals per tick
            FairTriage.P p = plain.poll();                                                              // doctor sees 1 per tick
            if (p.name.equals("Asha")) { plainServed = t; break; }
        }
        System.out.println("Plain PriorityQueue : 2 severity-2 arrivals per tick, 1 patient seen per tick");
        System.out.println("  Asha (S5, arrived tick 0) served at tick: " + (plainServed < 0 ? "NEVER within " + ticks + " ticks (waiting: " + plain.size() + ")" : plainServed));

        for (long T : new long[]{5, 10}) {
            FairTriage fair = new FairTriage(T);
            fair.admit("Asha", 5, 0);
            int served = -1;
            for (int t = 0; t < ticks; t++) {
                fair.admit("A" + t, 2, t); fair.admit("B" + t, 2, t);
                FairTriage.P p = fair.next(t);
                if (p.name.equals("Asha")) { served = t; break; }
            }
            System.out.println("Fair (aging, T=" + T + " ticks per level): Asha served at tick " + (served < 0 ? "NEVER" : served)
                + "   [Asha reaches severity 2 after " + 3 * T + " ticks, severity 1 after " + 4 * T + "]");
        }
    }
}
