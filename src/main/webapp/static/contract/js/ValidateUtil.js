/**
 * Created by lz on 2017/6/10.
 *
 *    ********************** Important *********************
 *
 * Note one：
 *     These methods here are used in multi-places, be very carefull to change them.
 * Note two：
 *     If you want to change any of the codes, tell me please!
 */

//is the str is null or ""
//return：null：true；not null：false
var validateIsNotNull = function (str) {
    return "" != str && str !=null && str != undefined
};

//is the str is include Chinese characters
//return：exist：true；not exite：false
var validateChinese = function (str) {
    console.info(str);
    return /.*[\u4e00-\u9fa5]+.*$/.test(str)
};

// is the str length < the length
//return：<=：true；>：false
var validateLength = function (str,len) {
    return str.length <= len
};

//is only num in the str
//yes:true;no:false
var validateNum = function (str) {
    return /^\d+[.]?\d*$/.test(str)
};

//is there specialChars in str
//return: exist:true; not exist:false
var validateSpecialChars = function(str){
    if(str=='')
        return false;
    else if(/^[0-9A-Za-z_]{1,}$/.test(str))
        return false;
    return true;
};

//date ：2017-06-15 16:21:31
//return: start<end:true; start<end:false
var validateStartEndTime = function(start,end){
    var s = new Date(start);
    var e = new Date(end);
    return s < e;
    // console.info("结束时间不能小于开始时间！");
};

//check if the str in allCaname or not
var checkStrExist = function (str,allCname) {
    for(var i=0;i<allCname.length;i++){
        if (str == allCname[i])
            return true;
    }
    return false;
};

