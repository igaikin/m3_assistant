package io.github.m3_assistant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class CalendarController {

@GetMapping("/calendar")
public String showCalendar(
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month,
        Model model) {

    LocalDate now = LocalDate.now();
    int y = (year != null) ? year : now.getYear();
    int m = (month != null) ? month : now.getMonthValue();

    model.addAttribute("year", y);
    model.addAttribute("month", m);
    model.addAttribute("today", now);

    return "calendar";
}
}