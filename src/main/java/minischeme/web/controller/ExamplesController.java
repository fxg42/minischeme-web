package minischeme.web.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.pivovarit.function.ThrowingPredicate.unchecked;
import static java.util.Comparator.comparing;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path="/api/examples", produces="application/json")
@lombok.extern.slf4j.Slf4j
public class ExamplesController {

  @GetMapping("/listing")
  public ResponseEntity<SemanticDropdownResponse> list() throws Exception {
    final var found = Files.list(new ClassPathResource("static/public/examples").getFile().toPath())
      .filter(not(Files::isDirectory))
      .filter(not(unchecked(Files::isHidden)))
      .map(SemanticDropdownResponseItem::new)
      .sorted(comparing(SemanticDropdownResponseItem::getName))
      .collect(toList())
      ;
    return new ResponseEntity<>(new SemanticDropdownResponse(found), HttpStatus.OK);
  }
}

@lombok.Data
class SemanticDropdownResponse {
  private Boolean success = true;
  @lombok.NonNull private List<SemanticDropdownResponseItem> results;
}

@lombok.Data
class SemanticDropdownResponseItem {
  private String name;
  private String value;
  private String text;
  private Boolean disabled;

  public SemanticDropdownResponseItem(Path path) {
    final var filename = path.getFileName().toString();
    final var location = path.subpath(path.getNameCount()-3, path.getNameCount()).toString();
    name = filename;
    value = String.format("/%s", location);
    text = filename;
    disabled = false;
  }
}
