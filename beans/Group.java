package Splitwise.beans;

import java.util.List;

public class Group {

    private User admin;
    private List<User> users;

    public Group(User admin) {
        this.admin = admin;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
