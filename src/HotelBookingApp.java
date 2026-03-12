
abstract class Room {
    protected int numberOfBeds;
    protected int squareFeet;
    protected double pricePerNight;

    public Room(int numberOfBeds, int squareFeet, double pricePerNight) {
        this.numberOfBeds = numberOfBeds;
        this.squareFeet = squareFeet;
        this.pricePerNight = pricePerNight;
    }
}

class SingleRoom extends Room { public SingleRoom() { super(1, 250, 1500.0); } }
class DoubleRoom extends Room { public DoubleRoom() { super(2, 450, 2500.0); } }

class RoomInventory {
    private Map<String, Integer> roomAvailability = new HashMap<>();

    public Map<String, Integer> getRoomAvailability() {
        return roomAvailability;
    }

    public void updateAvailability(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }
}


class FilePersistenceService {

    public void saveInventory(RoomInventory inventory, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, Integer> entry : inventory.getRoomAvailability().entrySet()) {
                writer.println(entry.getKey() + "=" + entry.getValue());
            }
            System.out.println("System State Saved to " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving inventory: " + e.getMessage());
        }
    }

    public void loadInventory(RoomInventory inventory, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("No existing data found. Starting with fresh inventory.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String roomType = parts[0];
                    int count = Integer.parseInt(parts[1]);
                    inventory.updateAvailability(roomType, count);
                }
            }
            System.out.println("System Recovery Successful: Data loaded from " + filePath);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading inventory: " + e.getMessage());
        }
    }
}

public class HotelBookingApp {
    public static void main(String[] args) {
        System.out.println("Hotel Management - Persistence & Recovery\n");

        RoomInventory inventory = new RoomInventory();
        FilePersistenceService persistenceService = new FilePersistenceService();
        String dataFile = "inventory_state.txt";

        persistenceService.loadInventory(inventory, dataFile);

        if (inventory.getRoomAvailability().isEmpty()) {
            System.out.println("Initializing default room counts...");
            inventory.updateAvailability("Single", 10);
            inventory.updateAvailability("Double", 5);
        }

        System.out.println("\n--- Current Inventory Status ---");
        inventory.getRoomAvailability().forEach((type, count) ->
                System.out.println(type + ": " + count + " available"));


        System.out.println("\nBooking 1 Single Room...");
        int singleCount = inventory.getRoomAvailability().get("Single");
        if (singleCount > 0) {
            inventory.updateAvailability("Single", singleCount - 1);
        }


        persistenceService.saveInventory(inventory, dataFile);

        System.out.println("\nProcess complete. Try running the program again to see the counts persist!");
    }
}

