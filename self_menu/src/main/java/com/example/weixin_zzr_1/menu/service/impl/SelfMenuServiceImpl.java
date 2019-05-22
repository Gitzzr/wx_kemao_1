package com.example.weixin_zzr_1.menu.service.impl;

import java.util.List;

import com.example.commons.service.WeixinProxy;
import com.example.weixin_zzr_1.menu.domain.Menu;
import com.example.weixin_zzr_1.menu.domain.SelfMenu;
import com.example.weixin_zzr_1.menu.repository.SelfMenuRepository;
import com.example.weixin_zzr_1.menu.service.SelfMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class SelfMenuServiceImpl implements SelfMenuService {

	private static final Logger LOG = LoggerFactory.getLogger(SelfMenuServiceImpl.class);
	@Autowired
	private SelfMenuRepository menuRepository;

	@Autowired
	private WeixinProxy weixinProxy;

	@Override
	public SelfMenu getMenu() {
		List<SelfMenu> all = menuRepository.findAll();
		if (all.isEmpty()) {
			// 数据库如果没有数据，则返回一个新的空对象
			return new SelfMenu();
		}
		// 数据库有数据，则返回第一条数据
		return all.get(0);
	}

	@Override
	public void saveMenu(SelfMenu selfMenu) {
		// 1.如果有二级菜单，那么一级菜单只能有name属性，其他属性不要
		selfMenu.getSubMenus().forEach(b1 -> {
			if (!b1.getSubMenus().isEmpty()) {
				// 有下一级，只保留name属性，其他属性都清空
				b1.setAppId(null);
				b1.setKey(null);
				b1.setMediaId(null);
				b1.setPagePath(null);
				b1.setType(null);
				b1.setUrl(null);
			}
		});

		// 2.删除原本所有的自定义菜单，然后重新插入数据
		this.menuRepository.deleteAll();
		this.menuRepository.save(selfMenu);

		// 3.把数据转换为JSON，发送给微信公众号平台
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode buttonNode = mapper.createObjectNode();
		ArrayNode buttonArrayNode = mapper.createArrayNode();
		buttonNode.set("button", buttonArrayNode);

		selfMenu.getSubMenus().forEach(b1 -> {

			ObjectNode menu = mapper.createObjectNode();
			menu.put("name", b1.getName());
			buttonArrayNode.add(menu);// 加入到集合里面
			if (b1.getSubMenus().isEmpty()) {
				// 没有下一级，就需要把key、appid之类的属性加上
				setValues(menu, b1);
			} else {
				// 有下一级，就需要再创建ArrayNode
				ArrayNode subButtons = mapper.createArrayNode();
				menu.set("sub_button", subButtons);
				b1.getSubMenus().forEach(b2 -> {
					// 需要把key、appid之类的属性加上
					ObjectNode subMenu = mapper.createObjectNode();
					subMenu.put("name", b2.getName());
					subButtons.add(subMenu);
					setValues(subMenu, b2);
				});
			}
		});

		try {
			String json = mapper.writeValueAsString(buttonNode);

			// 调用远程接口，把JSON字符串推送到微信公众号平台
			this.weixinProxy.saveMenu(json);
		} catch (JsonProcessingException e) {
			LOG.error("保存菜单出现问题：" + e.getLocalizedMessage(), e);
		}
	}

	private void setValues(ObjectNode subMenu, Menu m) {
		if (!StringUtils.isEmpty(m.getAppId())) {
			subMenu.put("appid", m.getAppId());
		}

		// 有些可以有key，有些则不能有key
		if ((m.getType().equals("location_select")// 定位
				|| m.getType().equals("pic_weixin")// 微信相册
				|| m.getType().equals("pic_photo_or_album")// 微信相册或拍照
				|| m.getType().equals("pic_sysphoto")// 拍照
				|| m.getType().equals("scancode_push")// 扫码
				|| m.getType().equals("scancode_waitmsg")// 扫码
		) //
				&& !StringUtils.isEmpty(m.getKey())) {
			subMenu.put("key", m.getKey());
		} else {
			m.setKey(null);
		}
		if (!StringUtils.isEmpty(m.getPagePath())) {
			subMenu.put("pagepath", m.getPagePath());
		}
		if (!StringUtils.isEmpty(m.getMediaId())) {
			subMenu.put("media_id", m.getMediaId());
		}
		if (!StringUtils.isEmpty(m.getUrl())) {
			subMenu.put("url", m.getUrl());
		}
		if (!StringUtils.isEmpty(m.getType())) {
			subMenu.put("type", m.getType());
		}
	}
}