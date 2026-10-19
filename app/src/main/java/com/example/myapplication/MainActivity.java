
package com.example.myapplication;

import android.os.Bundle;
import android.graphics.Color;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LinearLayout layout;
    private SharedPreferences preferences;

    private String userName = "Demo User";
    private String userEmail = "user@example.com";

    private EditText nameInput;
    private EditText emailInput;

    private int backgroundColor = Color.rgb(245, 247, 250);
    private int textColor = Color.rgb(35, 55, 90);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("MyAppData", MODE_PRIVATE);

        userName = preferences.getString("name", "Demo User");
        userEmail = preferences.getString("email", "user@example.com");

        showLogin();
    }

    // Creates a fresh screen with a heading and scrollable content.
    private void setupScreen(String title) {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setPadding(28, 28, 28, 28);
        layout.setBackgroundColor(backgroundColor);

        scrollView.addView(layout);
        setContentView(scrollView);

        TextView heading = new TextView(this);
        heading.setText(title);
        heading.setTextSize(27);
        heading.setTextColor(textColor);
        heading.setGravity(Gravity.CENTER);
        heading.setPadding(0, 12, 0, 24);

        layout.addView(heading, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
    }

    // Adds a text label.
    private void addLabel(String text) {
        TextView label = new TextView(this);
        label.setText(text);
        label.setTextSize(16);
        label.setTextColor(textColor);
        label.setPadding(0, 10, 0, 8);
        layout.addView(label);
    }

    // Adds an input field.
    private EditText addInput(String hint, boolean password) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setTextSize(16);
        input.setSingleLine(true);

        if (password) {
            input.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD
            );
            input.setTransformationMethod(
                    PasswordTransformationMethod.getInstance()
            );
        } else if (hint.toLowerCase().contains("email")) {
            input.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            );
        } else {
            input.setInputType(InputType.TYPE_CLASS_TEXT);
        }

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 5, 0, 12);
        layout.addView(input, params);

        return input;
    }

    // Adds a button without forcing uppercase text.
    private Button addButton(String text, Runnable action) {
        Button button = new Button(this);
        button.setText(text);
        button.setTransformationMethod(null);
        button.setTextSize(15);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 6, 0, 6);
        layout.addView(button, params);

        button.setOnClickListener(v -> action.run());

        return button;
    }

    private void showMessage(String message) {
        Toast.makeText(
                MainActivity.this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    // MODULE 1A: LOGIN
    private void showLogin() {
        setupScreen("Welcome Back");

        addLabel("Sign in to continue");

        EditText email = addInput("Email address", false);
        EditText password = addInput("Password", true);

        addButton("Login", () -> {
            String enteredEmail = email.getText().toString().trim();
            String enteredPassword = password.getText().toString();

            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                showMessage("Please enter your email and password.");
            } else if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(enteredEmail).matches()) {
                showMessage("Please enter a valid email address.");
            } else {
                userEmail = enteredEmail;
                userName = preferences.getString("name", "Demo User");
                showDashboard();
            }
        });

        addButton("Create New Account", this::showRegister);
        addButton("Forgot Password", this::showForgotPassword);

        addLabel("Demo application: login is not connected to a server.");
    }

    // MODULE 1B: REGISTRATION
    private void showRegister() {
        setupScreen("Create Account");

        EditText name = addInput("Full name", false);
        EditText email = addInput("Email address", false);
        EditText password = addInput("Password (minimum 6 characters)", true);
        EditText confirmPassword = addInput("Confirm password", true);

        addButton("Register", () -> {
            String enteredName = name.getText().toString().trim();
            String enteredEmail = email.getText().toString().trim();
            String enteredPassword = password.getText().toString();
            String confirmed = confirmPassword.getText().toString();

            if (enteredName.isEmpty() || enteredEmail.isEmpty()
                    || enteredPassword.isEmpty() || confirmed.isEmpty()) {
                showMessage("Please complete all fields.");
            } else if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(enteredEmail).matches()) {
                showMessage("Please enter a valid email.");
            } else if (enteredPassword.length() < 6) {
                showMessage("Password must contain at least 6 characters.");
            } else if (!enteredPassword.equals(confirmed)) {
                showMessage("Passwords do not match.");
            } else {
                userName = enteredName;
                userEmail = enteredEmail;

                preferences.edit()
                        .putString("name", userName)
                        .putString("email", userEmail)
                        .apply();

                showMessage("Demo registration completed.");
                showDashboard();
            }
        });

        addButton("Back to Login", this::showLogin);
    }

    private void showForgotPassword() {
        setupScreen("Forgot Password");

        EditText email = addInput("Enter your registered email", false);

        addButton("Continue", () -> {
            if (email.getText().toString().trim().isEmpty()) {
                showMessage("Please enter your email.");
            } else {
                showMessage("Password recovery requires a backend service.");
            }
        });

        addButton("Back to Login", this::showLogin);
    }

    // MAIN DASHBOARD: FOUR MODULES
    private void showDashboard() {
        setupScreen("Home Dashboard");

        addLabel("Welcome, " + userName);
        addLabel("Select a module to continue.");

        addButton("Module 1 - Login & Registration",
                this::showLogin);

        addButton("Module 2 - User Profile",
                this::showProfile);

        addButton("Module 3 - Settings",
                this::showSettings);

        addButton("Module 4 - Reports",
                this::showReports);

        addButton("Logout", this::showLogin);
    }

    // MODULE 2: USER PROFILE
    private void showProfile() {
        setupScreen("My Profile");

        addLabel("Update your personal information.");

        nameInput = addInput("Full name", false);
        emailInput = addInput("Email address", false);

        nameInput.setText(userName);
        emailInput.setText(userEmail);

        addButton("Save Profile", () -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty()) {
                showMessage("Name and email cannot be empty.");
            } else if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email).matches()) {
                showMessage("Please enter a valid email address.");
            } else {
                userName = name;
                userEmail = email;

                preferences.edit()
                        .putString("name", userName)
                        .putString("email", userEmail)
                        .apply();

                showMessage("Profile saved successfully.");
                showProfile();
            }
        });

        addButton("Back to Dashboard", this::showDashboard);
    }

    // MODULE 3: SETTINGS
    private void showSettings() {
        setupScreen("Settings");

        addLabel("Customize your application.");

        CheckBox darkMode = new CheckBox(this);
        darkMode.setText("Enable Dark Mode");
        darkMode.setTextColor(textColor);
        darkMode.setChecked(
                preferences.getBoolean("dark_mode", false)
        );
        layout.addView(darkMode);

        CheckBox notifications = new CheckBox(this);
        notifications.setText("Enable Notifications");
        notifications.setTextColor(textColor);
        notifications.setChecked(
                preferences.getBoolean("notifications", true)
        );
        layout.addView(notifications);

        addButton("Save Settings", () -> {
            boolean dark = darkMode.isChecked();
            boolean notify = notifications.isChecked();

            preferences.edit()
                    .putBoolean("dark_mode", dark)
                    .putBoolean("notifications", notify)
                    .apply();

            if (dark) {
                backgroundColor = Color.rgb(35, 39, 47);
                textColor = Color.WHITE;
            } else {
                backgroundColor = Color.rgb(245, 247, 250);
                textColor = Color.rgb(35, 55, 90);
            }

            showMessage("Settings saved.");
            showSettings();
        });

        addButton("Back to Dashboard", this::showDashboard);
    }

    // MODULE 4: REPORTS
    private void showReports() {
        setupScreen("Reports");

        addLabel("Application Summary");
        addLabel("Registered profile: " + userName);
        addLabel("Profile email: " + userEmail);
        addLabel("Available modules: 4");
        addLabel("Module 1: Login & Registration");
        addLabel("Module 2: User Profile");
        addLabel("Module 3: Settings");
        addLabel("Module 4: Reports");

        addButton("Refresh Report", this::showReports);
        addButton("Back to Dashboard", this::showDashboard);
    }
}
