import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.List;
import java.util.ArrayList;

import crud.software.Models.CrudConfiguration;
import crud.software.Models.CrudAuthTableConfig;
import crud.software.Models.CrudMessage;
import crud.software.Models.ColumnModel;
import crud.software.Models.FinalQueryData;
import crud.software.Models.CodeInput;

import crud.software.MySQL.MySQLDBHelper;
import crud.software.MySQL.MySQL_LaravelAPI;
import crud.software.MySQL.Flutter_LaravelMySQL;

public class App extends JFrame {

    private JTextField txtHost;
    private JTextField txtPort;
    private JTextField txtUsername;
    private JTextField txtPassword;
    private JTextField txtDatabase;

    private JList tableList;
    private DefaultListModel<String> tableListModel;

    private JTextField txtProjectName;
    private JButton btnConnect;
    private JLabel statusLabel;
	
	private JComboBox<String> cmbAuthTable;
	private JComboBox<String> cmbUsernameColumn;
	private JComboBox<String> cmbPasswordColumn;
    private JCheckBox chkIsEmail;
    private JCheckBox chkSkipAuth;

    public App() {

        // Create form fields
        txtHost = new JTextField(20);
		txtHost.setText("localhost");
        txtPort = new JTextField(10);
		txtPort.setText("3306");
        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);  // Using JPasswordField for security
        txtDatabase = new JTextField(20);

        tableListModel = new DefaultListModel<>(); // Initialize here
        tableList = new JList(tableListModel);

        txtProjectName = new JTextField(20);
		txtProjectName.setText("ProjectName");
        statusLabel = new JLabel();
		
		cmbAuthTable = new JComboBox<>();
        cmbUsernameColumn = new JComboBox<>();
		cmbPasswordColumn = new JComboBox<>();
        chkIsEmail = new JCheckBox("Is Email");
        chkSkipAuth = new JCheckBox("Skip Auth");
		
		cmbAuthTable.addActionListener(e -> updateColumnsDropdown());

        // Layout form fields 
        JPanel formPanel = new JPanel(new GridLayout(0, 2));
        formPanel.add(new JLabel("Host:"));
        formPanel.add(txtHost);
        formPanel.add(new JLabel("Port:"));
        formPanel.add(txtPort);
        formPanel.add(new JLabel("Username:"));
        formPanel.add(txtUsername);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(txtPassword);
        formPanel.add(new JLabel("Database:"));
        formPanel.add(txtDatabase);

        // Add Connect button
        btnConnect = new JButton("Connect");
        btnConnect.addActionListener(e -> loadTables());
        formPanel.add(btnConnect);

        // Add select/unselect buttons
        JButton btnSelectAll = new JButton("Select All");
        JButton btnUnselectAll = new JButton("Unselect All");

        // Add generate button
        JButton btnGenerate = new JButton("Generate API");

		// Layout UI
		add(formPanel, BorderLayout.NORTH);
		
		// Create a container JPanel with BoxLayout to stack components vertically
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
		centerPanel.add(new JScrollPane(tableList));

		// Auth Panel
		JPanel authPanel = new JPanel(new GridLayout(0, 2));
		authPanel.add(new JLabel("Auth Table:"));
		authPanel.add(cmbAuthTable);
		authPanel.add(new JLabel("Username Column:"));
		authPanel.add(cmbUsernameColumn);
		authPanel.add(new JLabel("Password Column:"));
		authPanel.add(cmbPasswordColumn);
		authPanel.add(chkIsEmail);
		authPanel.add(chkSkipAuth);
		
		centerPanel.add(authPanel);
		
		// Add the container panel to the frame
		add(centerPanel, BorderLayout.CENTER);

		// Create a panel for the buttons and status label
		JPanel southPanel = new JPanel(new FlowLayout());
		southPanel.add(new JLabel("Project Name:")); // optional label for clarity
		southPanel.add(txtProjectName);
		southPanel.add(btnSelectAll);
		southPanel.add(btnUnselectAll);
		southPanel.add(btnGenerate);
		southPanel.add(statusLabel);

		// Add the panel to the frame
		add(southPanel, BorderLayout.SOUTH);

        // Select/Unselect table buttons
        btnSelectAll.addActionListener(e -> {
            for (int i = 0; i < tableListModel.size(); i++) {
                tableList.addSelectionInterval(i, i);
            }
        });

