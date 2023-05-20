package sideeffect.project.repository.freeboard;

import java.time.temporal.ChronoUnit;
import java.util.List;
import sideeffect.project.dto.freeboard.FreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardScrollDto;

public interface FreeBoardRepositoryCustom {

    List<FreeBoardResponse> searchScroll(FreeBoardScrollDto scrollDto, Long userId);

    List<FreeBoardResponse> searchScrollWithKeyword(FreeBoardScrollDto scrollDto, Long userId);

    List<FreeBoardResponse> searchRankBoard(Integer size, Integer days, Long userId, ChronoUnit chronoUnit);

}
