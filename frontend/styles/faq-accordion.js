document.addEventListener('DOMContentLoaded', () => {
    // 1. Get all accordion headers
    const headers = document.querySelectorAll('.accordion-header');

    headers.forEach(header => {
        header.addEventListener('click', () => {
            // Get the parent item (the <li>)
            const item = header.closest('.accordion-item');

            // Get the content body and the icon
            const body = item.querySelector('.accordion-body');
            const icon = header.querySelector('.fa-chevron-down');

            // --- Logic to close other open accordions (optional: single open at a time) ---
            const container = header.closest('.accordion-container');
            const allBodies = container.querySelectorAll('.accordion-body');
            const allIcons = container.querySelectorAll('.fa-chevron-down');

            allBodies.forEach(otherBody => {
                if (otherBody !== body && !otherBody.classList.contains('hidden')) {
                    otherBody.classList.add('hidden');
                }
            });

            allIcons.forEach(otherIcon => {
                if (otherIcon !== icon && otherIcon.classList.contains('rotate-180')) {
                    otherIcon.classList.remove('rotate-180');
                }
            });
            // -----------------------------------------------------------------------------

            // 2. Toggle the hidden class on the body
            body.classList.toggle('hidden');

            // 3. Rotate the chevron icon
            icon.classList.toggle('rotate-180');
        });
    });
});