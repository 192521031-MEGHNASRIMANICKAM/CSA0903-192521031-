import java.util.*;

public class RankingTests {
    static class T implements Comparable<T> {                     // alternative: natural ordering
        final String name; final int points, gd;
        T(String n, int p, int g) { name = n; points = p; gd = g; }
        @Override public int compareTo(T o) {
            int c = Integer.compare(o.points, points);
            if (c != 0) return c;
            c = Integer.compare(o.gd, gd);
            return c != 0 ? c : name.compareTo(o.name);
        }
        @Override public String toString() { return name + "(" + points + "," + gd + ")"; }
    }
    static List<T> data(String... spec) {
        List<T> l = new ArrayList<>();
        for (String s : spec) { String[] p = s.split(","); l.add(new T(p[0], Integer.parseInt(p[1]), Integer.parseInt(p[2]))); }
        return l;
    }
    /** Brute force check of the principle: antisymmetry, transitivity of 'ranks above', equal => equal. */
    static int violations(List<T> l, Comparator<T> c) {
        int v = 0;
        for (T a : l) for (T b : l) {
            if (Integer.signum(c.compare(a, b)) != -Integer.signum(c.compare(b, a))) v++;
            for (T x : l) if (c.compare(a, b) < 0 && c.compare(b, x) < 0 && c.compare(a, x) >= 0) v++;
        }
        return v;
    }
    public static void main(String[] args) {
        Comparator<T> good = Comparator.<T>comparingInt(t -> t.points).reversed()
                .thenComparing(Comparator.<T>comparingInt(t -> t.gd).reversed()).thenComparing(t -> t.name);
        Comparator<T> subtraction = (a, b) -> b.gd - a.gd;                 // faulty: overflow
        Comparator<T> noTieBreak = Comparator.<T>comparingInt(t -> t.points).reversed()
                .thenComparing(Comparator.<T>comparingInt(t -> t.gd).reversed());   // faulty: ignores name

        System.out.println("DATA SET A (ties at every level)");
        List<T> a = data("Zebras,9,4", "Lions,6,2", "Hawks,9,4", "Eagles,9,6", "Bulls,6,2", "Cobras,6,-1", "Wolves,3,0", "Ants,9,6");
        List<T> r = new ArrayList<>(a); r.sort(good);
        System.out.println(" comparator : " + r);
        List<T> r2 = new ArrayList<>(a); Collections.sort(r2);
        System.out.println(" Comparable : " + r2 + "  same? " + r.toString().equals(r2.toString()));
        List<T> r3 = new ArrayList<>(a);                                    // alternative: three stable sorts
        r3.sort(Comparator.comparing((T t) -> t.name)); r3.sort(Comparator.comparingInt((T t) -> t.gd).reversed()); r3.sort(Comparator.comparingInt((T t) -> t.points).reversed());
        System.out.println(" 3 x stable : " + r3 + "  same? " + r.toString().equals(r3.toString()));
        System.out.println(" violations of the principle (good comparator): " + violations(a, good));

        System.out.println();
        System.out.println("DATA SET B (designed to break faulty comparators)");
        List<T> b = data("Alpha,9,2000000000", "Beta,9,-2000000000", "Gamma,9,0");
        List<T> g1 = new ArrayList<>(b); g1.sort(good);
        List<T> f1 = new ArrayList<>(b); f1.sort(subtraction);
        System.out.println(" correct       : " + g1 + "  violations=" + violations(b, good));
        System.out.println(" subtraction   : " + f1 + "  violations=" + violations(b, subtraction));
        List<T> c1 = data("Tigers,9,3", "Sharks,9,3"), c2 = data("Sharks,9,3", "Tigers,9,3");
        c1.sort(noTieBreak); c2.sort(noTieBreak);
        System.out.println(" no name break : input order A -> " + c1 + " ; input order B -> " + c2 + "  (depends on input order!)");
        c1 = data("Tigers,9,3", "Sharks,9,3"); c2 = data("Sharks,9,3", "Tigers,9,3");
        c1.sort(good); c2.sort(good);
        System.out.println(" correct       : input order A -> " + c1 + " ; input order B -> " + c2);

        System.out.println();
        Leaderboard<Team> lb = new Leaderboard<>(Comparator.comparingInt((Team t) -> t.points).reversed());
        List<Team> extra = List.of(new Team("X", 3, 0), new Team("Y", 5, 0));
        lb.addAll(extra);
        System.out.println("Wildcard addAll(List<? extends T>) + sorted copy: " + lb.ranking());
    }
}
