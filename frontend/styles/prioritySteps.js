let currentStep = 1;
const totalSteps = 5;

const progressBar = document.getElementById("progressBar");
const progressText = document.getElementById("progressText");
const steps = document.querySelectorAll(".form-step");
const nextBtn = document.getElementById("nextBtn");
const prevBtn = document.getElementById("prevBtn");

function showStep(step) {
  steps.forEach((el) => {
    el.classList.toggle("hidden", el.dataset.step != step);
  });

  const progressPercent = (step / totalSteps) * 100;
  progressBar.style.width = progressPercent + "%";

  const titles = [
    "Project Location",
    "Basic Information",
    "Project Themes",
    "Contact Information",
    "Budget & Objectives",
  ];

  progressText.textContent = `Step ${step} of ${totalSteps}: ${
    titles[step - 1]
  }`;

  prevBtn.style.display = step === 1 ? "none" : "inline-flex";
  nextBtn.textContent = step === totalSteps ? "Submit" : "Next";
}

nextBtn.addEventListener("click", () => {
  console.log(
    "📍 Next button clicked. Current step:",
    currentStep,
    "Total steps:",
    totalSteps
  );

  if (currentStep < totalSteps) {
    currentStep++;
    console.log("➡️ Moving to step:", currentStep);
    showStep(currentStep);
  } else {
    console.log("✅ Final step reached. Triggering form submission...");
    // Trigger form submission
    const form = document.getElementById("projectForm");
    if (form) {
      console.log("📋 Form element found:", form);
      form.dispatchEvent(
        new Event("submit", { cancelable: true, bubbles: true })
      );
      console.log("📤 Form submit event dispatched");
    } else {
      console.error("❌ Form element not found with ID 'projectForm'");
    }
  }
});

prevBtn.addEventListener("click", () => {
  if (currentStep > 1) {
    currentStep--;
    showStep(currentStep);
  }
});

// Initialize
showStep(currentStep);
