/**
 * recruiter.js
 * Recruiter (company) profile create / view / update.
 * Backend: GET/POST /job-portal/recruiter/profile/view|create, PUT .../update
 */

function initRecruiterProfilePage() {
  const form = document.getElementById("profileForm");
  if (!form) return;

  const alertBox = document.getElementById("profileAlert");
  const heading = document.getElementById("profileHeading");
  const submitBtn = document.getElementById("profileSubmitBtn");

  let profileExists = false;

  async function loadProfile() {
    try {
      const profile = await api.get("/job-portal/recruiter/profile/view");
      profileExists = true;
      heading.textContent = "Company Profile";
      submitBtn.textContent = "Save Changes";
      document.getElementById("companyName").value = profile.companyName || "";
      document.getElementById("companyDescription").value = profile.companyDescription || "";
      document.getElementById("companyLocation").value = profile.companyLocation || "";
    } catch (err) {
      if (err.status === 404) {
        profileExists = false;
        heading.textContent = "Create Company Profile";
        submitBtn.textContent = "Create Profile";
        showAlert(alertBox, "You haven't set up a company profile yet. You'll need one before posting jobs.", "info");
      } else {
        showApiError(alertBox, err);
      }
    }
  }

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    clearAlert(alertBox);

    const companyName = document.getElementById("companyName").value.trim();
    const companyDescription = document.getElementById("companyDescription").value.trim();
    const companyLocation = document.getElementById("companyLocation").value.trim();

    if (!companyName || !companyDescription || !companyLocation) {
      showAlert(alertBox, "All fields are required.", "error");
      return;
    }

    const body = { companyName, companyDescription, companyLocation };

    submitBtn.disabled = true;
    try {
      if (profileExists) {
        await api.put("/job-portal/recruiter/profile/update", body);
        showAlert(alertBox, "Profile updated.", "success");
      } else {
        await api.post("/job-portal/recruiter/profile/create", body);
        showAlert(alertBox, "Company profile created.", "success");
        profileExists = true;
        heading.textContent = "Company Profile";
        submitBtn.textContent = "Save Changes";
      }
    } catch (err) {
      showApiError(alertBox, err);
    } finally {
      submitBtn.disabled = false;
    }
  });

  loadProfile();
}
