import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Created by mtumilowicz on 2019-03-03.
 */
@Value
@Wither
@Builder
class Person {
    int id;
    int age;
    boolean active;
    
    Person active() {
        return this.withActive(true);
    }
}

