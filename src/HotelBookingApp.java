abstract class Room {
    protected int numberOfBeds;
    protected int squareFeet;
    protected double pricePerNight;

    public Room(int numberOfBeds, int squareFeet, double pricePerNight) {
        this.numberOfBeds = numberOfBeds;
        this.squareFeet = squareFeet;
        this.pricePerNight = pricePerNight;
    }

    /
    public void displayRoomDetails() {
        System.out.println("Beds: " + numberOfBeds);
        System.out.println("Size: " + squareFeet + " sqft");
        System.out.println("Price: " + pricePerNight);
    }
}



class SingleRoom extends Room {
    public SingleRoom() {
        super(1, 250, 1500.0);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super(2, 450, 2500.0);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super(3, 750, 5000.0);
    }
}
public class HotelBookingApp {
    public static void main(String[] args) {
        System.out.println("Hotel Room Initialization\n");

        // Availability tracking using simple variables (Use Case 2 limitations)
        int singleRoomAvailability = 10;
        int doubleRoomAvailability = 5;
        int suiteRoomAvailability = 2;

        // 1. Single Room Initialization
        SingleRoom single = new SingleRoom();
        System.out.println("Single Room:");
        single.displayRoomDetails();
        System.out.println("Available: " + singleRoomAvailability + "\n");

        // 2. Double Room Initialization
        DoubleRoom dbl = new DoubleRoom();
        System.out.println("Double Room:");
        dbl.displayRoomDetails();
        System.out.println("Available: " + doubleRoomAvailability + "\n");

        // 3. Suite Room Initialization
        SuiteRoom suite = new SuiteRoom();
        System.out.println("Suite Room:");
        suite.displayRoomDetails();
        System.out.println("Available: " + suiteRoomAvailability + "\n");
    }
}