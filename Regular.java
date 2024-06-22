import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Regular extends User {
    // Constructor
    public static void add_rating(Movie movie, Rating rating) {
        List<Rating> ratings = movie.getReview();
        ratings.add(rating);
        movie.setReview(ratings); // Use setReview to update the list of reviews
    }

    public void sterge_rating(Movie movie, Rating rating) {
        List<Rating> rating2 = movie.getReview();
        rating2.remove(rating);
        movie.addReview(rating2);
    }

    public static class ReviewWindow extends JFrame {
        private JComboBox<String> movieComboBox;
        private JList<String> reviewsList;
        private JButton addReviewButton;
        private JButton deleteReviewButton;

        private Movie selectedMovie; // Added field to store the selected movie
        private Series selectedSeries; // Added field to store the selected series
        private int selectedMovieIndex = -1;
        private String selectedMovieTitle;

        public ReviewWindow() {
            setTitle("Manager recenzii");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            initUI();

            // Make the window visible
            setVisible(true);
        }

        private void initUI() {
            // Create a panel for the content
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BorderLayout());

            // Add components to the content panel
            reviewsList = new JList<>();

            // Create a combo box for selecting movies
            movieComboBox = new JComboBox<>(getMovieTitles());
            movieComboBox.addActionListener(e -> chooseMovieAction());

            // Create buttons
            addReviewButton = new JButton("Adauga Recenzie");
            deleteReviewButton = new JButton("Sterge Recenzie");

            // Add action listeners to buttons
            addReviewButton.addActionListener(e -> performAddReviewAction());
            deleteReviewButton.addActionListener(e -> performDeleteReviewAction());

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    dispose();
                    IMDB.steag_regular--;
                }
            });

            // Create a panel for the buttons in the south
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonsPanel.add(addReviewButton);
            buttonsPanel.add(deleteReviewButton);

            // Add components to the content panel
            contentPanel.add(movieComboBox, BorderLayout.NORTH);
            contentPanel.add(new JScrollPane(reviewsList), BorderLayout.CENTER);
            contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

            // Add the content panel to the JFrame
            getContentPane().add(contentPanel);
        }

        private boolean isSelectedItemMovie() {
            return selectedMovie != null || selectedSeries != null;
        }

        private void chooseMovieAction() {
            selectedMovieTitle = (String) movieComboBox.getSelectedItem();
            if (selectedMovieTitle != null) {
                Production foundProduction = findMovieByTitle(selectedMovieTitle);
                if (foundProduction != null) {
                    if (foundProduction instanceof Movie) {
                        selectedMovie = (Movie) foundProduction; // Cast only if it's a Movie
                        selectedSeries = null; // Reset selected series
                        selectedMovieIndex = movieComboBox.getSelectedIndex();
                        updateReviewsList();
                    } else if (foundProduction instanceof Series) {
                        selectedSeries = (Series) foundProduction; // Set selected series
                        selectedMovie = null; // Reset selected movie
                        selectedMovieIndex = -1;
                        updateReviewsListForSeries(selectedSeries);
                    }
                }
            }
        }

        private void deleteReviewForSeries(Series series) {
            String selectedReviewText = reviewsList.getSelectedValue();
            if (selectedReviewText != null) {
                String[] parts = selectedReviewText.split(" - ");
                String commentsToDelete = parts[1].trim();

                if (commentsToDelete != null) {
                    List<Rating> reviews = series.get_Review_production();
                    Iterator<Rating> iterator = reviews.iterator();

                    while (iterator.hasNext()) {
                        Rating review = iterator.next();
                        // Assuming Rating class has a unique identifier named 'id'
                        if (review.getComments().equalsIgnoreCase(commentsToDelete) &&
                                review.getUsername().equalsIgnoreCase(IMDB.user_logat.getUsername())) {
                            iterator.remove();
                            series.deleteReviewFromSeries(review); // Call the new method to delete the review from the series
                            IMDB.user_logat.delete_productii_evaluate(series.series_get_title());
                            updateReviewsListForSeries(series);
                            return; // Exit the method after removing the first matching review
                        }
                    }

                    updateReviewsListForSeries(series);
                } else {
                    JOptionPane.showMessageDialog(this, "Va rugam alegeti o recenzie pentru a o sterge.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Va rugam alegeti o recenzie pentru a o sterge.");
            }
        }
        private void updateReviewsListForSeries(Series selectedSeries) {
            DefaultListModel<String> model = new DefaultListModel<>();

            List<Rating> reviewsCopy = new ArrayList<>(selectedSeries.get_Review_production());

            for (Rating rating : reviewsCopy) {
                if (rating.getUsername().trim().equalsIgnoreCase(IMDB.user_logat.getUsername())) {
                    model.addElement(rating.getUsername() + ": " + rating.getScore() + "/10 - " + rating.getComments());
                }
            }

            reviewsList.setModel(model);
            IMDB.user_logat.add_productii_evaluate(selectedSeries.series_get_title());
        }
        private void updateReviewsList() {
            DefaultListModel<String> model = new DefaultListModel<>();

            // Check if it's a movie or series and update reviews accordingly
            if (selectedMovie != null) {
                List<Rating> reviewsCopy = new ArrayList<>(selectedMovie.getReview());

                for (Rating rating : reviewsCopy) {
                    // Check if the review is from the current user
                    if (rating.getUsername().trim().equalsIgnoreCase(IMDB.user_logat.getUsername())) {
                        model.addElement(rating.getUsername() + ": " + rating.getScore() + "/10 - " + rating.getComments());
                    }
                }
            } else if (selectedSeries != null) {
                updateReviewsListForSeries(selectedSeries);
            }

            // Update the reviews list with the new model
            reviewsList.setModel(model);
            if (selectedMovieIndex != -1 && selectedMovieIndex < movieComboBox.getItemCount()) {
                movieComboBox.setSelectedItem(selectedMovieTitle);
            }
        }

        private void performAddReviewAction() {
            if (selectedMovie != null || selectedSeries != null) {
                String username = IMDB.user_logat.getUsername();

                // Check if the user has already reviewed the selected movie or series
                if (hasUserReviewed(username)) {
                    JOptionPane.showMessageDialog(this, "Ati acordat deja recenzie acestei productii");
                } else {
                    String scoreStr = JOptionPane.showInputDialog("Acordati o nota (1-10):");
                    String score = scoreStr;
                    String comments = JOptionPane.showInputDialog("Introduceti un comentariu: ");
                    if (scoreStr != null || comments != null) {
                        Rating newRating = new Rating(username, score, comments);
                        if (selectedMovie != null) {
                            selectedMovie.add_un_review(newRating);
                            double newrating = selectedMovie.calculate_rating_movie();
                            selectedMovie.setRating(String.valueOf(newrating));
                            updateReviewsList();
                            updateComboBox();
                            IMDB.user_logat.add_productii_evaluate(selectedMovie.gettitle());
                            ExperienceStrategy strategy = new AddReviewStrategy();
                            int experiencePoints = strategy.calculateExperience();
                            IMDB.user_logat.addExperience(experiencePoints);
                        } else if (selectedSeries != null) {
                            // Assuming Series has an addReview method
                            selectedSeries.series_adauga_recenzie(newRating);
                            IMDB.user_logat.add_productii_evaluate(selectedSeries.series_get_title());
                            updateReviewsList(); // Update the reviews list for the series
                            updateComboBox(); // Update the JComboBox after adding a review
                            double newrating = selectedSeries.calculate_rating_series();
                            selectedSeries.setRating(String.valueOf(newrating));
                            ExperienceStrategy strategy = new AddReviewStrategy();
                            int experiencePoints = strategy.calculateExperience();
                            IMDB.user_logat.addExperience(experiencePoints);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Va rugam sa alegeti un film sau serial!");
            }
        }
        private boolean hasUserReviewed(String username) {
            if (selectedMovie != null) {
                // Check if the user has already reviewed the selected movie
                List<Rating> movieReviews = selectedMovie.getReview();
                for (Rating review : movieReviews) {
                    if (review.getUsername().equalsIgnoreCase(username)) {
                        return true;
                    }
                }
            } else if (selectedSeries != null) {
                // Check if the user has already reviewed the selected series
                List<Rating> seriesReviews = selectedSeries.get_Review_production();
                for (Rating review : seriesReviews) {
                    if (review.getUsername().equalsIgnoreCase(username)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private void performDeleteReviewAction() {
            if (isSelectedItemMovie()) {
                if (selectedMovie != null) {
                    // Delete review for movie
                    deleteReview(selectedMovie);
                    double newrating = selectedMovie.calculate_rating_movie();
                    selectedMovie.setRating(String.valueOf(newrating));
                    IMDB.user_logat.add_productii_evaluate(selectedMovie.gettitle());
                } else if (selectedSeries != null) {
                    // Delete review for series
                    // Implement the logic based on your requirements
                    deleteReviewForSeries(selectedSeries);
                    double newrating = selectedSeries.calculate_rating_series();
                    selectedSeries.setRating(String.valueOf(newrating));
                    IMDB.user_logat.add_productii_evaluate(selectedSeries.series_get_title());

                }
            } else {
                JOptionPane.showMessageDialog(this, "Va rugam sa alegeti un film sau serial");
            }
        }

        private void deleteReview(Movie movie) {
            String selectedReviewText = reviewsList.getSelectedValue();
            if (selectedReviewText != null) {
                String[] parts = selectedReviewText.split(" - ");
                String commentsToDelete = parts[1].trim(); // Extract comments from the selected item

                if (commentsToDelete != null && !commentsToDelete.isEmpty()) {
                    List<Rating> reviews = movie.getReview();
                    Iterator<Rating> iterator = reviews.iterator();

                    while (iterator.hasNext()) {
                        Rating review = iterator.next();
                        // Assuming Rating class has a unique identifier named 'reviewId'
                        if (review.getComments().equalsIgnoreCase(commentsToDelete) &&
                                review.getUsername().equalsIgnoreCase(IMDB.user_logat.getUsername())) {
                            iterator.remove();
                            break; // Break out of the loop after removing the first matching review
                        }
                    }
                    double newrating = selectedMovie.calculate_rating_movie();
                    selectedMovie.setRating(String.valueOf(newrating));
                    updateReviewsList();
                    updateComboBox(); // Update the JComboBox after removing the review
                } else {
                    JOptionPane.showMessageDialog(this, "Va rugam alegeti o recenzie pentru a o sterge.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Va rugam alegeti o recenzie pentru a o sterge.");
            }
        }

        private String[] getMovieTitles() {
            // Provide an array of movie titles from your data source
            // For demonstration, a static array is used

            List<String> movieTitles = new ArrayList<>();  // Replace with actual movie titles
            for (Movie movie : IMDB.lista_filme) {
                String nume = movie.get_title();
                movieTitles.add(nume);
            }
            for (Series series : IMDB.lista_seriale) {
                String nume = series.series_get_title();
                movieTitles.add(nume);
            }
            return movieTitles.toArray(new String[0]);
        }

        public static Production findMovieByTitle(String title) {
            // Search both movies and series for the given title
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
            return null;  // Movie or series not found
        }

        private void updateComboBox() {
            String[] movieTitles = getMovieTitles();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(movieTitles);
            movieComboBox.setModel(model);

            // Keep the selected item if it is still present in the new model
            if (selectedMovieTitle != null && model.getIndexOf(selectedMovieTitle) != -1) {
                movieComboBox.setSelectedItem(selectedMovieTitle);
            }
        }

        public void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                // For demonstration purposes, show the JFrame when running the main method
                new ReviewWindow();
            });
        }
    }
}
