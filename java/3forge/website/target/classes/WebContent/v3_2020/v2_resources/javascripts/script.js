$(function() {
    // Hide address bar on IOs
    hideBar();

    // Apply background images from data attributes
    backgoundInit();

    // Portfolio hovers
    portfolioHovers();

    // Portfolio filters
    portfolioFilters();

    // Icon hovers
    iconHovers();

    // creeate twitter feeds
    createTwitter();

    // show feature icon description
    showDescription();

    // show mobile nav product menu
    showMobileSubmenus();

    // size documentation iframe
    documentationiframe();

    // set div blocks to window height
    windowHeight();

    // set up the slick carosel
    slickCarosel();

    // display password requirements
    passRules();

});

$(document).ready(function(){

  // initialize

  $('.scrollToTop').css('display','block');
  $('.jsenabled').css('display','block');
  setTimeout(function(){
    if(! /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) && ! navigator.userAgent.toLowerCase().match(/(iphone|ipod|ipad)/) ){$("#menuInfo").fadeIn(1500);}}, 2500);

  // ***TRIAL COUNTDOWN***

  if (document.getElementById('timer')) {

    $('#timer').countdown(expiresOn)
      .on('update.countdown', function(event) {
        $(this).html(event.strftime('<h3 style="color:white;">'
          +'<span class="digit">%D</span>days '
          +'<span class="digit">%H</span>hrs '
          +'<span class="digit">%M</span>min </h3>'));
      })
      .on('finish.countdown', function(event) {
        $(this).html('<p class="lead">Your free trial has ended. Contact our team to request an extension, schedule a demo or to learn more about our AMI One and AMI Enterprise options.</p>');
        $('#banner-message').css('display', 'none');
      });

  };

  // ***UNIVERSAL SCROLL TO TOP BTN***

	// Check to see if the window is top if not then display button

	$(window).scroll(function(){
		if ($(this).scrollTop() > 100) {
			$('.scrollToTop').css('opacity', '1');
		} else {
			$('.scrollToTop').css('opacity', '0');
		}
	});

	//Click event to scroll to top

	$('.scrollToTop').click(function(){
		$('html, body').animate({scrollTop : 0},800);
		return false;
	});

});

// funcrion to hide the address bar on mobile devices
function hideBar() {
    if( ( navigator.userAgent.match(/iPhone/i)) || (navigator.userAgent.match(/iPod/i) ) ) {
        if(window.addEventListener){
            window.addEventListener("load",function() {
                // Set a timeout...
                setTimeout(function(){
                    // Hide the address bar!
                    window.scrollTo(0, 1);
                }, 0);
            });
        }
    }
};

// Function to set data background images
function backgoundInit() {
    $('[data-background]').each(function() {
        var element = $(this);

        element.css('background', element.attr('data-background'));

        if ( element.attr('data-background-size') == "full" ) {
            element.css('background-size', '100%');
            element.css('background-attachment', 'fixed');
        };

    } );
};

// Function to handle the popovers in portfolio
function portfolioHovers() {
    $('.portfolio figure').find('a').on('mouseenter', function(){
        $(this).find('i').animate({
            top: '50%'
            }, 300
        );
    });
    $('.portfolio figure').find('a').on('mouseleave', function(){
        $(this).find('i').animate({
            top: '120%'
            }, 300, function() {
                $(this).css('top', '-100px');
            }
        );
    });
};

// Function to change Social icon colors on hover
function iconHovers(){
    $('[data-iconcolor]').each(function(){
        var element         = $(this);
        var original_color  =$(element).css('color');
        element.on('mouseenter', function(){
            element.css('color' , element.attr('data-iconcolor'));
        });
        element.on('mouseleave', function(){
            element.css('color' ,original_color);
        })

    });
};

// Function to handle the portfolio filters
function portfolioFilters() {
    var filters = $('.portfolio-filters');

    filters.on('click', 'a', function(e) {
        var active = $(this),
            portfolio = filters.next().find('.portfolio');
            activeClass = active.data('filter');


        filters.find('a').removeClass('active');
        active.addClass('active');

        if ( activeClass == 'all') {
            portfolio.find('li').removeClass('inactive');
        } else {
            portfolio.find('li').removeClass('inactive').not('.filter-' + activeClass ).addClass('inactive');
        }

        // manage PIE filters in case of ie8
        if (window.PIE) {
            // remove opacity from all PIE images
            $('ul.portfolio li a.box-inner').each(function(index,val){
            $(this).children().first().removeClass('opaque'); });
            // add opacity to the inactive ones
            $('ul.portfolio li.inactive a.box-inner').each(function(index,val){
            $(this).children().first().addClass('opaque'); });

            // for the squeared portfolio.
            $('ul.portfolio li .no-rounded a').each(function(index,val){
            $(this).removeClass('opaque'); });
            // add opacity to the inactive ones
            $('ul.portfolio li.inactive .no-rounded a').each(function(index,val){
            $(this).addClass('opaque'); });
        }
        e.preventDefault();
    });
};

