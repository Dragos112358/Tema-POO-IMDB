import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

public class Staffinterface extends JFrame {
    static class ActorRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Actor) {
                value = ((Actor) value).getNume();
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
    public Staffinterface() {
        setTitle("Interfata actori");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        JButton addActorButton = new JButton("Adauga Actor");
        JButton deleteActorButton = new JButton("Sterge Actor");
        JButton editActorButton = new JButton("Editeaza Actor");
        JButton close =new JButton("inchidere");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement your logic for Add Actor button
                //JOptionPane.showMessageDialog(null, "Add Actor Button Clicked");
                dispose();
                IMDB.staffint--;
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                IMDB.staffint--;
            }
        });
        addActorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement your logic for Add Actor button
                //JOptionPane.showMessageDialog(null, "Add Actor Button Clicked");
                dispose();
                new AddActorWindow();
            }
        });

        deleteActorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new DeleteActor();
            }
        });

        editActorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement your logic for Edit Actor button
                //JOptionPane.showMessageDialog(null, "Edit Actor Button Clicked");
                dispose();
                new EditActorWindow();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(addActorButton, gbc);

        gbc.gridy = 1;
        mainPanel.add(deleteActorButton, gbc);

        gbc.gridy = 2;
        mainPanel.add(editActorButton, gbc);

        gbc.gridy = 3;
        mainPanel.add(close, gbc);

        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Staffinterface();
            }
        });
    }
}
class DeleteActor extends JFrame {
    private JComboBox<Actor> actorComboBox;

    public DeleteActor() {
        setTitle("Stergere actori");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel label = new JLabel("Stergere actori");
        label.setBounds(100, 20, 150, 25);

        actorComboBox = new JComboBox<>();
        actorComboBox.setBounds(50, 50, 200, 25);
        actorComboBox.setRenderer(new Staffinterface.ActorRenderer());
        JButton deleteButton = new JButton("Sterge");
        JButton close = new JButton("Inchidere");
        deleteButton.setBounds(100, 100, 100, 30);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement your logic for deleting the selected actor
                Actor selectedActor = (Actor) actorComboBox.getSelectedItem();
                String name=selectedActor.getName();
                Actor actor2=IMDB.findActorByName(name);
                if(IMDB.user_logat.getUserType().equalsIgnoreCase("Contributor"))
                {
                    IMDB.user_logat.sterge_contributie_actori(selectedActor);
                }
                IMDB.actorList.remove(actor2);
                dispose();
                IMDB.staffint--;
            }
        });
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add your logic for handling "Stergere useri" button click
                dispose();
                IMDB.staffint--;
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                IMDB.staffint--;
            }
        });
        if(IMDB.user_logat.getUserType().equalsIgnoreCase("Admin")) {
            for (Actor actor : IMDB.actorList) {
                actorComboBox.addItem(actor);
            }
        }
        else {
            SortedSet<Actor> act = IMDB.user_logat.get_contributie_actori();
            for(Actor actor : act)
            {
                actorComboBox.addItem(actor);
            }
        }
        add(label);
        add(actorComboBox);
        add(deleteButton);
        add(close);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DeleteActor();
            }
        });
    }
}
class AddActorWindow extends JFrame {
    private JTextField nameField;
    private JTextArea biographyArea;
    private List<Actor.NameTypePair> roluri;
    private List<RolePanel> rolePanels;
    private JButton addButton;
    private JButton addRoleButton;

