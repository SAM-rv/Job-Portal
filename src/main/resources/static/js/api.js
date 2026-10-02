/**
 * api.js
 * Single place that knows how to talk to the Spring Boot backend.
 * Change API_BASE_URL here only - every other file uses this one.
 */

// TODO: verify this matches your backend's actual host/port and context-path
// (none of the provided source shows a server.servlet.context-path, so this
// assumes the backend has none).
const API_BASE_URL = "http://localhost:8080";

/**
 * Low level request helper.
 * - Always sends credentials so the JSESSIONID cookie is included (session auth).
 * - Parses JSON when possible, falls back to plain text (logout returns a string).
 * - Throws a normalized error object: { status, message, fieldErrors }
 */
async function apiRequest(path, { method = "GET", body, query } = {}) {
  const url = API_BASE_URL + path + buildQueryString(query);

  const options = {
    method,
    credentials: "include", // required: backend uses HttpSession, not JWT
    headers: {},
  };

  if (body !== undefined) {
    options.headers["Content-Type"] = "application/json";
    options.body = JSON.stringify(body);
  }

  let response;
  try {
    response = await fetch(url, options);
  } catch (networkError) {
    // fetch() itself throws on network failure / CORS block / server down
    throw {
      status: 0,
      message: "Could not reach the server. Make sure the backend is running and CORS is configured.",
      fieldErrors: null,
    };
  }

  const rawText = await response.text();
  let data = null;
  if (rawText) {
    try {
      data = JSON.parse(rawText);
    } catch (parseError) {
      // Some endpoints (e.g. logout) return a plain string, not JSON
      data = rawText;
    }
  }

  if (!response.ok) {
    const message =
      (data && typeof data === "object" && data.message) ? data.message :
      (typeof data === "string" && data) ? data :
      `Request failed (HTTP ${response.status})`;

    throw {
      status: response.status,
      message,
      fieldErrors: (data && typeof data === "object" && data.fieldErrors) ? data.fieldErrors : null,
    };
  }

  return data;
}

function buildQueryString(query) {
  if (!query) return "";
  const params = new URLSearchParams();
  Object.keys(query).forEach((key) => {
    const value = query[key];
    if (value !== undefined && value !== null && value !== "") {
      params.append(key, value);
    }
  });
  const qs = params.toString();
  return qs ? `?${qs}` : "";
}

const api = {
  get: (path, query) => apiRequest(path, { method: "GET", query }),
  post: (path, body, query) => apiRequest(path, { method: "POST", body, query }),
  put: (path, body, query) => apiRequest(path, { method: "PUT", body, query }),
  patch: (path, query) => apiRequest(path, { method: "PATCH", query }),
};

/**
 * Renders a normalized error thrown by apiRequest into an <div class="alert alert-error">
 * placed inside the given container element. Also lists field errors if present.
 */
function showApiError(containerEl, error) {
  if (!containerEl) return;
  let html = `<div class="alert alert-error">${escapeHtml(error.message || "Something went wrong.")}</div>`;
  if (error.fieldErrors) {
    const items = Object.entries(error.fieldErrors)
      .map(([field, msg]) => `<li><strong>${escapeHtml(field)}:</strong> ${escapeHtml(msg)}</li>`)
      .join("");
    html += `<ul class="field-error">${items}</ul>`;
  }
  containerEl.innerHTML = html;
}

function showAlert(containerEl, message, type = "info") {
  if (!containerEl) return;
  containerEl.innerHTML = `<div class="alert alert-${type}">${escapeHtml(message)}</div>`;
}

function clearAlert(containerEl) {
  if (containerEl) containerEl.innerHTML = "";
}

function escapeHtml(str) {
  if (str === null || str === undefined) return "";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

function toast(message, type = "success") {
  const el = document.createElement("div");
  el.className = `toast ${type}`;
  el.textContent = message;
  document.body.appendChild(el);
  setTimeout(() => el.remove(), 3000);
}
