package com.diplom.work.core.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public final class LogFilterDto {
    private String startDate;
    private String finishDate;

    public LocalDateTime getStartDate() {
        if (StringUtils.isEmptyOrWhitespace(startDate))
            return null;
        return LocalDate.parse(startDate).atStartOfDay();
    }

    public LocalDateTime getFinishDate() {
        if (StringUtils.isEmptyOrWhitespace(finishDate))
            return null;
        return LocalDate.parse(finishDate).atStartOfDay();
    }

    @JsonSetter("startDate")
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @JsonSetter("finishDate")
    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }
}
