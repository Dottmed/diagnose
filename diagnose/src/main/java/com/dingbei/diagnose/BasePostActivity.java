package com.dingbei.diagnose;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.dingbei.diagnose.gallery.PhotoViewerActivity;
import com.dingbei.diagnose.gallery.utils.GalleryUtil;
import com.dingbei.diagnose.gallery.utils.PhotoOperator;
import com.dingbei.diagnose.utils.AppLogger;
import com.dingbei.diagnose.utils.DiagnoseUtil;
import com.dingbei.diagnose.utils.FileUtil;
import com.dingbei.diagnose.view.RecordPop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author Dayo
 */

public abstract class BasePostActivity extends BaseAutoActivity {

    protected final int REQUEST_PHOTO = 1011;
    protected final int REQUEST_VOICE = 1012;
    protected final int REQUEST_VIDEO = 1013;
    protected final int SELECT_VIDEO = 1014;

    private GalleryUtil mGallery;
    private String mPhotoPath;
    private TakePhotoCallback mPhotoCallback;
    private ShootVideoCallback mVideoCallback;
    private MediaRecorder mRecorder;
    private String mRecordPath;
    private MediaPlayer mPlayer;
    private ImageView mCurrentPlay;
    private RecordPop mRecordPop;
    private View mLocationView;
    private RecordCallback mVoiceCallback;
    private String mCurrentPath;

    protected void init() {
        mGallery = new GalleryUtil();
        mRecordPop = new RecordPop(BasePostActivity.this, new RecordPop.OnRecordListener() {
            @Override
            public void onStart() {
                setRecorder();
            }

            @Override
            public void onStop(int duration) {
                stopRecorder(duration);
            }
        });
    }

    /**
     * open gallery
     * @param currentSize
     * @param maxSize
     */
    protected void selectPictures(int currentSize, int maxSize, final SelectPicturesCallback callback) {
        mGallery.selectPhotos(currentSize, maxSize, new GalleryUtil.OnResultCallback() {
            @Override
            public void onSuccess(List<String> list) {
                callback.callback(list);
            }

            @Override
            public void onFailure(String errorMsg) {
                showMsg(errorMsg);
            }
        });
    }

    protected void viewPictures(ArrayList<String> list, int position) {
        PhotoViewerActivity.startViewer(this, list, position);
    }

