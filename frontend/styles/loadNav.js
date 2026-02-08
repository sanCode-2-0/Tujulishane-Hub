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
            const placeholder = document.getElementById("nav-placeholder");
            // Use Alpine.mutateDom so Alpine properly tracks the new DOM nodes
            if (window.Alpine) {
                console.log("Alpine.js found, inserting nav with mutateDom");
                Alpine.mutateDom(() => {
                    placeholder.innerHTML = data;
                });
                Alpine.initTree(placeholder);
            } else {
                console.log("Alpine.js not found, inserting nav directly");
                placeholder.innerHTML = data;
            }
            console.log("Navigation inserted into DOM");
            // Trigger navigation loaded event
            window.dispatchEvent(new CustomEvent("navLoaded"));
            console.log("Navigation loaded event dispatched");
        })
        .catch((error) => console.error("Error loading navigation:", error));
});