    public AddActorWindow() {
        initializeUI();
    }
    private void initializeUI() {
        setTitle("Adaugare Actor");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                IMDB.staffint--;
            }
        });

        JLabel nameLabel = createLabel("Nume:", 20);
        nameField = createTextField(120, 20, 200, 25);

        JLabel biographyLabel = createLabel("Biografie:", 60);
        biographyArea = createTextArea(120, 60, 250, 100);

        JLabel rolesLabel = createLabel("Roluri:", 180);

        roluri = new ArrayList<>();
        rolePanels = new ArrayList<>();

        addButton = createButton("Adauga Actor", 150, 300, 150, 30, e -> addActor());
        addRoleButton = createButton("Adauga Rol", 150, 250, 150, 30, e -> addRole());

        addComponents(nameLabel, nameField, biographyLabel, biographyArea, rolesLabel, addRoleButton, addButton);

        addRolePanel();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JLabel createLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(20, y, 80, 25);
        return label;
    }

    private JTextField createTextField(int x, int y, int width, int height) {
        JTextField textField = new JTextField();
        textField.setBounds(x, y, width, height);
        return textField;
    }

    private JTextArea createTextArea(int x, int y, int width, int height) {
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(x, y, width, height);
        return textArea;
    }

    private JButton createButton(String text, int x, int y, int width, int height, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.addActionListener(listener);
        return button;
    }

    private void addComponents(JComponent... components) {
        for (JComponent component : components) {
            add(component);
        }
    }

    private void addRolePanel() {
        RolePanel rolePanel = new RolePanel();
        rolePanel.setBounds(120, 180 + rolePanels.size() * 70, 250, 60);
        rolePanels.add(rolePanel);
        add(rolePanel);
        revalidate();
        repaint();
    }

    private void addRole() {
        RolePanel lastRolePanel = rolePanels.get(rolePanels.size() - 1);
        roluri.add(new Actor.NameTypePair(lastRolePanel.getTitle(), lastRolePanel.getType()));
        lastRolePanel.clearFields();
        remove(lastRolePanel);
        rolePanels.remove(lastRolePanel);
        addRolePanel();
    }

    private void addActor() {
        String name = nameField.getText();
        String biography = biographyArea.getText();

        if (name.isEmpty()) {
            showMessage("Va rugam introduceti numele actorului.", "Error");
            return;
        }

        Actor newActor = new Actor(name);
        newActor.addBiografie(biography);

        for (Actor.NameTypePair pair : roluri) {
            newActor.addRole(pair.getName(), pair.getType());
        }
        IMDB.actorList.add(newActor);
        IMDB.user_logat.adauga_contributie_actori(newActor);
        dispose();
        IMDB.staffint--;
    }

    private void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddActorWindow());
    }

    private class RolePanel extends JPanel {
        private JTextField titleField;
        private JTextField typeField;

        public RolePanel() {
            setLayout(null);
            add(createLabel("Titlu:", 0));
            titleField = createTextField(90, 0, 150, 25);
            add(createLabel("Tip:", 30));
            typeField = createTextField(90, 30, 150, 25);
            add(titleField);
            add(typeField);
        }

        public String getTitle() {
            return titleField.getText();
        }

        public String getType() {
            return typeField.getText();
        }

        public void clearFields() {
            titleField.setText("");
            typeField.setText("");
        }
    }
}
class EditActorWindow extends JFrame {
    private JTextField nameField;
    private JTextArea biographyArea;
    private List<Actor.NameTypePair> roles;
    private JList<String> rolesList;
    private JComboBox<String> actorComboBox;

