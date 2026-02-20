import java.awt.*;
import java.awt.event.*;

/**
 * Simple styled AWT calculator (single file) that runs directly in Eclipse.
 *
 * How to run:
 * - In Eclipse: File -> New -> Java Project, then create a class named SimpleCalculatorAWT_Styled
 *   and paste this file content, or import this file into the project.
 * - Run the main() method.
 */
public class SimpleCalculatorAWT_Styled extends Frame
        implements ActionListener, WindowListener, MouseListener {

    private final TextField display = new TextField("0");
    private final Label status = new Label(" ");

    private double first = 0.0;
    private String op = null;
    private boolean startNew = true;

    // Theme
    private final Color bg = new Color(20, 22, 28);
    private final Color panelBg = new Color(28, 30, 38);
    private final Color displayBg = new Color(12, 14, 18);
    private final Color displayFg = new Color(235, 238, 245);

    private final Color numBg = new Color(45, 48, 60);
    private final Color opBg = new Color(70, 120, 200);
    private final Color utilBg = new Color(200, 90, 90);
    private final Color eqBg = new Color(70, 170, 120);

    public SimpleCalculatorAWT_Styled() {
        super("Calculator (AWT - Styled)");

        setLayout(new BorderLayout(10, 10));
        setBackground(bg);

        // Top: display + status
        Panel top = new Panel(new GridLayout(2, 1, 6, 6));
        top.setBackground(bg);

        display.setEditable(false);
        display.setBackground(displayBg);
        display.setForeground(displayFg);
        display.setFont(new Font("Monospaced", Font.BOLD, 30));

        status.setBackground(bg);
        status.setForeground(new Color(160, 170, 190));
        status.setFont(new Font("Dialog", Font.PLAIN, 12));

        top.add(display);
        top.add(status);
        add(top, BorderLayout.NORTH);

        // Center: keys with padding
        Panel center = new Panel(new BorderLayout());
        center.setBackground(bg);

        Panel keys = new Panel(new GridBagLayout());
        keys.setBackground(panelBg);

        // Layout 5x4
        // Row0: AC  ⌫  %   /
        // Row1: 7   8  9   *
        // Row2: 4   5  6   -
        // Row3: 1   2  3   +
        // Row4: 0 (wide)  .  =
        addButton(keys, 0, 0, 1, "AC", utilBg, Color.white);
        addButton(keys, 1, 0, 1, "⌫", utilBg, Color.white);
        addButton(keys, 2, 0, 1, "%", opBg, Color.white);
        addButton(keys, 3, 0, 1, "/", opBg, Color.white);

        addButton(keys, 0, 1, 1, "7", numBg, displayFg);
        addButton(keys, 1, 1, 1, "8", numBg, displayFg);
        addButton(keys, 2, 1, 1, "9", numBg, displayFg);
        addButton(keys, 3, 1, 1, "*", opBg, Color.white);

        addButton(keys, 0, 2, 1, "4", numBg, displayFg);
        addButton(keys, 1, 2, 1, "5", numBg, displayFg);
        addButton(keys, 2, 2, 1, "6", numBg, displayFg);
        addButton(keys, 3, 2, 1, "-", opBg, Color.white);

        addButton(keys, 0, 3, 1, "1", numBg, displayFg);
        addButton(keys, 1, 3, 1, "2", numBg, displayFg);
        addButton(keys, 2, 3, 1, "3", numBg, displayFg);
        addButton(keys, 3, 3, 1, "+", opBg, Color.white);

        addButton(keys, 0, 4, 2, "0", numBg, displayFg);
        addButton(keys, 2, 4, 1, ".", numBg, displayFg);
        addButton(keys, 3, 4, 1, "=", eqBg, Color.white);

        // padding panels
        center.add(keys, BorderLayout.CENTER);
        center.add(new Panel() {{ setBackground(bg); setPreferredSize(new Dimension(10, 10)); }}, BorderLayout.NORTH);
        center.add(new Panel() {{ setBackground(bg); setPreferredSize(new Dimension(10, 10)); }}, BorderLayout.SOUTH);
        center.add(new Panel() {{ setBackground(bg); setPreferredSize(new Dimension(10, 10)); }}, BorderLayout.EAST);
        center.add(new Panel() {{ setBackground(bg); setPreferredSize(new Dimension(10, 10)); }}, BorderLayout.WEST);

        add(center, BorderLayout.CENTER);

        addWindowListener(this);

        setSize(360, 520);
        setLocationRelativeTo(null);
        setVisible(true);

        setStatus("Ready");
    }

    private void addButton(Panel parent, int x, int y, int w, String label, Color bgColor, Color fgColor) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = w;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(6, 6, 6, 6);

        Button b = new Button(label);
        b.setFont(new Font("Dialog", Font.BOLD, 18));
        b.setBackground(bgColor);
        b.setForeground(fgColor);
        b.addActionListener(this);
        b.addMouseListener(this);

        parent.add(b, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        try {
            if (cmd.matches("\\d")) {
                appendDigit(cmd);
                return;
            }

            switch (cmd) {
                case ".":
                    appendDot();
                    return;
                case "AC":
                    clear();
                    return;
                case "⌫":
                    backspace();
                    return;
                case "=":
                    evaluate();
                    return;
                case "%":
                    double v = getDisplayValue();
                    setDisplay(v / 100.0);
                    startNew = true;
                    setStatus("Percent");
                    return;
                case "+":
                case "-":
                case "*":
                case "/":
                    setOperator(cmd);
                    return;
                default:
                    // ignore
            }
        } catch (Exception ex) {
            error("Invalid input");
        }
    }

    private void appendDigit(String d) {
        if (display.getText().equals("Error")) clear();

        if (startNew) {
            display.setText(d);
            startNew = false;
        } else {
            String t = display.getText();
            display.setText(t.equals("0") ? d : t + d);
        }
        setStatus(" ");
    }

    private void appendDot() {
        if (display.getText().equals("Error")) clear();

        if (startNew) {
            display.setText("0.");
            startNew = false;
            return;
        }
        if (!display.getText().contains(".")) {
            display.setText(display.getText() + ".");
        }
    }

    private void clear() {
        first = 0.0;
        op = null;
        startNew = true;
        display.setText("0");
        setStatus("Cleared");
    }

    private void backspace() {
        if (startNew) return;
        String t = display.getText();
        if (t.equals("Error")) { clear(); return; }

        if (t.length() <= 1 || (t.length() == 2 && t.startsWith("-"))) {
            display.setText("0");
            startNew = true;
        } else {
            display.setText(t.substring(0, t.length() - 1));
        }
    }

    private void setOperator(String newOp) {
        double current = getDisplayValue();

        if (op == null) {
            first = current;
        } else if (!startNew) {
            first = compute(first, current, op);
            setDisplay(first);
        }
        op = newOp;
        startNew = true;
        setStatus("Op: " + op);
    }

    private void evaluate() {
        if (op == null) return;

        double second = getDisplayValue();
        double r = compute(first, second, op);

        setDisplay(r);
        setStatus(trim(first) + " " + op + " " + trim(second) + " =");

        first = r;
        op = null;
        startNew = true;
    }

    private double getDisplayValue() {
        String t = display.getText();
        if (t == null || t.isEmpty() || t.equals("Error")) return 0.0;
        return Double.parseDouble(t);
    }

    private void setDisplay(double v) {
        display.setText(trim(v));
    }

    private void error(String msg) {
        display.setText("Error");
        setStatus(msg);
        first = 0.0;
        op = null;
        startNew = true;
    }

    private void setStatus(String msg) {
        status.setText(msg);
    }

    private static double compute(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0.0) throw new ArithmeticException("Divide by zero");
                return a / b;
            default: throw new IllegalArgumentException("Bad op");
        }
    }

    private static String trim(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "Error";
        String s = Double.toString(v);
        return s.endsWith(".0") ? s.substring(0, s.length() - 2) : s;
    }

    // Hover effect
    @Override public void mouseEntered(MouseEvent e) {
        if (e.getSource() instanceof Button b) {
            Color c = b.getBackground();
            b.setBackground(blend(c, Color.white, 0.10));
        }
    }

    @Override public void mouseExited(MouseEvent e) {
        if (e.getSource() instanceof Button b) {
            String label = b.getLabel();
            if (label.equals("AC") || label.equals("⌫")) b.setBackground(utilBg);
            else if (label.equals("=")) b.setBackground(eqBg);
            else if (label.equals("+") || label.equals("-") || label.equals("*") || label.equals("/") || label.equals("%")) b.setBackground(opBg);
            else b.setBackground(numBg);
        }
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}

    private static Color blend(Color a, Color b, double t) {
        int r = (int) Math.round(a.getRed() * (1 - t) + b.getRed() * t);
        int g = (int) Math.round(a.getGreen() * (1 - t) + b.getGreen() * t);
        int bl = (int) Math.round(a.getBlue() * (1 - t) + b.getBlue() * t);
        return new Color(clamp(r), clamp(g), clamp(bl));
    }

    private static int clamp(int x) { return Math.max(0, Math.min(255, x)); }

    // WindowListener
    @Override public void windowClosing(WindowEvent e) { dispose(); System.exit(0); }
    @Override public void windowOpened(WindowEvent e) {}
    @Override public void windowClosed(WindowEvent e) {}
    @Override public void windowIconified(WindowEvent e) {}
    @Override public void windowDeiconified(WindowEvent e) {}
    @Override public void windowActivated(WindowEvent e) {}
    @Override public void windowDeactivated(WindowEvent e) {}

    public static void main(String[] args) {
        new SimpleCalculatorAWT_Styled();
    }
}