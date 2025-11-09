// set-base-url.js
// Validates that BASE_URL is properly set from config.js
(function () {
  if (!window.BASE_URL) {
    // Emergency fallback - should not normally be reached if config.js loads first
    console.error(
      "❌ BASE_URL not found! Make sure config.js is loaded before set-base-url.js"
    );
    window.BASE_URL =
      "https://tujulishane-hub-backend-52b7e709d99f.herokuapp.com";
    console.warn("⚠️ Using emergency fallback URL:", window.BASE_URL);
  } else {
    console.log("✓ BASE_URL loaded from config.js:", window.BASE_URL);
    console.log(
      "  Environment:",
      window.USE_PROD ? "PRODUCTION" : "DEVELOPMENT"
    );
  }
})();
