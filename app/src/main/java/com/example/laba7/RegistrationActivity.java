package com.example.laba7;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "Registration";

    private EditText etFullName, etLogin, etEmail, etPhone, etPassword, etConfirmPassword, etDate;
    private CheckBox cbConsent;
    private Spinner spinner;

    private static final String FULLNAME_REGEX = "^[А-Яа-яёЁ\\s-]+$";
    private static final String LOGIN_REGEX = "^[a-zA-Z]+$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_REGEX = "^\\+7[\\s\\(]?\\d{3}[\\s\\)]?[\\s-]?\\d{3}[\\s-]?\\d{2}[\\s-]?\\d{2}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();
        setupSpinner();
        setupDatePicker();

        findViewById(R.id.btnRegister).setOnClickListener(v -> validateAndRegister());
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etLogin = findViewById(R.id.etLogin);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etDate = findViewById(R.id.etDate);
        cbConsent = findViewById(R.id.cbConsent);
        spinner = findViewById(R.id.spinner);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupDatePicker() {
        View.OnClickListener dateListener = v -> {
            Calendar calendar = Calendar.getInstance();
            String existingDate = etDate.getText().toString();
            if (!existingDate.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
                    sdf.setLenient(false);
                    Date parsed = sdf.parse(existingDate);
                    if (parsed != null) {
                        calendar.setTime(parsed);
                    }
                } catch (ParseException ignored) { }
            }
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    RegistrationActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Месяц от 0 до 11, приводим к человеческому
                        String dateStr = String.format(Locale.US, "%02d.%02d.%04d",
                                selectedDay, selectedMonth + 1, selectedYear);
                        etDate.setText(dateStr);
                    },
                    year, month, day);
            dialog.show();
        };
        etDate.setOnClickListener(dateListener);
        findViewById(R.id.btnDatePicker).setOnClickListener(dateListener);
    }

    private void validateAndRegister() {
        etFullName.setError(null);
        etLogin.setError(null);
        etEmail.setError(null);
        etPhone.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
        etDate.setError(null);

        boolean valid = true;
        if (!validateNotEmpty(etFullName, getString(R.string.error_empty_field))) valid = false;
        else if (!validatePattern(etFullName, FULLNAME_REGEX, getString(R.string.error_fullname))) valid = false;

        if (!validateNotEmpty(etLogin, getString(R.string.error_empty_field))) valid = false;
        else if (!validatePattern(etLogin, LOGIN_REGEX, getString(R.string.error_login))) valid = false;

        if (!validateNotEmpty(etEmail, getString(R.string.error_empty_field))) valid = false;
        else if (!validatePattern(etEmail, EMAIL_REGEX, getString(R.string.error_email))) valid = false;

        if (!validateNotEmpty(etPhone, getString(R.string.error_empty_field))) valid = false;
        else if (!validatePattern(etPhone, PHONE_REGEX, getString(R.string.error_phone))) valid = false;

        if (!validateNotEmpty(etPassword, getString(R.string.error_empty_field))) valid = false;
        else {
            String pwd = etPassword.getText().toString().trim();
            if (pwd.length() < 6 || !pwd.matches(".*[A-Z].*") || !pwd.matches(".*\\d.*")) {
                Log.d(TAG, "Password does not meet requirements");
                etPassword.setError(getString(R.string.error_password));
                valid = false;
            }
        }
        if (!validateNotEmpty(etConfirmPassword, getString(R.string.error_empty_field))) valid = false;
        else {
            String pwd = etPassword.getText().toString().trim();
            String confirm = etConfirmPassword.getText().toString().trim();
            if (!confirm.equals(pwd)) {
                Log.d(TAG, "Passwords do not match");
                etConfirmPassword.setError(getString(R.string.error_confirm_password));
                valid = false;
            }
        }

        if (!validateNotEmpty(etDate, getString(R.string.error_empty_field))) valid = false;
        else if (!validateDate(etDate)) valid = false;

        if (!cbConsent.isChecked()) {
            Log.d(TAG, "Consent not given");
            Toast.makeText(this, getString(R.string.error_consent), Toast.LENGTH_SHORT).show(); // можно и Toast
            valid = false;
        }

        if (valid) {
            Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Registration successful");
        } else {
            Log.d(TAG, "Registration failed due to validation errors");
        }
    }
    private boolean validateNotEmpty(EditText editText, String errorMsg) {
        String text = editText.getText().toString().trim();
        if (text.isEmpty()) {
            Log.d(TAG, editText.getHint() + " is empty");
            editText.setError(errorMsg);
            return false;
        }
        return true;
    }

    private boolean validatePattern(EditText editText, String regex, String errorMsg) {
        String text = editText.getText().toString().trim();
        if (!Pattern.matches(regex, text)) {
            Log.d(TAG, editText.getHint() + " does not match pattern: " + text);
            editText.setError(errorMsg);
            return false;
        }
        return true;
    }

    private boolean validateDate(EditText editText) {
        String dateStr = editText.getText().toString().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
        sdf.setLenient(false);
        try {
            Date parsedDate = sdf.parse(dateStr);
            if (parsedDate == null) {
                throw new ParseException("Invalid date", 0);
            }
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date today = cal.getTime();

            Calendar minDate = Calendar.getInstance();
            minDate.set(1900, Calendar.JANUARY, 1, 0, 0, 0);
            minDate.set(Calendar.MILLISECOND, 0);
            Date min = minDate.getTime();

            if (parsedDate.before(min) || parsedDate.after(today)) {
                Log.d(TAG, "Date out of range: " + dateStr);
                editText.setError(getString(R.string.error_date_range));
                return false;
            }
            return true;
        } catch (ParseException e) {
            Log.d(TAG, "Date parsing error: " + dateStr);
            editText.setError(getString(R.string.error_date_format));
            return false;
        }
    }
}