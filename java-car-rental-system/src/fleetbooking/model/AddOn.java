package fleetbooking.model;

public class AddOn {
    private String addOnId;
    private String name;
    private double pricePerDay;
    private boolean active;
    private String category;
    private String description;

    // Contructor
    public AddOn(String addOnId, String name, double pricePerDay) {
        this(addOnId, name, pricePerDay, true, "General", "");
    }

    // Overloaded Contructor
    public AddOn(String addOnId, String name, double pricePerDay, boolean active) {
        this(addOnId, name, pricePerDay, active, "General", "");
    }
    
    // Overloaded Contructor
    public AddOn(String addOnId, String name, double pricePerDay, boolean active, String category, String description) {
        this.addOnId = addOnId;
        this.name = name;
        this.pricePerDay = pricePerDay;
        this.active = active;
        this.category = (category == null || category.isEmpty()) ? "General" : category;
        this.description = (description == null) ? "" : description;
    }

    // Getters
    public String getAddOnId(){ 
        return addOnId; 
    }

    public String getName(){ 
        return name; 
    }
    public double getPricePerDay(){ 
        return pricePerDay; 
    }

    public boolean isActive(){ 
        return active; 
    }

    public String getCategory(){ 
        return category; 
    }

    public String getDescription(){ 
        return description; 
    }

    // Setters
    public void setName(String name){ 
        this.name = name; 
    }

    public void setPricePerDay(double pricePerDay){ 
        this.pricePerDay = pricePerDay; 
    }

    public void setActive(boolean active){ 
        this.active = active; 
    }

    public void setCategory(String category){ 
        this.category = category; 
    }

    public void setDescription(String description){ 
        this.description = description; 
    }

    @Override
    public String toString() {
        return String.format("%-8s %-20s RM%-10.2f %-8s %-15s", 
                addOnId, name, pricePerDay, active ? "Yes" : "No", category);
    }
    
    public String toDetailedString() {
        return String.format("%-8s %-20s %-12s %-8s %-15s %-30s", 
                addOnId, name, "RM" + pricePerDay, active ? "Yes" : "No", category, 
                description.length() > 28 ? description.substring(0, 25) + "..." : description);
    }
}