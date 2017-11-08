package io.github.jistol.geosns.service;

import io.github.jistol.geosns.exception.GeoSnsRuntimeException;
import io.github.jistol.geosns.jpa.dao.AttachDao;
import io.github.jistol.geosns.jpa.entry.Attach;
import io.github.jistol.geosns.jpa.entry.User;
import io.github.jistol.geosns.type.BaseProps;
import io.github.jistol.geosns.type.Code;
import io.github.jistol.geosns.util.Crypt;
import io.github.jistol.geosns.util.SessionUtil;
import io.github.jistol.geosns.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

import static io.github.jistol.geosns.util.Cast.string;

@Slf4j
@Service
public class StorageService {
    private static final String anonymous = "anonymous";

    @Autowired private BaseProps baseProps;

    @Autowired @Qualifier("baseCrypt") private Crypt baseCrypt;

    @Autowired private AttachDao attachDao;

    private Path getStoreParentPath(String... pathes) throws IOException {
        Path parentPath = Paths.get(baseProps.getAttachPath(), pathes);
        if (!Files.exists(parentPath)) {
            Files.createDirectories(parentPath);
        }
        return parentPath;
    }

    public Collection<Attach> store(MultipartFile[] files, String bucket, Long key) throws IOException {
        if (files == null || files.length < 1) {
            return new ArrayList<>();
        }

        Collection<Attach> pathList = new ArrayList<>();
        Path parentPath = getStoreParentPath(bucket, key.toString());
        for(MultipartFile uploadedFile : files) {
            final String ext = FilenameUtils.getExtension(uploadedFile.getOriginalFilename());
            final String filename = string(UUID.randomUUID().toString(), ".", ext);
            Path path = Paths.get(parentPath.toAbsolutePath().toString(), filename);
            Attach attach = new Attach();
            attach.setName(uploadedFile.getOriginalFilename());
            attach.setExt(ext);
            attach.setType(MediaType.parseMediaType(uploadedFile.getContentType()));
            attach.setPath(Paths.get(bucket, key.toString(), filename).toString());
            attach.setSize(uploadedFile.getSize());

            uploadedFile.transferTo(path.toFile());
            pathList.add(attach);
        }

        return pathList;
    }

    public String getSignedKey(HttpServletRequest req, Attach attach) throws Exception {
        final User user = SessionUtil.loadUser(req.getSession());
        final String custIp = Util.getClientIpAddr(req);
        final Long timeout = System.currentTimeMillis() + (baseProps.getFileTimeout() * 1000);
        return baseCrypt.encrypt(string(user == null? anonymous : user.getId(), "|", custIp, "|", timeout, "|", attach.getId()));
    }

    public String getSignedKey(HttpServletRequest req, Attach attach, Function<Exception, ? extends RuntimeException> exFun) {
        try {
            return this.getSignedKey(req, attach);
        } catch (Exception e) {
            throw exFun.apply(e);
        }
    }

    public Attach getAttach(HttpServletRequest req, String encKey) throws Exception {
        final User user = SessionUtil.loadUser(req.getSession());
        final String decKey = baseCrypt.decrypt(encKey);
        final String[] keys = decKey.split("[|]");

        if (!Util.equals(user == null? anonymous : Long.toString(user.getId()), keys[0])) {
            throw new GeoSnsRuntimeException(Code.denyFileAccessUser);
        }

        if (!Util.getClientIpAddr(req).equals(keys[1])) {
            throw new GeoSnsRuntimeException(Code.denyFileAccessIp);
        }

        if (Util.calFromNow(Long.parseLong(keys[2])) > baseProps.getFileTimeout()) {
            throw new GeoSnsRuntimeException(Code.denyFileAccessTimeout);
        }

        return attachDao.findOne(Long.parseLong(keys[3]));
    }

    public File load(Attach attach) {
        return Paths.get(baseProps.getAttachPath(), attach.getPath()).toFile();
    }

    public void remove(Attach attach) throws IOException {
        boolean result = Files.deleteIfExists(Paths.get(baseProps.getAttachPath(), attach.getPath()));
        log.debug("delete file - result : {}, id : {}, path : {}", result, attach.getId(), attach.getPath());
    }
}
