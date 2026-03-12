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

class AddOnServiceManager {
    private Map<String, List<Service>> servicesByReservation;

    public AddOnServiceManager() {
        servicesByReservation = new HashMap<>();
    }

    public void addService(String reservationId, Service service) {
        servicesByReservation.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
    }


    public double calculateTotalServiceCost(String reservationId) {
        List<Service> services = servicesByReservation.getOrDefault(reservationId, new ArrayList<>());
        double total = 0;
        for (Service s : services) {
            total += s.getCost();
        }
        return total;
    }
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

class RoomAllocationService {

    public String allocateRoom(Reservation res, RoomInventory inventory) {
        return res.getRoomType() + "-1";
    }
}
public class HotelBookingApp {
    public static void main(String[] args) {
        System.out.println("Add-On Service Selection");

s
        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocationService = new RoomAllocationService();
        AddOnServiceManager addonManager = new AddOnServiceManager();

        Reservation res = new Reservation("Abhi", "Single");
        String reservationId = allocationService.allocateRoom(res, inventory);


        Service breakfast = new Service("Breakfast", 500.0);
        Service spa = new Service("Spa", 1000.0);

        addonManager.addService(reservationId, breakfast);
        addonManager.addService(reservationId, spa);

        System.out.println("Reservation ID: " + reservationId);
        System.out.println("Total Add-On Cost: " + addonManager.calculateTotalServiceCost(reservationId));
    }
}

