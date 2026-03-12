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

// --- 2. SHARED RESOURCES (Thread-Safe Targets) ---

class RoomInventory {
    private Map<String, Integer> roomAvailability = new HashMap<>();
    public RoomInventory() {
        roomAvailability.put("Single", 5); // Limited stock to test concurrency
    }
    public Map<String, Integer> getRoomAvailability() { return roomAvailability; }
    public void updateAvailability(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }
}

class BookingRequestQueue {
    private Queue<Reservation> requestQueue = new LinkedList<>();
    public void addRequest(Reservation res) { requestQueue.offer(res); }
    public Reservation getNextRequest() { return requestQueue.poll(); }
    public boolean hasPendingRequests() { return !requestQueue.isEmpty(); }
}

class RoomAllocationService {
    public void allocateRoom(Reservation res, RoomInventory inventory) {
        int current = inventory.getRoomAvailability().get(res.getRoomType());
        if (current > 0) {
            inventory.updateAvailability(res.getRoomType(), current - 1);
            System.out.println(Thread.currentThread().getName() + " SUCCESS: Allocated " +
                    res.getRoomType() + " to " + res.getGuestName());
        } else {
            System.out.println(Thread.currentThread().getName() + " FAILED: No rooms left for " + res.getGuestName());
        }
    }
}

// --- 3. CONCURRENT PROCESSOR ---


class ConcurrentBookingProcessor implements Runnable {
    private BookingRequestQueue bookingQueue;
    private RoomInventory inventory;
    private RoomAllocationService allocationService;

    public ConcurrentBookingProcessor(BookingRequestQueue queue, RoomInventory inv, RoomAllocationService service) {
        this.bookingQueue = queue;
        this.inventory = inv;
        this.allocationService = service;
    }

    @Override
    public void run() {
        while (true) {
            Reservation reservation = null;

            // Synchronize on the queue to ensure only one thread pulls a request
            synchronized (bookingQueue) {
                if (bookingQueue.hasPendingRequests()) {
                    reservation = bookingQueue.getNextRequest();
                } else {
                    break; // No more requests, exit thread
                }
            }

            if (reservation != null) {
                // Synchronize on inventory to prevent overbooking (Race Condition)
                synchronized (inventory) {
                    allocationService.allocateRoom(reservation, inventory);
                }
            }

            // Small sleep to simulate network delay and show thread switching
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
    }
}
public class HotelBookingApp {
    public static void main(String[] args) {
        System.out.println("Concurrent Booking Simulation Started\n");

        // 1. Initialize Shared Resources
        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        RoomAllocationService allocationService = new RoomAllocationService();

        // 2. Pre-load the queue with 8 requests (only 5 rooms available)
        for (int i = 1; i <= 8; i++) {
            bookingQueue.addRequest(new Reservation("Guest-" + i, "Single"));
        }

        // 3. Create and start concurrent threads
        Thread t1 = new Thread(new ConcurrentBookingProcessor(bookingQueue, inventory, allocationService), "Thread-1");
        Thread t2 = new Thread(new ConcurrentBookingProcessor(bookingQueue, inventory, allocationService), "Thread-2");

        t1.start();
        t2.start();

        // 4. Wait for processing to complete
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread execution interrupted.");
        }

        System.out.println("\nSimulation Finished.");
        System.out.println("Final Single Room Inventory: " + inventory.getRoomAvailability().get("Single"));
    }
}

