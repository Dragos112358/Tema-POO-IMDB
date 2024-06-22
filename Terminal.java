import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Terminal {
    private final Scanner scanner;
    private User loggedInUser;
    private boolean inSubMenu; // Flag to track if the user is in a submenu

    public Terminal() {
        this.scanner = new Scanner(System.in);
        this.inSubMenu = false;
    }

    public void startTerminal() {
        while (true) {
            loggedInUser = runTerminal();

            // Perform actions with the logged-in user
            if (loggedInUser != null) {
                loggedInUser.displayInfo();
            }

            // Ask the user if they want to restart the terminal
            System.out.print("Doriti sa inchideti terminalul? (Da/nu): ");
            String restartChoice = scanner.nextLine().toLowerCase();

            if (!restartChoice.equalsIgnoreCase("no") &&
                    !restartChoice.equalsIgnoreCase(("nu"))) {
                System.out.println("Iesire terminal");
                break;
            }
        }
    }

    private User runTerminal() {
        System.out.println("Bine ati venit in mod terminal!");

        // Assuming IMDB.lista_useri is a list of User objects
        while (true) {
            if (loggedInUser == null) {
                // User not logged in, show login menu
                System.out.println("1. Logare");
                System.out.println("2. Iesire");
                System.out.print("Alegeti o optiune: ");

                // Check if there is more input available
                if (hasInput()) {
                    try {
                        int choice = scanner.nextInt();
                        scanner.nextLine(); // Consume the newline character

                        switch (choice) {
                            case 1:
                                loggedInUser = login();
                                break;
                            case 2:
                                System.out.println("Iesire terminal.");
                                return null; // Exit the terminal
                            default:
                                System.out.println("Alegere invalida. Va rugam reincercati");
                        }
                    } catch (InputMismatchException e) {
                        // Handle the exception (e.g., print an error message)
                        scanner.nextLine(); // Consume the invalid input
                    }
                } else {
                    System.out.println("Input invalid.");
                    scanner.nextLine(); // Consume the invalid input
                }
            } else {
                // User logged in, show main menu or submenu
                if (inSubMenu) {
                    // User is in a submenu, show submenu options
                    handleSubMenuChoice();
                } else {
                    // User is in the main menu, show main menu options
                    printMainMenu();
                    handleMainMenuChoice();
                }
            }
        }
    }

    private void printMainMenu() {
        System.out.println("Logat ca fiind: " + loggedInUser.getUsername());

        int optionNumber = 1; // Initialize the option number

        System.out.println(optionNumber++ + ". Vezi profilul meu");
        System.out.println(optionNumber++ + ". Productiile noastre");
        System.out.println(optionNumber++ + ". Pagina actorilor");
        System.out.println(optionNumber++ + ". Notificari");
        System.out.println(optionNumber++ + ". Cautare Actor/Film/Serial");
        System.out.println(optionNumber++ + ". Adauga/Sterge Actor/Film/Serial din Favorite");
        // Check for user type and add corresponding options
        if (IMDB.user_logat.getUserType().equalsIgnoreCase("Admin")) {
            System.out.println(optionNumber++ + ". Adauga/Sterge User");
        }
        if (IMDB.user_logat.getUserType().equalsIgnoreCase("Contributor")
                || IMDB.user_logat.getUserType().equalsIgnoreCase("Admin")) {
            System.out.println(optionNumber++ + ". Adauga/Sterge Actor/Film/Serial din Sistem");
            System.out.println(optionNumber++ + ". Updateaza Detalii Productii");
            System.out.println(optionNumber++ + ". Updateaza Detalii Actori");
        }
        if (IMDB.user_logat.getUserType().equalsIgnoreCase("Admin")) {
            System.out.println(optionNumber++ + ". Rezolva o cerere");
        }

        System.out.println(optionNumber++ + ". Managerul meu de Cereri");

        if (IMDB.user_logat.getUserType().equalsIgnoreCase("Regular")) {
            System.out.println(optionNumber++ + ". Adauga/Sterge recenzii productii");
        }
        if(IMDB.user_logat.getUserType().equalsIgnoreCase("Regular"))
        {
            System.out.println(optionNumber++ + ". Adauga/Sterge recenzii actori");
        }
        if(IMDB.user_logat.getUserType().equalsIgnoreCase("Contributor"))
        {
            System.out.println(optionNumber++ + ". Rezolvare cereri contributori");
        }
        System.out.println(optionNumber + ". Delogare");
        System.out.print("Alegeti o optiune: ");
    }

    private void handleMainMenuChoice() {
        // Check if there is more input available
        if (hasInput()) {
            String choice = scanner.nextLine().toLowerCase();

            if (isNumeric(choice)) {
                int numericChoice = Integer.parseInt(choice);
                if (numericChoice >= 1 && numericChoice <= 14) {
                    handleMainMenuOption(numericChoice);
                } else {
                    System.out.println("Alegere invalida. Va rugam reincercati");
                }
            } else {
                System.out.println("Input invalid. Va rugam sa introduceti un numar");
            }
        }
    }

    private void handleMainMenuOption(int choice) {
        switch (IMDB.user_logat.getUserType()) {
            case "Regular":
                handleRegularUserOption(choice);
                break;
            case "Contributor":
                handleContributorUserOption(choice);
                break;
            case "Admin":
                handleAdminUserOption(choice);
                break;
            default:
                System.out.println("Tip de user invalid");
                loggedInUser = null;
                break;
        }
        inSubMenu = true; // Introducetiing a submenu
    }

    private void handleRegularUserOption(int choice) {
        switch (choice) {
            case 1:
                viewProfile();
                break;
            case 2:
                viewProductionsDetails();
                break;
            case 3:
                viewActorsDetails();
                break;
            case 4:
                viewNotifications();
                break;
            case 5:
                search();
                break;
            case 6:
                manageFavorites();
                break;
            case 7:
                TextRequestManager.main(new String[0]);
                break;
            case 8:
                TextReviewManager.main(new String[0]);
                break;
            case 9:
                ReviewWindowActors.TextReviewWindowActors();
                break;
            case 10:
                System.out.println("Delogare.");
                loggedInUser = null;
                return; // Exit to the main menu
        }
    }

    private void handleContributorUserOption(int choice) {
        switch (choice) {
            case 1:
                viewProfile();
                break;
            case 2:
                viewProductionsDetails();
                break;
            case 3:
                viewActorsDetails();
                break;
            case 4:
                viewNotifications();
                break;
            case 5:
                search();
                break;
            case 6:
                manageFavorites();
                break;
            case 7:
                manageSystem();
                break;
            case 8:
                UpdateMovieDetails();
                break;
            case 9:
                updateActorDetails();
                break;
            case 10:
                TextRequestManager.main(new String[0]);
                break;
            case 11:
                 AdminSolver.main();
                break;
            case 12:
                System.out.println("Delogare.");
                loggedInUser = null;
                return; // Exit to the main menu
        }
    }

    private void handleAdminUserOption(int choice) {
        switch (choice) {
            case 1:
                viewProfile();
                break;
            case 2:
                viewProductionsDetails();
                break;
            case 3:
                viewActorsDetails();
                break;
            case 4:
                viewNotifications();
                break;
            case 5:
                search();
                break;
            case 6:
                manageFavorites();
                break;
            case 7:
                new Terminal.manageUsers().manageUsers();
                break;
            case 8:
                manageSystem();
                break;
            case 9:
                UpdateMovieDetails();
                break;
            case 10:
                updateActorDetails();
                break;
            case 11:
                RequestsHolder.solveRequest(IMDB.lista_cereri_admin);
                break;
            case 12:
                TextRequestManager.main(new String[0]);
                break;
            case 13:
                System.out.println("Delogare");
                loggedInUser = null;
                return; // Exit to the main menu
        }
    }

    private void handleSubMenuChoice() {
        printSubMenuPrompt();
        // Check if there is more input available
        if (hasInput()) {
            String choice = scanner.nextLine().toLowerCase();
            if ("back".equalsIgnoreCase(choice) || "bac".equalsIgnoreCase(choice)
                    || "ba".equalsIgnoreCase(choice) || "b".equalsIgnoreCase(choice)
                    || "exit".equalsIgnoreCase(choice) || "exi".equalsIgnoreCase(choice)
                    || "ex".equalsIgnoreCase(choice) || "e".equalsIgnoreCase(choice)) {
                System.out.println("Inapoi la meniul principal.");
                inSubMenu = false; // Exiting the submenu
            } else {
                System.out.println("Input invalid. Tastati back pentru a va intoarce in meniul principal. ");
            }
        }
    }

    private void printSubMenuPrompt() {
        System.out.println("Tastati 'back' sau 'exit' pentru a merge inapoi in meniul principal.");
        System.out.print("Tastati aici: ");
    }

    private boolean hasInput() {
        return scanner.hasNext();
    }

    private User login() {
        System.out.print("Introduceti email: ");
        String email = scanner.nextLine();

        System.out.print("Introduceti parola: ");
        String password = scanner.nextLine();

        // Check if the user with the provided email and password exists
        User user = loginUser(email, password);

        if (user != null) {
            System.out.println("Logare cu succes!");
            return user; // Return the logged-in user
        } else {
            System.out.println("Email sau parola invalide. Va rugam reincercati");
            return null;
        }
    }

    private static User loginUser(String email, String password) {
        // Iterate through the list of users and check for a match
        for (User user : IMDB.lista_useri) {
            if (user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)) {
                IMDB.user_logat = user;
                return user; // Return the logged-in user
            }
        }
        return null; // Return null if no matching user is found
    }

    private void viewProfile() {
        // Implement logic to view the user's profile
        System.out.println("Vezi profil: ");
        loggedInUser.displayinfo_mare(); // informatii despre user
    }

    private void viewProductionsDetails() {
        System.out.println("Detalii productii");
        int option = 0;
        System.out.print("Alege o optiune (1 pentru Filme, 2 pentru Seriale): ");
        try {
            option = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character
        } catch (InputMismatchException e) {
        }

        if (option == 1) {
            // Display Movies
            sortMovies();
            System.out.println("Filme:");
            displayProductionsMovie();
        } else if (option == 2) {
            // Display Series
            sortSeries();
            System.out.println("Seriale:");
            displayProductionsSeries();
        } else {
            System.out.println("Opțiune invalidă. Introduceți 1 pentru filme sau 2 pentru seriale.");
        }
    }

    private void sortMovies() {
        int selectedSortOption = 0;

        while (true) {
            try {
                System.out.print("Alege o opțiune de sortare (1 pentru sortare alfabetica, 2 pentru sorteaza dupa rating, 3 pentru sortare dupa anul lansarii, 4 pentru sortare dupa durata si 0 pentru fara sortare): ");
                selectedSortOption = scanner.nextInt();
                scanner.nextLine();  // Consume the newline character
                if (selectedSortOption >= 0 && selectedSortOption <= 4) {
                    break;
                } else {
                    System.out.println("Opțiune de sortare invalidă. Introduceți un număr între 1 și 4.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Opțiune invalidă. Introduceți un număr între 1 și 4.");
                scanner.nextLine();  // Consume the invalid input
            }
        }

        switch (selectedSortOption) {
            case 0:
                break;
            case 1:
                Collections.sort(IMDB.lista_filme, (movie1, movie2) -> movie1.gettitle().compareToIgnoreCase(movie2.gettitle()));
                break;
            case 2:
                Collections.sort(IMDB.lista_filme, Comparator.comparingDouble(Movie::getRatingAsDouble));
                break;
            case 3:
                Collections.sort(IMDB.lista_filme, Comparator.comparingInt(movie -> Integer.parseInt(movie.getReleaseYear())));
                break;
            case 4:
                Collections.sort(IMDB.lista_filme, Comparator.comparingInt(Movie::getDurationAsInt));
                break;
            default:
                System.out.println("Opțiune de sortare invalidă.");
        }
    }

    private void sortSeries() {
        int selectedSortOption = 0;

        while (true) {
            try {
                System.out.print("Alege o opțiune de sortare (1 pentru sortare alfabetica, 2 pentru sorteaza dupa rating, 3 pentru sortare dupa anul lansarii, 4 pentru sortare dupa numar sezoane si 0 pentru a nu sorta): ");

                selectedSortOption = scanner.nextInt();
                scanner.nextLine();  // Consume the newline character
                if (selectedSortOption >= 0 && selectedSortOption <= 4) {
                    break;
                } else {
                    System.out.println("Opțiune de sortare invalidă. Introduceți un număr între 1 și 4.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Opțiune invalidă. Introduceți un număr între 1 și 4.");
                scanner.nextLine();  // Consume the invalid input
            }
        }

        switch (selectedSortOption) {
            case 0:
                break;
            case 1:
                Collections.sort(IMDB.lista_seriale, Comparator.comparing(Series::series_get_title, String.CASE_INSENSITIVE_ORDER));
                break;
            case 2:
                Collections.sort(IMDB.lista_seriale, Comparator.comparingDouble(series -> Double.parseDouble(series.getRating())));
                Collections.reverse(IMDB.lista_seriale);
                break;
            case 3:
                Collections.sort(IMDB.lista_seriale, Comparator.comparingInt(series -> Integer.parseInt(series.getReleaseYear())));
                break;
            case 4:
                Collections.sort(IMDB.lista_seriale, Comparator.comparingInt(Series::getNumberOfSeasons));
                break;
        }
    }

    public int getUserExperienceForRating(Rating rating) {
        String username = rating.getUsername();
        User user = IMDB.findUserByUsername(username);
        int exp = user.getExperience();
        return exp;  // Placeholder value, replace with the actual logic
    }

    private void displayProductionsMovie() {
        System.out.println("Vezi detalii filme: ");

        for (Movie movie : IMDB.lista_filme) {
            System.out.println("Titlu: " + movie.title);
            System.out.println("Tip: " + movie.movie_get_type());
            System.out.println("Rating mediu: " + movie.getRating());
            System.out.println("Release Year: " + movie.getReleaseYear());
            System.out.println("Duration: " + movie.getDuration());
            System.out.println("Actori: " + movie.movie_get_actori());
            System.out.println("Directori: " + movie.movie_get_directors());
            System.out.println("Genuri: " + movie.movie_get_genre());
            System.out.println("Intriga productie: " + movie.getPlot());

            // Print movie reviews
            List<Rating> reviews = movie.getReview();

            if (reviews != null && !reviews.isEmpty()) {
                Collections.sort(reviews, Comparator.comparingInt(rating -> getUserExperienceForRating((Rating) rating)).reversed());
                System.out.println("Recenzii:");
                for (Rating review : reviews) {
                    System.out.println("Username: " + review.getUsername());
                    System.out.println("Rating: " + review.getScore());
                    System.out.println("Comentarii: " + review.getComments() + "\n");
                }
            }

            System.out.println(); // Add a line break between movies
        }
    }

    private void displayProductionsSeries() {
        for (Series series : IMDB.lista_seriale) {
            // Call displayInfoOnWindow and update seriesList
            series.displayInfo();
            // Print series reviews
            List<Rating> reviews = series.series_get_recenzii();
            System.out.println("Recenzii:");
            if (reviews != null && !reviews.isEmpty()) {
                Collections.sort(reviews, Comparator.comparingInt(rating -> getUserExperienceForRating((Rating) rating)).reversed());
                for (Rating review : reviews) {
                    System.out.println("Username: " + review.getUsername());
                    System.out.println("Rating: " + review.getScore());
                    System.out.println("Commentarii: " + review.getComments() + "\n");
                }
            }
            System.out.println(); // Add a line break between series
        }
    }

    private void viewActorsDetails() {
        // Implement logic to view actors details
        System.out.println("Vezi detalii actori");
        sortActors();
        for (Actor actor : IMDB.actorList) {
            System.out.println("Actor: " + actor.getNume());

            List<Actor.NameTypePair> roles = actor.getRoluri();
            if (roles != null) {
                for (Actor.NameTypePair role : roles) {
                    System.out.println("   Rol: " + role.getName() + " (" + role.getType() + ")");
                }
            }
            List<Rating> rating_actori = actor.get_ratings();
            System.out.println("Recenzii actori: ");
            for (Rating rating : rating_actori) {
                System.out.println("Username: " + rating.getUsername() + "\n"
                        + "Rating: " + rating.getScore() + "/10" + "\n" + "Comentarii: " + rating.getComments() + "\n\n");
            }
            if(rating_actori.size()==0)
            {
                System.out.println("Nu exista recenzie pentru acest actor");
            }


            System.out.println("Biografie:\n" + IMDB.wrapText(actor.getBiografie(), 70) + "\n");
        }
    }

    private void sortActors() {
        int selectedSortOption = 0;

        while (true) {
            try {
                System.out.print("Alege o opțiune de sortare pentru actori (1 pentru sortare alfabetica dupa nume, 2 pentru sortare dupa numarul de roluri si 0 pentru a evita sortarea): ");
                selectedSortOption = scanner.nextInt();
                scanner.nextLine();  // Consume the newline character

                if (selectedSortOption >= 0 && selectedSortOption <= 2) {
                    break;
                } else {
                    System.out.println("Opțiune de sortare invalidă. Introduceți 1 sau 2.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Opțiune invalidă. Introduceți 1 sau 2.");
                scanner.nextLine();  // Consume the invalid input
            }
        }

        switch (selectedSortOption) {
            case 1:
                Collections.sort(IMDB.actorList, Comparator.comparing(Actor::getNume, String.CASE_INSENSITIVE_ORDER));
                break;
            case 2:
                Collections.sort(IMDB.actorList);
                break;
            case 0:
                break;
        }
    }

    private void viewNotifications() {
        // Implement logic to view notifications
        for (Movie movie : IMDB.lista_filme) {
            // Check if the current user (IMDB.user_logat) has given a review for this movie
            boolean currentUserReviewed = movie.getReview()
                    .stream()
                    .anyMatch(rating -> rating.getUsername().equalsIgnoreCase(IMDB.user_logat.getUsername()));

            if (currentUserReviewed) {
                // Iterate through all reviews of the current movie
                for (Rating rating : movie.getReview()) {
                    // Ensure that the current rating is from a different user
                    if (!rating.getUsername().equalsIgnoreCase(IMDB.user_logat.getUsername())) {
                        User user = IMDB.findUserByUsername(rating.getUsername());
                        if (user != null) {
                            String notification = "Filmul " + "\"" + movie.get_title() + "\"" +
                                    " a primit review de la utilizatorul " + user.getUsername();
                            if (!IMDB.user_logat.getNotification().contains(notification)) {
                                IMDB.user_logat.adauga_notificare(notification);
                                UserNotificationHandler userObserver = new UserNotificationHandler(IMDB.user_logat);
                                IMDB.user_logat.addObserver(userObserver);
                                IMDB.user_logat.notifyObservers(notification);
                                IMDB.user_logat.removeObserver(userObserver);
                            }
                        }
                    }
                }
            }
        }
        for (Series series : IMDB.lista_seriale) {
            // Check if the current user (IMDB.user_logat) has given a review for this movie
            boolean currentUserReviewed = series.get_Review_production()
                    .stream()
                    .anyMatch(rating -> rating.getUsername().equalsIgnoreCase(IMDB.user_logat.getUsername()));

            if (currentUserReviewed) {
                // Iterate through all reviews of the current movie
                for (Rating rating : series.series_get_recenzii()) {
                    // Ensure that the current rating is from a different user
                    if (!rating.getUsername().equalsIgnoreCase(IMDB.user_logat.getUsername())) {
                        User user = IMDB.findUserByUsername(rating.getUsername());
                        if (user != null) {
                            String notification = "Serialul " + "\"" + series.series_get_title() + "\"" +
                                    " a primit review de la utilizatorul " + user.getUsername();
                            if (!IMDB.user_logat.getNotification().contains(notification)) {
                                IMDB.user_logat.adauga_notificare(notification);
                                IMDB.user_logat.notifyObservers(notification);

                            }
                        }
                    }
                }
            }
        }
        SortedSet<Production> productii = IMDB.user_logat.get_contributie_productii();
        // System.out.println(user.getUsername());
        for (Object prod : productii) {
            String nume = ((Production) prod).get_title();
            Production foundProduction = IMDB.findProductionByName(nume);

            if (foundProduction != null) {
                if (foundProduction instanceof Movie) {
                    // Handle Movie
                    Movie foundMovie = (Movie) foundProduction;
                    for (Rating rating : foundMovie.getReview()) {
                        if (!rating.getUsername().equalsIgnoreCase(IMDB.user_logat.getUsername())) {
                            User user = IMDB.findUserByUsername(rating.getUsername());
                            if (user != null) {
                                String notification = "Serialul " + "\"" + foundMovie.get_title() + "\"" +
                                        " a primit review de la utilizatorul " + user.getUsername();
                                if (!IMDB.user_logat.getNotification().contains(notification)) {
                                    IMDB.user_logat.adauga_notificare(notification);
                                    UserNotificationHandler userObserver = new UserNotificationHandler(IMDB.user_logat);
                                    IMDB.user_logat.addObserver(userObserver);
                                    IMDB.user_logat.notifyObservers(notification);
                                    IMDB.user_logat.removeObserver(userObserver);
                                }
                            }
                        }

                    }

                } else if (foundProduction instanceof Series) {
                    // Handle Series
                    Series foundSeries = (Series) foundProduction;
                    for (Rating rating : foundSeries.series_get_recenzii()) {
                        if (!rating.getUsername().equalsIgnoreCase(IMDB.user_logat.getUsername())) {
                            User user = IMDB.findUserByUsername(rating.getUsername());
                            if (user != null) {
                                String notification = "Serialul " + "\"" + foundSeries.series_get_title() + "\"" +
                                        " a primit review de la utilizatorul " + user.getUsername();
                                if (!IMDB.user_logat.getNotification().contains(notification)) {
                                    IMDB.user_logat.adauga_notificare(notification);
                                    UserNotificationHandler userObserver = new UserNotificationHandler(IMDB.user_logat);
                                    IMDB.user_logat.addObserver(userObserver);
                                    IMDB.user_logat.notifyObservers(notification);
                                    IMDB.user_logat.removeObserver(userObserver);
                                }
                            }
                        }

                    }
                }
            } else {
                System.out.println("Productia nu a fost gasita: " + nume);
            }
        }
        List<String> productii2 = IMDB.user_logat.da_productii();
        // System.out.println(user.getUsername());
        for (String string : productii2) {
            String nume = string;
            Production foundProduction = IMDB.findProductionByName(nume);

            if (foundProduction != null) {
                if (foundProduction instanceof Movie) {
                    // Handle Movie
                    Movie foundMovie = (Movie) foundProduction;
                    for (Rating rating : foundMovie.getReview()) {
                        if (!rating.getUsername().equalsIgnoreCase(IMDB.user_logat.getUsername())) {
                            User user = IMDB.findUserByUsername(rating.getUsername());
                            if (user != null) {
                                String notification = "Serialul " + "\"" + foundMovie.get_title() + "\"" +
                                        " a primit review de la utilizatorul " + user.getUsername();
                                if (!IMDB.user_logat.getNotification().contains(notification)) {
                                    IMDB.user_logat.adauga_notificare(notification);
                                    UserNotificationHandler userObserver = new UserNotificationHandler(IMDB.user_logat);
                                    IMDB.user_logat.addObserver(userObserver);
                                    IMDB.user_logat.notifyObservers(notification);
                                    IMDB.user_logat.removeObserver(userObserver);
                                }
                            }
                        }

                    }

                } else if (foundProduction instanceof Series) {
                    // Handle Series
                    Series foundSeries = (Series) foundProduction;
                    for (Rating rating : foundSeries.series_get_recenzii()) {
                        if (!rating.getUsername().equalsIgnoreCase(IMDB.user_logat.getUsername())) {
                            User user = IMDB.findUserByUsername(rating.getUsername());
                            if (user != null) {
                                String notification = "Serialul " + "\"" + foundSeries.series_get_title() + "\"" +
                                        " a primit review de la utilizatorul " + user.getUsername();
                                if (!IMDB.user_logat.getNotification().contains(notification)) {
                                    IMDB.user_logat.adauga_notificare(notification);
                                    UserNotificationHandler userObserver = new UserNotificationHandler(IMDB.user_logat);
                                    IMDB.user_logat.addObserver(userObserver);
                                    IMDB.user_logat.notifyObservers(notification);
                                    IMDB.user_logat.removeObserver(userObserver);
                                }
                            }
                        }

                    }
                }
            } else {
                System.out.println("Productia nu a fost gasita: " + nume);
            }
        }
        //List<Production> productii = IMDB.user_logat.contributie_productii;
        List<String> notificari = IMDB.user_logat.getNotification();
        System.out.println("Vezi notificari");
        for (Object notification : IMDB.user_logat.getNotification()) {
            System.out.println(notification);
        }
    }

    private void search() {
        System.out.print("Introduceti nume de actor/film/serial ");
        String searchTerm = scanner.nextLine().toLowerCase();

        System.out.println("Cautare: ");
        // Search in actors
        System.out.println("Actori:");
        for (Actor actor : IMDB.actorList) {
            double actorSimilarity = IMDB.SimilarityCalculator.calculateSimilarity(actor.getName().toLowerCase(), searchTerm);
            if (actorSimilarity > 0.8) {
                System.out.println("Actor: " + actor.getNume());
                System.out.println("Biografie: " + actor.getBiografie());
                // Add additional details if needed
            }
        }

        // Search in movies
        System.out.println("Filme:");
        for (Movie movie : IMDB.lista_filme) {
            // Perform search logic for movies
            // You can use the SimilarityCalculator or any other logic here
            double titleSimilarity = IMDB.SimilarityCalculator.calculateSimilarity(movie.title.toLowerCase(), searchTerm);
            if (titleSimilarity > 0.8) {
                System.out.println("Titlu: " + movie.title);
                System.out.println("Tip: " + movie.movie_get_type());
                // Add additional details if needed
            }
        }

        // Search in series
        System.out.println("Seriale:");
        for (Series series : IMDB.lista_seriale) {
            // Perform search logic for series
            // You can use the SimilarityCalculator or any other logic here
            double titleSimilarity = IMDB.SimilarityCalculator.calculateSimilarity(series.series_get_title().toLowerCase(), searchTerm);
            if (titleSimilarity > 0.8) {
                System.out.println("Titlu: " + series.series_get_title());
                System.out.println("Tip: " + "Series");
                // Add additional details if needed
            }
        }

        System.out.println("----------------------------");
    }

    private void displayUserFavoritesWithNumbers() {
        List<String> favorites = IMDB.user_logat.getFavorite();
        System.out.println("Lista de favorite: ");

        for (int i = 0; i < favorites.size(); i++) {
            System.out.println((i + 1) + ". " + favorites.get(i));
        }
    }

    public void manageFavorites() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Managing favorites:");
            System.out.println("1. Adauga Favorite");
            System.out.println("2. Sterge Favorite");
            System.out.println("3. Vizualizare Favorite");
            System.out.println("4. Iesire");
            int choice = 0;
            System.out.print("Introdu numar de la 1 la 4: ");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
            }

            switch (choice) {
                case 1:
                    int i = 1;
                    System.out.println("Actori");
                    for (Actor actor : IMDB.actorList) {
                        System.out.println(i + ". Actor: " + actor.getNume());
                        i++;
                    }
                    System.out.println("Filme");
                    for (Movie movie : IMDB.lista_filme) {
                        System.out.println(i + ". Film: " + movie.get_title());
                        i++;
                    }
                    System.out.println("Seriale");
                    for (Series series : IMDB.lista_seriale) {
                        System.out.println(i + ". Serial: " + series.series_get_title());
                        i++;
                    }

                    System.out.print("Introduceti numarul productiei/actorului pe care il adaugati: ");
                    int alegere = 0;
                    try {
                        alegere = scanner.nextInt();
                    } catch (InputMismatchException e) {
                    }

                    if (alegere > 0 && alegere <= i - 1) {
                        String newFavorite;
                        if (alegere <= IMDB.actorList.size()) {
                            newFavorite = "Actor: " + IMDB.actorList.get(alegere - 1).getNume();
                        } else if (alegere <= IMDB.actorList.size() + IMDB.lista_filme.size()) {
                            newFavorite = "Film: " + IMDB.lista_filme.get(alegere - 1 - IMDB.actorList.size()).get_title();
                        } else {
                            newFavorite = "Serial: " + IMDB.lista_seriale.get(alegere - 1 - IMDB.actorList.size() - IMDB.lista_filme.size()).series_get_title();
                        }

                        IMDB.user_logat.adauga_un_favorit(newFavorite);
                        System.out.println("Favorita adaugata cu succes!");
                    } else {
                        System.out.println("Alegere invalida. Va rugam introduceti ceva valid!");
                    }
                    break;
                case 2:
                    displayUserFavoritesWithNumbers();
                    System.out.print("Introduceti numarul din lista de preferate pentru a il sterge: ");
                    int favoriteNumberToDelete = 0;
                    try {
                        favoriteNumberToDelete = scanner.nextInt();
                    } catch (InputMismatchException e) {
                    }
                    scanner.nextLine(); // Consume the newline character

                    if (favoriteNumberToDelete >= 1 && favoriteNumberToDelete <= IMDB.user_logat.getFavorite().size()) {
                        String favoriteToDelete = (String) IMDB.user_logat.getFavorite().get(favoriteNumberToDelete - 1);
                        IMDB.user_logat.sterge_un_favorit(favoriteToDelete);
                        System.out.println("Favorita stearsa cu succes");
                    } else {
                        System.out.println("Numar invalid. Va rugam reincercati!");
                    }
                    break;

                case 3:
                    System.out.println("Lista mea de favorite: ");
                    List<String> favorites = IMDB.user_logat.getFavorite();
                    for (String favorite : favorites) {
                        System.out.println("- " + favorite);
                    }
                    break;

                case 4:
                    System.out.println("Iesire din managerul de favorite.");
                    return;

                default:
                    System.out.println("Alegere invalida. Va rugam alegeti un numar de la 1 la 4");
            }
        }
    }


    public class manageUsers {

        private Scanner scanner;

        public manageUsers() {
            scanner = new Scanner(System.in);
        }

        public boolean manageUsers() {
            boolean exit = false;

            while (true) {
                displayUserManagementMenu();
                int choice = getUserChoice();

                switch (choice) {
                    case 1:
                        addUser();
                        break;
                    case 2:
                        deleteUser();
                        break;
                    case 3:
                        return false;
                    default:
                        System.out.println("Alegere invalida. Introduceti un numar de la 1 la 3");
                }
            }
        }

        private void displayUserManagementMenu() {
            System.out.println("Adaugare/Stergere useri");
            System.out.println("1. Adaugare User");
            System.out.println("2. Stergere User");
            System.out.println("3. Iesire");
            System.out.print("Introdu alegerea(numar de la 1 la 3): ");
        }

        private int getUserChoice() {
            int choice = -1;

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                // Ignore and let choice remain -1
            }

            return choice;
        }

        private void addUser() {
            System.out.println("Adauga user nou...");

            // Get values from the user input
            String name = getUserInput("Introduceti nume: ");
            String username = getUserInput("Introduceti username: ");
            String email = getUserInput("Introduceti email: ");
            String password = generateStrongPassword(12);
            String userType = getUserInput("Alege tipul de user (Regular/Contributor/Admin): ");

            // Validate input
            if (name.isEmpty() || username.isEmpty() || email.isEmpty()) {
                System.out.println("Error: Please Introduceti valid information.");
                return;
            }

            // Create a new user with the provided information
            User newUser = new User();
            newUser.setName(name);
            newUser.setUserType(userType);
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setGender(null);
            newUser.setCountry(null);
            newUser.setEmail(email);
            newUser.setAge(null);
            newUser.setBirthdate(null);
            newUser.setNotifications(null);
            IMDB.lista_useri.add(newUser);

            // Add the new user to your user list or perform any necessary actions
            // For now, let's just print the user information
            System.out.println("Userul nou a fost adaugat cu succes!");
            newUser.displayinfo_mare();
        }

        private void deleteUser() {
            System.out.println("Stergere user...");

            // Get user input for username to delete
            String usernameToDelete = getUserInput("Introduceti usernameul pentru a sterge: ");

            // Find the user in your list and delete them
            User userToDelete = findUserByUsername(usernameToDelete);
            if (userToDelete != null) {
                // Implement logic to delete user and associated data
                IMDB.lista_useri.remove(userToDelete);
                System.out.println("User sters:");
                userToDelete.displayinfo_mare();
            } else {
                System.out.println("Userul nu a fost gasit.");
            }
        }

        private String getUserInput(String prompt) {
            System.out.print(prompt);
            return scanner.nextLine();
        }

        private User findUserByUsername(String username) {
            // Implement logic to find a user in your list by username
            // Return null if the user is not found
            for (User user : IMDB.lista_useri) {
                if (user.getUsername().equalsIgnoreCase(username)) {
                    return user;
                }
            }
            return null;
        }

        private String generateStrongPassword(int length) {
            // Implement your password generation logic here
            // This is a simple example; you may want to use a more sophisticated approach
            String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
            String NUMBERS = "0123456789";
            String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/";

            String ALL_CHARACTERS = UPPERCASE + LOWERCASE + NUMBERS + SPECIAL_CHARACTERS;

            Random random = new Random();
            StringBuilder password = new StringBuilder();

            for (int i = 0; i < length; i++) {
                int randomIndex = random.nextInt(ALL_CHARACTERS.length());
                char randomChar = ALL_CHARACTERS.charAt(randomIndex);
                password.append(randomChar);
            }

            return password.toString();
        }

        public void main(String[] args) {
            Admin admin = new Admin();
            manageUsers();
        }
    }

    private void manageSystem() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Management sistem.");
            System.out.println("1. Adauga");
            System.out.println("2. Sterge");
            System.out.println("0. Iesire");

            System.out.print("Introdu alegerea: ");
            int choice = 0;
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
            }

            switch (choice) {
                case 1:
                    addNewItem();
                    break;
                case 2:
                    deleteItem();
                    break;
                case 0:
                    System.out.println("Iesire...");
                    return;
                default:
                    System.out.println("Alegere invalida. Va rugam reincercati.");
            }
        }
    }

    private void addNewItem() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. Adauga Actor");
        System.out.println("2. Adauga Film");
        System.out.println("3. Adauga Serial");
        System.out.print("Introduceti alegerea. ");
        int choice = 0;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
        }

        switch (choice) {
            case 1:
                // Add logic to add a new actor
                System.out.println("Adaugare actor nou:");
                // Prompt for actor's name
                System.out.print("Introduceti numele actorului: ");
                scanner.nextLine();
                String actorName = scanner.nextLine();

                // Prompt for actor's biography
                System.out.print("Introduceti biografia actorului ");
                String actorBiography = scanner.nextLine();

                // Create a new actor object
                Actor newActor = new Actor(actorName);
                newActor.addBiografie(actorBiography);

                // Prompt for roles until the user decides to stop
                while (true) {
                    // Prompt for role name
                    System.out.print("Introduceți numele rolului (apăsați Introduceti pentru a termina adăugarea rolurilor): ");
                    String roleName = scanner.nextLine();
                    if (roleName.isEmpty()) {
                        break;  // Exit the loop if Introduceti is pressed
                    }

                    // Prompt for role type
                    System.out.print("Introduceti tipul de rol jucat: ");
                    String roleType = scanner.nextLine();

                    // Add the role to the actor
                    newActor.addRole(roleName, roleType);
                }

                // Add the new actor to the system
                IMDB.actorList.add(newActor);
                IMDB.user_logat.adauga_contributie_actori(newActor);
                ExperienceStrategy strategy = new AddActorStrategy();
                int experiencePoints = strategy.calculateExperience();
                IMDB.user_logat.addExperience(experiencePoints);

                System.out.println("Actor adaugat cu succes");
                break;
            case 2:
                // Add logic to add a new movie
                System.out.println("Adaugati un nou film:");

                // Prompt for movie title
                System.out.print("Introduceti numele filmului: ");
                scanner.nextLine();
                String movieTitle = scanner.nextLine();

                // Prompt for movie type
                String movieType = "Movie";

                // Create a new movie object
                Movie newMovie = new Movie(movieTitle, movieType);

                // Prompt for movie details
                System.out.print("Introduceti directorii: ");
                newMovie.movie_add_directors(scanner.nextLine());

                System.out.print("Introduceti actorii: ");
                newMovie.movie_add_actori(scanner.nextLine());

                System.out.print("Introduceti genurile in care se incadreaza: ");
                newMovie.movie_add_genre(scanner.nextLine());

                System.out.print("Introduceti intriga: ");
                newMovie.setPlot(scanner.nextLine());

                System.out.print("Introduceti rating mediu: ");
                String averageRating = scanner.nextLine();
                newMovie.setRating(averageRating);

                System.out.print("Introduceti durata: ");
                newMovie.setDuration(scanner.nextLine());

                System.out.print("Introduceti anul de lansare: ");
                newMovie.setReleaseYear(scanner.nextLine());

                // Add the new movie to the system
                IMDB.lista_filme.add(newMovie);
                IMDB.user_logat.adauga_productii2(movieTitle);
                ExperienceStrategy strategy2 = new AddProductionStrategy();
                int experiencePoints2 = strategy2.calculateExperience();
                IMDB.user_logat.addExperience(experiencePoints2);

                System.out.println("Filmul a fost adaugat cu succes!");
                break;
            case 3:
                // Add logic to add a new series
                System.out.println("Adaugare serial");

                // Prompt for series title
                System.out.print("Introduceti titlul serialului ");
                scanner.nextLine();
                String seriesTitle = scanner.nextLine();

                // Prompt for series type
                String seriesType = "Series";
                // Prompt for series directors
                System.out.print("Introduceti directorii ");
                String seriesDirectors = scanner.nextLine();

                // Prompt for series actors
                System.out.print("Introduceti actorii ");
                String seriesActors = scanner.nextLine();

                // Prompt for series genres
                System.out.print("Introduceti genurile: ");
                String seriesGenres = scanner.nextLine();

                // Prompt for series plot
                System.out.print("Introduceti intriga: ");
                String seriesPlot = scanner.nextLine();

                // Prompt for series release year
                System.out.print("Introduceti anul lansarii: ");
                String seriesReleaseYear = scanner.nextLine();

                // Prompt for the number of seasons
                System.out.print("Introduceti numarul de sezoane: ");
                int numberOfSeasons = 0;
                try {
                    numberOfSeasons = scanner.nextInt();
                } catch (InputMismatchException e) {
                }
                scanner.nextLine(); // Consume the newline character

                // Create a new series object
                Series newSeries = new Series(seriesType, seriesReleaseYear, seriesTitle);

                // Set series details
                newSeries.setDirectors(seriesDirectors);
                newSeries.setActors(seriesActors);
                newSeries.setGenre(seriesGenres);
                newSeries.setPlot(seriesPlot);
                newSeries.setNumberOfSeasons(numberOfSeasons);

                // Prompt for each season
                for (int i = 1; i <= numberOfSeasons; i++) {
                    System.out.println("Introduceti detaliile pentru sezonul " + i + " (Apasati Enter pentru a termina): ");

                    // Create a StringBuilder to store details for the current season
                    StringBuilder seasonDetailsBuilder = new StringBuilder();

                    seasonDetailsBuilder.append("Season ").append(i).append(":\n");

                    // Continue adding episodes until the user presses Introduceti directly
                    int episodeNumber = 1;
                    while (true) {
                        System.out.print("Introduceti detalii pentru Episoade " + episodeNumber + " (" +
                                "Apasati Enter pentru a termina): ");
                        // Prompt for episode name
                        System.out.print("Introduceti numele episodului: ");
                        String episodeName = scanner.nextLine();

                        // Check if the name is empty (user pressed Introduceti directly)
                        if (episodeName.isEmpty()) {
                            break;
                        }

                        // Prompt for episode duration
                        System.out.print("Introduceti durata episodului: ");
                        String episodeDuration = scanner.nextLine();

                        // Append episode details to the season
                        seasonDetailsBuilder.append("episodeName: ").append(episodeName)
                                .append(", duration: ").append(episodeDuration).append("\n");

                        episodeNumber++;
                    }

                    // Add the season details to the series
                    newSeries.series_add_sezoane(seasonDetailsBuilder.toString());
                }


                // Add the new series to the system
                IMDB.lista_seriale.add(newSeries);
                IMDB.user_logat.adauga_productii2(seriesTitle);
                ExperienceStrategy strategy3 = new AddReviewStrategy();
                int experiencePoints3 = strategy3.calculateExperience();
                IMDB.user_logat.addExperience(experiencePoints3);
                System.out.println("Serial nou adaugat cu succes");
                break;
            default:
                System.out.println("Alegere invalida. Va rugam reincercati.");
        }
    }

    private void deleteItem() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. Sterge Actor");
        System.out.println("2. Sterge Film");
        System.out.println("3. Sterge Serial");
        System.out.print("Introduceti alegerea : ");
        int choice = 0;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
        }
        if (IMDB.user_logat.getUserType().equalsIgnoreCase("ADMIN")) {
            switch (choice) {
                case 1:
                    System.out.println("Toti actorii:");
                    for (int i = 0; i < IMDB.actorList.size(); i++) {
                        System.out.println((i + 1) + ". Actor: " + IMDB.actorList.get(i).getName());
                    }

                    // Let the user choose an actor to delete
                    System.out.print("Introduceti numarul actorului pentru a il sterge (0 pentru a anula): ");
                    int actorChoice = 0;
                    try {
                        actorChoice = scanner.nextInt();
                    } catch (InputMismatchException e) {
                    }

                    if (actorChoice > 0 && actorChoice <= IMDB.actorList.size()) {
                        Actor actorToDelete = IMDB.actorList.get(actorChoice - 1);
                        IMDB.actorList.remove(actorToDelete);
                        System.out.println("Actor sters cu succes");
                    } else if (actorChoice != 0) {
                        System.out.println("Alegere invalida a actorului. Nu s-a sters nimic.");
                    }
                    break;
                case 2:
                    // Display all movies with numbers
                    System.out.println("Toate filmele: ");
                    for (int i = 0; i < IMDB.lista_filme.size(); i++) {
                        System.out.println((i + 1) + ". Titlu film: " + IMDB.lista_filme.get(i).gettitle());
                    }

                    // Let the user choose a movie to delete
                    System.out.print("Introduceti numarul filmului pentru a sterge (0 pentru a anula): ");
                    int movieChoice = 0;
                    try {
                        movieChoice = scanner.nextInt();
                    } catch (InputMismatchException e) {
                    }
                    if (movieChoice > 0 && movieChoice <= IMDB.lista_filme.size()) {
                        Movie movieToDelete = IMDB.lista_filme.get(movieChoice - 1);
                        IMDB.lista_filme.remove(movieToDelete);
                        System.out.println("Film sters cu succes");
                    } else if (movieChoice != 0) {
                        System.out.println("Alegere invalida a filmului. Nu s-a sters nimic.");
                    }
                    break;
                case 3:
                    // Display all series with numbers
                    System.out.println("Toate Serialele:");
                    for (int i = 0; i < IMDB.lista_seriale.size(); i++) {
                        System.out.println((i + 1) + ". Titlu serial: " + IMDB.lista_seriale.get(i).series_get_title());
                    }

                    // Let the user choose a series to delete
                    System.out.print("Introduceti numarul serialului pentru a il sterge (0 pentru a anula): ");
                    int seriesChoice = 0;
                    try {
                        seriesChoice = scanner.nextInt();
                    } catch (InputMismatchException e) {
                    }

                    if (seriesChoice > 0 && seriesChoice <= IMDB.lista_seriale.size()) {
                        Series seriesToDelete = IMDB.lista_seriale.get(seriesChoice - 1);
                        IMDB.lista_seriale.remove(seriesToDelete);
                        System.out.println("Serial sters cu succes!");
                    } else if (seriesChoice != 0) {
                        System.out.println("Alegere invalida a serialului. Nu s-a sters nimic.");
                    }
                    break;
                default:
                    System.out.println("Alegere invalida. Va rugam reincercati.");
            }
        }
        if (IMDB.user_logat.getUserType().equalsIgnoreCase("Contributor")) {
            switch (choice) {
                case 1:
                    SortedSet<Actor> lista_actori = IMDB.user_logat.contributie_actori;
                    System.out.println("Contributia ta la actori:");
                    int indexActor = 1;
                    for (Actor actor : lista_actori) {
                        System.out.println(indexActor + ". Actor: " + actor.getName());
                        indexActor++;
                    }

                    // Let the user choose an actor to delete
                    System.out.print("Introduceti numarul actorului pentru a il sterge (0 pentru a anula): ");
                    int actorChoice = 0;
                    try {
                        actorChoice = scanner.nextInt();
                    } catch (InputMismatchException e) {
                    }

                    if (actorChoice > 0 && actorChoice <= lista_actori.size()) {
                        // Find the actor at the selected index in the sorted set
                        Iterator<Actor> iterator = lista_actori.iterator();
                        Actor actorToDelete = null;
                        for (int i = 1; i <= actorChoice; i++) {
                            actorToDelete = iterator.next();
                        }

                        // Remove the actor from the sorted set
                        lista_actori.remove(actorToDelete);
                        String nume = actorToDelete.getNume();
                        Actor actor_nou = IMDB.findActorByName(nume);
                        IMDB.actorList.remove(actor_nou);

                        System.out.println("Actor sters cu succes");
                    } else if (actorChoice != 0) {
                        System.out.println("Alegere invalida a actorului. Nu s-a sters nimic.");
                    }
                    break;
                case 2:
                    // Display all movies with numbers
                    SortedSet<Production> productii = IMDB.user_logat.get_contributie_productii();
                    if (productii.size() == 0) {
                        System.out.println("Nu exista filme de sters");
                        break;
                    }
                    List<Movie> filme = new ArrayList<>();

                    for (Production prod : productii) {
                        String nume = prod.get_title();
                        Production prod1 = IMDB.findProductionByName(nume);
                        if (prod1 instanceof Movie) {
                            filme.add((Movie) prod1);
                        }
                    }
                    System.out.println("Filme de contributor: ");
                    for (int i = 0; i < filme.size(); i++) {
                        System.out.println((i + 1) + ". Titlu film: " + filme.get(i).gettitle());
                    }

                    // Let the user choose a movie to delete
                    System.out.print("Introduceti numarul filmului pentru a sterge (0 pentru a anula): ");
                    int movieChoice = 0;
                    try {
                        movieChoice = scanner.nextInt();
                    } catch (InputMismatchException e) {
                    }
                    if (movieChoice > 0 && movieChoice <= filme.size()) {
                        Movie movieToDelete = filme.get(movieChoice - 1);
                        String nume = movieToDelete.get_title();
                        IMDB.user_logat.elimina_contributie_productie(nume);
                        IMDB.lista_filme.remove(movieToDelete);
                        System.out.println("Film sters cu succes");
                    } else if (movieChoice != 0) {
                        System.out.println("Alegere invalida a filmului. Nu s-a sters nimic.");
                    }
                    break;
                case 3:
                    SortedSet<Production> productii2 = IMDB.user_logat.get_contributie_productii();
                    List<Series> seriale = new ArrayList<>();
                    for (Production prod : productii2) {
                        String nume = prod.get_title();
                        Production prod1 = IMDB.findProductionByName(nume);
                        if (prod1 instanceof Series) {
                            seriale.add((Series) prod1);
                        }
                    }
                    System.out.println("Seriale de contributor: ");
                    for (int i = 0; i < seriale.size(); i++) {
                        System.out.println((i + 1) + ". Titlu serial: " + seriale.get(i).series_get_title());
                    }
                    // Let the user choose a series to delete
                    System.out.print("Introduceti numarul serialului pentru a il sterge (0 pentru a anula): ");
                    int seriesChoice = 0;
                    try {
                        seriesChoice = scanner.nextInt();
                    } catch (InputMismatchException e) {
                    }

                    if (seriesChoice > 0 && seriesChoice <= seriale.size()) {
                        Series seriesToDelete = seriale.get(seriesChoice - 1);
                        String nume = seriesToDelete.series_get_title();
                        System.out.println(nume);
                        //IMDB.user_logat.elimina_contributie_productie(nume);
                        IMDB.lista_seriale.remove(seriesToDelete);
                        System.out.println("Serial sters cu succes!");
                    } else if (seriesChoice != 0) {
                        System.out.println("Alegere invalida a serialului. Nu s-a sters nimic.");
                    }
                    break;
                default:
                    System.out.println("Alegere invalida. Va rugam reincercati.");
            }
        }
    }

    private static void UpdateMovieDetails() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Lista productii");
        int index = 1;
        System.out.println("Lista filme");
        for (Movie movie : IMDB.lista_filme) {
            System.out.println(index + ". " + movie.get_title());
            index++;
        }
        System.out.println("Lista seriale");
        for (Series series : IMDB.lista_seriale) {
            System.out.println(index + ". " + series.series_get_title());
            index++;
        }

        System.out.print("Introduceti numarul pentru film/serial pentru editare (0 pentru iesire): ");
        int choice = 0;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
        }

        if (choice == 0) {
            System.out.println("Iesire...");
            return;
        }

        if (choice >= 1 && choice <= IMDB.lista_filme.size() + IMDB.lista_seriale.size()) {
            if (choice <= IMDB.lista_filme.size()) {
                Movie selectedMovie = IMDB.lista_filme.get(choice - 1);

                // Display movie details
                System.out.println("Editare film: " + selectedMovie.gettitle());

                // Edit title
                System.out.print("Introduceti titlu nou (Apasati enter pentru a lasa neschimbat): ");
                scanner.nextLine();
                String newTitle = scanner.nextLine();
                if (!newTitle.isEmpty()) {
                    selectedMovie.settitle(newTitle);
                }

                // Edit directors
                System.out.print("Introduceti directori noi (Apasati Enter pentru a pastra neschimbat): ");
                String newDirectors = scanner.nextLine();
                if (!newDirectors.isEmpty()) {
                    selectedMovie.movie_add_directors(newDirectors);
                }

                // Edit actors
                System.out.print("Introduceti actori noi (Apasati Enter pentru a pastra neschimbat): ");
                String newActors = scanner.nextLine();
                if (!newActors.isEmpty()) {
                    selectedMovie.movie_add_actori(newActors);
                }

                // Edit genre
                System.out.print("Introduceti genuri noi (Apasati Enter pentru a pastra neschimbat): ");
                String newGenre = scanner.nextLine();
                if (!newGenre.isEmpty()) {
                    selectedMovie.movie_add_genre(newGenre);
                }

                // Edit plot
                System.out.print("Introduceti intriga noua (Apasati Enter pentru a pastra neschimbat): ");
                String newPlot = scanner.nextLine();
                if (!newPlot.isEmpty()) {
                    selectedMovie.setPlot(newPlot);
                }

                // Edit duration
                System.out.print("Introduceti durata noua (Apasati Enter pentru a pastra neschimbat): ");
                String newDuration = scanner.nextLine();
                if (!newDuration.isEmpty()) {
                    selectedMovie.setDuration(newDuration);
                }

                // Edit release year
                System.out.print("Introduceti noul an al lansarii (Apasati Enter pentru a pastra neschimbat): ");
                String newReleaseYear = scanner.nextLine();
                if (!newReleaseYear.isEmpty()) {
                    selectedMovie.setReleaseYear(newReleaseYear);
                }

                System.out.println("Detalii film updatate cu succes!");
            } else {
                if (choice > IMDB.lista_filme.size() && choice <= IMDB.lista_seriale.size() + IMDB.lista_filme.size()) {
                    Series selectedSeries = IMDB.lista_seriale.get(choice - IMDB.lista_filme.size() - 1);

                    // Display series details
                    System.out.println("Editare seriale: " + selectedSeries.series_get_title());

                    // Edit title
                    System.out.print("Introduceti noul titlu (Apasati Enter pentru a pastra neschimbat): ");
                    scanner.nextLine();
                    String newTitle = scanner.nextLine();
                    if (!newTitle.isEmpty()) {
                        selectedSeries.series_set_title(newTitle);
                    }

                    // Edit directors
                    System.out.print("Introduceti directori noi (Apasati Enter pentru a pastra neschimbat): ");
                    String newDirectors = scanner.nextLine();
                    if (!newDirectors.isEmpty()) {
                        selectedSeries.setDirectors(newDirectors);
                    }

                    // Edit actors
                    System.out.print("Introduceti actori noi (Apasati Enter pentru a pastra neschimbat): ");
                    String newActors = scanner.nextLine();
                    if (!newActors.isEmpty()) {
                        selectedSeries.setActors(newActors);
                    }

                    // Edit genre
                    System.out.print("Introduceti genuri noi (Apasati Enter pentru a pastra neschimbat): ");
                    String newGenre = scanner.nextLine();
                    if (!newGenre.isEmpty()) {
                        selectedSeries.setGenre(newGenre);
                    }

                    // Edit plot
                    System.out.print("Introduceti intriga noua (Apasati Enter pentru a pastra neschimbat): ");
                    String newPlot = scanner.nextLine();
                    if (!newPlot.isEmpty()) {
                        selectedSeries.setPlot(newPlot);
                    }

                    // Edit release year
                    System.out.print("Introduceti noul an de lansare (Apasati Enter pentru a pastra neschimbat): ");
                    String newReleaseYear = scanner.nextLine();
                    if (!newReleaseYear.isEmpty()) {
                        selectedSeries.setReleaseYear(newReleaseYear);
                    }
                    //Edit episodes
                    int sezon2 = selectedSeries.getNumberOfSeasons();
                    System.out.println(sezon2);
                    for (int i = 1; i <= sezon2; i++) {
                        String sezon = selectedSeries.series_get_sezoane(i);
                        String seasonInfo = sezon;

                        System.out.println("Editare sezoane " + i);

                        // Display current season details
                        System.out.println("Detalii sezon curent: " + seasonInfo);

                        // Edit season details
                        System.out.print("Introduceti titlu nou pentru sezon (Apasati Enter pentru a pastra neschimbat): ");
                        String newSeasonTitle = scanner.nextLine();
                        if (!newSeasonTitle.isEmpty()) {
                            selectedSeries.series_set_sezoane(i, newSeasonTitle);
                        }

                        // Edit episodes within the season
                        String[] episodesInfo = seasonInfo.split("episodeName:");

                        // Start from index 1 to skip the first empty element
                        for (int j = 1; j < episodesInfo.length; j++) {
                            String episodeInfo = episodesInfo[j];

                            // Split episodeInfo to get episode details
                            String[] parts = episodeInfo.split("duration:");
                            String episodeName = parts[0].replaceAll(",", "").trim();
                            String duration = parts[1].replaceAll(",", "").trim();

                            // Now episodeName and duration contain the information for each episode
                            System.out.println("Editare episod: " + episodeName);

                            // Edit episode details
                            System.out.print("Introduceti noua durata pentru episod (Apasati Enter pentru a pastra neschimbat): ");
                            String newDuration = scanner.nextLine();
                            if (!newDuration.isEmpty()) {
                                duration = newDuration;
                            }
                            // Add more fields to edit for each episode if needed
                        }
                    }

                    System.out.println("Detalii serial actualizate cu succes!");
                }
            }
        } else {
            System.out.println("Alegere invalida. Va rugam reincercati.");
        }
    }


    private static void updateActorDetails() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Lista actorilor:");
        for (int i = 0; i < IMDB.actorList.size(); i++) {
            System.out.println((i + 1) + ". " + IMDB.actorList.get(i).getName());
        }

        System.out.print("Introduceti numarul actorului pe care doriti sa-l editati: ");
        int actorNumber = 0;
        try {
            actorNumber = scanner.nextInt();
        } catch (InputMismatchException e) {
        }

        if (actorNumber >= 1 && actorNumber <= IMDB.actorList.size()) {
            Actor selectedActor = IMDB.actorList.get(actorNumber - 1);

            // Display actor details
            System.out.println("Detalii actor: " + selectedActor.getName());
            System.out.println("Nume: " + selectedActor.getName());
            System.out.println("Biografie: " + selectedActor.getBiografie());

            // Display roles
            List<Actor.NameTypePair> roles = selectedActor.getRoluri();
            System.out.println("Roluri:");
            for (int i = 0; i < roles.size(); i++) {
                System.out.println((i + 1) + ". " + roles.get(i).getName() + " - " + roles.get(i).getType());
            }

            // Role editing
            System.out.print("Doriti sa editati rolurile? (Da/Nu): ");
            String editRolesOption = scanner.next();

            if (editRolesOption.equalsIgnoreCase("da")) {
                System.out.print("Introduceti numarul rolului pe care doriti sa-l editati (sau 0 pentru a adauga un nou rol): ");
                int roleNumber = 0;
                try {
                    roleNumber = scanner.nextInt();
                } catch (InputMismatchException e) {
                }
                scanner.nextLine();

                if (roleNumber == 0) {
                    // Add a new role
                    System.out.print("Numele noului rol: ");
                    String roleName = scanner.nextLine();
                    System.out.print("Tipul noului rol: ");
                    String roleType = scanner.nextLine();

                    selectedActor.addRole(roleName, roleType);
                    System.out.println("Rolul a fost adaugat cu succes!");
                } else if (roleNumber >= 1 && roleNumber <= roles.size()) {
                    // Edit an existing role
                    Actor.NameTypePair selectedRole = roles.get(roleNumber - 1);
                    System.out.print("Noul nume pentru rol: ");
                    String newRoleName = scanner.nextLine();
                    System.out.print("Noul tip pentru rol: ");
                    String newRoleType = scanner.nextLine();

                    selectedRole.setName(newRoleName);
                    selectedRole.setType(newRoleType);

                    System.out.println("Rolul a fost editat cu succes!");
                } else {
                    System.out.println("Numarul rolului introdus nu este valid.");
                }
            }

            // Additional attributes can be edited in a similar way
            // ...

            System.out.println("Actorul a fost actualizat cu succes!");
        } else {
            System.out.println("Numarul introdus nu este valid.");
        }
    }


    public void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (IMDB.actorList.isEmpty()) {
                System.out.println("Niciun actor disponibil pentru editare.");
            } else {
                // Call the updateActorDetails() method directly
                updateActorDetails();
            }
        });
    }

    public class RequestsHolder {

        private static List<Request> adminRequests;
        private static Scanner scanner = new Scanner(System.in);

        public RequestsHolder(List<Request> adminRequests) {
            this.adminRequests = adminRequests;
        }

        public static void solveRequest(List<Request> adminRequests) {
            if (adminRequests == null) {
                System.out.println("Lista adminilor nu este initializata");
                return;
            }

            System.out.println("Alegeti o cerere pentru a o rezolva");

            // Display available requests
            int index = 1;
            for (Request request : IMDB.lista_cereri_admin) {
                System.out.println(index + ". " + request.getProblemDescription());
                index++;
            }

            // Prompt for user input
            System.out.print("Introduceti numarul cererii pe care vreti sa o rezolvati (sau 0 pentru iesire): ");
            int choice = 0;
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
            }
            scanner.nextLine(); // Consume the newline character

            if (choice >= 1 && choice <= IMDB.lista_cereri_admin.size()) {
                // Valid choice, solve the selected request
                Request selectedRequest = IMDB.lista_cereri_admin.get(choice - 1);
                solveRequest(selectedRequest);
            } else if (choice == 0) {
                System.out.println("Iesire cerere rezolvata");
            } else {
                System.out.println("Alegere invalida!");
            }
        }

        private static void solveRequest(Request request) {
            // Implement logic to solve the request
            System.out.println("Rezolvare cerere: " + request.getProblemDescription());

            // Ask the user if they want to accept or refuse the request
            System.out.println("Doriti sa acceptati cerera? (da/nu): ");
            String response = scanner.nextLine().toLowerCase();

            if (response.equalsIgnoreCase("da")) {
                // User accepted the request
                // Example: Mark the request as solved
                IMDB.lista_cereri_admin.remove(request);

                // Notify the user who sent the request
                String username = request.getCreatorUsername();
                String notificationMessage = "Cererea dumneavoastra a fost acceptata.";
                for (User user : IMDB.lista_useri) {
                    if (user.getUsername().equals(username)) {
                        user.adauga_notificare(notificationMessage + " by user " + IMDB.user_logat.getUsername());
                        UserNotificationHandler userObserver = new UserNotificationHandler(user);
                        user.addObserver(userObserver);
                        user.notifyObservers(notificationMessage + " by user " + IMDB.user_logat.getUsername());
                        IMDB.user_logat.removeObserver(userObserver);
                        // Add experience points to the user who accepted the request
                        ExperienceStrategy strategy = new ResolveIssueStrategy();
                        int experiencePoints = strategy.calculateExperience();
                        IMDB.user_logat.addExperience(experiencePoints);
                        break; // Assuming usernames are unique, stop searching once found
                    }
                }
            } else if (response.equalsIgnoreCase("no") || response.equalsIgnoreCase("nu")) {
                // User refused the request
                // Example: Mark the request as not solved or take appropriate action
                System.out.println("Cerere refuzata de userul " + IMDB.user_logat.getUsername());
            } else {
                System.out.println("Raspuns invalid. Va rugam introduceti 'da' sau 'nu'.");
            }
        }

        public void main(String[] args) {
            RequestsHolder requestsHolder = new RequestsHolder(IMDB.lista_cereri_admin);
        }
    }

    public class TextRequestManager {

        private static User user_primeste = new User();

        public static void main(String[] args) {
            start();
        }

        public static void start() {
            System.out.println("Manager de cereri");
            initializeUser();

            Scanner scanner = new Scanner(System.in);
            int choice = 0;

            do {
                displayMainMenu();
                System.out.print("Introduceti alegerea : ");
                try {
                    choice = scanner.nextInt();
                } catch (InputMismatchException e) {
                }
                scanner.nextLine();  // Consume newline

                switch (choice) {
                    case 1:
                        showSentRequests();
                        break;
                    case 2:
                        showReceivedRequests();
                        break;
                    case 3:
                        createNewRequest();
                        break;
                    case 4:
                        deleteRequest();
                        break;
                    case 5:
                        System.out.println("Iesire manager de cereri!");
                        break;
                    default:
                        System.out.println("Alegere invalida. Va rugam reincercati.");
                }
            } while (choice != 5);
        }

        private static void initializeUser() {
        }

        private static void displayMainMenu() {
            System.out.println("\nMeniu principal:");
            System.out.println("1. Cereri trimise");
            System.out.println("2. Cereri primite");
            System.out.println("3. Creaza o cerere noua");
            System.out.println("4. Sterge cererea");
            System.out.println("5. Iesire");
        }

        private static void showSentRequests() {
            System.out.println("\nCereri trimise:");
            List<Request> cereri_trimise2 = IMDB.user_logat.getListaCereriTrimise();
            for (Request request : cereri_trimise2) {
                request.display_info();
            }

            // Implement logic to display sent requests
            // Use user_logat.getListaCereriTrimise() to get the list of sent requests
        }

        private static void showReceivedRequests() {
            System.out.println("\nCereri primite");
            List<Request> cereri_primte2 = IMDB.user_logat.getCereri_primite();
            for (Request request : cereri_primte2) {
                request.display_info();
            }
            if (IMDB.user_logat.getUserType().equalsIgnoreCase("Admin")) {
                // Iterate over lista_cereri_admin and add each request to the user's received requests
                for (Request adminRequest : IMDB.lista_cereri_admin) {
                    if (!IMDB.user_logat.getAllReceivedRequests().contains(adminRequest)) {
                        IMDB.user_logat.addRequestprimite(adminRequest);
                        adminRequest.display_info();
                    }
                }
            }
            // Implement logic to display received requests
            // Use user_logat.getListaCereriPrimite() to get the list of received requests
        }

        private static void createNewRequest() {
            System.out.println("Creaza o noua cerere...");

            Scanner scanner = new Scanner(System.in);

            // Auto-generate date and use the current user's username
            String pattern = "yyyy-MM-dd'T'HH:mm:ss";
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            String formattedDateTime = currentDateTime.format(formatter);
            String createdDateString = formattedDateTime;
            LocalDateTime createdDate = LocalDateTime.parse(createdDateString, DateTimeFormatter.ofPattern(pattern));

            String username = IMDB.user_logat.getUsername(); // Use the current user's username

            // User chooses the type of request
            int requestTypeOption = -1;
            do {
                System.out.println("Alege tipul de cerere:");
                System.out.println("1. Sterge cont");
                System.out.println("2. Probleme cu actorii");
                System.out.println("3. Probleme cu filmele");
                System.out.println("4. Altele");
                System.out.print("Introduceti numarul: ");
                try {
                    requestTypeOption = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character
                } catch (InputMismatchException e) {
                    System.out.println("Optiune invalida. Va rugam introduceti un numar.");
                    scanner.nextLine(); // Consume the invalid input
                }
            } while (requestTypeOption < 1 || requestTypeOption > 4);

            String type;
            switch (requestTypeOption) {
                case 1:
                    type = "DELETE_ACCOUNT";
                    break;
                case 2:
                    type = "ACTOR_ISSUE";
                    break;
                case 3:
                    type = "MOVIE_ISSUE";
                    break;
                case 4:
                    type = "OTHERS";
                    break;
                default:
                    type = "OTHERS";
            }
            String actorName = "";
            String movieName = "";
            String to = "ADMIN";
            if (requestTypeOption == 2) {
                System.out.print("Introduceti numele actorului: ");
                actorName = scanner.nextLine();
            }
            if (requestTypeOption == 3) {
                System.out.print("Introduceti titlu film/serial: ");
                movieName = scanner.nextLine();
            }

            System.out.print("Introduceti descrierea cererii: ");
            String description = scanner.nextLine();

            // Simulate finding the recipient
            for (User user : IMDB.lista_useri) {
                Actor actor1 = new Actor("nume");
                Movie movie1 = new Movie("Movie", "Movie");
                Series series1 = new Series("Series", "1000", "titlu");
                String actorsContribution = user.getActorsContribution();
                String productionsContribution = user.getProductionsContribution();

                for (Actor actor : IMDB.actorList) {
                    if (actor.getNume().equalsIgnoreCase(actorName)) {
                        actor1.setName(actorName);
                    }
                }
                System.out.println(actor1.getName());
                SortedSet<Actor> cont = user.get_contributie_actori();
                if (actorsContribution != null && cont.contains(actor1)) {
                    to = user.getUsername();
                    break;  // Update only once for the first match
                }
                for (Movie movie : IMDB.lista_filme) {
                    if (movie.get_title().equalsIgnoreCase(movieName)) {
                        to = user.getUsername();
                        break;
                    }
                }
                for (Series series : IMDB.lista_seriale) {
                    if (series.series_get_title().equalsIgnoreCase(movieName)) {
                        to = user.getUsername();
                        break;
                    }
                }
                SortedSet<Production> nou = user.get_contributie_productii();
                if (productionsContribution != null && nou.contains((Production) movie1)) {
                    to = user.getUsername();
                    user_primeste = user;
                    break;  // Update only once for the first match
                }
                to = "ADMIN";
            }
            // Simulate creating a request
            Request request = new Request(type, createdDate, username, actorName, movieName, to, description);
            System.out.println("\nCerere creata cu succes!");
            System.out.println("\nDetalii cerere:");
            IMDB.user_logat.addRequest(request);
            User user = IMDB.findUserByUsername(to);
            if (user != null && user.getUsername() != null && user.getUserType().equalsIgnoreCase("Contributor")) {
                user.addRequestprimite(request);
                user.adauga_notificare("Ati primit o cerere de la userul " + IMDB.user_logat.getUsername());
            }
            if (user != null && user.getUserType().equalsIgnoreCase("Admin")) {
                IMDB.lista_cereri_admin.add(request);
            }
            if (user == null)
                IMDB.lista_cereri_admin.add(request);
            request.display_info();
        }

        // Simulate finding a suitable recipient based on actor name and movie title

        private static void deleteRequest() {
            List<Request> cereri = IMDB.user_logat.getListaCereriTrimise();

            if (cereri.isEmpty()) {
                System.out.println("Nicio cerere de sters");
                return;
            }

            System.out.println("Cereri trimise: ");
            int i = 0;
            for (Request request : cereri) {
                System.out.println((i + 1) + ". ");
                request.display_info();
                i++;
            }

            Scanner scanner = new Scanner(System.in);

            // Ask the user to choose a request number for deletion
            System.out.print("Introduceti numarul cererii pentru a o sterge ( 0 pentru iesire): ");
            int selectedNumber;
            try {
                selectedNumber = scanner.nextInt();
                if (selectedNumber == 0)
                    return;
                if (selectedNumber < 1 || selectedNumber > cereri.size()) {
                    System.out.println("Numar cerere invalid. Va rugam introduceti un numar valid.");
                    return;
                }
                // Delete the selected request
                Request selectedRequest = cereri.get(selectedNumber - 1);
                if (cereri.contains(selectedRequest)) {
                    cereri.remove(selectedRequest);
                }
                IMDB.user_logat.deleteRequest(selectedRequest);
                String nume = selectedRequest.getResolverUsername();
                User user2 = IMDB.findUserByUsername(nume);
                if (user2 != null) {
                    user2.deleteRequest(selectedRequest);
                }
                if (IMDB.lista_cereri_admin.contains(selectedRequest))
                    IMDB.lista_cereri_admin.remove(selectedRequest);
                System.out.println("Cerere stearsa cu succes!");
            } catch (InputMismatchException e) {
                System.out.println("Input invalid. Va rugam introduceti un numar valid.");
            } finally {
                // Consume the remaining newline character
                scanner.nextLine();
            }
        }
    }

    private static boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    // Getter method for loggedInUser
    public User getLoggedInUser() {
        return loggedInUser;
    }

    public static class TextReviewManager {

        private Movie selectedMovie;
        private Series selectedSeries;

        public TextReviewManager() {
            // Constructor initialization if needed
        }

        public void startReviewManager() {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nFilme si seriale disponibile");
                displayMovieTitles();

                System.out.print("\nIntroduceti numarul filmului sau al serialului (0 pentru iesire): ");
                int choice = 0;
                try {
                    choice = scanner.nextInt();
                } catch (InputMismatchException e) {
                }

                if (choice == 0) {
                    System.out.println("Iesire manager de recenzii");
                    break;
                }

                if (!isValidChoice(choice)) {
                    System.out.println("Alegere invalida. Va rugam introduceti un numar valid.");
                    continue;
                }

                Production foundProduction = findProductionByChoice(choice);

                if (foundProduction instanceof Movie) {
                    selectedMovie = (Movie) foundProduction;
                    selectedSeries = null;
                } else if (foundProduction instanceof Series) {
                    selectedSeries = (Series) foundProduction;
                    selectedMovie = null;
                }

                // Add text menu options for adding, deleting, and viewing reviews
                while (true) {
                    displayReviewOptions();

                    System.out.print("Introduceti alegerea : ");
                    int reviewOption = 0;
                    try {
                        reviewOption = scanner.nextInt();
                    } catch (InputMismatchException e) {
                    }

                    switch (reviewOption) {
                        case 1:
                            performAddReviewAction(scanner);
                            break;
                        case 2:
                            performDeleteReviewAction(scanner);
                            break;
                        case 3:
                            viewReviews();
                            break;
                        case 4:
                            break;
                        default:
                            System.out.println("Alegere invalida. Va rugam introduceti un numar valid.");
                    }

                    if (reviewOption == 4) {
                        break;
                    }
                }
            }
        }

        private void performAddReviewAction(Scanner scanner) {
            System.out.println("\nAdaugare recenzie");

            if (selectedMovie != null || selectedSeries != null) {
                String username = IMDB.user_logat.getUsername();

                System.out.print("Da nota productiei (1-10): ");
                int score = 5;
                try {
                    score = scanner.nextInt();
                } catch (InputMismatchException e) {
                }
                String score2 = String.valueOf(score);

                scanner.nextLine(); // Consume the newline character

                System.out.print("Introduceti comentariile: ");
                String comments = scanner.nextLine();

                Rating newRating = new Rating(username, score2, comments);
                if (selectedMovie != null) {
                    // Check if the user has already reviewed the selected movie
                    if (selectedMovie.getReview().stream().anyMatch(r -> r.getUsername().equalsIgnoreCase(username))) {
                        System.out.println("Ati dat deja reecnzie la acest film");
                    } else {
                        selectedMovie.add_un_review(newRating);
                        double newrating = selectedMovie.calculate_rating_movie();
                        selectedMovie.setRating(String.valueOf(newrating));
                        ExperienceStrategy strategy = new AddReviewStrategy();
                        int experiencePoints = strategy.calculateExperience();
                        IMDB.user_logat.addExperience(experiencePoints);
                        System.out.println("Recenzie adaugata cu succes la filmul: " + selectedMovie.get_title());
                    }
                } else if (selectedSeries != null) {
                    // Check if the user has already reviewed the selected series
                    if (selectedSeries.get_Review_production().stream().anyMatch(r -> r.getUsername().equalsIgnoreCase(username))) {
                        System.out.println("Ati dat deja recenzie acestui serial");
                    } else {
                        // Assuming Series has an addReview method
                        selectedSeries.series_adauga_recenzie(newRating);
                        System.out.println("Recenzie adaugata cu succes serialului: " + selectedSeries.series_get_title());
                        double newrating = selectedSeries.calculate_rating_series();
                        selectedSeries.setRating(String.valueOf(newrating));
                        ExperienceStrategy strategy = new AddReviewStrategy();
                        int experiencePoints = strategy.calculateExperience();
                        IMDB.user_logat.addExperience(experiencePoints);
                    }
                }
            } else {
                System.out.println("Va rugam alegeti film/serial mai intai");
            }
        }

        private void performDeleteReviewAction(Scanner scanner) {
            System.out.println("\nStergere recenzii:");

            // Check if it's a movie or series and display user's reviews accordingly
            if (selectedMovie != null) {
                displayUserReviews(selectedMovie.getReview());
            } else if (selectedSeries != null) {
                displayUserReviews(selectedSeries.get_Review_production());
            } else {
                System.out.println("Va rugam alegeti un film/serial mai intai");
                return;
            }

            // Prompt the user to Introduceti the number of the review to delete
            System.out.print("Introduceti numarul recenziei pe care doriti sa o stergeti: ");
            int reviewNumber = 0;
            try {
                reviewNumber = scanner.nextInt();
            } catch (InputMismatchException e) {
            }

            // Check if the Introducetied review number is valid
            if (isValidReviewNumber(reviewNumber)) {
                deleteReviewByNumber(reviewNumber);
                System.out.println("Recenzie stearsa cu succes.");
            } else {
                System.out.println("Numar recenzie invalid.");
            }
        }

        private void displayUserReviews(List<Rating> reviews) {
            System.out.println("Recenziile tale:");

            // Display reviews by the user for the selected production
            for (int i = 0; i < reviews.size(); i++) {
                Rating review = reviews.get(i);
                if (review.getUsername().equalsIgnoreCase(IMDB.user_logat.getUsername())) {
                    System.out.println(1 + ": " + review.getScore() + "/10 - " + review.getComments());
                }
            }
        }

        private boolean isValidReviewNumber(int reviewNumber) {
            // Check if the Introducetied review number is valid
            return reviewNumber > 0 && reviewNumber <= getNumUserReviews();
        }

        private int getNumUserReviews() {
            // Get the number of user's reviews for the selected production
            if (selectedMovie != null) {
                return countUserReviews(selectedMovie.getReview());
            } else if (selectedSeries != null) {
                return countUserReviews(selectedSeries.get_Review_production());
            } else {
                return 0;
            }
        }

        private int countUserReviews(List<Rating> reviews) {
            // Count the number of reviews by the user for the selected production
            int count = 0;
            for (Rating review : reviews) {
                if (review.getUsername().equalsIgnoreCase(IMDB.user_logat.getUsername())) {
                    count++;
                }
            }
            return count;
        }

        private void deleteReviewByNumber(int reviewNumber) {
            // Delete the review based on the Introducetied review number
            List<Rating> reviews;
            if (selectedMovie != null) {
                reviews = selectedMovie.getReview();
            } else {
                reviews = selectedSeries.get_Review_production();
            }

            for (int i = 0; i < reviews.size(); i++) {
                Rating review = reviews.get(i);
                if (review.getUsername().equalsIgnoreCase(IMDB.user_logat.getUsername())) {
                    if (reviewNumber == 1) {
                        reviews.remove(i);
                    }
                    reviewNumber--;
                }
            }
            if (selectedMovie != null) {
                double newrating = selectedMovie.calculate_rating_movie();
                selectedMovie.setRating(String.valueOf(newrating));
            } else {
                double newrating = selectedSeries.calculate_rating_series();
                selectedSeries.setRating(String.valueOf(newrating));
            }
        }

        private void viewReviews() {
            System.out.println("\nVizualizare toate recenziile");

            // Check if it's a movie or series and display reviews accordingly
            if (selectedMovie != null) {
                displayReviews(selectedMovie.getReview());
            } else if (selectedSeries != null) {
                displayReviews(selectedSeries.get_Review_production());
            } else {
                System.out.println("Va rugam alegeti un film/serial mai intai.");
            }
        }

        private void displayReviews(List<Rating> reviews) {
            if (!reviews.isEmpty()) {
                System.out.println("Toate recenziile:");

                // Display all reviews in the console
                for (Rating review : reviews) {
                    System.out.println(review.getUsername() + ": " + review.getScore() + "/10 - " + review.getComments());
                }
            } else {
                System.out.println("Nicio recenzie disponinila pentru productia aleasa");
            }
        }

        private void displayReviewOptions() {
            System.out.println("\nOptiuni recenzii:");
            System.out.println("1. Adauga Recenzie");
            System.out.println("2. Sterge Recenzie");
            System.out.println("3. Vizualizare Recenzii");
            System.out.println("4. Inapoi la sectiune de alegere filme/seriale");
        }

        private boolean isValidChoice(int choice) {
            return choice >= 1 && choice <= IMDB.lista_filme.size() + IMDB.lista_seriale.size();
        }

        private Production findProductionByChoice(int choice) {
            int movieCount = IMDB.lista_filme.size();
            int seriesCount = IMDB.lista_seriale.size();

            if (choice <= movieCount) {
                return IMDB.lista_filme.get(choice - 1);
            } else {
                return IMDB.lista_seriale.get(choice - movieCount - 1);
            }
        }

        private void displayMovieTitles() {
            int count = 1;

            for (Movie movie : IMDB.lista_filme) {
                System.out.println(count + ". " + movie.get_title());
                count++;
            }

            for (Series series : IMDB.lista_seriale) {
                System.out.println(count + ". " + series.series_get_title());
                count++;
            }
        }

        public static void main(String[] args) {
            TextReviewManager reviewManager = new TextReviewManager();
            reviewManager.startReviewManager();
        }
    }

    public class ReviewWindowActors {
        private static Actor selectedActor;

        public static void TextReviewWindowActors() {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                printMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline

                switch (choice) {
                    case 1:
                        performAddReviewAction(scanner);
                        break;
                    case 2:
                        performDeleteReviewAction(scanner);
                        break;
                    case 3:
                        return; // Exit the program
                    default:
                        System.out.println("Alegere invalida. Va rog reincercati.");
                }
            }
        }

        private static void printMenu() {
            System.out.println("\n1. Adauga o recenzie");
            System.out.println("2. Sterge o recenzie");
            System.out.println("3. Iesire");
            System.out.print("Introduceti numarul (de la 1 la 3): ");
        }

        private static void performAddReviewAction(Scanner scanner) {
            System.out.println("Actori disponibili:");

            // Display a numbered list of actors
            for (int i = 0; i < IMDB.actorList.size(); i++) {
                System.out.println((i + 1) + ". " + IMDB.actorList.get(i).getNume());
            }

            System.out.print("Introduceti numarul actorului: ");
            int actorNumber = scanner.nextInt();

            if (actorNumber > 0 && actorNumber <= IMDB.actorList.size()) {
                selectedActor = IMDB.actorList.get(actorNumber - 1);
                addReview(scanner);
            } else {
                System.out.println("Numar invalid. Va rugam reincercati!");
            }
        }

        private static void addReview(Scanner scanner) {
            if (selectedActor != null) {
                String username = IMDB.user_logat.getUsername();

                if (hasUserReviewed(username)) {
                    System.out.println("Ati dat deja recenzie acestui actor.");
                    return;
                } else {
                    scanner.nextLine();
                    System.out.print("Introduceti un scor (1-10): ");
                    String scoreStr = scanner.nextLine();

                    System.out.print("Introduceti un comentariu: ");
                    String comments = scanner.nextLine();

                    Rating newRating = new Rating(username, scoreStr, comments);
                    selectedActor.add_rating(newRating);

                    System.out.println("Recenzie adaugata cu succes: " + selectedActor.getNume());
                }
            } else {
                System.out.println("Va rugam sa alegeti un actor");
            }
        }

        private static void performDeleteReviewAction(Scanner scanner) {
            System.out.println("Actori disponibili:");

            // Display a numbered list of actors
            for (int i = 0; i < IMDB.actorList.size(); i++) {
                System.out.println((i + 1) + ". " + IMDB.actorList.get(i).getNume());
            }

            System.out.print("Introduceti numarul actorului: ");
            int actorNumber = scanner.nextInt();

            if (actorNumber > 0 && actorNumber <= IMDB.actorList.size()) {
                selectedActor = IMDB.actorList.get(actorNumber - 1);
                deleteReview(scanner);
            } else {
                System.out.println("Numar invalid! Va rugam reincercati!");
            }
        }

        private static void deleteReview(Scanner scanner) {
            if (selectedActor != null) {
                System.out.println("Recenzii pentru acest actor, " + selectedActor.getNume() + ":");

                // Display a numbered list of user's reviews for the selected actor
                List<Rating> userReviews = getUserReviewsForActor();
                for (int i = 0; i < userReviews.size(); i++) {
                    Rating review = userReviews.get(i);
                    System.out.println((i + 1) + ". " + review.getScore() + "/10 - " + review.getComments());
                }

                // Prompt the user to choose a review to delete
                int reviewNumber=0;
                if(userReviews.size()!=0) {
                    System.out.print("Introduceti numarul recenziei: ");
                    reviewNumber = scanner.nextInt();
                }
                else {
                    System.out.println("Nu ati trimis nicio recenzie acestui film");
                    return;
                }
                if (reviewNumber > 0 && reviewNumber <= userReviews.size()) {
                    String commentsToDelete = userReviews.get(reviewNumber - 1).getComments();
                    Rating rating2=userReviews.get(reviewNumber-1);
                    selectedActor.delete_rating(rating2);
                    IMDB.user_logat.stergeRecenzii_Actor(selectedActor);
                    System.out.println("Recenzie stearsa cu succes " + selectedActor.getNume());
                } else {
                    System.out.println("Recenzie invalida. Va rugam reincercati!");
                }
            } else {
                System.out.println("Va rugam alegeti un actor intai!");
            }
        }

        private static List<Rating> getUserReviewsForActor() {
            List<Rating> userReviews = new ArrayList<>();

            if (selectedActor != null) {
                List<Rating> actorReviews = selectedActor.get_ratings();
                String username = IMDB.user_logat.getUsername();

                for (Rating review : actorReviews) {
                    if (review.getUsername().equalsIgnoreCase(username)) {
                        userReviews.add(review);
                    }
                }
            }

            return userReviews;
        }

        private static boolean hasUserReviewed(String username) {
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

        public void main(String[] args) {
            TextReviewWindowActors();
        }
    }
    public static class AdminSolver {

        private List<Request> userRequests = new ArrayList<>();

        public void Solve_Admin() {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Alege o cerere:");

            for (User user : IMDB.lista_useri) {
                List<Request> cereri = user.getAllSentRequests();
                for (Request request : cereri) {
                    if (request.getResolverUsername().equalsIgnoreCase(IMDB.user_logat.getUsername())) {
                        userRequests.add(request);
                    }
                }
            }

            if (!userRequests.isEmpty()) {
                System.out.println("Cereri disponibile:");
                for (int i = 0; i < userRequests.size(); i++) {
                    System.out.println((i + 1) + ". " + userRequests.get(i).getProblemDescription());
                }

                System.out.println("Alege o cerere (introduceti numarul corespunzator): ");
                int selectedRequestIndex = scanner.nextInt();
                if (selectedRequestIndex > 0 && selectedRequestIndex <= userRequests.size()) {
                    Request selectedRequest = userRequests.get(selectedRequestIndex - 1);
                    handleRequest(selectedRequest);
                } else {
                    System.out.println("Numarul introdus nu este valid.");
                }
            } else {
                System.out.println("Nu exista cereri.");
            }
        }

        private void handleRequest(Request request) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("1. Accepta");
            System.out.println("2. Refuza");
            System.out.println("3. Anuleaza");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    acceptRequest(request);
                    break;
                case 2:
                    rejectRequest(request);
                    break;
                case 3:
                    System.out.println("Operatiune anulata.");
                    break;
                default:
                    System.out.println("Optiune invalida.");
            }
        }

        private void acceptRequest(Request request) {
            String username = request.getCreatorUsername();
            User user = find_user(username);
            user.adauga_notificare("Cererea dumneavoastra a fost acceptata.");

            if (request.getType().equalsIgnoreCase("MOVIE_ISSUE")) {
                // For ReviewRequest, use AddReviewStrategy
                ExperienceStrategy strategy = new ResolveIssueStrategy();
                int experiencePoints = strategy.calculateExperience();
                user.addExperience(experiencePoints);
            } else if (request.getType().equalsIgnoreCase("ACTOR_ISSUE")) {
                // For IssueResolutionRequest, use ResolveIssueStrategy
                ExperienceStrategy strategy = new ResolveIssueStrategy();
                int experiencePoints = strategy.calculateExperience();
                user.addExperience(experiencePoints);
            }

            // Remove the accepted request from the userRequests list
            userRequests.remove(request);
            IMDB.user_logat.removeReceivedRequest(request);
            User user1 = IMDB.findUserByUsername(request.getCreatorUsername());
            user1.removeSentRequest(request);
            System.out.println("Cerere acceptata!");
        }

        private void rejectRequest(Request request) {
            String username = request.getCreatorUsername();
            User user = find_user(username);
            user.adauga_notificare("Ne pare rau, cererea dumneavoastra a fost respinsa.");
            System.out.println("Cerere respinsa cu succes!");
        }

        private User find_user(String username) {
            for (User user : IMDB.lista_useri) {
                if (user.getUsername().equalsIgnoreCase(username))
                    return user;
            }
            return null;
        }
        public static void main() {
            AdminSolver adminSolver = new AdminSolver();
            adminSolver.Solve_Admin();
        }
    }
}