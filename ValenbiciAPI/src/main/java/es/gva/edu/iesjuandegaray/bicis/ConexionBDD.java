package es.gva.edu.iesjuandegaray.bicis;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionBDD extends JFrame {

    private static final Logger LOG = Logger.getLogger(ConexionBDD.class.getName());

    private JPanel contentPane;
    private JTextField textFieldNEstaciones;
    private JTextArea textAreaDatos;
    private JLabel lblEstadoConexion;
    private JButton btnDatos;
    private JButton btnConectar;
    private JButton btnAdd;
    private JButton btnCerrar;

    private DatosJSon dJSon;
    private Connection con;
    private int numEst = 3;

    private static final String DB_URL = Config.get("db.url");
    private static final String DB_USER = Config.get("db.user");
    private static final String DB_PASS = Config.get("db.password");
    private static final String DB_DRIVER = Config.get("db.driver");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                LOG.log(Level.FINE, "No se pudo aplicar el look and feel del sistema", e);
            }
            new ConexionBDD().setVisible(true);
        });
    }

    public ConexionBDD() {
        dJSon = new DatosJSon(numEst);
        inicializarUI();
        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(600, 450));
    }

    private void inicializarUI() {
        setTitle("ValenbiciAPI - Gestion de Estaciones");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);

        JPanel panelNorte = crearPanelNorte();
        JPanel panelCentro = crearPanelCentro();
        JPanel panelSur = crearPanelSur();

        contentPane.add(panelNorte, BorderLayout.NORTH);
        contentPane.add(panelCentro, BorderLayout.CENTER);
        contentPane.add(panelSur, BorderLayout.SOUTH);
    }

    private JPanel crearPanelNorte() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        panel.add(new JLabel("Numero de estaciones:"));
        textFieldNEstaciones = new JTextField(String.valueOf(numEst), 5);
        panel.add(textFieldNEstaciones);

        btnDatos = new JButton("Obtener Datos");
        btnDatos.addActionListener(e -> obtenerDatos());
        panel.add(btnDatos);

        btnConectar = new JButton("Conectar BDD");
        btnConectar.addActionListener(e -> conectarBDD());
        panel.add(btnConectar);

        return panel;
    }

    private JPanel crearPanelCentro() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JLabel lblTitulo = new JLabel("Datos de las estaciones:");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(lblTitulo, BorderLayout.NORTH);

        textAreaDatos = new JTextArea();
        textAreaDatos.setEditable(false);
        textAreaDatos.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textAreaDatos);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelSur() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        btnAdd = new JButton("Anadir a BDD");
        btnAdd.setEnabled(false);
        btnAdd.addActionListener(e -> anadirBDD());
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(btnAdd, gbc);

        lblEstadoConexion = new JLabel(" Desconectado ", JLabel.CENTER);
        lblEstadoConexion.setOpaque(true);
        lblEstadoConexion.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gbc.gridx = 1; gbc.weightx = 0.4;
        panel.add(lblEstadoConexion, gbc);

        btnCerrar = new JButton("Cerrar Conexion");
        btnCerrar.setEnabled(false);
        btnCerrar.addActionListener(e -> cerrarConexion());
        gbc.gridx = 2; gbc.weightx = 0.3;
        panel.add(btnCerrar, gbc);

        return panel;
    }

    private void obtenerDatos() {
        try {
            numEst = Integer.parseInt(textFieldNEstaciones.getText().trim());
            if (numEst <= 0) {
                JOptionPane.showMessageDialog(this,
                    "El numero debe ser positivo",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Introduce un numero valido",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        btnDatos.setEnabled(false);
        textAreaDatos.setText("Consultando API...");

        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                dJSon.mostrarDatos(numEst);
                publish(dJSon.getDatos());
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                textAreaDatos.setText(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                btnDatos.setEnabled(true);
                if (!dJSon.getDatos().isEmpty()) {
                    btnAdd.setEnabled(con != null);
                }
            }
        }.execute();
    }

    private void conectarBDD() {
        btnConectar.setEnabled(false);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    Class.forName(DB_DRIVER);
                    con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                    return true;
                } catch (ClassNotFoundException e) {
                    LOG.log(Level.SEVERE, "Driver JDBC no encontrado", e);
                    return false;
                } catch (SQLException e) {
                    LOG.log(Level.SEVERE, "Error de conexion MySQL", e);
                    return false;
                }
            }

            @Override
            protected void done() {
                btnConectar.setEnabled(true);
                try {
                    if (get()) {
                        lblEstadoConexion.setText(" Conectado ");
                        lblEstadoConexion.setBackground(new Color(200, 255, 200));
                        btnCerrar.setEnabled(true);
                        btnAdd.setEnabled(!dJSon.getDatos().isEmpty());
                        JOptionPane.showMessageDialog(ConexionBDD.this,
                            "Conectado a MySQL correctamente",
                            "Exito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        lblEstadoConexion.setText(" Error de conexion ");
                        JOptionPane.showMessageDialog(ConexionBDD.this,
                            "No se pudo conectar.\nVerifica que MySQL este ejecutandose.",
                            "Error de Conexion", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error al obtener resultado", e);
                }
            }
        }.execute();
    }

    private void anadirBDD() {
        if (con == null) {
            JOptionPane.showMessageDialog(this,
                "Conecta primero a la base de datos",
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnAdd.setEnabled(false);
        textAreaDatos.setText("Insertando datos en la base de datos...");

        new SwingWorker<Integer, String>() {
            @Override
            protected Integer doInBackground() {
                int insertados = 0;
                String sql = "INSERT INTO historico (estacion_id, direccion, bicis_disponibles, anclajes_libres, estado_operativo, ubicacion) VALUES (?, ?, ?, ?, true, ST_GeomFromText(?))";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    for (EstacionBici estacion : dJSon.getEstaciones()) {
                        ps.setInt(1, estacion.getNumber());
                        ps.setString(2, estacion.getAddress());
                        ps.setInt(3, estacion.getAvailable());
                        ps.setInt(4, estacion.getFree());
                        ps.setString(5, "POINT(" + estacion.getLat() + " " + estacion.getLon() + ")");
                        ps.executeUpdate();
                        insertados++;
                        publish("Insertada estacion #" + estacion.getNumber() + ": " + estacion.getAddress());
                    }
                } catch (SQLException e) {
                    LOG.log(Level.SEVERE, "Error al insertar en BDD", e);
                    publish("Error: " + e.getMessage());
                }
                return insertados;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                textAreaDatos.setText(String.join("\n", chunks));
            }

            @Override
            protected void done() {
                btnAdd.setEnabled(con != null);
                try {
                    int count = get();
                    JOptionPane.showMessageDialog(ConexionBDD.this,
                        count + " estaciones insertadas correctamente",
                        "Completado", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error al insertar", e);
                }
            }
        }.execute();
    }

    private void cerrarConexion() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Error al cerrar conexion", e);
        } finally {
            con = null;
            lblEstadoConexion.setText(" Desconectado ");
            lblEstadoConexion.setBackground(null);
            btnCerrar.setEnabled(false);
            btnAdd.setEnabled(false);
        }
    }
}