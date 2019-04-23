package com.example.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.User;
import com.example.repository.IUserRepository;

@Service("userService")
public class UserService implements IUserService {
	private Logger logger = LoggerFactory.getLogger(UserService.class);
	@Autowired
	private IUserRepository userRepository;

	/* (non-Javadoc)
	 * @see com.example.service.IUserService#findAll()
	 */
	@Override
	public List<User> findAll() {
		return this.userRepository.findAll();
	}
	/* (non-Javadoc)
	 * @see com.example.service.IUserService#findById(java.lang.Long)
	 */
	@Override
	public User findById(Long userId) {
		return this.userRepository.getOne(userId);
	}

	/* (non-Javadoc)
	 * @see com.example.service.IUserService#findByName(java.lang.String)
	 */
	@Override
	public User findByName(String userName) {
		return this.userRepository.findByUserName(userName);
	}
	
	/* (non-Javadoc)
	 * @see com.example.service.IUserService#deleteById(java.lang.Long)
	 */
	@Override
	public void deleteById(Long userId) {
		this.userRepository.deleteById(userId);
	}

	/* (non-Javadoc)
	 * @see com.example.service.IUserService#save(com.example.model.User)
	 */
	@Override
	public User save(User user) {
        return this.userRepository.save(user);
	}
}
