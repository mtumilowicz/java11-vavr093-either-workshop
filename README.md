[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

# java11-vavr093-either-workshop

# project description
* https://www.vavr.io/vavr-docs/#_either
* https://static.javadoc.io/io.vavr/vavr/0.9.3/io/vavr/control/Either.html
* https://github.com/mtumilowicz/java11-vavr-either
* on the workshop we will try to fix failing `Workshop`
* answers: `Answers` (same tests as in `Workshop` but correctly solved)

# theory in a nutshell
* `Either` represents a value of two possible types.
* An `Either` is either a `Left` or a `Right`
* By convention the success case is `Right` and the failure is `Left`
* simple example
    ```
    Either<String, Integer> div(int x, int y) {
        return (y == 0) ? Either.left("cannot div by 0!") : Either.right(x / y);
    }
    ```
* models a computation that may either result in an error (for example returning some error message or even exception), 
or return a successfully computed value
* you can think about `Either` as a pair (Left, Right) ~ (Object, Object) that has either left or right value
* `Try<T>` is isomorphic to `Either<Throwable, T>`
* `Either` is a generalisation of `Try`
* `interface Either<L, R> extends Value<R>, Serializable`
    * `interface Value<T> extends Iterable<T>`
* two implementations:
    * `final class Left<L, R> implements Either<L, R>`
    * `final class Right<L, R> implements Either<L, R>`
    
# conclusions in a nutshell