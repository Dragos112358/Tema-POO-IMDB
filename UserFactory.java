// Enum to represent different account types
enum AccountType {
    REGULAR,
    CONTRIBUTOR,
    ADMIN
}

// User interface (or abstract class)
interface User2 {
    void displayType();
}

// Concrete implementation for Regular User
class RegularUser extends User {
    public void displayType() {
        System.out.println("Regular User");
    }
}

// Concrete implementation for Contributor User
class ContributorUser extends User {
    public void displayType() {
        System.out.println("Contributor User");
    }
}

// Concrete implementation for Admin User
class AdminUser implements User2 {
    @Override
    public void displayType() {
        System.out.println("Admin User");
    }
}

// Factory class to create instances of User based on AccountType
public class UserFactory {
    public Object createUser(AccountType accountType) {
        switch (accountType) {
            case REGULAR:
                return new RegularUser();
            case CONTRIBUTOR:
                return new ContributorUser();
            case ADMIN:
                return new AdminUser();
            default:
                throw new IllegalArgumentException("Unsupported account type: " + accountType);
        }
    }

    public void main(String[] args) {
        // Example usage of the UserFactory
        UserFactory userFactory = new UserFactory();

        // Create a regular user
        User2 regularUser = (User2) userFactory.createUser(AccountType.REGULAR);
        regularUser.displayType();

        // Create a contributor user
        User2 contributorUser = (User2) userFactory.createUser(AccountType.CONTRIBUTOR);
        contributorUser.displayType();

        // Create an admin user
        User2 adminUser = (User2) userFactory.createUser(AccountType.ADMIN);
        adminUser.displayType();
    }
}

