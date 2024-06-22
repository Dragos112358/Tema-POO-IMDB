// Interfața pentru strategia de experiență
public interface ExperienceStrategy {
    int calculateExperience();
}

// Implementare pentru adăugarea unei recenzii
class AddReviewStrategy implements ExperienceStrategy {
    @Override
    public int calculateExperience() {
        // Logica de calcul pentru adăugarea unei recenzii
        // Poți returna un anumit număr de puncte de experiență în funcție de complexitatea acțiunii
        return 3;
    }
}

// Implementare pentru rezolvarea unei cereri
class ResolveIssueStrategy implements ExperienceStrategy {
    @Override
    public int calculateExperience() {
        // Logica de calcul pentru rezolvarea unei cereri
        // Poți returna un anumit număr de puncte de experiență în funcție de complexitatea acțiunii
        return 6;
    }
}

// Implementare pentru adăugarea unei noi produse sau a unui actor
class AddActorStrategy implements ExperienceStrategy {
    @Override
    public int calculateExperience() {
        // Logica de calcul pentru adăugarea unei noi produse sau a unui actor
        // Poți returna un anumit număr de puncte de experiență în funcție de complexitatea acțiunii
        return 7;
    }
}
class AddProductionStrategy implements ExperienceStrategy {
    @Override
    public int calculateExperience() {
        // Logica de calcul pentru adăugarea unei noi produse sau a unui actor
        // Poți returna un anumit număr de puncte de experiență în funcție de complexitatea acțiunii
        return 10;
    }
}

