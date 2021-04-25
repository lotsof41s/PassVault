package Controller;

import View.LoginView;

import javax.swing.JFrame;

/**
 *
 * @author jaredb
 */
public class StartupController {

    private final LoginView lView;

    public StartupController() {
        lView = new LoginView(new JFrame());
    }

    public void start() {
        lView.setVisible(true);
    }

}
