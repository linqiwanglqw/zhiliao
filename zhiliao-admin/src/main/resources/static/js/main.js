(function ($) {
    $.getUrlParam = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");//构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg);//匹配目标参数
        if (r != null) return unescape(r[2]); return null;//返回参数值
    }
})(jQuery);
Array.prototype.indexOf = function(val) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] == val) return i;
    }
    return -1;
};
Array.prototype.remove = function(val) {
    var index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};
var opts = {
    lines: 13, // 花瓣数目
    length: 20, // 花瓣长度
    width: 10, // 花瓣宽度
    radius: 30, // 花瓣距中心半径
    corners: 1, // 花瓣圆滑度 (0-1)
    rotate: 0, // 花瓣旋转角度
    direction: 1, // 花瓣旋转方向 1: 顺时针, -1: 逆时针
    color: '#5882FA', // 花瓣颜色
    speed: 1, // 花瓣旋转速度
    trail: 60, // 花瓣旋转时的拖影(百分比)
    shadow: false, // 花瓣是否显示阴影
    hwaccel: false, //spinner 是否启用硬件加速及高速旋转
    className: 'spinner', // spinner css 样式名称
    zIndex: 2e9, // spinner的z轴 (默认是2000000000)
    top: 'auto', // spinner 相对父容器Top定位 单位 px
    left: 'auto'// spinner 相对父容器Left定位 单位 px
};

Handlebars.registerHelper('compare', function(left, operator, right, options) {
    var operators = {
        '==': function(l, r) {return l == r; },
        '===': function(l, r) {return l === r; },
        '!=': function(l, r) {return l != r; },
        '<': function(l, r) {return l < r; },
        '>': function(l, r) {return l > r; },
        '<=': function(l, r) {return l <= r; },
        '>=': function(l, r) {return l >= r; },
        'typeof': function(l, r) {return typeof l === r; }
    };
    if (!operators[operator]) {
        throw new Error('Handlerbars Helper "compare" doesn\'t know the operator ' + operator);
    }
    var result = operators[operator](left, right);
    if (result) {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

Handlebars.registerHelper('range_number', function(n, block) {
    var accum = '';
    for(var i = 1; i <= n; ++i)
        accum += block.fn(i);
    return accum;
});


function login() {
    var username = $("#inputEmail").val();
    var password = $("#inputPassword").val();
    window.location.href=
    $.ajax({
        method: "POST",
        url: "/mm/login/loginPost",
        contentType: 'application/json',
        data:JSON.stringify({
            "username": username,
            "password": password
        }),
        success: function(data) {
            if (data.success){
                window.location.href = "home.ftl?session="+data.result.session;
            }else{
                alert("账户或密码错误")
            }
        }
    });
}

function logout() {
    $.ajax({
        method: "POST",
        url: "/mm/login/logout",
        contentType: 'application/json',
        success: function(data) {
            window.location.href = "index.ftl";
        }
    });
}
function checkLogin() {
    var session = $.getUrlParam('session');
    $.ajax({
        method: "POST",
        url: "/mm/login/checkLogin",
        contentType: 'application/json',
        data:JSON.stringify({
            "session": session
        }),
        success: function(data) {
            if (!data.success){
                window.location.href = "index.ftl";
            }else{
                $('#username').html(data.result.username);
            }
        }
    });
}
function sumUserSubFlow() {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/order/sumUserSubFlow",
        contentType: 'application/json',
        data:JSON.stringify({
            "page":1,
            "pageSize":1
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{

                var source   = document.getElementById("userSubFlowSum-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#content-display').html(html);
            }
        }
    });
}
function userSubFlowList(page,pageSize) {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/order/userSubFlowList",
        contentType: 'application/json',
        data:JSON.stringify({
            "page":page,
            "pageSize":pageSize
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{

                var source   = document.getElementById("userSubFlow-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#content-display').html(html);
            }
        }
    });
}
function findJfConfig() {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/findJfConfig",
        contentType: 'application/json',
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("jfConfig-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#content-display').html(html);
            }
        }
    });
}
function findJfConfigUpdate() {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/findJfConfig",
        contentType: 'application/json',
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("jfConfig-update-modal-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#modal-dialog').html(html);
            }
        }
    });
}
function updateJfConfig() {
    var coinGiftNewer = $('#u_coinGiftNewer').val();
    var coinGiftOlder = $('#u_coinGiftOlder').val();
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/updateJfConfig",
        contentType: 'application/json',
        data:JSON.stringify({
            "coinGiftNewer":coinGiftNewer,
            "coinGiftOlder":coinGiftOlder
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                $('#versionModal').modal('hide');
                findJfConfig();
            }
        }
    });
}
function findCoinRulePageList() {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/findCoinRulePageList",
        contentType: 'application/json',
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("rulePage-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#content-display').html(html);
            }
        }
    });
}
function findCoinRulePage(id) {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/findCoinRulePage",
        contentType: 'application/json',
        data:JSON.stringify({
            "id":id
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("rulePage-update-modal-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#modal-dialog').html(html);
                $('#u_rulePageContent').summernote();
            }
        }
    });
}
function addRulePageModal() {
    var source   = document.getElementById("rulePage-add-modal-template").innerHTML;
    var template = Handlebars.compile(source);
    var html = template();
    $('#modal-dialog').html(html);
    $('#a_rulePageContent').summernote();

}
function addCoinRulePage() {
    var content = $("#a_rulePageContent").val();
    var language = $("#a_rulePageLanguage").val();
    $.ajax({
        method: "POST",
        url: "/mm/user/addCoinRulePage",
        contentType: 'application/json',
        data:JSON.stringify({
            "content":content,
            "language":language
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                $('#versionModal').modal('hide');
                findCoinRulePageList();
            }
        }
    });
}
function  delCoinRulePage(id) {
   if (confirm("是否确定删除?")){
    $.ajax({
        method: "POST",
        url: "/mm/user/delCoinRulePage",
        contentType: 'application/json',
        data:JSON.stringify({
            "id":id
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                findCoinRulePageList();
            }
        }
    });
   }
}
function updateCoinRulePage(id) {
    var content = $("#u_rulePageContent").val();
    var language = $("#u_rulePageLanguage").val();
    $.ajax({
        method: "POST",
        url: "/mm/user/updateCoinRulePage",
        contentType: 'application/json',
        data:JSON.stringify({
            "id":id,
            "content":content,
            "language":language
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                $('#versionModal').modal('hide');
                findCoinRulePageList();
            }
        }
    });
}
function taskList() {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/taskList",
        contentType: 'application/json',
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("task-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#content-display').html(html);
            }
        }
    });
}
function goodsList() {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/goodsList",
        contentType: 'application/json',
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("goods-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#content-display').html(html);
            }
        }
    });
}

function goodsContentList(id) {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/goodsContentList",
        contentType: 'application/json',
        data:JSON.stringify({
            "id":id
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("goods-content-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#content-display').html(html);
            }
        }
    });
}

function taskContentList(id) {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/taskContentList",
        contentType: 'application/json',
        data:JSON.stringify({
            "id":id
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("task-content-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result,"id":id};
                var html = template(context);
                $('#content-display').html(html);
            }
        }
    });
}
function findTask(id) {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/findTask",
        contentType: 'application/json',
        data:JSON.stringify({
            "id":id
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("task-update-modal-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#modal-dialog').html(html);
            }
        }
    });
}
function addTaskModal() {
    var source   = document.getElementById("task-add-modal-template").innerHTML;
    var template = Handlebars.compile(source);
    var html = template();
    $('#modal-dialog').html(html);
}
function addTask() {
    var taskType = $("#a_taskType").val();
    var taskCategory = $("#a_taskCategory").val();
    var coin = $("#a_taskCoin").val();
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/addTask",
        contentType: 'application/json',
        data:JSON.stringify({
            "taskType":taskType,
            "taskCategory":taskCategory,
            "coin":coin
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                $('#versionModal').modal('hide');
                taskList();
            }
        }
    });
}
function updateTask(id) {
    var taskType = $("#u_taskType").val();
    var taskCategory = $("#u_taskCategory").val();
    var coin = $("#u_taskCoin").val();
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/updateTask",
        contentType: 'application/json',
        data:JSON.stringify({
            "id":id,
            "taskType":taskType,
            "taskCategory":taskCategory,
            "coin":coin
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                $('#versionModal').modal('hide');
                taskList();
            }
        }
    });
}

function findTaskContent(id) {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/findTaskContent",
        contentType: 'application/json',
        data:JSON.stringify({
            "id":id
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("task-content-update-modal-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#modal-dialog').html(html);
            }
        }
    });
}
function addTaskContentModal(taskId) {
    var source   = document.getElementById("task-content-add-modal-template").innerHTML;
    var template = Handlebars.compile(source);
    var context = {"taskId":taskId};
    var html = template(context);
    $('#modal-dialog').html(html);
}
function addTaskContent(taskId) {
    var taskName = $("#a_taskName").val();
    var taskIcon = $("#a_taskIcon").val();
    var language = $("#a_taskContentLanguage").val();
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/addTaskContent",
        contentType: 'application/json',
        data:JSON.stringify({
            "taskId":taskId,
            "taskName":taskName,
            "taskIcon":taskIcon,
            "language":language
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                $('#versionModal').modal('hide');
                taskContentList(taskId);
            }
        }
    });
}

