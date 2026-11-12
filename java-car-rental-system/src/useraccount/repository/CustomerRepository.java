package useraccount.repository;

import useraccount.model.Customer;
import useraccount.service.AuthService;
import util.AppPaths;
import util.FileUtil;

public final class CustomerRepository {
    private static Customer[] customers = new Customer[20];
    private static int size = 0;
    private static boolean loaded = false;

    private CustomerRepository() {}

    public static void loadFromFile() {
        if (loaded) return;
        size = 0;
        for (String line : FileUtil.readAllLines(AppPaths.CUSTOMERS_FILE)) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            if (p.length < 5) continue;
            customers = addInternal(customers, new Customer(p[0], p[1], p[2], p[3], p[4]));
            size++;
        }
        loaded = true;
    }

    public static void saveToFile() {
        String[] lines = new String[size];
        for (int i = 0; i < size; i++) {
            Customer c = customers[i];
            lines[i] = String.join("|", c.getId(), c.getName(), c.getPhone(), c.getLicenseNumber(), c.getLicenseType());
        }
        FileUtil.writeAllLines(AppPaths.CUSTOMERS_FILE, lines);
    }

    public static Customer[] listAll() {
        Customer[] out = new Customer[size];
        for (int i = 0; i < size; i++) out[i] = customers[i];
        return out;
    }

    public static Customer getById(String id) {
        if (id == null) return null;
        String target = id.trim();
        for (int i = 0; i < size; i++) {
            if (customers[i].getId().equalsIgnoreCase(target)) return customers[i];
        }
        return null;
    }

    public static boolean add(Customer c) {
        if (c == null || getById(c.getId()) != null) return false;
        customers = addInternal(customers, c);
        size++;
        saveToFile();
        return true;
    }

    public static boolean update(Customer updated) {
        if (updated == null) return false;
        for (int i = 0; i < size; i++) {
            if (customers[i].getId().equalsIgnoreCase(updated.getId())) {
                customers[i] = updated;
                saveToFile();
                return true;
            }
        }
        return false;
    }

    public static boolean deleteById(String id) {
        if (id == null) return false;
        String target = id.trim();
        int idx = indexOf(target);
        if (idx < 0) return false;
        for (int i = idx; i < size - 1; i++) customers[i] = customers[i + 1];
        customers[size - 1] = null;
        size--;
        saveToFile();
        AuthService.deleteUserByUsername(target);
        return true;
    }

    public static boolean isEmpty() { 
        return size == 0; 
    }

    private static int indexOf(String id) {
        for (int i = 0; i < size; i++) if (customers[i].getId().equalsIgnoreCase(id)) return i;
        return -1;
    }

    private static Customer[] addInternal(Customer[] arr, Customer c) {
        if (size >= arr.length) {
            Customer[] next = new Customer[Math.max(arr.length * 2, 10)];
            for (int i = 0; i < arr.length; i++) next[i] = arr[i];
            arr = next;
        }
        arr[size] = c;
        return arr;
    }
}
