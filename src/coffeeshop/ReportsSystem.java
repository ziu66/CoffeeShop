/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.awt.print.PrinterException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

public class ReportsSystem extends JFrame {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private JPanel salesReportPanel;
    private JPanel topProductsPanel;
    private JPanel customerAnalyticsPanel;

    private JComboBox<String> periodSelector;
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

    private List<DailySalesData> dailySalesDataList;
    private List<ProductData> topProductsDataList;
    private List<CustomerData> topCustomersDataList;

    private double loadedTotalSales;
    private int loadedTotalOrders;
    private double loadedAvgOrderValue;

    public ReportsSystem(JFrame parent, User admin) {
        this.currentAdmin = admin;
        setAlwaysOnTop(true);

        // Set up the frame for full screen
        setTitle("But First, Coffee - Reports & Analytics");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize window
        setUndecorated(false); // Keep window decorations (title bar, etc.)
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        
        // Add window listener for proper closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        // Add escape key to close window
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel");
        getRootPane().getActionMap().put("Cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        

        // Initialize dates (default: Last 30 Days)
        Calendar cal = Calendar.getInstance();
        endDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -29);
        startDate = cal.getTime();

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endCal.set(Calendar.MILLISECOND, 999);
        endDate = endCal.getTime();

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

        // Add panels to tabbed pane with safe icon loading
        tabbedPane.addTab("Sales Overview", createTabIcon("/images/sales_icon_small.png"), salesReportPanel);
        tabbedPane.addTab("Top Products", createTabIcon("/images/product_icon_small.png"), topProductsPanel);
        tabbedPane.addTab("Customer Analytics", createTabIcon("/images/customer_icon_small.png"), customerAnalyticsPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Create bottom button panel
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

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
         // Set default selected item based on initial date range (Last 30 Days)
        periodSelector.setSelectedItem("Last 30 Days");

        periodSelector.setPreferredSize(new Dimension(150, 30));
        periodSelector.setBackground(new Color(60, 60, 60));
        periodSelector.setForeground(Color.WHITE);
        periodSelector.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        periodSelector.addActionListener(e -> {
            if (periodSelector.getSelectedItem().equals("Custom Period")) {
                showDateRangePicker();
            } else {
                updateDateRange((String) periodSelector.getSelectedItem());
                 // Automatically generate report when a predefined period is selected
                 loadReportData();
            }
        });

        controlsPanel.add(periodLabel);
        controlsPanel.add(Box.createHorizontalStrut(10));
        controlsPanel.add(periodSelector);
        controlsPanel.add(Box.createHorizontalStrut(15));
        // The Generate Report button's primary purpose is after the custom date picker,
        // but keeping it visible and functional for other periods is also fine.
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

        btnPrint = createStyledButton("Print Report", new Color(70, 130, 180));
        btnBack = createStyledButton("Back to Dashboard", new Color(218, 165, 32));

        btnPrint.addActionListener(e -> printReport());
        btnBack.addActionListener(e -> dispose());

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
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Summary cards panel (unchanged)
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        summaryPanel.setBackground(new Color(40, 40, 40));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        lblTotalSales = new JLabel("₱0.00", SwingConstants.CENTER);
        lblTotalOrders = new JLabel("0", SwingConstants.CENTER);
        lblAvgOrderValue = new JLabel("₱0.00", SwingConstants.CENTER);

        summaryPanel.add(createSummaryCard("Total Sales", lblTotalSales, "/images/sales_icon.png"));
        summaryPanel.add(createSummaryCard("Total Orders", lblTotalOrders, "/images/orders_icon.png"));
        summaryPanel.add(createSummaryCard("Avg Order Value", lblAvgOrderValue, "/images/avg_order_icon.png"));

        panel.add(summaryPanel, BorderLayout.NORTH);

        // Create chart panel with BoxLayout for better control
        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        verticalSplit.setDividerLocation(0.75); // 75% for chart, 25% for table
        verticalSplit.setResizeWeight(0.75); // Chart gets priority in resizing
        verticalSplit.setDividerSize(5);
        verticalSplit.setBackground(new Color(40, 40, 40));
        verticalSplit.setBorder(null);

        // Chart panel setup (unchanged)
        salesChartPanel = new JPanel(new BorderLayout());
        salesChartPanel.setBackground(new Color(50, 50, 50));
        salesChartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            "Sales Trend",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            Color.WHITE
        ));
        salesChartPanel.setPreferredSize(new Dimension(800, 400)); // Set preferred size

        // Create table panel
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
        tablePanel.setPreferredSize(new Dimension(tablePanel.getPreferredSize().width, 150));
           

