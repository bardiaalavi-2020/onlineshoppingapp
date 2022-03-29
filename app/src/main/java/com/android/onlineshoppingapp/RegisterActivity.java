package com.android.onlineshoppingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private ImageView imageViewBack;
    private AutoCompleteTextView textViewListDay, textViewListMonth, textViewListYear;
    private ArrayList<Integer> arrayListDay, arrayListMonth, arrayListYear;
    private TextInputEditText editTextFirstName, editTextLastName, editTextUsername;
    private TextInputEditText editTextPassword, editTextRePassword, editTextPhone, editTextEmail;
    private TextInputLayout txtLayoutFirstName, txtLayoutLastName, txtLayoutUsername;
    private TextInputLayout txtLayoutPassword, txtLayoutRePassword, txtLayoutPhone, txtLayoutEmail;
    private Button btnRegister;
    private RadioGroup radioGroupAccountType, radioGroupSex;
    private RadioButton radioButtonPurchase, radioButtonSell, radioButtonMale, radioButtonFemale, radioButtonOther;
    private CheckBox checkBoxAgree;
    private String accountType, sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imageViewBack = findViewById(R.id.ivBackRegister);
        editTextFirstName = findViewById(R.id.editTxtFirstname);
        txtLayoutFirstName = findViewById(R.id.txtFieldFirstname);
        editTextLastName = findViewById(R.id.editTxtLastname);
        txtLayoutLastName = findViewById(R.id.txtFieldLastname);
        editTextUsername = findViewById(R.id.editTxtUsername);
        txtLayoutUsername = findViewById(R.id.txtFieldUsername);
        editTextEmail = findViewById(R.id.editTxtEmail);
        txtLayoutEmail = findViewById(R.id.txtFieldEmail);
        editTextPassword = findViewById(R.id.editTxtPassword);
        txtLayoutPassword = findViewById(R.id.txtFieldPassword);
        editTextRePassword = findViewById(R.id.editTxtRePassword);
        txtLayoutRePassword = findViewById(R.id.txtFieldRePassword);
        editTextPhone = findViewById(R.id.editTxtPhone);
        txtLayoutPhone = findViewById(R.id.txtFieldPhone);
        radioGroupSex = findViewById(R.id.radioGroupSex);
        radioButtonMale = findViewById(R.id.radioBtnMale);
        radioButtonFemale = findViewById(R.id.radioBtnFemale);
        radioButtonOther = findViewById(R.id.radioBtnOther);
        textViewListDay = findViewById(R.id.tvListDay);
        textViewListMonth = findViewById(R.id.tvListMonth);
        textViewListYear = findViewById(R.id.tvListYear);
        radioGroupAccountType = findViewById(R.id.rgAccType);
        radioButtonPurchase = findViewById(R.id.radioBtnPurchase);
        radioButtonSell = findViewById(R.id.radioBtnSell);
        checkBoxAgree = findViewById(R.id.checkBoxAgree);
        btnRegister = findViewById(R.id.btnRegister);

        // Click on back button
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // navigate to login activity
                finish();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        // check null input data: first name
        editTextFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean onFocus) {
                if (!onFocus) {
                    if (editTextFirstName.getText().toString().trim().equals("")) {
                        txtLayoutFirstName.setHelperText("Tên không được để trống");
                    } else if (!includeCharInAlphabet(editTextFirstName.getText().toString().trim())) {
                        txtLayoutFirstName.setHelperText("Tên không hợp lệ");
                    }
                } else {
                    txtLayoutFirstName.setHelperTextEnabled(false);
                }
            }
        });

        // check null input data: last name
        editTextLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean onFocus) {
                if (!onFocus) {
                    if (editTextLastName.getText().toString().trim().equals("")) {
                        txtLayoutLastName.setHelperText("Họ không được để trống");
                    } else if (!includeCharInAlphabet(editTextLastName.getText().toString().trim())) {
                        txtLayoutLastName.setHelperText("Họ không hợp lệ");
                    }
                } else {
                    txtLayoutLastName.setHelperTextEnabled(false);
                }
            }
        });

        // Check null input data: username
        editTextUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean onFocus) {
                if (!onFocus) {
                    if (editTextUsername.getText().toString().trim().equals("")) {
                        txtLayoutUsername.setHelperText("Tên đăng nhập không được để trống!");
                    } else if (!includeCharInAlphabet(editTextUsername.getText().toString().trim())) {
                        txtLayoutUsername.setHelperText("Tên đăng nhập phải chứa ít nhất 1 ký tự chữ");
                    }
                } else {
                    txtLayoutUsername.setHelperTextEnabled(false);
                }
            }
        });

        // Check null input data: email
        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean onFocus) {
                if (!onFocus) {
                    if (editTextEmail.getText().toString().trim().equals("")) {
                        txtLayoutEmail.setHelperText("Email không được để trống!");
                    } else if (!isCorrectEmailFormat(editTextEmail.getText().toString().trim())) {
                        txtLayoutEmail.setHelperText("Email bạn vừa nhập không đúng định dạng");
                    }
                } else {
                    txtLayoutEmail.setHelperTextEnabled(false);
                }
            }
        });

        // Check null input data: password
        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean onFocus) {
                if (!onFocus) {
                    if (editTextPassword.getText().toString().equals("")) {
                        txtLayoutPassword.setHelperText("Mật khẩu không được để trống!");
                    } else if (!isLongEnough(editTextPassword.getText().toString(), 8)) {
                        txtLayoutPassword.setHelperText("Mật khẩu phải có ít nhất 8 kí tự");
                    } else if (isCorrectTextFormat(editTextPassword.getText().toString())) {
                        txtLayoutPassword.setHelperText("Mật khẩu chỉ bao gồm số, chữ cái và các kí tự _ . -");
                    }
                } else {
                    txtLayoutPassword.setHelperTextEnabled(false);
                }
            }
        });

        // Check null input data: Re-password
        editTextRePassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean onFocus) {
                if (!onFocus) {
                    if (editTextRePassword.getText().toString().equals(editTextPassword.getText().toString())) {
                        txtLayoutRePassword.setHelperTextEnabled(false);
                    } else {
                        txtLayoutRePassword.setHelperText("Xác nhận mật khẩu không đúng!");
                    }
                }
            }
        });

        // Check null input data: phone
        editTextPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean onFocus) {
                if (!onFocus) {
                    if (editTextPhone.getText().toString().trim().equals("")) {
                        txtLayoutPhone.setHelperText("Số điện thoại không được để trống!");
                    } else if (!isLongEnough(editTextPhone.getText().toString().trim(), 6)) {
                        txtLayoutPhone.setHelperText("Số điện thoại bạn vừa nhập không hợp lệ");
                    }
                } else {
                    txtLayoutPhone.setHelperTextEnabled(false);
                }
            }
        });

        // Choose sex
        sex = radioButtonMale.getText().toString();
        radioGroupSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioBtnMale:
                        sex = radioButtonMale.getText().toString();
                        break;
                    case R.id.radioBtnFemale:
                        sex = radioButtonFemale.getText().toString();
                        break;
                    case R.id.radioBtnOther:
                        sex = radioButtonOther.getText().toString();
                        break;
                    default:
                        break;
                }
            }
        });


        // List of Day
        arrayListDay = new ArrayList<Integer>();
        for (int i = 1; i <= 31; i++) {
            arrayListDay.add(i);
        }
        ArrayAdapter<Integer> listDayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                arrayListDay
        );
        textViewListDay.setAdapter(listDayAdapter);

        // List of Month
        arrayListMonth = new ArrayList<Integer>();
        for (int i = 1; i <= 12; i++) {
            arrayListMonth.add(i);
        }
        ArrayAdapter<Integer> listMonthAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                arrayListMonth
        );
        textViewListMonth.setAdapter(listMonthAdapter);

        // List of Year
        arrayListYear = new ArrayList<>();
        int currentYear = LocalDateTime.now().getYear();
        for (int i = currentYear; i > currentYear - 100; i--) {
            arrayListYear.add(i);
        }
        ArrayAdapter<Integer> listYearAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                arrayListYear
        );
        textViewListYear.setAdapter(listYearAdapter);

        // Choose type of account
        accountType = radioButtonPurchase.getText().toString();
        radioGroupAccountType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioBtnPurchase:
                        accountType = radioButtonPurchase.getText().toString();
                        break;
                    case R.id.radioBtnSell:
                        accountType = radioButtonSell.getText().toString();
                        break;
                    default:
                        break;
                }
            }
        });

        // Click on Register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // validate date of birth
                if (validateDateOfBirth()) {
                    if (editTextFirstName.getText().toString().trim().equals("") ||
                            editTextLastName.getText().toString().trim().equals("") ||
                            editTextUsername.getText().toString().trim().equals("") ||
                            editTextEmail.getText().toString().trim().equals("") ||
                            editTextPhone.getText().toString().trim().equals("") ||
                            editTextPassword.getText().toString().equals("")) {
                        Toast.makeText(RegisterActivity.this, "Bạn chưa điền đầy đủ thông tin!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (checkBoxAgree.isChecked()) {
                            Toast.makeText(RegisterActivity.this,
                                    "Họ và tên: " + editTextLastName.getText().toString().trim() +
                                            " " + editTextFirstName.getText().toString().trim() +
                                            "\nUsername: " + editTextUsername.getText().toString().trim() +
                                            "\nEmail: " + editTextEmail.getText().toString().trim() +
                                            "\nPassword: " + editTextPassword.getText().toString() +
                                            "\nPhone: " + editTextPhone.getText().toString().trim() +
                                            "\nGiới tính: " + sex +
                                            "\nSN: " + textViewListDay.getText().toString() +
                                            "/" + textViewListMonth.getText().toString() +
                                            "/" + textViewListYear.getText().toString() +
                                            "\nLoại tài khoản: " + accountType +
                                            "\nĐăng ký thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Bạn phải đồng ý với các Điều khoản dịch vụ \n\t\t\t\t\t\t\t\tvà Chính sách bảo mật!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });
    }

    // ----------------------- Function ------------------------


    private boolean includeCharInAlphabet(String str) {
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (int i = 0; i < str.length(); i++) {
            for (int j = 0; j < alphabet.length; j++) {
                if (str.toLowerCase().charAt(i) == alphabet[j])
                    return true;
            }
        }
        return false;
    }

    private boolean isCorrectTextFormat(String str) {
        if (str.matches("[^a-zA-Z0-9._-]"))
            return true;
        return false;
    }

    private boolean isCorrectEmailFormat(String str) {
        if (str.matches("[a-zA-Z0-9._-]+@[a-z]+\\.[a-zA-Z0-9.]+"))
            return true;
        return false;
    }

    private boolean isLongEnough(String str, int num) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            count++;
        }
        if (count >= num) return true;
        else return false;
    }

    private boolean validateDateOfBirth() {
        int day = Integer.valueOf(textViewListDay.getText().toString());
        int month = Integer.valueOf(textViewListMonth.getText().toString());
        int year = Integer.valueOf(textViewListYear.getText().toString());

        // check leap year
        if (!checkLeapYear(year)) {
            if (day > dayOfMonth(month)) {
                Toast.makeText(this, "Bạn vừa nhập ngày không tồn tại!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            if (month == 2 && day > 29) {
                Toast.makeText(this, "Bạn vừa nhập ngày không tồn tại!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private int dayOfMonth(int month) {
        int m = 0;
        switch (month) {
            case 2:
                m = 28;
                break;
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                m = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                m = 30;
                break;
            default:
                break;
        }
        return m;
    }

    private boolean checkLeapYear(int year) {
        return (((year % 4 == 0) && (year % 100 != 0)) ||
                (year % 400 == 0));
    }

}