import java.util.ArrayList;
import java.util.Random;

public class FormaSor {
    Forma h;
    ArrayList<Forma> formas;
    Random rand;

    public FormaSor() {
        rand = new Random();
        h = null;
        formas = new ArrayList<Forma>();

        for (int i = 0; i < 2; i++) {//next nek itt adhatnek meg tobbet
            formas.add(new Forma(rand.nextInt(7) + 1));
        }
    }

    public void shiftUp() {
        formas.add(new Forma(rand.nextInt(7) + 1));//hozzáad egy új véletlenszerű formát
        formas.remove(0);
    }

    public Forma newMino() {

        return formas.get(0);// az aktuális következő formát ami erkezik fentrol
    }
}
