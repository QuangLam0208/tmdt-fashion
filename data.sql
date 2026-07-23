SET FOREIGN_KEY_CHECKS = 0;

-- Xóa dữ liệu cũ
TRUNCATE TABLE addresses;
TRUNCATE TABLE cart_items;
TRUNCATE TABLE categories;
TRUNCATE TABLE coupons;
TRUNCATE TABLE email_logs;
TRUNCATE TABLE notifications;
TRUNCATE TABLE order_histories;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE otps;
TRUNCATE TABLE password_reset_tokens;
TRUNCATE TABLE product_images;
TRUNCATE TABLE product_variants;
TRUNCATE TABLE products;
TRUNCATE TABLE recently_viewed_items;
TRUNCATE TABLE refresh_tokens;
TRUNCATE TABLE return_request_images;
TRUNCATE TABLE return_requests;
TRUNCATE TABLE review_images;
TRUNCATE TABLE reviews;
TRUNCATE TABLE tokens;
TRUNCATE TABLE user_coupons;
TRUNCATE TABLE users;
TRUNCATE TABLE wishlist_items;

-- ========================================================
-- 1. USERS (40 dòng, id 1-40 liền mạch) -- pass admin+cus: 123456
-- ========================================================
INSERT INTO `users` (`user_id`, `email`, `full_name`, `password`, `phone`, `role`, `status`, `email_verified`, `two_factor_enabled`) VALUES
(1, 'admin@fashion.com', 'Admin', '$2a$12$Q7WrBwIlYS8C24yRPtw8R.f3EIthr39JuGpyD16PSMyPL75cZYT16', '0900009001', 'ADMIN', 'ACTIVE', 1, 0),
(2, 'admin2@fashion.com', 'Admin Quản Lý', '$2a$10$hashed2', '0900000002', 'ADMIN', 'ACTIVE', 1, 0),
(3, 'customer@gmail.com', 'Nguyễn Trọng A', '$2a$10$wY1txQsroSlcdRys6q.CQuDk9Y4K4nJk6zX4/zH5J4g5r4p/K3zI6', '0900000003', 'CUSTOMER', 'ACTIVE', 1, 0),
(4, 'khach04@gmail.com', 'Lê Thị B', '$2a$10$hashed4', '0900000004', 'CUSTOMER', 'ACTIVE', 1, 0),
(5, 'khach05@gmail.com', 'Trần Văn C', '$2a$10$hashed5', '0900000005', 'CUSTOMER', 'ACTIVE', 1, 0),
(6, 'khach06@gmail.com', 'Phạm Quỳnh D', '$2a$10$hashed6', '0900000006', 'CUSTOMER', 'ACTIVE', 1, 0),
(7, 'khach07@gmail.com', 'Hoàng Bảo E', '$2a$10$hashed7', '0900000007', 'CUSTOMER', 'BLOCKED', 1, 0),
(8, 'khach08@gmail.com', 'Vũ Đức F', '$2a$10$hashed8', '0900000008', 'CUSTOMER', 'ACTIVE', 1, 0),
(9, 'khach09@gmail.com', 'Đặng Ngọc G', '$2a$10$hashed9', '0900000009', 'CUSTOMER', 'PENDING', 0, 0),
(10, 'khach10@gmail.com', 'Bùi Xuân H', '$2a$10$hashed10', '0900000010', 'CUSTOMER', 'ACTIVE', 1, 0),
(11, 'khach11@gmail.com', 'Đỗ Minh I', '$2a$10$hashed11', '0900000011', 'CUSTOMER', 'ACTIVE', 1, 0),
(12, 'khach12@gmail.com', 'Hồ Thúy J', '$2a$10$hashed12', '0900000012', 'CUSTOMER', 'ACTIVE', 1, 0),
(13, 'khach13@gmail.com', 'Ngô Kiến K', '$2a$10$hashed13', '0900000013', 'CUSTOMER', 'ACTIVE', 1, 0),
(14, 'khach14@gmail.com', 'Dương Thảo L', '$2a$10$hashed14', '0900000014', 'CUSTOMER', 'ACTIVE', 1, 0),
(15, 'khach15@gmail.com', 'Lý Hải M', '$2a$10$hashed15', '0900000015', 'CUSTOMER', 'ACTIVE', 1, 0),
(16, 'khach16@gmail.com', 'Vương Phi N', '$2a$10$hashed16', '0900000016', 'CUSTOMER', 'BLOCKED', 1, 0),
(17, 'khach17@gmail.com', 'Đinh Tiến O', '$2a$10$hashed17', '0900000017', 'CUSTOMER', 'ACTIVE', 1, 0),
(18, 'khach18@gmail.com', 'Lâm Tâm P', '$2a$10$hashed18', '0900000018', 'CUSTOMER', 'PENDING', 0, 0),
(19, 'khach19@gmail.com', 'Trương Quốc Q', '$2a$10$hashed19', '0900000019', 'CUSTOMER', 'ACTIVE', 1, 0),
(20, 'khach20@gmail.com', 'Phan Thanh R', '$2a$10$hashed20', '0900000020', 'CUSTOMER', 'ACTIVE', 1, 0),
(21, 'khach21@gmail.com', 'Mai Phương S', '$2a$10$hashed21', '0900000021', 'CUSTOMER', 'ACTIVE', 1, 0),
(22, 'khach22@gmail.com', 'Đào Trọng T', '$2a$10$hashed22', '0900000022', 'CUSTOMER', 'ACTIVE', 1, 0),
(23, 'khach23@gmail.com', 'Tô Lan U', '$2a$10$hashed23', '0900000023', 'CUSTOMER', 'ACTIVE', 1, 0),
(24, 'khach24@gmail.com', 'Thái Hà V', '$2a$10$hashed24', '0900000024', 'CUSTOMER', 'BLOCKED', 0, 0),
(25, 'khach25@gmail.com', 'Châu Lệ W', '$2a$10$hashed25', '0900000025', 'CUSTOMER', 'ACTIVE', 1, 0),
(26, 'khach26@gmail.com', 'Cao Đạt X', '$2a$10$hashed26', '0900000026', 'CUSTOMER', 'ACTIVE', 1, 0),
(27, 'khach27@gmail.com', 'Lư Tấn Y', '$2a$10$hashed27', '0900000027', 'CUSTOMER', 'ACTIVE', 1, 0),
(28, 'khach28@gmail.com', 'Bạch Ngọc Z', '$2a$10$hashed28', '0900000028', 'CUSTOMER', 'PENDING', 0, 0),
(29, 'khach29@gmail.com', 'Tôn Thất A1', '$2a$10$hashed29', '0900000029', 'CUSTOMER', 'ACTIVE', 1, 0),
(30, 'khach30@gmail.com', 'Giang Di B2', '$2a$10$hashed30', '0900000030', 'CUSTOMER', 'ACTIVE', 1, 0),
(31, 'cus@fashion.com', 'Customer', '$2a$12$eRlsAFa/zm9anUdsZUGozu1PtfkPm0hRXeJWJ2za7x8OfgnPMthxq', '0900123001', 'CUSTOMER', 'ACTIVE', 1, 0),
(32, 'khach41@gmail.com', 'Hoàng Thái Bình', '$2a$10$wY1txQsroSlcdRys6q.CQuDk9Y4K4nJk6zX4/zH5J4g5r4p/K3zI6', '0901000041', 'CUSTOMER', 'ACTIVE', 1, 0),
(33, 'khach42@gmail.com', 'Lý Thu Thảo', '$2a$10$wY1txQsroSlcdRys6q.CQuDk9Y4K4nJk6zX4/zH5J4g5r4p/K3zI6', '0901000042', 'CUSTOMER', 'ACTIVE', 1, 0),
(34, 'khach43@gmail.com', 'Trần Hữu Khang', '$2a$10$wY1txQsroSlcdRys6q.CQuDk9Y4K4nJk6zX4/zH5J4g5r4p/K3zI6', '0901000043', 'CUSTOMER', 'BLOCKED', 1, 0),
(35, 'khach44@gmail.com', 'Phạm Trà My', '$2a$10$wY1txQsroSlcdRys6q.CQuDk9Y4K4nJk6zX4/zH5J4g5r4p/K3zI6', '0901000044', 'CUSTOMER', 'ACTIVE', 1, 0),
(36, 'khach45@gmail.com', 'Nguyễn Tiến Dũng', '$2a$10$wY1txQsroSlcdRys6q.CQuDk9Y4K4nJk6zX4/zH5J4g5r4p/K3zI6', '0901000045', 'CUSTOMER', 'ACTIVE', 1, 0),
(37, 'khach46@gmail.com', 'Vũ Tuấn Anh', '$2a$10$wY1txQsroSlcdRys6q.CQuDk9Y4K4nJk6zX4/zH5J4g5r4p/K3zI6', '0901000046', 'CUSTOMER', 'PENDING', 0, 0),
(38, 'khach47@gmail.com', 'Đinh Thanh Tùng', '$2a$10$wY1txQsroSlcdRys6q.CQuDk9Y4K4nJk6zX4/zH5J4g5r4p/K3zI6', '0901000047', 'CUSTOMER', 'ACTIVE', 1, 0),
(39, 'khach48@gmail.com', 'Lê Hoài Thu', '$2a$10$wY1txQsroSlcdRys6q.CQuDk9Y4K4nJk6zX4/zH5J4g5r4p/K3zI6', '0901000048', 'CUSTOMER', 'ACTIVE', 1, 0),
(40, 'khach49@gmail.com', 'Ngô Bá Khá', '$2a$10$wY1txQsroSlcdRys6q.CQuDk9Y4K4nJk6zX4/zH5J4g5r4p/K3zI6', '0901000049', 'CUSTOMER', 'ACTIVE', 1, 0);

