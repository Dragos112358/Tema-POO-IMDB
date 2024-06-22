//biblioteci parsare fisiere JSON
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//biblioteci pentru lucru cu interfete grafice
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class IMDB {
    private static IMDB instance = new IMDB(); // Volatile for safe publication

    private IMDB() { //singleton pattern
    }

    // Method for getting the singleton instance, using lazy initialization with double-checked locking
    public static IMDB getInstance() {
        return instance;
    }

    public int ok=0;
    public List<User> regularUsers;
    public List<User> contributors;
    public List<User> admins;
    public static List<Request> lista_cereri_admin=new ArrayList<>();
    public static User user_logat;
    public static int steag_admin=0; //steag pt instanta de admin
    public static int steag_productii=0; //steag pt instanta de productionwork din StaffInterface
    public static int steag_favorite=0;
    public static int steag_regular=0;
    public static int staffint=0; // steag pt instanta Staff Interface
    public String tipuserlogat; //tipul userului care s-a logat
    public static List<Actor> actorList = new ArrayList<>(); //lista de actori
    public static List<Request> requestList=new ArrayList<>(); //lista de cereri pentru admini
    public static List<Movie> lista_filme=new ArrayList<>(); //lista de filme
    public static List<User> lista_useri=new ArrayList<>(); //lista useri
    public static List<Series> lista_seriale=new ArrayList<>(); //lista seriale
    public static int stegulet=0; //numara de cate ori deschid RequestManager
    public static void dispose() {
        System.exit(0);
    }
    //private Movie film;
    public void run() {
        // Încărcați datele parsate din fișierele JSON
        datedinActors(); //aici creez actorList
        parseazaProductii(); //aici creez lista_filme si lista_seriale
        parseazaAccounts(); //Aici creez lista_useri
        citire_cereri(); //aici completez lista de cereri din requests.json
        new MovieAppUI();
        startApplicationFlow();
    }
    public static class SimilarityCalculator {
        private static final double SIMILARITY_THRESHOLD = 0.6;
        //aici calculez similaritatea a 2 stringuri pentru parte de cautare a unei productii sau actor
        public static double calculateSimilarity(String s1, String s2) {
            // Convert strings to sets of characters
            HashSet<Character> set1 = new HashSet<>();
            HashSet<Character> set2 = new HashSet<>();
            //creez 2 seturi de caractere carora le calculez ulterior intersectia
            for (char c : s1.toCharArray()) {
                set1.add(c);
            }

            for (char c : s2.toCharArray()) {
                set2.add(c);
            }


            int intersectionSize = 0;
            for (char c : set1) {
                if (set2.contains(c)) {
                    intersectionSize++;
                }
            }
            //calculez similaritatea
            int unionSize = set1.size() + set2.size() - intersectionSize;

            return (double) intersectionSize / unionSize;
        }
    }
    //Aceasta functie returneaza toti atomii lexicali de dupa un string dat
      String[] findWordAfter(String input, String givenWord) {
          String[] words = input.split("\""); // Split the input string into words
          int i=0;
          int j=0;
          for ( i = 0; i < words.length - 2; i++) {
              if (words[i].equals(givenWord)) {
                  j++;
              }
          }
          String [] rezultat=new String[j];
          j=0;
          for ( i = 0; i < words.length - 2; i++) {
              if (words[i].equals(givenWord)) {
                   rezultat[j]=words[i + 2];
                    j++;
              }
          }
          return rezultat;
      }
      //Aceasta functie imi gaseste un user in functie de username(dat ca string)
    public static User findUserByUsername(String username) {
        for (User user : lista_useri) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null; // User not found
    }
    public static Actor findActorByName(String username)
    {
        for(Actor actor : actorList)
        {
            if(actor.getNume().equalsIgnoreCase(username))
                return actor;
        }
        return null;
    }
    //Aceasta funcite gaseste primul user de un anumit tip (Regular, Contributor sau Admin)
    public static User findUserbyUserType(String usertype)
    {
        for (User user : lista_useri) {
            if (user.getUserType().equalsIgnoreCase(usertype)) {
                return user;
            }
        }
        return null; // User not found
    }
    //Aceasta functie gaseste o productie in functie de numele sau (dat ca string)
    public static Production findProductionByName(String name) {
        // Search in movies
        for (Movie movie : lista_filme) {
            if (movie.get_title().equalsIgnoreCase(name)) {
                return movie;
            }
        }

        // Search in series
        for (Series series : lista_seriale) {
            if (series.series_get_title().equalsIgnoreCase(name)) {
                return series;
            }
        }

        // Production not found
        return null;
    }
    public static boolean is_movie(Production prod)
    {
        for(Movie movie : lista_filme)
        {
            String nume=prod.get_title();
            if(nume.equals(movie.gettitle()))
                return true;
        }
        return false;
    }
    public static boolean is_series(Production prod) {
        String nume = prod.get_title();
        if (nume == null) {
            // If the title is null, it cannot be a series
            return false;
        }

        for (Series series : lista_seriale) {
            if (nume.equals(series.series_get_title())) {
                return true;
            }
        }

        return false;
    }
    //Aceasta functie gaseste un cuvant care se afla la o distanta de k atomi lexicali fata de un cuvant cautat
    //Functie foarte utila pentru a gasi un cuvant imediat aflat dupa un camp in fisierele JSON
    String Gaseste_un_cuvant(String input, String givenWord, int k) {
        String[] words = input.split("\""); // Split the input string into words
        int i=0;
        int j=0;
        for ( i = 0; i < words.length - k; i++) {
            if(words[i].equals(givenWord))
            {
                return words[i+k];
            }
        }
        return "Nu exista";
    }
    //Aceasta functie extrage text dupa ce intalneste ":"
    String extractTextAfterColon(String input) {
        int colonIndex = input.indexOf(':');

        if (colonIndex != -1 && colonIndex < input.length() - 1) {
            return input.substring(colonIndex + 1).trim();
        } else {
            // Return an empty string or null, depending on your requirements
            return "";
        }
    }

        String[] findWordAfter2(String input, String givenWord) {
        String[] words = input.split("\""); // Split the input string into words
        int i=0;
        int j=0;
        for ( i = 0; i < words.length; i++) {
            if (words[i].equals(givenWord) || words[i].equals("}]}]")) {
                j++;
            }
        }
        String [] rezultat=new String[j];
        j=0;
        for ( i = 0; i < words.length; i++) {
            if (words[i].equals(givenWord) || words[i].equals("}]}]")) {
                rezultat[j]=words[i - 5]; // Return the word after the given word
                j++;
            }
        }
        return rezultat;
    }
    //Parsez conturile utilizatorilor
    //Functie pentru a parsa filme si seriale
    private void datedinActors() {
        String filePath = "C:\\Users\\gbonc\\OneDrive\\Desktop\\POO anul 2 semestrul 1\\Tema 1 POO\\POO-TEMA-2023-input\\Actors.json";
        ObjectMapper objectMapper = new ObjectMapper();
        int i=0;
        try { //Fac acest try, deoarece lucrez cu fisier JSON
            // Read JSON file into JsonNode
            JsonNode jsonNode = objectMapper.readTree(new File(filePath));
            String[] name = findWordAfter(jsonNode.toString(), "name"); // extrag numele actorilor
            String[] biografie = findWordAfter(jsonNode.toString(), "biography"); //extrag biografia actorilor
            String[] tiptitlu =  findWordAfter(jsonNode.toString(), "title"); //extrag titlurile filmelor
            String[] ultimul_film = findWordAfter2(jsonNode.toString(),"}],");
            int j=0;
            //Aici ma ocup sa parsez actorii
            for ( i = 0; i < name.length; i++) {
                Actor actor1=new Actor(name[i]);
                if(i>=biografie.length)
                {
                    actor1.addBiografie("Nu exista!");
                }
                else
                    actor1.addBiografie(biografie[i]);
                ok=0;
                //Aici ma ocup de rolurile actorilor
                while(ok == 0)
                {
                    //Actorii din JSON joaca doar in filme
                    if(tiptitlu[j].equals(ultimul_film[i]))
                    {
                        actor1.addRole(tiptitlu[j],"Movie");
                        j++;
                        ok=1; //ok=1 pentru a vedea ultimul rol inainte de a trece la urmatorul actor
                    }
                    else
                    {
                        actor1.addRole(tiptitlu[j], "Movie");
                        j++;
                    }
                }
                actorList.add(actor1); // Adauga actori la actorList
                ok=0;

            }
            for (Actor actor : actorList) { //aici setez numarul de roluri ale fiecarui actor
                int k=0;
                List<Actor.NameTypePair> roles = actor.getRoluri();
                if (roles != null) {
                    for (Actor.NameTypePair role : roles) {
                        k++;
                    }
                    actor.set_number_roles(k);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void parseazaProductii()
    {
        String filePath = "C:\\Users\\gbonc\\OneDrive\\Desktop\\POO anul 2 semestrul 1\\Tema 1 POO\\POO-TEMA-2023-input\\production.json";
        ObjectMapper objectMapper = new ObjectMapper();
        int i=0;
        int j=0;
        try { //Fac acest try pentru a nu primi erori de la parsarea fisierului JSON
            JsonNode jsonNode = objectMapper.readTree(new File(filePath));
            String[] filme=jsonNode.toString().split("\\{|\\}");
            String rating=null;
            String username=null;
            String comment=null;
            String title_rezerva=null;
            for(i=0;i< filme.length;i++) {
                String title = Gaseste_un_cuvant(filme[i].toString(), "title",2); // extrag numele actorilor
                if(title.indexOf("Nu exista")==-1)
                    title_rezerva=title;
                String type = Gaseste_un_cuvant(filme[i].toString(), "type",2); //extrag biografia actorilor
                String nr_sezoane =Gaseste_un_cuvant(filme[i].toString(),"numSeasons",2);
                String actori = null;
                String directori = null;
                String genre = null;
                String[] result = jsonNode.toString().split("\"");
                if (type.equals("Movie")) {
                    //Aici citesc date despre film
                    Movie film = new Movie(title, type);
                    String extragere = filme[i].replace("\"", "");
                    String[] extragere2 = extragere.split("\\[|\\]");
                    for (j = 0; j < extragere2.length; j++) {
                        if (extragere2[j].equals(",actors:"))
                            actori = extragere2[j + 1];
                        if (extragere2[j].contains("directors:"))
                            directori = extragere2[j + 1];
                        if (extragere2[j].contains("genre"))
                            genre = extragere2[j + 1];
                        film.movie_add_directors(directori);
                        film.movie_add_actori(actori);
                        film.movie_add_genre(genre);
                        //Adaug la film caracteristicile de pana acum
                    }
                    int l = 0;
                    i++;
                    List <Rating> recenzii=new ArrayList<>();
                    //Aici iau recenziile pentru fiecare film
                    while (filme[i].contains("username")) {
                        String extragere4 = filme[i].replace("\"", " ");
                        String extragere5 = extragere4.replaceAll("\\n|\\r", "");
                        String[] extragere3 = extragere5.split(",");
                        //extrag username, rating si comentarii
                        for (j = 0; j < extragere3.length; j++) {
                            if (extragere3[j].contains("username")) {
                                username = extractTextAfterColon(extragere3[j]);
                            }
                            if (extragere3[j].contains("rating")) {
                                rating = extractTextAfterColon(extragere3[j]);
                            }
                            if (extragere3[j].contains("comment")) {
                                comment = extractTextAfterColon(extragere3[j]);
                            }
                        }
                        //Instantiez un obiect de tip rating, pe care il adaug la recenzii
                        //Recenziile sunt de tip rating
                        Rating rating1 =new Rating(username,rating,comment);
                        recenzii.add(rating1);
                        l++;
                        i+=2;
                    }
                    i--;
                    film.addReview(recenzii);
                    String plot = Gaseste_un_cuvant(filme[i].toString(), "plot",2);
                    String duration=Gaseste_un_cuvant(filme[i].toString(),"duration",2);
                    film.setDuration(duration);
                    String releaseYear=Gaseste_un_cuvant(filme[i].toString(), "releaseYear",1);
                    String releaseYear2 = releaseYear.replace(":","");
                    film.setReleaseYear(releaseYear2);
                    film.setPlot(plot);
                    String average_rating = Gaseste_un_cuvant(filme[i].toString(), "averageRating",1);
                    String average2= average_rating.replace(":","");
                    String average3=average2.replace(",","");
                    film.setRating(average3);
                    lista_filme.add(film);
                    i++;
                }
                //aici parsez serialele
                if(type.equals("Series"))
                {
                    List<Rating> recenzii2=new ArrayList<>();
                    Series serial = new Series(type,"0",title);
                    String filtrare = filme[i].replace("\"", "");
                    String[] filtrare2 = filtrare.split("\\[|\\]");
                    String sezon=new String();
                    for (j = 0; j < filtrare2.length; j++) {
                        if (filtrare2[j].contains("actors:"))
                            actori = filtrare2[j + 1];
                        if (filtrare2[j].contains("directors:"))
                            directori = filtrare2[j + 1];
                        if (filtrare2[j].contains("genres"))
                            genre = filtrare2[j + 1];
                    }
                    serial.setActors(actori);
                    serial.setDirectors(directori);
                    serial.setGenre(genre);
                    //Fiecarui serial ii adaug actori, directori si genuri
                    int l=0;
                    i++;
                    while (filme[i].contains("username")) {
                        String extragere4 = filme[i].replace("\"", " ");
                        String extragere5 = extragere4.replaceAll("\\n|\\r", "");
                        String[] extragere3 = extragere5.split(",");

                        //Aici citesc fiecare rating pentru fiecare serial
                        for (j = 0; j < extragere3.length; j++) {
                            if (extragere3[j].contains("username")) {
                                username = extractTextAfterColon(extragere3[j]);
                            }
                            if (extragere3[j].contains("rating")) {
                                rating = extractTextAfterColon(extragere3[j]);
                            }
                            if (extragere3[j].contains("comment")) {
                                comment = extractTextAfterColon(extragere3[j]);
                            }
                        }
                        //Adaug rating pe baza datelor extrase mai sus
                        Rating rating2=new Rating(username,rating,comment);
                        recenzii2.add(rating2);
                        l++;
                        i+=2;
                    }
                    StringBuilder resultStringBuilder = new StringBuilder();
                    String an_lansare1 = Gaseste_un_cuvant(filme[i-1].toString(), "releaseYear",1);
                    String an_lasare2=an_lansare1.replace(":","");
                    String an_lansare=an_lasare2.replace(",","");
                    String sezoane1=Gaseste_un_cuvant(filme[i-1].toString(),"numSeasons",1);
                    String sezoane2=sezoane1.replace(":","");
                    String sezoane=sezoane2.replace(",","");
                    String plot = Gaseste_un_cuvant(filme[i-1].toString(), "plot",2);
                    String rating_serial1=Gaseste_un_cuvant(filme[i-1].toString(),"averageRating",1);
                    String rating_serial2=rating_serial1.replace(":","");
                    String rating_serial = rating_serial2.replace(",","");
                    //Setez toate datele pentru serial
                    serial.setRating(rating_serial);
                    serial.setReleaseYear(an_lansare);
                    serial.setPlot(plot);
                    serial.setNrSezoane(sezoane);
                    serial.series_set_recenzii(recenzii2);
                    while(filme[i].contains("Season") || filme[i].contains("episodeName"))
                    {
                        if(filme[i].indexOf("Season")== -1)
                            resultStringBuilder.append(filme[i]);
                        if(filme[i+1].contains("Season") || filme[i+1].contains("\\]"))
                        {
                            i--;
                        }
                        else {
                            if ( filme[i].contains("Season")) {
                                i--;
                                // serial.series_add_episodes(String.valueOf(resultStringBuilder));
                                if(resultStringBuilder!=null) {
                                    sezon = String.valueOf(resultStringBuilder);
                                    String sezon2 = sezon.replaceAll("\"Season (\\d+)\":\\[", "Season $1:\n")
                                            .replaceAll("\"episodeName\":\"(.*?)\",\"duration\":\"(\\d+ minutes)\"", "    episodeName: $1, duration: $2\n")
                                            .replaceAll("\\],","");
                                    serial.series_add_sezoane(sezon2);
                                }
                                resultStringBuilder = new StringBuilder();
                                resultStringBuilder.append(filme[i+1]);
                            }
                        }
                        i+=2;
                    }
                    //Aici fac fiecare sezon sa aiba un aspect decent(Fara acolade, ghilimele sau spatii inutile)
                    if(resultStringBuilder!=null) {
                        sezon=String.valueOf(resultStringBuilder);
                        String sezon2 = sezon.replaceAll("\"Season (\\d+)\":\\[", "Season $1:\n")
                                .replaceAll("\"episodeName\":\"(.*?)\",\"duration\":\"(\\d+ minutes)\"", "    episodeName: $1, duration: $2\n")
                                .replaceAll("\\],","");
                        serial.series_add_sezoane(sezon2);
                    }
                    lista_seriale.add(serial);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

    }
    private void parseazaAccounts()
    {
        String filePath="C:\\Users\\gbonc\\OneDrive\\Desktop\\POO anul 2 semestrul 1\\Tema 1 POO\\POO-TEMA-2023-input\\accounts.json";
        ObjectMapper objectMapper = new ObjectMapper();
        int i=0;
        int j=0;
        try {
            JsonNode jsonNode = objectMapper.readTree(new File(filePath));
            String[] accounts=jsonNode.toString().split("\\{|\\}");
            for(i=1;i<accounts.length;i++) {
                User user = new User();
                String username = null;
                String experience = null;
                String password = null;
                String email = null;
                String gender = null;
                String userType = null;
                String productionsContribution = null;
                String actorsContribution = null;
                String favouriteProduction = null;
                String favouriteActors = null;
                String name = null;
                String country = null;
                String age2 = null;
                String birthdate = null;
                String notifications = null;
                //extrag date cu ajutorul functiilor de mai sus
                String experience3 = null;
                if (accounts[i].contains("username")) {
                    username = Gaseste_un_cuvant(accounts[i].toString(), "username", 2);
                    experience = Gaseste_un_cuvant(accounts[i].toString(), "experience", 2);
                    if (experience.equalsIgnoreCase("information"))
                        experience = Gaseste_un_cuvant(accounts[i].toString(), "experience", 1);
                    String experience2 = experience.replace(":", "");
                    experience3 = experience2.replace(",", "");
                    if (experience3 == null)
                        experience3 = "1000000"; //experienta infinita pentru admini
                    i += 2;
                }
                if (accounts[i].contains("email")) {
                    email = Gaseste_un_cuvant(accounts[i].toString(), "email", 2);
                    password = Gaseste_un_cuvant(accounts[i].toString(), "password", 2);
                    i++;
                }
                if (accounts[i].contains("nume") || accounts[i].contains("name")) {
                    name = Gaseste_un_cuvant(accounts[i].toString(), "nume", 2);
                    if (name == "Nu exista")
                        name = Gaseste_un_cuvant(accounts[i].toString(), "name", 2);
                    country = Gaseste_un_cuvant(accounts[i].toString(), "country", 2);
                    String age = Gaseste_un_cuvant(accounts[i].toString(), "age", 1);
                    age2=age.replace(":","").replace(",","");
                    gender = Gaseste_un_cuvant(accounts[i].toString(), "gender", 2);
                    birthdate = Gaseste_un_cuvant(accounts[i].toString(), "birthDate", 2);
                    i++;
                }
                if (accounts[i].contains("userType")) {
                    userType = Gaseste_un_cuvant(accounts[i].toString(), "userType", 2);
                    String[] extragere = accounts[i].split("\\[|\\]");
                    for (j = 0; j < extragere.length; j++) {
                        if (extragere[j].contains("favoriteProductions"))
                            favouriteProduction = extragere[j + 1];
                        if (extragere[j].contains("favoriteActors"))
                            favouriteActors = extragere[j + 1];
                        if (extragere[j].contains("productionsContribution"))
                            productionsContribution = extragere[j + 1];
                        if (extragere[j].contains("actorsContribution"))
                            actorsContribution = extragere[j + 1];
                        if (extragere[j].contains("notifications")) {
                            notifications = extragere[j + 1];
                        }

                    }
                    i++;
                }
                //Toate datele extrase le asociez userului curent
                user.setUsername(username);
                user.setExperience(experience3);
                user.setUserType(userType);
                user.setExperience(experience);
                user.setEmail(email);
                user.setPassword(password);
                user.setName(name);
                user.setCountry(country);
                user.setAge(age2);
                user.setGender(gender);
                LocalDate localDate = LocalDate.parse(birthdate);
                LocalDateTime localDateTime = localDate.atStartOfDay();
                user.setBirthdate(localDateTime);
                user.setActorsContribution(actorsContribution);
                user.setFavouriteActors(favouriteActors);
                user.setFavouriteProduction(favouriteProduction);
                user.setProductionsContribution(productionsContribution);
                for (Movie movie : lista_filme) {
                    List<Rating> recenzii = movie.getReview();
                    for (Rating rating : recenzii) {
                        if (rating.getUsername().equalsIgnoreCase(user.getUsername()))
                            user.add_productii_evaluate(movie.get_title());
                    }
                }
                for (Series series : lista_seriale) {
                    List<Rating> recenzii = series.get_Review_production();
                    for (Rating rating : recenzii) {
                        if (rating.getUsername().equalsIgnoreCase(user.getUsername()))
                            user.add_productii_evaluate(series.series_get_title());
                    }
                }
                if (favouriteProduction != null)
                    user.convert_productii_preferate(favouriteProduction);
                if (productionsContribution != null) {
                    user.convert_contributie_Productii(productionsContribution);
                }
                user.setNotifications(notifications);
                if (notifications != null) {
                    user.convert_notification(notifications);
                }
                if (favouriteActors != null)
                    user.convert_actori_preferati(favouriteActors);
                if (actorsContribution != null)
                    user.convert_contributie_Actori(actorsContribution);
                List<String> preferate = new ArrayList<>();
                //Adaug la lista de preferate filme, seriale si actori
                for (Object prod : user.getProductiiPreferate()) {
                    Production findprod = findProductionByName(((Production) prod).get_title());
                    if (findprod instanceof Movie) {
                        String nume = ((Movie) findprod).gettitle();
                        if (!preferate.contains(nume)) {
                            preferate.add("Film: " + nume);
                        }
                    }
                    if (findprod instanceof Series) {
                        String nume = ((Series) findprod).series_get_title();
                        if (!preferate.contains(nume)) {
                            preferate.add("Serial: " + nume);
                        }
                    }

                }
                SortedSet<Actor> nou = user.get_actori_preferati();
                for (Object actor : nou) {
                    Actor actor2 = (Actor) actor;
                    String nume = actor2.getNume();
                    if (!preferate.contains(nume)) {
                        preferate.add("Actor: " + nume);
                    }
                }

                user.setfavorite(preferate);
                lista_useri.add(user); //Adaug la lista de useri userul curent cu toate datele parsate
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //Aici citesc cererile din requests.json
    private void citire_cereri()
    {
        String filePath = "C:\\Users\\gbonc\\OneDrive\\Desktop\\POO anul 2 semestrul 1\\Tema 1 POO\\POO-TEMA-2023-input\\requests.json";
        ObjectMapper objectMapper = new ObjectMapper();
        int i=0;
        try { //fac iar un try pentru a manipula fisiere JSON
            JsonNode jsonNode = objectMapper.readTree(new File(filePath));
            String[] type = findWordAfter(jsonNode.toString(), "type"); // extrag tipul cererii
            String[] created_date = findWordAfter(jsonNode.toString(), "createdDate"); //extrag data crearii
            String[] username =  findWordAfter(jsonNode.toString(), "username"); //extrag numele celui care a trimis
            String[] actorname = findWordAfter(jsonNode.toString(), "actorName"); //extrag numele actorului
            String[] to = findWordAfter(jsonNode.toString(),"to"); //extrag numele destinatarului
            String[] description = findWordAfter(jsonNode.toString(),"description"); //extrag descrierea problemei
            String[] movie_title =findWordAfter(jsonNode.toString(),"movieTitle"); //extrag titlu productie
            int j=0;
            int k=0;
            for(i=0;i<type.length;i++) //Ma orientez dupa type ca fiind numarul total de cereri (toate cererile au type)
            {
                String pattern = "yyyy-MM-dd'T'HH:mm:ss"; //formatez data sa arate ca in cerinta
                LocalDateTime createdDate = LocalDateTime.parse(created_date[i], DateTimeFormatter.ofPattern(pattern));

                Request request1 = new Request(
                        type[i], // Convert string to enum
                        createdDate,
                        username[i],
                        "Nu exista", // Inlocuiesc campurile daca gasesc nume de actor sau de productie
                        "Nu exista",
                        to[i],
                        description[i]
                );
                if(type[i].equals("ACTOR_ISSUE"))
                {
                    request1.addactorname(actorname[j]); //am gasit nume de actor
                    j++;
                }
                if(type[i].equals("MOVIE_ISSUE")) //am gasit nume de productii
                {
                    request1.addmovie(movie_title[k]);
                    k++;
                }
                if(type[i].equalsIgnoreCase("DELETE_ACCOUNT") || type[i].equalsIgnoreCase("OTHERS")) {
                    lista_cereri_admin.add(request1); //ce este DELETE_ACCOUNT sau OTHERS se adauga in lista pentru admini
                }
                requestList.add(request1);
                User user=findUserByUsername(username[i]);
                user.addRequest(request1); //Adaug cererea trimisa la trimise pentru userul curent
                User user_primeste=new User();
                user_primeste=findUserByUsername(to[i]);//adaug cererea primita la primite pentru userul specificat in to[i]
                if(user_primeste==null) //daca nu gaseste user dupa nume, inseamna ca scrie "Admin"
                    user_primeste=findUserbyUserType("Admin");
                user_primeste.addRequestprimite(request1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static boolean isValidUser(String enteredUsername, String enteredPassword,
                                       String validUsername, String validPassword) {
        return enteredUsername.equals(validUsername) && enteredPassword.equals(validPassword);
    }
    private void startApplicationFlow() {
        // Implementați flow-ul aplicației în funcție de rolul utilizatorului
    }
    //functie utila pentru a limita numarul de caractere pe rand
    public static String wrapText(String text, int lineLength) {
        StringBuilder result = new StringBuilder();
        String[] words = text.split("\\s+");

        int currentLineLength = 0;
        for (String word : words) {
            if (currentLineLength + word.length() > lineLength) {
                result.append("\n");
                currentLineLength = 0;
            }
            result.append(word).append(" ");
            currentLineLength += word.length() + 1; // +1 for the space
        }

        return result.toString().trim();
    }
    //Aici incepe interfata grafica(cu optiuni de autentificare in interfata grafica, terminal si buton de iesire
    public class MovieAppUI extends JFrame {
        public MovieAppUI() {
            setTitle("IMDB");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setSize(500, 400);

            // Create components
            JButton loginButton = createStyledButton("Autentificare", Color.BLUE);
            JButton browseButton = createStyledButton("Terminal", Color.GREEN);
            JButton iesire = createStyledButton("Iesire", Color.CYAN);

            loginButton.addActionListener(e -> showLoginDialog());
            browseButton.addActionListener(e -> showMainPage());
            iesire.addActionListener(e -> iesire_inceput());

            //Poza de fundal pentru pagina de start
            JLabel photoLabel = new JLabel(new ImageIcon("Best-Thriller-Movies-on-Amazon-Prime.jpg"));

            // Create layout
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            panel.add(photoLabel, BorderLayout.CENTER);

            // Adjusted preferred size for smaller buttons
            loginButton.setPreferredSize(new Dimension(200, 30));
            browseButton.setPreferredSize(new Dimension(200, 30));
            iesire.setPreferredSize(new Dimension(200, 30));

            JPanel buttonPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 10, 0); // Add some space between buttons
            buttonPanel.add(loginButton, gbc);
            gbc.gridy++;
            buttonPanel.add(browseButton, gbc);
            gbc.gridy++;
            buttonPanel.add(iesire, gbc);

            panel.add(buttonPanel, BorderLayout.SOUTH);

            // Set layout manager
            setLayout(new BorderLayout());
            add(panel, BorderLayout.CENTER);

            // Center the frame on the screen
            setLocationRelativeTo(null);

            setVisible(true);
        }
        //functie pentru a crea butoane frumoase
        private JButton createStyledButton(String text, Color color) {
            JButton button = new JButton(text);
            button.setPreferredSize(new Dimension(120,30));
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            return button;
        }
        //Aici sunt condus spre logare (spre LoginForm)
        private void showLoginDialog() {
            dispose();
            new LoginForm();
        }
        private void showMainPage() {
            //Aici trec in modul terminal al aplicatiei
            Terminal terminal = new Terminal();
            terminal.startTerminal();

            // After the terminal is done, you can perform additional actions
            User loggedInUser = terminal.getLoggedInUser();

            if (loggedInUser != null) {
                loggedInUser.displayInfo();
            }
        }
        private void iesire_inceput()
        {
            System.exit(0);
        }

        private void showMenuPage() {
            JOptionPane.showMessageDialog(this, "Implementare pagina de meniu aici.");
            // Implement menu page logic here
        }

        public void main(String[] args) {
            SwingUtilities.invokeLater(() -> new MovieAppUI());
        }
    }

    public class LoginForm extends JFrame {
        private JTextField usernameField;
        private JPasswordField passwordField;

        public LoginForm() {
            //Aici are loc logarea in interfata grafica
            setTitle("Authentificare");
            setSize(400, 200);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Read user data from a JSON file
            String filePath = "C:\\Users\\gbonc\\OneDrive\\Desktop\\POO anul 2 semestrul 1\\Tema 1 POO\\POO-TEMA-2023-input\\accounts.json";
            ObjectMapper objectMapper = new ObjectMapper();
            // Creare componente
            JLabel usernameLabel = new JLabel("Username:"); //Etichete username si parola
            JLabel passwordLabel = new JLabel("Parola:");
            usernameField = new JTextField(20);
            passwordField = new JPasswordField(20);
            JButton loginButton = new JButton("Logare");
            JButton backButton = new JButton("Inapoi la pagina principala");
            //Butonul de refresh este util daca ai introdus gresit parola si nu vrei sa o stergi
            JButton refreshButton = new JButton("Refresh");
            //La apasarea butonului login:
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String enteredUsername = usernameField.getText();
                        char[] enteredPasswordChars = passwordField.getPassword();
                        String enteredPassword = new String(enteredPasswordChars);

                        // Verifica daca userul si parola sunt prea lungi
                        if (enteredUsername.length() > 255 || enteredPassword.length() > 255) {
                            throw new IllegalArgumentException("Username sau parola prea lungi");
                        }

                        // Authenticate the user
                        boolean authenticationSuccess = authenticateUser(enteredUsername, enteredPassword);

                        if (authenticationSuccess) {
                            new MainPage();
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(LoginForm.this, "Username sau parola introduse gresit");
                        }
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(LoginForm.this, "Username sau parola prea lungi");
                    }
                }
            });

            // Acest backButton ma duce inapoi in pagina de intrare ( cea cu autentificare, terminal si iesire)
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    new MovieAppUI();
                }
            });

            refreshButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Clear the entered username and password fields
                    usernameField.setText("");
                    passwordField.setText("");
                }
            });

            // Creaza layout
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            // Adaug componente
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(usernameLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            panel.add(usernameField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(passwordLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            panel.add(passwordField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(loginButton, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            panel.add(backButton, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(refreshButton, gbc);

            // Add the panel to the authentication window
            add(panel);

            // Center the window on the screen
            setLocationRelativeTo(null);

            // Display the window
            setVisible(true);
        }
        //Functie pentru verificarea datelor introduse pentru logare
        private boolean authenticateUser(String enteredUsername, String enteredPassword) {
            //Iau toti userii din lista de useri logati si verific sa se potriveasca mailul si parola
            for (User user : lista_useri) {
                if (enteredUsername.equals(user.getEmail()) && enteredPassword.equals(user.getPassword())) {
                    ok = 1;
                    tipuserlogat = user.getUserType();
                    user_logat = user; //salvez userul logat in sistem (e foarte util)
                    return true;
                }
            }
            return false;
        }

        public void main(String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new LoginForm();
                }
            });
        }
    }

    public class MainPage extends JFrame {
        private void addComponent(Container container, Component component, GridBagConstraints gbc) {
            container.add(component, gbc);
        }

        //aici este pagina principala cu cele 15 butoane ale sale
        private static final Color PRIMARY_COLOR = new Color(63, 81, 181); // Blue
        static int red = (int) (Math.random() * 256);
        static int green = (int) (Math.random() * 256);
        static int blue = (int) (Math.random() * 256);
        //Aici generez o culoare la intamplare pentru fundal pe care userul o poate schimba dupa cum doreste
        private static final Color SECONDARY_COLOR = new Color(red,green,blue); // Yellow
        private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
        private JButton changeColorButton;
        public MainPage() {
            setTitle("Pagina Principala");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setSize(700, 550);
            UIManager.put("Panel.background", SECONDARY_COLOR);
            UIManager.put("Button.background", PRIMARY_COLOR);
            UIManager.put("Button.foreground", BUTTON_TEXT_COLOR);
            UIManager.put("OptionPane.background", SECONDARY_COLOR);
            UIManager.put("OptionPane.messageForeground", PRIMARY_COLOR);
            // Creare componente
            JButton productiiButton = new JButton("Productii sistem"); //toti le pot vedea
            JButton notificatiiButton=new JButton("Notificari"); //toti le pot vedea
            JButton recomandariButton = new JButton("Recomandari"); //toti le pot vedea
            JButton modificare_actor = new JButton("Editare Actori"); //doar contributori si admini
            JButton editare_recenzii = new JButton("Adauga/Sterge recenzie"); //doar regular
            JButton cautareButton = new JButton("Cautare"); // toti pot face o cautare
            JButton actoriButton = new JButton("Pagina Actorilor"); //toti pot vedea pagina actorilor
            JButton delogareButton = new JButton("Delogare"); //toti se pot deloga
            JButton modificare_productie = new JButton("Editare productii"); //doar contributori si admini
            JButton favorite = new JButton("Favorite"); //toti
            JButton inchidere = new JButton("Inchidere"); //toti
            JButton rezolvare_cereri_admin=new JButton("Rezolvare cereri admin"); //admini
            changeColorButton = new JButton("Schimba culoare fundal"); //toti
            JButton cereri = new JButton("Cererile mele"); //toti
            JButton useri = new JButton(("Adaugare/stergere user")); //doar admin
            JButton recenziiactori=new JButton("Recenzii actori");
            JButton request_contributori=new JButton("Rezolvare cereri contributori");
            // Adaugare ascultatori pentru butoane
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            panel.add(productiiButton, gbc);
            panel.add(notificatiiButton, gbc);
            panel.add(recomandariButton, gbc);
            panel.add(cereri,gbc);
            if(user_logat.getUserType().equalsIgnoreCase("Admin")) {
                panel.add(useri, gbc);
            }
            if(user_logat.getUserType().equalsIgnoreCase("Contributor"))
                panel.add(request_contributori,gbc);
            if(user_logat.getUserType().equalsIgnoreCase("Contributor") ||
                    user_logat.getUserType().equalsIgnoreCase("Admin")) {
                panel.add(modificare_actor, gbc);
            }
            if(user_logat.getUserType().equalsIgnoreCase("Contributor") ||
                    user_logat.getUserType().equalsIgnoreCase("Admin")) {
                panel.add(modificare_productie, gbc);
            }
            panel.add(favorite,gbc);
            if(user_logat.getUserType().equalsIgnoreCase("Admin")) {
                panel.add(rezolvare_cereri_admin, gbc);
            }
            if(user_logat.getUserType().equalsIgnoreCase("Regular")) {
                panel.add(editare_recenzii, gbc);
            }
            if(user_logat.getUserType().equalsIgnoreCase("Regular")) {
                panel.add(recenziiactori, gbc);
            }
            panel.add(cautareButton, gbc);
            panel.add(actoriButton, gbc);
            panel.add(inchidere, gbc);
            panel.add(delogareButton, gbc);
            panel.add(changeColorButton,gbc);
            // Add padding to the panel
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            // Add panel to the main frame
            add(panel);
            setVisible(true);
            setLocationRelativeTo(null);
            rezolvare_cereri_admin.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new RequestsHolder();
                }
            });
            request_contributori.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new Contributor();
                }
            });
            recenziiactori.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new ReviewWindowActors();
                }
            });
            modificare_productie.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(steag_productii==0) {
                        new ProductionWork();
                        steag_productii++;
                    }
                }
            });
            favorite.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(steag_favorite==0) {
                        new Favorite();
                        steag_favorite++;
                    }
                }
            });
            useri.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (steag_admin == 0) {
                        new Admin();
                        steag_admin++;
                    }
                }
            });
            modificare_actor.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(staffint==0) {
                        new Staffinterface();
                        staffint++;
                    }
                }
            });
            changeColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    changeColor(panel);
                }
            });
            inchidere.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            recomandariButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Implementare pentru afișarea recomandărilor
                    dispose();
                    new Recomandari();

                }
            });
            productiiButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new AfisareProductii();
                }
            });
            editare_recenzii.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(steag_regular==0)
                    {
                        new Regular.ReviewWindow();
                        steag_regular++;
                    }
                }
            });

            cautareButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Implementare pentru afișarea câmpului de căutare
                    //JOptionPane.showMessageDialog(MainPage.this, "Implementare cautare aici.");
                    JTextField searchTermField = new JTextField();
                    Object[] message = {
                            "Introduceti termenul de cautare:", searchTermField
                    };

                    int option = JOptionPane.showConfirmDialog(MainPage.this, message,
                            "Cautare", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        String searchTerm = searchTermField.getText().toLowerCase(); // convert to lowercase for case-insensitive comparison
                        cautare searchResultWindow = new cautare();
                        //Cauta in randul actorilor
                        for (Actor actor : actorList) {
                            double actorSimilarity = SimilarityCalculator.calculateSimilarity(actor.getName().toLowerCase(), searchTerm);
                            List<Actor.NameTypePair> roles = actor.getRoluri();
                            StringBuilder rolesPlayed = new StringBuilder();
                            //Cauta fiecare rol in parte
                            if (roles != null) {
                                for (Actor.NameTypePair role : roles) {
                                    rolesPlayed.append("Rol: ").append(role.getName()).append(" (").append(role.getType()).append(")\n");
                                }
                            }
                            if (actorSimilarity > 0.8 ) {
                                // Include actor and role in the search results
                                searchResultWindow.updateSearchResults("In lista, am gasit un actor:");
                                searchResultWindow.updateSearchResults("Actor: " + actor.getNume() + "\nBiografie: " + actor.getBiografie());
                                searchResultWindow.updateSearchResults(rolesPlayed.toString());
                                //break; // Break from the role loop if a similar role is found
                            }
                            for (Actor.NameTypePair role : roles) {
                                double roleSimilarity = SimilarityCalculator.calculateSimilarity(role.getName().toLowerCase(), searchTerm);
                                    if(roleSimilarity > 0.8)
                                    {
                                        searchResultWindow.updateSearchResults("In lista, am gasit un rol");
                                        searchResultWindow.updateSearchResults("Rolul este " + role.getName());
                                        searchResultWindow.updateSearchResults("Acest rol apartine actorului " + actor.getNume());
                                        //searchResultWindow.updateSearchResults(rolesPlayed.toString());
                                       // break;
                                    }
                            }
                        }
                        for (Movie movie : lista_filme) {
                            int m = 0;
                            double titleSimilarity = SimilarityCalculator.calculateSimilarity(movie.title.toLowerCase(), searchTerm);
                            double typeSimilarity = SimilarityCalculator.calculateSimilarity(movie.movie_get_type().toLowerCase(), searchTerm);
                            double actorsSimilarity = SimilarityCalculator.calculateSimilarity(movie.movie_get_actori().toLowerCase(), searchTerm);
                            double directorsSimilarity = SimilarityCalculator.calculateSimilarity(movie.movie_get_directors().toLowerCase(), searchTerm);
                            double genreSimilarity = SimilarityCalculator.calculateSimilarity(movie.movie_get_genre().toLowerCase(), searchTerm);
                            if (titleSimilarity > 0.8)
                                searchResultWindow.updateSearchResults("In lista de productii , am gasit un titlu:");
                            if (typeSimilarity > 0.8)
                                searchResultWindow.updateSearchResults("In lista de productii , am gasit un tip comun:");
                            if (actorsSimilarity > 0.8)
                                searchResultWindow.updateSearchResults("In lista de productii , am gasit actori comuni:");
                            if (directorsSimilarity > 0.8)
                                searchResultWindow.updateSearchResults("In lista de productii , am gasit directori comuni:");
                            if (genreSimilarity > 0.8)
                                searchResultWindow.updateSearchResults("In lista de productii , am gasit genuri de film comune:");
                            if (titleSimilarity > 0.8 || typeSimilarity > 0.8 || actorsSimilarity > 0.8 || directorsSimilarity > 0.8 || genreSimilarity > 0.8) {
                                searchResultWindow.updateSearchResults("Titlu: " + movie.title);
                                searchResultWindow.updateSearchResults("Tip: " + movie.movie_get_type());
                                searchResultWindow.updateSearchResults("Actori: " + movie.movie_get_actori());
                                searchResultWindow.updateSearchResults("Directori: " + movie.movie_get_directors());
                                searchResultWindow.updateSearchResults("Genuri : " + movie.movie_get_genre());

                            }
                        }
                        for(Series series : lista_seriale)
                        {
                            double titleSimilarity = SimilarityCalculator.calculateSimilarity(series.series_get_title().toLowerCase(), searchTerm);
                            //double typeSimilarity = SimilarityCalculator.calculateSimilarity(series.series_get_().toLowerCase(), searchTerm);
                            double actorsSimilarity = SimilarityCalculator.calculateSimilarity(series.getActors().toLowerCase(), searchTerm);
                            double directorsSimilarity = SimilarityCalculator.calculateSimilarity(series.getDirectors().toLowerCase(), searchTerm);
                            double genreSimilarity = SimilarityCalculator.calculateSimilarity(series.getGenre().toLowerCase(), searchTerm);
                            if (titleSimilarity > 0.8)
                                searchResultWindow.updateSearchResults("In lista de productii , am gasit un titlu:");
                            if (actorsSimilarity > 0.8)
                                searchResultWindow.updateSearchResults("In lista de productii , am gasit actori comuni:");
                            if (directorsSimilarity > 0.8)
                                searchResultWindow.updateSearchResults("In lista de productii , am gasit directori comuni:");
                            if (genreSimilarity > 0.8)
                                searchResultWindow.updateSearchResults("In lista de productii , am gasit genuri de film comune:");
                            if (titleSimilarity > 0.8 || actorsSimilarity > 0.8 || directorsSimilarity > 0.8 || genreSimilarity > 0.8) {
                                searchResultWindow.updateSearchResults("Titlu: " + series.series_get_title());
                                searchResultWindow.updateSearchResults("Tip: " + "Series");
                                searchResultWindow.updateSearchResults("Actori: " + series.getActors());
                                searchResultWindow.updateSearchResults("Directori: " + series.getDirectors());
                                searchResultWindow.updateSearchResults("Genuri : " + series.getGenre());
                            }

                        }
                        searchResultWindow.setVisible(true);
                    }

                }
            });
            notificatiiButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Implementare pentru navigarea la pagina de actori
                    //JOptionPane.showMessageDialog(MainPage.this, "Implementare pagina actorilor aici.");
                    dispose();
                    new DisplayNotifications();
                }
            });
            actoriButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Implementare pentru navigarea la pagina de actori
                    //JOptionPane.showMessageDialog(MainPage.this, "Implementare pagina actorilor aici.");
                    new AfisareActori(actorList);
                }
            });
            cereri.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Iterate through all users
                    for (User user : lista_useri) {
                        // Iterate through all requests
                        for (Request request : requestList) {
                            if (request.getType().equalsIgnoreCase(user.getUserType())
                                    || request.getCreatorUsername().equalsIgnoreCase(user.getUsername())
                                    && !user.getAllSentRequests().contains(request)) {
                                user.addSentRequest(request);
                                // other code...
                            }

                            if ((request.getResolverUsername().equalsIgnoreCase(user.getUserType()) ||
                                    request.getResolverUsername().equalsIgnoreCase(user.getUsername()))
                                    && !user.getAllReceivedRequests().contains(request)) {
                                user.addReceivedRequest(request);
                                // other code...
                            }
                        }
                    }
                    if(stegulet==0) {
                        new RequestManager();
                        stegulet++;
                    }

                    // other code...
                }
            });

            delogareButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    new LoginForm();
                }
            });;
        }
        private JButton createStyledButton(String text) {
            JButton button = new JButton(text);
            button.setBackground(PRIMARY_COLOR);
            button.setPreferredSize(new Dimension(150, 30));
            button.setForeground(BUTTON_TEXT_COLOR);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
            return button;
        }
        private void changeColor(JPanel panel) {
            // Generate random RGB values
            int red = (int) (Math.random() * 256);
            int green = (int) (Math.random() * 256);
            int blue = (int) (Math.random() * 256);
            // Create a new random color
            Color randomColor = new Color(red, green, blue);
            UIManager.put("Panel.background", randomColor);
            UIManager.put("Button.background", PRIMARY_COLOR);
            UIManager.put("Button.foreground", BUTTON_TEXT_COLOR);
            UIManager.put("OptionPane.background", randomColor);
            UIManager.put("OptionPane.messageForeground", PRIMARY_COLOR);
            // Set the background color of the panel to the random color
            panel.setBackground(randomColor);
        }

        public void main(String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new MainPage();
                }
            });
        }
    }
    public class AfisareActori extends JFrame {
        private List<Actor> actorList;
        JTextArea actorTextArea = new JTextArea();

        public AfisareActori(List<Actor> actorList) {
            this.actorList = actorList;

            // Set the title of the frame
            setTitle("Lista de actori");

            // Create a JTextArea to display actor information
            actorTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
            actorTextArea.setEditable(false);

            // Append actor information to the JTextArea
            for (Actor actor : actorList) {
                actorTextArea.append("Actor: " + actor.getNume() + "\n");

                List<Actor.NameTypePair> roles = actor.getRoluri();
                if (roles != null) {
                    for (Actor.NameTypePair role : roles) {
                        actorTextArea.append("   Rol: " + role.getName() + " (" + role.getType() + ")\n");
                    }
                }
                List<Rating> rating_actori=actor.get_ratings();
                actorTextArea.append("Recenzii actori: " + "\n");
                for(Rating rating : rating_actori)
                {
                    actorTextArea.append("Username: " + rating.getUsername() + "\n"
                            + "Rating: " + rating.getScore() + "/10" +"\n" + "Comentarii: " + rating.getComments() + "\n\n");
                }

                actorTextArea.append("Biografie:\n" + wrapText(actor.getBiografie(), 70) + "\n\n");
            }

            // Create a JScrollPane to handle scrolling if the content exceeds the JTextArea size
            JScrollPane scrollPane = new JScrollPane(actorTextArea);

            // Create an "OK" button
            JButton okButton = new JButton("OK");

            // Add an ActionListener to the "OK" button
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Close the current window
                    dispose();
                    // You can add code here to return to the previous window
                }
            });
            JButton sortButton = new JButton("Sortare dupa nume");
            JButton sortByRolesButton = new JButton("Sortare dupa numarul de roluri");

            sortByRolesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Sort the actorList by the number of roles and then by name
                    Collections.sort(actorList);
                    // Refresh the display with the sorted list
                    refreshDisplay();
                }
            });
            // Add an ActionListener to the "Sort by Name" button
            sortButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Sort the actorList by name
                    Collections.sort(actorList, Comparator.comparing(Actor::getNume, String.CASE_INSENSITIVE_ORDER));
                    // Refresh the display with the sorted list
                    refreshDisplay();
                }
            });

