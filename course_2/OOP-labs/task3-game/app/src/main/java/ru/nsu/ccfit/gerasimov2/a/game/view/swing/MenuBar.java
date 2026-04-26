package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import ru.nsu.ccfit.gerasimov2.a.game.model.UserResult;
import ru.nsu.ccfit.gerasimov2.a.game.model.UserResultFileManager;

class NewGameItem extends JMenuItem {
    NewGameItem(SwingView view, ModelBox modelBox, String name) {
        super(name);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("New game started!");
                view.getGameArea().setVisible(true);
                modelBox.getModel().restart();
            }
        });
    }
}

class ExitItem extends JMenuItem {
    ExitItem(ModelBox modelBox, String name) {
        super(name);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}

class GameOverItem extends JMenuItem {
    GameOverItem(SwingView view, ModelBox modelBox, String name) {
        super(name);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = Dialogs.askUsername(null);
                if (username == null || username.isEmpty()) return;
                System.out.println("Saving results for " + username + ": Score = " + modelBox.getModel().getScore());

                try {
                    modelBox.getModel().saveUserResult(username);
                } catch (IOException ex) {
                    System.err.println("Cannot save result: " + ex.getLocalizedMessage());
                }
                modelBox.getModel().reset();
                view.getGameArea().setVisible(false);
            }
        });
    }
}

class LeaderboardItem extends JMenuItem {
    LeaderboardItem(ModelBox modelBox, String name) {
        super(name);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserResultFileManager loader = new UserResultFileManager();
                List<UserResult> results;
                try {
                    results = loader.load();
                    Dialogs.showLeaderboard(results);
                } catch (IOException e1) {
                    Dialogs.showWarning("Cannot load leaderboard: " + e1.getLocalizedMessage());
                }
            }
        });
    }
}

public class MenuBar extends JMenuBar {
    MenuBar(ModelBox modelBox, SwingView view) {
        setBackground(Color.BLACK);

        List<JMenuItem> menuItems = new ArrayList<>();
        // --------------------------Game menu---------------------------------
        JMenu gameMenu = new JMenu("Игра");
        add(gameMenu);
        menuItems.add(gameMenu);

        JMenuItem newGameItem = new NewGameItem(view, modelBox, "Новая игра!");
        gameMenu.add(newGameItem);
        menuItems.add(newGameItem);
        

        JMenuItem exitItem = new ExitItem(modelBox, "Выйти в windows");
        gameMenu.add(exitItem);
        menuItems.add(exitItem);
        

        JMenuItem gameOverItem = new GameOverItem(view, modelBox, "Закончить игру");
        gameMenu.add(gameOverItem);
        menuItems.add(gameOverItem);

        // ----------------------------Info menu-------------------------------
        JMenu infoMenu = new JMenu( "Инфо");
        add(infoMenu);
        menuItems.add(infoMenu);
        
        JMenuItem leaderboardItem = new LeaderboardItem(modelBox, "Таблица лидеров");
        infoMenu.add(leaderboardItem);
        menuItems.add(leaderboardItem);

        
        // --------------------Configure theme of items------------------------------
        Font font = new Font("Arial", Font.BOLD, 16);
        for (var item : menuItems) {
            item.setFont(font);
            item.setForeground(Color.WHITE);
            item.setBackground(Color.BLACK);
        }
        
    }
}
