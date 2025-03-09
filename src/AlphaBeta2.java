import java.util.ArrayList;

public class AlphaBeta2 extends Joueur{
    private int profondeur;
    private long time;
    private long begin;

    AlphaBeta2 (int prof) {
        profondeur=prof;
        time=100000000;
    }

    AlphaBeta2 (int prof, long time) {
        profondeur=prof;
        this.time=time;
    }

    public void jouer (Echecs echecs) {
        if (echecs.getVictoire()==0) {
            begin=System.currentTimeMillis();
            Parcours parcours = simuler(echecs, profondeur, -999999, 999999,null);
            if (parcours != null && parcours.actions.size() > 0) {
                do {
                    echecs.action(parcours.actions.remove(0));
                }while(echecs.getOrdre() > 0 && parcours.actions.size() > 0);
            }
        }
    }

    public Parcours simuler (Echecs echecs, int reste, double alpha, double beta, Parcours parcoursPrecedent) {
        Parcours parcours = new Parcours();
        if (reste > 0 && echecs.getVictoire() == 0) {
            if (System.currentTimeMillis() - begin < time) {
                ArrayList<Integer> actionsPossibles = triActionsPossibles(echecs);
                if (actionsPossibles.size() <= 0)
                    return null;
                Parcours bestParcours = null;
                if (parcoursPrecedent != null && parcoursPrecedent.actions.size() > 0) {
                    int bestAction = parcoursPrecedent.actions.remove(0);
                    int index = actionsPossibles.indexOf(bestAction);
                    if (index < 0)
                        System.exit(0);
                    actionsPossibles.remove(index);
                    actionsPossibles.add(0,bestAction);
                }
                for (int i = 0; i < actionsPossibles.size(); i++) {
                    int action = actionsPossibles.get(i);
                    Echecs echecs2=echecs.clone();
                    echecs2.action(action);

                    if (echecs2.getOrdre() == 0)
                        parcours = simuler(echecs2, reste - 1, alpha, beta, parcoursPrecedent);
                    else
                        parcours = simuler(echecs2, reste, alpha, beta, parcoursPrecedent);
                    
                    if (parcours != null) {
                        if (!echecs.getTour()) {
                            if (alpha >= parcours.evaluation) {
                                parcours.actions.add(0,action);
                                return parcours;
                            }
                            beta = Math.min(beta, parcours.evaluation);
                            if (bestParcours == null || bestParcours.evaluation > parcours.evaluation) {
                                parcours.actions.add(0,action);
                                bestParcours = parcours;
                            }
                        }
                        else {
                            if (beta <= parcours.evaluation) {
                                parcours.actions.add(0,action);
                                return parcours;
                            }
                            alpha = Math.max(alpha, parcours.evaluation);
                            if (bestParcours == null || bestParcours.evaluation < parcours.evaluation) {
                                parcours.actions.add(0,action);
                                bestParcours = parcours;
                            }
                        }
                    }
                }

                return bestParcours;
            }
            return null;
        }
        parcours = new Parcours();
        parcours.evaluation = echecs.evaluation();
        return parcours;
    }

    public void setBegin (long begin) {
        this.begin=begin;
    }

    private ArrayList<Integer> triActionsPossibles(Echecs echecs) {
        int[] actionPossibles = echecs.getActions();
        ArrayList<Integer> tri=new ArrayList<>();
        ArrayList<Double> values=new ArrayList<>();
        for (int i = 0; i < actionPossibles.length; i++) {
            int action = actionPossibles[i];
            double value = Evaluation.evaluationAction2(echecs, action);
            
            insert(tri,values,action,value);
        }
        return tri;
    }

    private void insert (ArrayList<Integer> tri, ArrayList<Double> values, int action, double value) {
        int indice = getIndice(values, value);
        values.add(indice,value);
        tri.add(indice,action);
    }

    private int getIndice (ArrayList<Double> values, double value) {
        int longueur=values.size();
        if (longueur==0 || value>=values.get(0))
            return 0;
        if (values.get(longueur-1)>value)
            return longueur;
        int debut=0;
        int fin=longueur-1;
        int milieu;
        while (debut<=fin) {
            milieu=debut+(fin-debut)/2;
            if (values.get(milieu)==value) {
                return milieu;
            } else {
                if (values.get(milieu)>value) {
                    debut=milieu+1;
                } else {
                    fin=milieu-1;
                }
            }
        }
        return debut;
    }

}
