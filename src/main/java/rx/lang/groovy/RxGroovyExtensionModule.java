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
import groovy.lang.MetaMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.m12n.ExtensionModule;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.functions.Action;
import rx.functions.Function;
import rx.observables.BlockingObservable;

/**
 * ExtensionModule that adds extension methods to support groovy.lang.Closure
 * anywhere rx.util.functions.Function/Action is used in classes defined in CLASS_TO_EXTEND.
 * 
 * It is specifically intended for providing extension methods on Observable.
 */
public class RxGroovyExtensionModule extends ExtensionModule {

    @SuppressWarnings("rawtypes")
    private final static Class[] CLASS_TO_EXTEND = new Class[] { Observable.class, BlockingObservable.class };

    public RxGroovyExtensionModule() {
        super("RxGroovyExtensionModule", "1.0");
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<MetaMethod> getMetaMethods() {
        //        System.out.println("**** RxGroovyExtensionModule => Initializing and returning MetaMethods.");
        List<MetaMethod> methods = new ArrayList<MetaMethod>();

        for (Class classToExtend : CLASS_TO_EXTEND) {
            ArrayList<Method> funcMethods = new ArrayList<Method>();
            ArrayList<String> names = new ArrayList<String>();
            for (final Method m : classToExtend.getMethods()) {
                for (Class c : m.getParameterTypes()) {
                    if (Function.class.isAssignableFrom(c)) {
                        funcMethods.add(m);
                        names.add(m.getName());
                        // break out of parameter-type loop
                        break;
                    }
                }
            }

            CallSiteArray callSites = new CallSiteArray(classToExtend, names.toArray(new String[0]));
            for (int index = 0; index < funcMethods.size(); index++) {
                methods.add(createMetaMethod(funcMethods.get(index), callSites, index));
            }
        }

        return methods;
    }

    private MetaMethod createMetaMethod(final Method m, final CallSiteArray callSites, final int callSiteIndex) {
        if (m.getDeclaringClass().equals(Observable.class) && m.getName().equals("create")) {
            return specialCasedOverrideForCreate(m);
        }
        return new MetaMethod() {

            @Override
            public int getModifiers() {
                return m.getModifiers();
            }

            @Override
            public String getName() {
                return m.getName();
            }

            @SuppressWarnings("rawtypes")
            @Override
            public Class getReturnType() {
                return m.getReturnType();
            }

            @Override
            public CachedClass getDeclaringClass() {
                return ReflectionCache.getCachedClass(m.getDeclaringClass());
            }

            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public Object invoke(Object object, Object[] arguments) {
                //                System.out.println("***** RxGroovyExtensionModule => invoked [" + getName() + "]: " + object + " args: " + arguments[0]);
                try {
                    Object[] newArgs = new Object[arguments.length];
                    for (int i = 0; i < arguments.length; i++) {
                        final Object o = arguments[i];
                        if (o instanceof Closure) {
                            if (Action.class.isAssignableFrom(m.getParameterTypes()[i])) {
                                newArgs[i] = new GroovyActionWrapper((Closure) o);
                            } else if (OnSubscribe.class.isAssignableFrom(m.getParameterTypes()[i])) {
                                newArgs[i] = new GroovyOnSubscribeWrapper((Closure) o);
                            } else {
                                newArgs[i] = new GroovyFunctionWrapper((Closure) o);
                            }
                        } else {
                            newArgs[i] = o;
                        }
                    }

                    return callSites.array[callSiteIndex].call(object, newArgs);
                } catch (Throwable e) {
                    if (e instanceof RuntimeException) {
                        // re-throw whatever was thrown to us
                        throw (RuntimeException) e;
                    } else {
                        throw new RuntimeException(e);
                    }
                }
            }

            @SuppressWarnings("rawtypes")
            @Override
            public CachedClass[] getParameterTypes() {
                Class[] pts = m.getParameterTypes();
                CachedClass[] cc = new CachedClass[pts.length];
                for (int i = 0; i < pts.length; i++) {
                    if (Function.class.isAssignableFrom(pts[i])) {
                        // function type to be replaced by closure
                        cc[i] = ReflectionCache.getCachedClass(Closure.class);
                    } else {
                        // non-function type
                        cc[i] = ReflectionCache.getCachedClass(pts[i]);
                    }
                }
                return cc;
            }
        };
    }

    /**
     * Special case until we finish migrating off the deprecated 'create' method signature
     */
    private MetaMethod specialCasedOverrideForCreate(final Method m) {
        return new MetaMethod() {

            @Override
            public int getModifiers() {
                return m.getModifiers();
            }

            @Override
            public String getName() {
                return m.getName();
            }

            @Override
            public Class<?> getReturnType() {
                return m.getReturnType();
            }

            @Override
            public CachedClass getDeclaringClass() {
                return ReflectionCache.getCachedClass(m.getDeclaringClass());
            }

            @Override
            @SuppressWarnings("unchecked")
            public Object invoke(Object object, final Object[] arguments) {
                return Observable.create(new GroovyCreateWrapper((Closure) arguments[0]));
            }

            @SuppressWarnings("rawtypes")
            @Override
            public CachedClass[] getParameterTypes() {
                Class[] pts = m.getParameterTypes();
                CachedClass[] cc = new CachedClass[pts.length];
                for (int i = 0; i < pts.length; i++) {
                    if (Function.class.isAssignableFrom(pts[i])) {
                        // function type to be replaced by closure
                        cc[i] = ReflectionCache.getCachedClass(Closure.class);
                    } else {
                        // non-function type
                        cc[i] = ReflectionCache.getCachedClass(pts[i]);
                    }
                }
                return cc;
            }
        };
    }

}
