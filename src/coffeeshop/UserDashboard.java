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
    import java.util.Collections;
    import java.util.HashMap;
    import java.util.Map;


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

                    if (panelCache.containsKey(cardName)) {
                        cardLayout.show(cardPanel, cardName);
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

            String[] categories = {"DRINK", "MEAL"};

            new Thread(() -> {
                for (String category : categories) {
                    List<MenuItem> items = getMenuItemsByCategory(category);
                    for (MenuItem item : items) {
                        loadItemImage(item);
                    }
                }
            }).start();

            // Create category panels
            for (String category : categories) {
                List<MenuItem> items = getMenuItemsByCategory(category);
                JPanel categoryPanel = createCategoryPanel(category, items);
                contentPanel.add(categoryPanel);
                contentPanel.add(Box.createVerticalStrut(30));
            }

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
      
        private JPanel createCategoryPanel(String category, List<MenuItem> items) {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.setBackground(new Color(40, 40, 40));

            // Create header panel
            JPanel headerPanel = new JPanel();
            headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
            headerPanel.setBackground(new Color(40, 40, 40));

            // Map database category to display name
            String displayCategory;
            switch(category) {
                case "DRINK":
                    displayCategory = "Drinks";
                    break;
                case "MEAL":
                    displayCategory = "Meals";
                    break;
                case "MERCHANDISE":
                    displayCategory = "Shop Merchandise";
                    break;
                default:
                    displayCategory = category;
            }

            JLabel categoryLabel = new JLabel(displayCategory);
            categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            categoryLabel.setForeground(new Color(218, 165, 32));
            categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            headerPanel.add(categoryLabel);
            headerPanel.add(Box.createVerticalStrut(10));

            JSeparator separator = new JSeparator();
            separator.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
            separator.setForeground(new Color(70, 70, 70));
            headerPanel.add(separator);
            headerPanel.add(Box.createVerticalStrut(15));

            JPanel itemsGrid = new JPanel(new GridLayout(0, 3, 20, 20));
            itemsGrid.setBackground(new Color(40, 40, 40));

            if (items.isEmpty()) {
                JLabel noItemsLabel = new JLabel("No items available in this category");
                noItemsLabel.setForeground(Color.WHITE);
                noItemsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                itemsGrid.add(noItemsLabel);
            } else {
                for (MenuItem item : items) {
                    JPanel itemPanel = createItemPanel(item);
                    itemsGrid.add(itemPanel);
                }
            }

            panel.add(headerPanel, BorderLayout.NORTH);
            panel.add(itemsGrid, BorderLayout.CENTER);

            return panel;
        }

      private JPanel createItemPanel(MenuItem item) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(50, 50, 50));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Image label - centered
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);

        // Load the image using ImageHandler
        ImageHandler.loadItemImage(item);

        // Set the image or default icon
        if (item.getImageIcon() != null) {
            imageLabel.setIcon(item.getImageIcon());
        } else {
            imageLabel.setIcon(createDefaultIcon(item));
        }        
        // Center the image using GridBagLayout for perfect centering
        JPanel imagePanel = new JPanel(new GridBagLayout());
        imagePanel.setBackground(new Color(50, 50, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        imagePanel.add(imageLabel, gbc);

        // Item info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(50, 50, 50));

        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel("P" + String.format("%.2f", item.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        priceLabel.setForeground(new Color(218, 165, 32));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add description if available
        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            JLabel descLabel = new JLabel("<html><body style='width: 150px'>" + 
                                         item.getDescription() + "</body></html>");
            descLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            descLabel.setForeground(new Color(200, 200, 200));
            descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            infoPanel.add(nameLabel);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(priceLabel);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(descLabel);
        } else {
            infoPanel.add(nameLabel);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(priceLabel);
        }

        // Content panel (image and info)
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(50, 50, 50));
        contentPanel.add(imagePanel, BorderLayout.CENTER);
        contentPanel.add(infoPanel, BorderLayout.SOUTH);

        // Add to cart button
        JButton addButton = new JButton("Add to Cart");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setBackground(new Color(235, 94, 40));
        addButton.setForeground(Color.WHITE);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.setPreferredSize(new Dimension(panel.getWidth(), 40));
        addButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        addButton.setOpaque(true);
        addButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(245, 104, 50), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        addButton.setFocusPainted(false);

        // Hover effects
        addButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addButton.setBackground(new Color(255, 114, 60));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addButton.setBackground(new Color(235, 94, 40));
            }
        });

        addButton.addActionListener(e -> {
            cartManager.addToCart(item, 1);
        });

        // Add components to the item panel
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(addButton, BorderLayout.SOUTH);

        return panel;
    }

        private List<MenuItem> getMenuItemsByCategory(String category) {
            // Check cache first
            if (menuItemCache.containsKey(category)) {
                return menuItemCache.get(category);
            }

            List<MenuItem> items = new ArrayList<>();
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT product_id, name, price, description, image_url FROM products WHERE category = ? AND is_available = 1";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, category);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    items.add(new MenuItem(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("description"),
                        rs.getString("image_url")
                    ));
                }
                menuItemCache.put(category, items); 
            } catch (SQLException e) {
                System.err.println("Error loading menu items: " + e.getMessage());
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
                // Use a BorderLayout for the main panel, allowing scrolling in the center
                JPanel mainRewardsPanel = new JPanel(new BorderLayout());
                mainRewardsPanel.setBackground(new Color(40, 40, 40)); // Dark background

                // Panel to hold all the vertical content sections, placed inside a JScrollPane
                JPanel contentPanel = new JPanel();
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                contentPanel.setBackground(new Color(40, 40, 40));
                contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // Add padding

                // Add section header (centered)
                JLabel headerLabel = new JLabel("Rewards Program");
                headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
                headerLabel.setForeground(Color.WHITE);
                headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the header
                contentPanel.add(headerLabel);
                contentPanel.add(Box.createVerticalStrut(20));

                // User points (centered)
                // Fetch points every time this panel is created or shown to stay updated
                int userPoints = getUserPoints();
                JLabel pointsLabel = new JLabel("Your current points: " + userPoints);
                pointsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                pointsLabel.setForeground(new Color(218, 165, 32)); // Gold color for points
                pointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center points label
                contentPanel.add(pointsLabel);
                contentPanel.add(Box.createVerticalStrut(30));

                // Program description (wrapped in panel, using JLabel+HTML)
                JPanel descPanel = new JPanel();
                descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.Y_AXIS)); // Use BoxLayout internally
                descPanel.setBackground(new Color(50, 50, 50)); // Slightly lighter than background
                descPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                descPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the description panel itself within BoxLayout

                JLabel howItWorksLabel = new JLabel("How It Works:");
                howItWorksLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                howItWorksLabel.setForeground(Color.WHITE);
                howItWorksLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Left align text within descPanel

                // Use JLabel with HTML for better formatting of bullet points
                JLabel descText = new JLabel(
                    "<html><body style='width: 350px;'>" + // Set a preferred max width
                    "<b>•</b> Earn 1 point for every P50 spent<br>" +
                    "<b>•</b> Redeem 100 points for a free regular drink<br>" +
                    "<b>•</b> Redeem 200 points for a free premium drink<br>" +
                    "<b>•</b> Special birthday reward - double points all month!" +
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
                rewardsLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // FIX: Center this label
                contentPanel.add(rewardsLabel);
                contentPanel.add(Box.createVerticalStrut(10));

                JPanel rewardsGridContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
                rewardsGridContainer.setBackground(new Color(40, 40, 40)); // Match background
                rewardsGridContainer.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the container itself

                JPanel rewardsGrid = new JPanel(new GridLayout(0, 2, 15, 15)); // The grid with reward panels
                rewardsGrid.setBackground(new Color(40, 40, 40)); // Match background

                String[][] rewards = {
                    {"50% Off Any Order", "500"}, // Name, Points Cost
                    {"20% Off Any Order", "100"},

                };

                for (String[] reward : rewards) {
                    // Pass the current user points to determine if redeemable
                    JPanel rewardPanel = createRewardPanel(reward[0], Integer.parseInt(reward[1]), userPoints);
                    rewardsGrid.add(rewardPanel);
                }

                rewardsGridContainer.add(rewardsGrid); // Add the grid to its centering container
                contentPanel.add(rewardsGridContainer); // Add the container to the main content BoxLayout

                // Add vertical glue to push content towards the top if the content is short
                contentPanel.add(Box.createVerticalGlue());

                // Put the contentPanel into a JScrollPane
                JScrollPane scrollPane = new JScrollPane(contentPanel);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPane.setBorder(null); // Remove default border
                scrollPane.getViewport().setBackground(new Color(40, 40, 40)); // Match background
                scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Faster scrolling

                // Add the scroll pane to the center of the main rewards panel
                mainRewardsPanel.add(scrollPane, BorderLayout.CENTER);

                // Return the main panel
                return mainRewardsPanel;
            }

        private int getUserPoints() {
            if (cachedUserPoints != null) {
                return cachedUserPoints;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String query = "SELECT points_balance FROM user_rewards WHERE user_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, currentUser.getUserId());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    cachedUserPoints = rs.getInt("points_balance");
                    return cachedUserPoints;
                } else {
                    initializeUserRewards(currentUser.getUserId());
                    cachedUserPoints = 0;
                    return 0;
                }
            } catch (SQLException e) {
                System.err.println("Error getting user points: " + e.getMessage());
                return 0;
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

        private JPanel createRewardPanel(String rewardName, int pointsCost, int userPoints) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(50, 50, 50)); // Slightly lighter background
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));

            JLabel nameLabel = new JLabel(rewardName);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nameLabel.setForeground(Color.WHITE);

            JLabel pointsLabel = new JLabel(pointsCost + " points");
            pointsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            pointsLabel.setForeground(new Color(218, 165, 32)); // Gold color for points

            JButton redeemButton = new JButton("Redeem");
            redeemButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            redeemButton.setBackground(new Color(218, 165, 32)); // Gold button
            redeemButton.setForeground(Color.BLACK);
            redeemButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 215, 0), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            redeemButton.setFocusPainted(false);
            redeemButton.setEnabled(userPoints >= pointsCost);

            // Check if this reward has already been redeemed by the user
            boolean isRedeemed = checkIfRewardRedeemed(currentUser.getUserId(), rewardName);
            String redemptionCode = getRedemptionCode(currentUser.getUserId(), rewardName);

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(new Color(50, 50, 50));
            infoPanel.add(nameLabel);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(pointsLabel);

            if (isRedeemed) {
                // If redeemed, show the code and expiration
                JLabel redeemedLabel = new JLabel("Redeemed! Code: " + redemptionCode);
                redeemedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                redeemedLabel.setForeground(new Color(0, 200, 0)); // Green color
                infoPanel.add(redeemedLabel);

                // Disable the redeem button
                redeemButton.setEnabled(false);
                redeemButton.setBackground(new Color(100, 100, 100));
                redeemButton.setForeground(new Color(150, 150, 150));
                redeemButton.setText("Already Redeemed");
            } else {
                // Original button behavior for non-redeemed rewards
                if (!redeemButton.isEnabled()) {
                    redeemButton.setToolTipText("Not enough points");
                    redeemButton.setBackground(new Color(100, 100, 100)); // Disabled state
                    redeemButton.setForeground(new Color(150, 150, 150));
                } else {
                    // Hover effects (only for enabled buttons)
                    redeemButton.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseEntered(java.awt.event.MouseEvent evt) {
                            if (redeemButton.isEnabled()) {
                                redeemButton.setBackground(new Color(255, 215, 0)); // Brighter gold on hover
                            }
                        }
                        public void mouseExited(java.awt.event.MouseEvent evt) {
                            if (redeemButton.isEnabled()) {
                                redeemButton.setBackground(new Color(218, 165, 32)); // Back to normal gold
                            }
                        }
                    });
                }

                redeemButton.addActionListener(e -> {
                    int option = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to redeem " + rewardName + " for " + pointsCost + " points?",
                        "Confirm Redemption",
                        JOptionPane.YES_NO_OPTION);

                    if (option == JOptionPane.YES_OPTION) {
                        try {
                            if (userPoints < pointsCost) {
                                JOptionPane.showMessageDialog(this,
                                    "You don't have enough points for this reward.",
                                    "Insufficient Points",
                                    JOptionPane.WARNING_MESSAGE);
                                return;
                            }

                            int rewardId = getRewardIdByName(rewardName);
                            if (rewardId == -1) {
                                throw new Exception("Reward not found in database");
                            }

                            // Generate code
                            String rewardCode = generateRewardCode();

                            // Record the redemption with code
                            recordRedemption(currentUser.getUserId(), rewardId, rewardCode);

                            // Deduct points
                            updateUserPoints(currentUser.getUserId(), -pointsCost);

                            JOptionPane.showMessageDialog(this,
                                "You have successfully redeemed " + rewardName + ".\n" +
                                "Your reward code: " + rewardCode + "\n" +
                                "Enter this code in the checkout page to apply your discount.",
                                "Redemption Successful",
                                JOptionPane.INFORMATION_MESSAGE);

                            cardLayout.show(cardPanel, "rewards");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this,
                                "Error redeeming reward: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            }

            panel.add(infoPanel, BorderLayout.CENTER);
            panel.add(redeemButton, BorderLayout.EAST);

            // Add hover effect to the entire panel
            panel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    panel.setBackground(new Color(60, 60, 60));
                    panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(218, 165, 32), 1),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                    ));
                    infoPanel.setBackground(new Color(60, 60, 60));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    panel.setBackground(new Color(50, 50, 50));
                    panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                    ));
                    infoPanel.setBackground(new Color(50, 50, 50));
                }
            });

            return panel;
        }

        private boolean checkIfRewardRedeemed(int userId, String rewardName) {
            try {
                Connection conn = DBConnection.getConnection();
                String query = "SELECT rr.* FROM reward_redemptions rr " +
                              "JOIN rewards r ON rr.reward_id = r.reward_id " +
                              "WHERE rr.user_id = ? AND r.name = ? AND rr.expires_at > CURRENT_TIMESTAMP";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.setString(2, rewardName);
                ResultSet rs = stmt.executeQuery();
                return rs.next(); // Returns true if there's an active redemption
            } catch (SQLException e) {
                System.err.println("Error checking reward redemption: " + e.getMessage());
                return false;
            }
        }

        private String getRedemptionCode(int userId, String rewardName) {
            try {
                Connection conn = DBConnection.getConnection();
                String query = "SELECT rr.code FROM reward_redemptions rr " +
                              "JOIN rewards r ON rr.reward_id = r.reward_id " +
                              "WHERE rr.user_id = ? AND r.name = ? AND rr.expires_at > CURRENT_TIMESTAMP";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.setString(2, rewardName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("code");
                }
                return null;
            } catch (SQLException e) {
                System.err.println("Error getting redemption code: " + e.getMessage());
                return null;
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

        private void recordRedemption(int userId, int rewardId, String code) throws SQLException {
            Connection conn = DBConnection.getConnection();

            // Calculate expiration date (5 days from now)
            java.util.Date now = new java.util.Date();
            java.util.Date expiresAt = new java.util.Date(now.getTime() + (5 * 24 * 60 * 60 * 1000));
            java.sql.Timestamp expiresAtSql = new java.sql.Timestamp(expiresAt.getTime());

            // First get the points cost and discount amount for this reward
            String rewardQuery = "SELECT points_cost, discount_amount FROM rewards WHERE reward_id = ?";
            PreparedStatement rewardStmt = conn.prepareStatement(rewardQuery);
            rewardStmt.setInt(1, rewardId);
            ResultSet rs = rewardStmt.executeQuery();

            if (rs.next()) {
                int pointsUsed = rs.getInt("points_cost");
                double discountAmount = rs.getDouble("discount_amount");

                // Then insert into redemptions with all required fields
                String query = "INSERT INTO reward_redemptions (user_id, reward_id, points_used, discount_amount, code, redemption_date, expires_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.setInt(2, rewardId);
                stmt.setInt(3, pointsUsed);
                stmt.setDouble(4, discountAmount);
                stmt.setString(5, code);
                stmt.setTimestamp(6, expiresAtSql);
                stmt.executeUpdate();
            }
        }

        private void updateUserPoints(int userId, int pointsChange) throws SQLException {
            Connection conn = DBConnection.getConnection();
            String query = "UPDATE user_rewards SET points_balance = points_balance + ? WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, pointsChange);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
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
    }