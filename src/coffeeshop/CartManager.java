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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;

    public class CartManager {
        private final User currentUser;
        private List<CartItem> cartItems = new ArrayList<>();
        private int currentCartId = -1;
        private JPanel checkoutPanel;
        private JLabel cartTotalLabel;
        private JPanel cardPanel;
        private JLabel subtotalLabel;
        private JLabel subtotalValue;
        private JLabel totalValue;
        private JLabel shippingValue;
        private JLabel checkoutSubtotalValue;
        private JLabel checkoutShippingValue;
        private JLabel checkoutTotalValue;
        private CardLayout cardLayout;  // Add this field

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
            // Initialize all UI components once
            this.cartTotalLabel = new JLabel("Selected Total: ₱0.00");
            this.subtotalLabel = new JLabel("Subtotal (0 items)");
            this.subtotalValue = new JLabel("₱0.00");
            this.totalValue = new JLabel("₱0.00");
            this.shippingValue = new JLabel("₱0.00");
            this.cardLayout = new CardLayout();
            

            cartTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            cartTotalLabel.setForeground(TEXT_COLOR);
            subtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            subtotalLabel.setForeground(SECONDARY_TEXT);
            subtotalValue.setFont(new Font("Segoe UI", Font.BOLD, 13));
            subtotalValue.setForeground(TEXT_COLOR);
            totalValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
            totalValue.setForeground(ACCENT_COLOR);

            loadCartItems();
        }

        public List<CartItem> getCartItems() {
            return cartItems;
        }

       public double getCartTotal() {
            double total = 0.0;
            System.out.println("Debug - Checking cart items selection state:");
            for (CartItem item : cartItems) {
                System.out.printf("- %s: selected=%b, qty=%d%n", 
                    item.getItem().getName(), 
                    item.isSelected(), 
                    item.getQuantity());
                if (item.isSelected()) {
                    double itemTotal = item.getItem().getPrice() * item.getQuantity();
                    total += itemTotal;
                }
            }
            System.out.println("Calculated cart total: ₱" + total);
            return total;
        }

        public void setCardPanel(JPanel cardPanel) {
            this.cardPanel = cardPanel;
        }

        public JPanel createCartPanel(JPanel cardPanel, CardLayout cardLayout) {
            this.cardPanel = cardPanel;
            this.cardLayout = cardLayout; 

            // Initialize the summary labels with current values
            updateSummary();

            JPanel cartPanel = new JPanel(new BorderLayout());
            cartPanel.setBackground(BACKGROUND_COLOR);
            cartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Title Panel
            JPanel titlePanel = new JPanel(new BorderLayout());
            titlePanel.setBackground(BACKGROUND_COLOR);
            titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

            JLabel titleLabel = new JLabel("Your Cart");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setForeground(TEXT_COLOR);
            titlePanel.add(titleLabel, BorderLayout.WEST);
            cartPanel.add(titlePanel, BorderLayout.NORTH);

            // Items Panel
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
                // Select All Panel
                JPanel selectAllPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                selectAllPanel.setBackground(DARKER_BG);
                selectAllPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JCheckBox selectAll = new JCheckBox("SELECT ALL");
                selectAll.setFont(new Font("Segoe UI", Font.BOLD, 14));
                selectAll.setForeground(TEXT_COLOR);
                selectAll.setBackground(DARKER_BG);

                boolean allSelected = !cartItems.isEmpty() && cartItems.stream().allMatch(CartItem::isSelected);
                selectAll.setSelected(allSelected);

                selectAll.addActionListener(e -> {
                    boolean selected = selectAll.isSelected();
                    cartItems.forEach(item -> item.setSelected(selected));
                    updateCartDisplay(itemsPanel);
                    updateSummary();
                });

               selectAllPanel.add(selectAll);
               itemsPanel.add(selectAllPanel);
               itemsPanel.add(Box.createVerticalStrut(5));

                for (CartItem item : cartItems) {
                itemsPanel.add(createCartItemPanel(item, itemsPanel));
                itemsPanel.add(Box.createVerticalStrut(5));
            }
        }

            JScrollPane scrollPane = new JScrollPane(itemsPanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
            cartPanel.add(scrollPane, BorderLayout.CENTER);

            // Order Summary Panel
            JPanel summaryPanel = new JPanel();
            summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
            summaryPanel.setBackground(DARKER_BG);
            summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
            ));

            // Subtotal Panel - Create new labels here that reference the instance variables
            JPanel subtotalPanel = new JPanel(new BorderLayout());
            subtotalPanel.setBackground(DARKER_BG);
            subtotalPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

            // Create new labels that will mirror the instance variables
            JLabel currentSubtotalLabel = new JLabel(subtotalLabel.getText());
            JLabel currentSubtotalValue = new JLabel(subtotalValue.getText());

            // Copy the styles from the instance labels
            currentSubtotalLabel.setFont(subtotalLabel.getFont());
            currentSubtotalLabel.setForeground(subtotalLabel.getForeground());
            currentSubtotalValue.setFont(subtotalValue.getFont());
            currentSubtotalValue.setForeground(subtotalValue.getForeground());

            subtotalPanel.add(currentSubtotalLabel, BorderLayout.WEST);
            subtotalPanel.add(currentSubtotalValue, BorderLayout.EAST);
            summaryPanel.add(subtotalPanel);

            // Voucher Panel
            JPanel voucherPanel = new JPanel(new BorderLayout(5, 0));
            voucherPanel.setBackground(DARKER_BG);
            voucherPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

            JLabel voucherLabel = new JLabel("Voucher:");
            voucherLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            voucherLabel.setForeground(SECONDARY_TEXT);

            JTextField voucherField = new JTextField();
            voucherField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            voucherField.setBackground(new Color(50, 50, 50));
            voucherField.setForeground(TEXT_COLOR);
            voucherField.setCaretColor(TEXT_COLOR);
            voucherField.setPreferredSize(new Dimension(150, 22));

            JButton applyBtn = new JButton("APPLY");
            applyBtn.setBackground(SUCCESS_COLOR);
            applyBtn.setForeground(Color.WHITE);
            applyBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
            applyBtn.setBorderPainted(false);
            applyBtn.setFocusPainted(false);
            applyBtn.setPreferredSize(new Dimension(60, 22));

            JPanel voucherInputPanel = new JPanel(new BorderLayout(5, 0));
            voucherInputPanel.setBackground(DARKER_BG);
            voucherInputPanel.add(voucherField, BorderLayout.CENTER);
            voucherInputPanel.add(applyBtn, BorderLayout.EAST);

            voucherPanel.add(voucherLabel, BorderLayout.WEST);
            voucherPanel.add(voucherInputPanel, BorderLayout.CENTER);
            summaryPanel.add(voucherPanel);
            summaryPanel.add(Box.createVerticalStrut(10));

            // Total Panel - Create new label that references the instance variable
            JPanel totalPanel = new JPanel(new BorderLayout());
            totalPanel.setBackground(DARKER_BG);
            totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

            JLabel totalTextLabel = new JLabel("Total");
            totalTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            totalTextLabel.setForeground(TEXT_COLOR);

            // Create new label that mirrors the instance variable
            JLabel currentTotalValue = new JLabel(totalValue.getText());
            currentTotalValue.setFont(totalValue.getFont());
            currentTotalValue.setForeground(totalValue.getForeground());

            totalPanel.add(totalTextLabel, BorderLayout.WEST);
            totalPanel.add(currentTotalValue, BorderLayout.EAST);
            summaryPanel.add(totalPanel);
            
            // Add this to your cart panel creation code
            totalValue.addPropertyChangeListener("text", e -> {
                if ("₱60.00".equals(totalValue.getText()) && getSelectedItemCount() == 0) {
                    totalValue.setText("₱0.00");
                }
            });

            // Checkout Button
            JButton checkoutBtn = new JButton("CHECKOUT");
            checkoutBtn.setBackground(BUTTON_COLOR);
            checkoutBtn.setForeground(Color.WHITE);
            checkoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            checkoutBtn.setBorderPainted(false);
            checkoutBtn.setFocusPainted(false);
            checkoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            checkoutBtn.setPreferredSize(new Dimension(200, 40));

            checkoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    checkoutBtn.setBackground(HOVER_COLOR);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    checkoutBtn.setBackground(BUTTON_COLOR);
                }
            });

            
            checkoutBtn.addActionListener(e -> {
                if (getSelectedItemCount() > 0) {
                    try {
                        updateCheckoutTotals(true); // Update totals but DON'T clear items
                        if (cardPanel != null && cardLayout != null) {
                            cardLayout.show(cardPanel, "checkout"); // Just switch panels
                        } else {
                            JOptionPane.showMessageDialog(null,
                                "System error: Unable to proceed to checkout",
                                "Checkout Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                            "Error during checkout: " + ex.getMessage(),
                            "Checkout Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(cardPanel, 
                        "Please select at least one item to checkout",
                        "No Items Selected",
                        JOptionPane.WARNING_MESSAGE);
                }
            });

            JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
            buttonContainer.setBackground(DARKER_BG);
            buttonContainer.add(checkoutBtn);
            summaryPanel.add(buttonContainer);

            cartPanel.add(summaryPanel, BorderLayout.SOUTH);

            // Add a listener to update the local labels when the instance labels change
            subtotalLabel.addPropertyChangeListener("text", evt -> {
                currentSubtotalLabel.setText(subtotalLabel.getText());
            });
            subtotalValue.addPropertyChangeListener("text", evt -> {
                currentSubtotalValue.setText(subtotalValue.getText());
            });
            totalValue.addPropertyChangeListener("text", evt -> {
                currentTotalValue.setText(totalValue.getText());
            });

            return cartPanel;
        }
        
        private void updateSummary() {
            int selectedCount = getSelectedItemCount();
            double subtotal = getCartTotal();

            if (subtotalLabel != null) {
                subtotalLabel.setText("Subtotal (" + selectedCount + " items)");
            }
            if (subtotalValue != null) {
                subtotalValue.setText("₱" + String.format("%.2f", subtotal));
            }
            if (totalValue != null) {
                // Force 0.00 when no items are selected
                double displayTotal = selectedCount > 0 ? subtotal : 0.00;
                totalValue.setText("₱" + String.format("%.2f", displayTotal));
            }
            if (cartTotalLabel != null) {
                cartTotalLabel.setText("Selected Total: ₱" + String.format("%.2f", subtotal));
            }

            if (cardPanel != null) {
                cardPanel.revalidate();
                cardPanel.repaint();
            }
            System.out.println("Debug - updateSummary called: " + 
                 "selectedCount=" + selectedCount + 
                 ", subtotal=" + subtotal + 
                 ", totalValue=" + totalValue.getText());
        }

    private JPanel createCartItemPanel(CartItem cartItem, JPanel parentPanel) {
        JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
        itemPanel.setBackground(DARKER_BG);
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel leftPanel = new JPanel(new BorderLayout(15, 0));
        leftPanel.setBackground(DARKER_BG);

        JCheckBox selectBox = new JCheckBox();
        selectBox.setSelected(cartItem.isSelected());
        selectBox.setBackground(DARKER_BG);
        selectBox.addActionListener(e -> {
            boolean newState = selectBox.isSelected();
            System.out.println("Toggling selection for " + cartItem.getItem().getName() + 
                              " to " + newState);
            cartItem.setSelected(newState);
            updateCartDisplay(parentPanel);
            // Make sure this updates everything
            updateSummary();
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

        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setBackground(DARKER_BG);

        JLabel priceLabel = new JLabel("₱" + String.format("%.2f", cartItem.getItem().getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setForeground(ACCENT_COLOR);
        priceLabel.setHorizontalAlignment(SwingConstants.RIGHT);

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

        minusBtn.addActionListener(e -> {
            if (cartItem.getQuantity() > 1) {
                int newQuantity = cartItem.getQuantity() - 1;
                cartItem.setQuantity(newQuantity);
                quantityLabel.setText(String.valueOf(newQuantity));
                updateCartItemQuantity(cartItem.getItem().getProductId(), newQuantity);
                updateSummary();
            } else {
                // Optionally remove item if quantity reaches 0
                removeCartItem(cartItem.getItem().getProductId());
                cartItems.remove(cartItem);
                updateCartDisplay(parentPanel);
                updateSummary();
            }
        });

        plusBtn.addActionListener(e -> {
            int newQuantity = cartItem.getQuantity() + 1;
            cartItem.setQuantity(newQuantity);
            quantityLabel.setText(String.valueOf(newQuantity));
            updateCartItemQuantity(cartItem.getItem().getProductId(), newQuantity);
            updateSummary();
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
            updateCartDisplay(parentPanel);
            // Make sure this updates everything
            updateSummary();
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

        private void updateCartDisplay(JPanel itemsPanel) {
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
                JPanel selectAllPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                selectAllPanel.setBackground(DARKER_BG);
                selectAllPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JCheckBox selectAll = new JCheckBox("SELECT ALL");
                selectAll.setFont(new Font("Segoe UI", Font.BOLD, 14));
                selectAll.setForeground(TEXT_COLOR);
                selectAll.setBackground(DARKER_BG);

                boolean allSelected = !cartItems.isEmpty() && cartItems.stream().allMatch(CartItem::isSelected);
                selectAll.setSelected(allSelected);

                selectAll.addActionListener(e -> {
                    boolean selected = selectAll.isSelected();
                    cartItems.forEach(item -> item.setSelected(selected));
                    updateCartDisplay(itemsPanel);
                    updateSummary();
                });

                selectAllPanel.add(selectAll);
                itemsPanel.add(selectAllPanel);
                itemsPanel.add(Box.createVerticalStrut(10));

                for (CartItem item : cartItems) {
                    itemsPanel.add(createCartItemPanel(item, itemsPanel));
                    itemsPanel.add(Box.createVerticalStrut(10));
                }
            }

            updateSummary();

            itemsPanel.revalidate();
            itemsPanel.repaint();
        }

    public JPanel createCheckoutPanel(JPanel mainCardPanel, CardLayout cardLayout) {
        JPanel checkoutPanel = new JPanel(new BorderLayout());
        checkoutPanel.setBackground(BACKGROUND_COLOR);
        checkoutPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title and back button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton backBtn = new JButton("← Back to Cart");
        backBtn.addActionListener(e -> cardLayout.show(mainCardPanel, "cart"));
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

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Delivery Method Panel (unchanged)
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

        // Shipping Address Panel (updated)
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

        // Address Combo Box
        JComboBox<String> addressCombo = new JComboBox<>();
        addressCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressCombo.setBackground(new Color(50, 50, 50));
        addressCombo.setForeground(TEXT_COLOR);
        addressCombo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        addressCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, addressCombo.getPreferredSize().height));

        // Load current address
        addressCombo.addItem(currentUser.getAddress());
        addressPanel.add(addressCombo);
        addressPanel.add(Box.createVerticalStrut(10));

        // Add Address Button
        JButton addAddressBtn = new JButton("Add Shipping Address");
        addAddressBtn.setContentAreaFilled(false);
        addAddressBtn.setBorderPainted(false);
        addAddressBtn.setFocusPainted(false);
        addAddressBtn.setForeground(ACCENT_COLOR);
        addAddressBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addAddressBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addAddressBtn.addActionListener(e -> showAddAddressDialog(addressCombo));

        addressPanel.add(addAddressBtn);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        contentPanel.add(addressPanel, gbc);

        // Order Summary Panel (unchanged)
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
        // Update the instance labels first
        subtotalLabel.setText("Subtotal (" + getSelectedItemCount() + " items)");
        subtotalValue.setText("₱" + String.format("%.2f", getCartTotal()));

        // Create checkout labels that mirror the instance labels
        JLabel checkoutSubtotalLabel = new JLabel("Subtotal (" + getSelectedItemCount() + " items)");
        checkoutSubtotalLabel.setFont(subtotalLabel.getFont());
        checkoutSubtotalLabel.setForeground(subtotalLabel.getForeground());
        this.checkoutSubtotalValue = new JLabel("₱" + String.format("%.2f", getCartTotal())); // Just the subtotal without shipping
        checkoutSubtotalValue.setFont(subtotalValue.getFont());
        checkoutSubtotalValue.setForeground(subtotalValue.getForeground());
        
        subtotalPanel.add(checkoutSubtotalLabel, BorderLayout.WEST);
        subtotalPanel.add(checkoutSubtotalValue, BorderLayout.EAST);

        
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
     
        totalValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalValue.setForeground(ACCENT_COLOR);
        
        this.checkoutTotalValue = new JLabel("₱" + String.format("%.2f", getCartTotal() + 60.00)); // Include shipping by default
        checkoutTotalValue.setFont(totalValue.getFont());
        checkoutTotalValue.setForeground(totalValue.getForeground());
        totalPanel.add(totalLabel, BorderLayout.WEST);
        totalPanel.add(checkoutTotalValue, BorderLayout.EAST);
        
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
        cardDetailsPanel.setFocusable(true);

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
        walletDetailsPanel.setFocusable(true);
         
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
            // Scroll to card details after the panel is visible
            SwingUtilities.invokeLater(() -> scrollToComponent(cardDetailsPanel));
        });

        walletBtn.addActionListener(e -> {
            cardDetailsPanel.setVisible(false);
            walletDetailsPanel.setVisible(true);
            contentPanel.revalidate();
            contentPanel.repaint();
            // Scroll to wallet details after the panel is visible
            SwingUtilities.invokeLater(() -> scrollToComponent(walletDetailsPanel));
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

        placeOrderBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                placeOrderBtn.setBackground(HOVER_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                placeOrderBtn.setBackground(BUTTON_COLOR);
            }
        });
        
        placeOrderBtn.addActionListener(e -> {
            if (getSelectedItemCount() > 0) {
                placeOrderBtn.setEnabled(false); // Disable button to prevent double clicks

                // Use SwingWorker for background processing
                new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        try {
                            processOrder(currentUser);
                            return true; // Success
                        } catch (SQLException ex) {
                            return false; // Failure
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            boolean success = get(); // Get the result
                            if (success) {
                                // Show confirmation on success
                                SwingUtilities.invokeLater(() -> {
                                    cardLayout.show(mainCardPanel, "orderConfirmation");
                                });
                            }
                        } catch (Exception ex) {
                            // Show error message on failure
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(checkoutPanel,
                                    "Error processing order: " + ex.getMessage(),
                                    "Order Error",
                                    JOptionPane.ERROR_MESSAGE);
                            });
                        } finally {
                            // Re-enable button in all cases
                            SwingUtilities.invokeLater(() -> {
                                placeOrderBtn.setEnabled(true);
                                forceCartRefresh(); // Refresh cart display
                            });
                        }
                    }
                }.execute();
            } else {
                JOptionPane.showMessageDialog(checkoutPanel, 
                    "Please select at least one item to checkout",
                    "No Items Selected",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        buttonPanel.add(placeOrderBtn);
        checkoutPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.checkoutPanel = checkoutPanel;
        updateCheckoutTotals(true);
        
        return checkoutPanel;
    }
    
    private void showAddAddressDialog(JComboBox<String> addressCombo) {
        // Create a custom panel for the dialog
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(DARKER_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title Label
        JLabel titleLabel = new JLabel("Add New Shipping Address");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Input Field
        JTextField addressField = new JTextField(20);
        addressField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressField.setBackground(new Color(50, 50, 50));
        addressField.setForeground(TEXT_COLOR);
        addressField.setCaretColor(TEXT_COLOR);
        addressField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(addressField, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(DARKER_BG);

        // OK Button (Accent Color)
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        okButton.setBackground(ACCENT_COLOR);
        okButton.setForeground(Color.WHITE);
        okButton.setBorderPainted(false);
        okButton.setFocusPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> {
            String newAddress = addressField.getText().trim();
            if (!newAddress.isEmpty()) {
                saveAddressToDatabase(newAddress);
                addressCombo.addItem(newAddress);
                addressCombo.setSelectedItem(newAddress);
                ((Window) SwingUtilities.getRoot(panel)).dispose(); // Close dialog
            }
        });

        // Cancel Button (Neutral Color)
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelButton.setBackground(new Color(70, 70, 70));
        cancelButton.setForeground(TEXT_COLOR);
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> {
            ((Window) SwingUtilities.getRoot(panel)).dispose(); // Close dialog
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Create and display the dialog
        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(checkoutPanel),
            "Add Address",
            true
        );
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().setBackground(DARKER_BG);
        dialog.setResizable(false);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(checkoutPanel); // Center relative to parent
        dialog.setVisible(true);
    }
    
    private void saveAddressToDatabase(String address) {
        try (Connection conn = DBConnection.getConnection()) {
            // First check if this is the user's first address (other than default)
            String checkQuery = "SELECT COUNT(*) AS count FROM user_addresses WHERE user_id = ?";
            PreparedStatement checkPS = conn.prepareStatement(checkQuery);
            checkPS.setInt(1, currentUser.getUserId());
            ResultSet rs = checkPS.executeQuery();

            boolean isFirstAddress = false;
            if (rs.next()) {
                isFirstAddress = rs.getInt("count") == 0;
            }

            // Insert into addresses table
            String insert = "INSERT INTO user_addresses (user_id, address, is_default) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(insert);
            ps.setInt(1, currentUser.getUserId());
            ps.setString(2, address);
            ps.setInt(3, isFirstAddress ? 1 : 0); // Set as default if first address
            ps.executeUpdate();

            // If this is the first address, update the users table too
            if (isFirstAddress) {
                String updateUser = "UPDATE users SET address = ? WHERE user_id = ?";
                PreparedStatement updatePS = conn.prepareStatement(updateUser);
                updatePS.setString(1, address);
                updatePS.setInt(2, currentUser.getUserId());
                updatePS.executeUpdate();
                currentUser.setAddress(address);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(checkoutPanel,
                "Error saving address: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCheckoutTotals(boolean isDelivery) {
        double subtotal = getCartTotal(); // This already calculates only selected items
        double shipping = isDelivery ? 60.00 : 0.00;
        double total = subtotal + shipping;

        if (checkoutSubtotalValue != null) {
            checkoutSubtotalValue.setText("₱" + String.format("%.2f", subtotal));
            subtotalValue.setText("₱" + String.format("%.2f", subtotal)); // Also update the main subtotal
        }
        if (shippingValue != null) {
            shippingValue.setText(isDelivery ? "₱60.00" : "₱0.00");
        }
        if (checkoutTotalValue != null) {
            checkoutTotalValue.setText("₱" + String.format("%.2f", total));
            totalValue.setText("₱" + String.format("%.2f", total)); // Also update the main total
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
    
    public JPanel createOrderConfirmationPanel(JPanel cardPanel, CardLayout cardLayout) {
        JPanel confirmationPanel = new JPanel(new BorderLayout());
        confirmationPanel.setBackground(BACKGROUND_COLOR);
        confirmationPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
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
            cardLayout.show(cardPanel, "cart");  // Changed from "products" to "cart"
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
        updateSummary();
    }
    
        /**
     * Forces a complete refresh of the cart display
     */
    public void refreshCartDisplay() {
        System.out.println("[DEBUG] Refreshing cart display...");

        if (cardPanel != null) {
            // Find and remove existing cart panel
            for (Component comp : cardPanel.getComponents()) {
                if ("cartPanel".equals(comp.getName())) {
                    cardPanel.remove(comp);
                    break;
                }
            }

            // Create and add new cart panel
            JPanel newCartPanel = createCartPanel(cardPanel, cardLayout);
            newCartPanel.setName("cartPanel");
            cardPanel.add(newCartPanel, "cart");

            cardPanel.revalidate();
            cardPanel.repaint();
        }

        updateSummary();
        System.out.println("[DEBUG] Cart display refreshed");
    }    
    
    private void processOrder(User user) throws SQLException {
    if (getSelectedItemCount() == 0) {
        throw new IllegalStateException("No items selected for order");
    }

    try (Connection conn = DBConnection.getConnection()) {
        conn.setAutoCommit(false);

        // 1. Create the order record
        String orderQuery = "INSERT INTO orders (user_id, order_date, status, " +
                          "delivery_method, delivery_address, payment_method, " +
                          "subtotal, shipping_fee, total_amount) " +
                          "VALUES (?, NOW(), 'PENDING', ?, ?, ?, ?, ?, ?)";

        PreparedStatement orderPS = conn.prepareStatement(orderQuery, PreparedStatement.RETURN_GENERATED_KEYS);
    
        orderPS.setInt(1, user.getUserId());
        orderPS.setString(2, "DELIVERY");
        orderPS.setString(3, user.getAddress());
        orderPS.setString(4, "COD");

        double subtotal = getCartTotal();
        double shipping = 60.00;
        double total = subtotal + shipping;

        orderPS.setDouble(5, subtotal);
        orderPS.setDouble(6, shipping);
        orderPS.setDouble(7, total);

        orderPS.executeUpdate();

        // Get the generated order ID
        int orderId;
        try (ResultSet generatedKeys = orderPS.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating order failed, no ID obtained.");
            }
        }

        // 2. Add order items
        String itemQuery = "INSERT INTO order_items (order_id, product_id, quantity, price_at_order) " +
                         "VALUES (?, ?, ?, ?)";
        PreparedStatement itemPS = conn.prepareStatement(itemQuery);

        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                itemPS.setInt(1, orderId);
                itemPS.setInt(2, item.getItem().getProductId());
                itemPS.setInt(3, item.getQuantity());
                itemPS.setDouble(4, item.getItem().getPrice());
                itemPS.addBatch();
            }
        }

        itemPS.executeBatch();

        // 3. Remove ordered items from cart
        String deleteQuery = "DELETE FROM cart_items WHERE cart_id = ? AND product_id = ?";
        PreparedStatement deletePS = conn.prepareStatement(deleteQuery);

        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                deletePS.setInt(1, currentCartId);
                deletePS.setInt(2, item.getItem().getProductId());
                deletePS.addBatch();
            }
        }

        deletePS.executeBatch();

        // Commit transaction
        conn.commit();

        // Clear selected items from local cart
        clearSelectedItems();
        
    } catch (SQLException e) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.rollback();
        }
        throw e; // Re-throw to be handled by the SwingWorker
    }
}
    
    private void forceCartRefresh() {
        // Reload cart items from database
        loadCartItems();

        // Recreate the cart panel
        if (cardPanel != null) {
            // Remove existing cart panel if it exists
            for (Component comp : cardPanel.getComponents()) {
                if (comp.getName() != null && comp.getName().equals("cartPanel")) {
                    cardPanel.remove(comp);
                    break;
                }
            }

            // Create and add new cart panel
            JPanel newCartPanel = createCartPanel(cardPanel, cardLayout);
            newCartPanel.setName("cartPanel");
            cardPanel.add(newCartPanel, "cart");

            // Force UI update
            cardPanel.revalidate();
            cardPanel.repaint();
        }

        updateSummary();
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
        // Validate input
        if (product == null || quantity <= 0) {
            JOptionPane.showMessageDialog(null,
                "Invalid product or quantity",
                "Add to Cart Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Debug logging
            System.out.println("[DEBUG] Adding to cart - Product: " + product.getName() + 
                             ", Qty: " + quantity + 
                             ", Current Cart ID: " + currentCartId);

            // Create cart if doesn't exist
            if (currentCartId == -1) {
                String checkQuery = "SELECT cart_id FROM carts WHERE user_id = ?";
                try (PreparedStatement checkPS = conn.prepareStatement(checkQuery)) {
                    checkPS.setInt(1, currentUser.getUserId());
                    ResultSet rs = checkPS.executeQuery();

                    if (rs.next()) {
                        currentCartId = rs.getInt("cart_id");
                        System.out.println("[DEBUG] Found existing cart ID: " + currentCartId);
                    } else {
                        String createQuery = "INSERT INTO carts (user_id) VALUES (?)";
                        try (PreparedStatement createPS = conn.prepareStatement(createQuery, 
                                PreparedStatement.RETURN_GENERATED_KEYS)) {
                            createPS.setInt(1, currentUser.getUserId());
                            createPS.executeUpdate();

                            ResultSet keys = createPS.getGeneratedKeys();
                            if (keys.next()) {
                                currentCartId = keys.getInt(1);
                                System.out.println("[DEBUG] Created new cart ID: " + currentCartId);
                            }
                        }
                    }
                }
            }

            // Check if item exists in cart
            String checkItemQuery = "SELECT quantity FROM cart_items WHERE cart_id = ? AND product_id = ?";
            try (PreparedStatement checkItemPS = conn.prepareStatement(checkItemQuery)) {
                checkItemPS.setInt(1, currentCartId);
                checkItemPS.setInt(2, product.getProductId());
                ResultSet itemRS = checkItemPS.executeQuery();

                if (itemRS.next()) {
                    // Update existing item
                    int currentQty = itemRS.getInt("quantity");
                    String updateQuery = "UPDATE cart_items SET quantity = ? WHERE cart_id = ? AND product_id = ?";
                    try (PreparedStatement updatePS = conn.prepareStatement(updateQuery)) {
                        updatePS.setInt(1, currentQty + quantity);
                        updatePS.setInt(2, currentCartId);
                        updatePS.setInt(3, product.getProductId());
                        updatePS.executeUpdate();
                        System.out.println("[DEBUG] Updated existing item quantity");
                    }

                    // Update local cart
                    for (CartItem item : cartItems) {
                        if (item.getItem().getProductId() == product.getProductId()) {
                            item.setQuantity(item.getQuantity() + quantity);
                            break;
                        }
                    }
                } else {
                    // Add new item
                    String addQuery = "INSERT INTO cart_items (cart_id, product_id, quantity, date_added) " +
                                    "VALUES (?, ?, ?, NOW())";
                    try (PreparedStatement addPS = conn.prepareStatement(addQuery)) {
                        addPS.setInt(1, currentCartId);
                        addPS.setInt(2, product.getProductId());
                        addPS.setInt(3, quantity);
                        addPS.executeUpdate();
                        System.out.println("[DEBUG] Added new item to cart");
                    }

                    // Add to local cart
                    CartItem newItem = new CartItem(product);
                    newItem.setQuantity(quantity);
                    newItem.setSelected(false);
                    cartItems.add(newItem);
                }
            }

            // Show success message
            JOptionPane.showMessageDialog(null,
                product.getName() + " (x" + quantity + ") added to cart!",
                "Product Added",
                JOptionPane.INFORMATION_MESSAGE);

            // Force UI refresh
            SwingUtilities.invokeLater(() -> {
                loadCartItems();
                refreshCartDisplay();
            });

        } catch (SQLException e) {
            System.err.println("[ERROR] addToCart failed: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error adding item to cart. Please try again.\nError: " + e.getMessage(),
                "Cart Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
 * Loads cart items from database while preserving selection states
 */
private void loadCartItems() {
    System.out.println("[DEBUG] Loading cart items...");
    
    // Store current selection states before clearing
    Map<Integer, Boolean> selectionStates = new HashMap<>();
    for (CartItem item : cartItems) {
        selectionStates.put(item.getItem().getProductId(), item.isSelected());
    }

    // Clear current items
    List<CartItem> oldCartItems = new ArrayList<>(cartItems);
    cartItems.clear();

    try (Connection conn = DBConnection.getConnection()) {
        // First get the cart ID if we don't have it
        if (currentCartId == -1) {
            String cartQuery = "SELECT cart_id FROM carts WHERE user_id = ?";
            try (PreparedStatement cartPS = conn.prepareStatement(cartQuery)) {
                cartPS.setInt(1, currentUser.getUserId());
                ResultSet cartRS = cartPS.executeQuery();
                if (cartRS.next()) {
                    currentCartId = cartRS.getInt("cart_id");
                    System.out.println("[DEBUG] Loaded cart ID: " + currentCartId);
                } else {
                    System.out.println("[DEBUG] No cart exists for user");
                    return; // No cart exists yet
                }
            }
        }

        // Load all items for this cart
        String itemsQuery = "SELECT ci.*, p.name, p.description, p.price, p.category " +
                          "FROM cart_items ci " +
                          "JOIN products p ON ci.product_id = p.product_id " +
                          "WHERE ci.cart_id = ?";
        try (PreparedStatement itemsPS = conn.prepareStatement(itemsQuery)) {
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
                // Restore selection state if previously set
                item.setSelected(selectionStates.getOrDefault(menuItem.getProductId(), false));
                cartItems.add(item);
                
                System.out.println("[DEBUG] Loaded item: " + menuItem.getName() + 
                                 " (Qty: " + item.getQuantity() + 
                                 ", Selected: " + item.isSelected() + ")");
            }
        }
        
        System.out.println("[DEBUG] Total items loaded: " + cartItems.size());

    } catch (SQLException e) {
        System.err.println("[ERROR] loadCartItems failed: " + e.getMessage());
        e.printStackTrace();
        // Restore old items if loading failed
        cartItems = oldCartItems;
        JOptionPane.showMessageDialog(null,
            "Error loading cart items. Please try again.",
            "Cart Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void updateCartItemQuantity(int productId, int newQuantity) {
        try (Connection conn = DBConnection.getConnection()) {
            String updateQuery = "UPDATE cart_items SET quantity = ? WHERE cart_id = ? AND product_id = ?";
            PreparedStatement ps = conn.prepareStatement(updateQuery);
            ps.setInt(1, newQuantity);
            ps.setInt(2, currentCartId);
            ps.setInt(3, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error updating cart quantity: " + e.getMessage(),
                "Database Error",
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

            // Check if cart is now empty
            String checkQuery = "SELECT COUNT(*) FROM cart_items WHERE cart_id = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkQuery);
            checkPs.setInt(1, currentCartId);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                // Delete empty cart
                String deleteCartQuery = "DELETE FROM carts WHERE cart_id = ?";
                PreparedStatement deleteCartPs = conn.prepareStatement(deleteCartQuery);
                deleteCartPs.setInt(1, currentCartId);
                deleteCartPs.executeUpdate();
                currentCartId = -1; // Reset cart ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error removing item from cart: " + e.getMessage(),
                "Cart Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void scrollToComponent(JComponent component) {
        JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, component);
        if (viewport != null) {
            Rectangle rect = component.getBounds();
            Rectangle viewRect = viewport.getViewRect();
            // Center the component in the viewport
            rect.setLocation(rect.x - viewRect.x, rect.y - viewRect.y);
            viewport.scrollRectToVisible(rect);
        }
    }
    
}