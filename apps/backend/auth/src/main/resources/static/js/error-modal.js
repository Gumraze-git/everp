document.addEventListener('DOMContentLoaded', () => {
    const modal = document.querySelector('[data-error-modal]');
    if (!modal) {
        return;
    }

    modal.classList.add('is-open');

    const fallbackUrl = modal.getAttribute('data-error-fallback');
    const closeButtons = modal.querySelectorAll('[data-error-close]');

    closeButtons.forEach((button) => {
        button.addEventListener('click', () => {
            if (fallbackUrl && fallbackUrl !== "#") {
                window.location.href = fallbackUrl;
                return;
            }
            if (window.history.length > 1) {
                window.history.back();
            } else {
                window.location.href = '/';
            }
        });
    });
});
