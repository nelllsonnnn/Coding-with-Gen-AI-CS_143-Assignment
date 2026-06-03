import java.util.*;
import java.io.*;

// Represents a single Pokémon card
class Card {
    private String name;
    private String set;
    private String energyType;
    private String rarity;

    public Card(String name, String set, String energyType, String rarity) {
        this.name       = name;
        this.set        = set;
        this.energyType = energyType;
        this.rarity     = rarity;
    }

    public String getName()       { return name; }
    public String getSet()        { return set; }
    public String getEnergyType() { return energyType; }
    public String getRarity()     { return rarity; }

    @Override
    public String toString() {
        return String.format("%-30s | %-25s | %-12s | %s",
                name, set, energyType, rarity);
    }
}

// Manages the full card collection using a TreeMap (sorted A-Z by card name)
class Collection {
    // TreeMap automatically keeps entries sorted by key (card name, A-Z)
    private TreeMap<String, Card> inventory;

    public Collection() {
        inventory = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    // Load cards from a .txt file
    // Skips comment lines (starting with #, -, =, or blank lines)
    // Expects lines formatted as: Name | Set | Energy Type | Rarity
    public void loadFromFile(String filePath) {
        int loaded = 0;
        int skipped = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip blank lines, headers, dividers, and section labels
                if (line.isEmpty()
                        || line.startsWith("=")
                        || line.startsWith("-")
                        || line.startsWith("#")
                        || line.startsWith("Format")
                        || line.startsWith("Sets:")
                        || line.startsWith("Total")
                        || line.startsWith("END")) {
                    continue;
                }

                // Split on the pipe character
                String[] parts = line.split("\\|");
                if (parts.length != 4) {
                    skipped++;
                    continue;
                }

                String name       = parts[0].trim();
                String set        = parts[1].trim();
                String energyType = parts[2].trim();
                String rarity     = parts[3].trim();

                // Skip the header row inside the file
                if (name.equalsIgnoreCase("Name")) {
                    continue;
                }

                // If a card name already exists, append the set to make it unique
                // (e.g. "Flareon ex | Prismatic Evolutions" vs "Flareon ex | 151")
                String key = name;
                if (inventory.containsKey(key)) {
                    key = name + " (" + set + ")";
                }

                inventory.put(key, new Card(name, set, energyType, rarity));
                loaded++;
            }

            System.out.println("Loaded " + loaded + " cards from file.");
            if (skipped > 0) {
                System.out.println("Skipped " + skipped + " unreadable line(s).");
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
            System.out.println("Starting with an empty collection.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Add a new card manually
    public void addCard(Card card) {
        String key = card.getName();
        if (inventory.containsKey(key)) {
            System.out.println("A card named \"" + key + "\" already exists.");
        } else {
            inventory.put(key, card);
            System.out.println("Added: " + key);
        }
    }

    // Remove a card by name
    public void removeCard(String name) {
        if (inventory.remove(name) != null) {
            System.out.println("Removed: " + name);
        } else {
            System.out.println("Card not found: " + name);
        }
    }

    // Search for a card by name (case-insensitive)
    public Card searchCard(String name) {
        return inventory.get(name);
    }

    // Display all cards sorted A-Z (TreeMap handles this automatically)
    public void displayAll() {
        if (inventory.isEmpty()) {
            System.out.println("Your collection is empty.");
            return;
        }
        System.out.println("\n======= POKÉMON CARD INVENTORY (A-Z) =======");
        System.out.printf("%-30s | %-25s | %-12s | %s%n",
                "Name", "Set", "Energy Type", "Rarity");
        System.out.println("-".repeat(85));
        for (Card card : inventory.values()) {
            System.out.println(card);
        }
        System.out.println("-".repeat(85));
        System.out.println("Total unique cards: " + inventory.size());
    }
}

// Main class — runs the interactive menu
public class PokemonInventory {

    public static void main(String[] args) {
        Collection collection = new Collection();
        Scanner scanner = new Scanner(System.in);

        // Load cards from the .txt file
        // Put pokemon_card_list.txt in the same folder as this .java file
        collection.loadFromFile("pokemon_card_list.txt");

        boolean running = true;

        while (running) {
            System.out.println("\n===== POKÉMON CARD INVENTORY TRACKER =====");
            System.out.println("1. View all cards (A-Z)");
            System.out.println("2. Add a card");
            System.out.println("3. Remove a card");
            System.out.println("4. Search for a card");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    collection.displayAll();
                    break;

                case "2":
                    System.out.print("Card name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Set: ");
                    String set = scanner.nextLine().trim();
                    System.out.print("Energy type (Fire, Water, Grass, etc.): ");
                    String energy = scanner.nextLine().trim();
                    System.out.print("Rarity (Common, Uncommon, Rare, etc.): ");
                    String rarity = scanner.nextLine().trim();
                    collection.addCard(new Card(name, set, energy, rarity));
                    break;

                case "3":
                    System.out.print("Card name to remove: ");
                    String removeName = scanner.nextLine().trim();
                    collection.removeCard(removeName);
                    break;

                case "4":
                    System.out.print("Card name to search: ");
                    String searchName = scanner.nextLine().trim();
                    Card found = collection.searchCard(searchName);
                    if (found != null) {
                        System.out.println("\nFound:");
                        System.out.println(found);
                    } else {
                        System.out.println("Card not found: " + searchName);
                    }
                    break;

                case "5":
                    running = false;
                    System.out.println("Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }

        scanner.close();
    }
}
