import java.util.ArrayList;

public class Index {

    // un index est un vecteur d'EntreeIndex
    private class EntreeIndex {

        // une EntreeIndex associe un vecteur trié d'entiers (sorties) à une String (entree)
        private String entree;
        private ArrayList<Integer> sorties;

        public ArrayList<Integer> getSorties() { return sorties; }

        //constructeur
        public EntreeIndex(String entree) {
            this.entree = entree;
            sorties = new ArrayList<>();
        }

        public int rechercherSortie(Integer sortie) {
            //{}=>{recherche dichotomique de sortie dans sorties (triée dans l'ordre croissant)
            // résultat = l'indice de sortie dans sorties si trouvé, - l'indice d'insertion si non trouvé }
            int inf = 0;
            int sup = sorties.size();
            int m;

            while (inf < sup) {
                m = (sup + inf) / 2;
                if (sorties.get(m) >= sortie) {
                    sup = m;
                } else {
                    inf = m + 1;
                }
            }
            if (inf < sorties.size() && sorties.get(inf).equals(sortie)) {
                return inf;
            } else {
                return -(inf + 1);
            }
        }

        public void ajouterSortie(Integer sortie) {
            //{}=>{insère sortie à la bonne place dans sorties (triée dans l'ordre croissant)
            // remarque : utilise rechercherSortie de EntreeIndex }
            if (sorties.size() == 0) {
                sorties.add(0, sortie);
            } else {
                int i = rechercherSortie(sortie);

                if (i < 0) {

                    int positionInsertion = -(i + 1);
                    sorties.add(positionInsertion, sortie);
                }
            }
        }

        @Override
        public String toString() {
            return entree + "=>" + sorties;
        }
    }

    //Un vecteur d'EntreeIndex trié sur l'attribut entree (String) des EntreeIndex
    private ArrayList<EntreeIndex> table;

    //constructeur
    public Index() {
        table = new ArrayList<>();
    }


    public int rechercherEntree(String entree) {
        //{}=>  {recherche dichotomique de entree dans table (triée dans l'ordre lexicographique des attributs entree des EntreeIndex) }
        //résultat =  l'indice de entree dans table si trouvé et -l'indice d'insertion sinon }
        int inf = 0;
        int sup = table.size();
        int m;

        while (inf < sup) {
            m = (sup + inf) / 2;
            int cmp = table.get(m).entree.compareTo(entree);

            if (cmp == 0) {
                return m;
            } else if (cmp > 0) {
                sup = m;
            } else {
                inf = m + 1;
            }
        }

        return -(inf + 1);
    }

    public void ajouterSortieAEntree(String entree, Integer sortie) {
        // {}=>{ajoute l'entier sortie dans les sorties associées à l'entrée entree
        // si l'entrée entree n'existe pas elle est créée.
        // ne fait rien si sortie était déjà présente dans ses sorties.
        // remarque : utilise la fonction rechercherEntree de Index et la procedure ajouterSortie de EntreeIndex}
        if (table.isEmpty()) {
            table.addFirst(new EntreeIndex(entree));
            table.getFirst().ajouterSortie(sortie);
        } else {
            int i = rechercherEntree(entree);

            if (i >= 0) {

                table.get(i).ajouterSortie(sortie);
            } else {

                int positionInsertion = -(i + 1);
                table.add(positionInsertion, new EntreeIndex(entree));
                table.get(positionInsertion).ajouterSortie(sortie);
            }
        }
    }

    public ArrayList<Integer> rechercherSorties(String entree) {
        // {}=>{résultat = les sorties associées à l'entrée entree
        // si l'entrée entree n'existe pas, une ArrayList vide est retournée.
        // remarque : utilise la fonction rechercherEntree de Index}
        int i = rechercherEntree(entree);
        if (i < 0) {
            return new ArrayList<Integer>();
        } else {
            return table.get(i).getSorties();
        }
    }

    public void afficher() {
        // {}=>{affiche la table de l'index}
        for (EntreeIndex entreeIndex : table) {
            System.out.println(entreeIndex);
        }
    }
}
