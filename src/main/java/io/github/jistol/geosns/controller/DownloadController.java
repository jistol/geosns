package io.github.jistol.geosns.controller;

import com.google.common.io.Files;
import io.github.jistol.geosns.jpa.entry.Attach;
import io.github.jistol.geosns.service.StorageService;
import io.github.jistol.geosns.type.BaseProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import java.io.File;

import static io.github.jistol.geosns.util.Cast.string;

@Controller
@RequestMapping("/download")
public class DownloadController {
    @Autowired private BaseProps baseProps;
    @Autowired private StorageService storageService;

    @ResponseBody
    @GetMapping("/post/{key}")
    public ResponseEntity<byte[]> serveFile(HttpServletRequest req, @PathVariable("key") String key) throws Exception {
        Attach attach = storageService.getAttach(req, key);
        File file = storageService.load(attach);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + file.getName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, new MimetypesFileTypeMap().getContentType(file.getName()))
                .header(HttpHeaders.CONTENT_LENGTH, Long.toString(file.length()))
                .header(HttpHeaders.CACHE_CONTROL, string("max-age=", baseProps.getFileTimeout()))
                .body(Files.toByteArray(file));
    }

}
