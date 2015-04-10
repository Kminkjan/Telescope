$(document).ready(function(){

    animationClick('#name', '#slogan', '#searchField', '#searchLink');
    
    function animationClick(elementOne, elementTwo, elementThree, elementFour){  

        elementOne = $(elementOne); 
        elementTwo = $(elementTwo); 
        elementThree = $(elementThree); 
        elementFour = $(elementFour); 
        var counter = 0;
        
        //Title
        elementFour.click(               
            function() {              
                elementOne.addClass('animated bounceOutLeft');
                    
                //wait for animation to finish before removing classes        
                window.setTimeout( function(){             
                    elementOne.removeClass('animated bounceOutLeft'); 
                    elementOne.remove();
                }, 800);
            }               
        ); 
        
        //Slogan
        elementFour.click(               
            function() {              
                elementTwo.addClass('animated bounceOutRight');
                    
                //wait for animation to finish before removing classes        
                window.setTimeout( function(){             
                    elementTwo.removeClass('animated bounceOutRight');  
                    elementTwo.remove();
                }, 800); 
            }               
        ); 
        
         //Title
        elementFour.click(               
            function() {        
                
                if(counter === 0){
   
                elementThree.addClass('animated bounceOutUp');
        
                //wait for animation to finish before removing classes        
                window.setTimeout( function(){             
                    elementThree.removeClass('animated bounceOutUp'); 
                    counter++;
                }, 800);
                }
            } 

        );

    }
    $("#inputField").keyup(function(event){
    
            if(event.keyCode == 13){
        
                $("#searchLink").click();
    }
});
});