//is the expression is a correct expression
var validateExpress = function(str,allCname){
    var resArray = str.split(/[-+*/&|!><=().]/);
    var flag = true;
    var reStr = '条件`'+str+'`中：';
    for (var i=0;i<resArray.length;i++){
        if(resArray[i]!="" && !(/^[0-9]|(true)|(false)|(\"(.*?)\")/g.test(resArray[i]))){
            var reFlag = checkStrExist(resArray[i],allCname);
            var reCname = resArray[i];
            flag = flag && reFlag;
            if(!reFlag)
                reStr = reStr + '标识：' +reCname+ '，未找到，请检查。';
        }
    }
    if(!flag)
        return reStr;
    return flag;
};

//validate func is name like 'Func***(a,b,c,...) and cname'
var validateFunc = function (str) {
    var resArray = str.split(/[(),]/);
    var flag = true;
    var reStr = '函数`'+str+'`中：';
    var isFunc = resArray.indexOf('Func');
    if(isFunc < 0)
        return reStr+"命名异常，请检查";
    for (var i=1;i<resArray.length;i++){
        if(resArray[i]!="" && !(/^[0-9]|(true)|(false)|(\"(.*?)\")/g.test(resArray[i]))){
            var reFlag = checkStrExist(resArray[i],allCname);
            var reCname = resArray[i];
            flag = flag && reFlag;
            if(!reFlag)
                reStr = reStr + '标识：' +reCname+ '，未找到，请检查。';
        }
    }
    if(!flag)
        return reStr;
    return flag;
};

//validate cell property value
var validateCommonCell = function (parentName,name,value,cnameArray) {
    var len = 0;
    var reStr = '';
    var isCname = name.indexOf('Cname');
    var isExpression = name.indexOf('ExpressionStr');
    var isFunc = name.indexOf('Func');
    var isCaption = name.indexOf('Caption');
    var isDescription = name.indexOf('Description');
    if(isCname >=0){
        //***_Cname validate
        len = 50;
        console.info(value);
        len = 50;
        // console.info(name);
        reStr = validateLength(value,len)?'':parentName+':'+name+':'+'标识：长度不能超过'+len+'个字符！';
        reStr = validateIsNotNull(value)?reStr:parentName+':'+name+':'+'标识：不能为空！';
        reStr = validateSpecialChars(value)?parentName+':'+name+':'+'标识:不能含有特殊字符或中文':reStr;
        return reStr;
    }else if(isExpression >=0){
        reStr = validateExpress(value,cnameArray)==true?'':parentName+':'+name+':'+validateExpress(value,cnameArray);
        // return reStr;
    }else if(isFunc >= 0){
        reStr = validateFunc(value,cnameArray)==true?'':parentName+':'+name+':'+validateFunc(value,cnameArray);
        return reStr
    }else if(isCaption >= 0){
        len = 50;
        reStr = validateLength(value,len)?'':parentName+':'+name+':'+'名称：长度不能超过'+len+'个字符！';
        reStr = validateIsNotNull(value)?reStr:parentName+':'+name+':'+'名称：不能为空！';
        return reStr;
    }else if(isDescription >= 0){
        len = 300;
        // console.info(name);
        return (validateIsNotNull(value)&&validateLength(value,len))?'':parentName+':'+name+':'+'描述：不能为空且长度不能超过'+len+'个字符！';
    }else if(parentName=='Start'){
        switch(name){
            case 'ContractAssets_Amount':
                reStr = validateNum(value)?'':parentName+':'+name+':'+'只能输入数字！';
                reStr = validateIsNotNull(value)?reStr:'';
                return reStr;
            default:
                return '';
        }
    }else if (parentName=='Decision'){
        switch(name){
            default:
                return '';
        }
    }else if (parentName=='Enquiry'){
        switch(name){
            default:
                return '';
        }
    }else if (parentName=='Action'){
        switch(name){
            default:
                return '';
        }
    }else if ('Plan' == parentName){
        switch(name){
            default:
                return '';
        }
    }
    return '';
};

var validateGraph = function (ui) {
    var editor = ui.editor;
    var graph = editor.graph;
    var model = graph.getModel();
    var cellsObj = model.cells;
    var resStr = '';
    var startCell = 0;
    for (var x in cellsObj){
        // console.info(x);
        if (x==0 || x==1)
            continue;
        var mxcell = cellsObj[x];
        if (mxcell.typeId == 'Start')
            startCell++;
        if(mxcell.edge){
            //validate the edge has source ang target
            if(mxcell.source==null && mxcell.target==null)
                resStr = resStr + "/nc:edge:存在连线找不到起始组件和目标组件！";
            else if(mxcell.source==null && mxcell.target!=null)
                resStr = resStr + "/nc:edge:存在连线找不到起始组件，目标组件名称为："+mxcell.target.value.getAttribute('Caption');
            else if(mxcell.source!=null && mxcell.target==null )
                resStr = resStr + "/nc:edge:存在连线找不到目标组件，起始组件名称为："+mxcell.source.value.getAttribute('Caption');
        }else{
            //validate the cell has more then one edge
            if(!mxcell.getEdgeCount())
                resStr = resStr + "/nb:cell:"+mxcell.getTypeId()+"，名称为："+mxcell.value.getAttribute('Caption')+"的组件没有连线！";
        }
    }
    if(!startCell)
        resStr = resStr + "/na:a:没有开始节点！";
    else if (startCell>1)
        resStr = resStr + "/na:a:找到多个开始节点！";
    return resStr
};

var validateArrangePanel = function (ui) {
    // return '';
    var editor = ui.editor;
    var graph = editor.graph;
    var model = graph.getModel();
    var cellsObj = model.cells;
    var resStr = '';
    var cnameArray = graph.getCnameArray()
    for (var x in cellsObj){
        // console.info(x);
        if (x==0 || x==1)
            continue;
        var mxcell = cellsObj[x];
        if(mxcell.edge)
            continue;
        var values = mxcell.value.attributes;
        var parentName = mxcell.typeId;
        for(var i=0;i<values.length;i++){
            var name = values[i].name;
            var value = values[i].value;
            // console.info(value);
            var s = validateCommonCell(parentName,name,value,cnameArray);
            if(s!='')
                resStr = resStr + '/n'+x+':'+i+':'+s;
        }
    }
    return resStr
};

//validate  the panel include proerty and graph
var validateMxGraph  =function(ui){
    var panelRes = validateArrangePanel(ui);
    var graphRes = validateGraph(ui);
    return panelRes + '*' + graphRes;
};