package com.example.ui;

import com.example.dao.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    /** Viền ô nhập — xám rất nhạt */
    private static final Color FIELD_BORDER = new Color(226, 232, 240);
    /** Phụ đề */
    private static final Color SUBTITLE = new Color(100, 116, 139);
    /** Nhãn trường — xám đậm, semi-bold */
    private static final Color LABEL = new Color(71, 85, 105);

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();

        setTitle("Hệ Thống Xét Tuyển");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(UiFrameDefaults.MAIN_SIZE);
        setMinimumSize(UiFrameDefaults.MAIN_MIN);
        setLocationRelativeTo(null);

        UiShellTheme.ShellGradientPanel root = new UiShellTheme.ShellGradientPanel();
        root.setLayout(new GridBagLayout());
        setContentPane(root);

        UiShellTheme.LoginCardPanel card = new UiShellTheme.LoginCardPanel(new BorderLayout());
        card.setBorder(new EmptyBorder(40, 44, 40, 44));

        JLabel title = new JLabel("Hệ Thống Xét Tuyển", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 25f));
        title.setForeground(Color.BLACK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Đăng nhập tài khoản quản trị / thí sinh", SwingConstants.CENTER);
        sub.setForeground(SUBTITLE);
        sub.setFont(sub.getFont().deriveFont(Font.PLAIN, 14f));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.add(title);
        head.add(Box.createVerticalStrut(12));
        head.add(sub);

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 0, 6, 0);

        JLabel lUser = new JLabel("Tài khoản");
        lUser.setForeground(LABEL);
        lUser.setFont(lUser.getFont().deriveFont(Font.BOLD, 12f));
        body.add(lUser, gc);

        gc.gridy++;
        txtUsername = new JTextField();
        styleField(txtUsername);
        body.add(txtUsername, gc);

        gc.gridy++;
        gc.insets = new Insets(20, 0, 6, 0);
        JLabel lPass = new JLabel("Mật khẩu");
        lPass.setForeground(LABEL);
        lPass.setFont(lPass.getFont().deriveFont(Font.BOLD, 12f));
        body.add(lPass, gc);

        gc.gridy++;
        gc.insets = new Insets(0, 0, 28, 0);
        txtPassword = new JPasswordField();
        styleField(txtPassword);
        body.add(txtPassword, gc);

        gc.gridy++;
        gc.insets = new Insets(0, 0, 12, 0);
        btnLogin = new JButton("Đăng nhập");
        UiButtons.stylePrimary(btnLogin);
        body.add(btnLogin, gc);

        gc.gridy++;
        gc.insets = new Insets(0, 0, 0, 0);
        JButton btnTraCuuNhanh = new JButton("Tra cứu nhanh");
        UiButtons.styleSecondary(btnTraCuuNhanh);
        body.add(btnTraCuuNhanh, gc);

        card.add(head, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(44, 52, 44, 52);
        root.add(card, c);
        card.setPreferredSize(UiFrameDefaults.LOGIN_CARD);

        btnLogin.addActionListener(e -> handleLogin());
        btnTraCuuNhanh.addActionListener(e -> {
            JFrame searchFrame = new JFrame("Tra Cứu Kết Quả Tuyển Sinh");
            searchFrame.setSize(UiFrameDefaults.MAIN_SIZE);
            searchFrame.setMinimumSize(UiFrameDefaults.MAIN_MIN);
            searchFrame.setLocationRelativeTo(this);
            UiShellTheme.ShellGradientPanel searchRoot = new UiShellTheme.ShellGradientPanel(true);
            searchRoot.setLayout(new BorderLayout());

            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.setBorder(new EmptyBorder(56, 56, 56, 56));

            JLabel searchTitle = TraCuuPanel.buildSearchTitleLabel();
            searchTitle.setBorder(new EmptyBorder(0, 0, 14, 0));
            wrapper.add(searchTitle, BorderLayout.NORTH);

            UiShellTheme.RoundedCardPanel searchCard = new UiShellTheme.RoundedCardPanel(new BorderLayout());
            searchCard.setBorder(new EmptyBorder(18, 18, 18, 18));
            searchCard.add(new TraCuuPanel(), BorderLayout.CENTER);
            wrapper.add(searchCard, BorderLayout.CENTER);

            searchRoot.add(wrapper, BorderLayout.CENTER);
            searchFrame.setContentPane(searchRoot);
            searchFrame.setVisible(true);
        });
    }

    private static void styleField(JComponent field) {
        field.setFont(field.getFont().deriveFont(14f));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
                new EmptyBorder(11, 13, 11, 13)));
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tài khoản và mật khẩu!");
            return;
        }

        Object result = userDAO.authenticateUser(username, password);

        if (result != null) {
            this.dispose();

            if (result instanceof com.example.entity.User) {
                com.example.entity.User admin = (com.example.entity.User) result;
                // JOptionPane.showMessageDialog(null, "Đăng nhập ADMIN thành công!");
                new AdminFrame(admin).setVisible(true);
            } else if (result instanceof com.example.entity.ThiSinh) {
                com.example.entity.ThiSinh ts = (com.example.entity.ThiSinh) result;
                JOptionPane.showMessageDialog(null, "Chào thí sinh: " + ts.getHo() + " " + ts.getTen());
                new UserFrame(ts).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Tài khoản hoặc mật khẩu không chính xác!");
        }
    }
}
