package sideeffect.project.repository.freeboard;

import java.util.List;
import sideeffect.project.dto.freeboard.FreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardScrollDto;

public interface FreeBoardRepositoryCustom {

    List<FreeBoardResponse> searchScroll(FreeBoardScrollDto scrollDto, Long userId);

    List<FreeBoardResponse> searchScrollWithKeyword(FreeBoardScrollDto scrollDto, Long userId);

}
