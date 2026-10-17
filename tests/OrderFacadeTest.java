public class OrderFacadeTest {
    private static int passed = 0;

    public static void main(String[] args) {
        testSuccessfulOrder();
        testPaymentRollback();
        testShippingRollback();
        testNotificationRollback();
        testCancellation();
        testInsufficientStock();
        testUnknownOrder();
        System.out.println("\nALL " + passed + " TESTS PASSED");
    }

    private static void check(boolean condition, String name) {
        if (!condition) {
            throw new AssertionError("FAILED: " + name);
        }
    }

    private static void done(String name) {
        passed++;
        System.out.println("PASS: " + name);
    }

    private static void testSuccessfulOrder() {
        OrderFacade facade = new OrderFacade();
        var result = facade.placeOrder("Mouse", 1, "a@example.com", "OK", "Astana");
        check(result.success() && result.orderId() != null, "success result");
        done("successful order");
    }

    private static void testPaymentRollback() {
        InventoryService stock = new InventoryService();
        PaymentProcessor pay = new PaymentProcessor();
        OrderFacade facade = new OrderFacade(stock, pay,
                new ShippingService(), new NotificationService());
        var failed = facade.placeOrder("Keyboard", 5, "a@example.com", "DECLINE", "Astana");
        check(!failed.success(), "payment must decline");
        check(stock.available("Keyboard") == 5, "stock must be released");
        check(pay.activeCharges() == 0, "no payment should remain");
        done("payment rollback");
    }

    private static void testShippingRollback() {
        InventoryService stock = new InventoryService();
        PaymentProcessor pay = new PaymentProcessor();
        ShippingService ship = new ShippingService();
        OrderFacade facade = new OrderFacade(stock, pay, ship, new NotificationService());
        var failed = facade.placeOrder("Keyboard", 5, "a@example.com", "OK", "INVALID");
        check(!failed.success(), "shipping must fail");
        check(stock.available("Keyboard") == 5, "stock must be released");
        check(pay.activeCharges() == 0, "payment must be refunded");
        check(ship.activeShipments() == 0, "shipment must not remain");
        done("shipping rollback");
    }

    private static void testNotificationRollback() {
        InventoryService stock = new InventoryService();
        PaymentProcessor pay = new PaymentProcessor();
        ShippingService ship = new ShippingService();
        NotificationService mail = new NotificationService();
        OrderFacade facade = new OrderFacade(stock, pay, ship, mail);
        var failed = facade.placeOrder("Keyboard", 5, "invalid", "OK", "Astana");
        check(!failed.success(), "invalid email must fail");
        check(stock.available("Keyboard") == 5, "stock must be released");
        check(pay.activeCharges() == 0, "payment must be refunded");
        check(ship.activeShipments() == 0, "shipment must be cancelled");
        check(mail.sentCount() == 0, "notification must not be stored");
        done("notification rollback");
    }

    private static void testCancellation() {
        InventoryService stock = new InventoryService();
        PaymentProcessor pay = new PaymentProcessor();
        ShippingService ship = new ShippingService();
        OrderFacade facade = new OrderFacade(stock, pay, ship, new NotificationService());
        var created = facade.placeOrder("Mouse", 2, "a@example.com", "OK", "Astana");
        check(created.success() && stock.available("Mouse") == 0, "stock reserved");
        var cancelled = facade.cancelOrder(created.orderId());
        check(cancelled.success(), "cancel succeeds");
        check(stock.available("Mouse") == 2, "stock restored");
        check(pay.activeCharges() == 0, "payment refunded");
        check(ship.activeShipments() == 0, "shipping cancelled");
        check(!facade.cancelOrder(created.orderId()).success(), "cannot cancel twice");
        done("cancellation and double-cancel protection");
    }

    private static void testInsufficientStock() {
        OrderFacade facade = new OrderFacade();
        var failed = facade.placeOrder("Mouse", 3, "a@example.com", "OK", "Astana");
        check(!failed.success() && failed.message().contains("Not enough stock"),
                "stock check");
        done("insufficient stock");
    }

    private static void testUnknownOrder() {
        OrderFacade facade = new OrderFacade();
        check(!facade.cancelOrder("ORD-999").success(), "unknown order rejected");
        done("unknown order");
    }
}
