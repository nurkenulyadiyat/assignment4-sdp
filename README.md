# Assignment 4 — Online Order Facade (Java 17)

A small console project demonstrating the **Facade design pattern**. The `Main` client uses only `OrderFacade`, while the facade coordinates inventory, payment, shipping, and notifications.

## Project structure

```text
src/
  Main.java                 Client
  OrderFacade.java          Facade and order state
  InventoryService.java     Stock reservations and release
  PaymentProcessor.java     Simulated charges and refunds
  ShippingService.java      Simulated shipment scheduling/cancellation
  NotificationService.java  Simulated email messages

tests/
  OrderFacadeTest.java      7 automated tests, no dependencies

docs/
  facade-uml.png            UML class diagram
  facade-uml.puml           UML source (PlantUML)
  facade-uml.dot            UML drawing source (Graphviz)
run.bat                     Windows demo + tests
```

## How to run

**Requirements:** JDK 17 or newer. No Maven, Gradle, external libraries, database, or internet connection.

**Windows:** open this folder in PowerShell and run:

```powershell
.\run.bat
```

**IntelliJ IDEA:** open the project folder, select JDK 17+, then run `src/Main.java`. To run the tests, run `tests/OrderFacadeTest.java` after marking `src` and `tests` as Sources Roots if needed.

**macOS/Linux (terminal):**

```bash
mkdir -p out
javac --release 17 -d out src/*.java tests/*.java
java -cp out Main
java -cp out OrderFacadeTest
```

## Facade API

```java
OrderFacade facade = new OrderFacade();
var result = facade.placeOrder("Mouse", 1, "user@example.com", "OK", "Astana");
facade.cancelOrder(result.orderId());
```

`placeOrder` performs **reserve stock → charge payment → schedule shipping → send notification**. If a step fails, completed operations are undone in reverse order (cancel shipping → refund payment → release stock). `cancelOrder` also refunds and restores stock.

For predictable demo failures, use payment token `DECLINE` or address `INVALID`.

**Simulation only:** Prices, stock, payments, shipping, and email are kept in memory. There are no actual bank charges, deliveries, or emails.

## Clean Code principles

1. Meaningful API names (`placeOrder`, `cancelOrder`, `reserve`, `refund`).
2. Single Responsibility: each service does one job, while the facade only coordinates.
3. The client does not know or instantiate subsystem objects.
4. Small classes with private state and focused methods.
5. Clear error handling and rollback after a failed step.

## Tests

The seven self-validating tests cover successful orders, failed payments, failed shipping, invalid email, cancellation (including repeated cancellation), insufficient stock, and unknown orders.

## References

- Lecture 6: The Facade Design Pattern (Almas Ospanov).
- Freeman et al., *Head First Design Patterns*, Chapter 7 (Facade).
- Assignment 4 instructions, ShP-2216.

## GitHub: meaningful incremental commits

Run these in the extracted project folder, after configuring your own Git name and email:

```bash
git init
git add src/InventoryService.java src/PaymentProcessor.java src/ShippingService.java src/NotificationService.java
git commit -m "Add order subsystem services"
git add src/OrderFacade.java src/Main.java
git commit -m "Implement facade and client demo"
git add tests/
git commit -m "Test order rollback and cancellation"
git add README.md docs/ run.bat .gitignore
git commit -m "Document design and execution"
```

Then create an **empty GitHub repository**, add it as `origin`, and push `main`. Copy the resulting repository URL into the Moodle report. Do not commit `out/` or `.class` files.
