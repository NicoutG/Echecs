import java.util.Random;
import java.util.ArrayList;

public class Noeud {
    private int n;
    private int w;
    private boolean joueur;
    private ArrayList<int []> depPossibles;
    private Echecs echecs;
    private Noeud [] noeuds;

    public Noeud (Echecs echecs, boolean joueur) {
        n=0;
        w=0;
        this.joueur=joueur;
        this.echecs=echecs;
        depPossibles=echecs.getAllDepPossibles();
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
        if (0<=index && index<depPossibles.size())
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
            Echecs echecs2=echecs.clone();
            echecs2.action(dep[0]);
            echecs2.action(dep[1]);
            if (echecs2.getOrdre()==2)
                echecs2.action(67);
            
            noeuds[index]=new Noeud(echecs2, joueur);
            return noeuds[index];
        }
        return null;
    }

    private double uct (double c, int w, int n, int N) {
        return 1.0*w/n+c*Math.sqrt(Math.log(N)/n);
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
            Echecs echecs2=echecs.clone();
            res+=jouer(echecs2);
        }
        w+=res;
        return res;
    }

    private int jouer (Echecs echecs) {
        if (echecs.getVictoire()==0) {
            jouerCoupSemiAleatoire(echecs);
            return jouer(echecs);
        }
        else {
            if ((echecs.getVictoire()==1 && joueur) || (echecs.getVictoire()==2 && !joueur))
                return 1;
            else
                return 0;
        }
    }

    private void jouerCoupAleatoire (Echecs echecs) {
        ArrayList <int []> deps=echecs.getAllDepPossibles();
        Random random=new Random();
        int choix=random.nextInt(deps.size());
        echecs.action(deps.get(choix)[0]);
        echecs.action(deps.get(choix)[1]);
        if (echecs.getOrdre()==2)
                echecs.action(67);
    }

    private void jouerCoupSemiAleatoire (Echecs echecs) {
        ArrayList <int []> deps=echecs.getAllDepPossibles();
        for (int i=0;i<deps.size();i++) {
            Echecs echecs2=echecs.clone();
            echecs2.action(deps.get(i)[0]);
            echecs2.action(deps.get(i)[1]);
            if (echecs2.getOrdre()==2)
                echecs2.action(67);
            
            if (echecs2.getVictoire()==1 || echecs2.getVictoire()==2) {
                echecs.action(deps.get(i)[0]);
                echecs.action(deps.get(i)[1]);
                if (echecs.getOrdre()==2)
                    echecs.action(67);
                return ;
            }

        }
        Random random=new Random();
        int choix=random.nextInt(deps.size());
        echecs.action(deps.get(choix)[0]);
        echecs.action(deps.get(choix)[1]);
        if (echecs.getOrdre()==2)
                echecs.action(67);
    }

    private void jouerCoupMinMax (Echecs echecs) {
        int n=3;
        ArrayList <int []> deps=echecs.getAllDepPossibles();
        ArrayList <int []> depBest=new ArrayList <int []> ();
        ArrayList <Double> evaluations=new ArrayList <> ();
        for (int i=0;i<deps.size();i++) {
            Echecs echecs2=echecs.clone();
            echecs2.action(deps.get(i)[0]);
            echecs2.action(deps.get(i)[1]);
            double evaluation=echecs2.evaluation();
            if (evaluation>=1000 || evaluation<=-1000) {
                echecs.action(deps.get(i)[0]);
                echecs.action(deps.get(i)[1]);
                if (echecs.getOrdre()==2)
                    echecs.action(67);
                return ;
            }
            if (!echecs.getTour())
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
        echecs.action(depBest.get(choix)[0]);
        echecs.action(depBest.get(choix)[1]);
        if (echecs.getOrdre()==2)
                echecs.action(67);
    }
}