    public EditActorWindow() {
        this.roles = new ArrayList<>();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Editare Actor");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                IMDB.staffint--;
            }
        });

        actorComboBox = new JComboBox<>();
        actorComboBox.setBounds(120, 10, 200, 25);
        updateActorComboBox();

        actorComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Actor selectedActor = getSelectedActor();
                if (selectedActor != null) {
                    roles = selectedActor.get_roles();
                    populateFields(selectedActor);
                }
            }
        });

        JLabel nameLabel = createLabel("Nume:", 50);
        nameField = createTextField(120, 50, 200, 25);

        JLabel biographyLabel = createLabel("Biografie:", 90);
        biographyArea = createTextArea();
        JScrollPane biographyScrollPane = new JScrollPane(biographyArea);
        biographyScrollPane.setBounds(120, 90, 350, 100);
        add(biographyScrollPane);

        JLabel rolesLabel = createLabel("Roluri:", 210);

        rolesList = createRolesList();
        JScrollPane rolesListScrollPane = new JScrollPane(rolesList);
        rolesListScrollPane.setBounds(120, 210, 350, 100);
        add(rolesListScrollPane);

        JButton addRoleButton = createButton("Adauga Rol", 120, 320, 120, 30, e -> addRole());
        JButton deleteRoleButton = createButton("Sterge Rol", 350, 320, 120, 30, e -> deleteRole());
        JButton saveButton = createButton("Salveaza", 120, 360, 120, 30, e -> saveChanges());
        JButton closeButton = new JButton("Inchidere");
        add(closeButton);
        closeButton.setBounds(350, 360, 120, 30);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                IMDB.staffint--;
            }
        });

        addComponents(actorComboBox, nameLabel, nameField, biographyLabel, rolesLabel,
                addRoleButton, deleteRoleButton, saveButton, closeButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JLabel createLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(20, y, 80, 25);
        return label;
    }

    private JTextField createTextField(int x, int y, int width, int height) {
        JTextField textField = new JTextField();
        textField.setBounds(x, y, width, height);
        return textField;
    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setBounds(0, 0, 350, 100);
        return textArea;
    }

    private JList<String> createRolesList() {
        DefaultListModel<String> rolesListModel = new DefaultListModel<>();
        JList<String> list = new JList<>(rolesListModel);
        list.setBounds(0, 0, 350, 100);
        return list;
    }

    private JButton createButton(String text, int x, int y, int width, int height, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.addActionListener(listener);
        return button;
    }

    private void addComponents(JComponent... components) {
        for (JComponent component : components) {
            add(component);
        }
    }

    private void updateActorComboBox() {
        for (Actor actor : IMDB.actorList) {
            actorComboBox.addItem(actor.getName());
        }
    }

    private Actor getSelectedActor() {
        String selectedActorName = (String) actorComboBox.getSelectedItem();
        if (selectedActorName != null) {
            for (Actor actor : IMDB.actorList) {
                if (actor.getName().equals(selectedActorName)) {
                    return actor;
                }
            }
        }
        return null;
    }

    private void populateFields(Actor actor) {
        nameField.setText(actor.getName());
        String text= actor.getBiografie();
        String biographyText = actor.getBiografie();
        String wrappedBiography = IMDB.wrapText(biographyText, 60);
        biographyArea.setText(wrappedBiography);
        DefaultListModel<String> rolesListModel = (DefaultListModel<String>) rolesList.getModel();
        rolesListModel.clear();
        for (Actor.NameTypePair pair : roles) {
            rolesListModel.addElement(pair.getName() + " - " + pair.getType());
        }
    }

    private void addRole() {
        String title = JOptionPane.showInputDialog(this, "Introduceti titlul:");
        String type = JOptionPane.showInputDialog(this, "Introduceti tipul productiei:");

        if (title != null && type != null) {
            Actor.NameTypePair newRole = new Actor.NameTypePair(title, type);

            // Check if the role already exists in the list
            if (!roles.contains(newRole)) {
                roles.add(newRole);
                updateRolesList();
            }
        }
    }

    private void deleteRole() {
        int selectedIndex = rolesList.getSelectedIndex();
        if (selectedIndex != -1) {
            roles.remove(selectedIndex);
            updateRolesList();
        }
    }

    private void updateRolesList() {
        DefaultListModel<String> rolesListModel = (DefaultListModel<String>) rolesList.getModel();
        rolesListModel.clear();

        Set<String> uniqueRoles = new HashSet<>();

        for (Actor.NameTypePair pair : roles) {
            String roleRepresentation = pair.getName() + " - " + pair.getType();

            // Check if the role representation is unique before adding it to the list
            if (uniqueRoles.add(roleRepresentation)) {
                rolesListModel.addElement(roleRepresentation);
            }
        }
    }

    private void saveChanges() {
        Actor selectedActor = getSelectedActor();
        if (selectedActor != null) {
            String newName = nameField.getText();
            String newBiography = biographyArea.getText();

            // Perform validation and update actor
            if (!newName.isEmpty()) {
                selectedActor.setName(newName);
                selectedActor.addBiografie(newBiography);
                selectedActor.addRoles(new ArrayList<>(roles));
                selectedActor.set_number_roles(roles.size());
                JOptionPane.showMessageDialog(this, "Schimbari salvate cu succes!", "Succes", JOptionPane.INFORMATION_MESSAGE);
                ExperienceStrategy strategy = new AddActorStrategy();
                int experiencePoints = strategy.calculateExperience();
                IMDB.user_logat.addExperience(experiencePoints);
                dispose();
                IMDB.staffint--;
            } else {
                showMessage("Va rugam introduceti numele actorului.", "Error");
            }
        } else {
            showMessage("Niciun actor selectat.", "Error");
        }
    }

    private void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (!IMDB.actorList.isEmpty()) {
                new EditActorWindow();
            } else {
                System.out.println("Niciun actor disponibil pentru editare.");
            }
        });
    }
}
class ProductionWork extends JFrame {
    public ProductionWork() {
        // Set up the JFrame
        setTitle("Lucru cu productii");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Add components to the content panel
        JLabel label = new JLabel("Aici se editeaza productii.");
        JButton closeButton = new JButton("Close");
        JButton addProductionButton = new JButton("Adauga productie");
        JButton deleteProductionButton = new JButton("Sterge productie");
        JButton editProductionButton = new JButton("Editeaza productie");

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                IMDB.steag_productii--;
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                IMDB.steag_productii--;
            }
        });
        // Add action listeners for the new buttons
        addProductionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AddProductionWindow();
            }
        });

        deleteProductionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new DeleteProductionWindow();
            }
        });

        editProductionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add your logic for "Editeaza productie" button here
                //JOptionPane.showMessageDialog(ProductionWork.this, "Editeaza productie functionality");
                dispose();
                new EditProductionListWindow();
            }
        });

        // Add components to the panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);  // Padding

        gbc.gridy++;
        contentPanel.add(addProductionButton, gbc);

        gbc.gridy++;
        contentPanel.add(deleteProductionButton, gbc);

        gbc.gridy++;
        contentPanel.add(editProductionButton, gbc);

        gbc.gridy++;
        contentPanel.add(closeButton, gbc);

        // Add the content panel to the JFrame
        getContentPane().add(contentPanel);

        setLocationRelativeTo(null);
        setVisible(true);

        // Make the window visible
        setVisible(true);
    }

    public void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create an instance of ProductionWork
            new ProductionWork();
        });
    }
}
class DeleteProductionWindow extends JFrame {
    private JComboBox<String> productionComboBox;
    private JButton deleteButton;

