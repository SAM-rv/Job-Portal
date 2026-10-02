/**
 * jobs.js
 * Everything job-related that isn't an application:
 *  - Candidate: browse/search/filter jobs, job details, apply eligibility
 *  - Recruiter: create job, my jobs, update job
 *
 * IMPORTANT - see the "known limitation" note below before changing this file.
 *
 * KNOWN LIMITATION: the backend has no "GET job by id" endpoint. Job details
 * and the update-job form are populated from the job object the user already
 * has on screen (from the browse/my-jobs list), carried over via
 * sessionStorage. If this page is opened directly (bookmark/refresh) without
 * coming from a list first, there is no backend call this frontend can make
 * to recover the job - it will show a "go back to the list" message instead
 * of inventing an endpoint that doesn't exist.
 */

// TODO: EmploymentTypes enum values are unconfirmed (not present in the
// uploaded backend source). Values are entered as free text below until you
// share EmploymentTypes.java - then this becomes a <select>.
// TODO: JobStatus enum values beyond "Open" are unconfirmed - same treatment.

function formatDate(dateStr) {
  if (!dateStr) return "-";
  return dateStr; // backend sends ISO yyyy-MM-dd, which is readable as-is
}

function isDeadlinePassed(dateStr) {
  if (!dateStr) return false;
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const deadline = new Date(dateStr + "T00:00:00");
  return deadline < today;
}

function isDeadlineTodayOrEarlier(dateStr) {
  // Apply is blocked unless deadline is strictly after today (matches
  // JobApplicationService: !deadline.isAfter(now) -> ExpiredDeadlineException)
  if (!dateStr) return false;
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const deadline = new Date(dateStr + "T00:00:00");
  return deadline <= today;
}

function statusBadge(status) {
  const cls = status === "Open" ? "badge-open" : "badge-closed";
  return `<span class="badge ${cls}">${escapeHtml(status || "-")}</span>`;
}

function skillTags(skills) {
  if (!skills || skills.length === 0) return `<span class="muted">-</span>`;
  return skills.map((s) => `<span class="skill-tag">${escapeHtml(s)}</span>`).join("");
}

// ---------------------------------------------------------------
// Candidate: Browse / Search / Filter Jobs
// ---------------------------------------------------------------
function initBrowseJobsPage() {
  const listEl = document.getElementById("jobList");
  if (!listEl) return;
  const alertBox = document.getElementById("jobsAlert");
  const filterForm = document.getElementById("filterForm");
  const clearBtn = document.getElementById("clearFiltersBtn");

  let myApplications = []; // used to mark "already applied"

  async function loadMyApplications() {
    try {
      myApplications = await api.get("/job-portal/job/application/candidate/view");
    } catch (err) {
      myApplications = []; // non-fatal - apply button will just rely on backend response
    }
  }

  function alreadyApplied(jobId) {
    return myApplications.some(
      (app) => app.job && app.job.Job_Id === jobId && app.status !== "Withdrawn"
    );
  }

  async function loadJobs(filters) {
    clearAlert(alertBox);
    listEl.innerHTML = `<p class="muted">Loading jobs...</p>`;
    try {
      const jobs = await api.get("/job-portal/job/search&filter", filters);
      renderJobs(jobs);
    } catch (err) {
      listEl.innerHTML = "";
      showApiError(alertBox, err);
    }
  }

  function renderJobs(jobs) {
    if (!jobs || jobs.length === 0) {
      listEl.innerHTML = `<div class="empty-state">No jobs match your search right now.</div>`;
      return;
    }
    listEl.innerHTML = jobs.map((job) => jobCardHtml(job)).join("");

    listEl.querySelectorAll("[data-view-id]").forEach((btn) => {
      btn.addEventListener("click", () => {
        const job = jobs.find((j) => String(j.Job_Id) === btn.dataset.viewId);
        sessionStorage.setItem("jp_selected_job", JSON.stringify(job));
        window.location.href = `job-details.html?id=${job.Job_Id}`;
      });
    });

    listEl.querySelectorAll("[data-apply-id]").forEach((btn) => {
      btn.addEventListener("click", async () => {
        btn.disabled = true;
        try {
          await api.post("/job-portal/job/application/create", undefined, { job_id: btn.dataset.applyId });
          toast("Application submitted.", "success");
          btn.outerHTML = `<span class="badge badge-neutral">Already Applied</span>`;
        } catch (err) {
          toast(err.message || "Could not apply.", "error");
          btn.disabled = false;
        }
      });
    });
  }

  function jobCardHtml(job) {
    const closed = job.status !== "Open";
    const expired = isDeadlineTodayOrEarlier(job.applicationDeadline);
    const applied = alreadyApplied(job.Job_Id);

    let actionHtml;
    if (applied) {
      actionHtml = `<span class="badge badge-neutral">Already Applied</span>`;
    } else if (closed) {
      actionHtml = `<span class="badge badge-closed">Closed</span>`;
    } else if (expired) {
      actionHtml = `<span class="badge badge-closed">Deadline Passed</span>`;
    } else {
      actionHtml = `<button class="btn btn-primary btn-sm" data-apply-id="${job.Job_Id}">Apply</button>`;
    }

    return `
      <div class="card">
        <div style="display:flex; justify-content:space-between; gap:1rem; flex-wrap:wrap;">
          <div>
            <h3>${escapeHtml(job.title)}</h3>
            <p class="muted">${escapeHtml(job.company)} &middot; ${escapeHtml(job.location)}</p>
          </div>
          ${statusBadge(job.status)}
        </div>
        <div class="job-detail-row"><span class="label">Salary</span><span>${job.minSalary ?? "-"} - ${job.maxSalary ?? "-"}</span></div>
        <div class="job-detail-row"><span class="label">Employment type</span><span>${escapeHtml(job.empType || "-")}</span></div>
        <div class="job-detail-row"><span class="label">Min. experience</span><span>${job.minExperience ?? "-"} yrs</span></div>
        <div class="job-detail-row"><span class="label">Deadline</span><span>${formatDate(job.applicationDeadline)}</span></div>
        <div class="job-detail-row"><span class="label">Skills</span><span>${skillTags(job.skills)}</span></div>
        <div class="btn-row" style="margin-top:0.75rem;">
          <button class="btn btn-secondary btn-sm" data-view-id="${job.Job_Id}">View Details</button>
          ${actionHtml}
        </div>
      </div>
    `;
  }

  filterForm.addEventListener("submit", (e) => {
    e.preventDefault();
    loadJobs(readFilters());
  });

  clearBtn.addEventListener("click", () => {
    filterForm.reset();
    loadJobs({});
  });

  function readFilters() {
    return {
      title: document.getElementById("f_title").value.trim(),
      location: document.getElementById("f_location").value.trim(),
      skill: document.getElementById("f_skill").value.trim(),
      emp_type: document.getElementById("f_emp_type").value.trim(),
      min_exp: document.getElementById("f_min_exp").value,
      salary: document.getElementById("f_salary").value,
    };
  }

  (async () => {
    await loadMyApplications();
    await loadJobs({});
  })();
}

