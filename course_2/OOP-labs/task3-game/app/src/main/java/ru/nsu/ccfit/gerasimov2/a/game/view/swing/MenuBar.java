package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import ru.nsu.ccfit.gerasimov2.a.game.model.GameModel;


class NewGameItem extends JMenuItem {
    NewGameItem(Context ctx, String name) {
        super(name);
        setForeground(Theme.WHITE);
        setBackground(Color.BLACK);
        setFont(ctx.font);
        addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("New game started!");
                    ctx.gameArea.setVisible(true);
                    ctx.model.reset();
                }
            }
        );
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
    GameOverItem(Context ctx, String name) {
        super(name);
        setForeground(Theme.WHITE);
        setBackground(Color.BLACK);
        setFont(ctx.font);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ctx.model.reset();
                //String userName = askUserName();
                //ctx.model.saveUserResult(userName);
                ctx.gameArea.setVisible(false);
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

public class MenuBar extends JMenuBar {
    MenuBar(GameArea gameArea, GameModel model) {
        setBackground(Color.BLACK);        
        Context ctx = new Context(gameArea, model, new Font("Arial", Font.BOLD, 16));
        JMenu gameMenu = new GameMenu(ctx, "Игра");
      
        JMenuItem newGameItem  = new NewGameItem(ctx, "Новая игра!");
        JMenuItem exitItem     = new ExitItem(ctx, "Выйти в windows");
        JMenuItem gameOverItem = new GameOverItem(ctx, "Закончить игру");
      
        gameMenu.add(newGameItem);
        gameMenu.add(exitItem);
        gameMenu.add(gameOverItem);
        
        add(gameMenu);
    }
}
