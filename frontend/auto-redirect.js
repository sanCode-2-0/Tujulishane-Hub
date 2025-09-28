// Auto-redirect script for index.html

(function () {
  // Check if user is already logged in (using localStorage key 'accessToken')
  if (localStorage.getItem("accessToken")) {
    // Redirect to post-login dashboard or homepage
    window.location.href = "index-post-login.html";
  }
})();
