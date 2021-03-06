package android.media;

import android.app.backup.FullBackup;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.IBinder;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MediaMetadataRetriever {
    private static final int EMBEDDED_PICTURE_TYPE_ANY = 65535;
    public static final int METADATA_KEY_ALBUM = 1;
    public static final int METADATA_KEY_ALBUMARTIST = 13;
    public static final int METADATA_KEY_ARTIST = 2;
    public static final int METADATA_KEY_AUTHOR = 3;
    public static final int METADATA_KEY_BITRATE = 20;
    public static final int METADATA_KEY_CAPTURE_FRAMERATE = 25;
    public static final int METADATA_KEY_CD_TRACK_NUMBER = 0;
    public static final int METADATA_KEY_COMPILATION = 15;
    public static final int METADATA_KEY_COMPOSER = 4;
    public static final int METADATA_KEY_DATE = 5;
    public static final int METADATA_KEY_DISC_NUMBER = 14;
    public static final int METADATA_KEY_DURATION = 9;
    public static final int METADATA_KEY_EXIF_LENGTH = 34;
    public static final int METADATA_KEY_EXIF_OFFSET = 33;
    public static final int METADATA_KEY_GENRE = 6;
    public static final int METADATA_KEY_HAS_AUDIO = 16;
    public static final int METADATA_KEY_HAS_IMAGE = 26;
    public static final int METADATA_KEY_HAS_VIDEO = 17;
    public static final int METADATA_KEY_IMAGE_COUNT = 27;
    public static final int METADATA_KEY_IMAGE_HEIGHT = 30;
    public static final int METADATA_KEY_IMAGE_PRIMARY = 28;
    public static final int METADATA_KEY_IMAGE_ROTATION = 31;
    public static final int METADATA_KEY_IMAGE_WIDTH = 29;
    public static final int METADATA_KEY_IS_DRM = 22;
    public static final int METADATA_KEY_LOCATION = 23;
    public static final int METADATA_KEY_LYRIC = 1000;
    public static final int METADATA_KEY_MIMETYPE = 12;
    public static final int METADATA_KEY_NUM_TRACKS = 10;
    public static final int METADATA_KEY_TIMED_TEXT_LANGUAGES = 21;
    public static final int METADATA_KEY_TITLE = 7;
    public static final int METADATA_KEY_VIDEO_FRAME_COUNT = 32;
    public static final int METADATA_KEY_VIDEO_HEIGHT = 19;
    public static final int METADATA_KEY_VIDEO_ROTATION = 24;
    public static final int METADATA_KEY_VIDEO_WIDTH = 18;
    public static final int METADATA_KEY_WRITER = 11;
    public static final int METADATA_KEY_YEAR = 8;
    public static final int OPTION_ARGB8888 = 5;
    public static final int OPTION_CLOSEST = 3;
    public static final int OPTION_CLOSEST_SYNC = 2;
    public static final int OPTION_NEXT_SYNC = 1;
    public static final int OPTION_PREVIOUS_SYNC = 0;
    private long mNativeContext;

    public static final class BitmapParams {
        private Config inPreferredConfig = Config.ARGB_8888;
        private Config outActualConfig = Config.ARGB_8888;

        public void setPreferredConfig(Config config) {
            if (config != null) {
                this.inPreferredConfig = config;
                return;
            }
            throw new IllegalArgumentException("preferred config can't be null");
        }

        public Config getPreferredConfig() {
            return this.inPreferredConfig;
        }

        public Config getActualConfig() {
            return this.outActualConfig;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Option {
    }

    private native List<Bitmap> _getFrameAtIndex(int i, int i2, BitmapParams bitmapParams);

    private native Bitmap _getFrameAtTime(long j, int i, int i2, int i3);

    private native Bitmap _getImageAtIndex(int i, BitmapParams bitmapParams);

    private native void _setDataSource(MediaDataSource mediaDataSource) throws IllegalArgumentException;

    private native void _setDataSource(IBinder iBinder, String str, String[] strArr, String[] strArr2) throws IllegalArgumentException;

    private native byte[] getEmbeddedPicture(int i);

    private final native void native_finalize();

    private static native void native_init();

    private native void native_setup();

    public native String extractMetadata(int i);

    public native Bitmap getThumbnailImageAtIndex(int i, BitmapParams bitmapParams, int i2, int i3);

    public native void release();

    public native void setDataSource(FileDescriptor fileDescriptor, long j, long j2) throws IllegalArgumentException;

    static {
        System.loadLibrary("media_jni");
        native_init();
    }

    public MediaMetadataRetriever() {
        native_setup();
    }

    public void setDataSource(String path) throws IllegalArgumentException {
        if (path != null) {
            FileInputStream is;
            try {
                is = new FileInputStream(path);
                setDataSource(is.getFD(), 0, DataSourceDesc.LONG_MAX);
                is.close();
                return;
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException();
            } catch (IOException e2) {
                throw new IllegalArgumentException();
            } catch (RuntimeException e3) {
                throw new IllegalArgumentException();
            } catch (Throwable th) {
                r1.addSuppressed(th);
            }
        }
        throw new IllegalArgumentException();
    }

    public void setDataSource(String uri, Map<String, String> headers) throws IllegalArgumentException {
        int i = 0;
        String[] keys = new String[headers.size()];
        String[] values = new String[headers.size()];
        for (Entry<String, String> entry : headers.entrySet()) {
            keys[i] = (String) entry.getKey();
            values[i] = (String) entry.getValue();
            i++;
        }
        _setDataSource(MediaHTTPService.createHttpServiceBinderIfNecessary(uri), uri, keys, values);
    }

    public void setDataSource(FileDescriptor fd) throws IllegalArgumentException {
        setDataSource(fd, 0, DataSourceDesc.LONG_MAX);
    }

    public void setDataSource(Context context, Uri uri) throws IllegalArgumentException, SecurityException {
        if (uri != null) {
            String scheme = uri.getScheme();
            if (scheme == null || scheme.equals(ContentResolver.SCHEME_FILE)) {
                setDataSource(uri.getPath());
                return;
            }
            AssetFileDescriptor fd = null;
            try {
                fd = context.getContentResolver().openAssetFileDescriptor(uri, FullBackup.ROOT_TREE_TOKEN);
                if (fd != null) {
                    FileDescriptor descriptor = fd.getFileDescriptor();
                    if (descriptor.valid()) {
                        if (fd.getDeclaredLength() < 0) {
                            setDataSource(descriptor);
                        } else {
                            setDataSource(descriptor, fd.getStartOffset(), fd.getDeclaredLength());
                        }
                        if (fd != null) {
                            try {
                                fd.close();
                            } catch (IOException e) {
                            }
                        }
                        return;
                    }
                    throw new IllegalArgumentException();
                }
                throw new IllegalArgumentException();
            } catch (FileNotFoundException e2) {
                throw new IllegalArgumentException();
            } catch (SecurityException e3) {
                if (fd != null) {
                    try {
                        fd.close();
                    } catch (IOException e4) {
                    }
                }
                setDataSource(uri.toString());
            } catch (Throwable th) {
                if (fd != null) {
                    try {
                        fd.close();
                    } catch (IOException e5) {
                    }
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setDataSource(MediaDataSource dataSource) throws IllegalArgumentException {
        _setDataSource(dataSource);
    }

    public Bitmap getFrameAtTime(long timeUs, int option) {
        if (option >= 0 && option <= 5) {
            return _getFrameAtTime(timeUs, option, -1, -1);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unsupported option: ");
        stringBuilder.append(option);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public Bitmap getScaledFrameAtTime(long timeUs, int option, int dstWidth, int dstHeight) {
        StringBuilder stringBuilder;
        if (option < 0 || option > 3) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Unsupported option: ");
            stringBuilder.append(option);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (dstWidth <= 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid width: ");
            stringBuilder.append(dstWidth);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (dstHeight > 0) {
            return _getFrameAtTime(timeUs, option, dstWidth, dstHeight);
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid height: ");
            stringBuilder.append(dstHeight);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public Bitmap getFrameAtTime(long timeUs) {
        return getFrameAtTime(timeUs, 2);
    }

    public Bitmap getFrameAtTime() {
        return _getFrameAtTime(-1, 2, -1, -1);
    }

    public Bitmap getFrameAtIndex(int frameIndex, BitmapParams params) {
        return (Bitmap) getFramesAtIndex(frameIndex, 1, params).get(0);
    }

    public Bitmap getFrameAtIndex(int frameIndex) {
        return (Bitmap) getFramesAtIndex(frameIndex, 1).get(0);
    }

    public List<Bitmap> getFramesAtIndex(int frameIndex, int numFrames, BitmapParams params) {
        return getFramesAtIndexInternal(frameIndex, numFrames, params);
    }

    public List<Bitmap> getFramesAtIndex(int frameIndex, int numFrames) {
        return getFramesAtIndexInternal(frameIndex, numFrames, null);
    }

    private List<Bitmap> getFramesAtIndexInternal(int frameIndex, int numFrames, BitmapParams params) {
        if ("yes".equals(extractMetadata(17))) {
            int frameCount = Integer.parseInt(extractMetadata(32));
            if (frameIndex >= 0 && numFrames >= 1 && frameIndex < frameCount && frameIndex <= frameCount - numFrames) {
                return _getFrameAtIndex(frameIndex, numFrames, params);
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid frameIndex or numFrames: ");
            stringBuilder.append(frameIndex);
            stringBuilder.append(", ");
            stringBuilder.append(numFrames);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        throw new IllegalStateException("Does not contail video or image sequences");
    }

    public Bitmap getImageAtIndex(int imageIndex, BitmapParams params) {
        return getImageAtIndexInternal(imageIndex, params);
    }

    public Bitmap getImageAtIndex(int imageIndex) {
        return getImageAtIndexInternal(imageIndex, null);
    }

    public Bitmap getPrimaryImage(BitmapParams params) {
        return getImageAtIndexInternal(-1, params);
    }

    public Bitmap getPrimaryImage() {
        return getImageAtIndexInternal(-1, null);
    }

    private Bitmap getImageAtIndexInternal(int imageIndex, BitmapParams params) {
        if ("yes".equals(extractMetadata(26))) {
            String imageCount = extractMetadata(27);
            if (imageIndex < Integer.parseInt(imageCount)) {
                return _getImageAtIndex(imageIndex, params);
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid image index: ");
            stringBuilder.append(imageCount);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        throw new IllegalStateException("Does not contail still images");
    }

    public byte[] getEmbeddedPicture() {
        return getEmbeddedPicture(65535);
    }

    protected void finalize() throws Throwable {
        try {
            native_finalize();
        } finally {
            super.finalize();
        }
    }
}
