package com.qianbajin.shortvdhelper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;

/**
 * @author weiyitai
 */
public class MainActivity extends Activity {

    public static final String[] PERMISSION = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    /**
     * 文件夹大小及数量
     */
    private TextView mDirSize;
    /**
     * 删除的空文件夹数量
     */
    private TextView mDeleteCount;
    /**
     * 删除多余的文件
     */
    private TextView mDeleteFile;
    /**
     * 复制子文件到父文件夹
     */
    private TextView mCopyFile;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean permission = checkPermission();
        Log.d("MainActivity", "permission:" + permission);
        refreshFileSize();
        mDirSize = findViewById(R.id.tv_dir_size);
        mDeleteCount = findViewById(R.id.tv_del_count);
        mDeleteFile = findViewById(R.id.tv_del_file);
        mCopyFile = findViewById(R.id.tv_copy_file);

        calculateDir(null);
    }

    public void calculateDir(View view) {
        ThreadManager.runOnBackground(() -> {
            long l = System.currentTimeMillis();
            long[] dirSize = FileUtil.getDirSize(new File(FileUtil.path));
            long l1 = System.currentTimeMillis() - l;
            Log.d("MainActivity", "dirSize:" + FileUtil.getFileSizeString(dirSize[1]) + "  " + l1);
            runOnUiThread(() -> mDirSize.setText(getString(R.string.dir_size, dirSize[0], FileUtil.getFileSizeString(dirSize[1]), l1)));
        });
    }

    public void deleteEmptyDir(View view) {
        showDialog();
        ThreadManager.runOnBackground(() -> {
            long l = System.currentTimeMillis();
            int count = FileUtil.deleteEmptyDir();
            long l1 = System.currentTimeMillis() - l;
            runOnUiThread(() -> {
                dismissDialog();
                if (count == 0) {
                    Toast.makeText(this, R.string.no_empty_dir, Toast.LENGTH_SHORT).show();
                } else {
                    mDeleteCount.setText(getString(R.string.delete_count, count, l1));
                }
            });
        });
    }

    public void deleteMore(View view) {
        showDialog();
        ThreadManager.runOnBackground(() -> {
            long l1 = System.currentTimeMillis();
            long[] longs = FileUtil.deleteUnnecessaryFile();
            Log.d("MainActivity", "deleteMore:" + Arrays.toString(longs));
            runOnUiThread(() -> {
                dismissDialog();
                if (longs[0] > 0) {
                    mDeleteFile.setText(getString(R.string.del_file, longs[0], FileUtil.getFileSizeString(longs[1]), System.currentTimeMillis() - l1));
                } else {
                    Toast.makeText(this, R.string.no_more_file_to_delete, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public void copy2Parent(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.tip)
                .setMessage(R.string.move_tip)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    showDialog();
                    ThreadManager.runOnBackground(() -> {
                        long l = System.currentTimeMillis();
                        long[] longs = FileUtil.move2Parent();
                        Log.d("MainActivity", "longs:" + Arrays.toString(longs));
                        runOnUiThread(() -> {
                            dismissDialog();
                            if (longs[0] > 0) {
                                mCopyFile.setText(getString(R.string.move_file, longs[0], longs[0], FileUtil.getFileSizeString(longs[2]), System.currentTimeMillis() - l));
                            } else {
                                Toast.makeText(this, R.string.no_file_to_move, Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.moving));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    private void dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private boolean checkPermission() {
        boolean has = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String s : PERMISSION) {
                int i = checkSelfPermission(s);
                if (i != PackageManager.PERMISSION_GRANTED) {
                    has = false;
                    requestPermissions(new String[]{s}, 100);
                }
            }
        }
        return has;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("MainActivity", "permissions:" + Arrays.toString(permissions) + " " + Arrays.toString(grantResults));
    }

    private void refreshFileSize() {
        TextView textView = findViewById(R.id.tv_size);
        long availableSize = FileUtil.getAvailableSize();
        String available = FileUtil.getFileSizeString(availableSize);
        long sdTotal = FileUtil.getSdTotal();
        String total = FileUtil.getFileSizeString(sdTotal);
        Log.d("MainActivity", "sdTotal:" + sdTotal + "  availableSize:" + availableSize);

        textView.setText(getString(R.string.sd_size, available, total));
        ProgressBar progressBar = findViewById(R.id.sd_progress);
        progressBar.setMax((int) (sdTotal / 1024));
        progressBar.setProgress((int) ((sdTotal - availableSize) / 1024));
    }
}
