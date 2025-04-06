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
    
    // Color scheme to match dashboard
    private final Color BACKGROUND_COLOR = new Color(40, 40, 40);
    private final Color DARKER_BG = new Color(30, 30, 30);
    private final Color BORDER_COLOR = new Color(60, 60, 60);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color SECONDARY_TEXT = new Color(180, 180, 180);
    private final Color ACCENT_COLOR = new Color(235, 94, 40);  // Orange accent
    private final Color BUTTON_COLOR = new Color(235, 94, 40);
    private final Color SUCCESS_COLOR = new Color(76, 175, 80);
    
    public CartManager(User user) {
        this.currentUser = user;
        loadCartItems();
        updateCartTotal();
    }
    
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    
    public double getCartTotal() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                total += item.getItem().getPrice() * item.getQuantity();
            }
        }
        return total;
    }
    
    // Create cart panel that can be added to the main user dashboard
    public JPanel createCartPanel(JPanel cardPanel, CardLayout cardLayout) {
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBackground(BACKGROUND_COLOR);
        cartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title panel
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

        // Cart items panel
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
            });
            selectAllPanel.add(selectAll);
            itemsPanel.add(selectAllPanel);
            itemsPanel.add(Box.createVerticalStrut(10));

            // Add cart items
            for (CartItem item : cartItems) {
                itemsPanel.add(createCartItemPanel(item, itemsPanel, itemCountLabel));
                itemsPanel.add(Box.createVerticalStrut(10));
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
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Subtotal
        JPanel subtotalPanel = new JPanel(new BorderLayout());
        subtotalPanel.setBackground(DARKER_BG);
        JLabel subtotalLabel = new JLabel("Subtotal (" + getSelectedItemCount() + " items)");
        subtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtotalLabel.setForeground(SECONDARY_TEXT);
        JLabel subtotalValue = new JLabel("₱" + String.format("%.2f", getCartTotal()));
        subtotalValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subtotalValue.setForeground(TEXT_COLOR);
        subtotalPanel.add(subtotalLabel, BorderLayout.WEST);
        subtotalPanel.add(subtotalValue, BorderLayout.EAST);
        summaryPanel.add(subtotalPanel);

        // Shipping fee
        JPanel shippingPanel = new JPanel(new BorderLayout());
        shippingPanel.setBackground(DARKER_BG);
        shippingPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel shippingLabel = new JLabel("Shipping Fee");
        shippingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        shippingLabel.setForeground(SECONDARY_TEXT);
        JLabel shippingValue = new JLabel("₱40.00");
        shippingValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        shippingValue.setForeground(TEXT_COLOR);
        shippingPanel.add(shippingLabel, BorderLayout.WEST);
        shippingPanel.add(shippingValue, BorderLayout.EAST);
        summaryPanel.add(shippingPanel);

        // Voucher code
        JPanel voucherPanel = new JPanel(new BorderLayout(5, 0));
        voucherPanel.setBackground(DARKER_BG);
        voucherPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel voucherTitleLabel = new JLabel("Enter Voucher Code");
        voucherTitleLabel.setForeground(SECONDARY_TEXT);
        voucherTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JTextField voucherField = new JTextField();
        voucherField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        voucherField.setBackground(new Color(50, 50, 50));
        voucherField.setForeground(TEXT_COLOR);
        voucherField.setCaretColor(TEXT_COLOR);
        
        JButton applyBtn = new JButton("APPLY");
        applyBtn.setBackground(SUCCESS_COLOR);
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        applyBtn.setBorderPainted(false);
        applyBtn.setFocusPainted(false);
        
        voucherPanel.add(voucherTitleLabel, BorderLayout.NORTH);
        voucherPanel.add(voucherField, BorderLayout.CENTER);
        voucherPanel.add(applyBtn, BorderLayout.EAST);
        summaryPanel.add(voucherPanel);
        summaryPanel.add(Box.createVerticalStrut(10));

        // Total
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(DARKER_BG);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        JLabel totalLabel = new JLabel("Total");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(TEXT_COLOR);
        JLabel totalValue = new JLabel("₱" + String.format("%.2f", getCartTotal() + 40.00));
        totalValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalValue.setForeground(ACCENT_COLOR);
        totalPanel.add(totalLabel, BorderLayout.WEST);
        totalPanel.add(totalValue, BorderLayout.EAST);
        summaryPanel.add(totalPanel);

        // VAT notice
        JLabel vatLabel = new JLabel("VAT included, where applicable");
        vatLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        vatLabel.setForeground(SECONDARY_TEXT);
        vatLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        summaryPanel.add(vatLabel);
        summaryPanel.add(Box.createVerticalStrut(15));

        // Checkout button
        JButton checkoutBtn = new JButton("PROCEED TO CHECKOUT");
        checkoutBtn.setBackground(BUTTON_COLOR);
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        checkoutBtn.setBorderPainted(false);
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setPreferredSize(new Dimension(0, 50));
        checkoutBtn.addActionListener(e -> {
            if (getSelectedItemCount() > 0) {
                cardLayout.show(cardPanel, "checkout");
            } else {
                JOptionPane.showMessageDialog(cartPanel, 
                    "Please select at least one item to checkout",
                    "No Items Selected",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        summaryPanel.add(checkoutBtn);

        cartPanel.add(summaryPanel, BorderLayout.SOUTH);

        return cartPanel;
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
            updateCartDisplay(parentPanel, itemCountLabel);
            updateCartTotal(); 
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
        minusBtn.setBorderPainted(false);
        minusBtn.setFocusPainted(false);
        
        JLabel quantityLabel = new JLabel(String.valueOf(cartItem.getQuantity()));
        quantityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        quantityLabel.setForeground(TEXT_COLOR);
        quantityLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        
        JButton plusBtn = new JButton("+");
        plusBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        plusBtn.setPreferredSize(new Dimension(30, 30));
        plusBtn.setBackground(new Color(60, 60, 60));
        plusBtn.setForeground(TEXT_COLOR);
        plusBtn.setBorderPainted(false);
        plusBtn.setFocusPainted(false);
        
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
        deleteBtn.addActionListener(e -> {
            removeCartItem(cartItem.getItem().getProductId());
            cartItems.remove(cartItem);
            updateCartDisplay(parentPanel, itemCountLabel);
            updateCartTotal();
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
            selectAll.addActionListener(e -> {
                boolean selected = selectAll.isSelected();
                cartItems.forEach(item -> item.setSelected(selected));
                updateCartDisplay(itemsPanel, itemCountLabel);
            });
            selectAllPanel.add(selectAll);
            updateCartTotal();
            itemsPanel.add(selectAllPanel);
            itemsPanel.add(Box.createVerticalStrut(10));

            // Add cart items
            for (CartItem item : cartItems) {
                itemsPanel.add(createCartItemPanel(item, itemsPanel, itemCountLabel));
                itemsPanel.add(Box.createVerticalStrut(10));
            }
        }

        itemCountLabel.setText(cartItems.size() + " item(s)");
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }
    
    private void updateCartTotal() {
        if (cartTotalLabel != null) {
            cartTotalLabel.setText("Selected Total: ₱" + String.format("%.2f", getCartTotal()));
        }
    }

    // Create checkout panel for the main dashboard
    public JPanel createCheckoutPanel(JPanel cardPanel, CardLayout cardLayout) {
        JPanel checkoutPanel = new JPanel(new BorderLayout());
        checkoutPanel.setBackground(BACKGROUND_COLOR);
        checkoutPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title and back button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton backBtn = new JButton("← Back to Cart");
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setForeground(ACCENT_COLOR);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "cart"));
        titlePanel.add(backBtn, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Checkout", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        checkoutPanel.add(titlePanel, BorderLayout.NORTH);

        // Main content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);

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
        addressPanel.add(addAddressBtn);
        contentPanel.add(addressPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Order summary
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(DARKER_BG);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel summaryTitle = new JLabel("Order Summary");
        summaryTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        summaryTitle.setForeground(TEXT_COLOR);
        summaryPanel.add(summaryTitle);
        summaryPanel.add(Box.createVerticalStrut(15));

        // Add order items with dividers
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
        
        // Add a divider before totals
        JSeparator totalsSeparator = new JSeparator();
        totalsSeparator.setForeground(BORDER_COLOR);
        totalsSeparator.setBackground(BORDER_COLOR);
        summaryPanel.add(totalsSeparator);
        summaryPanel.add(Box.createVerticalStrut(10));

        // Subtotal
        JPanel subtotalPanel = new JPanel(new BorderLayout());
        subtotalPanel.setBackground(DARKER_BG);
        subtotalPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel subtotalLabel = new JLabel("Subtotal (" + getSelectedItemCount() + " items)");
        subtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtotalLabel.setForeground(SECONDARY_TEXT);

        JLabel subtotalValue = new JLabel("₱" + String.format("%.2f", getCartTotal()));
        subtotalValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subtotalValue.setForeground(TEXT_COLOR);

        subtotalPanel.add(subtotalLabel, BorderLayout.WEST);
        subtotalPanel.add(subtotalValue, BorderLayout.EAST);
        summaryPanel.add(subtotalPanel);

        // Shipping fee
        JPanel shippingPanel = new JPanel(new BorderLayout());
        shippingPanel.setBackground(DARKER_BG);
        shippingPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel shippingLabel = new JLabel("Shipping Fee");
        shippingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        shippingLabel.setForeground(SECONDARY_TEXT);

        JLabel shippingValue = new JLabel("₱40.00");
        shippingValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        shippingValue.setForeground(TEXT_COLOR);

        shippingPanel.add(shippingLabel, BorderLayout.WEST);
        shippingPanel.add(shippingValue, BorderLayout.EAST);
        summaryPanel.add(shippingPanel);

        contentPanel.add(summaryPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Payment method
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
        
        // Custom radio buttons with better styling
        JRadioButton codBtn = createStyledRadioButton("Cash on Delivery", true);
        JRadioButton cardBtn = createStyledRadioButton("Credit/Debit Card", false);
        JRadioButton gcashBtn = createStyledRadioButton("GCash", false);

        paymentGroup.add(codBtn);
        paymentGroup.add(cardBtn);
        paymentGroup.add(gcashBtn);

        JPanel codPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        codPanel.setBackground(DARKER_BG);
        codPanel.add(codBtn);
        
        JPanel cardPaymentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        cardPanel.setBackground(DARKER_BG);
        cardPaymentPanel.add(cardBtn);
        
        JPanel gcashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        gcashPanel.setBackground(DARKER_BG);
        gcashPanel.add(gcashBtn);

        paymentPanel.add(codPanel);
        paymentPanel.add(cardPanel);
        paymentPanel.add(gcashPanel);
        contentPanel.add(paymentPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Total and place order button
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(BACKGROUND_COLOR);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        JLabel totalLabel = new JLabel("Total");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(TEXT_COLOR);

        JLabel totalValue = new JLabel("₱" + String.format("%.2f", getCartTotal() + 40.00));
        totalValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalValue.setForeground(ACCENT_COLOR);

        totalPanel.add(totalLabel, BorderLayout.WEST);
        totalPanel.add(totalValue, BorderLayout.EAST);
        contentPanel.add(totalPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        JButton placeOrderBtn = new JButton("PLACE ORDER");
        placeOrderBtn.setBackground(BUTTON_COLOR);
        placeOrderBtn.setForeground(Color.WHITE);
        placeOrderBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        placeOrderBtn.setBorderPainted(false);
        placeOrderBtn.setFocusPainted(false);
        placeOrderBtn.setPreferredSize(new Dimension(0, 50));
        placeOrderBtn.addActionListener(e -> {
            if (processOrder()) {
                // Clear selected items and show confirmation
                JOptionPane.showMessageDialog(checkoutPanel,
                    "Order placed successfully!",
                    "Order Confirmation",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Clear cart and return to dashboard
                for (CartItem item : new ArrayList<>(cartItems)) {
                    if (item.isSelected()) {
                        cartItems.remove(item);
                        removeCartItem(item.getItem().getProductId());
                    }
                }
                
                cardLayout.show(cardPanel, "dashboard");
            }
        });
        contentPanel.add(placeOrderBtn);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        checkoutPanel.add(scrollPane, BorderLayout.CENTER);

        this.checkoutPanel = checkoutPanel;
        return checkoutPanel;
    }
    
    private JRadioButton createStyledRadioButton(String text, boolean selected) {
        JRadioButton radioButton = new JRadioButton(text);
        radioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        radioButton.setForeground(TEXT_COLOR);
        radioButton.setBackground(DARKER_BG);
        radioButton.setSelected(selected);
        return radioButton;
    }
    
    private boolean processOrder() {
        try (Connection conn = DBConnection.getConnection()) {
            // First save address if modified
            if (checkoutPanel != null) {
                // Find the address textarea - this is a simplified approach
                Component[] components = checkoutPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JScrollPane) {
                        JViewport viewport = ((JScrollPane) comp).getViewport();
                        Component view = viewport.getView();
                        if (view instanceof JTextArea) {
                            String address = ((JTextArea) view).getText();
                            if (!address.equals(currentUser.getAddress())) {
                                // Update user address
                                String updateQuery = "UPDATE users SET address = ? WHERE user_id = ?";
                                PreparedStatement ps = conn.prepareStatement(updateQuery);
                                ps.setString(1, address);
                                ps.setInt(2, currentUser.getUserId());
                                ps.executeUpdate();
                                currentUser.setAddress(address);
                            }
                            break;
                        }
                    }
                }
            }
            
            // Create a new order
            String orderQuery = "INSERT INTO orders (user_id, order_date, status, shipping_fee, total_amount) VALUES (?, NOW(), ?, ?, ?)";
            PreparedStatement orderPS = conn.prepareStatement(orderQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            orderPS.setInt(1, currentUser.getUserId());
            orderPS.setString(2, "Processing");
            orderPS.setDouble(3, 40.00);
            orderPS.setDouble(4, getCartTotal() + 40.00);
            orderPS.executeUpdate();
            
            ResultSet rs = orderPS.getGeneratedKeys();
            int orderId = -1;
            if (rs.next()) {
                orderId = rs.getInt(1);
            } else {
                return false;
            }
            
            // Add order items
            String itemQuery = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
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
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error placing order: " + e.getMessage(),
                "Order Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private int getSelectedItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                count++;
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
                newItem.setSelected(true);
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
            // First get the active cart
            String cartQuery = "SELECT ci.* FROM cart_items ci " +
                                "JOIN carts c ON ci.cart_id = c.cart_id " +
                                "WHERE c.user_id = ?";
            PreparedStatement cartPS = conn.prepareStatement(cartQuery);
            cartPS.setInt(1, currentUser.getUserId());
            ResultSet cartRS = cartPS.executeQuery();

            if (cartRS.next()) {
                currentCartId = cartRS.getInt("cart_id");

                // Now load cart items
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
                        itemsRS.getString("description") // Using description instead of image_path
                    );

                    CartItem item = new CartItem(menuItem);
                    item.setQuantity(itemsRS.getInt("quantity"));
                    item.setSelected(true);
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