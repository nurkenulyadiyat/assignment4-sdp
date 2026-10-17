import java.util.HashMap;
import java.util.Map;

public class PaymentProcessor {
    private final Map<String, Integer> charges = new HashMap<>();
    private int nextPaymentNumber = 1;

    public String charge(int total, String paymentToken) {
        if (paymentToken == null || paymentToken.isBlank()) {
            throw new IllegalArgumentException("Payment token is required");
        }
        if (paymentToken.equalsIgnoreCase("DECLINE")) {
            throw new IllegalStateException("Payment declined");
        }

        String paymentId = "PAY-" + nextPaymentNumber++;
        charges.put(paymentId, total);
        return paymentId;
    }

    public void refund(String paymentId) {
        charges.remove(paymentId);
    }

    int activeCharges() {
        return charges.size();
    }
}
