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

//这个类本身不是菜单,而是为了存储一个成员变量
@Entity
@Table(name = "wx_self_menu")
public class SelfMenu {

	@Id // 表示一个主键
	@GenericGenerator(name = "uuid2", strategy = "uuid2") // 定义Hibernate的主键生成器
	@GeneratedValue(generator = "uuid2") // 使用名为uuid2的主键生成器
	@Column(length = 36) // 指定列的长度
	private String id;
	// 这个属性，最终要被转换为发送给微信公众号的button属性
	@OneToMany(cascade = CascadeType.ALL) // 一个自定义菜单里面有多个按钮
	@JoinColumn(name = "menu_id")
	private List<Menu> subMenus = new LinkedList<>();

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
}
