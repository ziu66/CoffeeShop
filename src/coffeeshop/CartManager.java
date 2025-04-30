/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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
import javax.swing.plaf.basic.BasicComboBoxUI;

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
        private JLabel checkoutSubtotalLabel;
        private JLabel checkoutShippingValue;
        private JLabel checkoutTotalValue;
        private CardLayout cardLayout;  // Add this field
        private static final int POINTS_PER_500_PESOS = 50;
        private JLabel rewardPointsLabel;
        private JLabel rewardDiscountLabel;
        private JLabel checkoutRewardDiscountLabel;
        private double rewardDiscount = 0.0;
        private boolean isDeliverySelected = true; 
        private JCheckBox selectAllCheckbox;
        private JPanel selectAllPanel;
        private JPanel orderConfirmationPanel; // <-- ADD THIS LINE
        private JLabel orderIdLabel; 
        private Reward appliedReward = null;

        private final Color BACKGROUND_COLOR = new Color(40, 40, 40);
        private final Color DARKER_BG = new Color(30, 30, 30);
        private final Color BORDER_COLOR = new Color(60, 60, 60);
        private final Color TEXT_COLOR = Color.WHITE;
        private final Color SECONDARY_TEXT = new Color(180, 180, 180);
        private final Color ACCENT_COLOR = new Color(235, 94, 40);  // Orange accent
        private final Color BUTTON_COLOR = new Color(235, 94, 40);
        private final Color SUCCESS_COLOR = new Color(76, 175, 80);
        private final Color HOVER_COLOR = new Color(245, 124, 70);  // Lighter orange for hover effects
        private static final Color COMBO_BOX_BG = new Color(50, 50, 50);
        private static final Color COMBO_BOX_SELECTION = new Color(70, 70, 70);
        private static final Color COMBO_BOX_BORDER = new Color(80, 80, 80);

        public CartManager(User user) {
            this.currentUser = user;
            // Initialize all UI components once
            this.cartTotalLabel = new JLabel("Selected Total: ₱0.00"); // Used in Cart view only
            this.subtotalLabel = new JLabel("Subtotal (0 items)"); // Used in Cart view summary
            this.subtotalValue = new JLabel("₱0.00"); // Used in Cart view summary
            this.totalValue = new JLabel("₱0.00"); // Used in Cart view summary (without shipping)
           
            this.checkoutSubtotalLabel = new JLabel("Subtotal (0 items)"); // <-- INITIALIZE NEW INSTANCE VARIABLE
            this.checkoutSubtotalValue = new JLabel("₱0.00");
            this.checkoutShippingValue = new JLabel("₱0.00");
            this.checkoutTotalValue = new JLabel("₱0.00");
            this.checkoutRewardDiscountLabel = new JLabel("");

            this.checkoutSubtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            this.checkoutSubtotalLabel.setForeground(SECONDARY_TEXT);
            this.checkoutSubtotalValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
            this.checkoutSubtotalValue.setForeground(TEXT_COLOR);
            this.checkoutShippingValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
            this.checkoutShippingValue.setForeground(TEXT_COLOR);
            this.checkoutRewardDiscountLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            this.checkoutRewardDiscountLabel.setForeground(new Color(0, 200, 0)); // Green
            this.checkoutTotalValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
            this.checkoutTotalValue.setForeground(ACCENT_COLOR);
            
            cartTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            cartTotalLabel.setForeground(TEXT_COLOR);
            subtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            subtotalLabel.setForeground(SECONDARY_TEXT);
            subtotalValue.setFont(new Font("Segoe UI", Font.BOLD, 13));
            subtotalValue.setForeground(TEXT_COLOR);
            totalValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
            totalValue.setForeground(ACCENT_COLOR);

            // Initialize rewardDiscountLabel for Cart view
            rewardDiscountLabel = new JLabel(""); // Initialize instance variable
            rewardDiscountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            rewardDiscountLabel.setForeground(new Color(0, 200, 0)); // Green color for discount
            rewardDiscountLabel.setVisible(false); // Initially hidden


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
                    total += itemTotal;
                }
            }
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

            // Items Panel - Set layout ONCE here
            JPanel itemsPanel = new JPanel();
            itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS)); // Set layout once
            itemsPanel.setBackground(BACKGROUND_COLOR); // Keep main items panel background dark
            itemsPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // Add left/right padding

            // Select All Panel setup - Create the panel and checkbox instances
            this.selectAllPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Changed to 0,0 for tighter layout
            this.selectAllPanel.setBackground(DARKER_BG); // Use darker background for this row
            this.selectAllPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            this.selectAllPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40)); // Fixed height but allow width to expand
            this.selectAllPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Ensure left alignment in BoxLayout

            this.selectAllCheckbox = new JCheckBox("SELECT ALL"); // Use the instance variable
            this.selectAllCheckbox.setFont(new Font("Segoe UI", Font.BOLD, 14));
            this.selectAllCheckbox.setForeground(TEXT_COLOR);
            this.selectAllCheckbox.setBackground(DARKER_BG); // Match panel background

            // Add the selectAll checkbox to its panel
            this.selectAllPanel.add(this.selectAllCheckbox); // Use the instance variable

            // Add the selectAllPanel to the itemsPanel unconditionally
            // Its visibility will be managed in updateCartDisplay
            itemsPanel.add(this.selectAllPanel); // Use the instance variable
            itemsPanel.add(Box.createVerticalStrut(5)); // Small space after select all


            // Listener for selectAll - This listener WILL rebuild the list
            this.selectAllCheckbox.addActionListener(e -> { // Use the instance variable
                boolean selected = this.selectAllCheckbox.isSelected();
                // Update selection state in data model
                cartItems.forEach(item -> item.setSelected(selected));
                // Rebuild the UI to reflect changes (indicator colors, empty state, etc.)
                // Pass the itemsPanel and the instance selectAllCheckbox
                updateCartDisplay(itemsPanel, this.selectAllCheckbox); // Pass necessary components
                updateSummary(); // Update totals
            });


            // Add items or empty message
            if (cartItems.isEmpty()) {
                JPanel emptyPanel = new JPanel(new BorderLayout());
                emptyPanel.setBackground(DARKER_BG); // Use darker background for the empty message area
                emptyPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
                emptyPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align in BoxLayout

                JLabel emptyLabel = new JLabel("Your cart is empty", SwingConstants.CENTER);
                emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                emptyLabel.setForeground(SECONDARY_TEXT);
                emptyPanel.add(emptyLabel, BorderLayout.CENTER);
                itemsPanel.add(emptyPanel);

                // Hide the selectAll panel when the cart is empty
                this.selectAllPanel.setVisible(false); // Use the instance variable and hide the panel

            } else {
                // Show the selectAll panel if it was hidden
                this.selectAllPanel.setVisible(true); // Use the instance variable

                 // Add individual cart items
                 for (CartItem item : cartItems) {
                     // Pass itemsPanel (parent) and the instance selectAllCheckbox
                     JPanel itemPanel = createCartItemPanel(item, itemsPanel, this.selectAllCheckbox); // Pass necessary components
                     itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Ensure left alignment in BoxLayout
                     itemsPanel.add(itemPanel);
                     itemsPanel.add(Box.createVerticalStrut(5));
                 }
            }

            // Add vertical glue at the end to push items to the top and fill space
            // This helps the background fill correctly even with few items (always added)
            itemsPanel.add(Box.createVerticalGlue());


            JScrollPane scrollPane = new JScrollPane(itemsPanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
            scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Faster scrolling
            scrollPane.getViewport().setBackground(BACKGROUND_COLOR); // Match scrollpane viewport background to main panel
            cartPanel.add(scrollPane, BorderLayout.CENTER);


            // Order Summary Panel (rest of this section remains the same)
            JPanel summaryPanel = new JPanel();
            summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
            summaryPanel.setBackground(DARKER_BG); // Use darker background for summary
            summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR), // Top border
                BorderFactory.createEmptyBorder(8, 15, 8, 15) // Internal padding
            ));

            // Subtotal Panel - Create new labels here that reference the instance variables
            JPanel subtotalPanel = new JPanel(new BorderLayout());
            subtotalPanel.setBackground(DARKER_BG);
            subtotalPanel.setBorder(null);

            JLabel currentSubtotalLabel = new JLabel(subtotalLabel.getText()); // Use instance label's text
            JLabel currentSubtotalValue = new JLabel(subtotalValue.getText()); // Use instance label's text

            currentSubtotalLabel.setFont(subtotalLabel.getFont()); // Copy font
            currentSubtotalLabel.setForeground(subtotalLabel.getForeground()); // Copy color
            currentSubtotalValue.setFont(subtotalValue.getFont()); // Copy font
            currentSubtotalValue.setForeground(subtotalValue.getForeground()); // Copy color

            subtotalPanel.add(currentSubtotalLabel, BorderLayout.WEST);
            subtotalPanel.add(currentSubtotalValue, BorderLayout.EAST);
            summaryPanel.add(subtotalPanel);

            // Voucher/Reward Discount Panel
            JPanel voucherPanel = new JPanel(new BorderLayout());
            voucherPanel.setBackground(DARKER_BG);
            voucherPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

            JLabel voucherLabel = new JLabel("Voucher:");
            voucherLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            voucherLabel.setForeground(SECONDARY_TEXT);

            JTextField voucherField = new JTextField();
            voucherField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            voucherField.setBackground(new Color(50, 50, 50)); // Input background
            voucherField.setForeground(TEXT_COLOR);
            voucherField.setCaretColor(TEXT_COLOR);

            JButton applyBtn = new JButton("APPLY");
            applyBtn.setBackground(SUCCESS_COLOR);
            applyBtn.setForeground(Color.WHITE);
            applyBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
            applyBtn.setBorderPainted(false);
            applyBtn.setFocusPainted(false);
            applyBtn.setPreferredSize(new Dimension(60, 22));

            JButton removeBtn = new JButton("REMOVE");
            removeBtn.setBackground(new Color(200, 50, 50)); // Red for remove
            removeBtn.setForeground(Color.WHITE);
            removeBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
            removeBtn.setBorderPainted(false);
            removeBtn.setFocusPainted(false);
            removeBtn.setPreferredSize(new Dimension(70, 22));
            removeBtn.setEnabled(false); // Disabled initially
            removeBtn.setVisible(true); // Always visible

            // Button panel for Apply/Remove buttons
            JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0)); // 1 row, 2 cols, 5px horizontal gap
            buttonPanel.setBackground(DARKER_BG);
            buttonPanel.add(applyBtn);
            buttonPanel.add(removeBtn);

            // Input panel for voucher field and buttons
            JPanel inputPanel = new JPanel(new BorderLayout(5, 0)); // 5px horizontal gap
            inputPanel.setBackground(DARKER_BG);
            inputPanel.add(voucherField, BorderLayout.CENTER); // Field takes center
            inputPanel.add(buttonPanel, BorderLayout.EAST); // Buttons on the right

            voucherPanel.add(voucherLabel, BorderLayout.WEST); // Label on the left
            voucherPanel.add(inputPanel, BorderLayout.CENTER); // Input area fills center
            summaryPanel.add(voucherPanel);

            // Add the reward discount label here, managed by updateSummary
            summaryPanel.add(this.rewardDiscountLabel); // Use the instance variable
            summaryPanel.add(Box.createVerticalStrut(10)); // Space before total

            // Updated apply button action listener
            applyBtn.addActionListener(e -> {
                String voucherCode = voucherField.getText().trim();
                if (!voucherCode.isEmpty()) {
                    try {
                        Reward redemption = getRewardRedemption(voucherCode, currentUser.getUserId());
                        if (redemption != null && !redemption.isRedeemed()) { // Check if NOT already used
                            // Mark voucher as used in DB (if applicable to your rewards logic)
                            // updateRewardRedemptionStatus(redemption.getRedemptionId(), true); // Assuming you have this method
                            rewardDiscount = redemption.getDiscountAmount();
                            // Update the labels directly
                            rewardDiscountLabel.setText("Reward Discount: -₱" + String.format("%.2f", rewardDiscount));
                            rewardDiscountLabel.setVisible(true);
                            // Update the label in checkout panel too
                            checkoutRewardDiscountLabel.setText("-₱" + String.format("%.2f", rewardDiscount));
                            checkoutRewardDiscountLabel.setVisible(rewardDiscount > 0);
                            removeBtn.setEnabled(true);
                            voucherField.setText(""); // Clear field on success
                            updateSummary(); // Recalculate and update totals

                            JOptionPane.showMessageDialog(checkoutPanel,
                                "Voucher applied successfully! (-₱" + String.format("%.2f", rewardDiscount) + ")",
                                "Voucher Applied",
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                             rewardDiscount = 0.0; // Ensure discount is reset
                             // Update the labels to hidden/zero
                             rewardDiscountLabel.setVisible(false);
                             checkoutRewardDiscountLabel.setVisible(false);
                             removeBtn.setEnabled(false);
                             updateSummary(); // Recalculate and update totals

                             JOptionPane.showMessageDialog(checkoutPanel,
                                 (redemption != null && redemption.isRedeemed()) ? "This voucher has already been used." : "Invalid or expired voucher code",
                                 "Invalid Code",
                                 JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(checkoutPanel,
                            "Error validating voucher: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(checkoutPanel,
                        "Please enter a voucher code",
                        "No Code Entered",
                        JOptionPane.WARNING_MESSAGE);
                }
            });


            // Remove button action listener
            removeBtn.addActionListener(e -> {
                rewardDiscount = 0.0; // Reset discount
                rewardDiscountLabel.setText(""); // Clear text
                rewardDiscountLabel.setVisible(false); // Hide label
                checkoutRewardDiscountLabel.setText(""); // Clear checkout label
                checkoutRewardDiscountLabel.setVisible(false); // Hide checkout label
                removeBtn.setEnabled(false); // Disable remove button
                updateSummary(); // Recalculate and update totals
                 // No need to update checkout totals here, updateSummary handles it
            });

            // Initialize remove button state based on initial rewardDiscount value
            removeBtn.setEnabled(rewardDiscount > 0);


            // Total Panel
            JPanel totalPanel = new JPanel(new BorderLayout());
            totalPanel.setBackground(DARKER_BG);
            totalPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR), // Top border
                BorderFactory.createEmptyBorder(10, 0, 0, 0) // Internal padding
            ));

            JLabel totalTextLabel = new JLabel("Total");
            totalTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            totalTextLabel.setForeground(TEXT_COLOR);

            // Create new label that mirrors the instance variable totalValue
            JLabel currentTotalValue = new JLabel(totalValue.getText());
            currentTotalValue.setFont(totalValue.getFont()); // Copy font
            currentTotalValue.setForeground(totalValue.getForeground()); // Copy color

            totalPanel.add(totalTextLabel, BorderLayout.WEST);
            totalPanel.add(currentTotalValue, BorderLayout.EAST);
            summaryPanel.add(totalPanel);

            // Add property change listeners to the instance labels to update the local labels
             subtotalLabel.addPropertyChangeListener("text", evt -> {
                 currentSubtotalLabel.setText(subtotalLabel.getText());
             });
             subtotalValue.addPropertyChangeListener("text", evt -> {
                 currentSubtotalValue.setText(subtotalValue.getText());
             });
             totalValue.addPropertyChangeListener("text", evt -> {
                 currentTotalValue.setText(totalValue.getText());
             });
             // No need for rewardDiscountLabel listener here, it's updated directly


            // Checkout Button
            JButton checkoutBtn = new JButton("CHECKOUT");
            checkoutBtn.setBackground(BUTTON_COLOR);
            checkoutBtn.setForeground(Color.WHITE);
            checkoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            checkoutBtn.setBorderPainted(false);
            checkoutBtn.setFocusPainted(false);
            checkoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            checkoutBtn.setPreferredSize(new Dimension(200, 40)); // Set preferred size

            // Hover effects
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
                        // Initialize checkout with current delivery method
                        updateCheckoutTotals(isDeliverySelected);
                        if (cardPanel != null && cardLayout != null) {
                            cardLayout.show(cardPanel, "checkout");
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

            // Container for the checkout button
            JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
            buttonContainer.setBackground(DARKER_BG);
            buttonContainer.add(checkoutBtn);
            summaryPanel.add(buttonContainer);

            // Add the summary panel to the main cart panel
            cartPanel.add(summaryPanel, BorderLayout.SOUTH);

            // Set initial state of selectAll checkbox using the instance variable
            boolean allSelectedOnInit = !cartItems.isEmpty() && cartItems.stream().allMatch(CartItem::isSelected);
            this.selectAllCheckbox.setSelected(allSelectedOnInit); // Use the instance variable


            return cartPanel;
        }
        
        private void updateSummary() {
        int selectedCount = getSelectedItemCount();
        double subtotal = getCartTotal(); // Get total of *selected* items
        // double total = subtotal - rewardDiscount; // Cart view total usually doesn't include shipping

        if (subtotalLabel != null) {
            subtotalLabel.setText("Subtotal (" + selectedCount + " items)");
        }
        if (subtotalValue != null) {
            subtotalValue.setText("₱" + String.format("%.2f", subtotal));
        }
        // FIX: Update the cart total label considering only subtotal and reward discount
        if (totalValue != null) { // Cart total label (usually excludes shipping)
            double cartTotal = subtotal - rewardDiscount;
            // Only show total if there are selected items, or if a discount was applied with 0 subtotal (though unlikely)
            double displayCartTotal = (selectedCount > 0 || rewardDiscount > 0) ? cartTotal : 0.00;
            if (displayCartTotal < 0) displayCartTotal = 0; // Ensure cart total also doesn't go below zero
            totalValue.setText("₱" + String.format("%.2f", displayCartTotal));
        }
        // FIX: Update the cartTotalLabel at the top of the cart item list
        if (cartTotalLabel != null) {
            double currentCartSelectedTotal = subtotal - rewardDiscount; // This was previously just subtotal, now includes reward
            if (currentCartSelectedTotal < 0) currentCartSelectedTotal = 0;
            cartTotalLabel.setText("Selected Total: ₱" + String.format("%.2f", currentCartSelectedTotal));
        }

        // FIX: Update the reward discount label visibility based on the rewardDiscount value
        if (rewardDiscountLabel != null) {
            rewardDiscountLabel.setVisible(rewardDiscount > 0);
        }


        if (cardPanel != null) {
            cardPanel.revalidate();
            cardPanel.repaint();
        }
    }
        
    private JPanel createCartItemPanel(CartItem cartItem, JPanel parentPanel, JCheckBox selectAllCheckbox) { // Keep parameter for clarity, but use instance variable internally
        // Main panel with BorderLayout
        JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
        itemPanel.setBackground(DARKER_BG);
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        Dimension cardSize = new Dimension(500, 120);
        itemPanel.setPreferredSize(cardSize);
        itemPanel.setMinimumSize(cardSize);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, cardSize.height));

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(DARKER_BG);

        // Indicator panel (left border)
        JPanel indicatorPanel = new JPanel();
        indicatorPanel.setPreferredSize(new Dimension(6, 0));
        // Set background based on initial selection state
        indicatorPanel.setBackground(cartItem.isSelected() ? ACCENT_COLOR : new Color(0,0,0,0));
        contentWrapper.add(indicatorPanel, BorderLayout.WEST);

        // Content panel (right of indicator)
        JPanel contentPanel = new JPanel(new BorderLayout(10, 0));
        contentPanel.setBackground(DARKER_BG);

        JPanel leftPanel = new JPanel(new BorderLayout(15, 0));
        leftPanel.setBackground(DARKER_BG);

        JCheckBox selectBox = new JCheckBox();
        selectBox.setSelected(cartItem.isSelected());
        selectBox.setBackground(DARKER_BG);
        selectBox.addActionListener(e -> {
            boolean newState = selectBox.isSelected();
            System.out.println("Toggling selection for " + cartItem.getItem().getName() +
                              " to " + newState);
            cartItem.setSelected(newState); // Update the data model

            // Update indicator color directly on this panel
            indicatorPanel.setBackground(newState ? ACCENT_COLOR : new Color(0,0,0,0));

            boolean allItemsNowSelected = cartItems.stream().allMatch(CartItem::isSelected);
            if (this.selectAllCheckbox != null) { // Use instance variable
                this.selectAllCheckbox.setSelected(allItemsNowSelected);
            }

            updateSummary();

            parentPanel.revalidate(); // Revalidate parent to ensure layout is correct
            parentPanel.repaint();    // Repaint parent
        });

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(DARKER_BG);

        JLabel nameLabel = new JLabel(cartItem.getItem().getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Use item description if available, otherwise default size text
        String description = cartItem.getItem().getDescription(); // Use cartItem's MenuItem
        JLabel descLabel;
        if (description != null && !description.trim().isEmpty()) {
             descLabel = new JLabel("<html><body style='width: 150px'>" + description + "</body></html>");
             descLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
             descLabel.setForeground(new Color(200, 200, 200));
        } else {
             descLabel = new JLabel("Size: Regular"); // Default text if no description
             descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
             descLabel.setForeground(SECONDARY_TEXT);
        }
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);


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
                cartItem.setQuantity(newQuantity); // Update local list immediately for visual feedback
                quantityLabel.setText(String.valueOf(newQuantity)); // Update quantity label
                updateCartItemQuantity(cartItem.getItem().getProductId(), newQuantity); // Update DB
                updateSummary(); // Update totals
                 parentPanel.revalidate(); // Revalidate and repaint parent
                 parentPanel.repaint();
            } else {
                // --- MODIFIED REMOVAL LOGIC ---
                // If quantity is 1 and minus is clicked, remove the item completely
                // Call the database removal method and then force a full UI refresh
                removeCartItem(cartItem.getItem().getProductId()); // Remove from DB
                forceCartRefresh(); // Reload data and rebuild UI

            }
        });

        plusBtn.addActionListener(e -> {
            int newQuantity = cartItem.getQuantity() + 1;
            cartItem.setQuantity(newQuantity); // Update local list
            quantityLabel.setText(String.valueOf(newQuantity)); // Update label
            updateCartItemQuantity(cartItem.getItem().getProductId(), newQuantity); // Update DB
            updateSummary(); // Update totals
             parentPanel.revalidate(); // Revalidate and repaint parent
             parentPanel.repaint();
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
            // --- MODIFIED REMOVAL LOGIC ---
            // Call the database removal method and then force a full UI refresh
            removeCartItem(cartItem.getItem().getProductId()); // Remove from DB
            forceCartRefresh(); // Reload data and rebuild UI

            // No need to manipulate local cartItems list here or call updateCartDisplay,
            // forceCartRefresh handles the full sync.
            // --- END MODIFIED REMOVAL LOGIC ---
        });

        // Panel to hold quantity and remove button vertically
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(DARKER_BG);
        buttonPanel.add(quantityPanel, BorderLayout.CENTER);
        buttonPanel.add(deleteBtn, BorderLayout.SOUTH);

        rightPanel.add(priceLabel, BorderLayout.NORTH);
        rightPanel.add(buttonPanel, BorderLayout.CENTER);

        contentPanel.add(leftPanel, BorderLayout.CENTER);
        contentPanel.add(rightPanel, BorderLayout.EAST);

        // Add content panel to wrapper
        contentWrapper.add(contentPanel, BorderLayout.CENTER);

        // Add wrapper to main panel
        itemPanel.add(contentWrapper, BorderLayout.CENTER);

        return itemPanel;
    }

    private void updateCartDisplay(JPanel itemsPanel, JCheckBox selectAllCheckbox) {
        itemsPanel.removeAll();
        // Layout setting is now in createCartPanel
        itemsPanel.setBackground(BACKGROUND_COLOR); // Main panel background

        // Re-add the selectAllPanel first (its visibility is handled below)
        // Use the instance variable selectAllPanel
        if (this.selectAllPanel != null) { // Add null check just in case, though it's initialized in createCartPanel
             itemsPanel.add(this.selectAllPanel);
             itemsPanel.add(Box.createVerticalStrut(5)); // Space after select all panel
        }

        if (cartItems.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBackground(DARKER_BG);
            emptyPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
            emptyPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align in BoxLayout

            JLabel emptyLabel = new JLabel("Your cart is empty", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            emptyLabel.setForeground(SECONDARY_TEXT);
            emptyPanel.add(emptyLabel, BorderLayout.CENTER);
            itemsPanel.add(emptyPanel);

            // Update selectAll checkbox and panel visibility
            if (this.selectAllCheckbox != null) { // Use instance variable
                 this.selectAllCheckbox.setSelected(false); // Cart is empty, so none are selected
            }
            if (this.selectAllPanel != null) { // Use instance variable
                 this.selectAllPanel.setVisible(false); // Hide the panel when empty
            }

        } else {
             // Update selectAll checkbox state based on current items
             if (this.selectAllCheckbox != null) { // Use instance variable
                 boolean allSelected = cartItems.stream().allMatch(CartItem::isSelected);
                 this.selectAllCheckbox.setSelected(allSelected);
             }
             // Show the selectAll panel if it was hidden
             if (this.selectAllPanel != null) { // Use instance variable
                 this.selectAllPanel.setVisible(true);
             }


             // Add individual cart items
             for (CartItem item : cartItems) {
                 JPanel itemPanel = createCartItemPanel(item, itemsPanel, this.selectAllCheckbox); // Pass necessary components
                 itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Ensure left alignment in BoxLayout
                 itemsPanel.add(itemPanel);
                 itemsPanel.add(Box.createVerticalStrut(5));
             }
        }

        // Add vertical glue at the end (always)
        itemsPanel.add(Box.createVerticalGlue());

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }
 
     public void forceCartRefresh() {
        System.out.println("[DEBUG] Force refreshing cart display...");

        // 1. Reload cart items from database
        loadCartItems(); // This updates the 'cartItems' list

        updateSummary(); // This also updates the cart labels
        updateCheckoutTotals(isDeliverySelected); // Update checkout labels based on current selection


        if (cardPanel != null && cardLayout != null) {

            JPanel newCartPanel = createCartPanel(cardPanel, cardLayout);

            cardPanel.add(newCartPanel, "cart"); // Add the new panel with the existing name
            System.out.println("[DEBUG] Added new cart panel, replacing old one.");


            // 5. Force the container to revalidate and repaint its layout
            cardPanel.revalidate();
            cardPanel.repaint();
             System.out.println("[DEBUG] Card panel revalidated and repainted.");

             SwingUtilities.invokeLater(() -> {
                 cardLayout.show(cardPanel, "cart");
                  System.out.println("[DEBUG] Explicitly showing new cart panel.");
             });


            System.out.println("[DEBUG] Cart display refresh attempted.");

        } else {
             System.err.println("[DEBUG] cardPanel or cardLayout is null, cannot refresh cart display.");
        }
    }
     
    public JPanel createCheckoutPanel(JPanel mainCardPanel, CardLayout cardLayout) {
        this.cardPanel = mainCardPanel;
        this.cardLayout = cardLayout;

        JPanel checkoutPanel = new JPanel(new BorderLayout());
        checkoutPanel.setBackground(BACKGROUND_COLOR);
        checkoutPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title and back button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton backBtn = new JButton("← Back to Cart");
            backBtn.addActionListener(e -> {
                // When going back to cart, ensure summary shows cart totals (no shipping)
                // updateCheckoutTotals(false); // updateSummary is called when cart is shown, which updates cart labels
                cardLayout.show(mainCardPanel, "cart");
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

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Delivery Method Panel
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
        JRadioButton deliveryBtn = createStyledRadioButton("Delivery (₱60.00)", isDeliverySelected);
        JRadioButton pickupBtn = createStyledRadioButton("Pickup (Free)", !isDeliverySelected);

        // Add listeners to update totals when delivery method changes
        deliveryBtn.addActionListener(e -> {
            isDeliverySelected = true;
            updateCheckoutTotals(true); // Pass true for delivery
        });
        pickupBtn.addActionListener(e -> {
            isDeliverySelected = false;
            updateCheckoutTotals(false); // Pass false for pickup
        });

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

        // Shipping Address Panel
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

        // Address ComboBox
        JComboBox<String> addressCombo = new JComboBox<>();
        addressCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressCombo.setBackground(COMBO_BOX_BG);
        addressCombo.setForeground(TEXT_COLOR);
        addressCombo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        // Custom renderer
        addressCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? COMBO_BOX_SELECTION : COMBO_BOX_BG);
                setForeground(TEXT_COLOR);
                return this;
            }
        });

        // Editor component styling
        JTextField editor = (JTextField) addressCombo.getEditor().getEditorComponent();
        editor.setBackground(COMBO_BOX_BG);
        editor.setForeground(TEXT_COLOR);
        editor.setCaretColor(TEXT_COLOR);
        editor.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        // ComboBox UI styling
        addressCombo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton();
                button.setIcon(new ImageIcon(createColoredArrowIcon()));
                button.setBackground(COMBO_BOX_BG);
                button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                return button;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(COMBO_BOX_BG);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });

        // Add addresses to combo box
        List<String> addresses = getUserAddresses(currentUser.getUserId());
        for (String addr : addresses) {
            addressCombo.addItem(addr);
        }

        if (currentUser.getAddress() != null && !currentUser.getAddress().isEmpty()) {
            addressCombo.setSelectedItem(currentUser.getAddress());
        }

        addressPanel.add(addressCombo);
        addressPanel.add(Box.createVerticalStrut(10));

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

        // Order Summary Panel
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
        // ADD THIS LINE to center the title
        summaryTitle.setAlignmentX(Component.CENTER_ALIGNMENT); 
        summaryPanel.add(summaryTitle);
        summaryPanel.add(Box.createVerticalStrut(15));

        // Items list
        boolean firstItem = true;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                if (!firstItem) {
                    summaryPanel.add(Box.createVerticalStrut(10));
                }

                JPanel itemPanel = new JPanel(new BorderLayout());
                itemPanel.setBackground(DARKER_BG);

                JLabel itemName = new JLabel(item.getItem().getName() + " × " + item.getQuantity());
                itemName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                itemName.setForeground(TEXT_COLOR);

                JLabel itemPrice = new JLabel("₱" + String.format("%.2f", item.getItem().getPrice() * item.getQuantity()));
                itemPrice.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                itemPrice.setForeground(TEXT_COLOR);

                itemPanel.add(itemName, BorderLayout.WEST);
                itemPanel.add(itemPrice, BorderLayout.EAST);
                summaryPanel.add(itemPanel);

                firstItem = false;
            }
        }

        summaryPanel.add(Box.createVerticalStrut(15));

        summaryPanel.add(Box.createVerticalStrut(15)); // Space before totals

        // Subtotal Panel
        JPanel subtotalPanel = new JPanel(new BorderLayout()); // Use BorderLayout for label and value
        subtotalPanel.setBackground(DARKER_BG);
        subtotalPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0)); // Add padding if needed

        // Use the instance variables for labels
        this.checkoutSubtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Ensure consistent font/color
        this.checkoutSubtotalLabel.setForeground(SECONDARY_TEXT);
        this.checkoutSubtotalValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        this.checkoutSubtotalValue.setForeground(TEXT_COLOR);


        // ADD the instance labels to the new subtotalPanel
        subtotalPanel.add(this.checkoutSubtotalLabel, BorderLayout.WEST); 
        subtotalPanel.add(this.checkoutSubtotalValue, BorderLayout.EAST); 

        // ADD the subtotalPanel to the summaryPanel
        summaryPanel.add(subtotalPanel); 

        JPanel rewardPanel = new JPanel(new BorderLayout());
        rewardPanel.setBackground(DARKER_BG);
        rewardPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel rewardLabel = new JLabel("Reward Discount"); // Local label for text
        rewardLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rewardLabel.setForeground(SECONDARY_TEXT);

        rewardPanel.add(rewardLabel, BorderLayout.WEST); // Use local label for text
        rewardPanel.add(this.checkoutRewardDiscountLabel, BorderLayout.EAST); // Use instance variable for value
        summaryPanel.add(rewardPanel);


        // Shipping Panel
        JPanel shippingPanel = new JPanel(new BorderLayout());
        shippingPanel.setBackground(DARKER_BG);
        shippingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0)); // Add padding before Total

        JLabel shippingLabel = new JLabel("Shipping Fee"); // Local label for text
        shippingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        shippingLabel.setForeground(SECONDARY_TEXT);

        shippingPanel.add(shippingLabel, BorderLayout.WEST); // Use local label for text
        shippingPanel.add(this.checkoutShippingValue, BorderLayout.EAST); // Use instance variable for value
        summaryPanel.add(shippingPanel);

        // Total Panel
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(DARKER_BG);
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR), // Top border
            BorderFactory.createEmptyBorder(10, 0, 0, 0) // Internal padding
        ));

        JLabel totalLabel = new JLabel("Total"); // Local label for text
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(TEXT_COLOR);

        totalPanel.add(totalLabel, BorderLayout.WEST); // Use local label for text
        totalPanel.add(this.checkoutTotalValue, BorderLayout.EAST); // Use instance variable for value
        summaryPanel.add(totalPanel);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        contentPanel.add(summaryPanel, gbc);

        // Payment Method Panel (No changes needed here for this specific issue)
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setBackground(DARKER_BG);
        paymentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel paymentTitle = new JLabel("Payment Method", SwingConstants.CENTER);
        paymentTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        paymentTitle.setForeground(TEXT_COLOR);
        paymentTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
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

        // Card Details Panel (unchanged for this issue)
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

        // E-Wallet Details Panel (unchanged for this issue)
        JPanel walletDetailsPanel = new JPanel();
        walletDetailsPanel.setLayout(new BoxLayout(walletDetailsPanel, BoxLayout.Y_AXIS));
        walletDetailsPanel.setBackground(DARKER_BG);
        walletDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        walletDetailsPanel.setVisible(false);
        walletDetailsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel walletDetailsTitle = new JLabel("E-Wallet Details", SwingConstants.CENTER);
        walletDetailsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        walletDetailsTitle.setForeground(TEXT_COLOR);
        walletDetailsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        walletDetailsPanel.add(walletDetailsTitle);
        walletDetailsPanel.add(Box.createVerticalStrut(15));

        JPanel walletContentPanel = new JPanel();
        walletContentPanel.setLayout(new BoxLayout(walletContentPanel, BoxLayout.Y_AXIS));
        walletContentPanel.setBackground(DARKER_BG);
        walletContentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        walletContentPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));

        JLabel walletProviderLabel = new JLabel("E-Wallet Provider", SwingConstants.CENTER);
        walletProviderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        walletProviderLabel.setForeground(TEXT_COLOR);
        walletProviderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        walletContentPanel.add(walletProviderLabel);
        walletContentPanel.add(Box.createVerticalStrut(5));

        String[] walletProviders = {"GCash", "PayMaya", "GrabPay", "Coins.ph"};
        JComboBox<String> walletProviderDropdown = new JComboBox<>(walletProviders);
        walletProviderDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        walletProviderDropdown.setBackground(new Color(50, 50, 50));
        walletProviderDropdown.setForeground(TEXT_COLOR);
        walletProviderDropdown.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        walletProviderDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);
        walletProviderDropdown.setMaximumSize(new Dimension(200, 30));
        walletContentPanel.add(walletProviderDropdown);
        walletContentPanel.add(Box.createVerticalStrut(10));

        JLabel mobileNumberLabel = new JLabel("Mobile Number", SwingConstants.CENTER);
        mobileNumberLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mobileNumberLabel.setForeground(TEXT_COLOR);
        mobileNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        walletContentPanel.add(mobileNumberLabel);
        walletContentPanel.add(Box.createVerticalStrut(5));

        JTextField mobileNumberField = new JTextField();
        mobileNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mobileNumberField.setBackground(new Color(50, 50, 50));
        mobileNumberField.setForeground(TEXT_COLOR);
        mobileNumberField.setCaretColor(TEXT_COLOR);
        mobileNumberField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        mobileNumberField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mobileNumberField.setMaximumSize(new Dimension(200, 30));
        walletContentPanel.add(mobileNumberField);

        walletDetailsPanel.add(walletContentPanel);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        contentPanel.add(walletDetailsPanel, gbc);

        // Payment method radio button listeners to toggle details panels
        cardBtn.addActionListener(e -> {
            cardDetailsPanel.setVisible(true);
            walletDetailsPanel.setVisible(false);
            contentPanel.revalidate();
            contentPanel.repaint();
            SwingUtilities.invokeLater(() -> scrollToComponent(cardDetailsPanel));
        });

        walletBtn.addActionListener(e -> {
            cardDetailsPanel.setVisible(false);
            walletDetailsPanel.setVisible(true);
            contentPanel.revalidate();
            contentPanel.repaint();
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
                new SwingWorker<Integer, Void>() { // Return type is Integer for orderId
                    // --- ADD FIELDS HERE ---
                    private Integer orderIdResult; // Field to hold the order ID
                    private boolean isSuccess = false; // Field to indicate success/failure
                    // --- END ADD FIELDS ---

                    @Override
                    protected Integer doInBackground() throws Exception {
                        try {
                            // Process order and return the orderId
                            orderIdResult = processOrder(currentUser); // Assign to field
                            isSuccess = true; // Assign to field
                            return orderIdResult; // Return the orderId
                        } catch (SQLException ex) {
                            // Rethrow SQLException to be caught in done()
                            throw ex;
                        } catch (Exception ex) {
                             // Wrap other exceptions in a generic Exception or runtime
                             throw new RuntimeException("General Error during order processing", ex);
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                             // No need to declare orderId or success here; use fields
                             get(); // Call get() to check for exceptions from doInBackground

                        } catch (Exception ex) {
                            // Catch exceptions from get() (InterruptedException, ExecutionException)
                            // ExecutionException wraps the exception from doInBackground
                            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                            System.err.println("Error during order processing: " + cause.getMessage());
                            cause.printStackTrace();

                             SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(checkoutPanel,
                                    "There was an error processing your order. Please try again.\nError: " + cause.getMessage(),
                                    "Order Error",
                                    JOptionPane.ERROR_MESSAGE);
                            });

                        } finally {

                             SwingUtilities.invokeLater(() -> {
                                 placeOrderBtn.setEnabled(true);
                                 // This will rebuild the cart view.
                                 forceCartRefresh();
                             });

                            if (isSuccess && orderIdResult != null) {
                                SwingUtilities.invokeLater(() -> {
                                    // Update the orderIdLabel using the instance variable
                                    if (CartManager.this.orderIdLabel != null) {
                                         CartManager.this.orderIdLabel.setText("Order ID: ORD" + orderIdResult); // Use the actual orderIdResult field
                                    }
                                    cardLayout.show(mainCardPanel, "orderConfirmation"); // Switch view
                                });
                             }
                        }
                    }
                }.execute(); // Execute the SwingWorker
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
        
        updateCheckoutTotals(isDeliverySelected);


        return checkoutPanel;
    }
    
    private Image createColoredArrowIcon() {
        BufferedImage image = new BufferedImage(10, 5, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(TEXT_COLOR);
        g2.fillPolygon(new int[] {0, 5, 10}, new int[] {0, 5, 0}, 3);
        g2.dispose();
        return image;
    }
    
    private List<String> getUserAddresses(int userId) {
        List<String> addresses = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            // 1. Get primary address from users table
            String primaryQuery = "SELECT address FROM users WHERE user_id = ?";
            try (PreparedStatement primaryPS = conn.prepareStatement(primaryQuery)) {
                primaryPS.setInt(1, userId);
                ResultSet primaryRS = primaryPS.executeQuery();
                if (primaryRS.next()) {
                    String primaryAddress = primaryRS.getString("address");
                    if (primaryAddress != null && !primaryAddress.trim().isEmpty()) {
                        addresses.add(primaryAddress);
                    }
                }
            }

            String additionalQuery = "SELECT address FROM user_addresses WHERE user_id = ? AND address NOT IN " +
                                   "(SELECT address FROM users WHERE user_id = ?) " +
                                   "ORDER BY is_default DESC, address_id";
            try (PreparedStatement additionalPS = conn.prepareStatement(additionalQuery)) {
                additionalPS.setInt(1, userId);
                additionalPS.setInt(2, userId);
                ResultSet additionalRS = additionalPS.executeQuery();
                while (additionalRS.next()) {
                    addresses.add(additionalRS.getString("address"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error loading addresses: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }

        return addresses;
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
        if (address == null || address.trim().isEmpty()) {
            JOptionPane.showMessageDialog(checkoutPanel,
                "Address cannot be empty",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Check if address already exists for this user
            String checkQuery = "SELECT COUNT(*) FROM user_addresses WHERE user_id = ? AND address = ?";
            try (PreparedStatement checkPS = conn.prepareStatement(checkQuery)) {
                checkPS.setInt(1, currentUser.getUserId());
                checkPS.setString(2, address);
                ResultSet rs = checkPS.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(checkoutPanel,
                        "This address is already saved",
                        "Duplicate Address",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }

            // Insert new address
            String insert = "INSERT INTO user_addresses (user_id, address, is_default) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setInt(1, currentUser.getUserId());
                ps.setString(2, address);
                // Set as non-default (0) by default - primary address remains default
                ps.setInt(3, 0);
                ps.executeUpdate();
            }

            // Refresh the address combo box
            SwingUtilities.invokeLater(() -> {
                Component[] components = checkoutPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) comp;
                        Component view = scrollPane.getViewport().getView();
                        if (view instanceof JPanel) {
                            JPanel contentPanel = (JPanel) view;
                            for (Component innerComp : contentPanel.getComponents()) {
                                if (innerComp instanceof JComboBox) {
                                    @SuppressWarnings("unchecked")
                                    JComboBox<String> combo = (JComboBox<String>) innerComp;
                                    combo.addItem(address);
                                    combo.setSelectedItem(address);
                                    break;
                                }
                            }
                        }
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(checkoutPanel,
                "Error saving address: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

     private void updateCheckoutTotals(boolean isDelivery) {
        double subtotal = getCartTotal();
        double shipping = isDelivery ? 60.00 : 0.00;
        double checkoutTotal = subtotal + shipping - rewardDiscount;
        int selectedCount = getSelectedItemCount();

        SwingUtilities.invokeLater(() -> {
            // Update CART summary labels (using instance variables for cart view)
            // Ensure these are only updated when the cart panel is visible if performance is a concern
            // For now, updating them always is fine.
            if (subtotalLabel != null) { // Instance variable for Cart subtotal TEXT
                subtotalLabel.setText("Subtotal (" + selectedCount + " items)");
            }
            if (subtotalValue != null) { // Instance variable for Cart subtotal VALUE
                subtotalValue.setText("₱" + String.format("%.2f", subtotal));
            }
             if (rewardDiscountLabel != null) { // Instance variable for Cart reward TEXT+VALUE
                 rewardDiscountLabel.setText(rewardDiscount > 0 ?
                     "Reward Discount: -₱" + String.format("%.2f", rewardDiscount) : "");
                 rewardDiscountLabel.setVisible(rewardDiscount > 0);
             }
            if (totalValue != null) { // Instance variable for Cart total VALUE (no shipping)
                double cartTotal = subtotal - rewardDiscount; // Calculate cart total here
                totalValue.setText("₱" + String.format("%.2f", cartTotal));
            }

            if (this.checkoutSubtotalLabel != null) { // Instance variable for Checkout subtotal TEXT
                 this.checkoutSubtotalLabel.setText("Subtotal (" + selectedCount + " items)");
            }
            if (this.checkoutSubtotalValue != null) { // Instance variable for Checkout subtotal VALUE
                this.checkoutSubtotalValue.setText("₱" + String.format("%.2f", subtotal));
            }
            if (this.checkoutRewardDiscountLabel != null) { // Instance variable for Checkout reward TEXT+VALUE
                 this.checkoutRewardDiscountLabel.setText(rewardDiscount > 0 ?
                     "-₱" + String.format("%.2f", rewardDiscount) : "");
                 this.checkoutRewardDiscountLabel.setVisible(rewardDiscount > 0);
            }
            if (this.checkoutShippingValue != null) { // Instance variable for Checkout shipping VALUE
                this.checkoutShippingValue.setText("₱" + String.format("%.2f", shipping)); // Use calculated shipping
            }
            if (this.checkoutTotalValue != null) { // Instance variable for Checkout total VALUE
                this.checkoutTotalValue.setText("₱" + String.format("%.2f", checkoutTotal)); // Use calculated checkoutTotal
            }
        });
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
        // Create the instance panel here
        this.orderConfirmationPanel = new JPanel(new BorderLayout()); // Use the instance variable
        this.orderConfirmationPanel.setBackground(BACKGROUND_COLOR);
        this.orderConfirmationPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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

        // Initialize the instance JLabel here
        this.orderIdLabel = new JLabel("Order ID: Generating..."); // Use the instance variable
        this.orderIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        this.orderIdLabel.setForeground(TEXT_COLOR);
        this.orderIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

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
            cardLayout.show(cardPanel, "cart");
            // clearSelectedItems(); // This is already called by forceCartRefresh after order success/failure

             // Reset the orderIdLabel text when leaving the confirmation screen
             if (this.orderIdLabel != null) {
                 this.orderIdLabel.setText("Order ID: Generating...");
             }
        });

        centerPanel.add(iconLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(subtitleLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(this.orderIdLabel); // Add the instance variable JLabel
        centerPanel.add(Box.createVerticalStrut(50));
        centerPanel.add(continueBtn);

        this.orderConfirmationPanel.add(centerPanel, BorderLayout.CENTER); // Add to instance panel

        return this.orderConfirmationPanel; // Return the instance panel
    }
        
    private String generateOrderId() {
        return String.format("%d%03d", System.currentTimeMillis() % 100000, (int)(Math.random() * 1000));
    }
    
    private void clearSelectedItems() {
        List<CartItem> itemsToRemove = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                isDeliverySelected = true;
                itemsToRemove.add(item);
                removeCartItem(item.getItem().getProductId());
            }
        }
        cartItems.removeAll(itemsToRemove);
        
        
        rewardDiscount = 0.0;
            if (rewardDiscountLabel != null) {
                rewardDiscountLabel.setVisible(false);
            }
            if (checkoutRewardDiscountLabel != null) {
                checkoutRewardDiscountLabel.setVisible(false);
            }
            
            updateSummary();
            updateCheckoutTotals(true);
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
    
    private Integer processOrder(User user) throws SQLException { // Return the generated order ID
        if (getSelectedItemCount() == 0) {
            throw new IllegalStateException("No items selected for order");
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Calculate order totals
            double subtotal = getCartTotal();
            double shipping = isDeliverySelected ? 60.00 : 0.00; // Use isDeliverySelected here
            double total = subtotal + shipping - rewardDiscount;

            // 1. Create the order record (updated to include reward points)
            // Add `order_id` column if it's auto-increment, or generate it here
            String orderQuery = "INSERT INTO orders (user_id, order_date, status, " +
                              "delivery_method, delivery_address, payment_method, " +
                              "subtotal, shipping_fee, reward_discount, total_amount, " +
                              "points_earned) VALUES (?, NOW(), 'PENDING', ?, ?, ?, " +
                              "?, ?, ?, ?, ?)";

            PreparedStatement orderPS = conn.prepareStatement(orderQuery,
                PreparedStatement.RETURN_GENERATED_KEYS);

            orderPS.setInt(1, user.getUserId());
            // Use isDeliverySelected to determine delivery method string
            orderPS.setString(2, isDeliverySelected ? "DELIVERY" : "PICKUP");
            orderPS.setString(3, user.getAddress()); // Assuming address is always the primary user address for now
            // Determine payment method (assuming default COD or add logic based on radio buttons)
            orderPS.setString(4, "COD"); // <-- Needs logic to get selected payment method
            orderPS.setDouble(5, subtotal);
            orderPS.setDouble(6, shipping); // Use calculated shipping
            orderPS.setDouble(7, rewardDiscount);
            orderPS.setDouble(8, total); // Use calculated total

            // Calculate and set points earned (50 points per ₱500 spent)
            int pointsEarned = calculateEarnedPoints(subtotal); // Calculate points based on subtotal before shipping/discount
            orderPS.setInt(9, pointsEarned);

            orderPS.executeUpdate();

            // Get the generated order ID
            int orderId = -1; // Initialize orderId
            try (ResultSet generatedKeys = orderPS.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                    System.out.println("[DEBUG] Generated Order ID: " + orderId);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            // 2. Add order items
            String itemQuery = "INSERT INTO order_items (order_id, product_id, quantity, price_at_order) " +
                             "VALUES (?, ?, ?, ?)";
            PreparedStatement itemPS = conn.prepareStatement(itemQuery);

            for (CartItem item : cartItems) {
                if (item.isSelected()) { // Only add selected items
                    itemPS.setInt(1, orderId);
                    itemPS.setInt(2, item.getItem().getProductId());
                    itemPS.setInt(3, item.getQuantity());
                    itemPS.setDouble(4, item.getItem().getPrice()); // Price at the time of order
                    itemPS.addBatch();
                }
            }

            itemPS.executeBatch();

            // 3. Remove ordered items from cart
            String deleteQuery = "DELETE FROM cart_items WHERE cart_id = ? AND product_id = ?";
            PreparedStatement deletePS = conn.prepareStatement(deleteQuery);

            for (CartItem item : cartItems) {
                if (item.isSelected()) { // Only remove selected items
                    deletePS.setInt(1, currentCartId);
                    deletePS.setInt(2, item.getItem().getProductId());
                    deletePS.addBatch();
                }
            }

            deletePS.executeBatch();

            // Commit transaction
            conn.commit();
            System.out.println("[DEBUG] Database transaction committed successfully.");

            return orderId; // Return the generated order ID

        } catch (SQLException e) {
            // Rollback transaction on error
            try (Connection conn = DBConnection.getConnection()) {
                if (conn != null && !conn.getAutoCommit()) { // Check if connection is valid and not already committed
                    conn.rollback();
                     System.err.println("[ERROR] Database transaction rolled back due to error.");
                }
            } catch (SQLException rollbackEx) {
                System.err.println("[ERROR] Error during rollback: " + rollbackEx.getMessage());
                 rollbackEx.printStackTrace();
            }
            throw e; // Re-throw SQLException to be caught by the SwingWorker
        }
    }
    
    private int getSelectedItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                count += item.getQuantity(); // Add the quantity of each selected item
            }
        }
        System.out.println("[DEBUG] Selected item quantity: " + count);
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
    
    private int calculateEarnedPoints(double totalAmount) {
        return (int)(totalAmount / 500) * POINTS_PER_500_PESOS;
    }

    private void updateUserRewardPoints(int userId, int pointsChange) {
        try (Connection conn = DBConnection.getConnection()) {
            // Check if user has a rewards record
            String checkQuery = "SELECT user_id FROM user_rewards WHERE user_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, userId);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    // Create record if doesn't exist
                    String insertQuery = "INSERT INTO user_rewards (user_id, points_balance) VALUES (?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setInt(2, Math.max(pointsChange, 0)); // Start with 0 or positive points
                        insertStmt.executeUpdate();
                    }
                    return;
                }
            }

            // Update existing record
            String updateQuery = "UPDATE user_rewards SET points_balance = points_balance + ? WHERE user_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, pointsChange);
                updateStmt.setInt(2, userId);
                updateStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error updating reward points: " + e.getMessage());
        }
    }

    private int getUserRewardPoints(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT points_balance FROM user_rewards WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                return rs.next() ? rs.getInt("points_balance") : 0;
            }
        } catch (SQLException e) {
            System.err.println("Error getting reward points: " + e.getMessage());
            return 0;
        }
    }
    
    private Reward getRewardRedemption(String code, int userId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            // FIX: Select redemption_id, redeemed status, and expiry date
            String query = "SELECT rr.redemption_id, r.name, r.discount_amount, rr.redeemed, rr.expires_at " +
                          "FROM reward_redemptions rr " +
                          "JOIN rewards r ON rr.reward_id = r.reward_id " +
                          // FIX: Use BINARY for case-sensitive comparison; check for unredeemed and non-expired
                          "WHERE rr.user_id = ? AND BINARY rr.code = ? AND rr.redeemed = 0 AND rr.expires_at > CURRENT_TIMESTAMP";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setString(2, code);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    Reward reward = new Reward(
                        rs.getString("name"),
                        rs.getDouble("discount_amount")
                    );
                    // FIX: Set the redeemed status loaded from the database
                    reward.setRedeemed(rs.getBoolean("redeemed"));
                    // FIX: Set the expiration date loaded from the database
                    reward.setExpiresAt(rs.getTimestamp("expires_at"));
                    // FIX: Set the redemption ID
                    reward.setRedemptionId(rs.getInt("redemption_id")); // Set the ID

                    System.out.println("[DEBUG] Found valid redemption: ID=" + reward.getRedemptionId() + ", Name=" + reward.getName() + ", Discount=" + reward.getDiscountAmount() + ", Redeemed=" + reward.isRedeemed() + ", Expired=" + reward.isExpired());
                    return reward; // Return the fully populated Reward object
                } else {
                    System.out.println("[DEBUG] No valid, unredeemed, non-expired redemption found for code: " + code + " and user: " + userId);
                }
            }
        }
        return null; // Return null if no matching redemption is found
    }
    }