-- ========================================================
-- 2. CATEGORIES (12 danh mục)
-- ========================================================
INSERT INTO `categories` (`category_id`, `parent_id`, `name`) VALUES
(1, NULL, 'Thời Trang Nam'),
(2, NULL, 'Thời Trang Nữ'),
(3, NULL, 'Phụ Kiện'),
(4, 1, 'Áo Nam'),
(5, 1, 'Quần Nam'),
(6, 2, 'Áo Nữ'),
(7, 2, 'Chân Váy & Đầm'),
(8, 2, 'Quần Nữ'),
(9, 3, 'Túi Xách & Balo'),
(10, 3, 'Mắt Kính'),
(11, 3, 'Trang Sức'),
(12, 1, 'Giày Dép Nam');

-- ========================================================
-- 3. PRODUCTS (70 sản phẩm, id 1-70 liền mạch)
-- ========================================================
INSERT INTO `products` (`product_id`, `category_id`, `name`, `description`, `status`) VALUES
(1, 4, 'Áo Thun Basic Nam', 'Áo thun cotton trơn đơn giản.', 'ACTIVE'),
(2, 4, 'Áo Sơ Mi Kẻ Sọc', 'Áo sơ mi dài tay họa tiết kẻ sọc.', 'ACTIVE'),
(3, 4, 'Áo Khoác Bomber', 'Áo khoác gió thời trang nam.', 'ACTIVE'),
(4, 5, 'Quần Jean Rách Gối', 'Quần jean slimfit phong cách trẻ trung.', 'ACTIVE'),
(5, 5, 'Quần Tây Ống Suông', 'Quần âu thanh lịch công sở.', 'ACTIVE'),
(6, 5, 'Quần Short Kaki', 'Quần đùi đi chơi, mặc nhà thoải mái.', 'ACTIVE'),
(7, 6, 'Áo Trễ Vai Nữ Tính', 'Áo kiểu trễ vai điệu đà.', 'ACTIVE'),
(8, 6, 'Áo Sơ Mi Lụa', 'Chất liệu lụa satin mềm mịn.', 'ACTIVE'),
(9, 6, 'Áo Croptop Năng Động', 'Áo thun croptop in chữ.', 'ACTIVE'),
(10, 7, 'Chân Váy Xếp Ly', 'Chân váy dáng ngắn xòe.', 'ACTIVE'),
(11, 7, 'Đầm Body Dạ Hội', 'Đầm dự tiệc ôm sát quyến rũ.', 'ACTIVE'),
(12, 7, 'Đầm Maxi Đi Biển', 'Váy dài qua gót họa tiết hoa.', 'ACTIVE'),
(13, 8, 'Quần Ống Rộng Nữ', 'Quần culottes cạp cao.', 'ACTIVE'),
(14, 8, 'Quần Jean Skinny Nữ', 'Quần bó tôn dáng co giãn tốt.', 'OUT_OF_STOCK'),
(15, 9, 'Túi Xách Da Đeo Chéo', 'Túi nữ da PU form hộp.', 'ACTIVE'),
(16, 9, 'Balo Laptop 15inch', 'Balo chống sốc chống nước.', 'ACTIVE'),
(17, 10, 'Mắt Kính Chống Cận', 'Gọng kính titan siêu nhẹ.', 'ACTIVE'),
(18, 10, 'Kính Râm Thời Trang', 'Kính đi nắng chống UV400.', 'ACTIVE'),
(19, 11, 'Dây Chuyền Bạc 925', 'Dây chuyền nữ mặt hoa tuyết.', 'ACTIVE'),
(20, 11, 'Vòng Tay Phong Thủy', 'Vòng đá thạch anh mix charm.', 'ACTIVE'),
(21, 12, 'Giày Sneaker Trắng', 'Giày thể thao basic dễ phối.', 'ACTIVE'),
(22, 12, 'Giày Tây Nam Da Bò', 'Giày oxford công sở.', 'DISCONTINUED'),
(23, 4, 'Áo Len Cổ Lọ Nam', 'Áo dệt kim ấm áp mùa đông.', 'ACTIVE'),
(24, 4, 'Áo Polo Thể Thao', 'Chất vải thể thao thấm mồ hôi.', 'ACTIVE'),
(25, 5, 'Quần Jogger Túi Hộp', 'Quần phong cách streetwear.', 'ACTIVE'),
(26, 6, 'Áo Blazer Nữ', 'Áo khoác blazer thanh lịch.', 'ACTIVE'),
(27, 7, 'Chân Váy Bút Chì', 'Dành cho môi trường văn phòng.', 'ACTIVE'),
(28, 9, 'Túi Tote Canvas', 'Túi vải thân thiện môi trường.', 'ACTIVE'),
(29, 12, 'Dép Quai Hậu Nam', 'Dép sandal đi học, đi chơi.', 'ACTIVE'),
(30, 4, 'Áo Thun In Phản Quang', 'Áo thun form rộng unisex.', 'ACTIVE'),
(31, 6, 'Áo Thun Cổ Tim Nữ', 'Áo thun nữ dáng ôm nhẹ nhàng.', 'ACTIVE'),
(32, 7, 'Váy Chữ A Điệu Đà', 'Váy thiết kế dáng chữ A dễ mặc.', 'ACTIVE'),
(33, 9, 'Túi Trống Thể Thao', 'Túi đeo chéo đựng đồ tập gym, đá bóng.', 'ACTIVE'),
(34, 12, 'Giày Lười Nam Moca', 'Giày da bò nguyên tấm êm ái.', 'ACTIVE'),
(35, 5, 'Quần Đùi Thể Thao Nam', 'Quần đùi vải dù thoáng mát.', 'ACTIVE'),
(36, 11, 'Bông Tai Kim Loại', 'Bông tai mạ vàng hợp kim phong cách Hàn Quốc.', 'ACTIVE'),
(37, 4, 'Áo Sơ Mi Trắng Công Sở', 'Áo sơ mi nam vải cotton pha, form vừa, phù hợp đi làm.', 'ACTIVE'),
(38, 4, 'Áo Thun Cổ Tròn Basic', 'Áo thun nam cổ tròn, chất cotton co giãn nhẹ, form regular.', 'ACTIVE'),
(39, 4, 'Áo Khoác Denim Nam', 'Áo khoác jean nam phong cách bụi bặm, dễ phối đồ.', 'ACTIVE'),
(40, 4, 'Áo Vest Nam Slimfit', 'Áo vest nam form ôm, phù hợp dự tiệc, đi làm.', 'ACTIVE'),
(41, 5, 'Quần Tây Caro Sọc', 'Quần tây nam họa tiết caro sọc tinh tế, chất vải co giãn.', 'ACTIVE'),
(42, 5, 'Quần Jean Ống Đứng', 'Quần jean nam ống đứng basic, dễ phối nhiều loại áo.', 'ACTIVE'),
(43, 5, 'Quần Kaki Nam', 'Quần kaki nam form regular, chất vải dày dặn bền đẹp.', 'ACTIVE'),
(44, 5, 'Quần Jogger Nỉ Nam', 'Quần jogger nỉ bông ấm áp, bo gấu ống, phong cách thể thao.', 'ACTIVE'),
(45, 6, 'Áo Kiểu Tay Bồng', 'Áo kiểu nữ tay bồng điệu đà, chất voan mềm mại.', 'ACTIVE'),
(46, 6, 'Áo Len Nữ Cổ Cao', 'Áo len dệt kim cổ cao giữ ấm, phù hợp mùa thu đông.', 'ACTIVE'),
(47, 6, 'Áo Sơ Mi Caro Nữ', 'Áo sơ mi nữ họa tiết caro trẻ trung, chất cotton thoáng mát.', 'ACTIVE'),
(48, 6, 'Áo Hai Dây Nữ', 'Áo hai dây nữ chất thun mềm mịn, mặc mát cả ngày.', 'ACTIVE'),
(49, 7, 'Đầm Ren Dự Tiệc', 'Đầm ren nữ tính dành cho tiệc tối, dáng ôm nhẹ tôn dáng.', 'ACTIVE'),
(50, 7, 'Chân Váy Denim', 'Chân váy jean nữ dáng chữ A, phong cách năng động.', 'ACTIVE'),
(51, 7, 'Đầm Suông Công Sở', 'Đầm suông thanh lịch, phù hợp môi trường công sở.', 'ACTIVE'),
(52, 7, 'Váy Yếm Jeans', 'Váy yếm denim trẻ trung, dễ phối cùng áo thun bên trong.', 'ACTIVE'),
(53, 8, 'Quần Baggy Nữ', 'Quần baggy nữ form rộng thoải mái, chất kaki co giãn nhẹ.', 'ACTIVE'),
(54, 8, 'Quần Legging Thể Thao', 'Quần legging nữ co giãn 4 chiều, phù hợp tập gym yoga.', 'ACTIVE'),
(55, 8, 'Quần Culottes Ống Rộng', 'Quần culottes nữ cạp cao ống rộng, tôn dáng thanh mảnh.', 'ACTIVE'),
(56, 9, 'Balo Du Lịch Chống Nước', 'Balo du lịch dung tích lớn, vải chống nước, nhiều ngăn tiện lợi.', 'ACTIVE'),
(57, 9, 'Túi Đeo Chéo Mini', 'Túi đeo chéo mini nữ nhỏ gọn, thiết kế thời trang.', 'ACTIVE'),
(58, 9, 'Ví Cầm Tay Nữ', 'Ví da cầm tay nữ, nhiều ngăn đựng thẻ và tiền.', 'ACTIVE'),
(59, 9, 'Túi Xách Tote Da', 'Túi tote da nữ form hộp, phong cách công sở sang trọng.', 'ACTIVE'),
(60, 10, 'Kính Mát Phi Công', 'Kính mát gọng kim loại phong cách phi công, chống UV.', 'ACTIVE'),
(61, 10, 'Kính Cận Gọng Nhựa', 'Kính cận gọng nhựa nhẹ, nhiều màu sắc thời trang.', 'ACTIVE'),
(62, 10, 'Kính Bơi Chống Nước', 'Kính bơi chống nước, chống tia UV, ôm sát khuôn mặt.', 'ACTIVE'),
(63, 11, 'Nhẫn Bạc Đơn Giản', 'Nhẫn bạc 925 thiết kế trơn đơn giản, phù hợp mọi trang phục.', 'ACTIVE'),
(64, 11, 'Lắc Tay Charm', 'Lắc tay bạc đính charm nhỏ xinh, phong cách trẻ trung.', 'ACTIVE'),
(65, 11, 'Bộ Trang Sức Ngọc Trai', 'Bộ trang sức ngọc trai gồm dây chuyền và bông tai.', 'ACTIVE'),
(66, 11, 'Khuyên Tai Ngọc Trai', 'Khuyên tai ngọc trai nhân tạo, thiết kế tinh tế nữ tính.', 'ACTIVE'),
(67, 12, 'Giày Boot Nam Cổ Cao', 'Giày boot da nam cổ cao, đế cao su chống trơn trượt.', 'ACTIVE'),
(68, 12, 'Giày Thể Thao Chạy Bộ', 'Giày thể thao nam đế êm, thiết kế thoáng khí phù hợp chạy bộ.', 'ACTIVE'),
(69, 12, 'Dép Tông Nam Basic', 'Dép tông nam quai ngang đơn giản, đế êm chống trượt.', 'ACTIVE'),
(70, 4, 'Áo Ba Lỗ Tập Gym', 'Áo ba lỗ nam chất thun co giãn, phù hợp tập gym thể thao.', 'ACTIVE');

