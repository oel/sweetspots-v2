package com.genuine.android.sweetspots;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.genuine.android.sweetspots.GameView.ICellListener;

public class GameActivity extends Activity {

    public static final String EXTRA_BOARD_SIZE = "com.genuine.android.sweetspots.library.GameActivity.EXTRA_BOARD_SIZE";
    public static final String EXTRA_NUM_OF_TARGETS = "com.genuine.android.sweetspots.library.GameActivity.EXTRA_NUM_OF_TARGETS";
    public static final String EXTRA_BOARD_ZONES = "com.genuine.android.sweetspots.library.GameActivity.EXTRA_BOARD_ZONES";

    private GameView mGameView;
    private TextView mInfoView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ImageView mTargetCount;
        Button mButtonSave;
        Button mButtonRestore;
        Button mButtonConfirm;

        // Log.i("INFO: GameActivity", "Within onCreate()");

        setContentView(R.layout.lib_game);

        mGameView = (GameView) findViewById(R.id.game_view);

        mTargetCount = (ImageView) findViewById(R.id.target_count);
        mButtonSave = (Button) findViewById(R.id.game_save);
        mButtonRestore = (Button) findViewById(R.id.game_restore);
        mButtonConfirm = (Button) findViewById(R.id.game_confirm);
        mInfoView = (TextView) findViewById(R.id.info_message);

        int bSize = 0, numOfTargets = 0;
        String bZones;
        int[][] zVal;

        bSize = getIntent().getIntExtra(EXTRA_BOARD_SIZE, 0);
        numOfTargets = getIntent().getIntExtra(EXTRA_NUM_OF_TARGETS, 0);        
        bZones = getIntent().getStringExtra(EXTRA_BOARD_ZONES);

        zVal = convertZoneVal(bSize, bZones);

        mGameView.initGameState(bSize, numOfTargets, numOfTargets, numOfTargets, zVal);

        mGameView.setFocusable(true);
        mGameView.setFocusableInTouchMode(true);

        if (numOfTargets == 2) {
            mTargetCount.setImageResource(R.drawable.lib_target_double);
        }

        mGameView.setCellListener(new CellListener());
        mButtonSave.setOnClickListener(new ButtonSaveListener());
        mButtonRestore.setOnClickListener(new ButtonRestoreListener());
        mButtonConfirm.setOnClickListener(new ButtonConfirmListener());

        // mButtonRestore.setEnabled(false);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Log.i("INFO: GameActivity", "Within onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Log.i("INFO: GameActivity", "Within onResume()");

        mGameView.resetStartTime();
    }

    /*
    // Require <Activity android:configChanges="keyboardHidden|orientation|screenSize" /> in AndroidManifest.xml
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Log.i("INFO: GameActivity", "Within onConfigurationChanged()");

        setContentView(R.layout.lib_game);
    }
    */

    private static class CellListener implements ICellListener {
        public void onCellSelected() {

            // Log.i("INFO: GameActivity", "Within onCellSelected()");
        }
    }

    private class ButtonSaveListener implements OnClickListener {
        public void onClick(View v) {
            mGameView.saveGame();
            // mButtonRestore.setEnabled(true);

            String text = getString(R.string.info_game_saved);
            // mInfoView.setText(text);
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    private class ButtonRestoreListener implements OnClickListener {
        public void onClick(View v) {
            mGameView.restoreGame();

            String text = getString(R.string.info_game_restored);
            // mInfoView.setText(text);
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    private class ButtonConfirmListener implements OnClickListener {
        public void onClick(View v) {
            String text;
            if (!mGameView.allRowsValid() || !mGameView.allColsValid() || !mGameView.allZonesValid()
                || !mGameView.noAdjacentTargets()) {

                text = getString(R.string.info_failure);
                mInfoView.setText(text);
            }
            else {
                text = getString(R.string.info_success) + mGameView.getTimeTaken() + "s";
                mInfoView.setText(text);
            }
        }
    }

    int[][] convertZoneVal(int bSize, String bZones) {
        String[] zData = bZones.split("\\s+");
        int[][] zVal = new int[bSize][bSize];

        for (int j = 0; j < bSize; j++) {
            for (int i = 0; i < bSize; i++) {
                try {
                    zVal[i][j] = Integer.parseInt(zData[j*bSize+i]);
                }
                catch(NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "ERROR: parsing board zone data!", Toast.LENGTH_SHORT).show();
                    GameActivity.this.finish();
                }
            }
        }
        return zVal;
    }
}
