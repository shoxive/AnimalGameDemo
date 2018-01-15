package com.shoxive.animalgamedemo.entity;

/**
 * 记录全局棋盘动作
 * Created by shoxive on 18/1/12.
 */

public enum CheckAction {
    UNFOCUS,//失去焦点，常规状态(初始状态，翻牌后状态 移动棋子状态)
    SELECTED,//选中状态
}
