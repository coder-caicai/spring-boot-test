var page = require('webpage').create();
system = require('system')
address = system.args[1];//获得命令行第二个参数 接下来会用到
data = system.args[2];
cookies = system.args[3];
// var url = address;
// page.open('http://www.html-js.com/', function () {
//     page.render('../../../../../static/report/sample.png');
//     phantom.exit();
// });
var url = address;
//console.log(url);

phantom.addCookie({
    'token' : cookies
})
page.open(url,'post',data, function (status) {
    //Page is loaded!
    if (status !== 'success') {
        console.log('Unable to post!');
    } else {
        //console.log(page.content);
        //var title = page.evaluate(function() {
        //  return document.title;//示范下如何使用页面的jsapi去操作页面的  www.oicqzone.com
        //  });
        //console.log(title);
        // page.render('../../../../../static/report/sample.png');
        page.render('/Users/cheng/sample.png')
        console.log(page.content);
    }
    phantom.exit();
});