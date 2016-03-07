
package barqsoft.footballscores.service.rx;

import retrofit2.Response;
import rx.Observable;

public class Transformers {

    private Transformers() {
        throw new UnsupportedOperationException("no instances");
    }

    /**
     * Applies the {@link HttpErrorOperator} on the given network observable
     *
     * @param <T>
     * @return
     */
    public static <T> Observable.Transformer<Response<T>, Response<T>> applyHttpErrorOperator() {
        return responseObservable -> responseObservable.lift(new HttpErrorOperator<>());
    }
}