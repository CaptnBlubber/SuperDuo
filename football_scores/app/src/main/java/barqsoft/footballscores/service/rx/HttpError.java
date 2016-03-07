/*
 * Created by Thomas Keller
 *
 * Copyright (c) 2016, Maxdome
 *
 * All rights reserved.
 */

package barqsoft.footballscores.service.rx;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Models an HTTP error, that is either caused by an IOException, or by an status code >= 300
 * <p>
 */
public class HttpError extends Exception {
    private final int mCode;

    private transient final ResponseBody mBody;

    HttpError(int code, ResponseBody body) {
        super("HTTP error " + code);
        mCode = code;
        mBody = body;
    }

    HttpError(IOException error) {
        super("HTTP request failure", error);
        mCode = -1;
        mBody = null;
    }

    public int getCode() {
        return mCode;
    }

    public ResponseBody getBody() {
        return mBody;
    }
}