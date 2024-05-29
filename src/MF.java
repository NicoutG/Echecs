import java.util.Observer;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Observable;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.GridLayout;


/**
 * Classe MF permet la gestion de l'interface graphique
 */

public class MF extends JFrame implements Observer{
    
    private Plateau plateau;

    private JButton tab [][] = null;

    private JButton changePion [] = null;

    private HashMap<String, ImageIcon> images = null;

    /**
     * Constructeur de MF
     * @param niv le niveau actuel
     */
    public MF (Plateau plat) {
        // Construit l'interface graphique
        build();
        plateau=plat;
    }

    /**
     * Construit l'interface graphique
     */
    private void build () {

        // Création de la fenêtre
        setTitle("Echecs"); 
        setLayout(new BorderLayout());

        int tailleCase=90;

        // Initialisation de la matrice de JLabels
        tab = new JButton [8][8];

        JPanel jp=new JPanel(); 
        //jp.setLayout(new GridLayout(1, 2));

        JPanel jpE=new JPanel(); 
        jpE.setLayout(new GridLayout(8, 8));
        jpE.setPreferredSize(new Dimension(8*tailleCase, 8*tailleCase));

        for (int j=0;j<8;j++)
            for (int i=0;i<8;i++) {
                tab[i][j]=new JButton();
                final int nb=i+8*j;
                tab[i][j].addActionListener(e -> {
                    plateau.action(nb);
                    requestFocusInWindow();
                });
                jpE.add(tab[i][j]);
            }
        
        jp.add(jpE, BorderLayout.CENTER);

        JPanel jpC=new JPanel(); 
        jpC.setLayout(new GridLayout(4, 1));
        jpC.setPreferredSize(new Dimension(tailleCase, 4*tailleCase));

        changePion=new JButton [4];
        for (int i=0;i<4;i++) {
            changePion[i]=new JButton();
                final int nb=64+i;
                changePion[i].addActionListener(e -> {
                    plateau.action(nb);
                    requestFocusInWindow();
                });
                jpC.add(changePion[i]);
        }

        jp.add(jpC, BorderLayout.CENTER);
        this.add(jp, BorderLayout.CENTER);
        this.pack();
        
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
        
        String folder="data/img/";

        // Récupère la liste des textures
        File fold=new File (folder);
        Vector <String> textures=new Vector <String> ();
        for (File file : fold.listFiles()) {
            if (!file.isDirectory())
                textures.add(file.getName());
        }
        images = new HashMap <String, ImageIcon>();
        // Charge les images et les stocke dans le HashMap
        for (int i=0;i<textures.size();i++) {
            images.put(textures.get(i),new ImageIcon(new ImageIcon( folder+textures.get(i)).getImage().getScaledInstance(tailleCase, tailleCase, java.awt.Image.SCALE_FAST))); // Load the images and store them in the HashMap
        }
    }

    /**
     * Met à jour l'interface graphique
     * @param o l'objet observable
     * @param arg l'objet passé en argument
     */
    @Override
    public void update(Observable o, Object arg) {

        // couleur du plateau
        for (int i=0;i<8;i++)
            for (int j=0;j<8;j++) {
                tab[i][j].setIcon(null);
                if ((i+j)%2==1)
                    tab[i][j].setBackground(Color.DARK_GRAY);
                else
                    tab[i][j].setBackground(Color.WHITE);
            }
        if (plateau.getOrdre()==1) {
            int caseSelec=plateau.getCaseSelec();
            int x=caseSelec%8;
            int y=caseSelec/8;
            tab[x][y].setBackground(Color.YELLOW);
            Vector <Integer> dep=plateau.getDepPossiblesPionSelec();
            for (int i=0;i<dep.size();i++) {
                x=dep.get(i)%8;
                y=dep.get(i)/8;
                tab[x][y].setBackground(Color.RED);
            }
        }
        else {

            // évaluation
            int eval=plateau.evaluation();
            if (Math.abs(eval)<=30)
                System.out.println("Evaluation : "+eval+" (égalité)");
            else
                if (eval>30)
                    System.out.println("Evaluation : "+eval+" (avantage Blanc)");
                else
                    System.out.println("Evaluation : "+eval+" (avantage Noir)");
            
            int caseDep=plateau.getCaseDep();
            if (caseDep>=0) {
                int x=caseDep%8;
                int y=caseDep/8;
                tab[x][y].setBackground(Color.YELLOW);
                int caseSelec=plateau.getCaseSelec();
                x=caseSelec%8;
                y=caseSelec/8;
                tab[x][y].setBackground(Color.YELLOW);
            }
        }

        // place les pions
        Vector <Pion> pions=plateau.getPions();
        Pion pion;
        for (int i=0;i<pions.size();i++) {
            pion=pions.get(i);
            int x=pion.position%8;
            int y=pion.position/8;
            if (pion.couleur)
                tab[x][y].setIcon(images.get("b"+pion.type+".png"));
            else
                tab[x][y].setIcon(images.get("n"+pion.type+".png"));
        }

        if (plateau.getOrdre()==3) {
            for (int i=0;i<4;i++) {
                changePion[i].setVisible(true);
            }
            if (plateau.getTour()) {
                changePion[0].setIcon(images.get("bc.png"));
                changePion[1].setIcon(images.get("bf.png"));
                changePion[2].setIcon(images.get("bt.png"));
                changePion[3].setIcon(images.get("bq.png"));
            }
            else {
                changePion[0].setIcon(images.get("nc.png"));
                changePion[1].setIcon(images.get("nf.png"));
                changePion[2].setIcon(images.get("nt.png"));
                changePion[3].setIcon(images.get("nq.png"));
            }
        }
        else {
            for (int i=0;i<4;i++)
                changePion[i].setVisible(false);
        }

        if (plateau.getVictoire()!=0) {
            if (plateau.getVictoire()==1)
                System.out.println("Victoire des blancs");
            else
            System.out.println("Victoire des noirs");
        }
    }
}
