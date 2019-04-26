import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by mtumilowicz on 2019-04-26.
 */
public class PersonServiceAnswer {
    static Consumer<Person> updateStats = person -> {
    };
    
    static Function<Person, Either<String, Tuple2<Person, Integer>>> loadStats = person -> {
        switch (person.getId()) {
            case 1:
                return Either.right(Tuple.of(person, 10));
            case 2:
                return Either.right(Tuple.of(person, 20));
            default:
                return Either.left("cannot load stats for person = ${it.id}");
        }
    };

    static Either<String, Person> process(PersonRequest personRequest) {
        return PersonRequestMapper.toPerson(personRequest)
                .peek(updateStats)
                .flatMap(loadStats)
                .filter(tuple -> tuple._2 > 15)
                .getOrElse(Either.left("stats <= 15"))
                .map(Tuple2::_1)
                .map(Person::active)
                .flatMap(PersonRepository::save);
    }
}
