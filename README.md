# Mobile Ticket App
*Android Java Application with Firebase Backend*

## Giới thiệu
Ứng dụng Đặt vé xem phim được phát triển với Giao diện (UI) Dark Mode đậm chất Cinematic (Rạp chiếu phim), sử dụng toàn bộ dịch vụ của Firebase làm Backend.

## Tính năng chính
1. **Quản lý Tài khoản (Firebase Authentication)**
   - Đăng nhập (Login).
   - Đăng ký (Register).
2. **Trang chủ (Firestore)**
   - Hiển thị danh sách các phim đang chiếu dạng Grid.
   - Tự động sinh dữ liệu ảo (Mock Data) nếu Database Movies trống.
3. **Chi tiết phim & Suất chiếu**
   - Xem thông tin chi tiết phim (Poster, Synopsis, Release Date).
   - Chọn suất chiếu phim từ các Rạp (Theaters).
4. **Chọn Ghế & Thanh toán**
   - Chọn các ghế ngồi tương tác trên sơ đồ Rạp chiếu.
   - Các trạng thái ghế: Available (Trống), Selected (Đang chọn), Booked (Đã đặt).
   - Tính tổng tiền (Total Price) và Book vé lưu lên Firestore.
5. **Vé của Tôi (My Tickets)**
   - Xem toàn bộ danh sách các vé đã đặt thành công cho tài khoản hiện đang đăng nhập.

## Kiến trúc Database (Firestore)
- **`users`**: Chứa thông tin bổ sung của User (Tên, Email).
- **`movies`**: Thông tin phim.
- **`showtimes`**: Lịch chiếu phim.
- **`tickets`**: Lịch sử đặt vé.

## Yêu cầu môi trường
- Android Studio Iguana / Jellyfish (hoặc bất kỳ bản nào hỗ trợ Gradle 8.2).
- **Đã tích hợp file `google-services.json`** tại thư mục `app/google-services.json`.

## ẢNH GIAO DIỆN ĐÃ LÀM ĐƯỢC
- Giao diện đăng ký
![alt text](Screenshot/Screenshot_2026-04-10-11-17-58-039_com.example.mobileticketapp.jpg)
- Giao diện đăng nhập
![alt text](Screenshot/Screenshot_2026-04-10-11-18-11-574_com.example.mobileticketapp.jpg)
- Giao diện trang chủ
![alt text](Screenshot/Screenshot_2026-04-10-11-18-15-221_com.example.mobileticketapp.jpg)
- Giao diện chi tiết phim
![alt text](Screenshot/Screenshot_2026-04-10-11-18-37-600_com.example.mobileticketapp.jpg)
- Giao diện chọn ghế
![alt text](Screenshot/Screenshot_2026-04-10-11-18-40-453_com.example.mobileticketapp.jpg)
- Giao diện vé của tôi
![alt text](Screenshot/Screenshot_2026-04-10-11-19-17-585_com.example.mobileticketapp.jpg)
- Thông báo hệ thống sắp đến giờ chiếu
![alt text](Screenshot/Screenshot_2026-04-10-11-19-11-061_com.example.mobileticketapp.jpg)