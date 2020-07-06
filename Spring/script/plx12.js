﻿const tag = "[plx12.js_v0.41]";
var v1 = bl$("id_div_4_Plx1_v1");
v1.innerHTML = tag+new Date;
//*
v1.g = null; 
v1.g = new classFrame();
v1.g.initGame();
v1.g.startGame();
//*/

function classFrame(){   
  var spList = [];
  var myGamePiece = null;
  var myGameArea = {
    canvas : document.createElement("canvas"),
    start : function() {
        this.canvas.width = 480;
        this.canvas.height = 270;
        this.canvas.style.backgroundColor = "grey";
        this.context = this.canvas.getContext("2d");

        v1.appendChild(this.canvas);
        this.interval = setInterval(updateGameArea, 20);
    },
    clear : function() {
        this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
    }
  } 
  function sprite(width, height, color, x, y) {
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;    
    this.update = function(){ 
        ctx = myGameArea.context;
        ctx.fillStyle = color;
        ctx.fillRect(this.x, this.y, this.width, this.height);
    }
  }
  function updateGameArea() {
    myGameArea.clear();
    myGamePiece.update();
    for(i in spList){
      spList[i].update();
    }
  }

  this.initGame = function () { 
    var tb = blo0.blDiv(v1, v1.id + "tb","tb",blGrey[1]);
    var b1 = blo0.blBtn(tb,tb.id + "b1","b1",blGrey[2]);
    var d1 = blo0.blDiv(v1, v1.id + "d1","d1",blGrey[3]);
    b1.onclick = function(){
      var d = new Date;
      d1.innerHTML = d.getMilliseconds();
      var x = d.getMilliseconds()/3;
      var y = d.getMilliseconds()%100;
      var sp = new sprite(20, 20, "green", x, y);
      spList.push(sp);
    }
  };

  this.startGame = function () { 
    myGamePiece = new sprite(30, 30, "red", 10, 120);
    var s = new sprite(11, 30, "blue", 110, 120);
    spList.push(s);
    myGameArea.start();  
  };  
}

