package Splitwise.service;

import Splitwise.beans.Expense;
import Splitwise.beans.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageContext {
    public static User currentUser = null;

    public static Map<Long, User> userLookup = new HashMap<>();
    public static Map<Integer, Expense> expenseLookup = new HashMap<>();
    public static List<User> usersList = new ArrayList<>();
    public static List<Expense> expenseList = new ArrayList<>();
    private static Integer expenseID = 1000;

    public static Integer getNextExpenseID() {
        return expenseID++;
    }
}
