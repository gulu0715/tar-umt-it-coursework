# Java Car Rental System

A console-based car rental management system developed for an Object-Oriented Programming coursework project. The codebase is divided into four functional modules, with shared UI, base-model and utility layers.

## Functional modules

### 1. User & Account Management
- User, customer and administrator models
- Login and customer registration flows
- Customer profile repository and account-related operations

### 2. Fleet & Booking Management
- Vehicle models, including Economy, SUV and Luxury vehicle types
- Booking records and booking lifecycle operations
- Optional add-ons and related services

### 3. Operations & Billing
- Vehicle inspection records and rentability evaluation
- Charge-item calculation and invoice generation
- Invoice records and CSV data export

### 4. Reporting & Analytics
- Revenue reports
- Vehicle usage reports
- Customer spending reports
- Report history and CSV export

## My contribution

This was a team coursework project with responsibilities allocated by module. **Fleet & Booking Management was my assigned module.** Beyond that assigned module, I also implemented substantial portions of the code in **User & Account Management, Operations & Billing, and Reporting & Analytics**. I am familiar with the implementation and interactions across all four modules.

The shared `ui/`, `common/` and `util/` packages support the four functional modules.

## Technologies and concepts

- Java
- Object-oriented programming (classes, inheritance, abstraction and polymorphism)
- Package-based modular organisation
- Console-based user interaction
- Local file handling and CSV export
- Date and time handling with `java.time`

## Requirements

- JDK 8 or later

## Compile

Run these commands from the `java-car-rental-system` folder on macOS, Linux or a shell with `find` and `javac` available:

```bash
mkdir -p out
find src -name "*.java" -print0 | xargs -0 javac -encoding UTF-8 -d out
```

## Run

```bash
java -cp out ui.Main
```

On the first run, the application creates a default administrator account and loads sample records when the data files are empty. Runtime data files are created in the current working directory and are excluded from Git. The default account is intended only for local coursework demonstration; do not reuse its credentials in a real system.

## Data and security notes

This is an academic console application. It currently uses local text files for persistence; account passwords are stored in the local data file without password hashing. Do not reuse real passwords or treat this coursework implementation as a production authentication system.

## Project structure

```text
java-car-rental-system/
├── docs/
│   └── architecture.md
├── src/
│   ├── common/
│   ├── fleetbooking/
│   ├── operationsbilling/
│   ├── reportinganalytics/
│   ├── ui/
│   ├── useraccount/
│   └── util/
└── README.md
```
