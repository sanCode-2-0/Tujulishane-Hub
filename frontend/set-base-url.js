// set-base-url.js
// Ensure BASE_URL is set immediately from config.js
(function () {
  // Config.js is already loaded via script tag, so BASE_URL should be in window
  if (!window.BASE_URL) {
    // Fallback to localhost if not set
    window.BASE_URL =
      "https://tujulishane-hub-backend-52b7e709d99f.herokuapp.com";
    console.warn(
      "BASE_URL not found in config.js, using fallback:",
      window.BASE_URL
    );
  } else {
    console.log("BASE_URL loaded from config.js:", window.BASE_URL);
  }
})();
