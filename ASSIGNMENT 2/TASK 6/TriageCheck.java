public class TriageCheck {
    public static void main(String[] args) {
        Triage er = new Triage();
        String[] names = {"Ravi", "Meena", "John", "Asha", "Kumar", "Lata", "Dev", "Zoya"};
        int[] sev =      {3,      1,       3,      5,      1,       2,      4,     2};
        for (int i = 0; i < names.length; i++) er.admit(names[i], sev[i]);
        System.out.println("Printing the PriorityQueue directly (heap array order, NOT treatment order):");
        System.out.println("  " + er.queue);
        String expected = "Meena(S1) Kumar(S1) Lata(S2) Zoya(S2) Ravi(S3) John(S3) Dev(S4) Asha(S5)";
        StringBuilder sb = new StringBuilder(); Patient p;
        while ((p = er.next()) != null) sb.append(p).append(' ');
        System.out.println("Hand-worked order : " + expected);
        System.out.println("Program order     : " + sb.toString().trim());
        System.out.println("Result            : " + (expected.equals(sb.toString().trim()) ? "PASS" : "FAIL"));
    }
}
