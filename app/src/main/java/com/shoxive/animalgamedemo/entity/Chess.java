package com.shoxive.animalgamedemo.entity;

/**
 * 棋子对象
 * Created by shoxive on 18/1/10.
 */

public class Chess {
    private int status;//标识棋子状态 0.隐藏 1.翻开 2.此局游戏已被移除
    private int type;//标识棋子类型  0.鼠 1.猫 2.狗 3.狼 4.豹 5.虎 6.狮 7.象
    private int ownership;//标识棋子归属 0.自己 1.对手
    private int position;//标识棋子位置（初始位置-->每次动作后更新位置）
    private boolean isSelect;//标识棋子是否被选中
    //标识棋子上下左右状态  0.不可移动（即不展示箭头） 1.可以移动或可以击杀 2.可以移动但会自杀
    private int leftType;
    private int topType;
    private int rightType;
    private int downType;
    private boolean isLastHand;//是否是上一手

    public boolean isLastHand() {
        return isLastHand;
    }

    public void setLastHand(boolean lastHand) {
        isLastHand = lastHand;
    }

    public int getLeftType() {
        return leftType;
    }

    public void setLeftType(int leftType) {
        this.leftType = leftType;
    }

    public int getTopType() {
        return topType;
    }

    public void setTopType(int topType) {
        this.topType = topType;
    }

    public int getRightType() {
        return rightType;
    }

    public void setRightType(int rightType) {
        this.rightType = rightType;
    }

    public int getDownType() {
        return downType;
    }

    public void setDownType(int downType) {
        this.downType = downType;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOwnership() {
        return ownership;
    }

    public void setOwnership(int ownership) {
        this.ownership = ownership;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
