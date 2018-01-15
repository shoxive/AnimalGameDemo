package com.shoxive.animalgamedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.shoxive.animalgamedemo.view.CheckerboardView;

public class MainActivity extends AppCompatActivity implements ChessBoardBridge {
    private TextView mTvTurns;
    private CheckerboardView mChessBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvTurns = (TextView) findViewById(R.id.tv_turns);
        mChessBoard = (CheckerboardView) findViewById(R.id.checkerboard);
        mChessBoard.addChessBoardBridge(this);
    }

    @Override
    public void updateTurns(boolean isBlueTurns) {
        if (isBlueTurns) {
            mTvTurns.setText("蓝色方回合");
            mTvTurns.setBackgroundResource(android.R.color.holo_blue_light);
        } else {
            mTvTurns.setText("红色方回合");
            mTvTurns.setBackgroundResource(android.R.color.holo_red_light);
        }
    }
}
