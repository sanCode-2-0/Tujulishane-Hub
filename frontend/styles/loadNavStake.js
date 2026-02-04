document.addEventListener("DOMContentLoaded", () => {
  fetch("Stakeholders/nav.html")
    .then((response) => {
      if (!response.ok) throw new Error("Failed to load navigation");
      return response.text();
    })
    .then((data) => {
      document.getElementById("nav-placeholder").innerHTML = data;
      // Initialize Alpine.js on the newly loaded content
      if (window.Alpine) {
        console.log("Alpine.js found, initializing tree for Stakeholders nav");
        window.Alpine.initTree(document.getElementById("nav-placeholder"));
      } else {
        console.log("Alpine.js not found for Stakeholders nav");
      }
    })
    .catch((error) => console.error(error));
});
