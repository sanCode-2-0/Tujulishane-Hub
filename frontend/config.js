// config.js
// Single toggle to switch frontend API target between production and development.
// Flip `USE_PROD` to `false` for local development (https://localhost:8080)
// or set up a local override in `frontend/config.local.js` (this file is gitignored
// by default if you create it — see config.local.js.example).

// --- Toggle ---
// true  => use production backend (Heroku)
// false => use local dev backend (localhost:8080)
const USE_PROD = true; // <--- flip this boolean to switch environments

// Backend endpoints
const PROD_URL = "https://tujulishane-hub-backend-52b7e709d99f.herokuapp.com";
const DEV_URL = "https://tujulishane-hub-backend-52b7e709d99f.herokuapp.com";

// Compute base URL from toggle. If a runtime global override is set (by a
// non-committed local file that sets window.__BASE_URL_OVERRIDE), prefer that.
let BASE_URL = "https://tujulishane-hub-backend-52b7e709d99f.herokuapp.com";
if (typeof window !== "undefined" && window.__BASE_URL_OVERRIDE) {
  BASE_URL = window.__BASE_URL_OVERRIDE;
}

console.log("BASE URL", BASE_URL);
export { BASE_URL, DEV_URL, PROD_URL, USE_PROD };
