package swp.group2.swpbe.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import swp.group2.swpbe.AuthService;
import swp.group2.swpbe.exception.ApiRequestException;

@RestController
public class StatisticController {

    @Autowired
    private StatisticService statisticService;
    @Autowired
    private AuthService authService;

    @GetMapping("admin/statistics")
    public StatisticResponse getAdminStatistic(@RequestHeader("Authorization") String token) {
        String userId = authService.loginUser(token);
        if (!authService.isAdmin(userId)) {
            throw new ApiRequestException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        return statisticService.getAdminStatistic();
    }
}