    protected void requestPermission(int requestCode) {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            handleRequest(requestCode);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_media),
                    requestCode, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        super.onPermissionsGranted(requestCode, perms);
        handleRequest(requestCode);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        super.onPermissionsDenied(requestCode, perms);
        showMsg(getString(R.string.permission_denied));
    }

    protected void handleRequest(int requestCode) {
        switch (requestCode) {
            case REQUEST_PHOTO :
                capture();
                break;
            case REQUEST_VOICE :
                if(mLocationView != null) {
                    mRecordPop.showAtLocation(mLocationView);
                }
                break;
            case REQUEST_VIDEO :
                startShoot();
                break;
        }
    }

    protected void record(View parent, RecordCallback callback) {
        mLocationView = parent;
        mVoiceCallback = callback;
        requestPermission(REQUEST_VOICE);
    }

    protected void startRecord(RecordCallback callback) {
        mVoiceCallback = callback;
        setRecorder();
    }

    private void setRecorder() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecordPath = FileUtil.TEMP + "db_" + System.currentTimeMillis() + ".m4a";
        mRecorder.setOutputFile(mRecordPath);
        //mRecorder.setMaxDuration(60000);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
            mRecorder.start();
            if(mLocationView != null) {
                mRecordPop.start();
            }
        } catch (IOException e) {
            AppLogger.e(e.getMessage());
            e.printStackTrace();
        }
    }

    protected void stopRecorder(int duration) {
        try {
            mRecorder.stop();  // stop the recording
        } catch (RuntimeException e) {
            // RuntimeException is thrown when stop() is called immediately after start().
            // In this case the output file is not properly constructed ans should be deleted.
            AppLogger.e("RuntimeException: stop() is called immediately after start()");
            //noinspection ResultOfMethodCallIgnored
            //mOutputFile.delete();
        }
        if(mRecorder != null) {
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;

            if(mLocationView != null) {
                mRecordPop.stop();
            }

            File file = FileUtil.renameFileWithSize(mRecordPath, "_" + duration);
            mVoiceCallback.callback(file.getPath(), duration);
        }
    }

    public void playRecord(String path, ImageView icon_play) {
        if(TextUtils.isEmpty(path)) {
           return;
        }

        if(path.equals(mCurrentPath)) {
            if(mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.stop();
                if(mCurrentPlay != null) {
                    mCurrentPlay.setSelected(false);
                }
                return;
            }
        }

        if(mPlayer == null) {
            mPlayer = new MediaPlayer();
        }else {
            mPlayer.reset();
        }

        try {
            if(mCurrentPlay != null) {
                mCurrentPlay.setSelected(false);
            }
            mCurrentPath = path;
            mCurrentPlay = icon_play;
            mCurrentPlay.setSelected(true);
            mPlayer.setDataSource(path);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mCurrentPlay.setSelected(false);
                }
            });
            //mPlayer.setOnErrorListener(this);
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            //mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playRecord(String path) {
        if(mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();

        }else {
            if(mPlayer == null) {
                mPlayer = new MediaPlayer();
            }else {
                mPlayer.reset();
            }

            try {
                mPlayer.setDataSource(path);
                //mPlayer.setOnErrorListener(this);
                mPlayer.prepareAsync();
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                //mPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void takePhoto(TakePhotoCallback callback) {
        mPhotoCallback = callback;
        requestPermission(REQUEST_PHOTO);
    }

    private void capture() {
        mPhotoPath = FileUtil.PHOTO + System.currentTimeMillis() + ".jpg";
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //适配7.0
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uri = FileProvider.getUriForFile(BasePostActivity.this, DiagnoseUtil.fileProvider, new File(mPhotoPath));

            }else {
                uri = Uri.fromFile(new File(mPhotoPath));
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            startActivityForResult(intent, REQUEST_PHOTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String handleCrop(String path) {
        PhotoOperator operate = PhotoOperator.getInstance();
        File file = null;
        try {
            file = operate.scale(path, 300, 50);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file != null ? file.getPath() : "";
    }

    protected void selectVideo(ShootVideoCallback callback) {
        mVideoCallback = callback;
        startActivityForResult(new Intent(BasePostActivity.this, VideoSelectActivity.class), SELECT_VIDEO);
    }

    protected void shootVideo(ShootVideoCallback callback) {
        mVideoCallback = callback;
        requestPermission(REQUEST_VIDEO);
    }

    private void startShoot() {
        startActivityForResult(new Intent(BasePostActivity.this, VideoRecordActivity.class), REQUEST_VIDEO);
    }

    protected void playVideo(String path, boolean canDownload) {
        startActivity(new Intent(BasePostActivity.this, VideoPlayActivity.class)
                .putExtra("path", path)
                .putExtra("canDownload", canDownload));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PHOTO :
                if(resultCode == RESULT_OK) {
                    String cropPath = handleCrop(mPhotoPath);

                    mPhotoCallback.callback(cropPath);

                    MediaScannerConnection.scanFile(BasePostActivity.this, new String[]{cropPath}, null, null);
                }
                break;
            case REQUEST_VIDEO:
                if(resultCode == RESULT_OK) {
                    String path = data.getStringExtra("path");
                    String duration = data.getStringExtra("duration");
//                    File file = FileUtil.renameFileWithSize(path, "_" + duration);
                    mVideoCallback.callback(path, duration);
                }
                break;
            case SELECT_VIDEO:
                if(resultCode == RESULT_OK) {
                    String path = data.getStringExtra("path");
                    String duration = data.getStringExtra("duration");
//                    File file = FileUtil.renameFileWithSize(path, "_" + duration);
                    mVideoCallback.callback(path, duration);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        String[] perms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            FileUtil.deleteFile(FileUtil.TEMP);
        }

        if(mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

        super.onDestroy();
    }

    public interface SelectPicturesCallback {
        void callback(List<String> list);
    }

    public interface TakePhotoCallback {
        void callback(String path);
    }

    public interface RecordCallback {
        void callback(String path, int duration);
    }

    public interface ShootVideoCallback {
        void callback(String path, String duration);
    }
}
