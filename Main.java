
import java.awt.*;
import java.sql.*;
import javax.swing.*;

// --- Database Connection Class ---
class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/parkingdb";
    private static final String USER = "root"; 
    private static final String PASS = "dev704@#ram"; 

    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Connected to database successfully!");
        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
        return con;
    }
}

// --- Supporting Classes ---
class Admin extends ParkingManager {
    Admin(String id, String pass) { 
        super(id, pass); 
    }
    boolean login(String id, String pass) { 
        return id.equals(managerId) && pass.equals(password); 
    }
    ParkingManager register(String id, String pass) { 
        return new ParkingManager(id, pass); 
    }
}

class ParkingManager {
    String managerId, password;
    ParkingManager(String id, String pass) {
        this.managerId = id; this.password = pass; 
    }
    boolean login(String id, String pass) { 
        return id.equals(managerId) && pass.equals(password); 
    }
    void assignSlotToVehicle(ParkingSlot slot, Vehicle v) { 
        slot.assignVehicle(v); 
    }
}

class Vehicle {
    int id;
    String vehicleNumber, ownerName, type;
    Vehicle(int id, String num, String owner, String type) {
        this.id = id; this.vehicleNumber = num; this.ownerName = owner; this.type = type;
    }
}

class ParkingSlot {
    int slotNumber;
    boolean occupied;
    Vehicle vehicle;
    ParkingSlot(int n) { 
        slotNumber = n; occupied = false; 
    }
    boolean checkAvailability() { 
        return !occupied; 
    }
    void assignVehicle(Vehicle v) { 
        occupied = true; vehicle = v; 
    }
    void freeSlot() {
        occupied = false; vehicle = null; 
    }
    String getStatus() {
        return "Slot " + slotNumber + (occupied ? " - Occupied by " + vehicle.vehicleNumber : " - Available");
    }
}

class ParkingRecord {
    int recordId;
    Vehicle vehicle;
    ParkingSlot slot;
    long entryTime, exitTime;
    double fee;

    ParkingRecord(int id, Vehicle v, ParkingSlot s) {
        recordId = id; vehicle = v; slot = s;
        entryTime = System.currentTimeMillis();
    }

    void closeRecord() {
        exitTime = System.currentTimeMillis();
        long duration = (exitTime - entryTime) / 1000; // seconds
        fee = duration * 0.5; // Rs. 0.5 per sec
    }
}

// --- MAIN CLASS ---
public class Main {
    private static Admin admin = new Admin("admin", "admin123");
    private static ParkingSlot[] slots = new ParkingSlot[50];
    private static ParkingRecord[] records = new ParkingRecord[50];
    private static ParkingManager[] managers = new ParkingManager[50];

    private static int recordCount = 0, managerCount = 0, vehicleCount = 0;
    private static JFrame mainFrame;
    private static ParkingManager loggedInUser = null;

