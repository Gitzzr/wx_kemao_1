package com.example.weixin_zzr_1.menu.service;

import com.example.weixin_zzr_1.menu.domain.SelfMenu;

public interface SelfMenuService {

	SelfMenu getMenu();

	void saveMenu(SelfMenu selfMenu);

}
