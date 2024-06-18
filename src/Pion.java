import java.util.Vector;

public class Pion implements Cloneable {
    public int position;
    public boolean couleur;
    public char type;
    public boolean deplacer;

    public Pion (int pos, boolean coul, char tp) {
        position=pos;
        couleur=coul;
        type=tp;
        deplacer=false;
    }

    public Vector <Integer> getDepPossibles (Vector <Pion> pions, int [][] echequier, int posPrec, int depPrec) {
        switch (type) {
            case 'p': return depPion(pions,echequier,posPrec,depPrec);
            case 'c': return depCavalier(pions,echequier);
            case 'f': return depFou(pions,echequier);
            case 't': return depTour(pions,echequier);
            case 'q': return depReine(pions,echequier);
            case 'k': return depRoi(pions,echequier);
        }
        return new Vector <Integer> ();
    }

    private Pion getPion (int pos, Vector <Pion> pions) {
        for (int i=0;i<pions.size();i++) 
            if (pions.get(i).position==pos)
                return pions.get(i);
        return null;
    }

    private Vector <Integer> depPion (Vector <Pion> pions, int [][] echequier, int posPrec, int depPrec) {
        Vector <Integer> res=new Vector <Integer> ();
        int x=position%8;
        int depPrendreG;
        int depPrendreD;
        int dep1;
        int dep2;
        if (couleur) {
            depPrendreG=position-9;
            depPrendreD=position-7;
            dep1=position-8;
            dep2=position-16;
        }
        else {
            depPrendreG=position+7;
            depPrendreD=position+9;
            dep1=position+8;
            dep2=position+16;
        }
        if (0<=dep1 && dep1<=64) {
            if (echequier[dep1%8][dep1/8]==0) {
                addDep(pions,echequier,dep1,res);
                if (!deplacer) {
                    if (echequier[dep2%8][dep2/8]==0)
                        addDep(pions,echequier,dep2,res);
                }
            }
            if (0<x) {
                int pion=echequier[depPrendreG%8][depPrendreG/8];
                if (pion!=0 && ((pion<7 && !couleur) || ((7<=pion && couleur))))
                    addDep(pions,echequier,depPrendreG,res); 
            }
            if (x<7) {
                int pion=echequier[depPrendreD%8][depPrendreD/8];
                if (pion!=0 && ((pion<7 && !couleur) || ((7<=pion && couleur))))
                    addDep(pions,echequier,depPrendreD,res);
            }
        }

        // prise en passant
        int yDepPrec=depPrec/8;
        int yPosPrec=posPrec/8;
        if (Math.abs(yPosPrec-yDepPrec)==2) {
            int xDepPrec=depPrec%8;
            if (couleur && 24<=position && position<32 && echequier[xDepPrec][yDepPrec]==7) {
                if (xDepPrec==x-1)
                    addDep(pions, echequier, position-9, res);
                if (xDepPrec==x+1)
                    addDep(pions, echequier, position-7, res);
            }
            if (!couleur && 32<=position && position<40 && echequier[xDepPrec][yDepPrec]==1) {
                if (xDepPrec==x-1)
                    addDep(pions, echequier, position+7, res);
                if (xDepPrec==x+1)
                    addDep(pions, echequier, position+9, res);
            }
        }

        return res;
    }

