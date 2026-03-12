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
class SuiteRoom  extends Room { public SuiteRoom()  { super(3, 750, 5000.0); } }

class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}


class RoomInventory {
    private Map<String, Integer> roomAvailability = new HashMap<>();
    public RoomInventory() {
        roomAvailability.put("Single", 5);
        roomAvailability.put("Double", 3);
        roomAvailability.put("Suite", 2);
    }
    public Map<String, Integer> getRoomAvailability() { return roomAvailability; }
    public void updateAvailability(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }
}


class CancellationService {

    private Stack<String> releasedRoomIds;


    private Map<String, String> reservationRoomTypeMap;

    public CancellationService() {
        releasedRoomIds = new Stack<>();
        reservationRoomTypeMap = new HashMap<>();
    }

    public void registerBooking(String reservationId, String roomType) {
        reservationRoomTypeMap.put(reservationId, roomType);
    }

    public void cancelBooking(String reservationId, RoomInventory inventory) {
        if (!reservationRoomTypeMap.containsKey(reservationId)) {
            System.out.println("Error: Reservation ID " + reservationId + " not found.");
            return;
        }

        String roomType = reservationRoomTypeMap.get(reservationId);

        int currentCount = inventory.getRoomAvailability().get(roomType);
        inventory.updateAvailability(roomType, currentCount + 1);

        releasedRoomIds.push(reservationId);
        reservationRoomTypeMap.remove(reservationId);

        System.out.println("SUCCESS: Reservation " + reservationId + " canceled. Inventory restored.");
    }


    public void showRollbackHistory() {
        System.out.println("\n--- Cancellation Rollback History (Stack: LIFO) ---");
        if (releasedRoomIds.isEmpty()) {
            System.out.println("No recent cancellations.");
        } else {
            // Making a copy to display without destroying the stack
            Stack<String> tempStack = (Stack<String>) releasedRoomIds.clone();
            while (!tempStack.isEmpty()) {
                System.out.println("Released ID: " + tempStack.pop());
            }
        }
    }
}
public class HotelBookingApp {
    public static void main(String[] args) {
        System.out.println("Hotel Booking Cancellation & Rollback System\n");

        // Initialize Services
        RoomInventory inventory = new RoomInventory();
        CancellationService cancellationService = new CancellationService();

        System.out.println("--- Registering Confirmed Bookings ---");
        cancellationService.registerBooking("Single-101", "Single");
        cancellationService.registerBooking("Double-202", "Double");
        cancellationService.registerBooking("Suite-303", "Suite");

        inventory.updateAvailability("Single", 4);
        inventory.updateAvailability("Double", 2);
        inventory.updateAvailability("Suite", 1);
        System.out.println("Inventory updated for 3 active bookings.\n");

        cancellationService.cancelBooking("Single-101", inventory);
        cancellationService.cancelBooking("Suite-303", inventory);


        cancellationService.showRollbackHistory();

        System.out.println("\n--- Final Inventory Check ---");
        inventory.getRoomAvailability().forEach((type, count) -> {
            System.out.println(type + ": " + count + " available");
        });
    }
}

