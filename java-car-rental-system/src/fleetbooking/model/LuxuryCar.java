package fleetbooking.model;

public class LuxuryCar extends Vehicle {
    
    public LuxuryCar(String carID, String model, double dailyRate, int seatingCapacity, String transmission) {
        super(carID, model, dailyRate, seatingCapacity, transmission);
        setVehicleType("Luxury");
        setEngineType("V8 / V12 Bi-Turbo");
        setEngineCapacity(4.0);
        setFuelType("Premium Petrol");
        setBrand("Mercedes-Benz/BMW/Audi/Lexus");
    }

    // Overrided Getters
    @Override // Highest insurance
    public double getInsuranceRate(){ 
        return 0.15; 
    } 

    @Override // Highest deposit
    public double getDepositRate(){ 
        return 0.30; 
    } 

    @Override // Requires thorough inspection
    public boolean requiresInspection(){ 
        return true; 
    } 

    @Override // Highest cleaning fee
    public double getCleaningFee(){ 
        return 50.00; 
    } 

    @Override
    public String getEngineSpecification() {
        return "4.0L V8 Bi-Turbo / 6.0L V12";
    }
    
    @Override
    public String getSpecialFeatures() {
        return "Leather Seats, Premium Sound System, Heated/Cooled Seats, Sunroof, Ambient Lighting, Advanced Safety Features";
    }
    
    @Override // km per liter - least fuel efficient
    public double getFuelEfficiency() {
        return 8.5; 
    }
    
    @Override // Luxury cars have AWD
    public boolean hasFourWheelDrive() {
        return true; 
    }
    
    @Override
    public String getTargetAudience() {
        return "Premium customers, business executives, special occasions, luxury experience seekers";
    }
}