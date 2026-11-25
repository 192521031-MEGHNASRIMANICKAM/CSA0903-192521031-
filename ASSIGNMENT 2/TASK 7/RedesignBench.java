import java.lang.management.ManagementFactory;
import java.util.*;

public class RedesignBench {
    static final com.sun.management.ThreadMXBean MX = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
    static long alloc() { return MX.getThreadAllocatedBytes(Thread.currentThread().getId()); }

    /* PREDICTION (before running): the original rebuilds a heap of all n waiting patients on every poll -> O(n) work per
       poll, O(n^2) total; time should grow ~16x when n grows 4x, and bytes allocated grow likewise.
       The bucket design inspects <=5 heads per poll -> O(1) per poll, O(n) total; time grows ~4x when n grows 4x.
       Both must serve patients in the IDENTICAL order. */
    static String[] runOriginal(int n, long T, int[] sev, long[] stats) {
        FairTriage f = new FairTriage(T);
        for (int i = 0; i < n; i++) f.admit("P" + i, sev[i], i);
        FairTriage.comparisons = 0; FairTriage.touched = 0;
        long a0 = alloc(), t0 = System.nanoTime();
        String[] out = new String[n];
        for (int k = 0; k < n; k++) out[k] = f.next(n + k).name;
        stats[0] = (System.nanoTime() - t0) / 1000; stats[1] = FairTriage.comparisons; stats[2] = FairTriage.touched; stats[3] = (alloc() - a0) / 1048576;
        return out;
    }
    static String[] runRedesign(int n, long T, int[] sev, long[] stats) {
        BucketTriage b = new BucketTriage(T);
        for (int i = 0; i < n; i++) b.admit("P" + i, sev[i], i);
        FairTriage.comparisons = 0; FairTriage.touched = 0;
        long a0 = alloc(), t0 = System.nanoTime();
        String[] out = new String[n];
        for (int k = 0; k < n; k++) out[k] = b.next(n + k).name;
        stats[0] = (System.nanoTime() - t0) / 1000; stats[1] = FairTriage.comparisons; stats[2] = FairTriage.touched; stats[3] = (alloc() - a0) / 1048576;
        return out;
    }
    public static void main(String[] args) {
        System.out.println("Fair (aging) triage: serve all n waiting patients. Aging step T = n/5 ticks per level.");
        System.out.printf("%-7s %-22s %10s %14s %14s %10s %s%n", "n", "Design", "time (us)", "comparisons", "patients seen", "alloc MB", "same order?");
        System.out.println("-".repeat(96));
        for (int w = 0; w < 2; w++) { int[] s = sevs(1500); long[] st = new long[4]; runOriginal(1500, 300, s, st); runRedesign(1500, 300, s, st); }
        for (int n : new int[]{1000, 4000, 12000}) {
            int[] sev = sevs(n); long T = n / 5;
            long[] so = new long[4], sr = new long[4];
            String[] o = runOriginal(n, T, sev, so);
            String[] r = runRedesign(n, T, sev, sr);
            boolean same = Arrays.equals(o, r);
            System.out.printf("%-7d %-22s %10d %14d %14d %10d%n", n, "Original (re-heapify)", so[0], so[1], so[2], so[3]);
            System.out.printf("%-7d %-22s %10d %14d %14d %10d %s%n", n, "Redesign (5 buckets)", sr[0], sr[1], sr[2], sr[3], same ? "YES (identical)" : "NO");
            System.out.printf("%-7s %-22s %9.1fx %13.0fx %13.0fx%n%n", "", "improvement", (double) so[0] / Math.max(1, sr[0]), (double) so[1] / sr[1], (double) so[2] / sr[2]);
        }
    }
    static int[] sevs(int n) { Random r = new Random(11); int[] s = new int[n]; for (int i = 0; i < n; i++) s[i] = 1 + r.nextInt(5); return s; }
}
