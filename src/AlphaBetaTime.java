public class AlphaBetaTime extends Joueur {
    private long time;

    AlphaBetaTime (long time) {
        this.time=time;
    }

    public void jouer (Plateau plateau) {
        long begin=System.currentTimeMillis();
        long end=begin+time;
        int profondeur=1;
        AlphaBeta alphaBeta=null;
        int [] res=null;
        long restant=time;
        int profondeurvalide=0;
        long duree=0;
        do {
            alphaBeta=new AlphaBeta(profondeur,restant);
            Plateau plateau2=plateau.clone();
            long beginProf=System.currentTimeMillis();
            alphaBeta.setBegin(System.currentTimeMillis());
            int [] resTemp=alphaBeta.simuler(plateau2, profondeur,-9999,9999);
            duree=System.currentTimeMillis()-beginProf;
            restant=end-System.currentTimeMillis();
            if (restant>0) {
                res=resTemp;
                profondeurvalide=profondeur;
                profondeur++;
            }
        } while (restant>0 && duree<restant);
        
        System.out.println("Profondeur : "+profondeurvalide);

        if (res!=null && res[1]!=-1) {
            plateau.action(res[1]);
            plateau.action(res[2]);
            if (plateau.getOrdre()==3)
                plateau.action(67);
        }
    }
}