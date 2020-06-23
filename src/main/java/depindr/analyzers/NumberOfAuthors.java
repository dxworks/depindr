package depindr.analyzers;

import depindr.Depinder;
import depindr.model.entity.Author;

import java.util.HashSet;

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
//        authors.forEach(author -> System.out.println("author: " + author.getID().getName()));
        authors.stream().distinct().forEach(author -> System.out.println("author name: " + author.getID().getName() + "    author email: " + author.getID().getEmail()));
        System.out.println("Total number of authors: " + authors.size());
    }
}