-- ========================================================
-- 4. PRODUCT_VARIANTS (123 biến thể, id 1-123 liền mạch)
-- ========================================================
INSERT INTO `product_variants` (`variant_id`, `product_id`, `color`, `size`, `price`, `stock_quantity`) VALUES
(1, 1, 'Trắng', 'M', 150000, 100),
(2, 1, 'Đen', 'M', 150000, 80),
(3, 1, 'Trắng', 'L', 150000, 50),
(4, 2, 'Xanh Dương', 'M', 250000, 40),
(5, 2, 'Xanh Dương', 'L', 250000, 30),
(6, 3, 'Đen', 'L', 450000, 20),
(7, 3, 'Rêu', 'XL', 450000, 15),
(8, 4, 'Xanh Nhạt', '30', 350000, 60),
(9, 4, 'Xanh Đậm', '32', 350000, 45),
(10, 5, 'Đen', '31', 400000, 30),
(11, 5, 'Xám', '32', 400000, 25),
(12, 6, 'Be', 'L', 180000, 100),
(13, 6, 'Đen', 'M', 180000, 80),
(14, 7, 'Hồng', 'S', 200000, 40),
(15, 7, 'Trắng', 'M', 200000, 50),
(16, 8, 'Kem', 'M', 300000, 20),
(17, 8, 'Xanh Ngọc', 'L', 300000, 10),
(18, 9, 'Đen', 'Freesize', 120000, 150),
(19, 10, 'Trắng', 'S', 220000, 70),
(20, 10, 'Đen', 'M', 220000, 60),
(21, 11, 'Đỏ', 'M', 850000, 10),
(22, 11, 'Đen', 'S', 850000, 8),
(23, 12, 'Vàng', 'Freesize', 350000, 35),
(24, 13, 'Be', 'M', 280000, 40),
(25, 13, 'Nâu', 'L', 280000, 25),
(26, 14, 'Xanh', '28', 320000, 0),
(27, 15, 'Đen', 'Freesize', 500000, 15),
(28, 15, 'Nâu', 'Freesize', 500000, 20),
(29, 16, 'Xám', '15 inch', 450000, 50),
(30, 17, 'Bạc', 'Freesize', 250000, 100),
(31, 18, 'Đen', 'Freesize', 150000, 120),
(32, 19, 'Bạc', 'Freesize', 600000, 15),
(33, 20, 'Hồng', '15cm', 350000, 25),
(34, 21, 'Trắng', '40', 650000, 30),
(35, 21, 'Trắng', '41', 650000, 25),
(36, 22, 'Nâu', '42', 950000, 0),
(37, 23, 'Xám', 'L', 380000, 40),
(38, 24, 'Đỏ', 'M', 220000, 60),
(39, 24, 'Trắng', 'M', 220000, 80),
(40, 25, 'Đen', 'L', 340000, 45),
(41, 26, 'Be', 'S', 550000, 20),
(42, 27, 'Đen', 'M', 250000, 35),
(43, 28, 'Trắng Ngà', 'Freesize', 90000, 200),
(44, 29, 'Đen', '41', 250000, 60),
(45, 30, 'Đen', 'XL', 180000, 90),
(46, 31, 'Hồng Nhạt', 'M', 130000, 100),
(47, 31, 'Trắng', 'S', 130000, 50),
(48, 32, 'Đen', 'M', 280000, 30),
(49, 32, 'Caro Đỏ', 'L', 290000, 20),
(50, 33, 'Đen', 'Freesize', 250000, 80),
(51, 34, 'Nâu', '40', 850000, 15),
(52, 34, 'Đen', '41', 850000, 25),
(53, 35, 'Đen', 'XL', 150000, 120),
(54, 35, 'Xám', 'L', 150000, 60),
(55, 36, 'Vàng', 'Freesize', 80000, 200),
(56, 37, 'Hồng', 'S', 200000, 39),
(57, 37, 'Trắng', 'M', 220000, 66),
(58, 38, 'Đỏ', 'M', 210000, 46),
(59, 38, 'Đen', 'L', 230000, 69),
(60, 39, 'Xanh Rêu', 'L', 220000, 53),
(61, 39, 'Xanh Navy', 'XL', 240000, 72),
(62, 40, 'Trắng', 'M', 180000, 60),
(63, 40, 'Xám', 'M', 200000, 15),
(64, 41, 'Đen', 'L', 290000, 67),
(65, 41, 'Be', 'L', 310000, 18),
(66, 42, 'Xanh Navy', 'XL', 300000, 74),
(67, 42, 'Nâu', '41', 320000, 21),
(68, 43, 'Xám', 'M', 310000, 81),
(69, 43, 'Vàng', 'Freesize', 330000, 24),
(70, 44, 'Be', 'L', 320000, 88),
(71, 44, 'Hồng', 'S', 340000, 27),
(72, 45, 'Nâu', '41', 170000, 95),
(73, 45, 'Đỏ', 'M', 190000, 30),
(74, 46, 'Vàng', 'Freesize', 180000, 22),
(75, 46, 'Xanh Rêu', 'L', 200000, 33),
(76, 47, 'Hồng', 'S', 190000, 29),
(77, 47, 'Trắng', 'M', 210000, 36),
(78, 48, 'Đỏ', 'M', 200000, 36),
(79, 48, 'Đen', 'L', 220000, 39),
(80, 49, 'Xanh Rêu', 'L', 360000, 43),
(81, 49, 'Xanh Navy', 'XL', 380000, 42),
(82, 50, 'Trắng', 'M', 320000, 50),
(83, 50, 'Xám', 'M', 340000, 45),
(84, 51, 'Đen', 'L', 330000, 57),
(85, 51, 'Be', 'L', 350000, 48),
(86, 52, 'Xanh Navy', 'XL', 340000, 64),
(87, 52, 'Nâu', '41', 360000, 51),
(88, 53, 'Xám', 'M', 290000, 71),
(89, 53, 'Vàng', 'Freesize', 310000, 54),
(90, 54, 'Be', 'L', 300000, 78),
(91, 54, 'Hồng', 'S', 320000, 57),
(92, 55, 'Nâu', '41', 260000, 85),
(93, 55, 'Đỏ', 'M', 280000, 60),
(94, 56, 'Vàng', 'Freesize', 310000, 92),
(95, 56, 'Xanh Rêu', 'L', 330000, 63),
(96, 57, 'Hồng', 'S', 320000, 99),
(97, 57, 'Trắng', 'M', 340000, 66),
(98, 58, 'Đỏ', 'M', 330000, 26),
(99, 58, 'Đen', 'L', 350000, 69),
(100, 59, 'Xanh Rêu', 'L', 340000, 33),
(101, 59, 'Xanh Navy', 'XL', 360000, 72),
(102, 60, 'Trắng', 'M', 200000, 40),
(103, 60, 'Xám', 'M', 220000, 15),
(104, 61, 'Đen', 'L', 210000, 47),
(105, 61, 'Be', 'L', 230000, 18),
(106, 62, 'Xanh Navy', 'XL', 220000, 54),
(107, 62, 'Nâu', '41', 240000, 21),
(108, 63, 'Xám', 'M', 280000, 61),
(109, 63, 'Vàng', 'Freesize', 300000, 24),
(110, 64, 'Be', 'L', 290000, 68),
(111, 64, 'Hồng', 'S', 310000, 27),
(112, 65, 'Nâu', '41', 250000, 75),
(113, 65, 'Đỏ', 'M', 270000, 30),
(114, 66, 'Vàng', 'Freesize', 260000, 82),
(115, 66, 'Xanh Rêu', 'L', 280000, 33),
(116, 67, 'Hồng', 'S', 420000, 89),
(117, 67, 'Trắng', 'M', 440000, 36),
(118, 68, 'Đỏ', 'M', 430000, 96),
(119, 68, 'Đen', 'L', 450000, 39),
(120, 69, 'Xanh Rêu', 'L', 440000, 23),
(121, 69, 'Xanh Navy', 'XL', 460000, 42),
(122, 70, 'Trắng', 'M', 180000, 30),
(123, 70, 'Xám', 'M', 200000, 45);

