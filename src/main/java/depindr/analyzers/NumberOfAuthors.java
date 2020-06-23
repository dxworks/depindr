package depindr.analyzers;

import depindr.Depinder;
import depindr.DepinderResult;
import depindr.model.dto.AuthorDependencyKnowledgeDTO;
import depindr.model.entity.Author;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NumberOfAuthors implements DepinderCommand {
    @Override
    public boolean parse(String[] args) {
        return args.length == 3;
    }

    @Override
    public String usage() {
        return "depinder --authors <json_output_name_for_results> <flag_for_removal_of_comments>";
    }

    @Override
    public void execute(Depinder depinder, String[] args) {
        HashSet<Author> authors = new HashSet<>(depinder.getAuthorRegistry().getAll());
        List<AuthorDependencyKnowledgeDTO> authorDependencyKnowledgeDTOS = authors.stream().map(author ->
                AuthorDependencyKnowledgeDTO.builder()
                        .authorName(author.getID().getName())
                        .authorEmail(author.getID().getEmail())
                        .dependencyKnowledge(getAuthorKnowledge(author))
                        .build()
        ).collect(Collectors.toList());

//        authors.forEach(author -> System.out.println("author: " + author.getID().getName()));
        authors.stream().distinct().forEach(author -> System.out.println("author name: " + author.getID().getName() + "    author email: " + author.getID().getEmail()));
        System.out.println("Total number of authors: " + authors.size());
    }

    private Map<String, Integer> getAuthorKnowledge(Author author) {
        return author.getResults().stream()
                .collect(Collectors.groupingBy(dr -> dr.getDependency().getID(), Collectors.summingInt(DepinderResult::getValue)));
    }
}
