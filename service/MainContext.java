package Splitwise.service;

import Splitwise.beans.Expense;
import Splitwise.beans.User;

import java.util.*;

public class MainContext {
    UserOperations userOperations;
    ExpenseOperations expenseOperations;
    Scanner scan;
    public MainContext() {
        userOperations = new UserOperations();
        expenseOperations = new ExpenseOperations();
        scan = new Scanner(System.in);
    }

    public void getUsersPage() throws Exception {
        System.out.println("-----User List-----");
        List<User> users = userOperations.getUsers();
        if (users == null || users.isEmpty()) {
            System.out.println("No Users Available");
            return;
        }
        users.forEach(System.out::println);
        System.out.println("--------------------------------------\n");
    }
    private void createUserPage() throws Exception {
        String name;
        String phone;
        System.out.println("Name");
        name = scan.nextLine();
        System.out.println("Phno");
        phone = scan.nextLine();
        userOperations.addUser(new User(
                Integer.parseInt(phone),
                name,
                User.UserType.GUSR
        ));
    }
    private void removeUserPage(boolean isCurrentUser) throws Exception {
        System.out.println("Enter User Phone : ");
        userOperations.removeUser(isCurrentUser ? StorageContext.currentUser.getPhno() : Long.parseLong(scan.nextLine()));
    }

    private void createExpensePage() throws Exception {
        System.out.println("Enter Description : ");
        String description = scan.nextLine();
        System.out.println("Enter Amount : ");
        double amount = Double.parseDouble(scan.nextLine());
        System.out.println("Kindly Choose the Expense Split Type : \n A) " + Expense.SplitType.EQUAL + "\nB) " + Expense.SplitType.PERCENTAGE + "\nChoice : ");
        String splitChoice = scan.nextLine();
        Expense.SplitType splitType = splitChoice.equalsIgnoreCase("A") ? Expense.SplitType.EQUAL  : Expense.SplitType.PERCENTAGE;
        System.out.println("Add Users to Expense\n//Enter 'Stop' to continue creating the expense//");
        HashSet<User> usersInvolved = new HashSet<>();
        Map<Long, Double> userVsSplitPercentile = new HashMap<>();
        while(true) {
            System.out.println("ID\tName");
            StorageContext.usersList.stream().filter(usr -> !usr.getPhno().equals(StorageContext.currentUser.getPhno()) && !usersInvolved.contains(usr)).forEach(user -> {
                    System.out.println(user.getPhno()+"\t"+user.getName());
            });
            System.out.println("Enter User phno. : ");
            String phnoInput = scan.nextLine();
            if (phnoInput.equalsIgnoreCase("stop")) {
                break;
            }
            User user = userOperations.getUser(Long.parseLong(phnoInput));
            if(usersInvolved.contains(user)) {
                System.out.println("Please dont add duplicate User");
                continue;
            }
            if (Expense.SplitType.PERCENTAGE.equals(splitType)) {
                System.out.println("Enter split % for this user : ");
                Double splitInput = scan.nextDouble();
                userVsSplitPercentile.put(user.getPhno(), splitInput);
            }
        }
        Expense expense = new Expense(amount, description, null, usersInvolved, splitType, userVsSplitPercentile);
        expenseOperations.createExpense(expense);
    }
    private void userSettlementViewPage() throws Exception {
        List<Expense> expenses = expenseOperations.getContributions();
        if (expenses.isEmpty()) {
            System.out.println("No Expense available");
        } else {
            System.out.println("==================People to Owe you==================");
            expenses.forEach(expense -> {
                System.out.println("Expense-ID : " + expense.getExpenseId());
                System.out.println("Amount : " + expense.getAmount());
                expense.getInvolvedUsers().forEach(user -> {
                    System.out.println("\t" + user.getName() + "owes : " + expenseOperations.getUserOwingAmount(expense, user));
                });
            });
            System.out.println("Your total Contribution : " + expenses.stream().mapToDouble(Expense::getAmount).sum());
            System.out.println();
            System.out.println("------------------You Owe to People--------------------");
            List<Expense> paybacks = expenseOperations.getOwings();
            paybacks.forEach(expense -> {
                System.out.println(expense.getExpenseId() + "\t" + "you owe to " + expense.getCreatedBy().getName() + " : " + expenseOperations.getUserOwingAmount(expense, StorageContext.currentUser));
            });
            System.out.println("Payback Total : " + paybacks.stream().mapToDouble(expense -> expenseOperations.getUserOwingAmount(expense, StorageContext.currentUser)).sum());
        }
    }
    private void settlementActionPage() throws Exception {
        //Rework
        System.out.println("------------------You Owe to People--------------------");
        List<Expense> paybacks = expenseOperations.getOwings();
        paybacks.forEach(expense -> {
            System.out.println(expense.getExpenseId() + "\t" + "you owe to " + expense.getCreatedBy().getName() + " : " + expenseOperations.getUserOwingAmount(expense));
        });
        System.out.println("Enter the expense ID to settle : ");
        expenseOperations.settleOwes(Integer.parseInt(scan.nextLine()));
    }
    private void completeAllSettlementAction() throws Exception {
        System.out.println("------------------You Owe to People--------------------");
        List<Expense> paybacks = expenseOperations.getOwings();
        paybacks.forEach(expense -> {
            System.out.println(expense.getExpenseId() + "\t" + "you owe to " + expense.getCreatedBy().getName() + " : " + expense.getAmount());
        });
        System.out.println("Settling all these expense, confirm to Pay [Y/N] : ");
        String confirmation = scan.nextLine();
        if(confirmation.equalsIgnoreCase("y") || confirmation.equalsIgnoreCase("yes") || confirmation.equalsIgnoreCase("confirm")) {
            expenseOperations.settleAllOwes();
        }
    }



