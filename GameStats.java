import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.List;

public class GameStats extends JPanel {
    JLabel title;
    JPanel stats;
    ScoreTablePanel[] eredmenyek;
    JPanel scoreSubmit;
    JButton cancelButton;
    JButton submitButton;
    JButton playAgainButton;
    JTextField nameField;
    String time;
    static String name;

    int lines;
    int singles;
    int doubles;
    int tetrominos;
    float linesPerMinute;
    float minosPerMinute;

    public GameStats(String N) {
        name = N;
    }

    public GameStats(boolean win, int min, int sec, int milli, int line, int single, int doub, int tetros) {
        String hundredth;
        String second;
        String minute;

        if (milli < 10) {
            hundredth = "0" + milli;
        } else hundredth = "" + milli;

        if (sec < 10) {
            second = "0" + sec;
        } else second = "" + sec;

        if (min < 10) {
            minute = "0" + min;
        } else minute = "" + min;

        time = minute + ":" + second + ":" + hundredth;

        lines = line;
        singles = single;
        doubles = doub;
        tetrominos = tetros;

        linesPerMinute = lines / (min + ((float) sec + ((float) milli / 1000)) / 60);
        minosPerMinute = tetrominos / (min + ((float) sec + ((float) milli / 1000)) / 60);

        setSize(450, 350);
        setPreferredSize(new Dimension(500, 350));
        setLayout(new FlowLayout());
        setOpaque(false);//lathatalan,atetszo legyen

        title = new JLabel(win ? "Time: " + time : "Game Over: " + time);
        title.setForeground(Color.white);
        title.setFont(new Font("Helvetica", Font.PLAIN, 50));

        stats = new JPanel();
        stats.setLayout(new FlowLayout());
        stats.setOpaque(false);
        stats.setPreferredSize(new Dimension(480, 140));

        eredmenyek = new ScoreTablePanel[6];

        Color gray = Color.white;
        Color lightGray = new Color(200, 200, 200, 148);

        eredmenyek[0] = new ScoreTablePanel(gray);
        eredmenyek[1] = new ScoreTablePanel(lightGray);
        eredmenyek[2] = new ScoreTablePanel(lightGray);
        eredmenyek[3] = new ScoreTablePanel(gray);
        eredmenyek[4] = new ScoreTablePanel(gray);
        eredmenyek[5] = new ScoreTablePanel(lightGray);

        eredmenyek[0].setLabel("Minos Per Minute: ");
        eredmenyek[0].setValue(String.format("%.2f", minosPerMinute));

        eredmenyek[1].setLabel("Minos Locked Down: ");
        eredmenyek[1].setValue("" + tetrominos);

        eredmenyek[2].setLabel("Lines Per Minute: ");
        eredmenyek[2].setValue(String.format("%.2f", linesPerMinute));

        eredmenyek[3].setLabel("Lines: ");
        eredmenyek[3].setValue("" + lines);

        eredmenyek[4].setLabel("Singles: ");
        eredmenyek[4].setValue("" + singles);

        eredmenyek[5].setLabel("Doubles: ");
        eredmenyek[5].setValue("" + doubles);


        for (ScoreTablePanel score : eredmenyek) {
            stats.add(score);
        }

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new FlowLayout());
        textPanel.setOpaque(false);

        scoreSubmit = new JPanel();
        scoreSubmit.setOpaque(false);

        System.out.println(name);
        nameField = new JTextField(name, 20);

        scoreSubmit = new JPanel();
        scoreSubmit.setOpaque(false);
        scoreSubmit.setLayout(new GridLayout(2, 1, 10, 10)); // Egymás alá helyezés, vertikális távolság

        ButtonHandler handler = new ButtonHandler();

        cancelButton = new JButton("Cancel");
        submitButton = new JButton("Submit Score");

        // Gombok megjelenésének módosítása

        cancelButton.setBackground(Color.RED);
        submitButton.setBackground(Color.BLUE);
        cancelButton.setForeground(Color.WHITE);
        submitButton.setForeground(Color.WHITE);

        cancelButton.setFont(new Font("Helvetica", Font.BOLD, 30));
        submitButton.setFont(new Font("Helvetica", Font.BOLD, 30));

        cancelButton.addActionListener(handler);
        submitButton.addActionListener(handler);

        if (win) {
            scoreSubmit.add(submitButton);
            scoreSubmit.add(cancelButton);
        } else {
            playAgainButton = new JButton("Play Again");
            playAgainButton.setBackground(Color.GREEN);
            playAgainButton.setForeground(Color.WHITE);
            playAgainButton.setFont(new Font("Helvetica", Font.BOLD, 30));
            playAgainButton.addActionListener(handler);

            scoreSubmit.add(playAgainButton);
        }


