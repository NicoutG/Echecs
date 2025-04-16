import java.util.ArrayList;

public class MinMax extends Joueur{
    private int profondeur;

    MinMax (int prof) {
        profondeur=prof;
    }

    public void jouer (Echecs echecs) {
        if (echecs.getVictoire()==0) {
            Echecs echecs2=echecs.clone();
            double[] res=simuler(echecs2, profondeur);
            if (res[1]!=-1) {
                echecs.action((int)res[1]);
                echecs.action((int)res[2]);
                if (echecs.getOrdre()==2)
                    echecs.action(67);
            }
        }
    }

    public double[] simuler (Echecs echecs, int reste) {
        double[] res=new double [3];
        if (reste>0 && echecs.getVictoire()==0) {
            ArrayList <double[]> vals=new ArrayList <> ();
            ArrayList <int []> depPossibles=echecs.getAllDepPossibles();
            for (int i=0;i<depPossibles.size();i++) {
                Echecs echecs2=echecs.clone();
                echecs2.action(depPossibles.get(i)[0]);
                echecs2.action(depPossibles.get(i)[1]);
                if (echecs2.getOrdre()==2)
                    echecs2.action(67);
                res=simuler(echecs2,reste-1);
                res[1]=depPossibles.get(i)[0];
                res[2]=depPossibles.get(i)[1];
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
        res[0]=Evaluation.evaluation(echecs);
        res[1]=-1;
        res[2]=-1;
        return res;
    }
}
