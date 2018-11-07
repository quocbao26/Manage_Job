package com.example.asus.model;

import android.content.Context;
import android.widget.Toast;

import com.example.asus.quanlycongviec.MainActivity;
import com.example.asus.quanlycongviec.R;

import java.util.HashMap;

public class CheckAll {

    // Tên hàm      : checkSpecial
    // Mô tả        : kiểm tra chuỗi có giá trị đặc biệt
    // Kiểu trả về  : true / false
    // Tham số      : String strId (chứa tên đăng nhập)
    public boolean checkSpecial(String strId) {
        char arrSpecial[] = {'=','!','@','#','$','%','^','&','*','(',')','-','/',',',':',';','.',' '};
        char s[] = strId.toCharArray(); // tách chuỗi strId thành mảng từng ký tự s
        for (int i = 0; i < s.length; i++)
        {
            for (int j = 0; j < arrSpecial.length; j++)
            {
                if (s[i] == arrSpecial[j])
                {
                    return true;
                }
            }
        }
        return false;
    }

    // Tên hàm : checkEmptyJob
    // Mô tả   : kiểm tra HashMap có rỗng không ?
    // Kiểu trả về : true / false
    // Tham số : HashMap job   (chứa thuộc tính)
    public boolean checkEmptyJob(HashMap job){
        if (job.get("name").equals("") && job.get("content").equals("") &&
                job.get("location").equals("") && job.get("date").equals("") && job.get("hour").equals(""))
            return true;
        else
            return false;
    }

    // Tên hàm : checkEmpty
    // Mô tả   : kiểm tra 3 giá trị truyền vào có rỗng không ?
    // Kiểu trả về : true / false
    // Tham số : String strId   (chứa tên đăng nhập)
    // Tham số : String strPass (chứa mật khẩu)
    // Tham số : String strName (chứa tên)
    public boolean checkEmpty(String strId, String strPass, String strName)
    {
        if (checkId(strId) || checkPass(strPass) || checkName(strName))
        {
            return true;
        }
        return false;
    }
    // Tên hàm : checkId
    // Mô tả   : kiểm tra giá trị truyền vào có rỗng không ?
    // Kiểu trả về : true / false
    // Tham số : String strId   (chứa tên đăng nhập)
    public boolean checkId(String strId) {
        return strId.length()==0;
    }

    // Tên hàm : checkPass
    // Mô tả   : kiểm tra giá trị truyền vào có rỗng không ?
    // Kiểu trả về : true / false
    // Tham số : String strPass   (chứa mật khẩu)
    public boolean checkPass(String strPass) {
        return strPass.length()==0;
    }

    // Tên hàm : checkName
    // Mô tả   : kiểm tra giá trị truyền vào có rỗng không ?
    // Kiểu trả về : true / false
    // Tham số : String strName   (chứa tên)
    public boolean checkName(String strName) {
        return strName.length()==0;
    }


}
