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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.service.IUserService#findAll()
	 */
	@Override
	public List<User> findAll() {
		return this.userRepository.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.service.IUserService#findById(java.lang.Long)
	 */
	@Override
	public User findById(Long userId) {
		return this.userRepository.getOne(userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.service.IUserService#findByName(java.lang.String)
	 */
	@Override
	public User findByName(String userName) {
		return this.userRepository.findByUserName(userName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.service.IUserService#deleteById(java.lang.Long)
	 */
	@Override
	public void deleteById(Long userId) {
		this.userRepository.deleteById(userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.service.IUserService#save(com.example.model.User)
	 */
	@Override
	public User save(User user) {
		return this.userRepository.save(user);
	}
	
	@Override
	public boolean executeUpdateSql(String query) {
		return this.userRepository.executeUpdateSql(query, null);
	}

	/*
	 * //https://blog.csdn.net/alex_fung/article/details/83009100 public Integer
	 * batchInsertIk(List<IK> ikList) { String sql =
	 * "insert SWJ_IK1(name,count) values(?,?)"; jdbcTemplate.batchUpdate(sql, new
	 * BatchPreparedStatementSetter() { public void setValues(PreparedStatement ps,
	 * int i) throws SQLException { String name = ikList.get(i).getName(); int count
	 * = ikList.get(i).getCount(); ps.setString(1, name); ps.setInt(2, count); }
	 * public int getBatchSize() { return ikList.size(); } }); return 0; }
	 * 
	 * public Integer batchUpdateIk(List<IK> ikList) { String sql =
	 * "update SWJ_IK1 set count=? where name=?"; jdbcTemplate.batchUpdate(sql, new
	 * BatchPreparedStatementSetter() { public void setValues(PreparedStatement ps,
	 * int i) throws SQLException { int count = ikList.get(i).getCount(); String
	 * name = ikList.get(i).getName(); ps.setInt(1, count); ps.setString(2, name); }
	 * public int getBatchSize() { return ikList.size(); } }); return 0; }
	 * 
	 * // 也可以自定义字段对应，但是要注意Object[]中元素的位置 public Integer batchInsertUsers(List<IK>
	 * list) { String sql = "insert SWJ_IK1(name,count) values(?,?)";
	 * jdbcTemplate.batchUpdate(sql,setParameters(list)); return 0; } private
	 * List<Object[]> setParameters(List<IK> list){ List<Object[]> parameters = new
	 * ArrayList<Object[]>(); for (IK ik : list) { parameters.add(new Object[] {
	 * ik.getName(),ik.getCount()}); } return parameters; }
	 */

}
