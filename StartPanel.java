import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StartPanel {
    private JFrame frame;
    public JTextField nameField;
    private JRadioButton easyButton;
    private JRadioButton hardButton;
    private BufferedImage img;
    public static Tetris tetris;
    public GameStats name;
    public GamePanel dificullty;

    public void createShowPanel() {
        frame = new JFrame("TETRISZ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 1100);
        frame.setLayout(new BorderLayout()); // Az egész ablak elrendezéséhez BorderLayout-ot használunk

        // Betöltjük a háttérképet
        try {
            img = ImageIO.read(new File("immg/hatter.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // A háttérpanel létrehozása
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (img != null) {
                    // A háttérkép kirajzolása
                    g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
                }
            }
        };
        backgroundPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight()); // Az egész ablakot kitölti
        backgroundPanel.setOpaque(false);  // Átlátszó háttér
        frame.add(backgroundPanel, BorderLayout.CENTER);  // A háttérpanel hozzáadása az ablak közepére

        // Cím hozzáadása közvetlenül a háttérpanelre
        JLabel titleLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<span style='color: red;'>T</span>" +
                "<span style='color: orange;'>E</span>" +
                "<span style='color: yellow;'>T</span>" +
                "<span style='color: green;'>R</span>" +
                "<span style='color: blue;'>I</span>" +
                "<span style='color: purple;'>S</span>" +
                "</div></html>");

        // Árnyék hozzáadása a szöveghez
        titleLabel.setForeground(Color.WHITE); // A szöveg színe fehér
        titleLabel.setFont(new Font("Snap ITC", Font.BOLD, 100));

        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Árnyék hozzáadása
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.GRAY); // Árnyék színe
                g2d.setFont(titleLabel.getFont());
                g2d.drawString("TETRISZ", 270 + 5, 150 + 5); // Árnyék pozíció
                g2d.setColor(Color.WHITE); // Szöveg színe
                g2d.drawString("TETRISZ", 270, 150); // Eredeti szöveg
            }
        };
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 270, 50));
        titlePanel.add(titleLabel);
        titlePanel.setBounds(0, 30, frame.getWidth(), 150); // A cím elhelyezése a háttér felett
        titlePanel.setOpaque(false);  // Átlátszó háttér
        backgroundPanel.add(titlePanel);  // A cím panelt a háttérpanelhez adunk hozzá

        // A beviteli mezők panelje
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS)); // függőlegesen elrendezés,egy oszlopban lesz elhelyezve, egy-egy sorban, fentről lefelé.
        inputPanel.setOpaque(false); // Átlátszó háttér
        backgroundPanel.add(inputPanel, BorderLayout.CENTER);

        // Névmező
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        namePanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 150, 50));
        JLabel nameLabel = new JLabel(" Nev:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 28));

        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 28));
        nameField.setPreferredSize(new Dimension(300, 40));

        namePanel.add(nameLabel);
        namePanel.setOpaque(false);
        namePanel.add(nameField);
        inputPanel.add(namePanel);

        // Szintválasztó mező
        JPanel difficultyPanel = new JPanel();
        difficultyPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        difficultyPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 132, 0));
        JLabel difficultyLabel = new JLabel("Szint:");
        difficultyLabel.setForeground(Color.WHITE);
        difficultyLabel.setFont(new Font("Arial", Font.PLAIN, 24));

        easyButton = new JRadioButton("Könnyű");
        easyButton.setForeground(Color.WHITE);
        easyButton.setFont(new Font("Arial", Font.PLAIN, 24));

        hardButton = new JRadioButton("Nehéz");
        hardButton.setForeground(Color.WHITE);
        hardButton.setFont(new Font("Arial", Font.PLAIN, 24));

        hardButton.setOpaque(false);
        easyButton.setOpaque(false);

        ButtonGroup difficultyGroup = new ButtonGroup();
        difficultyGroup.add(easyButton);
        difficultyGroup.add(hardButton);

        difficultyPanel.add(difficultyLabel);
        difficultyPanel.add(easyButton);
        difficultyPanel.add(hardButton);

        inputPanel.add(difficultyPanel);

        // Gombok panelje
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 150, 180));
        JButton startButton = new JButton("Start");
        startButton.setForeground(Color.white);
        startButton.setBackground(Color.RED);

        JButton exitButton = new JButton("Kilépés");
        exitButton.setForeground(Color.white);
        exitButton.setBackground(Color.green);

        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.setPreferredSize(new Dimension(200, 60));
        exitButton.setFont(new Font("Arial", Font.BOLD, 24));
        exitButton.setPreferredSize(new Dimension(200, 60));

        buttonPanel.add(startButton);
        buttonPanel.add(exitButton);
        buttonPanel.setOpaque(false);

        inputPanel.add(buttonPanel);

        // Eseménykezelők hozzáadása
        startButton.addActionListener(e -> handleStartButton());
        exitButton.addActionListener(e -> System.exit(0));
        difficultyPanel.setOpaque(false);

        frame.setVisible(true);
    }

    private void handleStartButton() {
        name = new GameStats(nameField.getText());

        String naame = nameField.getText();
        List<JRadioButton> difficultyButtons = Arrays.asList(easyButton, hardButton);

        // Stream az ellenőrzéshez
        String difficulty = difficultyButtons.stream()
                .filter(AbstractButton::isSelected)//Csak azokat a gombokat tartja meg, amelyek ki vannak választva
                .map(AbstractButton::getText)// objektumokat átalakítjuk a szöveges tartalmukra
                .findFirst()
                .orElse("");
        dificullty = new GamePanel(difficulty);

        // Stream a hiányzó mezők listázására
        List<String> missingFields = Stream.of(
                        naame.isEmpty() ? "Név" : null,
                        difficulty.isEmpty() ? "Szint" : null
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList()); // A toList() helyett ezt használjuk

        if (!missingFields.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Kérlek, töltsd ki az alábbi mezőket: " + String.join(", ", missingFields),
                    "Figyelmeztetés", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Üdv, " + naame + "! A kiválasztott szint: " + difficulty + ".",
                    "Információ", JOptionPane.INFORMATION_MESSAGE);
            tetris = new Tetris();
        }
    }

}
