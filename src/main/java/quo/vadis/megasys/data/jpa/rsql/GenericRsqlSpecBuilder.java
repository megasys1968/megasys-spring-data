package quo.vadis.megasys.data.jpa.rsql;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.concurrent.ThreadSafe;
import org.springframework.data.jpa.domain.Specification;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.LogicalOperator;
import cz.jirutka.rsql.parser.ast.Node;

@ThreadSafe
class GenericRsqlSpecBuilder<T> {

  private Specification<T> createSpecification(Node node) {
    if (node instanceof LogicalNode) {
      return createSpecification((LogicalNode) node);
    }
    if (node instanceof ComparisonNode) {
      return createSpecification((ComparisonNode) node);
    }
    return null;
  }

  Specification<T> createSpecification(LogicalNode logicalNode) {
    List<Specification> specs =
        logicalNode.getChildren().stream().map(this::createSpecification)
            .filter(Objects::nonNull).collect(Collectors.toList());

    Specification<T> result = specs.get(0);
    if (logicalNode.getOperator() == LogicalOperator.AND) {
      for (int i = 1; i < specs.size(); i++) {
        result = Specification.where(result).and(specs.get(i));
      }
    } else if (logicalNode.getOperator() == LogicalOperator.OR) {
      for (int i = 1; i < specs.size(); i++) {
        result = Specification.where(result).or(specs.get(i));
      }
    }

    return result;
  }

  Specification<T> createSpecification(ComparisonNode comparisonNode) {
    return Specification.where(new GenericRsqlSpecification<T>(comparisonNode.getSelector(),
            comparisonNode.getOperator(), comparisonNode.getArguments()));
  }
}
