# System Architecture

The Car Rental System is organised into four functional modules, supported by shared presentation, base-model and utility layers.

## Functional modules

| Module | Source package | Main responsibilities |
|---|---|---|
| User & Account Management | `useraccount` | User and admin models, customer profiles, authentication, registration and customer repository operations |
| Fleet & Booking Management | `fleetbooking` | Vehicle models and types, add-ons, booking records and related services |
| Operations & Billing | `operationsbilling` | Vehicle inspections, rentability evaluation, charge items and invoices |
| Reporting & Analytics | `reportinganalytics` | Revenue, vehicle-usage and customer-spending reports, including CSV export |

## Shared layers

- `ui/` — console entry point and customer, administrator and login/registration menus
- `common/model/` — shared abstract `Transaction` base class
- `util/` — file access, application data-file paths, input handling and validation helpers

## Main data flow

1. The console UI calls the relevant module services.
2. The account and fleet/booking services manage users, customers, vehicles, add-ons and bookings.
3. Operations & Billing uses booking and vehicle information to record inspections and generate invoices.
4. Reporting & Analytics reads booking and invoice data to generate summary reports.
5. Services persist application records in local text files; invoice and report exports also use CSV files.

The current implementation uses package-based separation and shared service calls rather than separate deployable applications.
