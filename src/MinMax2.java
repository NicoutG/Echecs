
public class MinMax2 extends Joueur{
    private int profondeur;

    MinMax2 (int prof) {
        profondeur=prof;
    }

    public void jouer (Echecs echecs) {
        if (echecs.getVictoire()==0) {
            Parcours parcours = simuler(echecs, profondeur);
            if (parcours != null && parcours.actions.size() > 0) {
                do {
                    echecs.action(parcours.actions.remove(0));
                }while(echecs.getOrdre() > 0 && parcours.actions.size() > 0);
            }
        }
    }

    public Parcours simuler (Echecs echecs, int reste) {
        Parcours parcours = new Parcours();
        if (reste > 0 && echecs.getVictoire() == 0) {
            int[] actionsPossibles = echecs.getActions();
            if (actionsPossibles.length <= 0)
                return null;
            Parcours bestParcours = null;
            for (int i = 0; i < actionsPossibles.length; i++) {
                int action = actionsPossibles[i];
                Echecs echecs2=echecs.clone();
                echecs2.action(action);

                if (echecs2.getOrdre() == 0)
                    parcours = simuler(echecs2, reste - 1);
                else
                    parcours = simuler(echecs2, reste);
                
                if (parcours != null) {
                    if (!echecs.getTour()) {
                        if (bestParcours == null || bestParcours.evaluation > parcours.evaluation) {
                            parcours.actions.add(0,action);
                            bestParcours = parcours;
                        }
                    }
                    else {
                        if (bestParcours == null || bestParcours.evaluation < parcours.evaluation) {
                            parcours.actions.add(0,action);
                            bestParcours = parcours;
                        }
                    }
                }
            }

            return bestParcours;
        }
        parcours = new Parcours();
        parcours.evaluation = Evaluation.evaluation(echecs);
        return parcours;
    }
}
