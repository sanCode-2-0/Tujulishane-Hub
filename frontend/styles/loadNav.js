document.addEventListener("DOMContentLoaded", () => {
    console.log("Loading navigation...");
    fetch("nav.html?v=" + Date.now())
        .then((response) => {
            console.log("Navigation fetch response:", response.status);
            if (!response.ok) throw new Error("Failed to load navigation");
            return response.text();
        })
        .then((data) => {
            console.log("Navigation HTML loaded, length:", data.length);
            document.getElementById("nav-placeholder").innerHTML = data;
            console.log("Navigation inserted into DOM");
            // Initialize Alpine.js on the newly loaded content
            if (window.Alpine) {
                console.log("Alpine.js found, initializing tree");
                window.Alpine.initTree(document.getElementById("nav-placeholder"));
            } else {
                console.log("Alpine.js not found");
            }
            // Trigger navigation loaded event
            window.dispatchEvent(new CustomEvent("navLoaded"));
            console.log("Navigation loaded event dispatched");
        })
        .catch((error) => console.error("Error loading navigation:", error));
});
