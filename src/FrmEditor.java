import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FrmEditor extends JFrame {

    private List<Figura> figuras;
    private JButton btnCargar, btnGuardar, btnSeleccionar, btnEliminar;
    private JComboBox<String> cmbTipo;
    private JToolBar tbEditor;
    private JPanel pnlGrafica;

    private Estado estado;
    private int x, y;
    private Color color;
    private Figura figuraSeleccionada;
    private Figura trazoTemporal;

    public FrmEditor() {
        figuras = new ArrayList<>();
        tbEditor = new JToolBar();
        btnCargar = new JButton();
        btnGuardar = new JButton();
        cmbTipo = new JComboBox<>();
        btnSeleccionar = new JButton();
        btnEliminar = new JButton();
        pnlGrafica = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.black);
                g.fillRect(0, 0, getWidth(), getHeight());

                // Dibujar todas las figuras almacenadas
                for (Figura figura : figuras) {
                    figura.dibujar(g);
                }

                // Dibujar la figura temporal (si existe)
                if (trazoTemporal != null) {
                    trazoTemporal.dibujar(g);
                }
            }
        };

        setSize(600, 300);
        setTitle("Editor de gráficas");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Configurar barra de herramientas
        btnCargar.setIcon(new ImageIcon(getClass().getResource("/iconos/AbrirArchivos.png")));
        btnCargar.setToolTipText("Cargar");
        btnCargar.addActionListener(this::btnCargarClick);  // Evento para cargar
        tbEditor.add(btnCargar);

        btnGuardar.setIcon(new ImageIcon(getClass().getResource("/iconos/Guardar.png")));
        btnGuardar.setToolTipText("Guardar");
        btnGuardar.addActionListener(this::btnGuardarClick);  // Evento para guardar
        tbEditor.add(btnGuardar);

        cmbTipo.setModel(new DefaultComboBoxModel<>(new String[]{"Línea", "Rectángulo", "Círculo"}));
        tbEditor.add(cmbTipo);

        btnSeleccionar.setIcon(new ImageIcon(getClass().getResource("/iconos/Seleccionar.png")));
        btnSeleccionar.setToolTipText("Seleccionar");
        btnSeleccionar.addActionListener(this::btnSeleccionarClick);
        tbEditor.add(btnSeleccionar);

        btnEliminar.setIcon(new ImageIcon(getClass().getResource("/iconos/Eliminar (2).png")));
        btnEliminar.setToolTipText("Eliminar");
        btnEliminar.addActionListener(this::btnEliminarClick);
        tbEditor.add(btnEliminar);

        pnlGrafica.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                pnlGraficaMouseClicked(evt);
            }
        });
        pnlGrafica.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent evt) {
                pnlGraficaMouseMoved(evt);
            }
        });

        pnlGrafica.setPreferredSize(new Dimension(300, 200));

        getContentPane().add(tbEditor, BorderLayout.NORTH);
        getContentPane().add(pnlGrafica, BorderLayout.CENTER);

        estado = Estado.NADA;
        color = Color.yellow;

        this.pack();
    }

    private void agregarFigura(Figura figura) {
        figuras.add(figura);
        pnlGrafica.repaint();
    }

    private void btnSeleccionarClick(ActionEvent evt) {
        estado = Estado.SELECCIONANDO;
        figuraSeleccionada = null;
        System.out.println("Modo de selección activado");
    }

    private void btnEliminarClick(ActionEvent evt) {
        if (figuraSeleccionada != null) {
            figuras.remove(figuraSeleccionada);
            figuraSeleccionada = null;
            pnlGrafica.repaint();
            System.out.println("Figura eliminada");
        } else {
            System.out.println("No hay ninguna figura seleccionada para eliminar");
        }
    }

    private void pnlGraficaMouseClicked(MouseEvent evt) {
        if (estado == Estado.NADA) {
            estado = Estado.TRAZANDO;
            x = evt.getX();
            y = evt.getY();
        } else if (estado == Estado.TRAZANDO) {
            estado = Estado.NADA;
            Figura.TipoFigura tipoFigura = switch (cmbTipo.getSelectedIndex()) {
                case 0 -> Figura.TipoFigura.LINEA;
                case 1 -> Figura.TipoFigura.RECTANGULO;
                case 2 -> Figura.TipoFigura.CIRCULO;
                default -> null;
            };
            Figura nuevaFigura = new Figura(x, y, evt.getX(), evt.getY(), color, tipoFigura);
            agregarFigura(nuevaFigura);
            trazoTemporal = null;  // Limpiar el trazo temporal después de agregar la figura
        } else if (estado == Estado.SELECCIONANDO) {
            for (Figura figura : figuras) {
                if (figura.contienePunto(evt.getX(), evt.getY())) {
                    figuraSeleccionada = figura;
                    break;
                }
            }
            if (figuraSeleccionada != null) {
                System.out.println("Figura seleccionada");
            } else {
                System.out.println("No se ha seleccionado ninguna figura");
            }
        }
    }

    private void pnlGraficaMouseMoved(MouseEvent evt) {
        if (estado == Estado.TRAZANDO) {
            Figura.TipoFigura tipoFigura = switch (cmbTipo.getSelectedIndex()) {
                case 0 -> Figura.TipoFigura.LINEA;
                case 1 -> Figura.TipoFigura.RECTANGULO;
                case 2 -> Figura.TipoFigura.CIRCULO;
                default -> null;
            };
            trazoTemporal = new Figura(x, y, evt.getX(), evt.getY(), color, tipoFigura);
            pnlGrafica.repaint();  // Redibujar el panel para mostrar la figura temporal
        }
    }

    // Método para guardar en archivo
    private void btnGuardarClick(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                for (Figura figura : figuras) {
                    writer.println(figura.toFileFormat());
                }
                System.out.println("Figuras guardadas en: " + file.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para cargar desde archivo
    private void btnCargarClick(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                figuras.clear();  // Limpiar las figuras actuales
                String line;
                while ((line = reader.readLine()) != null) {
                    Figura figura = Figura.fromFileFormat(line);
                    figuras.add(figura);
                }
                pnlGrafica.repaint();  // Redibujar todas las figuras cargadas
                System.out.println("Figuras cargadas desde: " + file.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
