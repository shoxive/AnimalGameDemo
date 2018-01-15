package com.shoxive.animalgamedemo.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shoxive.animalgamedemo.ChessBridge;
import com.shoxive.animalgamedemo.R;
import com.shoxive.animalgamedemo.entity.Chess;
import com.shoxive.animalgamedemo.entity.IConfig;
import com.shoxive.animalgamedemo.util.ScreenUtil;

/**
 * 棋子
 * Created by shoxive on 18/1/10.
 */

public class ChessView extends FrameLayout {
    private int mChessW;
    private int mChessH;
    private Chess mChess;
    private View mView;
    private ImageView mImgChess;
    private View arrow_left, arrow_top, arrow_right, arrow_down;
    private Context mContext;
    private ChessBridge mBridge;
    private int mTmpPosition;
    private int width;
    private ObjectAnimator mTrasnlateAnim;
    private float mLocationX = 0f;
    private float mLocationY = 0f;

    public Chess getChess() {
        return mChess;
    }

    public ChessView(Context context) {
        super(context);
        this.mContext = context;
        width = Resources.getSystem().getDisplayMetrics().widthPixels - ScreenUtil.dip2px(getContext(), 24);
        mChessW = (width - IConfig.CHESS_MARGIN * (IConfig.CHESS_ORDER + 1)) / IConfig.CHESS_ORDER;
        mChessH = (int) ((width * 1.1333 - IConfig.CHESS_MARGIN * (IConfig.CHESS_ORDER + 1)) / IConfig.CHESS_ORDER);
        mView = inflate(mContext, R.layout.layout_chess_view, null);
        mView.setLayoutParams(new LayoutParams(mChessW, mChessH));
        addView(mView);
        this.setClipChildren(false);
        initView();
    }