function updateTaskContent(id,taskId) {
    var taskName = $("#u_taskName").val();
    var taskIcon = $("#u_taskIcon").val();
    var language = $("#u_taskContentLanguage").val();
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/updateTaskContent",
        contentType: 'application/json',
        data:JSON.stringify({
            "id":id,
            "taskName":taskName,
            "taskIcon":taskIcon,
            "language":language
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                $('#versionModal').modal('hide');
                taskContentList(taskId);
            }
        }
    });
}
function findGoods(id) {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/findGoods",
        contentType: 'application/json',
        data:JSON.stringify({
            "id":id
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("goods-update-modal-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#modal-dialog').html(html);
            }
        }
    });
}
function updateGoods(id) {
    var num = $("#u_goodsNum").val();
    var remainNum = $("#u_goodsRemainNum").val();
    var coin = $("#u_goodsCoin").val();
    var goodsType = $("#u_goodsType").val();
    var display = $("#u_display").val();
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/updateGoods",
        contentType: 'application/json',
        data:JSON.stringify({
            "id":id,
            "goodsType":goodsType,
            "num":num,
            "remainNum":remainNum,
            "display":display,
            "coin":coin
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                $('#versionModal').modal('hide');
                goodsList();
            }
        }
    });
}
function findGoodsContent(id) {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/findGoodsContent",
        contentType: 'application/json',
        data:JSON.stringify({
            "id":id
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("goods-content-update-modal-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#modal-dialog').html(html);
            }
        }
    });
}
function addGoodsModal() {
    var source   = document.getElementById("goods-add-modal-template").innerHTML;
    var template = Handlebars.compile(source);
    var html = template();
    $('#modal-dialog').html(html);
}
function addGoods() {
    var num = $("#a_goodsNum").val();
    var remainNum = $("#a_goodsRemainNum").val();
    var coin = $("#a_goodsCoin").val();
    var goodsType = $("#a_goodsType").val();
    var display = $("#a_display").val();
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/addGoods",
        contentType: 'application/json',
        data:JSON.stringify({
            "goodsType":goodsType,
            "num":num,
            "remainNum":remainNum,
            "display":display,
            "coin":coin
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                $('#versionModal').modal('hide');
                goodsList();
            }
        }
    });
}

function addGoodsContentModal(goodsId) {
    var source   = document.getElementById("goods-content-add-modal-template").innerHTML;
    var template = Handlebars.compile(source);
    var context = {"goodsId":goodsId};
    var html = template(context);
    $('#modal-dialog').html(html);
}
function addGoodsContent(goodsId) {
    var title = $("#a_goodsTitle").val();
    var summary = $("#a_goodsSummary").val();
    var content = $("#a_goodsContent").val();
    var imageUrl = $("#a_goodsImageUrl").val();
    var detailImageUrl = $("#a_goodsDetailImageUrl").val();
    var language = $('#a_goodsLanguage').val();
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/addGoodsContent",
        contentType: 'application/json',
        data:JSON.stringify({
            "goodsId":goodsId,
            "title":title,
            "summary":summary,
            "content":content,
            "imageUrl":imageUrl,
            "detailImageUrl":detailImageUrl,
            "language":language
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                $('#versionModal').modal('hide');
                goodsContentList(goodsId);
            }
        }
    });
}
function updateGoodsContent(id,goodsId) {
    var title = $("#u_goodsTitle").val();
    var summary = $("#u_goodsSummary").val();
    var content = $("#u_goodsContent").val();
    var imageUrl = $("#u_goodsImageUrl").val();
    var detailImageUrl = $("#u_goodsDetailImageUrl").val();
    var language = $('#u_goodsLanguage').val();
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/updateGoodsContent",
        contentType: 'application/json',
        data:JSON.stringify({
            "id":id,
            "title":title,
            "summary":summary,
            "content":content,
            "imageUrl":imageUrl,
            "detailImageUrl":detailImageUrl,
            "language":language
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                $('#versionModal').modal('hide');
                goodsContentList(goodsId);
            }
        }
    });
}
function pushSelect() {
    $('#chooseDiv').html("");
    var source   = document.getElementById("push-select-template").innerHTML;
    var template = Handlebars.compile(source);
    var html = template();
    $('#content-display').html(html);
}