-- ========================================================
-- 5. PRODUCT_IMAGES (71 ảnh thật AI-generated theo mô tả, phủ đủ 70 sản phẩm)
-- ========================================================
INSERT INTO `product_images` (`image_id`, `product_id`, `color`, `url`) VALUES
(1, 1, 'Trắng', 'https://image.pollinations.ai/prompt/mens%20tshirt%20white?width=400&height=400&nologo=true&seed=1'),
(2, 1, 'Đen', 'https://image.pollinations.ai/prompt/mens%20tshirt%20white?width=400&height=400&nologo=true&seed=2'),
(3, 2, 'Xanh Dương', 'https://image.pollinations.ai/prompt/mens%20striped%20shirt?width=400&height=400&nologo=true&seed=3'),
(4, 7, 'Hồng', 'https://image.pollinations.ai/prompt/off%20shoulder%20top%20women?width=400&height=400&nologo=true&seed=4'),
(5, 11, 'Đỏ', 'https://image.pollinations.ai/prompt/evening%20dress%20red?width=400&height=400&nologo=true&seed=5'),
(6, 3, 'Đen', 'https://image.pollinations.ai/prompt/bomber%20jacket%20men?width=400&height=400&nologo=true&seed=6'),
(7, 4, 'Xanh Nhạt', 'https://image.pollinations.ai/prompt/ripped%20jeans%20men?width=400&height=400&nologo=true&seed=7'),
(8, 5, 'Đen', 'https://image.pollinations.ai/prompt/mens%20dress%20pants?width=400&height=400&nologo=true&seed=8'),
(9, 6, 'Be', 'https://image.pollinations.ai/prompt/khaki%20shorts%20men?width=400&height=400&nologo=true&seed=9'),
(10, 8, 'Kem', 'https://image.pollinations.ai/prompt/silk%20blouse%20women?width=400&height=400&nologo=true&seed=10'),
(11, 9, 'Đen', 'https://image.pollinations.ai/prompt/crop%20top%20women?width=400&height=400&nologo=true&seed=11'),
(12, 10, 'Trắng', 'https://image.pollinations.ai/prompt/pleated%20skirt?width=400&height=400&nologo=true&seed=12'),
(13, 12, 'Be', 'https://image.pollinations.ai/prompt/floral%20maxi%20dress?width=400&height=400&nologo=true&seed=13'),
(14, 13, 'Xanh', 'https://image.pollinations.ai/prompt/wide%20leg%20pants%20women?width=400&height=400&nologo=true&seed=14'),
(15, 14, 'Xanh', 'https://image.pollinations.ai/prompt/skinny%20jeans%20women?width=400&height=400&nologo=true&seed=15'),
(16, 15, 'Đen', 'https://image.pollinations.ai/prompt/leather%20crossbody%20bag?width=400&height=400&nologo=true&seed=16'),
(17, 16, 'Xám', 'https://image.pollinations.ai/prompt/laptop%20backpack?width=400&height=400&nologo=true&seed=17'),
(18, 17, 'Bạc', 'https://image.pollinations.ai/prompt/eyeglasses%20frame?width=400&height=400&nologo=true&seed=18'),
(19, 18, 'Đen', 'https://image.pollinations.ai/prompt/sunglasses%20fashion?width=400&height=400&nologo=true&seed=19'),
(20, 19, 'Bạc', 'https://image.pollinations.ai/prompt/silver%20necklace?width=400&height=400&nologo=true&seed=20'),
(21, 20, 'Hồng', 'https://image.pollinations.ai/prompt/beaded%20bracelet?width=400&height=400&nologo=true&seed=21'),
(22, 21, 'Trắng', 'https://image.pollinations.ai/prompt/white%20sneakers?width=400&height=400&nologo=true&seed=22'),
(23, 22, 'Nâu', 'https://image.pollinations.ai/prompt/oxford%20shoes%20men?width=400&height=400&nologo=true&seed=23'),
(24, 23, 'Xám', 'https://image.pollinations.ai/prompt/turtleneck%20sweater%20men?width=400&height=400&nologo=true&seed=24'),
(25, 24, 'Đỏ', 'https://image.pollinations.ai/prompt/polo%20shirt%20men?width=400&height=400&nologo=true&seed=25'),
(26, 25, 'Đen', 'https://image.pollinations.ai/prompt/jogger%20pants%20men?width=400&height=400&nologo=true&seed=26'),
(27, 26, 'Be', 'https://image.pollinations.ai/prompt/blazer%20women?width=400&height=400&nologo=true&seed=27'),
(28, 27, 'Đen', 'https://image.pollinations.ai/prompt/pencil%20skirt?width=400&height=400&nologo=true&seed=28'),
(29, 28, 'Trắng Ngà', 'https://image.pollinations.ai/prompt/canvas%20tote%20bag?width=400&height=400&nologo=true&seed=29'),
(30, 29, 'Đen', 'https://image.pollinations.ai/prompt/sandals%20men?width=400&height=400&nologo=true&seed=30'),
(31, 30, 'Đen', 'https://image.pollinations.ai/prompt/graphic%20tshirt%20oversized?width=400&height=400&nologo=true&seed=31'),
(32, 31, 'Hồng Nhạt', 'https://image.pollinations.ai/prompt/fitted%20tshirt%20women?width=400&height=400&nologo=true&seed=32'),
(33, 32, 'Đen', 'https://image.pollinations.ai/prompt/aline%20dress%20women?width=400&height=400&nologo=true&seed=33'),
(34, 33, 'Đen', 'https://image.pollinations.ai/prompt/duffel%20bag%20sports?width=400&height=400&nologo=true&seed=34'),
(35, 34, 'Nâu', 'https://image.pollinations.ai/prompt/loafers%20men%20leather?width=400&height=400&nologo=true&seed=35'),
(36, 35, 'Đen', 'https://image.pollinations.ai/prompt/athletic%20shorts%20men?width=400&height=400&nologo=true&seed=36'),
(37, 36, 'Vàng', 'https://image.pollinations.ai/prompt/hoop%20earrings%20gold?width=400&height=400&nologo=true&seed=37'),
(38, 37, 'Hồng', 'https://image.pollinations.ai/prompt/white%20dress%20shirt%20men?width=400&height=400&nologo=true&seed=38'),
(39, 38, 'Đỏ', 'https://image.pollinations.ai/prompt/mens%20tshirt%20basic?width=400&height=400&nologo=true&seed=39'),
(40, 39, 'Xanh Rêu', 'https://image.pollinations.ai/prompt/denim%20jacket%20men?width=400&height=400&nologo=true&seed=40'),
(41, 40, 'Trắng', 'https://image.pollinations.ai/prompt/suit%20jacket%20men?width=400&height=400&nologo=true&seed=41'),
(42, 41, 'Đen', 'https://image.pollinations.ai/prompt/plaid%20trousers%20men?width=400&height=400&nologo=true&seed=42'),
(43, 42, 'Xanh Navy', 'https://image.pollinations.ai/prompt/straight%20jeans%20men?width=400&height=400&nologo=true&seed=43'),
(44, 43, 'Xám', 'https://image.pollinations.ai/prompt/khaki%20pants%20men?width=400&height=400&nologo=true&seed=44'),
(45, 44, 'Be', 'https://image.pollinations.ai/prompt/fleece%20jogger%20pants?width=400&height=400&nologo=true&seed=45'),
(46, 45, 'Nâu', 'https://image.pollinations.ai/prompt/ruffle%20sleeve%20blouse?width=400&height=400&nologo=true&seed=46'),
(47, 46, 'Vàng', 'https://image.pollinations.ai/prompt/turtleneck%20sweater%20women?width=400&height=400&nologo=true&seed=47'),
(48, 47, 'Hồng', 'https://image.pollinations.ai/prompt/plaid%20shirt%20women?width=400&height=400&nologo=true&seed=48'),
(49, 48, 'Đỏ', 'https://image.pollinations.ai/prompt/tank%20top%20women?width=400&height=400&nologo=true&seed=49'),
(50, 49, 'Xanh Rêu', 'https://image.pollinations.ai/prompt/lace%20dress%20evening?width=400&height=400&nologo=true&seed=50'),
(51, 50, 'Trắng', 'https://image.pollinations.ai/prompt/denim%20skirt?width=400&height=400&nologo=true&seed=51'),
(52, 51, 'Đen', 'https://image.pollinations.ai/prompt/shift%20dress%20office?width=400&height=400&nologo=true&seed=52'),
(53, 52, 'Xanh Navy', 'https://image.pollinations.ai/prompt/denim%20overall%20dress?width=400&height=400&nologo=true&seed=53'),
(54, 53, 'Xám', 'https://image.pollinations.ai/prompt/baggy%20pants%20women?width=400&height=400&nologo=true&seed=54'),
(55, 54, 'Be', 'https://image.pollinations.ai/prompt/leggings%20women?width=400&height=400&nologo=true&seed=55'),
(56, 55, 'Nâu', 'https://image.pollinations.ai/prompt/culottes%20pants%20women?width=400&height=400&nologo=true&seed=56'),
(57, 56, 'Vàng', 'https://image.pollinations.ai/prompt/travel%20backpack%20waterproof?width=400&height=400&nologo=true&seed=57'),
(58, 57, 'Hồng', 'https://image.pollinations.ai/prompt/mini%20crossbody%20bag?width=400&height=400&nologo=true&seed=58'),
(59, 58, 'Đỏ', 'https://image.pollinations.ai/prompt/clutch%20wallet%20women?width=400&height=400&nologo=true&seed=59'),
(60, 59, 'Xanh Rêu', 'https://image.pollinations.ai/prompt/leather%20tote%20bag?width=400&height=400&nologo=true&seed=60'),
(61, 60, 'Trắng', 'https://image.pollinations.ai/prompt/aviator%20sunglasses?width=400&height=400&nologo=true&seed=61'),
(62, 61, 'Đen', 'https://image.pollinations.ai/prompt/plastic%20frame%20glasses?width=400&height=400&nologo=true&seed=62'),
(63, 62, 'Xanh Navy', 'https://image.pollinations.ai/prompt/swim%20goggles?width=400&height=400&nologo=true&seed=63'),
(64, 63, 'Xám', 'https://image.pollinations.ai/prompt/silver%20ring%20simple?width=400&height=400&nologo=true&seed=64'),
(65, 64, 'Be', 'https://image.pollinations.ai/prompt/charm%20bracelet%20silver?width=400&height=400&nologo=true&seed=65'),
(66, 65, 'Nâu', 'https://image.pollinations.ai/prompt/pearl%20jewelry%20set?width=400&height=400&nologo=true&seed=66'),
(67, 66, 'Vàng', 'https://image.pollinations.ai/prompt/pearl%20earrings?width=400&height=400&nologo=true&seed=67'),
(68, 67, 'Hồng', 'https://image.pollinations.ai/prompt/leather%20boots%20men?width=400&height=400&nologo=true&seed=68'),
(69, 68, 'Đỏ', 'https://image.pollinations.ai/prompt/running%20shoes%20men?width=400&height=400&nologo=true&seed=69'),
(70, 69, 'Xanh Rêu', 'https://image.pollinations.ai/prompt/flip%20flops%20men?width=400&height=400&nologo=true&seed=70'),
(71, 70, 'Trắng', 'https://image.pollinations.ai/prompt/tank%20top%20gym%20men?width=400&height=400&nologo=true&seed=71');

