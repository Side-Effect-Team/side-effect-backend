package sideeffect.project.repository.freeboard;


import java.util.List;
import sideeffect.project.dto.freeboard.FreeBoardResponse;

public interface FreeBoardRepositoryCustom {

    List<FreeBoardResponse> searchScroll(Long lastId, Long userId, int size);

    List<FreeBoardResponse> searchScrollWithKeyword(Long lastId, Long userId, String keyword, int size);
}
