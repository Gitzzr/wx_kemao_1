<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>菜单管理</title>
<link rel="stylesheet" href="/zzr_1/menu/css/menu.css"/>
</head>
<body>
<div class="main-container">
    <div class="wechat-container">
        <div class="content-container"></div>
        <div class="menus" id="menus">
        	<!-- 一级菜单 -->
        	<!-- v-for是使用Vue.js来循环生成HTML的特殊属性 -->
        	<!-- 所有v-开头、@开头、:开头的属性，都是Vue.js的 -->
            <div class="menu" v-for="m in subMenus">
                <div class="sub-menu" v-if="m.show">
                    <ul>
                    	<!-- 二级菜单的一个个按钮 -->
                        <li v-if="(m.subMenus && m.subMenus.length &lt 7) || (!m.subMenus)"
                            @click="addNewButton(m, $event)">+
                        </li>
                        <li v-for="m2 in m.subMenus"
                            @click="activeButton(m2, $event); selectMenu()"
                            v-bind:class="{ currentMenu : m2.active }">{{ m2.name }}
                        </li>
                    </ul>
                    <div class="down-arrow"></div>
                </div>
                <!-- 一级菜单的按钮 -->
                <div class="button"
                     v-on:click="toggleSubMenus(m)"
                     @click="activeButton(m, $event); selectMenu()"
                     v-bind:class="{ currentMenu : m.active }">{{ m.name }}
                </div>
            </div>
            <div class="menu" v-if="subMenus.length &lt; 3"
                 @click="hideLevelTwo(); addNewButton($data, $event);">
                <div class="button">+</div>
            </div>
        </div>
    </div>
    <div id="propertyEditor" class="property-container">
        <div>菜单属性编辑器</div>
        <div>
            <input type="hidden" v-model="selectedMenu.id"/>
            <div>
                名称 <input v-model="selectedMenu.name"/>
            </div>
            <div class="type" v-if="selectedMenu.name != null">
                类型
                <label>
                    <input name="type" v-model="selectedMenu.type" value="click" type="radio"/>
                    点击
                </label>
	            <div v-if="selectedMenu.type == 'click'">
	                指令内容<input v-model="selectedMenu.key"/>
	            </div>
                <label>
                    <input name="type" v-model="selectedMenu.type" value="view" type="radio"/>
                    跳转
                </label>
	            <div v-if="selectedMenu.type == 'view'">
	                网页地址<input v-model="selectedMenu.url"/>
	            </div>
                <label>
                    <input name="type" v-model="selectedMenu.type" value="scancode_push" type="radio"/>
                    扫码推送（iPhone 5.4或者Android 5.4.1以上）
                </label>
            	<div v-if="selectedMenu.type == 'scancode_push'">扫描二维码，服务器会收到扫描内容，并可下发信息</div>
            	<div v-if="selectedMenu.type == 'scancode_push'">
                    KEY <input v-model="selectedMenu.key"/>
            	</div>
                <label>
                    <input name="type" v-model="selectedMenu.type" value="scancode_waitmsg" type="radio"/>
                    扫码等待（iPhone 5.4或者Android 5.4.1以上）
                </label>
            	<div v-if="selectedMenu.type == 'scancode_waitmsg'">扫描二维码，并等待服务器下发信息</div>
            	<div v-if="selectedMenu.type == 'scancode_waitmsg'">
                    KEY <input v-model="selectedMenu.key"/>
            	</div>
                <label>
                    <input name="type" v-model="selectedMenu.type" value="pic_sysphoto" type="radio"/>
                    拍照（iPhone 5.4或者Android 5.4.1以上）
                </label>
            	<div v-if="selectedMenu.type == 'pic_sysphoto'">打开系统相机，把事件和图片发送给服务器</div>
            	<div v-if="selectedMenu.type == 'pic_sysphoto'">
                    KEY <input v-model="selectedMenu.key"/>
            	</div>
                <label>
                    <input name="type" v-model="selectedMenu.type" value="pic_photo_or_album" type="radio"/>
                    拍照或相册（iPhone 5.4或者Android 5.4.1以上）
                </label>
            	<div v-if="selectedMenu.type == 'pic_photo_or_album'">
                    KEY <input v-model="selectedMenu.key"/>
            	</div>
                <label>
                    <input name="type" v-model="selectedMenu.type" value="pic_weixin" type="radio"/>
                    相册（iPhone 5.4或者Android 5.4.1以上）
                </label>
            	<div v-if="selectedMenu.type == 'pic_weixin'">打开微信相册，把事件和图片发送给服务器</div>
            	<div v-if="selectedMenu.type == 'pic_weixin'">
                    KEY <input v-model="selectedMenu.key"/>
            	</div>
                <label>
                    <input name="type" v-model="selectedMenu.type" value="location_select" type="radio"/>
                    地理位置（iPhone 5.4或者Android 5.4.1以上）
                </label>
            	<div v-if="selectedMenu.type == 'location_select'">
            		选择地理位置并上报<br/>
                    KEY <input v-model="selectedMenu.key"/>
            	</div>
                <label>
                    <input name="type" v-model="selectedMenu.type" value="media_id" type="radio"/>
                    素材（资质验证未通过时使用）
                </label>
	            <div v-if="selectedMenu.type == 'media_id'">
	                选择永久素材的ID，可以是图片、音频、视频、图文素材。<br/>
	                图文消息的ID<input v-model="selectedMenu.mediaId"/>
	            </div>
                <label>
                    <input name="type" v-model="selectedMenu.type" value="view_limited" type="radio"/>
                    图文素材（资质验证未通过时使用）
                </label>
	            <div v-if="selectedMenu.type == 'view_limited'">
	                选择永久素材的ID，只能是图文素材。 
	                <input v-model="selectedMenu.mediaId"/>
	            </div>
            </div>
        </div>
    </div>
</div>
<div style="width: 800px;margin-left: auto;margin-right: auto; text-align: right;">
	<button onclick="saveMenus()">保存</button>
</div>

<script type="text/javascript" src="/zzr_1/menu/js/jquery.js"></script>
<script type="text/javascript" src="/zzr_1/menu/js/vue.js"></script>
<script type="text/javascript" src="/zzr_1/menu/js/menu.js"></script>

</body>
</html>