package Splitwise.beans;

import java.util.List;

public class User {

    public enum UserType {
        ADMIN,
        GUSR
    }
    private Long phno;
    private String name;
    private UserType userType;
    private List<Expense> expense;

    public User(long phno, String name, UserType userType) {
        this.phno = phno;
        this.name = name;
        this.userType = userType;
    }

    public Long getPhno() {
        return phno;
    }

    public void setPhno(Long phno) {
        this.phno = phno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public List<Expense> getExpense() {
        return expense;
    }

    public void setExpense(List<Expense> expense) {
        this.expense = expense;
    }

    @Override
    public String toString() {
        return "User{" +
                "phno=" + phno +
                ", name='" + name + '\'' +
                ", userType=" + userType +
                ", expense=" + expense +
                '}';
    }
}
