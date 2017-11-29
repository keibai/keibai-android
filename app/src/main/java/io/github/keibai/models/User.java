package io.github.keibai.models;

import java.sql.Timestamp;

public class User extends ModelAbstract {

    public String name;
    public String lastName;
    public String password;
    public String email;
    public double credit;
    public Timestamp createdAt;
    public Timestamp updatedAt;

    @Override
    public String toString() {
        return "Name: '" + name + "', Last Name: '" + lastName + "', Password: '" + password +
                "', Email: '" + email + "', Credit: '" + credit + "', Created At: '" + createdAt +
                "', Updated At: '" + updatedAt + "'";

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (Double.compare(user.credit, credit) != 0) return false;
        if (!name.equals(user.name)) return false;
        if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) return false;
        if (!password.equals(user.password)) return false;
        if (!email.equals(user.email)) return false;
        if (createdAt != null ? !createdAt.equals(user.createdAt) : user.createdAt != null) return false;
        return updatedAt != null ? updatedAt.equals(user.updatedAt) : user.updatedAt == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + password.hashCode();
        result = 31 * result + email.hashCode();
        temp = Double.doubleToLongBits(credit);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        return result;
    }
}
