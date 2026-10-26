
/*
 * KnowGrow - Login and Registration
 * Connected to Spring Boot + MySQL backend.
 */

const API_BASE = "/api";

const loginTab = document.getElementById("login-tab");
const registerTab = document.getElementById("register-tab");
const loginForm = document.getElementById("login-form");
const registerForm = document.getElementById("register-form");

// Toast notification
function showToast(message) {
    const toast = document.getElementById("toast");
    toast.textContent = message;
    toast.classList.add("show");

    clearTimeout(showToast.timer);
    showToast.timer = setTimeout(() => {
        toast.classList.remove("show");
    }, 3000);
}

// Switch between login and registration
function switchMode(mode) {
    const isLogin = mode === "login";

    loginTab.classList.toggle("active", isLogin);
    registerTab.classList.toggle("active", !isLogin);

    loginTab.setAttribute("aria-selected", String(isLogin));
    registerTab.setAttribute("aria-selected", String(!isLogin));

    loginForm.classList.toggle("hidden", !isLogin);
    registerForm.classList.toggle("hidden", isLogin);

    document.getElementById("form-eyebrow").textContent =
        isLogin ? "WELCOME BACK" : "GET STARTED";

    document.getElementById("form-title").textContent =
        isLogin ? "Sign in to your account" : "Create your account";

    document.getElementById("form-subtitle").textContent =
        isLogin
            ? "Enter your details to continue to KnowGrow."
            : "Set up your account to join your workspace.";
}

loginTab.addEventListener("click", () => switchMode("login"));
registerTab.addEventListener("click", () => switchMode("register"));

document.querySelectorAll("[data-switch]").forEach(button => {
    button.addEventListener("click", () => {
        switchMode(button.dataset.switch);
    });
});

// Show/hide passwords
document.querySelectorAll("[data-toggle]").forEach(button => {
    button.addEventListener("click", () => {
        const input = document.getElementById(button.dataset.toggle);
        const showing = input.type === "text";

        input.type = showing ? "password" : "text";
        button.textContent = showing ? "Show" : "Hide";
        button.setAttribute(
            "aria-label",
            showing ? "Show password" : "Hide password"
        );
    });
});

// Forgot password
document.getElementById("forgot-password").addEventListener("click", () => {
    showToast("Password reset is not available yet. Contact your administrator.");
});

// LOGIN
loginForm.addEventListener("submit", async event => {
    event.preventDefault();

    const submitButton = loginForm.querySelector(".submit-button");

    const payload = {
        email: document.getElementById("login-email").value.trim(),
        password: document.getElementById("login-password").value
    };

    submitButton.disabled = true;
    submitButton.textContent = "Signing in...";

    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.message || "Login failed.");
        }

        // Store user details for the current browser session
        sessionStorage.setItem(
            "knowgrowUser",
            JSON.stringify(result.user)
        );

        showToast(`Welcome, ${result.user.name}!`);

        console.log("Login successful:", result.user);

        // Redirect after successful login
        window.location.assign("/dashboard.html");

    } catch (error) {
        console.error("Login error:", error);

        if (error instanceof TypeError) {
            showToast(
                "Cannot connect to backend. Check that Spring Boot is running."
            );
        } else {
            showToast(error.message || "Unable to sign in.");
        }
    } finally {
        submitButton.disabled = false;
        submitButton.innerHTML = 'Sign in <span>→</span>';
    }
});

// REGISTRATION
registerForm.addEventListener("submit", async event => {
    event.preventDefault();

    const submitButton = registerForm.querySelector(".submit-button");
    const confirmInput = document.getElementById("confirm-password");

    const name = document.getElementById("register-name").value.trim();
    const email = document.getElementById("register-email").value.trim();
    const role = document.getElementById("register-role").value;
    const password = document.getElementById("register-password").value;
    const confirmPassword = confirmInput.value;

    if (password !== confirmPassword) {
        confirmInput.setCustomValidity("Passwords do not match.");
        confirmInput.reportValidity();
        confirmInput.setCustomValidity("");
        return;
    }

    if (password.length < 8) {
        showToast("Password must contain at least 8 characters.");
        return;
    }

    const payload = { name, email, role, password };

    submitButton.disabled = true;
    submitButton.textContent = "Creating account...";

    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.message || "Registration failed.");
        }

        showToast("Account created successfully! Please sign in.");

        registerForm.reset();
        loginForm.reset();

        switchMode("login");

        document.getElementById("login-email").value = email;

        console.log("Registration successful:", result.user);

    } catch (error) {
        console.error("Registration error:", error);

        if (error instanceof TypeError) {
            showToast(
                "Cannot connect to backend. Check that Spring Boot is running."
            );
        } else {
            showToast(error.message || "Unable to create account.");
        }
    } finally {
        submitButton.disabled = false;
        submitButton.innerHTML = 'Create account <span>→</span>';
    }
});

// Initial page state
switchMode("login");
