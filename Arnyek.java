import java.awt.*;

public class Arnyek extends Forma {
    public Arnyek(Forma forma) {
        super(forma, false);
        color = new Color(forma.color.getRed(), forma.color.getGreen(), forma.color.getBlue(), 100);
    }

    @Override
    public void rajzlefele(Graphics g)//a formak arnyeka, hogy tudjuk hova fog esni
    {
        for (int i = 0; i < 4; i++) {
            g.setColor(color);
            g.fillRect(Tetris.m * alak[i].x + 1, Tetris.m * alak[i].y + 1, Tetris.m - 2, Tetris.m - 2);
            g.setColor(Color.black);
            g.drawRect(Tetris.m * alak[i].x + 1, Tetris.m * alak[i].y + 1, Tetris.m - 3, Tetris.m - 3);
        }
    }
}