    public DeleteProductionWindow() {
        setTitle("Sterge productie");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();

        setVisible(true);
    }

    private void initUI() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        if(IMDB.user_logat.getUserType().equalsIgnoreCase("Admin")) {
            productionComboBox = new JComboBox<>(getProductionNames());
        }
        else {
            SortedSet<Production> lsita_contributii=IMDB.user_logat.get_contributie_productii();
            List<String> nume_productii=new ArrayList<>();
            for(Object production: lsita_contributii)
            {
                nume_productii.add(((Production)production).get_title());
            }
            productionComboBox = new JComboBox<>(nume_productii.toArray(new String[0]));
        }
        deleteButton = new JButton("Stergere");

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performDeleteAction();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                IMDB.steag_productii--;
            }
        });

        contentPanel.add(productionComboBox, BorderLayout.NORTH);
        contentPanel.add(deleteButton, BorderLayout.CENTER);

        getContentPane().add(contentPanel);
    }

    private void performDeleteAction() {
        String selectedProductionName = (String) productionComboBox.getSelectedItem();
        if (selectedProductionName != null) {
            Production selectedProduction = findProductionByName(selectedProductionName);
            if (selectedProduction != null) {
                // Check if the user is a contributor
                if (IMDB.user_logat.getUserType().equalsIgnoreCase("Contributor")) {
                    IMDB.user_logat.elimina_contributie_productie(selectedProductionName);
                    if (selectedProduction instanceof Movie) {
                        IMDB.lista_filme.remove(selectedProduction);
                    } else if (selectedProduction instanceof Series) {
                        IMDB.lista_seriale.remove(selectedProduction);
                    }
                    refreshContributorComboBox();
                }

                // Implement the logic to delete the selected production
                if (selectedProduction instanceof Movie) {
                    IMDB.lista_filme.remove(selectedProduction);
                } else if (selectedProduction instanceof Series) {
                    IMDB.lista_seriale.remove(selectedProduction);
                }
                IMDB.user_logat.elimina_productii(selectedProductionName);
                JOptionPane.showMessageDialog(this, "Productie stearsa cu succes.");
                dispose(); // Close the window after deleting
                IMDB.steag_productii--;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Va rugam alegeti o productie.");
        }
    }

    private String[] getProductionNames() {
        List<String> productionNames = new ArrayList<>();
        for (Movie movie : IMDB.lista_filme) {
            productionNames.add(movie.get_title());
        }
        for (Series series : IMDB.lista_seriale) {
            productionNames.add(series.series_get_title());
        }
        return productionNames.toArray(new String[0]);
    }

    private Production findProductionByName(String name) {
        for (Movie movie : IMDB.lista_filme) {
            if (movie.get_title().equals(name)) {
                return movie;
            }
        }
        for (Series series : IMDB.lista_seriale) {
            if (series.series_get_title().equals(name)) {
                return series;
            }
        }
        return null;
    }
    private void refreshContributorComboBox() {
        SortedSet<Production> lista_contributii = IMDB.user_logat.get_contributie_productii();
        List<String> nume_productii = new ArrayList<>();
        for (Production production : lista_contributii) {
            nume_productii.add(production.get_title());
        }
        productionComboBox.setModel(new DefaultComboBoxModel<>(nume_productii.toArray(new String[0])));
    }

    public void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DeleteProductionWindow();
        });
    }
}
class EditProductionListWindow extends JFrame {
    private JList<String> productionList;
    private DefaultListModel<String> listModel;
    private JButton editButton;

