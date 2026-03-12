class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}


class ReservationValidator {

    public void validate(String guestName, String roomType, RoomInventory inventory)
            throws InvalidBookingException {

        // Rule 1: Guest name must not be empty
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        // Rule 2: Room type must exist in the system
        Map<String, Integer> availability = inventory.getRoomAvailability();
        if (!availability.containsKey(roomType)) {
            throw new InvalidBookingException("Invalid room type requested: " + roomType);
        }

        // Rule 3: Check for availability early
        if (availability.get(roomType) <= 0) {
            throw new InvalidBookingException("No " + roomType + " rooms currently available.");
        }
    }
}

// --- 3. DATA MODELS & INVENTORY ---

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
}

class BookingRequestQueue {
    private Queue<Reservation> requestQueue = new LinkedList<>();
    public void addRequest(Reservation res) { requestQueue.offer(res); }
    public boolean hasPendingRequests() { return !requestQueue.isEmpty(); }
    public Reservation getNextRequest() { return requestQueue.poll(); }
}
public class HotelBookingApp {
    public static void main(String[] args) {

        System.out.println("Booking Validation");

        Scanner scanner = new Scanner(System.in);


        RoomInventory inventory = new RoomInventory();
        ReservationValidator validator = new ReservationValidator();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        try {

            System.out.print("Enter Guest Name: ");
            String guestName = scanner.nextLine();

            System.out.print("Enter Room Type (Single/Double/Suite): ");
            String roomType = scanner.nextLine();


            validator.validate(guestName, roomType, inventory);

            Reservation reservation = new Reservation(guestName, roomType);
            bookingQueue.addRequest(reservation);
            System.out.println("Success: Reservation for " + guestName + " added to queue.");

        } catch (InvalidBookingException e) {

            System.out.println("Booking Failed: " + e.getMessage());
        } catch (Exception e) {

            System.out.println("System Error: An unexpected error occurred.");
        } finally {

            scanner.close();
        }
    }
}

