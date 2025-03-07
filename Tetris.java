import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Tetris extends JFrame {

    public static int sz = 1100;
    public static int h = 1100;
    public static int m = 440 / 20;

    public static Navigation navigation;
    public static GamePanel app;

    public static ExecutorService executor;// automatikusan kezeli a szálak életciklusát, konyabb szalkezeles
    //szálak használata lehetővé teszi, hogy a játék futása és a felhasználói interakciók párhuzamosan történjenek anélkül, hogy a grafikus felület (UI) blokkolva lenne.

    public Tetris() {
        setLayout(new BorderLayout(0, 0));
        setSize(sz, h);

        navigation = new Navigation();
        app = new GamePanel();

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(100, 100));
        panel.setSize(100, 100);
        panel.setOpaque(false);

        add(app, BorderLayout.CENTER);
        add(navigation, BorderLayout.NORTH);

        setVisible(true);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        start();
    }

    public static void start() {
        executor = Executors.newCachedThreadPool();//Dinamikus szálkezelés, Inaktív szálak eltávolítása,
        executor.execute(app);
    }

    public static void stop() {

        executor.shutdown();//leállítja az új feladatok végrehajtását és bezárja a szálakat.
    }

    public static void restart() {
        navigation.playButton.setEnabled(true);

        app.resetGame();
        executor = Executors.newCachedThreadPool();// újra felhasználja a szálakat, amik már nem szükségesek, így elkerülhető a túlzott erőforrás-használat.
        executor.execute(app);// automatikusan kezeli a szálak létrehozását és újrafelhasználását.

        app.repaint();
        app.revalidate();//frissíti a komponensek elrendezését
    }


}
