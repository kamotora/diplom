package com.diplom.work.core.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
public final class LogFilterDto {
    private String startDate;
    private String finishDate;

    public LocalDateTime getStartDate(){
        if(StringUtils.isEmptyOrWhitespace(startDate))
            return null;
       return LocalDate.parse(startDate).atStartOfDay();
    }

    public LocalDateTime getFinishDate(){
        if(StringUtils.isEmptyOrWhitespace(finishDate))
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogFilterDto that = (LogFilterDto) o;
        return Objects.equals(startDate, that.startDate) &&
                Objects.equals(finishDate, that.finishDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, finishDate);
    }
}
