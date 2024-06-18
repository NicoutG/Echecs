import java.util.Vector;

public class AlphaBeta extends Joueur{
    private int profondeur;

    AlphaBeta (int prof) {
        profondeur=prof;
    }

    public void jouer (Plateau plateau) {
        Plateau plateau2=plateau.clone();
        int [] res=simuler(plateau2, profondeur,-9999,9999);
        if (res[1]!=-1) {
            plateau.action(res[1]);
            plateau.action(res[2]);
            if (plateau.getOrdre()==3)
                plateau.action(67);
        }
    }

    private int [] simuler (Plateau plateau, int reste, int alpha, int beta) {
        int [] res=new int [3];
        if (reste>0 || plateau.getVictoire()!=0) {
            Vector <int []> vals=new Vector <int []> ();
            int pionSize=plateau.getPions().size();
            for (int i=0;i<pionSize;i++) {
                Pion pion=plateau.getPions().get(i);
                if (pion.couleur==plateau.getTour()) {
                    Plateau plateau2=plateau.clone();
                    plateau2.action(pion.position);
                    if (plateau2.getDepPossiblesPionSelec()!=null) {
                        for (int dep : plateau2.getDepPossiblesPionSelec()) {
                            plateau2=plateau.clone();
                            plateau2.action(pion.position);
                            plateau2.action(dep);
                            if (plateau2.getOrdre()==3)
                                plateau2.action(67);
                            res=simuler(plateau2,reste-1,alpha,beta);
                            res[1]=pion.position;
                            res[2]=dep;
                            if (!plateau.getTour()) {
                                if (alpha>=res[0])
                                    return res;
                                beta=Math.min(beta,res[0]);
                            }
                            else {
                                if (beta<=res[0])
                                    return res;
                                alpha=Math.max(alpha,res[0]);
                            }
                            vals.add(res);
                        }
                    }
                }
            }

            // récupération de la meilleur ou de la pire valeur
            if (vals.size()!=0) {
                res=vals.get(0);
                vals.remove(0);
                int size=vals.size();
                for (int i=0;i<size;i++) {
                    if ((plateau.getTour() && vals.get(0)[0]>res[0]) || (!plateau.getTour() && vals.get(0)[0]<res[0]))
                        res=vals.get(0);
                    vals.remove(0);
                }
                return res;
            }
        }
        res[0]=plateau.evaluation();
        res[1]=-1;
        res[2]=-1;
        return res;
    }

}
