package Splitwise.beans;

import java.util.HashSet;
import java.util.Map;

public class Expense {
    public enum SplitType {
        EQUAL,
        PERCENTAGE
    }
    private Integer expenseId;
    private double amount;
    private String description;
    private User createdBy;
    private HashSet<User> involvedUsers;

    private Map<Long, Double> userVsSharePercentile;
    private SplitType splitType;

    public Expense(double amount, String description, User createdBy, HashSet<User> involvedUsers) {
        this(amount, description, createdBy, involvedUsers, SplitType.EQUAL, null);
    }
    public Expense(double amount, String description, User createdBy, HashSet<User> involvedUsers, SplitType splitType, Map<Long, Double> userVsSharePercentile) {
        this.amount = amount;
        this.description = description;
        this.createdBy = createdBy;
        this.involvedUsers = involvedUsers;
        this.splitType = splitType;
        this.userVsSharePercentile = userVsSharePercentile;
    }


    public Integer getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Integer expenseId) {
        this.expenseId = expenseId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public HashSet<User> getInvolvedUsers() {
        return this.involvedUsers;
    }

    public void setInvolvedUsers(HashSet<User> involvedUsers) {
        this.involvedUsers = involvedUsers;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public void setSplitType(SplitType splitType) {
        this.splitType = splitType;
    }

    public Map<Long, Double> getUserVsSharePercentile() {
        return userVsSharePercentile;
    }

    public void setUserVsSharePercentile(Map<Long, Double> userVsSharePercentile) {
        this.userVsSharePercentile = userVsSharePercentile;
    }
}
