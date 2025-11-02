document.addEventListener("alpine:init", () => {
    Alpine.data("navLoader", () => ({
        html: "",
        loaded: false,
        async init() {
            try {
                console.log("Fetching navigation...");
                const response = await fetch("nav.html?v=" + Date.now());
                if (!response.ok) throw new Error("Failed to fetch nav");
                this.html = await response.text();
                this.loaded = true;

                this.$nextTick(() => {
                    console.log("Navigation inserted, reinitializing UI libraries...");

                    // TwElements reinit (for modals, dropdowns, etc.)
                    const root = this.$root;
                    if (window.twe?.init) window.twe.init(root);
                    if (window.twElements?.init) window.twElements.init(root);
                    if (window.te?.init) window.te.init(root);

                    // Dispatch optional custom event
                    window.dispatchEvent(new CustomEvent("navLoaded"));
                });
            } catch (err) {
                console.error("Error loading nav:", err);
            }
        },
    }));
});