        // Create and configure sales table
        String[] salesColumns = {"Date", "Orders", "Items Sold", "Total Sales", "Avg Order Value"};
        salesTableModel = new DefaultTableModel(salesColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1 || columnIndex == 2) return Integer.class;
                if (columnIndex == 3 || columnIndex == 4) return Double.class;
                return super.getColumnClass(columnIndex);
            }
        };

        salesTable = new JTable(salesTableModel);
        styleTable(salesTable);
        JScrollPane tableScrollPane = new JScrollPane(salesTable);
        tableScrollPane.setBackground(new Color(50, 50, 50));
        tableScrollPane.getViewport().setBackground(new Color(50, 50, 50));

        // *** ADDED Preferred Size for Scroll Pane (kept from previous fix) ***
        // This helps ensure the table area gets a minimum amount of vertical space.
        tableScrollPane.setPreferredSize(new Dimension(tableScrollPane.getPreferredSize().width, 250)); // Adjust height as needed

        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Create content panel with chart and table
         JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(new Color(40, 40, 40));

        // Add chart panel with constraints
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.add(salesChartPanel, BorderLayout.CENTER);
        contentPanel.add(chartContainer, BorderLayout.CENTER);

        // Add table panel with constraints
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.add(tablePanel, BorderLayout.CENTER);
        tableContainer.setPreferredSize(new Dimension(tableContainer.getPreferredSize().width, 250));
        contentPanel.add(tableContainer, BorderLayout.SOUTH);

        verticalSplit.setTopComponent(salesChartPanel);
        verticalSplit.setBottomComponent(tablePanel);

        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(verticalSplit, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTopProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create split pane for chart and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(0.6); // Initial split location
        splitPane.setResizeWeight(0.5); // Give both sides equal resize weight
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
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 4) {
                    return Integer.class;
                }
                if (columnIndex == 5) {
                    return Double.class;
                }
                if (columnIndex == 6) {
                    return String.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        // Create table
        productsTable = new JTable(productsTableModel);
        styleTable(productsTable);

        JScrollPane tableScrollPane = new JScrollPane(productsTable);
        tableScrollPane.setBackground(new Color(50, 50, 50));
        tableScrollPane.getViewport().setBackground(new Color(50, 50, 50));

        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Corrected split pane components - using productsPieChartPanel instead of salesChartPanel
        splitPane.setLeftComponent(productsPieChartPanel);
        splitPane.setRightComponent(tablePanel);

        // Removed summaryPanel reference since it's not needed in this tab
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCustomerAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create split pane for chart and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(0.7); // Initial split location
        splitPane.setResizeWeight(0.5); // Give both sides equal resize weight
        splitPane.setDividerSize(5);
        splitPane.setBackground(new Color(40, 40, 40));
        splitPane.setBorder(null);

        // Customers chart panel
        customersChartPanel = new JPanel(new BorderLayout());
        customersChartPanel.setBackground(new Color(50, 50, 50));
        customersChartPanel.setMinimumSize(new Dimension(400, 300));
        customersChartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            "Customer Spending (Top 20)",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            Color.WHITE
        ));

        // Add resize listener
        customersChartPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateCustomersChart(topCustomersDataList);
            }
        });

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

        String[] customersColumns = {"Rank", "Customer ID", "Customer Name", "Email", "Orders", "Total Spent", "Avg Order"};
        customersTableModel = new DefaultTableModel(customersColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 4) return Integer.class;
                if (columnIndex == 5 || columnIndex == 6) return Double.class;
                return super.getColumnClass(columnIndex);
            }
        };

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

     // Helper to load icons safely
    private ImageIcon loadIcon(String path, int size) {
            try {
                java.net.URL iconUrl = getClass().getResource(path);
                if (iconUrl != null) {
                    ImageIcon icon = new ImageIcon(iconUrl);
                    if (size > 0) {
                        Image scaledIcon = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledIcon);
                    }
                    return icon;
                }
                // Return blank icon if not found
                return new ImageIcon(new BufferedImage(size > 0 ? size : 16, size > 0 ? size : 16, BufferedImage.TYPE_INT_ARGB));
            } catch (Exception e) {
                e.printStackTrace();
                return new ImageIcon(new BufferedImage(size > 0 ? size : 16, size > 0 ? size : 16, BufferedImage.TYPE_INT_ARGB));
            }
        }

    private JPanel createSummaryCard(String title, JLabel valueLabel, String iconPath) {
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
        ImageIcon icon = loadIcon(iconPath, 24);
        if (icon != null) {
            iconLabel.setIcon(icon);
        } else {
            // Handle icon not found - use text alternative
            iconLabel.setText("•");
            iconLabel.setForeground(new Color(218, 165, 32));
            iconLabel.setFont(new Font("Arial", Font.BOLD, 24));
        }

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);

        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);

        // Value panel - now uses the passed JLabel
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        valuePanel.setOpaque(false);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
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
            BorderFactory.createLineBorder(new Color(Math.min(bgColor.getRed() + 20, 255), Math.min(bgColor.getGreen() + 20, 255), Math.min(bgColor.getBlue() + 20, 255)), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(
                    Math.min(bgColor.getRed() + 30, 255), // Slightly lighter on hover
                    Math.min(bgColor.getGreen() + 30, 255),
                    Math.min(bgColor.getBlue() + 30, 255)
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
        table.setRowHeight(20);
        table.setShowVerticalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(30, 30, 30));
        header.setForeground(new Color(218, 165, 32));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
         header.setPreferredSize(new Dimension(header.getPreferredSize().width, 22)); // Increase header height
        header.setReorderingAllowed(false); // Prevent column reordering
        header.setResizingAllowed(true); // Allow column resizing

        // Align header text to center
         DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Create renderers for different alignments and backgrounds
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
         centerRenderer.setBackground(new Color(50, 50, 50)); // Match table background
        centerRenderer.setForeground(Color.WHITE); // Match table foreground

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBackground(new Color(50, 50, 50)); // Match table background
        leftRenderer.setForeground(Color.WHITE); // Match table foreground

         // Currency renderer
         CurrencyTableCellRenderer currencyRenderer = new CurrencyTableCellRenderer(currencyFormat);


        // Apply renderers based on column type/content
         // This needs to be applied *after* the model is set and populated,
         // but defining it here means we can reuse the renderers.
         // The actual application happens in populateTable methods or after.

         // Example application (can also be done after rows are added):
         TableColumnModel columnModel = table.getColumnModel();

         if (table.getModel() == salesTableModel) {
             // columnModel.getColumn(0) default left for Date string
             columnModel.getColumn(1).setCellRenderer(centerRenderer); // Orders
             columnModel.getColumn(2).setCellRenderer(centerRenderer); // Items Sold
             columnModel.getColumn(3).setCellRenderer(currencyRenderer); // Total Sales
             columnModel.getColumn(4).setCellRenderer(currencyRenderer); // Avg Order Value
         } else if (table.getModel() == productsTableModel) {
             columnModel.getColumn(0).setCellRenderer(centerRenderer); // Rank
             columnModel.getColumn(1).setCellRenderer(centerRenderer); // Product ID
              // columnModel.getColumn(2) default left for Name
              // columnModel.getColumn(3) default left for Category
             columnModel.getColumn(4).setCellRenderer(centerRenderer); // Quantity Sold
             columnModel.getColumn(5).setCellRenderer(currencyRenderer); // Revenue
             columnModel.getColumn(6).setCellRenderer(centerRenderer); // Percentage String
         } else if (table.getModel() == customersTableModel) {
             columnModel.getColumn(0).setCellRenderer(centerRenderer); // Rank
             columnModel.getColumn(1).setCellRenderer(centerRenderer); // Customer ID
              // columnModel.getColumn(2) default left for Name
              // columnModel.getColumn(3) default left for Email
             columnModel.getColumn(4).setCellRenderer(centerRenderer); // Orders
             columnModel.getColumn(5).setCellRenderer(currencyRenderer); // Total Spent
             columnModel.getColumn(6).setCellRenderer(currencyRenderer); // Avg Order
         }
    }

     // Custom Cell Renderer for currency values - Added background/foreground handling for selected/non-selected state
    private static class CurrencyTableCellRenderer extends DefaultTableCellRenderer {
        private final DecimalFormat formatter;
        private final Color nonSelectedBackground = new Color(50, 50, 50);
        private final Color nonSelectedForeground = Color.WHITE;

        public CurrencyTableCellRenderer(DecimalFormat formatter) {
            this.formatter = formatter;
            setHorizontalAlignment(JLabel.RIGHT); // Align numbers to the right
             setOpaque(true); // Ensure background color is painted
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Let super handle the basic rendering (font, border, selection colors)
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Manually set background and foreground if not selected
             // This is needed because DefaultTableCellRenderer might not fully override
             // default L&F colors, especially in custom themes.
             if (!isSelected) {
                 setBackground(nonSelectedBackground);
                 setForeground(nonSelectedForeground);
             } // else: super.getTableCellRendererComponent should handle selected colors

            String text;
            if (value instanceof Number) {
                text = formatter.format(value);
            } else if (value == null || (value instanceof String && ((String)value).trim().isEmpty())) {
                 text = formatter.format(0.0); // Display 0.00 if value is null or empty
             } else {
                 text = value.toString(); // Fallback for other types (shouldn't happen with Doubles)
             }

             setText(text); // Set the formatted text

            return this;
        }
    }


    private Icon createTabIcon(String iconPath) {
         ImageIcon icon = loadIcon(iconPath, 16); // Use the helper
         // If icon loading failed, loadIcon returns null.
         // We can return null or a default blank icon if needed, null is fine for JTabbedPane.
         return icon;
    }

    private void updateDateRange(String period) {
        Calendar cal = Calendar.getInstance();

        // Set start of day for 'today's' date reference
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date nowAtStartOfDay = cal.getTime(); // Reference point for periods relative to today

        switch (period) {
            case "Today":
                startDate = nowAtStartOfDay;
                endDate = Calendar.getInstance().getTime(); // Set end date to current time
                break;
            case "Yesterday":
                Calendar yesterdayStart = (Calendar) cal.clone();
                yesterdayStart.add(Calendar.DAY_OF_MONTH, -1);
                startDate = yesterdayStart.getTime(); // Start date is yesterday (start of day)

                Calendar yesterdayEnd = (Calendar) cal.clone(); // Start with today's start
                yesterdayEnd.add(Calendar.MILLISECOND, -1); // Go back to end of yesterday
                endDate = yesterdayEnd.getTime(); // End date is end of yesterday
                break;
            case "Last 7 Days":
                cal.add(Calendar.DAY_OF_MONTH, -6); // Today + 6 previous days = 7 days
                startDate = cal.getTime();
                 endDate = Calendar.getInstance().getTime(); // Set end date to current time
                break;
            case "Last 30 Days":
                cal.add(Calendar.DAY_OF_MONTH, -29); // Today + 29 previous days = 30 days
                startDate = cal.getTime();
                 endDate = Calendar.getInstance().getTime(); // Set end date to current time
                break;
            case "This Month":
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = cal.getTime();
                 endDate = Calendar.getInstance().getTime(); // Set end date to current time
                break;
            case "Last Month":
                cal.set(Calendar.DAY_OF_MONTH, 1); // First day of this month (start of day)
                cal.add(Calendar.MILLISECOND, -1); // Go back to end of last month
                endDate = cal.getTime(); // End date is end of previous month
                 // Now find the start date (first day of previous month)
                Calendar lastMonthStart = Calendar.getInstance();
                lastMonthStart.setTime(endDate); // Set calendar to end of last month
                lastMonthStart.set(Calendar.DAY_OF_MONTH, 1); // Go to first day of last month
                 lastMonthStart.set(Calendar.HOUR_OF_DAY, 0);
                 lastMonthStart.set(Calendar.MINUTE, 0);
                 lastMonthStart.set(Calendar.SECOND, 0);
                 lastMonthStart.set(Calendar.MILLISECOND, 0);
                startDate = lastMonthStart.getTime();
                break;
            case "This Year":
                cal.set(Calendar.DAY_OF_YEAR, 1);
                startDate = cal.getTime();
                 endDate = Calendar.getInstance().getTime(); // Set end date to current time
                break;
            default: // Should not happen with predefined periods
                 // Fallback to last 30 days
                cal = Calendar.getInstance();
                endDate = cal.getTime();
                cal.add(Calendar.DAY_OF_MONTH, -29);
                startDate = cal.getTime();
                break;
        }

         // Ensure endDate is always the end of the day unless it's 'Today' (which is current time)
         // Or specifically 'Yesterday' or 'Last Month' which have their end bounds set
         if (!period.equals("Today") && !period.equals("Yesterday") && !period.equals("Last Month")) {
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate); // Start with the calculated end date
            endCal.set(Calendar.HOUR_OF_DAY, 23);
            endCal.set(Calendar.MINUTE, 59);
            endCal.set(Calendar.SECOND, 59);
            endCal.set(Calendar.MILLISECOND, 999);
            endDate = endCal.getTime();
         }


         System.out.println("Report Period: " + dateFormat.format(startDate) + " to " + dateFormat.format(endDate)); // Debugging dates
    }

    private void showDateRangePicker() {
        JDialog dialog = new JDialog(this, "Select Date Range", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 280); // Increased height
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(40, 40, 40));

        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 15)); // Increased vertical gap
        contentPanel.setBackground(new Color(40, 40, 40));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Start date panel
        JPanel startDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startDatePanel.setBackground(new Color(40, 40, 40));
        JLabel startLabel = new JLabel("Start Date:");
        startLabel.setForeground(Color.WHITE);
        startLabel.setPreferredSize(new Dimension(80, 25)); // Fixed label size for alignment

        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setValue(startDate); // Pre-fill with current start date
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(startEditor);
        startDateSpinner.setPreferredSize(new Dimension(150, 28)); // Increased spinner size


        startDatePanel.add(startLabel);
        startDatePanel.add(startDateSpinner);

        // End date panel
        JPanel endDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        endDatePanel.setBackground(new Color(40, 40, 40));
        JLabel endLabel = new JLabel("End Date:");
        endLabel.setForeground(Color.WHITE);
        endLabel.setPreferredSize(new Dimension(80, 25)); // Fixed label size for alignment


        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setValue(endDate); // Pre-fill with current end date
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        endDateSpinner.setEditor(endEditor);
         endDateSpinner.setPreferredSize(new Dimension(150, 28)); // Increased spinner size


        endDatePanel.add(endLabel);
        endDatePanel.add(endDateSpinner);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0)); // Centered buttons
        buttonPanel.setBackground(new Color(40, 40, 40));

        JButton applyButton = createStyledButton("Apply", new Color(70, 130, 180));
        JButton cancelButton = createStyledButton("Cancel", new Color(130, 70, 70));

        applyButton.addActionListener(e -> {
            Date selectedStartDate = (Date) startDateSpinner.getValue();
            Date selectedEndDate = (Date) endDateSpinner.getValue();

            // Set end date to end of day for BETWEEN query correctness
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(selectedEndDate);
            endCal.set(Calendar.HOUR_OF_DAY, 23);
            endCal.set(Calendar.MINUTE, 59);
            endCal.set(Calendar.SECOND, 59);
            endCal.set(Calendar.MILLISECOND, 999);
            selectedEndDate = endCal.getTime();

             // Set start date to start of day for BETWEEN query correctness
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(selectedStartDate);
            startCal.set(Calendar.HOUR_OF_DAY, 0);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);
            selectedStartDate = startCal.getTime();


            // Validate date range
            if (selectedStartDate.after(selectedEndDate)) {
                JOptionPane.showMessageDialog(dialog, "Start date cannot be after end date.", "Invalid Date Range", JOptionPane.WARNING_MESSAGE);
                return; // Do not close dialog or apply dates
            }

            startDate = selectedStartDate;
            endDate = selectedEndDate;

            // Update selector text to "Custom Period"
            periodSelector.setSelectedItem("Custom Period");

            dialog.dispose();
             // Automatically generate report after applying custom period
             loadReportData();
        });

        cancelButton.addActionListener(e -> {
            dialog.dispose();
        });

        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);

        contentPanel.add(startDatePanel);
        contentPanel.add(endDatePanel);
        contentPanel.add(buttonPanel);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void loadReportData() {
        // Show a loading indicator
        JDialog loadingDialog = new JDialog(this, "Loading Reports", true); // Make it modal
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // Prevent closing while loading
        loadingDialog.setSize(300, 120); // Increased size slightly
        loadingDialog.setLocationRelativeTo(this);
        loadingDialog.getContentPane().setBackground(new Color(50, 50, 50));
        loadingDialog.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        JLabel loadingLabel = new JLabel("Generating reports... Please wait.");
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loadingDialog.add(loadingLabel);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(250, 20));
        loadingDialog.add(progressBar);


        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Connection conn = null;
                try {
                     conn = DBConnection.getConnection();
                     if (conn == null) {
                         throw new SQLException("Database connection failed.");
                     }

                     // Convert java.util.Date to java.sql.Timestamp for BETWEEN clause
                     Timestamp startTimestamp = new Timestamp(startDate.getTime());
                     Timestamp endTimestamp = new Timestamp(endDate.getTime());

                     // --- Load Sales Data ---
                     // Get sales summary
                     String summaryQuery = "SELECT COUNT(DISTINCT o.order_id) AS totalOrders, COALESCE(SUM(o.total_amount), 0) AS totalSales " +
                                            "FROM orders o " +
                                            "WHERE o.order_date BETWEEN ? AND ? AND o.status != 'CANCELLED'";

                     PreparedStatement summaryStmt = conn.prepareStatement(summaryQuery);
                     summaryStmt.setTimestamp(1, startTimestamp);
                    summaryStmt.setTimestamp(2, endTimestamp);

                     ResultSet summaryRs = summaryStmt.executeQuery();

                     if (summaryRs.next()) {
                         loadedTotalOrders = summaryRs.getInt("totalOrders");
                         loadedTotalSales = summaryRs.getDouble("totalSales");
                         loadedAvgOrderValue = loadedTotalOrders > 0 ? loadedTotalSales / loadedTotalOrders : 0;
                     } else {
                         loadedTotalOrders = 0;
                         loadedTotalSales = 0.0;
                         loadedAvgOrderValue = 0.0;
                     }
                     summaryRs.close();
                     summaryStmt.close();

                     // Get daily sales data for the report period
                     // NOTE: The LEFT JOIN is important here to include dates with 0 sales if needed,
                     // but the query aggregates by DATE(o.order_date), so only dates with orders
                     // will appear. If you need *all* dates in the range, you'd need a date-generating
                     // subquery or calendar table and LEFT JOIN to it.
                     String salesQuery = "SELECT DATE(o.order_date) AS sale_date, " +
                                            "COUNT(DISTINCT o.order_id) AS num_orders, " +
                                            "COALESCE(SUM(oi.quantity), 0) AS items_sold, " +
                                            "COALESCE(SUM(o.total_amount), 0) AS total_sales " +
                                            "FROM orders o " +
                                            "LEFT JOIN order_items oi ON o.order_id = oi.order_id " +
                                            "WHERE o.order_date BETWEEN ? AND ? AND o.status != 'CANCELLED' " +
                                            "GROUP BY DATE(o.order_date) " +
                                            "ORDER BY sale_date";

                     PreparedStatement salesStmt = conn.prepareStatement(salesQuery);
                     salesStmt.setTimestamp(1, startTimestamp);
                    salesStmt.setTimestamp(2, endTimestamp);
                     ResultSet salesRs = salesStmt.executeQuery();

                     dailySalesDataList = new ArrayList<>(); // Clear previous list
                     while (salesRs.next()) {
                         Date saleDate = salesRs.getDate("sale_date");
                         int numOrders = salesRs.getInt("num_orders");
                         int itemsSold = salesRs.getInt("items_sold");
                         double dailyTotalSales = salesRs.getDouble("total_sales");

                         dailySalesDataList.add(new DailySalesData(saleDate, numOrders, itemsSold, dailyTotalSales));
                     }
                     salesRs.close();
                     salesStmt.close();


                     // --- Load Top Products Data ---
                     topProductsDataList = new ArrayList<>(); // Clear previous list

                     String productsQuery = "SELECT p.product_id, p.name, p.category, " +
                                            "COALESCE(SUM(oi.quantity), 0) AS quantity_sold, " +
                                            "COALESCE(SUM(oi.price_at_order * oi.quantity), 0) AS revenue " +
                                            "FROM order_items oi " +
                                            "JOIN products p ON oi.product_id = p.product_id " +
                                            "JOIN orders o ON oi.order_id = o.order_id " +
                                            "WHERE o.order_date BETWEEN ? AND ? AND o.status != 'CANCELLED' " +
                                            "GROUP BY p.product_id, p.name, p.category " +
                                            "ORDER BY revenue DESC " +
                                            "LIMIT 20";

                     PreparedStatement productsStmt = conn.prepareStatement(productsQuery);
                     productsStmt.setTimestamp(1, startTimestamp);
                     productsStmt.setTimestamp(2, endTimestamp);
                     ResultSet productsRs = productsStmt.executeQuery();

                     while (productsRs.next()) {
                          int productId = productsRs.getInt("product_id");
                          String name = productsRs.getString("name");
                          String category = productsRs.getString("category");
                          int quantitySold = productsRs.getInt("quantity_sold");
                          double revenue = productsRs.getDouble("revenue");

                          topProductsDataList.add(new ProductData(productId, name, category, quantitySold, revenue));
                     }
                     productsRs.close();
                     productsStmt.close();

                     topCustomersDataList = new ArrayList<>(); // Clear previous list

                     String customersQuery = "SELECT u.user_id AS customer_id, u.full_name, u.email, " +
                                            "COUNT(o.order_id) AS order_count, " +
                                            "COALESCE(SUM(o.total_amount), 0) AS total_spent " +
                                            "FROM users u " +
                                            "JOIN orders o ON u.user_id = o.user_id " +
                                            "WHERE o.order_date BETWEEN ? AND ? AND o.status != 'CANCELLED' " +
                                            "GROUP BY u.user_id, u.full_name, u.email " +
                                            "ORDER BY total_spent DESC " +
                                            "LIMIT 20";

                     PreparedStatement customersStmt = conn.prepareStatement(customersQuery);
                     customersStmt.setTimestamp(1, startTimestamp);
                     customersStmt.setTimestamp(2, endTimestamp);
                     ResultSet customersRs = customersStmt.executeQuery();

                     while (customersRs.next()) {
                         int customerId = customersRs.getInt("customer_id");
                         String fullName = customersRs.getString("full_name");
                         String email = customersRs.getString("email");
                         int orderCount = customersRs.getInt("order_count");
                         double totalSpent = customersRs.getDouble("total_spent");

                         topCustomersDataList.add(new CustomerData(customerId, fullName, email, orderCount, totalSpent));
                     }
                     customersRs.close();
                     customersStmt.close();

                 } catch (SQLException ex) {
                     // Log the database error
                     ex.printStackTrace();
                     // Rethrow to be caught by done()
                     throw ex;
                 } finally {
                     if (conn != null) {
                         try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
                     }
                 }
                 return null; // Return null as required by SwingWorker<Void, Void>
            }

            @Override
            protected void done() {
                loadingDialog.dispose(); // Close loading dialog

                // --- Update UI on the EDT ---
                // This method runs automatically on the EDT after doInBackground finishes.
                try {
                    // Check for errors from doInBackground
                    get(); // This will re-throw any exceptions caught in doInBackground

                    // Update summary cards
                     lblTotalSales.setText(currencyFormat.format(loadedTotalSales));
                     lblTotalOrders.setText(String.valueOf(loadedTotalOrders));
                     lblAvgOrderValue.setText(currencyFormat.format(loadedAvgOrderValue));

                      // Populate tables from the data lists
                     populateSalesTableFromList();
                     populateProductsTableFromList();
                     populateCustomersTableFromList();

                     // Update charts
                     updateSalesChart();
                     updateProductsChart(topProductsDataList); // Pass the list
                     updateCustomersChart(topCustomersDataList); // Pass the list

                } catch (java.util.concurrent.ExecutionException ex) {
                     // Exception occurred in doInBackground (e.g., SQLException)
                     Throwable cause = ex.getCause();
                     cause.printStackTrace();
                     JOptionPane.showMessageDialog(ReportsSystem.this,
                        "An error occurred while loading data:\n" + cause.getMessage(),
                        "Data Loading Error",
                        JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    // Other exceptions that might occur in done() (e.g., during UI updates)
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ReportsSystem.this,
                       "An error occurred while updating the UI:\n" + ex.getMessage(),
                       "UI Update Error",
                       JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
        loadingDialog.setVisible(true); // Show dialog while worker is running
    }

     // Method to populate Sales Table from dailySalesDataList
     private void populateSalesTableFromList() {
         salesTableModel.setRowCount(0); // Clear table
         // Check if list is empty or null before processing
         if (dailySalesDataList != null && !dailySalesDataList.isEmpty()) {
             // Ensure data is sorted by date for the table (should be from query ORDER BY, but sort again for safety)
             Collections.sort(dailySalesDataList, Comparator.comparing(DailySalesData::getSaleDate));
             for (DailySalesData data : dailySalesDataList) {
                 double avgOrderValue = data.getNumOrders() > 0 ? data.getTotalSales() / data.getNumOrders() : 0;
                 Object[] row = {
                     dateFormat.format(data.getSaleDate()),
                     data.getNumOrders(),
                     data.getItemsSold(),
                     data.getTotalSales(), // Raw double for sorting/rendering
                     avgOrderValue         // Raw double for sorting/rendering
                 };
                 salesTableModel.addRow(row);
             }
         } else {
              // Add a placeholder row if no data
             salesTableModel.addRow(new Object[]{"No Data Available", 0, 0, 0.0, 0.0});
         }
         styleTable(salesTable); // Re-apply styling after populating
     }

      // Method to populate Products Table from topProductsDataList
      private void populateProductsTableFromList() {
          productsTableModel.setRowCount(0); // Clear table
           // Check if list is empty or null before processing
          if (topProductsDataList != null && !topProductsDataList.isEmpty()) {
              // Need total revenue for percentage calculation *from the entire fetched list*
              double totalRevenuePeriod = topProductsDataList.stream().mapToDouble(ProductData::getRevenue).sum();

              // Ensure data is sorted by revenue for the table ranking (should be from query, but sort again)
              Collections.sort(topProductsDataList, Comparator.comparingDouble(ProductData::getRevenue).reversed());

              int rank = 1;
              for (ProductData product : topProductsDataList) {
                   // Only add rows for products with some activity (quantity > 0 or revenue > 0)
                   if (product.getQuantitySold() > 0 || product.getRevenue() > 0) {
                       double percentage = totalRevenuePeriod > 0 ? (product.getRevenue() / totalRevenuePeriod) * 100 : 0;
                        Object[] row = {
                            rank,
                            product.getProductId(),
                            product.getName(),
                            product.getCategory(),
                            product.getQuantitySold(),
                            product.getRevenue(), // Raw double for sorting/rendering
                            String.format("%.2f%%", percentage) // Formatted string
                        };
                        productsTableModel.addRow(row);
                        rank++; // Increment rank only for added rows
                   }
              }
          }
           // Add placeholder row if table is still empty after filtering
          if (productsTableModel.getRowCount() == 0) {
              productsTableModel.addRow(new Object[]{1, "N/A", "No Product Data Available", "N/A", 0, 0.0, "0.00%"});
          }
          styleTable(productsTable); // Re-apply styling after populating
      }

      // Method to populate Customers Table from topCustomersDataList
      private void populateCustomersTableFromList() {
          customersTableModel.setRowCount(0); // Clear table
           // Check if list is empty or null before processing
          if (topCustomersDataList != null && !topCustomersDataList.isEmpty()) {
               // Ensure data is sorted by total spent for the table ranking (should be from query, but sort again)
              Collections.sort(topCustomersDataList, Comparator.comparingDouble(CustomerData::getTotalSpent).reversed());

              int rank = 1;
              for (CustomerData customer : topCustomersDataList) {
                  // Only add rows for customers with some spending or orders
                  if (customer.getOrderCount() > 0 || customer.getTotalSpent() > 0) {
                       double avgOrder = customer.getOrderCount() > 0 ? customer.getTotalSpent() / customer.getOrderCount() : 0;
                       Object[] row = {
                           rank,
                           customer.getCustomerId(),
                           customer.getName(),
                           customer.getEmail(),
                           customer.getOrderCount(),
                           customer.getTotalSpent(), // Raw double for sorting/rendering
                           avgOrder    // Raw double for sorting/rendering
                       };
                       customersTableModel.addRow(row);
                       rank++; // Increment rank only for added rows
                  }
              }
          }
           // Add placeholder row if table is still empty after filtering
          if (customersTableModel.getRowCount() == 0) {
              customersTableModel.addRow(new Object[]{1, "N/A", "No Customer Data Available", "N/A", 0, 0.0, 0.0});
          }
          styleTable(customersTable); // Re-apply styling after populating
      }

    private void updateSalesChart() {
       salesChartPanel.removeAll();
       salesChartPanel.setLayout(new BorderLayout());

       // Check for empty data
       boolean noDataForChart = (dailySalesDataList == null || dailySalesDataList.isEmpty() ||
                              dailySalesDataList.stream().allMatch(data -> data.getTotalSales() == 0.0));

       if (noDataForChart) {
           JPanel placeholderPanel = new JPanel(new BorderLayout());
           placeholderPanel.setBackground(new Color(50, 50, 50));
           JLabel chartLabel = new JLabel("No Sales Data Available for this Period", SwingConstants.CENTER);
           chartLabel.setForeground(Color.WHITE);
           chartLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
           placeholderPanel.add(chartLabel, BorderLayout.CENTER);
           salesChartPanel.add(placeholderPanel, BorderLayout.CENTER);
           salesChartPanel.revalidate();
           salesChartPanel.repaint();
           return;
       }

       DefaultCategoryDataset dataset = new DefaultCategoryDataset();
       String seriesName = "Total Sales";

       for (DailySalesData data : dailySalesDataList) {
           if (data.getTotalSales() >= 0) {
               dataset.addValue(data.getTotalSales(), seriesName, dateFormat.format(data.getSaleDate()));
           }
       }

       if (dataset.getColumnKeys().isEmpty()) {
           JPanel placeholderPanel = new JPanel(new BorderLayout());
           placeholderPanel.setBackground(new Color(50, 50, 50));
           JLabel chartLabel = new JLabel("No Sales Data Available for this Period", SwingConstants.CENTER);
           chartLabel.setForeground(Color.WHITE);
           chartLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
           placeholderPanel.add(chartLabel, BorderLayout.CENTER);
           salesChartPanel.add(placeholderPanel, BorderLayout.CENTER);
           salesChartPanel.revalidate();
           salesChartPanel.repaint();
           return;
       }

       // Create chart with all dark theme customizations
       JFreeChart chart = ChartFactory.createLineChart(
           null,
           "Date", 
           "Total Sales (₱)",
           dataset,
           PlotOrientation.VERTICAL,
           true,
           true,
           false
       );

       // DARK THEME CUSTOMIZATIONS
       chart.setBackgroundPaint(new Color(50, 50, 50));
       chart.setAntiAlias(true);
       chart.setTextAntiAlias(true);

       CategoryPlot plot = (CategoryPlot) chart.getPlot();
       plot.setBackgroundPaint(new Color(50, 50, 50));
       plot.setDomainGridlinePaint(new Color(80, 80, 80));
       plot.setRangeGridlinePaint(new Color(80, 80, 80));
       plot.setOutlinePaint(null);
       plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0)); // Remove extra space

       // Domain Axis (X-axis - Dates)
       CategoryAxis domainAxis = plot.getDomainAxis();
       domainAxis.setLabelPaint(Color.WHITE);
       domainAxis.setTickLabelPaint(Color.WHITE);
       domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
       domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
       domainAxis.setAxisLinePaint(new Color(100, 100, 100)); // Visible axis line
       domainAxis.setLowerMargin(0.02);
       domainAxis.setUpperMargin(0.02);

       // Range Axis (Y-axis - Values)
       NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
       rangeAxis.setLabelPaint(Color.WHITE);
       rangeAxis.setTickLabelPaint(Color.WHITE);
       rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
       rangeAxis.setNumberFormatOverride(currencyFormat);
       rangeAxis.setAxisLinePaint(new Color(100, 100, 100)); // Visible axis line
       rangeAxis.setAutoRangeIncludesZero(true);
       rangeAxis.setUpperMargin(0.1);

       // Line Renderer Customization
       LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
       renderer.setSeriesPaint(0, new Color(100, 180, 255));
       renderer.setSeriesStroke(0, new BasicStroke(2.0f));
       renderer.setSeriesShapesVisible(0, true);
       renderer.setSeriesShapesFilled(0, true);
       renderer.setSeriesShape(0, new Ellipse2D.Double(-2.5, -2.5, 5, 5));

       // Legend Customization
       LegendTitle legend = chart.getLegend();
       legend.setItemPaint(Color.WHITE);
       legend.setItemFont(new Font("Segoe UI", Font.PLAIN, 11));
       legend.setBackgroundPaint(new Color(60, 60, 60, 200));
       legend.setPosition(RectangleEdge.BOTTOM);

       // Create the chart panel with proper sizing
       ChartPanel chartPanel = new ChartPanel(chart) {
           @Override
           public Dimension getPreferredSize() {
               return new Dimension(
                   Math.max(salesChartPanel.getWidth() - 20, 100),
                   Math.max(salesChartPanel.getHeight() - 20, 100)
               );
           }

           @Override
           public void paintComponent(Graphics g) {
               Graphics2D g2 = (Graphics2D) g;
               g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
               super.paintComponent(g2);
           }
       };

       chartPanel.setOpaque(true);
       chartPanel.setBackground(new Color(50, 50, 50));
       chartPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
       chartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
       chartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);

       salesChartPanel.add(chartPanel, BorderLayout.CENTER);
       salesChartPanel.revalidate();
       salesChartPanel.repaint();
   }

    private void updateProductsChart(List<ProductData> products) {
           productsPieChartPanel.removeAll(); // Clear previous chart
           productsPieChartPanel.setLayout(new BorderLayout()); // Ensure BorderLayout is used

            // Update the border title for the bar chart
            ((TitledBorder) productsPieChartPanel.getBorder()).setTitle("Top Products by Sales (Bar Chart)");


            // Check if list is empty or null OR if all revenue values are zero
            boolean noDataForChart = (products == null || products.isEmpty() ||
                                      products.stream().allMatch(p -> p.getRevenue() == 0.0));

            if (noDataForChart) {
               // Add placeholder if no data or all revenue is zero
                JPanel placeholderPanel = new JPanel(new BorderLayout());
               placeholderPanel.setBackground(new Color(50, 50, 50));
                JLabel chartLabel = new JLabel("No Product Sales Data Available for this Period", SwingConstants.CENTER);
               chartLabel.setForeground(Color.WHITE);
               chartLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
               placeholderPanel.add(chartLabel, BorderLayout.CENTER);
                productsPieChartPanel.add(placeholderPanel, BorderLayout.CENTER);
                productsPieChartPanel.revalidate();
                productsPieChartPanel.repaint();
               return;
           }


           DefaultCategoryDataset dataset = new DefaultCategoryDataset();

           // Sort products by revenue descending (already sorted in load, but re-sort for safety)
           Collections.sort(products, Comparator.comparingDouble(ProductData::getRevenue).reversed());
           // Limit to top N for chart clarity (table already limits to 20, let's use max 20 bars)
           int maxBars = 20;

           for (int i = 0; i < Math.min(products.size(), maxBars); i++) {
               ProductData product = products.get(i);
                // Only add products with revenue > 0 to the chart dataset
                if(product.getRevenue() > 0) {
                    // Use product name
                    String productName = product.getName();
                    // Optional: Truncate long names if needed, similar to customer chart
                    // if (productName.length() > 20) {
                    //     productName = productName.substring(0, 17) + "...";
                    // }
                   dataset.addValue(product.getRevenue(), "Revenue", productName); // Use "Revenue" as series name
                }
           }

            // If dataset is still empty after filtering out zeros, show no data message again
            if (dataset.getColumnKeys().isEmpty()) {
                 JPanel placeholderPanel = new JPanel(new BorderLayout());
                placeholderPanel.setBackground(new Color(50, 50, 50));
                 JLabel chartLabel = new JLabel("No Product Sales Data Available for this Period", SwingConstants.CENTER);
                chartLabel.setForeground(Color.WHITE);
                chartLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                placeholderPanel.add(chartLabel, BorderLayout.CENTER);
                 productsPieChartPanel.add(placeholderPanel, BorderLayout.CENTER);
                 productsPieChartPanel.revalidate();
                 productsPieChartPanel.repaint();
                return;
            }


               JFreeChart chart = ChartFactory.createBarChart(
               null, // No title
               "Product Name", // X-axis label
               "Revenue (₱)", // Y-axis label
               dataset,
               PlotOrientation.VERTICAL,
               false, // Do not include legend for a single series
               true, // Tooltips
               false // URLs
           );

           // Enhanced styling
           chart.setBackgroundPaint(new Color(50, 50, 50));
           chart.setAntiAlias(true);
           chart.setTextAntiAlias(true);

           CategoryPlot plot = (CategoryPlot) chart.getPlot();
           plot.setBackgroundPaint(new Color(60, 60, 60));
           plot.setDomainGridlinePaint(new Color(80, 80, 80));
           plot.setRangeGridlinePaint(new Color(80, 80, 80));
           plot.setOutlinePaint(null);

           // Domain Axis (Product Names)
           CategoryAxis domainAxis = plot.getDomainAxis();
           domainAxis.setLabelPaint(Color.WHITE);
           domainAxis.setTickLabelPaint(Color.WHITE);
           domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
           domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // Angle labels for product names

           // Range Axis (Revenue)
           NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
           rangeAxis.setLabelPaint(Color.WHITE);
           rangeAxis.setTickLabelPaint(Color.WHITE);
           rangeAxis.setNumberFormatOverride(currencyFormat);
           rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
            // Add margin to prevent labels being cut off
            rangeAxis.setAutoRange(true);
            rangeAxis.setAutoRangeIncludesZero(true);
            rangeAxis.setUpperMargin(0.15); // Add 15% margin at the top


           // Custom renderer for bars
           BarRenderer renderer = (BarRenderer) plot.getRenderer();
           renderer.setSeriesPaint(0, new Color(255, 150, 100)); // Orange color for bars
           renderer.setShadowVisible(false);
           renderer.setBarPainter(new StandardBarPainter());
           renderer.setDrawBarOutline(false);
           renderer.setMaximumBarWidth(0.08); // Adjust bar width if needed

            // Add data labels on top of bars
            renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                "{2}", currencyFormat)); // {2} is the value
            renderer.setBaseItemLabelsVisible(true);
            renderer.setBaseItemLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
            renderer.setBaseItemLabelPaint(Color.WHITE);
            // Position labels above bars, rotated for space
            renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12,
                TextAnchor.BOTTOM_CENTER,
                TextAnchor.CENTER,
                Math.PI / 2.0 // Rotate labels 90 degrees
            ));


           // Legend - not needed for a single series bar chart created this way
           // chart.getLegend().setVisible(false);


           ChartPanel chartPanel = new ChartPanel(chart) {
               @Override
               public void paintComponent(Graphics g) {
                   Graphics2D g2 = (Graphics2D) g;
                   g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                   g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                   g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                   super.paintComponent(g2);
               }
           };

           chartPanel.setBackground(new Color(50, 50, 50));
           chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
           chartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
           chartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
           chartPanel.setMinimumDrawWidth(0);
           chartPanel.setMinimumDrawHeight(0);

           productsPieChartPanel.add(chartPanel, BorderLayout.CENTER); // Add the chart panel back
           productsPieChartPanel.revalidate();
           productsPieChartPanel.repaint();
       }

     private void updateCustomersChart(List<CustomerData> customers) {
        customersChartPanel.removeAll(); // Clear previous chart
        customersChartPanel.setLayout(new BorderLayout()); // Add this line to ensure proper layout

         // Check if list is empty or null OR if all spending values are zero
         boolean noDataForChart = (customers == null || customers.isEmpty() ||
                                   customers.stream().allMatch(c -> c.getTotalSpent() == 0.0));

         if (noDataForChart) {
            // Add placeholder if no data or all spending is zero
             JPanel placeholderPanel = new JPanel(new BorderLayout());
            placeholderPanel.setBackground(new Color(50, 50, 50));
             JLabel chartLabel = new JLabel("No Customer Spending Data Available for this Period", SwingConstants.CENTER);
            chartLabel.setForeground(Color.WHITE);
            chartLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            placeholderPanel.add(chartLabel, BorderLayout.CENTER);
             customersChartPanel.add(placeholderPanel, BorderLayout.CENTER);
             customersChartPanel.revalidate();
             customersChartPanel.repaint();
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String seriesName = "Total Spent";

        // Sort customers by total spent descending (already sorted in load, but re-sort for safety)
        Collections.sort(customers, Comparator.comparingDouble(CustomerData::getTotalSpent).reversed());
        // Limit to top N for chart clarity (table already limits to 20, let's use max 20 bars)
        int maxBars = 20;

        for (int i = 0; i < Math.min(customers.size(), maxBars); i++) {
            CustomerData customer = customers.get(i);
             // Only add customers with spending > 0 to the chart dataset
             if(customer.getTotalSpent() > 0) {
                 // Use full name, but truncate if too long to avoid clutter
                 String customerName = customer.getName();
                 if (customerName.length() > 15) { // Adjust length as needed
                     customerName = customerName.substring(0, 12) + "...";
                 }
                dataset.addValue(customer.getTotalSpent(), seriesName, customerName);
             }
        }

         // If dataset is still empty after filtering out zeros, show no data message again
         if (dataset.getColumnKeys().isEmpty()) {
              JPanel placeholderPanel = new JPanel(new BorderLayout());
             placeholderPanel.setBackground(new Color(50, 50, 50));
              JLabel chartLabel = new JLabel("No Customer Spending Data Available for this Period", SwingConstants.CENTER);
             chartLabel.setForeground(Color.WHITE);
             chartLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
             placeholderPanel.add(chartLabel, BorderLayout.CENTER);
              customersChartPanel.add(placeholderPanel, BorderLayout.CENTER);
              customersChartPanel.revalidate();
              customersChartPanel.repaint();
             return;
         }


            JFreeChart chart = ChartFactory.createBarChart(
            null, // No title
            "Customer",
            "Total Spent (₱)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        // Enhanced styling
        chart.setBackgroundPaint(new Color(50, 50, 50));
        chart.setAntiAlias(true);
        chart.setTextAntiAlias(true);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(60, 60, 60));
        plot.setDomainGridlinePaint(new Color(80, 80, 80));
        plot.setRangeGridlinePaint(new Color(80, 80, 80));
        plot.setOutlinePaint(null);

        // Domain Axis
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelPaint(Color.WHITE);
        domainAxis.setTickLabelPaint(Color.WHITE);
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        // Range Axis
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelPaint(Color.WHITE);
        rangeAxis.setTickLabelPaint(Color.WHITE);
        rangeAxis.setNumberFormatOverride(currencyFormat);
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));

        // Add these lines to prevent upward cropping:
        rangeAxis.setAutoRange(true);
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setUpperMargin(0.15); // Add 15% margin at the top

        // Custom renderer modifications:
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(100, 180, 255)); // Brighter blue
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setDrawBarOutline(false);
        renderer.setMaximumBarWidth(0.08); // Adjust bar width if needed


        // Adjust item label position to prevent overlap:
         // Add data labels on top of bars
         renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
             "{2}", currencyFormat)); // {2} is the value
         renderer.setBaseItemLabelsVisible(true);
         renderer.setBaseItemLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
         renderer.setBaseItemLabelPaint(Color.WHITE);

        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
            ItemLabelAnchor.OUTSIDE12,
            TextAnchor.BOTTOM_CENTER,  // Changed from TOP_CENTER to BOTTOM_CENTER
            TextAnchor.CENTER,
            Math.PI / 2.0 // Rotate labels 90 degrees
        ));

        // Legend
        LegendTitle legend = chart.getLegend();
        legend.setItemPaint(Color.WHITE);
        legend.setBackgroundPaint(new Color(60, 60, 60, 200));
        legend.setItemFont(new Font("Segoe UI", Font.PLAIN, 12));
        // Legend positioning might be good here too, e.g., RectangleEdge.BOTTOM


        ChartPanel chartPanel = new ChartPanel(chart) {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; // Corrected the type name
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                super.paintComponent(g2);
            }
        };

        chartPanel.setBackground(new Color(50, 50, 50));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        chartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
        chartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
        chartPanel.setMinimumDrawWidth(0);
        chartPanel.setMinimumDrawHeight(0);

        customersChartPanel.add(chartPanel, BorderLayout.CENTER); // Add the chart panel back
        customersChartPanel.revalidate();
        customersChartPanel.repaint();
    }


    private void printReport() {
        try {
            // Get the current tab
            int currentTab = tabbedPane.getSelectedIndex();
            JTable tableToPrint = null;
            String reportTitle = "";

            if (currentTab == 0) {
                tableToPrint = salesTable;
                reportTitle = "Sales Report (" + dateFormat.format(startDate) + " to " + dateFormat.format(endDate) + ")";
            } else if (currentTab == 1) {
                tableToPrint = productsTable;
                 reportTitle = "Top Products Report (" + dateFormat.format(startDate) + " to " + dateFormat.format(endDate) + ")";
            } else if (currentTab == 2) {
                tableToPrint = customersTable;
                 reportTitle = "Customer Analytics Report (" + dateFormat.format(startDate) + " to " + dateFormat.format(endDate) + ")";
            }

            if (tableToPrint != null && tableToPrint.getRowCount() > 0) {
                // Set print properties
                JTable.PrintMode mode = JTable.PrintMode.FIT_WIDTH;
                java.text.MessageFormat header = new java.text.MessageFormat(reportTitle);
                java.text.MessageFormat footer = new java.text.MessageFormat("Page {0}");
                boolean interactive = true; // Show print dialog

                boolean complete = tableToPrint.print(mode, header, footer, interactive, null, interactive, null);

                if (complete) {
                    // Printing was initiated and potentially completed (success doesn't mean physical print success)
                    // JOptionPane.showMessageDialog(this, "Printing job sent.", "Print Report", JOptionPane.INFORMATION_MESSAGE);
                    // Avoid showing message if dialog was just closed/cancelled
                } else {
                    // Printing cancelled by user
                    // JOptionPane.showMessageDialog(this, "Printing cancelled.", "Print Report", JOptionPane.WARNING_MESSAGE);
                     // Avoid showing warning if user cancels
                }
            } else {
                 JOptionPane.showMessageDialog(this, "No data to print in this report.", "Print Report", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this,
                "Error printing report: " + e.getMessage(),
                "Print Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Helper classes for data handling (used to pass data to chart methods)

    private class DailySalesData {
        private Date saleDate;
        private int numOrders;
        private int itemsSold;
        private double totalSales;

        public DailySalesData(Date saleDate, int numOrders, int itemsSold, double totalSales) {
            this.saleDate = saleDate;
            this.numOrders = numOrders;
            this.itemsSold = itemsSold;
            this.totalSales = totalSales;
        }

        public Date getSaleDate() { return saleDate; }
        public int getNumOrders() { return numOrders; }
        public int getItemsSold() { return itemsSold; }
        public double getTotalSales() { return totalSales; }
    }


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

         public int getProductId() { return productId; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public int getQuantitySold() { return quantitySold; }
        public double getRevenue() { return revenue; }
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
        public int getCustomerId() { return customerId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public int getOrderCount() { return orderCount; }
        public double getTotalSpent() { return totalSpent; }
    }

    
    // Main method for testing
     public static void main(String[] args) {
        // Example user for testing
        User testAdmin = new User();
        testAdmin.setUserId(1);
        testAdmin.setUsername("admin");
        testAdmin.setAdmin(true);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ReportsSystem reports = new ReportsSystem(null, testAdmin);
            reports.setVisible(true);
        });
    }
}