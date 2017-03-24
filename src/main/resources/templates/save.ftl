<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>aes</title>
    <script src="../static/js/jquery-1.11.1.min.js" type="text/javascript"></script>
    <script src="../static/js/aes.js" type="text/javascript"></script>
</head>
<body>
    <h1>${data}</h1>
    <form action="/auth/doSave" method="post">
        <label>公司名称 :</label> <input type="text" name="company"></br>
        <label>邮   箱 :</label>  <input type="text" name="mail"></br>
        <label>服务密码 :</label>  <input type="text" name="passWord"></br>
        <button type="submit">提交</button>
    </form>
</body>
</html>