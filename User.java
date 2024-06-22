import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
interface Observer {
    void update(String notification);
}

// Subject interface
interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String notification);
}
class UserNotificationHandler implements Observer {
    private User user;

    public UserNotificationHandler(User user) {
        this.user = user;
    }
    @Override
    public void update(String notification) {
        List<String> lista=this.user.getNotification();
        if(!lista.contains(notification))
        {
            this.user.adauga_notificare(notification);
        }
    }
}

public class User<T extends Comparable<T>> implements Subject{
    private String username;
    private int experience;
    private String password;
    private LocalDateTime birthdate;
    private String name;
    private String country;
    private String age;
    private String gender;
    private String email;
    private String userType;
    private String favouriteProduction;
    private String favouriteActors;
    private String productionsContribution;
    private String actorsContribution;
    private String notification;
    private List<String> favorite=new ArrayList<>();
    private List<Actor> recenzii_actor=new ArrayList<>();

    public List<Actor> getRecenzii_actor()
    {
        return this.recenzii_actor;
    }
    public void addRecenzii_Actor(Actor actor)
    {
        recenzii_actor.add(actor);
    }
    public void stergeRecenzii_Actor(Actor actor)
    {
        recenzii_actor.remove(actor);
    }
    private List<Request> cereri_primite = new ArrayList<>();
    private List<Request> cereri_trimise = new ArrayList<>();
    public void addReceivedRequest(Request request) {
        cereri_primite.add(request);
    }

    // Add a request to the sent requests list
    public void addSentRequest(Request request) {
        cereri_trimise.add(request);
    }

    // Remove a received request
    public void removeReceivedRequest(Request request) {
        cereri_primite.remove(request);
    }

    // Remove a sent request
    public void removeSentRequest(Request request) {
        cereri_trimise.remove(request);
    }

    // Get all received requests
    public List<Request> getAllReceivedRequests() {
        return cereri_primite;
    }

    // Get all sent requests
    public List<Request> getAllSentRequests() {
        return cereri_trimise;
    }
    private SortedSet<Actor> actori_preferati = new TreeSet<>();
    public SortedSet<Actor> get_actori_preferati()
    {
        return this.actori_preferati;
    }
    SortedSet<Actor> contributie_actori = new TreeSet<>();
    private SortedSet<Production> productii_preferate = new TreeSet<>();
    private SortedSet<Production> contributie_productii = new TreeSet<>();

    public SortedSet<Production> get_contributie_productii()
    {
        return this.contributie_productii;
    }
    private List<String> notificatii = new ArrayList<>();
    private Information information; // Added Information instance

    private List<String> productii_adaugate=new ArrayList<>();
    private List<String> productii_evaluate = new ArrayList<>();
    public void add_productii_evaluate(String string)
    {
        this.productii_evaluate.add(string);
    }
    public void sterge_contributie_actori(Actor actor)
    {
        this.contributie_actori.remove(actor);
    }
    public void elimina_contributie_productie(String name)
    {
        Production prod=IMDB.findProductionByName(name);
        if(name==null)
            return;
        this.contributie_productii.remove(prod);
    }
    public SortedSet<Actor> get_contributie_actori()
    {
        return this.contributie_actori;
    }
    public void adauga_contributie_actori(Actor actor)
    {
        this.contributie_actori.add(actor);
    }
    public void delete_productii_evaluate(String string)
    {
        this.productii_evaluate.remove(string);
    }
    public List<String> get_productii_evaluate()
    {
        return this.productii_evaluate;
    }
    public void adauga_productii(String nume) {
        this.productii_adaugate.add(nume);
    }
    public void adauga_productii2(String nume)
    {
        Production prod = IMDB.findProductionByName(nume);
        this.contributie_productii.add(prod);
    }
    public void elimina_productii(String nume)
    {
        this.productii_adaugate.remove(nume);
    }
    public List<String> da_productii()
    {
        return this.productii_adaugate;
    }

    @Override
    public void addObserver(Observer observer) {

        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String notification) {
        for (Observer observer : observers) {
            observer.update(notification);
        }
    }

    // Inner Information class
    private class Information {
        private Credentials credentials;
        private String name;
        private String country;
        private String age;
        private String gender;
        private LocalDateTime birthdate;

