import io.vavr.collection.List
import io.vavr.control.Either
import spock.lang.Specification 
/**
 * Created by mtumilowicz on 2019-04-10.
 */
class Answers extends Specification {
    /*
    static
    	sequence(Iterable<? extends Either<? extends L,? extends R>> eithers)
    method
        bimap(Function<? super L,? extends X> leftMapper,
         Function<? super R,? extends Y> rightMapper)
        filter(Predicate<? super R> predicate)
        flatMap(Function<? super R,? extends Either<L,? extends U>> mapper)
        	fold(Function<? super L,? extends U> leftMapper,
    Function<? super R,? extends U> rightMapper)
    get()
    	getLeft()
    		left()
    		map(Function<? super R,? extends U> mapper)
    		mapLeft(Function<? super L,? extends U> leftMapper)
    		orElse(Supplier<? extends Either<? extends L,? extends R>> supplier) 
    		orElseRun(Consumer<? super L> action)
    		peek(Consumer<? super R> action)
    		peekLeft(Consumer<? super L> action) 
    			right()
    				swap()
    				toOption()
    				toTry()
     
     
     
     */

    def "create successful (Right) Either with value 1"() {
        given:
        Either<String, Integer> success = Either.right(1)

        expect:
        success.get() == 1
    }

    def "create failed (Left) Either with message: 'wrong status'"() {
        given:
        Either<String, Integer> fail = Either.left('wrong status')

        expect:
        fail.left == 'wrong status'
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
        sum.get() == 10
        and:
        fail.left == 'cannot parse integer a'
    }
}
