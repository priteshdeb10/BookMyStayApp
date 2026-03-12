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


class Service {
    private String serviceName;
    private double cost;

    public Service(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() { return serviceName; }
    public double getCost() { return cost; }
}



class RoomInventory {
    private Map<String, Integer> roomAvailability = new HashMap<>();
    public RoomInventory() {
        roomAvailability.put("Single", 5);
        roomAvailability.put("Double", 3);
        roomAvailability.put("Suite", 2);
    }
    public Map<String, Integer> getRoomAvailability() { return roomAvailability; }
    public void updateAvailability(String roomType, int count) { roomAvailability.put(roomType, count); }
}

class BookingRequestQueue {
    private Queue<Reservation> requestQueue = new LinkedList<>();
    public void addRequest(Reservation res) { requestQueue.offer(res); }
    public Reservation getNextRequest() { return requestQueue.poll(); }
    public boolean hasPendingRequests() { return !requestQueue.isEmpty(); }
}

class RoomAllocationService {
    private Map<String, Integer> idCounters = new HashMap<>();

    public String allocateRoom(Reservation reservation, RoomInventory inventory) {
        String type = reservation.getRoomType();
        int currentCount = inventory.getRoomAvailability().getOrDefault(type, 0);

        if (currentCount > 0) {
            int nextId = idCounters.getOrDefault(type, 0) + 1;
            idCounters.put(type, nextId);
            inventory.updateAvailability(type, currentCount - 1);
            return type + "-" + nextId;
        }
        return null;
    }
}


class BookingHistory {
    private List<Reservation> confirmedReservations;

    public BookingHistory() {
        confirmedReservations = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        confirmedReservations.add(reservation);
    }


    public List<Reservation> getConfirmedReservations() {
        return confirmedReservations;
    }
}


class BookingReportService {

    public void generateReport(BookingHistory history) {
        System.out.println("\n--- Final Booking History Report ---");
        List<Reservation> records = history.getConfirmedReservations();

        if (records.isEmpty()) {
            System.out.println("No booking records found.");
        } else {
            for (Reservation res : records) {
                System.out.println("Guest: " + res.getGuestName() + " | Allocated: " + res.getRoomType());
            }
        }
        System.out.println("Total Confirmed Bookings: " + records.size());
    }
}
public class HotelBookingApp {
    public static void main(String[] args) {
        System.out.println("Booking History Report\n");


        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue queue = new BookingRequestQueue();
        RoomAllocationService allocator = new RoomAllocationService();
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();


        queue.addRequest(new Reservation("Abhi", "Single"));
        queue.addRequest(new Reservation("Subba", "Double"));
        queue.addRequest(new Reservation("Vanmathi", "Suite"));


        while (queue.hasPendingRequests()) {
            Reservation current = queue.getNextRequest();
            System.out.println("Processing booking for Guest: " + current.getGuestName() +
                    ", Room Type: " + current.getRoomType());

            String roomId = allocator.allocateRoom(current, inventory);

            if (roomId != null) {

                history.addReservation(current);
            }
        }

        reportService.generateReport(history);
    }
}

