import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Admin extends JFrame {
    static class RequestRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value instanceof User) {
                value = ((User) value).getUsername();
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
    public Admin() {
        // Set the layout manager to null for absolute positioning
        setLayout(null);

        // Create buttons
        JButton adaugareButton = new JButton("Adaugare useri");
        JButton stergereButton = new JButton("Stergere useri");
        JButton close = new JButton("Inchidere");

        // Set the bounds of the buttons
        adaugareButton.setBounds(50, 50, 150, 30);
        stergereButton.setBounds(50, 100, 150, 30);

        // Add action listeners to the buttons
        adaugareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add your logic for handling "Adaugare useri" button click
                dispose();
                new AddUserWindow();
                System.out.println("Adaugare useri button clicked");
            }
        });

        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add your logic for handling "Stergere useri" button click
                dispose();
                IMDB.steag_admin--;
            }
        });
        stergereButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add your logic for handling "Stergere useri" button click
                dispose();
                new DeleteUserWindow();
                System.out.println("Stergere useri button clicked");
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                IMDB.steag_admin--;
            }
        });
        // Add buttons to the frame
        add(adaugareButton);
        add(stergereButton);

        // Set frame properties
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    public void main(String[] args) {
        // Create an instance of the UserManagementWindow class
        Admin userManagementWindow = new Admin();
    }
}
class DeleteUserWindow extends JFrame {
    private JComboBox<User> userComboBox;
    public DeleteUserWindow() {
        // Set the layout manager to null for absolute positioning
        setLayout(null);

        // Create a JComboBox for user selection
        userComboBox = new JComboBox<>();
        userComboBox.setBounds(50, 20, 200, 30);
        userComboBox.setRenderer(new Admin.RequestRenderer());
        // Create a button
        JButton deleteButton = new JButton("Sterge utilizator");
        JButton closeButton = new JButton("Inchidere");

        // Set the bounds of the buttons
        deleteButton.setBounds(50, 70, 150, 30);
        closeButton.setBounds(50, 120, 150, 30);

        // Add the action listener to the buttons (you can add your logic here)
        deleteButton.addActionListener(e -> deleteUser((User) userComboBox.getSelectedItem()));
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                IMDB.steag_admin--;
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                IMDB.steag_admin--;
            }
        });
        for (User useri : IMDB.lista_useri) {
            userComboBox.addItem(useri);
        }
        // Add components to the frame
        add(userComboBox);
        add(deleteButton);
        add(closeButton);

        // Set frame properties
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window on exit
        setVisible(true);
        setLocationRelativeTo(null);
    }

    private void deleteUser(User selectedUser) {
        for (Movie movie : IMDB.lista_filme) {
            List<Rating> recenzii3 = movie.getReview();
            Iterator<Rating> iterator = recenzii3.iterator();
            while (iterator.hasNext()) {
                Rating recenzie = iterator.next();
                if (recenzie.getUsername().equalsIgnoreCase(selectedUser.getUsername())) {
                    iterator.remove();  // Use iterator to remove the element
                }
            }
            movie.addReview(recenzii3);
        }

        for (Series series : IMDB.lista_seriale) {
            List<Rating> recenzii4 = series.series_get_recenzii();
            Iterator<Rating> iterator = recenzii4.iterator();
            while (iterator.hasNext()) {
                Rating recenzie = iterator.next();
                if (recenzie.getUsername().equalsIgnoreCase(selectedUser.getUsername())) {
                    iterator.remove();  // Use iterator to remove the element
                }
            }
            series.series_set_recenzii(recenzii4);
        }
        selectedUser.stergeListaCereriTrimise();
        selectedUser.stergeListaCereriPrimite();

        IMDB.lista_useri.remove(selectedUser);
        userComboBox.removeItem(selectedUser);

        // You can close the window if needed
        dispose();
        IMDB.steag_admin--;
    }

    public void main(String[] args) {
        new DeleteUserWindow();
    }
}
class AddUserWindow extends JFrame {

    private JTextField nameField;
    private JComboBox<String> roleComboBox;
    private JTextField usernameField;
    private JTextField roleField;
    private JTextField passwordField;
    private JTextField emailField;
    private JButton addButton;

