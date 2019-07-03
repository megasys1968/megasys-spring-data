package quo.vadis.megasys.data.jpa.rsql;
import cz.jirutka.rsql.parser.RSQLParser;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import quo.vadis.megasys.data.TestAppConfig;
import quo.vadis.megasys.data.User;
import quo.vadis.megasys.data.UserRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestAppConfig.class)
class RsqlTest {
  @Autowired
  private UserRepository userRepo;

  @Test
  void test() {
    userRepo.deleteAll();
    userRepo.save(new User(null, "アムロ・レイ", 15));
    userRepo.save(new User(null, "シャア・アズナブル", 20));
    userRepo.save(new User(null, "ブライト・ノア", 19));

    CustomRsqlVisitor<User> rsqlVisitor = new CustomRsqlVisitor();
    RSQLParser rsqlParser = new RSQLParser();

    Specification<User> spec = rsqlParser.parse("name==シャア*,age=in=(15,19)").accept(rsqlVisitor);
    List<User> result = userRepo.findAll(spec);
    Assertions.assertEquals(3, result.size());


    spec = rsqlParser.parse("name==*イ*;age=in=(15)").accept(rsqlVisitor);
    result = userRepo.findAll(spec);
    Assertions.assertEquals(1, result.size());



  }

}
