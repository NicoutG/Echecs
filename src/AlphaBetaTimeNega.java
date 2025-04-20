public class AlphaBetaTimeNega extends Joueur {
    private long time;

    AlphaBetaTimeNega (long time) {
        this.time=time;
    }

    public void jouer (Echecs echecs) {
        if (echecs.getVictoire()==0) {
            long begin=System.currentTimeMillis();
            long end=begin+time;
            int profondeur=1;
            AlphaBetaNega alphaBeta=null;
            Parcours2 res = null;
            long restant = time;
            int profondeurvalide = 0;
            Parcours2 resTemp = null;
            long start = System.currentTimeMillis();
            long duration;
            do {
                alphaBeta = new AlphaBetaNega(profondeur,restant);
                Echecs echecs2 = echecs.clone();
                echecs2.activateRollBack();
                alphaBeta.setBegin(System.currentTimeMillis());
                resTemp = alphaBeta.simuler(echecs2, profondeur, 0,-Evaluation.MAXVAL,Evaluation.MAXVAL, resTemp);
                restant = end - System.currentTimeMillis();
                if (restant > 0) {
                    res = resTemp.clone();
                    profondeurvalide = profondeur;
                    profondeur++;
                }
                long endTime = System.currentTimeMillis();
                duration = endTime - start;
                start = endTime;
            } while (restant > 0 && restant > duration);

            if (res != null && res.actions.length > 0) {
                int i = 0;
                do {
                    echecs.action(res.actions[i]);
                    i++;
                }while(echecs.getOrdre() > 0 && i < res.actions.length);
            }
        }
    }
}