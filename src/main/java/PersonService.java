import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by mtumilowicz on 2019-04-26.
 */
public class PersonService {
    static Consumer<Person> updateStats = person -> {
    };

    static Function<Person, Either<String, Tuple2<Person, Integer>>> loadStats = person -> {
        switch (person.getId()) {
            case 1:
                return Either.right(Tuple.of(person, 10));
            case 2:
                return Either.right(Tuple.of(person, 20));
            default:
                return Either.left("cannot load stats for person = " + person.getId());
        }
    };

    static Either<String, Person> process(Person person) {
        return null; // loadStats.apply(person)
        // filter
        // getOrElse
        // map
        // map, Person.activate
        // flatMap, PersonRepository.save
    }
}
