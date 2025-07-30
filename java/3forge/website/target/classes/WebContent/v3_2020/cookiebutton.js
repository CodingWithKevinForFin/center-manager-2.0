setCookie = (cName, cValue, expDays) => {
	let date = new Date();
	date.setTime(date.getTime() + (expDays * 24 * 60 * 60 * 1000));
	const expires = "expires=" + date.toUTCString();
	document.cookie = cName + "=" + cValue + "; " + expires + "; path=/";
}

getCookie = (cName) => {
	const name = cName + "=";
	const cDecoded = decodeURIComponent(document.cookie);
	const cArr = cDecoded.split("; ");
	let value;
	cArr.forEach(val => {
		if(val.indexOf(name) === 0) value = val.substring(name.length);
	})
	return value;
}

cookieMessage = () => {
	if (!getCookie("cookie")) {
		const cookieBanner = document.querySelector(".ch2");
		if (cookieBanner) {
			cookieBanner.style.display = "block";
		}
	}
}

window.addEventListener("load", () => {
	const cookieButton = document.querySelector(".ch2-btn");
	if (cookieButton) {
		cookieButton.addEventListener("click", () => {
			const cookieBanner = document.querySelector(".ch2");
			if (cookieBanner) {
				cookieBanner.style.display = "none";
				setCookie("cookie", true, 10);
			}
		});
	}

	cookieMessage();
});
