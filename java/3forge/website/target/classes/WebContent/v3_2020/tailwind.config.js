/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./*.{html,js}', './node_modules/flowbite/**/*.js'],
  theme: {
    extend: {
      colors: {
        clifford: '#da373d',
        grey: '#808080',
        offgrey: 'rgb(223 223 223)',
        darkgrey: 'rgb(83 83 83)',
        slightgrey: 'rgb(201, 203, 198)',
        card1: 'rgb(208 213 215)',
        card2: 'rgb(229 233 235)',
        orangey: 'rgb(255 120 0)',
        kindawhite: 'rgb(245 245 245 / 61%)',
        slategrey: '#a4adb7',
        modal: '#14142c;',
        modaltitle: '#9c9aff;',
        indicator1: 'rgb(255 103 47)',
        papercard: '#f7f7f7;',
        darkbg: '#1e2129',
        bluewhite: '#f8fdff',
        threefone: 'rgb(243 102 0)',
        threeftwo: 'rgb(8 143 180)',
        threefthree: 'rgb(120 203 255)',
		lightorange: 'rgb(255 105 0)',
		shadegrey: '#898d9a',
		midblue: '#0975ab',
		whiteshade: '#f9f9f9'
      },
	  	  fontFamily: {
			inter: ['Inter', 'sans-serif'],
		  },
	      animation: {
        	'infinite-scroll': 'infinite-scroll 25s linear infinite',
      	  },
          keyframes: {
	        'infinite-scroll': {
	          from: { transform: 'translateX(0)' },
	          to: { transform: 'translateX(-100%)' },
	        }
      	  }            
    },
  },
  plugins: [
  	require('flowbite/plugin')
  ],
}

