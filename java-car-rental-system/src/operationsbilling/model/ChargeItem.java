package operationsbilling.model;

public class ChargeItem {
    private String chargeId;
    private String type;       
    private String description;
    private double amount;
    private String category;    
    
    public ChargeItem(String type, String description, double amount) {
        this.chargeId = generateChargeId();
        this.type = type;
        this.description = description;
        this.amount = amount;
        this.category = determineCategory(type);
    }
    
    private String generateChargeId() {
        return "CHG" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
    
    private String determineCategory(String type) {
        switch (type.toLowerCase()) {
            case "base_rental": 
                return "Rental";
            case "add_on": 
                return "AddOn";
            case "insurance": 
                return "Insurance";
            case "late_fee": 
                return "Penalty";
            case "damage_penalty": 
                return "Penalty";
            case "fuel_penalty": 
                return "Penalty";
            default: 
                return "Other";
        }
    }
    
    // Getters
    public String getChargeId() { return chargeId; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    
    // Setters
    public void setDescription(String description) { 
        this.description = description; 
    }

    public void setAmount(double amount) { 
        this.amount = amount; 
    }
    
    // Static methods for different charge types
    public static ChargeItem createBaseRental(double amount) {
        return new ChargeItem("BASE_RENTAL", "Base Rental Charge", amount);
    }
    
    public static ChargeItem createAddOnFee(double amount) {
        return new ChargeItem("ADD_ON", "Add-On Fee", amount);
    }
    
    public static ChargeItem createInsuranceCharge(double amount) {
        return new ChargeItem("INSURANCE", "Insurance Charge", amount);
    }
    
    public static ChargeItem createLateFee(double amount) {
        return new ChargeItem("LATE_FEE", "Late Return Fee", amount);
    }
    
    public static ChargeItem createDamagePenalty(double amount) {
        return new ChargeItem("DAMAGE_PENALTY", "Damage Penalty", amount);
    }
    
    public static ChargeItem createFuelPenalty(double amount) {
        return new ChargeItem("FUEL_PENALTY", "Fuel Penalty", amount);
    }
    
    @Override
    public String toString() {
        return String.format("%-12s %-20s %-15s RM%-10.2f", 
                chargeId, type, description, amount);
    }
    
    public String toDetailedString() {
        return String.format("  • %-20s : RM %10.2f (%s)", description, amount, category);
    }
}