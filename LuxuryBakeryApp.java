import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

/**
 * LuxuryBakeryApp: Main JFrame class for the Luxury Bakery Designer.
 * Users can place bakery items (cakes, donuts, croissants, cupcakes) on
 * a canvas, switch between luxury themes, and drag items to reposition them.
 *
 * Swing components: JComboBox (theme selector), JButtons (item + clear),
 *                   JLabel (title + status).
 * 2D Graphics: BakeryPanel draws the bakery room, shelves, and all items.
 * Listeners: ActionListener on buttons/combo, MouseListener on BakeryPanel,
 *            KeyListener on JFrame (Delete = clear all).
 */
public class LuxuryBakeryApp extends JFrame {

    BakeryPanel bakeryPanel;
    private JLabel statusLabel;
    private String selectedItem = null; // currently selected item type

    // Theme color sets: [wall, floor, shelf, accent]
    private static final Color[][] THEMES = {
        // Pastel Dream
        { new Color(255, 228, 225), new Color(245, 210, 200),
          new Color(255, 192, 203), new Color(220, 150, 160) },
        // Modern Black & Gold
        { new Color(30, 30, 30), new Color(20, 20, 20),
          new Color(60, 50, 20), new Color(212, 175, 55) },
        // Cozy Cafe
        { new Color(210, 180, 140), new Color(180, 140, 100),
          new Color(139, 90, 43), new Color(80, 50, 20) }
    };
    private int themeIndex = 0;

