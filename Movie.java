import java.util.ArrayList;
import java.util.List;

public class Movie extends Production {
    private String duration;
    private String releaseYear;
    private String directori;
    private String genre;
    private String Actori_Movie;
    private String type;
    private String plot;
    private List<String> reviewfilme = new ArrayList<String>();
    private String username;
    private String rating;
    private String comment;
    private List<Rating> recenzii;
    // Constructor
    public Movie(String title, String type) {
        this.title = title;
        this.type = type;
        this.reviewfilme=new ArrayList<String>();
        this.recenzii = new ArrayList<>();
    }

    // Metode de acces pentru a obține și seta valorile atributelor
    public String getDuration() {
        if(this.duration==null)
            this.duration="10 minutes";
        return this.duration;
    }
    public void setDuration(String Duration)
    {
        this.duration=Duration;
    }

    public String getReleaseYear() {
        if(this.releaseYear==null)
            this.releaseYear="1900";
        if(this.releaseYear.equalsIgnoreCase("Nu exista"))
            this.releaseYear="1900";
        return this.releaseYear;
    }
    public void setReleaseYear(String an_lansare)
    {
        this.releaseYear=an_lansare;
    }
    public String getPlot() {
        if(plot==null)
            this.plot="";
        return this.plot;
    }
    public void setPlot(String plot)
    {
        this.plot=plot;
    }

    public String gettitle() {
        if(this.title==null)
            this.title="";
        return title;
    }

    public void settitle(String title) {
        this.title = title;
    }

    public void movie_add_directors(String directori)
    {
        this.directori=directori;
    }
    public void movie_add_actori(String actori_Movie)
    {
        this.Actori_Movie=actori_Movie;
    }
    public void movie_add_genre(String genre)
    {
        this.genre=genre;
    }
    public String getRating() {
        if(this.rating==null)
            this.rating="6.0";
        return this.rating;
    }
    public double getRatingAsDouble() {
        double result = 0.0; // Default value if the conversion fails

        try {
            // Assuming 'rating' is a String variable containing the rating value
            result = Double.parseDouble(rating);
        } catch (NumberFormatException e) {
            // Handle the exception if the conversion fails
            //System.err.println("Error parsing rating: " + e.getMessage());
        }

        return result;
    }

    // Setter for rating
    public void setRating(String rating) {
        this.rating = rating;
    }
    public String movie_get_directors()
    {
        if(this.directori==null)
            this.directori="";
        return this.directori;
    }
    public String movie_get_actori()
    {
        if(this.Actori_Movie==null)
            this.Actori_Movie="";
        return this.Actori_Movie;
    }
    public String movie_get_genre()
    {
        if(this.genre==null)
            this.genre="";
        return this.genre;
    }
    public String movie_get_type()
    {
        if(this.type==null)
            this.type="Movie";
        return this.type;
    }
    public double calculate_rating_movie()
    {
        double suma=0;
        int i=0;
        if(this.recenzii!=null) {
            for (Rating rating : this.recenzii) {
                int nr = rating.getScore();
                suma = suma + nr;
                i++;
            }
            if (i != 0) {
                double average = suma / i;
                // Round to two decimal places
                return Math.round(average * 100.0) / 100.0;
            }
        }
        return 5;
    }
    public void addReview(List<Rating> review) {
        this.recenzii=review;
    }
    public void setReview(List<Rating> reviews2) {
        this.recenzii = reviews2;
    }
    public List<Rating> getReview()
    {
        return this.recenzii;
    }
    public void add_un_review(Rating newRating) {
        // Ensure that reviews is never null
        if (recenzii == null) {
            recenzii = new ArrayList<>();
        }
        recenzii.add(newRating);
    }
    @Override
    public void displayInfo() {
            System.out.println("Movie Title: " + gettitle());
            System.out.println("Type: " + movie_get_type());
            System.out.println("Release Year: " + getReleaseYear());
            System.out.println("Duration: " + getDuration());
            System.out.println("Directors: " + movie_get_directors());
            System.out.println("Actors: " + movie_get_actori());
            System.out.println("Genre: " + movie_get_genre());
            System.out.println("Plot: " + getPlot());

            // Display Reviews
            System.out.println("Reviews:");
            for (Rating rating : getReview()) {
                System.out.println("Username: " + rating.getUsername());
                System.out.println("Rating: " + rating.getScore());
                System.out.println("Comment: " + rating.getComments());
                System.out.println("------");
            }
    }
    public int getDurationAsInt() {
        try {
            String durationValue = duration.replaceAll("[^\\d]", ""); // Remove non-digit characters
            return Integer.parseInt(durationValue);
        } catch (NumberFormatException e) {
            // Handle the exception if the conversion fails
            System.err.println("Error parsing duration: " + e.getMessage());
            return 0; // Default value if the conversion fails
        }
    }
}