// ---------------------------------------------------------------
// Candidate: Job Details + Apply
// ---------------------------------------------------------------
function initJobDetailsPage() {
  const container = document.getElementById("jobDetailContainer");
  if (!container) return;
  const alertBox = document.getElementById("detailAlert");

  const params = new URLSearchParams(window.location.search);
  const jobId = params.get("id");
  const stored = sessionStorage.getItem("jp_selected_job");
  const job = stored ? JSON.parse(stored) : null;

  if (!job || String(job.Job_Id) !== String(jobId)) {
    container.innerHTML = "";
    showAlert(
      alertBox,
      "This job's details aren't available directly - the backend doesn't provide a way to fetch a single job by ID. Please go back to Browse Jobs and click \"View Details\" again.",
      "info"
    );
    return;
  }

  render(job);

  async function render(job) {
    const currentUser = getSessionUser();
    const isCandidate = currentUser && currentUser.role === "Candidate";

    let alreadyApplied = false;
    if (isCandidate) {
      try {
        const myApps = await api.get("/job-portal/job/application/candidate/view");
        alreadyApplied = myApps.some((a) => a.job && a.job.Job_Id === job.Job_Id && a.status !== "Withdrawn");
      } catch (e) { /* non-fatal */ }
    }

    const closed = job.status !== "Open";
    const expired = isDeadlineTodayOrEarlier(job.applicationDeadline);

    let actionHtml;
    if (!isCandidate) {
      actionHtml = "";
    } else if (alreadyApplied) {
      actionHtml = `<span class="badge badge-neutral">You already applied to this job</span>`;
    } else if (closed) {
      actionHtml = `<div class="alert alert-info">This job is closed.</div>`;
    } else if (expired) {
      actionHtml = `<div class="alert alert-info">The application deadline has passed.</div>`;
    } else {
      actionHtml = `<button id="applyBtn" class="btn btn-primary">Apply for this job</button>`;
    }

    container.innerHTML = `
      <div class="card">
        <h1>${escapeHtml(job.title)}</h1>
        <p class="muted">${escapeHtml(job.company)} &middot; ${escapeHtml(job.location)}</p>
        <p>${escapeHtml(job.description || "")}</p>
        <div class="spacer"></div>
        <div class="job-detail-row"><span class="label">Status</span>${statusBadge(job.status)}</div>
        <div class="job-detail-row"><span class="label">Salary range</span><span>${job.minSalary ?? "-"} - ${job.maxSalary ?? "-"}</span></div>
        <div class="job-detail-row"><span class="label">Employment type</span><span>${escapeHtml(job.empType || "-")}</span></div>
        <div class="job-detail-row"><span class="label">Min. experience</span><span>${job.minExperience ?? "-"} years</span></div>
        <div class="job-detail-row"><span class="label">Skills required</span><span>${skillTags(job.skills)}</span></div>
        <div class="job-detail-row"><span class="label">Posted date</span><span>${formatDate(job.postedDate)}</span></div>
        <div class="job-detail-row"><span class="label">Application deadline</span><span>${formatDate(job.applicationDeadline)}</span></div>
        <div class="job-detail-row"><span class="label">Posted by</span><span>${escapeHtml(job.recruiterFullName || "-")}</span></div>
        <div class="spacer"></div>
        ${actionHtml}
      </div>
    `;

    const applyBtn = document.getElementById("applyBtn");
    if (applyBtn) {
      applyBtn.addEventListener("click", async () => {
        applyBtn.disabled = true;
        clearAlert(alertBox);
        try {
          await api.post("/job-portal/job/application/create", undefined, { job_id: job.Job_Id });
          showAlert(alertBox, "Application submitted successfully.", "success");
          applyBtn.outerHTML = `<span class="badge badge-neutral">Application submitted</span>`;
        } catch (err) {
          showApiError(alertBox, err);
          applyBtn.disabled = false;
        }
      });
    }
  }
}

