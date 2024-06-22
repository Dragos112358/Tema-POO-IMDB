import java.util.List;
import java.util.SortedSet;

public abstract class Staff extends User {
    String userInfo;
    String username;
    public Staff(String userInfo, String username) {
        this.userInfo=userInfo;
        this.username=username;
    }

    protected Staff() {
    }
}

