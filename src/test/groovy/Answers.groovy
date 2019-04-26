import io.vavr.collection.HashMap
import io.vavr.collection.List
import io.vavr.collection.Map
import io.vavr.collection.Seq
import io.vavr.control.Either
import io.vavr.control.Option
import io.vavr.control.Try
import spock.lang.Specification

import java.time.Month
import java.util.function.Function
import java.util.stream.Collectors

/**
 * Created by mtumilowicz on 2019-04-10.
 */
class Answers extends Specification {
    /*
    method
    		left()
    		right()
    		orElse(Supplier<? extends Either<? extends L,? extends R>> supplier) 
    		orElseRun(Consumer<? super L> action) // log (function log(str) logfile = str)
    		peek(Consumer<? super R> action) // log
    		peekLeft(Consumer<? super L> action) // log
    				swap()   
     */

    def "create successful (Right) Either with value 1"() {
        given:
        Either<String, Integer> success = Either.right(1)

        expect:
        success.isRight()
        success.get() == 1
    }

    def "create failed (Left) Either with message: 'wrong status'"() {
        given:
        Either<String, Integer> fail = Either.left('wrong status')

        expect:
        fail.isLeft()
        fail.getLeft() == 'wrong status'
    }

    def "sum values of right eithers sequence or return the first failure"() {
        given:
        Either<String, Integer> n1 = Either.right(1)
        Either<String, Integer> n2 = Either.right(2)
        Either<String, Integer> n3 = Either.right(3)
        Either<String, Integer> n4 = Either.right(4)
        Either<String, Integer> failure1 = Either.left('cannot parse integer a')
        Either<String, Integer> failure2 = Either.left('cannot parse integer b')

        and:
        List<Either<String, Integer>> from1To4 = List.of(n1, n2, n3, n4)
        List<Either<String, Integer>> all = List.of(n1, n2, n3, n4, failure1, failure2)

        when:
        Either<String, Number> sum = Either.sequenceRight(from1To4)
                .map({ it.sum() })
        Either<String, Number> fail = Either.sequenceRight(all)
                .map({ it.sum() })

        then:
        sum.isRight()
        sum.get() == 10
        and:
        fail.isLeft()
        fail.getLeft() == 'cannot parse integer a'
    }

    def "count average expenses in a year (by month) or aggregate failures"() {
        given:
        def spendingByMonth = {
            Either.right(it.getValue())
        }

        and:
        def spendingByMonthExceptional = {
            switch (it) {
                case Month.MARCH:
                    Either.left('Expenses in March cannot be loaded.')
                    break
                case Month.APRIL:
                    Either.left('Expenses in April cannot be loaded.')
                    break
                default: Either.right(it.getValue())
            }
        }

        and:
        Function<Function<Month, Integer>, Map<Month, Either<String, Integer>>> expensesByMonthMap = {
            spendingIn ->
                HashMap.ofAll(Arrays.stream(Month.values())
                        .collect(Collectors.toMap(
                                Function.identity(),
                                { month -> spendingIn(month) })))
        }

        when:
        Seq<Either<String, Integer>> withoutFailure = expensesByMonthMap.apply(spendingByMonth).values()
        Seq<Either<String, Integer>> withFailures = expensesByMonthMap.apply(spendingByMonthExceptional).values()

        and:
        Either<Seq<String>, Option<Double>> average = Either.sequence(withoutFailure)
                .map({ it.average() })
        Either<Seq<String>, Option<Double>> failures = Either.sequence(withFailures)
                .map({ it.average() })

        then:
        average.isRight()
        average.get() == Option.some(6.5D)
        and:
        failures.isLeft()
        failures.getLeft().size() == 2
        failures.getLeft().containsAll('Expenses in March cannot be loaded.', 'Expenses in April cannot be loaded.')
    }

    def "square the left side or cube the right side"() {
        given:
        Either<Integer, Integer> left = Either.left(2)
        Either<Integer, Integer> right = Either.right(3)
        and:
        Function<Integer, Integer> square = { it**2 }
        Function<Integer, Integer> cube = { it**3 }

        when:
        Either<Integer, Integer> leftBimapped = left.bimap(square, cube)
        Either<Integer, Integer> rightBimapped = right.bimap(square, cube)

        then:
        leftBimapped.isLeft()
        leftBimapped.getLeft() == 4
        and:
        rightBimapped.isRight()
        rightBimapped.get() == 27
    }

