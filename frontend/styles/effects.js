document.addEventListener("DOMContentLoaded", () => {
    const elements = document.querySelectorAll('.fade');

    const observer = new IntersectionObserver((entries, obs) => {
        entries.forEach((entry, index) => {
            if (entry.isIntersecting) {
                // stagger animations
                setTimeout(() => {
                    entry.target.classList.add('show');
                }, index * 150);

                // Remove this if you want them to animate every time
                obs.unobserve(entry.target);
            }
        });
    }, { threshold: 0.2 });

    elements.forEach(el => observer.observe(el));
});

// document.addEventListener("DOMContentLoaded", () => {
//     const elements = document.querySelectorAll('.fade');

//     const observer = new IntersectionObserver((entries) => {
//         entries.forEach((entry, index) => {
//             if (entry.isIntersecting) {
//                 // stagger animations on enter
//                 setTimeout(() => {
//                     entry.target.classList.add('show');
//                 }, index * 150);
//             } else {
//                 // remove class when leaving viewport so it can re-trigger
//                 entry.target.classList.remove('show');
//             }
//         });
//     }, { threshold: 0.2 });

//     elements.forEach(el => observer.observe(el));
// });