function pushCount() {
    $('#chooseDiv').html("");
    var sex = $('#pushSex').val();
    var sAge = $('#pushSAge').val();
    if (sAge === ""){
        sAge = -1;
    }
    var endAge = $('#pushEndAge').val();
    if (endAge === ""){
        endAge = -1;
    }
    var area = $('#pushArea').val();
    var hasPlan = $('#pushPlan').val();
    var hasBudget = $('#pushBudget').val();
    $.ajax({
        method: "POST",
        url: "/mm/user/pushCount",
        contentType: 'application/json',
        data:JSON.stringify({
            "sex":sex,
            "startAge":sAge,
            "endAge":endAge,
            "area":area,
            "hasPlan":hasPlan,
            "hasBudget":hasBudget
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("push-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result,"sex":sex,"startAge":sAge,"endAge":endAge,"area":area,"hasPlan":hasPlan,"hasBudget":hasBudget};
                var html = template(context);
                $('#push-select-content').html(html);
            }
        }
    });
}
function pushModal(sex,area,startAge,endAge,hasPlan,hasBudget) {
    var source   = document.getElementById("push-modal-template").innerHTML;
    var template = Handlebars.compile(source);
    var context = {"sex":sex,"startAge":startAge,"endAge":endAge,"area":area,"hasPlan":hasPlan,"hasBudget":hasBudget};
    var html = template(context);
    $('#modal-dialog').html(html);
}
function push(sex,area,startAge,endAge,hasPlan,hasBudget) {
    if (confirm("是否确认推送?")){
        var title = $('#push_title').val();
        var content = $('#push_content').val();
    $.ajax({
        method: "POST",
        url: "/mm/user/push",
        contentType: 'application/json',
        data:JSON.stringify({
            "title":title,
            "content":content,
            "sex":sex,
            "startAge":startAge,
            "endAge":endAge,
            "area":area,
            "hasPlan":hasPlan,
            "hasBudget":hasBudget
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                alert("push success:"+data.result);
            }
        }
    });
    }
}
function findPushContentConfig() {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/findPushContentConfig",
        contentType: 'application/json',
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("pushContentConfig-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#content-display').html(html);
            }
        }
    });
}
function findPushContentConfigById(id) {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/findPushContentConfigById",
        contentType: 'application/json',
        data: JSON.stringify({
                "id":id
        }),
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("pushContentConfig-update-modal-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#modal-dialog').html(html);
            }
        }
    });
}
function addPushContentConfig() {
    var language = trim($('#a_p_language').val());
    var noOpenTitle = $('#a_noOpenTitle').val();
    var noOpenBody = $('#a_noOpenBody').val();
    var openHaveAccountTitle1 = $('#a_openHaveAccountTitle1').val();
    var openHaveAccountContent1 = $('#a_openHaveAccountContent1').val();
    var openHaveAccountTitle21 = $('#a_openHaveAccountTitle21').val();
    var openHaveAccountContent21 = $('#a_openHaveAccountContent21').val();
    var openHaveAccountTitle22 = $('#a_openHaveAccountTitle22').val();
    var openHaveAccountContent22 = $('#a_openHaveAccountContent22').val();
    var openHaveAccountTitle3 = $('#a_openHaveAccountTitle3').val();
    var openHaveAccountContent3 = $('#a_openHaveAccountContent3').val();
    var openHaveAccountTitle4 = $('#a_openHaveAccountTitle4').val();
    var openHaveAccountContent4 = $('#a_openHaveAccountContent4').val();
    var openNoAccountPlanTitle1 = $('#a_openNoAccountPlanTitle1').val();
    var openNoAccountPlanContent1 = $('#a_openNoAccountPlanContent1').val();
    var openNoAccountPlanTitle2 =  $('#a_openNoAccountPlanTitle2').val();
    var openNoAccountPlanContent2 = $('#a_openNoAccountPlanContent2').val();
    var openNoAccountPlanTitle3 = $('#a_openNoAccountPlanTitle3').val();
    var openNoAccountPlanContent3 = $('#a_openNoAccountPlanContent3').val();
    var openNoAccountPlanTitle4 = $('#a_openNoAccountPlanTitle4').val();
    var openNoAccountPlanContent4 = $('#a_openNoAccountPlanContent4').val();
    var openNoAccountPlanTitle5 = $('#a_openNoAccountPlanTitle5').val();
    var openNoAccountPlanContent5 = $('#a_openNoAccountPlanContent5').val();
    var openNoAccountPlanTitle6 = $('#a_openNoAccountPlanTitle6').val();
    var openNoAccountPlanContent6 = $('#a_openNoAccountPlanContent6').val();
    var openNoAccountPlanTitle7 = $('#a_openNoAccountPlanTitle7').val();
    var openNoAccountPlanContent7 = $('#a_openNoAccountPlanContent7').val();
    var openNoAccountPlanTitle8 = $('#a_openNoAccountPlanTitle8').val();
    var openNoAccountPlanContent8 = $('#a_openNoAccountPlanContent8').val();
    var openNoAccountPlanTitle9 = $('#a_openNoAccountPlanTitle9').val();
    var openNoAccountPlanContent9 = $('#a_openNoAccountPlanContent9').val();
    var openNoAccountPlanTitle10 = $('#a_openNoAccountPlanTitle10').val();
    var openNoAccountPlanContent10 = $('#a_openNoAccountPlanContent10').val();
    var openNoAccountPlanTitle111 = $('#a_openNoAccountPlanTitle111').val();
    var openNoAccountPlanContent111 = $('#a_openNoAccountPlanContent111').val();
    var openNoAccountPlanTitle112 = $('#a_openNoAccountPlanTitle112').val();
    var openNoAccountPlanContent112 = $('#a_openNoAccountPlanContent112').val();
    var openNoAccountPlanTitle12 = $('#a_openNoAccountPlanTitle12').val();
    var openNoAccountPlanContent12 = $('#a_openNoAccountPlanContent12').val();
    var openNoAccountPlanTitle13 = $('#a_openNoAccountPlanTitle13').val();
    var openNoAccountPlanContent13 = $('#a_openNoAccountPlanContent13').val();
    var openNoAccountPlanTitle14 = $('#a_openNoAccountPlanTitle14').val();
    var openNoAccountPlanContent14 = $('#a_openNoAccountPlanContent14').val();
    var openNoAccountPlanTitle15 = $('#a_openNoAccountPlanTitle15').val();
    var openNoAccountPlanContent15 = $('#a_openNoAccountPlanContent15').val();
    var openNoAccountBudgetTitle11 = $('#a_openNoAccountBudgetTitle11').val();
    var openNoAccountBudgetContent11 = $('#a_openNoAccountBudgetContent11').val();
    var openNoAccountBudgetTitle12 = $('#a_openNoAccountBudgetTitle12').val();
    var openNoAccountBudgetContent12 = $('#a_openNoAccountBudgetContent12').val();
    var openNoAccountBudgetTitle2 = $('#a_openNoAccountBudgetTitle2').val();
    var openNoAccountBudgetContent2 = $('#a_openNoAccountBudgetContent2').val();
    var openNoAccountBudgetTitle31 = $('#a_openNoAccountBudgetTitle31').val();
    var openNoAccountBudgetContent31 = $('#a_openNoAccountBudgetContent31').val();
    var openNoAccountBudgetTitle32 = $('#a_openNoAccountBudgetTitle32').val();
    var openNoAccountBudgetContent32 = $('#a_openNoAccountBudgetContent32').val();
    var openNoAccountOnlyTitle1 = $('#a_openNoAccountOnlyTitle1').val();
    var openNoAccountOnlyContent1 = $('#a_openNoAccountOnlyContent1').val();
    var openNoAccountOnlyTitle2 = $('#a_openNoAccountOnlyTitle2').val();
    var openNoAccountOnlyContent2 = $('#a_openNoAccountOnlyContent2').val();
    var openNoAccountOnlyTitle31 = $('#a_openNoAccountOnlyTitle31').val();
    var openNoAccountOnlyContent31 = $('#a_openNoAccountOnlyContent31').val();
    var openNoAccountOnlyTitle32 = $('#a_openNoAccountOnlyTitle32').val();
    var openNoAccountOnlyContent32 = $('#a_openNoAccountOnlyContent32').val();
    var closePushTitle1 = $('#a_closePushTitle1').val();
    var closePushContent1 = $('#a_closePushContent1').val();
    var closePushTitle2 = $('#a_closePushTitle2').val();
    var closePushContent2 = $('#a_closePushContent2').val();
    var closePushTitle3 = $('#a_closePushTitle3').val();
    var closePushContent3 = $('#a_closePushContent3').val();
    var closePushTitle4 = $('#a_closePushTitle4').val();
    var closePushContent4 = $('#a_closePushContent4').val();
    var closePushTitle5 = $('#a_closePushTitle5').val();
    var closePushContent5 = $('#a_closePushContent5').val();
    var closePushTitle6 = $('#a_closePushTitle6').val();
    var closePushContent6 = $('#a_closePushContent6').val();
    var closePushTitle7 = $('#a_closePushTitle7').val();
    var closePushContent7 = $('#a_closePushContent7').val();
    var closePushTitle8 = $('#a_closePushTitle8').val();
    var closePushContent8 = $('#a_closePushContent8').val();
    var closePushTitle9 = $('#a_closePushTitle9').val();
    var closePushContent9 = $('#a_closePushContent9').val();
    $.ajax({
        method: "POST",
        url: "/mm/user/savePushContentConfig",
        contentType: 'application/json',
        data:JSON.stringify({
            "language":language,
            "noOpenTitle":noOpenTitle,
            "noOpenBody":noOpenBody,
            "openHaveAccountTitle1":openHaveAccountTitle1,
            "openHaveAccountContent1":openHaveAccountContent1,
            "openHaveAccountTitle21":openHaveAccountTitle21,
            "openHaveAccountContent21":openHaveAccountContent21,
            "openHaveAccountTitle22":openHaveAccountTitle22,
            "openHaveAccountContent22":openHaveAccountContent22,
            "openHaveAccountTitle3":openHaveAccountTitle3,
            "openHaveAccountContent3":openHaveAccountContent3,
            "openHaveAccountTitle4":openHaveAccountTitle4,
            "openHaveAccountContent4":openHaveAccountContent4,
            "openNoAccountPlanTitle1":openNoAccountPlanTitle1,
            "openNoAccountPlanContent1":openNoAccountPlanContent1,
            "openNoAccountPlanTitle2":openNoAccountPlanTitle2,
            "openNoAccountPlanContent2":openNoAccountPlanContent2,
            "openNoAccountPlanTitle3":openNoAccountPlanTitle3,
            "openNoAccountPlanContent3":openNoAccountPlanContent3,
            "openNoAccountPlanTitle4":openNoAccountPlanTitle4,
            "openNoAccountPlanContent4":openNoAccountPlanContent4,
            "openNoAccountPlanTitle5":openNoAccountPlanTitle5,
            "openNoAccountPlanContent5":openNoAccountPlanContent5,
            "openNoAccountPlanTitle6":openNoAccountPlanTitle6,
            "openNoAccountPlanContent6":openNoAccountPlanContent6,
            "openNoAccountPlanTitle7":openNoAccountPlanTitle7,
            "openNoAccountPlanContent7":openNoAccountPlanContent7,
            "openNoAccountPlanTitle8":openNoAccountPlanTitle8,
            "openNoAccountPlanContent8":openNoAccountPlanContent8,
            "openNoAccountPlanTitle9":openNoAccountPlanTitle9,
            "openNoAccountPlanContent9":openNoAccountPlanContent9,
            "openNoAccountPlanTitle10":openNoAccountPlanTitle10,
            "openNoAccountPlanContent10":openNoAccountPlanContent10,
            "openNoAccountPlanTitle111":openNoAccountPlanTitle111,
            "openNoAccountPlanContent111":openNoAccountPlanContent111,
            "openNoAccountPlanTitle112":openNoAccountPlanTitle112,
            "openNoAccountPlanContent112":openNoAccountPlanContent112,
            "openNoAccountPlanTitle12":openNoAccountPlanTitle12,
            "openNoAccountPlanContent12":openNoAccountPlanContent12,
            "openNoAccountPlanTitle13":openNoAccountPlanTitle13,
            "openNoAccountPlanContent13":openNoAccountPlanContent13,
            "openNoAccountPlanTitle14":openNoAccountPlanTitle14,
            "openNoAccountPlanContent14":openNoAccountPlanContent14,
            "openNoAccountPlanTitle15":openNoAccountPlanTitle15,
            "openNoAccountPlanContent15":openNoAccountPlanContent15,
            "openNoAccountBudgetTitle11":openNoAccountBudgetTitle11,
            "openNoAccountBudgetContent11":openNoAccountBudgetContent11,
            "openNoAccountBudgetTitle12":openNoAccountBudgetTitle12,
            "openNoAccountBudgetContent12":openNoAccountBudgetContent12,
            "openNoAccountBudgetTitle2":openNoAccountBudgetTitle2,
            "openNoAccountBudgetContent2":openNoAccountBudgetContent2,
            "openNoAccountBudgetTitle31":openNoAccountBudgetTitle31,
            "openNoAccountBudgetContent31":openNoAccountBudgetContent31,
            "openNoAccountBudgetTitle32":openNoAccountBudgetTitle32,
            "openNoAccountBudgetContent32":openNoAccountBudgetContent32,
            "openNoAccountOnlyTitle1":openNoAccountOnlyTitle1,
            "openNoAccountOnlyContent1":openNoAccountOnlyContent1,
            "openNoAccountOnlyTitle2":openNoAccountOnlyTitle2,
            "openNoAccountOnlyContent2":openNoAccountOnlyContent2,
            "openNoAccountOnlyTitle31":openNoAccountOnlyTitle31,
            "openNoAccountOnlyContent31":openNoAccountOnlyContent31,
            "openNoAccountOnlyTitle32":openNoAccountOnlyTitle32,
            "openNoAccountOnlyContent32":openNoAccountOnlyContent32,
            "closePushTitle1":closePushTitle1,
            "closePushContent1":closePushContent1,
            "closePushTitle2":closePushTitle2,
            "closePushContent2":closePushContent2,
            "closePushTitle3":closePushTitle3,
            "closePushContent3":closePushContent3,
            "closePushTitle4":closePushTitle4,
            "closePushContent4":closePushContent4,
            "closePushTitle5":closePushTitle5,
            "closePushContent5":closePushContent5,
            "closePushTitle6":closePushTitle6,
            "closePushContent6":closePushContent6,
            "closePushTitle7":closePushTitle7,
            "closePushContent7":closePushContent7,
            "closePushTitle8":closePushTitle8,
            "closePushContent8":closePushContent8,
            "closePushTitle9":closePushTitle9,
            "closePushContent9":closePushContent9,
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                $('#versionModal').modal('hide');
                findPushContentConfig();
            }
        }
    });
}
function updatePushContentConfig(id) {
    var language = trim($('#u_p_language').val());
    var noOpenTitle = $('#u_noOpenTitle').val();
    var noOpenBody = $('#u_noOpenBody').val();
    var openHaveAccountTitle1 = $('#u_openHaveAccountTitle1').val();
    var openHaveAccountContent1 = $('#u_openHaveAccountContent1').val();
    var openHaveAccountTitle21 = $('#u_openHaveAccountTitle21').val();
    var openHaveAccountContent21 = $('#u_openHaveAccountContent21').val();
    var openHaveAccountTitle22 = $('#u_openHaveAccountTitle22').val();
    var openHaveAccountContent22 = $('#u_openHaveAccountContent22').val();
    var openHaveAccountTitle3 = $('#u_openHaveAccountTitle3').val();
    var openHaveAccountContent3 = $('#u_openHaveAccountContent3').val();
    var openHaveAccountTitle4 = $('#u_openHaveAccountTitle4').val();
    var openHaveAccountContent4 = $('#u_openHaveAccountContent4').val();
    var openNoAccountPlanTitle1 = $('#u_openNoAccountPlanTitle1').val();
    var openNoAccountPlanContent1 = $('#u_openNoAccountPlanContent1').val();
    var openNoAccountPlanTitle2 = $('#u_openNoAccountPlanTitle2').val();
    var openNoAccountPlanContent2 = $('#u_openNoAccountPlanContent2').val();
    var openNoAccountPlanTitle3 = $('#u_openNoAccountPlanTitle3').val();
    var openNoAccountPlanContent3 = $('#u_openNoAccountPlanContent3').val();
    var openNoAccountPlanTitle4 = $('#u_openNoAccountPlanTitle4').val();
    var openNoAccountPlanContent4 = $('#u_openNoAccountPlanContent4').val();
    var openNoAccountPlanTitle5 = $('#u_openNoAccountPlanTitle5').val();
    var openNoAccountPlanContent5 = $('#u_openNoAccountPlanContent5').val();
    var openNoAccountPlanTitle6 = $('#u_openNoAccountPlanTitle6').val();
    var openNoAccountPlanContent6 = $('#u_openNoAccountPlanContent6').val();
    var openNoAccountPlanTitle7 = $('#u_openNoAccountPlanTitle7').val();
    var openNoAccountPlanContent7 = $('#u_openNoAccountPlanContent7').val();
    var openNoAccountPlanTitle8 = $('#u_openNoAccountPlanTitle8').val();
    var openNoAccountPlanContent8 = $('#u_openNoAccountPlanContent8').val();
    var openNoAccountPlanTitle9 = $('#u_openNoAccountPlanTitle9').val();
    var openNoAccountPlanContent9 = $('#u_openNoAccountPlanContent9').val();
    var openNoAccountPlanTitle10 = $('#u_openNoAccountPlanTitle10').val();
    var openNoAccountPlanContent10 = $('#u_openNoAccountPlanContent10').val();
    var openNoAccountPlanTitle111 = $('#u_openNoAccountPlanTitle111').val();
    var openNoAccountPlanContent111 = $('#u_openNoAccountPlanContent111').val();
    var openNoAccountPlanTitle112 = $('#u_openNoAccountPlanTitle112').val();
    var openNoAccountPlanContent112 = $('#u_openNoAccountPlanContent112').val();
    var openNoAccountPlanTitle12 = $('#u_openNoAccountPlanTitle12').val();
    var openNoAccountPlanContent12 = $('#u_openNoAccountPlanContent12').val();
    var openNoAccountPlanTitle13 = $('#u_openNoAccountPlanTitle13').val();
    var openNoAccountPlanContent13 = $('#u_openNoAccountPlanContent13').val();
    var openNoAccountPlanTitle14 = $('#u_openNoAccountPlanTitle14').val();
    var openNoAccountPlanContent14 = $('#u_openNoAccountPlanContent14').val();
    var openNoAccountPlanTitle15 = $('#u_openNoAccountPlanTitle15').val();
    var openNoAccountPlanContent15 = $('#u_openNoAccountPlanContent15').val();
    var openNoAccountBudgetTitle11 = $('#u_openNoAccountBudgetTitle11').val();
    var openNoAccountBudgetContent11 = $('#u_openNoAccountBudgetContent11').val();
    var openNoAccountBudgetTitle12 = $('#u_openNoAccountBudgetTitle12').val();
    var openNoAccountBudgetContent12 = $('#u_openNoAccountBudgetContent12').val();
    var openNoAccountBudgetTitle2 = $('#u_openNoAccountBudgetTitle2').val();
    var openNoAccountBudgetContent2 = $('#u_openNoAccountBudgetContent2').val();
    var openNoAccountBudgetTitle31 = $('#u_openNoAccountBudgetTitle31').val();
    var openNoAccountBudgetContent31 = $('#u_openNoAccountBudgetContent31').val();
    var openNoAccountBudgetTitle32 = $('#u_openNoAccountBudgetTitle32').val();
    var openNoAccountBudgetContent32 = $('#u_openNoAccountBudgetContent32').val();
    var openNoAccountOnlyTitle1 = $('#u_openNoAccountOnlyTitle1').val();
    var openNoAccountOnlyContent1 = $('#u_openNoAccountOnlyContent1').val();
    var openNoAccountOnlyTitle2 = $('#u_openNoAccountOnlyTitle2').val();
    var openNoAccountOnlyContent2 = $('#u_openNoAccountOnlyContent2').val();
    var openNoAccountOnlyTitle31 = $('#u_openNoAccountOnlyTitle31').val();
    var openNoAccountOnlyContent31 = $('#u_openNoAccountOnlyContent31').val();
    var openNoAccountOnlyTitle32 = $('#u_openNoAccountOnlyTitle32').val();
    var openNoAccountOnlyContent32 = $('#u_openNoAccountOnlyContent32').val();
    var closePushTitle1 = $('#u_closePushTitle1').val();
    var closePushContent1 = $('#u_closePushContent1').val();
    var closePushTitle2 = $('#u_closePushTitle2').val();
    var closePushContent2 = $('#u_closePushContent2').val();
    var closePushTitle3 = $('#u_closePushTitle3').val();
    var closePushContent3 = $('#u_closePushContent3').val();
    var closePushTitle4 = $('#u_closePushTitle4').val();
    var closePushContent4 = $('#u_closePushContent4').val();
    var closePushTitle5 = $('#u_closePushTitle5').val();
    var closePushContent5 = $('#u_closePushContent5').val();
    var closePushTitle6 = $('#u_closePushTitle6').val();
    var closePushContent6 = $('#u_closePushContent6').val();
    var closePushTitle7 = $('#u_closePushTitle7').val();
    var closePushContent7 = $('#u_closePushContent7').val();
    var closePushTitle8 = $('#u_closePushTitle8').val();
    var closePushContent8 = $('#u_closePushContent8').val();
    var closePushTitle9 = $('#u_closePushTitle9').val();
    var closePushContent9 = $('#u_closePushContent9').val();
    $.ajax({
        method: "POST",
        url: "/mm/user/updatePushContentConfig",
        contentType: 'application/json',
        data:JSON.stringify({
            "id":id,
            "language":language,
            "noOpenTitle":noOpenTitle,
            "noOpenBody":noOpenBody,
            "openHaveAccountTitle1":openHaveAccountTitle1,
            "openHaveAccountContent1":openHaveAccountContent1,
            "openHaveAccountTitle21":openHaveAccountTitle21,
            "openHaveAccountContent21":openHaveAccountContent21,
            "openHaveAccountTitle22":openHaveAccountTitle22,
            "openHaveAccountContent22":openHaveAccountContent22,
            "openHaveAccountTitle3":openHaveAccountTitle3,
            "openHaveAccountContent3":openHaveAccountContent3,
            "openHaveAccountTitle4":openHaveAccountTitle4,
            "openHaveAccountContent4":openHaveAccountContent4,
            "openNoAccountPlanTitle1":openNoAccountPlanTitle1,
            "openNoAccountPlanContent1":openNoAccountPlanContent1,
            "openNoAccountPlanTitle2":openNoAccountPlanTitle2,
            "openNoAccountPlanContent2":openNoAccountPlanContent2,
            "openNoAccountPlanTitle3":openNoAccountPlanTitle3,
            "openNoAccountPlanContent3":openNoAccountPlanContent3,
            "openNoAccountPlanTitle4":openNoAccountPlanTitle4,
            "openNoAccountPlanContent4":openNoAccountPlanContent4,
            "openNoAccountPlanTitle5":openNoAccountPlanTitle5,
            "openNoAccountPlanContent5":openNoAccountPlanContent5,
            "openNoAccountPlanTitle6":openNoAccountPlanTitle6,
            "openNoAccountPlanContent6":openNoAccountPlanContent6,
            "openNoAccountPlanTitle7":openNoAccountPlanTitle7,
            "openNoAccountPlanContent7":openNoAccountPlanContent7,
            "openNoAccountPlanTitle8":openNoAccountPlanTitle8,
            "openNoAccountPlanContent8":openNoAccountPlanContent8,
            "openNoAccountPlanTitle9":openNoAccountPlanTitle9,
            "openNoAccountPlanContent9":openNoAccountPlanContent9,
            "openNoAccountPlanTitle10":openNoAccountPlanTitle10,
            "openNoAccountPlanContent10":openNoAccountPlanContent10,
            "openNoAccountPlanTitle111":openNoAccountPlanTitle111,
            "openNoAccountPlanContent111":openNoAccountPlanContent111,
            "openNoAccountPlanTitle112":openNoAccountPlanTitle112,
            "openNoAccountPlanContent112":openNoAccountPlanContent112,
            "openNoAccountPlanTitle12":openNoAccountPlanTitle12,
            "openNoAccountPlanContent12":openNoAccountPlanContent12,
            "openNoAccountPlanTitle13":openNoAccountPlanTitle13,
            "openNoAccountPlanContent13":openNoAccountPlanContent13,
            "openNoAccountPlanTitle14":openNoAccountPlanTitle14,
            "openNoAccountPlanContent14":openNoAccountPlanContent14,
            "openNoAccountPlanTitle15":openNoAccountPlanTitle15,
            "openNoAccountPlanContent15":openNoAccountPlanContent15,
            "openNoAccountBudgetTitle11":openNoAccountBudgetTitle11,
            "openNoAccountBudgetContent11":openNoAccountBudgetContent11,
            "openNoAccountBudgetTitle12":openNoAccountBudgetTitle12,
            "openNoAccountBudgetContent12":openNoAccountBudgetContent12,
            "openNoAccountBudgetTitle2":openNoAccountBudgetTitle2,
            "openNoAccountBudgetContent2":openNoAccountBudgetContent2,
            "openNoAccountBudgetTitle31":openNoAccountBudgetTitle31,
            "openNoAccountBudgetContent31":openNoAccountBudgetContent31,
            "openNoAccountBudgetTitle32":openNoAccountBudgetTitle32,
            "openNoAccountBudgetContent32":openNoAccountBudgetContent32,
            "openNoAccountOnlyTitle1":openNoAccountOnlyTitle1,
            "openNoAccountOnlyContent1":openNoAccountOnlyContent1,
            "openNoAccountOnlyTitle2":openNoAccountOnlyTitle2,
            "openNoAccountOnlyContent2":openNoAccountOnlyContent2,
            "openNoAccountOnlyTitle31":openNoAccountOnlyTitle31,
            "openNoAccountOnlyContent31":openNoAccountOnlyContent31,
            "openNoAccountOnlyTitle32":openNoAccountOnlyTitle32,
            "openNoAccountOnlyContent32":openNoAccountOnlyContent32,
            "closePushTitle1":closePushTitle1,
            "closePushContent1":closePushContent1,
            "closePushTitle2":closePushTitle2,
            "closePushContent2":closePushContent2,
            "closePushTitle3":closePushTitle3,
            "closePushContent3":closePushContent3,
            "closePushTitle4":closePushTitle4,
            "closePushContent4":closePushContent4,
            "closePushTitle5":closePushTitle5,
            "closePushContent5":closePushContent5,
            "closePushTitle6":closePushTitle6,
            "closePushContent6":closePushContent6,
            "closePushTitle7":closePushTitle7,
            "closePushContent7":closePushContent7,
            "closePushTitle8":closePushTitle8,
            "closePushContent8":closePushContent8,
            "closePushTitle9":closePushTitle9,
            "closePushContent9":closePushContent9,
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                $('#versionModal').modal('hide');
                findPushContentConfig();
            }
        }
    });
}
function addPushContentConfigModal() {
    $('#versionModal').modal('show');
    var source   = document.getElementById("pushContentConfig-add-modal-template").innerHTML;
    var template = Handlebars.compile(source);
    var html = template();
    $('#modal-dialog').html(html);
}
function deletePushContentConfig(id) {
    if (confirm("是否确认删除?")) {
        $.ajax({
            method: "POST",
            url: "/mm/user/deletePushContentConfig",
            contentType: 'application/json',
            data: JSON.stringify({
                "id":id
            }),
            success: function(data) {
                if (!data.success){
                    alert("查找错误");
                }else{
                    findPushContentConfig();
                }
            }
        });
    }
}
function findPushConfig() {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/findPushConfig",
        contentType: 'application/json',
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("pushConfig-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#content-display').html(html);
            }
        }
    });
}

