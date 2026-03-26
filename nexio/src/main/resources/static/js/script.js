// Navbar Scroll handle
window.addEventListener('scroll', () => {
    const navbar = document.getElementById('navbar');
    if (window.scrollY > 100) {
        navbar.classList.add('scrolled');
    } else {
        navbar.classList.remove('scrolled');
    }
});

// Intersection Observer for scroll reveal animations
const observerOptions = {
    root: null,
    rootMargin: '0px',
    threshold: 0.1
};

const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.classList.add('visible');
            observer.unobserve(entry.target);
        }
    });
}, observerOptions);

// Check login status and update UI
function checkLoginStatus() {
    const userStr = localStorage.getItem('user');
    const navAuthBtns = document.getElementById('navAuthBtns');
    if (navAuthBtns) {
        if (userStr) {
            const user = JSON.parse(userStr);
            let dashboardLink = 'dashboard.html';
            if (user.role === 'ADMIN') dashboardLink = 'admin_dashboard.html';
            if (user.role === 'AGENT') dashboardLink = 'agent_dashboard.html';

            navAuthBtns.innerHTML = `
                <div style="display: flex; align-items: center; gap: 16px;">
                    <a href="${dashboardLink}" class="btn glass" style="font-size: 0.85rem; padding: 8px 16px;">
                        <i data-lucide="layout-dashboard" style="width:14px; margin-right:6px;"></i> Dashboard
                    </a>
                    <div style="display: flex; align-items: center; gap: 12px; border-left: 1px solid rgba(0,0,0,0.1); padding-left: 16px;">
                        <span style="font-weight: 600; color: var(--text-secondary); font-size: 0.9rem;">${user.name.split(' ')[0]}</span>
                        <a href="profile.html" class="avatar" style="width: 38px; height: 38px; background: var(--primary); color: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; text-decoration: none; font-weight: 700; box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);">
                            ${user.name.charAt(0)}
                        </a>
                    </div>
                </div>
            `;
            lucide.createIcons();
        } else {
            navAuthBtns.innerHTML = `
                <a href="login.html" class="btn btn-white">Login</a>
                <a href="register.html" class="btn btn-primary">Sign Up</a>
            `;
        }
    }
}

function handleAuthError(response) {
    if (response.status === 401 || response.status === 403) {
        localStorage.removeItem('user');
        window.location.href = 'login.html?error=session_expired';
        return true;
    }
    return false;
}

// Add animation classes to elements
document.addEventListener('DOMContentLoaded', () => {
    checkLoginStatus();
    // Hero reveal
    const heroContent = document.querySelector('.hero-content');
    if (heroContent) {
        heroContent.style.opacity = '1';
        heroContent.style.transform = 'translateY(0)';
    }

    // Scroll reveals
    const revealElements = document.querySelectorAll('.card, .service-card, .step-card, .section-header');
    revealElements.forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(30px)';
        el.style.transition = 'all 0.6s cubic-bezier(0.4, 0, 0.2, 1)';
        observer.observe(el);
    });

    // Mock progress bar animation
    const progressBar = document.querySelector('.progress-bar');
    if (progressBar) {
        setTimeout(() => {
            progressBar.style.width = '65%';
        }, 500);
    }
});

// Add .visible class CSS logic into a dynamic style tag if needed, 
// but it's cleaner to handle via the CSS file.
// Let's add the visible class styling to CSS in a moment.

// Smooth scroll for nav links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth'
            });
        }
    });
});

// Mobile Menu Toggle (if hamburger exists)
const mobileMenuBtn = document.querySelector('.mobile-nav-toggle');
const navLinks = document.querySelector('.nav-links');

if (mobileMenuBtn) {
    mobileMenuBtn.addEventListener('click', () => {
        navLinks.classList.toggle('active');
        const icon = mobileMenuBtn.querySelector('i');
        if (navLinks.classList.contains('active')) {
            icon.setAttribute('data-lucide', 'x');
        } else {
            icon.setAttribute('data-lucide', 'menu');
        }
        lucide.createIcons();
    });
}
