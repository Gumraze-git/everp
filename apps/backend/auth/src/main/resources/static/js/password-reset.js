const newPasswordInput = document.getElementById('newPassword');
const confirmPasswordInput = document.getElementById('confirmPassword');
const confirmError = document.getElementById('confirmError');
const form = document.querySelector('form');

function validateMatch() {
    if (!confirmPasswordInput.value) {
        confirmError.textContent = '';
        confirmPasswordInput.classList.remove('error');
        return true;
    }

    if (newPasswordInput.value !== confirmPasswordInput.value) {
        confirmError.textContent = '비밀번호가 일치하지 않습니다.';
        confirmPasswordInput.classList.add('error');
        return false;
    }

    confirmError.textContent = '';
    confirmPasswordInput.classList.remove('error');
    return true;
}

confirmPasswordInput.addEventListener('input', validateMatch);
newPasswordInput.addEventListener('input', validateMatch);

form.addEventListener('submit', (event) => {
    if (!validateMatch()) {
        event.preventDefault();
    }
});
