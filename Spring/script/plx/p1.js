﻿
const p1Tag = "[plx/p1.js_v0.214]";

const btn4p1 = bl$("plx_p1_btn");

if(btn4p1){ 
    btn4p1.v = blo0.blMD(btn4p1.id+p1Tag,p1Tag,210,88,555,150,blGrey[0]);
    var tb = blo0.blDiv(btn4p1.v,btn4p1.v.id+"tb","tb0",blGrey[1]);
    tb.btnStoryBoard = blo0.blBtn(tb,"btnStoryBoard","storyBoard",blGrey[2]);
    tb.btnStoryBoard.style.float = "left";
    
    tb.btnStoryBoard.onclick = function(){
       if(!this.sb)  this.sb = new CStoryBoard(btn4p1.v);
       this.sb.show(this);
    }
    
    tb.btnPlayground = blo0.blBtn(tb,"btnPlayground","Playground",blGrey[2]);
    tb.btnPlayground.style.float = "left";
    
    tb.btnPlayground.onclick = function(){
        if(!this.pg)  this.pg = new CPlayground(btn4p1.v);
        this.pg.show(this);
    }
    btn4p1.onclick = function(){
        var b = this;
        _on_off_div(b,b.v);
        b.style.background = b.style.background=="red"?blGrey[5]:blColor[4];   
    } 
    btn4p1.onclick();
}
 
function CPlayground(parentDiv){
    var p = parentDiv;
    var ui = null;
    var w = 360;
    var h = 240;
    

    this.show = function(b){
        if(!ui){
            ui=blo0.blMDiv(p,"id_mdiv_4_playground","playground",555,5,w,111,blGrey[0]);
            var tb = blo0.blDiv(ui, "id_4_tb_playground","tb",blGrey[1]);
            tb.btnPlay = blo0.blBtn(tb,"id_4_btnPlay","play",blGrey[2]);
            tb.btnPlay.style.float = "left";
            tb.btnPlay.onclick = function(){
                o.play(this);
            }
            tb.btnDbg = o.dbgBtn(tb,"id_btn_4_dbgPlayground","dbg");

            var v1 = blo0.blDiv(ui,ui.id+"v1","",blGrey[1]);            
            var cvs = document.createElement("canvas");
            cvs.width = w;
            cvs.height = h;
            cvs.style.backgroundColor = "grey";
            cvs.style.float = "left";

            v1.appendChild(cvs);
            
            cvs.addEventListener('mousedown', function (e) {
                var x = e.offsetX;
                var y = e.offsetY;
                o.mousedown(cvs.getContext("2d"),x,y);                
            });
            
            
            var itv = setInterval(o.ftnTimer, 20,cvs.getContext("2d"),w,h);
        }
        _on_off_div(b,ui);
        b.style.background = b.style.background=="red"?blGrey[5]:blColor[4];   
    }; 
}
function CStoryBoard(parentDiv){

    var v = "CStoryBoard v0.12";
    var ui = null;
    var p = parentDiv; 
    
     
    this.show = function(b){
        if(!ui){    
            
            ui=blo0.blDiv(p,p.id+"_StoryBoard",v,blGrey[1]);   
            var tb =blo0.blDiv(ui,"tb4StoryBoard","tb2",blGrey[1]);
            tb.b1 = o.dbgBtn(tb,"id_btn_4_StoryBoardDbg","dbg");
            

            o.addClass(ui,"w3-row");  
            o.addClass(ui,"w3-red");

            o.uiColum(ui);   

            ui.draw = function(ctx){
                if(tb.b1.b)         o.text(ctx,ui.id,11,222);
            }
            o.reg2draw(ui);
        }
        _on_off_div(b,ui);
        b.style.background = b.style.background=="red"?blGrey[5]:blColor[4];   
    }; 
} 

