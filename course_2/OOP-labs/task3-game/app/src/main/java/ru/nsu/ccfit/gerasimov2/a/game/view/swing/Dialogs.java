package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.awt.Component;
import java.util.List;

import javax.swing.JOptionPane;

import ru.nsu.ccfit.gerasimov2.a.game.model.UserResult;

public class Dialogs {
    public static String askUsername(Component parentComponent) {
        String username = JOptionPane.showInputDialog(
                parentComponent,        
                "Введите ваш никнейм:", 
                "Сохранить реузльтат",  
                JOptionPane.QUESTION_MESSAGE
        );
        return username;
    }

    public static void showLeaderboard(List<UserResult> results) {
        StringBuilder sb = new StringBuilder();
        results.forEach(ur -> sb.append(ur.name).append(": ").append(ur.score).append('\n'));        
        JOptionPane.showMessageDialog(null, sb, "Leaderboard", JOptionPane.INFORMATION_MESSAGE);   
    }

    public static void showWarning(String string) {
        JOptionPane.showMessageDialog(null, string, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
