import java.util.ArrayList;

public class AlphaBeta extends Joueur{
    private int profondeur;
    private long time;
    private long begin;

    AlphaBeta (int prof) {
        profondeur=prof;
        time=100000000;
    }

    AlphaBeta (int prof, long time) {
        profondeur=prof;
        this.time=time;
    }

    public void jouer (Echecs echecs) {
        if (echecs.getVictoire()==0) {
            begin=System.currentTimeMillis();
            double[] res=simuler(echecs, profondeur,-9999,9999);
            if (res[1]!=-1) {
                echecs.action((int)res[1]);
                echecs.action((int)res[2]);
                if (echecs.getOrdre()==2)
                    echecs.action(67);
            }
        }
    }

    public double[] simuler (Echecs echecs, int reste, double alpha, double beta) {
        double[] res=new double [3];
        if (reste>0 && echecs.getVictoire()==0) {
            if (System.currentTimeMillis()-begin<time) {
                ArrayList <double[]> vals=new ArrayList <> ();
                ArrayList <int []> depPossibles=triDepPossibles(echecs);
                for (int i=0;i<depPossibles.size();i++) {
                    Echecs echecs2=echecs.clone();
                    echecs2.action(depPossibles.get(i)[0]);
                    echecs2.action(depPossibles.get(i)[1]);
                    if (echecs2.getOrdre()==2)
                        echecs2.action(67);
                    res=simuler(echecs2,reste-1,alpha,beta);
                    res[1]=depPossibles.get(i)[0];
                    res[2]=depPossibles.get(i)[1];
                    if (!echecs.getTour()) {
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
                        if ((echecs.getTour() && vals.get(0)[0]>res[0]) || (!echecs.getTour() && vals.get(0)[0]<res[0]))
                            res=vals.get(0);
                        vals.remove(0);
                    }
                    return res;
                }
            }
        }
        res[0]=echecs.evaluation();
        res[1]=-1;
        res[2]=-1;
        return res;
    }

    public void setBegin (long begin) {
        this.begin=begin;
    }

    private ArrayList <int []> triDepPossibles (Echecs echecs) {
        ArrayList <int []> depPossibles=echecs.getAllDepPossibles();
        ArrayList <int []> tri=new ArrayList <int []> ();
        ArrayList <Double> values=new ArrayList<>();
        int [][] echequier=echecs.getPlateau();
        if (depPossibles!=null) {
            for (int i=0;i<depPossibles.size();i++) {
                int caseSelec=depPossibles.get(i)[0];
                int caseDep=depPossibles.get(i)[1];
                double value = Evaluation.evaluationCoup(caseSelec, caseDep, echequier);
                insert(tri,values,depPossibles.get(i),value);
            }
        }
        return tri;
    }

    private void insert (ArrayList <int []> tri, ArrayList <Double> values, int [] dep, double value) {
        int indice=getIndice(values, value);
        values.add(indice,value);
        tri.add(indice,dep);
    }

    private int getIndice (ArrayList <Double> values, double value) {
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
