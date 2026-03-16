
// 비디오 루프 로직
let currentVideoIndex = 0;
const videoPlayer = document.getElementById('videoPlayer');

// 미디어 쿼리(1024px)와 일치하는 조건 추가
const isMobile = window.matchMedia("(max-width: 1024px)").matches;

if (videoPlayer && typeof VIDEO_PATHS !== 'undefined' && !isMobile) {
    videoPlayer.addEventListener('ended', function() {
        videoPlayer.classList.add('fade-out');

        setTimeout(() => {
            currentVideoIndex = (currentVideoIndex + 1) % VIDEO_PATHS.length;
            videoPlayer.src = VIDEO_PATHS[currentVideoIndex];
            videoPlayer.classList.remove('fade-out');
            videoPlayer.play().catch(err => console.warn('비디오 재생 실패:', err));
        }, 500);
    });
}

// 폼 유효성 검사
const emailInput = document.getElementById('email');
const passwordInput = document.getElementById('password');
const emailError = document.getElementById('emailError');
const passwordError = document.getElementById('passwordError');
const submitBtn = document.getElementById('submitBtn');
const loginForm = document.getElementById('loginForm');
const demoLoginToggle = document.getElementById('demoLoginToggle');
const demoLoginMenu = document.getElementById('demoLoginMenu');
const demoLoginItems = Array.from(document.querySelectorAll('.demo-login-item'));
const demoLoginContainer = document.querySelector('.demo-login');
let isSubmitting = false;

// 정규식
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const passwordRegex = /^.{8,}$/;

/**
 * 이메일 유효성 검사
 */
function validateEmail() {
    const email = emailInput.value.trim();

    // 빈 값
    if (email === '') {
        emailError.textContent = '';
        emailInput.classList.remove('error', 'valid');
        return true; // 빈 값은 에러 아님 (required는 HTML이 처리)
    }

    // 유효하지 않은 형식
    if (!emailRegex.test(email)) {
        emailError.textContent = '이메일 형식이 올바르지 않습니다.';
        emailInput.classList.add('error');
        emailInput.classList.remove('valid');
        return false;
    }

    // 유효한 형식
    emailError.textContent = '';
    emailInput.classList.remove('error');
    emailInput.classList.add('valid');
    return true;
}

/**
 * 비밀번호 유효성 검사
 */
function validatePassword() {
    const password = passwordInput.value;

    // 빈 값
    if (password === '') {
        passwordError.textContent = '';
        passwordInput.classList.remove('error', 'valid');
        return true; // 빈 값은 에러 아님 (required는 HTML이 처리)
    }

    // 유효하지 않은 형식
    if (!passwordRegex.test(password)) {
        passwordError.textContent = '비밀번호는 8자리 이상이어야 합니다.';
        passwordInput.classList.add('error');
        passwordInput.classList.remove('valid');
        return false;
    }

    // 유효한 형식
    passwordError.textContent = '';
    passwordInput.classList.remove('error');
    passwordInput.classList.add('valid');
    return true;
}

function closeDemoLoginMenu() {
    if (!demoLoginToggle || !demoLoginMenu) {
        return;
    }

    demoLoginToggle.setAttribute('aria-expanded', 'false');
    demoLoginMenu.hidden = true;
}

function openDemoLoginMenu() {
    if (!demoLoginToggle || !demoLoginMenu) {
        return;
    }

    demoLoginToggle.setAttribute('aria-expanded', 'true');
    demoLoginMenu.hidden = false;
}

function toggleDemoLoginMenu() {
    if (!demoLoginToggle || !demoLoginMenu) {
        return;
    }

    const expanded = demoLoginToggle.getAttribute('aria-expanded') === 'true';
    if (expanded) {
        closeDemoLoginMenu();
        return;
    }

    openDemoLoginMenu();
}

function disableLoginActions() {
    submitBtn.disabled = true;

    if (demoLoginToggle) {
        demoLoginToggle.disabled = true;
    }

    demoLoginItems.forEach((item) => {
        item.disabled = true;
    });
}

function submitDemoLogin(email, password) {
    if (isSubmitting) {
        return;
    }

    emailInput.value = email;
    passwordInput.value = password;

    const isEmailValid = validateEmail();
    const isPasswordValid = validatePassword();

    if (!isEmailValid || !isPasswordValid) {
        alert('테스트 계정 정보를 다시 확인해주세요.');
        return;
    }

    isSubmitting = true;
    disableLoginActions();
    loginForm.requestSubmit();
}

// blur 이벤트 (포커스를 잃을 때 검사)
emailInput.addEventListener('blur', validateEmail);
passwordInput.addEventListener('blur', validatePassword);

// input 이벤트 (입력 중에도 실시간 검사 - 이미 에러가 표시된 경우에만)
emailInput.addEventListener('input', function() {
    if (emailInput.classList.contains('error') || emailInput.classList.contains('valid')) {
        validateEmail();
    }
});

passwordInput.addEventListener('input', function() {
    if (passwordInput.classList.contains('error') || passwordInput.classList.contains('valid')) {
        validatePassword();
    }
});

// 폼 제출 시 최종 검사
loginForm.addEventListener('submit', function(e) {
    if (isSubmitting) {
        return;
    }

    const isEmailValid = validateEmail();
    const isPasswordValid = validatePassword();

    if (!isEmailValid || !isPasswordValid) {
        e.preventDefault();
        alert('입력한 정보를 다시 확인해주세요.');
        return;
    }

    // 중복 제출 방지
    isSubmitting = true;
    disableLoginActions();
});

const retryButton = document.getElementById('retryButton');
if (retryButton) {
    retryButton.addEventListener('click', () => {
        if (passwordInput) {
            passwordInput.value = '';
        }
        if (emailInput) {
            emailInput.focus();
        }
    });
}

if (demoLoginToggle && demoLoginMenu) {
    demoLoginToggle.addEventListener('click', (event) => {
        event.stopPropagation();

        if (isSubmitting) {
            return;
        }

        toggleDemoLoginMenu();
    });

    demoLoginItems.forEach((item) => {
        item.addEventListener('click', (event) => {
            event.stopPropagation();

            if (isSubmitting) {
                return;
            }

            const email = item.dataset.email;
            const password = item.dataset.password;

            if (!email || !password) {
                return;
            }

            closeDemoLoginMenu();
            submitDemoLogin(email, password);
        });
    });

    document.addEventListener('click', (event) => {
        if (!demoLoginContainer || demoLoginMenu.hidden) {
            return;
        }

        if (!demoLoginContainer.contains(event.target)) {
            closeDemoLoginMenu();
        }
    });

    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') {
            closeDemoLoginMenu();
        }
    });
}
