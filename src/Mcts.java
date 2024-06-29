public class Mcts extends Joueur {
    private double c;
    private int nbSimulations;
    private int nbSelections;
    private Noeud racine;

    public Mcts (int nbSelections, int nbSimulations, double c) {
        this.c=c;
        if (nbSelections<=0) {
            System.out.println("nbSelections doit être positif");
            this.nbSelections=1;
        }
        else
            this.nbSelections=nbSelections;
        if (nbSimulations<=0) {
            System.out.println("nbSimulations doit être positif");
            this.nbSimulations=1;
        }
        else
            this.nbSimulations=nbSimulations;
        
        racine=null;
    }

    public void jouer (Plateau plateau) {
        if (plateau.getVictoire()==0) {
            creerArbre(plateau);
            int [] dep=choixDep();
            if (dep==null)
                System.out.println("Aucun déplacement possible");
            else {
                plateau.action(dep[0]);
                plateau.action(dep[1]);
                if (plateau.getOrdre()==3)
                    plateau.action(67);
            }
        }
    }

    private void creerArbre (Plateau plateau) {
        Plateau plateau2=plateau.clone();
        racine=new Noeud(plateau2,plateau2.getTour());
        for (int i=0;i<nbSelections;i++) {
            racine.selection(c, nbSimulations);
        }   
    }

    private int [] choixDep () {
        double bestVictoire=0;
        int [] bestCoup=null;
        for (int i=0;i<racine.size();i++) {
            Noeud noeud=racine.get(i);
            if (noeud!=null) {
                double pourcentage=1.0*noeud.getW()/noeud.getN();
                if (pourcentage>bestVictoire) {
                    bestVictoire=pourcentage;
                    bestCoup=racine.getDep(i);
                }
            }
        }
        return bestCoup;
    }
}
