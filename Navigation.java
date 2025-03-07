import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Navigation extends JMenuBar {
    public boolean musicIsOn;
    public boolean IsOn;

    JMenuBar playPause;
    JMenuBar tobbi;

    JButton pauseButton;
    JButton playButton;
    JButton score;

    BufferedImage soundOn;
    BufferedImage soundOff;

    JLabel soundIcon;
    JButton soundValtas;

    JLabel playLabel;
    JLabel pauseLabel;
    JLabel scoreLabel;

    public Navigation() {
        musicIsOn = false;
        IsOn = false;

        ButtonHandler handler = new ButtonHandler();

        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        playPause = new JMenuBar();
        playPause.setLayout(new FlowLayout());
        playPause.setBackground(Color.black);

        tobbi = new JMenuBar();
        tobbi.setLayout(new FlowLayout());
        tobbi.setBackground(Color.black);

        scoreLabel = new JLabel("High Score");
        ImageIcon scoreIcon = loadImageIcon("img/high-scores.png", 24, 24);
        if (scoreIcon != null) {
            scoreLabel.setIcon(scoreIcon);
        }
        score = new JButton();
        score.add(scoreLabel);
        score.addActionListener(handler);

        tobbi.add(score, BorderLayout.CENTER);

        playLabel = new JLabel("Play");
        ImageIcon playIcon = loadImageIcon("img/play.png", 24, 24);
        if (playIcon != null) {
            playLabel.setIcon(playIcon);
        }

        playButton = new JButton();
        playButton.add(playLabel);
        playButton.addActionListener(handler);

        pauseLabel = new JLabel("Pause");
        ImageIcon pauseIcon = loadImageIcon("/img/pause.png", 24, 24);
        if (pauseIcon != null) {
            pauseLabel.setIcon(pauseIcon);
        }

        pauseButton = new JButton();
        pauseButton.add(pauseLabel);

        pauseButton.addActionListener(handler);

        playPause.add(playButton);
        playPause.add(pauseButton);

        soundIcon = new JLabel();

        soundOn = loadBufferedImage("/img/sound-on.png");
        assert soundOn != null;
        soundIcon.setIcon(new ImageIcon(soundOn.getScaledInstance(24, 24, Image.SCALE_SMOOTH)));// képet egy új méretre skálázza.

        soundOff = loadBufferedImage("/img/sound-off.png");
        assert soundOff != null;
        soundIcon.setIcon(new ImageIcon(soundOff.getScaledInstance(24, 24, Image.SCALE_SMOOTH)));


        JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout(new FlowLayout());
        menuBar.setBackground(null);

        soundValtas = new JButton();
        soundValtas.add(soundIcon);
        soundValtas.addActionListener(handler);

        menuBar.add(soundValtas);

        add(playPause, BorderLayout.WEST);
        add(menuBar, BorderLayout.EAST);

        pauseButton.setEnabled(false);
        setButtonsOff();
    }

    private BufferedImage loadBufferedImage(String path) {
        try {
            URL resource = getClass().getResource(path);//Ha az erőforrás megtalálható, a resource változó egy URL-t fog tartalmazni, ami a kép helyére mutat.
            if (resource != null) {
                return ImageIO.read(resource);
            } else {
                System.err.println("Image not found: " + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ImageIcon loadImageIcon(String path, int width, int height) {
        BufferedImage image = loadBufferedImage(path);
        if (image != null) {
            return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        }
        return null;
    }

    private class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == playButton) {
                if (Tetris.app.status == GamePanel.Status.START) {
                    Tetris.app.status = GamePanel.Status.COUNTDOWN;
                    pauseButton.setEnabled(false);
                } else if (Tetris.app.status == GamePanel.Status.PAUSED) {
                    Tetris.app.status = GamePanel.Status.PLAYING;
                    pauseButton.setEnabled(true);

                    if (Tetris.app.musicOn) {
                        Tetris.app.tetris.loop();
                    }
                }

                playButton.setEnabled(false);
                Tetris.app.repaint();
            }

            if (e.getSource() == pauseButton) {
                Tetris.app.status = GamePanel.Status.PAUSED;
                pauseButton.setEnabled(false);
                playButton.setEnabled(true);
            }

            if (e.getSource() == soundValtas) {//hangkep inditas es megallitas
                if (musicIsOn) {
                    soundIcon.setIcon(new ImageIcon(soundOff.getScaledInstance(24, 24, Image.SCALE_SMOOTH)));

                    Tetris.app.tetris.stop();
                    Tetris.app.musicOn = false;
                } else {
                    soundIcon.setIcon(new ImageIcon(soundOn.getScaledInstance(24, 24, Image.SCALE_SMOOTH)));

                    if (Tetris.app.status == GamePanel.Status.PLAYING) {
                        Tetris.app.tetris.play();
                    }
                    Tetris.app.musicOn = true;
                }
                musicIsOn = !musicIsOn;
                revalidate();//Frissíti az elrendezést:
            }

            repaint();
            setButtonsOff();
        }
    }

    public void setButtonsOff() {
        playButton.setFocusable(false);// képes fogadni a billentyűzet eseményeket
        pauseButton.setFocusable(false);
        score.setFocusable(false);
        soundValtas.setFocusable(false);
    }
}