        btnUnselectAll.addActionListener(e -> {
            tableList.clearSelection();
        });

        // Generate API button handler
        btnGenerate.addActionListener(e -> {
            generateAPI();
        });
    }

    private void loadTables() {
        // Create DB Helper
        MySQLDBHelper dbHelper = new MySQLDBHelper(txtHost.getText(), txtPort.getText(),
            txtUsername.getText(), new String(((JPasswordField) txtPassword).getPassword()), txtDatabase.getText());

        try {
            // Connect and load tables
            dbHelper.connect();
            List<String> tables = dbHelper.getListOfTable();

            // Populate list box
            tableListModel.clear();
            for (String table : tables) {
                tableListModel.addElement(table);
            }
			
			cmbAuthTable.removeAllItems();
			for (String table : tables) {
				cmbAuthTable.addItem(table);
			}
        } catch (Exception ex) {
            // Show error message (for now, printing the error message to the console)
            ex.printStackTrace();
        }
    }
	
	private void updateColumnsDropdown() {
		// Clear the previous items
		cmbUsernameColumn.removeAllItems();
		cmbPasswordColumn.removeAllItems();

		MySQLDBHelper dbHelper = new MySQLDBHelper(txtHost.getText(), txtPort.getText(),
			txtUsername.getText(), new String(((JPasswordField) txtPassword).getPassword()), txtDatabase.getText());

		try {
			dbHelper.connect();
			List<ColumnModel> columns = dbHelper.getTableColumns((String) cmbAuthTable.getSelectedItem());

			for (ColumnModel column : columns) {
				cmbUsernameColumn.addItem(column.getField());
				cmbPasswordColumn.addItem(column.getField());
			}

			dbHelper.getDbCon().close();
		} catch (Exception ex) {
			// Handle exception here
			ex.printStackTrace();
		}
	}

    private void generateAPI() {
        CrudConfiguration config = new CrudConfiguration();
		
		CrudAuthTableConfig authConfig = new CrudAuthTableConfig();
        authConfig.setAuthTableName((String) cmbAuthTable.getSelectedItem());
        authConfig.setUsernameColumnName((String)cmbUsernameColumn.getSelectedItem());
        authConfig.setPasswordColumnName((String)cmbPasswordColumn.getSelectedItem());
        authConfig.setEmail(chkIsEmail.isSelected());
        authConfig.setSkipAuth(chkSkipAuth.isSelected());
        config.setAuthTableConfig(authConfig);
		
        List<String> selectedTables = new ArrayList<>();
        for (Object table : tableList.getSelectedValuesList()) {
            selectedTables.add((String) table);
        }

        // Create API generator
        MySQL_LaravelAPI generator = new MySQL_LaravelAPI(config, false, "/");
		
		

        // Generate API
        MySQLDBHelper dbHelper = new MySQLDBHelper(txtHost.getText(), txtPort.getText(),
            txtUsername.getText(), new String(((JPasswordField) txtPassword).getPassword()), txtDatabase.getText());

        try {
            CodeInput<FinalQueryData> codeInput = generator.automator(txtProjectName.getText(), selectedTables, dbHelper);
			
			//Capture and display the messages after automator method
			ArrayList<CrudMessage> messages = generator.getMessages();
			if (messages != null && !messages.isEmpty()) {
				StringBuilder messageText = new StringBuilder();
				for (CrudMessage message : messages) {
					messageText.append(message.getMessage()).append("\n");
				}
				System.out.println(messageText.toString());
			}
			
			Flutter_LaravelMySQL generator3 = new Flutter_LaravelMySQL(txtProjectName.getText(), generator.getProjectFolder(), "/");
			generator3.createFlutterAPP(codeInput);
			
			ArrayList<CrudMessage> messages3 = generator3.getMessages();
			if (messages3 != null && !messages3.isEmpty()) {
				StringBuilder messageText = new StringBuilder();
				for (CrudMessage message : messages3) {
					messageText.append(message.getMessage()).append("\n");
				}
				System.out.println(messageText.toString());
			}
			
        } catch (Exception e) {
            // Handle the exception here
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        App gui = new App();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.pack();
        gui.setVisible(true);
    }
}