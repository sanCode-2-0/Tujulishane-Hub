document.addEventListener("DOMContentLoaded", () => {
    fetch("nav.html")
        .then(response => {
            if (!response.ok) throw new Error("Failed to load navigation");
            return response.text();
        })
        .then(data => {
            document.getElementById("nav-placeholder").innerHTML = data;
        })
        .catch(error => console.error(error));
});