        add(title);
        add(stats);

        if (win) {
            add(textPanel);
        }

        add(scoreSubmit, BorderLayout.CENTER);

        setVisible(true);
        nameField.setFocusable(true);//a felhasználói interakciókat
    }


    static class ScoreTablePanel extends JPanel {
        Color color;

        public ScoreTablePanel(Color clr) {
            setLayout(new BorderLayout(5, 5));
            setPreferredSize(new Dimension(230, 30));
            color = clr;
        }

        public void setLabel(String label) {

            this.add(new JLabel("   " + label), BorderLayout.WEST);
        }

        public void setValue(String value) {

            this.add(new JLabel(value + "   "), BorderLayout.EAST);
        }

        @Override
        public void paintComponent(Graphics g) {//az eredmenyek kiirasanal hivjuk meg
            g.setColor(color);
            g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 4, 4);//rajzol egy lekerekített sarkú téglalapot, (4,4)-minel nagyobb a szam annal jobban kerekiti le
        }
    }

    private class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == submitButton) {
                JFileChooser fileChooser = new JFileChooser();
                int choice = fileChooser.showSaveDialog(GameStats.this);//lehetővé teszi a felhasználó számára, hogy kiválassza, hová szeretné menteni a fájlt

                if (choice == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {//true- fájl végére fogunk írni (append módban),
                        String name = nameField.getText().trim();
                        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        // A játékos adatainak mentése
                        writer.write("Name: " + name + ", Score: " + lines + ", Time: " + time + ", Date: " + dateTime);
                        writer.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Most olvasd be a fájlt és írd ki a top 10 játékost
                    displayTop10(file);
                }
            } else if (event.getSource() == cancelButton) {
                Tetris.app.removeAll();
                Tetris.stop();
                Tetris.restart();
            } else if (event.getSource() == playAgainButton) {
                // Játék újraindítása
                Tetris.app.removeAll();
                Tetris.stop();
                Tetris.restart();
            }
        }

        private void displayTop10(File file) {
            try {
                List<PlayerScore> playerScores = new ArrayList<>();
                try (Scanner scanner = new Scanner(file)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] parts = line.split(", ");
                        String playerName = parts[0].split(": ")[1];
                        int playerScore = Integer.parseInt(parts[1].split(": ")[1]);
                        String playerTime = parts[2].split(": ")[1];
                        playerScores.add(new PlayerScore(playerName, playerScore, playerTime));
                    }
                }

                // Rendezés pontszám szerint csökkenő, idő szerint növekvő sorrendben
                playerScores.sort(Comparator.comparingInt(PlayerScore::getScore).reversed().thenComparing(PlayerScore::getTime));

                // Top 10 játékos kiírása
                JPanel top10Panel = new JPanel();
                top10Panel.setLayout(new BoxLayout(top10Panel, BoxLayout.Y_AXIS)); // Vertikális elrendezés
                top10Panel.setOpaque(false);

                remove(title);
                remove(scoreSubmit);
                JLabel title = new JLabel("Top 10 Játékos");
                title.setFont(new Font("Helvetica", Font.BOLD, 60));
                title.setForeground(Color.WHITE);
                add(title);

                JLabel cimke = new JLabel("Helyezes" + "   " + "Nev" + "       " + "Score" + "      " + "Ido");
                cimke.setFont(new Font("Helvetica", Font.BOLD, 20));
                cimke.setForeground(Color.WHITE);
                top10Panel.add(cimke);

                for (int i = 0; i < Math.min(10, playerScores.size()); i++) {
                    PlayerScore ps = playerScores.get(i);
                    JLabel playerLabel = new JLabel((i + 1) + "      " + ps.getName() + "       " + ps.getScore() + "       " + ps.getTime());
                    playerLabel.setForeground(Color.WHITE);
                    top10Panel.add(playerLabel);
                }

                // ScrollPane hozzáadása
                JScrollPane scrollPane = new JScrollPane(top10Panel);
                scrollPane.setPreferredSize(new Dimension(500, 150)); // Beállítjuk a scrollPane méretét
                scrollPane.setOpaque(false);
                scrollPane.getViewport().setOpaque(false);

                // Hozzáadás a panel aljára
                remove(stats);
                add(scrollPane);
                add(scoreSubmit, BorderLayout.CENTER);
                revalidate();
                repaint();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        class PlayerScore {
            private final String name;
            private final int score;
            private final String time;

            public PlayerScore(String name, int score, String time) {
                this.name = name;
                this.score = score;
                this.time = time;
            }

            public String getName() {

                return name;
            }

            public int getScore() {

                return score;
            }

            public String getTime() {
                return time;
            }
        }

    }
}