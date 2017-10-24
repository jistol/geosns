package io.github.jistol.geosns.service;

import io.github.jistol.geosns.jpa.entry.Attach;
import io.github.jistol.geosns.jpa.entry.Posting;
import io.github.jistol.geosns.type.BaseProps;
import io.github.jistol.geosns.util.Util;
import org.apache.commons.io.FilenameUtils;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static io.github.jistol.geosns.util.Cast.msgFormat;
import static io.github.jistol.geosns.util.Cast.string;

@Service
public class StorageService {
    @Autowired private BaseProps baseProps;

    private Path getStoreParentPath(String path) throws IOException {
        Path parentPath = Paths.get(msgFormat(baseProps.getPath(), path));
        if (!Files.exists(parentPath)) {
            Files.createDirectories(parentPath);
        }
        return parentPath;
    }

    public Collection<Attach> store(MultipartFile[] files, String rootPath, long uniqueId) throws IOException {
        if (files == null || files.length < 1) {
            return null;
        }

        Collection<Attach> pathList = new ArrayList<>();
        Path parentPath = getStoreParentPath(string(rootPath, File.pathSeparator, uniqueId));
        for(MultipartFile uploadedFile : files) {
            File file = Paths.get(parentPath.toAbsolutePath().toString(), UUID.randomUUID().toString()).toFile();
            uploadedFile.transferTo(file);
            Attach attach = new Attach();
            attach.setName(uploadedFile.getOriginalFilename());
            attach.setExt(FilenameUtils.getExtension(uploadedFile.getOriginalFilename()));
            attach.setType(MediaType.parseMediaType(uploadedFile.getContentType()));
            attach.setPath(uploadedFile.getOriginalFilename());
            attach.setSize(file.length());
            pathList.add(attach);
        }

        return pathList;
    }
}