function findPushConfigPage() {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/findPushConfig",
        contentType: 'application/json',
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("pushConfig-update-modal-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#modal-dialog').html(html);
            }
        }
    });
}
function updatePushConfig(id) {
    var gtNoOpenDay = trim($('#u_gtNoOpenDay').val());
    var noOpenDay = trim($('#u_noOpenDay').val());
    var ltOpenDay = trim($('#u_ltOpenDay').val());
    var accountDay = trim($('#u_accountDay').val());
    var planDay = trim($('#u_planDay').val());
    var lowPlanRate = trim($('#u_lowPlanRate').val());
    var middlePlanRate = trim($('#u_middlePlanRate').val());
    var lowBudgetDay = trim($('#u_lowBudgetDay').val());
    var middleBudgetDay = trim($('#u_middleBudgetDay').val());
    var noOpenHour1 = trim($('#u_noOpenHour1').val());
    var noOpenHour2 = trim($('#u_noOpenHour2').val());
    var noOpenHour3 = trim($('#u_noOpenHour3').val());
    $.ajax({
        method: "POST",
        url: "/mm/user/updatePushConfig",
        contentType: 'application/json',
        data:JSON.stringify({
            "id": id,
            "gtNoOpenDay":gtNoOpenDay,
            "noOpenDay":noOpenDay,
            "ltOpenDay":ltOpenDay,
            "accountDay":accountDay,
            "planDay":planDay,
            "lowPlanRate":lowPlanRate,
            "middlePlanRate":middlePlanRate,
            "lowBudgetDay":lowBudgetDay,
            "middleBudgetDay":middleBudgetDay,
            "noOpenHour1":noOpenHour1,
            "noOpenHour2":noOpenHour2,
            "noOpenHour3":noOpenHour3
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                // var source   = document.getElementById("version-template").innerHTML;
                // var template = Handlebars.compile(source);
                // var context = {"result":data.result};
                // var html = template(context);
                $('#versionModal').modal('hide');
                findPushConfig();
            }
        }
    });

}

