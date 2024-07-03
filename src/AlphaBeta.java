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
            Vector <int []> depPossibles=triDepPossibles(plateau);
            for (int i=0;i<depPossibles.size();i++) {
                Plateau plateau2=plateau.clone();
                plateau2.action(depPossibles.get(i)[0]);
                plateau2.action(depPossibles.get(i)[1]);
                if (plateau2.getOrdre()==3)
                    plateau2.action(67);
                res=simuler(plateau2,reste-1,alpha,beta);
                res[1]=depPossibles.get(i)[0];
                res[2]=depPossibles.get(i)[1];
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

    private Vector <int []> triDepPossibles (Plateau plateau) {
        Vector <int []> depPossibles=plateau.getDepPossibles();
        Vector <int []> tri=new Vector <int []> ();
        Vector <Integer> values=new Vector <Integer> ();
        int [][] echequier=plateau.getEchequier();
        for (int i=0;i<depPossibles.size();i++) {
            int caseSelec=depPossibles.get(i)[0];
            int caseDep=depPossibles.get(i)[1];
            int valSelec=0;
            int valDep=0;
            int value=0;
            if (echequier[caseDep%8][caseDep/8]!=0) {
                switch ((echequier[caseSelec%8][caseSelec/8]-1)%6) {
                    case 0: valSelec=1;break;
                    case 1: valSelec=3;break;
                    case 2: valSelec=3;break;
                    case 3: valSelec=5;break;
                    case 4: valSelec=9;break;
                    case 5: valSelec=10;break;
                }
                switch ((echequier[caseDep%8][caseDep/8]-1)%6) {
                    case 0: valDep=100;break;
                    case 1: valDep=300;break;
                    case 2: valDep=300;break;
                    case 3: valDep=500;break;
                    case 4: valDep=900;break;
                    case 5: valDep=1000;break;
                }
                value=valDep-valSelec;
            }
            else
                value=0;
            insert(tri,values,depPossibles.get(i),value);
        }
        return tri;
    }

    private void insert (Vector <int []> tri, Vector <Integer> values, int [] dep, int value) {
        int indice=getIndice(values, value);
        values.add(indice,value);
        tri.add(indice,dep);
    }

    private int getIndice (Vector <Integer> values, int value) {
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
