package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.*;

import item.Item;
import jpkmn.*;
import pokemon.Pokemon;

public class Console extends JTextField implements ActionListener {
  private Player player;

  public Console(Player p) {
    super("", 30);
    player = p;
    addActionListener(this);
  }

  private void handle(String s) {
    Driver.logConsoleEvent(s);
    ArrayList<String> string = parse(s);

    try {
      if (string.get(0).equals("pokemon")) {
        Pokemon target;
        int pos;
        boolean box = false;

        // 1-argument flavor
        if (string.size() == 1) {
          Tools.notify("err", "All Pokemon", player.party.getNameList());
          Driver.logConsoleEvent("Listing all pokemon\n"
              + player.party.getNameList());
          return;
        }

        // 2-argument flavor
        try {
          pos = Integer.parseInt(string.get(1));
        } catch (Exception e) {
          pos = Integer.parseInt(string.get(1).substring(3));
          box = true;
        }
        if (box) {
          target = player.box.get(pos);
        }
        else {
          target = player.party.pkmn[pos];
        }
        if (target == null) {
          Tools.notify("err", "CONSOLE ERROR", "Pokemon " + string.get(1)
              + " doesn't exist : " + s);
          Driver.logConsoleEvent("Console event failure. Pokemon "
              + string.get(1) + " doesn't exist : " + s);
          return;
        }
        if (string.size() == 2) {
          Tools.notify(target, "Pokemon Lookup", target.toString());
          Driver.logConsoleEvent("Looking up pokemon\n"+target.toString());
          return;
        }

        // x-argument flavor
        if (string.get(2).equals("heal")) {
          target.healDamage(target.health.max);
        }
        else if (string.get(2).equals("level")) {
          for (int levels = Integer.parseInt(string.get(3)); levels > 0; --levels)
            target.gainExperience(target.xpNeeded());
        }
        else if (string.get(2).equals("restore")) {
          target.resetTempStats();
          target.status.reset();
        }
        else if (string.get(2).equals("move")) {
          Tools.notify(target, "Pokemon Lookup", target.getMoveList());
          Driver.logConsoleEvent("Looking up move list\n"+target.getMoveList());
          return;
        }
        else if (string.get(2).equals("lead") && !box) {
          Pokemon swap = player.party.pkmn[0];
          player.party.pkmn[0] = target;
          player.party.pkmn[pos] = swap;
        }
        else {
          printError(s);
        }
      }

      else if (string.get(0).equals("bag")) {
        Item target;
        int pos;
        if (string.get(1).equals("potion")) {
          target = player.bag.potion(Integer.parseInt(string.get(2)));
          target.add(Integer.parseInt(string.get(3)));
        }
        else if (string.get(1).equals("ball")) {
          target = player.bag.ball(Integer.parseInt(string.get(2)));
          target.add(Integer.parseInt(string.get(3)));
        }
        else if (string.get(1).equals("stone")) {
          target = player.bag.stone(string.get(2));
          target.add(Integer.parseInt(string.get(3)));
        }
        else if (string.get(1).charAt(0) == 'x') {
          target = player.bag.xstat(string.get(1).substring(1));
          target.add(Integer.parseInt(string.get(2)));
        }
        else {
          printError(s);
        }
      }

      else if (string.get(0).equals("player")) {
        if (string.get(1).equals("cash")) {
          player.bag.cash += Integer.parseInt(string.get(2));
        }
        else if (string.get(1).equals("badge")) {
          player.badge += Integer.parseInt(string.get(2));
        }
        else {
          printError(s);
        }
      }

      else {
        printError(s);
      }

    } catch (Exception e) {
      printError(s);
    }
  }

  private void printError(String s) {
    Tools.notify("err", "CONSOLE ERROR", "Unknown command : " + s);
    Driver.logConsoleEvent("Console event failure. Unknown command: " + s);
  }

  private ArrayList<String> parse(String s) {
    if (s == null)
      return null;

    int length = s.length(), lastindex = 0;

    ArrayList<String> response = new ArrayList<String>();

    for (int i = 0; i < length; ++i) {
      if (s.charAt(i) == ' ') {
        response.add(s.substring(lastindex, i));
        lastindex = i + 1;
      }
      else if (i + 1 == length) {
        response.add(s.substring(lastindex));
      }
    }

    return response;
  }

  @Override
  public void actionPerformed(ActionEvent arg0) {
    String text = this.getText();
    handle(text);
    setText("");
  }

  private static final long serialVersionUID = 1L;
}