    public ChessView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initChess(Chess chess, ChessBridge bridge) {
        this.mChess = chess;
        this.mBridge = bridge;
        updateChessStatus();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void initView() {
        mImgChess = findViewById(R.id.img_chess);
        RelativeLayout.LayoutParams imgParams = (RelativeLayout.LayoutParams) mImgChess.getLayoutParams();
        imgParams.width = mChessW;
        imgParams.height = mChessH;
        imgParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImgChess.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChess != null && mBridge != null) {
                    mBridge.clickPoint(mChess.getPosition());
                }
            }
        });
        arrow_left = findViewById(R.id.arrow_left);
        arrow_top = findViewById(R.id.arrow_top);
        arrow_right = findViewById(R.id.arrow_right);
        arrow_down = findViewById(R.id.arrow_down);
    }

    /**
     * 根据棋子对象 展示相应的UI
     */
    public void updateChessStatus() {
        if (mChess == null) {
            return;
        }
        mTmpPosition = mChess.getPosition();
        switch (mChess.getStatus()) {
            case 0://棋子未翻开状态
                this.setVisibility(View.VISIBLE);
                mImgChess.setBackgroundResource(R.mipmap.chessman_back);
                break;
            case 1://棋子已翻开状态
                this.setVisibility(View.VISIBLE);
                switch (mChess.getOwnership()) {
                    case 0://自己的棋子
                        switch (mChess.getType()) {
                            case 0:
                                mImgChess.setBackgroundResource(R.mipmap.chessman_mouse_blue);
                                break;
                            case 1:
                                mImgChess.setBackgroundResource(R.mipmap.chessman_cat_blue);
                                break;
                            case 2:
                                mImgChess.setBackgroundResource(R.mipmap.chessman_dog_blue);
                                break;
                            case 3:
                                mImgChess.setBackgroundResource(R.mipmap.chessman_wolf_blue);
                                break;
                            case 4:
                                mImgChess.setBackgroundResource(R.mipmap.chessman_leopard_blue);
                                break;
                            case 5:
                                mImgChess.setBackgroundResource(R.mipmap.chessman_tiger_blue);
                                break;
                            case 6:
                                mImgChess.setBackgroundResource(R.mipmap.chessman_lion_blue);
                                break;
                            case 7:
                                mImgChess.setBackgroundResource(R.mipmap.chessman_elephant_blue);
                                break;
                        }
                        break;
                    case 1://对手的棋子
                        switch (mChess.getType()) {
                            case 0:
                                mImgChess.setBackgroundResource(R.mipmap.chessman_mouse_red);
                                break;
                            case 1:
                                mImgChess.setBackgroundResource(R.mipmap.chessman_cat_red);
                                break;
                            case 2:
                                mImgChess.setBackgroundResource(R.mipmap.chessman_dog_red);
                                break;
                            case 3:
                                mImgChess.setBackgroundResource(R.mipmap.chessman_wolf_red);
                                break;
                            case 4:
                                mImgChess.setBackgroundResource(R.mipmap.chessman_leopard_red);
                                break;
                            case 5:
                                mImgChess.setBackgroundResource(R.mipmap.chessman_tiger_red);
                                break;
                            case 6:
                                mImgChess.setBackgroundResource(R.mipmap.chessman_lion_red);
                                break;
                            case 7:
                                mImgChess.setBackgroundResource(R.mipmap.chessman_elephant_red);
                                break;
                        }
                        break;
                }
                break;
            case 2://这个棋子已被淘汰
                mImgChess.setBackgroundResource(0);
                break;
        }
//        if (mChess.getOwnership() == 0) {//自己的棋子才会有选中等操作
        if (mChess.isSelect()) {
            ViewCompat.animate(this).scaleX(1.15f).scaleY(1.15f).start();
            //0.不可移动（即不展示箭头） 1.可以移动或可以击杀 2.可以移动但会自杀
            this.bringToFront();
            switch (mChess.getLeftType()) {
                case 0:
                    arrow_left.setVisibility(View.GONE);
                    break;
                case 1:
                    arrow_left.setBackgroundResource(R.mipmap.arrow_green_left);
                    arrow_left.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    arrow_left.setBackgroundResource(R.mipmap.arrow_red_left);
                    arrow_left.setVisibility(View.VISIBLE);
                    break;
            }
            switch (mChess.getTopType()) {
                case 0:
                    arrow_top.setVisibility(View.GONE);
                    break;
                case 1:
                    arrow_top.setBackgroundResource(R.mipmap.arrow_green_up);
                    arrow_top.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    arrow_top.setBackgroundResource(R.mipmap.arrow_red_up);
                    arrow_top.setVisibility(View.VISIBLE);
                    break;
            }
            switch (mChess.getRightType()) {
                case 0:
                    arrow_right.setVisibility(View.GONE);
                    break;
                case 1:
                    arrow_right.setBackgroundResource(R.mipmap.arrow_green_right);
                    arrow_right.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    arrow_right.setBackgroundResource(R.mipmap.arrow_red_right);
                    arrow_right.setVisibility(View.VISIBLE);
                    break;
            }
            switch (mChess.getDownType()) {
                case 0:
                    arrow_down.setVisibility(View.GONE);
                    break;
                case 1:
                    arrow_down.setBackgroundResource(R.mipmap.arrow_green_down);
                    arrow_down.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    arrow_down.setBackgroundResource(R.mipmap.arrow_red_down);
                    arrow_down.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            ViewCompat.animate(this).scaleX(1.0f).scaleY(1.0f).start();
            arrow_left.setVisibility(View.GONE);
            arrow_top.setVisibility(View.GONE);
            arrow_right.setVisibility(View.GONE);
            arrow_down.setVisibility(View.GONE);
        }
//        }
    }

    /**
     * 从startPosition移动到endPosition
     *
     * @param startPosition
     * @param endPosition
     */
    public void updateStatusWithMoveAction(int startPosition, int endPosition) {

        mImgChess.setBackgroundResource(0);
        if (startPosition - endPosition == 1) {//棋子向左
            Log.d("updateStatusWithMoveAction--from==" + mLocationX, ",to===" + (mLocationX + (-(mChessW + IConfig.CHESS_MARGIN))));
            mTrasnlateAnim = ObjectAnimator.ofFloat(this, "translationX", mLocationX, mLocationX + (-(mChessW + IConfig.CHESS_MARGIN)));
            mTrasnlateAnim.setDuration(150);
            mTrasnlateAnim.start();
            mLocationX = mLocationX + (-(mChessW + IConfig.CHESS_MARGIN));
            Log.d("updateStatusWithMoveAction棋子--" + mChess.getType() + "状态是--" + mChess.getStatus(), "向左移动到" + endPosition);
        } else if (startPosition - endPosition == 4) {//棋子向上
            Log.d("updateStatusWithMoveAction--from==" + mLocationY, ",to===" + (mLocationY + (-(mChessH + IConfig.CHESS_MARGIN))));
            mTrasnlateAnim = ObjectAnimator.ofFloat(this, "translationY", mLocationY, mLocationY + (-(mChessH + IConfig.CHESS_MARGIN)));
            mTrasnlateAnim.setDuration(150);
            mTrasnlateAnim.start();
            mLocationY = mLocationY + (-(mChessH + IConfig.CHESS_MARGIN));
            Log.d("updateStatusWithMoveAction棋子--" + mChess.getType() + "状态是--" + mChess.getStatus(), "向上移动到" + endPosition);
        } else if (startPosition - endPosition == -1) {//棋子向右
            Log.d("updateStatusWithMoveAction--from==" + mLocationX, ",to===" + (mLocationX + mChessW + IConfig.CHESS_MARGIN));
            mTrasnlateAnim = ObjectAnimator.ofFloat(this, "translationX", mLocationX, mLocationX + mChessW + IConfig.CHESS_MARGIN);
            mTrasnlateAnim.setDuration(150);
            mTrasnlateAnim.start();
            mLocationX = mLocationX + (mChessW + IConfig.CHESS_MARGIN);
            Log.d("updateStatusWithMoveAction棋子--" + mChess.getType() + "状态是--" + mChess.getStatus(), "向右移动到" + endPosition);
        } else if (startPosition - endPosition == -4) {//棋子向下
            Log.d("updateStatusWithMoveAction--from==" + mLocationY, ",to===" + (mLocationY + mChessH + IConfig.CHESS_MARGIN));
            mTrasnlateAnim = ObjectAnimator.ofFloat(this, "translationY", mLocationY, mLocationY + mChessH + IConfig.CHESS_MARGIN);
            mTrasnlateAnim.setDuration(150);
            mTrasnlateAnim.start();
            mLocationY = mLocationY + (mChessH + IConfig.CHESS_MARGIN);
            Log.d("updateStatusWithMoveAction棋子--" + mChess.getType() + "状态是--" + mChess.getStatus(), "向下移动到" + endPosition);
        }
        updateChessStatus();
    }

    /**
     * 翻开当前棋子
     */
    public void openChess() {
        mChess.setStatus(1);
        updateChessStatus();
    }

    /**
     * 选中当前棋子
     */
    public void setSelectStatus(boolean isSelect) {
        mChess.setSelect(isSelect);
        if (!isSelect) {
            mChess.setLeftType(0);
            mChess.setRightType(0);
            mChess.setTopType(0);
            mChess.setDownType(0);
        }
        updateChessStatus();
    }

    /**
     * @param chess 需要移动到的位置的棋子
     */
    public void startAction(Chess chess) {
        if (mTmpPosition - chess.getPosition() == 1) {//棋子向左
            moveChess(0, chess);
        } else if (mTmpPosition - chess.getPosition() == 4) {//棋子向上
            moveChess(1, chess);
        } else if (mTmpPosition - chess.getPosition() == -1) {//棋子向右
            moveChess(2, chess);
        } else if (mTmpPosition - chess.getPosition() == -4) {//棋子向下
            moveChess(3, chess);
        }
    }
    /**
     *
     */
    /**
     * 执行移动棋子动画
     *
     * @param direction 方向 0左 1上 2右 3下
     */
    private void moveChess(int direction, Chess chess) {
        final int endPosition = chess.getPosition();
        this.bringToFront();
        switch (direction) {
            case 0:
                Log.d("moveChess棋子--from==" + mLocationX, ",to===" + (mLocationX + (-(mChessW + IConfig.CHESS_MARGIN))));
                mTrasnlateAnim = ObjectAnimator.ofFloat(this, "translationX", mLocationX, mLocationX + (-(mChessW + IConfig.CHESS_MARGIN)));
                mTrasnlateAnim.setDuration(150);
                mTrasnlateAnim.start();
                Log.d("moveChess棋子--" + mChess.getType() + "状态是--" + mChess.getStatus(), "向左移动到" + chess.getPosition());
                mLocationX = mLocationX + (-(mChessW + IConfig.CHESS_MARGIN));
                break;
            case 1:
                Log.d("moveChess棋子--from==" + mLocationY, ",to===" + (mLocationY + (-(mChessH + IConfig.CHESS_MARGIN))));
                mTrasnlateAnim = ObjectAnimator.ofFloat(this, "translationY", mLocationY, mLocationY + (-(mChessH + IConfig.CHESS_MARGIN)));
                mTrasnlateAnim.setDuration(150);
                mTrasnlateAnim.start();
                mLocationY = mLocationY + (-(mChessH + IConfig.CHESS_MARGIN));
                Log.d("moveChess棋子--" + mChess.getType() + "状态是--" + mChess.getStatus(), "向上移动到" + chess.getPosition());
                break;
            case 2:
                Log.d("moveChess棋子--from==" + mLocationX, ",to===" + (mLocationX + mChessW + IConfig.CHESS_MARGIN));
                mTrasnlateAnim = ObjectAnimator.ofFloat(this, "translationX", mLocationX, mLocationX + mChessW + IConfig.CHESS_MARGIN);
                mTrasnlateAnim.setDuration(150);
                mTrasnlateAnim.start();
                mLocationX = mLocationX + (mChessW + IConfig.CHESS_MARGIN);
                Log.d("moveChess棋子--" + mChess.getType() + "状态是--" + mChess.getStatus(), "向右移动到" + chess.getPosition());
                break;
            case 3:
                Log.d("moveChess棋子--from==" + mLocationY, ",to===" + (mLocationY + mChessH + IConfig.CHESS_MARGIN));
                mTrasnlateAnim = ObjectAnimator.ofFloat(this, "translationY", mLocationY, mLocationY + mChessH + IConfig.CHESS_MARGIN);
                mTrasnlateAnim.setDuration(150);
                mTrasnlateAnim.start();
                mLocationY = mLocationY + (mChessH + IConfig.CHESS_MARGIN);
                Log.d("moveChess棋子--" + mChess.getType() + "状态是--" + mChess.getStatus(), "向下移动到" + chess.getPosition());
                break;
        }
        /**
         * 更改两个棋子的最终状态
         */
        //互换位置
        mChess.setPosition(chess.getPosition());
        chess.setPosition(mTmpPosition);
        mChess.setSelect(false);
        chess.setSelect(false);
        mChess.setLeftType(0);
        mChess.setRightType(0);
        mChess.setTopType(0);
        mChess.setDownType(0);
        chess.setLeftType(0);
        chess.setRightType(0);
        chess.setTopType(0);
        chess.setDownType(0);
        //自杀的情况下，动画执行完后归位
        boolean needResetSelf = false;
        if (chess.getStatus() == 1) {//需要PK
            if (mChess.getType() > chess.getType()) {
                if (mChess.getType() == 7 && chess.getType() == 0) {//象吃鼠自杀 己方出局
                    mChess.setStatus(2);
                    needResetSelf = true;
                    //因为提前交换了位置，自杀后无需交换位置，再切换回来
                    chess.setPosition(mChess.getPosition());
                    mChess.setPosition(mTmpPosition);
                } else {
                    chess.setStatus(2);
                }
            } else if (mChess.getType() == chess.getType()) {//Boom 同归于尽
                mChess.setStatus(2);
                chess.setStatus(2);
            } else {
                if (mChess.getType() == 0 && chess.getType() == 7) {//老鼠吃大象 对方出局
                    chess.setStatus(2);
                } else {
                    mChess.setStatus(2);
                    needResetSelf = true;
                    //因为提前交换了位置，自杀后无需交换位置，再切换回来
                    chess.setPosition(mChess.getPosition());
                    mChess.setPosition(mTmpPosition);
                }
            }
        }
        mBridge.moveActionEnd(mTmpPosition, endPosition, needResetSelf);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mChessW, mChessH);
    }

}
