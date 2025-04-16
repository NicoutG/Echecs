public class Parcours2 {
    public double evaluation;
    public int[] actions;

    public Parcours2(int nbActions) {
        actions = new int[nbActions];
    }

    public Parcours2 clone() {
        Parcours2 parcours = new Parcours2(actions.length);
        parcours.evaluation = evaluation;
        parcours.actions = actions.clone();
        return parcours;
    }

    public String toString() {
        return evaluation + " " + actions.toString();
    }
}
