package com.server.withme.serivce.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.server.withme.entity.AccountOption;
import com.server.withme.entity.InitSafeZone;
import com.server.withme.entity.SafeZone;
import com.server.withme.entity.TTL;
import com.server.withme.model.InitSafeZoneDto;
import com.server.withme.model.VertexDto;
import com.server.withme.repository.InitSafeZoneRepository;
import com.server.withme.repository.SafeZoneRepository;
import com.server.withme.repository.TTLRepository;
import com.server.withme.serivce.IAccountOptionService;
import com.server.withme.serivce.ISafeZoneService;
import com.server.withme.util.IVertexUtil;

import lombok.RequiredArgsConstructor;
/**
 * Service for SafeZone
 *
 * @author Jongseong Baek
 */

@RequiredArgsConstructor
@Service
public class SafeZoneService implements ISafeZoneService{
	
	private final IAccountOptionService accountOptionService;
	
	private final InitSafeZoneRepository initSafeZoneRepository;
	
	private final TTLRepository ttlRepository;
	
	private final SafeZoneRepository safeZoneRepository;
	
	private final IVertexUtil vertexUtil;
	
	@Override
	public Boolean saveInitSafeZone(InitSafeZoneDto initSafeZoneDto, UUID accountId) {
		
		List<VertexDto> initSafeZoneList = initSafeZoneDto.getInitSafeZone();
		Map<String,String> result = vertexUtil.checkSafeZoneMinSize(initSafeZoneList);
		if(result.get("result").equals("false"))
			return false;
		
		AccountOption accountOption = accountOptionService.updateAccountOption(
				accountId,Double.valueOf(result.get("latitude")),Double.valueOf(result.get("longitude")));
		
		for(VertexDto vertex: initSafeZoneList) {
			initSafeZoneRepository.save(InitSafeZone.builder()
					.latitude(vertex.getLatitude())
					.longitude(vertex.getLongitude())
					.accountOption(accountOption).build());
		}
		return true;
	}
	
	@Override
	public void saveSafeZone(InitSafeZoneDto initSafeZoneDto, UUID accountId) {
		
		AccountOption accountOption = accountOptionService.findByAccountIdOrThrow(accountId);
		List<VertexDto> initSafeZoneList = initSafeZoneDto.getInitSafeZone();
		List<TTL> ttlList= ttlRepository.findByJoinFetch(accountOption.getId());

		List<VertexDto> safeZoneList = vertexUtil.calculateVertex(initSafeZoneList);
		
		int count=0;
		for(TTL ttl: ttlList) {
			while(count % 4 == 0) {
				//spring batch bulk로 create하기
				safeZoneRepository.save(SafeZone.builder()
					.latitude(safeZoneList.get(count).getLatitude())
					.longitude(safeZoneList.get(count).getLongitude())
					.ttl(ttl).build());	
				count++;
			}
		}
	}
	
	@Override
	public void deleteSafeZoneFirst(UUID accountId) {
		AccountOption accountOption = accountOptionService.findByAccountIdOrThrow(accountId);
		List<TTL> ttlList= ttlRepository.findByJoinFetch(accountOption.getId());
		
		List<SafeZone> safeZoneList = new ArrayList<>();
		for(TTL ttl: ttlList) {
			List<SafeZone> tempSafeZoneList = safeZoneRepository.findByJoinFetch(ttl.getId());
			safeZoneList.addAll(tempSafeZoneList);
		}
		
		List<SafeZone> deleteSafeZoneList = vertexUtil.calculateDeleteVertex(safeZoneList);
		
		//spring batch bulk로 delete 하기
		for(SafeZone deleteSafeZone: deleteSafeZoneList)
			safeZoneRepository.delete(deleteSafeZone);
		
	}
}
