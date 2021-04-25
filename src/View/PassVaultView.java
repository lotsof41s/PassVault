package View;

import Controller.FileController;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.util.List;

import javax.crypto.SecretKey;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author jaredb
 */
public class PassVaultView extends JFrame {
    
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JTextField tfWebsite;
    
    private JLabel lblUsername;
    private JLabel lblPassword;
    private JLabel lblWebsite;
    
    private JButton btnSave;
    private JButton btnExit;
    
    private JPanel inputPanel;
    private JPanel buttonPanel;
    private JPanel tablePanel;
    
    private JTable credentialTable;
    private DefaultTableModel tableModel;
    private JScrollPane scollableTable;
    
    private final String[] columnHeadings = {"Username/Email", "Password", "Website"};
    private final int ROWS = 0;
    
    private GridBagConstraints constraints;
    
    private FileController fileCtl;
    
    private final SecretKey key;
    
    public PassVaultView(SecretKey key) {
        this.key = key;
        
        initFrame();
        initFrameComponents();
    }
    
    private void initFrame() {
        setSize(470, 580);
        setResizable(false);
        setTitle("Password Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private void initFrameComponents() {
        fileCtl = new FileController();
        
        inputPanel = new JPanel(new GridBagLayout());
        buttonPanel = new JPanel();
        tablePanel = new JPanel();
        
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        
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
        
        lblWebsite = new JLabel("Website: ");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        inputPanel.add(lblWebsite, constraints);
        
        tfWebsite = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        inputPanel.add(tfWebsite, constraints);
        
        btnSave = new JButton("Save");
        btnSave.addActionListener(e -> save());
        
        btnExit = new JButton("Exit");
        btnExit.addActionListener(e -> exit());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnExit);
        
        tableModel = new DefaultTableModel(ROWS, columnHeadings.length);
        tableModel.setColumnIdentifiers(columnHeadings);
        
        credentialTable = new JTable(tableModel);
        
        scollableTable = new JScrollPane(credentialTable);
        tablePanel.add(scollableTable);
        
        loadTableValues();
        
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.SOUTH);
    }
    
    private void loadTableValues() {
        List<String> values = fileCtl.getTableValues(key);
        
        if (!values.isEmpty()) {
            for (int i = 0; i < values.size(); i++) {
                tableModel.addRow(new Object[]{values.get(i).split(",")[0].replaceAll(" ", ""),
                    values.get(i).split(",")[1].replaceAll(" ", ""),
                    values.get(i).split(",")[2].replaceAll(" ", "")});
            }
        }
    }
    
    private void save() {
        tableModel.addRow(new Object[]{tfUsername.getText(), String.valueOf(pfPassword.getPassword()), tfWebsite.getText()});
        fileCtl.saveCredentials(tfUsername.getText(), String.valueOf(pfPassword.getPassword()), tfWebsite.getText(), key);
        
        tfUsername.setText("");
        pfPassword.setText("");
        tfWebsite.setText("");
    }
    
    private void exit() {
        System.exit(0);
    }
    
}
