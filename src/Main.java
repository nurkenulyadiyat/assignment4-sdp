public class Main {
    public static void main(String[] args) {
        OrderFacade orders = new OrderFacade();

        System.out.println("1) Successful order");
        OrderFacade.Result first = orders.placeOrder(
                "Mouse", 1, "alice@example.com", "OK", "Astana");
        System.out.println(first.message());

        System.out.println("\n2) Declined payment (stock is released)");
        System.out.println(orders.placeOrder(
                "Keyboard", 5, "bob@example.com", "DECLINE", "Astana").message());

        System.out.println("\n3) Same stock is available for a new order");
        OrderFacade.Result second = orders.placeOrder(
                "Keyboard", 5, "bob@example.com", "OK", "Astana");
        System.out.println(second.message());

        System.out.println("\n4) Cancel an existing order");
        System.out.println(orders.cancelOrder(second.orderId()).message());

        System.out.println("\n5) Shipping failure (payment and stock are rolled back)");
        System.out.println(orders.placeOrder(
                "Keyboard", 5, "bob@example.com", "OK", "INVALID").message());

        System.out.println("\n6) Stock is available again after shipping failure");
        System.out.println(orders.placeOrder(
                "Keyboard", 5, "bob@example.com", "OK", "Astana").message());
    }
}
