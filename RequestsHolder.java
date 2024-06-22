import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RequestsHolder extends JFrame {

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
    public RequestsHolder() {
        setTitle("Cereri");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel chooseLabel = new JLabel("Alege o cerere:");
        chooseLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(chooseLabel, BorderLayout.NORTH);

        // Populate the combo box with requests from IMDB.lista_cereri_admin
        List<Request> adminRequests = IMDB.lista_cereri_admin;
        requestComboBox = new JComboBox<>(adminRequests.toArray(new Request[0]));
        requestComboBox.setRenderer(new RequestRenderer());
        panel.add(requestComboBox, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        JButton acceptButton = new JButton("Accepta");
        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Request selectedRequest = (Request) requestComboBox.getSelectedItem();
                if (selectedRequest != null) {
                    acceptRequest(selectedRequest);
                }
            }
        });

        JButton rejectButton = new JButton("Refuza");
        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Request selectedRequest = (Request) requestComboBox.getSelectedItem();
                if (selectedRequest != null) {
                    rejectRequest(selectedRequest);
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
        IMDB.lista_cereri_admin.remove(request);
        String username = request.getCreatorUsername();
        User user=find_user(username);
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
        dispose();
    }

    private void rejectRequest(Request request) {
        String username = request.getCreatorUsername();
        User user=find_user(username);
        user.adauga_notificare("Ne pare rau, cererea dumneavoastra a fost respinsa.");
        dispose();
    }
}
