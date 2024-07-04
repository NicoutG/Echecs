import java.util.Random;
import java.util.ArrayList;

public class Noeud {
    private int n;
    private int w;
    private boolean joueur;
    private ArrayList<int []> depPossibles;
    private Plateau plateau;
    private Noeud [] noeuds;

    public Noeud (Plateau plateau, boolean joueur) {
        n=0;
        w=0;
        this.joueur=joueur;
        this.plateau=plateau;
        depPossibles=plateau.getDepPossibles();
        noeuds=new Noeud[depPossibles.size()];
        for (int i=0;i<noeuds.length;i++)
            noeuds[i]=null;
    }

    public int size () {
        return noeuds.length;
    }

    public Noeud get (int index) {
        if (0<=index && index<size())
            return noeuds[index];
        return null;
    }

    public int [] getDep (int index) {
        if (0<=index && index<size())
            return depPossibles.get(index);
        return null;
    }

    public int getW () {
        return w;
    }

    public int getN () {
        return n;
    }

    private Noeud explorer (int index) {
        if (0<=index && index<size()) {
            if (noeuds[index]!=null)
                return noeuds[index];
            
            int [] dep=depPossibles.get(index);
            Plateau plateau2=plateau.clone();
            plateau2.action(dep[0]);
            plateau2.action(dep[1]);
            if (plateau2.getOrdre()==3)
                plateau2.action(67);
            
            noeuds[index]=new Noeud(plateau2, joueur);
            return noeuds[index];
        }
        return null;
    }

    private double uct (double c, int w, int n, int N) {
        return 1.0*w/n+c*Math.sqrt(Math.log(w)/n);
    }

    private boolean isFeuille () {
        if (noeuds==null)
            return true;
        for (int i=0;i<size();i++)
            if (noeuds[i]!=null)
                return false;
        return true;
    }

    public int selection (double c, int nbSimulations) {
        double bestUct=0;
        int bestNoeud=0;
        for (int i=0;i<size();i++) {
            double uct=0;
            if (noeuds[i]==null)
                uct=99999999999999.0;
            else
                uct=uct(c,noeuds[i].getW(),noeuds[i].getN(),n);
            if (uct>bestUct) {
                bestUct=uct;
                bestNoeud=i;
            }
        }
        Noeud noeudExp=explorer(bestNoeud);
        int res=0;
        if (noeudExp.isFeuille())
            res=noeudExp.simuler(nbSimulations);
        else
            res=noeudExp.selection(bestUct, nbSimulations);
        w+=res;
        n+=nbSimulations;
        return res;
    }

    private int simuler (int nbSimulations) {
        n+=nbSimulations;
        int res=0;
        for (int i=0;i<nbSimulations;i++) {
            Plateau plateau2=plateau.clone();
            res+=jouer(plateau2);
        }
        w+=res;
        return res;
    }

    private int jouer (Plateau plateau) {
        if (plateau.getVictoire()==0) {
            jouerCoupMinMax(plateau);
            return jouer(plateau);
        }
        else {
            if ((plateau.getVictoire()==1 && joueur) || (plateau.getVictoire()==2 && !joueur))
                return 1;
            else
                return 0;
        }
    }

    private void jouerCoupAleatoire (Plateau plateau) {
        ArrayList <int []> deps=plateau.getDepPossibles();
        Random random=new Random();
        int choix=random.nextInt(deps.size());
        plateau.action(deps.get(choix)[0]);
        plateau.action(deps.get(choix)[1]);
        if (plateau.getOrdre()==3)
                plateau.action(67);
    }

    private void jouerCoupSemiAleatoire (Plateau plateau) {
        ArrayList <int []> deps=plateau.getDepPossibles();
        for (int i=0;i<deps.size();i++) {
            Plateau plateau2=plateau.clone();
            plateau2.action(deps.get(i)[0]);
            plateau2.action(deps.get(i)[1]);
            if (plateau2.getOrdre()==3)
                    plateau2.action(67);
            
            if (plateau2.getVictoire()==1 || plateau2.getVictoire()==2) {
                plateau.action(deps.get(i)[0]);
                plateau.action(deps.get(i)[1]);
                if (plateau.getOrdre()==3)
                    plateau.action(67);
                return ;
            }

        }
        Random random=new Random();
        int choix=random.nextInt(deps.size());
        plateau.action(deps.get(choix)[0]);
        plateau.action(deps.get(choix)[1]);
        if (plateau.getOrdre()==3)
                plateau.action(67);
    }

    private void jouerCoupMinMax (Plateau plateau) {
        int n=3;
        ArrayList <int []> deps=plateau.getDepPossibles();
        ArrayList <int []> depBest=new ArrayList <int []> ();
        ArrayList <Integer> evaluations=new ArrayList <Integer> ();
        for (int i=0;i<deps.size();i++) {
            Plateau plateau2=plateau.clone();
            plateau2.action(deps.get(i)[0]);
            plateau2.action(deps.get(i)[1]);
            int evaluation=plateau2.evaluation();
            if (evaluation==1000 || evaluation==-1000) {
                plateau.action(deps.get(i)[0]);
                plateau.action(deps.get(i)[1]);
                if (plateau.getOrdre()==3)
                    plateau.action(67);
                return ;
            }
            if (!plateau.getTour())
                evaluation*=-1;
            if (depBest.size()==0) {
                depBest.add(deps.get(i));
                evaluations.add(evaluation);
            }
            else {
                if (evaluation>evaluations.get(0)) {
                    int j=1;
                    while (j<evaluations.size() && evaluations.get(j)<evaluation)
                        j++;
                    depBest.add(j,deps.get(i));
                    evaluations.add(j,evaluation);
                    if (depBest.size()<=n) {
                        depBest.remove(0);
                        evaluations.remove(0);
                    }
                }
                else
                    if (depBest.size()<n) {
                        depBest.add(0,deps.get(i));
                        evaluations.add(0,evaluation);
                    }
            }
        }
        Random random=new Random();
        int choix=random.nextInt(depBest.size());
        plateau.action(depBest.get(choix)[0]);
        plateau.action(depBest.get(choix)[1]);
        if (plateau.getOrdre()==3)
                plateau.action(67);
    }
}
