/**
 * Created by wangxiaobo on 16/8/25.
 */
$(function(){
    $('#submit').on('click',function(){
        var code = $('#sms').val();
        var img = $('#imgCode').val();
        $('#mask').removeClass('hide');
        $('#topTen,#all').addClass('disabled');
        $('#table,#alTablel').addClass('hide');
        if (!code && !img){
            var info = {mobile:$('#mobile').val(),pwd:$('#password').val(),clientId:10000};
            $.post('/call/simLogin',info,function(data){
                $('#mask').addClass('hide');
                if(data.status==="SUCCESS"){
                    $('#topTen,#all').removeClass('disabled');
                }else if(data.status==="SUCCESS_MSG"){
                    $('#smsDiv').removeClass('hide');
                }else if(data.status==="SUCCESS_IMG"){
                    $('#imgDiv').removeClass('hide');
                    $('#img').attr("src",data.data);
                }else if(data.status==="ERROR"){
                    alert(data.msg);
                }
            })
        }else{
            if(code){
                var info = {mobile:$('#mobile').val(),msg:$('#sms').val(),clientId:10000};
                $.post('/call/crawlData',info,function(data){
                    $('#mask').addClass('hide');
                    if(data.status==="SUCCESS"){
                        $('#topTen,#all').removeClass('disabled');
                        code = "";
                        $('#smsDiv').addClass('hide');
                    }
                })
            }else if(img){
                var info = {mobile:$('#mobile').val(),msg:$('#imgCode').val()};
                $.get('/call/msgConfirm',info,function(data){
                    $('#mask').addClass('hide');
                    if(data.status==="SUCCESS"){
                        $('#topTen,#all').removeClass('disabled');
                        code = "";
                        $('#smsDiv').addClass('hide');
                    }
                })
            }
        }
    });
    $('#topTen').on('click',function(){
        $('#table').removeClass('hide');
        $('#allTable').addClass('hide');
        $('#tableFirstTr').siblings().remove();
        var info = {mobile:$('#mobile').val()};
        $('#mask').removeClass('hide');
        $.get('/call/getTop10Data',info,function(data){
            $('#mask').addClass('hide');
            if(data.status==='SUCCESS'){
                for (var i = 0,len = data.data.length;i<len;i++){
                    $('#table').append('<tr><td>'+ i +'</td><td>'+ data.data[i].callTimes +'</td><td>'+ data.data[i].mobile +'</td></tr>');
                }
            }
        })
    });
    $('#all').on('click',function(){
        $('#allTable').removeClass('hide');
        $('#table').addClass('hide');
        $('#allTableFirstTr').siblings().remove();
        var info = {mobile:$('#mobile').val()};
        $('#mask').removeClass('hide');
        $.get('/call/getData',info,function(data){
            $('#mask').addClass('hide');

            if(data.status==='SUCCESS'){
                for (var i = 0, len = data.data.length;i<len;i++){
                    var callStyle = parseInt(data.data[i].callStyle);
                    var callStyleValue = '无';
                    if(callStyle == 0){
                        callStyleValue = '市话';
                    }else if(callStyle == 1){
                        callStyleValue = '漫游';
                    }else{
                        callStyleValue = '无';
                    }
                    $('#allTable').append('<tr><td>'+ i +'</td><td>'+ (Boolean(parseInt(data.data[i].callType)) ? '被叫':'主叫') +'</td><td>'
                        + callStyleValue +'</td><td>'+ data.data[i].callMobile +'</td><td>'+ data.data[i].callTimeCost
                        +'</td><td>'+ data.data[i].callArea +'</td><td>'+ data.data[i].callFee +'</td></tr>');
                }
            }
        })
    })
});