import java.util.Observable;
import java.util.Vector;

public class Plateau extends Observable {
    private Vector <Pion> pions;
    private boolean tour;
    private int ordre;
    private int caseSelec;
    private Pion pionSelec;
    private Vector <Integer> depPossibles;
    private int caseDep;
    private int [][] echequier;
    private int victoire=0;

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
        tour=true;
        ordre=0;
        caseSelec=-1;
        pionSelec=null;
        depPossibles=null;
        caseDep=-1;
        echequier=genereEchequier(pions);
    }

    public void maj () {
        setChanged();
        notifyObservers();
    }
    
    public boolean action (int nb) {
        if (victoire==0) {
            if (ordre==1) { // selection du déplacement
                boolean trouve=false;
                int i=0;
                if (0<depPossibles.size())
                    do {
                        if (nb==depPossibles.get(i))
                            trouve=true;
                        i++;
                    }while (!trouve && i<depPossibles.size());
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
                    depPossibles=pionSelec.getDepPossibles(pions,echequier,caseSelec,caseDep);
                    caseSelec=nb;
                    ordre=1;
                }
                else {
                    switch (nb) {
                        case 0: pionSelec.type='c';break;
                        case 1: pionSelec.type='f';break;
                        case 2: pionSelec.type='t';break;
                        case 3: pionSelec.type='q';break;
                    }
                    echequier[pionSelec.position%8][pionSelec.position/8]+=nb+1;
                    ordre=0;
                    tour=!tour;
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
            if (pionMange!=null)
                pions.remove(pionMange);
        }

        // prise en passant
        if (pion.type=='p' && echequier[caseDep%8][caseDep/8]==0) {
            if (pion.couleur && echequier[caseDep%8][caseDep/8+1]==7) {
                echequier[caseDep%8][caseDep/8+1]=0;
                Pion pionMange=getPion(pos+8);
                if (pionMange!=null)
                    pions.remove(pionMange);
            }
            if (!pion.couleur && echequier[caseDep%8][caseDep/8-1]==1) {
                echequier[caseDep%8][caseDep/8-1]=0;
                Pion pionMange=getPion(pos-8);
                if (pionMange!=null)
                    pions.remove(pionMange);
            }
        }

        pion.deplacer=true;
        pion.position=pos;
        echequier[caseDep%8][caseDep/8]=echequier[caseSelec%8][caseSelec/8];
        echequier[caseSelec%8][caseSelec/8]=0;

        // transformation de pion
        if (pion.type=='p' && ((pion.couleur && pos<8) || (!pion.couleur && 56<=pos)))
            ordre=3;
        else {
            // verfication de la victoire
            if (echecEtMat(pions,echequier,tour)) {
                if (tour)
                    victoire=1;
                else
                    victoire=2;
            }

            ordre=0;
            tour=!tour;
        }
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

    public Vector <Integer> getDepPossibles() {
        return depPossibles;
    }

    public int getVictoire() {
        return victoire;
    }

    public boolean echecEtMat (Vector <Pion> pis, int [][] echeq ,boolean joueur) {
        Pion pion=null;
        for (int i=0;i<pis.size();i++) {
            pion=pis.get(i);
            if (pion.couleur==!joueur)
                if (pion.getDepPossibles(pis,echeq,caseSelec,caseDep).size()!=0)
                    return false;
        }
        return true;
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
