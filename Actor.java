import java.util.ArrayList;
import java.util.List;

// Clasa Actor
public class Actor implements Comparable<Actor>{
    // Numele actorului
    private String nume;
    private List<Rating> ratings=new ArrayList<>();

    // Listă cu perechi de tipul Name:Type
    private List<NameTypePair> roluri;

    // Biografie personală
    private String biografie;
    private int nr_roluri;
    // Constructor pentru Actor
    public Actor(String nume) {
        this.nume = nume;
        this.roluri = new ArrayList<>();
    }
    public void Actor2(String nume) {
        this.nume = nume;
        this.roluri = new ArrayList<>();
    }

    // Getter pentru nume
    public String getNume() {
        return this.nume;
    }

    // Getter pentru roluri
    public List<NameTypePair> getRoluri() {
        return new ArrayList<>(roluri); // Returnează o copie a listei pentru a evita modificări neașteptate
    }
    public void addRole(String name, String type) {
        if (roluri == null) {
            roluri = new ArrayList<>();
        }
        roluri.add(new NameTypePair(name, type));
    }
    public void setName(String name)
    {
        this.nume=name;
    }

    // Getter pentru biografie
    public String getBiografie() {
        return biografie;
    }
    public void addBiografie(String biografie)
    {
        this.biografie=biografie;
    }
    public String getName() {
        return this.nume;
    }
    public void addRoles(ArrayList<NameTypePair> nameTypePairs) {
        for (NameTypePair newRole : nameTypePairs) {
            // Check if the new role already exists in the list
            boolean isDuplicate = false;
            for (NameTypePair existingRole : roluri) {
                if (existingRole.equals(newRole)) {
                    isDuplicate = true;
                    break;
                }
            }

            // If not a duplicate, add the new role to the list
            if (!isDuplicate) {
                roluri.add(newRole);
            }
        }
    }
    public  ArrayList<NameTypePair> get_roles()
    {
        return (ArrayList<NameTypePair>) this.roluri;
    }

    // Clasa internă pentru perechea Name:Type
    public static class NameTypePair {
        private String name;
        private String type;

        public NameTypePair(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
        public void setName(String name) {
            this.name = name;
        }

        // Setter for type
        public void setType(String type) {
            this.type = type;
        }
    }
    public int get_number_roles()
    {
        return this.nr_roluri;
    }
    public void set_number_roles(int k)
    {
        this.nr_roluri=k;
    }
    @Override
    public int compareTo(Actor otherActor) {
        int compareByRoles = Integer.compare(this.get_number_roles(), otherActor.get_number_roles());

        if (compareByRoles == 0) {
            return this.getName().compareToIgnoreCase(otherActor.getName());
        } else {
            return compareByRoles;
        }
    }
    public List<Rating> get_ratings() {
        return new ArrayList<>(ratings); // Return a copy to avoid unintended modifications
    }

    // Add a rating to the actor
    public void add_rating(Rating rating) {
        if (ratings == null) {
            ratings = new ArrayList<>();
        }
        ratings.add(rating);
    }
    public void delete_rating(Rating rating)
    {
        ratings.remove(rating);
    }
}

