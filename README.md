# RxGroovy: Reactive Extensions for Groovy

This is a Groovy adapter to [RxJava](http://github.com/ReactiveX/RxJava).

This adaptor allows 'groovy.lang.Closure' functions to be used and RxJava will know how to invoke them.

This enables code such as:

```groovy
  Observable.just("one", "two", "three")
    .take(2) 
    .subscribe { arg -> println arg }
```

## Master Build Status

<a href='https://travis-ci.org/ReactiveX/RxGroovy/builds'><img src='https://travis-ci.org/ReactiveX/RxGroovy.svg?branch=1.x'></a>

## Communication

Since RxGroovy is part of the RxJava family the communication channels are similar:

- Google Group: [RxJava](http://groups.google.com/d/forum/rxjava)
- Twitter: [@RxJava](http://twitter.com/RxJava)
- [GitHub Issues](https://github.com/ReactiveX/RxGroovy/issues)

## Versioning

RxGroovy 1.0.x is based on RxJava 1.0.x. As of 1.0.0 semantic versioning will be used.

## Full Documentation

RxJava:

- [Wiki](https://github.com/ReactiveX/RxJava/wiki)
- [Javadoc](http://reactivex.io/RxJava/javadoc/)

## Binaries

For version 1.x:

Binaries and dependency information for Maven, Ivy, Gradle and others can be found at [https://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.reactivex%22%20AND%20a%3A%22rxgroovy%22).

Example for Maven:

```xml
<dependency>
    <groupId>io.reactivex</groupId>
    <artifactId>rxgroovy</artifactId>
    <version>x.y.z</version>
</dependency>
```

and for Ivy:

```xml
<dependency org="io.reactivex" name="rxgroovy" rev="x.y.z" />
```

and for Gradle:

```groovy
compile 'io.reactivex:rxgroovy:x.y.z'
```

## Build

To build:

```
$ git clone git@github.com:ReactiveX/RxGroovy.git
$ cd RxGroovy/
$ ./gradlew build
```

Futher details on building can be found on the RxJava [Getting Started](https://github.com/ReactiveX/RxJava/wiki/Getting-Started) page of the wiki.

## Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/ReactiveX/RxGroovy/issues).


## LICENSE

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

