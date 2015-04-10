window.onload = function() {
    
	$('.paper').hide();
	
    var screenx = screen.width;
	var screeny = screen.height;

	var finalx = screenx - 200;
	var finaly = screeny - 200;
    
	var paper = new Raphael(document.getElementById('canvas_container'), finalx, finaly);
    
	var sitez = this;
    // First, checks if it isn't implemented yet.
    String.prototype.format = function() {
        var s = this,
        i = arguments.length;

        while (i--) {
            s = s.replace(new RegExp('\\{' + i + '\\}', 'gm'), arguments[i]);
        }
        return s;
    };
    
    // initiaties
    var achtergrond = paper.rect(0, 0, 1750, 900).attr({fill: '#1f272a'}).toBack();
    
    var oldx = 0;
    var oldy = 0;
    var xoffset = 0;
    var yoffset = 0;
    var isDown = false;
    var settingsVisible = false;
    var autoScroll = true;
    
    achtergrond.node.onmousedown = function(e) {
        e.preventDefault();
        oldx = e.clientX;
        oldy = e.clientY;
        isDown = true;
    };
    
    achtergrond.node.onmouseup = function() {
        isDown = false;
        man.refresh();
    };
    
    achtergrond.node.onmousemove = function(e) {
        if (isDown) {
            xoffset -= oldx - e.clientX;
            yoffset -= oldy - e.clientY;
            oldx = e.clientX;
            oldy = e.clientY;
            man.fastrefresh();
        }
    };
    
    achtergrond.node.onmousewheel = function(e) {
        if (e.wheelDelta >= 1) {
            factor += 0.025;
            man.refresh();   
        } else if (e.wheelDelta < -1) {
            if (factor > 0.025)
                factor -= 0.025;
            man.refresh();
        }
    };
    var myObjects = [];
    
    var Manager = function() {
        
        var managerRefresher=setInterval(function () {refreshManager();}, 200);
        var self = this;
        
        function refreshManager() {
            self.recalculatePositions();
            self.refresh();
        }
        this.addObject = function(name, size, color) {
            var contains = false;
            for (var i = 0; i < myObjects.length; i++) {
                if (myObjects[i].from === name) {
                    if (myObjects[i].size < 100) { // TEMP MAX SIZE
                        myObjects[i].addSize(size);
                    }
                    contains = true;
                }
            }
            if (!contains) {
            	//console.log("maak een nieuwe voor " + name);
                myObjects.push(new myObject(name, size, color));  
                myObjects[myObjects.length - 1].spawn();
            }
        };
        
        this.addHyperlink = function(name, destination, count) {
        	if (destination != name) {
	            for (var i = 0; i < myObjects.length; i++) {
	                if (myObjects[i].name == name) {
	                    var hyper = myObjects[i].getHyperlink(destination);
	                    if (hyper === null) {
	                        myObjects[i].addHyperlink(name, destination, count);
	                    } else {
	                        hyper.setCount(count);
	                    }
	                }
	            }
        	}
        };
        
        this.refresh = function() {
            if (selectedAmount === 0) {
                selectedItemsText.attr({text:"No domains selected"});
            } else {
                var pagesScanned = 0;
                var hyperlinked = 0;
                for (var i = 0; i < myObjects.length; i++) {
                    if (myObjects[i].getSelected())
                        pagesScanned+= myObjects[i].size;   

                    for (var h = 0; h < myObjects[i].hyperlinks.length; h++) {
                        if (man.getObject( myObjects[i].hyperlinks[h].url).getSelected()) {
                               hyperlinked += myObjects[i].hyperlinks[h].count;
                        }
                    }
                }
                
                selectedItemsText.attr({text:"Selected domains: " + selectedAmount + "\nScanned pages: " +pagesScanned +"\nReferred " + hyperlinked + " times"});
           }
            
            
            var needsZoom = false;
            var fromTop = false;
            var fromDown = false;
            var fromLeft = false;
            var fromRight = false;
            
            for (var i = 0; i < myObjects.length; i++) {
                myObjects[i].reposition();

                if (xoffset + (myObjects[i].x * factor) <= 0 + (myObjects[i].size * factor)) {
                    fromLeft = true;
                    needsZoom = true;
                }
                
                if (yoffset + (myObjects[i].y * factor) <= 0 + (myObjects[i].size * factor)) {
                    fromTop = true;
                    needsZoom = true;
                }
                
                if (xoffset + (myObjects[i].x * factor) >= finalx - (myObjects[i].size * factor)) {
                    fromRight = true;
                    needsZoom = true;
                }
                
                if (yoffset + (myObjects[i].y * factor) >= finaly - (myObjects[i].size * factor)) {
                    fromDown= true;
                    needsZoom = true;
                }
            }
            
            if (needsZoom && autoScroll) {
                if (fromTop) 
                    yoffset += 50;
                if (fromLeft)
                    xoffset += 50;
                if (factor > 0.025) 
                    factor -= 0.025;
            }

        };
        
        this.recalculatePositions = function() {
            for (var i = 0; i < myObjects.length; i++) {
                for (var x = 0; x < myObjects.length; x++) {
                    myObjects[i].intersect(myObjects[x]);
                }
            }
        };
        
        this.fastrefresh = function() {
            for (var i = 0; i < myObjects.length; i++) {
                myObjects[i].fastreposition();
            }
        };
        
        this.getObject = function(name) {
            for (var i = 0; i < myObjects.length; i++) {
                 if (myObjects[i].name == name) {
                     return myObjects[i];
                 }
            }
            return null;
        };
        
        this.moveSelectedObjects = function(xoff, yoff) {
            for (var i = 0; i < myObjects.length; i++) {
                if (myObjects[i].getSelected() || selectedAmount == 0 ) {
                    myObjects[i].x += xoff;
                    myObjects[i].y += yoff;
                }
            }
            man.refresh();
        }
    };
    
    var idteller = 0;
    var factor = 1.0;
    
    var selectedAmount = 0;
    
    var myObject = function(name, size, color) {
        var self = this;
        this.id = idteller;
        idteller++;
        this.name = name;
        this.x = 400 + (Math.random() * 800);
        this.y = 200 + (Math.random() * 500);
        this.size = size;
        this.color = color;
        this.hyperlinks = [];
        this.from = name;
    
        
        var circle = paper.circle(xoffset + this.x * factor, yoffset + this.y * factor, this.size * factor);
        circle.attr({fill: color, stroke:'white', 'stroke-width':0, cursor: 'pointer' });
        
        var text = paper.text(xoffset + this.x * factor, yoffset + (this.y - this.size / 2) * factor, this.name);
        text.attr({opacity: 0, 'font-size': (this.size / 3) * factor, fill: 'black', cursor: 'pointer'});
        text.animate({opacity: 0.5}, 1000);
        

        var img = new Image();
        img.onload = function () {
            var colorThief = new ColorThief();
            color = colorThief.getColor(img);
            circle.attr({fill:("rgb({0}, {1}, {2})").format(color[0], color[1], color[2])});
        };
        img.crossOrigin = 'Anonymous';
        img.src = 'http://grabicon.com/icon?domain=' + name + '&size=32&origin=roel.nl';
        
        var oldx = 0;
        var oldy = 0;
        var selected = false;
                    
        this.getSelected = function() {
            return selected;   
        };
        
        circle.node.onmouseover = function() {
            if (!selected)  {
                //circle.animate({'stroke-width': 4}, 500, 'bounce');
                //text.animate({opacity: 1}, 500);
            }
        };
	     
        circle.node.onmouseleave = function() {
            if (!selected) {
                //circle.animate({'stroke-width': 0}, 500, 'bounce');
                //text.animate({opacity: 0}, 500);
            }
        };
        
        circle.node.onmouseup = function(e) {
            if (selected) {
                circle.animate({'stroke-width': 0}, 500, 'bounce');
                selected = false;   
                selectedAmount--;
            } else {
                circle.animate({'stroke-width': 10}, 500, 'bounce');
                selectedAmount++;
                selected = true;   
            }
        };
        
        circle.node.onmousedown = function(e) {
            e.preventDefault();
        };
        
        text.node.onmouseover = function() {
            if (!selected)  {
                //circle.animate({'stroke-width': 4}, 500, 'bounce');
                //text.animate({opacity: 1}, 500);
            }
        };
        
        text.node.onmouseleave = function() {
            if (!selected) {
                //circle.animate({'stroke-width': 0}, 500, 'bounce');
                //text.animate({opacity: 0}, 500);
            }
        };
        
        text.node.onmouseup = function() {
            if (selected) {
                circle.animate({'stroke-width': 0}, 500, 'bounce');
                selected = false;   
                selectedAmount--;
            } else {
                circle.animate({'stroke-width': 10}, 500, 'bounce');
                selectedAmount++;
                selected = true;   
            }
        };
        
        text.node.onmousedown = function(e) {
            e.preventDefault();   
        };
        
        this.getHyperlink = function(names) {
            for (var i = 0; i < this.hyperlinks.length; i++) {
                if (this.hyperlinks[i].url === names) {
                    return this.hyperlinks[i];
                }
            }
            return null;
        };
                
        this.addSize = function(amount) {
            this.size += amount;
            this.reposition();
        };
        
        this.reposition = function() {
            for (var i = 0; i < this.hyperlinks.length; i++) {
                this.hyperlinks[i].reposition();
            }
            circle.animate( { r : this.size * factor}, 500, 'bounce');  
            text.animate( { 'font-size' : (this.size / 3) * factor }, 500, 'bounce');
            if (!isDown) {
                circle.animate( { cx : xoffset + this.x * factor}, 500, '>');
                circle.animate( { cy : yoffset + this.y * factor}, 500, '>');
                text.animate( { x : xoffset + this.x * factor}, 500, '>');
                text.animate( { y : yoffset + (this.y - this.size / 2) * factor}, 500, '>');
            }
            circle.toFront();
            text.toFront();
            sidebar.toFront();
            autoScrollText.toFront();
            autoScrollBox.toFront();
            selectionRight.toFront();
            selectionDown.toFront();
            selectionLeft.toFront();
            selectionUp.toFront();
            suggestionsWindow.toFront();
            suggestionText1.toFront();
            suggestionText2.toFront();
            suggestionText3.toFront();
            suggestionText4.toFront();
            suggestionText5.toFront(); 
            suggestionsDown.toFront();
        };
        
        var lastfastrepos = Date.now();
        
        this.fastreposition = function() {
            if (lastfastrepos + 100 < Date.now()) {
                lastfastrepos = Date.now();
                circle.animate( { r : this.size * factor}, 500, 'bounce');  
                text.animate( { 'font-size' : (this.size / 3) * factor }, 500, 'bounce');
                circle.animate( { cx : xoffset + this.x * factor}, 100, '>');
                circle.animate( { cy : yoffset + this.y * factor}, 100, '>');
                text.animate( { x : xoffset + this.x * factor}, 100, '>');
                text.animate( { y : yoffset + (this.y - this.size / 2) * factor}, 100, '>');
                for (var i = 0; i < this.hyperlinks.length; i++) {
                    this.hyperlinks[i].fastreposition();
                }
            }
        };

        this.addHyperlink = function(from, url, count) {
            var orgin = man.getObject(from);
            var target = man.getObject(url);
            
            if (target === null) {
            	return;
            }
            var xchange = (orgin.x - target.x) * 0.2;
            var ychange = (orgin.y - target.y) * 0.2;

            var otherxchange = (target.x - orgin.x) * 0.2;
            var otherychange = (target.y - orgin.y) * 0.2;

            orgin.x = orgin.x + otherxchange;
            orgin.y = orgin.y + otherychange;
            target.x = target.x + xchange;
            target.y = target.y + ychange;

            this.hyperlinks.push(new Hyperlink(from, url, count));
        };
        
        this.getCircle = function() {
            return circle;   
        };
        
        this.getText = function() {
            return text;   
        };
        
        this.getX = function() {
            return this.x;   
        };
        
        this.getY = function() {
            return this.y;   
        };
        
        this.addX = function(value, duration) {
            this.x ++;
        };
        
        this.addY = function(value, duration) {
            this.y ++;
        };
         
        this.spawn = function() {
            circle.animate( { r : 0}, 0);
        };
        
        this.intersect = function(object) {
            var myx = this.x;
            var myy = this.y;
            var mysize = this.size;
            var otherx = object.x;
            var othery = object.y;
            var othersize = object.size;
            
            var xsize = otherx - myx;
            if (xsize < 0) {
                xsize *= -1;
            }
            
            var ysize = othery - myy;
            if (ysize < 0) {
                ysize *= -1;
            }

            var schuineZijde = Math.sqrt((xsize * xsize) + (ysize * ysize)) + 0.5;
            var minSize = mysize + othersize;

            if (schuineZijde - 10 <= minSize && schuineZijde != 0) {
                
                var xchange = (myx - otherx) * 0.2;
                var ychange = (myy - othery) * 0.2;
                                
                var otherxchange = (otherx - myx) * 0.2;
                var otherychange = (othery - myy) * 0.2;
                
                this.x = this.x + xchange;
                this.y = this.y + ychange;
                object.x = object.x + otherxchange;
                object.y = object.y + otherychange;
            }
        };
    };
    
    var Hyperlink = function(from, url, count) {
        this.url = url;
        this.count = count;
        this.from = from;
        var parent = man.getObject(from);
        var target = man.getObject(url);
        
        var lines = [];
        var linecurrents = [];
        var linespeeds = [];

        var startpath = ("M{0},{1} L{2},{3}").format(xoffset + parent.x * factor, yoffset + parent.y * factor, xoffset + target.x * factor, yoffset +  target.y * factor); 
        
        var line = paper.path(startpath);
        line.attr({opacity:0.0, fill:'white', stroke:'white', 'stroke-width': 0});    
        line.animate({opacity:0.05, 'stroke-width': 3 + (Math.random() * 7)});
        
        for (var i = 0; i < 2; i++) {
            lines.push(paper.path("M 0,0"));
            lines[i].attr({'stroke-width': Math.random() * 5, stroke:parent.color, opacity:0.4});
            linecurrents.push(i * 50);
            linespeeds.push(0.5 + (Math.random() * 1.5));
        }
        
        var lineAnimator=setInterval(function () {refresh();}, 40);
        
        function refresh() {
            for (var i = 0; i < lines.length; i++) {
                var subpath3 = line.getSubpath((line.getTotalLength() / 100) * linecurrents[i], (line.getTotalLength() / 100) * (linecurrents[i] + 5));
                lines[i].attr({path:subpath3, stroke:parent.color});
      
                linecurrents[i] += linespeeds[i];
                if (linecurrents[i] > 95)
                    linecurrents[i] = 0;
            }
        }
        this.setCount = function(count) {
            if (this.count != count) {
                for (var i = 0; i < (count - this.count); i++) {
                    if (lines.length < 5) {
                        lines.push(paper.path("M 0,0"));
                        lines[lines.length - 1].attr({stroke:parent.color, opacity:0.4});
                        linecurrents.push(0);
                        linespeeds.push(Math.random() * 3);
                    }
                }
                this.count = count;

                var orgin = man.getObject(this.from);
                var target = man.getObject(this.url);

                var xchange = (orgin.x - target.x) * 0.1;
                var ychange = (orgin.y - target.y) * 0.1;

                var changefactor = count/20;
                if (changefactor > 0.4)
                    changefactor = 0.4;

                var otherxchange = (target.x - orgin.x) * changefactor;
                var otherychange = (target.y - orgin.y) * changefactor;

                orgin.x = orgin.x + otherxchange;
                orgin.y = orgin.y + otherychange;
                target.x = target.x + xchange;
                target.y = target.y + ychange
            }
        };
        
        this.getName = function() {
            return this.url;
        };
        
        this.reposition = function() {
            var newpath = ("M{0},{1} L{2},{3}").format(xoffset + parent.x * factor, yoffset + parent.y * factor, xoffset + target.x * factor, yoffset +  target.y * factor); 
            line.animate({path:newpath}, 500, 'elastic');
        };
        
        var lastfastrepos = Date.now();        
        this.fastreposition = function() {
            if (lastfastrepos + 100 < Date.now()) {
                lastfastrepos = Date.now();
                var newpath = ("M{0},{1} L{2},{3}").format(xoffset + parent.x * factor, yoffset + parent.y * factor, xoffset + target.x * factor, yoffset +  target.y * factor); 
                line.animate({path:newpath}, 100, '>');
            }
        };
    };

    var man = new Manager(); 
       
    //var myVar=setInterval(function () {pollData();}, 1000);
        
    var teller = 0;

    
    var sidebar = paper.rect(15, 15, 100, 30, 2);
    sidebar.attr({fill:'grey', 'fill-opacity':0.01, stroke:'black', 'stroke-width':3, 'stroke-opacity':0.1, cursor:'hand'});
    sidebar.node.onmousedown = function(e) {e.preventDefault();};
    
    var sidebarText = paper.text(65, 30, "Settings");
    sidebarText.attr({'font-size':18, opacity:0.2, fill:'white', cursor:'hand'});
    sidebarText.node.onmousedown = function(e) { e.preventDefault();};
    
    var autoScrollText = paper.text(109, 60, "Auto zoom-out");
    autoScrollText.attr({fill:'grey', opacity:0,'fill-opacity':0.8, 'font-size':18, cursor:'hand'});
    autoScrollText.node.onmousedown = function(e) { e.preventDefault();};
    
    var autoScrollBox = paper.rect(24, 50, 20, 20, 2);
    autoScrollBox.attr({fill:'grey', opacity:0,'fill-opacity':0.8, cursor:'hand'});
    autoScrollBox.node.onmousedown = function(e) { e.preventDefault();};
    
    var selectedItemsText = paper.text(18, 104, "Selected domains: 2\nScanned pages: 2374\nReferred 1230 times");
    selectedItemsText.attr({'text-anchor':"start",fill:'grey', opacity:0,'fill-opacity':0.8, 'font-size':16});
  
    var selectionRight = paper.path("M2,2 L2,25 L22,14 L2,2");
    selectionRight.transform("t140,170 r0");
    selectionRight.attr({cursor:'default', fill:'black', opacity:1});
    selectionRight.node.onmouseup = function() {
        man.moveSelectedObjects(50, 0);
    };
    selectionRight.node.onmousedown = function(e) { e.preventDefault();};
    
    var selectionDown = paper.path("M2,2 L2,25 L22,14 L2,2");
    selectionDown.transform("t85,200 r90");
    selectionDown.attr({cursor:'default', fill:'black', opacity:1});
    selectionDown.node.onmouseup = function() {
        man.moveSelectedObjects(0, 50);
    };
    selectionDown.node.onmousedown = function(e) { e.preventDefault();};
    
    var selectionLeft = paper.path("M2,2 L2,25 L22,14 L2,2");
    selectionLeft.transform("t30,170 r180");
    selectionLeft.attr({cursor:'default', fill:'black', opacity:1});
    selectionLeft.node.onmouseup = function() {
        man.moveSelectedObjects(-50, 0);
    };
    selectionLeft.node.onmousedown = function(e) { e.preventDefault();};
    
    var selectionUp = paper.path("M2,2 L2,25 L22,14 L2,2");
    selectionUp.transform("t85,140 r270");
    selectionUp.attr({cursor:'default', fill:'black', opacity:1});
    selectionUp.node.onmouseup = function() {
        man.moveSelectedObjects(0, -50);
    };
    selectionUp.node.onmousedown = function(e) { e.preventDefault();};
    
    var moveText = paper.text(61, 184, "Move around");
    moveText.attr({'text-anchor':"start",fill:'grey', opacity:0,'fill-opacity':0.6, 'font-size':12});
    moveText.node.onmousedown = function(e) { e.preventDefault();};
  
    selectionRight.hide();
    selectionDown.hide();
    selectionLeft.hide();
    selectionUp.hide();
    
    autoScrollBox.node.onmouseup = function() {
        if (autoScroll) {
            autoScrollBox.attr({'fill-opacity':0.1});   
            autoScroll = false;
        } else {
            autoScrollBox.attr({'fill-opacity':0.8});   
            autoScroll = true;
        }   
    };
    
    autoScrollText.node.onmouseup = function() {
        if (autoScroll) {
            autoScrollBox.attr({'fill-opacity':0.1});   
            autoScroll = false;
        } else {
            autoScrollBox.attr({'fill-opacity':0.8});   
            autoScroll = true;
        }   
    };
    
    sidebar.node.onmouseover = function() {
        sidebar.animate({'fill-opacity':0.2, 'stroke-opacity':2}, 100);
        sidebarText.animate({opacity:0.5}, 100);
    };
    
    sidebar.node.onmouseout = function() {
        if (!settingsVisible) {
            sidebar.animate({fill:'grey', 'fill-opacity':0.01, stroke:'black', 'stroke-width':3, 'stroke-opacity':0.1}, 100);
            sidebarText.animate({'font-size':18, opacity:0.2, fill:'white'}, 100);
        }
    };
    
    sidebar.node.onmouseup = function() {
        if (settingsVisible) {
            settingsVisible = false;
            ShowSettings(settingsVisible);
        } else {
            settingsVisible = true;
            ShowSettings(settingsVisible);
        }
    };
    
    sidebarText.node.onmouseup = function() {
        if (settingsVisible) {
            settingsVisible = false;
            ShowSettings(settingsVisible);
        } else {
            settingsVisible = true;
            ShowSettings(settingsVisible);
        }
    };

    sidebarText.node.onmouseover = function() {
        sidebar.animate({'fill-opacity':0.2, 'stroke-opacity':2}, 100);
        sidebarText.animate({opacity:0.5}, 100);
    };
    
    sidebarText.node.onmouseout = function() {
        if (!settingsVisible) {
            sidebar.animate({fill:'grey', 'fill-opacity':0.01, stroke:'black', 'stroke-width':3, 'stroke-opacity':0.1}, 100);
            sidebarText.animate({'font-size':18, opacity:0.2, fill:'white'}, 100);
        }
    };
    
    function ShowSettings(show) {
        if (show) {
            sidebarText.attr({x:100});
            sidebar.attr({'fill-opacity':0.2, x: 10,height:230, width:180});
            autoScrollBox.attr({opacity:1.0});   
            autoScrollText.attr({opacity:1.0});
            selectedItemsText.attr({opacity:1.0});
            selectionDown.show();
            selectionLeft.show();
            selectionUp.show();
            selectionRight.show();
            moveText.attr({opacity:1.0});
        } else {
            sidebar.attr({'fill-opacity':0.01, x: 15, height:30, width:100});
            sidebarText.attr({x:63});
            autoScrollBox.attr({opacity:0.0});
            autoScrollText.attr({opacity:0.0});
            selectedItemsText.attr({opacity:0.0});
            selectionRight.hide();
            selectionDown.hide();
            selectionLeft.hide();
            selectionUp.hide();
            moveText.attr({opacity:0.0});
        }
    }
    
    var suggestionsWindow = paper.rect((finalx / 2) - 60, 10, 140, 26, 2);
    suggestionsWindow.node.onmousedown = function(e) { e.preventDefault();};
    suggestionsWindow.attr({fill:'white','fill-opacity':0.04});
    suggestionsWindow.node.onmouseover = function() {
        suggestionsWindow.animate({height:130}, 200, '<>');
        suggestionText1.animate({opacity:0.5, y:45}, 200, '<>', 50);
        suggestionText2.animate({opacity:0.5, y:65}, 200, '<>', 100);
        suggestionText3.animate({opacity:0.5, y:85}, 200, '<>', 150);
        suggestionText4.animate({opacity:0.5, y:105}, 200, '<>', 200);
        suggestionText5.animate({opacity:0.5, y:125}, 200, '<>', 250);
    };
    suggestionsWindow.node.onmouseout = function() {
        suggestionsWindow.animate({height:26}, 200, '<>', 200);
        suggestionText1.animate({opacity:0.0, y:25}, 200, '<>');
        suggestionText2.animate({opacity:0.0, y:25}, 200, '<>');
        suggestionText3.animate({opacity:0.0, y:25}, 200, '<>');
        suggestionText4.animate({opacity:0.0, y:25}, 200, '<>');
        suggestionText5.animate({opacity:0.0, y:25}, 200, '<>');
    };
    
    var suggestionsDown = paper.path("M2,2 L2,25 L22,14 L2,2");
    suggestionsDown.transform("t" + finalx / 2 + ",10 r90");
    suggestionsDown.attr({cursor:'default', fill:'white', opacity:0.1});
    suggestionsDown.node.onmouseover = function() {
        suggestionsWindow.animate({height:130}, 200, '<>');
        suggestionText1.animate({opacity:0.5, y:45}, 200, '<>', 50);
        suggestionText2.animate({opacity:0.5, y:65}, 200, '<>', 100);
        suggestionText3.animate({opacity:0.5, y:85}, 200, '<>', 150);
        suggestionText4.animate({opacity:0.5, y:105}, 200, '<>', 200);
        suggestionText5.animate({opacity:0.5, y:125}, 200, '<>', 250);
    };
    suggestionsDown.node.onmousedown = function(e) { e.preventDefault();};

    
    var suggestionText1 = paper.text((finalx / 2) - 50,25,"1").attr({'text-anchor':"start", fill:'white', opacity:0.0, 'font-size':16, cursor:'hand'});
    var suggestionText2 = paper.text((finalx / 2) - 50,25,"2").attr({'text-anchor':"start", fill:'white', opacity:0.0, 'font-size':16, cursor:'hand'});
    var suggestionText3 = paper.text((finalx / 2) - 50,25,"3").attr({'text-anchor':"start", fill:'white', opacity:0.0, 'font-size':16, cursor:'hand'});
    var suggestionText4 = paper.text((finalx / 2) - 50,25,"4").attr({'text-anchor':"start", fill:'white', opacity:0.0, 'font-size':16, cursor:'hand'});
    var suggestionText5 = paper.text((finalx / 2) - 50,25,"5").attr({'text-anchor':"start", fill:'white', opacity:0.0, 'font-size':16, cursor:'hand'});
    
    suggestionText1.node.onmouseover = function() {
        suggestionsWindow.animate({height:130}, 200, '<>');
        suggestionText1.animate({opacity:0.5, y:45}, 200, '<>', 50);
        suggestionText2.animate({opacity:0.5, y:65}, 200, '<>', 100);
        suggestionText3.animate({opacity:0.5, y:85}, 200, '<>', 150);
        suggestionText4.animate({opacity:0.5, y:105}, 200, '<>', 200);
        suggestionText5.animate({opacity:0.5, y:125}, 200, '<>', 250);        
    };
    
    suggestionText2.node.onmouseover = function() {
        suggestionsWindow.animate({height:130}, 200, '<>');
        suggestionText1.animate({opacity:0.5, y:45}, 200, '<>', 50);
        suggestionText2.animate({opacity:0.5, y:65}, 200, '<>', 100);
        suggestionText3.animate({opacity:0.5, y:85}, 200, '<>', 150);
        suggestionText4.animate({opacity:0.5, y:105}, 200, '<>', 200);
        suggestionText5.animate({opacity:0.5, y:125}, 200, '<>', 250);        
    };
    
    suggestionText3.node.onmouseover = function() {
        suggestionsWindow.animate({height:130}, 200, '<>');
        suggestionText1.animate({opacity:0.5, y:45}, 200, '<>', 50);
        suggestionText2.animate({opacity:0.5, y:65}, 200, '<>', 100);
        suggestionText3.animate({opacity:0.5, y:85}, 200, '<>', 150);
        suggestionText4.animate({opacity:0.5, y:105}, 200, '<>', 200);
        suggestionText5.animate({opacity:0.5, y:125}, 200, '<>', 250);        
    };
    
    suggestionText4.node.onmouseover = function() {
        suggestionsWindow.animate({height:130}, 200, '<>');
        suggestionText1.animate({opacity:0.5, y:45}, 200, '<>', 50);
        suggestionText2.animate({opacity:0.5, y:65}, 200, '<>', 100);
        suggestionText3.animate({opacity:0.5, y:85}, 200, '<>', 150);
        suggestionText4.animate({opacity:0.5, y:105}, 200, '<>', 200);
        suggestionText5.animate({opacity:0.5, y:125}, 200, '<>', 250);        
    };
    
    suggestionText5.node.onmouseover = function() {
        suggestionsWindow.animate({height:130}, 200, '<>');
        suggestionText1.animate({opacity:0.5, y:45}, 200, '<>', 50);
        suggestionText2.animate({opacity:0.5, y:65}, 200, '<>', 100);
        suggestionText3.animate({opacity:0.5, y:85}, 200, '<>', 150);
        suggestionText4.animate({opacity:0.5, y:105}, 200, '<>', 200);
        suggestionText5.animate({opacity:0.5, y:125}, 200, '<>', 250);        
    };
    
    suggestionText1.node.onmouseup = function() {
        addSearchSuggestion(suggestionText1.attr("text"));   
    };
    
    suggestionText2.node.onmouseup = function() {
        addSearchSuggestion(suggestionText2.attr("text"));   
    };
    
    suggestionText3.node.onmouseup = function() {
        addSearchSuggestion(suggestionText3.attr("text"));   
    };
    
    suggestionText4.node.onmouseup = function() {
        addSearchSuggestion(suggestionText4.attr("text"));   
    };
    
    suggestionText5.node.onmouseup = function() {
        addSearchSuggestion(suggestionText5.attr("text"));   
    };
    
    suggestionText1.node.onmousedown = function(e) { e.preventDefault();};
    suggestionText2.node.onmousedown = function(e) { e.preventDefault();};
    suggestionText3.node.onmousedown = function(e) { e.preventDefault();};
    suggestionText4.node.onmousedown = function(e) { e.preventDefault();};
    suggestionText5.node.onmousedown = function(e) { e.preventDefault();};
    
    function addSearchSuggestion(text) {
        //console.log("Zoek extra naar: " + text);
    }
    
    
    var tags = [];
	var results = [];
	var suggestions = [];

	$("#searchLink").click(function() {
		
		tags = [];
		results = [];
		suggestions = [];
		
		var input = document.getElementById("inputField").value;
		tags = input.split(" ");

		var jsonArray = JSON.stringify({
			"tags" : tags
		});

		// Het id ontvangen van de searchrequest.
		$.ajax({

			type : "POST",
			url : "resources/backend/",
			headers : {
				"Content-Type" : "application/json"
			},
			dataType : "json",
			data : jsonArray

		}).fail(function(jqXHR, textStatus) {
			if (jqXHR.status == "400") {
				alert("400 Invalid JSON, please refer to Apiary.");
			}

			console.log("Request failed: " + textStatus);

		}).done(function(data) {

			//console.log(data.searchid);

			if (data.suggestions === null) {
				console.log("No suggestions!");
			} else {

				data.suggestions.forEach(function(suggestion) {

					suggestions.push(suggestion);

				});
                
				suggestionText1 = paper.text((finalx / 2) - 50,25,suggestions[0]).attr({'text-anchor':"start", fill:'white', opacity:0.0, 'font-size':16, cursor:'hand'});
			    suggestionText2 = paper.text((finalx / 2) - 50,25,suggestions[1]).attr({'text-anchor':"start", fill:'white', opacity:0.0, 'font-size':16, cursor:'hand'});
			    suggestionText3 = paper.text((finalx / 2) - 50,25,suggestions[2]).attr({'text-anchor':"start", fill:'white', opacity:0.0, 'font-size':16, cursor:'hand'});
			    suggestionText4 = paper.text((finalx / 2) - 50,25,suggestions[3]).attr({'text-anchor':"start", fill:'white', opacity:0.0, 'font-size':16, cursor:'hand'});
			    suggestionText5 = paper.text((finalx / 2) - 50,25,suggestions[4]).attr({'text-anchor':"start", fill:'white', opacity:0.0, 'font-size':16, cursor:'hand'});

			}

			// Refresh elke 2 seconden.
			var myVar = setInterval(function() {
				pollData(data.searchid);
			}, 2000);

			// Refresh elke 5 seconden.
			var myVar2 = setInterval(function() {
				man.refresh();
			}, 200);

		});

		$('.paper').show();
		
	});

	
	var testrating;
    
    function pollData(dataId) {
      
        
		this.dataId = dataId;
		results = [];

		// De resultaten ontvangen van het id.
		$.ajax({

			type : "GET",
			url : "resources/backend/result/" + dataId,
			dataType : "json",

		}).fail(function(jqXHR, textStatus) {

			console.log("API Request failed: " + textStatus);

		}).done(
				function(data) {

					// No result
					if (data === undefined) {

						console.log("No results for query");

					} else {

						//console.log(data);

						// Raphael
						data.results.forEach(function(result) {

							results.push(result);

							man.addObject(result.domain, result.rating, getRandomColor());

							testrating = result.rating;
							
							if (data.hyperLinks.length > 0) {
								data.hyperLinks.forEach(function (hyperlink){
									
									man.addHyperlink(hyperlink.completeurl, hyperlink.hyperlink, hyperlink.amount);
									
								});
							}

						});
					}
				});
		
		//for(var o = 0; o < myObjects.length; o++){
		//	console.log(myObjects.length);
		//}

}
    function getRandomColor() {
		var letters = '0123456789ABCDEF'.split('');
		var color = '#';
		for (var i = 0; i < 6; i++) {
			color += letters[Math.floor(Math.random() * 16)];
		}
		return color;
	}
};
