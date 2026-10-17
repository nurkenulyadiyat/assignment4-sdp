import java.util.HashMap;
import java.util.Map;

public class ShippingService {
    private final Map<String, String> shipments = new HashMap<>();
    private int nextShippingNumber = 1;

    public String schedule(String address) {
        if (address == null || address.isBlank() || address.equalsIgnoreCase("INVALID")) {
            throw new IllegalArgumentException("Shipping address is unavailable");
        }

        String trackingId = "SHIP-" + nextShippingNumber++;
        shipments.put(trackingId, address);
        return trackingId;
    }

    public void cancel(String trackingId) {
        shipments.remove(trackingId);
    }

    int activeShipments() {
        return shipments.size();
    }
}