function showInputMessage( message, status ) {
    var $input = $(':input[name="' + message.field + '"]');
    $input.tooltip( { title: message.message, placement : message.placement, trigger: 'manual' } );
    $input.tooltip( 'show' );
    $input.parents( '.control-group' ).addClass( status );
};

// create twitter feeds
function createTwitter() {
    $( '.twitter-feed' ).each( function() {
        $( this ).tweet({
            count: 3,
            username: 'tweepsum',
            loading_text: "searching twitter...",
            template: '<i class="icon-twitter"></i>{text} <small class="info text-italic"> {time}</small>'
        });
    });
};

// show feature icon description
function showDescription() {

  $('.featureicon-content').click(function() {

    var isSame = $(this)

    // if item clicked already has its description visible, hide description
    if (isSame.hasClass('moveup')) {
      isSame.removeClass('moveup');
    } else {
      // if another description is visible, hide it
      $('.featureicon-content').each(function() {
        var el = $(this);
        if (el.hasClass('moveup')) {
          el.removeClass('moveup');
        }
      });

      // show description of the item clicked
      $(this).toggleClass('moveup');

    }

  });

};

function ajaxNowFiles(url, formName){
	var elements = document.getElementById(formName).elements;
	var formData = new FormData();
	var allowedExtensions = ['pdf'];
	var maxFileSize = 5 * 1024 * 1024;
	
	for (var i = 0, element; element = elements[i++];) {
		if(element.type != 'file')
		{
			formData.append(element.id, element.value);
		}
		else
		{
			if (!element.files || element.files.length === 0) {
				if(document.getElementById('cover_letter').value === ""){
					break; 
				}
			    alert("Please upload a resume.");
			    return false;
			}
			
			if(document.getElementById('app_resume').value === "")
			{
				alert("Please upload a resume.");
				return false;
			}

			if(document.getElementById('transcript') && document.getElementById('transcript').value === "")
			{
				alert("Please upload a transcript.");
				return false;
			}
		
			var fileExtension = element.files[0].name.split('.').pop().toLowerCase();

            if (!allowedExtensions.includes(fileExtension)) {
                alert("Accepted file extensions are: .pdf");
                return false;
            }
            
            if (element.files[0].size > maxFileSize)
            {
            	alert("The submitted file is too big. Please upload a smaller file");
            	return false;
            }
            
			formData.append(element.id, element.files[0]);
		}
	}

	var xhr =new XMLHttpRequest();
	xhr.open("POST",url,true);
	xhr.send(formData);
	
	return true;
	
}

