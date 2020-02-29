$(".dropify").dropify({
    "messages": {
        "default": "点此选择或拖拽到此上传图片 --冰海前端组件:)",
        "replace": "点此选择或拖拽到此更新图片 --冰海前端组件:)",
        "remove": "删除",
        "error": "发生错误"
    },
    "error": {
        "fileSize": "图片不得大于1MB"
    }
});

var uploadToken = '';
function reloadToken() {
    $.getJSON('https://wx.nanayun.cn/qiniuToken', function (resp) {
        uploadToken = resp.data;
    })
}

function doUpload(thiz) {
    var inputId = $(thiz).attr("id");
    var putExtra = {
        fname: guid2(), //文件原文件名
        params: {},//用来放置自定义变量
        mimeType: null
    };
    var config = {
        useCdnDomain: true,
        region: qiniu.region.z2
    };
    var observable = qiniu.upload($("#" + inputId)[0].files[0], guid2() + ".png", uploadToken, putExtra, config);
    observable.subscribe(console.log, console.log, function (resp) {
        var url = "http://cdn.binghai.site/" + resp.key;
        $("#"+inputId.substr(5)).val(url);
        reloadToken();
    });
}

function guid2() {
    function S4() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    }
    return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
}

reloadToken();