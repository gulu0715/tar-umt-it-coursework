# Python Expense Tracker

A desktop expense-tracking application built with **Python** and **Tkinter**. The project is kept as a separate, self-contained folder in this coursework repository.

## Features

- Add, update and delete expense records
- Select from predefined expense categories
- Enter dates with an in-app date picker
- Search records by date range and category
- View an expense list and total spending summary
- Store records locally in a text file

## Requirements

- Python 3
- Tkinter (normally included with standard Python installations; some Linux distributions package it separately)

No third-party Python packages are required by the current source.

## Run

Open a terminal in this project folder and run:

```bash
python main.py
```

The application creates a `database/` folder and stores expense records in `database/expenses.txt` when data is saved. This runtime data is intentionally excluded from Git.

## Project structure

```text
python-expense-tracker/
├── main.py
├── README.md
└── src/
    ├── __init__.py
    └── expense_tracker.py
```

## Notes

This is a coursework application using local text-file persistence. It is intended as a learning project rather than a production financial-management system.
