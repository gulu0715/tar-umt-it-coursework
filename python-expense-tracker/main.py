"""Launch the Expense Tracker desktop application."""

import tkinter as tk

from src.expense_tracker import ExpenseTracker


def main() -> None:
    root = tk.Tk()
    root.title("Expense Tracker")
    root.geometry("1200x800")
    root.minsize(980, 680)

    ExpenseTracker(root, root.destroy)
    root.mainloop()


if __name__ == "__main__":
    main()
