package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import battle.Battle;
import pokemon.Pokemon;
import jpkmn.Driver;
import jpkmn.Player;

public class GameWindow extends JFrame {
  public Player player;

  private GodWindow gwin;
  private MessageWindow mwin;

  private BattleView bwin;
  private JPanel main = new JPanel();
  private JRootPane root;

  private static final long serialVersionUID = 1L;

  public GameWindow(Player p) {
    try {
      player = p;
      root = this.getRootPane();
      Tools.window = this;

      construct();

      if (Driver.god) {
        gwin = new GodWindow(player);
        gwin.setLocationRelativeTo(this);
      }
      if (Driver.message) {
        mwin = new MessageWindow();
        mwin.setLocationRelativeTo(this);
      }

      showMain();
      setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
      destruct();
    }
  }

  private void construct() {
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setLocationRelativeTo(null);
    root.setLayout(new FlowLayout());
    main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
    main.add(new BattleButton());
    main.add(new SaveButton());
    main.add(new QuitButton());
  }

  private void destruct() {
    if (gwin != null)
      gwin.dispose();
    if (mwin != null)
      mwin.dispose();
    super.dispose();
  }

  public void showMain() {
    setTitle("Main Menu");
    this.setSize(120, 120);
    root.removeAll();
    root.add(main);
  }

  public void showBattle(Battle b) {
    setTitle("Battle!");
    setSize(600, 250);
    root.removeAll();

    if (bwin == null)
      bwin = new BattleView(this, b);
    else
      bwin.load(b);

    root.add(bwin);
  }

  public void showMessage(Image icon, String title, String message) {
    mwin.addMessage(title, message);
  }

  private class BattleButton extends JButton implements ActionListener {

    public BattleButton() {
      super("Fight something");
      addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      Pokemon e = new Pokemon(Integer.parseInt(JOptionPane.showInputDialog(
          null, "LVL", "What level?")), Integer.parseInt(JOptionPane
          .showInputDialog(null, "NUM", "What number?")));

      Battle b = new Battle(player, e);
      showBattle(b);
    }

  }

  private class SaveButton extends JButton implements ActionListener {
    public SaveButton() {
      super("Save game");
      addActionListener(this);
    }

    public void actionPerformed(ActionEvent arg0) {
      JFileChooser fc = new JFileChooser(Driver.prefs.get("save_location", ""));
      File f;
      fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
      fc.addChoosableFileFilter(new FileNameExtensionFilter("JPokemon Files",
          "jpkmn"));
      if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        f = fc.getSelectedFile();

        try {
          // Make sure the file is of type .jpkmn
          if (!f.getName()
              .substring(f.getName().lastIndexOf('.'), f.getName().length())
              .equalsIgnoreCase(".jpkmn")) {
            gui.Tools.notify((Image) null, "ERROR", "not type .jpkmn");
          }

          // Store the default save location
          Driver.prefs.put("save_location", f.getAbsolutePath());
          Driver.prefs.flush();

          // Load game
          PrintWriter pw = new PrintWriter(f);

          player.toFile(pw);

          pw.close();

        } catch (Exception e) {
          e.printStackTrace();
          gui.Tools.notify((Image) null, "ERROR", "General Error");
        }
      } // End if
    }
  }

  private class QuitButton extends JButton implements ActionListener {
    public QuitButton() {
      super("Quit Game");
      addActionListener(this);
    }

    public void actionPerformed(ActionEvent arg0) {
      mwin.addMessage("title", "message");
      //destruct();
    }
  }

}