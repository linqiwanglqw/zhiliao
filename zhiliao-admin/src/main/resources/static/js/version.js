function showVersion(id){
    var url = "/moneymateManagement/getVersionInfoById";
    var fn = function (callResult) {
        var versionModel = callResult.result;
        $("#version_VersionId").text(versionModel.versionId);
        $("#version_VersionUrl").text(versionModel.updateUrl);
        $("#version_VersionDesc").text(versionModel.updateDesc);
        $("#version_Versionstatus").text(versionModel.status);
        $("#version_countryName").text(versionModel.countryName);
        $("#version_VersionCode").text(versionModel.versionCode);
        $("#version_targetVersion").text(versionModel.targetVersion);
    };
    var data = "id="+id;
    var dataType = "json";
    $.get(url,data,fn,dataType);
}

function editVersion(id){
    var url = "/moneymateManagement/getVersionInfoById";
    var fn = function (callResult) {
        var versionModel = callResult.result;
        $("#edit_versionId").val(versionModel.versionId);
        $("#edit_id").val(versionModel.id);
        $("#edit_versionUrl").val(versionModel.updateUrl);
        $("#edit_versionDesc").val(versionModel.updateDesc);
        $("#edit_versionLanguage").val(versionModel.language);
        $("#edit_versionCode").val(versionModel.versionCode);
        $("#edit_versionTarget").val(versionModel.targetVersion);
        $(".edit_versionStatus[value='0']").removeAttr("checked");
        $(".edit_versionStatus[value='"+versionModel.versionStatus+"']").prop('checked','true');
    };
    var data = "id="+id;
    var dataType = "json";
    $.get(url,data,fn,dataType);
}


function saveEditVersionInfo() {
    var versionId = $("#edit_versionId").val();
    var updateUrl = $("#edit_versionUrl").val();
    var updateDesc = $("#edit_versionDesc").val();
    var language = $("#edit_versionLanguage").val();
    var versionCode = $("#edit_versionCode").val();
    var targetVersion = $("#edit_versionTarget").val();
    var versionStatus = $(".edit_versionStatus:checked").val();
    var id = $("#edit_id").val();
    // 清除两边的空格
    String.prototype.trim = function() {
        return this.replace(/(^\s*)|(\s*$)/g, '');
    };

    if (versionId==null || versionId.trim()==""){
        alert("对外展示版本号不能是空");
        return;
    }
    if (updateUrl==null || updateUrl.trim()==""){
        alert("更新地址不能是空");
        return;
    }
    if (updateDesc==null || updateDesc.trim()==""){
        alert("版本描述不能是空");
        return;
    }
    if (language==null || language.trim()==""){
        alert("版本语言不能是空");
        return;
    }
    if (versionCode==null || versionCode.trim()==""){
        alert("实际版本号不能是空");
        return;
    }
    if (targetVersion==null || targetVersion.trim()==""){
        alert("目标版本号不能是空");
        return;
    }
    if (id==null || id.trim()==""){
        alert("版本ID不能是空");
        return;
    }
    if (versionStatus==null || versionStatus.trim()==""){
        alert("版本状态不能是空");
        return;
    }

    var url = "/moneymateManagement/editVersion";
    var successStr = "修改成功";
    var failStr = "修改失败";
    if (id == -1){
        url = "/moneymateManagement/addVersion";
        successStr = "添加成功";
        failStr = "添加失败";
    }
    var data = {"id":id,"versionId":versionId,"updateUrl":updateUrl,"updateDesc":updateDesc,
        "language":language,"targetVersion":targetVersion,"versionCode":versionCode,"versionStatus":versionStatus};
    var dataType = "json";
    var fn = function(data){
        if(data.result == true){
            alert(successStr);
        }else{
            alert(failStr);
        }
        window.location.href="/moneymateManagement/showVersion"
    }
    $.post(url,data,fn,dataType);
}





function delVersion(id){
    var url = "/moneymateManagement/delVersionById";
    var fn = function (data) {
        if(data.result == true){
            alert("删除成功");
        }else{
            alert("删除失败");
        }
        window.location.href="/moneymateManagement/showVersion"
    };
    var data = "id="+id;
    var dataType = "json";
    $.get(url,data,fn,dataType);
}

function delByQuery(){
    var url = "/moneymateManagement/delVersionsByQuery";
    var versionIds = [];
    //$('input:checkbox:checked') 等同于 $('input[type=checkbox]:checked')
    //意思是选择被选中的checkbox
    $.each($('input:checkbox:checked'),function(){
        var value = $(this).val();
        if(value != "on"){
            versionIds.push(value);
        }
    });
    var fn = function (data) {
        if(data.result == true){
            alert("删除成功");
        }else{
            alert("删除失败");
        }
        window.location.href="/moneymateManagement/showVersion"
    };
    var data = {versionIds:versionIds};
    var dataType = "json";
    $.post(url,data,fn,dataType);
}

function selectAll(){
    if($("#select_all").get(0).checked){
        $("[name=version_select_checkbox]").attr("checked",true);
    } else{
        $("[name=version_select_checkbox]").attr("checked",false);
    }

}


function searchVersionByVersionId() {
    var versionId = $("#searchVersionId").val();
    window.location.href = "/moneymateManagement/searchVersionByVersionId?versionId="+versionId;
}