// ---------------------------------------------------------------
// Recruiter: Create Job
// ---------------------------------------------------------------
function initCreateJobPage() {
  const form = document.getElementById("createJobForm");
  if (!form) return;
  const alertBox = document.getElementById("createJobAlert");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    clearAlert(alertBox);

    const body = readJobForm(alertBox);
    if (!body) return;

    const submitBtn = form.querySelector("button[type=submit]");
    submitBtn.disabled = true;
    try {
      const job = await api.post("/job-portal/job/create", body);
      showAlert(alertBox, "Job posted successfully.", "success");
      form.reset();
      setTimeout(() => { window.location.href = "jobs.html"; }, 900);
    } catch (err) {
      showApiError(alertBox, err);
    } finally {
      submitBtn.disabled = false;
    }
  });
}

// Shared reader/validator for the JobRequest fields (create + update forms
// use the same field ids).
function readJobForm(alertBox) {
  const title = document.getElementById("j_title").value.trim();
  const description = document.getElementById("j_description").value.trim();
  const company = document.getElementById("j_company").value.trim();
  const location = document.getElementById("j_location").value.trim();
  const minSalary = document.getElementById("j_minSalary").value;
  const maxSalary = document.getElementById("j_maxSalary").value;
  const skillsRaw = document.getElementById("j_skills").value.trim();
  const minExperience = document.getElementById("j_minExperience").value;
  const empType = document.getElementById("j_empType").value.trim();
  const deadline = document.getElementById("j_deadline").value;

  if (!title || !description || !company || !location || !empType || !deadline) {
    showAlert(alertBox, "Please fill in all required fields.", "error");
    return null;
  }
  if (minSalary === "" || maxSalary === "" || minExperience === "") {
    showAlert(alertBox, "Salary and experience fields are required.", "error");
    return null;
  }
  if (Number(minSalary) > Number(maxSalary)) {
    showAlert(alertBox, "Minimum salary cannot be greater than maximum salary.", "error");
    return null;
  }
  const skills = skillsRaw.split(",").map((s) => s.trim()).filter((s) => s.length > 0);
  if (skills.length === 0) {
    showAlert(alertBox, "Please list at least one required skill (comma separated).", "error");
    return null;
  }

  return {
    title,
    description,
    company,
    location,
    minSalary: Number(minSalary),
    maxSalary: Number(maxSalary),
    skills,
    minExperience: Number(minExperience),
    empType,
    deadline, // yyyy-MM-dd from <input type="date">
  };
}

