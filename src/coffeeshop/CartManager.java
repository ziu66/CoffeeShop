/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;


    public class CartManager {
        private final User currentUser;
        private final List<CartItem> cartItems = new ArrayList<>();
        private int currentCartId = -1;
        private JPanel checkoutPanel;
        private JLabel cartTotalLabel;
        private JPanel cardPanel;
        private JLabel subtotalLabel;
        private JLabel subtotalValue;
        private JLabel totalValue;
        private JLabel shippingValue;

        // Color scheme to match dashboard
        private final Color BACKGROUND_COLOR = new Color(40, 40, 40);
        private final Color DARKER_BG = new Color(30, 30, 30);
        private final Color BORDER_COLOR = new Color(60, 60, 60);
        private final Color TEXT_COLOR = Color.WHITE;
        private final Color SECONDARY_TEXT = new Color(180, 180, 180);
        private final Color ACCENT_COLOR = new Color(235, 94, 40);  // Orange accent
        private final Color BUTTON_COLOR = new Color(235, 94, 40);
        private final Color SUCCESS_COLOR = new Color(76, 175, 80);
        private final Color HOVER_COLOR = new Color(245, 124, 70);  // Lighter orange for hover effects

        public CartManager(User user) {
            this.currentUser = user;
            loadCartItems();
        }

        public List<CartItem> getCartItems() {
            return cartItems;
        }

       public double getCartTotal() {
            double total = 0.0;
            for (CartItem item : cartItems) {
                if (item.isSelected()) {
                    double itemTotal = item.getItem().getPrice() * item.getQuantity();
                    System.out.println("Adding to total: " + item.getItem().getName() + 
                                     " x" + item.getQuantity() + " = ₱" + itemTotal); // Debug
                    total += itemTotal;
                }
            }
            System.out.println("Calculated cart total: ₱" + total); // Debug
            return total;
        }

        public void setCardPanel(JPanel cardPanel) {
            this.cardPanel = cardPanel;
        }

        public JPanel createCartPanel(JPanel cardPanel, CardLayout cardLayout) {
            // Add this at the beginning of createCartPanel
            
            this.cartTotalLabel = new JLabel("Selected Total: ₱0.00");
            this.subtotalLabel = new JLabel("Subtotal (0 items)");
            this.subtotalValue = new JLabel("₱0.00");
            this.totalValue = new JLabel("₱0.00");
            cartTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            cartTotalLabel.setForeground(TEXT_COLOR);

            JPanel cartPanel = new JPanel(new BorderLayout());
            cartPanel.setBackground(BACKGROUND_COLOR);
            cartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JPanel titlePanel = new JPanel(new BorderLayout());
            titlePanel.setBackground(BACKGROUND_COLOR);
            titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

            JLabel titleLabel = new JLabel("Your Cart");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setForeground(TEXT_COLOR);
            titlePanel.add(titleLabel, BorderLayout.WEST);

            JLabel itemCountLabel = new JLabel(cartItems.size() + " item(s)");
            itemCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            itemCountLabel.setForeground(SECONDARY_TEXT);
            titlePanel.add(itemCountLabel, BorderLayout.EAST);
            cartPanel.add(titlePanel, BorderLayout.NORTH);

            JPanel itemsPanel = new JPanel();
            itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
            itemsPanel.setBackground(BACKGROUND_COLOR);

            if (cartItems.isEmpty()) {
                JPanel emptyPanel = new JPanel(new BorderLayout());
                emptyPanel.setBackground(DARKER_BG);
                emptyPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

                JLabel emptyLabel = new JLabel("Your cart is empty", SwingConstants.CENTER);
                emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                emptyLabel.setForeground(SECONDARY_TEXT);
                emptyPanel.add(emptyLabel, BorderLayout.CENTER);
                itemsPanel.add(emptyPanel);
            } else {
                // Add select all checkbox
                JPanel selectAllPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                selectAllPanel.setBackground(DARKER_BG);
                selectAllPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JCheckBox selectAll = new JCheckBox("SELECT ALL (" + cartItems.size() + " ITEM(S))");
                selectAll.setFont(new Font("Segoe UI", Font.BOLD, 14));
                selectAll.setForeground(TEXT_COLOR);
                selectAll.setBackground(DARKER_BG);
                selectAll.addActionListener(e -> {
                    boolean selected = selectAll.isSelected();
                    cartItems.forEach(item -> item.setSelected(selected));
                    updateCartDisplay(itemsPanel, itemCountLabel);
                    updateCartTotal();
                });

                selectAllPanel.add(selectAll);
                itemsPanel.add(selectAllPanel);
                itemsPanel.add(Box.createVerticalStrut(5));

                // Add cart items
                for (CartItem item : cartItems) {
                    itemsPanel.add(createCartItemPanel(item, itemsPanel, itemCountLabel));
                    itemsPanel.add(Box.createVerticalStrut(5));
                }
            }

            JScrollPane scrollPane = new JScrollPane(itemsPanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
            cartPanel.add(scrollPane, BorderLayout.CENTER);

            // Order summary panel
            JPanel summaryPanel = new JPanel();
            summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
            summaryPanel.setBackground(DARKER_BG);
            summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)  // Reduced padding from 15px to 8px
            ));

            // Subtotal - More compact layout
            JPanel subtotalPanel = new JPanel(new BorderLayout());
            subtotalPanel.setBackground(DARKER_BG);
            subtotalPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            subtotalLabel = new JLabel("Subtotal (0 items)");
            subtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            subtotalLabel.setForeground(SECONDARY_TEXT);
            subtotalValue = new JLabel("₱" + String.format("%.2f", getCartTotal()));  // Initialize with act
            subtotalValue.setFont(new Font("Segoe UI", Font.BOLD, 13));
            subtotalValue.setForeground(TEXT_COLOR);
            subtotalPanel.add(subtotalLabel, BorderLayout.WEST);
            subtotalPanel.add(subtotalValue, BorderLayout.EAST);
            summaryPanel.add(subtotalPanel);

            // Voucher code
            JPanel voucherPanel = new JPanel(new BorderLayout(5, 0));
            voucherPanel.setBackground(DARKER_BG);
            voucherPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Reduced padding

            JLabel voucherTitleLabel = new JLabel("Enter Voucher Code");
            voucherTitleLabel.setForeground(SECONDARY_TEXT);
            voucherTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Smaller font

            JTextField voucherField = new JTextField();
            voucherField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            voucherField.setBackground(new Color(50, 50, 50));
            voucherField.setForeground(TEXT_COLOR);
            voucherField.setCaretColor(TEXT_COLOR);
            voucherField.setPreferredSize(new Dimension(150, 22)); // Smaller input field
            voucherField.setMaximumSize(new Dimension(150, 22));
            
            JButton applyBtn = new JButton("APPLY");
            applyBtn.setBackground(SUCCESS_COLOR);
            applyBtn.setForeground(Color.WHITE);
            applyBtn.setFont(new Font("Segoe UI", Font.BOLD, 10)); // Smaller font
            applyBtn.setBorderPainted(false);
            applyBtn.setFocusPainted(false);
            applyBtn.setPreferredSize(new Dimension(60, 22)); // Smaller button
            applyBtn.setMaximumSize(new Dimension(60, 22));

            voucherPanel.add(voucherTitleLabel, BorderLayout.NORTH);
            voucherPanel.add(voucherField, BorderLayout.CENTER);
            voucherPanel.add(applyBtn, BorderLayout.EAST);
            summaryPanel.add(voucherPanel);
            summaryPanel.add(Box.createVerticalStrut(5)); // Reduced spacing (was 10)

            JPanel voucherInputPanel = new JPanel(new BorderLayout(5, 0));
            voucherInputPanel.setBackground(DARKER_BG);
            voucherInputPanel.add(voucherField, BorderLayout.CENTER);
            voucherInputPanel.add(applyBtn, BorderLayout.EAST);

            JLabel voucherLabel = new JLabel("Voucher:");
            voucherLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            voucherLabel.setForeground(SECONDARY_TEXT);

            voucherPanel.add(voucherLabel, BorderLayout.WEST);
            voucherPanel.add(voucherInputPanel, BorderLayout.CENTER);
            summaryPanel.add(voucherPanel);
            // Total
            JPanel totalPanel = new JPanel(new BorderLayout());
            totalPanel.setBackground(DARKER_BG);
            totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            JLabel totalLabel = new JLabel("Total");
            totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            totalLabel.setForeground(TEXT_COLOR);
            totalValue = new JLabel("₱" + String.format("%.2f", getCartTotal())); // Remove the + 60.00
            totalValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
            totalValue.setForeground(ACCENT_COLOR);
            totalPanel.add(totalLabel, BorderLayout.WEST);
            totalPanel.add(totalValue, BorderLayout.EAST);
            summaryPanel.add(totalPanel);

            JButton checkoutBtn = new JButton("CHECKOUT");
            checkoutBtn.setBackground(BUTTON_COLOR);
            checkoutBtn.setForeground(Color.WHITE);
            checkoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Smaller font
            checkoutBtn.setBorderPainted(false);
            checkoutBtn.setFocusPainted(false);
            checkoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            checkoutBtn.setPreferredSize(new Dimension(200, 40)); // Smaller button

            // Add hover effect for better UX
            checkoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    checkoutBtn.setBackground(HOVER_COLOR);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    checkoutBtn.setBackground(BUTTON_COLOR);
                }
            }); 

            // In createCartPanel method, modify the checkout button action listener:
           checkoutBtn.addActionListener(e -> {
                if (getSelectedItemCount() > 0) {
                    cardLayout.show(cardPanel, "checkout");
                } else {
                    JOptionPane.showMessageDialog(cardPanel, 
                        "Please select at least one item to checkout",
                        "No Items Selected",
                        JOptionPane.WARNING_MESSAGE);
                }
            });
           
            JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5)); // Reduced vertical gap
            buttonContainer.setBackground(DARKER_BG);
            buttonContainer.add(checkoutBtn);
            summaryPanel.add(buttonContainer);
            cartPanel.add(summaryPanel, BorderLayout.SOUTH);

            updateSummary();
            return cartPanel;
        }
        
        private void updateSummary() {
            SwingUtilities.invokeLater(() -> {
                int selectedCount = getSelectedItemCount();
                double subtotal = getCartTotal();

                // Debug to verify we have the right references
                System.out.println("subtotalLabel: " + (this.subtotalLabel != null));
                System.out.println("subtotalValue: " + (this.subtotalValue != null));
                System.out.println("totalValue: " + (this.totalValue != null));

                if (this.subtotalLabel != null) {
                    this.subtotalLabel.setText("Subtotal (" + selectedCount + " items)");
                }
                if (this.subtotalValue != null) {
                    this.subtotalValue.setText("₱" + String.format("%.2f", subtotal));
                }
                if (this.totalValue != null) {
                    this.totalValue.setText("₱" + String.format("%.2f", subtotal));
                }
                if (this.cartTotalLabel != null) {
                    this.cartTotalLabel.setText("Selected Total: ₱" + String.format("%.2f", subtotal));
                }

                // Force UI update if we have a card panel reference
                if (this.cardPanel != null) {
                    this.cardPanel.revalidate();
                    this.cardPanel.repaint();
                }
            });
        }

    private JPanel createCartItemPanel(CartItem cartItem, JPanel parentPanel, JLabel itemCountLabel) {
        JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
        itemPanel.setBackground(DARKER_BG);
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Left side - checkbox and item info
        JPanel leftPanel = new JPanel(new BorderLayout(15, 0));
        leftPanel.setBackground(DARKER_BG);

        JCheckBox selectBox = new JCheckBox();
        selectBox.setSelected(cartItem.isSelected());
        selectBox.setBackground(DARKER_BG);
        selectBox.addActionListener(e -> {
            cartItem.setSelected(selectBox.isSelected());
            System.out.println(cartItem.getItem().getName() + " selected: " + selectBox.isSelected()); // Debug
            updateCartDisplay(parentPanel, itemCountLabel);
            updateSummary(); // Make sure summary updates when items are selected/deselected
        });
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(DARKER_BG);

        JLabel nameLabel = new JLabel(cartItem.getItem().getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_COLOR);

        JLabel descLabel = new JLabel("Size: Regular");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(SECONDARY_TEXT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(descLabel);

        leftPanel.add(selectBox, BorderLayout.WEST);
        leftPanel.add(infoPanel, BorderLayout.CENTER);

        // Right side - price and delete button
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setBackground(DARKER_BG);

        JLabel priceLabel = new JLabel("₱" + String.format("%.2f", cartItem.getItem().getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setForeground(ACCENT_COLOR);
        priceLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Quantity controls
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        quantityPanel.setBackground(DARKER_BG);

        JButton minusBtn = new JButton("-");
        minusBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        minusBtn.setPreferredSize(new Dimension(30, 30));
        minusBtn.setBackground(new Color(60, 60, 60));
        minusBtn.setForeground(TEXT_COLOR);
        minusBtn.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        minusBtn.setFocusPainted(false);
        minusBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel quantityLabel = new JLabel(String.valueOf(cartItem.getQuantity()));
        quantityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        quantityLabel.setForeground(TEXT_COLOR);
        quantityLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JButton plusBtn = new JButton("+");
        plusBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        plusBtn.setPreferredSize(new Dimension(30, 30));
        plusBtn.setBackground(new Color(60, 60, 60));
        plusBtn.setForeground(TEXT_COLOR);
        plusBtn.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        plusBtn.setFocusPainted(false);
        plusBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add action listeners for quantity buttons
        minusBtn.addActionListener(e -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                quantityLabel.setText(String.valueOf(cartItem.getQuantity()));
                updateSummary(); // Update summary when quantity changes
                updateCartDisplay(parentPanel, itemCountLabel);
            }
        });

        plusBtn.addActionListener(e -> {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            quantityLabel.setText(String.valueOf(cartItem.getQuantity()));
            updateSummary(); // Update summary when quantity changes
            updateCartDisplay(parentPanel, itemCountLabel);
        });
        
        quantityPanel.add(minusBtn);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(plusBtn);

        JButton deleteBtn = new JButton("REMOVE");
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deleteBtn.setForeground(new Color(255, 99, 71));
        deleteBtn.setBackground(DARKER_BG);
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> {
            removeCartItem(cartItem.getItem().getProductId());
            cartItems.remove(cartItem);
            updateCartDisplay(parentPanel, itemCountLabel);
            updateSummary(); // Update summary when item is removed
        });

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(DARKER_BG);
        buttonPanel.add(quantityPanel, BorderLayout.CENTER);
        buttonPanel.add(deleteBtn, BorderLayout.SOUTH);

        rightPanel.add(priceLabel, BorderLayout.NORTH);
        rightPanel.add(buttonPanel, BorderLayout.CENTER);

        itemPanel.add(leftPanel, BorderLayout.CENTER);
        itemPanel.add(rightPanel, BorderLayout.EAST);

        return itemPanel;
    }

        private void updateCartDisplay(JPanel itemsPanel, JLabel itemCountLabel) {
        itemsPanel.removeAll();

        if (cartItems.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBackground(DARKER_BG);
            emptyPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
            
            JLabel emptyLabel = new JLabel("Your cart is empty", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            emptyLabel.setForeground(SECONDARY_TEXT);
            emptyPanel.add(emptyLabel, BorderLayout.CENTER);
            itemsPanel.add(emptyPanel);
        } else {
            // Add select all checkbox
            JPanel selectAllPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            selectAllPanel.setBackground(DARKER_BG);
            selectAllPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JCheckBox selectAll = new JCheckBox("SELECT ALL (" + cartItems.size() + " ITEM(S))");
            selectAll.setFont(new Font("Segoe UI", Font.BOLD, 14));
            selectAll.setForeground(TEXT_COLOR);
            selectAll.setBackground(DARKER_BG);

            // Set the initial state based on whether all items are selected
            boolean allSelected = !cartItems.isEmpty() && cartItems.stream().allMatch(CartItem::isSelected);
            selectAll.setSelected(allSelected);

            selectAll.addActionListener(e -> {
                boolean selected = selectAll.isSelected();
                cartItems.forEach(item -> item.setSelected(selected));
                updateCartDisplay(itemsPanel, itemCountLabel);
                updateSummary(); // Update the summary when selection changes
            });

            selectAllPanel.add(selectAll);
            itemsPanel.add(selectAllPanel);
            itemsPanel.add(Box.createVerticalStrut(10));

            // Add cart items - only create panels for selected items if needed
            for (CartItem item : cartItems) {
                itemsPanel.add(createCartItemPanel(item, itemsPanel, itemCountLabel));
                itemsPanel.add(Box.createVerticalStrut(10));
            }
        }

        updateSummary(); // Add this line to ensure summary updates with display
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }
    
    private void updateCartTotal() {
        if (cartTotalLabel != null) {
            cartTotalLabel.setText("Selected Total: ₱" + String.format("%.2f", getCartTotal()));
        }
    }

    // Create checkout panel for the main dashboard
    public JPanel createCheckoutPanel(JPanel mainCardPanel, CardLayout cardLayout) {
        // Change parameter name from cardPanel to mainCardPanel to avoid confusion
        JPanel checkoutPanel = new JPanel(new BorderLayout());
        checkoutPanel.setBackground(BACKGROUND_COLOR);
        checkoutPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title and back button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton backBtn = new JButton("← Back to Cart");
        backBtn.addActionListener(e -> {
            cardLayout.show(mainCardPanel, "cart"); // Use mainCardPanel instead
        });
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setForeground(ACCENT_COLOR);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        titlePanel.add(backBtn, BorderLayout.WEST);
    
        JLabel titleLabel = new JLabel("Checkout", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        checkoutPanel.add(titlePanel, BorderLayout.NORTH);

        // Main content - Using GridBagLayout for better symmetry
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Delivery Method - New section for pickup option
        JPanel deliveryMethodPanel = new JPanel();
        deliveryMethodPanel.setLayout(new BoxLayout(deliveryMethodPanel, BoxLayout.Y_AXIS));
        deliveryMethodPanel.setBackground(DARKER_BG);
        deliveryMethodPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel deliveryMethodTitle = new JLabel("Delivery Method");
        deliveryMethodTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        deliveryMethodTitle.setForeground(TEXT_COLOR);
        deliveryMethodPanel.add(deliveryMethodTitle);
        deliveryMethodPanel.add(Box.createVerticalStrut(15));

        ButtonGroup deliveryGroup = new ButtonGroup();
        
        JRadioButton deliveryBtn = createStyledRadioButton("Delivery (₱60.00)", true);
        JRadioButton pickupBtn = createStyledRadioButton("Pickup (Free)", false);

        deliveryBtn.addActionListener(e -> updateCheckoutTotals(true));
        pickupBtn.addActionListener(e -> updateCheckoutTotals(false));

        deliveryGroup.add(deliveryBtn);
        deliveryGroup.add(pickupBtn);
        
        
        JPanel deliveryBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        deliveryBtnPanel.setBackground(DARKER_BG);
        deliveryBtnPanel.add(deliveryBtn);
        
        JPanel pickupBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        pickupBtnPanel.setBackground(DARKER_BG);
        pickupBtnPanel.add(pickupBtn);
        
        deliveryMethodPanel.add(deliveryBtnPanel);
        deliveryMethodPanel.add(pickupBtnPanel);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        contentPanel.add(deliveryMethodPanel, gbc);

        // Shipping address
        JPanel addressPanel = new JPanel();
        addressPanel.setLayout(new BoxLayout(addressPanel, BoxLayout.Y_AXIS));
        addressPanel.setBackground(DARKER_BG);
        addressPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel addressTitle = new JLabel("Shipping Address");
        addressTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addressTitle.setForeground(TEXT_COLOR);
        addressPanel.add(addressTitle);
        addressPanel.add(Box.createVerticalStrut(10));

        JTextArea addressField = new JTextArea(currentUser.getAddress());
        addressField.setLineWrap(true);
        addressField.setWrapStyleWord(true);
        addressField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressField.setBackground(new Color(50, 50, 50));
        addressField.setForeground(TEXT_COLOR);
        addressField.setCaretColor(TEXT_COLOR);
        addressField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        addressField.setRows(3);
        JScrollPane addressScrollPane = new JScrollPane(addressField);
        addressScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        addressPanel.add(addressScrollPane);
        addressPanel.add(Box.createVerticalStrut(10));

        JButton addAddressBtn = new JButton("Add Shipping Address");
        addAddressBtn.setContentAreaFilled(false);
        addAddressBtn.setBorderPainted(false);
        addAddressBtn.setFocusPainted(false);
        addAddressBtn.setForeground(ACCENT_COLOR);
        addAddressBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addAddressBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addressPanel.add(addAddressBtn);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        contentPanel.add(addressPanel, gbc);

        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(DARKER_BG);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        JPanel priceDetailsPanel = new JPanel(new GridLayout(2, 1, 0, 2)); // 2 rows, 1 column with 2px gap
        priceDetailsPanel.setBackground(DARKER_BG);
        
        JLabel summaryTitle = new JLabel("Order Summary");
        summaryTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        summaryTitle.setForeground(TEXT_COLOR);
        summaryPanel.add(summaryTitle);
        summaryPanel.add(Box.createVerticalStrut(8));

        boolean firstItem = true;
        for (CartItem item : cartItems) {
            if (item.isSelected()) { 
                if (!firstItem) {
                    JSeparator separator = new JSeparator();
                    separator.setForeground(BORDER_COLOR);
                    separator.setBackground(BORDER_COLOR);
                    summaryPanel.add(separator);
                    summaryPanel.add(Box.createVerticalStrut(10));
                }

                JPanel itemPanel = new JPanel(new BorderLayout());
                itemPanel.setBackground(DARKER_BG);
                itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

                JLabel itemName = new JLabel(item.getItem().getName() + " × " + item.getQuantity());
                itemName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                itemName.setForeground(TEXT_COLOR);

                JLabel itemPrice = new JLabel("₱" + String.format("%.2f", item.getItem().getPrice() * item.getQuantity()));
                itemPrice.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                itemPrice.setForeground(TEXT_COLOR);

                itemPanel.add(itemName, BorderLayout.WEST);
                itemPanel.add(itemPrice, BorderLayout.EAST);
                summaryPanel.add(itemPanel);
                summaryPanel.add(Box.createVerticalStrut(5));

                firstItem = false;
            }
        }

        summaryPanel.add(Box.createVerticalStrut(10));
        
        JSeparator totalsSeparator = new JSeparator();
        totalsSeparator.setForeground(BORDER_COLOR);
        totalsSeparator.setBackground(BORDER_COLOR);
        summaryPanel.add(totalsSeparator);
        summaryPanel.add(Box.createVerticalStrut(10));

        JPanel subtotalPanel = new JPanel(new BorderLayout());
        subtotalPanel.setBackground(DARKER_BG);
        JLabel subtotalLabel = new JLabel("Subtotal (" + getSelectedItemCount() + " items)");
        subtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtotalLabel.setForeground(SECONDARY_TEXT);
        subtotalValue = new JLabel("₱" + String.format("%.2f", getCartTotal()));
        subtotalValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        subtotalValue.setForeground(TEXT_COLOR);
        subtotalPanel.add(subtotalLabel, BorderLayout.WEST);
        subtotalPanel.add(subtotalValue, BorderLayout.EAST);
        summaryPanel.add(subtotalPanel);

        JPanel shippingPanel = new JPanel(new BorderLayout());
        shippingPanel.setBackground(DARKER_BG);
        JLabel shippingLabel = new JLabel("Shipping Fee");
        shippingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        shippingLabel.setForeground(SECONDARY_TEXT);
        shippingValue = new JLabel("₱60.00"); // Default to delivery
        shippingValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        shippingValue.setForeground(TEXT_COLOR);
        shippingPanel.add(shippingLabel, BorderLayout.WEST);
        shippingPanel.add(shippingValue, BorderLayout.EAST);
        summaryPanel.add(shippingPanel);
        
        priceDetailsPanel.add(subtotalPanel);
        priceDetailsPanel.add(shippingPanel);
        summaryPanel.add(priceDetailsPanel);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(DARKER_BG);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JLabel totalLabel = new JLabel("Total");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(TEXT_COLOR);
        totalValue = new JLabel("₱" + String.format("%.2f", getCartTotal()));  // Remove the + 60.00
        totalValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalValue.setForeground(ACCENT_COLOR);
        totalPanel.add(totalLabel, BorderLayout.WEST);
        totalPanel.add(totalValue, BorderLayout.EAST);
        summaryPanel.add(totalPanel);


        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        contentPanel.add(summaryPanel, gbc);

        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setBackground(DARKER_BG);
        paymentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel paymentTitle = new JLabel("Payment Method");
        paymentTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        paymentTitle.setForeground(TEXT_COLOR);
        paymentPanel.add(paymentTitle);
        paymentPanel.add(Box.createVerticalStrut(15));

        ButtonGroup paymentGroup = new ButtonGroup();
        
        JRadioButton codBtn = createStyledRadioButton("Cash on Delivery", true);
        JRadioButton cardBtn = createStyledRadioButton("Credit/Debit Card", false);
        JRadioButton walletBtn = createStyledRadioButton("E-Wallet", false);
        
        paymentGroup.add(codBtn);
        paymentGroup.add(cardBtn);
        paymentGroup.add(walletBtn);
        
        JPanel codBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        codBtnPanel.setBackground(DARKER_BG);
        codBtnPanel.add(codBtn);
        
        JPanel cardBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        cardBtnPanel.setBackground(DARKER_BG);
        cardBtnPanel.add(cardBtn);
        
        JPanel walletBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        walletBtnPanel.setBackground(DARKER_BG);
        walletBtnPanel.add(walletBtn);
        
        paymentPanel.add(codBtnPanel);
        paymentPanel.add(cardBtnPanel);
        paymentPanel.add(walletBtnPanel);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        contentPanel.add(paymentPanel, gbc);

        JPanel cardDetailsPanel = new JPanel();
        cardDetailsPanel.setLayout(new BoxLayout(cardDetailsPanel, BoxLayout.Y_AXIS));
        cardDetailsPanel.setBackground(DARKER_BG);
        cardDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        cardDetailsPanel.setVisible(false);

        JLabel cardDetailsTitle = new JLabel("Card Details");
        cardDetailsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cardDetailsTitle.setForeground(TEXT_COLOR);
        cardDetailsPanel.add(cardDetailsTitle);
        cardDetailsPanel.add(Box.createVerticalStrut(15));
        
        JLabel cardNumberLabel = new JLabel("Card Number");
        cardNumberLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardNumberLabel.setForeground(TEXT_COLOR);
        cardDetailsPanel.add(cardNumberLabel);
        cardDetailsPanel.add(Box.createVerticalStrut(5));

        JTextField cardNumberField = new JTextField();
        cardNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardNumberField.setBackground(new Color(50, 50, 50));
        cardNumberField.setForeground(TEXT_COLOR);
        cardNumberField.setCaretColor(TEXT_COLOR);
        cardNumberField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        cardDetailsPanel.add(cardNumberField);
        cardDetailsPanel.add(Box.createVerticalStrut(10));

        JLabel nameOnCardLabel = new JLabel("Name on Card");
        nameOnCardLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameOnCardLabel.setForeground(TEXT_COLOR);
        cardDetailsPanel.add(nameOnCardLabel);
        cardDetailsPanel.add(Box.createVerticalStrut(5));

        JTextField nameOnCardField = new JTextField();
        nameOnCardField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameOnCardField.setBackground(new Color(50, 50, 50));
        nameOnCardField.setForeground(TEXT_COLOR);
        nameOnCardField.setCaretColor(TEXT_COLOR);
        nameOnCardField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        cardDetailsPanel.add(nameOnCardField);
        cardDetailsPanel.add(Box.createVerticalStrut(10));

        JPanel expiryAndCvvPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        expiryAndCvvPanel.setBackground(DARKER_BG);

        JPanel expiryPanel = new JPanel();
        expiryPanel.setLayout(new BoxLayout(expiryPanel, BoxLayout.Y_AXIS));
        expiryPanel.setBackground(DARKER_BG);

        JLabel expiryLabel = new JLabel("Expiry Date (MM/YY)");
        expiryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        expiryLabel.setForeground(TEXT_COLOR);
        expiryPanel.add(expiryLabel);
        expiryPanel.add(Box.createVerticalStrut(5));

        JTextField expiryField = new JTextField();
        expiryField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        expiryField.setBackground(new Color(50, 50, 50));
        expiryField.setForeground(TEXT_COLOR);
        expiryField.setCaretColor(TEXT_COLOR);
        expiryField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        expiryPanel.add(expiryField);

        JPanel cvvPanel = new JPanel();
        cvvPanel.setLayout(new BoxLayout(cvvPanel, BoxLayout.Y_AXIS));
        cvvPanel.setBackground(DARKER_BG);

        JLabel cvvLabel = new JLabel("CVV");
        cvvLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cvvLabel.setForeground(TEXT_COLOR);
        cvvPanel.add(cvvLabel);
        cvvPanel.add(Box.createVerticalStrut(5));

        JTextField cvvField = new JTextField();
        cvvField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cvvField.setBackground(new Color(50, 50, 50));
        cvvField.setForeground(TEXT_COLOR);
        cvvField.setCaretColor(TEXT_COLOR);
        cvvField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        cvvPanel.add(cvvField);

        expiryAndCvvPanel.add(expiryPanel);
        expiryAndCvvPanel.add(cvvPanel);
        cardDetailsPanel.add(expiryAndCvvPanel);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        contentPanel.add(cardDetailsPanel, gbc);

        JPanel walletDetailsPanel = new JPanel();
        walletDetailsPanel.setLayout(new BoxLayout(walletDetailsPanel, BoxLayout.Y_AXIS));
        walletDetailsPanel.setBackground(DARKER_BG);
        walletDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        walletDetailsPanel.setVisible(false);

        JLabel walletDetailsTitle = new JLabel("E-Wallet Details");
        walletDetailsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        walletDetailsTitle.setForeground(TEXT_COLOR);
        walletDetailsPanel.add(walletDetailsTitle);
        walletDetailsPanel.add(Box.createVerticalStrut(15));

        JLabel walletProviderLabel = new JLabel("E-Wallet Provider");
        walletProviderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        walletProviderLabel.setForeground(TEXT_COLOR);
        walletDetailsPanel.add(walletProviderLabel);
        walletDetailsPanel.add(Box.createVerticalStrut(5));

        String[] walletProviders = {"GCash", "PayMaya", "GrabPay", "Coins.ph"};
        JComboBox<String> walletProviderDropdown = new JComboBox<>(walletProviders);
        walletProviderDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        walletProviderDropdown.setBackground(new Color(50, 50, 50));
        walletProviderDropdown.setForeground(TEXT_COLOR);
        walletProviderDropdown.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        walletDetailsPanel.add(walletProviderDropdown);
        walletDetailsPanel.add(Box.createVerticalStrut(10));

        JLabel mobileNumberLabel = new JLabel("Mobile Number");
        mobileNumberLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mobileNumberLabel.setForeground(TEXT_COLOR);
        walletDetailsPanel.add(mobileNumberLabel);
        walletDetailsPanel.add(Box.createVerticalStrut(5));

        JTextField mobileNumberField = new JTextField();
        mobileNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mobileNumberField.setBackground(new Color(50, 50, 50));
        mobileNumberField.setForeground(TEXT_COLOR);
        mobileNumberField.setCaretColor(TEXT_COLOR);
        mobileNumberField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        walletDetailsPanel.add(mobileNumberField);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        contentPanel.add(walletDetailsPanel, gbc);

        cardBtn.addActionListener(e -> {
            cardDetailsPanel.setVisible(true);
            walletDetailsPanel.setVisible(false);
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        walletBtn.addActionListener(e -> {
            cardDetailsPanel.setVisible(false);
            walletDetailsPanel.setVisible(true);
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        codBtn.addActionListener(e -> {
            cardDetailsPanel.setVisible(false);
            walletDetailsPanel.setVisible(false);
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        checkoutPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton placeOrderBtn = new JButton("PLACE ORDER");
        placeOrderBtn.setBackground(BUTTON_COLOR);
        placeOrderBtn.setForeground(Color.WHITE);
        placeOrderBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        placeOrderBtn.setBorderPainted(false);
        placeOrderBtn.setFocusPainted(false);
        placeOrderBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        placeOrderBtn.setPreferredSize(new Dimension(300, 50));
        
        // Add hover effect
        placeOrderBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                placeOrderBtn.setBackground(HOVER_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                placeOrderBtn.setBackground(BUTTON_COLOR);
            }
        });
        
        placeOrderBtn.addActionListener(e -> {
            processOrder(currentUser);
            cardLayout.show(mainCardPanel, "orderConfirmation");
        });
        
        buttonPanel.add(placeOrderBtn);
        checkoutPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.checkoutPanel = checkoutPanel;
        updateCheckoutTotals(true);
        
        return checkoutPanel;
    }
                
    private void updateCheckoutTotals(boolean isDelivery) {
        double subtotal = getCartTotal();
        double shipping = isDelivery ? 60.00 : 0.00;
        double total = subtotal + shipping;

        if (subtotalValue != null) {
            subtotalValue.setText("₱" + String.format("%.2f", subtotal));
        }
        if (shippingValue != null) {
            shippingValue.setText(isDelivery ? "₱60.00" : "₱0.00");
        }
        if (totalValue != null) {
            totalValue.setText("₱" + String.format("%.2f", total));
        }
    }
    
    private JRadioButton createStyledRadioButton(String text, boolean selected) {
        JRadioButton radioButton = new JRadioButton(text);
        radioButton.setSelected(selected);
        radioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        radioButton.setForeground(TEXT_COLOR);
        radioButton.setBackground(DARKER_BG);
        radioButton.setFocusPainted(false);
        return radioButton;
    }
    
    private void updateOrderTotal(JLabel shippingLabel, String subtotalText) {
        JPanel parentPanel = (JPanel) shippingLabel.getParent().getParent();
        Component[] components = parentPanel.getComponents();
        
        String subtotalStr = subtotalText.substring(1); // Remove peso sign
        double subtotal = Double.parseDouble(subtotalStr);
        
        String shippingStr = shippingLabel.getText().substring(1); // Remove peso sign
        double shipping = Double.parseDouble(shippingStr);
        
        double total = subtotal + shipping;
        
        // Find the total label and update it
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                Component[] panelComps = panel.getComponents();
                for (Component panelComp : panelComps) {
                    if (panelComp instanceof JLabel) {
                        JLabel label = (JLabel) panelComp;
                        if (label.getText().equals("Total")) {
                            // This is the total panel, update the value label (which is the other component)
                            for (Component totalComp : panel.getComponents()) {
                                if (totalComp instanceof JLabel && totalComp != label) {
                                    ((JLabel) totalComp).setText("₱" + String.format("%.2f", total));
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public JPanel createOrderConfirmationPanel(JPanel cardPanel, CardLayout cardLayout) {
        JPanel confirmationPanel = new JPanel(new BorderLayout());
        confirmationPanel.setBackground(BACKGROUND_COLOR);
        confirmationPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Center content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Success icon (would be better with an actual icon but using text for simplicity)
        JLabel iconLabel = new JLabel("✓");
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 72));
        iconLabel.setForeground(SUCCESS_COLOR);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Order Placed Successfully!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Your order has been confirmed");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(SECONDARY_TEXT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel orderIdLabel = new JLabel("Order ID: ORD" + generateOrderId());
        orderIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        orderIdLabel.setForeground(TEXT_COLOR);
        orderIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton continueBtn = new JButton("CONTINUE SHOPPING");
        continueBtn.setBackground(BUTTON_COLOR);
        continueBtn.setForeground(Color.WHITE);
        continueBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        continueBtn.setBorderPainted(false);
        continueBtn.setFocusPainted(false);
        continueBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        continueBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueBtn.setMaximumSize(new Dimension(300, 50));
        
        continueBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                continueBtn.setBackground(HOVER_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                continueBtn.setBackground(BUTTON_COLOR);
            }
        });
        
        continueBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "products");
            // Clear selected items from cart
            clearSelectedItems();
        });
        
        centerPanel.add(iconLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(subtitleLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(orderIdLabel);
        centerPanel.add(Box.createVerticalStrut(50));
        centerPanel.add(continueBtn);
        
        confirmationPanel.add(centerPanel, BorderLayout.CENTER);
        
        return confirmationPanel;
    }
    
    private String generateOrderId() {
        // Simple order ID generator - timestamp + random number
        return String.format("%d%03d", System.currentTimeMillis() % 100000, (int)(Math.random() * 1000));
    }
    
    private void clearSelectedItems() {
        List<CartItem> itemsToRemove = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                itemsToRemove.add(item);
                removeCartItem(item.getItem().getProductId());
            }
        }
        cartItems.removeAll(itemsToRemove);
    }
    
    private void processOrder(User user) {
        // Implementation would connect to a database or service to store the order
        // For now, just print to console
        System.out.println("Processing order for user: " + user.getUsername());
        System.out.println("Items ordered:");
        
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                System.out.println("- " + item.getItem().getName() + " x " + item.getQuantity() 
                    + " @ ₱" + item.getItem().getPrice() + " = ₱" + (item.getItem().getPrice() * item.getQuantity()));
            }
        }
        
        System.out.println("Total: ₱" + String.format("%.2f", getCartTotal()));
        System.out.println("Shipping: ₱60.00");
        System.out.println("Grand Total: ₱" + String.format("%.2f", getCartTotal() + 60.00));
    }
    
    private int getSelectedItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                count += item.getQuantity();
            }
        }
        return count;
    }
    
    public void addToCart(MenuItem product, int quantity) {
        try (Connection conn = DBConnection.getConnection()) {
            if (currentCartId == -1) {
                // First check if user already has a cart
                String checkQuery = "SELECT cart_id FROM carts WHERE user_id = ? AND status = 'active'";
                PreparedStatement checkPS = conn.prepareStatement(checkQuery);
                checkPS.setInt(1, currentUser.getUserId());
                ResultSet rs = checkPS.executeQuery();

                if (rs.next()) {
                    currentCartId = rs.getInt("cart_id");
                } else {
                    // Create a new cart
                    String createQuery = "INSERT INTO carts (user_id, created_date, status) VALUES (?, NOW(), 'active')";
                    PreparedStatement createPS = conn.prepareStatement(createQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                    createPS.setInt(1, currentUser.getUserId());
                    createPS.executeUpdate();

                    ResultSet keys = createPS.getGeneratedKeys();
                    if (keys.next()) {
                        currentCartId = keys.getInt(1);
                    } else {
                        throw new SQLException("Failed to create cart");
                    }
                }
            }

            // Check if product already exists in cart
            String checkItemQuery = "SELECT * FROM cart_items WHERE cart_id = ? AND product_id = ?";
            PreparedStatement checkItemPS = conn.prepareStatement(checkItemQuery);
            checkItemPS.setInt(1, currentCartId);
            checkItemPS.setInt(2, product.getProductId());
            ResultSet rs = checkItemPS.executeQuery();

            if (rs.next()) {
                // Update quantity
                int currentQty = rs.getInt("quantity");
                String updateQuery = "UPDATE cart_items SET quantity = ? WHERE cart_id = ? AND product_id = ?";
                PreparedStatement updatePS = conn.prepareStatement(updateQuery);
                updatePS.setInt(1, currentQty + quantity);
                updatePS.setInt(2, currentCartId);
                updatePS.setInt(3, product.getProductId());
                updatePS.executeUpdate();

                // Update local cart items
                for (CartItem item : cartItems) {
                    if (item.getItem().getProductId() == product.getProductId()) {
                        item.setQuantity(item.getQuantity() + quantity);
                        break;
                    }
                }
            } else {
                // Add new item
                String addQuery = "INSERT INTO cart_items (cart_id, product_id, quantity, date_added) VALUES (?, ?, ?, NOW())";
                PreparedStatement addPS = conn.prepareStatement(addQuery);
                addPS.setInt(1, currentCartId);
                addPS.setInt(2, product.getProductId());
                addPS.setInt(3, quantity);
                addPS.executeUpdate();

                // Add to local cart items
                CartItem newItem = new CartItem(product);
                newItem.setQuantity(quantity);
                newItem.setSelected(false); // Changed from true to false
                cartItems.add(newItem);
            }

            JOptionPane.showMessageDialog(null,
                product.getName() + " added to cart!",
                "Product Added",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error adding item to cart: " + e.getMessage(),
                "Cart Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadCartItems() {
        cartItems.clear();

        try (Connection conn = DBConnection.getConnection()) {
            String cartQuery = "SELECT ci.* FROM cart_items ci " +
                              "JOIN carts c ON ci.cart_id = c.cart_id " +
                              "WHERE c.user_id = ?";
            PreparedStatement cartPS = conn.prepareStatement(cartQuery);
            cartPS.setInt(1, currentUser.getUserId());
            ResultSet cartRS = cartPS.executeQuery();

            if (cartRS.next()) {
                currentCartId = cartRS.getInt("cart_id");

                String itemsQuery = "SELECT ci.*, p.name, p.description, p.price, p.category " +
                                   "FROM cart_items ci " +
                                   "JOIN products p ON ci.product_id = p.product_id " +
                                   "WHERE ci.cart_id = ?";
                PreparedStatement itemsPS = conn.prepareStatement(itemsQuery);
                itemsPS.setInt(1, currentCartId);
                ResultSet itemsRS = itemsPS.executeQuery();

                while (itemsRS.next()) {
                    MenuItem menuItem = new MenuItem(
                        itemsRS.getInt("product_id"),
                        itemsRS.getString("name"),
                        itemsRS.getDouble("price"),
                        itemsRS.getString("description")
                    );

                    CartItem item = new CartItem(menuItem);
                    item.setQuantity(itemsRS.getInt("quantity"));
                    item.setSelected(false); // Explicitly set to false
                    cartItems.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error loading cart items: " + e.getMessage(),
                "Cart Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removeCartItem(int productId) {
        try (Connection conn = DBConnection.getConnection()) {
            String deleteQuery = "DELETE FROM cart_items WHERE cart_id = ? AND product_id = ?";
            PreparedStatement ps = conn.prepareStatement(deleteQuery);
            ps.setInt(1, currentCartId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error removing item from cart: " + e.getMessage(),
                "Cart Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}