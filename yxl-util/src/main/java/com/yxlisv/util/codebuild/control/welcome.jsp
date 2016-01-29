<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/jsp/commons/Style_Resources.jsp"%>
<%@ include file="/jsp/commons/Script_Resources.jsp"%>
<%@ include file="/jsp/commons/tipMessage.jsp"%>
 
<h4 style="margin:30px 0 10px 0">工具栏按钮</h4>
<div class="tool">
    <p><a class="ajaxLink" window="true" url="${pageContext.request.contextPath }/manager/employee/add" method="get"><strong class="add"></strong><span>新增</span></a></p>
    <p><a href="javascript:void(0)"><strong class="remove"></strong><span>删除</span></a></p>
    <p><a href="javascript:void(0)"><strong class="edit"></strong><span>修改</span></a></p>
    <p><a href="javascript:void(0)"><strong class="search"></strong><span>查询</span></a></p>
    <p><a href="javascript:void(0)"><strong class="save"></strong><span>保存</span></a></p>
    <p><a href="javascript:void(0)"><strong class="attachment"></strong><span>附件</span></a></p>
    <p><a href="javascript:void(0)"><strong class="table"></strong><span>表格</span></a></p>
    <p><a href="javascript:void(0)"><strong class="reback"></strong><span>返回</span></a></p>
    <p><a href="javascript:void(0)"><strong class="delivery"></strong><span>配送</span></a></p>
    <p><a href="javascript:void(0)"><strong class="excel"></strong><span>Excel导入</span></a></p>
    <p><a href="javascript:void(0)"><strong class="exportexcel"></strong><span>Excel导出</span></a></p>
    <p><a href="javascript:void(0)"><strong class="rent"></strong><span>生成租金报价单</span></a></p>
    <p><a href="javascript:void(0)"><strong class="build"></strong><span>查看装修流程</span></a></p>
    <p><a href="javascript:void(0)"><strong class="addPeople"></strong><span>新增跑盘员</span></a></p>
    <p><a href="javascript:void(0)"><strong class="minusPeople"></strong><span>员工离职</span></a></p>
</div>
<div class="tool">
    <p><a href="javascript:void(0)"><strong class="moreadd"></strong><span>批量新增</span></a></p>
    <p><a href="javascript:void(0)"><strong class="build"></strong><span>楼层设置</span></a></p>
    <p><a href="javascript:void(0)"><strong class="moreBuildSet"></strong><span>批量设置</span></a></p>
    <p><a href="javascript:void(0)"><strong class="completerate"></strong><span>完备率</span></a></p>
    <p><a href="javascript:void(0)"><strong class="modelrate"></strong><span>建模率</span></a></p>
    <p><a href="javascript:void(0)"><strong class="package"></strong><span>资料打包</span></a></p>
    <p><a href="javascript:void(0)"><strong class="ticketNumSet"></strong><span>联次设置</span></a></p>
    <p><a href="javascript:void(0)"><strong class="ticketSet"></strong><span>票据联设置</span></a></p>
    <p><a href="javascript:void(0)"><strong class="receive"></strong><span>接收人</span></a></p>
    <p><a href="javascript:void(0)"><strong class="adjustment"></strong><span>调整</span></a></p>
    <p><a href="javascript:void(0)"><strong class="upload"></strong><span>上传</span></a></p>
    <p><a href="javascript:void(0)"><strong class="disable"></strong><span>禁用</span></a></p>
    <p><a href="javascript:void(0)"><strong class="Documentdisable"></strong><span>文档禁用</span></a></p>
    <p><a href="javascript:void(0)"><strong class="Documentenable"></strong><span>文档启用</span></a></p>
