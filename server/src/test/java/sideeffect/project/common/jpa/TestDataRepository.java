package sideeffect.project.common.jpa;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(TestQuerydslConfiguration.class)
public class TestDataRepository {

}