    def "square and get the left side or cube and get the right side"() {
        given:
        Either<Integer, Integer> left = Either.left(2)
        Either<Integer, Integer> right = Either.right(3)
        and:
        Function<Integer, Integer> square = { it**2 }
        Function<Integer, Integer> cube = { it**3 }

        when:
        Integer leftFolded = left.fold(square, cube)
        Integer rightFolded = right.fold(square, cube)

        then:
        leftFolded == 4
        and:
        rightFolded == 27
    }

    def "square root"() {
        given:
        Either<String, Integer> negative = Either.right(-1)
        Either<String, Integer> positive = Either.right(1)
        Either<String, Integer> failure = Either.left("no data")

        and:
        Function<Either<String, Integer>, Option<Either<String, Double>>> square = {
            it.filter({ it >= 0 })
                    .map({ it.map({ Math.sqrt(it) }) })
        }

        expect:
        square.apply(negative).empty
        square.apply(positive).get().get() == 1D
        square.apply(failure).get().getLeft() == 'no data'
    }

    def "get person from database, and then estimate income wrapped with Either"() {
        given:
        Function<Person, Either<String, Integer>> estimateIncome = {
            switch (it.id) {
                case 1:
                    return Either.left("cannot estimate income for person = ${it.id}")
                default:
                    return Either.right(30)
            }
        }
        and:
        def personWithIncome = 2
        def personWithoutIncome = 1

        when:
        Either<String, Integer> withIncome = PersonRepository.findById(personWithIncome)
                .flatMap({ estimateIncome.apply(it) })
        Either<String, Integer> withoutIncome = PersonRepository.findById(personWithoutIncome)
                .flatMap({ estimateIncome.apply(it) })

        then:
        withIncome.isRight()
        withIncome.get() == 30
        withoutIncome.isLeft()
        withoutIncome.getLeft() == "cannot estimate income for person = ${personWithoutIncome}"
    }

    def "square right side"() {
        given:
        Either<Integer, Integer> right = Either.right(2)
        Either<Integer, Integer> left = Either.left(2)

        def square = { it**2 }

        when:
        def rightSquared = right.map(square)
        def leftUntouched = left.map(square)

        then:
        rightSquared.right()
        rightSquared.get() == 4
        leftUntouched.left()
        leftUntouched.getLeft() == 2
    }

    def "square left side"() {
        given:
        Either<Integer, Integer> right = Either.right(2)
        Either<Integer, Integer> left = Either.left(2)

        def square = { it**2 }

        when:
        def rightUntouched = right.mapLeft(square)
        def leftSquared = left.mapLeft(square)

        then:
        rightUntouched.right()
        rightUntouched.get() == 2
        leftSquared.left()
        leftSquared.getLeft() == 4
    }

    def "Option -> Either"() {
        given:
        Option<Integer> some = Option.some(1)
        Option<Integer> none = Option.none()
        def message = 'option was empty'

        when:
        Either<String, Integer> eitherFromSome = some.toEither(message)
        Either<String, Integer> eitherFromNone = none.toEither(message)

        then:
        eitherFromSome.isRight()
        eitherFromSome.get() == 1
        eitherFromNone.isLeft()
        eitherFromNone.getLeft() == message
    }

    def "Either -> Option"() {
        given:
        Either<String, Integer> right = Either.right(1)
        Either<String, Integer> left = Either.left('no data')

        when:
        def optionFromRight = right.toOption()
        def optionFromLeft = left.toOption()

        then:
        optionFromRight.defined
        optionFromRight.get() == 1
        optionFromLeft.empty
    }

    def "Try -> Either"() {
        given:
        def exception = new IllegalArgumentException("a")
        Try<Integer> success = Try.success(1)
        Try<Integer> failure = Try.failure(exception)

        when:
        Either<Throwable, Integer> eitherFromSuccess = success.toEither()
        Either<Throwable, Integer> eitherFromFailure = failure.toEither()

        then:
        eitherFromSuccess.isRight()
        eitherFromSuccess.get() == 1
        eitherFromFailure.isLeft()
        eitherFromFailure.getLeft() == exception
    }

    def "Either -> Try"() {
        given:
        Either<String, Integer> right = Either.right(1)
        Either<String, Integer> left = Either.left('no data')

        when:
        Try<Integer> tryFromRight = right.toTry()
        Try<Integer> tryFromLeft = left.toTry()

        then:
        tryFromRight.success
        tryFromRight.get() == 1
        tryFromLeft.failure
        tryFromLeft.cause.class == NoSuchElementException
        tryFromLeft.cause.message == 'get() on Left'
    }
}
