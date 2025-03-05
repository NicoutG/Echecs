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

    public void jouer (Echecs echecs) {
        if (echecs.getVictoire()==0) {
            creerArbre(echecs);
            int [] dep=choixDep();
            if (dep==null)
                System.out.println("Aucun déplacement possible");
            else {
                echecs.action(dep[0]);
                echecs.action(dep[1]);
                if (echecs.getOrdre()==2)
                    echecs.action(67);
            }
        }
    }

    private void creerArbre (Echecs echecs) {
        Echecs echecs2=echecs.clone();
        racine=new Noeud(echecs2,echecs2.getTour());
        for (int i=0;i<nbSelections;i++) {
            racine.selection(c, nbSimulations);
        }   
    }

    private int [] choixDep () {
        double bestVictoire=-1;
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
