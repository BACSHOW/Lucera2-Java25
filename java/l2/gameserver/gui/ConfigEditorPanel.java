package l2.gameserver.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Editor dos arquivos config/*.properties dentro da janela do servidor.
 *
 * <p>Cada opcao vira uma linha: True/False vira checkbox, o resto vira campo de texto (numeros sao conferidos).
 * O comentario acima da opcao no arquivo aparece como descricao. Ao salvar, so a linha da opcao alterada e
 * reescrita: comentarios, ordem, espacos e codificacao do arquivo ficam como estavam. Depois de salvar, as
 * configuracoes sao recarregadas (a acao de recarga e passada por quem usa o painel).</p>
 *
 * <p>A pasta e vigiada: arquivo novo, apagado ou editado por fora (Bloco de Notas) aparece sozinho na lista.
 * Um arquivo com alteracoes ainda nao salvas na janela nao e trocado; o aviso pede para recarregar.</p>
 *
 * <p>Opcoes que so valem depois de reiniciar (portas, banco...) ficam marcadas com "(reiniciar)".</p>
 */
public final class ConfigEditorPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ConfigEditorPanel.class);
    private static final Pattern NUMBER = Pattern.compile("-?\\d+(\\.\\d*)?");
    private static final Color CHANGED_BG = new Color(0x2a, 0x3a, 0x52);
    private static final Color INVALID_BG = new Color(0x52, 0x2a, 0x2a);

    private final File dir;
    private final Runnable reloadAction;
    private final String reloadNote;
    private final DefaultListModel<PropFile> fileModel = new DefaultListModel<>();
    private final JList<PropFile> fileList = new JList<>(this.fileModel);
    private final JTextField search = new JTextField(22);
    private final RowsPanel rows = new RowsPanel();
    private final JScrollPane rowsScroll = new JScrollPane(this.rows);
    private final JLabel info = new JLabel(" ");
    private final JButton save = button("Salvar e recarregar", e -> this.saveAll());
    private final JButton discard = button("Descartar", e -> this.discardAll());
    private final Timer searchDebounce = new Timer(200, e -> this.applyFilter());
    private final Timer diskDebounce = new Timer(700, e -> this.refreshFromDisk());
    private final BiPredicate<String, String> restartOnly;
    private final List<PropFile> files = new ArrayList<>();
    private boolean busy;

    /**
     * @param dir          pasta dos .properties (config)
     * @param reloadAction recarrega as configuracoes depois de salvar (roda fora da tela)
     * @param reloadNote   aviso mostrado depois de salvar (ex.: o que so vale apos reiniciar)
     */
    public ConfigEditorPanel(File dir, Runnable reloadAction, String reloadNote) {
        this(dir, reloadAction, reloadNote, null);
    }

    /**
     * @param restartOnly (nome do arquivo, opcao) -> true quando a opcao so vale depois de reiniciar; null = nenhuma
     */
    public ConfigEditorPanel(File dir, Runnable reloadAction, String reloadNote, BiPredicate<String, String> restartOnly) {
        super(new BorderLayout(0, 6));
        this.dir = dir;
        this.restartOnly = restartOnly;
        this.reloadAction = reloadAction;
        this.reloadNote = reloadNote;
        this.setBackground(Theme.BG);
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        top.setOpaque(false);
        JLabel l = new JLabel("Buscar:");
        this.search.setToolTipText("Procura pelo nome ou pela descrição da opção, em todos os arquivos");
        l.setForeground(Theme.TEXT);
        top.add(l);
        top.add(this.search);
        this.search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                ConfigEditorPanel.this.searchDebounce.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                ConfigEditorPanel.this.searchDebounce.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                ConfigEditorPanel.this.searchDebounce.restart();
            }
        });
        this.searchDebounce.setRepeats(false);
        top.add(this.save);
        top.add(this.discard);
        top.add(button("Recarregar do disco", e -> this.reloadFromDisk()));
        top.add(button("Bloco de Notas", e -> this.openCurrent()));
        top.add(button("Abrir pasta", e -> this.openFolder()));

        this.fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.fileList.setBackground(Theme.PANEL);
        this.fileList.setForeground(Theme.TEXT);
        this.fileList.setCellRenderer(new FileRenderer());
        this.fileList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                this.showRows();
            }
        });
        JScrollPane fileScroll = new JScrollPane(this.fileList);
        fileScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        fileScroll.setMinimumSize(new Dimension(180, 100));

        this.rows.setLayout(new BoxLayout(this.rows, BoxLayout.Y_AXIS));
        this.rows.setBackground(Theme.BG);
        this.rowsScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        this.rowsScroll.getViewport().setBackground(Theme.BG);
        this.rowsScroll.getVerticalScrollBar().setUnitIncrement(24);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileScroll, this.rowsScroll);
        split.setDividerLocation(230);
        split.setBorder(null);
        split.setBackground(Theme.BG);

        this.info.setForeground(Theme.MUTED);
        this.add(top, BorderLayout.NORTH);
        this.add(split, BorderLayout.CENTER);
        this.add(this.info, BorderLayout.SOUTH);

        this.loadFiles();
        this.diskDebounce.setRepeats(false);
        this.startWatcher();
    }

    /** Mostra um arquivo ja filtrado (ex.: o atalho "Editar rates" abre server.properties com "Rate"). */
    public void showFile(String fileName, String filter) {
        this.search.setText(filter == null ? "" : filter);
        this.applyFilter();
        for (int i = 0; i < this.fileModel.size(); i++) {
            if (this.fileModel.get(i).file.getName().equalsIgnoreCase(fileName)) {
                this.fileList.setSelectedIndex(i);
                this.fileList.ensureIndexIsVisible(i);
                return;
            }
        }
    }

    // ------------------------------------------------------------------ arquivos

    /** Subpasta com as configs dos servicos (Monster Data, Correio, Leilao, AutoFarm, Random Craft, Battle Pass). */
    private File customDir() {
        return new File(this.dir, "custom");
    }

    /** Nome mostrado na lista: os da subpasta custom aparecem como "custom/arquivo.properties". */
    private static String label(File f) {
        File parent = f.getParentFile();
        return parent != null && parent.getName().equalsIgnoreCase("custom") ? "custom/" + f.getName() : f.getName();
    }

    /** Os .properties da pasta e da subpasta custom: server/authserver primeiro, o resto em ordem alfabetica. */
    private File[] listPropertyFiles() {
        FileFilter props = f -> f.isFile() && f.getName().toLowerCase(Locale.ROOT).endsWith(".properties");
        File[] top = this.dir.listFiles(props);
        File[] custom = this.customDir().listFiles(props);
        int nTop = top == null ? 0 : top.length;
        int nCustom = custom == null ? 0 : custom.length;
        if (nTop + nCustom == 0) {
            return new File[0];
        }
        File[] list = new File[nTop + nCustom];
        if (nTop > 0) {
            System.arraycopy(top, 0, list, 0, nTop);
        }
        if (nCustom > 0) {
            System.arraycopy(custom, 0, list, nTop, nCustom);
        }
        Arrays.sort(list, (a, b) -> {
            int pa = a.getName().matches("(?i)(server|authserver)\\.properties") ? 0 : 1;
            int pb = b.getName().matches("(?i)(server|authserver)\\.properties") ? 0 : 1;
            return pa != pb ? pa - pb : label(a).compareToIgnoreCase(label(b));
        });
        return list;
    }

    private void loadFiles() {
        this.files.clear();
        for (File f : this.listPropertyFiles()) {
            try {
                this.files.add(PropFile.read(f));
            } catch (IOException e) {
                LOG.warn("Editor de configs: nao foi possivel ler " + f, e);
            }
        }
        this.applyFilter();
        if (this.fileList.getSelectedIndex() < 0 && !this.fileModel.isEmpty()) {
            this.fileList.setSelectedIndex(0);
        }
        this.updateButtons();
    }

    /** Vigia a pasta (sem ficar consultando: o sistema avisa quando algo muda). */
    private void startWatcher() {
        Thread t = new Thread(() -> {
            try (WatchService ws = FileSystems.getDefault().newWatchService()) {
                this.dir.toPath().register(ws, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_DELETE);
                if (this.customDir().isDirectory()) {
                    this.customDir().toPath().register(ws, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
                            StandardWatchEventKinds.ENTRY_DELETE);
                }
                while (true) {
                    WatchKey key = ws.take();
                    boolean relevant = false;
                    for (WatchEvent<?> ev : key.pollEvents()) {
                        Object name = ev.context();
                        relevant |= ev.kind() == StandardWatchEventKinds.OVERFLOW
                                || name != null && name.toString().toLowerCase(Locale.ROOT).endsWith(".properties");
                    }
                    if (!key.reset()) {
                        return;
                    }
                    if (relevant) {
                        SwingUtilities.invokeLater(this.diskDebounce::restart);
                    }
                }
            } catch (InterruptedException | ClosedWatchServiceException e) {
                // janela fechando
            } catch (IOException e) {
                LOG.warn("Editor de configs: nao foi possivel vigiar " + this.dir + "; use \"Recarregar do disco\".", e);
            }
        }, "ConfigEditor-watch");
        t.setDaemon(true);
        t.start();
    }

    /**
     * Algo mudou na pasta: rele so os arquivos que mudaram. Arquivo com alteracoes nao salvas na janela fica como
     * esta (o salvar dele e bloqueado ate recarregar), para nao perder o que foi digitado.
     */
    private void refreshFromDisk() {
        if (this.busy) {
            return;
        }
        Map<String, PropFile> old = new HashMap<>();
        for (PropFile pf : this.files) {
            old.put(pf.file.getName(), pf);
        }
        PropFile current = this.fileList.getSelectedValue();
        List<PropFile> next = new ArrayList<>();
        List<String> updated = new ArrayList<>();
        List<String> conflicts = new ArrayList<>();
        boolean currentReplaced = false;
        for (File f : this.listPropertyFiles()) {
            PropFile o = old.remove(f.getName());
            if (o != null && f.lastModified() == o.lastModified) {
                next.add(o);
                continue;
            }
            if (o != null && o.changes() > 0) {
                next.add(o);
                conflicts.add(f.getName());
                continue;
            }
            try {
                next.add(PropFile.read(f));
                updated.add(f.getName() + (o == null ? " (novo)" : ""));
                currentReplaced |= o != null && o == current;
            } catch (IOException e) {
                if (o != null) {
                    next.add(o);
                }
            }
        }
        for (String removed : old.keySet()) {
            updated.add(removed + " (apagado)");
            currentReplaced |= current != null && current.file.getName().equals(removed);
        }
        if (updated.isEmpty() && conflicts.isEmpty()) {
            return;
        }
        int scroll = this.rowsScroll.getVerticalScrollBar().getValue();
        this.files.clear();
        this.files.addAll(next);
        this.applyFilter();
        if (!currentReplaced) {
            SwingUtilities.invokeLater(() -> this.rowsScroll.getVerticalScrollBar().setValue(scroll));
        }
        this.updateButtons();
        if (!conflicts.isEmpty()) {
            this.info.setForeground(Theme.WARN);
            this.info.setText("Alterado fora da janela: " + String.join(", ", conflicts)
                    + ". Você tem alterações não salvas nele(s): salve não será possível até usar \"Recarregar do disco\".");
        } else {
            this.info.setForeground(Theme.OK);
            this.info.setText("Atualizado do disco: " + String.join(", ", updated) + ".");
        }
    }

    private void applyFilter() {
        String q = this.search.getText().trim().toLowerCase(Locale.ROOT);
        PropFile selected = this.fileList.getSelectedValue();
        this.fileModel.clear();
        for (PropFile pf : this.files) {
            pf.matches = q.isEmpty() ? -1 : (int) pf.entries.stream().filter(en -> en.matches(q)).count();
            if (q.isEmpty() || pf.matches > 0) {
                this.fileModel.addElement(pf);
            }
        }
        int idx = -1;
        for (int i = 0; selected != null && i < this.fileModel.size(); i++) {
            if (this.fileModel.get(i).file.getName().equals(selected.file.getName())) {
                idx = i;
                break;
            }
        }
        if (idx < 0 && !this.fileModel.isEmpty()) {
            idx = 0;
        }
        if (idx >= 0) {
            this.fileList.setSelectedIndex(idx);
        }
        this.showRows();
    }

    private void showRows() {
        this.rows.removeAll();
        PropFile pf = this.fileList.getSelectedValue();
        String q = this.search.getText().trim().toLowerCase(Locale.ROOT);
        if (pf != null) {
            for (Entry en : pf.entries) {
                if (q.isEmpty() || en.matches(q)) {
                    this.rows.add(this.row(pf, en));
                }
            }
            if (pf.entries.isEmpty()) {
                this.rows.add(muted("Nenhuma opção neste arquivo."));
            }
        } else {
            this.rows.add(muted(q.isEmpty() ? "Nenhum arquivo .properties em " + this.dir.getAbsolutePath() : "Nada encontrado para \"" + q + "\"."));
        }
        this.rows.add(Box.createVerticalGlue());
        this.rows.revalidate();
        this.rows.repaint();
        SwingUtilities.invokeLater(() -> this.rowsScroll.getVerticalScrollBar().setValue(0));
    }

    private JComponent row(PropFile pf, Entry en) {
        JPanel r = new JPanel(new BorderLayout(10, 0));
        r.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        JPanel texts = new JPanel(new BorderLayout(0, 1));
        texts.setOpaque(false);
        boolean restart = this.isRestartOnly(pf, en);
        JLabel key = new JLabel(restart ? "<html>" + escape(en.key) + " &nbsp;<font color='#e3a03a'>(reiniciar)</font></html>" : en.key);
        key.setForeground(Theme.TEXT);
        key.setFont(key.getFont().deriveFont(Font.BOLD));
        texts.add(key, BorderLayout.NORTH);
        if (!en.comment.isEmpty()) {
            JLabel c = new JLabel(firstLine(en.comment));
            c.setForeground(Theme.MUTED);
            c.setFont(c.getFont().deriveFont(11f));
            texts.add(c, BorderLayout.CENTER);
        }
        String tip = "<html><b>" + escape(en.key) + "</b> = " + escape(en.original)
                + (restart ? "<br><font color='#e3a03a'>Só vale depois de reiniciar o servidor.</font>" : "")
                + (en.comment.isEmpty() ? "" : "<br>" + escape(en.comment).replace("\n", "<br>")) + "</html>";
        r.setToolTipText(tip);
        texts.setToolTipText(tip);
        r.add(texts, BorderLayout.CENTER);

        JComponent editor;
        if (en.bool) {
            JCheckBox cb = new JCheckBox(en.value.equalsIgnoreCase("true") ? "Ligado" : "Desligado", en.value.equalsIgnoreCase("true"));
            cb.setOpaque(false);
            cb.setForeground(Theme.TEXT);
            cb.setEnabled(!en.readOnly);
            cb.addActionListener(e -> {
                en.value = en.boolText(cb.isSelected());
                cb.setText(cb.isSelected() ? "Ligado" : "Desligado");
                this.paintRow(r, en);
                this.changed(pf);
            });
            editor = cb;
        } else {
            JTextField tf = new JTextField(en.value, 18);
            tf.setBackground(Theme.PANEL);
            tf.setForeground(Theme.TEXT);
            tf.setCaretColor(Theme.TEXT);
            tf.setEditable(!en.readOnly);
            tf.setToolTipText(en.readOnly ? "Valor em varias linhas: edite pelo Bloco de Notas" : tip);
            tf.getDocument().addDocumentListener(new DocumentListener() {
                private void update() {
                    en.value = tf.getText();
                    ConfigEditorPanel.this.paintRow(r, en);
                    ConfigEditorPanel.this.changed(pf);
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    this.update();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    this.update();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    this.update();
                }
            });
            editor = tf;
        }
        JPanel east = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        east.setOpaque(false);
        east.add(editor);
        r.add(east, BorderLayout.EAST);
        this.paintRow(r, en);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, r.getPreferredSize().height));
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
        return r;
    }

    private boolean isRestartOnly(PropFile pf, Entry en) {
        try {
            return this.restartOnly != null && this.restartOnly.test(pf.file.getName(), en.key);
        } catch (RuntimeException e) {
            return false;
        }
    }

    private void paintRow(JPanel r, Entry en) {
        r.setOpaque(true);
        r.setBackground(!en.isValid() ? INVALID_BG : en.isChanged() ? CHANGED_BG : Theme.BG);
    }

    private void changed(PropFile pf) {
        this.fileList.repaint();
        this.updateButtons();
    }

    private void updateButtons() {
        int changes = 0;
        boolean invalid = false;
        for (PropFile pf : this.files) {
            changes += pf.changes();
            invalid |= pf.hasInvalid();
        }
        this.save.setEnabled(!this.busy && changes > 0 && !invalid);
        this.discard.setEnabled(!this.busy && changes > 0);
        this.save.setText(changes > 0 ? "Salvar e recarregar (" + changes + ")" : "Salvar e recarregar");
        if (!this.busy) {
            this.info.setText(invalid ? "Há valores inválidos (em vermelho): corrija antes de salvar."
                    : changes > 0 ? changes + " alteração(ões) não salvas (em azul). Passe o mouse sobre uma opção para ver a descrição completa."
                    : "Passe o mouse sobre uma opção para ver a descrição completa. " + this.reloadNote);
            this.info.setForeground(invalid ? Theme.ERROR : changes > 0 ? Theme.WARN : Theme.MUTED);
        }
    }

    // ------------------------------------------------------------------ acoes

    private void saveAll() {
        List<PropFile> dirty = new ArrayList<>();
        for (PropFile pf : this.files) {
            if (pf.changes() > 0) {
                dirty.add(pf);
            }
        }
        if (dirty.isEmpty()) {
            return;
        }
        StringBuilder sb = new StringBuilder("Salvar e recarregar as configurações?\n");
        for (PropFile pf : dirty) {
            for (Entry en : pf.entries) {
                if (en.isChanged()) {
                    sb.append("\n").append(pf.file.getName()).append(": ").append(en.key).append(" = ").append(en.original).append("  ->  ").append(en.value.trim());
                }
            }
        }
        if (JOptionPane.showConfirmDialog(this, sb.toString(), "Salvar configurações", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }
        for (PropFile pf : dirty) {
            if (pf.file.lastModified() != pf.lastModified) {
                JOptionPane.showMessageDialog(this, pf.file.getName() + " foi alterado fora da janela depois de aberto aqui.\n"
                        + "Use \"Recarregar do disco\" e refaça as alterações.", "Arquivo alterado", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        List<String> needRestart = new ArrayList<>();
        for (PropFile pf : dirty) {
            for (Entry en : pf.entries) {
                if (en.isChanged() && this.isRestartOnly(pf, en)) {
                    needRestart.add(en.key);
                }
            }
        }
        this.busy = true;
        this.updateButtons();
        this.info.setForeground(Theme.MUTED);
        this.info.setText("Salvando e recarregando...");
        Thread t = new Thread(() -> {
            String error = null;
            try {
                for (PropFile pf : dirty) {
                    pf.write();
                    for (Entry en : pf.entries) {
                        if (en.isChanged()) {
                            LOG.info("Config alterada pela janela: " + pf.file.getName() + " " + en.key + " = " + en.value.trim() + " (antes: " + en.original + ")");
                        }
                    }
                }
                if (this.reloadAction != null) {
                    this.reloadAction.run();
                }
            } catch (Throwable e) {
                LOG.warn("Editor de configs: falha ao salvar/recarregar", e);
                error = e.toString();
            }
            String err = error;
            SwingUtilities.invokeLater(() -> {
                this.busy = false;
                this.loadFiles();
                if (err != null) {
                    this.info.setForeground(Theme.ERROR);
                    this.info.setText("Falha: " + err);
                    JOptionPane.showMessageDialog(this, "Falha ao salvar/recarregar:\n" + err, "Erro", JOptionPane.ERROR_MESSAGE);
                } else if (!needRestart.isEmpty()) {
                    this.info.setForeground(Theme.WARN);
                    this.info.setText("Salvo e recarregado. Só valem depois de reiniciar: " + String.join(", ", needRestart) + ".");
                } else {
                    this.info.setForeground(Theme.OK);
                    this.info.setText("Salvo e recarregado. Tudo já está valendo.");
                }
            });
        }, "ConfigEditor-save");
        t.setDaemon(true);
        t.start();
    }

    private void discardAll() {
        if (JOptionPane.showConfirmDialog(this, "Descartar todas as alterações não salvas?", "Descartar",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }
        for (PropFile pf : this.files) {
            for (Entry en : pf.entries) {
                en.value = en.original;
            }
        }
        this.fileList.repaint();
        this.showRows();
        this.updateButtons();
    }

    private void reloadFromDisk() {
        boolean dirty = this.files.stream().anyMatch(pf -> pf.changes() > 0);
        if (dirty && JOptionPane.showConfirmDialog(this, "Recarregar os arquivos do disco e perder as alterações não salvas?",
                "Recarregar do disco", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }
        this.loadFiles();
    }

    private void openCurrent() {
        PropFile pf = this.fileList.getSelectedValue();
        if (pf == null) {
            return;
        }
        try {
            if (System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
                new ProcessBuilder("notepad.exe", pf.file.getAbsolutePath()).start();
            } else {
                Desktop.getDesktop().open(pf.file);
            }
            this.info.setForeground(Theme.MUTED);
            this.info.setText("Depois de editar no Bloco de Notas e salvar, clique em \"Recarregar do disco\" e use o botão de recarregar configs.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Não foi possível abrir:\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openFolder() {
        try {
            Desktop.getDesktop().open(this.dir.getAbsoluteFile());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Não foi possível abrir a pasta:\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------------------------------------------------ util de tela

    private static JButton button(String text, ActionListener action) {
        JButton b = new JButton(text);
        b.setFocusable(false);
        b.addActionListener(action);
        return b;
    }

    private static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Theme.MUTED);
        l.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return l;
    }

    private static String firstLine(String s) {
        int nl = s.indexOf('\n');
        String f = nl < 0 ? s : s.substring(0, nl) + " ...";
        return f.length() > 140 ? f.substring(0, 137) + "..." : f;
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /** Linhas que acompanham a largura da tela (sem barra horizontal). */
    private static final class RowsPanel extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return this.getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
            return 24;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
            return r.height - 24;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private static final class FileRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
            super.getListCellRendererComponent(list, value, index, selected, focus);
            PropFile pf = (PropFile) value;
            int ch = pf.changes();
            this.setText(label(pf.file) + (pf.matches >= 0 ? "  (" + pf.matches + ")" : "") + (ch > 0 ? "  *" + ch : ""));
            this.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
            if (!selected) {
                this.setBackground(Theme.PANEL);
                this.setForeground(ch > 0 ? Theme.WARN : Theme.TEXT);
            }
            return this;
        }
    }

    // ------------------------------------------------------------------ arquivo .properties

    /** Uma opcao do arquivo: chave, valor atual, valor no disco, comentario acima e a linha onde esta. */
    static final class Entry {
        final String key;
        final String original;
        final String comment;
        final int line;
        final boolean bool;
        final boolean numeric;
        final boolean readOnly;
        String value;

        Entry(String key, String value, String comment, int line, boolean readOnly) {
            this.key = key;
            this.original = value;
            this.value = value;
            this.comment = comment;
            this.line = line;
            this.readOnly = readOnly;
            this.bool = value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
            this.numeric = NUMBER.matcher(value).matches();
        }

        boolean isChanged() {
            return !this.value.trim().equals(this.original);
        }

        boolean isValid() {
            return !this.numeric || NUMBER.matcher(this.value.trim()).matches();
        }

        boolean matches(String q) {
            return this.key.toLowerCase(Locale.ROOT).contains(q) || this.comment.toLowerCase(Locale.ROOT).contains(q);
        }

        /** Mantem o estilo do arquivo: True/False, true/false ou TRUE/FALSE. */
        String boolText(boolean on) {
            String t = on ? "true" : "false";
            if (this.original.equals(this.original.toUpperCase(Locale.ROOT))) {
                return t.toUpperCase(Locale.ROOT);
            }
            if (Character.isUpperCase(this.original.charAt(0))) {
                return Character.toUpperCase(t.charAt(0)) + t.substring(1);
            }
            return t;
        }
    }

    /** Arquivo lido linha a linha; ao gravar so as linhas das opcoes alteradas mudam. */
    static final class PropFile {
        final File file;
        final Charset charset;
        final String eol;
        final boolean endsWithEol;
        final boolean bom;
        final List<String> lines;
        final List<Entry> entries = new ArrayList<>();
        final long lastModified;
        int matches = -1;

        private PropFile(File file, Charset charset, String text, boolean bom) {
            this.file = file;
            this.charset = charset;
            this.bom = bom;
            this.lastModified = file.lastModified();
            this.eol = text.contains("\r\n") ? "\r\n" : "\n";
            this.endsWithEol = text.endsWith("\n");
            this.lines = new ArrayList<>(Arrays.asList(text.split("\r?\n", -1)));
            if (this.endsWithEol) {
                this.lines.remove(this.lines.size() - 1);
            }
            this.parse();
        }

        static PropFile read(File f) throws IOException {
            byte[] bytes = Files.readAllBytes(f.toPath());
            Charset cs;
            String text;
            try {
                // UTF-8 se for valido; senao ISO-8859-1 (le e grava os bytes sem perder nada)
                text = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
                        .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(bytes)).toString();
                cs = StandardCharsets.UTF_8;
            } catch (CharacterCodingException e) {
                text = new String(bytes, StandardCharsets.ISO_8859_1);
                cs = StandardCharsets.ISO_8859_1;
            }
            boolean bom = text.startsWith("\uFEFF");
            return new PropFile(f, cs, bom ? text.substring(1) : text, bom);
        }

        private void parse() {
            StringBuilder comment = new StringBuilder();
            for (int i = 0; i < this.lines.size(); i++) {
                String raw = this.lines.get(i);
                String t = raw.trim();
                if (t.isEmpty()) {
                    comment.setLength(0);
                    continue;
                }
                if (t.startsWith("#") || t.startsWith("!")) {
                    String c = t.replaceFirst("^[#!]+", "").trim();
                    if (!c.isEmpty() && !c.matches("[-=#*_~ ]+")) {
                        if (comment.length() > 0) {
                            comment.append('\n');
                        }
                        comment.append(c);
                    }
                    continue;
                }
                int eq = raw.indexOf('=');
                if (eq <= 0) {
                    comment.setLength(0);
                    continue;
                }
                String key = raw.substring(0, eq).trim();
                String value = raw.substring(eq + 1).trim();
                boolean multiLine = value.endsWith("\\");
                this.entries.add(new Entry(key, value, comment.toString(), i, multiLine));
                comment.setLength(0);
            }
        }

        int changes() {
            int n = 0;
            for (Entry en : this.entries) {
                if (en.isChanged()) {
                    n++;
                }
            }
            return n;
        }

        boolean hasInvalid() {
            for (Entry en : this.entries) {
                if (!en.isValid()) {
                    return true;
                }
            }
            return false;
        }

        /** Troca so o valor das linhas alteradas, mantendo a chave e o espacamento originais. */
        void write() throws IOException {
            List<String> out = new ArrayList<>(this.lines);
            for (Entry en : this.entries) {
                if (!en.isChanged()) {
                    continue;
                }
                String raw = out.get(en.line);
                int eq = raw.indexOf('=');
                int v = eq + 1;
                while (v < raw.length() && (raw.charAt(v) == ' ' || raw.charAt(v) == '\t')) {
                    v++;
                }
                out.set(en.line, raw.substring(0, v) + en.value.trim());
            }
            String text = (this.bom ? "\uFEFF" : "") + String.join(this.eol, out) + (this.endsWithEol ? this.eol : "");
            Files.write(this.file.toPath(), text.getBytes(this.charset));
        }

        @Override
        public String toString() {
            return this.file.getName();
        }
    }
}