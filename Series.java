import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Series extends Production {
    private String releaseYear;
    private String rating;
    private String title;
    private String type;
    private List<Rating> recenzii;
    private String directors;
    private String Actors;
    private String Genre;
    //private Map<String, List<Episode>> episodesMap = new HashMap<>();
    private int numberOfSeasons;
    public String[] sezon = new String[100];
    private int contor; //pt fiecare sezon
    //private Map<String, List<Episode>> seasons;
    private List<List<Episode>> seasons;

    // Constructor
    public Series(String type, String release_year, String title) {
        super();
        this.releaseYear = release_year;
        this.type = type;
        this.title = title;
        this.contor = 0;
    }
    // Access methods for getting and setting attribute values
    public String getReleaseYear() {
        return releaseYear;
    }
    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }
    public void setPlot(String plot) {
        this.plotDescription = plot;
    }
    public String getPlot()
    {
        return this.plotDescription;
    }
    // Inner class for Season
    // ... existing methods
    public void setNrSezoane(String sezoane) {
        this.numberOfSeasons = Integer.parseInt(sezoane);
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRating() {
        return this.rating;
    }
    public double calculate_rating_series()
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

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public void series_add_sezoane(String sezon) {
        //System.out.println(sezon);
        this.sezon[contor] = sezon;
        //System.out.println("contor " + contor);
        contor++;
    }

    public String series_get_sezoane(int i) {
        return this.sezon[i];
    }
    public void series_set_sezoane(int i ,String sezon2)
    {
        this.sezon[i]=sezon2;
    }

    // Implementation of the abstract method from the Production class
    public String[] getReview() {
        return this.sezon;
    }
    public List<Rating> get_Review_production() {
        // Ensure that reviewList is never null
        if (recenzii == null) {
            recenzii = new ArrayList<>();
        }
        return recenzii;
    }
    public int get_number_reviews() {
        System.out.println(this.recenzii.size());
        return this.recenzii.size();

    }

    @Override
    public void displayInfo() {
        // Provide implementation for displaying information about the series
        // You can use System.out.println() or any other method you prefer
        System.out.println("Informatii serial");
        System.out.println("Titlu: " + this.title); // Assuming Production has a getTitle() method
        System.out.println("Rating mediu: " + this.rating);
        System.out.println("An lansare: " + releaseYear);
        System.out.println("Numar sezoane: " + numberOfSeasons);
        System.out.println("Intriga serial : " + plotDescription);
        System.out.println("Seasons:");
        for (int i = 0; i < contor; i++) {
            System.out.println(sezon[i]);
        }
    }

    public String displayInfoOnWindow() {
        // Create a StringBuilder to store the series information
        StringBuilder seriesInfo = new StringBuilder();

        // Append information about the series to the StringBuilder
        seriesInfo.append("Informatii serial\n");
        seriesInfo.append("Title: ").append(this.title).append("\n");
        seriesInfo.append("Release Year: ").append(releaseYear).append("\n");
        seriesInfo.append("Number of Seasons: ").append(numberOfSeasons).append("\n");
        seriesInfo.append("Plot : ").append(plotDescription).append("\n");
        seriesInfo.append("Rating : ").append(rating).append("\n");
        seriesInfo.append("Seasons:\n");
        for (int i = 0; i < contor; i++) {
            seriesInfo.append(sezon[i]).append("\n");
        }

        // Return the modified seriesInfo string
        return seriesInfo.toString();
    }

    public String series_get_title() {
        return this.title;
    }
    public void series_set_title(String title)
    {
        this.title=title;
    }

    public String getDirectors() {
        return directors;
    }

    public String getActors() {
        return Actors;
    }

    public String getGenre() {
        return Genre;
    }

    // Setter methods
    public void setDirectors(String directors) {
        this.directors = directors;
    }

    public void setActors(String actors) {
        this.Actors = actors;
    }

    public void setGenre(String genre) {
        this.Genre = genre;
    }

    public void series_set_recenzii(List<Rating> recenzii) {
        this.recenzii = recenzii;
    }
    public List<Rating> series_get_recenzii()
    {
        return this.recenzii;
    }
    public void series_adauga_recenzie(Rating recenzie)
    {

        // Add the Rating object to the list of reviews
        recenzii.add(recenzie);
    }
    public void deleteReviewFromSeries(Rating review) {
        List<Rating> reviews = get_Review_production();
        Iterator<Rating> iterator = reviews.iterator();

        while (iterator.hasNext()) {
            Rating existingReview = iterator.next();
            if (existingReview.equals(review)) {
                iterator.remove();
                break;
            }
        }
    }
    public void addSeason(List<Episode> season) {
        seasons.add(season);
    }

    public void addEpisodeToSeason(int seasonIndex, Episode episode) {
        if (seasonIndex >= 0 && seasonIndex < seasons.size()) {
            seasons.get(seasonIndex).add(episode);
        } else {
            System.out.println("Invalid season index");
        }
    }

}
