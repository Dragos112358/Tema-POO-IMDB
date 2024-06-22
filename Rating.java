public class Rating {
    private String username;
    private int score;
    private String comments;

    // Constructor
    public Rating(String username, String score, String comments) {
        this.username = username;

        // Check if the score is a valid integer
        try {
            this.score = Math.max(1, Math.min(10, Integer.parseInt(score)));
        } catch (NumberFormatException e) {
            this.score = 8;  // Set a default score of 8
        }
        this.comments=comments;
    }
    public String getComments()
    {
        return this.comments;
    }


    // Metode de acces pentru a obține și seta valorile atributelor
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return this.score;
    }

    // Asigurăm că scorul este între 1 și 10
    public void setScore(int score) {
        this.score = Math.max(1, Math.min(10, score));
    }


    public void setComments(String comments) {
        this.comments = comments;
    }

    public double getValue() {
        return score;
    }
    public User getUser()
    {
        for(User user : IMDB.lista_useri)
        {
            if(user.getUsername().equalsIgnoreCase(username))
            {
                return user;
            }
        }
        return null;
    }
}

