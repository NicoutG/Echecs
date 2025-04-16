public class AlphaBeta4 extends Joueur{
    private int profondeur;
    private long time;
    private long begin;

    AlphaBeta4 (int prof) {
        profondeur=prof;
        time=100000000;
    }

    AlphaBeta4 (int prof, long time) {
        profondeur=prof;
        profondeur=prof;
        this.time=time;
    }

    public void jouer (Echecs echecs) {
        if (echecs.getVictoire()==0) {
            begin=System.currentTimeMillis();
            Echecs echecsClone = echecs.clone();
            echecsClone.activateRollBack();
            Parcours parcours = simuler(echecsClone, profondeur, -Evaluation.MAXVAL, Evaluation.MAXVAL,null);
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
                int[] actionsPossibles;
                if (parcoursPrecedent != null && parcoursPrecedent.actions.size() > 0) {
                    int bestAction = parcoursPrecedent.actions.remove(0);
                    actionsPossibles = triActionsPossibles(echecs,bestAction);
                }
                else {
                    actionsPossibles = triActionsPossibles(echecs,null);
                }
                if (actionsPossibles.length <= 0) {
                    return null;
                }
                boolean tour = echecs.getTour();
                Parcours bestParcours = null;

                for (int i = 0; i < actionsPossibles.length; i++) {
                    int action = actionsPossibles[i];
                    echecs.action(action);

                    if (echecs.getOrdre() == 0)
                        parcours = simuler(echecs, reste - 1, alpha, beta, parcoursPrecedent);
                    else
                        parcours = simuler(echecs, reste, alpha, beta, parcoursPrecedent);
                    
                    if (parcours != null) {
                        if (!tour) {
                            if (alpha >= parcours.evaluation) {
                                parcours.actions.add(0,action);
                                echecs.rollBack();
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
                                echecs.rollBack();
                                return parcours;
                            }
                            alpha = Math.max(alpha, parcours.evaluation);
                            if (bestParcours == null || bestParcours.evaluation < parcours.evaluation) {
                                parcours.actions.add(0,action);
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
        parcours = new Parcours();
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
