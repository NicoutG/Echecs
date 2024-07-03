import java.util.Observable;
import java.util.Vector;

public class Plateau extends Observable {
    protected Vector <Pion> pions;
    protected boolean tour;
    protected int ordre;
    protected int caseSelec;
    protected Pion pionSelec;
    protected int indicePionSelec;
    protected Vector <Vector <Integer>> depPossibles;
    protected int caseDep;
    protected int [][] echequier;
    protected int victoire;
    protected int coupEgaliteBlanc;
    protected int coupEgaliteNoir;

    protected Vector <int []> config;

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
        pions=null;
        caseSelec=-1;
        pionSelec=null;
        depPossibles=null;
        caseDep=-1;
        coupEgaliteBlanc=0;
        coupEgaliteNoir=0;
        echequier=new int [8][8];
        victoire=0;
    }

    public void init () {
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
        coupEgaliteBlanc=0;
        coupEgaliteNoir=0;
        echequier=genereEchequier(pions);
        victoire=0;
        config=new Vector <int []> ();
        nouveauTour(true);
    }

    public void maj () {
        setChanged();
        notifyObservers();
    }

    private void addConfig () {
        int [] res=new int [2];
        res[0]=0;
        res[1]=0;
        for (Pion pion : pions) {
            int val=0;
            if (pion.couleur)
                switch (pion.type) {
                    case 'p': val=1;break;
                    case 'c': val=2;break;
                    case 'f': val=3;break;
                    case 't': val=4;break;
                    case 'q': val=5;break;
                    case 'k': val=6;break;
                }
            else {
                switch (pion.type) {
                    case 'p': val=7;break;
                    case 'c': val=8;break;
                    case 'f': val=9;break;
                    case 't': val=10;break;
                    case 'q': val=11;break;
                    case 'k': val=12;break;
                }
            }
            res[0]+=pion.position*val;
        }

        for (Vector <Integer> depPion : depPossibles)
            res[1]+=depPion.size();
        config.add(0,res);
    }

    private void nouveauTour (boolean tourSuivant) {
        // récupération des déplacements possibles
        depPossibles=new Vector <Vector <Integer>> ();
        for (int i=0;i<pions.size();i++)
            depPossibles.add(pions.get(i).getDepPossibles(pions,echequier,caseSelec,caseDep));
        
        addConfig();
        
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
        switch (victoire) {
            case 0: {
                int res=0;
                for (int i=0;i<pions.size();i++)
                    res+=evaluationPion(i);
                return res;
            }
            case 1: return 1000;
            case 2: return -1000;
            case 3: return 0;
        }
        return 0;
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
                    int indicePion=getPion(nb);
                    if (indicePion!=-1) {
                        Pion pion=pions.get(indicePion);
                        if (pion.couleur==tour) {
                            ordre=0;
                            action(nb);
                        }
                    }
                    return false;
                }
                deplacerPion(pionSelec, nb);
            }
            else {
                if (ordre==0) { // selection du pion
                    indicePionSelec=getPion(nb);
                    if (indicePionSelec==-1)
                        return false;
                    pionSelec=pions.get(indicePionSelec);
                    if (pionSelec.couleur!=tour)
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
            maj();
            return true;
        }
        return false;
    }

    private void deplacerPion (Pion pion, int pos) {
        caseSelec=pion.position;
        caseDep=pos;
        boolean mange=false;

        // si roque
        if (pion.type=='k' && pion.deplacer==0 && ((pion.couleur && (pos==58 || pos==62)) || (!pion.couleur && (pos==2 || pos==6)))) {
            Pion tourRoque=null;
            switch (pos) {
                case 58: {
                    tourRoque=pions.get(getPion(56));
                    tourRoque.position=59;
                    echequier[0][7]=0;
                    echequier[3][7]=4;
                }break;
                case 62: {
                    tourRoque=pions.get(getPion(63));
                    tourRoque.position=61;
                    echequier[7][7]=0;
                    echequier[5][7]=4;
                }break;
                case 2: {
                    tourRoque=pions.get(getPion(0));
                    tourRoque.position=3;
                    echequier[0][0]=0;
                    echequier[3][0]=10;
                }break;
                case 6: {
                    tourRoque=pions.get(getPion(7));
                    tourRoque.position=5;
                    echequier[0][0]=0;
                    echequier[5][0]=10;
                }break;
            }
            tourRoque.deplacer++;

        }
        else {
            int indicePion=getPion(pos);
            Pion pionMange=null;
            if (indicePion!=-1) {
                pionMange=pions.get(indicePion);
                mange=true;
                pions.remove(pionMange);
            }
        }

        // prise en passant
        if (pion.type=='p' && echequier[caseDep%8][caseDep/8]==0) {
            if (pion.couleur && echequier[caseDep%8][caseDep/8+1]==7) {
                echequier[caseDep%8][caseDep/8+1]=0;
                int indicePion=getPion(pos+8);
                Pion pionMange=null;
                if (indicePion!=-1) {
                    pionMange=pions.get(indicePion);
                    mange=true;
                    pions.remove(pionMange);
                }
            }
            if (!pion.couleur && echequier[caseDep%8][caseDep/8-1]==1) {
                echequier[caseDep%8][caseDep/8-1]=0;
                int indicePion=getPion(pos-8);
                Pion pionMange=null;
                if (indicePion!=-1) {
                    pionMange=pions.get(indicePion);
                    mange=true;
                    pions.remove(pionMange);
                }
            }
        }

        if (pionSelec.type=='p') {
            config=new Vector <int []> ();
            if (tour)
                coupEgaliteBlanc=0;
            else
                coupEgaliteNoir=0;
        }
        if (!mange) {
            if (tour)
                coupEgaliteBlanc++;
            else
                coupEgaliteNoir++;
        }
        else
            config=new Vector <int []> ();
        pion.deplacer++;
        pion.position=pos;
        echequier[caseDep%8][caseDep/8]=echequier[caseSelec%8][caseSelec/8];
        echequier[caseSelec%8][caseSelec/8]=0;

        // transformation de pion
        if (pion.type=='p' && ((pion.couleur && pos<8) || (!pion.couleur && 56<=pos)))
            ordre=3;
        else
            nouveauTour(!tour);
    }

    private int getPion (int pos) {
        for (int i=0;i<pions.size();i++) 
            if (pions.get(i).position==pos)
                return i;
        return -1;
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
        return depPossibles.get(indicePionSelec);
    }

    public Vector <int []> getDepPossibles () {
        Vector <int []> deps=new Vector <int []> ();
        for (int i=0;i<depPossibles.size();i++) {
            if (pions.get(i).couleur==tour)
                for (int j=0;j<depPossibles.get(i).size();j++) {
                    deps.add(new int [2]);
                    deps.lastElement()[0]=pions.get(i).position;
                    deps.lastElement()[1]=depPossibles.get(i).get(j);
                }
        }
        return deps;
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
        if (verifTripleRep() || (coupEgaliteBlanc>=50 && coupEgaliteNoir>=50))
            return 3;
        return 0;
    }

    private boolean verifTripleRep () {
        if (6<config.size()) {
            for (int i=0;i<config.size()-1;i++)  {
                int [] comp=config.get(i);
                int nb=1;
                for (int j=i+1;j<config.size();j++) {
                    int [] get=config.get(j);
                    if (comp[0]==get[0] && comp[1]==get[1]) {
                        nb++;
                        if (3<=nb)
                            return true;
                    }
                }
            }
        }
        return false;
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

    public int [][] getEchequier () {
        return echequier;
    }

    protected Plateau clone() {
        Plateau res=new Plateau();
        res.pions=new Vector <Pion> ();
        for (Pion pion: pions)
            res.pions.add(pion.clone());
        res.tour=tour;
        res.ordre=ordre;
        res.caseSelec=caseSelec;
        if (pionSelec!=null)
            res.pionSelec=pionSelec.clone();
        res.depPossibles=new Vector <Vector <Integer>> ();
        for (int i=0;i<depPossibles.size();i++) {
            res.depPossibles.add(new Vector<>());
            for (int dep : depPossibles.get(i))
                res.depPossibles.get(i).add(dep);
        }
        res.caseDep=caseDep;
        for (int i=0;i<8;i++)
            for (int j=0;j<8;j++)
                res.echequier[i][j]=echequier[i][j];
        res.victoire=victoire;
        res.coupEgaliteBlanc=coupEgaliteBlanc;
        res.coupEgaliteNoir=coupEgaliteNoir;
        res.config=new Vector<int []>();
        for (int i=0;i<config.size();i++) {
            int [] copie=new int [2];
            int [] original=config.get(i);
            copie[0]=original[0];
            copie[1]=original[1];
            res.config.add(copie);
        }
        return res;
    }

}