    /**
     * Constructor: builds the full JFrame layout with title bar,
     * bakery canvas, and all Swing control components.
     */
    public LuxuryBakeryApp() {
        setTitle("Luxury Bakery Designer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        // Title label at the top
        JLabel titleLabel = new JLabel("✦  Luxury Bakery Designer  ✦", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(60, 30, 30));
        titleLabel.setForeground(new Color(255, 220, 170));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        bakeryPanel = new BakeryPanel(this);
        add(bakeryPanel, BorderLayout.CENTER);

        add(buildSidePanel(), BorderLayout.EAST);
        add(buildStatusBar(), BorderLayout.SOUTH);

        // KeyListener: Delete clears all items, Escape deselects tool
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    bakeryPanel.clearItems();
                    setStatus("Canvas cleared.");
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    selectedItem = null;
                    setStatus("Tool deselected. Click a button to choose an item.");
                }
            }
        });

        setVisible(true);
        setStatus("Welcome! Select an item and click the canvas to place it.");
    }
    private JPanel buildSidePanel() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(new Color(50, 28, 20));
        side.setBorder(BorderFactory.createEmptyBorder(18, 12, 18, 12));
        side.setPreferredSize(new Dimension(160, 0));

        // Section: Theme
        JLabel themeTitle = sectionLabel("— Theme —");
        side.add(themeTitle);
        side.add(Box.createVerticalStrut(6));

        String[] themeNames = {"Pastel Dream", "Modern Black & Gold", "Cozy Cafe"};
        JComboBox<String> themeBox = new JComboBox<>(themeNames);
        themeBox.setMaximumSize(new Dimension(145, 30));
        themeBox.setFont(new Font("SansSerif", Font.PLAIN, 12));
        themeBox.setBackground(new Color(80, 50, 35));
        themeBox.setForeground(new Color(255, 220, 170));
        themeBox.setFocusable(false);
        /**
         * ActionListener on JComboBox: switching the theme updates the
         * color arrays used in BakeryPanel's paintComponent, causing the
         * background, floor, shelves, and accents to all redraw.
         */
        themeBox.addActionListener(e -> {
            themeIndex = themeBox.getSelectedIndex();
            bakeryPanel.repaint();
            setStatus("Theme changed to: " + themeNames[themeIndex]);
            requestFocus();
        });
        side.add(themeBox);
        side.add(Box.createVerticalStrut(20));

        // Section: Add Items
        JLabel itemTitle = sectionLabel("— Add Items —");
        side.add(itemTitle);
        side.add(Box.createVerticalStrut(8));

        String[] items = {"Cake", "Cupcake", "Donut", "Croissant"};
        String[] emojis = {"🎂", "🧁", "🍩", "🥐"};
        Color[] btnColors = {
            new Color(200, 100, 120),
            new Color(180, 120, 160),
            new Color(160, 100, 80),
            new Color(140, 100, 60)
        };

        for (int i = 0; i < items.length; i++) {
            final String type = items[i];
            final String emoji = emojis[i];
            JButton btn = makeButton(emoji + " " + type, btnColors[i]);
            /**
             * ActionListener on each item button: sets the selectedItem
             * type so the next canvas click places that bakery item.
             */
            btn.addActionListener(e -> {
                selectedItem = type;
                setStatus("Selected: " + type + " — click the canvas to place it!");
                requestFocus();
            });
            side.add(btn);
            side.add(Box.createVerticalStrut(6));
        }

        side.add(Box.createVerticalStrut(16));
        side.add(sectionLabel("— Actions —"));
        side.add(Box.createVerticalStrut(8));

        // Clear button
        JButton clearBtn = makeButton("🗑 Clear All", new Color(100, 40, 40));
        /**
         * ActionListener on Clear button: removes all BakeryItem objects
         */
        clearBtn.addActionListener(e -> {
            bakeryPanel.clearItems();
            setStatus("Canvas cleared. Add some items!");
            requestFocus();
        });
        side.add(clearBtn);
        side.add(Box.createVerticalStrut(6));

        // Hint label
        side.add(Box.createVerticalStrut(16));
        JLabel hint = new JLabel("<html><center><font color='#aa8866' size='2'>"
            + "Drag items<br>to reposition<br><br>Del = clear all<br>Esc = deselect"
            + "</font></center></html>");
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(hint);

        return side;
    }

    /**
     * Builds a small status bar at the bottom of the window
     * to show the user helpful messages about what's happening.
     */
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        bar.setBackground(new Color(40, 20, 15));
        statusLabel = new JLabel("Ready.");
        statusLabel.setForeground(new Color(200, 170, 140));
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        bar.add(statusLabel);
        return bar;
    }

    /**
     * Creates a styled section heading label for the side panel.
     */
    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setForeground(new Color(212, 175, 55));
        lbl.setFont(new Font("Serif", Font.BOLD, 13));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(145, 20));
        return lbl;
    }

    /**
     * Creates a consistently styled side panel button with the given
     * label text and background color.
     */
    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(new Color(255, 240, 210));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(145, 32));
        return btn;
    }

    /** Updates the status bar message. */
    public void setStatus(String msg) { statusLabel.setText(msg); }

    /** Returns the currently selected item type string. */
    public String getSelectedItem() { return selectedItem; }

    /** Returns the current theme color array. */
    public Color[] getTheme() { return THEMES[themeIndex]; }
    /**
     * BakeryItem stores the type, position, and a random size/color
     * variation for one item placed on the bakery canvas.
     */
    static class BakeryItem {
        String type;
        int x, y;
        int size;
        Color mainColor, accentColor;
        /**
         * Constructs a BakeryItem of the given type at position (x, y)
         * with randomized size and color variation for variety.
         */
        public BakeryItem(String type, int x, int y) {
            this.type = type;
            this.x = x;
            this.y = y;
            Random r = new Random();
            this.size = 28 + r.nextInt(18);

            switch (type) {
                case "Cake" -> {
                    Color[] cakes = {new Color(255, 182, 193), new Color(200, 160, 220),
                                     new Color(255, 218, 185), new Color(152, 251, 152)};
                    mainColor = cakes[r.nextInt(cakes.length)];
                    accentColor = new Color(255, 100, 120);
                }
                case "Cupcake" -> {
                    mainColor = new Color(255, 200, 170);
                    accentColor = new Color(r.nextInt(100) + 155, r.nextInt(80), r.nextInt(100) + 100);
                }
                case "Donut" -> {
                    mainColor = new Color(210, 150, 90);
                    accentColor = new Color(r.nextInt(100) + 155, r.nextInt(100), r.nextInt(150));
                }
                case "Croissant" -> {
                    mainColor = new Color(210, 160, 80);
                    accentColor = new Color(170, 110, 40);
                }
                default -> { mainColor = Color.WHITE; accentColor = Color.GRAY; }
            }
        }
    }
    /**
     * BakeryPanel extends JPanel and draws the full bakery scene:
     * walls, floor, display counter, shelves, and all placed items.
     * It handles mouse clicks to place items and drag to reposition.
     */
    static class BakeryPanel extends JPanel {

        private final ArrayList<BakeryItem> items = new ArrayList<>();
        private final LuxuryBakeryApp app;
        private BakeryItem dragging = null;
        private int dragOffX, dragOffY;

        /**
         * Constructor: registers the MouseListener for placing items
         * and the MouseMotionListener for drag-to-reposition.
         */
        public BakeryPanel(LuxuryBakeryApp app) {
            this.app = app;
            setBackground(Color.WHITE);
            /**
             * MouseListener: on press, either starts dragging an existing
             * item or places a new item of the selected type at the click
             * position both cause meaningful graphical updates to the panel.
             */
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    // Check if clicking on an existing item to drag it
                    for (int i = items.size() - 1; i >= 0; i--) {
                        BakeryItem item = items.get(i);
                        int dx = e.getX() - item.x, dy = e.getY() - item.y;
                        if (dx * dx + dy * dy <= item.size * item.size) {
                            dragging = item;
                            dragOffX = dx; dragOffY = dy;
                            app.setStatus("Dragging " + item.type + " — release to drop.");
                            return;
                        }
                    }
                    // Place new item if one is selected
                    String type = app.getSelectedItem();
                    if (type != null) {
                        items.add(new BakeryItem(type, e.getX(), e.getY()));
                        repaint();
                        app.setStatus("Placed " + type + " at (" + e.getX() + ", " + e.getY() + ")");
                    } else {
                        app.setStatus("No item selected. Choose one from the panel.");
                    }
                    app.requestFocus();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (dragging != null) {
                        app.setStatus(dragging.type + " dropped.");
                        dragging = null;
                    }
                }
            });

            // MouseMotionListener for dragging items around the canvas
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (dragging != null) {
                        dragging.x = e.getX() - dragOffX;
                        dragging.y = e.getY() - dragOffY;
                        repaint();
                    }
                }
            });
        }
        /** Removes all items and repaints the canvas. */
        public void clearItems() { items.clear(); repaint(); }

        /**
         * paintComponent: draws the complete bakery scene layer by layer —
         * background, floor, window, counter, shelves, items, then overlay text.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                                RenderingHints.VALUE_RENDER_QUALITY);

            Color[] theme = app.getTheme();
            int w = getWidth(), h = getHeight();

            drawRoom(g2, w, h, theme);
            drawWindow(g2, w, theme);
            drawCounter(g2, w, h, theme);
            drawShelves(g2, w, h, theme);

            for (BakeryItem item : items) drawItem(g2, item);

            // Hint overlay if canvas is empty
            if (items.isEmpty()) {
                g2.setColor(new Color(theme[3].getRed(), theme[3].getGreen(),
                                      theme[3].getBlue(), 100));
                g2.setFont(new Font("Serif", Font.ITALIC, 16));
                String hint = "Select an item from the panel and click here to place it ✦";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(hint, (w - fm.stringWidth(hint)) / 2, h / 2);
            }
        }
        private void drawRoom(Graphics2D g2, int w, int h, Color[] theme) {
            // Wall
            g2.setColor(theme[0]);
            g2.fillRect(0, 0, w, (int)(h * 0.70));
            // Floor
            g2.setColor(theme[1]);
            g2.fillRect(0, (int)(h * 0.70), w, h);
            // Baseboard
            g2.setColor(theme[3]);
            g2.setStroke(new BasicStroke(3));
            g2.drawLine(0, (int)(h * 0.70), w, (int)(h * 0.70));
            g2.setStroke(new BasicStroke(1));
        }

        /**
         * Draws a decorative bakery window on the wall with a warm glow.
         */
        private void drawWindow(Graphics2D g2, int w, Color[] theme) {
            int wx = w / 2 - 70, wy = 18, ww = 140, wh = 100;
            // Window frame
            g2.setColor(theme[3]);
            g2.setStroke(new BasicStroke(4));
            g2.drawRoundRect(wx, wy, ww, wh, 12, 12);
            g2.setStroke(new BasicStroke(1));
            // Sky inside window
            g2.setColor(new Color(180, 210, 240, 180));
            g2.fillRoundRect(wx + 4, wy + 4, ww - 8, wh - 8, 8, 8);
            // Cross divider
            g2.setColor(theme[3]);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(wx + ww / 2, wy, wx + ww / 2, wy + wh);
            g2.drawLine(wx, wy + wh / 2, wx + ww, wy + wh / 2);
            g2.setStroke(new BasicStroke(1));
            // Bakery sign below window
            g2.setColor(theme[3]);
            g2.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 13));
            String sign = "✦ La Maison ✦";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(sign, w / 2 - fm.stringWidth(sign) / 2, wy + wh + 22);
        }

        /**
         * Draws the main display counter at the bottom of the canvas
         * with a thick top surface and darker front face.
         */
        private void drawCounter(Graphics2D g2, int w, int h, Color[] theme) {
            int cy = (int)(h * 0.68);
            // Counter front face
            g2.setColor(theme[2]);
            g2.fillRect(0, cy, w, 38);
            // Counter top surface
            g2.setColor(theme[2].brighter());
            g2.fillRect(0, cy - 10, w, 14);
            // Counter accent line
            g2.setColor(theme[3]);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRect(0, cy - 10, w, 48);
            g2.setStroke(new BasicStroke(1));
        }

        private void drawShelves(Graphics2D g2, int w, int h, Color[] theme) {
            int[] shelfYs = {(int)(h * 0.28), (int)(h * 0.48)};
            for (int sy : shelfYs) {
                // Shelf board
                g2.setColor(theme[2]);
                g2.fillRoundRect(30, sy, w - 60, 14, 4, 4);
                // Shelf top highlight
                g2.setColor(theme[2].brighter());
                g2.fillRoundRect(30, sy, w - 60, 5, 4, 4);
                // Shelf outline
                g2.setColor(theme[3]);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(30, sy, w - 60, 14, 4, 4);
                g2.setStroke(new BasicStroke(1));
                // Bracket supports
                for (int bx = 60; bx < w - 60; bx += 120) {
                    g2.setColor(theme[3]);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawLine(bx, sy + 14, bx - 10, sy + 38);
                    g2.drawLine(bx, sy + 14, bx + 10, sy + 38);
                    g2.setStroke(new BasicStroke(1));
                }
            }
        }
        private void drawItem(Graphics2D g2, BakeryItem item) {
            switch (item.type) {
                case "Cake"      -> drawCake(g2, item);
                case "Cupcake"   -> drawCupcake(g2, item);
                case "Donut"     -> drawDonut(g2, item);
                case "Croissant" -> drawCroissant(g2, item);
            }
        }

        private void drawCake(Graphics2D g2, BakeryItem b) {
            int x = b.x, y = b.y, s = b.size;
            // Bottom tier
            g2.setColor(b.mainColor.darker());
            g2.fillRoundRect(x - s, y, s * 2, s, 6, 6);
            g2.setColor(b.mainColor);
            g2.fillRoundRect(x - s, y - 4, s * 2, s, 6, 6);
            // Frosting layer
            g2.setColor(Color.WHITE);
            g2.fillRect(x - s + 2, y - 4, s * 2 - 4, 5);
            // Top tier
            g2.setColor(b.mainColor.darker());
            g2.fillRoundRect(x - s / 2 - 4, y - s - 4, s + 8, s - 4, 6, 6);
            g2.setColor(b.mainColor);
            g2.fillRoundRect(x - s / 2 - 4, y - s - 8, s + 8, s - 4, 6, 6);
            // Top frosting
            g2.setColor(Color.WHITE);
            g2.fillRect(x - s / 2, y - s - 8, s, 5);
            // Cherry
            g2.setColor(new Color(200, 40, 60));
            g2.fillOval(x - 5, y - s - 20, 10, 10);
            // Outline
            g2.setColor(b.accentColor);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(x - s, y - 4, s * 2, s, 6, 6);
            g2.drawRoundRect(x - s / 2 - 4, y - s - 8, s + 8, s - 4, 6, 6);
            g2.setStroke(new BasicStroke(1));
        }

        /**
         * Draws a cupcake 
         */
        private void drawCupcake(Graphics2D g2, BakeryItem b) {
            int x = b.x, y = b.y, s = b.size;
            // Paper cup (trapezoid)
            int[] cx = {x - s / 2, x + s / 2, x + s / 2 - 4, x - s / 2 + 4};
            int[] cy = {y, y, y - s + 6, y - s + 6};
            g2.setColor(new Color(230, 200, 170));
            g2.fillPolygon(cx, cy, 4);
            g2.setColor(new Color(180, 140, 100));
            g2.drawPolygon(cx, cy, 4);
            // Lines on cup
            g2.setColor(new Color(180, 140, 100, 120));
            for (int lx = x - s / 2 + 6; lx < x + s / 2 - 2; lx += 8)
                g2.drawLine(lx, y - s + 8, lx, y - 2);
            // Cake body
            g2.setColor(b.mainColor);
            g2.fillOval(x - s / 2 - 2, y - s, s + 4, s / 2 + 4);
            // Frosting swirl
            g2.setColor(b.accentColor);
            g2.fillOval(x - s / 2 + 2, y - s - s / 2 + 2, s - 4, s / 2 + 4);
            g2.setColor(b.accentColor.brighter());
            g2.fillOval(x - s / 4, y - s - s / 2, s / 2, s / 3);
            // Sprinkle dot on top
            g2.setColor(new Color(255, 220, 50));
            g2.fillOval(x - 3, y - s - s / 2 - 5, 6, 6);
            g2.setColor(b.accentColor.darker());
            g2.setStroke(new BasicStroke(1f));
            g2.drawOval(x - s / 2 + 2, y - s - s / 2 + 2, s - 4, s / 2 + 4);
            g2.setStroke(new BasicStroke(1));
        }

        /**
         * Draws a donut 
         */
        private void drawDonut(Graphics2D g2, BakeryItem b) {
            int x = b.x, y = b.y, s = b.size;
            // Donut body (outer ring)
            g2.setColor(b.mainColor);
            g2.fillOval(x - s, y - s / 2, s * 2, s);
            g2.setColor(b.accentColor);
            g2.fillOval(x - s + 4, y - s / 2 + 2, s * 2 - 8, s - 8);
            g2.setColor(getBackground());
            g2.fillOval(x - s / 3, y - s / 4, s * 2 / 3, s / 2);
            g2.setColor(b.mainColor.darker());
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x - s / 3, y - s / 4, s * 2 / 3, s / 2);
            // Sprinkles
            Random r = new Random(b.x + b.y);
            Color[] sprinkleColors = {Color.WHITE, new Color(255, 220, 50),
                                       new Color(100, 200, 255), new Color(255, 150, 200)};
            for (int i = 0; i < 7; i++) {
                double angle = r.nextDouble() * Math.PI * 2;
                int sx = (int)(x + (s * 0.55) * Math.cos(angle));
                int sy2 = (int)(y + (s * 0.28) * Math.sin(angle));
                g2.setColor(sprinkleColors[i % sprinkleColors.length]);
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(sx - 3, sy2, sx + 3, sy2);
            }
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(b.mainColor.darker());
            g2.drawOval(x - s, y - s / 2, s * 2, s);
            g2.setStroke(new BasicStroke(1));
        }

        /**
         * Draws a croissant 
         */
        private void drawCroissant(Graphics2D g2, BakeryItem b) {
            int x = b.x, y = b.y, s = b.size;
            
            g2.setColor(b.mainColor);
            Arc2D body = new Arc2D.Float(x - s, y - s / 2, s * 2, s, 20, 140, Arc2D.PIE);
            g2.fill(body);
            // Darker curved layers for flaky texture
            g2.setColor(b.accentColor);
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 1; i <= 4; i++) {
                Arc2D layer = new Arc2D.Float(x - s + i * 4, y - s / 2 + i * 2,
                                              s * 2 - i * 8, s - i * 3, 25, 130, Arc2D.OPEN);
                g2.draw(layer);
            }
            // Tips of the croissant
            g2.setColor(b.mainColor.darker());
            g2.fillOval(x - s + 4, y - 6, 14, 10);
            g2.fillOval(x + s - 18, y - 6, 14, 10);
            g2.setStroke(new BasicStroke(1));
            g2.setColor(b.accentColor.darker());
            g2.draw(body);
        }
    }

    /**
     * Main method: launches LuxuryBakeryApp on the Swing event thread.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LuxuryBakeryApp::new);
    }
}