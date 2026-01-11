package SouceCode;

import javax.swing.*;
import java.awt.*;

public class JPanelHelpGuide extends JPanel {

    public JPanelHelpGuide() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ===== TITLE =====
        JLabel lblTitle = new JLabel("HƯỚNG DẪN SỬ DỤNG HỆ THỐNG BÁN HÀNG");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblTitle.setForeground(new Color(255, 99, 71));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // ===== NỘI DUNG HƯỚNG DẪN =====
        JTextArea txtGuide = new JTextArea();
        txtGuide.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtGuide.setForeground(new Color(50, 50, 50));
        txtGuide.setLineWrap(true);
        txtGuide.setWrapStyleWord(true);
        txtGuide.setEditable(false);
        txtGuide.setBackground(Color.WHITE);
        txtGuide.setMargin(new Insets(20, 20, 20, 20));

        String guideText = """
            CHÀO MỪNG BẠN ĐẾN VỚI HỆ THỐNG QUẢN LÝ BÁN HÀNG!

            Dưới đây là hướng dẫn chi tiết các chức năng chính:

            1. TẠO HÓA ĐƠN MỚI (Sales Dashboard)
               • Nhấn nút "Add Product" để chọn sản phẩm và số lượng.
               • Nhập thông tin khách hàng (tên + số điện thoại) lần đầu.
               • Có thể chọn mã giảm giá (Coupon) nếu có.
               • Kiểm tra tổng tiền → Nhấn "Save Bill" để lưu hóa đơn.
               • Hệ thống tự động trừ tồn kho và lưu thông tin khách hàng.

            2. QUẢN LÝ HÓA ĐƠN (Manage Bills)
               • Xem danh sách tất cả hóa đơn đã tạo.
               • Tìm kiếm nhanh theo mã bill.
               • Nhấn "View Details" để xem chi tiết sản phẩm trong hóa đơn.
               • Nhấn "Update Status" để thay đổi trạng thái (ví dụ: Đã thanh toán, Đang xử lý...).

            3. QUẢN LÝ KHÁCH HÀNG (Customer Management)
               • Xem, thêm, sửa, xóa thông tin khách hàng.
               • Click phải vào khách hàng → chọn "View Bills" để xem tất cả hóa đơn của khách đó.
               • Click vào hóa đơn → xem chi tiết sản phẩm đã mua.

            4. TRẢ HÀNG (Return Goods)
               • Mở chi tiết hóa đơn (từ Manage Bills hoặc Customer Management).
               • Nhấn nút "RETURN GOODS".
               • Chọn sản phẩm cần trả → nhập số lượng và lý do.
               • Hệ thống tự động:
                  – Cộng lại số lượng vào tồn kho
                  – Cập nhật lại tổng tiền hóa đơn
                  – Lưu lịch sử trả hàng

            LƯU Ý QUAN TRỌNG:
               • Luôn kiểm tra tồn kho trước khi thêm sản phẩm.
               • Nhập đầy đủ thông tin khách hàng để dễ quản lý sau này.
               • Khi trả hàng phải nhập lý do rõ ràng.
               • Nếu gặp lỗi, liên hệ quản trị viên hệ thống.

            Chúc bạn làm việc hiệu quả và phục vụ khách hàng tốt nhất! 
            """;

        txtGuide.setText(guideText);

        JScrollPane scrollPane = new JScrollPane(txtGuide);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(247, 222, 155), 3));
        add(scrollPane, BorderLayout.CENTER);

        // ===== FOOTER =====
        JLabel lblFooter = new JLabel("Hệ thống quản lý bán hàng - Phiên bản 1.0");
        lblFooter.setFont(new Font("SansSerif", Font.ITALIC, 16));
        lblFooter.setForeground(Color.GRAY);
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblFooter, BorderLayout.SOUTH);
    }
}