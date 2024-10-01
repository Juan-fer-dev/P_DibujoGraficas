import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;

public class Figura {
    private int x1, y1, x2, y2;
    private Color color;
    private TipoFigura tipo; // Enum para indicar el tipo de figura

    // Constructor de la figura
    public Figura(int x1, int y1, int x2, int y2, Color color, TipoFigura tipo) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.tipo = tipo;
    }

    // Método para dibujar la figura
    public void dibujar(Graphics g) {
        g.setColor(color);
        switch (tipo) {
            case LINEA:
                g.drawLine(x1, y1, x2, y2);
                break;
            case RECTANGULO:
                g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
                break;
            case CIRCULO:
                int radio = (int) Math.hypot(x2 - x1, y2 - y1);
                g.drawOval(x1 - radio, y1 - radio, 2 * radio, 2 * radio);
                break;
        }
    }

    // Verificar si un punto está dentro de la figura
    public boolean contienePunto(int x, int y) {
        switch (tipo) {
            case LINEA:
                double distancia = Line2D.ptSegDist(x1, y1, x2, y2, x, y);
                return distancia < 5.0; // Ajustar la tolerancia si es necesario
            case RECTANGULO:
                return x >= Math.min(x1, x2) && x <= Math.max(x1, x2) && y >= Math.min(y1, y2) && y <= Math.max(y1, y2);
            case CIRCULO:
                int dx = x - x1;
                int dy = y - y1;
                int radio = (int) Math.hypot(x2 - x1, y2 - y1);
                return Math.sqrt(dx * dx + dy * dy) <= radio;
            default:
                return false;
        }
    }

    // Método para convertir la figura a formato de archivo
    public String toFileFormat() {
        return String.format("%s;%d;%d;%d;%d;%d;%d;%d",
                tipo.name(), x1, y1, x2, y2, color.getRed(), color.getGreen(), color.getBlue());
    }

    // Método para crear una figura desde el formato de archivo
    public static Figura fromFileFormat(String line) {
        String[] parts = line.split(";");
        TipoFigura tipo = TipoFigura.valueOf(parts[0]);
        int x1 = Integer.parseInt(parts[1]);
        int y1 = Integer.parseInt(parts[2]);
        int x2 = Integer.parseInt(parts[3]);
        int y2 = Integer.parseInt(parts[4]);
        int r = Integer.parseInt(parts[5]);
        int g = Integer.parseInt(parts[6]);
        int b = Integer.parseInt(parts[7]);
        Color color = new Color(r, g, b);

        return new Figura(x1, y1, x2, y2, color, tipo);
    }

    // Enum para tipos de figura
    public enum TipoFigura {
        LINEA, RECTANGULO, CIRCULO
    }
}
