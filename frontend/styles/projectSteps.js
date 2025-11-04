let currentStep = 1;
const totalSteps = 6;

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
    "Supporting Documents",
  ];

  progressText.textContent = `Step ${step} of ${totalSteps}: ${
    titles[step - 1]
  }`;

  prevBtn.style.display = step === 1 ? "none" : "inline-flex";
  nextBtn.textContent = step === totalSteps ? "Submit" : "Next";
}

nextBtn.addEventListener("click", () => {
  if (currentStep < totalSteps) {
    currentStep++;
    showStep(currentStep);
  } else {
    // On last step, trigger form submission
    const form = document.getElementById("projectForm");
    if (form) {
      form.dispatchEvent(
        new Event("submit", { bubbles: true, cancelable: true })
      );
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
