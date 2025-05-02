    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */

 package coffeeshop;
    import javax.swing.*;
    import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.image.BufferedImage;
    import java.io.File;
    import java.io.IOException;
    import java.net.URL;
    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.util.List;
    import java.util.ArrayList;
import java.util.Arrays;
    import java.util.Collections;
    import java.util.HashMap;
    import java.util.Map;
import javax.swing.border.Border;


    public class UserDashboard extends JFrame {
        private JPanel mainPanel;
        private JPanel headerPanel;
        private final User currentUser;
        private JButton[] navButtons;
        private CardLayout cardLayout;
        private JLabel cartCounter; // Not used in provided code, keep for context if needed elsewhere
        private int currentActiveIndex = 0;
        private JPanel cardPanel; // This will hold all your different "pages"
        private Map<String, JPanel> panelCache = new HashMap<>();
        private boolean isInitialLoad = true; // Not used in provided code, keep for context if needed elsewhere
        private Map<String, List<MenuItem>> menuItemCache = new HashMap<>();
        private Integer cachedUserPoints = null;

        private final CartManager cartManager;
        private int notificationsButtonIndex = -1;


        public UserDashboard(User user) {
            this.currentUser = user;
            this.cartManager = new CartManager(user);
            this.cardLayout = new CardLayout();
            this.cardPanel = new JPanel(cardLayout); // Initialize here too

            setUndecorated(false); // Important for proper fullscreen behavior
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setExtendedState(JFrame.MAXIMIZED_BOTH);

            initializeUI();
        }

        private JPanel createNotificationsContent() {
            return new NotificationsPanel(currentUser);
        }

        private void initializeUI() {
            mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(new Color(40, 40, 40));

            // Show loading panel immediately
            JPanel loadingPanel = createLoadingPanel();
            mainPanel.add(loadingPanel, BorderLayout.CENTER);
            setContentPane(mainPanel);

            setTitle("But First, Coffee - " + currentUser.getFullName());
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setLocationRelativeTo(null);
            setVisible(true); // Make sure to show the frame immediately

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                // Initialize card panel
                cardPanel = new JPanel(cardLayout);
                cardPanel.setBackground(new Color(40, 40, 40));

                JPanel menuPanel = createMenuContent();
                panelCache.put("menu", menuPanel);
                cardPanel.add(menuPanel, "menu");

                cardPanel.add(createLoadingPanel(), "merchandise");
                cardPanel.add(createLoadingPanel(), "rewards");
                cardPanel.add(createLoadingPanel(), "cart");
                cardPanel.add(createLoadingPanel(), "myorders");
                cardPanel.add(createNotificationsContent(), "notifications"); // Changed this line

                cardPanel.add(cartManager.createOrderConfirmationPanel(cardPanel, cardLayout), "orderConfirmation");
                cardPanel.add(cartManager.createCheckoutPanel(cardPanel, cardLayout), "checkout");

                return null;
            }

                @Override
                protected void done() {
                    mainPanel.removeAll();
                    mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
                    mainPanel.add(cardPanel, BorderLayout.CENTER);

                    updateActiveButton(0);
                    cardLayout.show(cardPanel, "menu");

                    mainPanel.revalidate();
                    mainPanel.repaint();
                }
            }.execute();
        }

            private JPanel createCartContent() {
            return cartManager.createCartPanel(cardPanel, cardLayout);
        }

        private JPanel createLoadingPanel() {
            JPanel loadingPanel = new JPanel(new BorderLayout());
            loadingPanel.setBackground(new Color(40, 40, 40));

            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setBackground(new Color(40, 40, 40));
            centerPanel.setBorder(BorderFactory.createEmptyBorder(100, 0, 100, 0));

            // Animated loading icon (using FontAwesome or similar)
            JLabel loadingIcon = new JLabel();
            loadingIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Create a rotating animation
            Timer timer = new Timer(100, new ActionListener() {
                private float angle = 0f;

                @Override
                public void actionPerformed(ActionEvent e) {
                    angle += 30f;
                    if (angle >= 360) angle = 0;

                    // Create a rotated version of the icon
                    Icon originalIcon = UIManager.getIcon("OptionPane.informationIcon");
                    if (originalIcon != null) {
                        Image image = ((ImageIcon)originalIcon).getImage();
                        Image rotatedImage = new ImageIcon(
                            new BufferedImage(originalIcon.getIconWidth(), originalIcon.getIconHeight(),
                                            BufferedImage.TYPE_INT_ARGB)
                        ).getImage();

                        Graphics2D g2d = (Graphics2D)rotatedImage.getGraphics();
                        g2d.rotate(Math.toRadians(angle), originalIcon.getIconWidth()/2, originalIcon.getIconHeight()/2);
                        g2d.drawImage(image, 0, 0, null);
                        g2d.dispose();

                        loadingIcon.setIcon(new ImageIcon(rotatedImage));
                    }
                }
            });
            timer.start();

            // Loading text with dots animation
            JLabel loadingText = new JLabel("Loading");
            loadingText.setFont(new Font("Segoe UI", Font.BOLD, 24));
            loadingText.setForeground(new Color(218, 165, 32)); // Gold color
            loadingText.setAlignmentX(Component.CENTER_ALIGNMENT);

            Timer textTimer = new Timer(500, new ActionListener() {
                private int dotCount = 0;

                @Override
                public void actionPerformed(ActionEvent e) {
                    dotCount = (dotCount + 1) % 4;
                    String dots = String.join("", Collections.nCopies(dotCount, "."));
                    loadingText.setText("Loading" + dots);
                }
            });
            textTimer.start();

            // Add components
            centerPanel.add(Box.createVerticalGlue());
            centerPanel.add(loadingIcon);
            centerPanel.add(Box.createVerticalStrut(30));
            centerPanel.add(loadingText);
            centerPanel.add(Box.createVerticalGlue());

            loadingPanel.add(centerPanel, BorderLayout.CENTER);

            // Make sure to stop timers when panel is removed
            loadingPanel.putClientProperty("timers", new Timer[]{timer, textTimer});

            return loadingPanel;
        }

        private JPanel createHeaderPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(Color.BLACK);
            panel.setPreferredSize(new Dimension(1200, 250));

            JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName(), SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            welcomeLabel.setForeground(new Color(218, 165, 32)); // Gold color to match admin theme
            welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            panel.add(welcomeLabel, BorderLayout.NORTH);

            JPanel gifContainer = new JPanel(new BorderLayout());
            gifContainer.setBackground(Color.BLACK);

            try {
                File gifFile = new File("C:\\Users\\sophi\\Downloads\\images\\header-backgroundd.gif");
                if (gifFile.exists()) {
                    new Thread(() -> {
                        ImageIcon gifIcon = new ImageIcon(gifFile.getAbsolutePath());
                        SwingUtilities.invokeLater(() -> {
                            JLabel gifLabel = new JLabel(gifIcon);
                            gifLabel.setHorizontalAlignment(SwingConstants.CENTER);
                            gifContainer.add(gifLabel, BorderLayout.CENTER);
                            gifContainer.revalidate();
                            gifContainer.repaint();
                        });
                    }).start();
                } else {
                    throw new IOException("GIF file not found");
                }
            } catch (Exception e) {
                System.err.println("GIF Error: " + e.getMessage());
                JLabel errorLabel = new JLabel("Header Image Missing", SwingConstants.CENTER);
                errorLabel.setForeground(Color.RED);
                gifContainer.add(errorLabel, BorderLayout.CENTER);
            }
            panel.add(gifContainer, BorderLayout.CENTER);

            JPanel navBar = new JPanel(new BorderLayout());
            navBar.setBackground(Color.BLACK);
            navBar.setPreferredSize(new Dimension(1200, 50));

            // Left-aligned content (logo + buttons)
            JPanel navContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
            navContent.setBackground(Color.BLACK);

            // Logo
            URL logoUrl = getClass().getResource("/images/logo.png");
            if (logoUrl != null) {
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image scaledLogo = logoIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
                navContent.add(logoLabel);
            } else {
                JLabel missingLogo = new JLabel("[LOGO]");
                missingLogo.setForeground(Color.WHITE);
                navContent.add(missingLogo);
            }

            // Navigation Buttons with CART and MY ORDERS
            String[] navItems = {"MENU", "MERCHANDISE", "REWARDS", "MY ORDERS", "CART"};
            navButtons = new JButton[navItems.length];

            for (int i = 0; i < navItems.length; i++) {
                navButtons[i] = new JButton(navItems[i]);
                navButtons[i].setForeground(Color.WHITE);
                navButtons[i].setBackground(Color.BLACK);
                navButtons[i].setBorder(BorderFactory.createEmptyBorder(0, 15, 8, 15)); // Initial inactive state
                navButtons[i].setFont(new Font("Arial", Font.BOLD, 14));
                navButtons[i].setContentAreaFilled(false);
                navButtons[i].setFocusPainted(false);

                // Set icon for CART button
                if (navItems[i].equals("CART")) {
                    try {
                        URL cartIconUrl = getClass().getResource("/images/cart-icon.png");
                        if (cartIconUrl != null) {
                            ImageIcon icon = new ImageIcon(cartIconUrl);
                            navButtons[i].setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                        }
                    } catch (Exception e) {
                        System.err.println("Couldn't load cart icon: " + e.getMessage());
                    }
                }

                final int buttonIndex = i;

                navButtons[i].addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        JButton source = (JButton)evt.getSource();
                        if (currentActiveIndex != buttonIndex) {
                            source.setForeground(new Color(218, 165, 32));
                        }
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        JButton source = (JButton)evt.getSource();
                        if (currentActiveIndex != buttonIndex) {
                            source.setForeground(Color.WHITE);
                        }
                    }
                });

                final int index = i;
                navButtons[index].addActionListener(e -> {
                    updateActiveButton(index);
                    String cardName = navItems[index].toLowerCase().replace(" ", "");

                    if ("rewards".equals(cardName) || "myorders".equals(cardName)) {
                        refreshUserPointsCache(); // This will fetch the latest points from DB
                    }
                    if (panelCache.containsKey(cardName)) {
                        cardLayout.show(cardPanel, cardName);
                    if ("myorders".equals(cardName)) {
                        JPanel cachedPanel = panelCache.get(cardName);
                         if (cachedPanel instanceof MyOrders) {
                             ((MyOrders) cachedPanel).refreshOrdersList(); // Call the refresh method on the cached instance
                             System.out.println("[DEBUG] Refreshed cached MyOrders panel data.");
                         } else {
                              System.err.println("[ERROR] Cached 'myorders' panel is not an instance of MyOrders.");
                         }
                    }
                    } else {
                        cardLayout.show(cardPanel, cardName);
                        new Thread(() -> {
                            final JPanel contentPanel;

                            switch(cardName) {
                                case "menu":
                                    contentPanel = createMenuContent();
                                    break;
                                case "merchandise":
                                    contentPanel = createMerchandiseContent();
                                    break;
                                case "rewards":
                                    contentPanel = createRewardsContent();
                                    break;
                                case "cart":
                                    contentPanel = createCartContent();
                                    break;
                                case "myorders":
                                    contentPanel = new MyOrders(currentUser);
                                    break;
                                default:
                                    contentPanel = null;
                            }
                            

                            if (contentPanel != null) {
                                SwingUtilities.invokeLater(() -> {
                                    cardPanel.add(contentPanel, cardName);
                                    cardPanel.revalidate();
                                    cardPanel.repaint();
                                    panelCache.put(cardName, contentPanel);
                                    cardLayout.show(cardPanel, cardName);
                                });
                            } else {
                                System.err.println("Could not create panel for cardName: " + cardName);
                                SwingUtilities.invokeLater(() -> {
                                    if (panelCache.containsKey("menu")) {
                                        cardLayout.show(cardPanel, "menu");
                                    } else {
                                        System.err.println("Fallback 'menu' panel not found!");
                                    }
                                });
                            }
                        }).start();
                    }
                });
                navContent.add(navButtons[i]);
            }

            updateActiveButton(0);

            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            rightPanel.setBackground(Color.BLACK);
            rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

            JLabel navWelcomeLabel = new JLabel("Welcome, " + currentUser.getFullName() + "! ");
            navWelcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            navWelcomeLabel.setForeground(new Color(220, 220, 220));
            rightPanel.add(navWelcomeLabel);

            // Add notifications button
            JButton notificationsButton = createNotificationsButton();
            rightPanel.add(notificationsButton);

            JButton logoutButton = new JButton("Logout");
            logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            logoutButton.setForeground(new Color(200, 200, 200));
            logoutButton.setBackground(new Color(70, 70, 70));
            logoutButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));
            logoutButton.setFocusPainted(false);
            logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    logoutButton.setForeground(Color.WHITE);
                    logoutButton.setBackground(new Color(90, 90, 90));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    logoutButton.setForeground(new Color(200, 200, 200));
                    logoutButton.setBackground(new Color(70, 70, 70));
                }
            });

            logoutButton.addActionListener(e -> {
                new LoginForm().setVisible(true);
                dispose();
            });

            rightPanel.add(logoutButton);

            navBar.add(navContent, BorderLayout.WEST);
            navBar.add(rightPanel, BorderLayout.EAST);

            panel.add(navBar, BorderLayout.SOUTH);
            return panel;
        }

        private void updateActiveButton(int activeIndex) {
            currentActiveIndex = activeIndex;

            for (int i = 0; i < navButtons.length; i++) {
                JButton button = navButtons[i];
                if (i == activeIndex) {
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(0, 15, 5, 15),
                        BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(218, 165, 32))
                    ));
                    button.setForeground(new Color(218, 165, 32));
                } else {
                    button.setBorder(BorderFactory.createEmptyBorder(0, 15, 8, 15));
                    button.setForeground(Color.WHITE);
                }
            }
        }

        private JPanel createMenuContent() {
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(new Color(40, 40, 40)); // Dark background

            JPanel headerPanel = new JPanel();
            headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
            headerPanel.setBackground(new Color(40, 40, 40));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));

            JLabel headerLabel = new JLabel("Our Menu");
            headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            headerLabel.setForeground(Color.WHITE);
            headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            headerPanel.add(headerLabel);

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(new Color(40, 40, 40));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

            // Define the desired order of sections
            String[] drinkSubcategoriesOrder = {"Espresso Based", "Brewed Coffee"}; // Add other types as needed
            String otherDrinksTitle = "Other Drinks"; // Title for drinks without a specific type

            // --- Load and Group Drinks ---
            List<MenuItem> allDrinkItems = getMenuItemsByCategory("DRINK");

            // Map to hold drinks grouped by their type
            Map<String, List<MenuItem>> drinksByType = new HashMap<>();
            List<MenuItem> otherDrinkItems = new ArrayList<>(); // For items with no/other type

            for (MenuItem item : allDrinkItems) {
                String type = item.getDrinkType();
                if (type != null && !type.trim().isEmpty()) {
                    // Add to the map for known types
                    if (Arrays.asList(drinkSubcategoriesOrder).contains(type)) {
                         drinksByType.computeIfAbsent(type, k -> new ArrayList<>()).add(item);
                    } else {
                        // If type exists but isn't one of the main subcategories, put in 'Other'
                        otherDrinkItems.add(item);
                    }
                } else {
                    // If drink_type is null or empty, put in 'Other'
                    otherDrinkItems.add(item);
                }
            }

            // --- Create and Add Drink Sections in Order ---
             // Load images for all drinks in the background
             new Thread(() -> {
                for (MenuItem item : allDrinkItems) {
                    loadItemImage(item);
                }
             }).start();

            for (String type : drinkSubcategoriesOrder) {
                List<MenuItem> itemsForType = drinksByType.get(type);
                if (itemsForType != null && !itemsForType.isEmpty()) {
                    // Use the renamed method createItemSectionPanel
                    JPanel sectionPanel = createItemSectionPanel(type, itemsForType);
                    contentPanel.add(sectionPanel);
                    contentPanel.add(Box.createVerticalStrut(30)); // Space between sections
                }
            }

            // Add 'Other Drinks' section if needed
            if (!otherDrinkItems.isEmpty()) {
                 JPanel otherSectionPanel = createItemSectionPanel(otherDrinksTitle, otherDrinkItems);
                 contentPanel.add(otherSectionPanel);
                 contentPanel.add(Box.createVerticalStrut(30)); // Space after drinks
            }


            // --- Load and Add Meal Section ---
            List<MenuItem> mealItems = getMenuItemsByCategory("MEAL");
             // Load images for meals in the background
             new Thread(() -> {
                 for (MenuItem item : mealItems) {
                     loadItemImage(item);
                 }
              }).start();

            if (mealItems != null && !mealItems.isEmpty()) {
                 // Use the renamed method createItemSectionPanel
                 JPanel mealSectionPanel = createItemSectionPanel("Meals", mealItems); // Title "Meals"
                 contentPanel.add(mealSectionPanel);
                 contentPanel.add(Box.createVerticalStrut(30)); // Space after meals
            }


            // Merchandise is handled by a separate tab, so we don't add it here unless needed on the main menu page


            JScrollPane scrollPane = new JScrollPane(contentPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBorder(null);
            scrollPane.getViewport().setBackground(new Color(40, 40, 40));
            scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Faster scrolling

            mainPanel.add(headerPanel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            return mainPanel;
        }


        private void loadItemImage(MenuItem item) {
            ImageHandler.loadItemImage(item);
        }

        private ImageIcon createDefaultIcon(MenuItem item) {
            BufferedImage image = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();

            g2d.setColor(new Color(70, 70, 70));
            g2d.fillRect(0, 0, 150, 150);

            String initial = "";
            if (item.getName() != null && !item.getName().isEmpty()) {
                initial = item.getName().substring(0, 1).toUpperCase();
            } else {
                initial = "?";
            }

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 48));

            // Center the text
            FontMetrics fm = g2d.getFontMetrics();
            int x = (150 - fm.stringWidth(initial)) / 2;
            int y = ((150 - fm.getHeight()) / 2) + fm.getAscent();

            g2d.drawString(initial, x, y);
            g2d.dispose();

            return new ImageIcon(image);
        }

      private JPanel createItemPanel(MenuItem item) {
        // Main panel using BoxLayout for vertical stacking
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(50, 50, 50)); // Dark grey background
        // Base border with 1px line and 5px internal padding
        Border originalBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 1), // Slightly lighter grey border
            BorderFactory.createEmptyBorder(5, 5, 5, 5) // Padding inside border to 5px
        );
        panel.setBorder(originalBorder);

        // Set maximum size to constrain the width to fit the 150px image snugly
        // Calculated as: Image (150) + Border (1*2) + Padding (5*2) = 150 + 2 + 10 = 162px
        panel.setMaximumSize(new Dimension(162, Integer.MAX_VALUE)); // Constrain width to 162
        // Keep a minimum height to prevent it from becoming too squashed, adjust slightly if needed
        panel.setMinimumSize(new Dimension(162, 240)); // Set a minimum height


        // Image container - Use FlowLayout to center the image horizontally
        JPanel imageContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Center alignment, 0 gap
        imageContainer.setBackground(new Color(50, 50, 50)); // Match panel background
        imageContainer.setAlignmentX(Component.CENTER_ALIGNMENT); // Center this container within the main panel's BoxLayout

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);

         ImageHandler.loadItemImage(item);

        if (item.getImageIcon() != null) {
            imageLabel.setIcon(item.getImageIcon());
        } else {
            imageLabel.setIcon(createDefaultIcon(item)); // Fallback to default icon
        }
        imageContainer.add(imageLabel);


        // *** Panel to hold ALL text elements (Name, Price, Description) ***
        // Use BoxLayout.Y_AXIS to stack Name, Price, and Description vertically within this panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS)); // Stack vertically
        textPanel.setBackground(new Color(50, 50, 50)); // Match background
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // No internal padding here

        // Item Name Label (centered within the textPanel)
        JLabel nameLabel = new JLabel(item.getName());
        // *** Name font size 15, bold ***
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLabel.setForeground(Color.WHITE); // White text
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // *** Align name CENTER within textPanel's BoxLayout ***


        // Price Label (centered within the textPanel)
        // Note: This price label will now show the *base* price from the products table,
        // which might be a placeholder for drinks. The size-specific price is shown in the size selection dialog.
        JLabel priceLabel = new JLabel("P" + String.format("%.2f", item.getPrice())); // Display base price from MenuItem
        // *** Price Font Size 13, bold ***
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        priceLabel.setForeground(new Color(218, 165, 32)); // Gold color
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // *** Align price CENTER within textPanel's BoxLayout ***


        // Add description if available (centered within the textPanel)
        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
             // Use HTML for wrapping and center alignment. Adjust width hint for the description text.
             // Inner width of the main panel is 162 - (2*1 + 2*5) = 162 - 12 = 150px.
            JLabel descLabel = new JLabel("<html><body style='width: 140px; text-align: center;'>" + // HTML width hint and text-align center
                                         item.getDescription() + "</body></html>");
            // *** Description font size 11, italic ***
            descLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            descLabel.setForeground(new Color(200, 200, 200)); // Lighter grey
            descLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // *** Align description CENTER within textPanel's BoxLayout ***

            // Add components to the textPanel with spacing
            textPanel.add(nameLabel);
            textPanel.add(Box.createVerticalStrut(3)); // Small gap after name
            textPanel.add(priceLabel);
            textPanel.add(Box.createVerticalStrut(3)); // Small gap after price
            textPanel.add(descLabel);
        } else {
            // Add components to the textPanel with spacing (no description)
            textPanel.add(nameLabel);
            textPanel.add(Box.createVerticalStrut(5)); // Slightly larger gap after name if no description
            textPanel.add(priceLabel);
        }

        // *** Align the textPanel block itself to the CENTER in the main panel's BoxLayout ***
        // This makes the entire text block centered
        textPanel.setAlignmentX(Component.CENTER_ALIGNMENT);


        // Add to cart button
        JButton addButton = new JButton("Add to Cart");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setBackground(new Color(235, 94, 40)); // Orange background
        addButton.setForeground(Color.WHITE);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.setUI(new javax.swing.plaf.basic.BasicButtonUI()); // Keep custom look
        addButton.setOpaque(true); // Ensure background color is painted
        // Vertical padding 3, horizontal 10
        addButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(245, 104, 50), 1),
            BorderFactory.createEmptyBorder(3, 10, 3, 10) // Vertical padding 3, horizontal 10
        ));
        addButton.setFocusPainted(false);

         // Set the button's maximum size and alignment directly to make it stretch
        addButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, addButton.getPreferredSize().height)); // Stretches horizontally
        addButton.setMinimumSize(new Dimension(100, addButton.getPreferredSize().height)); // Ensure minimum width
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center button horizontally (since max width is Integer.MAX_VALUE, this makes it fill)


        // *** MODIFIED ACTION LISTENER ***
         addButton.addActionListener(e -> {
            // Check if the item is a DRINK that requires size selection
            if (item.getCategory().equals("DRINK")) {
                // Show size selection dialog for drinks
                List<Size> availableSizes = getAvailableSizesForProduct(item.getId());

                if (availableSizes.isEmpty()) {
                     JOptionPane.showMessageDialog(panel,
                         "Sizes not available for this drink.",
                         "Error",
                         JOptionPane.ERROR_MESSAGE);
                     System.err.println("[ERROR] No sizes found for drink product ID: " + item.getId());
                     return; // Exit if no sizes found
                }

                // Create a combo box for sizes
                JComboBox<Size> sizeComboBox = new JComboBox<>(availableSizes.toArray(new Size[0]));
                 sizeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                 // --- START MODIFICATIONS FOR BASIC STYLING (Revised Renderer) ---

                 // Define dark background and light text colors (can reuse CartManager colors if public)
                 // Using specific values here as in the previous example for self-containment
                 Color darkBg = new Color(50, 50, 50);
                 Color selectedBg = new Color(70, 70, 70); // Slightly different color for selection
                 Color lightFg = Color.WHITE;

                 // Apply colors to the JComboBox itself (for the selected value area displayed when closed)
                 sizeComboBox.setBackground(darkBg);
                 sizeComboBox.setForeground(lightFg);
                 // Optional: Add a basic border matching your theme
                 // sizeComboBox.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));


                 // Custom renderer to style the items in the dropdown list popup
                 sizeComboBox.setRenderer(new DefaultListCellRenderer() {
                     @Override
                     public Component getListCellRendererComponent(JList<?> list, Object value,
                             int index, boolean isSelected, boolean cellHasFocus) {

                         // --- MODIFIED RENDERER IMPLEMENTATION ---
                         // Create a new JLabel instead of relying on super's component
                         JLabel label = new JLabel();
                         label.setOpaque(true); // <-- THIS IS CRUCIAL for background color to show

                         // Set the text for the list item
                         if (value instanceof Size) {
                              label.setText(((Size) value).toString()); // Use the toString() method from Size class
                         } else if (value != null) {
                             // Handle potential non-Size values (like null or default placeholder)
                              label.setText(value.toString());
                         } else {
                             label.setText(""); // Handle null value
                         }

                         // Set background and foreground colors based on selection state
                         if (isSelected) {
                             label.setBackground(selectedBg); // Use the selected background color
                             label.setForeground(lightFg);    // Use light text color
                         } else {
                             label.setBackground(darkBg);     // Use the standard dark background color
                             label.setForeground(lightFg);    // Use light text color
                         }

                         // Optional: Add padding to list items
                         label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add some padding

                         return label; // Return the custom-configured JLabel
                         // --- END MODIFIED RENDERER IMPLEMENTATION ---
                     }
                 });


                 // Style the text field editor component (where the selected value appears when closed)
                 JTextField editor = (JTextField) sizeComboBox.getEditor().getEditorComponent();
                 editor.setBackground(darkBg);
                 editor.setForeground(lightFg);
                 editor.setCaretColor(lightFg); // Make the text cursor visible
                 // Optional: Add padding inside the editor
                 editor.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));


                // --- END MODIFICATIONS FOR BASIC STYLING ---


                JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
                // --- START MODIFICATIONS FOR SIZE PANEL STYLING ---
                 // Set the panel background explicitly to ensure the dialog's content area is dark
                 sizePanel.setBackground(darkBg);
                // --- END MODIFICATIONS FOR SIZE PANEL STYLING ---

                // --- START MODIFICATIONS FOR SIZE LABEL STYLING ---
                JLabel selectSizeLabel = new JLabel("Select Size:");
                selectSizeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                selectSizeLabel.setForeground(lightFg); // Set label text color
                sizePanel.add(selectSizeLabel);
                // --- END MODIFICATIONS FOR SIZE LABEL STYLING ---

                sizePanel.add(sizeComboBox);


                // Show the dialog
                // Note: Styling JOptionPane buttons themselves is not handled here.
                // The dialog's background itself might still be affected by LaF,
                // but the sizePanel we are inserting *should* cover it with its background.
                int result = JOptionPane.showConfirmDialog(
                    panel,                            // Parent component
                    sizePanel,                        // The message content (our styled panel)
                    "Select Size for " + item.getName(), // Dialog title
                    JOptionPane.OK_CANCEL_OPTION,     // Option type
                    JOptionPane.PLAIN_MESSAGE         // Message type (no default icon)
                );

                // Process the dialog result
                if (result == JOptionPane.OK_OPTION) {
                    Size selectedSize = (Size) sizeComboBox.getSelectedItem();
                    if (selectedSize != null) {
                        cartManager.addToCart(item, selectedSize, 1); // Quantity is 1 when adding from menu
                    } else {
                         // Should not happen if list is not empty, but handle defensively
                         JOptionPane.showMessageDialog(panel,
                             "No size selected.",
                             "Selection Error",
                             JOptionPane.WARNING_MESSAGE);
                    }
                }
                // If result is CANCEL_OPTION or closed, do nothing.

            } else {
                // For MEAL or MERCHANDISE items, add directly with null size
                cartManager.addToCart(item, null, 1); // Quantity is 1, Size is null
            }
             // Note: cartManager.addToCart already handles forceCartRefresh()

             System.out.println("[DEBUG] Add to Cart button clicked for " + item.getName());
        });


        // Hover effects (keep them and update padding calculation for hover border)
         panel.addMouseListener(new java.awt.event.MouseAdapter() {
             Color originalBg = new Color(50, 50, 50); // Use the original background color value
             Color hoverBg = new Color(60, 60, 60); // Slightly darker on hover
             Border baseOriginalBorder = originalBorder; // Use a final variable for the original border
             Border hoverBorder = BorderFactory.createCompoundBorder(
                 BorderFactory.createLineBorder(new Color(218, 165, 32), 2), // Thicker gold border on hover
                 BorderFactory.createEmptyBorder(4, 4, 4, 4) // Adjusted padding for 2px border based on new 5px original
             );


             public void mouseEntered(java.awt.event.MouseEvent evt) {
                 panel.setBackground(hoverBg);
                 panel.setBorder(hoverBorder); // Use the adjusted hover border
                 // Also update background of inner panels to match
                 imageContainer.setBackground(hoverBg);
                 textPanel.setBackground(hoverBg); // Update the textPanel background
             }
             public void mouseExited(java.awt.event.MouseEvent evt) {
                 panel.setBackground(originalBg);
                 panel.setBorder(baseOriginalBorder); // Reset to original border
                 // Reset background of inner panels
                 imageContainer.setBackground(originalBg);
                 textPanel.setBackground(originalBg); // Reset textPanel background
             }
         });

        panel.add(imageContainer);      // Image (centered via its own container)
        panel.add(Box.createVerticalStrut(8)); // Gap below image and text block
        panel.add(textPanel);       // Add the single textPanel here (it's centered via its own setAlignmentX)
        panel.add(Box.createVerticalGlue()); // Use glue BEFORE the button to push it to the bottom
        panel.add(addButton);           // Add the button DIRECTLY here (centered/stretched via setAlignmentX/setMaximumSize)
        panel.add(Box.createVerticalStrut(5)); // Small gap at the bottom after button


        return panel;
    }

    private List<Size> getAvailableSizesForProduct(int productId) {
        List<Size> sizes = new ArrayList<>();
        String query = "SELECT s.size_id, s.size_name, ps.price " +
                       "FROM product_sizes ps " +
                       "JOIN sizes s ON ps.size_id = s.size_id " +
                       "WHERE ps.product_id = ? " +
                       "ORDER BY s.sort_order, s.size_name"; // Order sizes logically

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sizes.add(new Size(
                        rs.getInt("size_id"),
                        rs.getString("size_name"),
                        rs.getDouble("price") // This is the size-specific price from product_sizes
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Error fetching sizes for product " + productId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return sizes;
    }
      
    private JPanel createItemSectionPanel(String sectionTitle, List<MenuItem> items) {
            JPanel panel = new JPanel(); // This is the main panel for the section block (with the yellow border)
            panel.setLayout(new BorderLayout());
            panel.setBackground(new Color(40, 40, 40));

            // Adjust the border for the yellow box
            // Using 1px line and 10px internal padding (as set in a previous step)
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 165, 32), 1), // 1px yellow line border
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Horizontal padding (Left/Right) 10, Vertical (Top/Bottom) 10.
            ));

            // Create header panel - This panel holds the section title and separator
            JPanel headerPanel = new JPanel();
            headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
            headerPanel.setBackground(new Color(40, 40, 40));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Add bottom margin to header content


            // *** Use the sectionTitle parameter directly for the label ***
            JLabel sectionLabel = new JLabel(sectionTitle);
            sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            sectionLabel.setForeground(new Color(218, 165, 32));
            sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align label left
            headerPanel.add(sectionLabel);
            headerPanel.add(Box.createVerticalStrut(10));

            JSeparator separator = new JSeparator();
            separator.setMaximumSize(new Dimension(Short.MAX_VALUE, 1)); // Stretch horizontally
            separator.setForeground(new Color(70, 70, 70));
            separator.setAlignmentX(Component.LEFT_ALIGNMENT); // Align separator left (stretches anyway within headerPanel)
            headerPanel.add(separator);

             JPanel itemsGridContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20)); // Horizontal/Vertical gap *between item panels* if using FlowLayout
             itemsGridContainer.setBackground(new Color(40, 40, 40)); // Match background


            // The actual panel containing the item panels (using GridLayout)
             // Display 6 items per row
             // new GridLayout(0, 6, 20, 20) means 0 rows (dynamic), 6 columns, 20px horizontal and vertical gap
            JPanel itemsContentPanel = new JPanel(new GridLayout(0, 6, 20, 20)); // 6 columns, 20px gap
            itemsContentPanel.setBackground(new Color(40, 40, 40)); // Match background


            if (items == null || items.isEmpty()) { // Handle null list case too
                JLabel noItemsLabel = new JLabel("No items available in this section.");
                noItemsLabel.setForeground(Color.WHITE);
                itemsGridContainer.add(noItemsLabel); // Add the message to the centering container
            } else {
                for (MenuItem item : items) {
                    // Call the createItemPanel method (which creates the individual grey boxes)
                    JPanel itemPanel = createItemPanel(item);
                    itemsContentPanel.add(itemPanel); // Add individual item panels to the grid panel
                }
                itemsGridContainer.add(itemsContentPanel); // Add the grid panel to the centering container
            }

            // Add the header panel to the NORTH region of the main 'panel' (inside the yellow border)
            panel.add(headerPanel, BorderLayout.NORTH);
            // Add the items grid container to the CENTER region of the main 'panel' (inside the yellow border)
            panel.add(itemsGridContainer, BorderLayout.CENTER);


            return panel;
        }

                private List<MenuItem> getMenuItemsByCategory(String category) {
            // Check cache first
            if (menuItemCache.containsKey(category)) {
                // Assuming the cache stores MenuItem objects that now have drinkType
                return menuItemCache.get(category);
            }

            List<MenuItem> items = new ArrayList<>();
            Connection conn = null; // Declare conn outside try for finally block
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                conn = DBConnection.getConnection();
                // *** Select the new 'drink_type' column ***
                String sql = "SELECT product_id, name, price, description, image_url, category, drink_type FROM products WHERE category = ? AND is_available = 1 ORDER BY name ASC"; // Added category and drink_type, ordered by name
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, category);
                rs = stmt.executeQuery();

                while (rs.next()) {
                    // *** Use the updated constructor including category and drinkType ***
                    items.add(new MenuItem(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        rs.getString("category"), // Pass category
                        rs.getString("drink_type") // Pass drink_type
                    ));
                }
                menuItemCache.put(category, items);
            } catch (SQLException e) {
                System.err.println("Error loading menu items for category " + category + ": " + e.getMessage());
                e.printStackTrace(); // Print stack trace for more details
            } finally {
                // Close resources
                try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
                try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
                try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignore */ }
            }

            return items;
        }
                
        private JPanel createMerchandiseContent() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(new Color(40, 40, 40)); // Dark background
            panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

            JLabel headerLabel = new JLabel("Merchandise");
            headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            headerLabel.setForeground(Color.WHITE);
            headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(headerLabel);
            panel.add(Box.createVerticalStrut(20));

            JLabel descLabel = new JLabel("<html>Explore our exclusive collection of merchandise. " +
                                         "Wear your favorite coffee brand with pride!</html>");
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            descLabel.setForeground(new Color(200, 200, 200)); // Light gray for description
            descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(descLabel);
            panel.add(Box.createVerticalStrut(30));

            List<MenuItem> merchandiseItems = getMenuItemsByCategory("MERCHANDISE");
            JPanel itemsGrid = new JPanel(new GridLayout(0, 3, 20, 20));
            itemsGrid.setBackground(new Color(40, 40, 40)); // Dark background

            if (merchandiseItems.isEmpty()) {
                JLabel noItemsLabel = new JLabel("No merchandise available at this time");
                noItemsLabel.setForeground(Color.WHITE);
                noItemsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                panel.add(noItemsLabel);
            } else {
                for (MenuItem item : merchandiseItems) {
                    JPanel itemPanel = createItemPanel(item);
                    itemsGrid.add(itemPanel);
                }
            }

            panel.add(itemsGrid);
            return panel;
        }

    private JPanel createRewardsContent() {
        // Use a BorderLayout for the main panel. It will contain the contentPanel directly.
        JPanel mainRewardsPanel = new JPanel(new BorderLayout());
        mainRewardsPanel.setBackground(new Color(40, 40, 40)); // Dark background

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(40, 40, 40));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // Add padding

        JLabel headerLabel = new JLabel("Rewards Program");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the header
        contentPanel.add(headerLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Refresh points cache before displaying
        refreshUserPointsCache();
        int userPoints = getUserPoints(); // Get points from the refreshed cache

        JLabel pointsLabel = new JLabel("Your current points: " + userPoints);
        pointsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pointsLabel.setForeground(new Color(218, 165, 32)); // Gold color for points
        pointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center points label
        contentPanel.add(pointsLabel);
        contentPanel.add(Box.createVerticalStrut(30));

        JPanel descPanel = new JPanel();
        descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.Y_AXIS)); // Use BoxLayout internally
        descPanel.setBackground(new Color(50, 50, 50)); // Slightly lighter than background
        descPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        descPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the description panel itself within BoxLayout
        descPanel.setMaximumSize(new Dimension(500, Short.MAX_VALUE)); // Limit max width

        JLabel howItWorksLabel = new JLabel("How It Works:");
        howItWorksLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        howItWorksLabel.setForeground(Color.WHITE);
        howItWorksLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descText = new JLabel(
            "<html><body style='width: 350px;'>" + // Set a preferred max width
            "<b>•</b> Earn 1 point for every P50 spent (awarded upon order delivery)<br>" + // Updated description
            "<b>•</b> Redeem points for exclusive rewards and discounts<br>" +
            "<b>•</b> Special birthday reward - double points all month!" + // Example
            "</body></html>"
        );
        descText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descText.setForeground(new Color(200, 200, 200)); // Light gray for text
        descText.setAlignmentX(Component.LEFT_ALIGNMENT); // Left align text within descPanel

        descPanel.add(howItWorksLabel);
        descPanel.add(Box.createVerticalStrut(10));
        descPanel.add(descText);

        contentPanel.add(descPanel);
        contentPanel.add(Box.createVerticalStrut(30));

        JLabel rewardsLabel = new JLabel("Available Rewards");
        rewardsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rewardsLabel.setForeground(new Color(218, 165, 32)); // Gold color
        rewardsLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center this label
        contentPanel.add(rewardsLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Use a panel to hold the rewards grid, allowing it to be centered as a block
        JPanel rewardsGridContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15)); // Use FlowLayout for centering
        rewardsGridContainer.setBackground(new Color(40, 40, 40)); // Match background
        rewardsGridContainer.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the container itself in the BoxLayout

        JPanel rewardsGrid = new JPanel(new GridLayout(0, 3, 15, 15)); // The grid with reward panels (3 columns)
        rewardsGrid.setBackground(new Color(40, 40, 40)); // Match background

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT reward_id, name, points_cost, discount_amount, description " + // Added description
                           "FROM rewards " +
                           "WHERE is_active = 1 " + // Only show active rewards
                           "ORDER BY points_cost ASC, reward_id ASC"; // Order by points cost, then id

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();

                if (!rs.isBeforeFirst()) { // Check if ResultSet is empty
                     JLabel noRewardsLabel = new JLabel("No rewards available at this time.");
                     noRewardsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                     noRewardsLabel.setForeground(new Color(200, 200, 200));
                     noRewardsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                     JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Use flow layout to center the label
                     centerWrapper.setBackground(new Color(40, 40, 40));
                     rewardsGridContainer.add(centerWrapper); // Add the centered label wrapper
                } else {
                     while (rs.next()) {
                         int rewardId = rs.getInt("reward_id");
                         String name = rs.getString("name");
                         int pointsCost = rs.getInt("points_cost");
                         double discountAmount = rs.getDouble("discount_amount");
                         String description = rs.getString("description"); // Get description

                         // Pass all necessary info to createRewardPanel
                         JPanel rewardPanel = createRewardPanel(rewardId, name, pointsCost, discountAmount, userPoints, description); // Pass description
                         rewardsGrid.add(rewardPanel);
                     }
                     // Add the grid to its centering container ONLY if there are items
                     rewardsGridContainer.add(rewardsGrid);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading available rewards: " + e.getMessage());
            e.printStackTrace();
            // Display an error message in the UI if needed
            JLabel errorLabel = new JLabel("Error loading rewards.");
            errorLabel.setForeground(Color.RED); // Use a red color for errors
             rewardsGridContainer.removeAll(); // Clear any potential partial grid
             JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Use flow layout to center the label
             centerWrapper.setBackground(new Color(40, 40, 40));
             centerWrapper.add(errorLabel);
             rewardsGridContainer.add(centerWrapper);
        }

        contentPanel.add(rewardsGridContainer);

        mainRewardsPanel.add(contentPanel, BorderLayout.CENTER);

        return mainRewardsPanel;
    }


    private JPanel createRewardPanel(int rewardId, String rewardName, int pointsCost, double discountAmount, int userPoints, String description){ // Added rewardId and description
        JPanel panel = new JPanel(new BorderLayout(15, 5)); // Add horizontal and vertical gap
        panel.setBackground(new Color(50, 50, 50)); // Slightly lighter background
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(350, 180)); // Adjusted size for content
        panel.setMinimumSize(new Dimension(200, 100));
        panel.setMaximumSize(new Dimension(400, 250));


        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS)); // Stack labels vertically
        infoPanel.setBackground(new Color(50, 50, 50));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(rewardName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pointsLabel = new JLabel(pointsCost + " points");
        pointsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pointsLabel.setForeground(new Color(218, 165, 32)); // Gold color for points
        pointsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Optional: Show discount amount here if desired
         if (discountAmount > 0) {
             JLabel discountLabel = new JLabel("Value: -₱" + String.format("%.2f", discountAmount));
             discountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
             discountLabel.setForeground(new Color(0, 200, 0)); // Green
             discountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
             infoPanel.add(nameLabel);
             infoPanel.add(Box.createVerticalStrut(3)); // Smaller gap
             infoPanel.add(discountLabel);
             infoPanel.add(Box.createVerticalStrut(8)); // Larger gap before points
             infoPanel.add(pointsLabel);
         } else {
             // For 0-cost/0-discount rewards, just name and points (which is 0)
             infoPanel.add(nameLabel);
             infoPanel.add(Box.createVerticalStrut(8)); // Gap before points
             infoPanel.add(pointsLabel);
         }

        // Add description label
        if (description != null && !description.trim().isEmpty()) {
            JLabel descLabel = new JLabel("<html><body style='width: 180px;'>" + description + "</body></html>");
            descLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            descLabel.setForeground(new Color(180, 180, 180)); // Lighter gray for description
            descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(Box.createVerticalStrut(5)); // Space before description
            infoPanel.add(descLabel);
        }


        JButton redeemButton = new JButton("Redeem");
        redeemButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        redeemButton.setBackground(new Color(218, 165, 32)); // Gold button
        redeemButton.setForeground(Color.BLACK);
        redeemButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0).darker(), 1), // Use a darker gold border
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        redeemButton.setFocusPainted(false);
        redeemButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        // Check if this reward has already been redeemed by the user (check active, non-expired)
        boolean isRedeemed = checkIfRewardRedeemed(currentUser.getUserId(), rewardId); // Check by ID
        String redemptionCode = null;
        if (isRedeemed) {
             redemptionCode = getRedemptionCode(currentUser.getUserId(), rewardId); // Get the code if redeemed by ID
        }

        if (isRedeemed) {
            // If redeemed, show the code and expiration
            JLabel redeemedLabel = new JLabel("<html>Redeemed! Code: <b>" + (redemptionCode != null ? redemptionCode : "N/A") + "</b></html>");
            redeemedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            redeemedLabel.setForeground(new Color(0, 200, 0)); // Green color
            redeemedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(Box.createVerticalStrut(8)); // Space before code
            infoPanel.add(redeemedLabel);

            // Disable the redeem button
            redeemButton.setEnabled(false);
            redeemButton.setBackground(new Color(100, 100, 100));
            redeemButton.setForeground(new Color(150, 150, 150));
            redeemButton.setText("Already Redeemed");
            redeemButton.setToolTipText(null); // Remove tooltip

        } else {
            // Original button behavior for non-redeemed rewards
            // Enable if user points are sufficient
            redeemButton.setEnabled(userPoints >= pointsCost);

            if (!redeemButton.isEnabled()) {
                redeemButton.setToolTipText("Requires " + pointsCost + " points"); // Inform user why disabled
                redeemButton.setBackground(new Color(100, 100, 100)); // Disabled state background
                redeemButton.setForeground(new Color(150, 150, 150)); // Disabled state foreground
            } else {
                 redeemButton.setToolTipText("Redeem this reward"); // Tooltip for enabled button
                 // Hover effects (only for enabled buttons)
                 redeemButton.addMouseListener(new java.awt.event.MouseAdapter() {
                     public void mouseEntered(java.awt.event.MouseEvent evt) {
                         if (redeemButton.isEnabled()) { // Check again inside listener
                             redeemButton.setBackground(new Color(255, 215, 0)); // Brighter gold on hover
                         }
                     }
                     public void mouseExited(java.awt.event.MouseEvent evt) {
                         if (redeemButton.isEnabled()) { // Check again inside listener
                             redeemButton.setBackground(new Color(218, 165, 32)); // Back to normal gold
                         }
                     }
                 });

                 redeemButton.addActionListener(e -> {
                     int option = JOptionPane.showConfirmDialog(this,
                         "<html>Are you sure you want to redeem <b>" + rewardName + "</b> for <b>" + pointsCost + "</b> points?</html>",
                         "Confirm Redemption",
                         JOptionPane.YES_NO_OPTION);

                     if (option == JOptionPane.YES_OPTION) {
                         // Re-check points just in case they changed since panel creation
                         // *** IMPORTANT: Fetch latest points here before proceeding ***
                         refreshUserPointsCache(); // Ensure cache is fresh
                         int currentPoints = getUserPoints(); // Get latest points

                         if (currentPoints < pointsCost) {
                             JOptionPane.showMessageDialog(this,
                                 "You no longer have enough points for this reward.", // Message updated
                                 "Insufficient Points",
                                 JOptionPane.WARNING_MESSAGE);
                              // Refresh the panel's button state immediately
                              refreshRewardsPanel(); // Refresh the entire panel to show updated points/button states
                             return; // Stop the redemption process
                         }

                         Connection conn = null;
                         try {
                             conn = DBConnection.getConnection();
                             conn.setAutoCommit(false); // Start transaction

                             // 1. Record the redemption
                             String rewardCode = generateRewardCode();
                             recordRedemption(conn, currentUser.getUserId(), rewardId, discountAmount, pointsCost, rewardCode); // Pass connection

                             // 2. Deduct points
                             updateUserPoints(conn, currentUser.getUserId(), -pointsCost); // Pass connection

                             conn.commit(); // Commit transaction
                             System.out.println("Redemption successful. Code: " + rewardCode);

                             // Refresh user points cache after successful commit
                             refreshUserPointsCache();

                             JOptionPane.showMessageDialog(this,
                                 "<html>You have successfully redeemed <b>" + rewardName + "</b>.<br>" +
                                 "Your reward code: <b>" + rewardCode + "</b><br>" +
                                 "Enter this code in the checkout page to apply your discount.</html>",
                                 "Redemption Successful",
                                 JOptionPane.INFORMATION_MESSAGE);

                             // Refresh the rewards panel to show the redeemed state and updated points
                             refreshRewardsPanel(); // Call the refresh method

                         } catch (SQLException ex) {
                             if (conn != null) {
                                  try {
                                      conn.rollback(); // Rollback transaction on error
                                      System.err.println("Redemption transaction rolled back.");
                                  } catch (SQLException rollbackEx) {
                                      System.err.println("Error during rollback: " + rollbackEx.getMessage());
                                  }
                              }
                             ex.printStackTrace();
                             JOptionPane.showMessageDialog(this,
                                 "Error redeeming reward: " + ex.getMessage(),
                                 "Error",
                                 JOptionPane.ERROR_MESSAGE);
                              // Optionally refresh the panel to reflect any state changes even on error
                              refreshRewardsPanel();
                         } finally {
                              if (conn != null) {
                                  try {
                                      conn.setAutoCommit(true); // Restore auto-commit
                                      conn.close();
                                  } catch (SQLException closeEx) {
                                      System.err.println("Error closing connection after redemption: " + closeEx.getMessage());
                                  }
                              }
                         }
                     }
                 });
            }
        }


        panel.add(infoPanel, BorderLayout.CENTER);

        // Use a FlowLayout panel to contain the button for better positioning within the BorderLayout.EAST cell
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center the button horizontally in its cell
        buttonPanel.setBackground(new Color(50, 50, 50)); // Match panel background
        buttonPanel.add(redeemButton);
        // Optional: Add some spacing around the button if needed
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5)); // Add a little padding

        panel.add(buttonPanel, BorderLayout.EAST); // Add the button panel to the EAST


        // Add hover effect to the entire panel
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(60, 60, 60));
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(218, 165, 32), 2), // Thicker gold border on hover
                    BorderFactory.createEmptyBorder(14, 14, 14, 14) // Adjusted padding to match border
                ));
                infoPanel.setBackground(new Color(60, 60, 60)); // Match info panel background
                buttonPanel.setBackground(new Color(60, 60, 60)); // Match button panel background
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(50, 50, 50));
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15) // Original padding
                ));
                infoPanel.setBackground(new Color(50, 50, 50)); // Reset info panel background
                buttonPanel.setBackground(new Color(50, 50, 50)); // Reset button panel background
            }
        });

        return panel;
    }

        // Need a helper method to refresh the rewards panel content
       private void refreshRewardsPanel() {
            // Find the existing rewards panel in the cardPanel cache
            JPanel oldRewardsPanel = panelCache.get("rewards");

            // Create a new rewards panel - this calls createRewardsContent internally
            JPanel newRewardsPanel = createRewardsContent(); // This recreates the entire panel with updated data

            if (oldRewardsPanel != null) {
                // Replace the old panel in the cardPanel
                // Find the index of the old panel
                int oldIndex = -1;
                for (int i = 0; i < cardPanel.getComponentCount(); i++) {
                    // We find the component by its identity, not its constraint
                    if (cardPanel.getComponent(i) == oldRewardsPanel) {
                        oldIndex = i;
                        break;
                    }
                }

                if (oldIndex != -1) {
                    cardPanel.remove(oldRewardsPanel); // Remove the old one
                    cardPanel.add(newRewardsPanel, "rewards", oldIndex); // Add the new one at the same index
                    panelCache.put("rewards", newRewardsPanel); // Update the cache with the new panel

                    // Ensure the card layout shows the new panel if the rewards card was currently visible
                    // Check if the *Rewards* card was the one currently active using the nav button index
                    // Assuming "REWARDS" is the 3rd button (index 2) based on your navItems array order
                    if (currentActiveIndex == 2) { // Index 2 corresponds to "REWARDS"
                         SwingUtilities.invokeLater(() -> cardLayout.show(cardPanel, "rewards"));
                    } else {
                         // If another card was active, just revalidate/repaint
                         cardPanel.revalidate();
                         cardPanel.repaint();
                    }


                    System.out.println("[DEBUG] Rewards panel refreshed.");
                } else {
                     System.err.println("[ERROR] Could not find old Rewards panel component in cardPanel for removal.");
                     // Fallback: Just add the new one if removal failed, might lead to duplicates or incorrect layout
                     cardPanel.add(newRewardsPanel, "rewards");
                     panelCache.put("rewards", newRewardsPanel);
                     // Force show if it was intended to be the active panel, otherwise just revalidate
                     if (currentActiveIndex == 2) {
                         SwingUtilities.invokeLater(() -> cardLayout.show(cardPanel, "rewards")); // Force show
                     } else {
                         cardPanel.revalidate();
                         cardPanel.repaint();
                     }
                }
            } else {
                 // This case should ideally not happen if the panel was added correctly initially
                 System.err.println("[ERROR] Rewards panel not found in cache for refresh. Creating new.");
                 // Fallback: just create and add if not cached
                 cardPanel.add(newRewardsPanel, "rewards"); // Add the new one
                 panelCache.put("rewards", newRewardsPanel); // Cache it
                 // Force show if it was intended to be the active panel
                 if (currentActiveIndex == 2) {
                    SwingUtilities.invokeLater(() -> cardLayout.show(cardPanel, "rewards")); // Force show
                 }
                 cardPanel.revalidate();
                 cardPanel.repaint();
            }
        }

        // Also update the recordRedemption method signature to accept discountAmount and pointsUsed
        private void recordRedemption(Connection conn, int userId, int rewardId, double discountAmount, int pointsUsed, String code) throws SQLException {
            // Calculate expiration date (5 days from now)
            java.util.Date now = new java.util.Date();
            // Use 5L * ... for long arithmetic to prevent overflow if multiplication exceeds int max
            java.util.Date expiresAt = new java.util.Date(now.getTime() + (5L * 24 * 60 * 60 * 1000)); // 5 days
            java.sql.Timestamp expiresAtSql = new java.sql.Timestamp(expiresAt.getTime());

            // Insert into redemptions with all required fields
            // Use the passed Connection object
            String query = "INSERT INTO reward_redemptions (user_id, reward_id, points_used, discount_amount, code, redemption_date, expires_at, redeemed) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, 0)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, rewardId);
                stmt.setInt(3, pointsUsed); // Use pointsUsed parameter (which is reward's points_cost)
                stmt.setDouble(4, discountAmount); // Use discountAmount parameter
                stmt.setString(5, code);
                stmt.setTimestamp(6, expiresAtSql);
                stmt.executeUpdate();
                System.out.println("[DEBUG] Redemption recorded in DB. Code: " + code);
            } catch (SQLException e) {
                System.err.println("[ERROR] Error recording redemption in DB: " + e.getMessage());
                throw e; // Re-throw to trigger rollback in the caller
            }
        }

        // Update updateUserPoints method to refresh the cache after update
        private void updateUserPoints(Connection conn, int userId, int pointsChange) throws SQLException {
            // Use the passed Connection object
            String query = "UPDATE user_rewards SET points_balance = points_balance + ? WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, pointsChange);
                stmt.setInt(2, userId);
                int updatedRows = stmt.executeUpdate();

                if (updatedRows == 0) {
                    // Handle case where user_rewards record doesn't exist (shouldn't happen if initialized)
                    // Or if the user_id didn't match.
                     System.err.println("[WARNING] updateUserPoints affected 0 rows for user_id " + userId + ". Change: " + pointsChange);
                     // Optionally throw an exception here if this indicates an error
                     // throw new SQLException("Failed to update points for user " + userId);
                }
                 System.out.println("[DEBUG] User points updated in DB. Change: " + pointsChange);

            } catch (SQLException e) {
                 System.err.println("[ERROR] Error updating user points in DB: " + e.getMessage());
                 throw e; // Re-throw to trigger rollback in the caller
            }
        }

    // Ensure the `checkIfRewardRedeemed` method uses rewardName and checks for active/non-expired
    private boolean checkIfRewardRedeemed(int userId, int rewardId) { // Check by ID
        try (Connection conn = DBConnection.getConnection()) {
            // Join with rewards to ensure the reward is active, though the calling logic already filters by is_active
            // The critical checks are user_id, reward_id, redeemed = 0, and expires_at > CURRENT_TIMESTAMP
            String query = "SELECT 1 FROM reward_redemptions rr " +
                           "JOIN rewards r ON rr.reward_id = r.reward_id " + // Join is good practice to ensure it's a valid reward
                           "WHERE rr.user_id = ? AND rr.reward_id = ? AND rr.redeemed = 0 AND rr.expires_at > CURRENT_TIMESTAMP";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, rewardId); // Use rewardId
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next(); // Returns true if there is at least one matching row
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error checking if reward " + rewardId + " redeemed for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return false; // Assume not redeemed on error
        }
    }
    
        // Ensure the `getRedemptionCode` method uses rewardName and checks for active/non-expired
    private String getRedemptionCode(int userId, int rewardId) { // Get by ID
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT rr.code FROM reward_redemptions rr " +
                           "JOIN rewards r ON rr.reward_id = r.reward_id " + // Join is good practice
                           "WHERE rr.user_id = ? AND rr.reward_id = ? AND rr.redeemed = 0 AND rr.expires_at > CURRENT_TIMESTAMP";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, rewardId); // Use rewardId
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("code");
                    }
                    return null; // No active, unredeemed code found
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error getting redemption code for reward " + rewardId + " for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return null; // Return null on error
        }
    }

        private int getUserPoints() {
            // Return cached value if available
            if (cachedUserPoints != null) {
                // System.out.println("[DEBUG] Returning cached points: " + cachedUserPoints);
                return cachedUserPoints;
            }

            // Fetch from DB if not cached
            try (Connection conn = DBConnection.getConnection()) {
                String query = "SELECT points_balance FROM user_rewards WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, currentUser.getUserId());
                    try (ResultSet rs = stmt.executeQuery()) {

                        if (rs.next()) {
                            cachedUserPoints = rs.getInt("points_balance");
                            // System.out.println("[DEBUG] Fetched and cached points: " + cachedUserPoints);
                            return cachedUserPoints;
                        } else {
                            // If user_rewards record doesn't exist, create it and return 0
                            System.out.println("[DEBUG] No user_rewards record found for user " + currentUser.getUserId() + ". Initializing.");
                            initializeUserRewards(currentUser.getUserId()); // Initialize it
                            cachedUserPoints = 0; // Cache the initial 0
                            return 0;
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("[ERROR] Error getting user points from DB: " + e.getMessage());
                e.printStackTrace();
                return 0; // Return 0 or handle error appropriately
            }
        }

        public void refreshUserPointsCache() {
            cachedUserPoints = null;
            getUserPoints(); // This will refresh the cache
        }

        private void initializeUserRewards(int userId) {
            try {
                Connection conn = DBConnection.getConnection();
                String query = "INSERT INTO user_rewards (user_id, points_balance) VALUES (?, 0)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error initializing user rewards: " + e.getMessage());
            }
        }

        private int getRewardIdByName(String rewardName) throws SQLException {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT reward_id FROM rewards WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, rewardName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("reward_id");
            }
            return -1;
        }

        private String generateRewardCode() {
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            StringBuilder code = new StringBuilder();

            for (int i = 0; i < 8; i++) {
                int index = (int)(Math.random() * chars.length());
                code.append(chars.charAt(index));
            }

            return code.toString();
        }

        private JButton createNotificationsButton() {
            JButton button = new JButton();
            try {
                URL iconUrl = getClass().getResource("/images/notification-icon.png");
                if (iconUrl != null) {
                    ImageIcon icon = new ImageIcon(iconUrl);
                    button.setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                } else {
                    button.setText("Notifications");
                }
            } catch (Exception e) {
                button.setText("Notifications");
            }

            button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(70, 70, 70));
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            button.setFocusPainted(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Create popup panel
            JPanel popupPanel = new JPanel(new BorderLayout());
            popupPanel.setPreferredSize(new Dimension(400, 500));
            popupPanel.setBackground(new Color(40, 40, 40));
            popupPanel.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 1));

            // Add notifications content
            NotificationsPanel notificationsPanel = new NotificationsPanel(currentUser);
            popupPanel.add(notificationsPanel, BorderLayout.CENTER);

            // Create the popup
            JPopupMenu popupMenu = new JPopupMenu();
            popupMenu.setBorder(BorderFactory.createEmptyBorder());
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.add(popupPanel, BorderLayout.CENTER);
            popupMenu.add(wrapper);

            button.addActionListener(e -> {
                // Position the popup below the button
                popupMenu.show(button, 0, button.getHeight());

                // Refresh notifications when opened
                notificationsPanel.loadNotifications();
            });

            // Hover effects
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(90, 90, 90));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(70, 70, 70));
                }
            });

            return button;
        }

        protected Void doInBackground() throws Exception {
            // Initialize card panel
            cardPanel = new JPanel(cardLayout);
            cardPanel.setBackground(new Color(40, 40, 40));

            JPanel menuPanel = createMenuContent();
            panelCache.put("menu", menuPanel);
            cardPanel.add(menuPanel, "menu");

            cardPanel.add(createLoadingPanel(), "merchandise");
            cardPanel.add(createLoadingPanel(), "rewards");
            cardPanel.add(createLoadingPanel(), "cart");
            cardPanel.add(createLoadingPanel(), "myorders");

            cardPanel.add(cartManager.createOrderConfirmationPanel(cardPanel, cardLayout), "orderConfirmation");
            cardPanel.add(cartManager.createCheckoutPanel(cardPanel, cardLayout), "checkout");

            return null;
        }
        
         public JPanel getCachedPanel(String name) {
            return panelCache.get(name);
        }
         
    }