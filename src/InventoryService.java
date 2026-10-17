import java.util.HashMap;
import java.util.Map;

public class InventoryService {
    private final Map<String, Integer> stock = new HashMap<>();
    private final Map<String, Integer> prices = Map.of(
            "Keyboard", 50,
            "Mouse", 20
    );

    public InventoryService() {
        stock.put("Keyboard", 5);
        stock.put("Mouse", 2);
    }

    public int reserve(String product, int quantity) {
        if (product == null || !prices.containsKey(product)) {
            throw new IllegalArgumentException("Unknown product: " + product);
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (stock.get(product) < quantity) {
            throw new IllegalStateException("Not enough stock for " + product);
        }

        stock.put(product, stock.get(product) - quantity);
        return prices.get(product) * quantity;
    }

    public void release(String product, int quantity) {
        stock.put(product, stock.get(product) + quantity);
    }

    int available(String product) {
        return stock.get(product);
    }
}
