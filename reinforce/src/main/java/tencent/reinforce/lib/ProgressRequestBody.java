package tencent.reinforce.lib;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.internal.Util;

import java.io.File;
import java.io.IOException;

import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * On 2020-05-21
 */
public class ProgressRequestBody extends RequestBody {

    private MediaType mContentType;
    private File mFile;

    private static final long SEGMENT_SIZE = 4 * 1024;
    private int mLastPercent = -1;

    private ProgressRequestBody(MediaType contentType, File file) {
        mContentType = contentType;
        mFile = file;
    }

    @Override
    public MediaType contentType() {
        return mContentType;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;

        try {
            long total = contentLength();
            source = Okio.source(mFile);
            long read;
            long upload = 0;
            while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
                upload += read;
                sink.flush();
                if (total > 0) {
                    int percent = (int) (upload * 1f / total * 100);
                    if (mLastPercent != percent) {
                        mLastPercent = percent;
                        System.out.println("Uploaded " + percent + "%");
                    }
                }
            }
        } finally {
            Util.closeQuietly(source);
        }
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }

    public static RequestBody create(final MediaType contentType, final File file) {
        if (file == null) {
            throw new NullPointerException("content == null");
        } else {
            return new ProgressRequestBody(contentType, file);
        }
    }
}
