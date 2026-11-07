import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Path DATA_FILE = Path.of("inventory.tsv");
    private static final Map<Integer, Medicine> inventory = new LinkedHashMap<>();
    private static int nextId = 1;

    private static final class Medicine {
        final int id;
        final String name;
        int quantity;
        final double price;

        Medicine(int id, String name, int quantity, double price) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }
    }

    public static void main(String[] args) {
        loadInventory();
        try (Scanner input = new Scanner(System.in)) {
            System.out.println("Pharmacy inventory manager (Java)");
            while (true) {
                System.out.println("\n1) List stock  2) Add medicine  3) Record sale  4) Quit");
                System.out.print("Choose: ");
                String choice = input.nextLine().trim();
                switch (choice) {
                    case "1" -> listInventory();
                    case "2" -> addMedicine(input);
                    case "3" -> recordSale(input);
                    case "4" -> {
                        saveInventory();
                        System.out.println("Inventory saved. Goodbye!");
                        return;
                    }
                    default -> System.out.println("Please enter 1, 2, 3, or 4.");
                }
            }
        }
    }

    private static void listInventory() {
        if (inventory.isEmpty()) {
            System.out.println("No medicines yet.");
            return;
        }
        System.out.printf("%-5s %-30s %10s %12s%n", "ID", "Medicine", "Quantity", "Price");
        for (Medicine item : inventory.values()) {
            System.out.printf("%-5d %-30s %10d %12.2f%n", item.id, item.name, item.quantity, item.price);
        }
    }

    private static void addMedicine(Scanner input) {
        System.out.print("Medicine name: ");
        String name = input.nextLine().trim();
        if (name.isEmpty() || name.contains("\t")) {
            System.out.println("Enter a name without tabs.");
            return;
        }
        int quantity = readInt(input, "Quantity: ", 0);
        double price = readDouble(input, "Price: ", 0);
        inventory.put(nextId, new Medicine(nextId, name, quantity, price));
        nextId++;
        saveInventory();
        System.out.println("Medicine added.");
    }

    private static void recordSale(Scanner input) {
        listInventory();
        int id = readInt(input, "Medicine ID to sell: ", 1);
        Medicine item = inventory.get(id);
        if (item == null) {
            System.out.println("Medicine not found.");
            return;
        }
        int amount = readInt(input, "Quantity sold: ", 1);
        if (amount > item.quantity) {
            System.out.println("Not enough stock. Available: " + item.quantity);
            return;
        }
        item.quantity -= amount;
        saveInventory();
        System.out.printf("Sale recorded. Total: %.2f%n", amount * item.price);
    }

    private static int readInt(Scanner input, String prompt, int minimum) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(input.nextLine().trim());
                if (value >= minimum) return value;
            } catch (NumberFormatException ignored) {
                // Ask again with a clear validation message.
            }
            System.out.println("Enter a whole number of at least " + minimum + ".");
        }
    }

    private static double readDouble(Scanner input, String prompt, double minimum) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(input.nextLine().trim());
                if (Double.isFinite(value) && value >= minimum) return value;
            } catch (NumberFormatException ignored) {
                // Ask again with a clear validation message.
            }
            System.out.println("Enter a valid number of at least " + minimum + ".");
        }
    }

    private static void loadInventory() {
        if (!Files.exists(DATA_FILE)) return;
        try {
            for (String line : Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8)) {
                String[] fields = line.split("\\t", -1);
                if (fields.length != 4) continue;
                try {
                    int id = Integer.parseInt(fields[0]);
                    Medicine item = new Medicine(id, fields[1], Integer.parseInt(fields[2]), Double.parseDouble(fields[3]));
                    if (!item.name.isBlank() && item.quantity >= 0 && Double.isFinite(item.price) && item.price >= 0) {
                        inventory.put(id, item);
                        nextId = Math.max(nextId, id + 1);
                    }
                } catch (NumberFormatException ignored) {
                    System.err.println("Skipping invalid inventory row.");
                }
            }
        } catch (IOException error) {
            System.err.println("Could not read " + DATA_FILE + ": " + error.getMessage());
        }
    }

    private static void saveInventory() {
        StringBuilder contents = new StringBuilder();
        for (Medicine item : inventory.values()) {
            contents.append(item.id).append('\t').append(item.name).append('\t')
                    .append(item.quantity).append('\t').append(item.price).append('\n');
        }
        try {
            Files.writeString(DATA_FILE, contents, StandardCharsets.UTF_8);
        } catch (IOException error) {
            System.err.println("Could not save inventory: " + error.getMessage());
        }
    }
}
