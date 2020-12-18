<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>角色添加</title>
<%@ include file="/static/base/common.jspf"%>
<script type="text/javascript" src="${ctx}/static/js/hp_form.js"></script>
</head>
<body>
	<div class="body_main">
		<form class="layui-form layui-form-pane" action="${ctx}/role/update">
			<input type="hidden" value="${role.id}" name="id">
			<div class="layui-form-item">
				<label class="layui-form-label">角色名</label>
				<div class="layui-input-block">
					<input type="text" name="role" autocomplete="off" value="${role.role}"
						placeholder="请输入角色名" class="layui-input">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">备注</label>
				<div class="layui-input-block">
					<input type="text" name="remark" autocomplete="off" value="${role.remark}"
						placeholder="请输入备注" class="layui-input">
				</div>
			</div>
			<div class="layui-form-item">
				<div class="layui-input-block">
					<button class="layui-btn" lay-submit="" lay-filter="demo1">立即提交</button>
					<button type="reset" class="layui-btn layui-btn-primary">重置</button>
				</div>
			</div>
		</form>
	</div>
	<script type="text/javascript">
		layui.use('form', function() {
			var form = layui.form;

			//通用弹出层表单提交方法
			form.on('submit(demo1)', function(data){
				console.log(data.field);

				$.post($('form').attr("action"),data.field, function (e){
					// var data = JSON.parse(e);
					// let e.result;
					if (e.result==true) {
						parent.closeLayer(e.msg);
					}else {
						layer.msg('操作失败：' + e.msg, {icon: 2, time: 2000});
					}
				})
				return false;
			})
		});
	</script>
</body>
</html>
