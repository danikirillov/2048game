import javax.swing.*;
import java.awt.*;

public class View extends JPanel {
    private static final Color BG_COLOR = new Color(0xbbada0);
    private static final String FONT_NAME = "Arial";
    private static final int TILE_SIZE = 96;
    private static final int TILE_MARGIN = 12;

    private Controller controller;

    boolean isGameWon = false;
    boolean isGameLost = false;

    public View(Controller controller) {
        setFocusable(true);
        this.controller = controller;
        addKeyListener(controller);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(BG_COLOR);
        g.fillRect(0, 0, this.getSize().width, this.getSize().height);

        drawTiles(g);

        g.drawString("Score: " + controller.getScore(), 140, 465);

        endGameCheck();
    }

    private void endGameCheck() {
        if (isGameWon)
            showMessage("You've won!");
        else if (isGameLost)
            showMessage("You've lost :(");
    }

    private void showMessage(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message));
    }

    private void drawTiles(Graphics g) {
        for (int x = 0; x < 4; ++x)
            for (int y = 0; y < 4; ++y)
                drawTile(g, x, y);
    }

    private void drawTile(Graphics g, int x, int y) {
        Tile tile = controller.getGameTiles()[y][x];

        Graphics2D graphics2D = ((Graphics2D) g);

        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.setColor(tile.getTileColor());
        graphics2D.fillRoundRect(offset(x), offset(y), TILE_SIZE, TILE_SIZE, 8, 8);

        graphics2D.setColor(tile.getFontColor());
        graphics2D.setFont(getTileFont(tile));
        drawTileValue(graphics2D, tile, offset(x), offset(y));
    }


    private static int offset(int arg) {
        return arg * (TILE_MARGIN + TILE_SIZE) + TILE_MARGIN;
    }

    private void drawTileValue(Graphics2D graphics2D, Tile tile, int offsetX, int offsetY) {
        if (tile.value != 0) {
            String value = String.valueOf(tile.value);
            final FontMetrics fm = getFontMetrics(getTileFont(tile));

            final int fontWidth = fm.stringWidth(value);
            final int fontHeight = -(int) fm.getLineMetrics(value, graphics2D).getBaselineOffsets()[2];

            graphics2D.drawString(value, offsetX + (TILE_SIZE - fontWidth) / 2, offsetY + TILE_SIZE - (TILE_SIZE - fontHeight) / 2 - 2);
        }
    }

    private static Font getTileFont(Tile tile) {
        final int fontSize = tile.value < 100 ? 36 : tile.value < 1000 ? 32 : 24;
        return new Font(FONT_NAME, Font.BOLD, fontSize);
    }
}