function CClient(){
    var w = {};
    this.exeCmd = function(v0,v1,v2){
        var ta = bl$("id_ta_4_script_editor");
        if(!ta) v0.innerHTML = "No ta.";
        else{
            ta.value = "exeCmd" + Date();
            var btnRun = blo0.blBtn(v1, v1.id+ "btnRun", "run", "green");
            btnRun.onclick = function(){                    
                w._2do = function(txt){ 
                    v0.innerHTML = txt;        
                } 
                blo0.blAjx(w, "http://localhost:8080/command?cmd="+ta.value ); 
            }
        }
    };
    this.getJSFiles = function(v0,v1,v2){
			w._2do = function(txt){ 
				v0.innerHTML = ""; 
				eval("var o=" + txt);
				for(i in o.resource){
					var b = blo0.blBtn(v0,v0.id+i,i,blGrey[2]);
					b.onclick = function(_this,_jsf){						
						return function(){
							 v1.innerHTML = _this.id;
							 var btnMP4 = blo0.blBtn(v1, v1.id+ "b1", "createMP4", blGrey[2]);
							 v2.innerHTML = _jsf;
							 var vMP4 = blo0.blDiv(v2, v2.id + "vMP4", "vMP4", blGrey[2]);  

							 btnMP4.onclick = function(){
								var url = "http://localhost:8080/image/video?script="+_jsf; 
								var w1 = {};
								w1._2do = function(txt){ 
									vMP4.innerHTML = txt;	
								}
								blo0.blAjx(w1,url);							
							}
						}
					}(b,o.resource[i]);					
				}  
			}
			blo0.blAjx(w, "http://localhost:8080/getResourceOnServer?filetype=json" ); 
    };
    
    this.getMp3Files = function(v0){
        w._2do = function(txt){ 
            eval("var o=" + txt);
            v0.innerHTML = "";
            for(i in o.resource){
                var b = blo0.blBtn(v0,v0.id+i,i,blGrey[2]);
                b.onclick = function(_this,_r){						
                    return function(){
                         alert(_r);                          
                    }
                }(b,o.resource[i]);	                		
            }  
        }
        blo0.blAjx(w, "http://localhost:8080/getResourceOnServer?filetype=mp3" ); 
    };
}

var o = {};
o.x = 50;
o.y = 30;
o.s = "o.s";
o.list2draw = [];
o.bPlay = false;
o.play = function(btn){
    if(o.bPlay){
        o.bPlay = false;
        btn.innerHTML = "play";
    }
    else{
        o.bPlay = true;
        btn.innerHTML = "stop";
    }
}
o.draw = function(ctx){
    var s = "o.draw: " + o.bPlay ;
    s += " o.list2draw.length=" + o.list2draw.length;
    o.text(ctx,s,100,100);

    for(i in o.list2draw){
        o.list2draw[i].draw(ctx);
    }
}
o.reg2draw  = function(user){
    o.list2draw.push(user);
}
o.dbgBtn = function(tb,id,html){
    var btn = blo0.blBtn(tb,id,html,"grey"); 
           
    btn.style.float = "left";
       
    btn.onclick = function(_tb){ 
        return function(){
                if("grey"==this.style.backgroundColor){
                    this.style.backgroundColor = "green";
                    this.b = true;
                }
                else{
                    this.style.backgroundColor = "grey";
                    this.b = false;
                }
            }
    }(tb); 
    return btn;            
}
o.mousedown = function(ctx,x,y){
    o.s = x + ":" + y;
    o.x = x;
    o.y = y;    
};
o.ftnTimer = function(ctx,w,h){ 
    ctx.clearRect(0, 0, w, h);
    
    ctx.fillStyle = "grey";
    ctx.fillRect(0,0,w,h);
 
    o.text(ctx,"xd--" + Date(),15,20);  
    o.text(ctx,o.s,o.x,o.y);  

    o.draw(ctx);
};
o.text = function(ctx,txt,x,y){ 
    ctx.font= 12 + "px Comic Sans MS";
    ctx.fillStyle = "white";
    ctx.fillText(txt, x,y); 
};
    o.uiCards = function(_p,_c){  
        _p.style.overflow = "auto";        
        var cardV = blo0.blDiv(_p,_p.id+"cardV","cardV",blGrey[2]);
        cardV.style.width = 20*111 +"px";
        cardV.style.height = "50px";
        cardV.style.backgroundColor = "coral";
        cardV.style.float = "left";
    };
    o.uiColum = function(ui){           
        var s = '<div class="w3-col s3 w3-green w3-center"><p>s6</p></div>';
        s+='<div class="w3-col s9 w3-dark-grey w3-center" id="uiRight">   </div>';
        var v1 = blo0.blDiv(ui,ui.id+"v1",s,blGrey[2]);
       
        o.addClass(v1,"w3-row"); 
        var r = bl$("uiRight");  
        o.uiCards(r,"red");  
    };
    o.addClass = function (d,cn) {        d.classList.add(cn);    };
      

    var b = bl$("btnStoryBoard");    
    o.addClass(b,"w3-button"); 
    o.addClass(b,"w3-blue"); 
 

    var b = bl$("btnPlayground");    
    o.addClass(b,"w3-button"); 
    o.addClass(b,"w3-green"); 