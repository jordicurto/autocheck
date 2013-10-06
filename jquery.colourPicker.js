jQuery.fn.colourPicker=function(conf){
	var config=jQuery.extend({
		id:'jquery-colour-picker',
		ico:'ico.gif',
		title:'Pick a colour',
		inputBG:true,
		speed:500,
		openTxt:'Open colour picker'
	},conf);
	var hexInvert=function(hex){var r=hex.substr(0,2);
	var g=hex.substr(2,2);var b=hex.substr(4,2);
	return 0.212671*r+0.715160*g+0.072169*b<0.5?'ffffff':'000000'};
	var colourPicker=jQuery('#'+config.id);
	if(!colourPicker.length){
		colourPicker=jQuery('<div id="'+config.id+'"></div>').appendTo(document.body).hide();jQuery(document.body).click(function(event){
			if(!(jQuery(event.target).is('#'+config.id) || jQuery(event.target).parents('#'+config.id).length)){
				colourPicker.hide(config.speed)
			}
		})
	}
	return this.each(function(){
		var select=jQuery(this);var icon=jQuery('<a href="#"><img src="'+config.ico+'" alt="'+config.openTxt+'" /></a>').insertAfter(select);
		var input=jQuery('<input type="text" name="'+select.attr('name')+'" value="'+select.val()+'" size="6" style="display: none;"/>').insertAfter(select);
		var loc='';
		jQuery('option',select).each(function(){
			var option=jQuery(this);var hex=option.val();
			var title=option.text();loc+='<li><a href="#" title="'+title+'" rel="'+hex+'" style="background: #'+hex+'; colour: '+hexInvert(hex)+';">'+title+'</a></li>'
		});select.remove();
		if(config.inputBG){
			input.change(function(){
				input.css({
					background:'#'+input.val(),
					color:'#'+hexInvert(input.val())
				})
			});
			input.change()
		}
		icon.click(function(){
			var iconPos=icon.offset();
			var heading=config.title?'<h2>'+config.title+'</h2>':'';
			colourPicker.html(heading+'<ul>'+loc+'</ul>').css({
				position:'absolute',
				left:iconPos.left+'px',
				top:iconPos.top+'px'
			}).show(config.speed);
			jQuery('a',colourPicker).click(function(){
				var hex=jQuery(this).attr('rel');input.val(hex);
				if(config.inputBG){
					input.css({
						background:'#'+hex,
						color:'#'+hexInvert(hex)
					})
				}
				var selection="";
				var textarea=cf.document.getElementById("pst");
				if('selectionStart'in textarea){
					if(textarea.selectionStart!=textarea.selectionEnd){
						selection=textarea.value.substring(textarea.selectionStart,textarea.selectionEnd)
					}
				}
				else{
					var textRange=cf.document.selection.createRange();
					var rangeParent=textRange.parentElement();
					if(rangeParent===textarea){
						selection=textRange.text
					}
				}
				if(selection!='') {
					var pst=$('input[name="pst"]',cf.document).val();
					if($('#toolbar',cf.document).is(':checked')) {
						if($.cookie("vngbbcode_color")!=null) pst=pst.replace(selection,'[/color][color=#'+hex+'] '+selection+'[/color][color=#'+$.cookie("vngbbcode_color")+'] ');
						else pst=pst.replace(selection,'[color=#'+hex+'] '+selection+'[/color]')
					}
					else pst=pst.replace(selection,'[color=#'+hex+'] '+selection+'[/color]');$('input[name="pst"]',cf.document).val(pst)
				}
				else{
					$.cookie("vngbbcode_color",hex,{expires:30},{path:'/'})
				}
				input.change();
				colourPicker.hide(config.speed);
				return false
			});
			return false
		})
	})
};