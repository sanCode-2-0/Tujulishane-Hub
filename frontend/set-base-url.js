// set-base-url.js
// Load optional local override, then import config.js and set window.BASE_URL
(function () {
  function loadConfigModule() {
    // Use a small module to import the ES module `config.js` and expose BASE_URL
    var m = document.createElement("script");
    m.type = "module";
    m.textContent =
      "import('./config.js').then(m => { window.BASE_URL = m.BASE_URL; }).catch(e => { console.warn('Could not import config.js to set BASE_URL', e); });";
    document.head.appendChild(m);
  }

  // Try to load config.local.js first (optional override)
  try {
    var s = document.createElement("script");
    s.src = "./config.local.js";
    s.async = false;

    // Wait for the script to load (or fail) before loading config.js
    s.onload = function () {
      loadConfigModule();
    };
    s.onerror = function () {
      // config.local.js doesn't exist or failed to load - that's fine
      loadConfigModule();
    };

    document.head.appendChild(s);
  } catch (e) {
    // If script creation fails, just load config module
    loadConfigModule();
  }
})();
