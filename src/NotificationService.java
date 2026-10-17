import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private final List<String> sentMessages = new ArrayList<>();

    public void send(String email, String text) {
        if (email == null || !email.contains("@") || email.startsWith("@") || email.endsWith("@")) {
            throw new IllegalArgumentException("Invalid customer email");
        }

        String message = email + ": " + text;
        sentMessages.add(message);
        System.out.println("[Email simulation] " + message);
    }

    int sentCount() {
        return sentMessages.size();
    }
}
