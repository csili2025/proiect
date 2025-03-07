import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;

public class Forma//formak mozgatas, kirajzolasa, letrehozasa es forgatasa
{
    Color color;
    Point[] alak;

    int id;

    public Forma(Forma forma, boolean reset) {
        this.id = forma.id;
        this.color = forma.color;

        if (reset) {
            this.alak = getStartCoors();
        } else {
            this.alak = new Point[4];
            for (int i = 0; i < 4; i++) {
                alak[i] = new Point(forma.alak[i].x, forma.alak[i].y);
            }
        }
    }

    public Forma(int id) {
        this.id = id;
        alak = getStartCoors();
    }

    public Point[] getStartCoors()//forma letrehozas
    {
        alak = new Point[4];

        switch (id) {
            case 1://kocka forma
            {
                int red = (int) (Math.random() * 256);
                int green = (int) (Math.random() * 256);
                int blue = (int) (Math.random() * 256);
                color = new Color(red, green, blue);

                alak[0] = new Point(4, -1);
                alak[1] = new Point(5, -1);
                alak[2] = new Point(4, 0);
                alak[3] = new Point(5, 0);

                break;
            }
            case 2: //4 pont egy sorban
            {
                int red = (int) (Math.random() * 256);
                int green = (int) (Math.random() * 256);
                int blue = (int) (Math.random() * 256);
                color = new Color(red, green, blue);

                alak[0] = new Point(3, 0);
                alak[1] = new Point(4, 0);
                alak[2] = new Point(5, 0);
                alak[3] = new Point(6, 0);

                break;
            }
            case 3: // T -forma
            {
                int red = (int) (Math.random() * 256);
                int green = (int) (Math.random() * 256);
                int blue = (int) (Math.random() * 256);
                color = new Color(red, green, blue);

                alak[0] = new Point(3, 0);
                alak[1] = new Point(4, 0);
                alak[2] = new Point(5, 0);
                alak[3] = new Point(4, -1);

                break;
            }
            case 4://egyik iranyu villam
            {
                int red = (int) (Math.random() * 256);
                int green = (int) (Math.random() * 256);
                int blue = (int) (Math.random() * 256);
                color = new Color(red, green, blue);

                alak[0] = new Point(3, -1);
                alak[1] = new Point(4, -1);
                alak[2] = new Point(4, 0);
                alak[3] = new Point(5, 0);

                break;
            }
            case 5://egy L betu
            {
                int red = (int) (Math.random() * 256);
                int green = (int) (Math.random() * 256);
                int blue = (int) (Math.random() * 256);
                color = new Color(red, green, blue);

                alak[0] = new Point(3, 0);
                alak[1] = new Point(4, 0);
                alak[2] = new Point(5, 0);
                alak[3] = new Point(5, -1);

                break;
            }
            case 6://masik L betu
            {
                int red = (int) (Math.random() * 256);
                int green = (int) (Math.random() * 256);
                int blue = (int) (Math.random() * 256);
                color = new Color(red, green, blue);

                alak[0] = new Point(3, 0);
                alak[1] = new Point(4, 0);
                alak[2] = new Point(5, 0);
                alak[3] = new Point(3, -1);

                break;
            }
            case 7://masik oldali villam
            {
                int red = (int) (Math.random() * 256);
                int green = (int) (Math.random() * 256);
                int blue = (int) (Math.random() * 256);
                color = new Color(red, green, blue);

                alak[0] = new Point(4, -1);
                alak[1] = new Point(5, -1);
                alak[2] = new Point(3, 0);
                alak[3] = new Point(4, 0);

                break;
            }
        }

        return alak;
    }

    public void rajzlefele(Graphics g)//tablan ahogy jon le
    {
        for (int i = 0; i < 4; i++)//minden forma negy kicsi kockabol all
        {
            g.setColor(color);
            g.fillRect(Tetris.m * alak[i].x, Tetris.m * alak[i].y,//kitöltött téglalapot/negyzet rajzol ki, amelynek bal felső sarkát a (x, y)
                    Tetris.m, Tetris.m);

            g.setColor(Color.white);
            g.drawRect(Tetris.m * alak[i].x, Tetris.m * alak[i].y,//egy téglalapot/negyzet rajzol, de csak a körvonalát
                    Tetris.m - 1, Tetris.m - 1);

            g.setColor(Color.black);
            g.drawRect(Tetris.m * alak[i].x, Tetris.m * alak[i].y,
                    Tetris.m, Tetris.m);

        }
    }

    public void rajzsorba(Graphics g, int width, int height)//next elemek kirajzolasa
    {
        int dim = Tetris.m / 6 * 5;
        int xseged = (width - (dim * 3)) / 2;
        int yseged = (height - (dim * 2)) / 2;


        switch (id) {
            case 1: {
                xseged += -1 * dim / 2;
                break;
            }
            case 2: {
                xseged += -1 * dim / 2;
                yseged += -1 * dim / 2;
            }
        }

        for (int i = 0; i < 4; i++) {
            g.setColor(color);
            g.fillRect(dim * (alak[i].x - 3) + xseged, dim * (alak[i].y + 1) + yseged,
                    dim,
                    dim);

            g.setColor(Color.white);
            g.drawRect(dim * (alak[i].x - 3) + xseged, dim * (alak[i].y + 1) + yseged,
                    dim - 1,
                    dim - 1);

            g.setColor(Color.black);
            g.drawRect(dim * (alak[i].x - 3) + xseged, dim * (alak[i].y + 1) + yseged,
                    dim,
                    dim);
        }
    }

    public void move(Point shift)//lefele eses
    {
        for (int i = 0; i < 4; i++) {
            alak[i].x += shift.x;
            alak[i].y += shift.y;
        }
    }

