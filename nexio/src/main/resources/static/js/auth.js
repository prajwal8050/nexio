// Auth Logic
document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('registerForm');
    const loginForm = document.getElementById('loginForm');
    const forgotPasswordForm = document.getElementById('forgotPasswordForm');

    if (document.getElementById('sendOtpBtn')) {
        document.getElementById('sendOtpBtn').addEventListener('click', async () => {
            const email = document.getElementById('email').value;
            if (!email) {
                alert("Please enter your email first.");
                return;
            }
            try {
                const response = await fetch('/api/auth/register/send-otp', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email: email })
                });
                if (response.ok) {
                    alert("OTP sent to " + email);
                } else {
                    const error = await response.text();
                    alert("Error: " + error);
                }
            } catch (err) {
                console.error(err);
                alert("Something went wrong");
            }
        });
    }

    if (document.getElementById('sendForgotOtpBtn')) {
        document.getElementById('sendForgotOtpBtn').addEventListener('click', async () => {
            const email = document.getElementById('email').value;
            if (!email) {
                alert("Please enter your email first.");
                return;
            }
            try {
                const response = await fetch('/api/auth/forgot-password/send-otp', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email: email })
                });
                if (response.ok) {
                    alert("OTP sent to " + email);
                } else {
                    const error = await response.text();
                    alert("Error: " + error);
                }
            } catch (err) {
                console.error(err);
                alert("Something went wrong");
            }
        });
    }

    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (password !== confirmPassword) {
                alert("Passwords do not match!");
                return;
            }

            const data = {
                name: document.getElementById('name').value,
                email: document.getElementById('email').value,
                phone: document.getElementById('phone').value,
                password: password,
                otp: document.getElementById('otp').value
            };

            try {
                const response = await fetch('/api/auth/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(data)
                });

                if (response.ok) {
                    alert("Registration successful! Please login.");
                    window.location.href = 'login.html';
                } else {
                    const error = await response.text();
                    alert("Error: " + error);
                }
            } catch (err) {
                console.error(err);
                alert("Something went wrong");
            }
        });
    }

    if (forgotPasswordForm) {
        forgotPasswordForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (password !== confirmPassword) {
                alert("Passwords do not match!");
                return;
            }

            const data = {
                email: document.getElementById('email').value,
                newPassword: password,
                otp: document.getElementById('otp').value
            };

            try {
                const response = await fetch('/api/auth/reset-password', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(data)
                });

                if (response.ok) {
                    alert("Password reset successful! Please login with your new password.");
                    window.location.href = 'login.html';
                } else {
                    const error = await response.text();
                    alert("Error: " + error);
                }
            } catch (err) {
                console.error(err);
                alert("Something went wrong");
            }
        });
    }

    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const data = {
                email: document.getElementById('email').value,
                password: document.getElementById('password').value
            };

            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(data)
                });

                if (response.ok) {
                    const user = await response.json();
                    localStorage.setItem('user', JSON.stringify(user));
                    if (user.role === 'ADMIN') {
                        window.location.href = 'admin_dashboard.html';
                    } else if (user.role === 'AGENT') {
                        window.location.href = 'agent_dashboard.html';
                    } else {
                        window.location.href = 'dashboard.html';
                    }
                } else {
                    const error = await response.text();
                    alert("Error: " + error);
                }
            } catch (err) {
                console.error(err);
                alert("Something went wrong");
            }
        });
    }
});
