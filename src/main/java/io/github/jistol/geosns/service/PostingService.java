package io.github.jistol.geosns.service;

import io.github.jistol.geosns.jpa.dao.PostingDao;
import io.github.jistol.geosns.jpa.entry.Posting;
import io.github.jistol.geosns.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Service
public class PostingService {
    @Autowired private StorageService storageService;
    @Autowired private PostingDao postingDao;

    public Posting save(HttpSession httpSession, String message, MultipartFile[] attaches) throws IOException {
        Posting posting = new Posting();
//        posting.setUser(SessionUtil.loadUser(httpSession));
        posting.setMessage(message);
        posting.setAttaches(storageService.store(attaches, "posting", posting.getId()));
        return postingDao.save(posting);
    }
}