</div>
<div class="tool">
    <p><a href="javascript:void(0)"><strong class="tools"></strong><span>报修</span></a></p>
    <p><a href="javascript:void(0)"><strong class="printing"></strong><span>打印</span></a></p>
    <p><a href="javascript:void(0)"><strong class="copys"></strong><span>复制</span></a></p>
    <p><a href="javascript:void(0)"><strong class="crash"></strong><span>报废</span></a></p>
    <p><a href="javascript:void(0)"><strong class="cardinfo"></strong><span>证照信息</span></a></p>
    <p><a href="javascript:void(0)"><strong class="numberinfo"></strong><span>生成合同编码</span></a></p>
    <p><a href="javascript:void(0)"><strong class="storemap"></strong><span>铺位地图</span></a></p>
    <p><a href="javascript:void(0)"><strong class="limit"></strong><span>授权</span></a></p>
    <p><a href="javascript:void(0)"><strong class="request"></strong><span>包点接收</span></a></p>
    <p><a href="javascript:void(0)"><strong class="send"></strong><span>包点发送</span></a></p>
    <p><a href="javascript:void(0)"><strong class="editor"></strong><span>编辑</span></a></p>
    <p><a href="javascript:void(0)"><strong class="stencilclose"></strong><span>关闭</span></a></p>
</div>
<div id="container">
	<!--工具栏开始 -->
	<div class="tool">
        <p><a href="javascript:void(0)"><strong class="add"></strong><span>新增</span></a></p>
        <p><a href="javascript:void(0)"><strong class="remove"></strong><span>删除</span></a></p>
        <p><a href="javascript:void(0)"><strong class="edit"></strong><span>修改</span></a></p>
        <p><a href="javascript:void(0)"><strong class="excel"></strong><span>Excel</span></a></p>
    </div>
    <!--工具栏结束 -->
    
    <!--搜索栏开始 -->
    <div class="searchBox">   
            <ul class="wrapfix">             
                <li><select name="">
                    <option>选择类型</option>
                </select></li>
                <li>
                    <div class="f7" style="color:#999;width:115px">
                        <input type="text" readonly="true" name="deptName" value="" style="color:#999;width:110px;">
                        <strong title="清空"></strong>
                        <span class="p_hov" onclick="" title="选择"></span>
                    </div>
                </li>
                <li><input name="" type="text" reg="number" tip="" class="Wdate" value="2013-05-06"></li>
                <li><input type="text" value="房号" class="default input2"  /></li>
              
                <li class="split"></li>
                <li><input type="text" value="关键字"  class="default kwd" /></li>
                
                <li class="btns"><a href="javascript:void(0)" class="mini-button importantBtn"><span class="mini-button-text  ">新增</span></a></li>
                <li><a class="toAdvanceSh"  id="toAdvanceSh" href="javascript:void(0)"> 高级查询</a></li>
            </ul> 
            <ul  id="searchAdvance" class="wrapfix searchAdvance">
                <li><input type="text"  value="区域" class="default input1" /></li>
                <li><input type="text"  value="片区/商圈" class="default input1" /></li>
                <li class="split"></li>
    
                <li><input type="text" value="面积" class="default input2"  /></li>
                 
                <li><input type="text" value="楼层" class="default input2" /> </li>	 
                <li><input type="text" value="售价[万]" class="default input2"/></li>
                <li><input type="text" value="租价[元]" class="default input2"/></li>
                <li><input type="text" value="朝向" class="default input2"/></li>
                <li><input type="text" value="装修情况" class="default input2"/></li>
                 <li class="split"></li>
                <li> <select name="">
                    <option>路段</option>
                </select></li>
                <li><input type="text" value="路段" class="default" style="width:133px"/></li>
              </ul> 
    </div>
    <!--搜索栏结束 -->
    
    <div class="yyfgx"></div>
    
    <!--Tab栏开始 -->
   <div class="table_title">
		<ul id="state_menu">
        	<li  class="table_title_on">
	    		<a href="javascript:void(0)">全部<span>(13)</span></a> 
	        </li>
	    	<li class=" " curtab="sale">
				 
				<a href="javascript:void(0)" class="">其他</a> 
	    	
	        </li>
	        <li>
	    		<a href="javascript:void(0)" class="">其他</a> 
	    		
	        </li>
	      
	        <li>
	    		<a href="javascript:void(0)" class="">其他</a> 
	    		
	        </li>
	        <li>
	    		<a href="javascript:void(0)">我的盘<span></span></a> 
	        </li>
	        <li>
	    		<a href="javascript:void(0)">我关注的盘<span></span></a>
	        </li>
	    </ul>
	    
 
	</div>
    <!--Tab栏结束 -->
    
    <div class="searchResult demo_layout">
    这里是搜索结果
    </div>
