/**
 * Created by ccz on 16/8/25.
 */
$(function(){

    $('#ebutton').on('click',function(){
        var info = {para:$('#etext').val()};
        $.get('/aes/encrypt',info,function(data){
                $('#etextarea').text(data);
        })
    });


    $('#dbutton').on('click',function(){
        var info = {para:$('#dtext').val()};
        $.get('/aes/decrypt',info,function(data){
            $('#dtextarea').text(data);
        })
    });
    
});