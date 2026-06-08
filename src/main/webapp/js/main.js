
function togglePassword(fieldId) {
    const input = document.getElementById(fieldId);
    if (!input) return;
    input.type = input.type === 'password' ? 'text' : 'password';
}


function changeQty(delta) {
    const input = document.getElementById('quantity');
    if (!input) return;
    const max = parseInt(input.max) || Infinity;
    const newVal = Math.max(1, Math.min(max, (parseInt(input.value) || 1) + delta));
    input.value = newVal;
}


function showError(fieldId, message) {
    const el = document.getElementById(fieldId + '-error');
    if (el) el.textContent = message;
    const input = document.getElementById(fieldId);
    if (input) input.classList.add('input-error');
}

function clearErrors(form) {
    form.querySelectorAll('.form-error').forEach(el => el.textContent = '');
    form.querySelectorAll('.input-error').forEach(el => el.classList.remove('input-error'));
}


const loginForm = document.getElementById('login-form');
if (loginForm) {
    loginForm.addEventListener('submit', function (e) {
        clearErrors(this);
        let valid = true;
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;

        if (username.length < 3) {
            showError('username', 'Минимум 3 символа');
            valid = false;
        }
        if (password.length < 6) {
            showError('password', 'Минимум 6 символов');
            valid = false;
        }
        if (!valid) e.preventDefault();
    });
}


const registerForm = document.getElementById('register-form');
if (registerForm) {
    const passwordInput = document.getElementById('password');


    if (passwordInput) {
        passwordInput.addEventListener('input', function () {
            const val = this.value;
            const bar = document.getElementById('password-strength');
            if (!bar) return;
            let strength = 0;
            if (val.length >= 6) strength++;
            if (val.length >= 10) strength++;
            if (/[A-Z]/.test(val)) strength++;
            if (/[0-9]/.test(val)) strength++;
            if (/[^A-Za-z0-9]/.test(val)) strength++;
            const colors = ['#ef4444', '#f59e0b', '#eab308', '#22c55e', '#10b981'];
            const widths  = ['20%', '40%', '60%', '80%', '100%'];
            bar.style.background = colors[Math.max(0, strength - 1)] || '#334155';
            bar.style.width = strength > 0 ? widths[strength - 1] : '0';
        });
    }

    registerForm.addEventListener('submit', function (e) {
        clearErrors(this);
        let valid = true;
        const username = document.getElementById('username').value.trim();
        const email    = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;
        const confirm  = document.getElementById('confirmPassword').value;
        const emailRegex = /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/;

        if (username.length < 3) {
            showError('username', 'Минимум 3 символа');
            valid = false;
        }
        if (!emailRegex.test(email)) {
            showError('email', 'Неверный формат email');
            valid = false;
        }
        if (password.length < 6) {
            showError('password', 'Минимум 6 символов');
            valid = false;
        }
        if (password !== confirm) {
            showError('confirm', 'Пароли не совпадают');
            valid = false;
        }
        if (!valid) e.preventDefault();
    });
}

const orderForm = document.getElementById('order-form');
if (orderForm) {
    orderForm.addEventListener('submit', function (e) {
        clearErrors(this);
        const qtyInput = document.getElementById('quantity');
        const qty = parseInt(qtyInput.value);
        const max = parseInt(qtyInput.max);
        if (!qty || qty < 1) {
            showError('qty', 'Количество должно быть больше 0');
            e.preventDefault();
        } else if (qty > max) {
            showError('qty', 'Недостаточно товара на складе');
            e.preventDefault();
        }
    });
}


const productForm = document.getElementById('product-form');
if (productForm) {
    productForm.addEventListener('submit', function (e) {
        clearErrors(this);
        let valid = true;
        const name  = document.getElementById('name').value.trim();
        const price = parseFloat(document.getElementById('price').value);
        const stock = parseInt(document.getElementById('stock').value);

        if (!name) {
            showError('name', 'Введите название товара');
            valid = false;
        }
        if (isNaN(price) || price < 0) {
            showError('price', 'Введите корректную цену');
            valid = false;
        }
        if (isNaN(stock) || stock < 0) {
            showError('stock', 'Количество не может быть отрицательным');
            valid = false;
        }
        if (!valid) e.preventDefault();
    });
}


document.querySelectorAll('.product-card').forEach(card => {
    card.addEventListener('mouseenter', function () {
        this.style.willChange = 'transform';
    });
    card.addEventListener('mouseleave', function () {
        this.style.willChange = 'auto';
    });
});
