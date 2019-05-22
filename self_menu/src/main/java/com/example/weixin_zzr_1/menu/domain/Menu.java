package com.example.weixin_zzr_1.menu.domain;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

// 这里就是一个个的按钮
@Entity
@Table(name = "wx_self_menu_button")
public class Menu {

	@Id // 表示一个主键
	@GenericGenerator(name = "uuid2", strategy = "uuid2") // 定义Hibernate的主键生成器
	@GeneratedValue(generator = "uuid2") // 使用名为uuid2的主键生成器
	@Column(length = 36) // 指定列的长度
	private String id;
	// 这个属性，最终要被转换为发送给微信公众号的sub_button属性
	@OneToMany(cascade = CascadeType.ALL) // 一个自定义菜单里面有多个按钮
	@JoinColumn(name = "parent_id")
	private List<Menu> subMenus = new LinkedList<>();

	/** 菜单的响应动作类型，view表示网页类型，click表示点击类型，miniprogram表示小程序类型 */
	private String type;
	/** 菜单标题，不超过16个字节，子菜单不超过60个字节 */
	private String name;
	/** 菜单KEY值，用于消息接口推送，不超过128字节 */
	// key是一个关键字，必须要使用反单引号括起来
	@Column(name = "`key`")
	private String key;
	/** 网页 链接，用户点击菜单可打开链接，不超过1024字节。 type为miniprogram时，不支持小程序的老版本客户端将打开本url。 */
	private String url;
	/** 调用新增永久素材接口返回的合法media_id */
	private String mediaId;
	/** 小程序的appid（仅认证公众号可配置） */
	private String appId;
	/** 小程序的页面路径 */
	private String pagePath;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Menu> getSubMenus() {
		return subMenus;
	}

	public void setSubMenus(List<Menu> subMenus) {
		this.subMenus = subMenus;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getPagePath() {
		return pagePath;
	}

	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}

}