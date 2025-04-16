public class AlphaBeta5Tests extends Joueur{
    private int profondeur;
    private long time;
    private long begin;
    private int nbActions = 0;
    private int nbEvaluations = 0;
    private int nbRollBack = 0;
    private long durationAction = 0;
    private long durationEvaluation = 0;
    private long durationTri = 0;
    private long durationRollBack = 0;

    AlphaBeta5Tests (int prof) {
        profondeur=prof;
        time=100000000;
    }

    AlphaBeta5Tests (int prof, long time) {
        profondeur=prof;
        profondeur=prof;
        this.time=time;
    }

    public void jouer (Echecs echecs) {
        if (echecs.getVictoire()==0) {
            nbActions = 0;
            nbEvaluations = 0;
            nbRollBack = 0;
            durationAction = 0;
            durationEvaluation = 0;
            durationTri = 0;
            durationRollBack = 0;
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
            System.out.println("NbActions " + nbActions);
            System.out.println("NbEvaluations " + nbEvaluations);
            System.out.println("NbRollBack " + nbRollBack);
            System.out.println("Duration " + (System.currentTimeMillis() - begin));
            System.out.println("DurationAction " + durationAction);
            System.out.println("DurationEvaluation " + durationEvaluation);
            System.out.println("DurationTri " + durationTri);
            System.out.println("DurationRollBack " + durationRollBack);
        }
    }

    public Parcours2 simuler (Echecs echecs, int reste, int nbCoups, double alpha, double beta, Parcours2 parcoursPrecedent) {
        Parcours2 parcours;
        if (reste > 0 && echecs.getVictoire() == 0) {
            if (System.currentTimeMillis() - begin < time) {
                int[] actionsPossibles;
                if (parcoursPrecedent != null && parcoursPrecedent.actions.length > nbCoups) {
                    int bestAction = parcoursPrecedent.actions[nbCoups];
                    long start = System.currentTimeMillis();
                    actionsPossibles = triActionsPossibles(echecs,bestAction);
                    durationTri += System.currentTimeMillis() - start;
                }
                else {
                    long start = System.currentTimeMillis();
                    actionsPossibles = triActionsPossibles(echecs,null);
                    durationTri += System.currentTimeMillis() - start;
                }
                if (actionsPossibles.length <= 0) {
                    return null;
                }
                boolean tour = echecs.getTour();
                Parcours2 bestParcours = null;

                for (int i = 0; i < actionsPossibles.length; i++) {
                    int action = actionsPossibles[i];
                    long start = System.currentTimeMillis();
                    echecs.action(action);
                    durationAction += System.currentTimeMillis() - start;
                    nbActions++;
                    if (echecs.getOrdre() == 0)
                        parcours = simuler(echecs, reste - 1, nbCoups + 1, alpha, beta, parcoursPrecedent);
                    else
                        parcours = simuler(echecs, reste, nbCoups + 1, alpha, beta, parcoursPrecedent);
                    
                    if (parcours != null) {
                        if (!tour) {
                            if (alpha >= parcours.evaluation) {
                                parcours.actions[nbCoups] = action;
                                start = System.currentTimeMillis();
                                echecs.rollBack();
                                durationRollBack += System.currentTimeMillis() - start;
                                nbRollBack++;
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
                                start = System.currentTimeMillis();
                                echecs.rollBack();
                                durationRollBack += System.currentTimeMillis() - start;
                                nbRollBack++;
                                return parcours;
                            }
                            alpha = Math.max(alpha, parcours.evaluation);
                            if (bestParcours == null || bestParcours.evaluation < parcours.evaluation) {
                                parcours.actions[nbCoups] = action;
                                bestParcours = parcours;
                            }
                        }
                    }
                    nbRollBack++;
                    start = System.currentTimeMillis();
                    echecs.rollBack();
                    durationRollBack += System.currentTimeMillis() - start;
                }
                return bestParcours;
            }
            return null;
        }
        parcours = new Parcours2(nbCoups);
        nbEvaluations++;
        long start = System.currentTimeMillis();
        parcours.evaluation = Evaluation.evaluation(echecs);
        durationEvaluation += System.currentTimeMillis() - start;
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
