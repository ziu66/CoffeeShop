/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Date; 
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.awt.print.PrinterException;

public class ReportsSystem extends JFrame {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private JPanel salesReportPanel;
    private JPanel topProductsPanel;
    private JPanel customerAnalyticsPanel;
    
    private JComboBox<String> periodSelector;
    private JButton btnGenerateReport;
    private JButton btnExportPDF;
    private JButton btnPrint;
    private JButton btnBack;
    
    private JTable salesTable;
    private JTable productsTable;
    private JTable customersTable;
    
    private DefaultTableModel salesTableModel;
    private DefaultTableModel productsTableModel;
    private DefaultTableModel customersTableModel;
    
    private JLabel lblTotalSales;
    private JLabel lblTotalOrders;
    private JLabel lblAvgOrderValue;
    
    private JPanel salesChartPanel;
    private JPanel productsPieChartPanel;
    private JPanel customersChartPanel;
    
    private final DecimalFormat currencyFormat = new DecimalFormat("₱#,##0.00");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    private User currentAdmin;
    private Date startDate, endDate;

    public ReportsSystem(User admin) {
        this.currentAdmin = admin;
        
        // Set up the frame
        setTitle("But First, Coffee - Reports & Analytics");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Initialize dates
        Calendar cal = Calendar.getInstance();
        endDate = cal.getTime(); // Today
        cal.add(Calendar.MONTH, -1);
        startDate = cal.getTime(); // One month ago
        
        // Initialize main panel
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(40, 40, 40));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(new Color(30, 30, 30));
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Create report panels
        salesReportPanel = createSalesReportPanel();
        topProductsPanel = createTopProductsPanel();
        customerAnalyticsPanel = createCustomerAnalyticsPanel();
        
        // Add panels to tabbed pane
        tabbedPane.addTab("Sales Overview", createTabIcon("/images/sales-icon.png"), salesReportPanel);
        tabbedPane.addTab("Top Products", createTabIcon("/images/products-icon.png"), topProductsPanel);
        tabbedPane.addTab("Customer Analytics", createTabIcon("/images/users-icon.png"), customerAnalyticsPanel);
        
        // Style the tabs
        tabbedPane.setBackgroundAt(0, new Color(30, 30, 30));
        tabbedPane.setBackgroundAt(1, new Color(30, 30, 30));
        tabbedPane.setBackgroundAt(2, new Color(30, 30, 30));
        
        // Add tabbed pane to main panel
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Create bottom button panel
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Set the content pane
        setContentPane(mainPanel);
        
