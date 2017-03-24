package com.hbc.api.mapper;


import java.util.List;

import com.hbc.api.dto.BestFriendDTO;
import com.hbc.api.model.TelephoneNumber;
import com.hbc.api.util.MyMapper;

public interface TelephoneNumberMapper extends MyMapper<TelephoneNumber> {
	 
	public List<TelephoneNumber> getFyTelephoneNumbers(List<BestFriendDTO> list);
	
	public List<TelephoneNumber> getCollectionTelephoneNumbers(List<BestFriendDTO> list);
	
	public List<TelephoneNumber> getCreditTelephoneNumbers(List<BestFriendDTO> list);
	
	public List<TelephoneNumber> getCreditMediumTelephoneNumbers(List<BestFriendDTO> list);
	
	public List<TelephoneNumber> getCreditCardTelephoneNumbers(List<BestFriendDTO> list);
}