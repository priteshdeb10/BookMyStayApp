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

--

class RoomInventory {
    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();

        roomAvailability.put("Single", 5);
        roomAvailability.put("Double", 3);
        roomAvailability.put("Suite", 2);
    }

    public Map<String, Integer> getRoomAvailability() { return roomAvailability; }

    public void updateAvailability(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }
}

class BookingRequestQueue {
    private Queue<Reservation> requestQueue = new LinkedList<>();

    public void addRequest(Reservation reservation) { requestQueue.offer(reservation); }
    public Reservation getNextRequest() { return requestQueue.poll(); }
    public boolean hasPendingRequests() { return !requestQueue.isEmpty(); }
}

class RoomAllocationService {
    private Set<String> allocatedRoomIds = new HashSet<>();
    private Map<String, Set<String>> assignedRoomsByType = new HashMap<>();
    private Map<String, Integer> idCounters = new HashMap<>(); // Helper for sequential IDs


    public void allocateRoom(Reservation reservation, RoomInventory inventory) {
        String type = reservation.getRoomType();
        int currentCount = inventory.getRoomAvailability().getOrDefault(type, 0);

        if (currentCount > 0) {
            String roomId = generateRoomId(type);

            // Track the allocation
            allocatedRoomIds.add(roomId);
            assignedRoomsByType.computeIfAbsent(type, k -> new HashSet<>()).add(roomId);

            // Update inventory immediately
            inventory.updateAvailability(type, currentCount - 1);

            System.out.println("Booking confirmed for Guest: " + reservation.getGuestName() +
                    ", Room ID: " + roomId);
        } else {
            System.out.println("Booking failed for " + reservation.getGuestName() + ": No " + type + " available.");
        }
    }

    private String generateRoomId(String roomType) {
        int nextId = idCounters.getOrDefault(roomType, 0) + 1;
        idCounters.put(roomType, nextId);
        return roomType + "-" + nextId;
    }
}
public class HotelBookingApp {
    public static void main(String[] args) {
        System.out.println("Room Allocation Processing");

        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        RoomAllocationService allocationService = new RoomAllocationService();


        bookingQueue.addRequest(new Reservation("Abhi", "Single"));

        bookingQueue.addRequest(new Reservation("Subba", "Single"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Suite"));


        while (bookingQueue.hasPendingRequests()) {
            Reservation currentRequest = bookingQueue.getNextRequest();
            allocationService.allocateRoom(currentRequest, inventory);
        }
    }
}