</div>
 	<div class="demo-box">
    	<div class="box-hd"><h2 class="xtitle">1.默认按钮</h2></div>
        <div class="box-bd">
            <a href="javascript:void(0)" class="mini-button importantBtn"><span class="mini-button-text">重要按钮</span></a>
            <a href="javascript:void(0)" id="aa" onclick="copyscript(aa)" class="mini-button"><span class="mini-button-text ">普通按钮</span></a>
            <a href="javascript:void(0)"  class="mini-button mini-button-disabled"><span class="mini-button-text ">新增-失效</span></a>
             <a href="javascript:void(0)" class="mini-button mini-button-h30 importantBtn"><span class="mini-button-text">高度为30大按钮</span></a>
             <a href="javascript:void(0)" class="mini-button mini-button-h30 "><span class="mini-button-text">高度为30大按钮</span></a>
             <a href="javascript:void(0)" class="mini-button mini-button-h30 mini-button-disabled"><span class="mini-button-text">高度为30大按钮</span></a>
             <div class="method code">
            	code:
                <pre><ol><li class="l1">&lt;a href=&quot;javascript:void(0)&quot;class=&quot;mini-button importantBtn &quot;&gt;&lt;span class=&quot;mini-button-text &quot;&gt;重要按钮&lt;/span&gt;&lt;/a&gt;</li><li class="l1">&lt;a href=&quot;javascript:void(0)&quot;class=&quot;mini-button &quot;&gt;&lt;span class=&quot;mini-button-text &quot;&gt;普通按钮&lt;/span&gt;&lt;/a&gt;</li><li class="l1">&lt;a href=&quot;javascript:void(0)&quot;class=&quot;mini-button mini-button-disabled &quot;&gt;&lt;span class=&quot;mini-button-text &quot;&gt;失效按钮&lt;/span&gt;&lt;/a&gt;</li></ol></pre>
            </div>
        </div>
    </div> 
   
<div class="demo-box">
    	<div class="box-hd">
    	    <h2 class="xtitle">2.按钮组</h2>
    	</div>
        <div class="box-bd">
   			<div class="btn_group">
            <a href="javascript:void(0)" id="aa" onclick="copyscript(aa)" class="mini-button"><span class="mini-button-text ">普通按钮</span></a>
            <a href="javascript:void(0)" id="aa" onclick="copyscript(aa)" class="mini-button"><span class="mini-button-text ">普通按钮</span></a>
            <a href="javascript:void(0)" id="aa" onclick="copyscript(aa)" class="mini-button"><span class="mini-button-text ">普通按钮</span></a>			            </div>
            
            <div class="btn_group">
            <a href="javascript:void(0)" id="aa" onclick="copyscript(aa)" class="mini-button"><span class="mini-button-text ">按钮1</span></a>
            <a href="javascript:void(0)" id="aa" onclick="copyscript(aa)" class="mini-button"><span class="mini-button-text ">按钮2</span></a>
            <a href="javascript:void(0)" id="aa" onclick="copyscript(aa)" class="mini-button"><span class="mini-button-text ">按钮3</span></a>			            </div>
            
            <div class="method code">
            	code:
                <pre><ol><li class="l1">&lt;div class=&quot;btn_group&quot;&gt;</li><li class="l2">&lt;a href=&quot;javascript:void(0)&quot; class=&quot;mini-button&quot;&gt;&lt;span class=&quot;mini-button-text &quot;&gt;按钮1&lt;/span&gt;&lt;/a&gt;</li><li  class="l2">&lt;a href=&quot;javascript:void(0)&quot; class=&quot;mini-button&quot;&gt;&lt;span class=&quot;mini-button-text &quot;&gt;按钮2&lt;/span&gt;&lt;/a&gt;</li><li  class="l2">&lt;a href=&quot;javascript:void(0)&quot; class=&quot;mini-button&quot;&gt;&lt;span class=&quot;mini-button-text &quot;&gt;按钮3&lt;/span&gt;&lt;/a&gt;</li><li  class="l1">&lt;/div&gt;</li></ol></pre>
            </div>
            
	</div>
