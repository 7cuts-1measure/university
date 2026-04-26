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

import ru.nsu.ccfit.gerasimov2.a.game.model.GameModel;
import ru.nsu.ccfit.gerasimov2.a.game.model.UserResult;
import ru.nsu.ccfit.gerasimov2.a.game.model.UserResultFileManager;

class NewGameItem extends JMenuItem {
    NewGameItem(SwingView view, Context ctx, String name) {
        super(name);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("New game started!");
                view.getGameArea().setVisible(true);
                ctx.model.restart();
            }
        });
    }
}

class ExitItem extends JMenuItem {
    ExitItem(Context ctx, String name) {
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
    GameOverItem(SwingView view, Context ctx, String name) {
        super(name);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = Dialogs.askUsername(null);
                if (username == null || username.isEmpty()) return;
                System.out.println("Saving results for " + username + ": Score = " + ctx.model.getScore());

                try {
                    ctx.model.saveUserResult(username);
                } catch (IOException ex) {
                    System.err.println("Cannot save result: " + ex.getLocalizedMessage());
                }
                ctx.model.reset();
                view.getGameArea().setVisible(false);
            }
        });
    }
}

class LeaderboardItem extends JMenuItem {
    LeaderboardItem(Context ctx, String name) {
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
    MenuBar(GameModel model, SwingView view) {
        setBackground(Color.BLACK);
        Context ctx = new Context(model);

        List<JMenuItem> menuItems = new ArrayList<>();
        // --------------------------Game menu---------------------------------
        JMenu gameMenu = new JMenu("Игра");
        add(gameMenu);
        menuItems.add(gameMenu);

        JMenuItem newGameItem = new NewGameItem(view, ctx, "Новая игра!");
        gameMenu.add(newGameItem);
        menuItems.add(newGameItem);
        

        JMenuItem exitItem = new ExitItem(ctx, "Выйти в windows");
        gameMenu.add(exitItem);
        menuItems.add(exitItem);
        

        JMenuItem gameOverItem = new GameOverItem(view, ctx, "Закончить игру");
        gameMenu.add(gameOverItem);
        menuItems.add(gameOverItem);

        // ----------------------------Info menu-------------------------------
        JMenu infoMenu = new JMenu( "Инфо");
        add(infoMenu);
        menuItems.add(infoMenu);
        
        JMenuItem leaderboardItem = new LeaderboardItem(ctx, "Таблица лидеров");
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
