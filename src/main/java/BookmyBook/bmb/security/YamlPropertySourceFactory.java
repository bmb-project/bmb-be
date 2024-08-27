package BookmyBook.bmb.security;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class YamlPropertySourceFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) throws IOException {
        Resource resource = encodedResource.getResource();
        Yaml yaml = new Yaml();
        try (InputStream inputStream = resource.getInputStream()) {
            Map<String, Object> map = yaml.load(inputStream);
            // 기본값으로 "yamlProperties"를 사용하여 이름이 비어있지 않도록 함
            String sourceName = (name != null && !name.isEmpty()) ? name : "yamlProperties";
            return new MapPropertySource(sourceName, map);
        }
    }
}

