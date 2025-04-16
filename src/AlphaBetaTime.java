public class AlphaBetaTime extends Joueur {
    private long time;

    AlphaBetaTime (long time) {
        this.time=time;
    }

    public void jouer (Echecs echecs) {
        if (echecs.getVictoire()==0) {
            long begin=System.currentTimeMillis();
            long end=begin+time;
            int profondeur=1;
            AlphaBeta alphaBeta=null;
            double[] res=null;
            long restant=time;
            int profondeurvalide=0;
            do {
                alphaBeta=new AlphaBeta(profondeur,restant);
                Echecs echecs2=echecs.clone();
                alphaBeta.setBegin(System.currentTimeMillis());
                double[] resTemp=alphaBeta.simuler(echecs2, profondeur,-999999,999999);
                restant=end-System.currentTimeMillis();
                if (restant>0) {
                    res=resTemp;
                    profondeurvalide=profondeur;
                    profondeur++;
                }
            } while (restant>0);

            if (res!=null && res[1]!=-1) {
                echecs.action((int)res[1]);
                echecs.action((int)res[2]);
                if (echecs.getOrdre() == 2)
                    echecs.action(67);
            }
        }
    }
}