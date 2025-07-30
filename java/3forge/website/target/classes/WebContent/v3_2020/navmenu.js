function updateTopNav() {
            var topNav = document.querySelector('.top-nav');
            var menuToggle = document.getElementById('menu-toggle');
            var topNavBrand = document.querySelector('.wbs-logo');
            var menuButton = document.querySelector('.menu-button');
            var menu = document.querySelector('.menu');
            if(menuToggle.checked == false && window.innerWidth <=999) {
            	menu.style.opacity = "0";
            }
            
            
            if (window.innerWidth <= 999 && menuToggle.checked) {
                topNav.classList.add('open');
                topNavBrand.classList.add('open');
                menuButton.classList.add('open');
            } else {
                topNav.classList.remove('open');
                topNavBrand.classList.remove('open');
                menuButton.classList.remove('open');
                menu.style.opacity = "1";
            }
        }

document.getElementById('menu-toggle').addEventListener('change', updateTopNav);
window.addEventListener('resize', updateTopNav);