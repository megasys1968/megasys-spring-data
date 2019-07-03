package quo.vadis.megasys.data.jpa.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import javax.annotation.Nullable;
import lombok.Getter;

@Getter
public enum RsqlSearchOperation {
  // @formatter:off
  EQUAL(RSQLOperators.EQUAL),
  NOT_EQUAL(RSQLOperators.NOT_EQUAL),
  GREATER_THAN(RSQLOperators.GREATER_THAN),
  GREATER_THAN_OR_EQUAL(RSQLOperators.GREATER_THAN_OR_EQUAL),
  LESS_THAN(RSQLOperators.LESS_THAN),
  LESS_THAN_OR_EQUAL(RSQLOperators.LESS_THAN_OR_EQUAL),
  IN(RSQLOperators.IN),
  NOT_IN(RSQLOperators.NOT_IN); 
  // @formatter:on

  private ComparisonOperator operator;

  RsqlSearchOperation(ComparisonOperator operator) {
    this.operator = operator;
  }

  public static @Nullable RsqlSearchOperation getSimpleOperator(ComparisonOperator operator) {
    for (RsqlSearchOperation operation : values()) {
      if (operation.getOperator() == operator) {
        return operation;
      }
    }
    return null;
  }
}
