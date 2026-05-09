package shoppingcart.cli;

import java.math.BigDecimal;
import java.util.Scanner;
import shoppingcart.model.ShippingOption;
import shoppingcart.model.StateTax;
import shoppingcart.service.CartService;

public class ShoppingCartApp {
    private static Scanner scanner = new Scanner(System.in);
    private static CartService cartService = new CartService();
    private static String customerName; 
    private static StateTax customerState;
    private static ShippingOption shippingOption;

    public static void main(String[] args) {
        System.out.println("Welcome to the Shopping Cart Application!");

        // Collect initial customer info
        collectCustomerInfo();

        // Main menu loop
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1:
                    addItem();
                    break;
                case 2:
                    System.out.println(cartService.getCartContents());
                    break;
                case 3:
                    editQuantity();
                    break;
                case 4:
                    removeItem();
                    break;
                case 5:
                    getCurrentTotal();
                    break;
                case 6:
                    checkout();
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
    }

    private static void collectCustomerInfo() {
        System.out.print("Enter your name: ");
        customerName = scanner.nextLine();

        System.out.print("Enter your state (e.g., IL, CA, NY): ");
        String stateInput;
        do {
            stateInput = scanner.nextLine();
            if (!StateTax.isValidState(stateInput)) {
                System.out.print("Invalid state abbreviation. Please enter a valid US state abbreviation: ");
            }
        } while (!StateTax.isValidState(stateInput));
        customerState = StateTax.fromString(stateInput);

        System.out.print("Choose shipping option (STANDARD/NEXT_DAY): ");
        String shippingInput = scanner.nextLine().toUpperCase();
        try {
            shippingOption = ShippingOption.valueOf(shippingInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid shipping option. Defaulting to STANDARD.");
            shippingOption = ShippingOption.STANDARD;
        }
    }

    private static void displayMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Add item to cart");
        System.out.println("2. See contents of shopping cart"); 
        System.out.println("3. Edit quantity of items in cart");
        System.out.println("4. Remove items from cart");
        System.out.println("5. Get current total");
        System.out.println("6. Checkout");
    }

    private static void addItem() {
        System.out.print("Enter item name: ");
        String name = scanner.nextLine();

        int quantity = getIntInput("Enter quantity: ");
        if (quantity < 1) {
            System.out.println("Error: Quantity must be at least 1.");
            return;
        }

        System.out.print("Enter item price: ");
        BigDecimal price = BigDecimal.valueOf(getDoubleInput(""));
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Error: Price must be positive.");
            return;
        }

        if (cartService.addItem(name, quantity, price)) {
            System.out.println("Item added. Current cart has " + cartService.getItemCount() + " items.");
        } else {
            System.out.println("Error adding item.");
        }
    }

    private static void editQuantity() {
        System.out.print("Enter item name to edit: ");
        String name = scanner.nextLine();

        int newQuantity = getIntInput("Enter new quantity: ");
        if (newQuantity < 1) {
            System.out.println("Error: Quantity must be at least 1.");
            return;
        }

        if (cartService.updateQuantity(name, newQuantity)) {
            System.out.println("Quantity updated.");
        } else {
            System.out.println("Item not found.");
        }
    }

    private static void removeItem() {
        System.out.print("Enter item name to remove: ");
        String name = scanner.nextLine();

        if (cartService.removeItem(name)) {
            System.out.println("Item removed.");
        } else {
            System.out.println("Item not found.");
        }
    }

    private static BigDecimal getCurrentTotal() {
        BigDecimal total = cartService.getCurrentTotal(customerState, shippingOption);
        System.out.printf("Current total: $%.2f\n", total.doubleValue());
        return total;
    }

    private static void checkout() {
        if (getCurrentTotal().compareTo(BigDecimal.valueOf(1)) < 0) {
            System.out.println("The minimum purchase amount is $1.00. Please add items before checking out.");
            return;
        }
        else if (getCurrentTotal().compareTo(BigDecimal.valueOf(99999.99)) > 0) {
            System.out.println("Total exceeds maximum allowed. Please adjust your cart.");
            return;
        }
        System.out.println("Transaction completed.");
        cartService.checkout();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}