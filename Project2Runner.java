import javax.swing.SwingUtilities;

public class Project2Runner {

    /*
     * Name: <your name> PO PO THIT
     * Student ID: <your id> 501391334
     *
     ******** Project Description ********
     *
     * This program is a Luxury Bakery Designer. The user sees a bakery room
     * with walls, a floor, shelves, a counter, and a window. You can place
     * four types of bakery items on the canvas: cakes, cupcakes, donuts, and
     * croissants. Just click a button to choose an item, then click the canvas
     * to place it. You can also drag items around to reposition them. There
     * are three color themes you can switch between using a dropdown menu:
     * Pastel Dream, Modern Black & Gold, and Cozy Cafe. Each theme changes
     * the colors of the whole room. A status bar at the bottom tells you
     * what is happening. Press Delete to clear everything or Escape to
     * deselect the current item.
     *
     *
     ******** Swing Requirement ********
     *
     * The program uses several Swing components. There is a JComboBox
     * (around line 90 in LuxuryBakeryApp.java) that lets the user pick a
     * color theme. There are four JButtons for choosing which item to place
     * (Cake, Cupcake, Donut, Croissant) starting around line 108, and one
     * more JButton to clear the canvas around line 124. There are also
     * JLabels used for the title at the top, the status bar at the bottom,
     * and the section headings in the side panel.
     *
     *
     ******** 2D Graphics Requirement ********
     *
     * The drawing happens inside BakeryPanel which extends JPanel
     * (around line 230 in LuxuryBakeryApp.java). The paintComponent method
     * draws the bakery room using colored rectangles for the walls and floor.
     * It draws shelves using rounded rectangles with bracket lines, and a
     * counter along the bottom. Each bakery item is drawn with 2D shapes:
     * cakes use layered rectangles with a cherry on top, cupcakes use a
     * polygon cup with oval frosting, donuts use circles with a hole and
     * sprinkle lines, and croissants use Arc2D curves for the crescent shape.
     *
     *
     ******** Event Listener Requirement ********
     *
     * There are multiple event listeners in the program. The JComboBox has
     * an ActionListener (around line 98) that changes the theme and redraws
     * the canvas when the user picks a new one. Each item button also has
     * an ActionListener (around line 113) that sets which item will be placed
     * next. The BakeryPanel has a MouseListener (around line 255) that places
     * a new item where the user clicks, or starts dragging an existing one.
     * A MouseMotionListener (around line 278) moves the item while dragging.
     * There is also a KeyListener on the JFrame (around line 62) that clears
     * all items when Delete is pressed and deselects the tool when Escape
     * is pressed.
     */

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LuxuryBakeryApp::new);
    }
}