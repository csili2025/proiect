
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.applet.AudioClip;
import java.applet.Applet;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Image;
import java.util.Arrays;
import java.util.Objects;

public class GamePanel extends JPanel implements Runnable {
    public Image backImage;
    public ImageIcon backIcon;

    public enum Status {START, PLAYING, PAUSED, COUNTDOWN, GAMEOVER}

    ;
    public Status status;

    public GameStats gameStats;

    public AudioClip tetris;
    public AudioClip moveSound;
    public AudioClip rotateSound;
    public AudioClip hardDropSound;
    public AudioClip countDownSound;
    public AudioClip countOverSound;

    private int keret;

    public boolean musicOn;

    private int kesik;
    private boolean kesleltetes;

    private int countDown;

    private MatrixPanel matrix;
    private StatsPanel stats;
    private QueuePanel queue;

    private boolean[] keys;

    private boolean hasHeld;

    private FormaSor minos;
    private Forma currentForma;
    static String szint;

    private int maradeksor;
    private Arnyek arnyek;
    private int lines, singles, doubles, minoCount;

    private int kiutve;

    public GamePanel() {
        setPreferredSize(new Dimension(Tetris.sz, Tetris.h));
        setSize(getPreferredSize());

        setLayout(new FlowLayout());
        addKeyListener(new KeyHandler());
        addComponentListener(new ComponentHandler());// //figyeljük a komponens méretének és helyzetének változásait
        setBackground(null);
        setFocusable(true); // képes fogadni a billentyűzet eseményeket
        setOpaque(false);// Átlátszó háttér

        backImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("hatter.jpg"))).getImage();
        Image back = backImage.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
        backIcon = new ImageIcon(back);

        //hangok

        tetris = Applet.newAudioClip(getClass().getResource("img/tetris.aiff"));
        rotateSound = Applet.newAudioClip(getClass().getResource("img/forog.wav"));
        moveSound = rotateSound;
        hardDropSound = Applet.newAudioClip(getClass().getResource("img/drop.wav"));
        countDownSound = Applet.newAudioClip(getClass().getResource("img/down.wav"));
        countOverSound = Applet.newAudioClip(getClass().getResource("img/down.wav"));

        resetGame();
    }

    public GamePanel(String s) {
        szint = s;
    }


    public void resetGame() {
        musicOn = Tetris.navigation.musicIsOn;

        lines = singles = doubles = minoCount = 0;

        kiutve = 0;
        countDown = 3;
        kesik = 0;
        kesleltetes = false;

        status = Status.START;

        keret = 0;
        hasHeld = false;

        keys = new boolean[7];

        Arrays.fill(keys, false);

        minos = new FormaSor();

        if (Objects.equals(szint, "Könnyű")) {
            maradeksor = 1;
        } else {
            maradeksor = 23;
        }


        stats = new StatsPanel();//bal oldal
        queue = new QueuePanel();//jobbb oldal
        matrix = new MatrixPanel();//kozepso resz

        JPanel gameAndQueue = new JPanel();
        gameAndQueue.setLayout(new BorderLayout(0, 0));
        gameAndQueue.setOpaque(false);

        JPanel game = new JPanel();
        game.setLayout(new BorderLayout(4, 4));
        game.setBackground(null);
        game.setOpaque(false);
        game.setBorder(BorderFactory.createLoweredBevelBorder());//keret
        game.add(matrix, BorderLayout.CENTER);

        JPanel pad = new JPanel();
        pad.setOpaque(false);
        pad.setBackground(null);
        pad.setPreferredSize(new Dimension(this.getWidth(), 50));

        add(pad);

        gameAndQueue.add(game, BorderLayout.CENTER);
        gameAndQueue.add(queue, BorderLayout.EAST);
        gameAndQueue.add(stats, BorderLayout.WEST);

        add(gameAndQueue);
    }


    @Override
    public void run() {
        while (status != Status.GAMEOVER) {
            if (status == Status.PLAYING) {
                keret++;

                if (keret % 2 == 0) {
                    stats.timer.update();

                    if (keret % 4 == 0) {
                        stats.repaint();
                    }
                }

                if (kesleltetes) {
                    kesik++;
                }

                if (kesik == 15) {
                    kesik = 0;
                    kesleltetes = false;
                    matrix.minoToMatrixElement(currentForma);
                    setNextMino();
                }

                if (keret % 30 == 0) {
                    currentForma.move(new Point(0, 1));

                    checkMoveAgainstMatrix(currentForma, new Point(0, 1));

                    getGhostCoors();

                    this.repaint();
                }
            } else if (status == Status.START) {

            } else if (status == Status.PAUSED) {

            } else if (status == Status.COUNTDOWN) {
                keret++;

                if (keret % 40 == 0) {
                    countDownSound.play();
                    countDown--;
                    repaint();

                    if (countDown == 1) {
                        if (musicOn) {
                            tetris.loop();
                        }
                    }
                }

                if (countDown == 0) {
                    countOverSound.play();

                    status = Status.PLAYING;

                    setNextMino();

                    arnyek = new Arnyek(currentForma);
                    getGhostCoors();

                    Tetris.navigation.pauseButton.setEnabled(true);
                }
            }

            try {
                Thread.sleep(25);
            } catch (InterruptedException exception) {
                break;
            }
        }
    }

    public void getGhostCoors() {//az aktuális forma árnyékát
        if (status != Status.PLAYING) {
            return;
        }

        arnyek = new Arnyek(currentForma);

        while (!checkGhostAgainstMatrix(arnyek)) {
            arnyek.move(new Point(0, 1));
        }
    }


    public boolean checkGhostAgainstMatrix(Forma forma) {// az árnyék (ghost) forma elérte-e a mátrix alját vagy ütközött-e más elemekkel.
        for (int i = 0; i < 4; i++) {
            if (forma.alak[i].y == 19) {
                return true;
            }


            for (int j = 0; j < matrix.elem.length; j++) {
                for (int k = 0; k < matrix.elem[j].length; k++) {
                    if (matrix.elem[j][k] != null && forma.alak[i].x == j
                            && forma.alak[i].y == k - 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public boolean checkMinoAgainstMatrix(Forma forma) {//az aktuális forma ütközik-e a mátrix elemeivel vagy túlment-e az alsó határon.
        for (int i = 0; i < 4; i++) {
            if (forma.alak[i].y >= 19) {
                return true;
            }

            for (int j = 0; j < matrix.elem.length; j++) {
                for (int k = 0; k < matrix.elem[j].length; k++) {
                    if (matrix.elem[j][k] != null && forma.alak[i].x == j
                            && forma.alak[i].y == k - 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public boolean MinoOutHatar(Forma forma) {
        int count = 0;
        for (int i = 0; i < 4; i++) {
            if (forma.alak[i].x <= 9 && forma.alak[i].x >= 0) {
                count++;

                if (count == 4) {
                    return false;
                }
            }
        }
        return true;
    }


    public void checkMinoAgainstBorders(Forma forma) {//Ha a forma kilóg az x-határokon, visszahelyezi a megfelelő tartományba.
        if (!MinoOutHatar(forma)) {
            return;
        }

        int x = 1;
        int y = 0;

        for (int i = 0; i < 4; i++) {
            if (forma.alak[i].x > 9) {
                x = -1;
            }
        }

        Point shift = new Point(x, y);

        while (MinoOutHatar(forma)) {
            forma.move(shift);
        }
    }


    public void checkMinoAgainstMatrixAndCorrect(Forma forma) {//Ha a forma ütközik a mátrixszal, addig mozgatja felfelé, amíg már nem ütközik.
        if (!checkMinoAgainstMatrix(forma)) {
            return;
        }

        Point shift = new Point(0, -1);

        while (checkMinoAgainstMatrix(forma)) {
            forma.move(shift);
        }
    }


    public void checkMoveAgainstMatrix(Forma forma, Point shift) {//a forma elmozdítása érvényes-e; ha nem, visszavonja az elmozdítást.
        for (int i = 0; i < 4; i++) {
            if (forma.alak[i].y > 19) {
                currentForma.move(new Point(shift.x * -1, shift.y * -1));
                if (shift.x == 0) {
                    kesleltetes = true;
                }
                return;
            }

            if (forma.alak[i].x > 9 || forma.alak[i].x < 0) {
                currentForma.move(new Point(shift.x * -1, shift.y * -1));
            }

            for (int j = 0; j < matrix.elem.length; j++) {
                for (int k = 0; k < matrix.elem[j].length; k++) {
                    if (matrix.elem[j][k] != null && forma.alak[i].x == j
                            && forma.alak[i].y == k) {
                        currentForma.move(new Point(shift.x * -1, shift.y * -1));


                        if (shift.x == 0) {
                            kesleltetes = true;
                        }
                        return;
                    }
                }
            }
        }
    }


    public void setNextMino() {//Előkészíti a következő formát a játékhoz
        minoCount++;
        kesik = 0;
        kesleltetes = false;

        matrix.checkLineClears();

        boolean gameOver = maradeksor <= 0;

        if (gameOver) {
            gameOver(true);
        }

        if (status == Status.PLAYING) {
            currentForma = new Forma(minos.newMino(), true);
            minos.shiftUp();
            queue.adjust();
            hasHeld = false;
        }
    }


    public void gameOver(boolean wins) {
        for (int j = 0; j < keys.length; j++) {
            keys[j] = false;
        }

        status = Status.GAMEOVER;

        tetris.stop();

        gameStats = new GameStats(wins, stats.timer.minutes,
                stats.timer.seconds,
                stats.timer.hundredths,
                lines, singles, doubles, minoCount);

        Tetris.navigation.pauseButton.setEnabled(false);

        JPanel pad = new JPanel();
        pad.setOpaque(false);
        pad.setBackground(null);
        pad.setPreferredSize(new Dimension(this.getWidth(), 50));

        this.removeAll();
        this.revalidate();
        this.add(pad);
        this.add(gameStats);
        this.repaint();
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backIcon.getImage(), 0, 0, null);
    }


    public void keyUpdate() {
        if (keys[0]) {
            forgataskeys();
        }

        if (keys[1]) {
            mozgaskeys(new Point(1, 0));
        }

        if (keys[2]) {
            mozgaskeys(new Point(0, 1));
        }

        if (keys[3]) {
            mozgaskeys(new Point(-1, 0));
        }

        if (keys[4]) {
            dropkeys();
        }

        if (keys[5] && !hasHeld) {
            holdkeys();
        }

        if (keys[6]) {
            pausekeys();
        }
    }

    private void forgataskeys() {
        kesik = 0;
        kesleltetes = false;
        currentForma.rotate(1);

        while (checkMinoAgainstMatrix(currentForma) || MinoOutHatar(currentForma)) {
            checkMinoAgainstMatrixAndCorrect(currentForma);
            checkMinoAgainstBorders(currentForma);
        }

        keys[0] = false;
        rotateSound.play();
    }

    private void mozgaskeys(Point direction) {
        currentForma.move(direction);
        checkMoveAgainstMatrix(currentForma, direction);

        if (direction.x != 0) {
            kesleltetes = false;
            kesik = 0;
        }

        if (moveSound != null) {
            moveSound.play();
        }

        if (direction.x != 0) {
            keys[direction.x > 0 ? 1 : 3] = false;
        }
    }

    private void dropkeys() {
        currentForma.alak = arnyek.alak;
        matrix.minoToMatrixElement(currentForma);
        setNextMino();

        keys[4] = false;
        hardDropSound.play();
    }

    private void holdkeys() {
        if (minos.h != null) {
            Forma tempC = new Forma(minos.h, true);
            minos.h = new Forma(currentForma, true);
            currentForma = new Forma(tempC, true);
        } else {
            minos.h = new Forma(currentForma, true);
            currentForma = new Forma(minos.newMino(), true);

            minos.shiftUp();
            queue.adjust();
        }

        stats.hold.forma = minos.h;
        hasHeld = true;
    }

    private void pausekeys() {
        if (status == Status.PAUSED) {
            Tetris.navigation.playButton.doClick();
        } else if (status == Status.PLAYING) {
            Tetris.navigation.pauseButton.doClick();
        }

        Tetris.navigation.repaint();
        repaint();
    }


    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent event) {
            if (status == Status.PLAYING) {
                if (event.getKeyCode() == KeyEvent.VK_UP) {
                    keys[0] = true;
                }
                if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
                    keys[1] = true;
                }
                if (event.getKeyCode() == KeyEvent.VK_DOWN) {
                    keys[2] = true;
                }
                if (event.getKeyCode() == KeyEvent.VK_LEFT) {
                    keys[3] = true;
                }
                if (event.getKeyCode() == KeyEvent.VK_SPACE) {
                    keys[4] = true;
                }
                if (event.getKeyCode() == KeyEvent.VK_SHIFT) {
                    keys[5] = true;
                }
            }
            if (event.getKeyCode() == KeyEvent.VK_P) {
                keys[6] = true;
            }
            keyUpdate();
            getGhostCoors();
            repaint();
        }


        @Override
        public void keyReleased(KeyEvent event) {
            if (status == Status.PLAYING) {
                if (event.getKeyCode() == KeyEvent.VK_UP) {//forgatja a format
                    keys[0] = false;
                }
                if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
                    keys[1] = false;
                }
                if (event.getKeyCode() == KeyEvent.VK_DOWN) {
                    keys[2] = false;
                }
                if (event.getKeyCode() == KeyEvent.VK_LEFT) {//forgatja a format
                    keys[3] = false;
                }
                if (event.getKeyCode() == KeyEvent.VK_SPACE) {//leteszi a forma
                    keys[4] = false;
                }
                if (event.getKeyCode() == KeyEvent.VK_SHIFT) { //egy format felre tesz
                    keys[5] = false;
                }

            }
            if (event.getKeyCode() == KeyEvent.VK_P) {//megallitja
                keys[6] = false;
            }
        }
    }


    private class ComponentHandler extends ComponentAdapter {

        @Override
        public void componentResized(ComponentEvent e) {
            Image lake = backImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
            backIcon = new ImageIcon(lake);
        }

    }


    class MatrixPanel extends JPanel {
        Color[][] elem;

        public MatrixPanel() {
            setSize(270, 540);
            setOpaque(false);

            elem = new Color[10][20];

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 20; j++) {
                    elem[i][j] = null;
                }
            }
        }

        public void minoToMatrixElement(Forma forma) {
            for (int i = 0; i < 4; i++) {
                if (forma.alak[i].y < 0) {
                    gameOver(false);
                    return;
                }
                elem[forma.alak[i].x][forma.alak[i].y] = forma.color;
            }
        }

        public void checkLineClears() {
            for (int i = 0; i < 20; i++) {
                int count = 0;
                for (int j = 0; j < 10; j++) {
                    if (elem[j][i] != null) {
                        count++;
                    }
                    if (count == elem.length) {
                        System.out.println("Line clear at:" + i);
                        removeRow(i);
                        maradeksor--;
                        lines++;
                        kiutve++;
                        stats.updateLines();
                    }
                }
            }
            if (kiutve == 1) {
                singles++;
            } else if (kiutve == 2) {
                doubles++;
            }
            kiutve = 0;
        }

        public void removeRow(int row) {
            for (int i = 0; i < 10; i++) {
                elem[i][row] = null;
            }

            Color[][] temp = new Color[10][20];

            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 10; j++) {
                    if (elem[j][i] != null) {
                        temp[j][i] = elem[j][i];
                    }
                }
            }

            for (int i = row; i >= 0; i--) {
                for (int j = 0; j < 10; j++) {
                    elem[j][i] = null;

                    if (i != 0) {
                        if (temp[j][i - 1] != null) {
                            elem[j][i] = temp[j][i - 1];
                        }
                    }
                }
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawBackground(g);

            if (status == Status.PLAYING || status == Status.PAUSED) {

                for (int i = 0; i < elem.length; i++) {
                    for (int j = 0; j < elem[i].length; j++) {
                        if (elem[i][j] != null)
                            drawElement(g, elem[i][j], i, j, Tetris.m);
                    }
                }
                arnyek.rajzlefele(g);
                currentForma.rajzlefele(g);
            } else if (status == Status.COUNTDOWN) {
                g.setColor(Color.white);
                g.setFont(new Font("Helvetica", Font.PLAIN, 36));
                g.drawString("" + countDown, getWidth() / 2 - 9, getHeight() / 2 - 36);
            }
        }

        public void drawBackground(Graphics g) {//saktabla
            for (int i = 0; i < elem.length; i++) {
                for (int j = 0; j < elem[i].length; j++) {
                    if ((i + j) % 2 == 0) {
                        g.setColor(new Color(40, 40, 40, 128));
                    } else {
                        g.setColor(new Color(50, 50, 50, 128));
                    }

                    g.fillRoundRect(i * Tetris.m, j * Tetris.m, Tetris.m + 1, Tetris.m + 1, 4, 4);
                }
            }
        }

        public void drawElement(Graphics g, Color color, int x, int y, int dim) {
            g.setColor(color);
            g.fillRect(x * dim + 1, y * dim + 1, dim - 1, dim - 1);
            g.setColor(Color.white);
            g.drawRect(x * dim + 1, y * dim + 1, dim - 2, dim - 2);
            g.setColor(Color.black);
            g.drawRect(x * dim + 1, y * dim + 1, dim - 1, dim - 1);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(220, 440);
        }
    }


    public class QueuePanel extends JPanel {
        MinoPanel[] formanext;

        public QueuePanel() {

            setLayout(new BorderLayout());
            setOpaque(false);

            JPanel queue = new JPanel();
            queue.setLayout(new FlowLayout());
            queue.setOpaque(false);


            JPanel queueL = new JPanel();
            queueL.setPreferredSize(new Dimension(88, 28));
            queueL.setBackground(Color.gray);
            queueL.setBorder(BorderFactory.createLineBorder(Color.darkGray));

            JLabel queueLabel = new JLabel("Next");
            queueLabel.setForeground(Color.WHITE);
            queueL.add(queueLabel);

            queue.add(queueL);
            formanext = new MinoPanel[2];//next nek

            for (int i = 0; i < 2; i++) {//ha nexnek tobbet szeretnek ezt feljebb lehet tenni
                formanext[i] = new MinoPanel(minos.formas.get(i));

                if (i == 0) {
                    formanext[i].setPreferredSize(new Dimension(88, 88));
                    formanext[i].setBorder(BorderFactory.createLoweredBevelBorder());
                } else if (i == 1) {
                    formanext[i].setPreferredSize(new Dimension(79, 69));
                }

                queue.add(formanext[i]);
            }

            if (szint.equals("Könnyű")) {
                add(queue, BorderLayout.CENTER);
            }
        }

        public void adjust() {
            for (int i = 0; i < 1; i++) {
                formanext[i].forma = new Forma(minos.formas.get(i), true);
            }
        }

        @Override
        public void paintComponent(Graphics g) {

            super.paintComponent(g);
        }

        @Override
        public Dimension getPreferredSize() {

            return new Dimension(110, 440);
        }
    }


    static class MinoPanel extends JPanel {
        Forma forma;

        public MinoPanel(Forma forma) {
            this.forma = forma;

            setBackground(null);
            setOpaque(false);
            setBorder(BorderFactory.createLineBorder(Color.darkGray));
            setPreferredSize(new Dimension(70, 60));
        }

        public MinoPanel() {
            setBackground(null);
            setPreferredSize(new Dimension(70, 60));
        }


        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(new Color(128, 128, 128, 128));
            g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 10, 10);

            if (forma != null) {
                forma.rajzsorba(g, this.getWidth(), this.getHeight());
            }
        }
    }

    public class StatsPanel extends JPanel {
        MinoPanel hold;
        JLabel holdLabel;
        Ido timer;
        JLabel timerLabel;
        JLabel maradeksorLabel;
        JLabel szintek;

        public StatsPanel() {
            setOpaque(false);
            setLayout(new FlowLayout());

            timer = new Ido();

            JPanel time = createElementPanel(new Dimension(100, 28));
            timerLabel = new JLabel("Time");
            timerLabel.setForeground(Color.WHITE);
            time.add(timerLabel);

            JPanel holdL = createElementPanel(new Dimension(88, 28));
            holdLabel = new JLabel("Hold");
            holdLabel.setForeground(Color.WHITE);
            holdL.add(holdLabel);

            JPanel linesL = createElementPanel(new Dimension(88, 28));
            JLabel lines = new JLabel("Lines");
            lines.setForeground(Color.WHITE);
            linesL.add(lines);

            JPanel szL = createElementPanel(new Dimension(100, 28));
            JLabel sz = new JLabel("Szint");
            sz.setForeground(Color.WHITE);
            szL.add(sz);

            JPanel linePanel = createElementPanel(new Dimension(88, 40));
            maradeksorLabel = new JLabel("" + maradeksor);
            maradeksorLabel.setFont(new Font("Helvetica", Font.PLAIN, 24));
            maradeksorLabel.setForeground(Color.WHITE);
            linePanel.add(maradeksorLabel);

            JPanel szPanel = createElementPanel(new Dimension(100, 40));
            szintek = new JLabel("" + szint);
            szintek.setFont(new Font("Helvetica", Font.PLAIN, 24));
            szintek.setForeground(Color.WHITE);
            szPanel.add(szintek);

            hold = new MinoPanel();
            hold.setPreferredSize(new Dimension(88, 88));
            hold.setBorder(BorderFactory.createLineBorder(Color.darkGray));
            hold.setOpaque(false);

            add(holdL);
            add(hold);

            add(createPaddingPanel(new Dimension(88, 30)));
            add(time);
            add(timer);
            add(createPaddingPanel(new Dimension(88, 10)));
            add(linesL);
            add(linePanel);
            add(createPaddingPanel(new Dimension(88, 20)));
            add(szL);
            add(szPanel);
        }

        private JPanel createElementPanel(Dimension size) {
            JPanel panel = new JPanel();
            panel.setPreferredSize(size);
            panel.setBackground(Color.gray);
            panel.setBorder(BorderFactory.createLineBorder(Color.darkGray));
            return panel;
        }

        private JPanel createPaddingPanel(Dimension size) {
            JPanel panel = new JPanel();
            panel.setPreferredSize(size);
            panel.setOpaque(false);
            return panel;
        }

        public void updateLines() {

            maradeksorLabel.setText("" + maradeksor);
        }

        @Override
        public void paintComponent(Graphics g) {

            super.paintComponent(g);
        }

        @Override
        public Dimension getPreferredSize() {

            return new Dimension(110, 440);
        }
    }
}