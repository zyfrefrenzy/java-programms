package carrentalsystem.java;

import java.time.LocalDate;
import java.util.*;

// Abstract Car class (Abstraction, Encapsulation)
abstract class Car {
    private String registrationNumber;
    private String model;
    private boolean available;
    private double rentalPricePerDay;

    public Car(String registrationNumber, String model, double rentalPricePerDay) {
        this.registrationNumber = registrationNumber;
        this.model = model;
        this.available = true;
        this.rentalPricePerDay = rentalPricePerDay;
    }

    public String getRegistrationNumber() { return registrationNumber; }
    public String getModel() { return model; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public double getRentalPricePerDay() { return rentalPricePerDay; }

    // Polymorphic method
    public double calculateRentalCost(int days) {
        return days * rentalPricePerDay;
    }

    @Override
    public String toString() {
        return model + " [" + registrationNumber + "] - " + (available ? "Available" : "Rented");
    }
}

// Inheritance & Polymorphism
class LuxuryCar extends Car {
    public LuxuryCar(String registrationNumber, String model, double rentalPricePerDay) {
        super(registrationNumber, model, rentalPricePerDay);
    }

    @Override
    public double calculateRentalCost(int days) {
        return super.calculateRentalCost(days) * 1.2;
    }
}

// Customer class (Encapsulation)
class Customer {
    private String customerId;
    private String name;
    private String contactInfo;
    private List<RentalTransaction> rentalHistory;

    public Customer(String customerId, String name, String contactInfo) {
        this.customerId = customerId;
        this.name = name;
        this.contactInfo = contactInfo;
        this.rentalHistory = new ArrayList<>();
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getContactInfo() { return contactInfo; }

    public List<RentalTransaction> getRentalHistory() { return rentalHistory; }
    public void addTransaction(RentalTransaction transaction) {
        rentalHistory.add(transaction);
    }

    @Override
    public String toString() {
        return name + " (" + customerId + ")";
    }
}

// Rental Transaction (Encapsulation)
class RentalTransaction {
    private Car car;
    private Customer customer;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private boolean active;
    private double totalCost;

    public RentalTransaction(Car car, Customer customer, LocalDate rentalDate, int rentalDays) {
        this.car = car;
        this.customer = customer;
        this.rentalDate = rentalDate;
        this.returnDate = null;
        this.active = true;
        this.totalCost = car.calculateRentalCost(rentalDays);
    }

    public void closeTransaction(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.active = false;
        car.setAvailable(true);
    }

    public Car getCar() { return car; }
    public Customer getCustomer() { return customer; }
    public LocalDate getRentalDate() { return rentalDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isActive() { return active; }
    public double getTotalCost() { return totalCost; }

    @Override
    public String toString() {
        return "Rental: " + car + " by " + customer + " from " + rentalDate +
               (returnDate != null ? " to " + returnDate : "") +
               " | Cost: $" + String.format("%.2f", totalCost) +
               " | " + (active ? "Active" : "Closed");
    }
}

// Rental Agency (System Manager)
class RentalAgency {
    private Map<String, Car> cars;
    private Map<String, Customer> customers;
    private List<RentalTransaction> transactions;

    public RentalAgency() {
        this.cars = new HashMap<>();
        this.customers = new HashMap<>();
        this.transactions = new ArrayList<>();
    }

    // Car management
    public void addCar(Car car) { cars.put(car.getRegistrationNumber(), car); }
    public Car findCar(String regNo) { return cars.get(regNo); }
    public List<Car> getAvailableCars() {
        List<Car> available = new ArrayList<>();
        for (Car car : cars.values()) if (car.isAvailable()) available.add(car);
        return available;
    }

    // Customer management
    public void addCustomer(Customer customer) { customers.put(customer.getCustomerId(), customer); }
    public Customer findCustomer(String customerId) { return customers.get(customerId); }

    // Rental operations
    public RentalTransaction rentCar(String regNo, String customerId, int days) {
        Car car = findCar(regNo);
        Customer customer = findCustomer(customerId);
        if (car == null || !car.isAvailable() || customer == null) return null;

        car.setAvailable(false);
        RentalTransaction t = new RentalTransaction(car, customer, LocalDate.now(), days);
        transactions.add(t);
        customer.addTransaction(t);
        return t;
    }

    public boolean returnCar(String regNo) {
        for (RentalTransaction t : transactions) {
            if (t.getCar().getRegistrationNumber().equals(regNo) && t.isActive()) {
                t.closeTransaction(LocalDate.now());
                return true;
            }
        }
        return false;
    }

    public List<RentalTransaction> getTransactions() { return transactions; }
}



public class CarRentalSystemJava {

   
    public static void main(String[] args) {
        RentalAgency agency = new RentalAgency();

        // Add cars
        agency.addCar(new Car("KBC123", "Toyota Corolla", 40) {});
        agency.addCar(new LuxuryCar("KBP789", "BMW 5 Series", 100));
        agency.addCar(new Car("LMN456", "Honda Civic", 50) {});

        // Add customers
        agency.addCustomer(new Customer("C001", "Alice", "alice@email.com"));
        agency.addCustomer(new Customer("C002", "Bob", "bob@email.com"));

        // List available cars
        System.out.println("Available cars:");
        for (Car car : agency.getAvailableCars())
            System.out.println(car);

        // Rent car for Alice
        RentalTransaction tx1 = agency.rentCar("KBC123", "C001", 5);
        assert tx1 != null && !tx1.getCar().isAvailable();
        System.out.println("\nAlice rents Toyota Corolla for 5 days: " + tx1);

        // Try to rent same car again for Bob (should fail)
        RentalTransaction txFail = agency.rentCar("KBC123", "C002", 2);
        assert txFail == null;
        System.out.println("Bob tries to rent the same car: " + (txFail == null ? "Failed (Already Rented)" : "Success"));

        // Bob rents luxury car
        RentalTransaction tx2 = agency.rentCar("KBP789", "C002", 3);
        assert tx2 != null && !tx2.getCar().isAvailable();
        System.out.println("\nBob rents BMW 5 Series for 3 days: " + tx2);

        // Return a car (Alice returns)
        boolean returned = agency.returnCar("KBC123");
        assert returned && agency.findCar("KBC123").isAvailable();
        System.out.println("\nAlice returns the car: " + (returned ? "Success" : "Failed"));

        // Print all transactions
        System.out.println("\nTransactions:");
        for (RentalTransaction t : agency.getTransactions())
            System.out.println(t);

        // Validate rental history
        assert agency.findCustomer("C001").getRentalHistory().size() == 1;
        assert agency.findCustomer("C002").getRentalHistory().size() == 1;

        System.out.println("\nAll tests passed.");
    }
}