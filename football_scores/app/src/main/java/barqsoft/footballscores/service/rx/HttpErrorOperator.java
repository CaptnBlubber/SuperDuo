/*
 * Created by Thomas Keller
 *
 * Copyright (c) 2016, Maxdome
 *
 * All rights reserved.
 */

package barqsoft.footballscores.service.rx;

import java.io.IOException;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;

/**
 * Redirects an unsuccessful HTTP Response to the error stream. Also repackages IO exceptions.
 * <p>
 * Check for {@link HttpError} there.
 */
class HttpErrorOperator<T> implements Observable.Operator<Response<T>, Response<T>> {

    @Override
    public Subscriber<? super Response<T>> call(final Subscriber<? super Response<T>> subscriber) {

        return new Subscriber<Response<T>>() {
            @Override
            public void onCompleted() {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                if (e instanceof IOException) {
                    subscriber.onError(new HttpError((IOException) e));
                }
                subscriber.onError(e);
            }

            @Override
            public void onNext(Response<T> r) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                if (!r.isSuccess()) {
                    subscriber.onError(new HttpError(r.code(), r.errorBody()));
                    return;
                }
                subscriber.onNext(r);
            }
        };
    }
}