</div>  
    
    <div class="demo-box">
    	<div class="box-hd">
    	    <h2 class="xtitle">3.下拉按钮</h2>
    	</div>
        <div class="box-bd">
   			<div class="mini-button-menu" id="btnMenu">
                <a href="javascript:void(0)" id="aa" onclick="copyscript(aa)" class="mini-button "><span class="mini-button-text ">普通按钮</span><i class='ctr drop'></i></a>
                <div class="dropMenu">
                    <ul>
                        <li><a href="javascript:void(0)">普通菜单</a></li>
                        <li class="current">
                            <a href="javascript:void(0)">选中菜单</a>
                        </li>
                        <li><a href="javascript:void(0)">普通菜单</a></li>
                        <li><a href="javascript:void(0)">普通菜单</a></li>
                    </ul>
                </div>			            
            </div>
            
            <div class="method code">
            	code:
                <pre><ol><li class="l1">&lt;div class=&quot;mini-button-menu&quot;&gt;</li><li class="l2">  &lt;a href=&quot;javascript:void(0)&quot;class=&quot;mini-button &quot;&gt;&lt;span class=&quot;mini-button-text &quot;&gt;普通按钮&lt;/span&gt;&lt;i class=&quot;ctr drop&quot;&gt;&lt;/i&gt;&lt;/a&gt;</li><li  class="l2">  &lt;div class=&quot;dropMenu&quot;&gt;</li><li  class="l3">    &lt;ul&gt;</li><li class="l4">      &lt;li&gt;&lt;a href=&quot;javascript:void(0)&quot;&gt;普通菜单&lt;/a&gt;&lt;/li&gt;</li><li class="l4">      &lt;li class=&quot;current&quot;&gt;&lt;a href=&quot;javascript:void(0)&quot;&gt;选中菜单&lt;/a&gt;&lt;/li&gt;</li><li class="l4">      &lt;li&gt;&lt;a href=&quot;javascript:void(0)&quot;&gt;普通菜单&lt;/a&gt;&lt;/li&gt;</li><li class="l4">      &lt;li&gt;&lt;a href=&quot;javascript:void(0)&quot;&gt;普通菜单&lt;/a&gt;&lt;/li&gt;</li><li  class="l3">    &lt;/ul&gt;</li><li class="l2">&lt;/div&gt; </li><li class="l1">&lt;/div&gt;
