import java.util.*;

class NoHash {                       // equals() only, as written by the junior developer
    final String regNo; NoHash(String r) { regNo = r; }
    @Override public boolean equals(Object o) { return o instanceof NoHash && regNo.equals(((NoHash) o).regNo); }
}
class Fixed {                        // equals() + hashCode() on the same field
    final String regNo; Fixed(String r) { regNo = r; }
    @Override public boolean equals(Object o) { return o instanceof Fixed && regNo.equals(((Fixed) o).regNo); }
    @Override public int hashCode() { return regNo.hashCode(); }
}
class Mutable {                      // hashCode depends on a NON-final field
    String regNo; Mutable(String r) { regNo = r; }
    @Override public boolean equals(Object o) { return o instanceof Mutable && regNo.equals(((Mutable) o).regNo); }
    @Override public int hashCode() { return regNo.hashCode(); }
}

public class HashContractTests {
    static void row(String test, String predicted, String observed) {
        System.out.printf("%-52s %-10s %-10s %s%n", test, predicted, observed, predicted.equals(observed) ? "as predicted" : "DIFFERENT");
    }
    public static void main(String[] args) {
        System.out.printf("%-52s %-10s %-10s %s%n", "Test", "Predicted", "Observed", "Verdict");
        System.out.println("-".repeat(90));

        Set<NoHash> s1 = new HashSet<>();                       // Test 1: looks correct (same object twice)
        NoHash same = new NoHash("192311001");
        s1.add(same); s1.add(same);
        row("T1 no hashCode: SAME object added twice (size)", "1", "" + s1.size());

        Set<NoHash> s2 = new HashSet<>();                       // Test 2: exposes duplicates
        s2.add(new NoHash("192311001")); s2.add(new NoHash("192311002")); s2.add(new NoHash("192311001"));
        row("T2 no hashCode: two equal objects, 3 scans (size)", "3", "" + s2.size());

        Set<Fixed> s3 = new HashSet<>();                        // after the fix
        s3.add(new Fixed("192311001")); s3.add(new Fixed("192311002")); s3.add(new Fixed("192311001"));
        row("T2b with hashCode: same 3 scans (size)", "2", "" + s3.size());

        Set<Mutable> s4 = new HashSet<>();                      // Test 3: mutate a hashed field after insertion
        Mutable m = new Mutable("192311001");
        s4.add(m);
        m.regNo = "192399999";
        row("T3 field changed after add: contains(m)", "false", "" + s4.contains(m));
        s4.add(new Mutable("192311001"));
        row("T3 original regNo re-scanned: size (duplicate!)", "2", "" + s4.size());

        System.out.println();
        NoHash x = new NoHash("192311001"), y = new NoHash("192311001");
        System.out.println("T2 detail: x.equals(y) = " + x.equals(y) + ", x.hashCode()==y.hashCode() ? " + (x.hashCode() == y.hashCode()));
        Fixed p = new Fixed("192311001"), q = new Fixed("192311001");
        System.out.println("Fixed    : p.equals(q) = " + p.equals(q) + ", p.hashCode()==q.hashCode() ? " + (p.hashCode() == q.hashCode()));
    }
}
