import java.util.*;

/** REDESIGN: five FIFO buckets (one per severity). Same aging rule, but only the 5 queue HEADS are inspected per poll.
    Exploits: within a bucket, arrival order == age order, so the head always has the smallest effective severity of its bucket. */
public class BucketTriage {
    private final ArrayDeque<FairTriage.P>[] bucket;
    private final long T; private int size = 0;
    @SuppressWarnings("unchecked")
    BucketTriage(long T) { this.T = T; bucket = new ArrayDeque[6]; for (int i = 1; i <= 5; i++) bucket[i] = new ArrayDeque<>(); }

    int eff(FairTriage.P p, long clock) { return (int) Math.max(1, p.sev - (clock - p.arrival) / T); }
    void admit(String name, int sev, long clock) { bucket[sev].addLast(new FairTriage.P(name, sev, clock)); size++; }

    FairTriage.P next(long clock) {
        FairTriage.P best = null; int bestEff = 0;
        for (int s = 1; s <= 5; s++) {
            FairTriage.P h = bucket[s].peekFirst();
            if (h == null) continue;
            FairTriage.touched++;
            int e = eff(h, clock);
            if (best != null) FairTriage.comparisons++;
            if (best == null || e < bestEff || (e == bestEff && h.arrival < best.arrival)) { best = h; bestEff = e; }
        }
        if (best != null) { bucket[best.sev].pollFirst(); size--; }
        return best;
    }
}