</li></ol></pre>
            </div>
   		</div>
    </div>   
   
   <br />

   
   <div class="demo-box">
    	<div class="box-hd">
    	    <h2 class="xtitle">4.图标按钮集</h2>
    	</div>
        <div class="box-bd" id="btnCollective">
        	<div class="grayTips yellowTips">点击按钮可复制代码</div>
            
            <hr/>
            
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-add">新增</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-edit">修改</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-remove">删除</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-save">保存</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-copy">复制</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-cut">剪切</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-paste">粘贴</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-setting">设置</span></a><a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-download">下载</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-upload">上传</span></a>
            <span class="mini-button-split"></span>    
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-import">导入</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-export">导出</span></a>
            <span class="mini-button-split"></span>    
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-excel">导出excel</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-word">导出word</span></a>
            <span class="mini-button-split"></span>  
            
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-ok">应用</span></a>
             <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-cancel">取消</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-reload">重新载入/刷新</span></a>
        <hr/>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-search">搜索</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-print">打印</span></a>	
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-help">帮助</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-undo">撒消</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-redo">重做</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-zoomin">缩小</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-zoomout">放大</span></a>
            <hr/>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-date">日期</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-filter">过滤</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-find">查找</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-folder">文件夹</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-folderopen">文件夹打开</span></a>
            <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-attach">附件</span></a>
            <hr/>
             <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-minusCrm">向上折叠</span></a>
             <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-plusCrm">向下展开</span></a>
             <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-expandH">向左展开</span></a>
             <a href="javascript:void(0)" class="mini-button"><span class="mini-button-text  mini-button-icon icon-collapseH">向右折叠</span></a>
             <hr/>
        <h4 style="margin:30px 0 10px 0">平面按钮</h4>    
            <a href="javascript:void(0)" class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-add">新增</span></a>
            <a href="javascript:void(0)" class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-edit">修改</span></a>
            <a href="javascript:void(0)" class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-remove">删除</span></a>
        

        <hr/>
            <h4 style="margin:30px 0 10px 0">链接按钮</h4>  
            <a href="javascript:void(0)" class="link-button icon-add">新增</a>
            <a href="javascript:void(0)" class="link-button icon-edit">修改</a>
            <a href="javascript:void(0)" class="link-button icon-remove">删除</a>
            <a href="javascript:void(0)" class="link-button icon-save">保存</a>
            <a href="javascript:void(0)" class="link-button icon-copy">复制</a>
            <a href="javascript:void(0)" class="link-button icon-cut">剪切</a>
            <a href="javascript:void(0)" class="link-button icon-paste">粘贴</a>
           
               
            <hr/>
            <a href="javascript:void(0)" class="link-button icon-import">导入</a>
            <a href="javascript:void(0)" class="link-button icon-export">导出</a>
            <a href="javascript:void(0)" class="link-button icon-excel">excel</a>
            <a href="javascript:void(0)" class="link-button icon-word">word</a>
            
            
            <span class="mini-button-split"></span>    
            <a href="javascript:void(0)" class="link-button icon-upload">上传</a>
            <a href="javascript:void(0)" class="link-button icon-download">下载</a>
            <span class="mini-button-split"></span>    
            <a href="javascript:void(0)" class="link-button icon-ok">应用</a>
            <a href="javascript:void(0)" class="link-button icon-cancel">取消</a>
            <a href="javascript:void(0)" class="link-button icon-reload">刷新/重载</a>
            <hr/>
            
             
           
            
            <a href="javascript:void(0)" class="link-button icon-print">打印</a>
            <a href="javascript:void(0)" class="link-button icon-help">帮助</a>
            
            <a href="javascript:void(0)" class="link-button icon-zoomout">放大</a>
            <a href="javascript:void(0)" class="link-button icon-zoomin">缩小</a>
            <a href="javascript:void(0)" class="link-button icon-find">查找</a> 
             <a href="javascript:void(0)" class="link-button icon-folderopen">文件夹打开</a> 
             <a href="javascript:void(0)" class="link-button icon-folder">文件夹</a> 
             <a href="javascript:void(0)" class="link-button icon-date">日期</a> 
             <a href="javascript:void(0)" class="link-button icon-filter">过滤</a> 
              <a href="javascript:void(0)" class="link-button icon-attach">附件</a> 
              <hr/> 
              <a href="javascript:void(0)" class="link-button icon-minusCrm">折叠</a>  
             <a href="javascript:void(0)" class="link-button icon-plusCrm">展开</a> 
             <a href="javascript:void(0)" class="link-button icon-collapseH">向左折叠</a>
             <a href="javascript:void(0)" class="link-button icon-expandH">向右折叠</a>
            <hr/>
            
            <h4 style="margin:30px 0 10px 0">常用功能图标</h4>    
            <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-add">新增</span></a>
            <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-edit">修改</span></a>
            <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-remove">删除</span></a>
            <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-print">打印</span></a>
            <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-undo">撤消</span></a>
            <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-zoomin">放大</span></a>
            <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-zoomin">放大</span></a>
            <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-zoomout">缩小</span></a>
            <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-wait">loading</span></a>
            <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-upload">上传</span></a>
            <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-addfolder">新增文件夹</span></a>
            <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-save">保存</span></a>
            <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-cut">剪切</span></a>
            <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-ok">ok</span></a>
            <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-no">错误</span></a>
            <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-excel">导出excel</span></a>
            <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-word">导出word</span></a>
             <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-upload-a">上传</span></a>
             <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-album">相册</span></a>
             <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-plusCrm">展开</span></a>
             <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-minusCrm">收缩</span></a>
             <a class="mini-button mini-button-plain"><span class="mini-button-text  mini-button-icon icon-sync">同步</span></a>
        
        </div>
   </div>
   <br/><br/><br/>  
