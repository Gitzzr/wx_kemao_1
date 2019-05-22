// 模拟数据
//let menuData = {
//    "subMenus": [ //
//        {
//        	"id": null,
//            "name": "菜单",
//            "type": null,
//            "url": null,
//            "mediaId": null,
//            "key": null,
//            "show": false,
//            "active": false,
//            "subMenus": [//
//                {
//                	"id": null,
//                    "type": "view",
//                    "url": null,
//                    "mediaId": null,
//                    "key": null,
//                    "name": "搜索",
//                    "active": false,
//                    "url": "http://www.soso.com/"
//                }, {
//                	"id": null,
//                    "type": "miniprogram",
//                    "url": null,
//                    "mediaId": null,
//                    "key": null,
//                    "name": "wxa",
//                    "active": false,
//                    "url": "http://mp.weixin.qq.com",
//                    "appid": "wx286b93c14bbf93aa",
//                    "pagePath": "pages/lunar/index"
//                }, {
//                	"id": null,
//                    "type": "click",
//                    "url": null,
//                    "mediaId": null,
//                    "active": false,
//                    "name": "赞一下我们",
//                    "key": "V1001_GOOD"
//                }]
//        },//
//        {
//        	"id": null,
//            "name": "发图",
//            "type": null,
//            "url": null,
//            "mediaId": null,
//            "key": null,
//            "show": false,
//            "active": false,
//            "subMenus": [ //
//                {
//                	"id": null,
//                    "type": "pic_sysphoto",
//                    "url": null,
//                    "mediaId": null,
//                    "active": false,
//                    "name": "系统拍照发图",
//                    "key": "rselfmenu_1_0"
//                }, {
//                	"id": null,
//                    "type": "pic_photo_or_album",
//                    "url": null,
//                    "mediaId": null,
//                    "active": false,
//                    "name": "拍照或者相册发图",
//                    "key": "rselfmenu_1_1"
//                }, {
//                	"id": null,
//                    "type": "pic_weixin",
//                    "url": null,
//                    "mediaId": null,
//                    "active": false,
//                    "name": "微信相册发图",
//                    "key": "rselfmenu_1_2"
//                }]
//        }]
//};
//initMenus();

let menuData = {};

// 获取远程数据

$.ajax({
	url: "",
	method: "get",
	dataType: "json",//要求返回JSON
	success: function (responseData) {
		menuData = responseData;
		initMenus();
    },
    error: function (responseData) {
        console.error(responseData);
    }
});
function initMenus(){
	let menus = new Vue({
		// el: "#menus",
			el: ".main-container",
		    data: menuData,
		    methods: {
		        // 显示或隐藏二级菜单
		        toggleSubMenus(m) {
		            // 先把所有二级菜单隐藏
		            this.hideLevelTwo();
		            // 显示点击时的二级菜单
		            m.show = !m.show;
		        },
		        activeButton(m, event) {
		            // 取消所有按钮的激活状态
		            this.subMenus.forEach((x) => {
		                if(x != m)
		                {
		                    x.active = false;
		                }
		                if (x.subMenus) {
		                    x.subMenus.forEach((y) => {
		                        if(y != m
		                )
		                    {
		                        y.active = false;
		                    }
		                })
		                    ;
		                }
		        	});
		            // 把当前被点击的对象激活
		            m.active = true;
		        },
		        hideLevelTwo() {
		            this.subMenus.forEach((x) => {
		                x.show = false;
		        	});
		        },
		        addNewButton(menu, event) {
		        	let emptyMenu = {
		        		    "id": null,
		        		    "name": "菜单",
		        		    "type": null,
		        		    "url": null,
		        		    "mediaId": null,
		        		    "eventKey": null,
		        		    "show": false,
		        		    "active": false,
		        		    "subMenus": []
		        		};
		            menu.subMenus.push(emptyMenu);
		            this.activeButton(emptyMenu);
		        },
		        // 找到当前选中的菜单
		        current(){
		        	for( let i = 0; i < this.subMenus.length; i++ ){
		        		let x = this.subMenus[i];
		        		if(x.active === true){
		        			return x;
		        		}
		        		for( let j = 0; j < x.subMenus.length; j++ ){
		        			let y = x.subMenus[j];
		        			if(y.active === true){
		            			return y;
		            		}
		        		}
		        	}
		        	return {};
		        },
		    	selectMenu(){
		    	}
		    },
		    computed: {
		    	selectedMenu(){
		    		return this.current();
		    	}
		    }
		});
}

// 2.需要把修改后的信息，直接提交到服务器并保存到数据库
function saveMenus(){
	let json = JSON.stringify(menuData);
	$.ajax({
		url: "",
		method: "post",
		contentType: "application/json; charset=utf-8",
		data: json,
		dataType: "json",
		success: function (responseData) {
            console.log(responseData);
        },
        error: function (responseData) {
            console.error(responseData);
        }
	});
}