-- ========================================================
-- 6. ADDRESSES (30 địa chỉ)
-- ========================================================
INSERT INTO `addresses` (`address_id`, `user_id`, `full_address`, `receiver_name`, `receiver_phone`, `is_default`) VALUES
(1, 3, '12A Nguyễn Văn Cừ, Q5, TP.HCM', 'Nguyễn Trọng A', '0900000003', 1),
(2, 4, '45B Lê Lợi, Q1, TP.HCM', 'Lê Thị B', '0900000004', 1),
(3, 5, '99 Trần Hưng Đạo, Hoàn Kiếm, HN', 'Trần Văn C', '0900000005', 1),
(4, 6, 'KDC Trung Sơn, Bình Chánh, TP.HCM', 'Phạm Quỳnh D', '0900000006', 1),
(5, 7, '22 Hoàng Diệu, Hải Châu, Đà Nẵng', 'Hoàng Bảo E', '0900000007', 1),
(6, 8, '102 Cầu Giấy, Cầu Giấy, HN', 'Vũ Đức F', '0900000008', 1),
(7, 10, 'Hẻm 51, 3/2, Ninh Kiều, Cần Thơ', 'Bùi Xuân H', '0900000010', 1),
(8, 11, 'Phố Cổ Hội An, Quảng Nam', 'Đỗ Minh I', '0900000011', 1),
(9, 12, '15 Lê Duẩn, Ba Đình, HN', 'Hồ Thúy J', '0900000012', 1),
(10, 13, 'Chợ Bến Thành, Q1, TP.HCM', 'Ngô Kiến K', '0900000013', 1),
(11, 14, '55 Quang Trung, Gò Vấp, TP.HCM', 'Dương Thảo L', '0900000014', 1),
(12, 15, 'Tòa nhà Bitexco, Q1, TP.HCM', 'Lý Hải M', '0900000015', 1),
(13, 17, '80 Láng Hạ, Đống Đa, HN', 'Đinh Tiến O', '0900000017', 1),
(14, 19, 'Bãi Sau, Vũng Tàu', 'Trương Quốc Q', '0900000019', 1),
(15, 20, '120 Võ Văn Kiệt, Sơn Trà, ĐN', 'Phan Thanh R', '0900000020', 1),
(16, 21, '4A Phạm Ngọc Thạch, Q3, TP.HCM', 'Mai Phương S', '0900000021', 1),
(17, 22, 'Đại học Cần Thơ, Ninh Kiều', 'Đào Trọng T', '0900000022', 1),
(18, 23, 'KĐT Sala, Quận 2, TP.HCM', 'Tô Lan U', '0900000023', 1),
(19, 25, '120 Trần Phú, Nha Trang', 'Châu Lệ W', '0900000025', 1),
(20, 26, '15 Hùng Vương, Huế', 'Cao Đạt X', '0900000026', 1),
(21, 27, '68 Nguyễn Du, Hai Bà Trưng, HN', 'Lư Tấn Y', '0900000027', 1),
(22, 29, '50 Tôn Đức Thắng, Q1, TP.HCM', 'Tôn Thất A1', '0900000029', 1),
(23, 30, 'Landmark 81, Bình Thạnh, TP.HCM', 'Giang Di B2', '0900000030', 1),
(24, 3, 'Địa chỉ phụ của A, Q7, TP.HCM', 'Trọng A (Cơ quan)', '0900000003', 0),
(25, 4, 'Địa chỉ phụ của B, Q2, TP.HCM', 'Lê B (Nhà riêng)', '0900000004', 0),
(26, 5, 'Địa chỉ phụ của C, Tây Hồ, HN', 'Trần C', '0900000005', 0),
(27, 8, 'Địa chỉ phụ của F, Nam Từ Liêm, HN', 'Vũ F', '0900000008', 0),
(28, 11, 'Địa chỉ phụ của I, TP.Tam Kỳ', 'Minh I', '0900000011', 0),
(29, 20, 'Địa chỉ phụ của R, Thanh Khê, ĐN', 'Thanh R', '0900000020', 0),
(30, 30, 'Địa chỉ phụ của B2, Q1, TP.HCM', 'Giang Di B2', '0900000030', 0);

