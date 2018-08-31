package com.maning.updatelibrary.http;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * @author : maning
 * @desc :  下载监听所用
 */
public class ProgressResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private final AbsFileProgressCallback progressListener;
    private BufferedSource bufferedSource;
    private Handler mUIHandler = new Handler(Looper.getMainLooper());

    public ProgressResponseBody(ResponseBody mResponseBody, AbsFileProgressCallback mProgressListener) {
        responseBody = mResponseBody;
        progressListener = mProgressListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;

                final long finalBytesRead = bytesRead;
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressListener.onProgress(totalBytesRead, responseBody.contentLength(), finalBytesRead == -1);
                    }
                });
                return bytesRead;
            }
        };
    }

    public interface ProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }


}
