import java.util.Random;

public class AlphaBetaLMR extends Joueur{
    private int profondeur;
    private long time;
    private long begin;

    AlphaBetaLMR (int prof) {
        profondeur=prof;
        time=100000000;
    }

    AlphaBetaLMR (int prof, long time) {
        profondeur=prof;
        profondeur=prof;
        this.time=time;
    }

    public void jouer (Echecs echecs) {
        if (echecs.getVictoire()==0) {
            begin=System.currentTimeMillis();
            Echecs echecsClone = echecs.clone();
            echecsClone.activateRollBack();
            Parcours2 parcours = simuler(echecsClone, profondeur, 0, -Evaluation.MAXVAL, Evaluation.MAXVAL,null);
            if (parcours != null && parcours.actions.length> 0) {
                int i = 0;
                do {
                    echecs.action(parcours.actions[i]);
                    i++;
                }while(echecs.getOrdre() > 0 && i < parcours.actions.length);
            }
        }
    }

    public Parcours2 simuler (Echecs echecs, int reste, int nbCoups, double alpha, double beta, Parcours2 parcoursPrecedent) {
        Parcours2 parcours;
        if (reste > 0 && echecs.getVictoire() == 0) {
            if (System.currentTimeMillis() - begin < time) {
                int[] actionsPossibles;
                if (parcoursPrecedent != null && parcoursPrecedent.actions.length > nbCoups) {
                    int bestAction = parcoursPrecedent.actions[nbCoups];
                    actionsPossibles = triActionsPossibles(echecs,bestAction);
                }
                else {
                    actionsPossibles = triActionsPossibles(echecs,null);
                }
                if (actionsPossibles.length <= 0) {
                    return null;
                }
                boolean tour = echecs.getTour();
                Parcours2 bestParcours = null;

                for (int i = 0; i < actionsPossibles.length; i++) {
                    int action = actionsPossibles[i];
                    double evalAction = Evaluation.evaluationAction(echecs, action);
                    echecs.action(action);
                    Random random = new Random();
                    double convergence = 2.1;

                    if (echecs.getOrdre() == 0) {
                        if (evalAction > 0 || 1.0 * i / actionsPossibles.length < Math.pow(random.nextDouble(),convergence))
                            parcours = simuler(echecs, reste - 1, nbCoups + 1, alpha, beta, parcoursPrecedent);
                        else
                            parcours = simuler(echecs, reste - 2, nbCoups + 1, alpha, beta, parcoursPrecedent);
                    }
                    else
                        parcours = simuler(echecs, reste, nbCoups + 1, alpha, beta, parcoursPrecedent);
                    
                    if (parcours != null) {
                        if (!tour) {
                            if (alpha >= parcours.evaluation) {
                                parcours.actions[nbCoups] = action;
                                echecs.rollBack();
                                return parcours;
                            }
                            beta = Math.min(beta, parcours.evaluation);
                            if (bestParcours == null || bestParcours.evaluation > parcours.evaluation) {
                                parcours.actions[nbCoups] = action;
                                bestParcours = parcours;
                            }
                        }
                        else {
                            if (beta <= parcours.evaluation) {
                                parcours.actions[nbCoups] = action;
                                echecs.rollBack();
                                return parcours;
                            }
                            alpha = Math.max(alpha, parcours.evaluation);
                            if (bestParcours == null || bestParcours.evaluation < parcours.evaluation) {
                                parcours.actions[nbCoups] = action;
                                bestParcours = parcours;
                            }
                        }
                    }
                    echecs.rollBack();
                }
                return bestParcours;
            }
            return null;
        }
        parcours = new Parcours2(nbCoups);
        parcours.evaluation = Evaluation.evaluation(echecs);
        return parcours;
    }

    public void setBegin (long begin) {
        this.begin=begin;
    }

    private int[] triActionsPossibles(Echecs echecs, Integer priority) {
        int[] actions = echecs.getActions();
        int n = actions.length;
        double[] values = new double[n];
    
        // Calcul des valeurs des actions
        for (int i = 0; i < n; i++) {
            values[i] = Evaluation.evaluationAction(echecs, actions[i]);
        }
    
        // Gestion de la priorité en mettant en première position avant le tri
        if (priority != null) {
            for (int i = 0; i < n; i++) {
                if (actions[i] == priority) {
                    swap(actions, values, i, 0);
                    break;
                }
            }
            // On trie seulement après le premier élément
            quickSort(actions, values, 1, n - 1);
        } else {
            quickSort(actions, values, 0, n - 1);
        }
    
        return actions;
    }
    
    // QuickSort optimisé en une seule fonction
    private void quickSort(int[] actions, double[] values, int low, int high) {
        while (low < high) {
            double pivot = values[high];
            int i = low - 1;
            for (int j = low; j < high; j++) {
                if (values[j] >= pivot) {  // Tri décroissant
                    swap(actions, values, ++i, j);
                }
            }
            swap(actions, values, i + 1, high);
            int pivotIndex = i + 1;
    
            if (pivotIndex - low < high - pivotIndex) {
                quickSort(actions, values, low, pivotIndex - 1);
                low = pivotIndex + 1;
            } else {
                quickSort(actions, values, pivotIndex + 1, high);
                high = pivotIndex - 1;
            }
        }
    }
    
    // Fonction d'échange pour éviter la duplication de code
    private void swap(int[] actions, double[] values, int i, int j) {
        int tempAction = actions[i];
        double tempValue = values[i];
        actions[i] = actions[j];
        values[i] = values[j];
        actions[j] = tempAction;
        values[j] = tempValue;
    }

}
