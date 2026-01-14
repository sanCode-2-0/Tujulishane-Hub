// config.js
// Single toggle to switch frontend API target between production and development.
// Flip `USE_PROD` to `false` for local development (https://localhost:8080)
// or set up a local override in `frontend/config.local.js` (this file is gitignored
// by default if you create it — see config.local.js.example).

// --- Toggle ---
// true  => use production backend (Heroku)
// false => use local dev backend (localhost:8080)
const USE_PROD = true; // <--- flip this boolean to switch environments

// Backend endpoints - SET THESE ONCE FOR YOUR WHOLE APP
const PROD_URL = "https://api-tujulishane-hub.onrender.com";
const DEV_URL = "http://localhost:8080"; // Local development backend

// Compute base URL from toggle. If a runtime global override is set (by a
// non-committed local file that sets window.__BASE_URL_OVERRIDE), prefer that.
let BASE_URL = USE_PROD ? PROD_URL : DEV_URL;
if (typeof window !== "undefined" && window.__BASE_URL_OVERRIDE) {
  BASE_URL = window.__BASE_URL_OVERRIDE;
}

console.log("BASE URL", BASE_URL);

// Set as global variables for non-module scripts
window.BASE_URL = BASE_URL;
window.DEV_URL = DEV_URL;
window.PROD_URL = PROD_URL;
window.USE_PROD = USE_PROD;

// Also export for module scripts (if needed)
if (typeof module !== "undefined" && module.exports) {
  module.exports = { BASE_URL, DEV_URL, PROD_URL, USE_PROD };
}
