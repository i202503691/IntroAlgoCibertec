package Ejercicios;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class ProFinal extends JFrame {
    private static JTabbedPane pestanas;
    private static JPanel panelJugadores, panelEstadisticas, panelConsultas;

    private static int[] numerosCamiseta = new int[25];
    private static String[] nombres = new String[25];
    private static String[] posiciones = new String[25];

    private static int[] partidosJugados = new int[25];
    private static int[] tries = new int[25];
    private static int[] conversiones = new int[25];
    private static int[] penales = new int[25];
    private static int[] dropGoals = new int[25];
    private static int[] faltas = new int[25];
    private static int[] minutosJugados = new int[25];

    private static int cantidadJugadores = 0;
    private static int totalPartidos = 0;

    private static JComboBox<String> comboJugadoresEstadisticas;
    private static JComboBox<String> comboPartidos;
    private static JComboBox<String> comboConsultaJugador;
    private JComboBox<String> comboNumeroCamiseta;
    private DefaultComboBoxModel<String> modeloCamiseta;

    public ProFinal() {
        setTitle("Estadísticas del club Los Halcones");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        inicializarComponentes();
        setVisible(true);
    }

    private void inicializarComponentes() {
        pestanas = new JTabbedPane();
        panelJugadores = crearPanelJugadores();
        panelEstadisticas = crearPanelEstadisticas();
        panelConsultas = crearPanelConsultas();

        pestanas.addTab("Jugadores", panelJugadores);
        pestanas.addTab("Estadísticas", panelEstadisticas);
        pestanas.addTab("Consultas", panelConsultas);

        getContentPane().add(pestanas);
    }

    private JPanel crearPanelJugadores() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formulario = new JPanel(new GridLayout(4, 2));

        modeloCamiseta = new DefaultComboBoxModel<>();
        for (int i = 1; i <= 23; i++) {
            modeloCamiseta.addElement(String.valueOf(i));
        }
        comboNumeroCamiseta = new JComboBox<>(modeloCamiseta);

        comboNumeroCamiseta.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                int num = Integer.parseInt(value.toString());
                if (buscarJugador(num) != -1) {
                    c.setForeground(Color.GRAY);
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        comboNumeroCamiseta.addActionListener(e -> {
            if (comboNumeroCamiseta.getSelectedItem() != null) {
                int seleccionado = Integer.parseInt(comboNumeroCamiseta.getSelectedItem().toString());
                if (buscarJugador(seleccionado) != -1) {
                    JOptionPane.showMessageDialog(null, "Ese número ya está registrado. Elige otro.");
                    comboNumeroCamiseta.setSelectedIndex(-1);
                }
            }
        });

        JTextField txtNombre = new JTextField();
        JComboBox<String> comboPosicion = new JComboBox<>();
        String[] posicionesDisponibles = {
            "Pilar izquierdo (Prop)", "Talonador (Hooker)", "Pilar derecho (Prop)",
            "Segunda línea izquierdo", "Segunda línea derecho", "ala lado ciego (izquierdo)",
            "ala lado abierto (derecho)", "Medio melé ", "Medio scrum ", "Apertura (Fly half)",
            "Ala izquierdo (Left wing)", "centro interior", "centro exterior", "Ala derecho (right wing)",
            "Zaguero (Full back)", "Suplente 1", "Suplente 2", "Suplente 3", "Suplente 4",
            "Suplente 5", "Suplente 6", "Suplente 7", "Suplente 8"
        };
        for (String pos : posicionesDisponibles) {
            comboPosicion.addItem(pos);
        }

        // 🟨 Conjunto para rastrear las posiciones ya usadas
        java.util.Set<String> posicionesUsadas = new java.util.HashSet<>();

        // 🎨 Renderer para sombrear posiciones ya utilizadas
        comboPosicion.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (posicionesUsadas.contains(value.toString())) {
                    c.setForeground(Color.GRAY);
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        formulario.add(new JLabel("Número de camiseta:"));
        formulario.add(comboNumeroCamiseta);
        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("Posición:"));
        formulario.add(comboPosicion);

        JButton btnAgregar = new JButton("Agregar jugador");
        formulario.add(btnAgregar);

        DefaultTableModel modeloTabla = new DefaultTableModel(new String[]{"Camiseta", "Nombre", "Posición"}, 0);
        JTable tablaJugadores = new JTable(modeloTabla);
        panel.add(formulario, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaJugadores), BorderLayout.CENTER);

        btnAgregar.addActionListener(e -> {
            if (comboNumeroCamiseta.getSelectedItem() == null) return;

            int numero = Integer.parseInt((String) comboNumeroCamiseta.getSelectedItem());
            String nombre = txtNombre.getText().trim();
            String posicion = (String) comboPosicion.getSelectedItem();

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(null, "El nombre no puede estar vacío.");
                return;
            }

            if (buscarJugador(numero) != -1) {
                JOptionPane.showMessageDialog(null, "Número de camiseta ya registrado");
                return;
            }

            if (posicionesUsadas.contains(posicion)) {
                JOptionPane.showMessageDialog(null, "Esa posición ya fue asignada. Elige otra.");
                return;
            }

            // ✅ Registrar jugador y marcar la posición como usada
            agregarJugador(numero, nombre, posicion);
            posicionesUsadas.add(posicion); // Agregar posición usada

            modeloTabla.addRow(new Object[]{numero, nombre, posicion});
            comboJugadoresEstadisticas.addItem(numero + " - " + nombre);
            comboConsultaJugador.addItem(nombre + " - Camiseta: " + numero);

            comboNumeroCamiseta.repaint();
            comboPosicion.repaint(); // Refrescar combo para mostrar grises
        });

        return panel;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formulario = new JPanel(new GridLayout(12, 2));
        JLabel lblRegistrar = new JLabel("Registrar Nuevo Partido");
        formulario.add(lblRegistrar);
        JButton btnRegistrarPartido = new JButton("Registrar Partido");
        formulario.add(btnRegistrarPartido);

        JLabel lblJugadorPartido = new JLabel("Seleccionar Jugador y Partido");
        formulario.add(lblJugadorPartido);
        formulario.add(new JLabel());

        comboJugadoresEstadisticas = new JComboBox<>();
        comboPartidos = new JComboBox<>();
        formulario.add(new JLabel("Jugador:"));
        formulario.add(comboJugadoresEstadisticas);
        formulario.add(new JLabel("Partido:"));
        formulario.add(comboPartidos);

        JLabel lblEstadisticas = new JLabel("Estadísticas del Jugador");
        formulario.add(lblEstadisticas);
        formulario.add(new JLabel());

        JTextField txtMinutos = new JTextField();
        JTextField txtTries = new JTextField();
        JTextField txtConversiones = new JTextField();
        JTextField txtPenales = new JTextField();
        JTextField txtDropGoals = new JTextField();
        JTextField txtFaltas = new JTextField();

        formulario.add(new JLabel("Minutos jugados:"));
        formulario.add(txtMinutos);
        formulario.add(new JLabel("Tries:"));
        formulario.add(txtTries);
        formulario.add(new JLabel("Conversiones:"));
        formulario.add(txtConversiones);
        formulario.add(new JLabel("Penales:"));
        formulario.add(txtPenales);
        formulario.add(new JLabel("Drop Goals:"));
        formulario.add(txtDropGoals);
        formulario.add(new JLabel("Faltas:"));
        formulario.add(txtFaltas);

        JButton btnRegistrar = new JButton("Registrar estadísticas");
        formulario.add(btnRegistrar);

        panel.add(formulario, BorderLayout.NORTH);

        JLabel lblInstruccionesPrimero = new JLabel("<html>Instrucciones:<br><br>" +
                "1. Primero registre los partidos necesarios con el botón 'Registrar Nuevo Partido'<br><br>" +
                "2. Seleccione un jugador y un partido existente<br>" +
                "3. Ingrese las estadísticas del jugador para ese partido<br>" +
                "4. Presione 'Registrar Estadísticas' para guardar los datos<br><br>" +
                "Nota: Puede registrar estadísticas para cualquier partido ya creado, no necesariamente el último.</html>");
        lblInstruccionesPrimero.setPreferredSize(new Dimension(600, 150));
        panel.add(lblInstruccionesPrimero, BorderLayout.CENTER);

        btnRegistrarPartido.addActionListener(e -> {
            totalPartidos++;
            comboPartidos.addItem(String.valueOf(totalPartidos));
            JOptionPane.showMessageDialog(null, "Partido registrado. Ahora es el partido #" + totalPartidos);
        });

        btnRegistrar.addActionListener(e -> {
            int index = comboJugadoresEstadisticas.getSelectedIndex();
            if (index < 0) return;

            try {
                int minutos = Integer.parseInt(txtMinutos.getText());
                int triesCount = Integer.parseInt(txtTries.getText());
                int conversionesCount = Integer.parseInt(txtConversiones.getText());
                int penalesCount = Integer.parseInt(txtPenales.getText());
                int dropGoalsCount = Integer.parseInt(txtDropGoals.getText());
                int faltasCount = Integer.parseInt(txtFaltas.getText());

                if (minutos < 0 || triesCount < 0 || conversionesCount < 0 || penalesCount < 0 || dropGoalsCount < 0 || faltasCount < 0) {
                    JOptionPane.showMessageDialog(null, "Todos los valores deben ser positivos");
                    return;
                }

                registrarEstadisticas(index, minutos, triesCount, conversionesCount, penalesCount, dropGoalsCount, faltasCount);
                JOptionPane.showMessageDialog(null, "Estadísticas registradas correctamente");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Debes ingresar números válidos en todos los campos");
            }
        });

        txtMinutos.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume(); // Ignora caracteres que no sean dígitos
                }
            }
        });
        txtTries.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume(); // Ignora caracteres que no sean dígitos
                }
            }
        });
        txtConversiones.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume(); // Ignora caracteres que no sean dígitos
                }
            }
        });
        txtPenales.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume(); // Ignora caracteres que no sean dígitos
                }
            }
        });
        txtDropGoals.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume(); // Ignora caracteres que no sean dígitos
                }
            }
        });
        txtFaltas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume(); // Ignora caracteres que no sean dígitos
                }
            }
        });
        return panel;
    }

    private JPanel crearPanelConsultas() {
        JPanel panel = new JPanel(new BorderLayout());

        // Usamos GridBagLayout para alinear los botones en una fila
        JPanel controles = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Espaciado entre los botones

        comboConsultaJugador = new JComboBox<>();
        JTextArea areaEstadisticas = new JTextArea(15, 40);
        areaEstadisticas.setEditable(false);
        areaEstadisticas.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Label para seleccionar el jugador
        gbc.gridx = 0;
        gbc.gridy = 0;
        controles.add(new JLabel("Jugador:"), gbc);

        // ComboBox para seleccionar el jugador
        gbc.gridx = 1;
        controles.add(comboConsultaJugador, gbc);

        // Crear los botones
        JButton btnVerEstadisticas = new JButton("Ver estadísticas jugador");
        JButton btnVerRanking = new JButton("Ver ranking de jugadores por puntos");
        JButton btnEstadisticasEquipo = new JButton("Ver estadísticas globales del equipo");

        // Colocar los botones en la misma fila (usando GridBagLayout)
        gbc.gridx = 0;
        gbc.gridy = 1;
        controles.add(btnVerEstadisticas, gbc);

        gbc.gridx = 1;
        controles.add(btnVerRanking, gbc);

        gbc.gridx = 2;
        controles.add(btnEstadisticasEquipo, gbc);

        panel.add(controles, BorderLayout.NORTH);

        // Acción para ver estadísticas globales del equipo
        btnEstadisticasEquipo.addActionListener(e -> {
            int totalMinutos = 0, totalTries = 0, totalConv = 0, totalPen = 0, totalDrop = 0, totalFaltas = 0, totalPts = 0, totalPJ = 0;
            int maxPts = -1, idxMax = -1;

            for (int i = 0; i < cantidadJugadores; i++) {
                int pts = tries[i] * 5 + conversiones[i] * 2 + penales[i] * 3 + dropGoals[i] * 3;
                totalPts += pts;
                totalTries += tries[i];
                totalConv += conversiones[i];
                totalPen += penales[i];
                totalDrop += dropGoals[i];
                totalFaltas += faltas[i];
                totalMinutos += minutosJugados[i];
                totalPJ += partidosJugados[i];
                if (pts > maxPts) {
                    maxPts = pts;
                    idxMax = i;
                }
            }

            int jugadores = cantidadJugadores;
            StringBuilder sb = new StringBuilder();
            sb.append("ESTADÍSTICAS DEL EQUIPO\n");
            sb.append("========================\n\n");
            sb.append("TOTALES DEL EQUIPO\n");
            sb.append("------------------\n");
            sb.append("Puntos totales: ").append(totalPts).append("\n");
            sb.append("Tries: ").append(totalTries).append("\n");
            sb.append("Conversiones: ").append(totalConv).append("\n");
            sb.append("Penales: ").append(totalPen).append("\n");
            sb.append("Drop Goals: ").append(totalDrop).append("\n");
            sb.append("Minutos totales: ").append(totalMinutos).append("\n");
            sb.append("Faltas: ").append(totalFaltas).append("\n\n");

            sb.append("PROMEDIOS\n");
            sb.append("---------\n");
            sb.append("Puntos por jugador: ").append(jugadores > 0 ? totalPts / jugadores : 0).append("\n");
            sb.append("Minutos por jugador: ").append(jugadores > 0 ? totalMinutos / jugadores : 0).append("\n");
            sb.append("Faltas por jugador: ").append(jugadores > 0 ? totalFaltas / jugadores : 0).append("\n\n");

            if (idxMax >= 0) {
                sb.append("JUGADOR MÁS ANOTADOR\n");
                sb.append("---------------------\n");
                sb.append(nombres[idxMax]).append(" - Camiseta: ").append(numerosCamiseta[idxMax])
                        .append(" - ").append(maxPts).append(" puntos\n");
            }

            areaEstadisticas.setText(sb.toString());
        });

        // Acción para ver ranking de jugadores
        btnVerRanking.addActionListener(e -> {
            int[] indices = new int[cantidadJugadores];
            int[] puntos = new int[cantidadJugadores];
            for (int i = 0; i < cantidadJugadores; i++) {
                int totalPuntos = tries[i] * 5 + conversiones[i] * 2 + penales[i] * 3 + dropGoals[i] * 3;
                puntos[i] = totalPuntos;
                indices[i] = i;
            }

            // Ordenar por puntos de mayor a menor (burbuja simple)
            for (int i = 0; i < cantidadJugadores - 1; i++) {
                for (int j = i + 1; j < cantidadJugadores; j++) {
                    if (puntos[i] < puntos[j]) {
                        int tempPuntos = puntos[i];
                        puntos[i] = puntos[j];
                        puntos[j] = tempPuntos;

                        int tempIndex = indices[i];
                        indices[i] = indices[j];
                        indices[j] = tempIndex;
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("RANKING DE JUGADORES POR PUNTOS\n");
            sb.append("================================\n");
            sb.append(String.format("%-5s %-12s %-8s %-6s %-6s %-6s %-6s\n", "Pos", "Nombre", "Puntos", "Tries", "Conv", "Pen", "Drop"));
            sb.append("=========================================================\n");
            for (int i = 0; i < cantidadJugadores; i++) {
                int idx = indices[i];
                sb.append(String.format("%-5d %-12s %-8d %-6d %-6d %-6d %-6d\n",
                        (i + 1),
                        nombres[idx],
                        puntos[i],
                        tries[idx],
                        conversiones[idx],
                        penales[idx],
                        dropGoals[idx]
                ));
            }

            areaEstadisticas.setText(sb.toString());
        });

        // Acción para ver estadísticas del jugador
        btnVerEstadisticas.addActionListener(e -> {
            int index = comboConsultaJugador.getSelectedIndex();
            if (index < 0) return;

            String nombre = nombres[index];
            int numero = numerosCamiseta[index];
            String posicion = posiciones[index];
            int partidos = partidosJugados[index];
            int minutos = minutosJugados[index];

            int triesCount = tries[index];
            int conversionesCount = conversiones[index];
            int penalesCount = penales[index];
            int dropGoalsCount = dropGoals[index];
            int faltasCount = faltas[index];

            int puntosTries = triesCount * 5;
            int puntosConversiones = conversionesCount * 2;
            int puntosPenales = penalesCount * 3;
            int puntosDropGoals = dropGoalsCount * 3;
            int totalPuntos = puntosTries + puntosConversiones + puntosPenales + puntosDropGoals;

            double promedioMinutos = partidos > 0 ? (double) minutos / partidos : 0;
            double eficiencia = minutos > 0 ? (double) totalPuntos / minutos : 0;

            StringBuilder sb = new StringBuilder();
            sb.append("ESTADÍSTICAS DE JUGADOR\n");
            sb.append("========================\n");
            sb.append("Nombre: ").append(nombre).append("\n");
            sb.append("Número: ").append(numero).append("\n");
            sb.append("Posición: ").append(posicion).append("\n\n");
            sb.append("Partidos jugados: ").append(partidos).append("\n");
            sb.append("Minutos totales: ").append(minutos).append("\n");
            sb.append(String.format("Promedio minutos/partido: %.2f\n\n", promedioMinutos));

            sb.append("ANOTACIONES:\n\n");
            sb.append(String.format("Tries: %d (%d pts)\n", triesCount, puntosTries));
            sb.append(String.format("Conversiones: %d (%d pts)\n", conversionesCount, puntosConversiones));
            sb.append(String.format("Penales: %d (%d pts)\n", penalesCount, puntosPenales));
            sb.append(String.format("Drop goals: %d (%d pts)\n\n", dropGoalsCount, puntosDropGoals));

            sb.append(String.format("PUNTOS TOTALES: %d\n", totalPuntos));
            sb.append(String.format("EFICIENCIA (pts/min): %.4f\n", eficiencia));
            sb.append(String.format("Faltas cometidas: %d\n", faltasCount));

            areaEstadisticas.setText(sb.toString());
        });

        panel.add(new JScrollPane(areaEstadisticas), BorderLayout.CENTER);

        // Llenar el ComboBox con los nombres y números de los jugadores
        for (int i = 0; i < cantidadJugadores; i++) {
            comboConsultaJugador.addItem(nombres[i] + " - Camiseta: " + numerosCamiseta[i]);
        }

        return panel;
    }
  
   
    private void agregarJugador(int numero, String nombre, String posicion) {
        if (cantidadJugadores < 25) {
            numerosCamiseta[cantidadJugadores] = numero;
            nombres[cantidadJugadores] = nombre;
            posiciones[cantidadJugadores] = posicion;
            partidosJugados[cantidadJugadores] = 0;
            tries[cantidadJugadores] = 0;
            conversiones[cantidadJugadores] = 0;
            penales[cantidadJugadores] = 0;
            dropGoals[cantidadJugadores] = 0;
            faltas[cantidadJugadores] = 0;
            minutosJugados[cantidadJugadores] = 0;
            cantidadJugadores++;
        }
    }

    private int buscarJugador(int numero) {
        for (int i = 0; i < cantidadJugadores; i++) {
            if (numerosCamiseta[i] == numero) {
                return i;
            }
        }
        return -1;
    }

    private void registrarEstadisticas(int index, int minutos, int triesCount, int conversionesCount, int penalesCount, int dropGoalsCount, int faltasCount) {
        partidosJugados[index]++;
        tries[index] += triesCount;
        conversiones[index] += conversionesCount;
        penales[index] += penalesCount;
        dropGoals[index] += dropGoalsCount;
        faltas[index] += faltasCount;
        minutosJugados[index] += minutos;
    }

    public static void main(String[] args) {
        new ProFinal();
    }
}
