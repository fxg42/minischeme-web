package minischeme.web.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.Comparator.comparing;
import static com.pivovarit.function.ThrowingPredicate.unchecked;

@RestController
@RequestMapping(path="/examples", produces="application/json")
@lombok.extern.slf4j.Slf4j
public class ExamplesController {

  private static final String ROOT = "static/public/examples";
  
  @GetMapping
  public ResponseEntity<SemanticDropdownResponse> list() throws Exception {
     final var listings = Files.list(Paths.get(getClass().getClassLoader().getSystemResource(ROOT).toURI()))
      .filter(not(Files::isDirectory))
      .filter(not(unchecked(Files::isHidden)))
      .map(ExampleListing::of)
      .sorted(comparing(ExampleListing::getFilename))
      .collect(toList());
    return new ResponseEntity<>(new SemanticDropdownResponse(listings), HttpStatus.OK);
  }
}

@lombok.Data
@lombok.AllArgsConstructor
class ExampleListing {
  private String filename;
  private String location;

  public static ExampleListing of(Path path) {
    final var filename = path.getFileName();
    final var location = path.subpath(path.getNameCount()-3, path.getNameCount());
    return new ExampleListing(filename.toString(), location.toString());
  }
}

@lombok.Data
class SemanticDropdownResponse {
  private Boolean success = true;
  private List<SemanticDropdownResponseItem> results;

  public SemanticDropdownResponse(List<ExampleListing> listings) {
    results = listings.stream().map(SemanticDropdownResponseItem::new).collect(toList());
  }
}

@lombok.Data
class SemanticDropdownResponseItem {
  private String name;
  private String value;
  private String text;
  private Boolean disabled;

  public SemanticDropdownResponseItem(ExampleListing listing) {
    name = listing.getFilename();
    value = "/"+listing.getLocation();
    text = listing.getFilename();
    disabled = false;
  }
}
