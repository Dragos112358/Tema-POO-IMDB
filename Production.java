import java.util.List;

public abstract class Production implements Comparable<Production> {

    protected String title;
    protected List<String> directors;
    protected List<String> actors;
    protected List<Genre> genres;
    protected List<Rating> ratings;
    protected String plotDescription;
    protected double overallRating;
    protected String type;

    public Production() {
        this.title = title;
    }

    public abstract void displayInfo();

    public int compareTo(Production otherProduction) {
        // Compare productions based on their titles (case-insensitive)
        return this.title.compareToIgnoreCase(otherProduction.get_title());
    }

    public String get_title() {
        return this.title;
    }

    public List<Rating> get_Review_production() {
        // Implement this method for Movie
        return ratings; // Assuming you store ratings in the 'ratings' field
    }

    public String setTitle(String title) {
        this.title = title;
        return title;
    }

    public String getType() {
        return this.type;
    }
    public void setType(String type)
    {
        this.type=type;
    }
}

