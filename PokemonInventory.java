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

/*
 
  ----jGRASP exec: java PokemonInventory
 Loaded 236 cards from file.
 Skipped 4 unreadable line(s).
 
 ===== POK╔MON CARD INVENTORY TRACKER =====
 1. View all cards (A-Z)
 2. Add a card
 3. Remove a card
 4. Search for a card
 5. Exit
 Choose an option: 1
 
 ======= POK╔MON CARD INVENTORY (A-Z) =======
 Name                           | Set                       | Energy Type  | Rarity
 -------------------------------------------------------------------------------------
 Abra                           | 151                       | Psychic      | Common
 Aerodactyl ex                  | 151                       | Colorless    | Double Rare
 Alakazam ex                    | 151                       | Psychic      | Double Rare
 Applin                         | Prismatic Evolutions      | Grass        | Common
 Arbok ex                       | 151                       | Darkness     | Double Rare
 Arcanine                       | 151                       | Fire         | Uncommon
 Arcanine ex                    | Ascended Heroes           | Fire         | Double Rare
 Articuno ex                    | 151                       | Water        | Double Rare
 Beautifly                      | Ascended Heroes           | Grass        | Uncommon
 Blastoise ex                   | 151                       | Water        | Double Rare
 Budew                          | Prismatic Evolutions      | Grass        | Common
 Bulbasaur                      | Ascended Heroes           | Grass        | Common
 Bulbasaur                      | 151                       | Grass        | Common
 Canari                         | Ascended Heroes           | Colorless    | Special Illustration Rare
 Chansey ex                     | 151                       | Colorless    | Double Rare
 Charizard ex                   | Ascended Heroes           | Fire         | Double Rare
 Charizard ex                   | 151                       | Fire         | Double Rare
 Charjabug                      | Ascended Heroes           | Lightning    | Common
 Charmander                     | 151                       | Fire         | Common
 Charmeleon                     | 151                       | Fire         | Uncommon
 Clefable                       | Ascended Heroes           | Psychic      | Uncommon
 Clefable                       | 151                       | Colorless    | Uncommon
 Clefairy                       | Ascended Heroes           | Psychic      | Common
 Clefairy                       | 151                       | Colorless    | Common
 Cloyster                       | 151                       | Water        | Uncommon
 Cottonee                       | Prismatic Evolutions      | Grass        | Common
 Cubone                         | 151                       | Fighting     | Common
 Dewgong                        | 151                       | Water        | Uncommon
 Diglett                        | 151                       | Fighting     | Common
 Dipplin                        | Prismatic Evolutions      | Grass        | Uncommon
 Ditto ex                       | 151                       | Colorless    | Double Rare
 Dodrio                         | 151                       | Colorless    | Uncommon
 Doduo                          | 151                       | Colorless    | Common
 Drowzee                        | 151                       | Psychic      | Common
 Dugtrio                        | 151                       | Fighting     | Uncommon
 Dustox                         | Ascended Heroes           | Grass        | Uncommon
 Eevee                          | 151                       | Colorless    | Common
 Eevee                          | Prismatic Evolutions      | Colorless    | Common
 Eevee ex                       | Prismatic Evolutions      | Colorless    | Double Rare
 Ekans                          | 151                       | Darkness     | Common
 Electabuzz                     | 151                       | Lightning    | Uncommon
 Electrode                      | 151                       | Lightning    | Uncommon
 Entei                          | Prismatic Evolutions      | Fire         | Rare
 Erika's Tangela                | Ascended Heroes           | Grass        | Rare
 Espeon                         | 151                       | Psychic      | Uncommon
 Espeon                         | Prismatic Evolutions      | Psychic      | Uncommon
 Espeon ex                      | Prismatic Evolutions      | Psychic      | Double Rare
 Espeon ex                      | Prismatic Evolutions      | Psychic      | Special Illustration Rare
 Ethan's Magcargo               | Ascended Heroes           | Fire         | Rare
 Exeggcute                      | Prismatic Evolutions      | Grass        | Common
 Exeggutor                      | Prismatic Evolutions      | Grass        | Uncommon
 Farfetch'd                     | 151                       | Colorless    | Uncommon
 Fearow                         | 151                       | Colorless    | Uncommon
 Fezandipiti ex                 | Ascended Heroes           | Psychic      | Ultra Rare
 Flareon                        | 151                       | Fire         | Uncommon
 Flareon                        | Prismatic Evolutions      | Fire         | Uncommon
 Flareon ex                     | Prismatic Evolutions      | Fire         | Double Rare
 Flareon ex                     | Prismatic Evolutions      | Fire         | Special Illustration Rare
 Gastly                         | 151                       | Psychic      | Common
 Gengar ex                      | 151                       | Psychic      | Double Rare
 Geodude                        | 151                       | Fighting     | Common
 Glaceon                        | Prismatic Evolutions      | Water        | Uncommon
 Glaceon ex                     | Prismatic Evolutions      | Water        | Double Rare
 Glaceon ex                     | Prismatic Evolutions      | Water        | Special Illustration Rare
 Gloom                          | 151                       | Grass        | Uncommon
 Golbat                         | 151                       | Darkness     | Uncommon
 Goldeen                        | 151                       | Water        | Common
 Goldeen                        | Prismatic Evolutions      | Water        | Common
 Golduck                        | 151                       | Water        | Uncommon
 Golem ex                       | 151                       | Fighting     | Double Rare
 Graveler                       | 151                       | Fighting     | Uncommon
 Grimer                         | 151                       | Darkness     | Common
 Growlithe                      | 151                       | Fire         | Common
 Gyarados ex                    | 151                       | Water        | Double Rare
 Haunter                        | 151                       | Psychic      | Uncommon
 Hearthflame Mask Ogerpon ex    | Prismatic Evolutions      | Fire         | Double Rare
 Hitmonchan                     | 151                       | Fighting     | Rare
 Hitmonlee                      | 151                       | Fighting     | Rare
 Hop's Pincurchin ex            | Ascended Heroes           | Lightning    | Double Rare
 Horsea                         | 151                       | Water        | Common
 Hydrapple ex                   | Prismatic Evolutions      | Grass        | Double Rare
 Hypno                          | 151                       | Psychic      | Uncommon
 Iono's Bellibolt ex            | Ascended Heroes           | Lightning    | Ultra Rare
 Iono's Kilowattrel             | Ascended Heroes           | Lightning    | Uncommon
 Iono's Tadbulb                 | Ascended Heroes           | Lightning    | Uncommon
 Iono's Wattrel                 | Ascended Heroes           | Lightning    | Common
 Ivysaur                        | Ascended Heroes           | Grass        | Uncommon
 Ivysaur                        | 151                       | Grass        | Uncommon
 Jigglypuff                     | 151                       | Colorless    | Common
 Jolteon                        | 151                       | Lightning    | Uncommon
 Jolteon                        | Prismatic Evolutions      | Lightning    | Uncommon
 Jolteon ex                     | Prismatic Evolutions      | Lightning    | Double Rare
 Jolteon ex                     | Prismatic Evolutions      | Lightning    | Special Illustration Rare
 Jynx                           | 151                       | Psychic      | Uncommon
 Kabuto                         | 151                       | Water        | Common
 Kabutops                       | 151                       | Water        | Uncommon
 Kadabra                        | 151                       | Psychic      | Uncommon
 Kangaskhan ex                  | 151                       | Colorless    | Double Rare
 Kingler                        | 151                       | Water        | Uncommon
 Krabby                         | 151                       | Water        | Common
 Lapras ex                      | 151                       | Water        | Double Rare
 Leafeon                        | Prismatic Evolutions      | Grass        | Uncommon
 Leafeon ex                     | Prismatic Evolutions      | Grass        | Double Rare
 Leafeon ex                     | Prismatic Evolutions      | Grass        | Special Illustration Rare
 Lickitung                      | 151                       | Colorless    | Common
 Lillie's Clefairy ex           | Ascended Heroes           | Psychic      | Special Illustration Rare
 Litleo                         | Prismatic Evolutions      | Fire         | Common
 Machamp ex                     | 151                       | Fighting     | Double Rare
 Machoke                        | 151                       | Fighting     | Uncommon
 Machop                         | 151                       | Fighting     | Common
 Magikarp                       | 151                       | Water        | Common
 Magmar                         | 151                       | Fire         | Common
 Magnemite                      | 151                       | Lightning    | Common
 Magneton                       | 151                       | Lightning    | Uncommon
 Mankey                         | 151                       | Fighting     | Common
 Marill                         | Ascended Heroes           | Water        | Common
 Marnie's Grimmsnarl ex         | Ascended Heroes           | Darkness     | Ultra Rare
 Marowak                        | 151                       | Fighting     | Uncommon
 Mega Blaziken ex               | Ascended Heroes           | Fire         | Ultra Rare
 Mega Charizard X ex            | Ascended Heroes           | Fire         | Mega Attack Rare
 Mega Charizard Y ex            | Ascended Heroes           | Fire         | Mega Hyper Rare
 Mega Diancie ex                | Ascended Heroes           | Fighting     | Mega Attack Rare
 Mega Dragonite ex              | Ascended Heroes           | Dragon       | Mega Hyper Rare
 Mega Eelektross ex             | Ascended Heroes           | Lightning    | Ultra Rare
 Mega Emboar ex                 | Ascended Heroes           | Grass        | Ultra Rare
 Mega Feraligatr ex             | Ascended Heroes           | Water        | Ultra Rare
 Mega Gengar ex                 | Ascended Heroes           | Darkness     | Special Illustration Rare
 Mega Gyarados ex               | Ascended Heroes           | Water        | Ultra Rare
 Mega Hawlucha ex               | Ascended Heroes           | Fighting     | Mega Attack Rare
 Mega Infernape ex              | Ascended Heroes           | Fire         | Ultra Rare
 Mega Lucario ex                | Ascended Heroes           | Metal        | Mega Attack Rare
 Mega Sceptile ex               | Ascended Heroes           | Grass        | Ultra Rare
 Mega Scrafty ex                | Ascended Heroes           | Darkness     | Ultra Rare
 Mega Swampert ex               | Ascended Heroes           | Water        | Ultra Rare
 Meowth                         | 151                       | Darkness     | Common
 Mew ex                         | 151                       | Psychic      | Double Rare
 Mewtwo ex                      | 151                       | Psychic      | Double Rare
 Miraidon ex                    | Ascended Heroes           | Lightning    | Double Rare
 Moltres ex                     | 151                       | Fire         | Double Rare
 Mr. Mime                       | 151                       | Psychic      | Uncommon
 Muk ex                         | 151                       | Darkness     | Double Rare
 N's Zoroark ex                 | Ascended Heroes           | Darkness     | Ultra Rare
 Nidoking                       | 151                       | Fighting     | Rare
 Nidoqueen                      | 151                       | Fighting     | Rare
 Nidoran F                      | 151                       | Fighting     | Common
 Nidoran M                      | 151                       | Fighting     | Common
 Nidorina                       | 151                       | Fighting     | Uncommon
 Nidorino                       | 151                       | Fighting     | Uncommon
 Ninetales ex                   | 151                       | Fire         | Double Rare
 Numel                          | Ascended Heroes           | Fire         | Common
 Oddish                         | 151                       | Grass        | Common
 Omanyte                        | 151                       | Water        | Common
 Omastar                        | 151                       | Water        | Uncommon
 Onix                           | 151                       | Fighting     | Common
 Paras                          | 151                       | Grass        | Common
 Parasect                       | 151                       | Grass        | Uncommon
 Persian                        | 151                       | Darkness     | Uncommon
 Pidgeot ex                     | 151                       | Colorless    | Double Rare
 Pidgeotto                      | 151                       | Colorless    | Uncommon
 Pidgey                         | 151                       | Colorless    | Common
 Pikachu                        | 151                       | Lightning    | Common
 Pikachu ex                     | Ascended Heroes           | Lightning    | Special Illustration Rare
 Pikachu ex                     | Prismatic Evolutions      | Lightning    | Double Rare
 Pinsir                         | Prismatic Evolutions      | Grass        | Common
 Poliwag                        | 151                       | Water        | Common
 Poliwhirl                      | 151                       | Water        | Uncommon
 Poliwrath                      | 151                       | Water        | Rare
 Ponyta                         | 151                       | Fire         | Common
 Porygon                        | 151                       | Colorless    | Common
 Primeape                       | 151                       | Fighting     | Uncommon
 Psyduck                        | 151                       | Water        | Common
 Pyroar                         | Prismatic Evolutions      | Fire         | Uncommon
 Raichu                         | 151                       | Lightning    | Rare
 Raikou                         | Prismatic Evolutions      | Lightning    | Rare
 Rapidash                       | 151                       | Fire         | Uncommon
 Raticate                       | 151                       | Colorless    | Uncommon
 Rattata                        | 151                       | Colorless    | Common
 Rhydon                         | 151                       | Fighting     | Uncommon
 Rhyhorn                        | 151                       | Fighting     | Common
 Sandshrew                      | 151                       | Fighting     | Common
 Sandslash                      | 151                       | Fighting     | Uncommon
 Seadra                         | 151                       | Water        | Uncommon
 Seaking                        | 151                       | Water        | Uncommon
 Seaking                        | Prismatic Evolutions      | Water        | Uncommon
 Seel                           | 151                       | Water        | Common
 Shellder                       | 151                       | Water        | Common
 Slowbro                        | 151                       | Psychic      | Uncommon
 Slowking                       | Prismatic Evolutions      | Water        | Uncommon
 Slowpoke                       | 151                       | Psychic      | Common
 Slowpoke                       | Prismatic Evolutions      | Water        | Common
 Slugma                         | Ascended Heroes           | Fire         | Common
 Snorlax                        | 151                       | Colorless    | Uncommon
 Spearow                        | 151                       | Colorless    | Common
 Squirtle                       | 151                       | Water        | Common
 Starmie                        | 151                       | Water        | Rare
 Staryu                         | 151                       | Water        | Common
 Steven's Metagross ex          | Ascended Heroes           | Metal        | Ultra Rare
 Stunfisk                       | Ascended Heroes           | Lightning    | Common
 Suicune                        | Prismatic Evolutions      | Water        | Rare
 Sylveon                        | Prismatic Evolutions      | Psychic      | Uncommon
 Sylveon ex                     | Prismatic Evolutions      | Psychic      | Double Rare
 Sylveon ex                     | Prismatic Evolutions      | Psychic      | Special Illustration Rare
 Tapu Koko                      | Ascended Heroes           | Lightning    | Rare
 Tauros                         | 151                       | Colorless    | Common
 Teal Mask Ogerpon ex           | Prismatic Evolutions      | Grass        | Double Rare
 Team Rocket's Exeggcute        | Ascended Heroes           | Psychic      | Common
 Team Rocket's Exeggutor        | Ascended Heroes           | Psychic      | Uncommon
 Team Rocket's Mewtwo ex        | Ascended Heroes           | Psychic      | Special Illustration Rare
 Tentacool                      | 151                       | Water        | Common
 Tentacruel                     | 151                       | Water        | Uncommon
 Togekiss                       | Ascended Heroes           | Psychic      | Rare
 Togepi                         | Ascended Heroes           | Psychic      | Common
 Togetic                        | Ascended Heroes           | Psychic      | Uncommon
 Tynamo                         | Ascended Heroes           | Lightning    | Common
 Umbreon                        | 151                       | Darkness     | Uncommon
 Umbreon                        | Prismatic Evolutions      | Darkness     | Uncommon
 Umbreon ex                     | Prismatic Evolutions      | Darkness     | Special Illustration Rare
 Umbreon ex                     | Prismatic Evolutions      | Darkness     | Special Illustration Rare
 Vaporeon                       | 151                       | Water        | Uncommon
 Vaporeon                       | Prismatic Evolutions      | Water        | Uncommon
 Vaporeon ex                    | Prismatic Evolutions      | Water        | Double Rare
 Vaporeon ex                    | Prismatic Evolutions      | Water        | Special Illustration Rare
 Venomoth                       | 151                       | Grass        | Uncommon
 Venonat                        | 151                       | Grass        | Common
 Venusaur ex                    | 151                       | Grass        | Double Rare
 Vikavolt                       | Ascended Heroes           | Lightning    | Uncommon
 Vileplume                      | 151                       | Grass        | Rare
 Voltorb                        | 151                       | Lightning    | Common
 Voltorb ex                     | Ascended Heroes           | Lightning    | Double Rare
 Vulpix                         | 151                       | Fire         | Common
 Wartortle                      | 151                       | Water        | Uncommon
 Wellspring Mask Ogerpon ex     | Prismatic Evolutions      | Water        | Double Rare
 Whimsicott                     | Prismatic Evolutions      | Grass        | Uncommon
 Wigglytuff ex                  | 151                       | Colorless    | Double Rare
 Zapdos ex                      | 151                       | Lightning    | Double Rare
 Zubat                          | 151                       | Darkness     | Common
 -------------------------------------------------------------------------------------
 Total unique cards: 236
 
 ===== POK╔MON CARD INVENTORY TRACKER =====
 1. View all cards (A-Z)
 2. Add a card
 3. Remove a card
 4. Search for a card
 5. Exit
 Choose an option: 2
 Card name: Gengar VMAX
 Set: Fusion Strike
 Energy type (Fire, Water, Grass, etc.): Dark
 Rarity (Common, Uncommon, Rare, etc.): Special Illustration Rare
 Added: Gengar VMAX
 
 ===== POK╔MON CARD INVENTORY TRACKER =====
 1. View all cards (A-Z)
 2. Add a card
 3. Remove a card
 4. Search for a card
 5. Exit
 Choose an option: 1
 
 ======= POK╔MON CARD INVENTORY (A-Z) =======
 Name                           | Set                       | Energy Type  | Rarity
 -------------------------------------------------------------------------------------
 Abra                           | 151                       | Psychic      | Common
 Aerodactyl ex                  | 151                       | Colorless    | Double Rare
 Alakazam ex                    | 151                       | Psychic      | Double Rare
 Applin                         | Prismatic Evolutions      | Grass        | Common
 Arbok ex                       | 151                       | Darkness     | Double Rare
 Arcanine                       | 151                       | Fire         | Uncommon
 Arcanine ex                    | Ascended Heroes           | Fire         | Double Rare
 Articuno ex                    | 151                       | Water        | Double Rare
 Beautifly                      | Ascended Heroes           | Grass        | Uncommon
 Blastoise ex                   | 151                       | Water        | Double Rare
 Budew                          | Prismatic Evolutions      | Grass        | Common
 Bulbasaur                      | Ascended Heroes           | Grass        | Common
 Bulbasaur                      | 151                       | Grass        | Common
 Canari                         | Ascended Heroes           | Colorless    | Special Illustration Rare
 Chansey ex                     | 151                       | Colorless    | Double Rare
 Charizard ex                   | Ascended Heroes           | Fire         | Double Rare
 Charizard ex                   | 151                       | Fire         | Double Rare
 Charjabug                      | Ascended Heroes           | Lightning    | Common
 Charmander                     | 151                       | Fire         | Common
 Charmeleon                     | 151                       | Fire         | Uncommon
 Clefable                       | Ascended Heroes           | Psychic      | Uncommon
 Clefable                       | 151                       | Colorless    | Uncommon
 Clefairy                       | Ascended Heroes           | Psychic      | Common
 Clefairy                       | 151                       | Colorless    | Common
 Cloyster                       | 151                       | Water        | Uncommon
 Cottonee                       | Prismatic Evolutions      | Grass        | Common
 Cubone                         | 151                       | Fighting     | Common
 Dewgong                        | 151                       | Water        | Uncommon
 Diglett                        | 151                       | Fighting     | Common
 Dipplin                        | Prismatic Evolutions      | Grass        | Uncommon
 Ditto ex                       | 151                       | Colorless    | Double Rare
 Dodrio                         | 151                       | Colorless    | Uncommon
 Doduo                          | 151                       | Colorless    | Common
 Drowzee                        | 151                       | Psychic      | Common
 Dugtrio                        | 151                       | Fighting     | Uncommon
 Dustox                         | Ascended Heroes           | Grass        | Uncommon
 Eevee                          | 151                       | Colorless    | Common
 Eevee                          | Prismatic Evolutions      | Colorless    | Common
 Eevee ex                       | Prismatic Evolutions      | Colorless    | Double Rare
 Ekans                          | 151                       | Darkness     | Common
 Electabuzz                     | 151                       | Lightning    | Uncommon
 Electrode                      | 151                       | Lightning    | Uncommon
 Entei                          | Prismatic Evolutions      | Fire         | Rare
 Erika's Tangela                | Ascended Heroes           | Grass        | Rare
 Espeon                         | 151                       | Psychic      | Uncommon
 Espeon                         | Prismatic Evolutions      | Psychic      | Uncommon
 Espeon ex                      | Prismatic Evolutions      | Psychic      | Double Rare
 Espeon ex                      | Prismatic Evolutions      | Psychic      | Special Illustration Rare
 Ethan's Magcargo               | Ascended Heroes           | Fire         | Rare
 Exeggcute                      | Prismatic Evolutions      | Grass        | Common
 Exeggutor                      | Prismatic Evolutions      | Grass        | Uncommon
 Farfetch'd                     | 151                       | Colorless    | Uncommon
 Fearow                         | 151                       | Colorless    | Uncommon
 Fezandipiti ex                 | Ascended Heroes           | Psychic      | Ultra Rare
 Flareon                        | 151                       | Fire         | Uncommon
 Flareon                        | Prismatic Evolutions      | Fire         | Uncommon
 Flareon ex                     | Prismatic Evolutions      | Fire         | Double Rare
 Flareon ex                     | Prismatic Evolutions      | Fire         | Special Illustration Rare
 Gastly                         | 151                       | Psychic      | Common
 Gengar ex                      | 151                       | Psychic      | Double Rare
 Gengar VMAX                    | Fusion Strike             | Dark         | Special Illustration Rare
 Geodude                        | 151                       | Fighting     | Common
 Glaceon                        | Prismatic Evolutions      | Water        | Uncommon
 Glaceon ex                     | Prismatic Evolutions      | Water        | Double Rare
 Glaceon ex                     | Prismatic Evolutions      | Water        | Special Illustration Rare
 Gloom                          | 151                       | Grass        | Uncommon
 Golbat                         | 151                       | Darkness     | Uncommon
 Goldeen                        | 151                       | Water        | Common
 Goldeen                        | Prismatic Evolutions      | Water        | Common
 Golduck                        | 151                       | Water        | Uncommon
 Golem ex                       | 151                       | Fighting     | Double Rare
 Graveler                       | 151                       | Fighting     | Uncommon
 Grimer                         | 151                       | Darkness     | Common
 Growlithe                      | 151                       | Fire         | Common
 Gyarados ex                    | 151                       | Water        | Double Rare
 Haunter                        | 151                       | Psychic      | Uncommon
 Hearthflame Mask Ogerpon ex    | Prismatic Evolutions      | Fire         | Double Rare
 Hitmonchan                     | 151                       | Fighting     | Rare
 Hitmonlee                      | 151                       | Fighting     | Rare
 Hop's Pincurchin ex            | Ascended Heroes           | Lightning    | Double Rare
 Horsea                         | 151                       | Water        | Common
 Hydrapple ex                   | Prismatic Evolutions      | Grass        | Double Rare
 Hypno                          | 151                       | Psychic      | Uncommon
 Iono's Bellibolt ex            | Ascended Heroes           | Lightning    | Ultra Rare
 Iono's Kilowattrel             | Ascended Heroes           | Lightning    | Uncommon
 Iono's Tadbulb                 | Ascended Heroes           | Lightning    | Uncommon
 Iono's Wattrel                 | Ascended Heroes           | Lightning    | Common
 Ivysaur                        | Ascended Heroes           | Grass        | Uncommon
 Ivysaur                        | 151                       | Grass        | Uncommon
 Jigglypuff                     | 151                       | Colorless    | Common
 Jolteon                        | 151                       | Lightning    | Uncommon
 Jolteon                        | Prismatic Evolutions      | Lightning    | Uncommon
 Jolteon ex                     | Prismatic Evolutions      | Lightning    | Double Rare
 Jolteon ex                     | Prismatic Evolutions      | Lightning    | Special Illustration Rare
 Jynx                           | 151                       | Psychic      | Uncommon
 Kabuto                         | 151                       | Water        | Common
 Kabutops                       | 151                       | Water        | Uncommon
 Kadabra                        | 151                       | Psychic      | Uncommon
 Kangaskhan ex                  | 151                       | Colorless    | Double Rare
 Kingler                        | 151                       | Water        | Uncommon
 Krabby                         | 151                       | Water        | Common
 Lapras ex                      | 151                       | Water        | Double Rare
 Leafeon                        | Prismatic Evolutions      | Grass        | Uncommon
 Leafeon ex                     | Prismatic Evolutions      | Grass        | Double Rare
 Leafeon ex                     | Prismatic Evolutions      | Grass        | Special Illustration Rare
 Lickitung                      | 151                       | Colorless    | Common
 Lillie's Clefairy ex           | Ascended Heroes           | Psychic      | Special Illustration Rare
 Litleo                         | Prismatic Evolutions      | Fire         | Common
 Machamp ex                     | 151                       | Fighting     | Double Rare
 Machoke                        | 151                       | Fighting     | Uncommon
 Machop                         | 151                       | Fighting     | Common
 Magikarp                       | 151                       | Water        | Common
 Magmar                         | 151                       | Fire         | Common
 Magnemite                      | 151                       | Lightning    | Common
 Magneton                       | 151                       | Lightning    | Uncommon
 Mankey                         | 151                       | Fighting     | Common
 Marill                         | Ascended Heroes           | Water        | Common
 Marnie's Grimmsnarl ex         | Ascended Heroes           | Darkness     | Ultra Rare
 Marowak                        | 151                       | Fighting     | Uncommon
 Mega Blaziken ex               | Ascended Heroes           | Fire         | Ultra Rare
 Mega Charizard X ex            | Ascended Heroes           | Fire         | Mega Attack Rare
 Mega Charizard Y ex            | Ascended Heroes           | Fire         | Mega Hyper Rare
 Mega Diancie ex                | Ascended Heroes           | Fighting     | Mega Attack Rare
 Mega Dragonite ex              | Ascended Heroes           | Dragon       | Mega Hyper Rare
 Mega Eelektross ex             | Ascended Heroes           | Lightning    | Ultra Rare
 Mega Emboar ex                 | Ascended Heroes           | Grass        | Ultra Rare
 Mega Feraligatr ex             | Ascended Heroes           | Water        | Ultra Rare
 Mega Gengar ex                 | Ascended Heroes           | Darkness     | Special Illustration Rare
 Mega Gyarados ex               | Ascended Heroes           | Water        | Ultra Rare
 Mega Hawlucha ex               | Ascended Heroes           | Fighting     | Mega Attack Rare
 Mega Infernape ex              | Ascended Heroes           | Fire         | Ultra Rare
 Mega Lucario ex                | Ascended Heroes           | Metal        | Mega Attack Rare
 Mega Sceptile ex               | Ascended Heroes           | Grass        | Ultra Rare
 Mega Scrafty ex                | Ascended Heroes           | Darkness     | Ultra Rare
 Mega Swampert ex               | Ascended Heroes           | Water        | Ultra Rare
 Meowth                         | 151                       | Darkness     | Common
 Mew ex                         | 151                       | Psychic      | Double Rare
 Mewtwo ex                      | 151                       | Psychic      | Double Rare
 Miraidon ex                    | Ascended Heroes           | Lightning    | Double Rare
 Moltres ex                     | 151                       | Fire         | Double Rare
 Mr. Mime                       | 151                       | Psychic      | Uncommon
 Muk ex                         | 151                       | Darkness     | Double Rare
 N's Zoroark ex                 | Ascended Heroes           | Darkness     | Ultra Rare
 Nidoking                       | 151                       | Fighting     | Rare
 Nidoqueen                      | 151                       | Fighting     | Rare
 Nidoran F                      | 151                       | Fighting     | Common
 Nidoran M                      | 151                       | Fighting     | Common
 Nidorina                       | 151                       | Fighting     | Uncommon
 Nidorino                       | 151                       | Fighting     | Uncommon
 Ninetales ex                   | 151                       | Fire         | Double Rare
 Numel                          | Ascended Heroes           | Fire         | Common
 Oddish                         | 151                       | Grass        | Common
 Omanyte                        | 151                       | Water        | Common
 Omastar                        | 151                       | Water        | Uncommon
 Onix                           | 151                       | Fighting     | Common
 Paras                          | 151                       | Grass        | Common
 Parasect                       | 151                       | Grass        | Uncommon
 Persian                        | 151                       | Darkness     | Uncommon
 Pidgeot ex                     | 151                       | Colorless    | Double Rare
 Pidgeotto                      | 151                       | Colorless    | Uncommon
 Pidgey                         | 151                       | Colorless    | Common
 Pikachu                        | 151                       | Lightning    | Common
 Pikachu ex                     | Ascended Heroes           | Lightning    | Special Illustration Rare
 Pikachu ex                     | Prismatic Evolutions      | Lightning    | Double Rare
 Pinsir                         | Prismatic Evolutions      | Grass        | Common
 Poliwag                        | 151                       | Water        | Common
 Poliwhirl                      | 151                       | Water        | Uncommon
 Poliwrath                      | 151                       | Water        | Rare
 Ponyta                         | 151                       | Fire         | Common
 Porygon                        | 151                       | Colorless    | Common
 Primeape                       | 151                       | Fighting     | Uncommon
 Psyduck                        | 151                       | Water        | Common
 Pyroar                         | Prismatic Evolutions      | Fire         | Uncommon
 Raichu                         | 151                       | Lightning    | Rare
 Raikou                         | Prismatic Evolutions      | Lightning    | Rare
 Rapidash                       | 151                       | Fire         | Uncommon
 Raticate                       | 151                       | Colorless    | Uncommon
 Rattata                        | 151                       | Colorless    | Common
 Rhydon                         | 151                       | Fighting     | Uncommon
 Rhyhorn                        | 151                       | Fighting     | Common
 Sandshrew                      | 151                       | Fighting     | Common
 Sandslash                      | 151                       | Fighting     | Uncommon
 Seadra                         | 151                       | Water        | Uncommon
 Seaking                        | 151                       | Water        | Uncommon
 Seaking                        | Prismatic Evolutions      | Water        | Uncommon
 Seel                           | 151                       | Water        | Common
 Shellder                       | 151                       | Water        | Common
 Slowbro                        | 151                       | Psychic      | Uncommon
 Slowking                       | Prismatic Evolutions      | Water        | Uncommon
 Slowpoke                       | 151                       | Psychic      | Common
 Slowpoke                       | Prismatic Evolutions      | Water        | Common
 Slugma                         | Ascended Heroes           | Fire         | Common
 Snorlax                        | 151                       | Colorless    | Uncommon
 Spearow                        | 151                       | Colorless    | Common
 Squirtle                       | 151                       | Water        | Common
 Starmie                        | 151                       | Water        | Rare
 Staryu                         | 151                       | Water        | Common
 Steven's Metagross ex          | Ascended Heroes           | Metal        | Ultra Rare
 Stunfisk                       | Ascended Heroes           | Lightning    | Common
 Suicune                        | Prismatic Evolutions      | Water        | Rare
 Sylveon                        | Prismatic Evolutions      | Psychic      | Uncommon
 Sylveon ex                     | Prismatic Evolutions      | Psychic      | Double Rare
 Sylveon ex                     | Prismatic Evolutions      | Psychic      | Special Illustration Rare
 Tapu Koko                      | Ascended Heroes           | Lightning    | Rare
 Tauros                         | 151                       | Colorless    | Common
 Teal Mask Ogerpon ex           | Prismatic Evolutions      | Grass        | Double Rare
 Team Rocket's Exeggcute        | Ascended Heroes           | Psychic      | Common
 Team Rocket's Exeggutor        | Ascended Heroes           | Psychic      | Uncommon
 Team Rocket's Mewtwo ex        | Ascended Heroes           | Psychic      | Special Illustration Rare
 Tentacool                      | 151                       | Water        | Common
 Tentacruel                     | 151                       | Water        | Uncommon
 Togekiss                       | Ascended Heroes           | Psychic      | Rare
 Togepi                         | Ascended Heroes           | Psychic      | Common
 Togetic                        | Ascended Heroes           | Psychic      | Uncommon
 Tynamo                         | Ascended Heroes           | Lightning    | Common
 Umbreon                        | 151                       | Darkness     | Uncommon
 Umbreon                        | Prismatic Evolutions      | Darkness     | Uncommon
 Umbreon ex                     | Prismatic Evolutions      | Darkness     | Special Illustration Rare
 Umbreon ex                     | Prismatic Evolutions      | Darkness     | Special Illustration Rare
 Vaporeon                       | 151                       | Water        | Uncommon
 Vaporeon                       | Prismatic Evolutions      | Water        | Uncommon
 Vaporeon ex                    | Prismatic Evolutions      | Water        | Double Rare
 Vaporeon ex                    | Prismatic Evolutions      | Water        | Special Illustration Rare
 Venomoth                       | 151                       | Grass        | Uncommon
 Venonat                        | 151                       | Grass        | Common
 Venusaur ex                    | 151                       | Grass        | Double Rare
 Vikavolt                       | Ascended Heroes           | Lightning    | Uncommon
 Vileplume                      | 151                       | Grass        | Rare
 Voltorb                        | 151                       | Lightning    | Common
 Voltorb ex                     | Ascended Heroes           | Lightning    | Double Rare
 Vulpix                         | 151                       | Fire         | Common
 Wartortle                      | 151                       | Water        | Uncommon
 Wellspring Mask Ogerpon ex     | Prismatic Evolutions      | Water        | Double Rare
 Whimsicott                     | Prismatic Evolutions      | Grass        | Uncommon
 Wigglytuff ex                  | 151                       | Colorless    | Double Rare
 Zapdos ex                      | 151                       | Lightning    | Double Rare
 Zubat                          | 151                       | Darkness     | Common
 -------------------------------------------------------------------------------------
 Total unique cards: 237
 
 ===== POK╔MON CARD INVENTORY TRACKER =====
 1. View all cards (A-Z)
 2. Add a card
 3. Remove a card
 4. Search for a card
 5. Exit
 Choose an option: 3
 Card name to remove: Genger VMAX
 Card not found: Genger VMAX
 
 ===== POK╔MON CARD INVENTORY TRACKER =====
 1. View all cards (A-Z)
 2. Add a card
 3. Remove a card
 4. Search for a card
 5. Exit
 Choose an option: 3
 Card name to remove: Gengar VMAX
 Removed: Gengar VMAX
 
 ===== POK╔MON CARD INVENTORY TRACKER =====
 1. View all cards (A-Z)
 2. Add a card
 3. Remove a card
 4. Search for a card
 5. Exit
 Choose an option: 4
 Card name to search: Gengar VMAX
 Card not found: Gengar VMAX
 
 ===== POK╔MON CARD INVENTORY TRACKER =====
 1. View all cards (A-Z)
 2. Add a card
 3. Remove a card
 4. Search for a card
 5. Exit
 Choose an option: 1
 
 ======= POK╔MON CARD INVENTORY (A-Z) =======
 Name                           | Set                       | Energy Type  | Rarity
 -------------------------------------------------------------------------------------
 Abra                           | 151                       | Psychic      | Common
 Aerodactyl ex                  | 151                       | Colorless    | Double Rare
 Alakazam ex                    | 151                       | Psychic      | Double Rare
 Applin                         | Prismatic Evolutions      | Grass        | Common
 Arbok ex                       | 151                       | Darkness     | Double Rare
 Arcanine                       | 151                       | Fire         | Uncommon
 Arcanine ex                    | Ascended Heroes           | Fire         | Double Rare
 Articuno ex                    | 151                       | Water        | Double Rare
 Beautifly                      | Ascended Heroes           | Grass        | Uncommon
 Blastoise ex                   | 151                       | Water        | Double Rare
 Budew                          | Prismatic Evolutions      | Grass        | Common
 Bulbasaur                      | Ascended Heroes           | Grass        | Common
 Bulbasaur                      | 151                       | Grass        | Common
 Canari                         | Ascended Heroes           | Colorless    | Special Illustration Rare
 Chansey ex                     | 151                       | Colorless    | Double Rare
 Charizard ex                   | Ascended Heroes           | Fire         | Double Rare
 Charizard ex                   | 151                       | Fire         | Double Rare
 Charjabug                      | Ascended Heroes           | Lightning    | Common
 Charmander                     | 151                       | Fire         | Common
 Charmeleon                     | 151                       | Fire         | Uncommon
 Clefable                       | Ascended Heroes           | Psychic      | Uncommon
 Clefable                       | 151                       | Colorless    | Uncommon
 Clefairy                       | Ascended Heroes           | Psychic      | Common
 Clefairy                       | 151                       | Colorless    | Common
 Cloyster                       | 151                       | Water        | Uncommon
 Cottonee                       | Prismatic Evolutions      | Grass        | Common
 Cubone                         | 151                       | Fighting     | Common
 Dewgong                        | 151                       | Water        | Uncommon
 Diglett                        | 151                       | Fighting     | Common
 Dipplin                        | Prismatic Evolutions      | Grass        | Uncommon
 Ditto ex                       | 151                       | Colorless    | Double Rare
 Dodrio                         | 151                       | Colorless    | Uncommon
 Doduo                          | 151                       | Colorless    | Common
 Drowzee                        | 151                       | Psychic      | Common
 Dugtrio                        | 151                       | Fighting     | Uncommon
 Dustox                         | Ascended Heroes           | Grass        | Uncommon
 Eevee                          | 151                       | Colorless    | Common
 Eevee                          | Prismatic Evolutions      | Colorless    | Common
 Eevee ex                       | Prismatic Evolutions      | Colorless    | Double Rare
 Ekans                          | 151                       | Darkness     | Common
 Electabuzz                     | 151                       | Lightning    | Uncommon
 Electrode                      | 151                       | Lightning    | Uncommon
 Entei                          | Prismatic Evolutions      | Fire         | Rare
 Erika's Tangela                | Ascended Heroes           | Grass        | Rare
 Espeon                         | 151                       | Psychic      | Uncommon
 Espeon                         | Prismatic Evolutions      | Psychic      | Uncommon
 Espeon ex                      | Prismatic Evolutions      | Psychic      | Double Rare
 Espeon ex                      | Prismatic Evolutions      | Psychic      | Special Illustration Rare
 Ethan's Magcargo               | Ascended Heroes           | Fire         | Rare
 Exeggcute                      | Prismatic Evolutions      | Grass        | Common
 Exeggutor                      | Prismatic Evolutions      | Grass        | Uncommon
 Farfetch'd                     | 151                       | Colorless    | Uncommon
 Fearow                         | 151                       | Colorless    | Uncommon
 Fezandipiti ex                 | Ascended Heroes           | Psychic      | Ultra Rare
 Flareon                        | 151                       | Fire         | Uncommon
 Flareon                        | Prismatic Evolutions      | Fire         | Uncommon
 Flareon ex                     | Prismatic Evolutions      | Fire         | Double Rare
 Flareon ex                     | Prismatic Evolutions      | Fire         | Special Illustration Rare
 Gastly                         | 151                       | Psychic      | Common
 Gengar ex                      | 151                       | Psychic      | Double Rare
 Geodude                        | 151                       | Fighting     | Common
 Glaceon                        | Prismatic Evolutions      | Water        | Uncommon
 Glaceon ex                     | Prismatic Evolutions      | Water        | Double Rare
 Glaceon ex                     | Prismatic Evolutions      | Water        | Special Illustration Rare
 Gloom                          | 151                       | Grass        | Uncommon
 Golbat                         | 151                       | Darkness     | Uncommon
 Goldeen                        | 151                       | Water        | Common
 Goldeen                        | Prismatic Evolutions      | Water        | Common
 Golduck                        | 151                       | Water        | Uncommon
 Golem ex                       | 151                       | Fighting     | Double Rare
 Graveler                       | 151                       | Fighting     | Uncommon
 Grimer                         | 151                       | Darkness     | Common
 Growlithe                      | 151                       | Fire         | Common
 Gyarados ex                    | 151                       | Water        | Double Rare
 Haunter                        | 151                       | Psychic      | Uncommon
 Hearthflame Mask Ogerpon ex    | Prismatic Evolutions      | Fire         | Double Rare
 Hitmonchan                     | 151                       | Fighting     | Rare
 Hitmonlee                      | 151                       | Fighting     | Rare
 Hop's Pincurchin ex            | Ascended Heroes           | Lightning    | Double Rare
 Horsea                         | 151                       | Water        | Common
 Hydrapple ex                   | Prismatic Evolutions      | Grass        | Double Rare
 Hypno                          | 151                       | Psychic      | Uncommon
 Iono's Bellibolt ex            | Ascended Heroes           | Lightning    | Ultra Rare
 Iono's Kilowattrel             | Ascended Heroes           | Lightning    | Uncommon
 Iono's Tadbulb                 | Ascended Heroes           | Lightning    | Uncommon
 Iono's Wattrel                 | Ascended Heroes           | Lightning    | Common
 Ivysaur                        | Ascended Heroes           | Grass        | Uncommon
 Ivysaur                        | 151                       | Grass        | Uncommon
 Jigglypuff                     | 151                       | Colorless    | Common
 Jolteon                        | 151                       | Lightning    | Uncommon
 Jolteon                        | Prismatic Evolutions      | Lightning    | Uncommon
 Jolteon ex                     | Prismatic Evolutions      | Lightning    | Double Rare
 Jolteon ex                     | Prismatic Evolutions      | Lightning    | Special Illustration Rare
 Jynx                           | 151                       | Psychic      | Uncommon
 Kabuto                         | 151                       | Water        | Common
 Kabutops                       | 151                       | Water        | Uncommon
 Kadabra                        | 151                       | Psychic      | Uncommon
 Kangaskhan ex                  | 151                       | Colorless    | Double Rare
 Kingler                        | 151                       | Water        | Uncommon
 Krabby                         | 151                       | Water        | Common
 Lapras ex                      | 151                       | Water        | Double Rare
 Leafeon                        | Prismatic Evolutions      | Grass        | Uncommon
 Leafeon ex                     | Prismatic Evolutions      | Grass        | Double Rare
 Leafeon ex                     | Prismatic Evolutions      | Grass        | Special Illustration Rare
 Lickitung                      | 151                       | Colorless    | Common
 Lillie's Clefairy ex           | Ascended Heroes           | Psychic      | Special Illustration Rare
 Litleo                         | Prismatic Evolutions      | Fire         | Common
 Machamp ex                     | 151                       | Fighting     | Double Rare
 Machoke                        | 151                       | Fighting     | Uncommon
 Machop                         | 151                       | Fighting     | Common
 Magikarp                       | 151                       | Water        | Common
 Magmar                         | 151                       | Fire         | Common
 Magnemite                      | 151                       | Lightning    | Common
 Magneton                       | 151                       | Lightning    | Uncommon
 Mankey                         | 151                       | Fighting     | Common
 Marill                         | Ascended Heroes           | Water        | Common
 Marnie's Grimmsnarl ex         | Ascended Heroes           | Darkness     | Ultra Rare
 Marowak                        | 151                       | Fighting     | Uncommon
 Mega Blaziken ex               | Ascended Heroes           | Fire         | Ultra Rare
 Mega Charizard X ex            | Ascended Heroes           | Fire         | Mega Attack Rare
 Mega Charizard Y ex            | Ascended Heroes           | Fire         | Mega Hyper Rare
 Mega Diancie ex                | Ascended Heroes           | Fighting     | Mega Attack Rare
 Mega Dragonite ex              | Ascended Heroes           | Dragon       | Mega Hyper Rare
 Mega Eelektross ex             | Ascended Heroes           | Lightning    | Ultra Rare
 Mega Emboar ex                 | Ascended Heroes           | Grass        | Ultra Rare
 Mega Feraligatr ex             | Ascended Heroes           | Water        | Ultra Rare
 Mega Gengar ex                 | Ascended Heroes           | Darkness     | Special Illustration Rare
 Mega Gyarados ex               | Ascended Heroes           | Water        | Ultra Rare
 Mega Hawlucha ex               | Ascended Heroes           | Fighting     | Mega Attack Rare
 Mega Infernape ex              | Ascended Heroes           | Fire         | Ultra Rare
 Mega Lucario ex                | Ascended Heroes           | Metal        | Mega Attack Rare
 Mega Sceptile ex               | Ascended Heroes           | Grass        | Ultra Rare
 Mega Scrafty ex                | Ascended Heroes           | Darkness     | Ultra Rare
 Mega Swampert ex               | Ascended Heroes           | Water        | Ultra Rare
 Meowth                         | 151                       | Darkness     | Common
 Mew ex                         | 151                       | Psychic      | Double Rare
 Mewtwo ex                      | 151                       | Psychic      | Double Rare
 Miraidon ex                    | Ascended Heroes           | Lightning    | Double Rare
 Moltres ex                     | 151                       | Fire         | Double Rare
 Mr. Mime                       | 151                       | Psychic      | Uncommon
 Muk ex                         | 151                       | Darkness     | Double Rare
 N's Zoroark ex                 | Ascended Heroes           | Darkness     | Ultra Rare
 Nidoking                       | 151                       | Fighting     | Rare
 Nidoqueen                      | 151                       | Fighting     | Rare
 Nidoran F                      | 151                       | Fighting     | Common
 Nidoran M                      | 151                       | Fighting     | Common
 Nidorina                       | 151                       | Fighting     | Uncommon
 Nidorino                       | 151                       | Fighting     | Uncommon
 Ninetales ex                   | 151                       | Fire         | Double Rare
 Numel                          | Ascended Heroes           | Fire         | Common
 Oddish                         | 151                       | Grass        | Common
 Omanyte                        | 151                       | Water        | Common
 Omastar                        | 151                       | Water        | Uncommon
 Onix                           | 151                       | Fighting     | Common
 Paras                          | 151                       | Grass        | Common
 Parasect                       | 151                       | Grass        | Uncommon
 Persian                        | 151                       | Darkness     | Uncommon
 Pidgeot ex                     | 151                       | Colorless    | Double Rare
 Pidgeotto                      | 151                       | Colorless    | Uncommon
 Pidgey                         | 151                       | Colorless    | Common
 Pikachu                        | 151                       | Lightning    | Common
 Pikachu ex                     | Ascended Heroes           | Lightning    | Special Illustration Rare
 Pikachu ex                     | Prismatic Evolutions      | Lightning    | Double Rare
 Pinsir                         | Prismatic Evolutions      | Grass        | Common
 Poliwag                        | 151                       | Water        | Common
 Poliwhirl                      | 151                       | Water        | Uncommon
 Poliwrath                      | 151                       | Water        | Rare
 Ponyta                         | 151                       | Fire         | Common
 Porygon                        | 151                       | Colorless    | Common
 Primeape                       | 151                       | Fighting     | Uncommon
 Psyduck                        | 151                       | Water        | Common
 Pyroar                         | Prismatic Evolutions      | Fire         | Uncommon
 Raichu                         | 151                       | Lightning    | Rare
 Raikou                         | Prismatic Evolutions      | Lightning    | Rare
 Rapidash                       | 151                       | Fire         | Uncommon
 Raticate                       | 151                       | Colorless    | Uncommon
 Rattata                        | 151                       | Colorless    | Common
 Rhydon                         | 151                       | Fighting     | Uncommon
 Rhyhorn                        | 151                       | Fighting     | Common
 Sandshrew                      | 151                       | Fighting     | Common
 Sandslash                      | 151                       | Fighting     | Uncommon
 Seadra                         | 151                       | Water        | Uncommon
 Seaking                        | 151                       | Water        | Uncommon
 Seaking                        | Prismatic Evolutions      | Water        | Uncommon
 Seel                           | 151                       | Water        | Common
 Shellder                       | 151                       | Water        | Common
 Slowbro                        | 151                       | Psychic      | Uncommon
 Slowking                       | Prismatic Evolutions      | Water        | Uncommon
 Slowpoke                       | 151                       | Psychic      | Common
 Slowpoke                       | Prismatic Evolutions      | Water        | Common
 Slugma                         | Ascended Heroes           | Fire         | Common
 Snorlax                        | 151                       | Colorless    | Uncommon
 Spearow                        | 151                       | Colorless    | Common
 Squirtle                       | 151                       | Water        | Common
 Starmie                        | 151                       | Water        | Rare
 Staryu                         | 151                       | Water        | Common
 Steven's Metagross ex          | Ascended Heroes           | Metal        | Ultra Rare
 Stunfisk                       | Ascended Heroes           | Lightning    | Common
 Suicune                        | Prismatic Evolutions      | Water        | Rare
 Sylveon                        | Prismatic Evolutions      | Psychic      | Uncommon
 Sylveon ex                     | Prismatic Evolutions      | Psychic      | Double Rare
 Sylveon ex                     | Prismatic Evolutions      | Psychic      | Special Illustration Rare
 Tapu Koko                      | Ascended Heroes           | Lightning    | Rare
 Tauros                         | 151                       | Colorless    | Common
 Teal Mask Ogerpon ex           | Prismatic Evolutions      | Grass        | Double Rare
 Team Rocket's Exeggcute        | Ascended Heroes           | Psychic      | Common
 Team Rocket's Exeggutor        | Ascended Heroes           | Psychic      | Uncommon
 Team Rocket's Mewtwo ex        | Ascended Heroes           | Psychic      | Special Illustration Rare
 Tentacool                      | 151                       | Water        | Common
 Tentacruel                     | 151                       | Water        | Uncommon
 Togekiss                       | Ascended Heroes           | Psychic      | Rare
 Togepi                         | Ascended Heroes           | Psychic      | Common
 Togetic                        | Ascended Heroes           | Psychic      | Uncommon
 Tynamo                         | Ascended Heroes           | Lightning    | Common
 Umbreon                        | 151                       | Darkness     | Uncommon
 Umbreon                        | Prismatic Evolutions      | Darkness     | Uncommon
 Umbreon ex                     | Prismatic Evolutions      | Darkness     | Special Illustration Rare
 Umbreon ex                     | Prismatic Evolutions      | Darkness     | Special Illustration Rare
 Vaporeon                       | 151                       | Water        | Uncommon
 Vaporeon                       | Prismatic Evolutions      | Water        | Uncommon
 Vaporeon ex                    | Prismatic Evolutions      | Water        | Double Rare
 Vaporeon ex                    | Prismatic Evolutions      | Water        | Special Illustration Rare
 Venomoth                       | 151                       | Grass        | Uncommon
 Venonat                        | 151                       | Grass        | Common
 Venusaur ex                    | 151                       | Grass        | Double Rare
 Vikavolt                       | Ascended Heroes           | Lightning    | Uncommon
 Vileplume                      | 151                       | Grass        | Rare
 Voltorb                        | 151                       | Lightning    | Common
 Voltorb ex                     | Ascended Heroes           | Lightning    | Double Rare
 Vulpix                         | 151                       | Fire         | Common
 Wartortle                      | 151                       | Water        | Uncommon
 Wellspring Mask Ogerpon ex     | Prismatic Evolutions      | Water        | Double Rare
 Whimsicott                     | Prismatic Evolutions      | Grass        | Uncommon
 Wigglytuff ex                  | 151                       | Colorless    | Double Rare
 Zapdos ex                      | 151                       | Lightning    | Double Rare
 Zubat                          | 151                       | Darkness     | Common
 -------------------------------------------------------------------------------------
 Total unique cards: 236
 
 ===== POK╔MON CARD INVENTORY TRACKER =====
 1. View all cards (A-Z)
 2. Add a card
 3. Remove a card
 4. Search for a card
 5. Exit
 Choose an option: 
  ----jGRASP: Process ended by user.
 
  ----jGRASP exec: java PokemonInventory
 Loaded 236 cards from file.
 Skipped 4 unreadable line(s).
 
 ===== POK╔MON CARD INVENTORY TRACKER =====
 1. View all cards (A-Z)
 2. Add a card
 3. Remove a card
 4. Search for a card
 5. Exit
 Choose an option: 1
 
 ======= POK╔MON CARD INVENTORY (A-Z) =======
 Name                           | Set                       | Energy Type  | Rarity
 -------------------------------------------------------------------------------------
 Abra                           | 151                       | Psychic      | Common
 Aerodactyl ex                  | 151                       | Colorless    | Double Rare
 Alakazam ex                    | 151                       | Psychic      | Double Rare
 Applin                         | Prismatic Evolutions      | Grass        | Common
 Arbok ex                       | 151                       | Darkness     | Double Rare
 Arcanine                       | 151                       | Fire         | Uncommon
 Arcanine ex                    | Ascended Heroes           | Fire         | Double Rare
 Articuno ex                    | 151                       | Water        | Double Rare
 Beautifly                      | Ascended Heroes           | Grass        | Uncommon
 Blastoise ex                   | 151                       | Water        | Double Rare
 Budew                          | Prismatic Evolutions      | Grass        | Common
 Bulbasaur                      | Ascended Heroes           | Grass        | Common
 Bulbasaur                      | 151                       | Grass        | Common
 Canari                         | Ascended Heroes           | Colorless    | Special Illustration Rare
 Chansey ex                     | 151                       | Colorless    | Double Rare
 Charizard ex                   | Ascended Heroes           | Fire         | Double Rare
 Charizard ex                   | 151                       | Fire         | Double Rare
 Charjabug                      | Ascended Heroes           | Lightning    | Common
 Charmander                     | 151                       | Fire         | Common
 Charmeleon                     | 151                       | Fire         | Uncommon
 Clefable                       | Ascended Heroes           | Psychic      | Uncommon
 Clefable                       | 151                       | Colorless    | Uncommon
 Clefairy                       | Ascended Heroes           | Psychic      | Common
 Clefairy                       | 151                       | Colorless    | Common
 Cloyster                       | 151                       | Water        | Uncommon
 Cottonee                       | Prismatic Evolutions      | Grass        | Common
 Cubone                         | 151                       | Fighting     | Common
 Dewgong                        | 151                       | Water        | Uncommon
 Diglett                        | 151                       | Fighting     | Common
 Dipplin                        | Prismatic Evolutions      | Grass        | Uncommon
 Ditto ex                       | 151                       | Colorless    | Double Rare
 Dodrio                         | 151                       | Colorless    | Uncommon
 Doduo                          | 151                       | Colorless    | Common
 Drowzee                        | 151                       | Psychic      | Common
 Dugtrio                        | 151                       | Fighting     | Uncommon
 Dustox                         | Ascended Heroes           | Grass        | Uncommon
 Eevee                          | 151                       | Colorless    | Common
 Eevee                          | Prismatic Evolutions      | Colorless    | Common
 Eevee ex                       | Prismatic Evolutions      | Colorless    | Double Rare
 Ekans                          | 151                       | Darkness     | Common
 Electabuzz                     | 151                       | Lightning    | Uncommon
 Electrode                      | 151                       | Lightning    | Uncommon
 Entei                          | Prismatic Evolutions      | Fire         | Rare
 Erika's Tangela                | Ascended Heroes           | Grass        | Rare
 Espeon                         | 151                       | Psychic      | Uncommon
 Espeon                         | Prismatic Evolutions      | Psychic      | Uncommon
 Espeon ex                      | Prismatic Evolutions      | Psychic      | Double Rare
 Espeon ex                      | Prismatic Evolutions      | Psychic      | Special Illustration Rare
 Ethan's Magcargo               | Ascended Heroes           | Fire         | Rare
 Exeggcute                      | Prismatic Evolutions      | Grass        | Common
 Exeggutor                      | Prismatic Evolutions      | Grass        | Uncommon
 Farfetch'd                     | 151                       | Colorless    | Uncommon
 Fearow                         | 151                       | Colorless    | Uncommon
 Fezandipiti ex                 | Ascended Heroes           | Psychic      | Ultra Rare
 Flareon                        | 151                       | Fire         | Uncommon
 Flareon                        | Prismatic Evolutions      | Fire         | Uncommon
 Flareon ex                     | Prismatic Evolutions      | Fire         | Double Rare
 Flareon ex                     | Prismatic Evolutions      | Fire         | Special Illustration Rare
 Gastly                         | 151                       | Psychic      | Common
 Gengar ex                      | 151                       | Psychic      | Double Rare
 Geodude                        | 151                       | Fighting     | Common
 Glaceon                        | Prismatic Evolutions      | Water        | Uncommon
 Glaceon ex                     | Prismatic Evolutions      | Water        | Double Rare
 Glaceon ex                     | Prismatic Evolutions      | Water        | Special Illustration Rare
 Gloom                          | 151                       | Grass        | Uncommon
 Golbat                         | 151                       | Darkness     | Uncommon
 Goldeen                        | 151                       | Water        | Common
 Goldeen                        | Prismatic Evolutions      | Water        | Common
 Golduck                        | 151                       | Water        | Uncommon
 Golem ex                       | 151                       | Fighting     | Double Rare
 Graveler                       | 151                       | Fighting     | Uncommon
 Grimer                         | 151                       | Darkness     | Common
 Growlithe                      | 151                       | Fire         | Common
 Gyarados ex                    | 151                       | Water        | Double Rare
 Haunter                        | 151                       | Psychic      | Uncommon
 Hearthflame Mask Ogerpon ex    | Prismatic Evolutions      | Fire         | Double Rare
 Hitmonchan                     | 151                       | Fighting     | Rare
 Hitmonlee                      | 151                       | Fighting     | Rare
 Hop's Pincurchin ex            | Ascended Heroes           | Lightning    | Double Rare
 Horsea                         | 151                       | Water        | Common
 Hydrapple ex                   | Prismatic Evolutions      | Grass        | Double Rare
 Hypno                          | 151                       | Psychic      | Uncommon
 Iono's Bellibolt ex            | Ascended Heroes           | Lightning    | Ultra Rare
 Iono's Kilowattrel             | Ascended Heroes           | Lightning    | Uncommon
 Iono's Tadbulb                 | Ascended Heroes           | Lightning    | Uncommon
 Iono's Wattrel                 | Ascended Heroes           | Lightning    | Common
 Ivysaur                        | Ascended Heroes           | Grass        | Uncommon
 Ivysaur                        | 151                       | Grass        | Uncommon
 Jigglypuff                     | 151                       | Colorless    | Common
 Jolteon                        | 151                       | Lightning    | Uncommon
 Jolteon                        | Prismatic Evolutions      | Lightning    | Uncommon
 Jolteon ex                     | Prismatic Evolutions      | Lightning    | Double Rare
 Jolteon ex                     | Prismatic Evolutions      | Lightning    | Special Illustration Rare
 Jynx                           | 151                       | Psychic      | Uncommon
 Kabuto                         | 151                       | Water        | Common
 Kabutops                       | 151                       | Water        | Uncommon
 Kadabra                        | 151                       | Psychic      | Uncommon
 Kangaskhan ex                  | 151                       | Colorless    | Double Rare
 Kingler                        | 151                       | Water        | Uncommon
 Krabby                         | 151                       | Water        | Common
 Lapras ex                      | 151                       | Water        | Double Rare
 Leafeon                        | Prismatic Evolutions      | Grass        | Uncommon
 Leafeon ex                     | Prismatic Evolutions      | Grass        | Double Rare
 Leafeon ex                     | Prismatic Evolutions      | Grass        | Special Illustration Rare
 Lickitung                      | 151                       | Colorless    | Common
 Lillie's Clefairy ex           | Ascended Heroes           | Psychic      | Special Illustration Rare
 Litleo                         | Prismatic Evolutions      | Fire         | Common
 Machamp ex                     | 151                       | Fighting     | Double Rare
 Machoke                        | 151                       | Fighting     | Uncommon
 Machop                         | 151                       | Fighting     | Common
 Magikarp                       | 151                       | Water        | Common
 Magmar                         | 151                       | Fire         | Common
 Magnemite                      | 151                       | Lightning    | Common
 Magneton                       | 151                       | Lightning    | Uncommon
 Mankey                         | 151                       | Fighting     | Common
 Marill                         | Ascended Heroes           | Water        | Common
 Marnie's Grimmsnarl ex         | Ascended Heroes           | Darkness     | Ultra Rare
 Marowak                        | 151                       | Fighting     | Uncommon
 Mega Blaziken ex               | Ascended Heroes           | Fire         | Ultra Rare
 Mega Charizard X ex            | Ascended Heroes           | Fire         | Mega Attack Rare
 Mega Charizard Y ex            | Ascended Heroes           | Fire         | Mega Hyper Rare
 Mega Diancie ex                | Ascended Heroes           | Fighting     | Mega Attack Rare
 Mega Dragonite ex              | Ascended Heroes           | Dragon       | Mega Hyper Rare
 Mega Eelektross ex             | Ascended Heroes           | Lightning    | Ultra Rare
 Mega Emboar ex                 | Ascended Heroes           | Grass        | Ultra Rare
 Mega Feraligatr ex             | Ascended Heroes           | Water        | Ultra Rare
 Mega Gengar ex                 | Ascended Heroes           | Darkness     | Special Illustration Rare
 Mega Gyarados ex               | Ascended Heroes           | Water        | Ultra Rare
 Mega Hawlucha ex               | Ascended Heroes           | Fighting     | Mega Attack Rare
 Mega Infernape ex              | Ascended Heroes           | Fire         | Ultra Rare
 Mega Lucario ex                | Ascended Heroes           | Metal        | Mega Attack Rare
 Mega Sceptile ex               | Ascended Heroes           | Grass        | Ultra Rare
 Mega Scrafty ex                | Ascended Heroes           | Darkness     | Ultra Rare
 Mega Swampert ex               | Ascended Heroes           | Water        | Ultra Rare
 Meowth                         | 151                       | Darkness     | Common
 Mew ex                         | 151                       | Psychic      | Double Rare
 Mewtwo ex                      | 151                       | Psychic      | Double Rare
 Miraidon ex                    | Ascended Heroes           | Lightning    | Double Rare
 Moltres ex                     | 151                       | Fire         | Double Rare
 Mr. Mime                       | 151                       | Psychic      | Uncommon
 Muk ex                         | 151                       | Darkness     | Double Rare
 N's Zoroark ex                 | Ascended Heroes           | Darkness     | Ultra Rare
 Nidoking                       | 151                       | Fighting     | Rare
 Nidoqueen                      | 151                       | Fighting     | Rare
 Nidoran F                      | 151                       | Fighting     | Common
 Nidoran M                      | 151                       | Fighting     | Common
 Nidorina                       | 151                       | Fighting     | Uncommon
 Nidorino                       | 151                       | Fighting     | Uncommon
 Ninetales ex                   | 151                       | Fire         | Double Rare
 Numel                          | Ascended Heroes           | Fire         | Common
 Oddish                         | 151                       | Grass        | Common
 Omanyte                        | 151                       | Water        | Common
 Omastar                        | 151                       | Water        | Uncommon
 Onix                           | 151                       | Fighting     | Common
 Paras                          | 151                       | Grass        | Common
 Parasect                       | 151                       | Grass        | Uncommon
 Persian                        | 151                       | Darkness     | Uncommon
 Pidgeot ex                     | 151                       | Colorless    | Double Rare
 Pidgeotto                      | 151                       | Colorless    | Uncommon
 Pidgey                         | 151                       | Colorless    | Common
 Pikachu                        | 151                       | Lightning    | Common
 Pikachu ex                     | Ascended Heroes           | Lightning    | Special Illustration Rare
 Pikachu ex                     | Prismatic Evolutions      | Lightning    | Double Rare
 Pinsir                         | Prismatic Evolutions      | Grass        | Common
 Poliwag                        | 151                       | Water        | Common
 Poliwhirl                      | 151                       | Water        | Uncommon
 Poliwrath                      | 151                       | Water        | Rare
 Ponyta                         | 151                       | Fire         | Common
 Porygon                        | 151                       | Colorless    | Common
 Primeape                       | 151                       | Fighting     | Uncommon
 Psyduck                        | 151                       | Water        | Common
 Pyroar                         | Prismatic Evolutions      | Fire         | Uncommon
 Raichu                         | 151                       | Lightning    | Rare
 Raikou                         | Prismatic Evolutions      | Lightning    | Rare
 Rapidash                       | 151                       | Fire         | Uncommon
 Raticate                       | 151                       | Colorless    | Uncommon
 Rattata                        | 151                       | Colorless    | Common
 Rhydon                         | 151                       | Fighting     | Uncommon
 Rhyhorn                        | 151                       | Fighting     | Common
 Sandshrew                      | 151                       | Fighting     | Common
 Sandslash                      | 151                       | Fighting     | Uncommon
 Seadra                         | 151                       | Water        | Uncommon
 Seaking                        | 151                       | Water        | Uncommon
 Seaking                        | Prismatic Evolutions      | Water        | Uncommon
 Seel                           | 151                       | Water        | Common
 Shellder                       | 151                       | Water        | Common
 Slowbro                        | 151                       | Psychic      | Uncommon
 Slowking                       | Prismatic Evolutions      | Water        | Uncommon
 Slowpoke                       | 151                       | Psychic      | Common
 Slowpoke                       | Prismatic Evolutions      | Water        | Common
 Slugma                         | Ascended Heroes           | Fire         | Common
 Snorlax                        | 151                       | Colorless    | Uncommon
 Spearow                        | 151                       | Colorless    | Common
 Squirtle                       | 151                       | Water        | Common
 Starmie                        | 151                       | Water        | Rare
 Staryu                         | 151                       | Water        | Common
 Steven's Metagross ex          | Ascended Heroes           | Metal        | Ultra Rare
 Stunfisk                       | Ascended Heroes           | Lightning    | Common
 Suicune                        | Prismatic Evolutions      | Water        | Rare
 Sylveon                        | Prismatic Evolutions      | Psychic      | Uncommon
 Sylveon ex                     | Prismatic Evolutions      | Psychic      | Double Rare
 Sylveon ex                     | Prismatic Evolutions      | Psychic      | Special Illustration Rare
 Tapu Koko                      | Ascended Heroes           | Lightning    | Rare
 Tauros                         | 151                       | Colorless    | Common
 Teal Mask Ogerpon ex           | Prismatic Evolutions      | Grass        | Double Rare
 Team Rocket's Exeggcute        | Ascended Heroes           | Psychic      | Common
 Team Rocket's Exeggutor        | Ascended Heroes           | Psychic      | Uncommon
 Team Rocket's Mewtwo ex        | Ascended Heroes           | Psychic      | Special Illustration Rare
 Tentacool                      | 151                       | Water        | Common
 Tentacruel                     | 151                       | Water        | Uncommon
 Togekiss                       | Ascended Heroes           | Psychic      | Rare
 Togepi                         | Ascended Heroes           | Psychic      | Common
 Togetic                        | Ascended Heroes           | Psychic      | Uncommon
 Tynamo                         | Ascended Heroes           | Lightning    | Common
 Umbreon                        | 151                       | Darkness     | Uncommon
 Umbreon                        | Prismatic Evolutions      | Darkness     | Uncommon
 Umbreon ex                     | Prismatic Evolutions      | Darkness     | Special Illustration Rare
 Umbreon ex                     | Prismatic Evolutions      | Darkness     | Special Illustration Rare
 Vaporeon                       | 151                       | Water        | Uncommon
 Vaporeon                       | Prismatic Evolutions      | Water        | Uncommon
 Vaporeon ex                    | Prismatic Evolutions      | Water        | Double Rare
 Vaporeon ex                    | Prismatic Evolutions      | Water        | Special Illustration Rare
 Venomoth                       | 151                       | Grass        | Uncommon
 Venonat                        | 151                       | Grass        | Common
 Venusaur ex                    | 151                       | Grass        | Double Rare
 Vikavolt                       | Ascended Heroes           | Lightning    | Uncommon
 Vileplume                      | 151                       | Grass        | Rare
 Voltorb                        | 151                       | Lightning    | Common
 Voltorb ex                     | Ascended Heroes           | Lightning    | Double Rare
 Vulpix                         | 151                       | Fire         | Common
 Wartortle                      | 151                       | Water        | Uncommon
 Wellspring Mask Ogerpon ex     | Prismatic Evolutions      | Water        | Double Rare
 Whimsicott                     | Prismatic Evolutions      | Grass        | Uncommon
 Wigglytuff ex                  | 151                       | Colorless    | Double Rare
 Zapdos ex                      | 151                       | Lightning    | Double Rare
 Zubat                          | 151                       | Darkness     | Common
 -------------------------------------------------------------------------------------
 Total unique cards: 236
 
 ===== POK╔MON CARD INVENTORY TRACKER =====
 1. View all cards (A-Z)
 2. Add a card
 3. Remove a card
 4. Search for a card
 5. Exit
 Choose an option: 2
 Card name: Gengar VMAX
 Set: Fusion Strike
 Energy type (Fire, Water, Grass, etc.): Darkness
 Rarity (Common, Uncommon, Rare, etc.): Alternate Art Secret
 Added: Gengar VMAX
 
 ===== POK╔MON CARD INVENTORY TRACKER =====
 1. View all cards (A-Z)
 2. Add a card
 3. Remove a card
 4. Search for a card
 5. Exit
 Choose an option: 1
 
 ======= POK╔MON CARD INVENTORY (A-Z) =======
 Name                           | Set                       | Energy Type  | Rarity
 -------------------------------------------------------------------------------------
 Abra                           | 151                       | Psychic      | Common
 Aerodactyl ex                  | 151                       | Colorless    | Double Rare
 Alakazam ex                    | 151                       | Psychic      | Double Rare
 Applin                         | Prismatic Evolutions      | Grass        | Common
 Arbok ex                       | 151                       | Darkness     | Double Rare
 Arcanine                       | 151                       | Fire         | Uncommon
 Arcanine ex                    | Ascended Heroes           | Fire         | Double Rare
 Articuno ex                    | 151                       | Water        | Double Rare
 Beautifly                      | Ascended Heroes           | Grass        | Uncommon
 Blastoise ex                   | 151                       | Water        | Double Rare
 Budew                          | Prismatic Evolutions      | Grass        | Common
 Bulbasaur                      | Ascended Heroes           | Grass        | Common
 Bulbasaur                      | 151                       | Grass        | Common
 Canari                         | Ascended Heroes           | Colorless    | Special Illustration Rare
 Chansey ex                     | 151                       | Colorless    | Double Rare
 Charizard ex                   | Ascended Heroes           | Fire         | Double Rare
 Charizard ex                   | 151                       | Fire         | Double Rare
 Charjabug                      | Ascended Heroes           | Lightning    | Common
 Charmander                     | 151                       | Fire         | Common
 Charmeleon                     | 151                       | Fire         | Uncommon
 Clefable                       | Ascended Heroes           | Psychic      | Uncommon
 Clefable                       | 151                       | Colorless    | Uncommon
 Clefairy                       | Ascended Heroes           | Psychic      | Common
 Clefairy                       | 151                       | Colorless    | Common
 Cloyster                       | 151                       | Water        | Uncommon
 Cottonee                       | Prismatic Evolutions      | Grass        | Common
 Cubone                         | 151                       | Fighting     | Common
 Dewgong                        | 151                       | Water        | Uncommon
 Diglett                        | 151                       | Fighting     | Common
 Dipplin                        | Prismatic Evolutions      | Grass        | Uncommon
 Ditto ex                       | 151                       | Colorless    | Double Rare
 Dodrio                         | 151                       | Colorless    | Uncommon
 Doduo                          | 151                       | Colorless    | Common
 Drowzee                        | 151                       | Psychic      | Common
 Dugtrio                        | 151                       | Fighting     | Uncommon
 Dustox                         | Ascended Heroes           | Grass        | Uncommon
 Eevee                          | 151                       | Colorless    | Common
 Eevee                          | Prismatic Evolutions      | Colorless    | Common
 Eevee ex                       | Prismatic Evolutions      | Colorless    | Double Rare
 Ekans                          | 151                       | Darkness     | Common
 Electabuzz                     | 151                       | Lightning    | Uncommon
 Electrode                      | 151                       | Lightning    | Uncommon
 Entei                          | Prismatic Evolutions      | Fire         | Rare
 Erika's Tangela                | Ascended Heroes           | Grass        | Rare
 Espeon                         | 151                       | Psychic      | Uncommon
 Espeon                         | Prismatic Evolutions      | Psychic      | Uncommon
 Espeon ex                      | Prismatic Evolutions      | Psychic      | Double Rare
 Espeon ex                      | Prismatic Evolutions      | Psychic      | Special Illustration Rare
 Ethan's Magcargo               | Ascended Heroes           | Fire         | Rare
 Exeggcute                      | Prismatic Evolutions      | Grass        | Common
 Exeggutor                      | Prismatic Evolutions      | Grass        | Uncommon
 Farfetch'd                     | 151                       | Colorless    | Uncommon
 Fearow                         | 151                       | Colorless    | Uncommon
 Fezandipiti ex                 | Ascended Heroes           | Psychic      | Ultra Rare
 Flareon                        | 151                       | Fire         | Uncommon
 Flareon                        | Prismatic Evolutions      | Fire         | Uncommon
 Flareon ex                     | Prismatic Evolutions      | Fire         | Double Rare
 Flareon ex                     | Prismatic Evolutions      | Fire         | Special Illustration Rare
 Gastly                         | 151                       | Psychic      | Common
 Gengar ex                      | 151                       | Psychic      | Double Rare
 Gengar VMAX                    | Fusion Strike             | Darkness     | Alternate Art Secret
 Geodude                        | 151                       | Fighting     | Common
 Glaceon                        | Prismatic Evolutions      | Water        | Uncommon
 Glaceon ex                     | Prismatic Evolutions      | Water        | Double Rare
 Glaceon ex                     | Prismatic Evolutions      | Water        | Special Illustration Rare
 Gloom                          | 151                       | Grass        | Uncommon
 Golbat                         | 151                       | Darkness     | Uncommon
 Goldeen                        | 151                       | Water        | Common
 Goldeen                        | Prismatic Evolutions      | Water        | Common
 Golduck                        | 151                       | Water        | Uncommon
 Golem ex                       | 151                       | Fighting     | Double Rare
 Graveler                       | 151                       | Fighting     | Uncommon
 Grimer                         | 151                       | Darkness     | Common
 Growlithe                      | 151                       | Fire         | Common
 Gyarados ex                    | 151                       | Water        | Double Rare
 Haunter                        | 151                       | Psychic      | Uncommon
 Hearthflame Mask Ogerpon ex    | Prismatic Evolutions      | Fire         | Double Rare
 Hitmonchan                     | 151                       | Fighting     | Rare
 Hitmonlee                      | 151                       | Fighting     | Rare
 Hop's Pincurchin ex            | Ascended Heroes           | Lightning    | Double Rare
 Horsea                         | 151                       | Water        | Common
 Hydrapple ex                   | Prismatic Evolutions      | Grass        | Double Rare
 Hypno                          | 151                       | Psychic      | Uncommon
 Iono's Bellibolt ex            | Ascended Heroes           | Lightning    | Ultra Rare
 Iono's Kilowattrel             | Ascended Heroes           | Lightning    | Uncommon
 Iono's Tadbulb                 | Ascended Heroes           | Lightning    | Uncommon
 Iono's Wattrel                 | Ascended Heroes           | Lightning    | Common
 Ivysaur                        | Ascended Heroes           | Grass        | Uncommon
 Ivysaur                        | 151                       | Grass        | Uncommon
 Jigglypuff                     | 151                       | Colorless    | Common
 Jolteon                        | 151                       | Lightning    | Uncommon
 Jolteon                        | Prismatic Evolutions      | Lightning    | Uncommon
 Jolteon ex                     | Prismatic Evolutions      | Lightning    | Double Rare
 Jolteon ex                     | Prismatic Evolutions      | Lightning    | Special Illustration Rare
 Jynx                           | 151                       | Psychic      | Uncommon
 Kabuto                         | 151                       | Water        | Common
 Kabutops                       | 151                       | Water        | Uncommon
 Kadabra                        | 151                       | Psychic      | Uncommon
 Kangaskhan ex                  | 151                       | Colorless    | Double Rare
 Kingler                        | 151                       | Water        | Uncommon
 Krabby                         | 151                       | Water        | Common
 Lapras ex                      | 151                       | Water        | Double Rare
 Leafeon                        | Prismatic Evolutions      | Grass        | Uncommon
 Leafeon ex                     | Prismatic Evolutions      | Grass        | Double Rare
 Leafeon ex                     | Prismatic Evolutions      | Grass        | Special Illustration Rare
 Lickitung                      | 151                       | Colorless    | Common
 Lillie's Clefairy ex           | Ascended Heroes           | Psychic      | Special Illustration Rare
 Litleo                         | Prismatic Evolutions      | Fire         | Common
 Machamp ex                     | 151                       | Fighting     | Double Rare
 Machoke                        | 151                       | Fighting     | Uncommon
 Machop                         | 151                       | Fighting     | Common
 Magikarp                       | 151                       | Water        | Common
 Magmar                         | 151                       | Fire         | Common
 Magnemite                      | 151                       | Lightning    | Common
 Magneton                       | 151                       | Lightning    | Uncommon
 Mankey                         | 151                       | Fighting     | Common
 Marill                         | Ascended Heroes           | Water        | Common
 Marnie's Grimmsnarl ex         | Ascended Heroes           | Darkness     | Ultra Rare
 Marowak                        | 151                       | Fighting     | Uncommon
 Mega Blaziken ex               | Ascended Heroes           | Fire         | Ultra Rare
 Mega Charizard X ex            | Ascended Heroes           | Fire         | Mega Attack Rare
 Mega Charizard Y ex            | Ascended Heroes           | Fire         | Mega Hyper Rare
 Mega Diancie ex                | Ascended Heroes           | Fighting     | Mega Attack Rare
 Mega Dragonite ex              | Ascended Heroes           | Dragon       | Mega Hyper Rare
 Mega Eelektross ex             | Ascended Heroes           | Lightning    | Ultra Rare
 Mega Emboar ex                 | Ascended Heroes           | Grass        | Ultra Rare
 Mega Feraligatr ex             | Ascended Heroes           | Water        | Ultra Rare
 Mega Gengar ex                 | Ascended Heroes           | Darkness     | Special Illustration Rare
 Mega Gyarados ex               | Ascended Heroes           | Water        | Ultra Rare
 Mega Hawlucha ex               | Ascended Heroes           | Fighting     | Mega Attack Rare
 Mega Infernape ex              | Ascended Heroes           | Fire         | Ultra Rare
 Mega Lucario ex                | Ascended Heroes           | Metal        | Mega Attack Rare
 Mega Sceptile ex               | Ascended Heroes           | Grass        | Ultra Rare
 Mega Scrafty ex                | Ascended Heroes           | Darkness     | Ultra Rare
 Mega Swampert ex               | Ascended Heroes           | Water        | Ultra Rare
 Meowth                         | 151                       | Darkness     | Common
 Mew ex                         | 151                       | Psychic      | Double Rare
 Mewtwo ex                      | 151                       | Psychic      | Double Rare
 Miraidon ex                    | Ascended Heroes           | Lightning    | Double Rare
 Moltres ex                     | 151                       | Fire         | Double Rare
 Mr. Mime                       | 151                       | Psychic      | Uncommon
 Muk ex                         | 151                       | Darkness     | Double Rare
 N's Zoroark ex                 | Ascended Heroes           | Darkness     | Ultra Rare
 Nidoking                       | 151                       | Fighting     | Rare
 Nidoqueen                      | 151                       | Fighting     | Rare
 Nidoran F                      | 151                       | Fighting     | Common
 Nidoran M                      | 151                       | Fighting     | Common
 Nidorina                       | 151                       | Fighting     | Uncommon
 Nidorino                       | 151                       | Fighting     | Uncommon
 Ninetales ex                   | 151                       | Fire         | Double Rare
 Numel                          | Ascended Heroes           | Fire         | Common
 Oddish                         | 151                       | Grass        | Common
 Omanyte                        | 151                       | Water        | Common
 Omastar                        | 151                       | Water        | Uncommon
 Onix                           | 151                       | Fighting     | Common
 Paras                          | 151                       | Grass        | Common
 Parasect                       | 151                       | Grass        | Uncommon
 Persian                        | 151                       | Darkness     | Uncommon
 Pidgeot ex                     | 151                       | Colorless    | Double Rare
 Pidgeotto                      | 151                       | Colorless    | Uncommon
 Pidgey                         | 151                       | Colorless    | Common
 Pikachu                        | 151                       | Lightning    | Common
 Pikachu ex                     | Ascended Heroes           | Lightning    | Special Illustration Rare
 Pikachu ex                     | Prismatic Evolutions      | Lightning    | Double Rare
 Pinsir                         | Prismatic Evolutions      | Grass        | Common
 Poliwag                        | 151                       | Water        | Common
 Poliwhirl                      | 151                       | Water        | Uncommon
 Poliwrath                      | 151                       | Water        | Rare
 Ponyta                         | 151                       | Fire         | Common
 Porygon                        | 151                       | Colorless    | Common
 Primeape                       | 151                       | Fighting     | Uncommon
 Psyduck                        | 151                       | Water        | Common
 Pyroar                         | Prismatic Evolutions      | Fire         | Uncommon
 Raichu                         | 151                       | Lightning    | Rare
 Raikou                         | Prismatic Evolutions      | Lightning    | Rare
 Rapidash                       | 151                       | Fire         | Uncommon
 Raticate                       | 151                       | Colorless    | Uncommon
 Rattata                        | 151                       | Colorless    | Common
 Rhydon                         | 151                       | Fighting     | Uncommon
 Rhyhorn                        | 151                       | Fighting     | Common
 Sandshrew                      | 151                       | Fighting     | Common
 Sandslash                      | 151                       | Fighting     | Uncommon
 Seadra                         | 151                       | Water        | Uncommon
 Seaking                        | 151                       | Water        | Uncommon
 Seaking                        | Prismatic Evolutions      | Water        | Uncommon
 Seel                           | 151                       | Water        | Common
 Shellder                       | 151                       | Water        | Common
 Slowbro                        | 151                       | Psychic      | Uncommon
 Slowking                       | Prismatic Evolutions      | Water        | Uncommon
 Slowpoke                       | 151                       | Psychic      | Common
 Slowpoke                       | Prismatic Evolutions      | Water        | Common
 Slugma                         | Ascended Heroes           | Fire         | Common
 Snorlax                        | 151                       | Colorless    | Uncommon
 Spearow                        | 151                       | Colorless    | Common
 Squirtle                       | 151                       | Water        | Common
 Starmie                        | 151                       | Water        | Rare
 Staryu                         | 151                       | Water        | Common
 Steven's Metagross ex          | Ascended Heroes           | Metal        | Ultra Rare
 Stunfisk                       | Ascended Heroes           | Lightning    | Common
 Suicune                        | Prismatic Evolutions      | Water        | Rare
 Sylveon                        | Prismatic Evolutions      | Psychic      | Uncommon
 Sylveon ex                     | Prismatic Evolutions      | Psychic      | Double Rare
 Sylveon ex                     | Prismatic Evolutions      | Psychic      | Special Illustration Rare
 Tapu Koko                      | Ascended Heroes           | Lightning    | Rare
 Tauros                         | 151                       | Colorless    | Common
 Teal Mask Ogerpon ex           | Prismatic Evolutions      | Grass        | Double Rare
 Team Rocket's Exeggcute        | Ascended Heroes           | Psychic      | Common
 Team Rocket's Exeggutor        | Ascended Heroes           | Psychic      | Uncommon
 Team Rocket's Mewtwo ex        | Ascended Heroes           | Psychic      | Special Illustration Rare
 Tentacool                      | 151                       | Water        | Common
 Tentacruel                     | 151                       | Water        | Uncommon
 Togekiss                       | Ascended Heroes           | Psychic      | Rare
 Togepi                         | Ascended Heroes           | Psychic      | Common
 Togetic                        | Ascended Heroes           | Psychic      | Uncommon
 Tynamo                         | Ascended Heroes           | Lightning    | Common
 Umbreon                        | 151                       | Darkness     | Uncommon
 Umbreon                        | Prismatic Evolutions      | Darkness     | Uncommon
 Umbreon ex                     | Prismatic Evolutions      | Darkness     | Special Illustration Rare
 Umbreon ex                     | Prismatic Evolutions      | Darkness     | Special Illustration Rare
 Vaporeon                       | 151                       | Water        | Uncommon
 Vaporeon                       | Prismatic Evolutions      | Water        | Uncommon
 Vaporeon ex                    | Prismatic Evolutions      | Water        | Double Rare
 Vaporeon ex                    | Prismatic Evolutions      | Water        | Special Illustration Rare
 Venomoth                       | 151                       | Grass        | Uncommon
 Venonat                        | 151                       | Grass        | Common
 Venusaur ex                    | 151                       | Grass        | Double Rare
 Vikavolt                       | Ascended Heroes           | Lightning    | Uncommon
 Vileplume                      | 151                       | Grass        | Rare
 Voltorb                        | 151                       | Lightning    | Common
 Voltorb ex                     | Ascended Heroes           | Lightning    | Double Rare
 Vulpix                         | 151                       | Fire         | Common
 Wartortle                      | 151                       | Water        | Uncommon
 Wellspring Mask Ogerpon ex     | Prismatic Evolutions      | Water        | Double Rare
 Whimsicott                     | Prismatic Evolutions      | Grass        | Uncommon
 Wigglytuff ex                  | 151                       | Colorless    | Double Rare
 Zapdos ex                      | 151                       | Lightning    | Double Rare
 Zubat                          | 151                       | Darkness     | Common
 -------------------------------------------------------------------------------------
 Total unique cards: 237
 
 ===== POK╔MON CARD INVENTORY TRACKER =====
 1. View all cards (A-Z)
 2. Add a card
 3. Remove a card
 4. Search for a card
 5. Exit
 Choose an option: 4
 Card name to search: Gengar VMAX
 
 Found:
 Gengar VMAX                    | Fusion Strike             | Darkness     | Alternate Art Secret
 
 ===== POK╔MON CARD INVENTORY TRACKER =====
 1. View all cards (A-Z)
 2. Add a card
 3. Remove a card
 4. Search for a card
 5. Exit
 Choose an option: 3
 Card name to remove: Gengar VMAX
 Removed: Gengar VMAX
 
 ===== POK╔MON CARD INVENTORY TRACKER =====
 1. View all cards (A-Z)
 2. Add a card
 3. Remove a card
 4. Search for a card
 5. Exit
 Choose an option: 1
 
 ======= POK╔MON CARD INVENTORY (A-Z) =======
 Name                           | Set                       | Energy Type  | Rarity
 -------------------------------------------------------------------------------------
 Abra                           | 151                       | Psychic      | Common
 Aerodactyl ex                  | 151                       | Colorless    | Double Rare
 Alakazam ex                    | 151                       | Psychic      | Double Rare
 Applin                         | Prismatic Evolutions      | Grass        | Common
 Arbok ex                       | 151                       | Darkness     | Double Rare
 Arcanine                       | 151                       | Fire         | Uncommon
 Arcanine ex                    | Ascended Heroes           | Fire         | Double Rare
 Articuno ex                    | 151                       | Water        | Double Rare
 Beautifly                      | Ascended Heroes           | Grass        | Uncommon
 Blastoise ex                   | 151                       | Water        | Double Rare
 Budew                          | Prismatic Evolutions      | Grass        | Common
 Bulbasaur                      | Ascended Heroes           | Grass        | Common
 Bulbasaur                      | 151                       | Grass        | Common
 Canari                         | Ascended Heroes           | Colorless    | Special Illustration Rare
 Chansey ex                     | 151                       | Colorless    | Double Rare
 Charizard ex                   | Ascended Heroes           | Fire         | Double Rare
 Charizard ex                   | 151                       | Fire         | Double Rare
 Charjabug                      | Ascended Heroes           | Lightning    | Common
 Charmander                     | 151                       | Fire         | Common
 Charmeleon                     | 151                       | Fire         | Uncommon
 Clefable                       | Ascended Heroes           | Psychic      | Uncommon
 Clefable                       | 151                       | Colorless    | Uncommon
 Clefairy                       | Ascended Heroes           | Psychic      | Common
 Clefairy                       | 151                       | Colorless    | Common
 Cloyster                       | 151                       | Water        | Uncommon
 Cottonee                       | Prismatic Evolutions      | Grass        | Common
 Cubone                         | 151                       | Fighting     | Common
 Dewgong                        | 151                       | Water        | Uncommon
 Diglett                        | 151                       | Fighting     | Common
 Dipplin                        | Prismatic Evolutions      | Grass        | Uncommon
 Ditto ex                       | 151                       | Colorless    | Double Rare
 Dodrio                         | 151                       | Colorless    | Uncommon
 Doduo                          | 151                       | Colorless    | Common
 Drowzee                        | 151                       | Psychic      | Common
 Dugtrio                        | 151                       | Fighting     | Uncommon
 Dustox                         | Ascended Heroes           | Grass        | Uncommon
 Eevee                          | 151                       | Colorless    | Common
 Eevee                          | Prismatic Evolutions      | Colorless    | Common
 Eevee ex                       | Prismatic Evolutions      | Colorless    | Double Rare
 Ekans                          | 151                       | Darkness     | Common
 Electabuzz                     | 151                       | Lightning    | Uncommon
 Electrode                      | 151                       | Lightning    | Uncommon
 Entei                          | Prismatic Evolutions      | Fire         | Rare
 Erika's Tangela                | Ascended Heroes           | Grass        | Rare
 Espeon                         | 151                       | Psychic      | Uncommon
 Espeon                         | Prismatic Evolutions      | Psychic      | Uncommon
 Espeon ex                      | Prismatic Evolutions      | Psychic      | Double Rare
 Espeon ex                      | Prismatic Evolutions      | Psychic      | Special Illustration Rare
 Ethan's Magcargo               | Ascended Heroes           | Fire         | Rare
 Exeggcute                      | Prismatic Evolutions      | Grass        | Common
 Exeggutor                      | Prismatic Evolutions      | Grass        | Uncommon
 Farfetch'd                     | 151                       | Colorless    | Uncommon
 Fearow                         | 151                       | Colorless    | Uncommon
 Fezandipiti ex                 | Ascended Heroes           | Psychic      | Ultra Rare
 Flareon                        | 151                       | Fire         | Uncommon
 Flareon                        | Prismatic Evolutions      | Fire         | Uncommon
 Flareon ex                     | Prismatic Evolutions      | Fire         | Double Rare
 Flareon ex                     | Prismatic Evolutions      | Fire         | Special Illustration Rare
 Gastly                         | 151                       | Psychic      | Common
 Gengar ex                      | 151                       | Psychic      | Double Rare
 Geodude                        | 151                       | Fighting     | Common
 Glaceon                        | Prismatic Evolutions      | Water        | Uncommon
 Glaceon ex                     | Prismatic Evolutions      | Water        | Double Rare
 Glaceon ex                     | Prismatic Evolutions      | Water        | Special Illustration Rare
 Gloom                          | 151                       | Grass        | Uncommon
 Golbat                         | 151                       | Darkness     | Uncommon
 Goldeen                        | 151                       | Water        | Common
 Goldeen                        | Prismatic Evolutions      | Water        | Common
 Golduck                        | 151                       | Water        | Uncommon
 Golem ex                       | 151                       | Fighting     | Double Rare
 Graveler                       | 151                       | Fighting     | Uncommon
 Grimer                         | 151                       | Darkness     | Common
 Growlithe                      | 151                       | Fire         | Common
 Gyarados ex                    | 151                       | Water        | Double Rare
 Haunter                        | 151                       | Psychic      | Uncommon
 Hearthflame Mask Ogerpon ex    | Prismatic Evolutions      | Fire         | Double Rare
 Hitmonchan                     | 151                       | Fighting     | Rare
 Hitmonlee                      | 151                       | Fighting     | Rare
 Hop's Pincurchin ex            | Ascended Heroes           | Lightning    | Double Rare
 Horsea                         | 151                       | Water        | Common
 Hydrapple ex                   | Prismatic Evolutions      | Grass        | Double Rare
 Hypno                          | 151                       | Psychic      | Uncommon
 Iono's Bellibolt ex            | Ascended Heroes           | Lightning    | Ultra Rare
 Iono's Kilowattrel             | Ascended Heroes           | Lightning    | Uncommon
 Iono's Tadbulb                 | Ascended Heroes           | Lightning    | Uncommon
 Iono's Wattrel                 | Ascended Heroes           | Lightning    | Common
 Ivysaur                        | Ascended Heroes           | Grass        | Uncommon
 Ivysaur                        | 151                       | Grass        | Uncommon
 Jigglypuff                     | 151                       | Colorless    | Common
 Jolteon                        | 151                       | Lightning    | Uncommon
 Jolteon                        | Prismatic Evolutions      | Lightning    | Uncommon
 Jolteon ex                     | Prismatic Evolutions      | Lightning    | Double Rare
 Jolteon ex                     | Prismatic Evolutions      | Lightning    | Special Illustration Rare
 Jynx                           | 151                       | Psychic      | Uncommon
 Kabuto                         | 151                       | Water        | Common
 Kabutops                       | 151                       | Water        | Uncommon
 Kadabra                        | 151                       | Psychic      | Uncommon
 Kangaskhan ex                  | 151                       | Colorless    | Double Rare
 Kingler                        | 151                       | Water        | Uncommon
 Krabby                         | 151                       | Water        | Common
 Lapras ex                      | 151                       | Water        | Double Rare
 Leafeon                        | Prismatic Evolutions      | Grass        | Uncommon
 Leafeon ex                     | Prismatic Evolutions      | Grass        | Double Rare
 Leafeon ex                     | Prismatic Evolutions      | Grass        | Special Illustration Rare
 Lickitung                      | 151                       | Colorless    | Common
 Lillie's Clefairy ex           | Ascended Heroes           | Psychic      | Special Illustration Rare
 Litleo                         | Prismatic Evolutions      | Fire         | Common
 Machamp ex                     | 151                       | Fighting     | Double Rare
 Machoke                        | 151                       | Fighting     | Uncommon
 Machop                         | 151                       | Fighting     | Common
 Magikarp                       | 151                       | Water        | Common
 Magmar                         | 151                       | Fire         | Common
 Magnemite                      | 151                       | Lightning    | Common
 Magneton                       | 151                       | Lightning    | Uncommon
 Mankey                         | 151                       | Fighting     | Common
 Marill                         | Ascended Heroes           | Water        | Common
 Marnie's Grimmsnarl ex         | Ascended Heroes           | Darkness     | Ultra Rare
 Marowak                        | 151                       | Fighting     | Uncommon
 Mega Blaziken ex               | Ascended Heroes           | Fire         | Ultra Rare
 Mega Charizard X ex            | Ascended Heroes           | Fire         | Mega Attack Rare
 Mega Charizard Y ex            | Ascended Heroes           | Fire         | Mega Hyper Rare
 Mega Diancie ex                | Ascended Heroes           | Fighting     | Mega Attack Rare
 Mega Dragonite ex              | Ascended Heroes           | Dragon       | Mega Hyper Rare
 Mega Eelektross ex             | Ascended Heroes           | Lightning    | Ultra Rare
 Mega Emboar ex                 | Ascended Heroes           | Grass        | Ultra Rare
 Mega Feraligatr ex             | Ascended Heroes           | Water        | Ultra Rare
 Mega Gengar ex                 | Ascended Heroes           | Darkness     | Special Illustration Rare
 Mega Gyarados ex               | Ascended Heroes           | Water        | Ultra Rare
 Mega Hawlucha ex               | Ascended Heroes           | Fighting     | Mega Attack Rare
 Mega Infernape ex              | Ascended Heroes           | Fire         | Ultra Rare
 Mega Lucario ex                | Ascended Heroes           | Metal        | Mega Attack Rare
 Mega Sceptile ex               | Ascended Heroes           | Grass        | Ultra Rare
 Mega Scrafty ex                | Ascended Heroes           | Darkness     | Ultra Rare
 Mega Swampert ex               | Ascended Heroes           | Water        | Ultra Rare
 Meowth                         | 151                       | Darkness     | Common
 Mew ex                         | 151                       | Psychic      | Double Rare
 Mewtwo ex                      | 151                       | Psychic      | Double Rare
 Miraidon ex                    | Ascended Heroes           | Lightning    | Double Rare
 Moltres ex                     | 151                       | Fire         | Double Rare
 Mr. Mime                       | 151                       | Psychic      | Uncommon
 Muk ex                         | 151                       | Darkness     | Double Rare
 N's Zoroark ex                 | Ascended Heroes           | Darkness     | Ultra Rare
 Nidoking                       | 151                       | Fighting     | Rare
 Nidoqueen                      | 151                       | Fighting     | Rare
 Nidoran F                      | 151                       | Fighting     | Common
 Nidoran M                      | 151                       | Fighting     | Common
 Nidorina                       | 151                       | Fighting     | Uncommon
 Nidorino                       | 151                       | Fighting     | Uncommon
 Ninetales ex                   | 151                       | Fire         | Double Rare
 Numel                          | Ascended Heroes           | Fire         | Common
 Oddish                         | 151                       | Grass        | Common
 Omanyte                        | 151                       | Water        | Common
 Omastar                        | 151                       | Water        | Uncommon
 Onix                           | 151                       | Fighting     | Common
 Paras                          | 151                       | Grass        | Common
 Parasect                       | 151                       | Grass        | Uncommon
 Persian                        | 151                       | Darkness     | Uncommon
 Pidgeot ex                     | 151                       | Colorless    | Double Rare
 Pidgeotto                      | 151                       | Colorless    | Uncommon
 Pidgey                         | 151                       | Colorless    | Common
 Pikachu                        | 151                       | Lightning    | Common
 Pikachu ex                     | Ascended Heroes           | Lightning    | Special Illustration Rare
 Pikachu ex                     | Prismatic Evolutions      | Lightning    | Double Rare
 Pinsir                         | Prismatic Evolutions      | Grass        | Common
 Poliwag                        | 151                       | Water        | Common
 Poliwhirl                      | 151                       | Water        | Uncommon
 Poliwrath                      | 151                       | Water        | Rare
 Ponyta                         | 151                       | Fire         | Common
 Porygon                        | 151                       | Colorless    | Common
 Primeape                       | 151                       | Fighting     | Uncommon
 Psyduck                        | 151                       | Water        | Common
 Pyroar                         | Prismatic Evolutions      | Fire         | Uncommon
 Raichu                         | 151                       | Lightning    | Rare
 Raikou                         | Prismatic Evolutions      | Lightning    | Rare
 Rapidash                       | 151                       | Fire         | Uncommon
 Raticate                       | 151                       | Colorless    | Uncommon
 Rattata                        | 151                       | Colorless    | Common
 Rhydon                         | 151                       | Fighting     | Uncommon
 Rhyhorn                        | 151                       | Fighting     | Common
 Sandshrew                      | 151                       | Fighting     | Common
 Sandslash                      | 151                       | Fighting     | Uncommon
 Seadra                         | 151                       | Water        | Uncommon
 Seaking                        | 151                       | Water        | Uncommon
 Seaking                        | Prismatic Evolutions      | Water        | Uncommon
 Seel                           | 151                       | Water        | Common
 Shellder                       | 151                       | Water        | Common
 Slowbro                        | 151                       | Psychic      | Uncommon
 Slowking                       | Prismatic Evolutions      | Water        | Uncommon
 Slowpoke                       | 151                       | Psychic      | Common
 Slowpoke                       | Prismatic Evolutions      | Water        | Common
 Slugma                         | Ascended Heroes           | Fire         | Common
 Snorlax                        | 151                       | Colorless    | Uncommon
 Spearow                        | 151                       | Colorless    | Common
 Squirtle                       | 151                       | Water        | Common
 Starmie                        | 151                       | Water        | Rare
 Staryu                         | 151                       | Water        | Common
 Steven's Metagross ex          | Ascended Heroes           | Metal        | Ultra Rare
 Stunfisk                       | Ascended Heroes           | Lightning    | Common
 Suicune                        | Prismatic Evolutions      | Water        | Rare
 Sylveon                        | Prismatic Evolutions      | Psychic      | Uncommon
 Sylveon ex                     | Prismatic Evolutions      | Psychic      | Double Rare
 Sylveon ex                     | Prismatic Evolutions      | Psychic      | Special Illustration Rare
 Tapu Koko                      | Ascended Heroes           | Lightning    | Rare
 Tauros                         | 151                       | Colorless    | Common
 Teal Mask Ogerpon ex           | Prismatic Evolutions      | Grass        | Double Rare
 Team Rocket's Exeggcute        | Ascended Heroes           | Psychic      | Common
 Team Rocket's Exeggutor        | Ascended Heroes           | Psychic      | Uncommon
 Team Rocket's Mewtwo ex        | Ascended Heroes           | Psychic      | Special Illustration Rare
 Tentacool                      | 151                       | Water        | Common
 Tentacruel                     | 151                       | Water        | Uncommon
 Togekiss                       | Ascended Heroes           | Psychic      | Rare
 Togepi                         | Ascended Heroes           | Psychic      | Common
 Togetic                        | Ascended Heroes           | Psychic      | Uncommon
 Tynamo                         | Ascended Heroes           | Lightning    | Common
 Umbreon                        | 151                       | Darkness     | Uncommon
 Umbreon                        | Prismatic Evolutions      | Darkness     | Uncommon
 Umbreon ex                     | Prismatic Evolutions      | Darkness     | Special Illustration Rare
 Umbreon ex                     | Prismatic Evolutions      | Darkness     | Special Illustration Rare
 Vaporeon                       | 151                       | Water        | Uncommon
 Vaporeon                       | Prismatic Evolutions      | Water        | Uncommon
 Vaporeon ex                    | Prismatic Evolutions      | Water        | Double Rare
 Vaporeon ex                    | Prismatic Evolutions      | Water        | Special Illustration Rare
 Venomoth                       | 151                       | Grass        | Uncommon
 Venonat                        | 151                       | Grass        | Common
 Venusaur ex                    | 151                       | Grass        | Double Rare
 Vikavolt                       | Ascended Heroes           | Lightning    | Uncommon
 Vileplume                      | 151                       | Grass        | Rare
 Voltorb                        | 151                       | Lightning    | Common
 Voltorb ex                     | Ascended Heroes           | Lightning    | Double Rare
 Vulpix                         | 151                       | Fire         | Common
 Wartortle                      | 151                       | Water        | Uncommon
 Wellspring Mask Ogerpon ex     | Prismatic Evolutions      | Water        | Double Rare
 Whimsicott                     | Prismatic Evolutions      | Grass        | Uncommon
 Wigglytuff ex                  | 151                       | Colorless    | Double Rare
 Zapdos ex                      | 151                       | Lightning    | Double Rare
 Zubat                          | 151                       | Darkness     | Common
 -------------------------------------------------------------------------------------
 Total unique cards: 236
 
 ===== POK╔MON CARD INVENTORY TRACKER =====
 1. View all cards (A-Z)
 2. Add a card
 3. Remove a card
 4. Search for a card
 5. Exit
 Choose an option: 5
 Goodbye!
 
  ----jGRASP: Operation complete.
 
*/