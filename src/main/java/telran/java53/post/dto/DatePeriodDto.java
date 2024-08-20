package telran.java53.post.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DatePeriodDto {
	LocalDate dateFrom;
    LocalDate dateTo;
}
