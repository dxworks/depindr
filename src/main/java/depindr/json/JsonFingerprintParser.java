package depindr.json;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import depindr.model.entity.Dependency;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JsonFingerprintParser {

    public List<Dependency> parseTechnologiesFile(String filePath) {
        JsonConfigurationDTO configurationDTO = getConfigurationDTO(filePath);
        if (configurationDTO == null)
            return Collections.emptyList();

        return configurationDTO.getTechnologies().stream()
                .map(DependencyJsonDTO::toDependency)
                .collect(Collectors.toList());
    }


    public JsonConfigurationDTO getConfigurationDTO(String filePath){
        Gson gson = new Gson();

        try {
            return gson.fromJson(new FileReader(Paths.get(filePath).toFile()), JsonConfigurationDTO.class);
        } catch (FileNotFoundException e) {
            log.error("Could not read JSON file!", e);
        } catch (
        JsonSyntaxException e) {
            log.error("File " + filePath + " is could not be parsed as a JSON Technology file!", e);
        }

        return null;
    }
}
