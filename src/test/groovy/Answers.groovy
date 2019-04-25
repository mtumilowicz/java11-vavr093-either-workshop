import io.vavr.control.Either
import spock.lang.Specification 
/**
 * Created by mtumilowicz on 2019-04-10.
 */
class Answers extends Specification {
    /*
    static
    	sequence(Iterable<? extends Either<? extends L,? extends R>> eithers)
        sequenceRight(Iterable<? extends Either<? extends L,? extends R>> eithers)
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
}
