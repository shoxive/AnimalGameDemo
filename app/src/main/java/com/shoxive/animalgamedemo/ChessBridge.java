package com.shoxive.animalgamedemo;

/**
 * Created by shoxive on 18/1/11.
 */

public interface ChessBridge {
    //选中单个棋子，通知棋盘调度其他棋子(棋盘需处理三种模式1.翻开棋子 2.未选中任何棋子选中棋子 3.已选中棋子，更换一个选中棋子 4.已选中棋子，PK其他棋子，5.已选中棋子，移动到无棋子处)
    void clickPoint(int position);
    //棋子受棋盘调度，开始执行相应动作，动作完成后通过此接口通知棋盘更新状态
    void moveActionEnd(int startPosition,int endPosition,boolean needResetSelf);
}
