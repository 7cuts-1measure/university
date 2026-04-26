package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import ru.nsu.ccfit.gerasimov2.a.game.model.GameModel;
import ru.nsu.ccfit.gerasimov2.a.game.model.UserResult;
import ru.nsu.ccfit.gerasimov2.a.game.model.UserResultFileManager;

class NewGameItem extends JMenuItem {
    NewGameItem(GameArea gameArea, Context ctx, String name) {
        super(name);
        setForeground(Theme.WHITE);
        setBackground(Color.BLACK);
        setFont(ctx.font);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("New game started!");
                gameArea.setVisible(true);
                ctx.model.restart();
            }
        });
    }
}

class ExitItem extends JMenuItem {
    ExitItem(Context ctx, String name) {
        super(name);
        setForeground(Theme.WHITE);
        setBackground(Color.BLACK);
        setFont(ctx.font);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}

class GameOverItem extends JMenuItem {
    GameOverItem(GameArea gameArea, Context ctx, String name) {
        super(name);
        setForeground(Theme.WHITE);
        setBackground(Color.BLACK);
        setFont(ctx.font);
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
                gameArea.setVisible(false);
            }
        });
    }
}

class GameMenu extends JMenu {
    GameMenu(Context ctx, String name) {
        super(name);
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        setFont(ctx.font);
    }
}

class InfoMenu extends JMenu {
    InfoMenu(Context ctx, String name) {
        super(name);
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        setFont(ctx.font);
    }
}

class LeaderboardItem extends JMenuItem {
    LeaderboardItem(Context ctx, String name) {
        super(name);
        setForeground(Theme.WHITE);
        setBackground(Color.BLACK);
        setFont(ctx.font);
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
    MenuBar(GameArea gameArea, GameModel model) {
        setBackground(Color.BLACK);
        Context ctx = new Context(gameArea, model, new Font("Arial", Font.BOLD, 16));
        // --------------------------Игра---------------------------------
        JMenu gameMenu = new GameMenu(ctx, "Игра");

        JMenuItem newGameItem = new NewGameItem(gameArea, ctx, "Новая игра!");
        JMenuItem exitItem = new ExitItem(ctx, "Выйти в windows");
        JMenuItem gameOverItem = new GameOverItem(gameArea, ctx, "Закончить игру");

        gameMenu.add(newGameItem);
        gameMenu.add(exitItem);
        gameMenu.add(gameOverItem);
        // ----------------------------Инфо-------------------------------
        JMenu infoMenu = new InfoMenu(ctx, "Инфо");
        JMenuItem leaderboardItem = new LeaderboardItem(ctx, "Таблица лидеров");

        infoMenu.add(leaderboardItem);
        // ------------------------------------------------------------------
        add(gameMenu);
        add(infoMenu);
    }
}
