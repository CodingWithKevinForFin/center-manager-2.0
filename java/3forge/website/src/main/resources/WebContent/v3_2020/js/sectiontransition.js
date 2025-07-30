document.addEventListener("DOMContentLoaded", () => {
    const sections = document.querySelectorAll('.transition-section');

    // Function to determine the threshold based on screen size
    const getThreshold = () => {
        const width = window.innerWidth;
        if (width < 600) {
            return 0.1; // Smaller screens
        } else if (width < 1200) {
            return 0.2; // Medium screens
        } else if (width < 1600) {
            return 0.25; // Larger screens
        }
  	      else {
            return 0.3; // Larger screens
        }
    };

    // Initial observer options
    const observerOptions = {
        root: null, // Use the viewport as the root
        rootMargin: '0px',
        threshold: getThreshold()
    };

    // Function to create a new observer with updated options
    const createObserver = () => {
        return new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('visible');
                    observer.unobserve(entry.target); // Stop observing once it's visible
                }
            });
        }, observerOptions);
    };

    let observer = createObserver();

    // Observe each section
    sections.forEach(section => {
        observer.observe(section);
    });

    // Update observer on window resize
    window.addEventListener('resize', () => {
        observer.disconnect(); // Disconnect the old observer
        observerOptions.threshold = getThreshold(); // Update the threshold
        observer = createObserver(); // Create a new observer
        sections.forEach(section => {
            if (!section.classList.contains('visible')) {
                observer.observe(section); // Re-observe sections that are not visible
            }
        });
    });
});