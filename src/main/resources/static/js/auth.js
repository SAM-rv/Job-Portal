/**
 * auth.js
 * Handles login/register/logout and the small bit of client-side "who am I"
 * state used ONLY to drive navigation/UI. The backend session cookie is the
 * real source of authentication truth - this is not a security boundary.
 */

const SESSION_KEY = "jp_user"; // sessionStorage key: { id, fullname, email, role }

function saveSessionUser(user) {
  sessionStorage.setItem(SESSION_KEY, JSON.stringify(user));
}

function getSessionUser() {
  const raw = sessionStorage.getItem(SESSION_KEY);
  return raw ? JSON.parse(raw) : null;
}

function clearSessionUser() {
  sessionStorage.removeItem(SESSION_KEY);
  sessionStorage.removeItem("jp_selected_job");
}

/**
 * Call at the top of every protected page.
 * requiredRole: "Candidate" | "Recruiter" | null (any logged-in role)
 * This is a UX guard only - if the real session has expired server-side,
 * the first API call on the page will fail and show a proper error.
 */
function requireAuth(requiredRole) {
  const user = getSessionUser();
  if (!user) {
    window.location.href = pathToRoot() + "login.html";
    return null;
  }
  if (requiredRole && user.role !== requiredRole) {
    window.location.href = pathToRoot() + (user.role === "Candidate" ? "candidate/dashboard.html" : "recruiter/dashboard.html");
    return null;
  }
  return user;
}

// Pages live one folder deep (candidate/, recruiter/) or at the root.
function pathToRoot() {
  return window.location.pathname.includes("/candidate/") || window.location.pathname.includes("/recruiter/")
    ? "../"
    : "";
}

function renderNavbar(activePage) {
  const user = getSessionUser();
  const navEl = document.getElementById("navbar");
  if (!navEl || !user) return;

  const root = pathToRoot();
  const links = user.role === "Candidate"
    ? [
        ["Dashboard", "candidate/dashboard.html", "dashboard"],
        ["Browse Jobs", "candidate/jobs.html", "jobs"],
        ["My Applications", "candidate/applications.html", "applications"],
        ["My Profile", "candidate/profile.html", "profile"],
      ]
    : [
        ["Dashboard", "recruiter/dashboard.html", "dashboard"],
        ["My Jobs", "recruiter/jobs.html", "jobs"],
        ["Create Job", "recruiter/create-job.html", "create-job"],
        ["Company Profile", "recruiter/profile.html", "profile"],
      ];

  const linksHtml = links
    .map(([label, href, key]) => `<a href="${root}${href}" class="${key === activePage ? "active" : ""}">${label}</a>`)
    .join("");

  navEl.innerHTML = `
    <div class="navbar-inner">
      <div class="brand">Job Portal</div>
      <div class="nav-links">
        ${linksHtml}
        <span class="nav-user">${escapeHtml(user.fullname)} &middot; ${escapeHtml(user.role)}</span>
        <a href="#" id="logoutLink">Logout</a>
      </div>
    </div>
  `;

  document.getElementById("logoutLink").addEventListener("click", async (e) => {
    e.preventDefault();
    await logout();
  });
}

async function logout() {
  try {
    await api.post("/job-portal/auth/logout");
  } catch (err) {
    // Even if the backend call fails (e.g. session already gone), still
    // clear local UI state and send the user back to login.
  }
  clearSessionUser();
  window.location.href = pathToRoot() + "login.html";
}

// ---- Login page wiring ----
function initLoginPage() {
  const form = document.getElementById("loginForm");
  if (!form) return;
  const alertBox = document.getElementById("loginAlert");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    clearAlert(alertBox);

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    if (!email || !password) {
      showAlert(alertBox, "Please enter both email and password.", "error");
      return;
    }

    const submitBtn = form.querySelector("button[type=submit]");
    submitBtn.disabled = true;

    try {
      const response = await api.post("/job-portal/auth/login", { email, password });
      saveSessionUser({
        id: response.id,
        fullname: response.fullname,
        email: response.email,
        role: response.role,
      });
      window.location.href = response.role === "Candidate"
        ? "candidate/dashboard.html"
        : "recruiter/dashboard.html";
    } catch (err) {
      // Note: this backend returns HTTP 404 (not 401) for wrong email/password.
      showApiError(alertBox, err);
    } finally {
      submitBtn.disabled = false;
    }
  });
}

// ---- Register page wiring ----
function initRegisterPage() {
  const form = document.getElementById("registerForm");
  if (!form) return;
  const alertBox = document.getElementById("registerAlert");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    clearAlert(alertBox);

    const fullname = document.getElementById("fullname").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const role = document.querySelector('input[name="role"]:checked')?.value;

    if (!fullname || !email || !password || !role) {
      showAlert(alertBox, "Please fill in every field and choose an account type.", "error");
      return;
    }
    if (password.length < 8 || password.length > 20) {
      showAlert(alertBox, "Password must be between 8 and 20 characters.", "error");
      return;
    }
    if (!/^\S+@\S+\.\S+$/.test(email)) {
      showAlert(alertBox, "Please enter a valid email address.", "error");
      return;
    }

    const submitBtn = form.querySelector("button[type=submit]");
    submitBtn.disabled = true;

    try {
      await api.post("/job-portal/auth/register", { fullname, email, password, role });
      showAlert(alertBox, "Account created. You can now log in.", "success");
      form.reset();
      setTimeout(() => { window.location.href = "login.html"; }, 1200);
    } catch (err) {
      showApiError(alertBox, err);
    } finally {
      submitBtn.disabled = false;
    }
  });
}
