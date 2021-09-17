package com.laoxin.LXBot.module;

import com.laoxin.LXBot.bean.CqGoBean;

public interface ModuleInterface {


    /**
     * 执行判断
     * @return
     */
    boolean whetherRun(CqGoBean cqGoBean);

    /**
     * 功能实现
     * @param cqGo
     */
    void run(CqGoBean cqGo);
}
