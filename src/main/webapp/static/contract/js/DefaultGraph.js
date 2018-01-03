DefaultGraph = function(editorUi){
    this.editorUi = editorUi;
    window.default_graph = this;
    window.mxgraoh_index = window.mxgraoh_index +1;
    // console.log("DefaultGraph",window.mxgraoh_index);
//  debugger;
    var that = this;
    setTimeout(function(){
        that.init();
    },500);

};
/**
 * 添加默认的start的menue
 * @returns {null}
 */
EditorUi.prototype.createDefaultMenu = function()
{
    return new DefaultGraph(this);

};
DefaultGraph.prototype.init = function () {
    debugger
    var action = this.editorUi.actions.get("import");
    action.funct();
    var defaultField = '<mxGraphModel dx="1726" dy="495" grid="1" gridSize="10" guides="1" tooltips="1" connect="1" arrows="1" fold="1" page="1" pageScale="1" pageWidth="826" pageHeight="1169" background="#ffffff" ><root><mxCell id="0"/><mxCell id="1" parent="0"/><object label="开始" Ctype="Component_Contract" id="21" ><mxCell style="ellipse;whiteSpace=wrap;html=1;fill=#006600;fillColor=#00CC00;0" vertex="1" typeId = "Start" parent="1"><mxGeometry x="80" y="110" width="40" height="40" as="geometry"/></mxCell></object></root></mxGraphModel>';
    window.openFile.setData(defaultField, "智能合约");
}