    public void rotate(int n) {
        switch (id) {
            case 1: //kocka
            {
                break;
            }

            case 2://4 pont egy sorba
            {

                if (alak[0].y != alak[1].y) {
                    n *= -1;
                }
                alak[0].x += 1 * n;
                alak[0].y += -1 * n;
                alak[2].x += -1 * n;
                alak[2].y += 1 * n;
                alak[3].x += -2 * n;
                alak[3].y += 2 * n;

                break;
            }
            case 3:// T -forma
            {
                if (alak[0].y == alak[1].y && alak[1].y == alak[2].y) {
                    if (alak[1].y > alak[3].y) {
                        alak[0].x += 1 * n;
                        alak[0].y += -1 * n;
                        alak[2].x += -1 * n;
                        alak[2].y += 1 * n;
                        alak[3].x += 1 * n;
                        alak[3].y += 1 * n;
                    } else {
                        alak[0].x += -1 * n;
                        alak[0].y += 1 * n;
                        alak[2].x += 1 * n;
                        alak[2].y += -1 * n;
                        alak[3].x += -1 * n;
                        alak[3].y += -1 * n;
                    }
                } else {
                    if (alak[1].x < alak[3].x) {
                        alak[0].x += 1 * n;
                        alak[0].y += 1 * n;
                        alak[2].x += -1 * n;
                        alak[2].y += -1 * n;
                        alak[3].x += -1 * n;
                        alak[3].y += 1 * n;
                    } else {
                        alak[0].x += -1 * n;
                        alak[0].y += -1 * n;
                        alak[2].x += 1 * n;
                        alak[2].y += 1 * n;
                        alak[3].x += 1 * n;
                        alak[3].y += -1 * n;
                    }
                }
                break;
            }
            case 4://egyik iranyu villam
            {
                if (alak[0].y != alak[1].y) {
                    n *= -1;
                }
                alak[0].x += 2 * n;
                alak[0].y += -1 * n;
                alak[1].x += 1 * n;
                alak[1].y += 0 * n;
                alak[2].x += 0 * n;
                alak[2].y += -1 * n;
                alak[3].x += -1 * n;
                alak[3].y += 0 * n;

                break;
            }
            case 5: //egyik L betu
            {
                if (alak[0].y == alak[2].y && alak[1].y == alak[2].y) {
                    if (alak[1].y > alak[3].y) {
                        alak[0].x += 1 * n;
                        alak[0].y += -1 * n;
                        alak[1].x += 0;
                        alak[1].y += 0;
                        alak[2].x += -1 * n;
                        alak[2].y += 1 * n;
                        alak[3].x += 0;
                        alak[3].y += 2 * n;
                    } else {
                        alak[0].x += -1 * n;
                        alak[0].y += 1 * n;
                        alak[1].x += 0;
                        alak[1].y += 0;
                        alak[2].x += 1 * n;
                        alak[2].y += -1 * n;
                        alak[3].x += 0;
                        alak[3].y += -2 * n;
                    }
                } else if (alak[0].x == alak[2].x && alak[1].x == alak[2].x) {
                    if (alak[1].x < alak[3].x) {
                        alak[0].x += 1 * n;
                        alak[0].y += 1 * n;
                        alak[1].x += 0;
                        alak[1].y += 0;
                        alak[2].x += -1 * n;
                        alak[2].y += -1 * n;
                        alak[3].x += -2 * n;
                        alak[3].y += 0;
                    } else {
                        alak[0].x += -1 * n;
                        alak[0].y += -1 * n;
                        alak[1].x += 0;
                        alak[1].y += 0;
                        alak[2].x += 1 * n;
                        alak[2].y += 1 * n;
                        alak[3].x += 2 * n;
                        alak[3].y += 0;
                    }
                }
                break;
            }
            case 6://masik L betu
            {
                if (alak[0].y == alak[2].y && alak[1].y == alak[2].y) {
                    if (alak[1].y > alak[3].y) {
                        alak[0].x += 1 * n;
                        alak[0].y += -1 * n;
                        alak[1].x += 0;
                        alak[1].y += 0;
                        alak[2].x += -1 * n;
                        alak[2].y += 1 * n;
                        alak[3].x += 2 * n;
                        alak[3].y += 0;
                    } else {
                        alak[0].x += -1 * n;
                        alak[0].y += 1 * n;
                        alak[1].x += 0;
                        alak[1].y += 0;
                        alak[2].x += 1 * n;
                        alak[2].y += -1 * n;
                        alak[3].x += -2 * n;
                        alak[3].y += 0;
                    }
                } else if (alak[0].x == alak[2].x && alak[1].x == alak[2].x) {
                    if (alak[1].x < alak[3].x) {
                        alak[0].x += 1 * n;
                        alak[0].y += 1 * n;
                        alak[1].x += 0;
                        alak[1].y += 0;
                        alak[2].x += -1 * n;
                        alak[2].y += -1 * n;
                        alak[3].x += 0;
                        alak[3].y += 2 * n;
                    } else {
                        alak[0].x += -1 * n;
                        alak[0].y += -1 * n;
                        alak[1].x += 0;
                        alak[1].y += 0;
                        alak[2].x += 1 * n;
                        alak[2].y += 1 * n;
                        alak[3].x += 0;
                        alak[3].y += -2 * n;
                    }
                }
                break;
            }
            case 7://masik villam forma
            {
                if (alak[0].y != alak[1].y) {
                    n *= -1;
                }
                alak[0].x += 1 * n;
                alak[0].y += 0 * n;
                alak[1].x += 0 * n;
                alak[1].y += 1 * n;
                alak[2].x += 1 * n;
                alak[2].y += -2 * n;
                alak[3].x += 0 * n;
                alak[3].y += -1 * n;

                break;
            }
        }
    }
}