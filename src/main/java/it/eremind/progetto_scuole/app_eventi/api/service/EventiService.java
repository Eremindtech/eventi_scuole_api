package it.eremind.progetto_scuole.app_eventi.api.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;




@Service
public class EventiService {


	protected Logger logger=LoggerFactory.getLogger(this.getClass().getName());
	
	@Autowired
	private UserRepo uRepo;

	@Autowired
	private ResourcesService resService;
	



	@Transactional(readOnly=true)
	public UserDto getUserData(User userDb) {
		UserDto user=new UserDto();
		user.setIdUser(userDb.getIdUser());
		user.setData(new UserDto.Data(userDb.getFirstName(), userDb.getLastName()));
		user.setPhys(new UserDto.PhysicalDetails(userDb.getDateOfBirth(), userDb.getHeight(), userDb.getWeight(), userDb.getSex()));
		logger.debug("getUserData: user="+user);
		return user;
	}





	
}
