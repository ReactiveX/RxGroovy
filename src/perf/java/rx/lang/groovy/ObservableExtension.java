package rx.lang.groovy;

import rx.Observable;
import rx.functions.Func1;

public class ObservableExtension {
    public <T> Observable<T> filter(Observable<T> self, Func1<? super T, Boolean> predicate) {
        return self.filter(predicate);
    }
}
