import java.time.LocalDate;
import java.time.LocalDateTime;

public class Request {
    // Enum pentru tipul de cerere
    public enum RequestType {
        PRODUCT_ISSUE,
        ACTOR_NAME,
        DELETE_ACCOUNT,
        OTHER
    }

    private String type;
    private LocalDateTime createdDate;
    private String username;
    private String actorName; // Add this field for ACTOR_ISSUE type
    private String movieTitle; // Add this field for MOVIE_ISSUE type
    private String to;
    private String description;

    // Constructor
    public Request(String type, LocalDateTime createdDate, String username,
                   String actorName, String movieTitle, String to, String description) {
        this.type = type;
        this.createdDate = createdDate;
        this.username = username;
        this.actorName = actorName;
        this.movieTitle = movieTitle;
        this.to = to;
        this.description = description;
    }

    // Metode de acces pentru a obține și seta valorile atributelor
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public void addactorname(String actor)
    {
        this.actorName=actor;
    }
    public String getactorname()
    {
        return actorName;
    }
    public void addmovie(String movie)
    {
        this.movieTitle=movie;
    }
    public String getmovie()
    {
        return movieTitle;
    }
    public LocalDateTime getCreationDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getTitle() {
        return movieTitle;
    }

    public void setTitle(String title) {
        this.movieTitle = title;
    }

    public String getProblemDescription() {
        return description;
    }

    public void setProblemDescription(String problemDescription) {
        this.description = problemDescription;
    }

    public String getCreatorUsername() {
        return username;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.username = creatorUsername;
    }

    public String getResolverUsername() {
        return to;
    }

    public void setResolverUsername(String resolverUsername) {
        this.to = resolverUsername;
    }
    public void display_info()
    {
        System.out.println("Type: " + getType());
        System.out.println("Username: " + getCreatorUsername());
        System.out.println("Date: " + getCreationDate());
        System.out.println("To : " + getResolverUsername());
        System.out.println("Descriere: " + getProblemDescription());
        if(this.movieTitle!=null)
            System.out.println("Titlu film: " + getmovie());
        if(this.actorName!=null)
            System.out.println("Nume actor: " + getactorname());
        System.out.println();
    }
}