    private Vector <Integer> depCavalier (Vector <Pion> pions, int [][] echequier) {
        Vector <Integer> res=new Vector <Integer> ();
        int x=position%8;
        int dep;
        dep=position-17;
        if (0<=dep && 0<x) {
            int pion=echequier[dep%8][dep/8];
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,dep,res);
        }
        dep=position-15;
        if (0<=dep && x<7) {
            int pion=echequier[dep%8][dep/8];
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,dep,res);
        }
        dep=position-10;
        if (0<=dep && 1<x) {
            int pion=echequier[dep%8][dep/8];
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,dep,res);
        }
        dep=position-6;
        if (0<=dep && x<6) {
            int pion=echequier[dep%8][dep/8];
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,dep,res);
        }
        dep=position+6;
        if (dep<64 && 1<x) {
            int pion=echequier[dep%8][dep/8];
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,dep,res);
        }
        dep=position+10;
        if (dep<=64 && x<6) {
            int pion=echequier[dep%8][dep/8];
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,dep,res);
        }
        dep=position+15;
        if (dep<64 && 0<x) {
            int pion=echequier[dep%8][dep/8];
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,dep,res);
        }
        dep=position+17;
        if (dep<64 && x<7) {
            int pion=echequier[dep%8][dep/8];
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,dep,res);
        }
        return res;
    }

    private Vector <Integer> depFou (Vector <Pion> pions, int [][] echequier) {
        Vector <Integer> res=new Vector <Integer> ();
        int x=position%8;
        int y=position/8;
        boolean bloque=false;
        int xdep=x-1;
        int ydep=y-1;
        while (xdep>=0 && ydep>=0 && !bloque) {
            int pion=echequier[xdep][ydep];
            if (pion!=0)
                bloque=true;
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,ydep*8+xdep,res);
            xdep--;
            ydep--;
        }
        bloque=false;
        xdep=x+1;
        ydep=y-1;
        while (xdep<8 && ydep>=0 && !bloque) {
            int pion=echequier[xdep][ydep];
            if (pion!=0)
                bloque=true;
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,ydep*8+xdep,res);
            xdep++;
            ydep--;
        }
        bloque=false;
        xdep=x+1;
        ydep=y+1;
        while (xdep<8 && ydep<8 && !bloque) {
            int pion=echequier[xdep][ydep];
            if (pion!=0)
                bloque=true;
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,ydep*8+xdep,res);
            xdep++;
            ydep++;
        }
        bloque=false;
        xdep=x-1;
        ydep=y+1;
        while (xdep>=0 && ydep<8 && !bloque) {
            int pion=echequier[xdep][ydep];
            if (pion!=0)
                bloque=true;
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,ydep*8+xdep,res);
            xdep--;
            ydep++;
        }
        return res;
    }

    private Vector <Integer> depTour (Vector <Pion> pions, int [][] echequier) {
        Vector <Integer> res=new Vector <Integer> ();
        int x=position%8;
        int y=position/8;
        boolean bloque=false;
        int xdep=x;
        int ydep=y-1;
        while (ydep>=0 && !bloque) {
            int pion=echequier[xdep][ydep];
            if (pion!=0)
                bloque=true;
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,ydep*8+xdep,res);
            ydep--;
        }
        bloque=false;
        xdep=x+1;
        ydep=y;
        while (xdep<8 && !bloque) {
            int pion=echequier[xdep][ydep];
            if (pion!=0)
                bloque=true;
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,ydep*8+xdep,res);
            xdep++;
        }
        bloque=false;
        xdep=x;
        ydep=y+1;
        while (ydep<8 && !bloque) {
            int pion=echequier[xdep][ydep];
            if (pion!=0)
                bloque=true;
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,ydep*8+xdep,res);
            ydep++;
        }
        bloque=false;
        xdep=x-1;
        ydep=y;
        while (xdep>=0 && !bloque) {
            int pion=echequier[xdep][ydep];
            if (pion!=0)
                bloque=true;
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,ydep*8+xdep,res);
            xdep--;
        }
        return res;
    }

    private Vector <Integer> depReine (Vector <Pion> pions, int [][] echequier) {
        Vector <Integer> res=depFou(pions, echequier);
        Vector <Integer> res2=depTour(pions, echequier);
        for (int i=0;i<res2.size();i++)
            res.add(res2.get(i));
        return res;
    }

    private Vector <Integer> depRoi (Vector <Pion> pions, int [][] echequier) {
        Vector <Integer> res=new Vector <Integer> ();
        int x=position%8;
        int y=position/8;
        int dep, pion;
        if (0<y) {
            if (0<x) {
                dep=position-9;
                pion=echequier[dep%8][dep/8];
                if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                    addDep(pions,echequier,dep,res);
            }
            dep=position-8;
            pion=echequier[dep%8][dep/8];
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,dep,res);
            if (x<7) {
                dep=position-7;
                pion=echequier[dep%8][dep/8];
                if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                    addDep(pions,echequier,dep,res);
            }
        }
        if (0<x) {
            dep=position-1;
            pion=echequier[dep%8][dep/8];
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,dep,res);
        }
        if (x<7) {
            dep=position+1;
            pion=echequier[dep%8][dep/8];
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,dep,res);
        }
        if (y<7) {
            if (0<x) {
                dep=position+7;
                pion=echequier[dep%8][dep/8];
                if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                    addDep(pions,echequier,dep,res);
            }
            dep=position+8;
            pion=echequier[dep%8][dep/8];
            if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                addDep(pions,echequier,dep,res);
            if (x<7) {
                dep=position+9;
                pion=echequier[dep%8][dep/8];
                if (pion==0 || (pion<7 && !couleur) || ((7<=pion && couleur)))
                    addDep(pions,echequier,dep,res);
            }
        }

        // roquer
        if (!deplacer) {
            if (!echec(pions, echequier, couleur)) {
                Pion tour;
                if (couleur) {
                    if (echequier[0][7]==4 && echequier[1][7]==0 && echequier[2][7]==0 && echequier[3][7]==0) {
                        tour=getPion(56, pions);
                        if (!tour.deplacer) {
                            if (depAutorise(pions,echequier,59))
                                addDep(pions, echequier, 58, res);
                        }
                    }
                    if (echequier[5][7]==0 && echequier[6][7]==0 && echequier[7][7]==4) {
                        tour=getPion(63, pions);
                        if (!tour.deplacer) {
                            if (depAutorise(pions,echequier,61))
                                addDep(pions, echequier, 62, res);
                        }
                    }
                }else {
                    if (echequier[0][0]==10 && echequier[1][0]==0 && echequier[2][0]==0 && echequier[3][0]==0) {
                        tour=getPion(0, pions);
                        if (!tour.deplacer) {
                            if (depAutorise(pions,echequier,3))
                                addDep(pions, echequier, 2, res);
                        }
                    }
                    if (echequier[5][0]==0 && echequier[6][0]==0 && echequier[7][0]==10) {
                        tour=getPion(7, pions);
                        if (!tour.deplacer) {
                            if (depAutorise(pions,echequier,5))
                                addDep(pions, echequier, 6, res);
                        }
                    }
                }
            }
        }
        return res; 
    }

    private boolean addDep (Vector <Pion> pions, int [][] echequier, int dep, Vector <Integer> deps) {
        boolean autor=depAutorise( pions,echequier,dep);
        if (autor)
            deps.add(dep);
        return autor;
    }

    public boolean depAutorise (Vector <Pion> pions, int [][] echequier, int dep) {
        int [][] echequierDep=new int [8][8];
        for (int i=0;i<8;i++)
            for (int j=0;j<8;j++)
                echequierDep[i][j]=echequier[i][j];
        
        // prise en passant
        if (type=='p' && echequierDep[dep%8][dep/8]==0) {
            if (couleur && echequierDep[dep%8][dep/8+1]==7)
                echequierDep[dep%8][dep/8+1]=0;
            if (!couleur && echequierDep[dep%8][dep/8-1]==1)
                echequierDep[dep%8][dep/8-1]=0;
        }

        echequierDep[dep%8][dep/8]=echequier[position%8][position/8];
        echequierDep[position%8][position/8]=0;
        return !echec(pions, echequierDep, couleur);
    }

    public boolean echec (Vector <Pion> pions, int [][] echequier, boolean couleurRoi) {
        boolean trouve=false;
        int k=0, x=0, y=0, val;
        while (k<64 && !trouve) {
            x=k%8;
            y=k/8;
            val=echequier[x][y];
            if ((couleurRoi && val==6) || (!couleurRoi && val==12))
                trouve=true;
            else
                k++;
        }

        // verif pion
        if (couleurRoi) {
            if (0<y) {
                if (0<x && echequier[x-1][y-1]==7)
                    return true;
                if (x<7 && echequier[x+1][y-1]==7)
                    return true;
            }
        }
        else {
            if (y<7) {
                if (0<x && echequier[x-1][y+1]==1)
                    return true;
                if (x<7 && echequier[x+1][y+1]==1)
                    return true;
            }
        }

        // verif cavalier
        int [][] pos1={{x-1,y-2},{x+1,y-2},{x-2,y-1},{x+2,y-1},{x-2,y+1},{x+2,y+1},{x-1,y+2},{x+1,y+2}};
        for (int i=0;i<pos1.length;i++) {
            int xp=pos1[i][0];
            int yp=pos1[i][1];
            if (0<=xp && xp<8 && 0<=yp && yp<8) {
                val=echequier[xp][yp];
                if ((couleurRoi && (val==8)) || (!couleurRoi && (val==2)))
                    return true;
            }
        }

        // verif roi
        int [][] pos2={{x-1,y-1},{x,y-1},{x+1,y-1},{x-1,y},{x+1,y},{x-1,y+1},{x,y+1},{x+1,y+1}};
        for (int i=0;i<pos2.length;i++) {
            int xp=pos2[i][0];
            int yp=pos2[i][1];
            if (0<=xp && xp<8 && 0<=yp && yp<8) {
                val=echequier[xp][yp];
                if ((couleurRoi && (val==11 || val==12)) || (!couleurRoi && (val==5 || val==6)))
                    return true;
            }
        }

        // verif tour, reine
        boolean bloque=false;
        int xdep=x;
        int ydep=y-1;
        while (ydep>=0 && !bloque) {
            val=echequier[xdep][ydep];
            if (val!=0)
                bloque=true;
            if ((couleurRoi && (val==10 || val==11)) || (!couleurRoi && (val==4 || val==5)))
                return true;
            ydep--;
        }
        bloque=false;
        xdep=x+1;
        ydep=y;
        while (xdep<8 && !bloque) {
            val=echequier[xdep][ydep];
            if (val!=0)
                bloque=true;
            if ((couleurRoi && (val==10 || val==11)) || (!couleurRoi && (val==4 || val==5)))
                return true;
            xdep++;
        }
        bloque=false;
        xdep=x;
        ydep=y+1;
        while (ydep<8 && !bloque) {
            val=echequier[xdep][ydep];
            if (val!=0)
                bloque=true;
            if ((couleurRoi && (val==10 || val==11)) || (!couleurRoi && (val==4 || val==5)))
                return true;
            ydep++;
        }
        bloque=false;
        xdep=x-1;
        ydep=y;
        while (xdep>=0 && !bloque) {
            val=echequier[xdep][ydep];
            if (val!=0)
                bloque=true;
            if ((couleurRoi && (val==10 || val==11)) || (!couleurRoi && (val==4 || val==5)))
                return true;
            xdep--;
        }

        // verif fou, reine
        bloque=false;
        xdep=x-1;
        ydep=y-1;
        while (xdep>=0 && ydep>=0 && !bloque) {
            val=echequier[xdep][ydep];
            if (val!=0)
                bloque=true;
            if ((couleurRoi && (val==9 || val==11)) || (!couleurRoi && (val==3 || val==5))) 
                return true;
            ydep--;
            xdep--;
        }
        bloque=false;
        xdep=x+1;
        ydep=y-1;
        while (xdep<8 && ydep>=0 && !bloque) {
            val=echequier[xdep][ydep];
            if (val!=0)
                bloque=true;
            if ((couleurRoi && (val==9 || val==11)) || (!couleurRoi && (val==3 || val==5))) 
                return true;
            ydep--;
            xdep++;
        }
        bloque=false;
        xdep=x+1;
        ydep=y+1;
        while (xdep<8 && ydep<8 && !bloque) {
            val=echequier[xdep][ydep];
            if (val!=0)
                bloque=true;
            if ((couleurRoi && (val==9 || val==11)) || (!couleurRoi && (val==3 || val==5))) 
                return true;
            ydep++;
            xdep++;
        }
        bloque=false;
        xdep=x-1;
        ydep=y+1;
        while (xdep>=0 && ydep<8 && !bloque) {
            val=echequier[xdep][ydep];
            if (val!=0)
                bloque=true;
            if ((couleurRoi && (val==9 || val==11)) || (!couleurRoi && (val==3 || val==5))) 
                return true;
            ydep++;
            xdep--;
        }
        return false;
    }

    protected Pion clone() {
        Pion res=new Pion(position, couleur, type);
        res.deplacer=deplacer;
        return res;
    }

}