    public EditProductionListWindow() {
        setTitle("Editeaza lista de productii");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();

        setVisible(true);
    }

    private void initUI() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        productionList = new JList<>(listModel);
        productionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        updateProductionList();

        JScrollPane listScrollPane = new JScrollPane(productionList);

        editButton = new JButton("Editeaza selectatul");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                IMDB.steag_productii--;
            }
        });
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performEditAction();
            }
        });


        contentPanel.add(listScrollPane, BorderLayout.CENTER);
        contentPanel.add(editButton, BorderLayout.SOUTH);

        getContentPane().add(contentPanel);
    }

    private void updateProductionList() {
        listModel.clear();

        for (Movie movie : IMDB.lista_filme) {
            listModel.addElement(movie.get_title());
        }

        for (Series series : IMDB.lista_seriale) {
            listModel.addElement(series.series_get_title());
        }
    }

    private void performEditAction() {
        // Get the selected productions
        int[] selectedIndices = productionList.getSelectedIndices();
        for (int selectedIndex : selectedIndices) {
            String selectedTitle = listModel.getElementAt(selectedIndex);

            // Find the selected production by title
            Production selectedProduction = findProductionByTitle(selectedTitle);
            if (selectedProduction != null) {
                // Open the EditProductionWindow for the selected production
                new EditProductionWindow(selectedProduction);
            }
        }
    }

    private Production findProductionByTitle(String title) {
        for (Movie movie : IMDB.lista_filme) {
            if (movie.get_title().equals(title)) {
                return movie;
            }
        }

        for (Series series : IMDB.lista_seriale) {
            if (series.series_get_title().equals(title)) {
                return series;
            }
        }

        return null;
    }

    public void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EditProductionListWindow();
        });
    }
}
class EditProductionWindow extends JFrame {
    private Object production; // Use Object to accept either Movie or Series

    private JTextField titleField;
    private JTextField directorsField;
    private JTextField actorsField;
    private JTextField genresField;
    private JTextArea plotArea;

    private JButton saveButton;

    public EditProductionWindow(Object production) {
        this.production = production;

        // Set the title based on the type of production
        setTitle(production instanceof Movie ? "Editeaza film" : "Editeaza serial");

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();

        setVisible(true);
    }

