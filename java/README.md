1. Mở app MySQL WorkBench 8.0 CE

2. Kết nối CSDL và import file database.sql

3. Mở file "hibernate.cfg.xml" trong \tuyensinh\src\main\resources

4. Kiểm tra thông tin kết nối CSDL đúng với máy chưa (username và password CSDL) ở dòng <property name="connection.username">root</property> <property name="connection.password">123456</property> <property name="connection.pool_size">5</property>

5. Chạy file "App.java" trong \tuyensinh\src\main\java\com\example

6. Login với Admin: admin/123456
              User: user1/123456 (ít quyền hơn Admin)
          Thí sinh: 001204000001/123456 (mặc định là CCCD/123456)
