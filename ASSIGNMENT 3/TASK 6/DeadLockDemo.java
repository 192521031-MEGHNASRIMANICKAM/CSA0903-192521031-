
class Wallet {

    String name;
    int balance = 1000;

    Wallet(String name) {
        this.name = name;
    }
}

public class DeadlockDemo {

    // Unsafe transfer: can cause deadlock
    static void transferUnsafe(Wallet from, Wallet to, int amount) {

        synchronized (from) {

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            synchronized (to) {
                from.balance -= amount;
                to.balance += amount;
            }
        }
    }

    // Safe transfer using consistent lock ordering
    static void transferSafe(Wallet a, Wallet b, int amount) {

        Wallet first;
        Wallet second;

        if (a.name.compareTo(b.name) < 0) {
            first = a;
            second = b;
        } else {
            first = b;
            second = a;
        }

        synchronized (first) {

            synchronized (second) {

                a.balance -= amount;
                b.balance += amount;
            }
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        Wallet a = new Wallet("A");
        Wallet b = new Wallet("B");

        Thread t1 = new Thread(
            () -> transferSafe(a, b, 100));

        Thread t2 = new Thread(
            () -> transferSafe(b, a, 50));

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Wallet A balance: " + a.balance);
        System.out.println("Wallet B balance: " + b.balance);
    }
}
