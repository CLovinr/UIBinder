;(function(window, undefined) {

	var namespace = "jsbinderbuilder";

	/**
	 * 用于js绑定。
	 * @param {Object} selector 可为null，用于搜索的元素选择器。
	 * @param {Object} prefixClass 绑定的接口类的全名。
	 */
	window.jsBinderBridge = window.jsBinderBridge || function(selector, prefixClass) {

		var domObjs = $(selector ? selector : document.body).find("[id]");
		var ids = [];
		domObjs.each(function() {
			var id = $(this).attr("id");
			if (id) {
				ids.push({
					"id": id
				});
			}
		});
		var data = {
			"ids": ids,
			"prefixClass": prefixClass,
			"setCall": function(id, name, value) {

				var obj = $("#" + id);
				switch (name) {
					case "onclick":
						obj.on("click", function() {
							value();
						});
						break;

					case "ATTR_ENABLE":
						obj.attr("disable", !value);
						break;
					case "ATTR_FOCUS_REQUEST":
						obj.focus();
						break;
					case "ATTR_VISIBLE":
						obj.css("display", value ? "" : "none");
						break;
					case "ATTR_VALUE":
						obj.val(value);
						break;
					case "ATTR_VALUE_CHANGE_LISTENER":
						obj.on("change", function() {
							value({
								"value":$(this).val()
							});
						});
						break
				}
			},
			"getCall": function(cid, nameIds) {

				var returnObj = {
					"values": null
				};

				var as = [];
				returnObj.values = as;

				for (var i = 0; i < nameIds.length; i++) {
					var id = nameIds[i].id;
					var name = nameIds[i].name;
					var val = null;
					var obj = $("#" + id);
					switch (name) {

						case "ATTR_ENABLE":
							val = !obj.attr("disable");
							break;

						case "ATTR_VISIBLE":
							val = obj.css("display") != "none";
							break;
						case "ATTR_VALUE":
							val = obj.val();
							break;
						case "ATTR_BOUNDS":
							{
								var offset = obj.offset();
								val = [offset.left, offset.top, obj.width(), obj.height()];
							}
							break
					}
					as.push(val);

				}

				window[namespace].jsBinderGetter(cid, returnObj);
			}
		};
		window[namespace].jsBinder(data);
		window.onbeforeunload = function() {
			window[namespace].jsBinderRelease();
		};
	};

})(window);