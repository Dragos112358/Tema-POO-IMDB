public class Episode {
    private String episodeName;
    private int duration;

    // Constructor
    public Episode(String episodeName) {
        this.episodeName = episodeName;
        this.duration = duration;
    }

    // Metode de acces pentru a obține și seta valorile atributelor
    public String getEpisodeName() {
        return episodeName;
    }

    public void setEpisodeName(String episodeName) {
        this.episodeName = episodeName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
