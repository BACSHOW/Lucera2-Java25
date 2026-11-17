package l2.gameserver.gui;

import java.awt.Color;

/** Cores da janela do servidor (tema escuro, parecido com o console). */
public final class Theme {
    public static final Color BG = new Color(0x15, 0x18, 0x1d);
    public static final Color PANEL = new Color(0x1d, 0x21, 0x28);
    public static final Color BORDER = new Color(0x2c, 0x32, 0x3c);
    public static final Color GRID = new Color(0x2a, 0x2f, 0x38);
    public static final Color TEXT = new Color(0xe3, 0xe6, 0xea);
    public static final Color MUTED = new Color(0x8b, 0x94, 0xa1);
    public static final Color LOG = new Color(0xc9, 0xd1, 0xd9);
    public static final Color WARN = new Color(0xe3, 0xa0, 0x3a);
    public static final Color ERROR = new Color(0xf0, 0x6a, 0x6a);
    public static final Color OK = new Color(0x5f, 0xc0, 0x8a);
    public static final Color HIGHLIGHT = new Color(0x3d, 0x5a, 0x80);

    // series dos graficos
    public static final Color BLUE = new Color(0x5b, 0x9b, 0xf0);
    public static final Color ORANGE = new Color(0xf0, 0x9a, 0x4a);
    public static final Color GREEN = new Color(0x5f, 0xc0, 0x8a);
    public static final Color PURPLE = new Color(0xb4, 0x8c, 0xf0);
    public static final Color GRAY = new Color(0x9a, 0xa4, 0xb0);

    private Theme() {
    }
}