    private void initUI() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(6, 2));

        // Initialize fields based on the type of production
        titleField = new JTextField(getTitleValue());
        directorsField = new JTextField(getDirectorsValue());
        actorsField = new JTextField(getActorsValue());
        genresField = new JTextField(getGenresValue());
        plotArea = new JTextArea(getPlotValue());

        saveButton = new JButton("Salveaza modificarile");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSaveAction();
            }
        });

        contentPanel.add(new JLabel("Titlu:"));
        contentPanel.add(titleField);
        contentPanel.add(new JLabel("Directori:"));
        contentPanel.add(directorsField);
        contentPanel.add(new JLabel("Actori:"));
        contentPanel.add(actorsField);
        contentPanel.add(new JLabel("Genuri:"));
        contentPanel.add(genresField);
        contentPanel.add(new JLabel("Intriga productie:"));
        contentPanel.add(new JScrollPane(plotArea));
        contentPanel.add(new JLabel()); // Placeholder for an empty cell
        contentPanel.add(saveButton);

        getContentPane().add(contentPanel);
    }

    private String getTitleValue() {
        if (production instanceof Movie) {
            return ((Movie) production).gettitle();
        } else if (production instanceof Series) {
            return ((Series) production).series_get_title();
        }
        return "";
    }

    private String getDirectorsValue() {
        if (production instanceof Movie) {
            return ((Movie) production).movie_get_directors();
        } else if (production instanceof Series) {
            return ((Series) production).getDirectors();
        }
        return "";
    }

    private String getActorsValue() {
        if (production instanceof Movie) {
            return ((Movie) production).movie_get_actori();
        } else if (production instanceof Series) {
            return ((Series) production).getActors();
        }
        return "";
    }

    private String getGenresValue() {
        if (production instanceof Movie) {
            return ((Movie) production).movie_get_genre();
        } else if (production instanceof Series) {
            return ((Series) production).getGenre();
        }
        return "";
    }

    private String getPlotValue() {
        if (production instanceof Movie) {
            return ((Movie) production).getPlot();
        } else if (production instanceof Series) {
            return ((Series) production).getPlot();
        }
        return "";
    }

    private void performSaveAction() {
        // Update the movie or series with the edited information
        if (production instanceof Movie) {
            Movie movie = (Movie) production;
            movie.settitle(titleField.getText());
            movie.movie_add_directors(directorsField.getText());
            movie.movie_add_actori(actorsField.getText());
            movie.movie_add_genre(genresField.getText());
            movie.setPlot(plotArea.getText());
            ExperienceStrategy strategy = new AddProductionStrategy();
            int experiencePoints = strategy.calculateExperience();
            IMDB.user_logat.addExperience(experiencePoints);
        } else if (production instanceof Series) {
            Series series = (Series) production;
            series.series_set_title(titleField.getText());
            series.setDirectors(directorsField.getText());
            series.setActors(actorsField.getText());
            series.setGenre(genresField.getText());
            series.setPlot(plotArea.getText());
            ExperienceStrategy strategy = new AddProductionStrategy();
            int experiencePoints = strategy.calculateExperience();
            IMDB.user_logat.addExperience(experiencePoints);
        }

        // Update the UI or save the changes to your data structure
        // For simplicity, we'll just print the updated information
        if (production instanceof Movie) {
            ((Movie) production).displayInfo();
        } else if (production instanceof Series) {
            ((Series) production).displayInfo();
        }

        // Optionally, you can notify the main application or update the UI accordingly

        // Close the EditProductionWindow
        dispose();
    }

    public void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        });
    }
}
class AddProductionWindow extends JFrame {
    private JComboBox<String> productionTypeComboBox;
    private JTextField titleField;
    private JTextField directorsField;
    private JTextField actorsField;
    private JTextField genresField;
    private JTextArea plotArea;
    private JLabel seasonsLabel;
    private JTextField seasonsField;
    private JLabel episodesLabel;
    private JTextField episodesField;
    private JButton addButton;
    private JButton addEpisodeButton;
    private JButton nextSeasonButton;
    int nr_sezoane=0;
    int nr_episoade=0;
    StringBuilder sezon= new StringBuilder();
    Series newSeries = new Series("Series", "2022", "titlu");

    public AddProductionWindow() {
        setTitle("Adauga productie");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();

        setVisible(true);
    }

