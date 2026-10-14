import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Thesaurus {

    private class EntreeSortie implements Comparable<EntreeSortie> {
        private String entree; // un mot
        private String sortie; // sa forme canonique

        public EntreeSortie(String entree, String sortie) {
            this.entree = entree;
            this.sortie = sortie;
        }

        public int compareTo(EntreeSortie o) {
            return this.entree.compareTo(o.entree);
        }
    }

    private ArrayList<EntreeSortie> table;

    public Thesaurus(String nomFichier) {
        //{}=>{ constructeur créant et initialisant l'attribut table à partir du contenu du fichier dont le nom est passé en paramètre, puis triant la table
        // en utilisant la méthode compareTo d'EntreeSortie
        // remarque 1 : utilise ajouterEntreeSortie et trierEntreesSorties
        // remarque 2 : pour la lecture du fichier, inspirez-vous de lireMotsOutils de Utilitaire
        // remarque 3 : pour les traitements de la chaîne lue, utilisez les méthodes indexOf,substring de String
        table = new ArrayList<>();

        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                int indSubStr=ligne.indexOf(":");
                table.add(new EntreeSortie(ligne.substring(0,indSubStr),ligne.substring(indSubStr+1)));
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        trierEntreesSorties(table);

    }

    public void ajouterEntreeSortie(String entree, String sortie) {
        //{}=>{ajoute à la fin de la table une nouvelle EntreeSortie avec les attributs entree et sortie}
        table.add(new EntreeSortie(entree,sortie));
    }


    public String rechercherSortiePourEntree(String entree) {
        // {l'attribut table du thesaurus est trié sur l'attribut entree des Entree-Sortie}=>
        // {résultat = la forme canonique associée à entree dans le thésaurus si l'entrée entree existe,
        // entree elle-même si elle n'existe pas. La recherche doit être dichotomique.
        // remarque : utilise compareTo de EntreeSortie }
        int inf=0;
        int sup=table.size()-1;
        int m;
        while(inf<sup){
            m=(inf+sup)/2;
            if(table.get(m).compareTo(new EntreeSortie(entree,""))>=0){
                sup=m;
            } else {
                inf=m+1;
            }
        } if (table.get(sup).compareTo(new EntreeSortie(entree,""))==0){
            return table.get(sup).sortie;
        } else {
            return entree;
        }
    }

    static void trierEntreesSorties(ArrayList<EntreeSortie> v) {
        //{} => {trie v sur la base de la méthode compareTo de EntreeSortie}
        triFusionWorker(v, 0, v.size() - 1);
    }

    private static void triFusionWorker(ArrayList<EntreeSortie> v, int borneInf, int borneSup) {
        if (borneInf < borneSup) {
            int m = (borneInf + borneSup) / 2;

            triFusionWorker(v, borneInf, m);
            triFusionWorker(v, m + 1, borneSup);

            fusionner(v, borneInf, m, borneSup);
        }
    }

    private static void fusionner(ArrayList<EntreeSortie> v, int borneInf, int m, int borneSup) {
        ArrayList<EntreeSortie> temp = new ArrayList<>();
        int iG = borneInf;
        int iD = m + 1;

        while (iG <= m && iD <= borneSup) {
            if (v.get(iG).compareTo(v.get(iD)) <= 0) {
                temp.add(v.get(iG));
                iG = iG + 1;
            } else {
                temp.add(v.get(iD));
                iD = iD + 1;
            }
        }

        while (iG <= m) {
            temp.add(v.get(iG));
            iG = iG + 1;
        }

        while (iD <= borneSup) {
            temp.add(v.get(iD));
            iD = iD + 1;
        }

        int k = 0;
        while (k < temp.size()) {
            v.set(borneInf + k, temp.get(k));
            k = k + 1;
        }

    }

}