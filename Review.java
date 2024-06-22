import java.util.ArrayList;
import java.util.List;

public class Review {
    public String username;
    public String rating;
    public String comment;
    public static List<Review> reviewfilme = new ArrayList<Review>();

    public Review(String username, String rating, String comment) {
        this.username = username;
        this.rating = rating;
        this.comment = comment;
    }
    public static List<Review> getReviews() {
        return reviewfilme;
    }

    public void setReviews(java.util.List<Review> reviewfilme) {
        this.reviewfilme = reviewfilme;
    }
    public String getUsername() {
        return this.username;
    }

    public String getRating() {
        return rating;
    }

    public String getComment() {
        return this.comment;
    }

    // Setter methods for Review attributes (if needed)
    // For example:
    public void setUsername(String username) {
        this.username = username;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
