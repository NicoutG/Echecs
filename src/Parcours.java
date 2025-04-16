import java.util.ArrayList;

public class Parcours {
    public double evaluation;
    public ArrayList<Integer> actions = new ArrayList<>(20);

    public Parcours clone() {
        Parcours parcours = new Parcours();
        parcours.evaluation = evaluation;
        parcours.actions = (ArrayList<Integer>)actions.clone();
        return parcours;
    }

    public String toString() {
        return evaluation + " " + actions.toString();
    }
}