function findTopicAll() {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/findTopicAll",
        contentType: 'application/json',
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("topic-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result.list};
                var html = template(context);
                $('#content-display').html(html);
            }
        }
    });
}

function findTopic(id) {
    $.ajax({
        method: "POST",
        url: "/mm/user/findTopic",
        contentType: 'application/json',
        data:JSON.stringify({
            "id": id
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                var source   = document.getElementById("topic-modal-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#modal-dialog').html(html);
            }
        }
    });
}
function trim(str) {
    if (str===undefined || str===""){
        alert("所填内容不能为空!");
        return
    }
    return str.split(" ").join("");
}
function updateTopic(id) {
    var skinName = trim($('#u_skinName').val());
    var statusBarColor = trim($('#u_statusBarColor').val());
    var homeBackgroundColor = trim($('#u_homeBackgroundColor').val());
    var iconNormalColor = trim($('#u_iconNormalColor').val());
    var iconSelectColor = trim($('#u_iconSelectColor').val());
    var tabTextNormalColor = trim($('#u_tabTextNormalColor').val());
    var tabTextSelectColor = trim($('#u_tabTextSelectColor').val());
    var tabAddNormalColor = trim($('#u_tabAddNormalColor').val());
    var tabAddSelectColor = trim($('#u_tabAddSelectColor').val());
    var tabBgColor = trim($('#u_tabBgColor').val());
    var commonTitleColor = trim($('#u_commonTitleColor').val());
    var radioNormalColor = trim($('#u_radioNormalColor').val());
    var radioSelectColor = trim($('#u_radioSelectColor').val());
    var buttonNormalColor = trim($('#u_buttonNormalColor').val());
    var buttonSelectColor = trim($('#u_buttonSelectColor').val());
    $.ajax({
        method: "POST",
        url: "/mm/user/updateTopic",
        contentType: 'application/json',
        data:JSON.stringify({
            "id": id,
            "skinName":skinName,
            "statusBarColor":statusBarColor,
            "homeBackgroundColor":homeBackgroundColor,
            "iconNormalColor":iconNormalColor,
            "iconSelectColor":iconSelectColor,
            "tabTextNormalColor":tabTextNormalColor,
            "tabTextSelectColor":tabTextSelectColor,
            "tabAddNormalColor":tabAddNormalColor,
            "tabAddSelectColor":tabAddSelectColor,
            "tabBgColor":tabBgColor,
            "commonTitleColor":commonTitleColor,
            "radioNormalColor":radioNormalColor,
            "radioSelectColor":radioSelectColor,
            "buttonNormalColor":buttonNormalColor,
            "buttonSelectColor":buttonSelectColor
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                // var source   = document.getElementById("version-template").innerHTML;
                // var template = Handlebars.compile(source);
                // var context = {"result":data.result};
                // var html = template(context);
                $('#versionModal').modal('hide');
                findTopicAll();
            }
        }
    });

}
function deleteTopic(id) {
    if (confirm("是否确认删除?不支持，同步会出问题")){
        // $.ajax({
        //     method: "POST",
        //     url: "/mm/user/deleteTopic",
        //     contentType: 'application/json',
        //     data:JSON.stringify({
        //         "id":id
        //     }),
        //     success: function(data) {
        //         if (!data.success){
        //             alert(data.message);
        //         }else{
        //             // var source   = document.getElementById("version-template").innerHTML;
        //             // var template = Handlebars.compile(source);
        //             // var context = {"result":data.result};
        //             // var html = template(context);
        //             findTopicAll();
        //         }
        //     }
        // });
    }
}
var fb_choice_count = 1;
var id_array = new Array();
function addFbChoiceButton() {
    var html = $('#add_fb_choice_div').html();
    var id = "a_fb_topic_choice_"+fb_choice_count;
    var buttonId = "a_fb_topic_choice_button_"+fb_choice_count;
    var divId = "a_fb_topic_choice_div_"+fb_choice_count;
    html += "<div id='"+divId+"'>填空答案: <input class='form-control' type='text' id='"+id+"' /><button id='"+buttonId+"' class='btn btn-primary' onclick=\"removeFbChoiceButton('"+id+"','"+divId+"')\">-</button></div>";
    fb_choice_count += 1;
    id_array.push(id);
    console.log(id_array);
    $('#fb_topic_num').html(id_array.length);
    $('#add_fb_choice_div').html(html);
}
function removeFbChoiceButton(removeId,divId){
    $("#"+divId).remove();
    // $("#"+removeButtonId).remove();
    id_array.remove(removeId);
    $('#fb_topic_num').html(id_array.length);
    console.log(id_array);
}
function addFbTopic() {
    var topicTitle = $('#a_fb_topic_title').val();
    var topicImg = $('#a_fb_topic_img').val();
    var topicSubject = $('#a_fb_topic_subject').val();
    var topicAnalyze = $('#a_fb_topic_analyze').val();
    var choiceList = new Array();
    for ( var i = 0; i <id_array.length; i++){
        var choice = {};
        choice["id"] = i+1;
        choice["content"] = $("#"+id_array[i]).val();
        choiceList.push(choice);
    }
    $.ajax({
        method: "POST",
        url: "/tk_admin/topic/saveFbTopic",
        contentType: 'application/json',
        data:JSON.stringify({
            "topicTitle":topicTitle,
            "topicImg":topicImg,
            "topicSubject":topicSubject,
            "fillBlankChoiceList":choiceList,
            "topicAnalyze":topicAnalyze
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                // var source   = document.getElementById("version-template").innerHTML;
                // var template = Handlebars.compile(source);
                // var context = {"result":data.result};
                // var html = template(context);
                $('#versionModal').modal('hide');
                findTopicList(1);
            }
        }
    });

}
function addRadioTopic() {
    var topicTitle = $('#a_ra_topic_title').val();
    var topicImg = $('#a_ra_topic_img').val();
    var topicSubject = $('#a_ra_topic_subject').val();
    var answer = $('#a_ra_topic_answer').val();
    var topicRadioAChoice = $('#a_ra_topic_a_choice').val();
    var topicRadioBChoice = $('#a_ra_topic_b_choice').val();
    var topicRadioCChoice = $('#a_ra_topic_c_choice').val();
    var topicRadioDChoice = $('#a_ra_topic_d_choice').val();
    var topicRadioEChoice = $('#a_ra_topic_e_choice').val();
    var topicAnalyze = $('#a_ra_topic_analyze').val();
    $.ajax({
        method: "POST",
        url: "/tk_admin/topic/saveRadioTopic",
        contentType: 'application/json',
        data:JSON.stringify({
            "topicTitle":topicTitle,
            "topicImg":topicImg,
            "topicSubject":topicSubject,
            "topicRadioAChoice":topicRadioAChoice,
            "topicRadioBChoice":topicRadioBChoice,
            "topicRadioCChoice":topicRadioCChoice,
            "topicRadioDChoice":topicRadioDChoice,
            "topicRadioEChoice":topicRadioEChoice,
            "answer":answer,
            "topicAnalyze":topicAnalyze
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                // var source   = document.getElementById("version-template").innerHTML;
                // var template = Handlebars.compile(source);
                // var context = {"result":data.result};
                // var html = template(context);
                $('#versionModal').modal('hide');
                findTopicList(1);
            }
        }
    });

}
function addMulChoiceTopic() {
    var topicTitle = $('#a_mul_topic_title').val();
    var topicImg = $('#a_mul_topic_img').val();
    var topicSubject = $('#a_mul_topic_subject').val();
    var answer = $('#a_mul_topic_answer').val();
    var topicMulAChoice = $('#a_mul_topic_a_choice').val();
    var topicMulBChoice = $('#a_mul_topic_b_choice').val();
    var topicMulCChoice = $('#a_mul_topic_c_choice').val();
    var topicMulDChoice = $('#a_mul_topic_d_choice').val();
    var topicMulEChoice = $('#a_mul_topic_e_choice').val();
    var topicMulFChoice = $('#a_mul_topic_f_choice').val();
    var topicMulGChoice = $('#a_mul_topic_g_choice').val();
    var topicMulHChoice = $('#a_mul_topic_h_choice').val();
    var topicAnalyze = $('#a_mul_topic_analyze').val();
    $.ajax({
        method: "POST",
        url: "/tk_admin/topic/saveMulChoiceTopic",
        contentType: 'application/json',
        data:JSON.stringify({
            "topicTitle":topicTitle,
            "topicImg":topicImg,
            "topicSubject":topicSubject,
            "topicMulAChoice":topicMulAChoice,
            "topicMulBChoice":topicMulBChoice,
            "topicMulCChoice":topicMulCChoice,
            "topicMulDChoice":topicMulDChoice,
            "topicMulEChoice":topicMulEChoice,
            "topicMulFChoice":topicMulFChoice,
            "topicMulGChoice":topicMulGChoice,
            "topicMulHChoice":topicMulHChoice,
            "answer":answer,
            "topicAnalyze":topicAnalyze
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                // var source   = document.getElementById("version-template").innerHTML;
                // var template = Handlebars.compile(source);
                // var context = {"result":data.result};
                // var html = template(context);
                $('#versionModal').modal('hide');
                findTopicList(1);
            }
        }
    });

}
function addQATopic() {
    var topicTitle = $('#a_qa_topic_title').val();
    var topicImg = $('#a_qa_topic_img').val();
    var topicSubject = $('#a_qa_topic_subject').val();
    var answer = $('#a_qa_topic_answer').val();
    var topicAnalyze = $('#a_qa_topic_analyze').val();
    $.ajax({
        method: "POST",
        url: "/tk_admin/topic/saveQATopic",
        contentType: 'application/json',
        data:JSON.stringify({
            "topicTitle":topicTitle,
            "topicImg":topicImg,
            "topicSubject":topicSubject,
            "answer":answer,
            "topicAnalyze":topicAnalyze
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                // var source   = document.getElementById("version-template").innerHTML;
                // var template = Handlebars.compile(source);
                // var context = {"result":data.result};
                // var html = template(context);
                $('#versionModal').modal('hide');
                findTopicList(1);
            }
        }
    });

}
function addJudgeTopic() {
    var topicTitle = $('#a_judge_topic_title').val();
    var topicImg = $('#a_judge_topic_img').val();
    var topicSubject = $('#a_judge_topic_subject').val();
    var answer = $('#a_judge_topic_answer').val();
    var topicAnalyze = $('#a_judge_topic_analyze').val();
    $.ajax({
        method: "POST",
        url: "/tk_admin/topic/saveJudgeTopic",
        contentType: 'application/json',
        data:JSON.stringify({
            "topicTitle":topicTitle,
            "topicImg":topicImg,
            "topicSubject":topicSubject,
            "answer":answer,
            "topicAnalyze":topicAnalyze
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                // var source   = document.getElementById("version-template").innerHTML;
                // var template = Handlebars.compile(source);
                // var context = {"result":data.result};
                // var html = template(context);
                $('#versionModal').modal('hide');
                findTopicList(1);
            }
        }
    });

}
function addTopicModal() {
    $.ajax({
        method: "POST",
        url: "/tk_admin/subject/allSubjectList",
        contentType: 'application/json',
        data:JSON.stringify({
            "page":1,
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                $('#versionModal').modal('show');
                var source   = document.getElementById("topic-add-modal-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#modal-dialog').html(html);
            }
        }
    });
}
function addTopicRadioModal() {
    $.ajax({
        method: "POST",
        url: "/tk_admin/subject/allSubjectList",
        contentType: 'application/json',
        data:JSON.stringify({
            "page":1,
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                $('#versionModal').modal('show');
                var source   = document.getElementById("topic-radio-add-modal-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#modal-dialog').html(html);
            }
        }
    });


}
function addTopicMulChoiceModal() {
    $.ajax({
        method: "POST",
        url: "/tk_admin/subject/allSubjectList",
        contentType: 'application/json',
        data:JSON.stringify({
            "page":1,
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                $('#versionModal').modal('show');
                var source   = document.getElementById("topic-mul-choice-add-modal-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#modal-dialog').html(html);
            }
        }
    });
}
function addTopicQAModal() {

    $.ajax({
        method: "POST",
        url: "/tk_admin/subject/allSubjectList",
        contentType: 'application/json',
        data:JSON.stringify({
            "page":1,
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                $('#versionModal').modal('show');
                var source   = document.getElementById("topic-qa-add-modal-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#modal-dialog').html(html);
            }
        }
    });
}
function addTopicJudgeModal() {
    $.ajax({
        method: "POST",
        url: "/tk_admin/subject/allSubjectList",
        contentType: 'application/json',
        data:JSON.stringify({
            "page":1,
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                $('#versionModal').modal('show');
                var source   = document.getElementById("topic-judge-add-modal-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#modal-dialog').html(html);
            }
        }
    });
}

function findTopicList(page) {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/tk_admin/topic/findTopicList",
        contentType: 'application/json',
        data:JSON.stringify({
            "page":page
        }),
        success: function(data) {
                // var source   = document.getElementById("version-template").innerHTML;
                // var template = Handlebars.compile(source);
                // var context = {"result":data.result};
                // var html = template(context);
                $('#content-display').html(data);
        }
    });
}

function findVersionById(id) {
    $.ajax({
        method: "POST",
        url: "/mm/version/getVersionInfoById",
        contentType: 'application/json',
        data:JSON.stringify({
            "id": id
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                var source   = document.getElementById("version-modal-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#modal-dialog').html(html);
                $('#u_updateDesc').summernote();
            }
        }
    });
}
function addVersionModal() {
    $('#versionModal').modal('show');
    var source   = document.getElementById("version-add-modal-template").innerHTML;
    var template = Handlebars.compile(source);
    var html = template();
    $('#modal-dialog').html(html);
    $('#a_updateDesc').summernote();
}
function addVersion() {
    var versionId = $('#a_versionId').val();
    var versionStatus = $('#a_versionStatus').val();
    var updateUrl = $('#a_updateUrl').val();
    var updateDesc = $('#a_updateDesc').val();
    var targetVersion = $('#a_targetVersion').val();
    var versionCode = $('#a_versionCode').val();
    var language = $('#a_language').val();
    $.ajax({
        method: "POST",
        url: "/mm/version/addVersion",
        contentType: 'application/json',
        data:JSON.stringify({
            "versionId":versionId,
            "versionStatus":versionStatus,
            "updateUrl":updateUrl,
            "updateDesc":updateDesc,
            "targetVersion":targetVersion,
            "versionCode":versionCode,
            "language":language
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                // var source   = document.getElementById("version-template").innerHTML;
                // var template = Handlebars.compile(source);
                // var context = {"result":data.result};
                // var html = template(context);
                $('#versionModal').modal('hide');
                findVersion();
            }
        }
    });
}
function findUserScore(page) {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/user/findUserScore",
        contentType: 'application/json',
        data:JSON.stringify({
            "page": page,
            "pageSize": 20
        }),
        success: function (data) {
            if (!data.success) {
                alert("查找错误");
            } else {
                var source = document.getElementById("userScore-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result": data.result};
                var html = template(context);
                $('#content-display').html(html);
            }
        }
    });
}
function addCourseModal() {
    $.ajax({
        method: "POST",
        url: "/tk_admin/subject/allSubjectList",
        contentType: 'application/json',
        data:JSON.stringify({
            "page":1,
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                $('#versionModal').modal('show');
                var source   = document.getElementById("course-add-modal-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#modal-dialog').html(html);
            }
        }
    });

    // $('#a_updateDesc').summernote();
}
function addCourse() {
    $('#chooseDiv').html("");
    var courseName = $('#a_course_name').val();
    var courseDesc = $('#a_course_desc').val();
    var coursePrice = $('#a_course_price').val();
    var courseZhePrice = $('#a_course_zhe_price').val();
    var courseStatus = $('#a_course_status').val();
    var courseSubjects = "";
    $('input:checkbox[name=a_course_subjects]:checked').each(function (i) {
        if (0 == i) {
            courseSubjects = $(this).val();
        } else {
            courseSubjects += ("," + $(this).val());
        }
    });
    $.ajax({
        method: "POST",
        url: "/tk_admin/course/saveCourse",
        contentType: 'application/json',
        data:JSON.stringify({
            "courseName":courseName,
            "courseDesc":courseDesc,
            "courseStatus":courseStatus,
            "coursePrice":parseFloat(coursePrice),
            "courseZhePrice":parseFloat(courseZhePrice),
            "subjects":courseSubjects
        }),
        success: function (data) {
            if (!data.success) {
                alert("添加失败："+data.message);
            } else {
                alert("添加成功");
                $('#versionModal').modal('hide');
                findCourseList();
            }
        }
    });
}
function findCourseList() {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/tk_admin/course/findCourseList",
        contentType: 'application/json',
        data:JSON.stringify({
            "page": 1,
        }),
        success: function (data) {
            $('#content-display').html(data);
        }
    });
}

function addSubjectModal() {
    $('#versionModal').modal('show');
    var source   = document.getElementById("subject-add-modal-template").innerHTML;
    var template = Handlebars.compile(source);
    var html = template();
    $('#modal-dialog').html(html);
    // $('#a_updateDesc').summernote();
}
function addSubject() {
    $('#chooseDiv').html("");
    var subjectName = $('#a_subject_name').val();
    $.ajax({
        method: "POST",
        url: "/tk_admin/subject/saveSubject",
        contentType: 'application/json',
        data:JSON.stringify({
            "subjectName":subjectName
        }),
        success: function (data) {
            if (!data.success) {
                alert("添加失败："+data.message);
            } else {
                alert("添加成功");
                $('#versionModal').modal('hide');
                findSubjectList();
            }
        }
    });
}
function findSubjectList() {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/tk_admin/subject/findSubjectList",
        contentType: 'application/json',
        data:JSON.stringify({
            "page": 1,
        }),
        success: function (data) {
             $('#content-display').html(data);
        }
    });
}

