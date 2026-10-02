/**
 * candidate.js
 * Candidate profile create / view / update.
 * Backend: GET/POST /job-portal/candidate/profile/view|create, PUT .../update
 */

function initCandidateProfilePage() {
  const form = document.getElementById("profileForm");
  if (!form) return;

  const alertBox = document.getElementById("profileAlert");
  const heading = document.getElementById("profileHeading");
  const submitBtn = document.getElementById("profileSubmitBtn");

  let profileExists = false;

  async function loadProfile() {
    try {
      const profile = await api.get("/job-portal/candidate/profile/view");
      profileExists = true;
      heading.textContent = "My Profile";
      submitBtn.textContent = "Save Changes";
      document.getElementById("phoneNumber").value = profile.phoneNumber || "";
      document.getElementById("location").value = profile.location || "";
      document.getElementById("education").value = profile.education || "";
      document.getElementById("skills").value = (profile.skills || []).join(", ");
      document.getElementById("yearOfExperience").value =
        profile.yearOfExperience !== null && profile.yearOfExperience !== undefined ? profile.yearOfExperience : "";
      document.getElementById("resumeUrl").value = profile.resumeUrl || "";
    } catch (err) {
      if (err.status === 404) {
        profileExists = false;
        heading.textContent = "Create My Profile";
        submitBtn.textContent = "Create Profile";
        showAlert(alertBox, "You don't have a profile yet. Fill in the details below to create one.", "info");
      } else {
        showApiError(alertBox, err);
      }
    }
  }

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    clearAlert(alertBox);

    const phoneNumber = document.getElementById("phoneNumber").value.trim();
    const location = document.getElementById("location").value.trim();
    const education = document.getElementById("education").value.trim();
    const skillsRaw = document.getElementById("skills").value.trim();
    const yearOfExperience = document.getElementById("yearOfExperience").value;
    const resumeUrl = document.getElementById("resumeUrl").value.trim();

    if (!/^[0-9]{10}$/.test(phoneNumber)) {
      showAlert(alertBox, "Phone number must be exactly 10 digits.", "error");
      return;
    }
    if (!location || !education) {
      showAlert(alertBox, "Location and education are required.", "error");
      return;
    }
    const skills = skillsRaw.split(",").map((s) => s.trim()).filter((s) => s.length > 0);
    if (skills.length === 0) {
      showAlert(alertBox, "Please list at least one skill (comma separated).", "error");
      return;
    }

    const body = {
      phoneNumber,
      location,
      education,
      skills,
      yearOfExperience: yearOfExperience === "" ? null : Number(yearOfExperience),
      resumeUrl: resumeUrl || null,
    };

    submitBtn.disabled = true;
    try {
      if (profileExists) {
        await api.put("/job-portal/candidate/profile/update", body);
        showAlert(alertBox, "Profile updated.", "success");
      } else {
        await api.post("/job-portal/candidate/profile/create", body);
        showAlert(alertBox, "Profile created.", "success");
        profileExists = true;
        heading.textContent = "My Profile";
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
