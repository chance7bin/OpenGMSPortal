package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/08
 */
public interface NoticeDao extends MongoRepository<Notice,String> {
    int countByRecipient(String recipient);

    int countByRecipientAndHasRead(String recipient, boolean hasRead);

    Page<Notice> findAllByRecipient(String email, Pageable pageable);

    List<Notice> findAllByRecipientAndHasRead(String email, boolean hasRead);

    Notice findFirstByRecipient(String email);

    Notice findFirstById(String noticeId);


}