-- ========================================================
-- 7. COUPONS (used_count đã đồng bộ với số đơn hàng thực tế áp dụng coupon)
-- ========================================================
INSERT INTO `coupons` (`coupon_id`, `code`, `discount_type`, `discount_value`, `min_order_amount`, `usage_limit`, `used_count`, `start_date`, `expiry_date`, `active`) VALUES
(1, 'WELCOME50', 'FIXED_AMOUNT', 50000, 200000, 1000, 2, '2023-01-01', '2026-12-31', 1),
(2, 'SUMMER20', 'PERCENTAGE', 20, 500000, 500, 1, '2024-05-01', '2025-08-31', 1),
(3, 'FREESHIP', 'FIXED_AMOUNT', 30000, 150000, 2000, 0, '2024-01-01', '2026-12-31', 1),
(4, 'VIP100K', 'FIXED_AMOUNT', 100000, 1000000, 50, 1, '2024-01-01', '2025-12-31', 1),
(5, 'BLACKFRIDAY', 'PERCENTAGE', 50, 0, 10, 0, '2024-11-20', '2024-11-30', 0);

-- ========================================================
-- USER_COUPONS (bổ sung dòng 6: user 33 dùng WELCOME50 trong order 36)
-- ========================================================
INSERT INTO `user_coupons` (`user_coupon_id`, `user_id`, `coupon_id`, `used`) VALUES
(1, 3, 1, 1),
(2, 4, 1, 0),
(3, 5, 2, 1),
(4, 6, 3, 0),
(5, 8, 4, 1),
(6, 33, 1, 1);

-- ========================================================
-- 8. ORDERS (45 đơn hàng, id 1-45) -- user_id 41-49 cũ đã đổi thành 32-40 cho khớp bảng users mới
-- ========================================================
INSERT INTO `orders` (`order_id`, `user_id`, `coupon_id`, `total_amount`, `status`, `payment_method`, `type`, `order_date`, `shipping_address`, `hidden_by_user`) VALUES
(1, 3, 1, 300000, 'COMPLETED', 'COD', 'ONLINE', '2024-01-10 10:00:00', '12A Nguyễn Văn Cừ, Q5, TP.HCM', 0),
(2, 4, NULL, 500000, 'PENDING_PAYMENT', 'BANK_TRANSFER', 'ONLINE', '2024-02-15 14:30:00', '45B Lê Lợi, Q1, TP.HCM', 0),
(3, 5, 2, 800000, 'SHIPPING', 'VNPAY', 'ONLINE', '2024-03-20 09:15:00', '99 Trần Hưng Đạo, Hoàn Kiếm, HN', 0),
(4, 6, NULL, 450000, 'DELIVERED', 'MOMO', 'ONLINE', '2024-03-25 16:45:00', 'KDC Trung Sơn, Bình Chánh, TP.HCM', 0),
(5, 8, 4, 1500000, 'CANCELLED', 'COD', 'ONLINE', '2024-04-01 10:00:00', '102 Cầu Giấy, Cầu Giấy, HN', 0),
(6, 10, NULL, 600000, 'PROCESSING', 'BANK_TRANSFER', 'ONLINE', '2024-04-10 11:00:00', 'Hẻm 51, 3/2, Ninh Kiều, Cần Thơ', 0),
(7, 11, NULL, 350000, 'PAID', 'MOMO', 'ONLINE', '2024-04-12 08:30:00', 'Phố Cổ Hội An, Quảng Nam', 0),
(8, 12, NULL, 280000, 'COMPLETED', 'COD', 'OFFLINE', '2024-04-15 15:00:00', 'Mua tại quầy', 0),
(9, 13, NULL, 750000, 'COMPLETED', 'VNPAY', 'ONLINE', '2024-04-18 19:20:00', 'Chợ Bến Thành, Q1, TP.HCM', 0),
(10, 14, NULL, 400000, 'PENDING_CONFIRMATION', 'COD', 'ONLINE', '2024-04-20 09:10:00', '55 Quang Trung, Gò Vấp, TP.HCM', 0),
(11, 15, NULL, 120000, 'COMPLETED', 'COD', 'ONLINE', '2024-04-21 14:00:00', 'Tòa nhà Bitexco, Q1, TP.HCM', 0),
(12, 17, NULL, 850000, 'COMPLETED', 'BANK_TRANSFER', 'ONLINE', '2024-04-22 10:00:00', '80 Láng Hạ, Đống Đa, HN', 0),
(13, 19, NULL, 900000, 'SHIPPING', 'MOMO', 'ONLINE', '2024-04-25 11:30:00', 'Bãi Sau, Vũng Tàu', 0),
(14, 20, NULL, 150000, 'DELIVERED', 'COD', 'ONLINE', '2024-04-26 13:45:00', '120 Võ Văn Kiệt, Sơn Trà, ĐN', 0),
(15, 21, NULL, 220000, 'CANCELLED', 'VNPAY', 'ONLINE', '2024-04-27 16:00:00', '4A Phạm Ngọc Thạch, Q3, TP.HCM', 0),
(16, 22, NULL, 650000, 'PROCESSING', 'COD', 'ONLINE', '2024-04-28 09:20:00', 'Đại học Cần Thơ, Ninh Kiều', 0),
(17, 23, NULL, 300000, 'COMPLETED', 'BANK_TRANSFER', 'ONLINE', '2024-04-29 14:15:00', 'KĐT Sala, Quận 2, TP.HCM', 0),
(18, 25, NULL, 500000, 'COMPLETED', 'MOMO', 'ONLINE', '2024-04-30 10:00:00', '120 Trần Phú, Nha Trang', 0),
(19, 26, NULL, 450000, 'COMPLETED', 'COD', 'ONLINE', '2024-05-01 08:00:00', '15 Hùng Vương, Huế', 0),
(20, 27, NULL, 250000, 'PENDING_PAYMENT', 'VNPAY', 'ONLINE', '2024-05-02 11:11:00', '68 Nguyễn Du, Hai Bà Trưng, HN', 0),
(21, 29, NULL, 180000, 'SHIPPING', 'COD', 'ONLINE', '2024-05-03 15:30:00', '50 Tôn Đức Thắng, Q1, TP.HCM', 0),
(22, 30, NULL, 600000, 'DELIVERED', 'BANK_TRANSFER', 'ONLINE', '2024-05-04 18:45:00', 'Landmark 81, Bình Thạnh, TP.HCM', 0),
(23, 3, NULL, 350000, 'COMPLETED', 'COD', 'ONLINE', '2024-05-05 09:00:00', '12A Nguyễn Văn Cừ, Q5, TP.HCM', 0),
(24, 4, NULL, 400000, 'CANCELLED', 'MOMO', 'ONLINE', '2024-05-06 14:00:00', '45B Lê Lợi, Q1, TP.HCM', 0),
(25, 5, NULL, 250000, 'PROCESSING', 'COD', 'ONLINE', '2024-05-07 10:20:00', '99 Trần Hưng Đạo, Hoàn Kiếm, HN', 0),
(26, 6, NULL, 850000, 'COMPLETED', 'VNPAY', 'ONLINE', '2024-05-08 16:30:00', 'KDC Trung Sơn, Bình Chánh, TP.HCM', 0),
(27, 8, NULL, 150000, 'PAID', 'BANK_TRANSFER', 'ONLINE', '2024-05-09 11:00:00', '102 Cầu Giấy, Cầu Giấy, HN', 0),
(28, 10, NULL, 300000, 'COMPLETED', 'COD', 'OFFLINE', '2024-05-10 14:50:00', 'Mua tại quầy', 0),
(29, 11, NULL, 450000, 'SHIPPING', 'MOMO', 'ONLINE', '2024-05-11 09:10:00', 'Phố Cổ Hội An, Quảng Nam', 0),
(30, 12, NULL, 220000, 'DELIVERED', 'COD', 'ONLINE', '2024-05-12 15:20:00', '15 Lê Duẩn, Ba Đình, HN', 0),
(31, 3, NULL, 200000, 'CONFIRMED', 'COD', 'ONLINE', '2024-06-01 10:00:00', '12A Nguyễn Văn Cừ, Q5, TP.HCM', 0),
(32, 4, NULL, 450000, 'CONFIRMED', 'BANK_TRANSFER', 'ONLINE', '2024-06-02 14:30:00', '45B Lê Lợi, Q1, TP.HCM', 0),
(33, 5, NULL, 300000, 'RETURNED', 'VNPAY', 'ONLINE', '2024-05-15 09:15:00', '99 Trần Hưng Đạo, Hoàn Kiếm, HN', 0),
(34, 6, NULL, 550000, 'RETURNED', 'MOMO', 'ONLINE', '2024-05-16 16:45:00', 'KDC Trung Sơn, Bình Chánh, TP.HCM', 0),
(35, 32, NULL, 130000, 'DELIVERED', 'COD', 'ONLINE', '2024-06-05 08:30:00', '123 Cầu Giấy, HN', 0),
(36, 33, 1, 410000, 'PROCESSING', 'MOMO', 'ONLINE', '2024-06-06 09:15:00', '45 Lê Duẩn, Đà Nẵng', 0),
(37, 35, NULL, 280000, 'CONFIRMED', 'VNPAY', 'ONLINE', '2024-06-07 14:00:00', 'Quận 1, TP.HCM', 0),
(38, 36, NULL, 850000, 'PENDING_PAYMENT', 'BANK_TRANSFER', 'ONLINE', '2024-06-08 10:20:00', 'KĐT Định Công, HN', 0),
(39, 38, NULL, 150000, 'CANCELLED', 'COD', 'ONLINE', '2024-06-09 16:45:00', 'Bình Thủy, Cần Thơ', 0),
(40, 39, NULL, 330000, 'SHIPPING', 'COD', 'ONLINE', '2024-06-10 11:30:00', 'Tân Bình, TP.HCM', 0),
(41, 40, NULL, 80000, 'COMPLETED', 'MOMO', 'ONLINE', '2024-06-11 15:00:00', 'Thủ Đức, TP.HCM', 0),
(42, 12, NULL, 250000, 'RETURNED', 'BANK_TRANSFER', 'ONLINE', '2024-05-20 09:00:00', 'Ba Đình, HN', 0),
(43, 14, NULL, 580000, 'CONFIRMED', 'COD', 'ONLINE', '2024-06-12 08:00:00', 'Gò Vấp, TP.HCM', 0),
(44, 25, NULL, 850000, 'PAYMENT_FAILED', 'VNPAY', 'ONLINE', '2024-06-12 19:30:00', 'Nha Trang, Khánh Hòa', 0),
(45, 8, NULL, 150000, 'DELIVERED', 'COD', 'ONLINE', '2024-06-13 14:25:00', 'Nam Từ Liêm, HN', 0);