// ---------------------------------------------------------------
// Recruiter: My Jobs
// ---------------------------------------------------------------
function initMyJobsPage() {
  const listEl = document.getElementById("myJobsList");
  if (!listEl) return;
  const alertBox = document.getElementById("myJobsAlert");

  async function load() {
    try {
      const jobs = await api.get("/job-portal/job/view");
      render(jobs);
    } catch (err) {
      showApiError(alertBox, err);
    }
  }

  function render(jobs) {
    if (!jobs || jobs.length === 0) {
      listEl.innerHTML = `<div class="empty-state">You haven't posted any jobs yet. <a href="create-job.html">Create one</a>.</div>`;
      return;
    }
    listEl.innerHTML = `
      <div class="table-wrap">
        <table>
          <thead>
            <tr><th>Title</th><th>Location</th><th>Salary</th><th>Type</th><th>Deadline</th><th>Status</th><th>Actions</th></tr>
          </thead>
          <tbody>
            ${jobs.map((job) => `
              <tr>
                <td>${escapeHtml(job.title)}</td>
                <td>${escapeHtml(job.location)}</td>
                <td>${job.minSalary ?? "-"} - ${job.maxSalary ?? "-"}</td>
                <td>${escapeHtml(job.empType || "-")}</td>
                <td>${formatDate(job.applicationDeadline)}</td>
                <td>${statusBadge(job.status)}</td>
                <td>
                  <div class="btn-row">
                    <button class="btn btn-secondary btn-sm" data-view-id="${job.Job_Id}">View</button>
                    <button class="btn btn-secondary btn-sm" data-edit-id="${job.Job_Id}">Update</button>
                    <button class="btn btn-secondary btn-sm" data-apps-id="${job.Job_Id}">Applications</button>
                  </div>
                </td>
              </tr>
            `).join("")}
          </tbody>
        </table>
      </div>
    `;

    listEl.querySelectorAll("[data-view-id]").forEach((btn) => {
      btn.addEventListener("click", () => {
        const job = jobs.find((j) => String(j.Job_Id) === btn.dataset.viewId);
        sessionStorage.setItem("jp_selected_job", JSON.stringify(job));
        window.location.href = `../candidate/job-details.html?id=${job.Job_Id}`;
      });
    });
    listEl.querySelectorAll("[data-edit-id]").forEach((btn) => {
      btn.addEventListener("click", () => {
        const job = jobs.find((j) => String(j.Job_Id) === btn.dataset.editId);
        sessionStorage.setItem("jp_job_to_update", JSON.stringify(job));
        window.location.href = `update-job.html?id=${job.Job_Id}`;
      });
    });
    listEl.querySelectorAll("[data-apps-id]").forEach((btn) => {
      btn.addEventListener("click", () => {
        window.location.href = `job-applications.html?job_id=${btn.dataset.appsId}`;
      });
    });
  }

  load();
}

// ---------------------------------------------------------------
// Recruiter: Update Job
// ---------------------------------------------------------------
function initUpdateJobPage() {
  const form = document.getElementById("updateJobForm");
  if (!form) return;
  const alertBox = document.getElementById("updateJobAlert");

  const params = new URLSearchParams(window.location.search);
  const jobId = params.get("id");
  const stored = sessionStorage.getItem("jp_job_to_update");
  const job = stored ? JSON.parse(stored) : null;

  if (!job || String(job.Job_Id) !== String(jobId)) {
    form.style.display = "none";
    showAlert(
      alertBox,
      "This job's data isn't available directly - the backend has no \"get job by id\" endpoint. Please go back to My Jobs and click \"Update\" again.",
      "info"
    );
    return;
  }

  document.getElementById("j_title").value = job.title || "";
  document.getElementById("j_description").value = job.description || "";
  document.getElementById("j_company").value = job.company || "";
  document.getElementById("j_location").value = job.location || "";
  document.getElementById("j_minSalary").value = job.minSalary ?? "";
  document.getElementById("j_maxSalary").value = job.maxSalary ?? "";
  document.getElementById("j_skills").value = (job.skills || []).join(", ");
  document.getElementById("j_minExperience").value = job.minExperience ?? "";
  document.getElementById("j_empType").value = job.empType || "";
  document.getElementById("j_deadline").value = job.applicationDeadline || "";
  document.getElementById("j_status").value = job.status || "";

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    clearAlert(alertBox);

    const body = readJobForm(alertBox);
    if (!body) return;
    const status = document.getElementById("j_status").value.trim();
    if (!status) {
      showAlert(alertBox, "Status is required.", "error");
      return;
    }

    const submitBtn = form.querySelector("button[type=submit]");
    submitBtn.disabled = true;
    try {
      await api.put("/job-portal/job/update", body, { job_id: jobId, status });
      showAlert(alertBox, "Job updated.", "success");
      setTimeout(() => { window.location.href = "jobs.html"; }, 900);
    } catch (err) {
      showApiError(alertBox, err);
    } finally {
      submitBtn.disabled = false;
    }
  });
}
