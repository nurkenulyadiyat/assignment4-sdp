import java.util.HashMap;
import java.util.Map;

public class OrderFacade {
    private final InventoryService inventory;
    private final PaymentProcessor payments;
    private final ShippingService shipping;
    private final NotificationService notifications;
    private final Map<String, Order> orders = new HashMap<>();
    private int nextOrderNumber = 1;

    public record Result(boolean success, String message, String orderId) { }

    private record Order(String product, int quantity, String email,
                         String paymentId, String trackingId) { }

    public OrderFacade() {
        this(new InventoryService(), new PaymentProcessor(),
                new ShippingService(), new NotificationService());
    }

    OrderFacade(InventoryService inventory, PaymentProcessor payments,
                ShippingService shipping, NotificationService notifications) {
        this.inventory = inventory;
        this.payments = payments;
        this.shipping = shipping;
        this.notifications = notifications;
    }

    public Result placeOrder(String product, int quantity, String email,
                             String paymentToken, String address) {
        boolean stockReserved = false;
        String paymentId = null;
        String trackingId = null;

        try {
            int total = inventory.reserve(product, quantity);
            stockReserved = true;

            paymentId = payments.charge(total, paymentToken);
            trackingId = shipping.schedule(address);

            String orderId = "ORD-" + nextOrderNumber++;
            notifications.send(email, "Order " + orderId + " confirmed.");
            orders.put(orderId, new Order(product, quantity, email, paymentId, trackingId));

            return new Result(true,
                    "Order " + orderId + " placed. Total: $" + total
                            + ", tracking: " + trackingId, orderId);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Undo completed steps in reverse order.
            if (trackingId != null) {
                shipping.cancel(trackingId);
            }
            if (paymentId != null) {
                payments.refund(paymentId);
            }
            if (stockReserved) {
                inventory.release(product, quantity);
            }
            return new Result(false, "Order failed: " + e.getMessage(), null);
        }
    }

    public Result cancelOrder(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            return new Result(false, "Order not found: " + orderId, null);
        }

        shipping.cancel(order.trackingId());
        payments.refund(order.paymentId());
        inventory.release(order.product(), order.quantity());
        orders.remove(orderId);
        notifications.send(order.email(), "Order " + orderId + " cancelled.");

        return new Result(true, "Order " + orderId + " cancelled and refunded.", orderId);
    }
}