-- ========================================================
-- 9. RETURN_REQUESTS (4 yêu cầu hoàn trả) -- bổ sung #2,#3,#4 cho order 33,34,42 (status RETURNED nhưng trước đây thiếu bản ghi)
-- ========================================================
INSERT INTO `return_requests` (`return_request_id`, `order_id`, `user_id`, `reason`, `description`, `status`, `request_date`, `processed_by`, `processed_at`, `rejection_reason`) VALUES
(1, 4, 6, 'Lỗi sản phẩm', 'Áo khoác bomber bị rách chỉ ở phần tay áo.', 'PENDING', '2024-03-26 10:00:00', NULL, NULL, NULL),
(2, 33, 5, 'Không đúng mô tả', 'Áo sơ mi lụa nhận được không đúng như hình, chất liệu khác mô tả.', 'COMPLETED', '2024-05-16 10:00:00', 1, '2024-05-18 09:00:00', NULL),
(3, 34, 6, 'Lỗi sản phẩm', 'Áo blazer nữ bị lỗi đường may ở tay áo.', 'COMPLETED', '2024-05-17 09:00:00', 1, '2024-05-19 10:00:00', NULL),
(4, 42, 12, 'Giao sai màu', 'Túi trống thể thao giao nhầm màu so với đơn đặt.', 'COMPLETED', '2024-05-21 10:00:00', 2, '2024-05-23 11:00:00', NULL);

-- ========================================================
-- RETURN_REQUEST_IMAGES (bổ sung ảnh minh chứng cho 3 yêu cầu hoàn trả mới)
-- ========================================================
INSERT INTO `return_request_images` (`return_request_id`, `image_url`) VALUES
(1, 'https://example.com/returns/error_bomber1.jpg'),
(2, 'https://example.com/returns/error_silk_blouse.jpg'),
(3, 'https://example.com/returns/error_blazer_seam.jpg'),
(4, 'https://example.com/returns/wrong_color_bag.jpg');

