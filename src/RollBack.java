public class RollBack {
    public int casePos;
    public long deps;
    public int ordre;
    public int nbCoupsNuls;
    public long blancs;
    public long noirs;
    public long speciaux;
    public long pions;
    public long cavaliers;
    public long fous;
    public long tours;
    public long reines;
    public long rois;
    
    public RollBack (int casePos, long deps, int ordre, int nbCoupsNuls, long blancs, long noirs, long speciaux, long pions, long cavaliers, long fous, long tours, long reines, long rois) {
        this.casePos = casePos;
        this.deps = deps;
        this.ordre = ordre;
        this.nbCoupsNuls = nbCoupsNuls;
        this.blancs = blancs;
        this.noirs = noirs;
        this.speciaux = speciaux;
        this.pions = pions;
        this.cavaliers = cavaliers;
        this.fous = fous;
        this.tours = tours;
        this.reines = reines;
        this.rois = rois;
    }

    public RollBack (int casePos, int ordre) {
        this.casePos = casePos;
        this.ordre = ordre;
    }
}