function ajaxNow(url, params) {
    console.log("starting ajax process");
    console.log("Request URL:", url);
    
    var paramsText = joinAndEncodeMap('&', '=', params);
    console.log("making new XMLHTTP request");
    
    var par = new XMLHttpRequest();
    
    par.onerror = function(o) {
        console.error("Error:", o);
        onAjaxError(o);
    };
    
	par.onreadystatechange = function() {
	    console.log("ReadyState:", par.readyState, "Status:", par.status);
	    if (par.readyState === 4) {
	        portletAjaxCallback(par);
	    }
	};

    
    console.log("open request");
    par.open("POST", url, true);
    
    console.log("set request header");
    par.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    
    console.log("send params:", paramsText);
    par.send(paramsText);
    par.paramsText = paramsText;
}

	
  	function escape(unsafe) {
  		return unsafe.replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
  	}
	
	function getFields(formName){
		var elements = document.getElementById(formName).elements;
		var r={};
		for (var i = 0, element; element = elements[i++];) {
		    if (element.id && element.value!="" && element.value!=null){
		    	if(element.type=='radio' && !element.checked)
		    		continue;
		    	else if (element.type == 'checkbox' && !element.checked)
		    		continue;
		    	r[element.id]=escape(element.value);
		    }
		}
		return r;
	}
	
	function onAjaxError(o) {
	    console.log("It appears the web server is not responding.<BR>Please refresh to try again testing (press F5)" + o);
	}
	function portletAjaxCallback(origReq) {
	    if (origReq.readyState != 4 || origReq.status != 200) {
	        return;
	    }

	    try {
	        var code = origReq.responseText;
	        const errorMatch = code.match(/showError\('(.+?)'\)/);

	        if (errorMatch) {
	            showError(errorMatch[1]); // Extract and display the error message
	        } else if (code.includes('dashboard.htm')) {
	            window.location.href = '/dashboard.htm';
	        } else if (code.includes('onChangeAccountResponse')) {
	            onChangeAccountResponse(true, '');
	        } else if (code.includes('passwordChanged')) {
	            passwordChanged();
	        } else if (code.includes('onContactSupportResponse')) {
	            var contactSupportMatch = code.match(/\(([^)]+)\)/);
	            if (contactSupportMatch) {
	                onContactSupportResponse(true, contactSupportMatch[1]);
	            }
	        } else if (code.includes('login.html') || code.includes('login')) {
	            window.location.href = '/login';
	        } else if (window.location.href.includes('secure_licenses')) {
	            showLicense(origReq.responseText);
	        } else {
	            // Handle any other cases or default behavior
	            console.warn("Unhandled response code:", code);
	            eval(code);
	        }
	    } catch (e) {
	        onAjaxError();
	    }
	}

	
	function joinAndEncodeMap(delim,eq,map){
	  if(map.length==0)
	    return '';
	  var r='';
	  var first=true;
	  for(var i in map){
		  var val=map[i];
		  if(val!=null){
	       if(first)
	         first=false;
	       else
	         r+=delim;
	       r+=encodeURIComponent(i)+eq;

	       //TODO: this should be nested
	       if(val instanceof Array){
	    	   for(var i in val){
	    		   if(i>0)
	    			   r+=',';
	    		   r+=encodeURIComponent(val[i].toString().replace(/\\/g,'\\\\').replace(/,/g,'\\,').replace(/</g, "&lt;").replace(/>/g, "&gt;"));
	    	   }
	    	   //log('array: '+r);
	       }else
	         r+=encodeURIComponent(val);
		  }
	  }
	  return r;
	};

function showMobileSubmenus() {

  $('.submenu-mobile').each(function() {
    $('.submenu-mobile').hide();
  })

  $('.submenu-link-mobile').click(function() {

    var linkId = this.id;

    switch (linkId) {
      case 'product-submenu-link':
        $('#product-submenu').slideToggle(300);
        $('#solutions-submenu').slideUp(300);
        $('#resources-submenu').slideUp(300);
        $('#company-submenu').slideUp(300);
        break;

      case 'solutions-submenu-link':
        $('#solutions-submenu').slideToggle(300);
        $('#product-submenu').slideUp(300);
        $('#resources-submenu').slideUp(300);
        $('#company-submenu').slideUp(300);
        break;

      case 'resources-submenu-link':
        $('#resources-submenu').slideToggle(300);
        $('#product-submenu').slideUp(300);
        $('#solutions-submenu').slideUp(300);
        $('#company-submenu').slideUp(300);
        break;

      case 'company-submenu-link':
        $('#company-submenu').slideToggle(300);
        $('#product-submenu').slideUp(300);
        $('#solutions-submenu').slideUp(300);
        $('#resources-submenu').slideUp(300);
        break;
    }

  });

};

// setting documentation iframe size
function documentationiframe() {

  // get current height
  var height = window.innerHeight - 110;

  // set the height and make visible

  // with media query
  if (window.innerWidth < 1295) {

    $('.dociframe').css({
      'height': height + 33,
      'opacity': '1'
    });

  } else {

    $('.dociframe').css({
      'height': height + 104,
      'opacity': '1'
    });

  }

  // update height with resize
  $(window).resize(function() {

    // get new height
    height = window.innerHeight - 110;

    // with media query
    if (window.innerWidth < 1295) {

      $('.dociframe').css({
        'height': height + 33,
        'opacity': '1'
      });

    } else {

      $('.dociframe').css({
        'height': height + 104,
        'opacity': '1'
      });

    }

  });

};

// setting div blocks to window height
function windowHeight() {

  // scroll down button for full window height containers

  $('#scroll-down').click(function(e) {
    var scrollPosition = $('body').scrollTop();

    if (scrollPosition == 0) {
      $('html, body').animate({scrollTop : height}, 800);
    } else {
      e.preventDefault(); // don't scroll if viewport is not at the top of the page
    }

  });

  // responsive

  if (window.innerWidth < 780 || window.innerHeight < 710) {
    $('#scroll-down-container').css('visibility', 'hidden');
  } else {
    $('#scroll-down-container').css('visibility', 'visible');
  }

  $(window).resize(function() {
    if (window.innerWidth < 780 || window.innerHeight < 710) {
      $('#scroll-down-container').css('visibility', 'hidden');
    } else {
      $('#scroll-down-container').css('visibility', 'visible');
    }
  });

};

