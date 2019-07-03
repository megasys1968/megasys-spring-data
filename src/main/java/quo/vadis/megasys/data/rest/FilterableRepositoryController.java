package quo.vadis.megasys.data.rest;

import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import cz.jirutka.rsql.parser.RSQLParser;
import quo.vadis.megasys.data.jpa.JpaSpecificationExecutorRepository;
import quo.vadis.megasys.data.jpa.rsql.CustomRsqlVisitor;

public interface FilterableRepositoryController<T, ID> {
  @SuppressWarnings("rawtypes")
  CustomRsqlVisitor rsqlVisitor = new CustomRsqlVisitor();
  RSQLParser rsqlParser = new RSQLParser();

  JpaSpecificationExecutorRepository<T, ID> getRepository();

  /**
   * 注意! Springで引数のPageableを生成する処理のなかで下記のエラーが発生する。
   * 
   * <pre>
   * java.lang.IllegalArgumentException: 'uriTemplate' must not be null
   *     at org.springframework.util.Assert.hasText(Assert.java:284) ~[spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   *     at org.springframework.web.util.UriTemplate.<init>(UriTemplate.java:64) ~[spring-web-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   *     at org.springframework.data.rest.webmvc.util.UriUtils.findMappingVariable(UriUtils.java:54) ~[spring-data-rest-webmvc-3.1.6.RELEASE.jar:3.1.6.RELEASE]
   *     at org.springframework.data.rest.webmvc.support.DomainClassResolver.resolve(DomainClassResolver.java:61) ~[spring-data-rest-webmvc-3.1.6.RELEASE.jar:3.1.6.RELEASE]
   *     at org.springframework.data.rest.webmvc.json.JacksonMappingAwareSortTranslator.translateSort(JacksonMappingAwareSortTranslator.java:96) ~[spring-data-rest-webmvc-3.1.6.RELEASE.jar:3.1.6.RELEASE]
   *     at org.springframework.data.rest.webmvc.json.MappingAwarePageableArgumentResolver.resolveArgument(MappingAwarePageableArgumentResolver.java:72) ~[spring-data-rest-webmvc-3.1.6.RELEASE.jar:3.1.6.RELEASE]
   *     at org.springframework.data.rest.webmvc.json.MappingAwarePageableArgumentResolver.resolveArgument(MappingAwarePageableArgumentResolver.java:43) ~[spring-data-rest-webmvc-3.1.6.RELEASE.jar:3.1.6.RELEASE]
   *     at org.springframework.web.method.support.HandlerMethodArgumentResolverComposite.resolveArgument(HandlerMethodArgumentResolverComposite.java:126) ~[spring-web-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   *     at org.springframework.web.method.support.InvocableHandlerMethod.getMethodArgumentValues(InvocableHandlerMethod.java:166) ~[spring-web-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   *     at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:134) ~[spring-web-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   *     at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:102) ~[spring-webmvc-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   *     at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:892) ~[spring-webmvc-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   *     at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:797) ~[spring-webmvc-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   *     at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87) ~[spring-webmvc-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   *     at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1038) ~[spring-webmvc-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   * </pre>
   * 
   * これはメソッドが定義されているクラスの@RequestMappingからリポジトリのドメインを特定しているが、 基底である本クラスでは定義できないことに起因する。
   * そこで本メソッドは必ず派生側クラスから呼び出すこと。
   * 
   * @param search
   * @param page
   * @param assembler
   * @return
   */
  default ResponseEntity<?> findAll(@Nullable String search, Pageable page,
      PagedResourcesAssembler<T> assembler) {
    Page<T> result = queryAll(search, page);
    return ResponseEntity.ok(assembler.toResource(result));
  }

  default ResponseEntity<?> findAll(@Nullable String search) {
    List<T> result = null;
    if (StringUtils.isNotBlank(search)) {
      @SuppressWarnings("unchecked")
      Specification<T> spec = rsqlParser.parse(search).accept((CustomRsqlVisitor<T>) rsqlVisitor);
      result = getRepository().findAll(spec);
    } else {
      result = getRepository().findAll();
    }
    Resources<Resource<T>> resources = Resources.wrap(result);
    return ResponseEntity.ok(resources);
  }

  default Page<T> queryAll(@Nullable String search, Pageable page) {
    Page<T> result = null;
    if (StringUtils.isNotBlank(search)) {
      @SuppressWarnings("unchecked")
      Specification<T> spec = rsqlParser.parse(search).accept((CustomRsqlVisitor<T>) rsqlVisitor);
      result = getRepository().findAll(spec, page);
    } else {
      result = getRepository().findAll(page);
    }
    return result;
  }
}
