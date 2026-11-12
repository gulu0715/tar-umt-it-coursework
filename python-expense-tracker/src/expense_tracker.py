import tkinter as tk
from tkinter import ttk, messagebox
import datetime
import os

class ExpenseTracker(tk.Frame):
    def __init__(self, parent, return_callback):
        # Initialize the tk.Frame parent class
        super().__init__(parent)
        
        self.parent = parent                         # Parent window reference
        self.return_callback = return_callback       # return main menu
        self.file_path = 'database/expenses.txt'     # file path for storing expenses
        
        # expense category
        self.categories = ['Food', 'Transport', 'Housing', 'Entertainment', 
                          'Shopping', 'Healthcare', 'Education', 'Other']
        
        # Create database folder if not found
        os.makedirs(os.path.dirname(self.file_path), exist_ok=True)

        # Variables for search filters
        self.search_start_date_var = tk.StringVar()
        self.search_end_date_var = tk.StringVar()
        self.search_category_var = tk.StringVar()

        # Configure this frame
        self.configure(bg='#2c3e50')
        
        # Configure the frame to expand
        self.grid_rowconfigure(0, weight=1)
        self.grid_columnconfigure(0, weight=1)
        
        # Pack the frame to fill the parent
        self.pack(fill=tk.BOTH, expand=True)

        self.setup_ui() # build UI
        self.load_expenses()    #load data

    def setup_ui(self):
        """Setup the Expense Tracker UI with grid layout"""
        # Configure main grid
        self.grid_rowconfigure(0, weight=0)  # Header
        self.grid_rowconfigure(1, weight=1)  # Content
        self.grid_columnconfigure(0, weight=1)
        
        # Header with blue background
        header_frame = tk.Frame(self, bg='#3498db', height=80)
        header_frame.grid(row=0, column=0, sticky='ew', padx=0, pady=(0, 5))
        header_frame.grid_propagate(False)
        header_frame.grid_columnconfigure(0, weight=1)

        # Header content frame
        header_content = tk.Frame(header_frame, bg='#3498db')
        header_content.pack(fill=tk.BOTH, expand=True, padx=20, pady=10)
        
        # Configure header columns
        header_content.grid_columnconfigure(0, weight=1)
        header_content.grid_columnconfigure(1, weight=2)
        header_content.grid_columnconfigure(2, weight=1)

        # Back button on left side
        back_btn = tk.Button(header_content,
                           text="← Back to Main",
                           font=('Arial', 11),
                           bg='white',
                           fg='#3498db',
                           relief='flat',
                           padx=15,
                           pady=5,
                           command=self.return_callback)
        back_btn.grid(row=0, column=0, sticky='w')

        # Main title in center
        tk.Label(header_content, 
                text="💰 Expense Tracker",
                font=('Arial', 24, 'bold'),
                bg='#3498db',
                fg='white').grid(row=0, column=1)

        # Empty space on right
        tk.Label(header_content, 
                bg='#3498db').grid(row=0, column=2)

        # Main content area
        content_frame = tk.Frame(self, bg='#2c3e50')
        content_frame.grid(row=1, column=0, sticky='nsew', padx=20, pady=10)
        
        # Configure content frame grid
        content_frame.grid_rowconfigure(0, weight=1)
        content_frame.grid_columnconfigure(0, weight=1)
        content_frame.grid_columnconfigure(1, weight=1)

        # Left panel for input form
        left_frame = tk.Frame(content_frame, bg='#34495e', relief='raised', borderwidth=2)
        left_frame.grid(row=0, column=0, sticky='nsew', padx=(0, 10), pady=0)
        
        # Configure left panel grid
        left_frame.grid_rowconfigure(0, weight=0)  # Form header
        left_frame.grid_rowconfigure(1, weight=1)  # Form content
        left_frame.grid_rowconfigure(2, weight=0)  # Buttons
        left_frame.grid_columnconfigure(0, weight=1)

        # Form header with dark background
        form_header = tk.Frame(left_frame, bg='#2c3e50', height=60)
        form_header.grid(row=0, column=0, sticky='ew')
        form_header.grid_propagate(False)
        form_header.grid_columnconfigure(0, weight=1)

        # Form title label
        tk.Label(form_header,
                text="Add New Expense",
                font=('Arial', 16, 'bold'),
                bg='#2c3e50',
                fg='white').pack(fill=tk.X, padx=20, pady=15, anchor='w')

        # Form container
        form_container = tk.Frame(left_frame, bg='#34495e')
        form_container.grid(row=1, column=0, sticky='nsew', padx=20, pady=10)
        
        # Configure form container grid
        for i in range(8):
            form_container.grid_rowconfigure(i, weight=0)
        form_container.grid_rowconfigure(8, weight=1)
        form_container.grid_columnconfigure(0, weight=1)

        # Form fields definition
        form_fields = [
            ("Amount (RM):", "amount_entry"),
            ("Category:", "category_combo"),
            ("Description:", "desc_entry"),
            ("Date:", "date_entry")
        ]

        for i, (label_text, widget_name) in enumerate(form_fields):
            # Form field label
            label = tk.Label(form_container, 
                           text=label_text,
                           font=('Arial', 11, 'bold'),
                           bg='#34495e',
                           fg='white',
                           anchor='w')
            label.grid(row=i*2, column=0, sticky='w', pady=(15, 5))

            # Form field widget
            if widget_name == "amount_entry":
                # Amount entry field with validation
                vcmd = (self.parent.register(self.validate_amount), '%P')
                self.amount_entry = tk.Entry(form_container, 
                                        font=('Arial', 11),
                                        relief='solid',
                                        bd=1,
                                        bg='#ecf0f1',
                                        fg='#2c3e50',
                                        validate='key',
                                        validatecommand=vcmd)
                self.amount_entry.grid(row=i*2+1, column=0, sticky='ew', pady=(0, 10))
            
            elif widget_name == "category_combo":
                # Category dropdown combobox
                self.category_var = tk.StringVar()
                self.category_combo = ttk.Combobox(form_container, 
                                                 textvariable=self.category_var,
                                                 values=self.categories, 
                                                 state='readonly',
                                                 font=('Arial', 11))
                self.category_combo.grid(row=i*2+1, column=0, sticky='ew', pady=(0, 10))
            
            elif widget_name == "desc_entry":
                # Description entry field
                self.desc_entry = tk.Entry(form_container, 
                                         font=('Arial', 11),
                                         relief='solid',
                                         bd=1,
                                         bg='#ecf0f1',
                                         fg='#2c3e50')
                self.desc_entry.grid(row=i*2+1, column=0, sticky='ew', pady=(0, 10))
            
            elif widget_name == "date_entry":
                # Date entry frame with calendar button
                date_frame = tk.Frame(form_container, bg='#34495e')
                date_frame.grid(row=i*2+1, column=0, sticky='ew', pady=(0, 10))
                date_frame.grid_columnconfigure(0, weight=1)
                date_frame.grid_columnconfigure(1, weight=0)
                
                # Read-only date entry field
                self.date_entry_var = tk.StringVar()
                self.date_entry_var.set(datetime.datetime.now().strftime('%Y-%m-%d'))

                self.date_entry = tk.Entry(date_frame,
                                  font=('Arial', 11),
                                  relief='solid',
                                  bd=1,
                                  bg='#ecf0f1',
                                  fg='#2c3e50',
                                  textvariable=self.date_entry_var,
                                  state='readonly')
                self.date_entry.grid(row=0, column=0, sticky='ew')
                
                # Calendar button
                cal_btn = tk.Button(date_frame, 
                                  text="📅", 
                                  font=('Arial', 11),
                                  bg='#3498db', 
                                  fg='white', 
                                  width=3,
                                  relief='flat',
                                  command=self.show_calendar)
                cal_btn.grid(row=0, column=1, padx=(5, 0), sticky='e')

        # Action buttons frame
        btn_frame = tk.Frame(left_frame, bg='#34495e')
        btn_frame.grid(row=2, column=0, sticky='ew', pady=(0, 20), padx=20)
        
        # Configure button grid
        btn_frame.grid_columnconfigure(0, weight=1)
        btn_frame.grid_columnconfigure(1, weight=1)
        btn_frame.grid_columnconfigure(2, weight=1)

        # Button configurations
        button_configs = [
            ("Add Expense", '#27ae60', self.add_expense, 0),
            ("Update", '#f39c12', self.update_expense, 1),
            ("Delete", '#e74c3c', self.delete_expense, 2)
        ]

        for text, color, command, col in button_configs:
            # Action button
            btn = tk.Button(btn_frame,
                          text=text,
                          font=('Arial', 11, 'bold'),
                          bg=color,
                          fg='white',
                          padx=15,
                          pady=8,
                          relief='flat',
                          command=command)
            btn.grid(row=0, column=col, padx=5, sticky='ew')
            
            # Hover effects for buttons
            btn.bind("<Enter>", lambda e, b=btn, c=color: b.config(bg=self.lighten_color(c, 20)))
            btn.bind("<Leave>", lambda e, b=btn, c=color: b.config(bg=c))

        # Right panel for expense list
        right_frame = tk.Frame(content_frame, bg='#34495e', relief='raised', borderwidth=2)
        right_frame.grid(row=0, column=1, sticky='nsew', padx=(10, 0), pady=0)
        
        # Configure right panel grid
        right_frame.grid_rowconfigure(0, weight=0)  # Header
        right_frame.grid_rowconfigure(1, weight=0)  # Search
        right_frame.grid_rowconfigure(2, weight=1)  # Treeview
        right_frame.grid_columnconfigure(0, weight=1)

        # Right panel header
        right_header = tk.Frame(right_frame, bg='#2c3e50', height=40)
        right_header.grid(row=0, column=0, sticky='ew')
        right_header.grid_propagate(False)
        right_header.grid_columnconfigure(0, weight=1)

        # Expense history title
        tk.Label(right_header,
                text="📋 Expense History",
                font=('Arial', 14, 'bold'),
                bg='#2c3e50',
                fg='white').pack(fill=tk.X, padx=15, pady=8, anchor='w')

        # Search and filter area
        search_frame = tk.Frame(right_frame, bg='#34495e')
        search_frame.grid(row=1, column=0, sticky='ew', padx=10, pady=(5, 5))
        search_frame.grid_columnconfigure(0, weight=1)

        # Search section container
        search_section = tk.Frame(search_frame, bg='#2c3e50', relief='solid', bd=1)
        search_section.pack(fill=tk.X, pady=(0, 5))

        # Search title label
        tk.Label(search_section,
                text="🔍 Search & Filter",
                font=('Arial', 11, 'bold'),
                bg='#2c3e50',
                fg='white',
                padx=10,
                pady=6).pack(anchor='w')

        # Filter inputs frame
        filter_frame = tk.Frame(search_section, bg='#2c3e50')
        filter_frame.pack(fill=tk.X, padx=8, pady=(0, 6))

        # Single row for filter inputs
        row_frame = tk.Frame(filter_frame, bg='#2c3e50')
        row_frame.pack(fill=tk.X, pady=2)

        # Start date filter label
        tk.Label(row_frame, text="From:", font=('Arial', 9),
                 bg='#2c3e50', fg='white').pack(side=tk.LEFT, padx=(0, 3))
        # Start date entry field (read-only)
        self.search_start_entry = tk.Entry(row_frame, textvariable=self.search_start_date_var, width=10,
                                           font=('Arial', 9), relief='solid', bd=1, 
                                           bg='#ecf0f1', fg='#2c3e50', state='readonly')
        self.search_start_entry.pack(side=tk.LEFT, padx=(0, 3))
        # Start date calendar button
        tk.Button(row_frame, text="📅", font=('Arial', 8), 
                  bg='#3498db', fg='white', relief='flat', width=2,
                  command=lambda: self.show_calendar_for_search('start')).pack(side=tk.LEFT, padx=(0, 5))
        
        # End date filter label
        tk.Label(row_frame, text="To:", font=('Arial', 9),
                 bg='#2c3e50', fg='white').pack(side=tk.LEFT, padx=(0, 3))
        # End date entry field (read-only)
        self.search_end_entry = tk.Entry(row_frame, textvariable=self.search_end_date_var, width=10,
                                         font=('Arial', 9), relief='solid', bd=1, 
                                         bg='#ecf0f1', fg='#2c3e50', state='readonly')
        self.search_end_entry.pack(side=tk.LEFT, padx=(0, 3))
        # End date calendar button
        tk.Button(row_frame, text="📅", font=('Arial', 8), 
                  bg='#3498db', fg='white', relief='flat', width=2,
                  command=lambda: self.show_calendar_for_search('end')).pack(side=tk.LEFT, padx=(0, 10))

        # Category filter label
        tk.Label(row_frame, text="Category:", font=('Arial', 9),
                 bg='#2c3e50', fg='white').pack(side=tk.LEFT, padx=(0, 3))
        
        # Category filter combobox
        self.search_category_combo = ttk.Combobox(row_frame, 
                                                textvariable=self.search_category_var,
                                                values=['All Categories'] + self.categories,
                                                state='readonly',
                                                width=12,
                                                font=('Arial', 9))
        self.search_category_combo.set('All Categories')
        self.search_category_combo.pack(side=tk.LEFT, padx=(0, 10))

        # Search button
        search_btn = tk.Button(row_frame, text="Search", font=('Arial', 9, 'bold'),
                  bg='#3498db', fg='white', padx=12, pady=3,
                  relief='flat', command=self.search_expenses)
        search_btn.pack(side=tk.LEFT, padx=(0, 5))
        
        # Clear button
        clear_btn = tk.Button(row_frame, text="Clear", font=('Arial', 9),
                  bg='#95a5a6', fg='white', padx=10, pady=3,
                  relief='flat', command=self.clear_search)
        clear_btn.pack(side=tk.LEFT)
        
        # Hover effects for search buttons
        search_btn.bind("<Enter>", lambda e: search_btn.config(bg='#2980b9'))
        search_btn.bind("<Leave>", lambda e: search_btn.config(bg='#3498db'))
        clear_btn.bind("<Enter>", lambda e: clear_btn.config(bg='#7f8c8d'))
        clear_btn.bind("<Leave>", lambda e: clear_btn.config(bg='#95a5a6'))

        # Summary section with blue background
        summary_frame = tk.Frame(search_frame, bg='#3498db', relief='solid', bd=1)
        summary_frame.pack(fill=tk.X, pady=(0, 0))

        # Total expenses summary label
        self.summary_label = tk.Label(summary_frame,
                                     text="Total Expenses: RM 0.00",
                                     font=('Arial', 10, 'bold'),
                                     bg='#3498db',
                                     fg='white',
                                     padx=12,
                                     pady=6)
        self.summary_label.pack()

        # Treeview container for expense list
        tree_container = tk.Frame(right_frame, bg='#34495e')
        tree_container.grid(row=2, column=0, sticky='nsew', padx=10, pady=(0, 10))
        
        # Configure tree container grid
        tree_container.grid_rowconfigure(0, weight=1)
        tree_container.grid_columnconfigure(0, weight=1)

        # Treeview columns definition
        columns = ('No', 'Date', 'Category', 'Description', 'Amount')
        
        # Treeview styling for dark theme
        style = ttk.Style()
        style.theme_use('clam')
        style.configure("Treeview", 
                       background="#34495e",
                       foreground="white",
                       rowheight=25,
                       fieldbackground="#34495e",
                       font=('Arial', 10))
        style.configure("Treeview.Heading", 
                       background="#2c3e50",
                       foreground="white",
                       font=('Arial', 10, 'bold'),
                       relief='flat')
        style.map('Treeview', 
                 background=[('selected', '#3498db')],
                 foreground=[('selected', 'white')])

        # Treeview widget creation
        self.tree = ttk.Treeview(tree_container, columns=columns, show='headings')

        # Column configuration
        column_config = [
            ('No', 60, 'center', False),
            ('Date', 120, 'center', False),
            ('Category', 120, 'center', False),
            ('Description', 300, 'w', True),
            ('Amount', 120, 'e', False)
        ]

        for col, width, anchor, stretch in column_config:
            self.tree.heading(col, text=col)
            self.tree.column(col, width=width, anchor=anchor, stretch=stretch)

        # Alternate row colors
        self.tree.tag_configure('oddrow', background='#2c3e50')
        self.tree.tag_configure('evenrow', background='#3b5998')

        # Scrollbars
        y_scrollbar = ttk.Scrollbar(tree_container, orient=tk.VERTICAL, command=self.tree.yview)
        x_scrollbar = ttk.Scrollbar(tree_container, orient=tk.HORIZONTAL, command=self.tree.xview)
        self.tree.configure(yscrollcommand=y_scrollbar.set, xscrollcommand=x_scrollbar.set)

        # Treeview layout
        self.tree.grid(row=0, column=0, sticky='nsew', padx=(0, 2))
        y_scrollbar.grid(row=0, column=1, sticky='ns', padx=(0, 0))
        x_scrollbar.grid(row=1, column=0, sticky='ew', padx=(0, 2))

        tree_container.grid_rowconfigure(0, weight=1)
        tree_container.grid_columnconfigure(0, weight=1)

        # Bind selection event
        self.tree.bind('<<TreeviewSelect>>', self.on_expense_select)

    def lighten_color(self, color, amount):
        """Lighten a color by given amount"""
        if color.startswith('#'):
            color = color.lstrip('#')
            rgb = tuple(int(color[i:i+2], 16) for i in (0, 2, 4))
            adjusted = tuple(min(255, c + amount) for c in rgb)
            return f'#{adjusted[0]:02x}{adjusted[1]:02x}{adjusted[2]:02x}'
        return color

    # Calendar functions
    def show_calendar_for_search(self, date_type='start'):
        """Show calendar for search date selection"""
        try:
            from SimpleCalendar import SimpleCalendar
            def set_date(date_str):
                if date_type == 'start':
                    self.search_start_date_var.set(date_str)
                else:
                    self.search_end_date_var.set(date_str)
            SimpleCalendar(self.parent, set_date)
        except ImportError:
            self.create_simple_date_picker_search()

    def create_simple_date_picker_search(self):
        """Simple date picker for search"""
        top = tk.Toplevel(self.parent)
        top.title("Select Date")
        top.geometry("300x250")
        top.configure(bg='#2c3e50')

        today = datetime.datetime.now()

        # Date picker title
        tk.Label(top, text="Select Date", font=('Arial', 14, 'bold'),
                bg='#2c3e50', fg='white').pack(pady=10)

        # Year selection
        tk.Label(top, text="Year:", font=('Arial', 11), bg='#2c3e50', fg='white').pack(pady=5)
        year_var = tk.StringVar(value=str(today.year))
        year_spin = tk.Spinbox(top, from_=2000, to=2100, textvariable=year_var,
                             width=10, font=('Arial', 11), relief='solid', bd=1, 
                             bg='#ecf0f1', fg='#2c3e50')
        year_spin.pack()

        # Month selection
        tk.Label(top, text="Month:", font=('Arial', 11), bg='#2c3e50', fg='white').pack(pady=5)
        month_var = tk.StringVar(value=str(today.month))
        month_spin = tk.Spinbox(top, from_=1, to=12, textvariable=month_var,
                              width=10, font=('Arial', 11), relief='solid', bd=1, 
                              bg='#ecf0f1', fg='#2c3e50')
        month_spin.pack()

        # Day selection
        tk.Label(top, text="Day:", font=('Arial', 11), bg='#2c3e50', fg='white').pack(pady=5)
        day_var = tk.StringVar(value=str(today.day))
        day_spin = tk.Spinbox(top, from_=1, to=31, textvariable=day_var,
                            width=10, font=('Arial', 11), relief='solid', bd=1, 
                            bg='#ecf0f1', fg='#2c3e50')
        day_spin.pack()

        def set_date():
            year = year_var.get()
            month = month_var.get().zfill(2)
            day = day_var.get().zfill(2)
            self.search_date_var.set(f"{year}-{month}-{day}")
            top.destroy()

        # Select date button
        date_btn = tk.Button(top, text="Select Date", command=set_date,
                 bg='#3498db', fg='white', font=('Arial', 11, 'bold'),
                 padx=20, pady=8, relief='flat')
        date_btn.pack(pady=20)
        
        # Hover effect for date button
        date_btn.bind("<Enter>", lambda e: date_btn.config(bg='#2980b9'))
        date_btn.bind("<Leave>", lambda e: date_btn.config(bg='#3498db'))

    def show_calendar(self):
        """Show calendar for expense date selection"""
        try:
            from SimpleCalendar import SimpleCalendar
            def set_date(date_str):
                self.date_entry_var.set(date_str)
            SimpleCalendar(self.parent, set_date)
        except ImportError:
            self.create_simple_date_picker()

    def create_simple_date_picker(self):
        """Simple date picker for expense date"""
        top = tk.Toplevel(self.parent)
        top.title("Select Date")
        top.geometry("300x250")
        top.configure(bg='#2c3e50')
        today = datetime.datetime.now()
        
        # Date picker title
        tk.Label(top, text="Select Date", font=('Arial', 14, 'bold'),
                bg='#2c3e50', fg='white').pack(pady=10)
        
        # Year selection
        tk.Label(top, text="Year:", font=('Arial', 11), bg='#2c3e50', fg='white').pack(pady=5)
        year_var = tk.StringVar(value=str(today.year))
        year_spin = tk.Spinbox(top, from_=2000, to=2100, textvariable=year_var,
                             width=10, font=('Arial', 11), relief='solid', bd=1, 
                             bg='#ecf0f1', fg='#2c3e50')
        year_spin.pack()
        
        # Month selection
        tk.Label(top, text="Month:", font=('Arial', 11), bg='#2c3e50', fg='white').pack(pady=5)
        month_var = tk.StringVar(value=str(today.month))
        month_spin = tk.Spinbox(top, from_=1, to=12, textvariable=month_var,
                              width=10, font=('Arial', 11), relief='solid', bd=1, 
                              bg='#ecf0f1', fg='#2c3e50')
        month_spin.pack()
        
        # Day selection
        tk.Label(top, text="Day:", font=('Arial', 11), bg='#2c3e50', fg='white').pack(pady=5)
        day_var = tk.StringVar(value=str(today.day))
        day_spin = tk.Spinbox(top, from_=1, to=31, textvariable=day_var,
                            width=10, font=('Arial', 11), relief='solid', bd=1, 
                            bg='#ecf0f1', fg='#2c3e50')
        day_spin.pack()

        def set_date():
            year = year_var.get()
            month = month_var.get().zfill(2)
            day = day_var.get().zfill(2)
            self.date_entry.delete(0, tk.END)
            self.date_entry.insert(0, f"{year}-{month}-{day}")
            top.destroy()

        # Select date button
        date_btn = tk.Button(top, text="Select Date", command=set_date,
                 bg='#3498db', fg='white', font=('Arial', 11, 'bold'),
                 padx=20, pady=8, relief='flat')
        date_btn.pack(pady=20)
        
        # Hover effect for date button
        date_btn.bind("<Enter>", lambda e: date_btn.config(bg='#2980b9'))
        date_btn.bind("<Leave>", lambda e: date_btn.config(bg='#3498db'))

    def search_expenses(self):
        """Filter expenses based on search criteria"""
        # get search values from input
        start_date = self.search_start_date_var.get().strip()
        end_date = self.search_end_date_var.get().strip()
        category_filter = self.search_category_var.get().strip()
        #if no select category, not apply category
        if category_filter == "All Categories":
            category_filter = ""
        # clear records before display search results
        for item in self.tree.get_children():
            self.tree.delete(item)

        total = 0.0
        match_count = 0
        #check whether the data file exists
        if os.path.exists(self.file_path):
            with open(self.file_path, 'r') as f:
                lines = [line.strip() for line in f if line.strip()] # read all non-emptylines
                no = 1

                for line in lines:
                    parts = line.split('|')
                    if len(parts) < 5:
                        continue

                    match = True
                    expense_date = parts[1]
                     # filter by start,end,category
                    if start_date and expense_date < start_date:
                         match = False
                    if end_date and expense_date > end_date:
                         match = False
                    if category_filter and parts[2] != category_filter:
                         match = False

                    if match:
                         try:
                             amount = float(parts[4])
                             total += amount
                             match_count += 1

                             self.tree.insert(
                                 '',
                                 tk.END,
                                 iid=parts[0],      #ID
                                 values=[
                                     no,            # No
                                     parts[1],      # Date
                                     parts[2],      # Category
                                     parts[3],      # Description
                                     f"{amount:.2f}"#amount
                                 ],
                                 tags=('evenrow' if no % 2 == 0 else 'oddrow',)
                             )
                             no += 1
                         except:
                            pass
        # update total expense
        self.summary_label.config(text=f"Total Expenses: RM {total:,.2f}")
        conditions = []
        if start_date:
            conditions.append(f"From: {start_date}")
        if end_date:
            conditions.append(f"To: {end_date}")
        if category_filter:
            conditions.append(f"Category: {category_filter}")

        if match_count == 0:
            messagebox.showinfo("Search Result", "No records found matching your search criteria.")
        else:
            if conditions:
                condition_str = ", ".join(conditions)
                messagebox.showinfo("Search Result", f"Found {match_count} record(s)\nSearch criteria: {condition_str}")
            else:
                messagebox.showinfo("Search Result", f"Showing all {match_count} record(s)")

    def clear_search(self):
        """Clear search filters and show all records"""
        self.search_start_date_var.set('')
        self.search_end_date_var.set('')
        self.search_category_combo.set('All Categories')
        self.load_expenses()

    # Core expense functions
    def load_expenses(self):
        """Load all expenses from database"""
        for item in self.tree.get_children():
            self.tree.delete(item)
        
        total = 0.0
        if os.path.exists(self.file_path):
            try:
                with open(self.file_path, 'r') as f:
                    lines = [line.strip() for line in f if line.strip()]
                    for idx, line in enumerate(lines):
                        parts = line.split('|')
                        if len(parts) >= 5:
                            try:
                                amount = float(parts[4])
                                total += amount
                                no = idx + 1
                               
                                # Alternate row colors
                                tag = 'evenrow' if idx % 2 == 0 else 'oddrow'
                                self.tree.insert(
                                    '',
                                    tk.END,
                                    iid=parts[0],   #ID
                                    values=[
                                        no,            # No（start from 1）
                                        parts[1],      # Date
                                        parts[2],      # Category
                                        parts[3],      # Description
                                        f"{amount:.2f}"
                                    ],
                                    tags=(tag,)
                                    )
                            except:
                                pass
            except Exception as e:
                messagebox.showerror("Error", f"Failed to load expenses: {e}")
        
        self.summary_label.config(text=f"Total Expenses: RM {total:,.2f}")

    def clear_form(self):
        """Clear input form"""
        self.amount_entry.delete(0, tk.END)
        self.category_var.set('')
        self.desc_entry.delete(0, tk.END)
        self.date_entry_var.set(datetime.datetime.now().strftime('%Y-%m-%d'))

    def on_expense_select(self, event):
        """Handle expense selection from treeview
         When a user selects a record in the table,
         this function will display the selected record's
         details in the input form for viewing or editing.
        """
        selection = self.tree.selection() # Get selected item from Treeview
        if selection:
            values = self.tree.item(selection[0])['values']    # Get values of the selected record
            if values:
                self.clear_form()
                # Note: values index 4 is the amount (string)
                self.amount_entry.insert(0, values[4].replace('RM ', '').replace(',', ''))
                self.category_var.set(values[2])        #category
                self.desc_entry.insert(0, values[3])    #Description
                self.date_entry_var.set(values[1])      #Date

    def add_expense(self):
        """Add new expense to database
               This function performs the following steps:
               1. Read and validate user input
               2. Generate a unique expense ID
               3. Save the record to file
               4. Refresh the expense list
        """
        ## Get amount ,category, description, and date from
        try:
            amount = float(self.amount_entry.get())
            category = self.category_var.get()
            description = self.desc_entry.get()
            date = self.date_entry_var.get()
             # Validate category selection
            if not category:
                messagebox.showwarning("Warning", "Please select a category")
                return
            # Validate date format (YYYY-MM-DD)
            try:
                datetime.datetime.strptime(date, '%Y-%m-%d')
            except ValueError:
                messagebox.showerror("Error", "Please enter a valid date (YYYY-MM-DD)")
                return
            # Determine last used expense ID
            last_id = 0
            if os.path.exists(self.file_path):
                try:
                    with open(self.file_path, 'r') as f:
                        lines = [ln for ln in (l.strip() for l in f) if ln]
                        if lines:
                            last_line = lines[-1]
                            parts = last_line.split('|')
                            try:
                                last_id = int(parts[0])
                            except (ValueError, IndexError):
                                last_id = 0
                except Exception as e:
                    messagebox.showerror("Error", f"Failed to read expenses file: {e}")
                    return
            # Generate new unique expense ID
            expense_id = last_id + 1
            
            with open(self.file_path, 'a') as f:
                f.write(f"{expense_id}|{date}|{category}|{description}|{amount}\n")
            # Refresh expense list in UI
            self.load_expenses()
            # Clear input form after successful add
            self.clear_form()
            messagebox.showinfo("Success", "Expense added successfully!")
        except ValueError:
            messagebox.showerror("Error", "Please enter a valid amount")  # Handle invalid amount input
    
    def validate_amount(self, value):
        if value.strip() == "":
           return True
        try:
           v = float(value)
           if v < 0:
             return False
           return True
        except ValueError:
           return False
    # update function
    def update_expense(self):
        """Update selected expense
         This function performs the following steps:
         1. Check if a record is selected
         2. Read and validate user input
         3. Update the selected record in the file
         4. Refresh the expense list
        
        """
        selection = self.tree.selection()  ## Get selected item from Treeview
        # Ensure a record is selected
        if not selection:
            messagebox.showwarning("Warning", "Please select an expense to update")
            return
        
        try:
            expense_id = selection[0]  # Selected expense ID (used as primary key
            # Read updated values from input form
            amount = float(self.amount_entry.get())
            category = self.category_var.get()
            description = self.desc_entry.get()
            date = self.date_entry_var.get()
             # Validate category selection
            if not category:
                messagebox.showwarning("Warning", "Please select a category")
                return
            # Validate date format
            try:
                datetime.datetime.strptime(date, '%Y-%m-%d')
            except ValueError:
                messagebox.showerror("Error", "Please enter a valid date (YYYY-MM-DD)")
                return
            # Read all records from file
            with open(self.file_path, 'r') as f:
                lines = f.readlines()
            # Write updated records back to file
            with open(self.file_path, 'w') as f:
                for line in lines:
                    if line.startswith(f"{expense_id}|"):    # If this is the selected record, update it
                        f.write(f"{expense_id}|{date}|{category}|{description}|{amount}\n")
                    else:
                        f.write(line)

            self.load_expenses()
            self.clear_form()
            messagebox.showinfo("Success", "Expense updated successfully!")
        # Handle invalid amount input
        except ValueError:
            messagebox.showerror("Error", "Please enter a valid amount")
    # delete function
    def delete_expense(self):
        """Delete selected expense
        This function performs the following steps:
        1. Check if a record is selected
        2. Ask user for confirmation
        3. Remove the record from file
        4. Refresh the expense list
        """
        selection = self.tree.selection() # Get selected item from Treeview
         # Ensure a record is selected
        if not selection:
            messagebox.showwarning("Warning", "Please select an expense to delete")
            return
         # Ask user for delete confirmation
        if messagebox.askyesno("Confirm", "Are you sure you want to delete this expense?"):
            expense_id = selection[0]
             # Read all records from file
            with open(self.file_path, 'r') as f:
                lines = f.readlines()
             # Write back all records except the deleted one
            with open(self.file_path, 'w') as f:
                for line in lines:
                    if not line.startswith(f"{expense_id}|"):
                        f.write(line)

            self.load_expenses()
            self.clear_form()
            messagebox.showinfo("Success", "Expense deleted successfully!")