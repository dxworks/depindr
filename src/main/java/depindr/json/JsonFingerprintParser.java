package depindr.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
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


    public void writeTechnologiesToFile(List<Dependency> technologies, Path filePath) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileWriter writer = new FileWriter(filePath.toFile());
            List<DependencyJsonDTO> technologyJsonDTOS = technologies.stream().map(DependencyJsonDTO::fromDependency).collect(Collectors.toList());
            JsonConfigurationDTO jsonConfigurationDTO = new JsonConfigurationDTO();
            jsonConfigurationDTO.setTechnologies(technologyJsonDTOS);
            gson.toJson(jsonConfigurationDTO, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log.error("Could not write JSON file!", e);
            throw e;
        }
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
