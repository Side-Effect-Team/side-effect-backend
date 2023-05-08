package sideeffect.project.repository.freeboard;


import java.util.List;
import sideeffect.project.dto.freeboard.FreeBoardResponse;

public interface FreeBoardRepositoryCustom {

    List<FreeBoardResponse> searchScroll(Long lastId, Long userId, Integer size);

    List<FreeBoardResponse> searchScrollWithKeyword(Long lastId, Long userId, String keyword, Integer size);
}
