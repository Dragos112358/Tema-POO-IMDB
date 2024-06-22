import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Contributor extends JFrame {
    private List<Request> userRequests=new ArrayList<>();
    private JComboBox<Request> requestComboBox;

    public User find_user(String username)
    {
        for(User user : IMDB.lista_useri)
        {
            if(user.getUsername().equalsIgnoreCase(username))
                return user;
        }
        return null;
    }
    public Contributor() {
        setTitle("Cereri");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel chooseLabel = new JLabel("Alege o cerere:");
        chooseLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(chooseLabel, BorderLayout.NORTH);
        for(User user : IMDB.lista_useri)
        {
            List<Request> cereri= user.getAllSentRequests();
            for(Request request : cereri)
            {
                if(request.getResolverUsername().equalsIgnoreCase(IMDB.user_logat.getUsername()))
                {
                    userRequests.add(request);
                }
            }
        }
        // Populate the combo box with requests from IMDB.lista_cereri_admin
        if (!userRequests.isEmpty()) {
            requestComboBox = new JComboBox<>(userRequests.toArray(new Request[0]));
            requestComboBox.setRenderer(new RequestRenderer());
            panel.add(requestComboBox, BorderLayout.CENTER);
        } else {
            JLabel noRequestsLabel = new JLabel("Nu exista cereri.");
            noRequestsLabel.setHorizontalAlignment(JLabel.CENTER);
            panel.add(noRequestsLabel, BorderLayout.CENTER);
        }

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        JButton acceptButton = new JButton("Accepta");
        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(requestComboBox!=null) {
                    Request selectedRequest = (Request) requestComboBox.getSelectedItem();
                    if (selectedRequest != null) {
                        acceptRequest(selectedRequest);
                    }
                }
            }
        });

        JButton rejectButton = new JButton("Refuza");
        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(requestComboBox!=null) {
                    Request selectedRequest = (Request) requestComboBox.getSelectedItem();
                    if (selectedRequest != null) {
                        rejectRequest(selectedRequest);
                    }
                }
            }
        });

        JButton cancelButton = new JButton("Anuleaza");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(panel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RequestsHolder());
    }

    private class RequestRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Request) {
                value = ((Request) value).getProblemDescription();
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    private void acceptRequest(Request request) {
        String username = request.getCreatorUsername();
        User user = find_user(username);
        user.adauga_notificare("Cererea dumneavoastra a fost acceptata.");

        if (request.getType().equalsIgnoreCase("MOVIE_ISSUE")) {
            // For ReviewRequest, use AddReviewStrategy
            ExperienceStrategy strategy = new ResolveIssueStrategy();
            int experiencePoints = strategy.calculateExperience();
            user.addExperience(experiencePoints);
        } else if (request.getType().equalsIgnoreCase("ACTOR_ISSUE")) {
            // For IssueResolutionRequest, use ResolveIssueStrategy
            ExperienceStrategy strategy = new ResolveIssueStrategy();
            int experiencePoints = strategy.calculateExperience();
            user.addExperience(experiencePoints);
        }

        // Remove the accepted request from the userRequests list
        userRequests.remove(request);
        IMDB.user_logat.removeReceivedRequest(request);
        User user1 =IMDB.findUserByUsername(request.getCreatorUsername());
        user1.removeSentRequest(request);
        // Update the combo box model
        DefaultComboBoxModel<Request> comboBoxModel = new DefaultComboBoxModel<>(userRequests.toArray(new Request[0]));
        requestComboBox.setModel(comboBoxModel);

        // Dispose the frame or perform other actions
        dispose();
    }
    private void rejectRequest(Request request) {
        String username = request.getCreatorUsername();
        User user=find_user(username);
        user.adauga_notificare("Ne pare rau, cererea dumneavoastra a fost respinsa.");
        dispose();
    }
}

