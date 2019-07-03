package quo.vadis.megasys.data;

import org.springframework.data.jpa.repository.JpaRepository;
import quo.vadis.megasys.data.jpa.JpaSpecificationExecutorRepository;

public interface UserRepository extends JpaSpecificationExecutorRepository<User, Integer> {

}