        // Load initial data
        loadReportData();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Reports & Analytics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(218, 165, 32)); // Gold color
        panel.add(titleLabel, BorderLayout.WEST);
        
        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlsPanel.setBackground(new Color(30, 30, 30));
        
        // Period selector
        JLabel periodLabel = new JLabel("Time Period:");
        periodLabel.setForeground(Color.WHITE);
        
        periodSelector = new JComboBox<>(new String[]{
            "Today", "Yesterday", "Last 7 Days", "Last 30 Days", 
            "This Month", "Last Month", "This Year", "Custom Period"
        });
        periodSelector.setPreferredSize(new Dimension(150, 30));
        periodSelector.setBackground(new Color(60, 60, 60));
        periodSelector.setForeground(Color.WHITE);
        periodSelector.addActionListener(e -> {
            if (periodSelector.getSelectedItem().equals("Custom Period")) {
                showDateRangePicker();
            } else {
                updateDateRange((String) periodSelector.getSelectedItem());
            }
        });
        
        // Generate report button
        btnGenerateReport = new JButton("Generate Report");
        btnGenerateReport.setBackground(new Color(70, 130, 180)); // Steel blue
        btnGenerateReport.setForeground(Color.WHITE);
        btnGenerateReport.setFocusPainted(false);
        btnGenerateReport.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGenerateReport.addActionListener(e -> loadReportData());
        
        controlsPanel.add(periodLabel);
        controlsPanel.add(Box.createHorizontalStrut(10));
        controlsPanel.add(periodSelector);
        controlsPanel.add(Box.createHorizontalStrut(15));
        controlsPanel.add(btnGenerateReport);
        
        panel.add(controlsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        
        // Bottom buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(new Color(30, 30, 30));
        
        btnExportPDF = createStyledButton("Export PDF", new Color(70, 130, 180));
        btnPrint = createStyledButton("Print Report", new Color(70, 130, 180));
        btnBack = createStyledButton("Back to Dashboard", new Color(218, 165, 32));
        
        btnExportPDF.addActionListener(e -> exportReportToPDF());
        btnPrint.addActionListener(e -> printReport());
        btnBack.addActionListener(e -> dispose());
        
        buttonsPanel.add(btnExportPDF);
        buttonsPanel.add(Box.createHorizontalStrut(10));
        buttonsPanel.add(btnPrint);
        buttonsPanel.add(Box.createHorizontalStrut(20));
        buttonsPanel.add(btnBack);
        
        panel.add(buttonsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createSalesReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Summary cards panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setBackground(new Color(40, 40, 40));
        
        // Total Sales card
        JPanel totalSalesCard = createSummaryCard("Total Sales", "₱0.00", "/images/sales-icon.png");
        lblTotalSales = (JLabel)((JPanel)totalSalesCard.getComponent(1)).getComponent(0);
        
        // Total Orders card
        JPanel totalOrdersCard = createSummaryCard("Total Orders", "0", "/images/orders-icon.png");
        lblTotalOrders = (JLabel)((JPanel)totalOrdersCard.getComponent(1)).getComponent(0);
        
        // Average Order Value card
        JPanel avgOrderCard = createSummaryCard("Avg Order Value", "₱0.00", "/images/average-icon.png");
        lblAvgOrderValue = (JLabel)((JPanel)avgOrderCard.getComponent(1)).getComponent(0);
        
        summaryPanel.add(totalSalesCard);
        summaryPanel.add(totalOrdersCard);
        summaryPanel.add(avgOrderCard);
        
        // Create content panel with chart and table
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        contentPanel.setBackground(new Color(40, 40, 40));
        
        // Sales chart panel
        salesChartPanel = new JPanel();
        salesChartPanel.setBackground(new Color(50, 50, 50));
        salesChartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            "Sales Trend",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            Color.WHITE
        ));
        salesChartPanel.setLayout(new BorderLayout());
        
        // Placeholder for chart
        JLabel chartPlaceholder = new JLabel("Sales Chart Visualization Will Appear Here", SwingConstants.CENTER);
        chartPlaceholder.setForeground(Color.WHITE);
        salesChartPanel.add(chartPlaceholder, BorderLayout.CENTER);
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(50, 50, 50));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            "Sales Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            Color.WHITE
        ));
        
        // Create table model
        String[] salesColumns = {"Date", "Orders", "Items Sold", "Total Sales", "Avg Order Value"};
        salesTableModel = new DefaultTableModel(salesColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        salesTable = new JTable(salesTableModel);
        styleTable(salesTable);
        
        JScrollPane tableScrollPane = new JScrollPane(salesTable);
        tableScrollPane.setBackground(new Color(50, 50, 50));
        tableScrollPane.getViewport().setBackground(new Color(50, 50, 50));
        
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        contentPanel.add(salesChartPanel);
        contentPanel.add(tablePanel);
        
        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTopProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Create split pane for chart and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(550);
        splitPane.setDividerSize(5);
        splitPane.setBackground(new Color(40, 40, 40));
        splitPane.setBorder(null);
        
        // Products chart panel
        productsPieChartPanel = new JPanel();
        productsPieChartPanel.setBackground(new Color(50, 50, 50));
        productsPieChartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            "Top Products by Sales",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            Color.WHITE
        ));
        productsPieChartPanel.setLayout(new BorderLayout());
        
        // Placeholder for pie chart
        JLabel pieChartPlaceholder = new JLabel("Products Distribution Chart Will Appear Here", SwingConstants.CENTER);
        pieChartPlaceholder.setForeground(Color.WHITE);
        productsPieChartPanel.add(pieChartPlaceholder, BorderLayout.CENTER);
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(50, 50, 50));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            "Product Performance",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            Color.WHITE
        ));
        
        // Create table model
        String[] productsColumns = {"Rank", "Product ID", "Product Name", "Category", "Quantity Sold", "Revenue", "% of Total"};
        productsTableModel = new DefaultTableModel(productsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        productsTable = new JTable(productsTableModel);
        styleTable(productsTable);
        
        JScrollPane tableScrollPane = new JScrollPane(productsTable);
        tableScrollPane.setBackground(new Color(50, 50, 50));
        tableScrollPane.getViewport().setBackground(new Color(50, 50, 50));
        
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(productsPieChartPanel);
        splitPane.setRightComponent(tablePanel);
        
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCustomerAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Create split pane for chart and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(350);
        splitPane.setDividerSize(5);
        splitPane.setBackground(new Color(40, 40, 40));
        splitPane.setBorder(null);
        
        // Customers chart panel
        customersChartPanel = new JPanel();
        customersChartPanel.setBackground(new Color(50, 50, 50));
        customersChartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            "Customer Activity",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            Color.WHITE
        ));
        customersChartPanel.setLayout(new BorderLayout());
        
        // Placeholder for customer chart
        JLabel chartPlaceholder = new JLabel("Customer Analytics Chart Will Appear Here", SwingConstants.CENTER);
        chartPlaceholder.setForeground(Color.WHITE);
        customersChartPanel.add(chartPlaceholder, BorderLayout.CENTER);
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(50, 50, 50));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            "Top Customers",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            Color.WHITE
        ));
        
        // Create table model
        String[] customersColumns = {"Rank", "Customer ID", "Customer Name", "Email", "Orders", "Total Spent", "Avg Order"};
        customersTableModel = new DefaultTableModel(customersColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        customersTable = new JTable(customersTableModel);
        styleTable(customersTable);
        
        JScrollPane tableScrollPane = new JScrollPane(customersTable);
        tableScrollPane.setBackground(new Color(50, 50, 50));
        tableScrollPane.getViewport().setBackground(new Color(50, 50, 50));
        
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        splitPane.setTopComponent(customersChartPanel);
        splitPane.setBottomComponent(tablePanel);
        
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSummaryCard(String title, String value, String iconPath) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(new Color(50, 50, 50));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        titlePanel.setOpaque(false);
        
        // Try to load the icon
        JLabel iconLabel = new JLabel();
        try {
            java.net.URL iconUrl = getClass().getResource(iconPath);
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                Image scaledIcon = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                iconLabel.setIcon(new ImageIcon(scaledIcon));
            }
        } catch (Exception e) {
            // If icon can't be loaded, use text alternative
            iconLabel.setText("•");
            iconLabel.setForeground(new Color(218, 165, 32));
            iconLabel.setFont(new Font("Arial", Font.BOLD, 24));
        }
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        
        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        
        // Value panel
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        valuePanel.setOpaque(false);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(new Color(218, 165, 32));
        
        valuePanel.add(valueLabel);
        
        // Add to card
        card.add(titlePanel, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(bgColor.getRed() + 20, bgColor.getGreen() + 20, bgColor.getBlue() + 20), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(
                    Math.min(bgColor.getRed() + 20, 255),
                    Math.min(bgColor.getGreen() + 20, 255),
                    Math.min(bgColor.getBlue() + 20, 255)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void styleTable(JTable table) {
        table.setBackground(new Color(50, 50, 50));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(70, 70, 70));
        table.setSelectionBackground(new Color(100, 100, 100));
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(25);
        table.setShowVerticalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Style the header
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(30, 30, 30));
        header.setForeground(new Color(218, 165, 32));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }
    
    private Icon createTabIcon(String iconPath) {
        try {
            java.net.URL iconUrl = getClass().getResource(iconPath);
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                Image scaledIcon = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledIcon);
            }
        } catch (Exception e) {
            // If icon can't be loaded, return null
        }
        return null;
    }
    
    private void updateDateRange(String period) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        endDate = cal.getTime(); // End date is today (midnight)
        
        switch (period) {
            case "Today":
                startDate = cal.getTime(); // Start date is also today (midnight)
                break;
            case "Yesterday":
                cal.add(Calendar.DAY_OF_MONTH, -1);
                startDate = cal.getTime();
                endDate = cal.getTime(); // End date is also yesterday
                break;
            case "Last 7 Days":
                cal.add(Calendar.DAY_OF_MONTH, -6); // Today + 6 previous days = 7 days
                startDate = cal.getTime();
                break;
            case "Last 30 Days":
                cal.add(Calendar.DAY_OF_MONTH, -29); // Today + 29 previous days = 30 days
                startDate = cal.getTime();
                break;
            case "This Month":
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = cal.getTime();
                break;
            case "Last Month":
                cal.set(Calendar.DAY_OF_MONTH, 1); // First day of this month
                cal.add(Calendar.DAY_OF_MONTH, -1); // Last day of previous month
                endDate = cal.getTime();
                cal.set(Calendar.DAY_OF_MONTH, 1); // First day of previous month
                startDate = cal.getTime();
                break;
            case "This Year":
                cal.set(Calendar.DAY_OF_YEAR, 1);
                startDate = cal.getTime();
                break;
            default:
                cal.add(Calendar.MONTH, -1);
                startDate = cal.getTime();
                break;
        }
        
        // Reset end date to end of day (23:59:59.999)
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endCal.set(Calendar.MILLISECOND, 999);
        endDate = endCal.getTime();
    }
    
    private void showDateRangePicker() {
        JDialog dialog = new JDialog(this, "Select Date Range", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(40, 40, 40));
        
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        contentPanel.setBackground(new Color(40, 40, 40));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Start date panel
        JPanel startDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startDatePanel.setBackground(new Color(40, 40, 40));
        JLabel startLabel = new JLabel("Start Date:");
        startLabel.setForeground(Color.WHITE);
        
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setValue(startDate);
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(startEditor);
        
        startDatePanel.add(startLabel);
        startDatePanel.add(startDateSpinner);
        
        // End date panel
        JPanel endDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        endDatePanel.setBackground(new Color(40, 40, 40));
        JLabel endLabel = new JLabel("End Date:");
        endLabel.setForeground(Color.WHITE);
        
        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setValue(endDate);
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        endDateSpinner.setEditor(endEditor);
        
        endDatePanel.add(endLabel);
        endDatePanel.add(endDateSpinner);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(40, 40, 40));
        
        JButton applyButton = createStyledButton("Apply", new Color(70, 130, 180));
        JButton cancelButton = createStyledButton("Cancel", new Color(130, 70, 70));
        
        applyButton.addActionListener(e -> {
            startDate = (Date) startDateSpinner.getValue();
            endDate = (Date) endDateSpinner.getValue();
            
            // Set end date to end of day
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate);
            endCal.set(Calendar.HOUR_OF_DAY, 23);
            endCal.set(Calendar.MINUTE, 59);
            endCal.set(Calendar.SECOND, 59);
            endCal.set(Calendar.MILLISECOND, 999);
            endDate = endCal.getTime();
            
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        
        contentPanel.add(startDatePanel);
        contentPanel.add(endDatePanel);
        contentPanel.add(buttonPanel);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void loadReportData() {
        loadSalesData();
        loadTopProductsData();
        loadCustomerAnalyticsData();
    }
    
    private void loadSalesData() {
        // Clear table
        salesTableModel.setRowCount(0);
        
        try {
            Connection conn = DBConnection.getConnection();
            
            // Get sales summary
            String summaryQuery = "SELECT COUNT(*) AS totalOrders, SUM(total_amount) AS totalSales " +
                                  "FROM orders " +
                                  "WHERE order_date BETWEEN ? AND ? AND status != 'CANCELLED'";
            
            PreparedStatement summaryStmt = conn.prepareStatement(summaryQuery);
            summaryStmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            summaryStmt.setTimestamp(2, new Timestamp(endDate.getTime()));
            
            ResultSet summaryRs = summaryStmt.executeQuery();
            
            if (summaryRs.next()) {
                int totalOrders = summaryRs.getInt("totalOrders");
                double totalSales = summaryRs.getDouble("totalSales");
                double avgOrderValue = totalOrders > 0 ? totalSales / totalOrders : 0;
                
                // Update summary cards
                lblTotalSales.setText(currencyFormat.format(totalSales));
                lblTotalOrders.setText(String.valueOf(totalOrders));
                lblAvgOrderValue.setText(currencyFormat.format(avgOrderValue));
            }
            
            summaryRs.close();
            summaryStmt.close();
            
            // Get daily sales data for the report period
            String salesQuery = "SELECT DATE(order_date) AS sale_date, " +
                                "COUNT(*) AS num_orders, " +
                                "COUNT(oi.order_item_id) AS items_sold, " +
                                "SUM(o.total_amount) AS total_sales " +
                                "FROM orders o " +
                                "LEFT JOIN order_items oi ON o.order_id = oi.order_id " +
                                "WHERE o.order_date BETWEEN ? AND ? AND o.status != 'CANCELLED' " +
                                "GROUP BY DATE(o.order_date) " +
                                "ORDER BY sale_date";

            
            PreparedStatement salesStmt = conn.prepareStatement(salesQuery);
            salesStmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            salesStmt.setTimestamp(2, new Timestamp(endDate.getTime()));
            
            ResultSet salesRs = salesStmt.executeQuery();
            
            while (salesRs.next()) {
                Date saleDate = salesRs.getDate("sale_date");
                int numOrders = salesRs.getInt("num_orders");
                int itemsSold = salesRs.getInt("items_sold");
                double totalSales = salesRs.getDouble("total_sales");
                double avgOrderValue = numOrders > 0 ? totalSales / numOrders : 0;
                
                Object[] row = {
                    dateFormat.format(saleDate),
                    numOrders,
                    itemsSold,
                    currencyFormat.format(totalSales),
                    currencyFormat.format(avgOrderValue)
                };
                
                salesTableModel.addRow(row);
            }
            
            salesRs.close();
            salesStmt.close();
            
            // Update the sales chart
            updateSalesChart();
            
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading sales data: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void loadTopProductsData() {
        // Clear table
        productsTableModel.setRowCount(0);
        
        try {
            Connection conn = DBConnection.getConnection();
            
            // Get top products by sales
            String productsQuery = "SELECT p.product_id, p.name, p.category, " +
                                    "SUM(oi.quantity) AS quantity_sold, " +
                                    "SUM(oi.price_at_order * oi.quantity) AS revenue " +
                                    "FROM order_items oi " +
                                    "JOIN products p ON oi.product_id = p.product_id " +
                                    "JOIN orders o ON oi.order_id = o.order_id " +
                                    "WHERE o.order_date BETWEEN ? AND ? AND o.status != 'CANCELLED' " +
                                    "GROUP BY p.product_id, p.name, p.category " +
                                    "ORDER BY revenue DESC " +
                                    "LIMIT 20";
            
            PreparedStatement productsStmt = conn.prepareStatement(productsQuery);
            productsStmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            productsStmt.setTimestamp(2, new Timestamp(endDate.getTime()));
            
            ResultSet productsRs = productsStmt.executeQuery();
            
            // Get total revenue for percentage calculation
            double totalRevenue = 0;
            List<ProductData> productsList = new ArrayList<>();
            
            while (productsRs.next()) {
                int productId = productsRs.getInt("product_id");
                String name = productsRs.getString("name");
                String category = productsRs.getString("category");
                int quantitySold = productsRs.getInt("quantity_sold");
                double revenue = productsRs.getDouble("revenue");
                
                totalRevenue += revenue;
                
                ProductData product = new ProductData(productId, name, category, quantitySold, revenue);
                productsList.add(product);
            }
            
            // Add rows to table with rank and percentage
            int rank = 1;
            for (ProductData product : productsList) {
                double percentage = totalRevenue > 0 ? (product.revenue / totalRevenue) * 100 : 0;
                
                Object[] row = {
                    rank,
                    product.productId,
                    product.name,
                    product.category,
                    product.quantitySold,
                    currencyFormat.format(product.revenue),
                    String.format("%.2f%%", percentage)
                };
                
                productsTableModel.addRow(row);
                rank++;
            }
            
            productsRs.close();
            productsStmt.close();
            
            // Update the products chart
            updateProductsChart(productsList, totalRevenue);
            
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading products data: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void loadCustomerAnalyticsData() {
        // Clear table
        customersTableModel.setRowCount(0);
        
        try {
            Connection conn = DBConnection.getConnection();
            
            // Get top customers by sales
            String customersQuery = "SELECT u.user_id AS customer_id, u.full_name, u.email, " +
                                    "COUNT(o.order_id) AS order_count, " +
                                    "SUM(o.total_amount) AS total_spent " +
                                    "FROM users u " +
                                    "JOIN orders o ON u.user_id = o.user_id " +
                                    "WHERE o.order_date BETWEEN ? AND ? AND o.status != 'CANCELLED' " +
                                    "GROUP BY u.user_id, u.full_name, u.email " +
                                    "ORDER BY total_spent DESC " +
                                    "LIMIT 20";
            
            PreparedStatement customersStmt = conn.prepareStatement(customersQuery);
            customersStmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            customersStmt.setTimestamp(2, new Timestamp(endDate.getTime()));
            
            ResultSet customersRs = customersStmt.executeQuery();
            
            // Customer analytics data for chart
            List<CustomerData> customersList = new ArrayList<>();
            
            int rank = 1;
            while (customersRs.next()) {
                int customerId = customersRs.getInt("customer_id");
                String fullName = customersRs.getString("full_name");
                String email = customersRs.getString("email");
                int orderCount = customersRs.getInt("order_count");
                double totalSpent = customersRs.getDouble("total_spent");
                double avgOrder = orderCount > 0 ? totalSpent / orderCount : 0;
                
                Object[] row = {
                    rank,
                    customerId,
                    fullName,
                    email,
                    orderCount,
                    currencyFormat.format(totalSpent),
                    currencyFormat.format(avgOrder)
                };
                
                customersTableModel.addRow(row);
                
                CustomerData customer = new CustomerData(customerId, fullName, email, orderCount, totalSpent);
                customersList.add(customer);
                
                rank++;
            }
            
            customersRs.close();
            customersStmt.close();
            
            // Update the customers chart
            updateCustomersChart(customersList);
            
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading customer data: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void updateSalesChart() {
        // Create a sales trend chart
        // This would typically use a charting library like JFreeChart
        // For this example, we'll use a placeholder
        salesChartPanel.removeAll();
        
        JPanel chartPlaceholder = new JPanel(new BorderLayout());
        chartPlaceholder.setBackground(new Color(50, 50, 50));
        
        JLabel chartLabel = new JLabel("Sales Chart: " + dateFormat.format(startDate) + " to " + dateFormat.format(endDate), SwingConstants.CENTER);
        chartLabel.setForeground(Color.WHITE);
        chartLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        chartPlaceholder.add(chartLabel, BorderLayout.NORTH);
        salesChartPanel.add(chartPlaceholder, BorderLayout.CENTER);
        salesChartPanel.revalidate();
        salesChartPanel.repaint();
    }
    
    private void updateProductsChart(List<ProductData> products, double totalRevenue) {
        // Create a pie chart for product distribution
        // This would typically use a charting library like JFreeChart
        // For this example, we'll use a placeholder
        productsPieChartPanel.removeAll();
        
        JPanel chartPlaceholder = new JPanel(new BorderLayout());
        chartPlaceholder.setBackground(new Color(50, 50, 50));
        
        JLabel chartLabel = new JLabel("Product Distribution Chart: Top " + products.size() + " Products", SwingConstants.CENTER);
        chartLabel.setForeground(Color.WHITE);
        chartLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        chartPlaceholder.add(chartLabel, BorderLayout.NORTH);
        productsPieChartPanel.add(chartPlaceholder, BorderLayout.CENTER);
        productsPieChartPanel.revalidate();
        productsPieChartPanel.repaint();
    }
    
    private void updateCustomersChart(List<CustomerData> customers) {
        // Create a chart for customer analytics
        // This would typically use a charting library like JFreeChart
        // For this example, we'll use a placeholder
        customersChartPanel.removeAll();
        
        JPanel chartPlaceholder = new JPanel(new BorderLayout());
        chartPlaceholder.setBackground(new Color(50, 50, 50));
        
        JLabel chartLabel = new JLabel("Customer Spending Analysis: Top " + customers.size() + " Customers", SwingConstants.CENTER);
        chartLabel.setForeground(Color.WHITE);
        chartLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        chartPlaceholder.add(chartLabel, BorderLayout.NORTH);
        customersChartPanel.add(chartPlaceholder, BorderLayout.CENTER);
        customersChartPanel.revalidate();
        customersChartPanel.repaint();
    }
    
    private void exportReportToPDF() {
        // Implement PDF export functionality
        // This would typically use a library like iText or Apache PDFBox
        JOptionPane.showMessageDialog(this,
            "PDF Export feature is being implemented.",
            "Export to PDF",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void printReport() {
        try {
            // Get the current tab
            int currentTab = tabbedPane.getSelectedIndex();
            
            if (currentTab == 0) {
                // Print sales report
                salesTable.print();
            } else if (currentTab == 1) {
                // Print products report
                productsTable.print();
            } else if (currentTab == 2) {
                // Print customers report
                customersTable.print();
            }
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this,
                "Error printing report: " + e.getMessage(),
                "Print Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Helper classes for data handling
    
    private class ProductData {
        int productId;
        String name;
        String category;
        int quantitySold;
        double revenue;
        
        public ProductData(int productId, String name, String category, int quantitySold, double revenue) {
            this.productId = productId;
            this.name = name;
            this.category = category;
            this.quantitySold = quantitySold;
            this.revenue = revenue;
        }
    }
    
    private class CustomerData {
        int customerId;
        String name;
        String email;
        int orderCount;
        double totalSpent;
        
        public CustomerData(int customerId, String name, String email, int orderCount, double totalSpent) {
            this.customerId = customerId;
            this.name = name;
            this.email = email;
            this.orderCount = orderCount;
            this.totalSpent = totalSpent;
        }
    }
    
    // Main method for testing
        public static void main(String[] args) {
            // Example user for testing
            User testAdmin = new User();
            testAdmin.setUserId(1);
            testAdmin.setUsername("admin");
            testAdmin.setAdmin(true);

            // Set look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            SwingUtilities.invokeLater(() -> {
                ReportsSystem reportsSystem = new ReportsSystem(testAdmin);
                reportsSystem.setVisible(true);
            });
        }
    }