package com.example.asus.model;

// Mô tả            : Class thông báo thành công, thất bại, lỗi
// Tác giả          : Ngô Hoàng Quốc Bảo
// Email            : n14dccn067@student.ptithcm.edu.vn
// Ngày cập nhật    : 6/11/2018
public final class Error {

    // Login
    public final static String LOGIN_E001 = "Không bỏ trống";
    public final static String LOGIN_E002 = "Chưa nhập tài khoản";
    public final static String LOGIN_E003 = "Chưa nhập mật khẩu";

    public final static String LOGIN_E004 = "Sai tài khoản hoặc mật khẩu";
    public final static String NOT_CONNECT_INTERNET = "Chưa kết nối mạng!!";

    public final static String CONNECTED_INTERNET = "Đã kết nối mạng";
    public final static String LOGIN_SUCCESS = "Đăng nhập thành công";

    // Register
    public final static String REGISTER_E001 = "Không được bỏ trống";
    public final static String REGISTER_E002 = "Tên đăng nhập đã tồn tại";
    public final static String REGISTER_E003 = "Tên đăng nhập không chứa ký tự đặc biệt";
    public final static String REGISTER_E004 = "Mật khẩu không giống nhau";
    public final static String REGISTER_E005 = "Đăng ký thất bại";

    public final static String REGISTER_SUCCESS = "Đăng ký thành công";


    // Change Password
    public final static String CHANGEPASS_E001 = "Không bỏ trống";
    public final static String CHANGEPASS_E002 = "Hai mật khẩu mới không giống nhau";
    public final static String CHANGEPASS_E003 = "Mật khẩu cũ không chính xác";
    public final static String CHANGEPASS_SUCCESS = "Đổi mật khẩu thành công";
    public final static String CHANGEPASS_FAIL = "Đổi mật khẩu thất bại";

    //Manager User
    public final static String MANAGER_E001 = "Xóa thất bại";
    public final static String MANAGER_SUCCESS = "Đã xóa";

    // Insert
    public final static String INSERT_E001 = "Chưa tạo công việc";
    public final static String INSERT_E002 = "Thêm công việc thất bại";

    public final static String INSERT_SUCCESS = "Thêm công việc thành công";

    // Update
    public final static String UPDATE_E001 = "Vui lòng không bỏ trống";
    public final static String UPDATE_E002 = "Sửa công việc thất bại";

    public final static String UPDATE_SUCCESS = "Đã sửa công việc";

    // Delete
    public final static String DELETE_E001 = "Xóa thất bại";
    public final static String DELETE_SUCCESS = "Đã xóa";




}