function findAbConfig() {
    $('#chooseDiv').html("");
    $.ajax({
        method: "POST",
        url: "/mm/ab/find",
        contentType: 'application/json',
        success: function(data) {
            if (!data.success){
                alert("查找错误");
            }else{
                var source   = document.getElementById("ab-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#content-display').html(html);
            }
        }
    });
}
function viewTopicById(id) {
    $.ajax({
        method: "POST",
        url: "/tk_admin/topic/findTopicById",
        contentType: 'application/json',
        data:JSON.stringify({
            "id": id
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                var source   = document.getElementById("view-topic-modal-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result":data.result};
                var html = template(context);
                $('#modal-dialog').html(html);
            }
        }
    });
}
function updateAbConfig(id) {
    var abAction = $('#u_abAction').val();
    var abType = $('#u_abType').val();
    var uiType = $('#u_uiType').val();
    var abRate = $('#u_abRate').val();
    var uiAbRate = $('#u_uiAbRate').val();
    $.ajax({
        method: "POST",
        url: "/mm/ab/update",
        contentType: 'application/json',
        data:JSON.stringify({
            "id": id,
            "abAction":abAction,
            "abType":abType,
            "uiType":uiType,
            "abRate":abRate,
            "uiAbRate":uiAbRate
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                // var source   = document.getElementById("version-template").innerHTML;
                // var template = Handlebars.compile(source);
                // var context = {"result":data.result};
                // var html = template(context);
                $('#versionModal').modal('hide');
                findAbConfig();
            }
        }
    });

}
function addAbModal() {
    $('#versionModal').modal('show');
    var source   = document.getElementById("ab-add-modal-template").innerHTML;
    var template = Handlebars.compile(source);
    var html = template();
    $('#modal-dialog').html(html);
}
function addAbConfig() {
    var abAction = $('#a_abAction').val();
    var abType = $('#a_abType').val();
    var uiType = $('#a_uiType').val();
    var abRate = $('#a_abRate').val();
    var uiAbRate = $('#a_uiAbRate').val();
    $.ajax({
        method: "POST",
        url: "/mm/ab/add",
        contentType: 'application/json',
        data:JSON.stringify({
            "abAction":abAction,
            "abType":abType,
            "uiType":uiType,
            "abRate":abRate,
            "uiAbRate":uiAbRate
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                // var source   = document.getElementById("version-template").innerHTML;
                // var template = Handlebars.compile(source);
                // var context = {"result":data.result};
                // var html = template(context);
                $('#versionModal').modal('hide');
                findAbConfig();
            }
        }
    });
}

function delAbConfig(id) {
    $.ajax({
        method: "POST",
        url: "/mm/ab/del",
        contentType: 'application/json',
        data:JSON.stringify({
            "id": id
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                findAbConfig();
            }
        }
    });
}

function findStatisticsByAb(abType,uiType,fn) {
    var time = $('#statisticsTime').val();
    $.ajax({
        method: "POST",
        url: "/mm/statistics/findByAb",
        contentType: 'application/json',
        data:JSON.stringify({
            "abType": abType,
            "uiType": uiType,
            "time":time
        }),
        success: function(data) {
            if (!data.success){
                alert(data.message);
            }else{
                fn(data);
                //
                // var source = document.getElementById("statistic-template").innerHTML;
                // var template = Handlebars.compile(source);
                // var context = {"result": data.result};
                // var html = template(context);
                // $('#content-display').html(html);
            }
        }
    });
}
function statisticsDisplay() {
    $('#content-display').html("");
    var source = document.getElementById("statistic-display-template").innerHTML;
    var template = Handlebars.compile(source);
    var html = template();
    $('#chooseDiv').html(html);


    var picker1 = $('#datetimepicker2').datetimepicker({
        format: 'YYYY-MM-DD',
        locale: moment.locale('zh-cn'),
        //minDate: '2016-7-1'
    });
}
function findAbAccount(abType,uiType,fn) {
    var time = $('#statisticsTime').val();
    $.ajax({
        method: "POST",
        url: "/mm/statistics/findAbAccount",
        contentType: 'application/json',
        data:JSON.stringify({
            "abType":abType,
            "uiType":uiType,
            "time":time
        }),
        success: function(data) {
            fn(data);
        }
    });
}

function findAccount(fn) {
    var time = $('#statisticsTime').val();
    $.ajax({
        method: "POST",
        url: "/mm/statistics/findAccount",
        contentType: 'application/json',
        data:JSON.stringify({
            "time":time
        }),
        success: function(data) {
            fn(data);
        }
    });
}
function findStatisticsList() {
    $('#chooseDiv').html("");
    var spinner = new Spinner(opts);
    $.ajax({
        method: "POST",
        url: "/mm/statistics/findStatisticsList",
        contentType: 'application/json',
        beforeSend: function () {
            $('#myspin').text("");
            var target = $("#myspin").get(0);
            spinner.spin(target);
        },
        success: function (data) {
            if (data.success){
                spinner.spin();
                var source = document.getElementById("statistic-new-template").innerHTML;
                var template = Handlebars.compile(source);
                var context = {"result": data.result};
                var html = template(context);
                $('#content-display').html(html);
            }
        }
    });
}
function findStatistics() {
    var spinner = new Spinner(opts);
    var time = $('#statisticsTime').val();
    $.ajax({
        method: "POST",
        url: "/mm/statistics/find",
        contentType: 'application/json',
        data: JSON.stringify({
            "time": time
        }),
        beforeSend: function () {
            $('#myspin').text("");
            var target = $("#myspin").get(0);
            spinner.spin(target);
        },
        success: function (data) {
            // spinner.spin();
            if (!data.success) {
                alert(data.message);
                spinner.spin();
            } else {
                findAccount(function (accountData) {
                    findStatisticsByAb(0, 0, function (data1) {
                        findAbAccount(0, 0, function (accountData1) {
                            findStatisticsByAb(0, 1, function (data2) {
                                findAbAccount(0, 1, function (accountData2) {
                                    findStatisticsByAb(1, 0, function (data3) {
                                        findAbAccount(1, 0, function (accountData3) {
                                            findStatisticsByAb(1, 1, function (data4) {
                                                findAbAccount(1, 1, function (accountData4) {
                                                    spinner.spin();
                                                    for (var i in data.result) {
                                                        data.result[i].newerAppIdCount = accountData.result[i].newerAppIdCount;
                                                        data.result[i].startPeople = accountData.result[i].startPeople;
                                                        data.result[i].startNumber = accountData.result[i].startNumber;
                                                        data.result[i].countPlanPeople = accountData.result[i].countPlanPeople;
                                                        data.result[i].appIdCountUser = accountData.result[i].appIdCountUser;
                                                        data.result[i].countAppIdPlanPeople = accountData.result[i].countAppIdPlanPeople;
                                                        data.result[i].countAccount = accountData.result[i].countAccount;
                                                        data.result[i].countAccountPeople = accountData.result[i].countAccountPeople;
                                                        data.result[i].countAppIdAccount = accountData.result[i].countAppIdAccount;
                                                        data.result[i].countAppIdAccountPeople = accountData.result[i].countAppIdAccountPeople;
                                                        data.result[i].planAccountRate = accountData.result[i].planAccountRate;
                                                        data.result[i].appIdPlanAccountRate = accountData.result[i].appIdPlanAccountRate;
                                                        data.result[i].appIdAccountRate = accountData.result[i].appIdAccountRate;
                                                        data.result[i].accountStartRate = accountData.result[i].accountStartRate;
                                                        data.result[i].registerAppIdPlanRate = accountData.result[i].registerAppIdPlanRate;

                                                        data.result[i].accountStartRate00 = accountData1.result[i].accountStartRate00;
                                                        data.result[i].accountStartRate01 = accountData2.result[i].accountStartRate01;
                                                        data.result[i].accountStartRate10 = accountData3.result[i].accountStartRate10;
                                                        data.result[i].accountStartRate11 = accountData4.result[i].accountStartRate11;
                                                        data.result[i].allAccountPlanRate00 = data1.result[i].allAccountPlanRate00;
                                                        data.result[i].allAccountPlanRate01 = data2.result[i].allAccountPlanRate01;
                                                        data.result[i].allAccountPlanRate10 = data3.result[i].allAccountPlanRate10;
                                                        data.result[i].allAccountPlanRate11 = data4.result[i].allAccountPlanRate11;
                                                        data.result[i].allAccountRate00 = data1.result[i].allAccountRate00;
                                                        data.result[i].allAccountRate01 = data2.result[i].allAccountRate01;
                                                        data.result[i].allAccountRate10 = data3.result[i].allAccountRate10;
                                                        data.result[i].allAccountRate11 = data4.result[i].allAccountRate11;
                                                        data.result[i].allPlanRate00 = data1.result[i].allPlanRate00;
                                                        data.result[i].allPlanRate01 = data2.result[i].allPlanRate01;
                                                        data.result[i].allPlanRate10 = data3.result[i].allPlanRate10;
                                                        data.result[i].allPlanRate11 = data4.result[i].allPlanRate11;
                                                        data.result[i].appIdAccountRate00 = accountData1.result[i].appIdAccountRate00;
                                                        data.result[i].appIdAccountRate01 = accountData2.result[i].appIdAccountRate01;
                                                        data.result[i].appIdAccountRate10 = accountData3.result[i].appIdAccountRate10;
                                                        data.result[i].appIdAccountRate11 = accountData4.result[i].appIdAccountRate11;
                                                        data.result[i].appIdPlanAccountRate00 = accountData1.result[i].appIdPlanAccountRate00;
                                                        data.result[i].appIdPlanAccountRate01 = accountData2.result[i].appIdPlanAccountRate01;
                                                        data.result[i].appIdPlanAccountRate10 = accountData3.result[i].appIdPlanAccountRate10;
                                                        data.result[i].appIdPlanAccountRate11 = accountData4.result[i].appIdPlanAccountRate11;
                                                        data.result[i].countAccount00 = accountData1.result[i].countAccount00;
                                                        data.result[i].countAccount01 = accountData2.result[i].countAccount01;
                                                        data.result[i].countAccount10 = accountData3.result[i].countAccount10;
                                                        data.result[i].countAccount11 = accountData4.result[i].countAccount11;
                                                        data.result[i].countAccountPeople00 = accountData1.result[i].countAccountPeople00;
                                                        data.result[i].countAccountPeople01 = accountData2.result[i].countAccountPeople01;
                                                        data.result[i].countAccountPeople10 = accountData3.result[i].countAccountPeople10;
                                                        data.result[i].countAccountPeople11 = accountData4.result[i].countAccountPeople11;
                                                        data.result[i].countAllAccountPeople00 = data1.result[i].countAllAccountPeople00;
                                                        data.result[i].countAllAccountPeople01 = data2.result[i].countAllAccountPeople01;
                                                        data.result[i].countAllAccountPeople10 = data3.result[i].countAllAccountPeople10;
                                                        data.result[i].countAllAccountPeople11 = data4.result[i].countAllAccountPeople11;
                                                        data.result[i].countAllPlanPeople00 = data1.result[i].countAllPlanPeople00;
                                                        data.result[i].countAllPlanPeople01 = data2.result[i].countAllPlanPeople01;
                                                        data.result[i].countAllPlanPeople10 = data3.result[i].countAllPlanPeople10;
                                                        data.result[i].countAllPlanPeople11 = data4.result[i].countAllPlanPeople11;
                                                        data.result[i].countAllUser00 = data1.result[i].countAllUser00;
                                                        data.result[i].countAllUser01 = data2.result[i].countAllUser01;
                                                        data.result[i].countAllUser10 = data3.result[i].countAllUser10;
                                                        data.result[i].countAllUser11 = data4.result[i].countAllUser11;
                                                        data.result[i].countAppIdAccount00 = accountData1.result[i].countAppIdAccount00;
                                                        data.result[i].countAppIdAccount01 = accountData2.result[i].countAppIdAccount01;
                                                        data.result[i].countAppIdAccount10 = accountData3.result[i].countAppIdAccount10;
                                                        data.result[i].countAppIdAccount11 = accountData4.result[i].countAppIdAccount11;
                                                        data.result[i].countAppIdAccountPeople00 = accountData1.result[i].countAppIdAccountPeople00;
                                                        data.result[i].countAppIdAccountPeople01 = accountData2.result[i].countAppIdAccountPeople01;
                                                        data.result[i].countAppIdAccountPeople10 = accountData3.result[i].countAppIdAccountPeople10;
                                                        data.result[i].countAppIdAccountPeople11 = accountData4.result[i].countAppIdAccountPeople11;
                                                        data.result[i].countAppIdAllUser00 = data1.result[i].countAppIdAllUser00;
                                                        data.result[i].countAppIdAllUser01 = data2.result[i].countAppIdAllUser01;
                                                        data.result[i].countAppIdAllUser10 = data3.result[i].countAppIdAllUser10;
                                                        data.result[i].countAppIdAllUser11 = data4.result[i].countAppIdAllUser11;
                                                        data.result[i].countAppIdPlan00 = data1.result[i].countAppIdPlan00;
                                                        data.result[i].countAppIdPlan01 = data2.result[i].countAppIdPlan01;
                                                        data.result[i].countAppIdPlan10 = data3.result[i].countAppIdPlan10;
                                                        data.result[i].countAppIdPlan11 = data4.result[i].countAppIdPlan11;
                                                        data.result[i].countAppIdPlanPeople00 = accountData1.result[i].countAppIdPlanPeople00;
                                                        data.result[i].countAppIdPlanPeople01 = accountData2.result[i].countAppIdPlanPeople01;
                                                        data.result[i].countAppIdPlanPeople10 = accountData3.result[i].countAppIdPlanPeople10;
                                                        data.result[i].countAppIdPlanPeople11 = accountData4.result[i].countAppIdPlanPeople11;
                                                        data.result[i].countPlan00 = data1.result[i].countPlan00;
                                                        data.result[i].countPlan01 = data2.result[i].countPlan01;
                                                        data.result[i].countPlan10 = data3.result[i].countPlan10;
                                                        data.result[i].countPlan11 = data4.result[i].countPlan11;
                                                        data.result[i].countPlanPeople00 = accountData1.result[i].countPlanPeople00;
                                                        data.result[i].countPlanPeople01 = accountData2.result[i].countPlanPeople01;
                                                        data.result[i].countPlanPeople10 = accountData3.result[i].countPlanPeople10;
                                                        data.result[i].countPlanPeople11 = accountData4.result[i].countPlanPeople11;
                                                        data.result[i].countRegisterAllUser00 = data1.result[i].countRegisterAllUser00;
                                                        data.result[i].countRegisterAllUser01 = data2.result[i].countRegisterAllUser01;
                                                        data.result[i].countRegisterAllUser10 = data3.result[i].countRegisterAllUser10;
                                                        data.result[i].countRegisterAllUser11 = data4.result[i].countRegisterAllUser11;
                                                        data.result[i].newerAppIdCount00 = accountData1.result[i].newerAppIdCount00;
                                                        data.result[i].newerAppIdCount01 = accountData2.result[i].newerAppIdCount01;
                                                        data.result[i].newerAppIdCount10 = accountData3.result[i].newerAppIdCount10;
                                                        data.result[i].newerAppIdCount11 = accountData4.result[i].newerAppIdCount11;
                                                        data.result[i].newerCount00 = data1.result[i].newerCount00;
                                                        data.result[i].newerCount01 = data2.result[i].newerCount01;
                                                        data.result[i].newerCount10 = data3.result[i].newerCount10;
                                                        data.result[i].newerCount11 = data4.result[i].newerCount11;
                                                        data.result[i].planAccountRate00 = accountData1.result[i].planAccountRate00;
                                                        data.result[i].planAccountRate01 = accountData2.result[i].planAccountRate01;
                                                        data.result[i].planAccountRate10 = accountData3.result[i].planAccountRate10;
                                                        data.result[i].planAccountRate11 = accountData4.result[i].planAccountRate11;
                                                        data.result[i].registerAccountRate00 = data1.result[i].registerAccountRate00;
                                                        data.result[i].registerAccountRate01 = data2.result[i].registerAccountRate01;
                                                        data.result[i].registerAccountRate10 = data3.result[i].registerAccountRate10;
                                                        data.result[i].registerAccountRate11 = data4.result[i].registerAccountRate11;
                                                        data.result[i].registerAppIdPlanRate00 = accountData1.result[i].registerAppIdPlanRate00;
                                                        data.result[i].registerAppIdPlanRate01 = accountData2.result[i].registerAppIdPlanRate01;
                                                        data.result[i].registerAppIdPlanRate10 = accountData3.result[i].registerAppIdPlanRate10;
                                                        data.result[i].registerAppIdPlanRate11 = accountData4.result[i].registerAppIdPlanRate11;
                                                        data.result[i].registerPlanRate00 = data1.result[i].registerPlanRate00;
                                                        data.result[i].registerPlanRate01 = data2.result[i].registerPlanRate01;
                                                        data.result[i].registerPlanRate10 = data3.result[i].registerPlanRate10;
                                                        data.result[i].registerPlanRate11 = data4.result[i].registerPlanRate11;
                                                    }
                                                    var source = document.getElementById("statistic-template").innerHTML;
                                                    var template = Handlebars.compile(source);
                                                    var context = {"result": data.result};
                                                    var html = template(context);
                                                    $('#content-display').html(html);
                                                });
                                            });
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            }
        }
    });
}
var tableToExcel = (function() {
    var uri = 'data:application/vnd.ms-excel;base64,',
        template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40">'
            + '<head><meta http-equiv="Content-type" content="text/html;charset=UTF-8" /><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/>'
            + '</x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>',
        base64 = function(s) {
            return window.btoa(unescape(encodeURIComponent(s)))
        },
        format = function(s, c) {
            return s.replace(/{(\w+)}/g, function(m, p) {
                return c[p];
            })
        };
    return function(table, name) {
        var ctx = {
            worksheet : name || 'Worksheet',
            table : table.innerHTML
        };
        return uri + base64(format(template, ctx));
    }
})();

function exportExcel(){
    $('#exportExcel').attr('href',tableToExcel($('#statistic-table')[0], 'moneymate'));
}
function exportNewExcel(){
    $('#exportNewExcel').attr('href',tableToExcel($('#statistic-new-table')[0], 'moneymate'));
}