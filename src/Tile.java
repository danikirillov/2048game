import java.awt.*;

public class Tile {
    int value;

    public Tile() {
        value = 0;
    }

    public Tile(int value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public Color getFontColor() {
        return value < 16 ? new Color(0x776e65) : new Color(0xf9f6f2);
    }

    public Color getTileColor() {
        if (value == 0) return new Color(0xcdc1b4);
        if (value == 2) return new Color(0xeee4da);
        if (value == 4) return new Color(0xede0c8);
        if (value == 8) return new Color(0xf2b179);
        if (value == 16) return new Color(0xf59563);
        if (value == 32) return new Color(0xf67c5f);
        if (value == 64) return new Color(0xf65e3b);
        if (value == 128) return new Color(0xedcf72);
        if (value == 256) return new Color(0xedcc61);
        if (value == 512) return new Color(0xedc850);
        if (value == 1024) return new Color(0xedc53f);
        if (value == 2048) return new Color(0xedc22e);
        return new Color(0xff0000);
    }
}
