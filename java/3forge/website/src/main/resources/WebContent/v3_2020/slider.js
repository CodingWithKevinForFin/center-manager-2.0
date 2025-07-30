								var file1 = {}
						        var file1Selected = false;
						        var file2 = {}
						        var file2Selected = false;
						
						        function selectImage(imageNumber) {
						            const inputElement = document.getElementById(`imageUpload${imageNumber}`);
						            const file = inputElement.files[0];
						
						            if (file && file.type.startsWith('image/')) {
						                if (imageNumber === 1) {
						                    file1 = file;
						                    file1Selected = true;
						                    document.querySelector(`label[for="imageUpload${imageNumber}"]`).innerText = 'Change After Image';
						                    document.querySelector(`label[for="imageUpload${imageNumber}"]`).style.backgroundColor = 'green';
						                } else {
						                    file2 = file;
						                    file2Selected = true;
						                    document.querySelector(`label[for="imageUpload${imageNumber}"]`).innerText = 'Change Before Image';
						                    document.querySelector(`label[for="imageUpload${imageNumber}"]`).style.backgroundColor = 'green';
						                }
						
						                if (file1Selected && file2Selected) {
						                    document.querySelector('.showDiff').style.setProperty('visibility', 'visible');
						                }
						            } else {
						                alert('Please select a valid image file.');
						                inputElement.value = '';
						            }
						        }
						
						        function setAspectRatio(imageNumber) {
						            const imageElement = document.querySelector(`.image-${imageNumber}`);
						            const aspectRatio = imageElement.naturalWidth / imageElement.naturalHeight;
						            const aspectRatioString = aspectRatio.toString();
						            document.querySelector('.image-container').style.setProperty('--aspect-ratio', aspectRatioString);
						        }
						
						        const container = document.querySelector('.container');
						        const slider = document.querySelector('.slider');
						
						        slider.addEventListener('input', function (e) {
						            container.style.setProperty('--position', e.target.value + '%');
						        });
						        function loadImages() {
						            if (file1 && file2) {
						                const image1 = document.querySelector('.image-1');
						                const image2 = document.querySelector('.image-2');
						
						                image1.src = URL.createObjectURL(file1);
						                image2.src = URL.createObjectURL(file2);
						
						                const checkAspectRatio = () => {
						                    const aspectRatio1 = image1.naturalWidth / image1.naturalHeight;
						                    const aspectRatio2 = image2.naturalWidth / image2.naturalHeight;
						
						                    if (aspectRatio1 === aspectRatio2) {
						                        setAspectRatio(1);
						                        setAspectRatio(2);
						                        document.querySelector(`label[for="imageUpload1"]`).style.backgroundColor = '#0a192f';
						                        document.querySelector(`label[for="imageUpload2"]`).style.backgroundColor = '#0a192f';
						
						                    } else {
						                        alert('Aspect ratios of the images must be the same. Please select images with the same aspect ratio.');
						                        image1.src = './images/img1.png';
						                        image2.src = './images/img2.png';
						                        document.querySelector(`label[for="imageUpload1"]`).innerText = 'Select After Image';
						                        document.querySelector(`label[for="imageUpload2"]`).innerText = 'Select Before Image';
						                        document.querySelector(`label[for="imageUpload1"]`).style.backgroundColor = '#0a192f';
						                        document.querySelector(`label[for="imageUpload2"]`).style.backgroundColor = '#0a192f';
						                        document.querySelector('.showDiff').style.setProperty('visibility', 'hidden');
						
						                    }
						                };
						
						                image1.onload = checkAspectRatio;
						                image2.onload = checkAspectRatio;
						            }
						         }