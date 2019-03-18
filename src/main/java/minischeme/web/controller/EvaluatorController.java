package minischeme.web.controller;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
public class EvaluatorController {

  @GetMapping
  public String show(final Model model) {
    if (! model.containsAttribute("command")) {
      final var command = new RunCommand();
      command.setSource("(+ 1 2)");
      model.addAttribute("command", command);
    }
    return "show";
  }

  @PostMapping
  public String run(
        @Valid final RunCommand command
      , final BindingResult bindingResult
      , final RedirectAttributes redirectAttributes
    ) {

    if (bindingResult.hasErrors()) {
      redirectAttributes
        .addFlashAttribute("flash_error", "run_invalid_command_flash_error_message")
        .addFlashAttribute("command", command)
        ;
      return "redirect:/";
    }

    try {
      final var source = command.getSource();
      final var evaluationResult = "À IMPLÉMENTER 🤔";
      command.setResult(evaluationResult);

      redirectAttributes
        .addFlashAttribute("flash_success", "run_evaluation_flash_success_message")
        .addFlashAttribute("command", command)
        ;
    } catch (Exception e) {
      redirectAttributes
        .addFlashAttribute("flash_error", "run_evaluation_flash_error_message")
        .addFlashAttribute("command", command)
        ;
    }

    return "redirect:/";
  }
}

@lombok.Data
@lombok.NoArgsConstructor
class RunCommand implements Serializable {

  @NotBlank @NotNull private String source = "";
  @NotNull private String result = "";
}
