import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.SortedSet;

public class RequestManager extends JFrame {
    public static User user_primeste=new User();
    private static RequestManager instance;
    private User user_logat = IMDB.user_logat;
    public CereriTrimiseWindow cereriTrimiseWindow;
    public CereriPrimiteInterface cereriPrimiteWindow;

    public class RequestRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Request) {
                value = ((Request) value).getProblemDescription();
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    public enum RequestTypes {
        DELETE_ACCOUNT,
        ACTOR_ISSUE,
        MOVIE_ISSUE,
        OTHERS
    }

    public RequestManager() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Request Manager");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel messageLabel = new JLabel("Manager de cereri");
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        getContentPane().add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton cereriTrimiseButton = new JButton("Cereri trimise");
        cereriTrimiseButton.addActionListener(e -> {
            dispose();
            IMDB.stegulet--;
            if (cereriTrimiseWindow == null) {
                cereriTrimiseWindow = new CereriTrimiseWindow(this);
            } else {
                cereriTrimiseWindow.setVisible(true);
            }
        });
        if(!IMDB.user_logat.getUserType().equalsIgnoreCase("Admin")) {
            addCenteredButton(buttonPanel, cereriTrimiseButton, gbc);
        }

        JButton cereriPrimiteButton = new JButton("Cereri primite");
        cereriPrimiteButton.addActionListener(e -> {
            dispose();
            IMDB.stegulet--;
            if (cereriPrimiteWindow == null) {
                cereriPrimiteWindow = new CereriPrimiteInterface(this);
            } else {
                cereriPrimiteWindow.setVisible(true);
            }
        });
        addCenteredButton(buttonPanel, cereriPrimiteButton, gbc);

        JButton scrie_cerere = new JButton("Cerere noua");
        scrie_cerere.addActionListener(e -> {
            dispose();
            IMDB.stegulet--;
            new CreateRequestWindow(this);
        });
        if(!IMDB.user_logat.getUserType().equalsIgnoreCase("Admin")){
            addCenteredButton(buttonPanel, scrie_cerere, gbc);
        }

        JButton retragere_cerere = new JButton("Sterge cerere");
        retragere_cerere.addActionListener(e -> {
            dispose();
            IMDB.stegulet--;
            new StergereRequestWindow(this);
            // Handle delete request
        });
        addCenteredButton(buttonPanel, retragere_cerere, gbc);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> {
            dispose();
            IMDB.stegulet--;
            instance = null;
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                IMDB.stegulet--;
                instance = null;
            }
        });
        addCenteredButton(buttonPanel, closeButton, gbc);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addCenteredButton(JPanel panel, JButton button, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(button, gbc);
    }

    public void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (instance == null) {
                instance = new RequestManager();
            }
        });
    }

    public class CreateRequestWindow extends JFrame {

        private JTextField actorNameField;
        private JTextField descriptionField;
        private JTextField movieTitleField;

        private RequestManager requestManager;

        JComboBox<RequestTypes> typeComboBox = new JComboBox<>(RequestTypes.values());

        public CreateRequestWindow(RequestManager requestManager) {
            this.requestManager = requestManager;

            setTitle("Create Request");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel(new GridLayout(8, 2));

            panel.add(new JLabel("Tip:"));
            panel.add(typeComboBox);

            panel.add(new JLabel("Data crearii (YYYY-MM-DDTHH:mm:ss):"));
            String pattern = "yyyy-MM-dd'T'HH:mm:ss";
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            String formattedDateTime = currentDateTime.format(formatter);
            JTextField createdDateField = new JTextField(formattedDateTime);
            panel.add(createdDateField);
            panel.add(new JLabel("Username:"));
            JTextField usernameField = new JTextField(user_logat.getUsername());
            panel.add(usernameField);

            panel.add(new JLabel("Nume actor:"));
            actorNameField = new JTextField();
            panel.add(actorNameField);

            panel.add(new JLabel("Catre:"));
            JTextField toField = new JTextField();
            panel.add(toField);

            panel.add(new JLabel("Descriere:"));
            descriptionField = new JTextField();
            panel.add(descriptionField);

            panel.add(new JLabel("Titlu film/serial:"));
            movieTitleField = new JTextField();
            panel.add(movieTitleField);
            typeComboBox.addActionListener(e -> updateRecommendation(toField));

            // Add DocumentListener to the actorNameField
            actorNameField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateRecommendation(toField);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateRecommendation(toField);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateRecommendation(toField);
                }
            });

            // Add DocumentListener to the movieTitleField
            movieTitleField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateRecommendation(toField);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateRecommendation(toField);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateRecommendation(toField);
                }
            });

            JButton createButton = new JButton("Trimiteti cererea");
            createButton.addActionListener(e -> {
                createRequest(typeComboBox, createdDateField, usernameField, toField);
                if (requestManager != null) {
                    requestManager.dispose();
                }
            });

            panel.add(createButton);

            getContentPane().add(panel);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        private void updateRecommendation(JTextField toField) {
            String actorName = actorNameField.getText();
            String movieName = movieTitleField.getText();

            // Reset toField initially
            toField.setText("");
            RequestTypes selectedType = (RequestTypes) typeComboBox.getSelectedItem();
            if (selectedType == RequestTypes.DELETE_ACCOUNT || selectedType == RequestTypes.OTHERS) {
                toField.setText("ADMIN");
                for(User user : IMDB.lista_useri) {
                    if (user.getUserType().equalsIgnoreCase("ADMIN")) {
                        user_primeste = user;
                        //user_primeste.displayinfo();
                        return;
                    }
                }
            }
            for (User user : IMDB.lista_useri) {
                Actor actor1 = new Actor("nume");
                Movie movie1 = new Movie("Movie", "Movie");
                Series series1 = new Series("Series", "1000", "titlu");
                String actorsContribution = user.getActorsContribution();
                String productionsContribution = user.getProductionsContribution();

                for (Actor actor : IMDB.actorList) {
                    if (actor.getNume().equalsIgnoreCase(actorName)) {
                        actor1.setName(actorName);
                    }
                }
                SortedSet<Actor> cont=user.get_contributie_actori();
                if (actorsContribution != null && cont.contains(actor1)) {
                    toField.setText(user.getUsername());
                    user_primeste = user;
                    return;  // Update only once for the first match
                }
                for (Movie movie : IMDB.lista_filme) {
                    if (movie.get_title().equalsIgnoreCase(movieName)) {
                        movie1.settitle(movieName);
                    }
                }
                for (Series series : IMDB.lista_seriale) {
                    if (series.series_get_title().equalsIgnoreCase(movieName)) {
                        toField.setText(user.getUsername());
                        return;
                    }
                }
                SortedSet <Production> nou =user.get_contributie_productii();
                if (productionsContribution != null && nou.contains((Production) movie1)) {
                    toField.setText(user.getUsername());
                    user_primeste = user;
                    return;  // Update only once for the first match
                }
            }
            toField.setText("ADMIN");
            for(User user : IMDB.lista_useri)
            {
                if(user.getUserType().equalsIgnoreCase("ADMIN"))
                {
                    user_primeste=user;
                }
            }
        }

        private void createRequest(JComboBox<RequestTypes> typeComboBox, JTextField createdDateField,
                                   JTextField usernameField, JTextField toField) {
            String type = typeComboBox.getSelectedItem().toString();
            String createdDateString = createdDateField.getText();

            if (!createdDateString.isEmpty()) {
                String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                try {
                    LocalDateTime createdDate = LocalDateTime.parse(createdDateString, DateTimeFormatter.ofPattern(pattern));

                    String username = usernameField.getText();
                    String actorName = actorNameField.getText();
                    String to = toField.getText();
                    String description = descriptionField.getText();

                    if (description.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please enter a description for your request.");
                        return;  // Don't create the request if the description is empty
                    }

                    String movieTitle = movieTitleField.getText();

                    // Ensure user_logat is not null
                    if (user_logat != null) {
                        Request request = new Request(type, createdDate, username, actorName, movieTitle, to, description);
                        user_logat.addRequest(request);
                        if (user_primeste != null && user_primeste.getUserType().equalsIgnoreCase("Admin")) {
                            IMDB.lista_cereri_admin.add(request);
                            for (User user : IMDB.lista_useri) {
                                if (user.getUserType().equalsIgnoreCase("ADMIN")) {
                                    user.adauga_notificare("Userul " + user_logat.getUsername() + " v-a trimis o cerere de tip" + type);
                                    UserNotificationHandler userObserver = new UserNotificationHandler(user);
                                    user.addObserver(userObserver);
                                    user.adauga_notificare("Userul " + user_logat.getUsername() + " v-a trimis o cerere de tip" + type);
                                    user.removeObserver(userObserver);
                                }

                            }
                        } else if (user_primeste != null) {
                            user_primeste.addRequestprimite(request);
                            UserNotificationHandler userObserver = new UserNotificationHandler(user_primeste);
                            user_primeste.addObserver(userObserver);
                            user_primeste.adauga_notificare("Userul " + user_logat.getUsername() + " v-a trimis o cerere de tip" + type);
                            user_primeste.removeObserver(userObserver);
                            user_primeste.adauga_notificare("Userul " + user_logat.getUsername() + "v-a trimis o cerere de tip " + type);
                        }

                        JOptionPane.showMessageDialog(this, "Cererea dumneavoastra a fost trimisa!");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "User not logged in. Please log in and try again.");
                    }
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DDTHH:mm:ss");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a valid date.");
            }
        }
    }
    class CereriTrimiseWindow extends JFrame {
        private JTextArea textArea;
        private RequestManager requestManager;

        public CereriTrimiseWindow(RequestManager requestManager) {
            this.requestManager = requestManager;
            initializeUI();
        }

        private void initializeUI() {
            setTitle("Cereri Trimise");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            textArea = new JTextArea();
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);

            JLabel messageLabel = new JLabel("Lista cereri trimise");
            messageLabel.setHorizontalAlignment(JLabel.CENTER);
            getContentPane().add(messageLabel, BorderLayout.NORTH);
            getContentPane().add(scrollPane, BorderLayout.CENTER);

            JButton closeButton = new JButton("Inchidere");
            closeButton.addActionListener(e -> dispose());

            JPanel buttonPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            addCenteredButton(buttonPanel, closeButton, gbc);

            getContentPane().add(buttonPanel, BorderLayout.SOUTH);
            setLocationRelativeTo(null);
            setVisible(true);

            updateTextArea();
        }

        public void updateTextArea() {
            textArea.setText("");
            StringBuilder sb = new StringBuilder();
            User user= user_logat;
            List<Request> cereri_trimise2=user_logat.getListaCereriTrimise();
            List<Request> cereri_primite2=user.getListaCereriPrimite();
            for (Request request : cereri_trimise2) {
                sb.append("Tip: ").append(request.getType()).append("\n");
                sb.append("Data crearii: ").append(request.getCreationDate()).append("\n");
                sb.append("Username: ").append(request.getCreatorUsername()).append("\n");
                if (!request.getactorname().equals("Nu exista")) {
                    sb.append("Nume actor: ").append(request.getactorname()).append("\n");
                }
                sb.append("Catre: ").append(request.getResolverUsername()).append("\n");
                if (!request.getTitle().equals("Nu exista")) {
                    sb.append("Nume film: ").append(request.getTitle()).append("\n");
                }
                if (!request.getProblemDescription().equals("Nu exista")) {
                    sb.append("Descriere: ").append(request.getProblemDescription()).append("\n");
                }
                sb.append("\n");
            }
            if (cereri_trimise2.isEmpty()) {
                sb.append("Nu ati trimis nicio cerere pana acum!").append("\n");
            }
            textArea.setText(sb.toString());
        }

        private void addCenteredButton(JPanel panel, JButton button, GridBagConstraints gbc) {
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.anchor = GridBagConstraints.CENTER;
            panel.add(button, gbc);
        }
    }
    class CereriPrimiteInterface extends JFrame {
        private JTextArea textArea;
        private RequestManager requestManager;

        public CereriPrimiteInterface(RequestManager requestManager) {
            this.requestManager = requestManager;
            initializeUI();
        }

        private void initializeUI() {
            setTitle("Cereri Primite");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            textArea = new JTextArea();
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);

            JLabel messageLabel = new JLabel("Lista de cereri primite");
            messageLabel.setHorizontalAlignment(JLabel.CENTER);
            getContentPane().add(messageLabel, BorderLayout.NORTH);
            getContentPane().add(scrollPane, BorderLayout.CENTER);

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dispose());

            JPanel buttonPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            addCenteredButton(buttonPanel, closeButton, gbc);

            getContentPane().add(buttonPanel, BorderLayout.SOUTH);
            setLocationRelativeTo(null);
            setVisible(true);

            updateTextArea();
        }

        public void updateTextArea() {
            textArea.setText("");
            StringBuilder sb = new StringBuilder();
            User user=user_logat;
            java.util.List<Request> cereri_trimise2=user.getListaCereriTrimise();
            List<Request> cereri_primite2=user.getListaCereriPrimite();
            for (Request request : cereri_primite2) {
                sb.append("Tip: ").append(request.getType()).append("\n");
                sb.append("Data crearii: ").append(request.getCreationDate()).append("\n");
                sb.append("Username: ").append(request.getCreatorUsername()).append("\n");
                if (!request.getactorname().equals("Nu exista")) {
                    sb.append("Nume actor: ").append(request.getactorname()).append("\n");
                }
                if (!request.getmovie().equals("Nu exista")) {
                    sb.append("Nume film").append(request.getmovie()).append("\n");
                }
                sb.append("Catre: ").append(request.getResolverUsername()).append("\n");
                if (!request.getProblemDescription().equals("Nu exista")) {
                    sb.append("Descriere: ").append(request.getProblemDescription()).append("\n");
                }
                sb.append("\n");
            }
            if (cereri_primite2.isEmpty()) {
                sb.append("Nu ati primit nicio cerere pana acum!").append("\n");
            }
            textArea.setText(sb.toString());
        }
    }
    public class StergereRequestWindow extends JFrame {

        private RequestManager requestManager;
        private JComboBox<Request> requestComboBox;

        public StergereRequestWindow(RequestManager requestManager) {
            this.requestManager = requestManager;

            setTitle("Stergere Cerere");
            setSize(300, 150);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel(new GridLayout(3, 1));

            JLabel chooseLabel = new JLabel("Alegeti cererea pe care vreti sa o stergeti:");
            chooseLabel.setHorizontalAlignment(JLabel.CENTER);
            panel.add(chooseLabel);
            requestComboBox = new JComboBox<>();
            requestComboBox.setRenderer(new RequestRenderer());
            User user= user_logat;
            java.util.List<Request> cereri_trimise2=user.getListaCereriTrimise();
            List<Request> cereri_primite2=user.getListaCereriPrimite();
            for (Request request : cereri_trimise2) {
                requestComboBox.addItem(request);
            }

            panel.add(requestComboBox);
            JButton deleteButton = new JButton("Stergere");
            deleteButton.addActionListener(e -> {
                stergeRequest();
                dispose();
            });
            panel.add(deleteButton);

            getContentPane().add(panel);
            setLocationRelativeTo(requestManager); // Center the window relative to the main RequestManager window
            setVisible(true);

            // Add a window listener to handle window close events
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // Handle window close events (e.g., cancel deletion)
                    dispose();
                }
            });
        }

        private void stergeRequest() {
            // Get the selected request from the JComboBox
            Request selectedRequest = (Request) requestComboBox.getSelectedItem();
            if (selectedRequest != null) {
                // Implement the logic to delete the selected request
                requestManager.sterge_Request(selectedRequest);
            }
        }
    }
    public void sterge_Request(Request request) {
        User user= user_logat;
        java.util.List<Request> cereri_trimise2=user.getListaCereriTrimise();
        List<Request> cereri_primite2=user.getListaCereriPrimite();
        // Assuming IMDB.lista_cereri_trimise is your list of requests
        if (cereri_trimise2.contains(request)) {
            cereri_trimise2.remove(request);
        }
        IMDB.user_logat.deleteRequest(request);
        String nume=request.getResolverUsername();
        User user2=IMDB.findUserByUsername(nume);
        if(user2!=null) {
            user2.deleteRequest(request);
        }
        if(IMDB.lista_cereri_admin.contains(request))
            IMDB.lista_cereri_admin.remove(request);
        // You might want to update any UI elements or data structures after deletion
        // For example, updateTextArea() in CereriTrimiseWindow to refresh the displayed requests
        if (cereriTrimiseWindow != null) {
            cereriTrimiseWindow.updateTextArea();
        }
    }

}
