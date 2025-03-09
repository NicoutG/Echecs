public class AlphaBetaTime2 extends Joueur {
    private long time;

    AlphaBetaTime2 (long time) {
        this.time=time;
    }

    public void jouer (Echecs echecs) {
        if (echecs.getVictoire()==0) {
            long begin=System.currentTimeMillis();
            long end=begin+time;
            int profondeur=1;
            AlphaBeta2 alphaBeta=null;
            Parcours res = null;
            long restant = time;
            int profondeurvalide = 0;
            Parcours resTemp = null;
            do {
                alphaBeta = new AlphaBeta2(profondeur,restant);
                Echecs echecs2 = echecs.clone();
                alphaBeta.setBegin(System.currentTimeMillis());
                resTemp = alphaBeta.simuler(echecs2, profondeur,-999999,999999, resTemp);
                restant = end - System.currentTimeMillis();
                if (restant > 0) {
                    res = resTemp.clone();
                    profondeurvalide = profondeur;
                    profondeur++;
                }
            } while (restant > 0);
            
            System.out.println("Profondeur : "+profondeurvalide);

            if (res != null && res.actions.size() > 0) {
                do {
                    echecs.action(res.actions.remove(0));
                }while(echecs.getOrdre() > 0 && res.actions.size() > 0);
            }
        }
    }
}