        public Information(Credentials credentials, String name, String country, String age, String gender,
                           LocalDateTime birthdate) {
            this.credentials = credentials;
            this.name = name;
            this.country = country;
            this.age = age;
            this.gender = gender;
            this.birthdate = birthdate;
        }

        // Information class getters and setters
        public Credentials getCredentials() {
            return credentials;
        }

        public void setCredentials(Credentials credentials) {
            this.credentials = credentials;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public LocalDateTime getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(LocalDateTime birthdate) {
            this.birthdate = birthdate;
        }
    }

    // Inner Credentials class
    private class Credentials {
        private String email;
        private String password;

        public Credentials(String email, String password) {
            this.email = email;
            this.password = password;
        }

        // Credentials class getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // Getters and Setters for Information class
    public Information getInformation() {
        return information;
    }

    public void setInformation(Information information) {
        this.information = information;
    }
    private List<Observer> observers=new ArrayList<>();
    // Constructor
    public User() {
    }
    public void adauga_notificare(String notificare)
    {
        this.notificatii.add(notificare);
    }
    public void convert_notification(String input)
    {
        String[] parts = input.split("(?=(Filmul|Serialul))");
        for(int i = 0; i < parts.length;i++)
        {
            String notification=parts[i];
            notificatii.add(notification);
            //System.out.println(nameact);
        }

    }
    public void convert_actori_preferati(String input)
    {
        String[] parts = input.split(",");
        for(int i = 0; i < parts.length;i++)
        {
            String nameact=parts[i].replace("\"","");
            Actor newActor = new Actor( nameact);
            actori_preferati.add(newActor);
            //System.out.println(nameact);
        }
    }
    public void convert_contributie_Actori(String input)
    {
        String[] parts = input.split(",");
        for(int i = 0; i < parts.length;i++)
        {
            String nameact=parts[i].replace("\"","");;
            Actor newActor = new Actor( nameact);
            contributie_actori.add(newActor);
            //System.out.println(nameact);
        }
    }
    public void convert_contributie_Productii(String input) {
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String nameprod = matcher.group(1);
            Production newProd = new Production() {
                @Override
                public void displayInfo() {
                    // Implementation for displaying production info
                }
            }; // Replace ConcreteProduction with your actual implementation
            newProd.setTitle(nameprod);
            contributie_productii.add(newProd);
        }
    }
    public void convert_productii_preferate(String input) {
        String[] parts = input.split("\"");
        for (int i = 1; i < parts.length; i += 2) {
            String nameprod = parts[i];
            Production newProd = new Production() {
                @Override
                public void displayInfo() {
                    // Implementation for displaying production info
                }
            }; // Replace Production with your actual implementation
            newProd.setTitle(nameprod);
            productii_preferate.add(newProd);
        }
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public List<Request> getCereri_primite()
    {
        return this.cereri_primite;
    }
    public void addRequest(Request request)
    {
        this.cereri_trimise.add(request);

    }
    public void deleteRequest(Request request)
    {
        this.cereri_trimise.remove(request);
    }
    public void addRequestprimite(Request request)
    {
        this.cereri_primite.add(request);
    }
    public void setExperience(String experience) {
        try {
            // Try to parse the String to an int
            this.experience = Integer.parseInt(experience);
        } catch (NumberFormatException e) {
        }
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public SortedSet<Production> getProductiiPreferate() {
        return productii_preferate;
    }

    // Setter method
    public void setProductiiPreferate(SortedSet<Production> productii_preferate) {
        this.productii_preferate = productii_preferate;
    }
    public void setfavorite(List<String> favorite)
    {
        this.favorite=favorite;
    }
    public List<String> getFavorite()
    {
        return this.favorite;
    }
    public void sterge_un_favorit(String string)
    {
        this.favorite.remove(string);
    }
    public void adauga_un_favorit(String string)
    {
        this.favorite.add(string);
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthdate(LocalDateTime birthdate) {
        this.birthdate = birthdate;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setFavouriteProduction(String favouriteProduction) {
        this.favouriteProduction = favouriteProduction;
    }

    public void setFavouriteActors(String favouriteActors) {
        this.favouriteActors = favouriteActors;
    }

    public void setProductionsContribution(String productionsContribution) {
        this.productionsContribution = productionsContribution;
    }

    public void setActorsContribution(String actorsContribution) {
        this.actorsContribution = actorsContribution;
    }

    public void setNotifications(String notifications) {
        this.notification = notifications;
    }
    public String getUsername() {
        return username;
    }

    public int getExperience() {
        return experience;
    }
    public void addExperience(int exp) {
        this.experience=this.experience+exp;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getName() {
        return this.name;
    }

    public String getCountry() {
        return this.country;
    }

    public String getAge() {
        return this.age;
    }

    public String getGender() {
        return this.gender;
    }

    public LocalDateTime getBirthdate() {
        return this.birthdate;
    }

    public String getUserType() {
        return userType;
    }

    public String getFavouriteProduction() {
        return favouriteProduction;
    }

    public String getFavouriteActors() {
        return favouriteActors;
    }

    public String getProductionsContribution() {
        return productionsContribution;
    }

    public String getActorsContribution() {
        return actorsContribution;
    }

    public List<String> getNotification() {
        return notificatii;
    }
    public void displayinfo_mare()
    {
        System.out.println("username " + username);
        System.out.println("experience " + experience);
        System.out.println("email " + this.email);
        System.out.println("password " + this.password);
        System.out.println("name " + this.name);
        System.out.println("country " + this.country);
        System.out.println("age" + this.age);
        System.out.println("gender " + this.gender);
        System.out.println("birthdate " + this.birthdate);
        System.out.println("usertype " + userType);
        //System.out.println("Favorite Productions: " + favouriteProduction);
        //System.out.println("Favorite Actors: " + favouriteActors);
        System.out.println("Actori preferati: ");
        for(Actor actor: actori_preferati)
        {
            System.out.println(actor.getNume().trim());
        }
        System.out.println("Productii preferate: ");
        for(Production prod : productii_preferate)
        {
            System.out.println(prod.get_title());
        }
        //System.out.println("Productions Contribution: " + productionsContribution);
        System.out.println("Contributie productii");
        for(Production prod : contributie_productii)
        {
            System.out.println(prod.get_title());
        }
        System.out.println("Contributie actori");
        for(Actor actor: contributie_actori)
        {
            System.out.println(actor.getNume().trim());
        }
        System.out.println("Notificatii");
        if(notificatii!=null) {
            for (String not : notificatii) {
                System.out.println(not);
            }
        }
        System.out.println("__________");
    }
    public void displayinfo2()
    {
        for(Actor actor: contributie_actori)
        {
            System.out.println(actor.getNume().trim());
        }
    }
    public List<Request> getListaCereriTrimise() {
        return this.cereri_trimise;
    }
    public void stergeListaCereriTrimise()
    {
        this.cereri_trimise.clear();
    }
    public void stergeListaCereriPrimite()
    {
        this.cereri_primite.clear();
    }

    public void addCerereTrimisa(Request request) {
        this.cereri_trimise.add(request);
    }

    public List<Request> getListaCereriPrimite() {
        return cereri_primite;
    }

    public void addCererePrimite(Request request) {
        this.cereri_primite.add(request);
    }
    public void displayInfo() {
        System.out.println("Username: " + username);
        System.out.println("Experience: " + experience);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
        System.out.println("Name: " + name);
        System.out.println("Country: " + country);
        System.out.println("Age: " + age);
        System.out.println("Gender: " + gender);
        System.out.println("Birthdate: " + birthdate);
        System.out.println("User Type: " + userType);

        System.out.println("Favorite Actors: ");
        for (Actor actor : actori_preferati) {
            System.out.println("  - " + actor.getNume().trim());
        }

        System.out.println("Favorite Productions: ");
        for (Production prod : productii_preferate) {
            System.out.println("  - " + prod.get_title());
        }

        System.out.println("Contributie Actor: ");
        for (Actor actor : contributie_actori) {
            System.out.println("  - " + actor.getNume().trim());
        }

        System.out.println("Contributie Productii: ");
        for (Production prod : contributie_productii) {
            System.out.println("  - " + prod.get_title());
        }

        System.out.println("Notifications: ");
        for (String not : notificatii) {
            System.out.println("  - " + not);
        }

        System.out.println("__________");
    }
}
