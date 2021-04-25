package View;

import Controller.FileController;
import Controller.LoginController;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.crypto.SecretKey;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author jaredb
 */
public class LoginView extends JDialog {

    private JTextField tfUsername;
    private JTextField tfPin;
    private JPasswordField pfPassword;

    private JLabel lblUsername;
    private JLabel lblPassword;
    private JLabel lblFirstLogin;

    private JButton btnLogin;
    private JButton btnCancel;

    private JPanel inputPanel;
    private JPanel buttonPanel;
    private JPanel textPanel;

    private GridBagConstraints constraints;

    private LoginController lgnCtl;
    private FileController fileCtl;

    private PassVaultView pView;

    private final Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
    private final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    private boolean waitCursorShowing;

    public LoginView(JFrame parent) {
        super(parent, "Login", true);

        initComponents(parent);
    }

    private void waitCursor() {
        if (waitCursorShowing) {
            // set the cursor back to the default
            waitCursorShowing = false;
            this.setCursor(defaultCursor);
        } else {
            // change the cursor to the wait cursor
            waitCursorShowing = true;
            this.setCursor(waitCursor);
        }
    }

    private void initComponents(JFrame parent) {

        lgnCtl = new LoginController();
        fileCtl = new FileController();

        inputPanel = new JPanel(new GridBagLayout());
        buttonPanel = new JPanel();
        textPanel = new JPanel();

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;

        if (lgnCtl.isFirstLogin()) {

            JOptionPane.showMessageDialog(this, "It looks like this is your "
                    + "first time running\nPass Vault, please create a username "
                    + "and\npassword to use for future logins", "New account",
                    JOptionPane.INFORMATION_MESSAGE);
            lblFirstLogin = new JLabel("Please enter a username and password to use for future logins");
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            textPanel.add(lblFirstLogin);

            lblUsername = new JLabel("Username: ");
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
            inputPanel.add(lblUsername, constraints);

            tfUsername = new JTextField(20);
            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.gridwidth = 2;
            inputPanel.add(tfUsername, constraints);

            lblPassword = new JLabel("Password: ");
            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.gridwidth = 1;
            inputPanel.add(lblPassword, constraints);

            pfPassword = new JPasswordField(20);
            constraints.gridx = 1;
            constraints.gridy = 2;
            constraints.gridwidth = 2;
            inputPanel.add(pfPassword, constraints);

            btnLogin = new JButton("Create and login");
            btnLogin.addActionListener((ActionEvent e) -> {
                waitCursor();

                int option = JOptionPane.showConfirmDialog(this, "Would you like to use"
                        + " an additional 6 digit PIN to login with?", "Use PIN?", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    tfPin = new JTextField(20);

                    Object[] message = {"6-digit PIN:", tfPin};

                    JOptionPane.showMessageDialog(this, message, "Please enter a 6 digit PIN to login with",
                            JOptionPane.INFORMATION_MESSAGE);

                    lgnCtl.setPasswordBasedKey(String.valueOf(pfPassword.getPassword()));
                    fileCtl.savePin(tfPin.getText());
                    SecretKey key = lgnCtl.getPasswordBasedKey();
                    fileCtl.saveLoginCredentials(getUsername(), getPassword(), key);
                    pView = new PassVaultView(lgnCtl.getPasswordBasedKey());
                    pView.setVisible(true);
                    dispose();
                } else {
                    lgnCtl.setPasswordBasedKey(String.valueOf(pfPassword.getPassword()));
                    SecretKey key = lgnCtl.getPasswordBasedKey();
                    fileCtl.saveLoginCredentials(getUsername(), getPassword(), key);
                    pView = new PassVaultView(lgnCtl.getPasswordBasedKey());
                    pView.setVisible(true);
                    dispose();
                }
            });

            btnCancel = new JButton("Cancel");
            btnCancel.addActionListener((ActionEvent e) -> {
                dispose();
                parent.dispose();
                System.exit(0);
            });

            buttonPanel.add(btnLogin);
            buttonPanel.add(btnCancel);

            getContentPane().add(inputPanel, BorderLayout.CENTER);
            getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
            getContentPane().add(textPanel, BorderLayout.NORTH);

        } else {
            lblUsername = new JLabel("Username: ");
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            inputPanel.add(lblUsername, constraints);

            tfUsername = new JTextField(20);
            constraints.gridx = 1;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            inputPanel.add(tfUsername, constraints);

            lblPassword = new JLabel("Password: ");
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
            inputPanel.add(lblPassword, constraints);

            pfPassword = new JPasswordField(20);
            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.gridwidth = 2;
            inputPanel.add(pfPassword, constraints);

            btnLogin = new JButton("Login");
            btnLogin.addActionListener((ActionEvent e) -> {
                waitCursor();

                if (fileCtl.isLoginSaved() && fileCtl.isPinSaved()) {
                    if (lgnCtl.authenticate(getUsername(), getPassword())) {
                        tfPin = new JTextField(20);
                        Object[] message = {"6-digit PIN:", tfPin};
                        JOptionPane.showMessageDialog(this, message, "Please enter your 6 digit PIN",
                                JOptionPane.INFORMATION_MESSAGE);

                        while (!lgnCtl.authenticatePin(tfPin.getText())) {
                            JOptionPane.showMessageDialog(this, "Invalid PIN", "Login", JOptionPane.ERROR_MESSAGE);

                            waitCursor();

                            tfPin.setText("");
                            JOptionPane.showMessageDialog(this, message, "Please enter your 6 digit PIN",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }

                        waitCursor();

                        lgnCtl.setPasswordBasedKey(String.valueOf(pfPassword.getPassword()));
                        pView = new PassVaultView(lgnCtl.getPasswordBasedKey());
                        pView.setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid username or password", "Login", JOptionPane.ERROR_MESSAGE);

                        waitCursor();

                        // reset username and password
                        tfUsername.setText("");
                        pfPassword.setText("");
                        return;
                    }
                }
                if (lgnCtl.authenticate(getUsername(), getPassword())) {
                    lgnCtl.setPasswordBasedKey(String.valueOf(pfPassword.getPassword()));
                    pView = new PassVaultView(lgnCtl.getPasswordBasedKey());
                    pView.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password", "Login", JOptionPane.ERROR_MESSAGE);

                    waitCursor();

                    // reset username and password
                    tfUsername.setText("");
                    pfPassword.setText("");
                }
            });

            btnCancel = new JButton("Exit");
            btnCancel.addActionListener((ActionEvent e) -> {
                dispose();
                parent.dispose();
                System.exit(0);
            });

            buttonPanel.add(btnLogin);
            buttonPanel.add(btnCancel);

            getContentPane().add(inputPanel, BorderLayout.CENTER);
            getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
        }

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private String getUsername() {
        return tfUsername.getText().trim();
    }

    private String getPassword() {
        return String.valueOf(pfPassword.getPassword());
    }
}