// slick carosel
function slickCarosel() {

  // check for slider

  if (document.getElementById('slider-for')) {

      $('.client-slider').slick({
        centerMode: true,
        autoplay: true,
        autoplaySpeed: 3500,
        draggable: false,
        arrows: true,
        dots: false,
        pauseOnHover: false,
        focusOnSelect: true,
        // centerPadding: '100px',
        slidesToShow: 3,
        responsive: [
        {
          breakpoint: 1000,
          settings: {
            centerMode: false,
            slidesToShow: 1,
            slidesToScroll: 1,
            arrows: true,
            dots: false
          }
        },
        {
          breakpoint: 767,
          settings: "unslick"
        }
      ]
      });

      $('.slider-for').slick({
       slidesToShow: 1,
       slidesToScroll: 1,
       arrows: false,
       fade: true,
       asNavFor: '.slider-nav'
      });

      $('.slider-nav').slick({
       slidesToShow: 3,
       slidesToScroll: 1,
       asNavFor: '.slider-for',
       dots: false,
       draggable: false,
       centerMode: true,
       autoplay: true,
       autoplaySpeed: 4500,
       focusOnSelect: true,
       arrows: true,
      });

  };

};

// create an account form
if (document.getElementById('createacct')) {

  var createAcctBtn = document.getElementById('createacct');
  var microTxt = document.getElementById('microNotice');
  var errorMsg = document.getElementById('error');

  // while submitting the form
  function disableBtn() {
    createAcctBtn.disabled = true;
    createAcctBtn.textContent = 'Submitting...'
    microTxt.textContent = 'You will be redirected momentarily. Please do not refresh the page.';
    errorMsg.innerHTML = ' ';
    setTimeout(stillWorking, 3000);
  };

  // display error
  function showError(text){
    createAcctBtn.disabled = false;
    createAcctBtn.textContent = 'Activate my account';
    microTxt.textContent = 'All fields are required.';
    errorMsg.innerHTML = text;
    $('html, body').animate({scrollTop : 0},800); // bring user to top of page to see error msg
  };

  // change button text after 3s
  function stillWorking() {

    if (errorMsg.innerHTML == ' ') {
      createAcctBtn.textContent = 'Still working...';
    } else {
      createAcctBtn.textContent = 'Activate my account';
    }

  };
  
  // create an account form
	if (document.getElementById('submitapp')) {

	  var submitAppBtn = document.getElementById('submitapp');
	  
	  // while submitting the form
	  function disableBtn() {
	    submitAppBtn.disabled = true;
	    submitAppBtn.textContent = 'Submitting...'
	
	    setTimeout(stillWorking, 3000);
	  };

	  // display error
	  function showError(text){
	    submitAppBtn.disabled = false;
	    submitAppBtn.textContent = 'Activate my account';
	
	    $('html, body').animate({scrollTop : 0},800); // bring user to top of page to see error msg
	  };

	  // change button text after 3s
	  function stillWorking() {
	
	    if (errorMsg.innerHTML == ' ') {
	      submitAppBtn.textContent = 'Still working...';
	    } else {
	      submitAppBtn.textContent = 'Activate my account';
	    }
	
	  };
	 }

  // custom use case text area and character count
  function customUse() {

    var selectMenu = document.getElementById("use");
    var selectValue = selectMenu.options[selectMenu.selectedIndex].value;

    // show text area and character count
    if (selectValue == 'other') {
      $('#customUse').slideDown();
    } else {
      $('#customUse').slideUp();
    }

    // update character count
    (function() {
      var msg = document.getElementById('otheruse');
      var charCount = document.getElementById('charCount');

      msg.addEventListener('focus', updateCounter);
      msg.addEventListener('input', updateCounter);

      function updateCounter(e) {
        var target = e.target || e.srcElement;
        var count = 250 - target.value.length;

        var charMsg = count + ' characters remaining';
        charCount.innerHTML = charMsg;
      }
    }());

  };

  // call custom use function
  document.getElementById('use').addEventListener('change', customUse);

}

// show password rules
function passRules() {

  $('#pass1').focus(function() {
    $('#passrules').slideDown();
  });

  $('#newpass1').focus(function() {
    $('#passrules').slideDown();
  });

};
