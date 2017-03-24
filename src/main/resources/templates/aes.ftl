
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
    <div style="margin: 20px;">
        <h3>加密请求参数</h3>
         参数:<textarea rows="5" cols="60" id="etext" type="text" name="para"></textarea>---->
        <input style="margin: 20px;" type="button" id="ebutton" value="加密">
         ---->结果:<textarea id="etextarea" rows="5" cols="60"></textarea></br>

    </div>

    <div style="margin: 20px;">
        <h3>解密响应结果</h3>
        参数:<textarea rows="5" cols="60" id="dtext" type="text" name="para"></textarea>---->
        <input style="margin: 20px" type="button" id="dbutton" value="解密">
        ---->结果:<textarea id="dtextarea" rows="5" cols="60"></textarea></br>

    </div>

</body>
</html>