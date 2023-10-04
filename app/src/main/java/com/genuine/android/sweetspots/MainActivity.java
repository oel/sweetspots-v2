package com.genuine.android.sweetspots;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

    private int[] gameBoardSize;
    private int[] gameTargetCount;
    private String[] gameZoneData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Log.i("INFO: MainActivity", "Within onCreate()");

        gameBoardSize = getResources().getIntArray(R.array.board_size_array);
        gameTargetCount = getResources().getIntArray(R.array.target_count_array);
        gameZoneData = getResources().getStringArray(R.array.zone_data_array);

        int numOfGames = gameBoardSize.length;
        Button[] gameButton = new Button[numOfGames];
        gameButton[0] = (Button) findViewById(R.id.game01);
        gameButton[1] = (Button) findViewById(R.id.game02);
        gameButton[2] = (Button) findViewById(R.id.game03);
        gameButton[3] = (Button) findViewById(R.id.game04);
        gameButton[4] = (Button) findViewById(R.id.game05);
        gameButton[5] = (Button) findViewById(R.id.game06);
        gameButton[6] = (Button) findViewById(R.id.game07);
        gameButton[7] = (Button) findViewById(R.id.game08);
        gameButton[8] = (Button) findViewById(R.id.game09);
        gameButton[9] = (Button) findViewById(R.id.game10);
        gameButton[10] = (Button) findViewById(R.id.game11);
        gameButton[11] = (Button) findViewById(R.id.game12);
        gameButton[12] = (Button) findViewById(R.id.game13);
        gameButton[13] = (Button) findViewById(R.id.game14);

        for (int i = 0; i < numOfGames; i++) {
            gameButton[i].setOnClickListener(this);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.game01:
            startGame(gameBoardSize[0], gameTargetCount[0], gameZoneData[0]);
            break;
        case R.id.game02:
            startGame(gameBoardSize[1], gameTargetCount[1], gameZoneData[1]);
            break;
        case R.id.game03:
            startGame(gameBoardSize[2], gameTargetCount[2], gameZoneData[2]);
            break;
        case R.id.game04:
            startGame(gameBoardSize[3], gameTargetCount[3], gameZoneData[3]);
            break;
        case R.id.game05:
            startGame(gameBoardSize[4], gameTargetCount[4], gameZoneData[4]);
            break;
        case R.id.game06:
            startGame(gameBoardSize[5], gameTargetCount[5], gameZoneData[5]);
            break;
        case R.id.game07:
            startGame(gameBoardSize[6], gameTargetCount[6], gameZoneData[6]);
            break;
        case R.id.game08:
            startGame(gameBoardSize[7], gameTargetCount[7], gameZoneData[7]);
            break;
        case R.id.game09:
            startGame(gameBoardSize[8], gameTargetCount[8], gameZoneData[8]);
            break;
        case R.id.game10:
            startGame(gameBoardSize[9], gameTargetCount[9], gameZoneData[9]);
            break;
        case R.id.game11:
            startGame(gameBoardSize[10], gameTargetCount[10], gameZoneData[10]);
            break;
        case R.id.game12:
            startGame(gameBoardSize[11], gameTargetCount[11], gameZoneData[11]);
            break;
        case R.id.game13:
            startGame(gameBoardSize[12], gameTargetCount[12], gameZoneData[12]);
            break;
        case R.id.game14:
            startGame(gameBoardSize[13], gameTargetCount[13], gameZoneData[13]);
            break;
        default:
            break;
        }
    }

    void startGame(int boardSize, int numOfTargets, String boardZones) {

        // Log.i("INFO: MainActivity", "Within startGame()");

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.EXTRA_BOARD_SIZE, boardSize);
        intent.putExtra(GameActivity.EXTRA_NUM_OF_TARGETS, numOfTargets);
        intent.putExtra(GameActivity.EXTRA_BOARD_ZONES, boardZones);
        startActivity(intent);
    }
}
