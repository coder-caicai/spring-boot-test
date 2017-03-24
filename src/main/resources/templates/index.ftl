
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>通话详单</title>
    <link href="../static/css/bootstrap.min.css" rel="stylesheet">
    <link href="../static/css/index.css" rel="stylesheet">
    <script src="../static/js/jquery-1.11.1.min.js" type="text/javascript"></script>
    <script src="../static/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="../static/js/index.js" type="text/javascript"></script>
</head>
<body>
<div class="container">
    <h3>通话详单</h3>
    <div class="form-group">
        <label for="mobile">手机号码</label>
        <input id="mobile" type="tel" class="form-control"/>
    </div>
    <div class="form-group">
        <label for="password">服务密码</label>
        <input id="password" type="password" class="form-control"/>
    </div>
    <div class="form-group hide" id="smsDiv">
        <label for="sms">短信验证码</label>
        <input id="sms" type="number" class="form-control"/>
    </div>
    <div class="form-group hide" id="imgDiv">
        <label for="sms">图片验证码</label>
        <input id="imgCode"  class="form-control"/>
        <img  id="img" src="">

    </div>
    <div class="form-group">
        <a class="btn btn-default btn-primary" id="submit">提交</a>
    </div>
    <div class="form-group">
        <a class="btn btn-default disabled" id="topTen">前十位联系人</a>
        <a class="btn btn-default disabled" id="all">所有数据</a>
    </div>
    <table class="table table-hover hide" id="table">
        <tr id="tableFirstTr">
            <th>#</th>
            <th>呼叫次数</th>
            <th>手机号码</th>
        </tr>
    </table>
    <table class="table table-hover hide" id="allTable">
        <tr id="allTableFirstTr">
            <th>#</th>
            <th>呼叫类型</th>
            <th>呼叫方式</th>
            <th>对方号码</th>
            <th>通话时长</th>
            <th>通话地点</th>
            <th>通话费用</th>
        </tr>
    </table>
    <div class="mask hide" id="mask">
        <img src="../static/images/loading.gif">
    </div>
</div>
</body>
</html>