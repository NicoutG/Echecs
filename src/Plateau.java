import java.util.Observable;
import java.util.Vector;

public class Plateau extends Observable {
    private Vector <Pion> pions;
    private boolean tour;
    private int ordre;
    private int caseSelec;
    private Pion pionSelec;
    private Vector <Vector <Integer>> depPossibles;
    private int caseDep;
    private int [][] echequier;
    private int victoire;

    private Vector <Integer> caseSelecPre;
    private Vector <Integer> caseDepPre;
    private Vector <Pion> pionSelecPre;
    private Vector <Pion> pionMangePre;

    private final int valPion=10;
    private final int valCavalier=30;
    private final int valFou=30;
    private final int valTour=50;
    private final int valReine=90;
    private final int valRoi=100;
    private final int valDepPion=2;
    private final int valDepCavalier=3;
    private final int valDepFou=1;
    private final int valDepTour=1;
    private final int valDepReine=1;
    private final int valDepRoi=5;
    private final double coefPrise=0.5;

    public Plateau () {
        pions=new Vector <Pion> ();
        pions.add(new Pion(0,false,'t'));
        pions.add(new Pion(1,false,'c'));
        pions.add(new Pion(2,false,'f'));
        pions.add(new Pion(3,false,'q'));
        pions.add(new Pion(4,false,'k'));
        pions.add(new Pion(5,false,'f'));
        pions.add(new Pion(6,false,'c'));
        pions.add(new Pion(7,false,'t'));
        for (int i=8;i<16;i++)
            pions.add(new Pion(i,false,'p'));
        for (int i=48;i<56;i++)
            pions.add(new Pion(i,true,'p'));
        pions.add(new Pion(56,true,'t'));
        pions.add(new Pion(57,true,'c'));
        pions.add(new Pion(58,true,'f'));
        pions.add(new Pion(59,true,'q'));
        pions.add(new Pion(60,true,'k'));
        pions.add(new Pion(61,true,'f'));
        pions.add(new Pion(62,true,'c'));
        pions.add(new Pion(63,true,'t'));
        caseSelec=-1;
        pionSelec=null;
        depPossibles=null;
        caseDep=-1;
        echequier=genereEchequier(pions);
        victoire=0;
        caseSelecPre=new Vector <Integer> ();
        caseDepPre=new Vector <Integer> ();
        caseSelecPre=new Vector <Integer> ();
        pionSelecPre=new Vector <Pion> ();
        pionMangePre=new Vector <Pion> ();
        nouveauTour(true);
    }

    public void maj () {
        setChanged();
        notifyObservers();
    }

    private void nouveauTour (boolean tourSuivant) {
        
        // récupération des déplacements possibles
        depPossibles=new Vector <Vector <Integer>> ();
        for (int i=0;i<pions.size();i++)
            depPossibles.add(pions.get(i).getDepPossibles(pions,echequier,caseSelec,caseDep));
        
        // verfication de la victoire
        victoire=echecEtMat(pions,echequier);

        ordre=0;
        tour=tourSuivant;
    }

    private int evaluationPion (int nb) {
        int res=0;
        Vector <Integer> depPossiblesPion=depPossibles.get(nb);
        switch (pions.get(nb).type) {
            case 'p': res+=valPion+depPossiblesPion.size()*valDepPion;break;
            case 'c': res+=valCavalier+depPossiblesPion.size()*valDepCavalier;break;
            case 'f': res+=valFou+depPossiblesPion.size()*valDepFou;break;
            case 't': res+=valTour+depPossiblesPion.size()*valDepTour;break;
            case 'q': res+=valReine+depPossiblesPion.size()*valDepReine;break;
            case 'k': res+=depPossiblesPion.size()*valDepRoi;break;
        }
        if (pions.get(nb).couleur==tour)
            for (int i=0;i<depPossiblesPion.size();i++) {
                int val=echequier[depPossiblesPion.get(i)%8][depPossiblesPion.get(i)/8];
                if (val!=0)
                    switch ((val-1)%6) {
                        case 0:res+=coefPrise*valPion;break;
                        case 1:res+=coefPrise*valCavalier;break;
                        case 2:res+=coefPrise*valFou;break;
                        case 3:res+=coefPrise*valTour;break;
                        case 4:res+=coefPrise*valReine;break;
                        case 5:res+=coefPrise*valRoi;break;
                    }
            }
        if (!pions.get(nb).couleur)
            res=-res;
        return res;
    }

    public int evaluation () {
        int res=0;
        for (int i=0;i<pions.size();i++)
            res+=evaluationPion(i);
        return res;
    }
    
    public boolean action (int nb) {
        if (victoire==0) {
            if (ordre==1) { // selection du déplacement
                boolean trouve=false;
                Vector <Integer> depPossiblesPionSelec=getDepPossiblesPionSelec();
                int i=0;
                if (0<depPossiblesPionSelec.size())
                    do {
                        if (nb==depPossiblesPionSelec.get(i))
                            trouve=true;
                        i++;
                    }while (!trouve && i<depPossiblesPionSelec.size());
                if (!trouve) { // si le déplacement ne fait pas partie des mouvements autorisés
                    Pion pion=getPion(nb);
                    if (pion!=null && pion.couleur==tour) {
                        ordre=0;
                        action(nb);
                    }
                    return false;
                }
                deplacerPion(pionSelec, nb);
            }
            else {
                if (ordre==0) { // selection du pion
                    pionSelec=getPion(nb);
                    if (pionSelec==null || pionSelec.couleur!=tour)
                        return false;
                    caseSelec=nb;
                    ordre=1;
                }
                else {
                    if (nb<64 || 67<nb)
                        return false;
                    switch (nb) {
                        case 64: pionSelec.type='c';break;
                        case 65: pionSelec.type='f';break;
                        case 66: pionSelec.type='t';break;
                        case 67: pionSelec.type='q';break;
                    }
                    echequier[pionSelec.position%8][pionSelec.position/8]+=nb-63;
                    nouveauTour(!tour);
                }
            }
            setChanged();
            notifyObservers();
            return true;
        }
        return false;
    }

