import java.util.*;

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
        return String.format("%-20s | %-25s | %-12s | %s",
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

    // Add a new card (duplicate names are not allowed)
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
        System.out.printf("%-20s | %-25s | %-12s | %s%n",
                "Name", "Set", "Energy Type", "Rarity");
        System.out.println("-".repeat(72));
        for (Card card : inventory.values()) {
            System.out.println(card);
        }
        System.out.println("-".repeat(72));
        System.out.println("Total unique cards: " + inventory.size());
    }
}

// Main class — runs the interactive menu
public class PokemonInventory {

    public static void main(String[] args) {
        Collection collection = new Collection();
        Scanner scanner = new Scanner(System.in);

        // Seed with some starter data
        collection.addCard(new Card("Alakazam",  "Base Set",    "Psychic",   "Holo Rare"));
        collection.addCard(new Card("Blastoise", "Base Set",    "Water",     "Holo Rare"));
        collection.addCard(new Card("Charizard", "Base Set",    "Fire",      "Holo Rare"));
        collection.addCard(new Card("Dragonite", "Fossil",      "Colorless", "Rare"));
        collection.addCard(new Card("Eevee",     "Jungle",      "Colorless", "Common"));
        collection.addCard(new Card("Gengar",    "Fossil",      "Psychic",   "Holo Rare"));
        collection.addCard(new Card("Lugia",     "Neo Genesis", "Psychic",   "Holo Rare"));
        collection.addCard(new Card("Mewtwo",    "Base Set",    "Psychic",   "Holo Rare"));
        collection.addCard(new Card("Pikachu",   "Jungle",      "Lightning", "Common"));
        collection.addCard(new Card("Snorlax",   "Jungle",      "Colorless", "Rare"));

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
                    System.out.print("Rarity (Common, Uncommon, Rare, Holo Rare): ");
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
