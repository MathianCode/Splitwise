package Splitwise.service;

import Splitwise.beans.Expense;
import Splitwise.beans.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseOperations {

    public ExpenseOperations () {
        StorageContext.expenseList = new ArrayList<>();
    }
    public Expense getContribution(Integer expenseId) throws Exception {
        if(StorageContext.expenseLookup.containsKey(expenseId)) {
            return StorageContext.expenseLookup.get(expenseId);
        }
        if(StorageContext.expenseList == null || StorageContext.expenseList.isEmpty()) {
            System.out.println("No Expenses added yet");
            return null;
        }
        Expense expense = StorageContext.expenseList.stream().filter(exp -> exp.getExpenseId().equals(expenseId)).findFirst().orElse(null);
        return expense;
    }
    public List<Expense> getContributions() throws Exception {
        return StorageContext.expenseList.stream()
                .filter(exp -> exp.getCreatedBy().getPhno().equals(StorageContext.currentUser.getPhno()))
                .collect(Collectors.toList());
    }
    public List<Expense> getOwings() throws Exception {
        return StorageContext.expenseList.stream()
                .filter(exp -> exp.getInvolvedUsers().stream().anyMatch(user -> user.getPhno().equals(StorageContext.currentUser.getPhno())))
                .collect(Collectors.toList());
    }

    public double getUserOwingAmount(Expense expense) {
        return this.getUserOwingAmount(expense, StorageContext.currentUser);
    }
    public double getUserOwingAmount(Expense expense, User user) {
        double amount = expense.getAmount();
        if(Expense.SplitType.PERCENTAGE.equals(expense.getSplitType())) {
            double percentile = expense.getUserVsSharePercentile().get(user.getPhno());
            return amount - amount * percentile/100;
        }
        return amount/expense.getInvolvedUsers().size();
    }

    public void createExpense(Expense expense) throws Exception {
        expense.setCreatedBy(StorageContext.currentUser);
        expense.setExpenseId(StorageContext.getNextExpenseID());
        StorageContext.expenseList.add(expense);
    }
    public boolean settleAllOwes() throws Exception {
        List<Expense> expenses = this.getOwings();
        if(expenses.isEmpty()) {
            System.out.println("You dont have any Expenses or Owes");
            return false;
        }
        double totalAmount = expenses.stream().mapToDouble(expense -> this.getUserOwingAmount(expense, StorageContext.currentUser)).sum();
        System.out.println("You have settled : " + totalAmount);
        /*double amountFetchedFromWallet;
        if(amountFetchedFromWallet < totalAmount) {
            throw new Exception("Have entered an insufficent amount");
        } else if(amountFetchedFromWallet > totalAmount) {
            amountFetchedFromWallet = totalAmount;
            System.out.println(StorageContext.currentUser + "  has tried to pay extra " + (amountFetchedFromWallet-totalAmount));
            //throw new Exception("Have entered an exceeding amount");
        }*/
        expenses.forEach(expense -> {
            expense.getInvolvedUsers().remove(StorageContext.currentUser);
        });
        return true;
    }
    public boolean settleOwes(Integer expenseId) throws Exception {
        Expense expense = this.getContribution(expenseId);
        if(expense == null) {
            System.out.println("Invalid Expense ID");
            return false;
        }
        if(expense.getCreatedBy().getPhno().equals(StorageContext.currentUser.getPhno())) {
            throw new Exception("You can't settle your own created expense");
        }
        if(expense.getInvolvedUsers().stream().noneMatch(user -> user.getPhno().equals(StorageContext.currentUser.getPhno()))) {
            throw new Exception("Invalid User, you don't belong in this expense");
        }
        System.out.println("You have settled : " + this.getUserOwingAmount(expense, StorageContext.currentUser));
        return true;
    }
    public boolean hasExpenseToBeSettled(User user) throws Exception {
        boolean hasBorrowed = StorageContext.expenseList.stream().anyMatch(expense -> expense.getInvolvedUsers().stream().anyMatch(usr -> usr.getPhno().equals(user.getPhno())));
        boolean hasContributed = StorageContext.expenseList.stream().anyMatch(expense -> expense.getCreatedBy().getPhno().equals(user.getPhno()));
        return hasBorrowed || hasContributed;
    }
}
