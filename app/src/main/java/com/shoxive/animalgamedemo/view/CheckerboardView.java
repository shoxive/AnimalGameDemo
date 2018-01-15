package com.shoxive.animalgamedemo.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.shoxive.animalgamedemo.ChessBoardBridge;
import com.shoxive.animalgamedemo.ChessBridge;
import com.shoxive.animalgamedemo.entity.CheckAction;
import com.shoxive.animalgamedemo.entity.Chess;
import com.shoxive.animalgamedemo.entity.IConfig;
import com.shoxive.animalgamedemo.util.ScreenUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 棋盘
 * Created by shoxive on 18/1/10.
 */

public class CheckerboardView extends RelativeLayout implements ChessBridge {
    private final int ORDER = 4;//棋盘大小为4*4
    private List<Chess> mChessList;//棋子类集合
    private List<ChessView> mChessViewList;
    private List<LayoutParams> mParamsList;
    private int mMargin = 24;//棋子间距
    private Context mContext;
    private int mChessWidth;
    private int mChessHeight;
    private boolean isBlueTurns = true;

    public boolean isBlueTurns() {
        return isBlueTurns;
    }

    private CheckAction mActionState = CheckAction.UNFOCUS;
    private int mLaseSelectPosition = -1;
    private ChessBoardBridge mChessBoardBridge;

    public void addChessBoardBridge(ChessBoardBridge chessBoardBridge) {
        mChessBoardBridge = chessBoardBridge;
    }

    public CheckerboardView(Context context) {
        super(context);
        this.mContext = context;
    }

