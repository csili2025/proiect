import javax.swing.*;
import java.awt.*;

public class Ido extends JPanel {
    int minutes;
    int seconds;
    int hundredths;

    String min;
    String sec;
    String hund;

    String time;

    public Ido() {
        setLayout(new FlowLayout());
        setSize(getPreferredSize());
        setBorder(BorderFactory.createLineBorder(Color.white));
        setOpaque(false);

        min = sec = hund = "00";
        minutes = seconds = hundredths = 0;
        time = min + ":" + sec + ":" + hund;
    }

    public void update() { //Frissíti az időzítő értéké
        hundredths += 5;

        if (hundredths >= 100) {
            hundredths = 0;
            seconds++;
        }
        if (seconds >= 60) {
            seconds = 0;
            minutes++;
        }

        if (hundredths < 10) {
            hund = "0" + hundredths;
        } else hund = "" + hundredths;

        if (seconds < 10) {
            sec = "0" + seconds;
        } else sec = "" + seconds;

        if (minutes < 10) {
            min = "0" + minutes;
        } else min = "" + minutes;

        time = min + ":" + sec + ":" + hund;
    }

    @Override
    public void paintComponent(Graphics g) { //Az időzítő megjelenítését kezeli
        super.paintComponent(g);

        g.setColor(new Color(128, 128, 128, 128));
        g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 10, 10);

        g.setColor(Color.black);
        g.setFont(new Font("Helvetica", Font.PLAIN, 22));
        g.drawString(time, 5, 35);

    }


    @Override
    public Dimension getPreferredSize() {

        return new Dimension(100, 50);
    }


    @Override
    public String toString() {

        return min + ":" + sec + ":" + hund;
    }
}

