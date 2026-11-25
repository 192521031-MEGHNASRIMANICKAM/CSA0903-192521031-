import java.util.*;

/** Fair triage by AGING: effective severity = max(1, severity - waited/T). Re-heapifies the whole queue on every poll. */
public class FairTriage {
    static class P { final String name; final int sev; final long arrival; P(String n, int s, long a) { name = n; sev = s; arrival = a; } public String toString() { return name + "(S" + sev + ")"; } }
    static long comparisons = 0, touched = 0;
    private PriorityQueue<P> q = new PriorityQueue<>(Comparator.comparingLong((P p) -> p.arrival));
    private final long T;
    FairTriage(long T) { this.T = T; }

    int eff(P p, long clock) { return (int) Math.max(1, p.sev - (clock - p.arrival) / T); }
    void admit(String name, int sev, long clock) { q.add(new P(name, sev, clock)); }
    int size() { return q.size(); }

    P next(long clock) {
        if (q.isEmpty()) return null;
        PriorityQueue<P> rebuilt = new PriorityQueue<>((a, b) -> {
            comparisons++;
            int c = Integer.compare(eff(a, clock), eff(b, clock));
            return c != 0 ? c : Long.compare(a.arrival, b.arrival);
        });
        touched += q.size();
        rebuilt.addAll(q);                 // every waiting patient re-inserted: O(n) at least
        P best = rebuilt.poll();
        q = rebuilt;
        return best;
    }
}