    public CheckerboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mMargin = IConfig.CHESS_MARGIN;
        this.setClipChildren(false);
        this.setClipToPadding(false);
        initChess();
    }

    private void initChess() {
        int width = Resources.getSystem().getDisplayMetrics().widthPixels - ScreenUtil.dip2px(mContext, 24);
        mChessList = new ArrayList<>();
        for (int i = 0; i < (ORDER * ORDER); i++) {
            Chess chess = new Chess();
            chess.setType(i % ((ORDER * ORDER) / 2));
            chess.setOwnership(i / ((ORDER * ORDER) / 2));
            mChessList.add(chess);
        }
        Collections.shuffle(mChessList);
        for (int i = 0; i < (ORDER * ORDER); i++) {
            mChessList.get(i).setPosition(i);
        }
        mChessViewList = new ArrayList<>();
        mChessWidth = (width - (mMargin * (ORDER + 1))) / ORDER;
        mChessHeight = (((int) (width * 1.1333)) - (mMargin * (ORDER + 1))) / ORDER;
        for (int i = 0; i < mChessList.size(); i++) {
            ChessView chessView = new ChessView(getContext());
            chessView.setId(i + 1);
            chessView.initChess(mChessList.get(i), this, this);
            mChessViewList.add(chessView);
        }
    }

    private boolean mFirstInit;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!mFirstInit) {//只分配一次
            RelativeLayout.LayoutParams params = (LayoutParams) this.getLayoutParams();
            params.height = (int) (getMeasuredWidth() * 1.1333);

            for (int i = 0; i < mChessViewList.size(); i++) {
                RelativeLayout.LayoutParams layoutParams = new LayoutParams(mChessWidth, mChessHeight);
                //第一列
                if (i % ORDER == 0) {
                    layoutParams.leftMargin = mMargin;
                    layoutParams.rightMargin = mMargin;
                }
                //不是第一列
                if (i % ORDER != 0) {
                    layoutParams.rightMargin = mMargin;
                    layoutParams.addRule(RelativeLayout.RIGHT_OF, mChessViewList.get(i - 1).getId());
                }
                layoutParams.topMargin = mMargin;
                // 如果不是第一行，//设置纵向边距，非最后一行
                if ((i + 1) > ORDER) {
                    layoutParams.addRule(RelativeLayout.BELOW,
                            mChessViewList.get(i - ORDER).getId());
                }
                //最后一行
                if ((i + 1) > ((ORDER - 1) * ORDER)) {
                    layoutParams.bottomMargin = mMargin;
                }
                addView(mChessViewList.get(i), layoutParams);
            }
        }
        mFirstInit = true;
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }

    private void changeTurns() {
        isBlueTurns = !isBlueTurns;
        if (mChessBoardBridge != null) {
            mChessBoardBridge.updateTurns(isBlueTurns);
        }
        for (ChessView chessView : mChessViewList) {
            chessView.updateChessStatus();
        }
        mActionState = CheckAction.UNFOCUS;
        mLaseSelectPosition = -1;
    }

    @Override
    public void clickPoint(int position) {
        //判定是否执行点击事件
        //首先翻开棋子的动作不需要判定
        if (mChessList.get(position).getStatus() != 0) {
            if (mActionState == CheckAction.SELECTED && mLaseSelectPosition != -1) {//已有选中棋子，进行下一步动作
                //处理选择相邻棋子事件
                if ((position - mLaseSelectPosition == 1) || (position - mLaseSelectPosition == -1) || (position - mLaseSelectPosition == 4) || (position - mLaseSelectPosition == -4)) {
                    if ((position / ORDER != mLaseSelectPosition / ORDER) && ((position - mLaseSelectPosition == 1) || (position - mLaseSelectPosition == -1))) {
                        if ((mChessList.get(position).getOwnership() == 0 && !isBlueTurns) || (mChessList.get(position).getOwnership() == 1 && isBlueTurns)) {
                            return;
                        }
                    }
                } else {//非相邻棋子，判定是不是自己回合的，不是自己回合，过滤不处理
                    if ((mChessList.get(position).getOwnership() == 0 && !isBlueTurns) || (mChessList.get(position).getOwnership() == 1 && isBlueTurns)) {
                        return;
                    }
                }
            } else {//无选中棋子，第一次动作
                if ((mChessList.get(position).getOwnership() == 0 && !isBlueTurns) || (mChessList.get(position).getOwnership() == 1 && isBlueTurns)) {
                    //蓝色方回合首选红色方棋子或红色方回合首选蓝色棋子，直接过滤
                    return;
                }
            }
        }


        if (mChessList.get(position).getStatus() == 0) {//隐藏
            mChessViewList.get(position).openChess();
            changeTurns();
            for (int i = 0; i < mChessList.size(); i++) {
                if (mChessList.get(i).isSelect()) {
                    mChessList.get(i).setSelect(false);
                    mChessViewList.get(i).updateChessStatus();
                }
                if (mChessList.get(i).isLastHand() && i != position) {//标记上一手
                    mChessList.get(i).setLastHand(false);
                    mChessViewList.get(i).updateChessStatus();
                }
            }
            //触发翻牌动作 棋盘状态归位
            mActionState = CheckAction.UNFOCUS;
        } else if (mChessList.get(position).getStatus() == 1) {//翻开状态
            if (mChessList.get(position).isSelect()) {//棋子是选中状态
                mChessViewList.get(position).setSelectStatus(false);
                mActionState = CheckAction.UNFOCUS;
            } else {
                if (mActionState == CheckAction.SELECTED && mLaseSelectPosition != -1) {//已经选中一个棋子的情况下
                    if ((position - mLaseSelectPosition == 1) || (position - mLaseSelectPosition == -1) || (position - mLaseSelectPosition == 4) || (position - mLaseSelectPosition == -4)) {
                        //先处理非同一行的逻辑(行首和另一行行尾)
                        if ((position / ORDER != mLaseSelectPosition / ORDER) && ((position - mLaseSelectPosition == 1) || (position - mLaseSelectPosition == -1))) {
                            changeChessSelectState(position);
                        } else {
                            //两次选中自己的棋子，仅需切换选中状态
                            if ((mChessList.get(position).getOwnership() == mChessList.get(mLaseSelectPosition).getOwnership()) && (mChessList.get(position).getStatus() != 2)) {
                                changeChessSelectState(position);
                            } else {
                                //移动动作交给棋子自身处理
                                mChessViewList.get(mLaseSelectPosition).startAction(mChessList.get(position));
                            }
                        }
                    } else {
                        changeChessSelectState(position);
                    }
                } else {//没有棋子被选中，进行选中操作
                    changeChessSelectState(position);
                }
            }
        } else {//点击的是无棋子格
            if (mActionState == CheckAction.SELECTED && mLaseSelectPosition != -1) {//已经选中一个棋子的情况下
                if ((position - mLaseSelectPosition == 1) || (position - mLaseSelectPosition == -1) || (position - mLaseSelectPosition == 4) || (position - mLaseSelectPosition == -4)) {
                    if ((position / ORDER != mLaseSelectPosition / ORDER) && ((position - mLaseSelectPosition == 1) || (position - mLaseSelectPosition == -1))) {
                        //跨越边界的情况不做处理，保持状态不变
                    } else {
                        mChessViewList.get(mLaseSelectPosition).startAction(mChessList.get(position));
                    }
                }
            }
        }
    }

    @Override
    public void moveActionEnd(int startPosition, int endPosition, boolean needResetSelf) {
        //棋子移动结束，执行交换逻辑

        if (needResetSelf) {
            mChessList.get(endPosition).setLastHand(true);
            for (int i = 0; i < mChessList.size(); i++) {
                if (mChessList.get(i).isLastHand() && i != endPosition) {//标记上一手
                    mChessList.get(i).setLastHand(false);
                    mChessViewList.get(i).updateChessStatus();
                }
            }
            mChessViewList.get(endPosition).bringToFront();
            mChessViewList.get(startPosition).updateChessStatus();
            mChessViewList.get(endPosition).updateChessStatus();
            mChessViewList.get(startPosition).updateStatusWithMoveAction(endPosition, startPosition);//主动方棋子自杀，执行完自杀动画后，需要归位到之前的棋格(无需交换位置)
        } else {
            mChessList.get(startPosition).setLastHand(true);
            for (int i = 0; i < mChessList.size(); i++) {
                if (mChessList.get(i).isLastHand() && i != startPosition) {//标记上一手
                    mChessList.get(i).setLastHand(false);
                    mChessViewList.get(i).updateChessStatus();
                }
            }
            mChessViewList.get(startPosition).updateChessStatus();
            mChessViewList.get(endPosition).updateChessStatus();
            //交换
            mChessViewList.get(endPosition).updateStatusWithMoveAction(endPosition, startPosition);//从动棋子需要执行动画更换位置
            Collections.swap(mChessList, startPosition, endPosition);
            Collections.swap(mChessViewList, startPosition, endPosition);
        }
        changeTurns();
    }

    private void changeChessSelectState(int position) {
        if (position % ORDER == 0) {//第一列棋子
            if (position == 0) {//第一行第一列
                /**
                 * 第一个棋子与右边棋子的判定
                 */
                if (mChessList.get(position + 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setRightType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position + 1).getStatus() == 1) {//右边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position + 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position + 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position + 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setRightType(2);
                            } else {
                                mChessList.get(position).setRightType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position + 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setRightType(1);
                            } else {
                                mChessList.get(position).setRightType(2);
                            }
                        }
                    }
                }
                /**
                 * 第一个棋子与下方棋子的判定
                 */
                if (mChessList.get(position + 4).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setDownType(1);//标记下边箭头绿色可见
                } else if (mChessList.get(position + 4).getStatus() == 1) {//下边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position + 4).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position + 4).getType()) {//棋子比下方大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position + 4).getType() == 0) {//上边是象，下边是鼠
                                mChessList.get(position).setDownType(2);
                            } else {
                                mChessList.get(position).setDownType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position + 4).getType() == 7) {//上边是鼠,下边是象
                                mChessList.get(position).setDownType(1);
                            } else {
                                mChessList.get(position).setDownType(2);
                            }
                        }
                    }
                }
            } else if (position == 12) {//最后一行第一列
                if (mChessList.get(position + 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setRightType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position + 1).getStatus() == 1) {//右边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position + 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position + 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position + 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setRightType(2);
                            } else {
                                mChessList.get(position).setRightType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position + 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setRightType(1);
                            } else {
                                mChessList.get(position).setRightType(2);
                            }
                        }
                    }
                }
                if (mChessList.get(position - 4).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setTopType(1);//标记下边箭头绿色可见
                } else if (mChessList.get(position - 4).getStatus() == 1) {//下边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position - 4).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position - 4).getType()) {//棋子比下方大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position - 4).getType() == 0) {//上边是象，下边是鼠
                                mChessList.get(position).setTopType(2);
                            } else {
                                mChessList.get(position).setTopType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position - 4).getType() == 7) {//上边是鼠,下边是象
                                mChessList.get(position).setTopType(1);
                            } else {
                                mChessList.get(position).setTopType(2);
                            }
                        }
                    }
                }
            } else {
                if (mChessList.get(position + 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setRightType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position + 1).getStatus() == 1) {//右边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position + 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position + 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position + 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setRightType(2);
                            } else {
                                mChessList.get(position).setRightType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position + 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setRightType(1);
                            } else {
                                mChessList.get(position).setRightType(2);
                            }
                        }
                    }
                }
                if (mChessList.get(position - 4).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setTopType(1);//标记下边箭头绿色可见
                } else if (mChessList.get(position - 4).getStatus() == 1) {//下边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position - 4).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position - 4).getType()) {//棋子比下方大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position - 4).getType() == 0) {//上边是象，下边是鼠
                                mChessList.get(position).setTopType(2);
                            } else {
                                mChessList.get(position).setTopType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position - 4).getType() == 7) {//上边是鼠,下边是象
                                mChessList.get(position).setTopType(1);
                            } else {
                                mChessList.get(position).setTopType(2);
                            }
                        }
                    }
                }
                if (mChessList.get(position + 4).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setDownType(1);//标记下边箭头绿色可见
                } else if (mChessList.get(position + 4).getStatus() == 1) {//下边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position + 4).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position + 4).getType()) {//棋子比下方大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position + 4).getType() == 0) {//上边是象，下边是鼠
                                mChessList.get(position).setDownType(2);
                            } else {
                                mChessList.get(position).setDownType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position + 4).getType() == 7) {//上边是鼠,下边是象
                                mChessList.get(position).setDownType(1);
                            } else {
                                mChessList.get(position).setDownType(2);
                            }
                        }
                    }
                }
            }
        } else if (position % ORDER == 1) {//第二列棋子
            if (position == 1) {//第一行第二列
                /**
                 * 第一行棋子与左边棋子的判定
                 */
                if (mChessList.get(position - 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setLeftType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position - 1).getStatus() == 1) {//右边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position - 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position - 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position - 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setLeftType(2);
                            } else {
                                mChessList.get(position).setLeftType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position - 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setLeftType(1);
                            } else {
                                mChessList.get(position).setLeftType(2);
                            }
                        }
                    }
                }
                /**
                 * 第一个棋子与右边棋子的判定
                 */
                if (mChessList.get(position + 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setRightType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position + 1).getStatus() == 1) {//右边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position + 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position + 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position + 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setRightType(2);
                            } else {
                                mChessList.get(position).setRightType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position + 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setRightType(1);
                            } else {
                                mChessList.get(position).setRightType(2);
                            }
                        }
                    }
                }
                /**
                 * 第一个棋子与下方棋子的判定
                 */
                if (mChessList.get(position + 4).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setDownType(1);//标记下边箭头绿色可见
                } else if (mChessList.get(position + 4).getStatus() == 1) {//下边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position + 4).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position + 4).getType()) {//棋子比下方大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position + 4).getType() == 0) {//上边是象，下边是鼠
                                mChessList.get(position).setDownType(2);
                            } else {
                                mChessList.get(position).setDownType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position + 4).getType() == 7) {//上边是鼠,下边是象
                                mChessList.get(position).setDownType(1);
                            } else {
                                mChessList.get(position).setDownType(2);
                            }
                        }
                    }
                }
            } else if (position == 13) {//最后一行第一列
                if (mChessList.get(position - 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setLeftType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position - 1).getStatus() == 1) {//右边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position - 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position - 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position - 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setLeftType(2);
                            } else {
                                mChessList.get(position).setLeftType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position - 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setLeftType(1);
                            } else {
                                mChessList.get(position).setLeftType(2);
                            }
                        }
                    }
                }
                if (mChessList.get(position + 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setRightType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position + 1).getStatus() == 1) {//右边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position + 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position + 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position + 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setRightType(2);
                            } else {
                                mChessList.get(position).setRightType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position + 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setRightType(1);
                            } else {
                                mChessList.get(position).setRightType(2);
                            }
                        }
                    }
                }
                if (mChessList.get(position - 4).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setTopType(1);//标记下边箭头绿色可见
                } else if (mChessList.get(position - 4).getStatus() == 1) {//下边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position - 4).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position - 4).getType()) {//棋子比下方大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position - 4).getType() == 0) {//上边是象，下边是鼠
                                mChessList.get(position).setTopType(2);
                            } else {
                                mChessList.get(position).setTopType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position - 4).getType() == 7) {//上边是鼠,下边是象
                                mChessList.get(position).setTopType(1);
                            } else {
                                mChessList.get(position).setTopType(2);
                            }
                        }
                    }
                }
            } else {//中间两个棋子
                if (mChessList.get(position - 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setLeftType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position - 1).getStatus() == 1) {//右边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position - 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position - 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position - 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setLeftType(2);
                            } else {
                                mChessList.get(position).setLeftType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position - 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setLeftType(1);
                            } else {
                                mChessList.get(position).setLeftType(2);
                            }
                        }
                    }
                }
                if (mChessList.get(position + 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setRightType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position + 1).getStatus() == 1) {//右边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position + 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position + 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position + 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setRightType(2);
                            } else {
                                mChessList.get(position).setRightType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position + 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setRightType(1);
                            } else {
                                mChessList.get(position).setRightType(2);
                            }
                        }
                    }
                }
                if (mChessList.get(position - 4).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setTopType(1);//标记下边箭头绿色可见
                } else if (mChessList.get(position - 4).getStatus() == 1) {//下边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position - 4).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position - 4).getType()) {//棋子比下方大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position - 4).getType() == 0) {//上边是象，下边是鼠
                                mChessList.get(position).setTopType(2);
                            } else {
                                mChessList.get(position).setTopType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position - 4).getType() == 7) {//上边是鼠,下边是象
                                mChessList.get(position).setTopType(1);
                            } else {
                                mChessList.get(position).setTopType(2);
                            }
                        }
                    }
                }
                if (mChessList.get(position + 4).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setDownType(1);//标记下边箭头绿色可见
                } else if (mChessList.get(position + 4).getStatus() == 1) {//下边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position + 4).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position + 4).getType()) {//棋子比下方大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position + 4).getType() == 0) {//上边是象，下边是鼠
                                mChessList.get(position).setDownType(2);
                            } else {
                                mChessList.get(position).setDownType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position + 4).getType() == 7) {//上边是鼠,下边是象
                                mChessList.get(position).setDownType(1);
                            } else {
                                mChessList.get(position).setDownType(2);
                            }
                        }
                    }
                }
            }
        } else if (position % ORDER == 2) {//第三列棋子
            if (position == 2) {//第一行第二列
                /**
                 * 第一行棋子与左边棋子的判定
                 */
                if (mChessList.get(position - 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setLeftType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position - 1).getStatus() == 1) {//右边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position - 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position - 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position - 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setLeftType(2);
                            } else {
                                mChessList.get(position).setLeftType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position - 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setLeftType(1);
                            } else {
                                mChessList.get(position).setLeftType(2);
                            }
                        }
                    }
                }
                /**
                 * 第一个棋子与右边棋子的判定
                 */
                if (mChessList.get(position + 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setRightType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position + 1).getStatus() == 1) {//右边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position + 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position + 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position + 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setRightType(2);
                            } else {
                                mChessList.get(position).setRightType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position + 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setRightType(1);
                            } else {
                                mChessList.get(position).setRightType(2);
                            }
                        }
                    }
                }
                /**
                 * 第一个棋子与下方棋子的判定
                 */
                if (mChessList.get(position + 4).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setDownType(1);//标记下边箭头绿色可见
                } else if (mChessList.get(position + 4).getStatus() == 1) {//下边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position + 4).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position + 4).getType()) {//棋子比下方大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position + 4).getType() == 0) {//上边是象，下边是鼠
                                mChessList.get(position).setDownType(2);
                            } else {
                                mChessList.get(position).setDownType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position + 4).getType() == 7) {//上边是鼠,下边是象
                                mChessList.get(position).setDownType(1);
                            } else {
                                mChessList.get(position).setDownType(2);
                            }
                        }
                    }
                }
            } else if (position == 14) {//最后一行第一列
                if (mChessList.get(position - 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setLeftType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position - 1).getStatus() == 1) {//右边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position - 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position - 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position - 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setLeftType(2);
                            } else {
                                mChessList.get(position).setLeftType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position - 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setLeftType(1);
                            } else {
                                mChessList.get(position).setLeftType(2);
                            }
                        }
                    }
                }
                if (mChessList.get(position + 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setRightType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position + 1).getStatus() == 1) {//右边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position + 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position + 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position + 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setRightType(2);
                            } else {
                                mChessList.get(position).setRightType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position + 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setRightType(1);
                            } else {
                                mChessList.get(position).setRightType(2);
                            }
                        }
                    }
                }
                if (mChessList.get(position - 4).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setTopType(1);//标记下边箭头绿色可见
                } else if (mChessList.get(position - 4).getStatus() == 1) {//下边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position - 4).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position - 4).getType()) {//棋子比下方大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position - 4).getType() == 0) {//上边是象，下边是鼠
                                mChessList.get(position).setTopType(2);
                            } else {
                                mChessList.get(position).setTopType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position - 4).getType() == 7) {//上边是鼠,下边是象
                                mChessList.get(position).setTopType(1);
                            } else {
                                mChessList.get(position).setTopType(2);
                            }
                        }
                    }
                }
            } else {//中间两个棋子
                if (mChessList.get(position - 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setLeftType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position - 1).getStatus() == 1) {//右边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position - 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position - 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position - 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setLeftType(2);
                            } else {
                                mChessList.get(position).setLeftType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position - 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setLeftType(1);
                            } else {
                                mChessList.get(position).setLeftType(2);
                            }
                        }
                    }
                }
                if (mChessList.get(position + 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setRightType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position + 1).getStatus() == 1) {//右边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position + 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position + 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position + 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setRightType(2);
                            } else {
                                mChessList.get(position).setRightType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position + 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setRightType(1);
                            } else {
                                mChessList.get(position).setRightType(2);
                            }
                        }
                    }
                }
                if (mChessList.get(position - 4).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setTopType(1);//标记下边箭头绿色可见
                } else if (mChessList.get(position - 4).getStatus() == 1) {//下边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position - 4).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position - 4).getType()) {//棋子比下方大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position - 4).getType() == 0) {//上边是象，下边是鼠
                                mChessList.get(position).setTopType(2);
                            } else {
                                mChessList.get(position).setTopType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position - 4).getType() == 7) {//上边是鼠,下边是象
                                mChessList.get(position).setTopType(1);
                            } else {
                                mChessList.get(position).setTopType(2);
                            }
                        }
                    }
                }
                if (mChessList.get(position + 4).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setDownType(1);//标记下边箭头绿色可见
                } else if (mChessList.get(position + 4).getStatus() == 1) {//下边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position + 4).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position + 4).getType()) {//棋子比下方大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position + 4).getType() == 0) {//上边是象，下边是鼠
                                mChessList.get(position).setDownType(2);
                            } else {
                                mChessList.get(position).setDownType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position + 4).getType() == 7) {//上边是鼠,下边是象
                                mChessList.get(position).setDownType(1);
                            } else {
                                mChessList.get(position).setDownType(2);
                            }
                        }
                    }
                }
            }
        } else if (position % ORDER == 3) {//第四列棋子
            if (position == 3) {//第一行第二列
                /**
                 * 第一行棋子与左边棋子的判定
                 */
                if (mChessList.get(position - 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setLeftType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position - 1).getStatus() == 1) {//右边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position - 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position - 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position - 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setLeftType(2);
                            } else {
                                mChessList.get(position).setLeftType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position - 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setLeftType(1);
                            } else {
                                mChessList.get(position).setLeftType(2);
                            }
                        }
                    }
                }
                /**
                 * 第一个棋子与下方棋子的判定
                 */
                if (mChessList.get(position + 4).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setDownType(1);//标记下边箭头绿色可见
                } else if (mChessList.get(position + 4).getStatus() == 1) {//下边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position + 4).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position + 4).getType()) {//棋子比下方大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position + 4).getType() == 0) {//上边是象，下边是鼠
                                mChessList.get(position).setDownType(2);
                            } else {
                                mChessList.get(position).setDownType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position + 4).getType() == 7) {//上边是鼠,下边是象
                                mChessList.get(position).setDownType(1);
                            } else {
                                mChessList.get(position).setDownType(2);
                            }
                        }
                    }
                }
            } else if (position == 15) {//最后一行第一列
                if (mChessList.get(position - 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setLeftType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position - 1).getStatus() == 1) {//右边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position - 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position - 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position - 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setLeftType(2);
                            } else {
                                mChessList.get(position).setLeftType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position - 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setLeftType(1);
                            } else {
                                mChessList.get(position).setLeftType(2);
                            }
                        }
                    }
                }
                if (mChessList.get(position - 4).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setTopType(1);//标记下边箭头绿色可见
                } else if (mChessList.get(position - 4).getStatus() == 1) {//下边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position - 4).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position - 4).getType()) {//棋子比下方大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position - 4).getType() == 0) {//上边是象，下边是鼠
                                mChessList.get(position).setTopType(2);
                            } else {
                                mChessList.get(position).setTopType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position - 4).getType() == 7) {//上边是鼠,下边是象
                                mChessList.get(position).setTopType(1);
                            } else {
                                mChessList.get(position).setTopType(2);
                            }
                        }
                    }
                }
            } else {//中间两个棋子
                if (mChessList.get(position - 1).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setLeftType(1);//标记右边箭头绿色可见
                } else if (mChessList.get(position - 1).getStatus() == 1) {//左边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position - 1).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position - 1).getType()) {//左侧棋子比右侧大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position - 1).getType() == 0) {//左边是象，右边是鼠
                                mChessList.get(position).setLeftType(2);
                            } else {
                                mChessList.get(position).setLeftType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position - 1).getType() == 7) {//左边是鼠,右边是象
                                mChessList.get(position).setLeftType(1);
                            } else {
                                mChessList.get(position).setLeftType(2);
                            }
                        }
                    }
                }
                if (mChessList.get(position - 4).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setTopType(1);//标记下边箭头绿色可见
                } else if (mChessList.get(position - 4).getStatus() == 1) {//下边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position - 4).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position - 4).getType()) {//棋子比下方大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position - 4).getType() == 0) {//上边是象，下边是鼠
                                mChessList.get(position).setTopType(2);
                            } else {
                                mChessList.get(position).setTopType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position - 4).getType() == 7) {//上边是鼠,下边是象
                                mChessList.get(position).setTopType(1);
                            } else {
                                mChessList.get(position).setTopType(2);
                            }
                        }
                    }
                }
                if (mChessList.get(position + 4).getStatus() == 2) {//空格可以移动
                    mChessList.get(position).setDownType(1);//标记下边箭头绿色可见
                } else if (mChessList.get(position + 4).getStatus() == 1) {//下边是翻开的棋子
                    if (mChessList.get(position).getOwnership() != mChessList.get(position + 4).getOwnership()) {//棋子不属于同一方
                        if (mChessList.get(position).getType() > mChessList.get(position + 4).getType()) {//棋子比下方大
                            //首先进行象鼠逻辑判断
                            if (mChessList.get(position).getType() == 7 && mChessList.get(position + 4).getType() == 0) {//上边是象，下边是鼠
                                mChessList.get(position).setDownType(2);
                            } else {
                                mChessList.get(position).setDownType(1);
                            }
                        } else {//右侧比左侧大
                            //还是要进行象鼠特殊判定
                            if (mChessList.get(position).getType() == 0 && mChessList.get(position + 4).getType() == 7) {//上边是鼠,下边是象
                                mChessList.get(position).setDownType(1);
                            } else {
                                mChessList.get(position).setDownType(2);
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < mChessList.size(); i++) {
            if (mChessList.get(i).isSelect() && i != position) {
                mChessList.get(i).setSelect(false);
                mChessViewList.get(i).updateChessStatus();
            }
        }
        mActionState = CheckAction.SELECTED;
        mLaseSelectPosition = position;
        mChessViewList.get(position).setSelectStatus(true);
    }
}
