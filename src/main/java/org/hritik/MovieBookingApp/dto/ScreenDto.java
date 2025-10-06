package org.hritik.MovieBookingApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreenDto {

    private Long Id;
    private String name;
    private Integer totalSeats;
    private TheaterDto theater;
}
