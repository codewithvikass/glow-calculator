import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GlowCalculator extends JFrame implements ActionListener {
    private JTextField display;
    private JPanel panel;
    private String[] buttons = {
        "AC", "Del", "%", "/",
        "7", "8", "9", "*",
        "4", "5", "6", "-",
        "1", "2", "3", "+",
        "0", ".", "="
    };
    private Color bgColor = new Color(230, 210, 255);
    private Point mouseClickPoint;

    public GlowCalculator() {
        setUndecorated(true);
        setSize(320, 540);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(bgColor);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseClickPoint = e.getPoint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point currentPoint = e.getLocationOnScreen();
                setLocation(currentPoint.x - mouseClickPoint.x, currentPoint.y - mouseClickPoint.y);
            }
        });

        JButton closeBtn = new JButton("X") {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getModel().isRollover() ? new Color(255, 100, 100) : Color.RED);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 14));
                g.drawString("X", getWidth() / 2 - 5, getHeight() / 2 + 5);
            }
        };
        closeBtn.setBounds(280, 0, 40, 30);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setToolTipText("Close Calculator");
        closeBtn.addActionListener(e -> confirmClose());
        add(closeBtn);

        display = new JTextField("0");
        display.setBounds(20, 40, 280, 60);
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(new Color(210, 180, 255));
        display.setForeground(Color.BLACK);
        display.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        add(display);

        panel = new JPanel();
        panel.setBounds(20, 120, 280, 400);
        panel.setLayout(new GridLayout(5, 4, 10, 10));
        panel.setBackground(bgColor);
        add(panel);

        for (String text : buttons) {
            JButton btn = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    g.setColor(getModel().isPressed() ? new Color(150, 110, 255) : new Color(180, 140, 255));
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 20));
                    FontMetrics fm = g.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() + fm.getAscent()) / 2 - 4;
                    g.drawString(getText(), x, y);
                }
            };
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.addActionListener(this);
            panel.add(btn);
        }

        setupKeyBindings();

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void confirmClose() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Do you want to close this window?",
            "Confirm Close",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void setupKeyBindings() {
        InputMap im = display.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = display.getActionMap();

        // Bind digits 0-9
        for (int i = 0; i <= 9; i++) {
            final String digit = String.valueOf(i);
            im.put(KeyStroke.getKeyStroke(digit), digit);
            am.put(digit, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pressButton(digit);
                }
            });
        }

        // Decimal point
        im.put(KeyStroke.getKeyStroke('.'), ".");
        am.put(".", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pressButton(".");
            }
        });

        // Operators with their respective key strokes:

        // '+' -> Shift + '=' key
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.SHIFT_DOWN_MASK), "+");
        am.put("+", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pressButton("+");
            }
        });

        // '-' key
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "-");
        am.put("-", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pressButton("-");
            }
        });

        // '*' -> Shift + '8' key
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_8, InputEvent.SHIFT_DOWN_MASK), "*");
        am.put("*", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pressButton("*");
            }
        });

        // '/' key
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0), "/");
        am.put("/", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pressButton("/");
            }
        });

        // '%' -> Shift + '5' key
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_5, InputEvent.SHIFT_DOWN_MASK), "%");
        am.put("%", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pressButton("%");
            }
        });

        // Equals: '=' and Enter
        im.put(KeyStroke.getKeyStroke('='), "=");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "=");
        am.put("=", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pressButton("=");
            }
        });

        // Delete (Backspace)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "Del");
        am.put("Del", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pressButton("Del");
            }
        });

        // Escape key to clear (AC)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "AC");
        am.put("AC", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pressButton("AC");
            }
        });
    }

    private void pressButton(String command) {
        // Find and simulate button press
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (btn.getText().equals(command)) {
                    btn.doClick();
                    break;
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        String text = display.getText();

        switch (command) {
            case "AC":
                display.setText("0");
                break;
            case "Del":
                if (text.length() > 1)
                    display.setText(text.substring(0, text.length() - 1));
                else
                    display.setText("0");
                break;
            case "=":
                try {
                    double result = evaluate(text);
                    display.setText((result == (int) result) ? String.valueOf((int) result) : String.valueOf(result));
                } catch (Exception ex) {
                    display.setText("Error");
                }
                break;
            default:
                if (text.equals("0") || text.equals("Error"))
                    display.setText(command);
                else
                    display.setText(text + command);
        }
    }

    private double evaluate(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else if (eat('%')) {
                    // Just treat % as modulus operator (optional extension)
                    throw new RuntimeException("Unexpected: %"); 
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return x;
            }
        }.parse();
    }

    public static void main(String[] args) {
        new GlowCalculator();
    }
}
