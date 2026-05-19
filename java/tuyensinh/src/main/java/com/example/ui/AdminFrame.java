package com.example.ui;

import com.example.entity.User;
import com.example.ui.UiShellTheme.RoundedCardPanel;
import com.example.ui.UiShellTheme.ShellGradientPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AdminFrame extends JFrame {
    private final User loggedInUser;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel deck = new JPanel(cardLayout);

    public AdminFrame(User user) {
        this.loggedInUser = user;

        setTitle("Hệ Thống Quản Lý Tuyển Sinh - Xin chào: " + loggedInUser.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(UiFrameDefaults.MAIN_SIZE);
        setLocationRelativeTo(null);
        setMinimumSize(UiFrameDefaults.MAIN_MIN);

        ShellGradientPanel root = new ShellGradientPanel(true);
        root.setLayout(new BorderLayout());
        setContentPane(root);

        int gap = 20;
        int outer = 28;
        JPanel mainRow = new JPanel(new BorderLayout(gap, 0));
        mainRow.setOpaque(false);
        mainRow.setBorder(new EmptyBorder(outer, outer, outer, outer));

        RoundedCardPanel sidebarCard = new RoundedCardPanel(new BorderLayout());
        sidebarCard.setBorder(new EmptyBorder(18, 16, 18, 16));
        sidebarCard.setPreferredSize(new Dimension(280, 0));

        JPanel sideCanvas = new JPanel(new BorderLayout(0, 6));
        sideCanvas.setOpaque(true);
        sideCanvas.setBackground(SidebarUi.SIDEBAR_CANVAS);

        List<String> labels = new ArrayList<>();
        List<String> icons = new ArrayList<>();
        List<String> cardIds = new ArrayList<>();

        boolean isAdmin = "admin".equals(loggedInUser.getRole());
        if (isAdmin) {
            addNav(deck, labels, icons, cardIds, new UserPanel(), "users", "Quản lý Người dùng", "👥");
            addNav(deck, labels, icons, cardIds, new NguyenVongPanel(), "nv", "Xét tuyển & Nguyện vọng", "📋");
        }

        addNav(deck, labels, icons, cardIds, new ToHopPanel(), "th", "Quản lý Tổ hợp môn", "🔢");
        addNav(deck, labels, icons, cardIds, new NganhPanel(), "ng", "Quản lý Ngành", "🏫");
        addNav(deck, labels, icons, cardIds, new NganhToHopPanel(), "nth", "Quản lý Ngành - Tổ hợp", "🔗");
        addNav(deck, labels, icons, cardIds, new ThiSinhPanel(), "ts", "Quản lý Thí sinh", "👤");
        addNav(deck, labels, icons, cardIds, new BangQuyDoiPanel(), "bqd", "Quản lý Bảng quy đổi", "📊");
        addNav(deck, labels, icons, cardIds, new DiemThiPanel(), "dt", "Quản lý Điểm thi", "📝");
        addNav(deck, labels, icons, cardIds, new DiemXetTuyenPanel(), "dxt", "Điểm xét tuyển", "📈");
        addNav(deck, labels, icons, cardIds, new DiemCongPanel(), "dc", "Quản lý Điểm cộng", "⭐");

        JButton btnLogout = SidebarUi.createOutlineLogoutButton();
        sideCanvas.add(
                SidebarUi.buildCollapsibleNavBlock(sidebarCard, labels, icons,
                        i -> cardLayout.show(deck, cardIds.get(i)), btnLogout),
                BorderLayout.CENTER);

        sidebarCard.add(sideCanvas, BorderLayout.CENTER);
        sidebarCard.add(btnLogout, BorderLayout.SOUTH);

        RoundedCardPanel contentCard = new RoundedCardPanel(new BorderLayout());
        contentCard.setBorder(new EmptyBorder(22, 22, 22, 22));
        contentCard.add(deck, BorderLayout.CENTER);

        mainRow.add(sidebarCard, BorderLayout.WEST);
        mainRow.add(contentCard, BorderLayout.CENTER);
        root.add(mainRow, BorderLayout.CENTER);

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn đăng xuất?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            }
        });
    }

    private static void addNav(JPanel deck, List<String> labels, List<String> icons, List<String> cardIds,
                               JPanel panel, String id, String label, String iconEmoji) {
        deck.add(panel, id);
        labels.add(label);
        icons.add(iconEmoji);
        cardIds.add(id);
    }
}
