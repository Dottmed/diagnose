package com.dingbei.diagnose.gallery.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.dingbei.diagnose.utils.AppLogger;
import com.dingbei.diagnose.utils.FileUtil;
import com.dingbei.diagnose.utils.ValidateUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 图片压缩类
 */
public class PhotoOperator {

    private static PhotoOperator mOperate;

    public static PhotoOperator getInstance() {
        if (mOperate == null) {
            mOperate = new PhotoOperator();
        }
        return mOperate;
    }

    private File getTempFile(int width, int height) {
        File file = null;
        try {
            String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd").format(new Date());
            File path = new File(FileUtil.TEMP);
            boolean b = path.exists();
            if (!b) {
                path.mkdirs();
            }
            file = File.createTempFile(fileName, "_" + width + "x" + height + ".jpg", path);
        } catch (IOException e) {
            AppLogger.e(e.getMessage());
            e.printStackTrace();
        }
        return file;
    }

    public File scale(String path, long kb, int quality) throws Exception {
        File outputFile = new File(path);
        //读取长度
        long fileSize = outputFile.length();
        final long fileMaxSize = kb * 1024;

        //如果大于指定KB
        if (fileSize >= fileMaxSize) {
            //获得图片的宽和高
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int width = options.outWidth;
            int height = options.outHeight;

            //设置图片的高宽和大小
            double scale = Math.sqrt((float) fileSize / fileMaxSize);
            width = (int) (width / scale);
            height = (int) (height / scale);
            options.outHeight = height;
            options.outWidth = width;
//            options.inSampleSize = (int) (scale + 0.5);
            options.inSampleSize = (int) (scale);
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            //保存图片到本地
            outputFile = getTempFile(width, height);
            FileOutputStream fos = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);

            //释放资源
            fos.close();
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = null;
            fos = null;
            options = null;

        } else {
            //获得图片的宽和高
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            File tempFile = outputFile;
            outputFile = getTempFile(options.outWidth, options.outHeight);
            copyFileUsingFileChannels(tempFile, outputFile);
        }

        return outputFile;
    }


    private static void copyFileUsingFileChannels(File source, File dest)
            throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
            inputChannel = null;
            outputChannel = null;
        }
    }

    public static float getImageRatio(String url) {
        float ratio = 1.0f;
        String str = ValidateUtil.getMatchStr("(_\\d+x\\d+)", url);
        if (!TextUtils.isEmpty(str)) {
            str = str.substring(1, str.length());
            String[] temp = str.split("x");
            float width = Float.valueOf(temp[0]);
            float height = Float.valueOf(temp[1]);
            ratio = width / height;
            AppLogger.i(ratio + "");
            return ratio;
        }
        AppLogger.i(url);
        return ratio;
    }

}
