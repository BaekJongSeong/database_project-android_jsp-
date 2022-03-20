package com.server.withme.model;

import java.util.UUID;

import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for SafeZone Vertices
 *
 * @author Jongseong Baek
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SafeZoneDto {

	@NotNull
	private VertexDto leftTop;
	
	@NotNull
	private VertexDto rightTop;
	
	@NotNull
	private VertexDto rightDown;
	
	@NotNull
	private VertexDto leftDown;
}
