import lombok.Builder;
import lombok.Value;

/**
 * Created by mtumilowicz on 2019-03-03.
 */
@Value
@Builder
class Person {
    int id;
    int age;
}

