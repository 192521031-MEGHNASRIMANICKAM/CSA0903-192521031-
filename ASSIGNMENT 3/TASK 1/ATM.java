
import java.io.*;
import java.util.*;

public class ATM {

    static int readBalance(String filename) throws IOException {
        try (Scanner file = new Scanner(new File(filename))) {
            if (!file.hasNextInt()) {
                throw new InputMismatchException("Invalid balance");
            }
            return file.nextInt();
        }
    }

    static void withdraw(int balance, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                "Negative amount is not allowed");
        }

        if (amount > balance) {
            throw new IllegalArgumentException(
                "Insufficient balance");
        }

        System.out.println("Cash dispensed: " + amount);
        System.out.println("Remaining balance: "
                           + (balance - amount));
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            System.out.print("Enter account number: ");
            String account = sc.nextLine();

            System.out.print("Enter withdrawal amount: ");
            int amount = Integer.parseInt(sc.nextLine());

            int balance = readBalance("card.txt");

            System.out.println("Account: " + account);
            withdraw(balance, amount);

        } catch (NumberFormatException |
                 InputMismatchException e) {

            System.out.println("Invalid input: " + e.getMessage());

        } catch (FileNotFoundException e) {
            System.out.println("Card file not found");

        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());

        } catch (IllegalArgumentException e) {
            System.out.println("Transaction failed: "
                               + e.getMessage());

        } finally {
            System.out.println("ATM session ended.");
            sc.close();
        }
    }
}
