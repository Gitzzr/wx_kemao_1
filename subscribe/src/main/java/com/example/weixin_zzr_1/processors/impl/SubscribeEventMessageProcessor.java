package com.example.weixin_zzr_1.processors.impl;

import com.example.commons.domain.User;
import com.example.commons.domain.event.EventInMessage;
import com.example.commons.processors.EventMessageProcessor;
import com.example.commons.repository.UserRepository;
import com.example.weixin_zzr_1.service.WeixinProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("subscribeMessageProcessor")
public class SubscribeEventMessageProcessor implements EventMessageProcessor {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private WeixinProxy weixinProxy;
	
	private static final Logger LOG = LoggerFactory.getLogger(SubscribeEventMessageProcessor.class);
	@Override
	public void onMessage(EventInMessage msg) {
		LOG.warn("你看到我了没有");
		if (!msg.getEvent().equals("subscribe")) {
			// 非关注事件，不处理
			return;
		}
		// 发生操作的用户的OpenId
		String openId = msg.getFromUserName();
		// 1.检查用户是否已经关注
		User user = this.userRepository.findByOpenId(openId);
		// 2.如果用户还未关注，则调用远程接口获取用户信息
		if (user == null || user.getStatus() != User.Status.IS_SUBSCRIBE) {
			// 3.调用远程接口
			// TODO 根据ToUserName找到对应的微信号
			String account = "";
			User wxUser = weixinProxy.getUser(account, openId);
			if (wxUser == null) {
				return;
			}
			// 4.存储到数据库
			if (user != null) {
				// 原来关注过
				wxUser.setId(user.getId());
				wxUser.setSubTime(user.getSubTime());
				wxUser.setUnsubTime(null);
			}
			wxUser.setStatus(User.Status.IS_SUBSCRIBE);

			// 如果有id的值，会自动update；没有id的值会insert
			this.userRepository.save(wxUser);

			// 通过客服接口，发生一条信息给用户
			weixinProxy.sendText(account, openId, "欢迎关注我的公众号，回复帮助可获得人工智能菜单");
		}
	}
}
