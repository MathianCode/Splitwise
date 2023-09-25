package Splitwise.service;

import Splitwise.beans.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserOperations {
    public  UserOperations() {
        User defaultUser = new User(0, "admin", User.UserType.ADMIN);
        StorageContext.usersList.add(defaultUser);
    }

    public void login(Long phno) throws Exception {
        User fetchedUser = this.getUser(phno);
        if(fetchedUser == null) {
            throw new Exception("Invlid User");
        }
        StorageContext.currentUser = this.getUser(phno);
    }
    public void addUser(User newUser) throws Exception {
        if(!isAdmin(StorageContext.currentUser)) {
            throw new Exception("Invalid Admin");
        }

        User user = new User(newUser.getPhno(), newUser.getName(), newUser.getUserType());
        List<User> existingUsers = StorageContext.usersList;
        if(existingUsers == null) {
            existingUsers = new ArrayList<>();
        }
        existingUsers.add(user);
        StorageContext.userLookup.put(user.getPhno(), user);
    }
    public User getUser(Long phno) throws Exception {
        if(StorageContext.userLookup.containsKey(phno)) {
            return StorageContext.userLookup.get(phno);
        }
        List<User> fetchedUser = StorageContext.usersList.stream().filter(usr -> usr.getPhno().equals(phno)).collect(Collectors.toList());
        if(fetchedUser.isEmpty()) {
            return null;
        }
        return fetchedUser.get(0);
    }
    public List<User> getUsers() throws Exception {
        if(!this.isAdmin(StorageContext.currentUser)) {
            throw new Exception("Invalid Admin");
        }
        return StorageContext.usersList;
    }

    public void removeUser(Long userPhno) throws Exception {
        if(!this.isAdmin(StorageContext.currentUser) && !userPhno.equals(StorageContext.currentUser)) {
            throw new Exception("Invalid Admin");
        }
        if(StorageContext.usersList == null || StorageContext.usersList.isEmpty()) {
            throw new Exception("Invalid Group");
        }
        User fetchedUser = StorageContext.currentUser.getPhno().equals(userPhno) ? StorageContext.currentUser : this.getUser(userPhno);
        if(StorageContext.usersList.size() == 1 || fetchedUser.getPhno().equals(StorageContext.currentUser.getPhno())) {
            throw new Exception("Group cant be left without a at least single user");
        }
        if(new ExpenseOperations().hasExpenseToBeSettled(fetchedUser)) {
            throw new Exception(fetchedUser + " can't be removed as there are pending settlements to/from the user");
        }
        StorageContext.userLookup.remove(userPhno);
        StorageContext.usersList.remove(fetchedUser);
    }

    public void logout() {
        StorageContext.currentUser = null;
    }
    public boolean isAdmin(User user) {
        return User.UserType.ADMIN.equals(user.getUserType());
    }
}