    private void initUI() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(0, 2));

        // Add a JComboBox to allow the user to choose the type of production
        productionTypeComboBox = new JComboBox<>(new String[]{"Film", "Serial"});
        contentPanel.add(new JLabel("Tip productie:"));
        contentPanel.add(productionTypeComboBox);

        titleField = new JTextField();
        directorsField = new JTextField();
        actorsField = new JTextField();
        genresField = new JTextField();
        plotArea = new JTextArea();
        seasonsLabel = new JLabel("Numar sezoane:");
        seasonsField = new JTextField();
        episodesLabel = new JLabel("Episoade:");
        episodesField = new JTextField();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                IMDB.steag_productii--;
            }
        });

        addButton = new JButton("Adauga");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAddAction();
            }
        });

        addEpisodeButton = new JButton("Adauga Episod");
        addEpisodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAddEpisodeAction();
            }
        });

        nextSeasonButton = new JButton("Urmatorul Sezon");
        nextSeasonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performNextSeasonAction();
            }
        });

        contentPanel.add(new JLabel("Titlu:"));
        contentPanel.add(titleField);
        contentPanel.add(new JLabel("Directori:"));
        contentPanel.add(directorsField);
        contentPanel.add(new JLabel("Actori:"));
        contentPanel.add(actorsField);
        contentPanel.add(new JLabel("Genuri:"));
        contentPanel.add(genresField);
        contentPanel.add(new JLabel("Intriga productie:"));
        contentPanel.add(new JScrollPane(plotArea));
        contentPanel.add(seasonsLabel);
        contentPanel.add(seasonsField);
        contentPanel.add(episodesLabel);
        contentPanel.add(episodesField);
        contentPanel.add(addEpisodeButton); // Button for adding episodes
        contentPanel.add(nextSeasonButton); // Button for going to the next season
        contentPanel.add(addButton);

        // Initially hide the fields for seasons and episodes
        seasonsLabel.setVisible(false);
        seasonsField.setVisible(false);
        episodesLabel.setVisible(false);
        episodesField.setVisible(false);
        addEpisodeButton.setVisible(false);
        nextSeasonButton.setVisible(false);

        // Add an action listener to the JComboBox to show/hide the seasons and episodes fields
        productionTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedType = (String) productionTypeComboBox.getSelectedItem();
                boolean isSerial = "Serial".equals(selectedType);

                seasonsLabel.setVisible(isSerial);
                seasonsField.setVisible(isSerial);
                episodesLabel.setVisible(isSerial);
                episodesField.setVisible(isSerial);
                addEpisodeButton.setVisible(isSerial);
                nextSeasonButton.setVisible(isSerial);

                validate();
                repaint();
            }
        });

        getContentPane().add(contentPanel);
    }

    private void performAddAction() {
        String selectedType = (String) productionTypeComboBox.getSelectedItem();

        // Create a new movie or series based on the selected type
        if ("Film".equals(selectedType)) {
            Movie newMovie = new Movie(titleField.getText(), "Movie");
            newMovie.movie_add_directors(directorsField.getText());
            newMovie.movie_add_actori(actorsField.getText());
            newMovie.movie_add_genre(genresField.getText());
            newMovie.setPlot(plotArea.getText());
            newMovie.setRating("5");
            IMDB.lista_filme.add(newMovie);
            IMDB.user_logat.adauga_productii(newMovie.get_title());
            IMDB.user_logat.adauga_productii2(titleField.getText());
        } else if ("Serial".equals(selectedType)) {
            newSeries.setTitle(directorsField.getText());
            newSeries.setDirectors(directorsField.getText());
            newSeries.setActors(actorsField.getText());
            newSeries.setGenre(genresField.getText());
            newSeries.setPlot(plotArea.getText());
            newSeries.setNrSezoane(seasonsField.getText());
            newSeries.setRating("6");
            IMDB.lista_seriale.add(newSeries);
            IMDB.user_logat.adauga_productii(newSeries.series_get_title());
            IMDB.user_logat.adauga_productii2(titleField.getText());
        }

        dispose();
        IMDB.steag_productii--;
    }

    private void performAddEpisodeAction() {
        if (nr_episoade == 0)
            sezon.append("Sezon " + (nr_sezoane + 1) + "\n");
        nr_episoade++;
        sezon.append(episodesField.getText()).append("\n");
        episodesField.setText("");
    }

    private void performNextSeasonAction() {
        String nr = seasonsField.getText();
        int numar = 0;
        try {
            numar = Integer.parseInt(nr);
        } catch (NumberFormatException e) {
        }
        if (nr_sezoane > numar)
        {
            JOptionPane.showMessageDialog(this, "Prea multe sezoane!", "Avertisment", JOptionPane.WARNING_MESSAGE);
        }
        else {
            String sezon2 = sezon.toString();
            newSeries.series_add_sezoane(sezon2);
            sezon = new StringBuilder("\n");
            nr_episoade=0;
            nr_sezoane++;
            nr = seasonsField.getText();
            numar = 0;
            try {
                numar = Integer.parseInt(nr);
            } catch (NumberFormatException e) {
            }

        }


    }

    public void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddProductionWindow());
    }
}