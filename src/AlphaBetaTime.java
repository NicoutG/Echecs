public class AlphaBetaTime extends Joueur {
    private long time;

    AlphaBetaTime (long time) {
        this.time=time;
    }

    public void jouer (Plateau plateau) {
        if (plateau.getVictoire()==0) {
            long begin=System.currentTimeMillis();
            long end=begin+time;
            int profondeur=1;
            AlphaBeta alphaBeta=null;
            int [] res=null;
            long restant=time;
            int profondeurvalide=0;
            do {
                alphaBeta=new AlphaBeta(profondeur,restant);
                Plateau plateau2=plateau.clone();
                alphaBeta.setBegin(System.currentTimeMillis());
                int [] resTemp=alphaBeta.simuler(plateau2, profondeur,-9999,9999);
                restant=end-System.currentTimeMillis();
                if (restant>0) {
                    res=resTemp;
                    profondeurvalide=profondeur;
                    profondeur++;
                }
            } while (restant>0);
            
            System.out.println("Profondeur : "+profondeurvalide);

            if (res!=null && res[1]!=-1) {
                plateau.action(res[1]);
                plateau.action(res[2]);
                if (plateau.getOrdre()==3)
                    plateau.action(67);
            }
        }
    }
}