// Add the sortButton to the buttonPanel

            // Create a JPanel to hold the "OK" button
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(okButton);
            buttonPanel.add(sortButton);
            buttonPanel.add(sortByRolesButton);

            // Create a layout for the frame
            setLayout(new BorderLayout());

            // Add the JScrollPane to the content pane
            add(scrollPane, BorderLayout.CENTER);

            // Add the buttonPanel to the bottom of the frame
            add(buttonPanel, BorderLayout.SOUTH);

            // Set the size of the frame
            setSize(600, 450);

            // Set the default close operation
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            // Center the frame on the screen
            setLocationRelativeTo(null);

            // Make the frame visible
            setVisible(true);
        }
        private void refreshDisplay() {
            // Clear the current content of actorTextArea
            actorTextArea.setText("");

            // Append actor information to the JTextArea
            for (Actor actor : actorList) {
                actorTextArea.append("Actor: " + actor.getNume() + "\n");

                List<Actor.NameTypePair> roles = actor.getRoluri();
                if (roles != null) {
                    for (Actor.NameTypePair role : roles) {
                        actorTextArea.append("   Rol: " + role.getName() + " (" + role.getType() + ")\n");
                    }
                }

                actorTextArea.append("Biografie:\n" + wrapText(actor.getBiografie(), 70) + "\n\n");
            }

            // Revalidate and repaint the frame to update the changes
            revalidate();
            repaint();
        }

        public void main(String[] args) {
            // Create an instance of AfisareActori with the list of actors
            // In a real application, you would replace this list with your actual data

            new AfisareActori(actorList);
        }
    }
    public class DisplayNotifications extends JFrame {
        public DisplayNotifications() {
            // Set the title of the frame
            setTitle("Notificari");

            // Create a JTextArea to display notifications
            JTextArea notificationsTextArea = new JTextArea();
            notificationsTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
            notificationsTextArea.setEditable(false);
            for(Movie movie : IMDB.lista_filme) {
                // Check if the current user (user_logat) has given a review for this movie
                boolean currentUserReviewed = movie.getReview()
                        .stream()
                        .anyMatch(rating -> rating.getUsername().equalsIgnoreCase(user_logat.getUsername()));

                if (currentUserReviewed) {
                    // Iterate through all reviews of the current movie
                    for (Rating rating : movie.getReview()) {
                        // Ensure that the current rating is from a different user
                        if (!rating.getUsername().equalsIgnoreCase(user_logat.getUsername())) {
                            User user = findUserByUsername(rating.getUsername());
                            if (user != null) {
                                String notification= "Filmul " + "\"" +  movie.get_title() + "\"" +
                                        " a primit review de la utilizatorul " + user.getUsername();
                                if(!user_logat.getNotification().contains(notification))
                                {
                                    UserNotificationHandler userObserver = new UserNotificationHandler(user_logat);
                                    IMDB.user_logat.addObserver(userObserver);
                                    user_logat.adauga_notificare(notification);
                                    IMDB.user_logat.notifyObservers(notification);
                                    IMDB.user_logat.removeObserver(userObserver);
                                }
                            }
                        }
                    }
                }
            }
            for (Series series: lista_seriale) {
                // Check if the current user (user_logat) has given a review for this movie
                boolean currentUserReviewed = series.get_Review_production()
                        .stream()
                        .anyMatch(rating -> rating.getUsername().equalsIgnoreCase(user_logat.getUsername()));

                if (currentUserReviewed) {
                    // Iterate through all reviews of the current movie
                    for (Rating rating : series.series_get_recenzii()) {
                        // Ensure that the current rating is from a different user
                        if (!rating.getUsername().equalsIgnoreCase(user_logat.getUsername())) {
                            User user = findUserByUsername(rating.getUsername());
                            if (user != null) {
                                String notification = "Serialul " + "\"" + series.series_get_title() + "\"" +
                                        " a primit review de la utilizatorul " + user.getUsername();
                                if(!user_logat.getNotification().contains(notification))
                                {
                                    UserNotificationHandler userObserver = new UserNotificationHandler(user_logat);
                                    IMDB.user_logat.addObserver(userObserver);
                                    user_logat.adauga_notificare(notification);
                                    IMDB.user_logat.notifyObservers(notification);
                                    user_logat.removeObserver(userObserver);
                                }
                            }
                        }
                    }
                }
            }
            SortedSet<Production> productii = user_logat.get_contributie_productii();
            for (Object prod : productii) {
                String nume = ((Production) prod).get_title();
                Production foundProduction = findProductionByName(nume);

                if (foundProduction != null) {
                    if (foundProduction instanceof Movie) {
                        // Handle Movie
                        Movie foundMovie = (Movie) foundProduction;
                        for(Rating rating : foundMovie.getReview())
                        {
                            if (!rating.getUsername().equalsIgnoreCase(user_logat.getUsername())) {
                                User user = findUserByUsername(rating.getUsername());
                                if (user != null) {
                                    String notification = "Serialul " + "\"" + foundMovie.get_title() + "\"" +
                                            " a primit review de la utilizatorul " + user.getUsername();
                                    if(!user_logat.getNotification().contains(notification))
                                    {
                                        UserNotificationHandler userObserver = new UserNotificationHandler(user_logat);
                                        IMDB.user_logat.addObserver(userObserver);
                                        user_logat.adauga_notificare(notification);
                                        IMDB.user_logat.notifyObservers(notification);
                                        user_logat.removeObserver(userObserver);
                                    }
                                }
                            }

                        }

                    } else if (foundProduction instanceof Series) {
                        // Handle Series
                        Series foundSeries = (Series) foundProduction;
                        for(Rating rating : foundSeries.series_get_recenzii())
                        {
                            if (!rating.getUsername().equalsIgnoreCase(user_logat.getUsername())) {
                                User user = findUserByUsername(rating.getUsername());
                                if (user != null) {
                                    String notification = "Serialul " + "\"" + foundSeries.series_get_title() + "\"" +
                                            " a primit review de la utilizatorul " + user.getUsername();
                                    if(!user_logat.getNotification().contains(notification))
                                    {
                                        UserNotificationHandler userObserver = new UserNotificationHandler(user_logat);
                                        IMDB.user_logat.addObserver(userObserver);
                                        user_logat.adauga_notificare(notification);
                                        IMDB.user_logat.notifyObservers(notification);
                                        user_logat.removeObserver(userObserver);
                                    }
                                }
                            }

                        }
                    }
                } else {
                    System.out.println("Productia nu a fost gasita: " + nume);
                }
            }
            List<String> productii2 = user_logat.da_productii();
            for (String string : productii2) {
                String nume = string;
                Production foundProduction = findProductionByName(nume);

                if (foundProduction != null) {
                    if (foundProduction instanceof Movie) {
                        // Handle Movie
                        Movie foundMovie = (Movie) foundProduction;
                        for(Rating rating : foundMovie.getReview())
                        {
                            if (!rating.getUsername().equalsIgnoreCase(user_logat.getUsername())) {
                                User user = findUserByUsername(rating.getUsername());
                                if (user != null) {
                                    String notification = "Serialul " + "\"" + foundMovie.get_title() + "\"" +
                                            " a primit review de la utilizatorul " + user.getUsername();
                                    if(!user_logat.getNotification().contains(notification))
                                    {
                                        UserNotificationHandler userObserver = new UserNotificationHandler(user_logat);
                                        IMDB.user_logat.addObserver(userObserver);
                                        user_logat.adauga_notificare(notification);
                                        IMDB.user_logat.notifyObservers(notification);
                                        user_logat.removeObserver(userObserver);
                                    }
                                }
                            }

                        }

                    } else if (foundProduction instanceof Series) {
                        // Handle Series
                        Series foundSeries = (Series) foundProduction;
                        for(Rating rating : foundSeries.series_get_recenzii())
                        {
                            if (!rating.getUsername().equalsIgnoreCase(user_logat.getUsername())) {
                                User user = findUserByUsername(rating.getUsername());
                                if (user != null) {
                                    String notification = "Serialul " + "\"" + foundSeries.series_get_title() + "\"" +
                                            " a primit review de la utilizatorul " + user.getUsername();
                                    if(!user_logat.getNotification().contains(notification))
                                    {
                                        UserNotificationHandler userObserver = new UserNotificationHandler(user_logat);
                                        IMDB.user_logat.addObserver(userObserver);
                                        user_logat.adauga_notificare(notification);
                                        IMDB.user_logat.notifyObservers(notification);
                                        user_logat.removeObserver(userObserver);
                                    }
                                }
                            }

                        }
                    }
                } else {
                    System.out.println("Productia nu a fost gasita: " + nume);
                }
            }
            //List<Production> productii = user_logat.contributie_productii;
            List<String> notificari = user_logat.getNotification();
            if(notificari==null)
                notificari.add("Nu exista");
            for (String notification : notificari) {
                notificationsTextArea.append(wrapText(notification.trim(), 80) + "\n");
            }
            // Create a JScrollPane to handle scrolling if the content exceeds the JTextArea size
            JScrollPane scrollPane = new JScrollPane(notificationsTextArea);

            // Create an "OK" button
            JButton okButton = new JButton("OK");
            JButton pagprinc = new JButton("Pagina Principala");

            // Add an ActionListener to the "OK" button
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Close the current window
                    dispose();
                    new MainPage();
                    // You can add code here to return to the previous window or handle other actions
                }
            });
            pagprinc.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Code to be executed when the button is clicked
                    dispose();
                    new MainPage();
                }
            });
            // Create a JPanel to hold the "OK" button
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(okButton);
            buttonPanel.add(pagprinc);
            // Create a layout for the frame
            setLayout(new BorderLayout());

            // Add the JScrollPane to the content pane
            add(scrollPane, BorderLayout.CENTER);

            // Add the buttonPanel to the bottom of the frame
            add(buttonPanel, BorderLayout.SOUTH);

            // Set the size of the frame
            setSize(400, 300);

            // Set the default close operation
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Center the frame on the screen
            setLocationRelativeTo(null);

            // Make the frame visible
            setVisible(true);
        }
    }
    public class ReviewWindowActors extends JFrame {
        private JComboBox<String> actorComboBox;
        private JList<String> reviewsList;
        private JButton addReviewButton;
        private JButton deleteReviewButton;

        private Actor selectedActor;
        private int selectedActorIndex = -1;
        private String selectedActorName;

        public ReviewWindowActors() {
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

            // Create a combo box for selecting actors
            actorComboBox = new JComboBox<>(getActorNames());
            actorComboBox.addActionListener(e -> chooseActorAction());

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
                }
            });

            // Create a panel for the buttons in the south
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonsPanel.add(addReviewButton);
            buttonsPanel.add(deleteReviewButton);

            // Add components to the content panel
            contentPanel.add(actorComboBox, BorderLayout.NORTH);
            contentPanel.add(new JScrollPane(reviewsList), BorderLayout.CENTER);
            contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

            // Add the content panel to the JFrame
            getContentPane().add(contentPanel);
        }

        private boolean isSelectedItemActor() {
            return selectedActor != null;
        }

        private void chooseActorAction() {
            selectedActorName = (String) actorComboBox.getSelectedItem();
            if (selectedActorName != null) {
                selectedActor = findActorByName(selectedActorName);
                updateReviewsListForActor();
            }
        }

        private void updateReviewsListForActor() {
            DefaultListModel<String> model = new DefaultListModel<>();

            List<Rating> reviewsCopy = new ArrayList<>(selectedActor.get_ratings());

            for (Rating rating : reviewsCopy) {
                if (rating.getUsername().trim().equalsIgnoreCase(IMDB.user_logat.getUsername())) {
                    model.addElement(rating.getUsername() + ": " + rating.getScore() + "/10 - " + rating.getComments());
                }
            }

            reviewsList.setModel(model);
            IMDB.user_logat.add_productii_evaluate(selectedActor.getName());
        }

        private void updateReviewsList() {
            DefaultListModel<String> model = new DefaultListModel<>();

            if (selectedActor != null) {
                updateReviewsListForActor();
            }

            reviewsList.setModel(model);
            if (selectedActorIndex != -1 && selectedActorIndex < actorComboBox.getItemCount()) {
                actorComboBox.setSelectedItem(selectedActorName);
            }
        }

        private void performAddReviewAction() {
            if (selectedActor != null) {
                String username = IMDB.user_logat.getUsername();

                if (hasUserReviewed(username)) {
                    JOptionPane.showMessageDialog(this, "Ati acordat deja recenzie acestui actor");
                } else {
                    String scoreStr = JOptionPane.showInputDialog("Acordati o nota (1-10):");
                    String comments = JOptionPane.showInputDialog("Introduceti un comentariu: ");

                    if (scoreStr != null && comments != null) {
                        Rating newRating = new Rating(username, scoreStr, comments);
                        selectedActor.add_rating(newRating);

                        updateReviewsList();
                        updateComboBox();

                        IMDB.user_logat.add_productii_evaluate(selectedActor.getName());
                        ExperienceStrategy strategy = new AddReviewStrategy();
                        int experiencePoints = strategy.calculateExperience();
                        IMDB.user_logat.addExperience(experiencePoints);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Va rugam sa alegeti un actor!");
            }
        }

        private boolean hasUserReviewed(String username) {
            if (selectedActor != null) {
                List<Rating> actorReviews = selectedActor.get_ratings();
                for (Rating review : actorReviews) {
                    if (review.getUsername().equalsIgnoreCase(username)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private void performDeleteReviewAction() {
            if (isSelectedItemActor()) {
                deleteReview(selectedActor);
                updateReviewsList();
            } else {
                JOptionPane.showMessageDialog(this, "Va rugam sa alegeti un actor");
            }
        }

        private void deleteReview(Actor actor) {
            String selectedReviewText = reviewsList.getSelectedValue();
            if (selectedReviewText != null) {
                String[] parts = selectedReviewText.split(" - ");
                String commentsToDelete = parts[1].trim();

                if (commentsToDelete != null && !commentsToDelete.isEmpty()) {
                    List<Rating> reviews = actor.get_ratings();
                    Iterator<Rating> iterator = reviews.iterator();

                    while (iterator.hasNext()) {
                        Rating review = iterator.next();
                        if (review.getComments().equalsIgnoreCase(commentsToDelete) &&
                                review.getUsername().equalsIgnoreCase(IMDB.user_logat.getUsername())) {
                            iterator.remove();
                            actor.delete_rating(review);
                            IMDB.user_logat.stergeRecenzii_Actor(actor);
                            updateReviewsList();
                            updateComboBox();
                        }
                    }

                    updateReviewsList();
                    updateComboBox();
                } else {
                    JOptionPane.showMessageDialog(this, "Va rugam alegeti o recenzie pentru a o sterge.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Va rugam alegeti o recenzie pentru a o sterge.");
            }
        }

        private String[] getActorNames() {
            List<String> actorNames = new ArrayList<>();

            for (Actor actor : IMDB.actorList) {
                actorNames.add(actor.getNume());
            }

            return actorNames.toArray(new String[0]);
        }

        private Actor findActorByName(String name) {
            for (Actor actor : IMDB.actorList) {
                if (actor.getNume().equalsIgnoreCase(name)) {
                    return actor;
                }
            }
            return null;
        }

        private void updateComboBox() {
            String[] actorNames = getActorNames();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(actorNames);
            actorComboBox.setModel(model);

            if (selectedActorName != null && model.getIndexOf(selectedActorName) != -1) {
                actorComboBox.setSelectedItem(selectedActorName);
            }
        }

        public void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                new ReviewWindowActors();
            });
        }
    }
    public class AfisareProductii extends JFrame {
        public AfisareProductii() {
            // Set up the main frame
            setTitle("Afisare productii");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setSize(600, 400);
            // Center the frame on the screen
            setLocationRelativeTo(null);

            // Create buttons
            JButton moviesButton = createStyledButton("Filme", Color.BLUE);
            JButton seriesButton = createStyledButton("Seriale", Color.GREEN);
            JButton pagprinc = createStyledButton("Pagina Principala",Color.YELLOW);

            // Add action listeners to the buttons
            moviesButton.addActionListener(e -> new MovieDisplayFrame());
            seriesButton.addActionListener(e -> new SeriesDisplayFrame());

            pagprinc.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Code to be executed when the button is clicked
                    dispose();
                }
            });
            // Create a panel for buttons
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(1, 2, 20, 0));
            buttonPanel.add(moviesButton);
            buttonPanel.add(seriesButton);
            buttonPanel.add(pagprinc);

            // Load and resize the image
            ImageIcon originalIcon = new ImageIcon("Imagine POO.jpg");
            Image originalImage = originalIcon.getImage();
            Image resizedImage = originalImage.getScaledInstance(400, 250, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(resizedImage);

            // Create a label for the resized image
            JLabel imageLabel = new JLabel(resizedIcon);

            // Create a panel for the image and buttons
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(imageLabel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            // Add the main panel to the content pane
            add(mainPanel);

            pack();
            setVisible(true);
        }

        private JButton createStyledButton(String text, Color color) {
            JButton button = new JButton(text);
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setFont(new Font("Arial", Font.PLAIN, 16));
            return button;
        }

    }
    public class MovieDisplayFrame extends JFrame {

        private JTextArea textArea;
        private JComboBox<String> sortComboBox;

        public MovieDisplayFrame() {
            setTitle("Filme");
            setSize(600, 400);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            textArea = new JTextArea();
            textArea.setEditable(false);
            // Build the movie list
            updateMovieList(textArea);
            JScrollPane scrollPane = new JScrollPane(textArea);
            getContentPane().add(scrollPane, BorderLayout.CENTER);

            // Create a close button
            JButton closeButton = new JButton("Inchidere");
            closeButton.addActionListener(e -> dispose());

            // Create a JComboBox for sorting options
            sortComboBox = new JComboBox<>(new String[]{"Sortare alfabetica", "Sorteaza dupa rating", "Sortare dupa anul lansarii", "Sortare dupa durata"});
            sortComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Perform the selected sorting action
                    performSortingAction();
                }
            });

            // Create a panel for buttons and JComboBox
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(closeButton);
            buttonPanel.add(sortComboBox);

            // Add the button panel to the bottom of the frame
            getContentPane().add(buttonPanel, BorderLayout.SOUTH);

            setLocationRelativeTo(null);
            setVisible(true);
        }

        private void performSortingAction() {
            String selectedSortOption = (String) sortComboBox.getSelectedItem();

            switch (selectedSortOption) {
                case "Sortare alfabetica":
                    Collections.sort(lista_filme, (movie1, movie2) -> movie1.gettitle().compareToIgnoreCase(movie2.gettitle()));
                    break;
                case "Sorteaza dupa rating":
                    Collections.sort(lista_filme, Comparator.comparing(Movie::getRatingAsDouble, Comparator.reverseOrder()));
                    break;
                case "Sortare dupa anul lansarii":
                    Collections.sort(lista_filme, Comparator.comparingInt(movie -> Integer.parseInt(movie.getReleaseYear())));
                    break;
                case "Sortare dupa durata":
                    Collections.sort(lista_filme, Comparator.comparingInt(Movie::getDurationAsInt));
                    break;
            }

            // Update the displayed movie list
            updateMovieList(textArea);
        }

        private void updateMovieList(JTextArea textArea) {
            // Build the movie list
            StringBuilder movieList = new StringBuilder();
                for (Movie movie : lista_filme) {
                    movieList.append("Titlu: ").append(movie.gettitle()).append("\n");
                    movieList.append("Tip: ").append(movie.movie_get_type()).append("\n");
                    movieList.append("Actori: ").append(movie.movie_get_actori()).append("\n");
                    movieList.append("Directori: ").append(movie.movie_get_directors()).append("\n");
                    movieList.append("Genuri: ").append(movie.movie_get_genre()).append("\n");
                    movieList.append("Intriga productie: ").append(movie.getPlot()).append("\n");
                    movieList.append("Rating mediu: ").append(movie.getRating()).append("\n");
                    movieList.append("An lansare: ").append(movie.getReleaseYear()).append("\n");
                    movieList.append("Durata: ").append(movie.getDuration()).append("\n\n");
                    List<Rating> reviews = movie.getReview();

// Check if reviews is not null and not empty
                    if (reviews != null && !reviews.isEmpty()) {
                        Collections.sort(reviews, Comparator.comparingInt(rating -> getUserExperienceForRating((Rating) rating)).reversed());
                        movieList.append("Recenzii:").append("\n");
                        for (Rating review : reviews) {
                            movieList.append("Username: ").append(review.getUsername()).append("\n");
                            movieList.append("Rating: ").append(review.getScore()).append("\n");
                            movieList.append("Comentarii: ").append(review.getComments()).append("\n").append("\n");
                        }
                    }
                    else
                        movieList.append("Nu exista recenzii pana la acest moment!");
                }
            textArea.setText(movieList.toString());
        }

        public void main(String[] args) {
            SwingUtilities.invokeLater(() -> new MovieDisplayFrame());
        }
    }
    public int getUserExperienceForRating(Rating rating) {
        String username=rating.getUsername();
        User user=findUserByUsername(username);
        int exp=user.getExperience();
        return exp;  // Placeholder value, replace with the actual logic
    }
    public class SeriesDisplayFrame extends JFrame {

        private JTextArea textArea;
        private JComboBox<String> sortComboBox;

        public SeriesDisplayFrame() {
            setTitle("Seriale");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            textArea = new JTextArea();
            textArea.setEditable(false);

            // Build the series list
            updateSeriesList(textArea);
            JScrollPane scrollPane = new JScrollPane(textArea);
            getContentPane().add(scrollPane, BorderLayout.CENTER);

            // Create a close button
            JButton closeButton = new JButton("Inchidere");
            closeButton.addActionListener(e -> dispose());

            // Create a JComboBox for sorting options
            sortComboBox = new JComboBox<>(new String[]{"Sortare dupa titlu", "Sortare dupa rating mediu", "Sortare an lansare", "Sortare numar sezoane"});
            sortComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Perform the selected sorting action
                    performSortingAction();
                }
            });

            // Create a panel for buttons and JComboBox
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(closeButton);
            buttonPanel.add(sortComboBox);

            // Add the button panel to the bottom of the frame
            getContentPane().add(buttonPanel, BorderLayout.SOUTH);

            setLocationRelativeTo(null);
            setVisible(true);
        }

        private void performSortingAction() {
            String selectedSortOption = (String) sortComboBox.getSelectedItem();

            switch (selectedSortOption) {
                case "Sortare dupa titlu":
                    Collections.sort(lista_seriale, Comparator.comparing(Series::series_get_title, String.CASE_INSENSITIVE_ORDER));
                    break;
                case "Sortare dupa rating mediu":
                    Collections.sort(lista_seriale, Comparator.comparingDouble(series -> Double.parseDouble(series.getRating())));
                    Collections.reverse(lista_seriale);
                    break;
                case "Sortare an lansare":
                    Collections.sort(lista_seriale, Comparator.comparingInt(series -> Integer.parseInt(series.getReleaseYear())));
                    break;
                case "Sortare numar sezoane":
                    Collections.sort(lista_seriale, Comparator.comparingInt(Series::getNumberOfSeasons));
                    break;
            }

            // Update the displayed series list
            updateSeriesList(textArea);
        }

        private void updateSeriesList(JTextArea textArea) {
            // Build the series list
            StringBuilder seriesList = new StringBuilder();

            for (Series series : lista_seriale) {
                // Call displayInfoOnWindow and update seriesList
                seriesList.append(series.displayInfoOnWindow()).append("\n");
                //seriesList.append(series.getPlot()).append("\n");
                List<Rating> reviews = series.series_get_recenzii();
                seriesList.append("Reviews:").append("\n");
                if (reviews != null && !reviews.isEmpty()) {
                    Collections.sort(reviews, Comparator.comparingInt(rating -> getUserExperienceForRating((Rating) rating)).reversed());
                    for (Rating review : reviews) {
                        seriesList.append("Username: ").append(review.getUsername()).append("\n");
                        seriesList.append("Rating: ").append(review.getScore()).append("\n");
                        seriesList.append("Comentarii: ").append(review.getComments()).append("\n").append("\n\n");
                    }
                }
            }
            textArea.setText(seriesList.toString());
        }

        public void main(String[] args) {
            // Create an instance of the SeriesDisplayFrame
            SwingUtilities.invokeLater(SeriesDisplayFrame::new);
        }
    }

    public class cautare extends JFrame {
        private JTextArea resultTextArea;
        private int steag = 0;

        public cautare() {
            // Set up the JFrame
            setTitle("Cauta rezultat");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Create a JTextArea to display the search results
            resultTextArea = new JTextArea();
            resultTextArea.setEditable(false);

            // Add the JTextArea to the JFrame
            JScrollPane scrollPane = new JScrollPane(resultTextArea);
            getContentPane().add(scrollPane, BorderLayout.CENTER);

            // Create a button to perform the search
            JButton searchButton = new JButton("Cauta");
            //performSearch();
            searchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    performSearch();
                }
            });

            // Create a panel for the button
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(searchButton);

            // Add the button panel to the JFrame
            getContentPane().add(buttonPanel, BorderLayout.SOUTH);

            // Center the JFrame on the screen
            setLocationRelativeTo(null);
        }

        // Method to update the search results in the JTextArea
        public void updateSearchResults(String result) {
            String result2=wrapText(result,70);
            resultTextArea.append(result2 + "\n");
            steag = 1;
        }

        // Method to perform the search
        private void performSearch() {
            // Simulate a search result
            // Check if the result is empty
            if (resultTextArea.getText().trim().isEmpty()) {
                updateSearchResults("Nu exista rezultatul cautat!");
            }
        }

        public String getTextAreaText() {
            return resultTextArea.getText();
        }

        public void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                cautare searchResultWindow = new cautare();
                // For demonstration purposes, show the JFrame when running the main method
                searchResultWindow.setVisible(true);
            });
        }
    }
    public class Recomandari extends JFrame
    {
        private JTextArea recomandariTextArea;
        public Recomandari()
        {
            setTitle("Recomandari");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            // Create a JTextArea to display the recommendations
            recomandariTextArea = new JTextArea();
            recomandariTextArea.setEditable(false);

            // Add the JTextArea to the JFrame
            JScrollPane scrollPane = new JScrollPane(recomandariTextArea);
            getContentPane().add(scrollPane, BorderLayout.CENTER);
            JButton closeButton = new JButton("Inchidere");
            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose(); // Close the Recomandari window
                    new MainPage();
                }
            });
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    dispose();
                    new MainPage();
                }
            });


            // Create a panel for the button
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(closeButton);

            // Add the button panel to the JFrame
            getContentPane().add(buttonPanel, BorderLayout.SOUTH);

            // Center the JFrame on the screen
            setLocationRelativeTo(null);
            setVisible(true);
            List<String> recomandare= new ArrayList<>();
            for(Movie movie : lista_filme)
            {
                String actoriString = movie.movie_get_actori();
                List<String> caz = new ArrayList<>(Arrays.asList(actoriString.split(",")));
                List<String> actorname=new ArrayList<>();
                SortedSet<Actor> nou1= user_logat.get_actori_preferati();
                for(Object actor: nou1)
                {
                    Actor actor2=(Actor) actor;
                    String nume=actor2.getNume();
                    if(!actorname.contains(nume))
                        actorname.add(nume);
                }
                if(caz.stream().anyMatch(actorname::contains) && !recomandare.contains(movie.title)) {
                    recomandare.add(movie.title);
                    Double rating = Double.parseDouble(movie.getRating());
                    recomandariTextArea.append(movie.title + " - Rating: " + rating + System.lineSeparator());
                }
            }
            SortedSet<Actor> nou2= user_logat.get_actori_preferati();
            for(Series series : lista_seriale)
            {
                String actoriString = series.getActors();
                List<String> caz = new ArrayList<>(Arrays.asList(actoriString.split(",")));
                List<String> actorname=new ArrayList<>();
                for(Object actor: nou2)
                {
                    Actor actor2=(Actor) actor;
                    String nume=actor2.getNume();
                    if(!actorname.contains(nume))
                        actorname.add(nume);
                }
                if(caz.stream().anyMatch(actorname::contains) && !recomandare.contains(series.series_get_title())) {
                    recomandare.add(series.series_get_title());
                    Double rating = Double.parseDouble(series.getRating());
                    recomandariTextArea.append(series.series_get_title() + " - Rating: " + rating + System.lineSeparator());
                }
            }
            recomandariTextArea.append("Filme peste 9 " + System.lineSeparator());
            for(Movie movie: lista_filme)
            {
                Double x=Double.parseDouble(movie.getRating());
                if(x>9) {
                    recomandare.add(movie.get_title());
                    recomandariTextArea.append(movie.title + " - Rating: " + x + System.lineSeparator());
                }
            }
            recomandariTextArea.append("Seriale peste 8.5 " + System.lineSeparator());
            for(Series series : lista_seriale)
            {
                Double x=Double.parseDouble(series.getRating());
                if(x>=8.5) {
                    recomandare.add(series.series_get_title());
                    recomandariTextArea.append(series.series_get_title() + " - Rating: " + x + System.lineSeparator());
                }
            }
            updateRecomandari(recomandare);
        }
        public void updateRecomandari(List<String> recomandare) {
            // Join the elements of the list into a single string, each element on a new line
            //String recomandareText = String.join(System.lineSeparator(), recomandare);

            // Set the text in the JTextArea
            //recomandariTextArea.setText(recomandareText);
        }
    }
    public class Favorite extends JFrame {
        private JTextArea favoritesTextArea;
        private JComboBox<String> favoritesComboBox;
        private DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        private Set<String> uniqueFavorites = new HashSet<>();

        public Favorite() {
            // Set up the JFrame
            favoritesTextArea = new JTextArea();
            List<String> favoriteList = user_logat.getFavorite();
            for (Object item : favoriteList) {
                favoritesTextArea.append(item.toString() + "\n");
                uniqueFavorites.add(item.toString());
            }

            // Create a JComboBox to display the favorites
            for (String item : uniqueFavorites) {
                comboBoxModel.addElement(item);
            }

            // Initialize JComboBox with the model
            favoritesComboBox = new JComboBox<>(comboBoxModel);

            // Create a JTextArea to display the favorites
            favoritesTextArea.setEditable(false);

            // Set up the delete button
            JButton deleteButton = new JButton("Șterge Selectatul");
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Handle the delete button click event
                    String selectedFavorite = (String) favoritesComboBox.getSelectedItem();
                    user_logat.sterge_un_favorit(selectedFavorite);
                    uniqueFavorites.remove(selectedFavorite);
                    updateFavoritesTextArea();
                }
            });

            // Set up the add button
            JButton addButton = new JButton("Adaugă la Favorite");
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Prompt the user to choose between adding a movie or an actor
                    String[] options = {"Adaugă Productie", "Adaugă Actor"};
                    int choice = JOptionPane.showOptionDialog(
                            Favorite.this,
                            "Alege ce vrei să adaugi la favorite",
                            "Adaugă la favorite",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]);

                    if (choice == 0) { // Add Movie or Series
                        String selectedProduction = (String) JOptionPane.showInputDialog(
                                Favorite.this,
                                "Alege o productie:",
                                "Adaugă productie",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                getProductionsArray(),
                                null);
                        if (selectedProduction != null) {
                            Production prod = findProductionByName(selectedProduction);
                            String favoriteToAdd = null;
                            if (prod instanceof Movie) {
                                Movie movie = (Movie) prod;
                                favoriteToAdd = "Film: " + movie.get_title();
                            } else if (prod instanceof Series) {
                                Series series = (Series) prod;
                                favoriteToAdd = "Serial: " + series.series_get_title();
                            }

                            if (favoriteToAdd != null && uniqueFavorites.add(favoriteToAdd)) {
                                user_logat.adauga_un_favorit(favoriteToAdd);
                                updateFavoritesTextArea();
                            }
                        }
                    } else if (choice == 1) { // Add Actor
                        String selectedActor = (String) JOptionPane.showInputDialog(
                                Favorite.this,
                                "Alege un actor:",
                                "Adaugă Actor",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                getActorsArray(),
                                null);

                        if (selectedActor != null && uniqueFavorites.add(selectedActor)) {
                            user_logat.adauga_un_favorit(selectedActor);
                            updateFavoritesTextArea();
                        }
                    }
                }
            });

            // Set up the close button
            JButton closeButton = new JButton("Închide");
            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Handle the close button click event
                    dispose(); // Close the Favorite window
                    steag_favorite--;
                }
            });

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    dispose();
                    steag_favorite--;
                }
            });

            // Create a panel for the buttons, the JComboBox, and the JTextArea
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(addButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(closeButton);

            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(favoritesComboBox, BorderLayout.NORTH);
            contentPanel.add(favoritesTextArea, BorderLayout.CENTER);
            contentPanel.add(buttonPanel, BorderLayout.SOUTH);

            // Add the content panel to the JFrame
            getContentPane().add(contentPanel);

            // Set up the JFrame
            setTitle("Productii favorite");
            setSize(600, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        private void updateFavoritesTextArea() {
            // Update the JTextArea with the updated list of favorites
            favoritesTextArea.setText("");
            List<String> favoriteList = user_logat.getFavorite();
            for (Object item : favoriteList) {
                favoritesTextArea.append(item.toString() + "\n");
            }

            // Update the JComboBox model
            Set<String> tempSet = new HashSet<>(uniqueFavorites); // Create a temporary set
            comboBoxModel.removeAllElements();
            for (String item : tempSet) {
                comboBoxModel.addElement(item);
            }
        }

        private String[] getProductionsArray() {
            // Use the actual lista_filme for productions
            List<String> productions = new ArrayList<>();
            for (Movie movie : lista_filme) {
                String nume = movie.get_title();
                productions.add(nume);
            }
            for (Series series : lista_seriale) {
                String nume = series.series_get_title();
                productions.add(nume);
            }
            return productions.toArray(new String[0]);
        }

        private String[] getActorsArray() {
            // Use the actual actorlist for actors
            List<String> actors = new ArrayList<>();
            for (Actor actor : actorList) {
                String nume = "Actor: " + actor.getNume();
                actors.add(nume);
            }
            return actors.toArray(new String[0]);
        }

        public void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                // For demonstration purposes, show the JFrame when running the main method
                new Favorite();
            });
        }
    }
    public static void main(String[] args) {
        // Creați o instanță a aplicației și rulați metoda run
        IMDB imdbApp = IMDB.getInstance();
        imdbApp.run();
    }
}