    public static void main(String[] args) {
        for (int i = 0; i < slots.length; i++)
            slots[i] = new ParkingSlot(i + 1);

        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        mainFrame = new JFrame("Parking Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(500, 400);
        showLoginPanel();
        mainFrame.setVisible(true);
    }

    private static void showLoginPanel() {
        mainFrame.getContentPane().removeAll();
        mainFrame.setTitle("Parking Management System - Login");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; panel.add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; panel.add(passField, gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            if (admin.login(user, pass)) {
                loggedInUser = admin;
                showAdminMenu();
            } else {
                boolean found = false;
                for (int i = 0; i < managerCount; i++) {
                    if (managers[i].login(user, pass)) {
                        loggedInUser = managers[i];
                        showManagerMenu();
                        found = true; break;
                    }
                }
                if (!found)
                    JOptionPane.showMessageDialog(mainFrame, "Invalid login!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainFrame.setContentPane(panel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    // --- ADMIN MENU ---
    private static void showAdminMenu() {
        mainFrame.getContentPane().removeAll();
        mainFrame.setTitle("Admin Menu - Logged in as " + admin.managerId);

        JPanel panel = new JPanel(new GridLayout(8, 1, 10, 10));
        JButton assignSlotBtn = new JButton("1. Assign Slot");
        assignSlotBtn.addActionListener(e -> assignSlot(admin));
        panel.add(assignSlotBtn);

        JButton releaseSlotBtn = new JButton("2. Release Slot");
        releaseSlotBtn.addActionListener(e -> releaseSlot());
        panel.add(releaseSlotBtn);

        JButton viewStatusBtn = new JButton("3. View Slot Status");
        viewStatusBtn.addActionListener(e -> viewSlotStatus());
        panel.add(viewStatusBtn);

        JButton viewVehiclesBtn = new JButton("4. View All Vehicle Details");
        viewVehiclesBtn.addActionListener(e -> viewParkedVehicles());
        panel.add(viewVehiclesBtn);

        JButton registerMgrBtn = new JButton("5. Register Manager");
        registerMgrBtn.addActionListener(e -> registerManager());
        panel.add(registerMgrBtn);

        JButton reportBtn = new JButton("6. View Final Report");
        reportBtn.addActionListener(e -> viewFinalReport());
        panel.add(reportBtn);

        JButton logoutBtn = new JButton("7. Logout");
        logoutBtn.addActionListener(e -> showLoginPanel());
        panel.add(logoutBtn);

        mainFrame.setContentPane(new JScrollPane(panel));
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    // --- MANAGER MENU ---
    private static void showManagerMenu() {
        mainFrame.getContentPane().removeAll();
        mainFrame.setTitle("Manager Menu - Logged in as " + loggedInUser.managerId);

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));

        JButton assignSlotBtn = new JButton("1. Assign Slot");
        assignSlotBtn.addActionListener(e -> assignSlot(loggedInUser));
        panel.add(assignSlotBtn);

        JButton releaseSlotBtn = new JButton("2. Release Slot");
        releaseSlotBtn.addActionListener(e -> releaseSlot());
        panel.add(releaseSlotBtn);

        JButton viewStatusBtn = new JButton("3. View Slot Status");
        viewStatusBtn.addActionListener(e -> viewSlotStatus());
        panel.add(viewStatusBtn);

        JButton viewVehiclesBtn = new JButton("4. View All Vehicle Details");
        viewVehiclesBtn.addActionListener(e -> viewParkedVehicles());
        panel.add(viewVehiclesBtn);

        JButton reportBtn = new JButton("5. View Final Report");
        reportBtn.addActionListener(e -> viewFinalReport());
        panel.add(reportBtn);

        JButton logoutBtn = new JButton("6. Logout");
        logoutBtn.addActionListener(e -> showLoginPanel());
        panel.add(logoutBtn);

        mainFrame.setContentPane(new JScrollPane(panel));
        mainFrame.revalidate();
        mainFrame.repaint();
    }
    // --- FUNCTIONALITY ---
    private static void assignSlot(ParkingManager manager) {
        JTextField numField = new JTextField();
        JTextField ownerField = new JTextField();
        JTextField typeField = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Vehicle Number:")); inputPanel.add(numField);
        inputPanel.add(new JLabel("Owner Name:")); inputPanel.add(ownerField);
        inputPanel.add(new JLabel("Vehicle Type:")); inputPanel.add(typeField);

        int result = JOptionPane.showConfirmDialog(mainFrame, inputPanel, "Assign Slot", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String num = numField.getText();
            String owner = ownerField.getText();
            String type = typeField.getText();

            boolean slotFound = false;
            for (int j = 0; j < slots.length; j++) {
                if (slots[j].checkAvailability()) {
                    Vehicle v = new Vehicle(vehicleCount + 1, num, owner, type);
                    vehicleCount++;
                    manager.assignSlotToVehicle(slots[j], v);
                    records[recordCount] = new ParkingRecord(recordCount + 1, v, slots[j]);
                    recordCount++;
                    slotFound = true;

                    // --- Insert into Database ---
                    try (Connection con = DatabaseConnection.getConnection()) {
                        String query = "INSERT INTO parking_records(vehicle_number, owner_name, vehicle_type, slot_no) VALUES (?, ?, ?, ?)";
                        PreparedStatement ps = con.prepareStatement(query);
                        ps.setString(1, num);
                        ps.setString(2, owner);
                        ps.setString(3, type);
                        ps.setInt(4, slots[j].slotNumber);
                        ps.executeUpdate();
                        ps.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(mainFrame, "Slot assigned successfully!");
                    break;
                }
            }
            if (!slotFound)
                JOptionPane.showMessageDialog(mainFrame, "No slots available!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private static void releaseSlot() {
        String releaseNum = JOptionPane.showInputDialog(mainFrame, "Enter Vehicle Number to release slot:");
        if (releaseNum != null && !releaseNum.trim().isEmpty()) {
            boolean released = false;
            for (int j = 0; j < recordCount; j++) {
                if (records[j].vehicle.vehicleNumber.equals(releaseNum) && records[j].exitTime == 0) {
                    records[j].closeRecord();
                    records[j].slot.freeSlot();
                    try (Connection con = DatabaseConnection.getConnection()) {
                        String query = "UPDATE parking_records SET exit_time = NOW(), fee = ? WHERE vehicle_number = ? AND exit_time IS NULL";
                        PreparedStatement ps = con.prepareStatement(query);
                        ps.setDouble(1, records[j].fee);
                        ps.setString(2, releaseNum);
                        ps.executeUpdate();
                        ps.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(mainFrame, "Vehicle exited. Fee = Rs. " + String.format("%.2f", records[j].fee));
                    released = true;
                    break;
                }
            }
            if (!released)
                JOptionPane.showMessageDialog(mainFrame, "Vehicle not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private static void viewSlotStatus() {
        StringBuilder status = new StringBuilder();
        for (ParkingSlot slot : slots)
            status.append(slot.getStatus()).append("\n");
        JTextArea area = new JTextArea(status.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(mainFrame, new JScrollPane(area), "Slot Status", JOptionPane.INFORMATION_MESSAGE);
    }
    private static void viewParkedVehicles() {
        StringBuilder details = new StringBuilder("--- All Parked Vehicles ---\n");
        try (Connection con = DatabaseConnection.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM parking_records WHERE exit_time IS NULL");
            boolean found = false;
            while (rs.next()) {
                details.append("Vehicle No: ").append(rs.getString("vehicle_number")).append("\n")
                       .append("Owner: ").append(rs.getString("owner_name")).append("\n")
                       .append("Slot No: ").append(rs.getInt("slot_no")).append("\n\n");
                found = true;
            }
            if (!found) details.append("No vehicles are currently parked.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        JTextArea area = new JTextArea(details.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(mainFrame, new JScrollPane(area), "Parked Vehicles", JOptionPane.INFORMATION_MESSAGE);
    }
    private static void registerManager() {
        JTextField idField = new JTextField();
        JPasswordField passField = new JPasswordField();

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Manager ID:")); inputPanel.add(idField);
        inputPanel.add(new JLabel("Password:")); inputPanel.add(passField);

        int result = JOptionPane.showConfirmDialog(mainFrame, inputPanel, "Register Manager", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            managers[managerCount++] = admin.register(idField.getText(), new String(passField.getPassword()));
            JOptionPane.showMessageDialog(mainFrame, "Manager registered successfully!");
        }
    }
    private static void viewFinalReport() {
        try (Connection con = DatabaseConnection.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM parking_records WHERE exit_time IS NOT NULL");
            StringBuilder report = new StringBuilder("----- Final Report -----\n");
            double total = 0;
            boolean found = false;
            while (rs.next()) {
                found = true;
                report.append("Record ID: ").append(rs.getInt("record_id")).append("\n")
                      .append("Vehicle No: ").append(rs.getString("vehicle_number")).append("\n")
                      .append("Owner: ").append(rs.getString("owner_name")).append("\n")
                      .append("Slot No: ").append(rs.getInt("slot_no")).append("\n")
                      .append("Fee: Rs. ").append(rs.getDouble("fee")).append("\n")
                      .append("-----------------------------------\n");
                total += rs.getDouble("fee");
            }
            if (!found)
                report.append("No vehicles have exited yet.\n");
            else
                report.append("Total Revenue Collected: Rs. ").append(String.format("%.2f", total)).append("\n");

            JTextArea area = new JTextArea(report.toString());
            area.setEditable(false);
            JOptionPane.showMessageDialog(mainFrame, new JScrollPane(area), "Final Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