    private void deplacerPion (Pion pion, int pos) {
        caseSelec=pion.position;
        caseDep=pos;
        boolean mange=false;

        // si roque
        if (pion.type=='k' && !pion.deplacer && ((pion.couleur && (pos==58 || pos==62)) || (!pion.couleur && (pos==2 || pos==6)))) {
            Pion tourRoque=null;
            switch (pos) {
                case 58: {
                    tourRoque=getPion(56);
                    tourRoque.position=59;
                    echequier[0][7]=0;
                    echequier[3][7]=4;
                }break;
                case 62: {
                    tourRoque=getPion(63);
                    tourRoque.position=61;
                    echequier[7][7]=0;
                    echequier[5][7]=4;
                }break;
                case 2: {
                    tourRoque=getPion(0);
                    tourRoque.position=3;
                    echequier[0][0]=0;
                    echequier[3][0]=10;
                }break;
                case 6: {
                    tourRoque=getPion(7);
                    tourRoque.position=5;
                    echequier[0][0]=0;
                    echequier[5][0]=10;
                }break;
            }
            tourRoque.deplacer=true;

        }
        else {
            Pion pionMange=getPion(pos);
            if (pionMange!=null) {
                mange=true;
                pionMangePre.add(pionMange);
                pions.remove(pionMange);
            }
        }

        // prise en passant
        if (pion.type=='p' && echequier[caseDep%8][caseDep/8]==0) {
            if (pion.couleur && echequier[caseDep%8][caseDep/8+1]==7) {
                echequier[caseDep%8][caseDep/8+1]=0;
                Pion pionMange=getPion(pos+8);
                if (pionMange!=null) {
                    mange=true;
                    pionMangePre.add(pionMange);
                    pions.remove(pionMange);
                }
            }
            if (!pion.couleur && echequier[caseDep%8][caseDep/8-1]==1) {
                echequier[caseDep%8][caseDep/8-1]=0;
                Pion pionMange=getPion(pos-8);
                if (pionMange!=null) {
                    mange=true;
                    pionMangePre.add(pionMange);
                    pions.remove(pionMange);
                }
            }
        }

        caseSelecPre.add(caseSelec);
        caseDepPre.add(caseDep);
        pionSelecPre.add(pion);
        if (!mange)
            pionMangePre.add(null);

        pion.deplacer=true;
        pion.position=pos;
        echequier[caseDep%8][caseDep/8]=echequier[caseSelec%8][caseSelec/8];
        echequier[caseSelec%8][caseSelec/8]=0;

        // transformation de pion
        if (pion.type=='p' && ((pion.couleur && pos<8) || (!pion.couleur && 56<=pos)))
            ordre=3;
        else
            nouveauTour(!tour);
    }

    private Pion getPion (int pos) {
        for (int i=0;i<pions.size();i++) 
            if (pions.get(i).position==pos)
                return pions.get(i);
        return null;
    }

    public Vector <Pion> getPions() {
        return pions;
    }

    public boolean getTour() {
        return tour;
    }

    public int getOrdre() {
        return ordre;
    }

    public int getCaseSelec() {
        return caseSelec;
    }

    public int getCaseDep() {
        return caseDep;
    }

    public Vector <Integer> getDepPossiblesPionSelec () {
        for (int i=0;i<pions.size();i++)
            if (caseSelec==pions.get(i).position)
                return depPossibles.get(i);
        return null;
    }

    public int getVictoire() {
        return victoire;
    }

    public int echecEtMat (Vector <Pion> pis, int [][] echeq) {
        boolean depPosJ1=false;
        boolean depPosJ2=false;
        Pion pion=null;
        for (int i=0;i<pis.size();i++) {
            pion=pis.get(i);
            if (pion.couleur) {
                if (depPossibles.get(i).size()>0)
                    depPosJ1=true;
            }
            else
                if (depPossibles.get(i).size()>0)
                    depPosJ2=true;
        }
        if (!depPosJ2)
            return 1;
        if (!depPosJ1)
            return 2;
        return 0;
    }

    private int [][] genereEchequier (Vector <Pion> pions) {
        int [][] echequier=new int [8][8];
        for (int i=0;i<pions.size();i++) {
            Pion pion=pions.get(i);
            if (pion.couleur)
                switch (pion.type) {
                    case 'p': echequier[pion.position%8][pion.position/8]=1;break;
                    case 'c': echequier[pion.position%8][pion.position/8]=2;break;
                    case 'f': echequier[pion.position%8][pion.position/8]=3;break;
                    case 't': echequier[pion.position%8][pion.position/8]=4;break;
                    case 'q': echequier[pion.position%8][pion.position/8]=5;break;
                    case 'k': echequier[pion.position%8][pion.position/8]=6;break;
                }
            else {
                switch (pion.type) {
                    case 'p': echequier[pion.position%8][pion.position/8]=7;break;
                    case 'c': echequier[pion.position%8][pion.position/8]=8;break;
                    case 'f': echequier[pion.position%8][pion.position/8]=9;break;
                    case 't': echequier[pion.position%8][pion.position/8]=10;break;
                    case 'q': echequier[pion.position%8][pion.position/8]=11;break;
                    case 'k': echequier[pion.position%8][pion.position/8]=12;break;
                }
            }
        }
        return echequier;
    }

}
