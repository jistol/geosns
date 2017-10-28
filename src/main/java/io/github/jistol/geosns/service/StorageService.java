package io.github.jistol.geosns.service;

import io.github.jistol.geosns.jpa.entry.Attach;
import io.github.jistol.geosns.type.BaseProps;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static io.github.jistol.geosns.util.Cast.string;

@Service
public class StorageService {
    @Autowired private BaseProps baseProps;

    private Path getStoreParentPath(String... pathes) throws IOException {
        Path parentPath = Paths.get(baseProps.getAttachPath(), pathes);
        if (!Files.exists(parentPath)) {
            Files.createDirectories(parentPath);
        }
        return parentPath;
    }

    public Collection<Attach> store(MultipartFile[] files, String category, Long uniqueId) throws IOException {
        if (files == null || files.length < 1) {
            return null;
        }

        Collection<Attach> pathList = new ArrayList<>();
        Path parentPath = getStoreParentPath(category, uniqueId.toString());
        for(MultipartFile uploadedFile : files) {
            final String ext = FilenameUtils.getExtension(uploadedFile.getOriginalFilename());
            final String filename = string(UUID.randomUUID().toString(), ".", ext);
            Path path = Paths.get(parentPath.toAbsolutePath().toString(), filename);
            Attach attach = new Attach();
            attach.setName(uploadedFile.getOriginalFilename());
            attach.setExt(ext);
            attach.setType(MediaType.parseMediaType(uploadedFile.getContentType()));
            attach.setPath(path.toAbsolutePath().toString());
            attach.setSize(uploadedFile.getSize());

            uploadedFile.transferTo(path.toFile());
            pathList.add(attach);
        }

        return pathList;
    }
}
