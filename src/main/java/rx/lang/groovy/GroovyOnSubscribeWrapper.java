/**
 * Copyright 2014 Netflix, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rx.lang.groovy;

import groovy.lang.Closure;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.Subscription;

/**
 * Concrete wrapper that accepts a {@link Closure} and produces a {@link OnSubscribeFunc}.
 * 
 * @param <T>
 */
public class GroovyOnSubscribeWrapper<T> implements OnSubscribe<T> {

    private final Closure<Subscription> closure;

    public GroovyOnSubscribeWrapper(Closure<Subscription> closure) {
        this.closure = closure;
    }

    @Override
    public void call(Subscriber<? super T> observer) {
        closure.call(observer);
    }

}