/**
 * applications.js
 * Candidate: view own applications, withdraw.
 * Recruiter: view applications for one of their jobs, update status.
 */

// TODO: ApplicationStatus values beyond "Applied" and "Withdrawn" are
// unconfirmed (not present in the uploaded backend source). The recruiter's
// status field is free text until you share ApplicationStatus.java - then
// this becomes a <select> (excluding "Withdrawn", which the backend already
// rejects from a recruiter).

function appStatusBadge(status) {
  return `<span class="badge badge-status">${escapeHtml(status || "-")}</span>`;
}

// ---------------------------------------------------------------
// Candidate: My Applications
// ---------------------------------------------------------------
function initCandidateApplicationsPage() {
  const listEl = document.getElementById("applicationsList");
  if (!listEl) return;
  const alertBox = document.getElementById("applicationsAlert");

  async function load() {
    try {
      const apps = await api.get("/job-portal/job/application/candidate/view");
      render(apps);
    } catch (err) {
      showApiError(alertBox, err);
    }
  }

  function render(apps) {
    if (!apps || apps.length === 0) {
      listEl.innerHTML = `<div class="empty-state">You haven't applied to any jobs yet. <a href="jobs.html">Browse jobs</a>.</div>`;
      return;
    }
    listEl.innerHTML = `
      <div class="table-wrap">
        <table>
          <thead>
            <tr><th>Job</th><th>Company</th><th>Applied</th><th>Status</th><th></th></tr>
          </thead>
          <tbody>
            ${apps.map((app) => `
              <tr>
                <td>${escapeHtml(app.job?.title || "-")}</td>
                <td>${escapeHtml(app.job?.company || "-")}</td>
                <td>${formatDate(app.Applieddate)}</td>
                <td>${appStatusBadge(app.status)}</td>
                <td>
                  ${app.status !== "Withdrawn"
                    ? `<button class="btn btn-danger btn-sm" data-withdraw-id="${app.AppliCation_Id}">Withdraw</button>`
                    : `<span class="muted">-</span>`}
                </td>
              </tr>
            `).join("")}
          </tbody>
        </table>
      </div>
    `;

    listEl.querySelectorAll("[data-withdraw-id]").forEach((btn) => {
      btn.addEventListener("click", async () => {
        if (!confirm("Withdraw this application?")) return;
        btn.disabled = true;
        try {
          await api.patch("/job-portal/job/application/candidate/withdraw", { application_id: btn.dataset.withdrawId });
          toast("Application withdrawn.", "success");
          load();
        } catch (err) {
          toast(err.message || "Could not withdraw application.", "error");
          btn.disabled = false;
        }
      });
    });
  }

  load();
}

// ---------------------------------------------------------------
// Recruiter: View + update applications for one job
// ---------------------------------------------------------------
function initRecruiterJobApplicationsPage() {
  const listEl = document.getElementById("recruiterAppsList");
  if (!listEl) return;
  const alertBox = document.getElementById("recruiterAppsAlert");
  const titleEl = document.getElementById("jobAppsTitle");

  const params = new URLSearchParams(window.location.search);
  const jobId = params.get("job_id");

  if (!jobId) {
    showAlert(alertBox, "No job selected. Go back to My Jobs and choose \"Applications\" for a job.", "error");
    return;
  }
  titleEl.textContent = `Applications for Job #${jobId}`;

  async function load() {
    try {
      const apps = await api.get("/job-portal/job/application/recruiter/view", { job_id: jobId });
      render(apps);
    } catch (err) {
      showApiError(alertBox, err);
    }
  }

  function render(apps) {
    if (!apps || apps.length === 0) {
      listEl.innerHTML = `<div class="empty-state">No applications yet for this job.</div>`;
      return;
    }
    listEl.innerHTML = apps.map((app) => {
      const c = app.candidateProfileResponse || {};
      return `
        <div class="card">
          <div style="display:flex; justify-content:space-between; flex-wrap:wrap; gap:0.5rem;">
            <h3>${escapeHtml(c.fullName || "-")}</h3>
            ${appStatusBadge(app.status)}
          </div>
          <div class="job-detail-row"><span class="label">Email</span><span>${escapeHtml(c.email || "-")}</span></div>
          <div class="job-detail-row"><span class="label">Phone</span><span>${escapeHtml(c.phoneNumber || "-")}</span></div>
          <div class="job-detail-row"><span class="label">Education</span><span>${escapeHtml(c.education || "-")}</span></div>
          <div class="job-detail-row"><span class="label">Experience</span><span>${c.yearOfExperience ?? "-"} yrs</span></div>
          <div class="job-detail-row"><span class="label">Skills</span><span>${skillTags(c.skills)}</span></div>
          <div class="job-detail-row"><span class="label">Resume</span><span>${c.resumeUrl ? `<a href="${escapeHtml(c.resumeUrl)}" target="_blank" rel="noopener">View resume</a>` : "-"}</span></div>
          <div class="job-detail-row"><span class="label">Applied on</span><span>${formatDate(app.Applieddate)}</span></div>
          <div class="form-row" style="align-items:end; margin-top:0.75rem;">
            <div class="form-group" style="margin-bottom:0;">
              <label>Update status</label>
              <input type="text" data-status-input="${app.AppliCation_Id}" value="${escapeHtml(app.status)}" placeholder="e.g. Applied" />
              <p class="hint">Enter the exact status value from your backend's ApplicationStatus enum (not "Withdrawn" - recruiters can't set that).</p>
            </div>
            <button class="btn btn-primary" data-update-id="${app.AppliCation_Id}">Update Status</button>
          </div>
        </div>
      `;
    }).join("");

    listEl.querySelectorAll("[data-update-id]").forEach((btn) => {
      btn.addEventListener("click", async () => {
        const input = listEl.querySelector(`[data-status-input="${btn.dataset.updateId}"]`);
        const status = input.value.trim();
        if (!status) {
          toast("Please enter a status value.", "error");
          return;
        }
        btn.disabled = true;
        try {
          await api.patch("/job-portal/job/application/recruiter/update", {
            application_id: btn.dataset.updateId,
            status,
          });
          toast("Status updated.", "success");
          load();
        } catch (err) {
          toast(err.message || "Could not update status.", "error");
          btn.disabled = false;
        }
      });
    });
  }

  load();
}
