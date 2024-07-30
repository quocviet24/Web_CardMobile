package com.nishikatakagi.ProductDigital.controller.admin;

import com.nishikatakagi.ProductDigital.dto.statistic.OrderByDayDTO;
import com.nishikatakagi.ProductDigital.dto.statistic.TotalMoneyByMonthDTO;
import com.nishikatakagi.ProductDigital.dto.statistic.UserStatDTO;
import com.nishikatakagi.ProductDigital.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/statistic")
public class StatisticController {
    private static final Logger log = LoggerFactory.getLogger(StatisticController.class);
    @Autowired
    OrderService orderService;

    @GetMapping("")
    public String showStatistic(Model model) {
        DateRange dateRange = new DateRange();

        List<OrderByDayDTO> ordersByDay = orderService.getOrdersByDayInCurrentWeek();
        model.addAttribute("ordersByDay", ordersByDay);

        List<TotalMoneyByMonthDTO> totalMoney = orderService.getTotalMoneyByMonthAndYear(2024);
        model.addAttribute("totalMoney", totalMoney);


        List<UserStatDTO> top10userOfAllTime = orderService.getTopUsersWithLargestTotalMoney();
        dateRange = getStartAndEndDateOfWeek();
        List<UserStatDTO> top10userOfWeek = orderService.getTopUsersWithLargestTotalMoney(dateRange.startDate, dateRange.endDate);
//        for (UserStatDTO userStatDTO : top10userOfWeek) {
//            log.info(userStatDTO.toString());
//        }

        dateRange = getStartAndEndDateOfMonth();
        List<UserStatDTO> top10userOfMonth = orderService.getTopUsersWithLargestTotalMoney(dateRange.startDate, dateRange.endDate);
//        for (UserStatDTO userStatDTO : top10userOfMonth) {
//            log.info(userStatDTO.toString());
//        }

        dateRange = getStartAndEndDateOfYear();
        List<UserStatDTO> top10userOfYear = orderService.getTopUsersWithLargestTotalMoney(dateRange.startDate, dateRange.endDate);
//        for (UserStatDTO userStatDTO : top10userOfYear) {
//            log.info(userStatDTO.toString());
//        }
        model.addAttribute("year", 2024);
        model.addAttribute("top10user", top10userOfAllTime);
        model.addAttribute("top10userOfWeek", top10userOfWeek);
        model.addAttribute("top10userOfMonth", top10userOfMonth);
        model.addAttribute("top10userOfYear", top10userOfYear);
        return "pages/statistic/view.html";
    }

    @GetMapping("/revenueByYear")
    public String showStatisticBYYear(Model model, @RequestParam(value = "year", required = false) String yearString) {
        int year = Integer.parseInt(yearString);
        log.info("year: {}", year);
        model.addAttribute("year", year);

        DateRange dateRange = new DateRange();

        List<OrderByDayDTO> ordersByDay = orderService.getOrdersByDayInCurrentWeek();
        model.addAttribute("ordersByDay", ordersByDay);

        List<TotalMoneyByMonthDTO> totalMoney = orderService.getTotalMoneyByMonthAndYear(year);
        model.addAttribute("totalMoney", totalMoney);


        List<UserStatDTO> top10userOfAllTime = orderService.getTopUsersWithLargestTotalMoney();
        dateRange = getStartAndEndDateOfWeek();
        List<UserStatDTO> top10userOfWeek = orderService.getTopUsersWithLargestTotalMoney(dateRange.startDate, dateRange.endDate);
//        for (UserStatDTO userStatDTO : top10userOfWeek) {
//            log.info(userStatDTO.toString());
//        }

        dateRange = getStartAndEndDateOfMonth();
        List<UserStatDTO> top10userOfMonth = orderService.getTopUsersWithLargestTotalMoney(dateRange.startDate, dateRange.endDate);
//        for (UserStatDTO userStatDTO : top10userOfMonth) {
//            log.info(userStatDTO.toString());
//        }

        dateRange = getStartAndEndDateOfYear();
        List<UserStatDTO> top10userOfYear = orderService.getTopUsersWithLargestTotalMoney(dateRange.startDate, dateRange.endDate);
//        for (UserStatDTO userStatDTO : top10userOfYear) {
//            log.info(userStatDTO.toString());
//        }

        model.addAttribute("top10user", top10userOfAllTime);
        model.addAttribute("top10userOfWeek", top10userOfWeek);
        model.addAttribute("top10userOfMonth", top10userOfMonth);
        model.addAttribute("top10userOfYear", top10userOfYear);
        return "pages/statistic/view.html";
    }

    public DateRange getStartAndEndDateOfYear() {
        Calendar calendar = Calendar.getInstance();

        // Set the calendar to the first day of the year
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        Date startDate = calendar.getTime();

        // Set the calendar to the last day of the year
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        Date endDate = calendar.getTime();

        return new DateRange(startDate, endDate);
    }

    public DateRange getStartAndEndDateOfMonth() {
        Calendar calendar = Calendar.getInstance();

        // Set the calendar to the first day of the month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();

        // Set the calendar to the last day of the month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = calendar.getTime();

        return new DateRange(startDate, endDate);
    }

    public DateRange getStartAndEndDateOfWeek() {
        Calendar calendar = Calendar.getInstance();

        // Set the calendar to the first day of the week (Sunday/Monday depending on locale)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        Date startDate = calendar.getTime();

        // Set the calendar to the last day of the week (Saturday/Sunday depending on locale)
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        Date endDate = calendar.getTime();

        return new DateRange(startDate, endDate);
    }

    public class DateRange {
        private Date startDate;
        private Date endDate;

        public DateRange() {
        }

        public DateRange(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
        }
    }
}