-- ========================================================
-- 10. ORDER_ITEMS (49 dòng, id 1-49 liền mạch)
-- ========================================================
-- ĐÃ SỬA: (1) gán return_request_id trực tiếp thay vì UPDATE riêng biệt
--        (2) gộp 2 dòng trùng cùng variant 49 trong order 43 (order_item cũ 47+48) thành 1 dòng quantity=2
--        (3) dồn lại id liền mạch 1-49 sau khi gộp
INSERT INTO `order_items` (`order_item_id`, `order_id`, `variant_id`, `product_name`, `quantity`, `price`, `status`, `refund_status`, `is_reviewed`, `cancellation_reason`, `return_request_id`) VALUES
(1, 1, 1, 'Áo Thun Basic Nam', 2, 150000, 'COMPLETED', 'NONE', 1, NULL, NULL),
(2, 2, 4, 'Áo Sơ Mi Kẻ Sọc', 2, 250000, 'PENDING_PAYMENT', 'NONE', 0, NULL, NULL),
(3, 3, 10, 'Quần Tây Ống Suông', 2, 400000, 'SHIPPING', 'NONE', 0, NULL, NULL),
(4, 4, 6, 'Áo Khoác Bomber', 1, 450000, 'DELIVERED', 'PENDING', 0, NULL, 1),
(5, 5, 21, 'Đầm Body Dạ Hội', 1, 850000, 'CANCELLED', 'NONE', 0, 'Đổi ý không mua', NULL),
(6, 5, 34, 'Giày Sneaker Trắng', 1, 650000, 'CANCELLED', 'NONE', 0, 'Đổi ý không mua', NULL),
(7, 6, 16, 'Áo Sơ Mi Lụa', 2, 300000, 'PROCESSING', 'NONE', 0, NULL, NULL),
(8, 7, 23, 'Đầm Maxi Đi Biển', 1, 350000, 'PAID', 'NONE', 0, NULL, NULL),
(9, 8, 24, 'Quần Ống Rộng Nữ', 1, 280000, 'COMPLETED', 'NONE', 0, NULL, NULL),
(10, 9, 27, 'Túi Xách Da Đeo Chéo', 1, 500000, 'COMPLETED', 'NONE', 1, NULL, NULL),
(11, 9, 30, 'Mắt Kính Chống Cận', 1, 250000, 'COMPLETED', 'NONE', 0, NULL, NULL),
(12, 10, 11, 'Quần Tây Ống Suông', 1, 400000, 'PENDING_CONFIRMATION', 'NONE', 0, NULL, NULL),
(13, 11, 18, 'Áo Croptop Năng Động', 1, 120000, 'COMPLETED', 'NONE', 1, NULL, NULL),
(14, 12, 21, 'Đầm Body Dạ Hội', 1, 850000, 'COMPLETED', 'NONE', 1, NULL, NULL),
(15, 13, 29, 'Balo Laptop 15inch', 2, 450000, 'SHIPPING', 'NONE', 0, NULL, NULL),
(16, 14, 31, 'Kính Râm Thời Trang', 1, 150000, 'DELIVERED', 'NONE', 0, NULL, NULL),
(17, 15, 19, 'Chân Váy Xếp Ly', 1, 220000, 'CANCELLED', 'NONE', 0, 'Tìm được chỗ rẻ hơn', NULL),
(18, 16, 34, 'Giày Sneaker Trắng', 1, 650000, 'PROCESSING', 'NONE', 0, NULL, NULL),
(19, 17, 16, 'Áo Sơ Mi Lụa', 1, 300000, 'COMPLETED', 'NONE', 1, NULL, NULL),
(20, 18, 27, 'Túi Xách Da Đeo Chéo', 1, 500000, 'COMPLETED', 'NONE', 0, NULL, NULL),
(21, 19, 29, 'Balo Laptop 15inch', 1, 450000, 'COMPLETED', 'NONE', 0, NULL, NULL),
(22, 20, 30, 'Mắt Kính Chống Cận', 1, 250000, 'PENDING_PAYMENT', 'NONE', 0, NULL, NULL),
(23, 21, 45, 'Áo Thun In Phản Quang', 1, 180000, 'SHIPPING', 'NONE', 0, NULL, NULL),
(24, 22, 32, 'Dây Chuyền Bạc 925', 1, 600000, 'DELIVERED', 'NONE', 0, NULL, NULL),
(25, 23, 8, 'Quần Jean Rách Gối', 1, 350000, 'COMPLETED', 'NONE', 1, NULL, NULL),
(26, 24, 10, 'Quần Tây Ống Suông', 1, 400000, 'CANCELLED', 'NONE', 0, 'Sai size', NULL),
(27, 25, 30, 'Mắt Kính Chống Cận', 1, 250000, 'PROCESSING', 'NONE', 0, NULL, NULL),
(28, 26, 21, 'Đầm Body Dạ Hội', 1, 850000, 'COMPLETED', 'NONE', 0, NULL, NULL),
(29, 27, 31, 'Kính Râm Thời Trang', 1, 150000, 'PAID', 'NONE', 0, NULL, NULL),
(30, 28, 16, 'Áo Sơ Mi Lụa', 1, 300000, 'COMPLETED', 'NONE', 1, NULL, NULL),
(31, 29, 29, 'Balo Laptop 15inch', 1, 450000, 'SHIPPING', 'NONE', 0, NULL, NULL),
(32, 30, 19, 'Chân Váy Xếp Ly', 1, 220000, 'DELIVERED', 'NONE', 0, NULL, NULL),
(33, 31, 14, 'Áo Trễ Vai Nữ Tính', 1, 200000, 'CONFIRMED', 'NONE', 0, NULL, NULL),
(34, 32, 6, 'Áo Khoác Bomber', 1, 450000, 'CONFIRMED', 'NONE', 0, NULL, NULL),
(35, 33, 16, 'Áo Sơ Mi Lụa', 1, 300000, 'RETURNED', 'COMPLETED', 0, NULL, 2),
(36, 34, 41, 'Áo Blazer Nữ', 1, 550000, 'RETURNED', 'COMPLETED', 0, NULL, 3),
(37, 35, 46, 'Áo Thun Cổ Tim Nữ', 1, 130000, 'DELIVERED', 'NONE', 0, NULL, NULL),
(38, 36, 48, 'Váy Chữ A Điệu Đà', 1, 280000, 'PROCESSING', 'NONE', 0, NULL, NULL),
(39, 36, 46, 'Áo Thun Cổ Tim Nữ', 1, 130000, 'PROCESSING', 'NONE', 0, NULL, NULL),
(40, 37, 48, 'Váy Chữ A Điệu Đà', 1, 280000, 'CONFIRMED', 'NONE', 0, NULL, NULL),
(41, 38, 51, 'Giày Lười Nam Moca', 1, 850000, 'PENDING_PAYMENT', 'NONE', 0, NULL, NULL),
(42, 39, 53, 'Quần Đùi Thể Thao Nam', 1, 150000, 'CANCELLED', 'NONE', 0, 'Phí ship cao', NULL),
(43, 40, 50, 'Túi Trống Thể Thao', 1, 250000, 'SHIPPING', 'NONE', 0, NULL, NULL),
(44, 40, 55, 'Bông Tai Kim Loại', 1, 80000, 'SHIPPING', 'NONE', 0, NULL, NULL),
(45, 41, 55, 'Bông Tai Kim Loại', 1, 80000, 'COMPLETED', 'NONE', 1, NULL, NULL),
(46, 42, 50, 'Túi Trống Thể Thao', 1, 250000, 'RETURNED', 'COMPLETED', 0, 'Giao nhầm màu', 4),
(47, 43, 49, 'Váy Chữ A Điệu Đà', 2, 290000, 'CONFIRMED', 'NONE', 0, NULL, NULL),
(48, 44, 52, 'Giày Lười Nam Moca', 1, 850000, 'PAYMENT_FAILED', 'NONE', 0, NULL, NULL),
(49, 45, 54, 'Quần Đùi Thể Thao Nam', 1, 150000, 'DELIVERED', 'NONE', 0, NULL, NULL);

-- ========================================================
-- 11. REVIEWS (8 đánh giá) -- bổ sung #7 (order_item 30), #8 (order_item 45) do is_reviewed=1 nhưng trước đây thiếu review
-- ========================================================
INSERT INTO `reviews` (`review_id`, `user_id`, `product_id`, `order_item_id`, `rating`, `comment`, `created_at`) VALUES
(1, 3, 1, 1, 5, 'Áo đẹp, vải xịn mặc mát lắm nhé shop!', '2024-01-15 15:00:00'),
(2, 13, 15, 10, 4, 'Túi da mềm, form hơi nhỏ so với mình nghĩ nhưng oke.', '2024-04-20 10:00:00'),
(3, 15, 9, 13, 5, 'Áo croptop mặc siêu tôn dáng, vote 5 sao!', '2024-04-23 09:00:00'),
(4, 17, 11, 14, 5, 'Đầm mặc đi tiệc ai cũng khen, rất đáng tiền.', '2024-04-24 19:30:00'),
(5, 23, 8, 19, 5, 'Lụa mềm, mặc mát, giao hàng nhanh.', '2024-05-02 11:00:00'),
(6, 3, 4, 25, 4, 'Quần jean chất ổn, màu hơi nhạt hơn ảnh một xíu.', '2024-05-07 08:45:00'),
(7, 10, 8, 30, 5, 'Áo sơ mi lụa đẹp, mặc đi làm rất sang, đóng gói cẩn thận.', '2024-05-13 10:00:00'),
(8, 40, 36, 45, 5, 'Bông tai xinh, đeo nhẹ không đau tai, giao nhanh.', '2024-06-13 09:00:00');

-- ========================================================
-- REVIEW_IMAGES (3 ảnh minh họa đánh giá, không đổi)
-- ========================================================
INSERT INTO `review_images` (`review_image_id`, `review_id`, `image_url`) VALUES
(1, 1, 'https://example.com/reviews/r1_img1.jpg'),
(2, 4, 'https://example.com/reviews/r4_img1.jpg'),
(3, 4, 'https://example.com/reviews/r4_img2.jpg');

-- ========================================================
-- 12. CART_ITEMS, WISHLIST_ITEMS, RECENTLY_VIEWED_ITEMS, NOTIFICATIONS (không đổi)
-- ========================================================
INSERT INTO `cart_items` (`cart_item_id`, `user_id`, `variant_id`, `quantity`) VALUES
(1, 3, 6, 1), (2, 4, 10, 2), (3, 5, 21, 1), (4, 10, 30, 1), (5, 12, 1, 3);

INSERT INTO `wishlist_items` (`wishlist_item_id`, `user_id`, `product_id`) VALUES
(1, 3, 7), (2, 4, 5), (3, 4, 11), (4, 10, 20), (5, 15, 30);

INSERT INTO `recently_viewed_items` (`view_id`, `user_id`, `product_id`, `viewed_at`) VALUES
(1, 3, 3, '2024-05-10 09:00:00'), (2, 3, 4, '2024-05-10 09:05:00'),
(3, 4, 10, '2024-05-11 14:20:00'), (4, 10, 21, '2024-05-12 10:15:00');

INSERT INTO `notifications` (`id`, `user_id`, `title`, `content`, `type`, `related_id`, `is_read`, `created_at`) VALUES
(1, 3, 'Đơn hàng thành công', 'Đơn hàng #1 của bạn đã hoàn thành.', 'SUCCESS', 1, 1, '2024-01-12 10:00:00'),
(2, 6, 'Yêu cầu hoàn trả', 'Yêu cầu hoàn trả đơn #4 đang được xử lý.', 'INFO', 4, 0, '2024-03-26 10:05:00'),
(3, 10, 'Nhắc nhở thanh toán', 'Bạn có đơn hàng đang chờ thanh toán.', 'WARNING', 6, 0, '2024-04-10 12:00:00');

SET FOREIGN_KEY_CHECKS = 1;