    private void printMenu() {
        System.out.println("===============Menu==============");
        if (userOperations.isAdmin(StorageContext.currentUser)) {
            System.out.println("A) Get Users");
            System.out.println("B) Add Users");
            System.out.println("C) Remove Users");
        }
        System.out.println("1) Create Expense");
        System.out.println("2) Get Expenses");
        System.out.println("3) Settle An Expense");
        System.out.println("4) Settle All Expenses");
        System.out.println("5) Leave Group");
        System.out.println("6) Logout");
        System.out.println("7) Exit");
    }
    public void run() {
        System.out.println("//Say exit or bye to terminate this code");
        while(true) {
            try {
                if (StorageContext.currentUser == null) {
                    System.out.println("Login : User Phno please");
                    String usrInput = scan.nextLine();
                    if (usrInput.equalsIgnoreCase("exit") || usrInput.equals("bye")) {
                        return;
                    }
                    userOperations.login(Long.parseLong(usrInput));
                    continue;
                }
                this.printMenu();
                String input = scan.nextLine();
                switch (input) {
                    case "A":
                        this.getUsersPage();
                        break;
                    case "B":
                        this.createUserPage();
                        break;
                    case "C":
                        this.removeUserPage(false);
                        break;
                    case "1" : {
                        this.createExpensePage();
                        break;
                    }
                    case "2": {
                        this.userSettlementViewPage();
                        break;
                    }
                    case "3": {
                        //Rework
                        this.settlementActionPage();
                        break;
                    }
                    case "4": {
                        this.completeAllSettlementAction();
                        break;
                    }
                    case "5": {
                        this.removeUserPage(true);
                        userOperations.logout();
                        break;
                    }
                    case "6": {
                        userOperations.logout();
                        break;
                    }
                    case "7": {
                        System.out.println("Bye, Thankyou");
                        return;
                    }
                    default: {
                        System.out.println("Invlid input...\n");
                    }
                }
            } catch (Exception ex) {
                System.out.println("Exception : " + ex);
            }
        }
    }
    public static void main(String... args) throws Exception {
        new MainContext().run();
    }
}
