package com.dingbei.diagnose;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.dingbei.diagnose.utils.AppLogger;
import com.dingbei.diagnose.utils.CameraHelper;
import com.dingbei.diagnose.utils.PhoneUtil;
import com.dingbei.diagnose.view.RecorderProgress;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;


/**
 * 拍摄视频页面
 */

public class VideoRecordActivity extends AppCompatActivity {

    private Camera mCamera;
    private TextureView mPreview;
    private MediaRecorder mMediaRecorder;
    private File mOutputFile;

    private boolean isRecording = false;
    private ImageView captureButton;
    private RecorderProgress recorderProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dingbei_activity_video_record);
        setStatusBarColor(getResources().getColor(R.color.status_bar));
        initView();
    }

    /**
     * 改变状态栏颜色
     * @param color
     */
    public void setStatusBarColor(int color){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        captureButton = (ImageView) findViewById(R.id.img_capture);
        recorderProgress = (RecorderProgress) findViewById(R.id.progress_record);

        final Handler handler = new Handler();
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRecording) { //开始录制
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            new MediaPrepareTask().execute(null, null, null);
                        }
                    }, 300);
                }else {
                    stopCapture();
                }
            }
        });

        int height = PhoneUtil.getPhoneWidth(this);
        height = (int) (height * ((double) 640 / 480));
        mPreview = (TextureView) findViewById(R.id.surface_view);
        mPreview.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height));
        mPreview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                configurePreview();
                try {
                    mCamera.setPreviewTexture(surface);
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                    AppLogger.e("Surface texture is unavailable or unsuitable" + e.getMessage());
                }

            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (mCamera == null)
                    return false;
                mCamera.stopPreview();
                mCamera.release();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {


                if (mCamera == null || !isRecording)
                    return;
                // mCamera.autoFocus(new Camera.AutoFocusCallback() {
                //     @Override
                //     public void onAutoFocus(boolean success, Camera camera) {
                //         if(success)
                //             mCamera.cancelAutoFocus();
                //     }
                // });


            }
        });
    }


    /**
     * The capture button controls all user interaction. When recording, the button click
     * stops recording, releases {@link MediaRecorder} and {@link Camera}. When not recording,
     * it prepares the {@link MediaRecorder} and starts recording.
     */
    public void stopCapture() {
        recorderProgress.stopAnimation();//取消显示进度条
        // BEGIN_INCLUDE(stop_release_media_recorder)

        // stop recording and release camera
        try {
            mMediaRecorder.stop();  // stop the recording
        } catch (RuntimeException e) {
            // RuntimeException is thrown when stop() is called immediately after start().
            // In this case the output file is not properly constructed ans should be deleted.
            AppLogger.d("RuntimeException: stop() is called immediately after start()");
            //noinspection ResultOfMethodCallIgnored
            mOutputFile.delete();
        }

        releaseMediaRecorder(); // release the MediaRecorder object
        mCamera.lock();         // take camera access back from MediaRecorder

        // inform the user that recording has stopped
        isRecording = false;
        setCaptureButtonState();
        releaseCamera();
        // END_INCLUDE(stop_release_media_recorder)

        String path = mOutputFile.getPath();
        startActivityForResult(new Intent(this, VideoPlayActivity.class)
                .putExtra("path", path)
                .putExtra("canDownload", false), 11);

        //更新媒体库
        MediaScannerConnection.scanFile(VideoRecordActivity.this, new String[]{path}, null, null);
    }

    private String millisToMMSS(long millis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        return simpleDateFormat.format(millis);
    }

    private void setCaptureButtonState() {
        if (isRecording) {
            recorderProgress.startAnimation();//显示进度条
            captureButton.setImageResource(R.drawable.ic_record_recording);
        } else {
            captureButton.setImageResource(R.drawable.ic_record_start);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // if we are using MediaRecorder, release it first
        releaseMediaRecorder();
        // release the camera immediately on pause event
        releaseCamera();
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            // clear recorder configuration
            mMediaRecorder.reset();
            // release the recorder object
            mMediaRecorder.release();
            mMediaRecorder = null;
            // Lock camera for later use i.e taking it back from MediaRecorder.
            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
            //mCamera.lock();
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            // release the camera for other applications
            mCamera.release();
            mCamera = null;
        }
    }

    private void configurePreview() {
        // BEGIN_INCLUDE (configure_preview)
        mCamera = CameraHelper.getDefaultCameraInstance();
        // We need to make sure that our preview and recording video size are supported by the
        // camera. Query camera to find all the sizes and choose the optimal size given the
        // dimensions of our preview surface.
        Camera.Parameters parameters = mCamera.getParameters();
        //        List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        //        List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();
        //        Camera.Size optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes,
        //                mSupportedPreviewSizes, mPreview.getWidth(), mPreview.getHeight());

        // Use the same size for recording profile.
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        profile.videoFrameWidth = 640;
        profile.videoFrameHeight = 480;

        // likewise for the camera object itself.
        parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);
        // END_INCLUDE (configure_preview)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private boolean prepareVideoRecorder() {
        // Use the same size for recording profile.
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        profile.videoFrameWidth = 640;
        profile.videoFrameHeight = 480;

        // BEGIN_INCLUDE (configure_media_recorder)
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setOrientationHint(90);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);


        profile.videoCodec = MediaRecorder.VideoEncoder.H264;//视频编码格式
        profile.audioCodec = MediaRecorder.AudioEncoder.AAC;//音频编码格式
        profile.videoFrameRate = 30;//设置30帧
        profile.videoBitRate = 2000 * 1000;
        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(profile);

        // Step 4: Set output file
        mOutputFile = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO);
        if (mOutputFile == null) {
            return false;
        }
        mMediaRecorder.setOutputFile(mOutputFile.getPath());
        mMediaRecorder.setMaxDuration(60000); //设置最大时长
        // END_INCLUDE (configure_media_recorder)

        // Step 5: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            AppLogger.d("IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            AppLogger.d("IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }


    /**
     * Asynchronous task for preparing the {@link MediaRecorder} since it's a long blocking
     * operation.
     */
    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {

        private final int maxVideoTime = 60900; //录制的最长时间，进度条也需要设置

        @Override
        protected Boolean doInBackground(Void... voids) {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();
                isRecording = true;

            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                finish();
            }
            new CountDownTimer(maxVideoTime, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    if (isRecording)
                        stopCapture();
                }
            }.start();

            // inform the user that recording has started
            setCaptureButtonState();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11 && resultCode == RESULT_OK) {
            setResult(RESULT_OK, new Intent()
                    .putExtra("path", data.getStringExtra("path"))
                    .putExtra("duration", data.getStringExtra("duration")));
            finish();
        }
    }
}
