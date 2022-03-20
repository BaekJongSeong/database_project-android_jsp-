package com.server.withme.model;

import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* DTO for Vertex API
* latitude and longitude in vertex
*
* @author Jongseong Baek
*/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VertexDto {
	@NotNull
    private Double latitude;

    @NotNull
    private Double longitude;
}