    public AddUserWindow() {
        //System.out.println(IMDB.steag_admin);
        setTitle("Adauga utilizator nou");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // Create components
        JLabel nameLabel = new JLabel("Nume:");
        JLabel usernameLabel = new JLabel("Username:");
        JLabel emailLabel = new JLabel("Email:");
        JLabel roleLabel = new JLabel("Rol:");
        JLabel passwordLabel = new JLabel("Parola");
        nameField = new JTextField();
        usernameField = new JTextField();
        emailField=new JTextField();
        roleField=new JTextField();
        roleComboBox = new JComboBox<>(new String[]{"Regular", "Contributor", "Admin"});
        passwordField= new JTextField();
        addButton = new JButton("Adauga Utilizator");

        // Set bounds for components
        nameLabel.setBounds(50, 30, 80, 25);
        nameField.setBounds(150, 30, 200, 25);
        usernameLabel.setBounds(50, 65, 80, 25);  // Adjusted Y coordinate for username
        usernameField.setBounds(150, 65, 200, 25);
        emailLabel.setBounds(50, 100, 80, 25);    // Adjusted Y coordinate for email
        emailField.setBounds(150, 100, 200, 25);
        roleLabel.setBounds(50, 135, 80, 25);     // Adjusted Y coordinate for role
        roleComboBox.setBounds(150, 135, 200, 25);
        passwordLabel.setBounds(50, 170, 80, 25); // Adjusted Y coordinate for password
        passwordField.setBounds(150, 170, 200, 25);
        addButton.setBounds(150, 210, 100, 30);   // Adjusted Y coordinate for the button
        Random random = new Random();
        int randomNumber = random.nextInt(6) + 7;
        generateStrongPassword passwordGenerator = new generateStrongPassword();
        // Generate a strong password of length 12
        String strongPassword = passwordGenerator.generateStrongPassword(randomNumber);
        passwordField.setText(strongPassword);

        // Add action listener to the "Add User" button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                IMDB.steag_admin--;
            }
        });
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateUsername();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateUsername();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Plain text components do not fire these events
            }
        });

        // Add components to the frame
        add(nameLabel);
        add(nameField);
        add(usernameLabel);
        add(usernameField);
        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(roleLabel);
        add(roleComboBox);
        add(addButton);

        // Set frame properties
        setVisible(true);
        setLocationRelativeTo(null);
    }

    private void addUser() {
        // Get values from text fields
        String name = nameField.getText();
        String username = usernameField.getText();
        String email=emailField.getText();
        String password = passwordField.getText();
        String tip = (String) roleComboBox.getSelectedItem();
        // Validate input (you can add more validation as needed)
        if (name.isEmpty() || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Va rugam introduceti numele.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Va rugam introduceti emailul.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JButton closebutton =new JButton("Inchidere");
        closebutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IMDB.steag_admin--;
                dispose();
            }
        });
        // Generate a strong password (you can implement your password generation logic)
        // Generate a random number between 10 (inclusive) and 16 (exclusive)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                IMDB.steag_admin--;
            }
        });
        // Create a new user with the provided information
        User newUser = new User();
        newUser.setName(name);
        newUser.setUserType(tip);
        newUser.setUsername(usernameField.getText());
        newUser.setPassword(password);
        newUser.setGender(null);
        newUser.setCountry(null);
        newUser.setEmail(email);
        newUser.setAge(null);
        newUser.setBirthdate(null);
        newUser.setNotifications(null);
        // Add the new user to your user list or perform any necessary actions
        // For now, let's just print the user information
        //newUser.displayinfo();
        IMDB.lista_useri.add(newUser);
        // Close the window
        dispose();
        IMDB.steag_admin--;
    }

    class generateStrongPassword {
        private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
        private static final String NUMBERS = "0123456789";
        private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/";

        private static final String ALL_CHARACTERS = UPPERCASE + LOWERCASE + NUMBERS + SPECIAL_CHARACTERS;

        public String generateStrongPassword(int length) {
            SecureRandom random = new SecureRandom();
            StringBuilder password = new StringBuilder();

            for (int i = 0; i < length; i++) {
                int randomIndex = random.nextInt(ALL_CHARACTERS.length());
                char randomChar = ALL_CHARACTERS.charAt(randomIndex);
                password.append(randomChar);
            }

            return password.toString();
        }
    }
    private void updateUsername() {
        // Automatically update the username field when the name changes
        String name = nameField.getText().trim();
        Random random = new Random();
        int nr = random.nextInt(1000);
        String suggestedUsername = name.toLowerCase().replaceAll("\\s", "_") + "_" + String.valueOf(nr);
        usernameField.setText(suggestedUsername);
    }
    public void main(String[] args) {
        new AddUserWindow();
    }
}