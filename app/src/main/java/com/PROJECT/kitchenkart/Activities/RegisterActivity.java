package com.PROJECT.kitchenkart.Activities;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.PROJECT.kitchenkart.DataBase.DBHelper; // Correct import
import com.PROJECT.kitchenkart.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/** @noinspection ALL*/
public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword,
            etDob, etNic, etAddress, etPostalCode, etPhoneNumber, etDivision;
    private RadioGroup rgGender, rgUserType;
    private Button btnRegister;
    private TextView tvLoginLink;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase and local database helper
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dbHelper = new DBHelper(this);

        // Initialize UI components
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        rgGender = findViewById(R.id.rgGender);
        etDob = findViewById(R.id.etDob);
        etNic = findViewById(R.id.etNic);
        etAddress = findViewById(R.id.etAddress);
        etPostalCode = findViewById(R.id.etPostalCode);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etDivision = findViewById(R.id.etDivision);
        rgUserType = findViewById(R.id.rgUserType);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLogin);

        // Set up DatePickerDialog for Date of Birth field
        etDob.setOnClickListener(v -> showDatePickerDialog());
        etDob.setFocusable(false);
        etDob.setClickable(true);

        // Set up Register button click listener
        btnRegister.setOnClickListener(v -> registerUser());

        // Set up Login link click listener
        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDob.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String nic = etNic.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String postalCode = etPostalCode.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String division = etDivision.getText().toString().trim();

        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        String gender = "";
        if (selectedGenderId != -1) {
            RadioButton selectedGenderButton = findViewById(selectedGenderId);
            gender = selectedGenderButton.getText().toString();
        }

        int selectedUserTypeId = rgUserType.getCheckedRadioButtonId();
        String userType = "";
        if (selectedUserTypeId != -1) {
            RadioButton selectedUserTypeButton = findViewById(selectedUserTypeId);
            userType = selectedUserTypeButton.getText().toString().toLowerCase();
        }

        // --- Input Validation ---
        if (TextUtils.isEmpty(name)) { etName.setError(getString(R.string.error_name_required)); return; }
        if (TextUtils.isEmpty(email)) { etEmail.setError(getString(R.string.error_email_required)); return; }
        if (TextUtils.isEmpty(password)) { etPassword.setError(getString(R.string.error_password_required)); return; }
        if (TextUtils.isEmpty(confirmPassword)) { etConfirmPassword.setError(getString(R.string.error_confirm_password_required)); return; }
        if (!password.equals(confirmPassword)) { Toast.makeText(this, R.string.error_passwords_do_not_match, Toast.LENGTH_SHORT).show(); return; }
        if (TextUtils.isEmpty(gender)) { Toast.makeText(this, R.string.error_gender_required, Toast.LENGTH_SHORT).show(); return; }
        if (TextUtils.isEmpty(dob)) { etDob.setError(getString(R.string.error_dob_required)); return; }
        if (TextUtils.isEmpty(nic)) { etNic.setError(getString(R.string.error_nic_required)); return; }
        if (TextUtils.isEmpty(address)) { etAddress.setError(getString(R.string.error_address_required)); return; }
        if (TextUtils.isEmpty(postalCode)) { etPostalCode.setError(getString(R.string.error_postal_code_required)); return; }
        if (TextUtils.isEmpty(phoneNumber)) { etPhoneNumber.setError(getString(R.string.error_phone_number_required)); return; }
        if (phoneNumber.length() != 10 || !phoneNumber.startsWith("0")) {
            etPhoneNumber.setError(getString(R.string.error_invalid_phone_number));
            return;
        }
        if (TextUtils.isEmpty(division)) { etDivision.setError(getString(R.string.error_division_required)); return; }
        if (TextUtils.isEmpty(userType)) { Toast.makeText(this, R.string.error_user_type_required, Toast.LENGTH_SHORT).show(); return; }

        // --- Firebase Authentication and Firestore ---
        String finalUserType = userType;
        String finalGender = gender;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (mAuth.getCurrentUser() != null) {
                                String userId = mAuth.getCurrentUser().getUid();

                                // Store additional user data in Firestore
                                Map<String, Object> user = new HashMap<>();
                                user.put("name", name);
                                user.put("email", email);
                                user.put("gender", finalGender);
                                user.put("dob", dob);
                                user.put("nic", nic);
                                user.put("address", address);
                                user.put("postalCode", postalCode);
                                user.put("phoneNumber", phoneNumber);
                                user.put("division", division);
                                user.put("userType", finalUserType);

                                db.collection("users").document(userId)
                                        .set(user)
                                        .addOnSuccessListener(aVoid -> {
                                            // On Firestore success, save to local SQLite DB
                                            saveUserToLocalDatabase(userId, name, email, password, finalGender, dob, nic, address, postalCode, phoneNumber, division, finalUserType);
                                            Toast.makeText(RegisterActivity.this, R.string.success_registration, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(RegisterActivity.this, getString(R.string.error_failed_to_save_details) + e.getMessage(), Toast.LENGTH_LONG).show();
                                            if (mAuth.getCurrentUser() != null) {
                                                mAuth.getCurrentUser().delete();
                                            }
                                        });
                            }
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown registration error.";
                            Toast.makeText(RegisterActivity.this, getString(R.string.error_authentication_failed) + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveUserToLocalDatabase(String userId, String name, String email, String password, String gender, String dob, String nic, String address, String postalCode, String phoneNumber, String division, String userType) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_USER_ID, userId);
        cv.put(DBHelper.COLUMN_NAME, name);
        cv.put(DBHelper.COLUMN_EMAIL, email);
        cv.put(DBHelper.COLUMN_PASSWORD, password);
        cv.put(DBHelper.COLUMN_GENDER, gender);
        cv.put(DBHelper.COLUMN_DOB, dob);
        cv.put(DBHelper.COLUMN_NIC, nic);
        cv.put(DBHelper.COLUMN_ADDRESS, address);
        cv.put(DBHelper.COLUMN_POSTAL_CODE, postalCode);
        cv.put(DBHelper.COLUMN_PHONE_NUMBER, phoneNumber);
        cv.put(DBHelper.COLUMN_DIVISION, division);
        cv.put(DBHelper.COLUMN_USER_TYPE, userType);

        long id = dbHelper.insertUser(cv);
        if (id != -1) {
            Toast.makeText(this, "User saved locally (rowId=" + id + ")", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Local save failed", Toast.LENGTH_SHORT).show();
        }
    }
}