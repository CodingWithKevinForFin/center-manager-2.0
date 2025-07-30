let lastScrollTop = 0;
let initialLoad = true;

const bottom = document.getElementById("bottomnav");
bottom.style.transform = "translate3d(0, 0, 0)";

const banner = document.getElementById("sticky-banner");
const closeButton = banner.querySelector('[data-dismiss-target="#sticky-banner"]');
const navbar = document.getElementById("navbarwhole");
const startingElement = document.getElementById("starting");

function removeClass(element, className) {
    element.classList.remove(...className.split(" "));
}

startingElement.style.paddingTop = banner.style.display === "none" ? "88px" : "131px";

closeButton.addEventListener("click", () => {
    // Hide the banner
    banner.style.display = "none";

    // Adjust navbar styles
    removeClass(navbar, "mt-[2.7rem]");
    navbar.style.marginTop = "0";

    // Adjust starting padding
    startingElement.style.paddingTop = "88px";
});

window.onscroll = function () {
    if (window.innerWidth > 500) {
        if (!initialLoad) {
            let currentScroll = window.pageYOffset || document.documentElement.scrollTop;
            const top = document.getElementById("topnav");

            if (currentScroll > lastScrollTop) {
                // Scrolling down
                top.style.position = "fixed";
                top.style.width = "100%";
                top.style.zIndex = "100";
                top.style.transform = "translate3d(0, -100%, 0)";
                top.style.visibility = "hidden";
                top.style.transition = "transform 0.2s ease-out";

                navbar.style.height = "4.125rem";

                bottom.style.position = "fixed";
                bottom.style.width = "100%";
                bottom.style.zIndex = "100";
                bottom.style.transform = "translate3d(0, 0, 0)";
            } else {
                // Scrolling up
                top.style.transition = "transform 0.2s ease-in";
                top.style.position = "fixed";
                top.style.width = "100%";
                top.style.zIndex = "100";
                top.style.transform = "translate3d(0, 0, 0)";
                top.style.visibility = "visible";

                navbar.style.height = "6.688rem";

                bottom.style.position = "fixed";
                bottom.style.width = "100%";
                bottom.style.zIndex = "100";
                bottom.style.transform = "translate3d(0, 45px, 0)";
            }

            lastScrollTop = Math.max(0, currentScroll);
        }

        initialLoad = false;
    }
};